package com.claro.configuration;

import static org.quartz.JobBuilder.newJob;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

@Configuration
public class StartJob {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ApplicationContext applicationContext;

	@Value("${cache.cron}")
	private String cron;

	@PostConstruct
	public void init() {
		logger.info("Hello world from Quartz...");
	}

	@Bean
	public SpringBeanJobFactory springBeanJobFactory() {
		AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
		logger.debug("Configuring Job factory");

		jobFactory.setApplicationContext(applicationContext);
		return jobFactory;
	}

	@Bean
	public Scheduler scheduler(Trigger trigger, JobDetail job, SchedulerFactoryBean factory) throws SchedulerException {
		logger.debug("Getting a handle to the Scheduler");
		Scheduler scheduler = factory.getScheduler();
		scheduler.scheduleJob(job, trigger);

		logger.debug("Starting Scheduler threads");
		scheduler.start();
		return scheduler;
	}

	@Bean
	public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
		SchedulerFactoryBean factory = new SchedulerFactoryBean();
		factory.setJobFactory(springBeanJobFactory());
		factory.setQuartzProperties(quartzProperties());
		return factory;
	}

	public Properties quartzProperties() throws IOException {
		PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
		propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
		propertiesFactoryBean.afterPropertiesSet();
		return propertiesFactoryBean.getObject();
	}

	@Bean
	public JobDetail createJobTrigger() {

		return newJob().ofType(CamelBean.class).storeDurably().withIdentity(JobKey.jobKey("Qrtz_Job_Detail"))
				.withDescription("Invoke Sample Job service...").build();

	}

	@Bean
	public Trigger buildTrigger(JobDetail jobDetail) {

		if (!CronExpression.isValidExpression(cron)) {
			logger.error("Error la expresión {} es invalida",cron);
			cron = "0 0 12 1 1 ? *";
		}

		return TriggerBuilder.newTrigger().forJob(jobDetail).withIdentity("Qrtz_Trigger")
				.withDescription("Sample trigger").withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
	}

}
