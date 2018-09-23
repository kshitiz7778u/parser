package com.cs.parser.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cs.parser.dao.EventLogDao;

@Component
public class Listener implements JobExecutionListener{
	private static final Logger log = LogManager.getLogger(Listener.class);
	
	@Autowired
	EventLogDao eventLogDao;

	@Override
	public void beforeJob(JobExecution jobExecution) {
		log.info("START THE JOB================================>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		log.info("Total records in DB : " + eventLogDao.getCount());
		log.info("END THE JOB================================>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	}
}
