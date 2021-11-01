package com.whaleal.icefrog.socket.aio;

import com.whaleal.icefrog.core.io.IORuntimeException;
import com.whaleal.icefrog.core.io.IoUtil;
import com.whaleal.icefrog.core.thread.ThreadFactoryBuilder;
import com.whaleal.icefrog.core.thread.ThreadUtil;
import com.whaleal.icefrog.log.Log;
import com.whaleal.icefrog.log.LogFactory;
import com.whaleal.icefrog.socket.SocketConfig;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;

/**
 * 基于AIO的Socket服务端实现
 *
 * @author Looly
 * @author wh
 */
public class AioServer implements Closeable {
    private static final Log log = LogFactory.get();
    private static final AcceptHandler ACCEPT_HANDLER = new AcceptHandler();
    protected final SocketConfig config;
    protected IoAction<ByteBuffer> ioAction;
    private AsynchronousChannelGroup group;
    private AsynchronousServerSocketChannel channel;


    /**
     * 构造
     *
     * @param port 端口
     */
    public AioServer( int port ) {
        this(new InetSocketAddress(port), new SocketConfig());
    }

    /**
     * 构造
     *
     * @param address 地址
     * @param config  {@link SocketConfig} 配置项
     */
    public AioServer( InetSocketAddress address, SocketConfig config ) {
        this.config = config;
        init(address);
    }

    /**
     * 初始化
     *
     * @param address 地址和端口
     * @return this
     */
    public AioServer init( InetSocketAddress address ) {
        try {
            this.group = AsynchronousChannelGroup.withFixedThreadPool(//
                    config.getThreadPoolSize(), // 默认线程池大小
                    ThreadFactoryBuilder.create().setNamePrefix("icefrog-socket-").build()//
            );
            this.channel = AsynchronousServerSocketChannel.open(group).bind(address);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
        return this;
    }

    /**
     * 开始监听
     *
     * @param sync 是否阻塞
     */
    public void start( boolean sync ) {
        doStart(sync);
    }

    /**
     * 设置 Socket 的 Option 选项<br>
     * 选项见：{@link java.net.StandardSocketOptions}
     *
     * @param <T>   选项泛型
     * @param name  {@link SocketOption} 枚举
     * @param value SocketOption参数
     * @return this
     * @throws IOException IO异常
     */
    public <T> AioServer setOption( SocketOption<T> name, T value ) throws IOException {
        this.channel.setOption(name, value);
        return this;
    }

    /**
     * 获取IO处理器
     *
     * @return {@link IoAction}
     */
    public IoAction<ByteBuffer> getIoAction() {
        return this.ioAction;
    }

    /**
     * 设置IO处理器，单例存在
     *
     * @param ioAction {@link IoAction}
     * @return this;
     */
    public AioServer setIoAction( IoAction<ByteBuffer> ioAction ) {
        this.ioAction = ioAction;
        return this;
    }

    /**
     * 获取{@link AsynchronousServerSocketChannel}
     *
     * @return {@link AsynchronousServerSocketChannel}
     */
    public AsynchronousServerSocketChannel getChannel() {
        return this.channel;
    }

    /**
     * 处理接入的客户端
     *
     * @return this
     */
    public AioServer accept() {
        this.channel.accept(this, ACCEPT_HANDLER);
        return this;
    }

    /**
     * 服务是否开启状态
     *
     * @return 服务是否开启状态
     */
    public boolean isOpen() {
        return (null != this.channel) && this.channel.isOpen();
    }

    /**
     * 关闭服务
     */
    @Override
    public void close() {
        IoUtil.close(this.channel);

        if (null != this.group && false == this.group.isShutdown()) {
            try {
                this.group.shutdownNow();
            } catch (IOException e) {
                // ignore
            }
        }

        // 结束阻塞
        synchronized (this) {
            this.notify();
        }
    }

    // ------------------------------------------------------------------------------------- Private method start

    /**
     * 开始监听
     *
     * @param sync 是否阻塞
     */
    private void doStart( boolean sync ) {
        log.debug("Aio Server started, waiting for accept.");

        // 接收客户端连接
        accept();

        if (sync) {
            ThreadUtil.sync(this);
        }
    }
    // ------------------------------------------------------------------------------------- Private method end
}
