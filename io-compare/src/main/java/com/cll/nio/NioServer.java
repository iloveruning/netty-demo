package com.cll.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author chenliangliang
 * @date 2018/4/15
 */
public class NioServer {

    private final int port;

    public NioServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        final ServerSocketChannel socketChannel = ServerSocketChannel.open();
        socketChannel.configureBlocking(false);
        ServerSocket socket = socketChannel.socket();
        socket.bind(new InetSocketAddress(port));
        //打开Selector来处理Channel
        Selector selector = Selector.open();
        //将ServerSocket注册到Selector以接受连接
        socketChannel.register(selector, SelectionKey.OP_ACCEPT);

        final ByteBuffer msg = ByteBuffer.wrap("Hi!\r\n".getBytes());

        while (true) {
            //等待需要处理的新事件；阻塞 将一直持续到下一个传入事件
            selector.select();
            //获取所有接收事件的SelectionKey实例
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> it = keys.iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();
                try {
                    //检查事件是否是一个新的已经就绪可以被接受的连接
                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        //接受客户端，并将它注册到选择器
                        SocketChannel client = server.accept();
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, msg.duplicate());
                        System.out.println("Accepted connection from " + client);
                        //检查套接字是否已经准备好写数据
                    } else if (key.isWritable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        //将数据写到已连接的客户端
                        while (buffer.hasRemaining()) {
                            if (client.write(buffer) == 0) {
                                break;
                            }
                        }
                        client.close();
                    }
                } catch (IOException ee) {
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException eee) {// ignore on close}
                    }
                }
            }

        }
    }


    public static void main(String[] args) throws IOException {
        new NioServer(9800).start();
    }
}
