package com.whaleal.icefrog.socket;

import com.whaleal.icefrog.core.io.IORuntimeException;
import com.whaleal.icefrog.core.lang.Preconditions;

import javax.net.ServerSocketFactory;
import java.io.IOException;
import java.net.*;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Simple utility methods for working with network sockets &mdash; for example,
 * for finding available ports on {@code localhost}.
 *
 * <p>Within this class, a TCP port refers to a port for a {@link ServerSocket};
 * whereas, a UDP port refers to a port for a {@link DatagramSocket}.
 * <p>
 * socket编程常用的 一些工具类
 * 比如 在localhost  中寻找合适的端口
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class SocketUtil {

    /**
     * The default minimum value for port ranges used when finding an available
     * socket port.
     */
    public static final int PORT_RANGE_MIN = 1024;

    /**
     * The default maximum value for port ranges used when finding an available
     * socket port.
     */
    public static final int PORT_RANGE_MAX = 65535;
    private static final Random random = new Random(System.nanoTime());

    /**
     * Although {@code SocketUtis} consists solely of static utility methods,
     * this constructor is intentionally {@code public}.
     * Rationale<br>
     * <p>Static methods from this class may be invoked from within XML
     * configuration files using the Spring Expression Language (SpEL) and the
     * following syntax.
     * <pre><code>&lt;bean id="bean1" ... p:port="#{T(socketUtil).findAvailableTcpPort(12000)}" /&gt;</code></pre>
     * If this constructor were {@code private}, you would be required to supply
     * the fully qualified class name to SpEL's {@code T()} function for each usage.
     * Thus, the fact that this constructor is {@code public} allows you to reduce
     * boilerplate configuration with SpEL as can be seen in the following example.
     * <pre><code>&lt;bean id="socketUtil" class="socketUtil" /&gt;
     * &lt;bean id="bean1" ... p:port="#{socketUtil.findAvailableTcpPort(12000)}" /&gt;
     * &lt;bean id="bean2" ... p:port="#{socketUtil.findAvailableTcpPort(30000)}" /&gt;</code></pre>
     */
    public SocketUtil() {
    }

    /**
     * 获取远程端的地址信息，包括host和端口<br>
     * null表示channel为null或者远程主机未连接
     *
     * @param channel {@link AsynchronousSocketChannel}
     * @return 远程端的地址信息，包括host和端口，null表示channel为null或者远程主机未连接
     */
    public static SocketAddress getRemoteAddress( AsynchronousSocketChannel channel ) {
        try {
            return (null == channel) ? null : channel.getRemoteAddress();
        } catch (ClosedChannelException e) {
            // Channel未打开或已关闭，返回null表示未连接
            return null;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 远程主机是否处于连接状态<br>
     * 通过判断远程地址获取成功与否判断
     *
     * @param channel {@link AsynchronousSocketChannel}
     * @return 远程主机是否处于连接状态
     */
    public static boolean isConnected( AsynchronousSocketChannel channel ) {
        return null != getRemoteAddress(channel);
    }

    /**
     * 创建Socket并连接到指定地址的服务器
     *
     * @param hostname 地址
     * @param port     端口
     * @return {@link Socket}
     * @throws IORuntimeException IO异常
     * @since 1.0.0
     */
    public static Socket connect( String hostname, int port ) throws IORuntimeException {
        return connect(hostname, port, -1);
    }

    /**
     * 创建Socket并连接到指定地址的服务器
     *
     * @param hostname          地址
     * @param port              端口
     * @param connectionTimeout 连接超时
     * @return {@link Socket}
     * @throws IORuntimeException IO异常
     * @since 1.0.0
     */
    public static Socket connect( final String hostname, int port, int connectionTimeout ) throws IORuntimeException {
        return connect(new InetSocketAddress(hostname, port), connectionTimeout);
    }

    /**
     * 创建Socket并连接到指定地址的服务器
     *
     * @param address           地址
     * @param connectionTimeout 连接超时
     * @return {@link Socket}
     * @throws IORuntimeException IO异常
     * @since 1.0.0
     */
    public static Socket connect( InetSocketAddress address, int connectionTimeout ) throws IORuntimeException {
        final Socket socket = new Socket();
        try {
            if (connectionTimeout <= 0) {
                socket.connect(address);
            } else {
                socket.connect(address, connectionTimeout);
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
        return socket;
    }

    /**
     * Find an available TCP port randomly selected from the range
     * [{@value #PORT_RANGE_MIN}, {@value #PORT_RANGE_MAX}].
     *
     * @return an available TCP port number
     * @throws IllegalStateException if no available port could be found
     */
    public static int findAvailableTcpPort() {
        return findAvailableTcpPort(PORT_RANGE_MIN);
    }

    /**
     * Find an available TCP port randomly selected from the range
     * [{@code minPort}, {@value #PORT_RANGE_MAX}].
     *
     * @param minPort the minimum port number
     * @return an available TCP port number
     * @throws IllegalStateException if no available port could be found
     */
    public static int findAvailableTcpPort( int minPort ) {
        return findAvailableTcpPort(minPort, PORT_RANGE_MAX);
    }

    /**
     * Find an available TCP port randomly selected from the range
     * [{@code minPort}, {@code maxPort}].
     *
     * @param minPort the minimum port number
     * @param maxPort the maximum port number
     * @return an available TCP port number
     * @throws IllegalStateException if no available port could be found
     */
    public static int findAvailableTcpPort( int minPort, int maxPort ) {
        return SocketType.TCP.findAvailablePort(minPort, maxPort);
    }

    /**
     * Find the requested number of available TCP ports, each randomly selected
     * from the range [{@value #PORT_RANGE_MIN}, {@value #PORT_RANGE_MAX}].
     *
     * @param numRequested the number of available ports to find
     * @return a sorted set of available TCP port numbers
     * @throws IllegalStateException if the requested number of available ports could not be found
     */
    public static SortedSet<Integer> findAvailableTcpPorts( int numRequested ) {
        return findAvailableTcpPorts(numRequested, PORT_RANGE_MIN, PORT_RANGE_MAX);
    }

    /**
     * Find the requested number of available TCP ports, each randomly selected
     * from the range [{@code minPort}, {@code maxPort}].
     *
     * @param numRequested the number of available ports to find
     * @param minPort      the minimum port number
     * @param maxPort      the maximum port number
     * @return a sorted set of available TCP port numbers
     * @throws IllegalStateException if the requested number of available ports could not be found
     */
    public static SortedSet<Integer> findAvailableTcpPorts( int numRequested, int minPort, int maxPort ) {
        return SocketType.TCP.findAvailablePorts(numRequested, minPort, maxPort);
    }

    /**
     * Find an available UDP port randomly selected from the range
     * [{@value #PORT_RANGE_MIN}, {@value #PORT_RANGE_MAX}].
     *
     * @return an available UDP port number
     * @throws IllegalStateException if no available port could be found
     */
    public static int findAvailableUdpPort() {
        return findAvailableUdpPort(PORT_RANGE_MIN);
    }

    /**
     * Find an available UDP port randomly selected from the range
     * [{@code minPort}, {@value #PORT_RANGE_MAX}].
     *
     * @param minPort the minimum port number
     * @return an available UDP port number
     * @throws IllegalStateException if no available port could be found
     */
    public static int findAvailableUdpPort( int minPort ) {
        return findAvailableUdpPort(minPort, PORT_RANGE_MAX);
    }

    /**
     * Find an available UDP port randomly selected from the range
     * [{@code minPort}, {@code maxPort}].
     *
     * @param minPort the minimum port number
     * @param maxPort the maximum port number
     * @return an available UDP port number
     * @throws IllegalStateException if no available port could be found
     */
    public static int findAvailableUdpPort( int minPort, int maxPort ) {
        return SocketType.UDP.findAvailablePort(minPort, maxPort);
    }

    /**
     * Find the requested number of available UDP ports, each randomly selected
     * from the range [{@value #PORT_RANGE_MIN}, {@value #PORT_RANGE_MAX}].
     *
     * @param numRequested the number of available ports to find
     * @return a sorted set of available UDP port numbers
     * @throws IllegalStateException if the requested number of available ports could not be found
     */
    public static SortedSet<Integer> findAvailableUdpPorts( int numRequested ) {
        return findAvailableUdpPorts(numRequested, PORT_RANGE_MIN, PORT_RANGE_MAX);
    }

    /**
     * Find the requested number of available UDP ports, each randomly selected
     * from the range [{@code minPort}, {@code maxPort}].
     *
     * @param numRequested the number of available ports to find
     * @param minPort      the minimum port number
     * @param maxPort      the maximum port number
     * @return a sorted set of available UDP port numbers
     * @throws IllegalStateException if the requested number of available ports could not be found
     */
    public static SortedSet<Integer> findAvailableUdpPorts( int numRequested, int minPort, int maxPort ) {
        return SocketType.UDP.findAvailablePorts(numRequested, minPort, maxPort);
    }


    private enum SocketType {

        TCP {
            @Override
            protected boolean isPortAvailable( int port ) {
                try {
                    ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(
                            port, 1, InetAddress.getByName("localhost"));
                    serverSocket.close();
                    return true;
                } catch (Exception ex) {
                    return false;
                }
            }
        },

        UDP {
            @Override
            protected boolean isPortAvailable( int port ) {
                try {
                    DatagramSocket socket = new DatagramSocket(port, InetAddress.getByName("localhost"));
                    socket.close();
                    return true;
                } catch (Exception ex) {
                    return false;
                }
            }
        };

        /**
         * Determine if the specified port for this {@code SocketType} is
         * currently available on {@code localhost}.
         */
        protected abstract boolean isPortAvailable( int port );

        /**
         * Find a pseudo-random port number within the range
         * [{@code minPort}, {@code maxPort}].
         *
         * @param minPort the minimum port number
         * @param maxPort the maximum port number
         * @return a random port number within the specified range
         */
        private int findRandomPort( int minPort, int maxPort ) {
            int portRange = maxPort - minPort;
            return minPort + random.nextInt(portRange + 1);
        }

        /**
         * Find an available port for this {@code SocketType}, randomly selected
         * from the range [{@code minPort}, {@code maxPort}].
         *
         * @param minPort the minimum port number
         * @param maxPort the maximum port number
         * @return an available port number for this socket type
         * @throws IllegalStateException if no available port could be found
         */
        int findAvailablePort( int minPort, int maxPort ) {
            Preconditions.isTrue(minPort > 0, "'minPort' must be greater than 0");
            Preconditions.isTrue(maxPort >= minPort, "'maxPort' must be greater than or equal to 'minPort'");
            Preconditions.isTrue(maxPort <= PORT_RANGE_MAX, "'maxPort' must be less than or equal to " + PORT_RANGE_MAX);

            int portRange = maxPort - minPort;
            int candidatePort;
            int searchCounter = 0;
            do {
                if (searchCounter > portRange) {
                    throw new IllegalStateException(String.format(
                            "Could not find an available %s port in the range [%d, %d] after %d attempts",
                            name(), minPort, maxPort, searchCounter));
                }
                candidatePort = findRandomPort(minPort, maxPort);
                searchCounter++;
            }
            while (!isPortAvailable(candidatePort));

            return candidatePort;
        }

        /**
         * Find the requested number of available ports for this {@code SocketType},
         * each randomly selected from the range [{@code minPort}, {@code maxPort}].
         *
         * @param numRequested the number of available ports to find
         * @param minPort      the minimum port number
         * @param maxPort      the maximum port number
         * @return a sorted set of available port numbers for this socket type
         * @throws IllegalStateException if the requested number of available ports could not be found
         */
        SortedSet<Integer> findAvailablePorts( int numRequested, int minPort, int maxPort ) {
            Preconditions.isTrue(minPort > 0, "'minPort' must be greater than 0");
            Preconditions.isTrue(maxPort > minPort, "'maxPort' must be greater than 'minPort'");
            Preconditions.isTrue(maxPort <= PORT_RANGE_MAX, "'maxPort' must be less than or equal to " + PORT_RANGE_MAX);
            Preconditions.isTrue(numRequested > 0, "'numRequested' must be greater than 0");
            Preconditions.isTrue((maxPort - minPort) >= numRequested,
                    "'numRequested' must not be greater than 'maxPort' - 'minPort'");

            SortedSet<Integer> availablePorts = new TreeSet<>();
            int attemptCount = 0;
            while ((++attemptCount <= numRequested + 100) && availablePorts.size() < numRequested) {
                availablePorts.add(findAvailablePort(minPort, maxPort));
            }

            if (availablePorts.size() != numRequested) {
                throw new IllegalStateException(String.format(
                        "Could not find %d available %s ports in the range [%d, %d]",
                        numRequested, name(), minPort, maxPort));
            }

            return availablePorts;
        }
    }

}
