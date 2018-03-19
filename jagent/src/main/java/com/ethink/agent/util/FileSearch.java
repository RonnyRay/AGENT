package com.ethink.agent.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/* @类描述:查找文件
 * @date: 2017年11月6日
 * @author: dingfan
 *
 */
public class FileSearch {
	  	  	  
    /**   
     * 递归查找文件   
     * @param baseDirName  查找的文件夹路径   
     * @param targetFileName  需要查找的文件名   
     * @param fileList  查找到的文件集合   
     */ 
	 private static String tempFile="C:\\tempFile";
	 private final static Logger log = LoggerFactory.getLogger(FileSearch.class);
	 public static void findFiles(String baseDirName, String targetFileName,  List<String> fileList,boolean flag) {     
		 log.info("查找符合要去文件");    
        File baseDir = new File(baseDirName);       // 创建一个File对象  
        if (!baseDir.exists() || !baseDir.isDirectory()) {  // 判断目录是否存在  
            System.out.println("文件查找失败：" + baseDirName + "不是一个目录！");  
        }  
        String tempName = null;     
        //判断目录是否存在     
        File tempFiles;  
        File[] files = baseDir.listFiles();  
        for (int i = 0; i < files.length; i++) {  
            tempFiles = files[i]; 
            log.info("搜索所有符合文件");
           if (flag) {
        	   if(tempFiles.isDirectory()){  
                   findFiles(tempFiles.getAbsolutePath(), targetFileName, fileList,flag);  
               }else if(tempFiles.isFile()){  
                   tempName = tempFiles.getName();  
                   if(wildcardMatch(targetFileName, tempName)){  
                       // 匹配成功，将文件名添加到结果集  
                       fileList.add(tempFiles.getAbsoluteFile().toString());  
                   }  
               } 
		   } 
           else {  
        	    log.info("不进行子目录速搜");
                tempName = tempFiles.getName();  
                if(wildcardMatch(targetFileName, tempName)){  
                     // 匹配成功，将文件名添加到结果集  
                fileList.add(tempFiles.getAbsoluteFile().toString());  
                   }  
               
           }                 
        }  
    }     
	         
	/**   
	 * 通配符匹配   
	 * @param pattern    通配符模式   
	 * @param str    待匹配的字符串   
	 * @return    匹配成功则返回true，否则返回false   
	 */    
	private static boolean wildcardMatch(String pattern, String str) {     
	    int patternLength = pattern.length();     
	    int strLength = str.length();     
	    int strIndex = 0;     
	    char ch;     
	    for (int patternIndex = 0; patternIndex < patternLength; patternIndex++) {     
	        ch = pattern.charAt(patternIndex);     
	        if (ch == '*') {     
	            //通配符星号*表示可以匹配任意多个字符     
	            while (strIndex < strLength) {     
	                if (wildcardMatch(pattern.substring(patternIndex + 1),     
	                        str.substring(strIndex))) {     
	                    return true;     
	                }     
	                strIndex++;     
	            }     
	        } else if (ch == '?') {     
	            //通配符问号?表示匹配任意一个字符     
	            strIndex++;     
	            if (strIndex > strLength) {     
	                //表示str中已经没有字符匹配?了。     
	                return false;     
	            }     
	        } else {     
	            if ((strIndex >= strLength) || (ch != str.charAt(strIndex))) {     
	                return false;     
	            }     
	            strIndex++;     
	        }     
	    }     
	    return (strIndex == strLength);     
	}   

	/**   
	 * 筛选目标文件   
	 * @param resultList  符合条件文件路径  
	 * @param cmpType    比较方式  
	 * @param start      文件最长度小/最小创建时间、最小修改时间
	 * @param end        文件最大长度/最大创建时间、最大修改时  
	 */ 
	public static void selectFile(List<String> resultList, int cmpType, String start, String end) {
		if (cmpType==0) {
			log.info("按照文件创建时间匹配");
			for (int i = 0; i < resultList.size(); i++) {
				boolean flag = byCreateTime(resultList.get(i),start,end );
				if (flag) {
					try {
						FetchUrl.copyFolder( new File(resultList.get(i)),new File(tempFile));
					} catch (IOException e) {
						log.info("文件复制失败");
					}				  				  				   
			  } 
			}
		}			
		
		else if(cmpType==1) {
			log.info("按照文件修改时间匹配");
			for (int i = 0; i < resultList.size(); i++) {
				boolean flag =byModifiedTime(new File(resultList.get(i)),start,end );		
				if (flag) {
					try {
						FetchUrl.copyFolder( new File(resultList.get(i)),new File(tempFile));
					} catch (IOException e) {
						log.info("文件复制失败");
					}	
			   }		
		    }
		}
        else {
        	log.info("按照文件大小匹配");
        	for (int i = 0; i < resultList.size(); i++) {
        		boolean flag =byFileSize(new File(resultList.get(i)),start,end );	
        		if (flag) {
    				try {
    					FetchUrl.copyFolder( new File(resultList.get(i)),new File(tempFile));
    				} catch (IOException e) {
    					log.info("文件复制失败");
    			    }	
        	
	           }	
          }
       }
	}
	
	

	public static void main(String[] args) {  
			//    在此目录中找文件     
			String baseDIR = "e:/temp";      
			//    找扩展名为txt的文件     
			String fileName = "df";      
			List<String> resultList = new ArrayList<String>();  
			findFiles(baseDIR, fileName,resultList,true);      
			if (resultList.size() == 0) {     
				System.out.println("No File Fount.");     
			} else {     
				for (int i = 0; i < resultList.size(); i++) {     
					System.out.println(resultList.get(i));//显示查找结果。      
				}     
			}     
					    	         
		   
	}
	
	
	 /** 
     * 根据创建时间 筛选
	 * @param end 
	 * @param start 
     */  
	private static boolean byCreateTime(String filepath, String start, String end){  
		   Path path=Paths.get(filepath);    
		   BasicFileAttributeView basicview=Files.getFileAttributeView(path, BasicFileAttributeView.class,LinkOption.NOFOLLOW_LINKS );  
		   BasicFileAttributes attr;  		 
		       try {
				attr = basicview.readAttributes();
				long createDate =attr.creationTime().toMillis();  
				if (createDate>=Long.parseLong(start) && createDate<=Long.parseLong(end)) {
					return true;
				}				 		  
			} catch (IOException e) {
				log.info("获取文件创建时间异常");				
			}  
		  return false ;  
		}  
	
    
	/*  
     * 按照文件创建修改筛选文件
     */    
    public static Boolean byModifiedTime(File file, String start, String end){                   
        long modifyTime = file.lastModified();
        if (modifyTime>=Long.parseLong(start) && modifyTime<=Long.parseLong(end)) {       	
        	return true; 				
		}
		return false;
    }  
      
    
	/*
	 * 根据文件大小 筛选文件
	 */
    public static boolean byFileSize(File file, String start, String end ) {        
	   long length = file.length();  
	   if (length>=Long.parseLong(start) && length<=Long.parseLong(end)) {            		       
	       return true; 				
	    }	    	  
	  return false;    
    }
 }

