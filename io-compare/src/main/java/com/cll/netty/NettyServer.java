package com.cll.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

/**
 * @author chenliangliang
 * @date 2018/4/15
 */
public class NettyServer {

    private final int port;

    public NettyServer(int port){
        this.port=port;
    }

    public void start() throws InterruptedException {

        final EventLoopGroup group=new EpollEventLoopGroup();
        //final EventLoopGroup group=new OioEventLoopGroup();
        final ByteBuf buf=Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hi!\r\n",CharsetUtil.UTF_8));

        try {
            ServerBootstrap bootstrap=new ServerBootstrap();
            bootstrap.group(group)
                    .channel(EpollServerSocketChannel.class)
                    //.channel(OioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    ctx.writeAndFlush(buf.duplicate()).addListener(ChannelFutureListener.CLOSE);
                                }
                            });
                        }
                    });
            ChannelFuture future = bootstrap.bind().sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
