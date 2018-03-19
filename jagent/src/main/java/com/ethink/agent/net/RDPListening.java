package com.ethink.agent.net; 
import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ethink.agent.Config;
import com.ethink.agent.SerialNumber;
import com.ethink.agent.Starter;
import com.ethink.agent.event.Event;
import com.ethink.agent.event.EventHandler;

/* @类描述：RDP 接口监听
 * @date: 2017年10月16日
 * @author: dingfan
 *
 */
@Component
public class RDPListening  implements Starter { 
	
	private final static Logger log = LoggerFactory.getLogger(RDPListening.class);
	
	@Override
	public void start() {
		
		final String versionMsg = "301000|"+SerialNumber.getSerialNumber()+"|"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"|"+Config.get(Config.NEW_SOF_VER)+"|"+Config.get(Config.NEW_ADV_VER)+"|"+"";

		log.info("RDPListening+++++++++++++++star++++++++++++" );

		new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						String result = RDPMessageSend.send(versionMsg);
						log.info("RDP报文监听收到消息" + result);
						EventHandler.fireEvent(EventHandler.newEvent(Event.TYPE_RDPDATA, result));
					} catch (ConnectException e) {
						try {
							log.info("10秒后再次连接rdp");
							Thread.sleep(10000);
						} catch (InterruptedException e1) {
							log.error(null, e);
						}
						continue;
					}
					
					break;
				}
				log.info("RDPListening启动监听服务退出");
			}
		}).start();

		log.info("RDPListening++++++++++++++++++end+++++++++");

	}	     

	@Override
	public void stop() {
		
		
	}

}  
