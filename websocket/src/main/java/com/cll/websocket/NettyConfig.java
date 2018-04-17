package com.cll.websocket;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * Netty全局配置
 *
 * @author chenliangliang
 * @date 2018/4/17
 */
public class NettyConfig {

    public static ChannelGroup group=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
}
