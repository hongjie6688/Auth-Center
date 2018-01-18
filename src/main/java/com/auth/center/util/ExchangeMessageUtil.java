package com.auth.center.util;

import com.fish.result.Result;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 信息交换工具类
 *
 * @author
 * @create 2018-01-09 18:54
 **/
public class ExchangeMessageUtil {
    private static final Logger logger = LoggerFactory.getLogger(ExchangeMessageUtil.class);

    /**
     * 发送处理后的结果数据
     *
     * @param ctx
     * @param str
     */
    public static void sendMsg(ChannelHandlerContext ctx, Result result) {
        if (result.getSendType() == Result.SendType.STRING) {
            sendString(ctx, result.getResult());
        }
        if (result.getSendType() == Result.SendType.XML) {
            sendXmlMsg(ctx, result.getResult());
        }
    }

    /**
     * 发送处理后的结果数据
     *
     * @param ctx
     * @param str
     */
    public static void sendString(ChannelHandlerContext ctx, String str) {
        ByteBuf encoded = ctx.alloc().buffer(4 * str.length());
        encoded.writeBytes(str.getBytes());
        ctx.write(encoded);
        ctx.flush();
    }


    /**
     * 发送处理后的结果数据,  并关闭连接
     *
     * @param ctx
     * @param str
     */
    public static void sendMsgAndCloseConnection(ChannelHandlerContext ctx, String str) {
        ByteBuf encoded = ctx.alloc().buffer(4 * str.length());
        encoded.writeBytes(str.getBytes());
        ctx.write(encoded);
        ctx.flush();
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 发送xml 格式的数据
     *
     * @param ctx
     * @param xml
     */
    public static void sendXmlMsg(ChannelHandlerContext ctx, String xml) {
        ByteBuf encoded = ctx.alloc().buffer(2 * xml.length() + 17);
        byte[] xmlByte = xml.getBytes();
        encoded.writeInt(263442);
        encoded.writeInt(16 + 5 + xmlByte.length);
        encoded.writeInt(0);
        encoded.writeInt(0);
        encoded.writeByte(0);
        encoded.writeBytes(xmlByte);
        ctx.write(encoded);
        ctx.flush();
        logger.info("send Echo to client,xml:" + xml);
    }


    /**
     * 发送ping 数据
     *
     * @param ctx
     */
    public static void sendPing(ChannelHandlerContext ctx) {
        ByteBuf encode = ctx.alloc().buffer(12);
        encode.writeInt(1);
        encode.writeInt(12);
        encode.writeInt(0);
        ctx.write(encode);
        ctx.flush();
        logger.info("send Ping to client...");
    }

    public static void sendEcho(ChannelHandlerContext ctx, String acount, String token) {
        ByteBuf encoded = ctx.alloc().buffer(2 * (acount.length() + token.length()) + 12);
        byte[] name = acount.getBytes();
        byte[] pass = token.getBytes();

        encoded.writeInt(131074);
        encoded.writeInt(16);
        encoded.writeInt(0);
        encoded.writeInt(0);
        ctx.write(encoded);
        ctx.flush();
        logger.info("send Echo to client,acount:" + acount + ",token:" + token);
    }

}
