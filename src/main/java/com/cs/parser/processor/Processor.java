package com.cs.parser.processor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.cs.parser.bean.EventLog;

@Component
public class Processor implements ItemProcessor<EventLog, EventLog>{
	private static final Logger log = LogManager.getLogger(Processor.class);
	private static final int MAX_DURATION = 4;
	private static Map<String,EventLog> map= new ConcurrentHashMap<>();
	
	@Override
	public EventLog process(final EventLog event) throws Exception {
		log.debug(event.toString());
		if(!map.containsKey(event.getId())){
			map.put(event.getId(), event);
			return null;
		}	
		EventLog eventInMap = map.get(event.getId());
		Long diff = Math.abs(event.getTimestamp()-eventInMap.getTimestamp());
		event.setEventDuration(diff);
		String alert = diff>MAX_DURATION?"true":"false";
		event.setEventAlert(alert);
		//free up space
		map.remove(event.getId());
		return event;
	}
}

