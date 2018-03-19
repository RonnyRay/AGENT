package com.ethink.agent.util;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ethink.agent.Config;



/* @类描述：通过url下载文件并将文件存放指定目录
 * @date: 2017年10月17日
 * @author: dingfan
 * @param httpUrl 请求地址
 * @param fileSavePath 文件保存地址
 * @param fileName  文件名
 * 
 */

public class FetchUrl {	
	private static String fileSavePath="d:/local_download";
	final static String downloadResult[] =new String[3];
	private final static Logger log = LoggerFactory.getLogger(HttpUtil.class);
    public static File fecthFile(String httpUrl,String fileName)throws MalformedURLException, IOException {    	
    	downloadResult[0] = "";
    	downloadResult[1]="0";
    	downloadResult[2]="0";
    	final Timer timer = new Timer();  
        timer.scheduleAtFixedRate(new TimerTask() {  
            public void run() {  
            	if (!"Ok".equals(downloadResult[0])) {
            		  log.info("下载超时……………………………………"+"百分比为"+downloadPercent()+"%"+"………………………………………………");        								
    				downloadResult[2]="downloadPercent()"+"%";    				
    			}
    			else {	   			
    				log.info("下载成功…………………………"+"百分比为"+downloadPercent()+"%"+"…………………………………………………………");
    				timer.cancel();
    			}
            }  
        },300000, 300000);
        log.info("开始下载文件啦++++++++++++++++++++++++++++++");       
        // 打开输入流
        BufferedInputStream in = new BufferedInputStream(
            getInputStream(httpUrl));
      
        File file1 = new File(fileSavePath+"\\"+"无实际意思");
        if (!file1.getParentFile().mkdirs()) {           	
        }               
        File file = new File(fileSavePath +"\\"+ fileName);
        //文件大小
        downloadResult[1]= file.length()+"";        
        if (!file.exists()) {        	
            file.createNewFile();         
        }  
        // 打开输出流
        FileOutputStream out = new FileOutputStream(file);
        byte[] buff = new byte[1];
        // 读取数据
        while (in.read(buff) > 0) {
            out.write(buff);
        }
        out.flush();
        out.close();
        in.close();
        //文件是否下载成功
        downloadResult[0] = "Ok";
       log.info("文件下载成功………………………………………………………………………………");
        return file;
    }

  
   //获取url中的文件流
    private static InputStream getInputStream(String httpUrl) throws
        IOException {
        // 网页Url
        URL url = new URL(httpUrl);
        URLConnection ur = url.openConnection();
        ur.setRequestProperty("Content-Type", "text/plain; charset=utf-8");     
        return ur.getInputStream();
    }
    
    
  
   /* @方法描述：递归方式 计算文件的大小
    * @date: 2017年10月17日
    * @author: dingfan
    * @param file 文件
    * 
    */
   public static long getFile( File file) {
       if (file.isFile())
           return file.length();
       final File[] children = file.listFiles();
       long total = 0;
       if (children != null)
           for (final File child : children)
               total += getFile(child);
       return total;
   }   
   
   /* @方法描述：得到下载百分比
    * @date: 2017年10月17日
    * @author: dingfan
    * 
    */
   public static String downloadPercent() {	
	   log.info("获得下载百分比………………………………………………………………………………");
	int downloadSize= new Long(getFile(new File(fileSavePath))).intValue(); 	        				
	int fileTotalSize= new Long(downloadResult[1]).intValue();	        					        				        					        	  
	String downloadPercent = new DecimalFormat("0.00").format((float)downloadSize/fileTotalSize*100);  
	return downloadPercent;	
   }
   
   /* @方法描述：复制文件
    * @date: 2017年10月17日
    * @author: dingfan
    * @param file 文件
    * @param file 文件
    */
   public static void copyFolder(File filePath, File destFile) throws IOException {  
	   log.info("文件复制………………………………………………………………………………");
	   //判断文件是否是文件夹如果不是直接下载,如果是则进行文件夹文件递归复制 
	    if(filePath.isDirectory()){  
	        File newFolder=new File(destFile,filePath.getName());  	       
	        newFolder.mkdirs();  
	        File[] fileArray=filePath.listFiles();  	          
	        for(File file:fileArray){  
	            copyFolder(file, newFolder);  
	        }  	          
	    }else{  
	        File newFile=new File(destFile,filePath.getName());  
	        copyFile(filePath,newFile);  
	    } 
	    log.info("文件复制完成………………………………………………………………………………");
	}  

   
   /* @方法描述：复制文件
    * @date: 2017年10月17日
    * @author: dingfan
    * @param srcFile 文件原始地址
    * @param newFile 文件复制到的位置
    */
	private static void copyFile(File srcFile, File newFile) throws IOException{  
	    BufferedInputStream bis=new BufferedInputStream(new FileInputStream(srcFile));  
	    BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(newFile));  	      
	    byte[] bys=new byte[1024];  
	    int len=0;  
	    while((len=bis.read(bys))!=-1){  
	        bos.write(bys,0,len);  
	    }  
	    bos.close();  
	    bis.close();  	      
	}  
 
	
	
	public static void main(String[] args) {
	
	try {
		unZipFiles(new File("d:/a/abcd.zip"),"d:/s");
	} catch (IOException e) {
		System.out.println("解压失败");
	}
	System.out.println("解压成功");
	}
	
	public static void unZipFiles(String zipPath,String descDir)throws IOException{  
        unZipFiles(new File(zipPath), descDir);  
    }  
    /** 
     * 解压文件到指定目录 
     * @param zipFile 
     * @param descDir 
     * @author isea533 
     */  
    @SuppressWarnings("rawtypes")  
    public static void unZipFiles(File zipFile,String descDir)throws IOException{  
        File pathFile = new File(descDir);  
        if(!pathFile.exists()){  
            pathFile.mkdirs();  
        }  
        ZipFile zip = new ZipFile(zipFile);  
        for(Enumeration entries = zip.entries();entries.hasMoreElements();){  
            ZipEntry entry = (ZipEntry)entries.nextElement();  
            String zipEntryName = entry.getName();  
            InputStream in = zip.getInputStream(entry);  
            String outPath = (descDir+zipEntryName).replaceAll("\\*", "/");;  
            //判断路径是否存在,不存在则创建文件路径  
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));  
            if(!file.exists()){  
                file.mkdirs();  
            }  
            //判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压  
            if(new File(outPath).isDirectory()){  
                continue;  
            }  
            //输出文件路径信息  
            System.out.println(outPath);  
              
            OutputStream out = new FileOutputStream(outPath);  
            byte[] buf1 = new byte[1024];  
            int len;  
            while((len=in.read(buf1))>0){  
                out.write(buf1,0,len);  
            }  
            in.close();  
            out.close();  
            }  
        System.out.println("******************解压完毕********************");  
    }  
    
    
    
}
