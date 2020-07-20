package com.netty.nio.groupchat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;


/**
 * GroupChatServerHandler Class
 *
 * @author : yuxiang
 * @date : 2020/7/6
 */
public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {

    //定义一个channel组,管理所有的channel(每个客户端都有自己独立的Handler,所以要加static,因为此线程组要所有Handler共享)
    //GlobalEventExecutor是一个全局的事件执行器,是一个单例
    private static ChannelGroup channelGroup=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);



    //handlerAdded表示连接建立,一旦连接,第一个被执行
    //将当前channel加入到channelGroup
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel=ctx.channel();
        //将该客户加入聊天的信息推送给其它在线的客户
        //该方法会将channelGroup中所有的channel遍历并发送消息,因此无需我们自己遍历
        channelGroup.writeAndFlush("[客户端]"+channel.remoteAddress()+"加入聊天\n");
        channelGroup.add(channel);
    }

    //断开连接,将xx客户离开信息推送给当前在线的客户
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel=ctx.channel();
        //不需要执行 channelGroup.remove(channel);  也就是说还不需要去channelGroup中移除,handlerRemoved方法自动移除
        channelGroup.writeAndFlush("[客户端]"+channel.remoteAddress()+"离开聊天\n");
    }

    //表示channel处于活动状态,在服务端提示xx上线
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress()+"上线了~");
    }

    //表示channel处于非活动状态,在服务端提示xx下线
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress()+"下线了~");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        //获取到当前channel
        Channel channel=ctx.channel();
        //这时我们遍历channelGroup,根据不同的情况处理不同的消息(分开自己和其他人)
        channelGroup.forEach(ch->{
            if (channel!=ch){
                //说明不是当前channel,直接转发消息,将当前通道的信息转发给其他人
                ch.writeAndFlush("[客户]"+channel.remoteAddress()+"发送消息:"+msg+"\n");
            }else {
                //回显自己发送的消息给自己
                ch.writeAndFlush("[自己]发送了消息:"+msg+"\n");
            }
        });
    }

    //处理异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //关闭通道
        ctx.close();
    }
}
