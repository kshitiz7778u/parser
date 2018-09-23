package com.cs.parser.writer;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cs.parser.bean.EventLog;
import com.cs.parser.dao.EventLogDao;

@Component
public class Writer implements ItemWriter<EventLog> {
	private static final Logger log = LogManager.getLogger(Writer.class);

	@Autowired
	EventLogDao eventLogDao;
	
    @Override
	public void write(List<? extends EventLog> list) throws Exception {
    	if(list.size()==0){
    		return;
    	}
    	log.info("Writing data in DB");
    	eventLogDao.save(new ArrayList<>(list));
		log.info(list.size() + " items written !");
	}

}
