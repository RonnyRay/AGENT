package com.ethink.agent.util;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ethink.agent.task.executor.rdp.RDPStartExecutor;

/* @类描述：tcp监听处理线程
 * @date: 2017年10月13日
 * @author: dingfan
 *
 */
public class ServerThread implements  Callable<StringBuffer> {  
    private Socket socket;  
    private BufferedReader in;
    private BufferedWriter out;
    private String rdpMessage=""; 
    private static  ExecutorService  pool = Executors.newCachedThreadPool();
	private final static Logger log = LoggerFactory.getLogger(ServerThread.class);
	
    public ServerThread(Socket socket,String  rdpMessage) {  
          this.socket=socket;  
          this.rdpMessage=rdpMessage;  
    } 
   
	@Override
	public StringBuffer call() throws Exception {		
		 StringBuffer content= new StringBuffer();
		try {  			             
            while (true) {             	
             
               log.info("通过输入流接收客户端信息  ");
               in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8")); 
               if (!"".equals(rdpMessage)) {               
               log.info("rdp 报文发送   ");
        	   OutputStreamWriter outSW = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");              	  
        	   out = new BufferedWriter(outSW); 
        	   out.write(rdpMessage.toString());
        	   out.flush();  
			    }
               int ch;
               while ((ch = in.read()) != -1) {
                   content.append((char) ch);
               }               
               in.close();
               socket.close();                      
               return content;
            }                                   
         } catch (IOException e) {  
        	 log.info(e.getMessage()+"rdp 报文发送失败  ");
         }
		return content; 		
	}

	public static String getdata(Socket socket2, String rdpMessage) {
		 String datas="";
		 Future<StringBuffer> submit = pool.submit(new ServerThread(socket2,"")); 
		 try {
		 datas=submit.get().toString();	
		 return datas;
			
		} catch (InterruptedException | ExecutionException e) {	
			 log.info(e.getMessage()+"线程出错 ");
		}
		 return datas;
	}  
}     

