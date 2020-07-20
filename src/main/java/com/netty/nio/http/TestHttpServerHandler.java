package com.netty.nio.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

/**
 * TestHttpServerHandler Class
 *
 * SimpleChannelInboundHandler是ChannelInboundHandlerAdapter的子类
 * HttpObject表示客户端和服务器端相互通讯的数据被封装成httpObject
 *
 * @author : yuxiang
 * @date : 2020/6/24
 */
public class TestHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    //读取客户端数据
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

        //判断msg是不是httpRequest请求
        if (msg instanceof HttpRequest){
            System.out.println("msg 类型:"+msg.getClass());
            System.out.println("客户端地址:"+ctx.channel().remoteAddress());

            //获取到
            HttpRequest httpRequest=(HttpRequest)msg;
            //获取uri,过滤指定的资源
            URI uri = new URI(httpRequest.uri());
            if ("/favicon.ico".equals(uri.getPath())){
                System.out.println("请求了ico,不作响应");
                return;
            }
            //回复信息给浏览器(http协议)
            ByteBuf content= Unpooled.copiedBuffer("hello,我是服务器!", CharsetUtil.UTF_8);
            //构造一个http响应,即httpResponse
            FullHttpResponse httpResponse=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,content);
            //不设置utf-8会乱码
            httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain;charset=utf-8");
            httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH,content.readableBytes());
            //将构建好的即httpResponse返回
            ctx.writeAndFlush(httpResponse);
        }
    }
}
