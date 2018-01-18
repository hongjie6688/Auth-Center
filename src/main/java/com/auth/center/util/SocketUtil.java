package com.auth.center.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * socket相关的公共方法
 *
 * @author
 * @create 2018-01-09 19:35
 **/
public class SocketUtil {
    private static final Logger logger = LoggerFactory.getLogger(SocketUtil.class);
    public final static String CHARSET = "utf-8";

    public static int getSocketProt(ChannelHandlerContext ctx) {
        logger.info("incoming addr:->" + ctx.channel().localAddress());
        String addr = ctx.channel().localAddress().toString();
        int port = Integer.parseInt(addr.split(":")[1]);
        return port;
    }

    public static JSONObject getParamterJsonObject(ByteBuf in) throws UnsupportedEncodingException {
        int len = 0;
        int cmd = 0;
        int seq = 0;
        byte ver = 0;
        byte log = 0;
        byte fmt = 0;
        byte type = 0;
        int id = 0;
        len = in.readInt();
        ver = in.readByte();
        log = in.readByte();
        fmt = in.readByte();
        type = in.readByte();
        id = in.readInt();
        byte[] body = new byte[len - 12];
        in.readBytes(body);
        in.release();
        String ubody = new String(body, CHARSET);
        logger.info("data in:" + ubody);
        return JSON.parseObject(ubody);
    }

    public static String getXml() {
        String xml = "<cross-domain-policy> "
                + "<allow-access-from domain=\"*\" to-ports=\"3366,7788\"/>"
                + "</cross-domain-policy> ";
        return xml;
    }


}
