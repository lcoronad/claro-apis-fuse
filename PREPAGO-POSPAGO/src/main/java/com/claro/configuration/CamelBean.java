package com.claro.configuration;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class CamelBean implements Job {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ExecuteRoute executeRoute;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDetail jobDetail = context.getJobDetail();

		if (jobDetail != null) {
			log.info("-------------------------------------------------------------------");
			log.info("Job ** {} ** fired @ {}", context.getJobDetail().getKey().getName(), context.getFireTime());
			log.info("Ejecutando tarea programada:{} ", jobDetail.getKey());
			log.info("-------------------------------------------------------------------");
		}
		
		this.executeRoute.execute();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
