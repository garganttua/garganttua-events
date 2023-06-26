package com.garganttua.events.connectors.kafka;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;

public class GGEventsKafkaConnectorRoundRobinPartitioner implements Partitioner {

    private int index = -1;

    public void configure(Map<String, ?> configs) {
    }

    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
    	int part = 0;
    	List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
    	int numPartitions = partitions.size()-1;
    	
    	if( numPartitions <= 0 ) {
    		numPartitions = 1;
    	}

    	if( this.index == -1 ) {
    		Random rand = new Random();
    		this.index = rand.nextInt(numPartitions);
    	}
    	
        if( index > numPartitions ) {
        	index = 0;
        }
        
        part = index;
        index++;

        return part;
    }

    public void close() {
    }

}
