package org.bdigital;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Constants.FilterLevel;
import com.twitter.hbc.core.endpoint.Location;
import com.twitter.hbc.core.endpoint.Location.Coordinate;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.event.Event;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

public class TwitterKafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(TwitterKafkaProducer.class);

    /**
     * @param args
     */
    public static void main(String[] args) {

	BasicConfigurator.configure();

	BlockingQueue<String> processorQueue = new LinkedBlockingQueue<String>(10000);
	BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<Event>(10000);

	Authentication auth = new OAuth1("HzlOSyMnSbcfjaiyvBfQ",
		"Cee7ktUmOe3fzIxYuKp9Tqq5V7n4KJD9LOzGjnnsC8",
		"20428117-OpqDjT6iDatc0zGylS5am93QTSjrDQ0LqQiXimDms",
		"bYHS24VNamQ8oVwBy8hH2XJR5s5iYWLSfwm4wJ5Qg");

	StatusesFilterEndpoint endPoint = new StatusesFilterEndpoint().trackTerms(
		Arrays.asList(new String[] { "Barcelona", "Catalunya" })).locations(
		Arrays.asList(new Location[] { new Location(new Coordinate(0.13, 40.49),
			new Coordinate(3.49, 43.0)) 
		// SWLon, SWLat, NELon, NELat
		}));
	
	endPoint.filterLevel(FilterLevel.None);
	endPoint.stallWarnings(true);

	ClientBuilder builder = new ClientBuilder().hosts(Constants.STREAM_HOST)
		.authentication(auth)
		// .endpoint(new StatusesSampleEndpoint())
		.endpoint(endPoint).processor(new StringDelimitedProcessor(processorQueue))
		.eventMessageQueue(eventQueue);

	Client hosebirdClient = builder.build();
	hosebirdClient.connect();

	while (hosebirdClient.isDone() == false) {

	    try {

		String jsonTweet = processorQueue.take();
		log.info(jsonTweet);

	    } catch (Exception e) {
		e.printStackTrace();
	    }

	}
	hosebirdClient.stop();

    }

}
