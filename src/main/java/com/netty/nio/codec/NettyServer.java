package com.netty.nio.codec;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;

/**
 * NettyServer Class
 *
 * @author : yuxiang
 * @date : 2020/6/23
 */
public class NettyServer {
    public static void main(String[] args) throws InterruptedException {
        //创建BossGroup和WorkerGroup
        //bossGroup只是处理连接请求,和客户端业务处理交给workerGroup
        //两个都是无限循环
        EventLoopGroup bossGroup=new NioEventLoopGroup(); //含有NioEventLoop子线程个数默认cpu核数*2
        EventLoopGroup workerGroup=new NioEventLoopGroup();
        try {
            //创建服务器端的启动对象,配置参数
            ServerBootstrap bootstrap=new ServerBootstrap();
            //使用链式编程进行设置
            bootstrap.group(bossGroup,workerGroup)//设置两个线程组
                    .channel(NioServerSocketChannel.class)//使用NioServerSocketChannel作为服务器的通道实现
                    .option(ChannelOption.SO_BACKLOG,128)//设置线程队列等待连接的个数
                    .childOption(ChannelOption.SO_KEEPALIVE,true)//设置保持活动连接状态
                    //.handler()  该handler对应的是bossGroup
                    .childHandler(new ChannelInitializer<SocketChannel>() {//创建一个通道测试对象(匿名对象)
                        //给pipeline设置处理器
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline channelPipeline=socketChannel.pipeline();
                            //在pipeline中加入ProtoBufDecoder
                            //指定对哪种对象进行解码
                            channelPipeline.addLast("decoder",new ProtobufDecoder(StudentPOJO.Student.getDefaultInstance()));
                            channelPipeline.addLast(new NettyServerHandler()); //该handler对应的是workerGroup
                        }
                    });//给workerGroup的EventLoop对应的管道设置处理器
            System.err.println("服务器 is ready");

            //绑定一个端口并且同步,生成了一个ChannelFuture对象
            //启动服务器
            ChannelFuture channelFuture=bootstrap.bind(6668).sync();
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()){
                        System.out.println("监听端口6668成功");
                    }else {
                        System.out.println("监听端口6668失败");
                    }
                }
            });
            //对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
