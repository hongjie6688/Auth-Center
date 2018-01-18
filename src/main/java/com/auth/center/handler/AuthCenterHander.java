package com.auth.center.handler;


import com.auth.center.util.SocketUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * 连接处理类
 *
 * @author zhanghongjie
 */
public class AuthCenterHander extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // netty 中没有 session 的概念， 有 channel
        int port = SocketUtil.getSocketProt(ctx);
        ByteBuf in = (ByteBuf) msg;
        switch (port) {
            case 7788:
                int len = 0;
                int cmd = 0;
                int seq = 0;
                // 这一部分不要写到这里， 写到单独类里边去， 把类通过spring 去管理  这样以后扩展性会好一地
                cmd = in.readInt();
                len = in.readInt();
                seq = in.readInt();
                switch (cmd) {
                    case 65538:
                        byte[] rslt = new byte[30];
                        in.readBytes(rslt);
                        String acount = "";
                        acount = (new String(rslt)).trim();
                        //in.setIndex(42,len);
                        ByteBuf rest = ctx.alloc().buffer(len - 42);
                        in.readBytes(rest, len - 42);
                        byte[] tk = new byte[len - 42];
                        rest.readBytes(tk);
                        String token = "";
                        token = new String(tk);
                        Map param = new HashMap();
                        param.put("acount", acount);
                        param.put("token", token);
                        rest.release();
                        break;
                    default:
                        System.out.println("");
                }
                break;
            default:
                System.out.println("");
        }
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace(); // 打印异常
        ctx.close(); // 关闭通道
    }

    /**
     * 发送处理后的结果数据
     *
     * @param ctx
     * @param str
     */
    private void sendString(ChannelHandlerContext ctx, String str) {
        ByteBuf encoded = ctx.alloc().buffer(4 * str.length());
        encoded.writeBytes(str.getBytes());
        ctx.write(encoded);
        ctx.flush();
    }


    /**
     * 解析收到的信息
     *
     * @param msg
     * @return
     */
    private String getReceiveMsg(Object msg) {
        ByteBuf result = (ByteBuf) msg;
        byte[] result1 = new byte[result.readableBytes()];
        // msg中存储的是ByteBuf类型的数据，把数据读取到byte[]中
        result.readBytes(result1);
        String resultStr = new String(result1);
        // 释放资源，这行很关键
        result.release();
        return resultStr;
    }

    /**
     * 解析收到的utfbytes
     *
     * @param body
     * @return
     */
    private String getUTFBody(byte[] body) {
        String resultStr = new String(body);
        body = null;
        return resultStr;
    }

    private String getToken(byte[] body) {
        String resultStr = new String(body);
        body = null;
        return resultStr;
    }

    /**
     * Convert byte[] to hex string
     *
     * @param src byte[] data
     * @return hex string
     */
    private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    private String byteArrayToHexStr(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[byteArray.length * 2];
        for (int j = 0; j < byteArray.length; j++) {
            int v = byteArray[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private String byteArrayToHexString(byte[] array) {
        StringBuffer hexString = new StringBuffer();
        for (byte b : array) {
            int intVal = b & 0xff;
            if (intVal < 0x10) {
                hexString.append("0");
            }
            hexString.append(Integer.toHexString(intVal));
        }
        return hexString.toString();
    }

}
