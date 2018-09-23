package com.cs.parser.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cs.parser.bean.EventLog;
import com.cs.parser.dao.EventLogDao;
import com.google.common.collect.Lists;

@Repository
public class EventLogDaoImpl implements EventLogDao{
	private static final String INSERT_SQL = "INSERT INTO EVNT_LOG (EVNT_ID,EVNT_TYPE,EVNT_HOST,EVNT_ALRT,EVNT_DURATION) VALUES (?,?,?,?,?) ";
	private static final String COUNT_SQL = "SELECT COUNT(*) FROM EVNT_LOG";
	
	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	@Override
	public void save(List<EventLog> list){
		
		final int batchSize = 500;
	    List<List<EventLog>> batchLists = Lists.partition(list, batchSize);

	    for(List<EventLog> batch : batchLists) {  
	    	jdbcTemplate.batchUpdate(INSERT_SQL, new BatchPreparedStatementSetter() {
	            @Override
	            public void setValues(PreparedStatement ps, int i)
	                    throws SQLException {
	            	EventLog event = batch.get(i);
	                ps.setString(1, event.getId());
	                ps.setString(2, event.getEventType());
	                ps.setString(3, event.getEventHost());
	                ps.setString(4, event.getEventAlert());
	                ps.setLong(5, event.getEventDuration());
	            }

	            @Override
	            public int getBatchSize() {
	                return batch.size();
	            }
	        });
	    }
		
	}

	@Override
	public int getCount() {
		return jdbcTemplate.queryForObject(COUNT_SQL, Integer.class);
	}

}
