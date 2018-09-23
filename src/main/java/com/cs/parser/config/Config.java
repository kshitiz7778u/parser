package com.cs.parser.config;

import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import com.cs.parser.bean.EventLog;
import com.cs.parser.listener.Listener;
import com.cs.parser.processor.Processor;
import com.cs.parser.reader.Reader;
import com.cs.parser.writer.Writer;

@Configuration
@ImportResource({"classpath*:applicationContext.xml"})
public class Config {

    Logger logger = LoggerFactory.getLogger(Config.class);
    
    @Value("${cs.parser.file.location}")
    private String fileLocation;
    //record chunk at a time
    @Value("${cs.parser.chunk.size}")
    private int chunkSize;
    //max number of threads
    @Value("${cs.parser.throttle.limit}")
    private int throttleLimit;
    //concurrent files-->required if processing multiple files(test time required for splitting a file)
    @Value("${cs.parser.grid.size}")
    private int gridSize;
    
    private static final String SLAVE_STEP = "slaveStep";
    private static final String PARALLEL_JOB = "parallelJob";
    private static final String MASTER_STEP = "masterStep";

    @Autowired
    private ApplicationArguments  applicationArguments;
    
    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactory;


    @Bean
    @Qualifier("partitioningJob")
    public Job partitioningJob() throws Exception {
        return jobBuilderFactory.get(PARALLEL_JOB)
                .incrementer(new RunIdIncrementer())
                .listener(listener())
                .flow(masterStep())
                .end()
                .build();
    }
    
    /*
     * 
     * Required if split functionality is implemented
     * 
     */

    @Bean
    public Step masterStep() throws Exception {
        return stepBuilderFactory.get(MASTER_STEP)
                .partitioner(slaveStep())
                .partitioner("partition", partitioner())
                .gridSize(gridSize)
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();
    }

    /*
     * Added partition to read multiple files in parallel
     * 
     * todo :
     * can split a file into parts and execute simultaneously for one file as well
     * 
     */
    @Bean
    public Partitioner partitioner() throws Exception {
        MultiResourcePartitioner partitioner = new MultiResourcePartitioner();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        if(applicationArguments.getSourceArgs() !=null && applicationArguments.getSourceArgs().length>0){
        	fileLocation = applicationArguments.getSourceArgs()[0].split("=")[1];
        }
        partitioner.setResources(resolver.getResources(fileLocation));
        return partitioner;
    }

    @Bean
    public Step slaveStep() throws Exception {
        return stepBuilderFactory.get(SLAVE_STEP)
                .<EventLog,EventLog>chunk(chunkSize)
                .reader(reader(null))
                .writer(writer())
                .processor(processor())
                .throttleLimit(throttleLimit)
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();
    }
    
    @Bean
    public JobExecutionListener listener() {
		return new Listener();
	}

    @Bean
    @StepScope
    public FlatFileItemReader<EventLog> reader(@Value("#{stepExecutionContext['fileName']}") String file) throws MalformedURLException {
    	FlatFileItemReader<EventLog> reader = new FlatFileItemReader<>();
        reader.setResource(new UrlResource(file));
        LineMapper<EventLog> lineMapper = new Reader();
        reader.setLineMapper(lineMapper);
        return reader;
    }
    
    @Bean
    public ItemProcessor<EventLog, EventLog> processor() {
        return new Processor();
    }

    @Bean
    public ItemWriter<EventLog> writer() {
        return new Writer();
    }
}
