package org.bdigital;

import java.util.Properties;

public class TwitterKafkaConsumer {

    /**
     * @param args
     */
    public static void main(String[] args) {

	Properties props = new Properties();
	
	//zookeper
	props.put("zk.connect", "localhost:2181");
	props.put("zk.connectiontimeout.ms", "100000");
	props.put("groupid", "tweetsGroup");
	
    }

}
