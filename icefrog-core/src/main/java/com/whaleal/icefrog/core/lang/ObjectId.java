package com.whaleal.icefrog.core.lang;

import com.whaleal.icefrog.core.date.DateUtil;
import com.whaleal.icefrog.core.util.ClassLoaderUtil;
import com.whaleal.icefrog.core.util.RandomUtil;
import com.whaleal.icefrog.core.util.RuntimeUtil;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;

import static com.whaleal.icefrog.core.lang.Preconditions.isTrueArgument;
import static com.whaleal.icefrog.core.lang.Preconditions.notNull;

/**
 * MongoDB ID生成策略实现<br>
 * ObjectId由以下几部分组成：
 *
 * <pre>
 * 1. Time 时间戳。
 * 2. Machine 所在主机的唯一标识符，一般是机器主机名的散列值。
 * 3. PID 进程ID。确保同一机器中不冲突
 * 4. INC 自增计数器。确保同一秒内产生objectId的唯一性。
 * </pre>
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public final class ObjectId implements Comparable<ObjectId>, Serializable {

    /**
     * 线程安全的下一个随机数,每次生成自增+1
     */
    private static final AtomicInteger NEXT_INC = new AtomicInteger(RandomUtil.randomInt());
    /**
     * 机器信息
     */
    private static final int MACHINE = getMachinePiece() | getProcessPiece();


    // unused, as this class uses a proxy for serialization
    private static final long serialVersionUID = 1L;

    private static final int OBJECT_ID_LENGTH = 12;
    private static final int LOW_ORDER_THREE_BYTES = 0x00ffffff;

    // Use primitives to represent the 5-byte random value.
    private static final int RANDOM_VALUE1;
    private static final short RANDOM_VALUE2;

    private static final AtomicInteger NEXT_COUNTER = new AtomicInteger(new SecureRandom().nextInt());

    private static final char[] HEX_CHARS = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    static {
        try {
            SecureRandom secureRandom = new SecureRandom();
            RANDOM_VALUE1 = secureRandom.nextInt(0x01000000);
            RANDOM_VALUE2 = (short) secureRandom.nextInt(0x00008000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final int timestamp;
    private final int counter;
    private final int randomValue1;
    private final short randomValue2;

    /**
     * Create a new object id.
     */
    public ObjectId() {
        this(new Date());
    }

    /**
     * Constructs a new instance using the given date.
     *
     * @param date the date
     */
    public ObjectId( final Date date ) {
        this(dateToTimestampSeconds(date), NEXT_COUNTER.getAndIncrement() & LOW_ORDER_THREE_BYTES, false);
    }


    /**
     * Constructs a new instances using the given date and counter.
     *
     * @param date    the date
     * @param counter the counter
     * @throws IllegalArgumentException if the high order byte of counter is not zero
     */
    public ObjectId( final Date date, final int counter ) {
        this(dateToTimestampSeconds(date), counter, true);
    }

    /**
     * Creates an ObjectId using the given time, machine identifier, process identifier, and counter.
     *
     * @param timestamp the time in seconds
     * @param counter   the counter
     * @throws IllegalArgumentException if the high order byte of counter is not zero
     */
    public ObjectId( final int timestamp, final int counter ) {
        this(timestamp, counter, true);
    }

    private ObjectId( final int timestamp, final int counter, final boolean checkCounter ) {
        this(timestamp, RANDOM_VALUE1, RANDOM_VALUE2, counter, checkCounter);
    }

    private ObjectId( final int timestamp, final int randomValue1, final short randomValue2, final int counter,
                      final boolean checkCounter ) {
        if ((randomValue1 & 0xff000000) != 0) {
            throw new IllegalArgumentException("The machine identifier must be between 0 and 16777215 (it must fit in three bytes).");
        }
        if (checkCounter && ((counter & 0xff000000) != 0)) {
            throw new IllegalArgumentException("The counter must be between 0 and 16777215 (it must fit in three bytes).");
        }
        this.timestamp = timestamp;
        this.counter = counter & LOW_ORDER_THREE_BYTES;
        this.randomValue1 = randomValue1;
        this.randomValue2 = randomValue2;
    }

    /**
     * Constructs a new instance from a 24-byte hexadecimal string representation.
     *
     * @param hexString the string to convert
     * @throws IllegalArgumentException if the string is not a valid hex string representation of an ObjectId
     */
    public ObjectId( final String hexString ) {
        this(parseHexString(hexString));
    }

    /**
     * Constructs a new instance from the given byte array
     *
     * @param bytes the byte array
     * @throws IllegalArgumentException if array is null or not of length 12
     */
    public ObjectId( final byte[] bytes ) {
        this(ByteBuffer.wrap(isTrueArgument("bytes has length of 12", bytes, notNull("bytes", bytes).length == 12)));
    }

    /**
     * Constructs a new instance from the given ByteBuffer
     *
     * @param buffer the ByteBuffer
     * @throws IllegalArgumentException if the buffer is null or does not have at least 12 bytes remaining
     */
    public ObjectId( final ByteBuffer buffer ) {
        notNull("buffer", buffer);
        isTrueArgument("buffer.remaining() >=12", buffer.remaining() >= OBJECT_ID_LENGTH);

        // Note: Cannot use ByteBuffer.getInt because it depends on tbe buffer's byte order
        // and ObjectId's are always in big-endian order.
        timestamp = makeInt(buffer.get(), buffer.get(), buffer.get(), buffer.get());
        randomValue1 = makeInt((byte) 0, buffer.get(), buffer.get(), buffer.get());
        randomValue2 = makeShort(buffer.get(), buffer.get());
        counter = makeInt((byte) 0, buffer.get(), buffer.get(), buffer.get());
    }

    /**
     * 获取一个objectId的bytes表现形式
     *
     * @return objectId
     * @since 1.0.0
     */
    public static byte[] nextBytes() {
        final ByteBuffer bb = ByteBuffer.wrap(new byte[12]);
        bb.putInt((int) DateUtil.currentSeconds());// 4位
        bb.putInt(MACHINE);// 4位
        bb.putInt(NEXT_INC.getAndIncrement());// 4位

        return bb.array();
    }

    /**
     * 获取一个objectId用下划线分割
     *
     * @return objectId
     */
    public static String next() {
        return next(false);
    }

    /**
     * 获取一个objectId
     *
     * @param withHyphen 是否包含分隔符
     * @return objectId
     */
    public static String next( boolean withHyphen ) {
        byte[] array = nextBytes();
        final StringBuilder buf = new StringBuilder(withHyphen ? 26 : 24);
        int t;
        for (int i = 0; i < array.length; i++) {
            if (withHyphen && i % 4 == 0 && i != 0) {
                buf.append("-");
            }
            t = array[i] & 0xff;
            if (t < 16) {
                buf.append('0');
            }
            buf.append(Integer.toHexString(t));

        }
        return buf.toString();
    }

    /**
     * Gets a new object id.
     *
     * @return the new id
     */
    public static ObjectId get() {
        return new ObjectId();
    }

    /**
     * Gets a new object id with the given date value and all other bits zeroed.
     * <p>
     * The returned object id will compare as less than or equal to any other object id within the same second as the given date, and
     * less than any later date.
     * </p>
     *
     * @param date the date
     * @return the ObjectId
     */
    public static ObjectId getSmallestWithDate( final Date date ) {
        return new ObjectId(dateToTimestampSeconds(date), 0, (short) 0, 0, false);
    }

    /**
     * Checks if a string could be an {@code ObjectId}.
     * <p>
     * 给定的字符串是否为有效的ObjectId
     *
     * @param hexString a potential ObjectId as a String.  字符串
     * @return whether the string could be an object id  是否为有效的ObjectId
     * @throws IllegalArgumentException if hexString is null
     */
    public static boolean isValid( final String hexString ) {
        if (hexString == null) {
            throw new IllegalArgumentException();
        }

        int len = hexString.length();
        if (len != 24) {
            return false;
        }

        for (int i = 0; i < len; i++) {
            char c = hexString.charAt(i);
            if (c >= '0' && c <= '9') {
                continue;
            }
            if (c >= 'a' && c <= 'f') {
                continue;
            }
            if (c >= 'A' && c <= 'F') {
                continue;
            }

            return false;
        }

        return true;
    }

    private static byte[] parseHexString( final String s ) {
        if (!isValid(s)) {
            throw new IllegalArgumentException("invalid hexadecimal representation of an ObjectId: [" + s + "]");
        }

        byte[] b = new byte[OBJECT_ID_LENGTH];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16);
        }
        return b;
    }

    private static int dateToTimestampSeconds( final Date time ) {
        return (int) (time.getTime() / 1000);
    }

    private static int makeInt( final byte b3, final byte b2, final byte b1, final byte b0 ) {
        // CHECKSTYLE:OFF
        return (((b3) << 24) |
                ((b2 & 0xff) << 16) |
                ((b1 & 0xff) << 8) |
                ((b0 & 0xff)));
        // CHECKSTYLE:ON
    }

    private static short makeShort( final byte b1, final byte b0 ) {
        // CHECKSTYLE:OFF
        return (short) (((b1 & 0xff) << 8) | ((b0 & 0xff)));
        // CHECKSTYLE:ON
    }

    private static byte int3( final int x ) {
        return (byte) (x >> 24);
    }

    private static byte int2( final int x ) {
        return (byte) (x >> 16);
    }

    private static byte int1( final int x ) {
        return (byte) (x >> 8);
    }

    private static byte int0( final int x ) {
        return (byte) (x);
    }

    private static byte short1( final short x ) {
        return (byte) (x >> 8);
    }

    private static byte short0( final short x ) {
        return (byte) (x);
    }

    /**
     * 获取机器码片段
     *
     * @return 机器码片段
     */
    private static int getMachinePiece() {
        // 机器码
        int machinePiece;
        try {
            StringBuilder netSb = new StringBuilder();
            // 返回机器所有的网络接口
            Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
            // 遍历网络接口
            while (e.hasMoreElements()) {
                NetworkInterface ni = e.nextElement();
                // 网络接口信息
                netSb.append(ni.toString());
            }
            // 保留后两位
            machinePiece = netSb.toString().hashCode() << 16;
        } catch (Throwable e) {
            // 出问题随机生成,保留后两位
            machinePiece = (RandomUtil.randomInt()) << 16;
        }
        return machinePiece;
    }

    /**
     * 获取进程码片段
     *
     * @return 进程码片段
     */
    private static int getProcessPiece() {
        // 进程码
        // 因为静态变量类加载可能相同,所以要获取进程ID + 加载对象的ID值
        final int processPiece;
        // 进程ID初始化
        int processId;
        try {
            processId = RuntimeUtil.getPid();
        } catch (Throwable t) {
            processId = RandomUtil.randomInt();
        }

        final ClassLoader loader = ClassLoaderUtil.getClassLoader();
        // 返回对象哈希码,无论是否重写hashCode方法
        int loaderId = (loader != null) ? System.identityHashCode(loader) : 0;

        // 进程ID + 对象加载ID
        // 保留前2位
        final String processSb = Integer.toHexString(processId) + Integer.toHexString(loaderId);
        processPiece = processSb.hashCode() & 0xFFFF;

        return processPiece;
    }

    /**
     * Convert to a byte array.  Note that the numbers are stored in big-endian order.
     *
     * @return the byte array
     */
    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(OBJECT_ID_LENGTH);
        putToByteBuffer(buffer);
        return buffer.array();  // using .allocate ensures there is a backing array that can be returned
    }

    /**
     * Convert to bytes and put those bytes to the provided ByteBuffer.
     * Note that the numbers are stored in big-endian order.
     *
     * @param buffer the ByteBuffer
     * @throws IllegalArgumentException if the buffer is null or does not have at least 12 bytes remaining
     */
    public void putToByteBuffer( final ByteBuffer buffer ) {
        notNull("buffer", buffer);
        isTrueArgument("buffer.remaining() >=12", buffer.remaining() >= OBJECT_ID_LENGTH);

        buffer.put(int3(timestamp));
        buffer.put(int2(timestamp));
        buffer.put(int1(timestamp));
        buffer.put(int0(timestamp));
        buffer.put(int2(randomValue1));
        buffer.put(int1(randomValue1));
        buffer.put(int0(randomValue1));
        buffer.put(short1(randomValue2));
        buffer.put(short0(randomValue2));
        buffer.put(int2(counter));
        buffer.put(int1(counter));
        buffer.put(int0(counter));
    }

    // Big-Endian helpers, in this class because all other BSON numbers are little-endian

    /**
     * Gets the timestamp (number of seconds since the Unix epoch).
     *
     * @return the timestamp
     */
    public int getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the timestamp as a {@code Date} instance.
     *
     * @return the Date
     */
    public Date getDate() {
        return new Date((timestamp & 0xFFFFFFFFL) * 1000L);
    }

    /**
     * Converts this instance into a 24-byte hexadecimal string representation.
     *
     * @return a string representation of the ObjectId in hexadecimal format
     */
    public String toHexString() {
        char[] chars = new char[OBJECT_ID_LENGTH * 2];
        int i = 0;
        for (byte b : toByteArray()) {
            chars[i++] = HEX_CHARS[b >> 4 & 0xF];
            chars[i++] = HEX_CHARS[b & 0xF];
        }
        return new String(chars);
    }

    @Override
    public boolean equals( final Object o ) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ObjectId objectId = (ObjectId) o;

        if (counter != objectId.counter) {
            return false;
        }
        if (timestamp != objectId.timestamp) {
            return false;
        }

        if (randomValue1 != objectId.randomValue1) {
            return false;
        }

        if (randomValue2 != objectId.randomValue2) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = timestamp;
        result = 31 * result + counter;
        result = 31 * result + randomValue1;
        result = 31 * result + randomValue2;
        return result;
    }

    @Override
    public int compareTo( final ObjectId other ) {
        if (other == null) {
            throw new NullPointerException();
        }

        byte[] byteArray = toByteArray();
        byte[] otherByteArray = other.toByteArray();
        for (int i = 0; i < OBJECT_ID_LENGTH; i++) {
            if (byteArray[i] != otherByteArray[i]) {
                return ((byteArray[i] & 0xff) < (otherByteArray[i] & 0xff)) ? -1 : 1;
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return toHexString();
    }

    // see https://docs.oracle.com/javase/6/docs/platform/serialization/spec/output.html
    private Object writeReplace() {
        return new SerializationProxy(this);
    }


    // ----------------------------------------------------------------------------------------- Private method start

    // see https://docs.oracle.com/javase/6/docs/platform/serialization/spec/input.html
    private void readObject( final ObjectInputStream stream ) throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required");
    }

    private static class SerializationProxy implements Serializable {
        private static final long serialVersionUID = 1L;

        private final byte[] bytes;

        SerializationProxy( final ObjectId objectId ) {
            bytes = objectId.toByteArray();
        }

        private Object readResolve() {
            return new ObjectId(bytes);
        }
    }
    // ----------------------------------------------------------------------------------------- Private method end
}
