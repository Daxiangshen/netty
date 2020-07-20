package com.netty.nio.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.NettyRuntime;

/**
 * NettyClientHandler Class
 *
 * @author : yuxiang
 * @date : 2020/6/23
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    //当通道就绪时,就会触发该方法
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.err.println("client "+ctx);
        ctx.writeAndFlush(Unpooled.copiedBuffer("wtf", CharsetUtil.UTF_8));
    }

    //当通道有读取事件时会触发
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf=(ByteBuf) msg;
        System.err.println("服务器回复的消息:"+byteBuf.toString(CharsetUtil.UTF_8));
        System.err.println("服务器地址"+ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
