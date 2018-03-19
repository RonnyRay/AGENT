package com.ethink.agent.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ethink.agent.Config;
import com.ethink.agent.Starter;
import com.ethink.agent.event.Event;
import com.ethink.agent.event.EventHandler;
import com.ethink.agent.util.ServerThread;

/**
 * @类描述 Tcp报文主动监听
 * @创建时间 2017年10月12日
 * @author wangluliang
 */
@Component
public class TcpListening implements Starter {

	Boolean flag = true;
	ServerSocket server;
	private final static Logger log = LoggerFactory.getLogger(TcpListening.class);

	/**
	 * 启动Tcp报文主动监听
	 */
	@Override
	public void start() {
		flag = true;
		try {
			String taskServer = Config.get(Config.TASK_SERVER);
			String listenTaskPort = Config.get(Config.LISTEN_TASK_PORT);

			server = new ServerSocket(Integer.parseInt(listenTaskPort));

			log.info("TCP报文监听启动,port " + listenTaskPort);

			// final Socket socket[] = new
			// Socket[1];//匿名内部类传入变量需要final,定义final数组,数组内的值可变
			// 使用线程防止主线程被accept()阻塞

			new Thread(new Runnable() {
				private Socket socket = null;

				public void run() {
					while (flag) {
						try {
							socket  = server.accept();
							String data = ServerThread.getdata(socket , "");
							log.info("TCP报文监听收到消息,报文为" + data);
							if(data!=null&&!("".equals(data))){
								// 抛出事件
								EventHandler.fireEvent(EventHandler.newEvent(Event.TYPE_TCPDATA, data.substring(6)));
							}else{
								log.info("TCP报文监听消息为空,不作处理");
							}

						} catch (Exception e) {
							log.error("TCP报文监听接收数据失败!监听可能被关闭");
						} finally {
							try {
								if (socket  != null) {
									socket .close();
								}
							} catch (IOException e) {
								log.error("TCP报文监听socket关闭失败!", e);
							}
						}
					}
					return;
				}
			}).start();
		} catch (Exception e) {
			log.error("TCP报文监听启动失败!", e);
		}
	}

	@Override
	public void stop() {
		flag = false;
		if(server==null){
			return;
		}
		try {
			server.close();
		} catch (IOException e) {
			log.error("TCP报文监听server关闭失败!", e);
		}
	}

	/** 遗弃的方法 */
	public void handlerTcp(Socket socket) {
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			StringBuffer sb = new StringBuffer();
			String info = null;
			bw.write("你好啊");
			bw.flush();
			isr = new InputStreamReader(socket.getInputStream(), "utf-8");
			br = new BufferedReader(isr);
			while ((info = br.readLine()) != null) {
				sb.append(info);
			}
			System.out.println(sb.toString());

		} catch (Exception e) {
			log.error("TcpListening loading data fail!", e);
		} finally {
			try {
				isr.close();
				br.close();
				socket.close();
			} catch (IOException e) {
				log.error("TcpListening socket close fail!", e);
			}
		}

	}

	public static void main(String[] args) {
		new TcpListening().start();
	}

}
