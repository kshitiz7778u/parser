package com.cs.parser.dao.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.cs.parser.bean.EventLog;
import com.cs.parser.dao.EventLogDao;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations={"classpath:applicationContext.xml"})
@TestPropertySource(locations="classpath:application.properties")
public class EventLogDaoTest {
	
	@Autowired
    private EventLogDao eventLogDao;
	
	@MockBean
    private ApplicationArguments applicationArguments;
	
    @Test
    @Transactional
    @Rollback(true)
    public void testSaveAndGetCount()
    {
    	Mockito.when(applicationArguments.getSourceArgs()).thenReturn(new String[]{});
        EventLog event = new EventLog();
        event.setId("Test");
        event.setEventDuration(new Long(5));
        event.setEventAlert("true");
        List<EventLog> list = new ArrayList<>();
        list.add(event);
        eventLogDao.save(list);
        Assert.assertEquals(1, eventLogDao.getCount());
    }

}
