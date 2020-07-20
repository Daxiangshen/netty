package com.netty.nio.heartbeat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * MyServerHandler Class
 *
 * ChannelInboundHandlerAdapter是SimpleChannelInboundHandler的父类
 *
 * @author : yuxiang
 * @date : 2020/7/7
 */
public class MyServerHandler extends ChannelInboundHandlerAdapter {


    //ctx 上下文   evt 事件
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //先确定是否是IdleStateEvent事件
        if (evt instanceof IdleStateEvent){
            //将evt向下转型IdleStateEvent
            IdleStateEvent event= (IdleStateEvent) evt;
            String eventType=null;
            switch (event.state()){
                case READER_IDLE:
                    eventType="读空闲";
                    break;
                case WRITER_IDLE:
                    eventType="写空闲";
                    break;
                case ALL_IDLE:
                    eventType="读写空闲";
                    break;
            }
            System.out.println(ctx.channel().remoteAddress()+"---超时事件:--"+eventType);
            System.out.println("服务器做相应处理..");
        }
    }
}
