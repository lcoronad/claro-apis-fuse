package com.claro.configuration;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class CamelBean implements Job {

	private Logger log = LoggerFactory.getLogger(CamelBean.class);

	@Autowired
	private ExecuteRoute exe;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDetail jobDetail = context.getJobDetail();
		

		if (jobDetail != null) {
			log.info("-------------------------------------------------------------------");
			log.info("Job ** {} ** fired @ {}", context.getJobDetail().getKey().getName(), context.getFireTime());
			log.info("Ejecutando tarea programada:{} ", jobDetail.getKey());
			log.info("-------------------------------------------------------------------");
		}
		
		if(exe != null)
			exe.execute();
		

	}

}
