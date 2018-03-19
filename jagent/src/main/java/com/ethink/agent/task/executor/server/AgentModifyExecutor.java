package com.ethink.agent.task.executor.server;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ethink.agent.Agent;
import com.ethink.agent.Config;
import com.ethink.agent.annotation.TaskExecutors;
import com.ethink.agent.decode.HttpEncoder;
import com.ethink.agent.net.SendMessage;
import com.ethink.agent.net.TcpListening;
import com.ethink.agent.task.bean.ServerTask;
import com.ethink.agent.task.bean.Task;
import com.ethink.agent.task.executor.TaskExecutor;
import com.ethink.agent.util.BeanUtil;
import com.ethink.agent.util.TimeUtil;

/**
 * @类描述 AGENT参数修改执行器
 * @创建时间 2017年10月30日
 * @author wangluliang
 */
/**
 * @类描述
 * @创建时间 2017年10月31日
 * @author wangluliang
 */
@TaskExecutors(value = "agentModifyAct")
public class AgentModifyExecutor implements TaskExecutor {

	String taskServer = Config.get(Config.TASK_SERVER);

	// 需要发送的字段
	String filterField[] = { "tasktype", "planid", "taskid", "termid", "taskstatus", "percent", "extradescription" };

	private final static Logger log = LoggerFactory.getLogger(AgentModifyExecutor.class);

	/** 响应接口 */
	String url = taskServer + "/task/shutdownAct.ebf";

	/** 参数映射表 */
	Map<String, Object> mapping = new HashMap<String, Object>();

	/** 需刷新的参数 */
	String refreshParams[] = { "agentport", "tasksvrip", "tasksvrport" };

	public AgentModifyExecutor() {
		mapping.put("agentport", Config.LISTEN_TASK_PORT);
		mapping.put("tasksvrip", Config.TASK_SERVER);
		mapping.put("tasksvrport", Config.TASK_SERVER);
		mapping.put("pushsvrip", Config.PUSHSVRIP);
		mapping.put("pushsvrport", Config.PUSHSVRPORT);
		mapping.put("ejsvr", Config.EJSVR);
		mapping.put("ejsvrport", Config.EJSVRPORT);
		mapping.put("amsip", Config.AMSIP);
		mapping.put("amstcpport", Config.AMSTCPPORT);
		mapping.put("amsudpport", Config.AMSUDPPORT);
		mapping.put("sendinterval", Config.SENDINTERVAL);
		mapping.put("ejupload", Config.EJUPLOAD);
		mapping.put("ejonly", Config.EJONLY);
		mapping.put("ejtorlerant", Config.EJTORLERANT);
		mapping.put("ejhddtolerant", Config.EJHDDTOLERANT);
		mapping.put("transinfobatch", Config.TRANSINFOBATCH);
		mapping.put("filelimit", Config.FILELIMIT);
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Task task = (Task) context.getJobDetail().getJobDataMap().get("taskData");
		// 判断任务是否符合
		if (isAgentModify(task)) {
			ServerTask serverTask = (ServerTask) task;
			try {
				// 获取参数
				Map<String, Object> configMap = getConfigMap(serverTask);

				// 设置config参数
				boolean flag = configSetParams(configMap);

				// agent是否需要刷新
				boolean restart = needRefresh(serverTask);
				log.info("agent是否需要刷新:"+restart);
				// 上送任务状态
				if (flag) {
					// 返回任务状态等数据
					if(restart){
						log.info("agent参数修改成功,即将刷新agent");
						//刷新所需刷新的
						Agent.findBean(TcpListening.class).stop();
						Agent.findBean(TcpListening.class).start();
						log.info("agent刷新成功");
					}
					log.info("agent参数修改成功");
				}else{
					log.info("agent参数修改数据出现错误");
				}
				uploadMess(setTaskStatus(serverTask, "6", "100,", "执行成功"));
			} catch (Exception e) {
				log.error("agent参数修改失败", e);
				throw new JobExecutionException("agent参数修改失败", e);
			}

		}
	}

	private boolean needRefresh(ServerTask serverTask) throws Exception {
		Class<? extends ServerTask> clazz = serverTask.getClass();
		// 获得映射表的迭代器
		for(String param : refreshParams){
			Field field = clazz.getDeclaredField(param);
			// 获得属性的值
			field.setAccessible(true);
			if(field.get(serverTask)!=null){
				return true;
			}
		}
		return false;
	}

	/**
	 * 修改参数
	 * 
	 * @param configMap
	 * @return
	 */
	private boolean configSetParams(Map<String, Object> configMap) {
		boolean flag = true;
		for (String key : configMap.keySet()) {
			int re = Config.set(key, (String) configMap.get(key));
			log.info("修改config参数;name:"+key+";value:"+configMap.get(key));
			flag = (re == -1) ? false : flag;
		}
		;
		return flag;
	}

	/**
	 * 通过映射表自动找出所需设置的config参数
	 * 
	 * @param serverTask
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	private Map<String, Object> getConfigMap(ServerTask serverTask) throws Exception {
		Map<String, Object> configMap = new HashMap<String, Object>();
		Class<? extends ServerTask> clazz = serverTask.getClass();
		// 获得映射表的迭代器
		Iterator<Entry<String, Object>> iterator = mapping.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Object> entry = iterator.next();
			if (entry.getKey() != null && !("".equals(entry.getKey()))) {
				Field field = clazz.getDeclaredField(entry.getKey());
				// 获得属性的值
				field.setAccessible(true);
				if (field.get(serverTask) != null) {
					String configName = (String) entry.getValue();
					//ip与port
					if(configMap.get(configName)!=null){
						String second = (String) field.get(serverTask);
						String first = (String) configMap.get(configName);
						//比较长度，谁长谁是头
						String serverURL = (first.length()>second.length())?"http://"+first+":"+second:"http://"+second+":"+first;
						configMap.put(configName, serverURL);
					}else{
						configMap.put(configName, field.get(serverTask));
						
					}
				}
			}
		}

		return configMap;
	}

	private ServerTask setTaskStatus(ServerTask serverTask, String taskstatus, String percent,
			String Extradescription) {
		serverTask.setTaskstatus(taskstatus);
		serverTask.setPercent(percent);
		serverTask.setExtradescription(Extradescription);
		return serverTask;
	}

	/**
	 * 上送报文
	 * 
	 * @param serverTask
	 */
	private void uploadMess(ServerTask serverTask) {
		try {
			// 过滤字段
			ServerTask serverTaskFilter = (ServerTask) BeanUtil.filterBean(serverTask, filterField);
			// 将Task转为字符串
			String taskStr = new HttpEncoder().stringEncoder(serverTaskFilter);
			log.info("上传报文,url:" + url + "  报文内容：" + taskStr);
			String response = SendMessage.send(url, taskStr, SendMessage.TYPE_HTTP_GET);
			log.info("上传报文成功,响应为：" + response);
		} catch (Exception e) {
			log.error("上传任务失败", e);
			// 保存任务报文

		}
	}

	private boolean isSuccessUpdate(ServerTask serverTask) {
		Boolean flag = true;
		// 判断rdp版本
		if (Config.get(Config.AGENT_VER).equals(Config.get(Config.OLD_AGENT_VER))) {
			log.info("agent新版本与旧版本相同，升级失败");
			return false;
		}

		log.info("*******************RDP版本符合***********************");
		// 判断时间
		try {
			if (isTimeout(serverTask.getExectime(), serverTask.getValid())) {
				log.info("RDP版本升级时间过长，最长升级时间为：" + serverTask.getValid());
				return false;
			}
		} catch (Exception e) {
			log.error("升级校验失败，时间格式错误");
			return false;
		}
		return flag;
	}

	/**
	 * 判断是否超时，超时返回true
	 * 
	 * @param executime(开始执行时间)
	 * @param valid(任务有效期，分钟)
	 * @return
	 * @throws Exception
	 */
	private boolean isTimeout(String executime, String valid) throws Exception {
		Date starttime = TimeUtil.StringToTime(executime, "yyyy-MM-dd HH:mm:ss.SSS");

		Date datenow = new Date();
		// 执行时间
		long interval = datenow.getTime() - starttime.getTime();
		if (Long.parseLong(valid) * 1000 * 60 < interval) {
			return true;
		}
		return false;
	}

	public boolean isAgentModify(Task task) {
		if (task != null && task instanceof ServerTask) {
			ServerTask serverTask = (ServerTask) task;
			if ("agentModifyAct".equals(serverTask.getTasktype())) {
				return true;
			}
		}
		return false;
	}

}
