package com.garganttua.events.engine;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import com.garganttua.events.context.GGEventsContextDataflowInProcessMode;
import com.garganttua.events.context.GGEventsContextDestinationPolicy;
import com.garganttua.events.context.GGEventsContextHighAvailabilityMode;
import com.garganttua.events.context.GGEventsContextOriginPolicy;
import com.garganttua.events.context.GGEventsContextPublicationMode;
import com.garganttua.events.spec.interfaces.IGGEventsEngine;
import com.garganttua.events.spec.interfaces.IGGEventsEventHandler;
import com.garganttua.events.spec.objects.GGEventsEvent;

public class BuilderTest {

	@Test
	public void test() {
		
		int threadPoolSize = 100;
		int maxThreadPoolSize = 200;
		long threadPoolKeepAliveTime = 100;
		TimeUnit threadPoolKeepAliveTimeUnit = TimeUnit.SECONDS;
		BlockingQueue<Runnable> workQueue = new LinkedBlockingDeque<Runnable>();
		ExecutorService executorService = new ThreadPoolExecutor(threadPoolSize/2, maxThreadPoolSize/2, threadPoolKeepAliveTime, threadPoolKeepAliveTimeUnit, workQueue);
		ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(threadPoolSize/2);
		
		IGGEventsEngine engine = GGEventsBuilder.builder("assetId")
			.lookup("com.garganttua")
			.lookup("org.toto")
			.executorService(executorService)
			.scheduledExecutorService(scheduledExecutorService)
			.threadPoolKeepAliveTimeUnit(TimeUnit.SECONDS)
			.threadPoolKeepAliveTime(threadPoolKeepAliveTime)
			.threadPoolSize(threadPoolSize)
			.maxThreadPoolSize(maxThreadPoolSize)
			.eventHanlder(new IGGEventsEventHandler() {
				@Override
				public void handleEvent(GGEventsEvent event) {
					// TODO Auto-generated method stub
					
				}})
			.source("json-file", "1.0", "src/test/resources/context.json")
//			.source(new ContextSourceTest("configuration"))
			.context("tenantId", "clusterId")
			.topic("/test")
			.topic("/test2")
			.dataflow("uuid", "test", "test", true, "1.0", true)
			.dataflow("uuid2", "test", "test", true, "1.0", true)
			.connector("kafka", "kafka", "1.0", "url=localhost:29092&maxPollRecords=1&enableAutoCommit=false&autoOffsetReset=latest&partitionsAutoScalling=true&allowAutoCreateTopics=name")
			.subscription("uuid", "/test", "kafka", GGEventsContextPublicationMode.ON_CHANGE)
			.producerConfiguration(GGEventsContextDestinationPolicy.TO_ANY, "assetId")
			.consumerConfiguration(GGEventsContextDataflowInProcessMode.ONLY_ONE_CLUSTER_NODE, GGEventsContextOriginPolicy.FROM_ANY, GGEventsContextDestinationPolicy.TO_ANY, true, GGEventsContextHighAvailabilityMode.LOAD_BALANCED)
			.context()
			.subscription("uuid2", "/test2", "kafka", GGEventsContextPublicationMode.ON_CHANGE).context()
			.route("uuid", "kafka://uuid/test", "kafka://uuid2/test2")
			.processor("log", "1.0", "level=WARN")
//			.exceptions("to", "cast", "label")
//			.synchronization("lock", "lockObject")
			.context()
//			.lock("name", "type", "version", "configuration")
			.write("ContextSourceTest", "1.0", "configuration")
			.write(new ContextSourceTest("configuration"))
			.write()
			.builder()
			.build()
			.start();
		
//		String assetId = engine.getAssetInfos().getAssetId();
		
		engine.stop();
	}
}
