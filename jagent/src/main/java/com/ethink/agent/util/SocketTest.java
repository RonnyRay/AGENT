package com.ethink.agent.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class SocketTest {
	public static void server() throws Exception {
        //1. 获取服务端通道
        ServerSocketChannel ssChannel = ServerSocketChannel.open();
        ssChannel.bind(new InetSocketAddress(60001));
        //2. 设置为非阻塞模式
        ssChannel.configureBlocking(false);
        
        //3. 打开一个监听器
        Selector selector = Selector.open();
        //4. 向监听器注册接收事件
        ssChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (selector.select() > 0) {
            //5. 获取监听器上所有的监听事件值
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();

            //6. 如果有值
            while (it.hasNext()) {
                //7. 取到SelectionKey
                SelectionKey key = it.next();

                //8. 根据key值判断对应的事件
                if (key.isAcceptable()) {
                    //9. 接入处理
                    SocketChannel socketChannel = ssChannel.accept();
                    socketChannel.configureBlocking(false);
                    String text = "小王八蛋,别发了收到了";
                    byte[] arr = text.getBytes("GBK");
                    socketChannel.write(ByteBuffer.wrap(arr));
                    socketChannel.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    //10. 可读事件处理
                    SocketChannel channel = (SocketChannel) key.channel();
                    readMsg(channel);
                    
                  
                }
                //11. 移除当前key
                it.remove();
            }
        }
    }

    private static void readMsg(SocketChannel channel) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(1024);
        int len = 0;
        while ((len = channel.read(buf)) > 0) {
            buf.flip();
            byte[] bytes = new byte[1024];
            buf.get(bytes, 0, len);           
            System.out.println(new String(bytes, 0, len,"GBK"));
        }
       
    }
public static void main(String[] args) {
	try {
		server();
	} catch (Exception e) {
		e.printStackTrace();
	}
}
}

