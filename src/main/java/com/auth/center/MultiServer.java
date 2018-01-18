package com.auth.center;

import com.auth.center.handler.AuthCenterHander;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author
 * @create 2018-01-18 17:13
 **/
public class MultiServer {

    private List<Integer> ports;

    public MultiServer(List<Integer> ports) {
        this.ports = ports;
    }

    public void run() throws Exception {
        //EventLoopGroup是用来处理IO操作的多线程事件循环器
        //bossGroup 用来接收进来的连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //workerGroup 用来处理已经被接收的连接
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {

                            ch.pipeline().addLast(new AuthCenterHander());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            Collection<Channel> channels = new ArrayList<Channel>(ports.size());
            for (int port : ports) {
                Channel serverChannel = b.bind(port).sync().channel();
                channels.add(serverChannel);
            }
            for (Channel ch : channels) {
                ch.closeFuture().sync();
            }
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        List<Integer> ports = Arrays.asList(7788);
        new MultiServer(ports).run();
    }
}
