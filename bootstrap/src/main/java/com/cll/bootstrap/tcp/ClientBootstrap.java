package com.cll.bootstrap.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

/**
 * @author chenliangliang
 * @date 2018/4/16
 */
public class ClientBootstrap {


    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group=new NioEventLoopGroup();
        Bootstrap bootstrap=new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new SimpleChannelInboundHandler<ByteBuf>() {

                    @Override
                    protected void messageReceived(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
                        System.out.println("received msg: "+byteBuf.toString(CharsetUtil.UTF_8));
                    }
                });
        ChannelFuture future = bootstrap.connect(new InetSocketAddress("localhost", 8989));
        future.addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                System.out.println(channelFuture.channel().remoteAddress());
                System.out.println("Connection established");
            } else {
                System.err.println("Connection attempt failed");
                channelFuture.cause().printStackTrace();
            }
        });
        future.channel().writeAndFlush(Unpooled.copiedBuffer("ebrvuvr",CharsetUtil.UTF_8));
        future.sync();
    }

}
