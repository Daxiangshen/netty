package com.netty.nio.heartbeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * MyServer Class
 *
 * @author : yuxiang
 * @date : 2020/7/7
 */
public class MyServer {
    public static void main(String[] args) throws Exception{
        EventLoopGroup bossGroup=new NioEventLoopGroup();
        EventLoopGroup workerGroup=new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap=new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))//在bossGroup增加一个日志处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline=socketChannel.pipeline();
                            //加入netty提供的IdleStateHandler
                            /*
                            * 说明
                            * 1: IdleStateHandler是netty提供的处理空闲状态的处理器
                            * 2:long readerIdleTime  表示多长时间没有读,就会发送一个心跳检测包,检测是否还是连接的状态
                            * 3:long writerIdleTime  表示多长时间没有写,就会发送一个心跳检测包,检测是否还是连接的状态
                            * 4:long allIdleTime     表示多长时间既没有读也没有写,就会发送一个心跳检测包,检测是否还是连接的状态
                            * 5:当IdleStateHandler触发后,就会传递给pipeline的下一个handler去处理
                            *   通过调用下一个handler的UserEventTriggered,在该方法中处理IdleStateHandler(读空闲,写空闲,读写空闲)
                            * */
                            pipeline.addLast(new IdleStateHandler(3,5,7, TimeUnit.SECONDS));
                            //加入一个队空闲检测进一步处理的已定义handler
                            pipeline.addLast(new MyServerHandler());
                        }
                    });
            //启动服务器
            ChannelFuture channelFuture=serverBootstrap.bind(7000).sync();
            channelFuture.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
