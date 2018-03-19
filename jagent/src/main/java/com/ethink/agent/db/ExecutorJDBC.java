package com.ethink.agent.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ethink.agent.Config;

/**
 * @类描述 更新数据库结构
 * @创建时间 2017年10月30日
 * @author wangluliang
 */
public class ExecutorJDBC {
	
	private static final String CLASSNAME = "org.sqlite.JDBC";
	private static final String DBURL = "jdbc:sqlite:./agent.db";

	private final static Logger log = LoggerFactory.getLogger(ExecutorJDBC.class);
	
	private static Connection conn;
		
	
	/**
	 * 执行操作语句
	 * @param sql
	 * @return
	 */
	public static int executorSql(String sql){
		try {
			
			Statement state = getConnect().createStatement();
			int result = state.executeUpdate(sql);
			state.close();
			return result;
		} catch (Exception e) {
			log.error("数据库操作失败！",e);
			return -1;
		}
	}
	
	private static Connection getConnect() throws Exception{
		Class.forName(CLASSNAME);
		conn = DriverManager.getConnection(DBURL);
		return conn;
	}
	private static void close() throws Exception{
		if(conn!=null){
			conn.close();
		}
	}
	
	
	public  void executorJDBC(){
		String agentPath = Config.get(Config.AGENT_PATH);
		File agentFile = new File(agentPath+"/agent-update/ALERT_TABLE.sql");
		if(agentFile.exists()){
			log.info("---------开始更新数据库结构-------------");
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(agentPath+"/agent-update/ALERT_TABLE.sql"), "UTF-8"));
				String sql = null;
				while((sql=br.readLine())!=null){
					log.info("更新表结构,sql语句："+sql);
					int re = executorSql(sql);
					if(re!=-1){
						log.info("更新表结构成功,影响行数："+re+",sql:"+sql);
					}else{
						log.info("更新表结构失败,sql:"+sql);
					}
				}
			} catch (Exception e) {
				log.error("读取数据库脚本失败",e);
			}finally {
				try {
					close();
					if(br!=null)
						br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}else{
			log.info("---------数据库结构不需要更新-------------");
		}
		
	}


}
