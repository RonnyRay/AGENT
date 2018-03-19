package com.ethink.agent.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {
	
	private final static Logger LOG = LoggerFactory.getLogger(FileUtil.class);
	
	/**
	 * 文件重命名
	 * @param path
	 * @param oldname
	 * @param newname
	 */
	public static void renameFile(String path, String oldname, String newname) {
		if (!oldname.equals(newname)) {// 新的文件名和以前文件名不同时,才有必要进行重命名
			File oldfile = new File(path + File.separator + oldname);
			File newfile = new File(path + File.separator + newname);
			if (!oldfile.exists()) { 
				LOG.info("["+oldname+"]文件不存在,无法重命名..."); 
				return; 
			}
			if (newfile.exists()){
				LOG.info("["+newname+"]" + "已经存在！");
			}else{
				oldfile.renameTo(newfile);
				LOG.info("["+oldfile+"]文件已重命名为["+newfile+"]!"); 
			}
		} else {
			LOG.info("新文件名和旧文件名相同,无法重命名...");
		}
	}
	
	 /** 
     * 将存放在sourceFilePath目录下的源文件，打包成fileName名称的zip文件，并存放到zipFilePath路径下 
     * @param sourceFilePath :待压缩的文件路径 
     * @param zipFilePath :压缩后存放路径 
     * @param fileName :压缩后文件的名称 
     * @return 
     */  
	public static boolean fileToZip(String sourceFilePath, String zipFilePath, String fileName){  
		
	    boolean flag = false;  
	    File sourceFile = new File(sourceFilePath);      
	    FileOutputStream fos = null;  
	    ZipOutputStream zos = null;  
	      
	    if(sourceFile.exists() == false){  
	    	LOG.error("待压缩的文件目录："+sourceFilePath+"不存在.");  
	    }else{  
	        try {  
	            File zipFile = new File(zipFilePath + "/" + fileName +".zip");  
	            if(zipFile.exists()){  
	            	LOG.error(zipFilePath + "目录下存在名字为:" + fileName +".zip" +"打包文件.");  
	            }else{  
	                File[] sourceFiles = sourceFile.listFiles();  
	                if(null == sourceFiles || sourceFiles.length<1){  
	                	LOG.error("待压缩的文件目录：" + sourceFilePath + "里面不存在文件，无需压缩.");  
	                }else{  
	                    fos = new FileOutputStream(zipFile);  
	                    zos = new ZipOutputStream(new BufferedOutputStream(fos));  
	                    
	                    zip(zos, sourceFile, "");
	                    
	                    flag = true;  
	                }  
	            }  
	        } catch (FileNotFoundException e) {  
	            e.printStackTrace();  
	            throw new RuntimeException(e);  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	            throw new RuntimeException(e);  
	        } finally{  
	            //关闭流  
	            try {  
	                if(null != zos) zos.close();  
	            } catch (IOException e) {  
	                e.printStackTrace();  
	                throw new RuntimeException(e);  
	            }  
	        }  
	    }  
	    return flag;  
    }
	
    private static void zip(ZipOutputStream zos, File file, String base) throws IOException{
    	
        FileInputStream fis = null;  
        BufferedInputStream bis = null;  
        byte[] bufs = new byte[1024*10];

    	if(file.isDirectory()){ 
 
    		base += "\\";
    		System.out.println("base : " + base);
    		zos.putNextEntry(new ZipEntry(base));
 
    		File[] files = file.listFiles();
    		for (int i = 0; i < files.length; i++){
    			zip(zos, files[i], base + files[i].getName());
    		}
    		
    	}else{
    		System.out.println("base : " + base);
    		zos.putNextEntry(new ZipEntry(base));
    	    fis = new FileInputStream(file);  
            bis = new BufferedInputStream(fis, 1024*10);  
            int read = 0;  
            while((read=bis.read(bufs, 0, 1024*10)) != -1){  
                zos.write(bufs,0,read);  
            }
            
            fis.close();
            bis.close();
    	}
    }
    
	 /** 
     * 删除文件、文件夹 
     * @param path :待压缩的文件、文件夹路径
     * @return 
     */  
    public static void deleteFile(String path){
        
    	File file = new File(path);
    	
    	if(file == null || !file.exists()){
    		return;
    	}
    	
    	if(file.isDirectory()){
    		
    		File[] files = file.listFiles();
    		
    		for(File f : files){
    			deleteFile(f.getPath());
    		}
    	}
    	
    	file.delete();
    }
    
	public static void main(String[] args) {

	}
	
}
