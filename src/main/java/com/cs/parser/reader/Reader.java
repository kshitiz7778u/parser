package com.cs.parser.reader;

import org.springframework.batch.item.file.LineMapper;

import com.cs.parser.bean.EventLog;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Reader implements LineMapper<EventLog>{
	private ObjectMapper mapper = new ObjectMapper();


    /**
     * Interpret the line as a Json object and create a EventLog Entity from it.
     * 
     * @see LineMapper#mapLine(String, int)
     */
    @Override
    public EventLog mapLine(String line, int lineNumber) throws Exception {
        return mapper.readValue(line, EventLog.class);
    }
}
