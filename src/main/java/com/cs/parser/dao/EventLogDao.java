package com.cs.parser.dao;

import java.util.List;

import com.cs.parser.bean.EventLog;


public interface EventLogDao{
	public void save(List<EventLog> list);
	public int getCount();
}
