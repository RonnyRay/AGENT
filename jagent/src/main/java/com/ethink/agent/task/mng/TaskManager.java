/**
 * Ethink 2017 copyright
 * 
 */
package com.ethink.agent.task.mng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ethink.agent.annotation.AnnotationUtil;
import com.ethink.agent.db.TaskFunctionDao;
import com.ethink.agent.decode.Decoder;
import com.ethink.agent.event.Event;
import com.ethink.agent.event.EventListener;
import com.ethink.agent.task.bean.ServerTask;
import com.ethink.agent.task.bean.Task;
import com.ethink.agent.util.TimeUtil;

/**
 *
 * 描述: Task任务管理器
 * 
 * @author wangjing.dc@qq.com
 */
@Component
public class TaskManager implements EventListener {

	private JobListener jobListener = new JobListener() {

		@Override
		public String getName() {
			log.info(TaskManager.class.getName() + "的jobListener启动");
			return TaskManager.class.getName();
		}

		/*
		 * 任务执行之前执行
		 * 
		 * @see org.quartz.JobListener#jobToBeExecuted(org.quartz.
		 * JobExecutionContext)
		 */
		@Override
		public void jobToBeExecuted(JobExecutionContext context) {
			Task task = (Task) context.getJobDetail().getJobDataMap().get("taskData");
			// 更新task状态与启动时间
			taskStatusMng.update(task, TaskStatus.before_execute, TimeUtil.getNowDateTime());
		}

		/*
		 * Scheduler 在 JobDetail 即将被执行，但又被 TriggerListener 否决了时调用这个方法
		 * 这个方法正常情况下不执行,但是如果当TriggerListener中的vetoJobExecution方法返回true时,
		 * 那么执行这个方法. 需要注意的是 如果方法(2)执行 那么(1),(3)这个俩个方法不会执行,因为任务被终止了
		 * 
		 * @see org.quartz.JobListener#jobExecutionVetoed(org.quartz.
		 * JobExecutionContext)
		 */
		@Override
		public void jobExecutionVetoed(JobExecutionContext context) {
			Task task = (Task) context.getJobDetail().getJobDataMap().get("taskData");
			taskStatusMng.update(task, TaskStatus.vetoed);

		}

		/*
		 * 任务执行完成后执行,jobException如果它不为空则说明任务在执行过程中出现了异常
		 * 
		 * @see
		 * org.quartz.JobListener#jobWasExecuted(org.quartz.JobExecutionContext,
		 * org.quartz.JobExecutionException)
		 */
		@Override
		public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
			Task task = (Task) context.getJobDetail().getJobDataMap().get("taskData");
			if (jobException != null) {
				taskStatusMng.update(task, TaskStatus.error, null, TimeUtil.getNowDateTime());
				return;
			}
			Object result = context.getResult();
			// 任务结束更新task状态与结束时间
			if (result!=null&&(result instanceof Map)) {
				// 如果有taskStatus，将task的状态设为taskStatus
				Map<String, Object> mapResult = (Map<String, Object>) result;
				TaskStatus taskStatus = (TaskStatus) mapResult.get("taskStatus");
				taskStatusMng.update(task, taskStatus, null, TimeUtil.getNowDateTime());
				Object exit = mapResult.get("exit");
				if(exit!=null){
					log.info("agent已经关闭--------");
					System.exit(0);
				}
			} else {
				taskStatusMng.update(task, TaskStatus.executed, null, TimeUtil.getNowDateTime());
			}
		}

	};

	private static final Logger log = LoggerFactory.getLogger(TaskManager.class);

	@Autowired
	private TaskStatusManager taskStatusMng;

	@Autowired
	private TaskFunctionDao taskFunctionDao;

	private SchedulerFactory sf = new StdSchedulerFactory();

	public TaskManager() throws SchedulerException {
		GroupMatcher<JobKey> groupMatcher = GroupMatcher.groupEndsWith("_task");// 匹配以特定字符串结尾
		sf.getScheduler().getListenerManager().addJobListener(jobListener, groupMatcher);

	}

	public void addTask(Task task, String eventType) throws Exception {

		Scheduler sched = sf.getScheduler();

		JobDataMap jobData = new JobDataMap();
		jobData.put("taskData", task);
		// 创建jobDetail
		Class<? extends Job> jobClass = findTaskClass(task);
		if (jobClass == null) {
			log.error("检索Task失败，抛弃任务");
			return;
		}
		JobDetail job = JobBuilder.newJob(jobClass).usingJobData(jobData)
				.withIdentity(task.getTaskId(), task.getClass().getName() + "_task").build();

		// 创建Trigger
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger_" + task.getTaskId())
				.startAt(taskDate(task)).build();

		sched.scheduleJob(job, trigger);
		// 数据库插入task信息
		taskStatusMng.insert(task, TaskStatus.ready,eventType);
		// taskStatusMng.update(task, TaskStatus.ready);
		log.info("任务已加入调度。任务id:" + task.getTaskId() + "任务类型：" + task.getTaskType() + "执行时间：" + task.getExecuteTime()
				+ "任务报文：" + task.getTaskData());

		if (!sched.isStarted())
			sched.start();
	}

	/**
	 * 计算task试行时间
	 * 
	 * @param task
	 * @return
	 * @throws ParseException
	 */
	private Date taskDate(Task task) throws ParseException {
		if (task == null)
			return null;

		String executeTime = task.getExecuteTime();
		// 如果任务没有执行时间，执行时间设为立即执行
		if (executeTime == null || "".equals(executeTime) || "immediate".equals(executeTime)) {
			task.setExecuteTime("immediate");
			return new Date();
		}
		String dateTemplate = null;
		if (task instanceof ServerTask) {
			dateTemplate = "yyyy-MM-dd HH:mm:ss.SSS";
		} else {
			dateTemplate = "yyyyMMddHHmmss";
		}
		Date date = new SimpleDateFormat(dateTemplate).parse(executeTime);
		return date;
	}

	/**
	 * 检索Task执行器
	 * 
	 * @param task
	 * @return
	 */
	private Class<? extends Job> findTaskClass(Task task) {
		if (task == null)
			return null;
		// 获得executor映射关系表
		Map<String, Class<?>> executorMap = AnnotationUtil.getTaskExecutorMap("com.ethink.agent.task.executor");

		Class executor = executorMap.get(task.getTaskType());

		return executor;
	}

	@Override
	public void event(Event event) {
		// 如果是重启事件
		if (event.getEventType().equals(Event.TYPE_AGENTSTARTUP)) {
			try {
				taskRestore();
			} catch (Exception e) {
				log.error("任务还原失败！",e);
			}
			return;
		}
		
		String eventType = event.getEventType();
		Decoder decoder = findDecoder(eventType);
		Object obj = null;
		try {
			obj = decoder.decode((String) event.getEventData());
		} catch (Exception e) {
			log.info("解析报文出错，抛弃报文：" + event.getEventData(), e);
		}
		// task为空直接抛弃
		if (obj == null) {
			log.info("报文不符合规则，抛弃报文：" + event.getEventData());
			return;
		}
		// decode可能解析出多个task
		List<Task> list = new ArrayList<Task>();
		if (obj instanceof List) {
			list = (List<Task>) obj;
		} else {
			// decode解析为单个task
			Task task = (Task) obj;
			task.setTaskData((String) event.getEventData());
			list.add((Task) obj);
		}

		for (Task task : list) {
			// 为每个Task添加唯一的taskid
			// task.setTaskId(UuidUtil.uuid());
			try {
				this.addTask(task, eventType);
			} catch (Exception e) {
				log.error("任务添加失败", e);
			}
		}

	}

	/**
	 * 检索decoder
	 * 
	 * @param eventType
	 * @return
	 */
	private Decoder findDecoder(String eventType) {
		Map<String, Class<?>> decoderMap = AnnotationUtil.getDecoderMap(Event.class);
		Class<?> decoderClass = decoderMap.get(eventType);
		Decoder decoder = null;
		try {
			decoder = (Decoder) decoderClass.newInstance();
		} catch (Exception e) {
			log.error("获取decoder映射表失败", e);
		}
		return decoder;
	}

	/**
	 * 判断是否需要额外状态管理
	 * 
	 * @param task
	 * @return
	 */
	private boolean isUpdateTask(Task task) {
		String taskType = task.getTaskType();
		// 额外状态表
		String extraStatus[] = { "atmcUpdateAct", "adUpdateAct", "agentUpdateAct" };

		if (Arrays.asList(extraStatus).contains(taskType)) {
			return true;
		}

		return false;
	}

	/**
	 * task任务还原
	 * 
	 * @param eventData
	 * @throws Exception 
	 */
	private void taskRestore() throws Exception {
		// 获得未执行的任务列表和extra状态的任务
		List<Map<String, Object>> listMap = taskFunctionDao.selectNotyetExecute();
		log.info("还原未执行的任务列表和extra状态的任务，任务数量："+listMap.size());

		Map<String, Object> taskmap = new HashMap<String, Object>();
		
		for (Map<String, Object> mapTask : listMap) {
			// 加入任务调度
			Scheduler sched = sf.getScheduler();
			//解析还原task
			Task task;
			String msgType = (String) mapTask.get("msgType");
			Object obj = findDecoder(msgType).decode((String) mapTask.get("taskData"));
			if (obj instanceof List) {
				List<Task> list = (List<Task>) obj;
				task = list.get(0);
			} else {
				task = (Task) obj;
			}
			task.setTaskId((String) mapTask.get("taskId"));
			task.setTaskStatus((String) mapTask.get("taskStatus"));
			JobDataMap jobData = new JobDataMap();
			jobData.put("taskData", task);
			
			//该状态为升级状态
			if(TaskStatus.extra.toString().equals(task.getTaskStatus())){
				jobData.put("agentStart", true);
			}
			// 创建jobDetail
			Class<? extends Job> jobClass = findTaskClass(task);
			if (jobClass == null) {
				log.error("检索Task失败，抛弃任务");
				return;
			}
			
			
			JobDetail job = JobBuilder.newJob(jobClass).usingJobData(jobData)
					.withIdentity(task.getTaskId(), task.getClass().getName() + "_task").build();

			// 创建Trigger
			Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger_" + task.getTaskId())
					.startAt(taskDate(task)).build();

			sched.scheduleJob(job, trigger);
			// taskStatusMng.update(task, TaskStatus.ready);
			log.info("已还原任务，等待执行。任务id:" + task.getTaskId() + "任务类型：" + task.getTaskType() + "执行时间：" + task.getExecuteTime()
					+ "任务报文：" + task.getTaskData());

			if (!sched.isStarted())
				sched.start();
		}
	}

	@Override
	public int getPriority() {
		int priority = 1;
		return priority;
	}

}
