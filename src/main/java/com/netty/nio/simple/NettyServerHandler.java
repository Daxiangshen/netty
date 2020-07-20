package com.netty.nio.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * NettyServerHandler Class
 *
 * 我们自定义Handler需要继承Netty规定好的某个HandlerAdapter,这时自定义的Handler,才能称为一个Handler
 * ChannelInboundHandlerAdapter 用于处理入站i/o事件
 *
 * @author : yuxiang
 * @date : 2020/6/23
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    //读取数据的事件,这里可以读取客户端发送的消息
    /**
     * ChannelHandlerContext ctx:上下文对象,含有管道pipeline,通道channel,地址
     * Object msg:客户端发送的数据,默认是obj
     * */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.err.println("server ctx="+ctx);

        //将msg转为ByteBuffer (ByteBuf是netty提供的,和nio的有区别)
        ByteBuf byteBuf=(ByteBuf) msg;
        System.err.println("客户端发送消息是:"+byteBuf.toString(CharsetUtil.UTF_8));
        System.err.println("客户端地址:"+ctx.channel().remoteAddress());
    }

    //数据读取完毕
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

        //write+flush 将数据写入到缓冲,并刷新
        //一般讲,我们对发送的数据进行编码
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello,客户端",CharsetUtil.UTF_8));
    }

    //处理异常,一般是需要关闭通道
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
