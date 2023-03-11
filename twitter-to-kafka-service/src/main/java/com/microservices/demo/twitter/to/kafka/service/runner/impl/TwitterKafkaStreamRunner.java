package com.microservices.demo.twitter.to.kafka.service.runner.impl;

import com.microservices.demo.twitter.to.kafka.service.config.TwitterToKafkaServiceConfigData;
import com.microservices.demo.twitter.to.kafka.service.listener.TwitterKafkaStatusListener;
import com.microservices.demo.twitter.to.kafka.service.runner.StreamRunner;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.v1.*;

import java.util.Arrays;

@Component
public class TwitterKafkaStreamRunner implements StreamRunner {

    private final Logger LOG = LoggerFactory.getLogger(TwitterKafkaStreamRunner.class);

    private final TwitterKafkaStatusListener twitterKafkaStatusListener;
    private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;

    private TwitterStream twitterStream;

    public TwitterKafkaStreamRunner(TwitterKafkaStatusListener twitterKafkaStatusListener, TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData) {
        this.twitterKafkaStatusListener = twitterKafkaStatusListener;
        this.twitterToKafkaServiceConfigData = twitterToKafkaServiceConfigData;
    }

    @Override
    public void start() throws TwitterException {
        twitterStream = Twitter.newBuilder().listener(twitterKafkaStatusListener).build().v1().stream();
        String[] keywords = twitterToKafkaServiceConfigData.getTwitterKeywords().toArray(new String[] {});
        FilterQuery filterQuery = FilterQuery.ofTrack(keywords);
        twitterStream.filter(filterQuery);
        LOG.info("Started filtering twitter stream for keywords {}", Arrays.toString(keywords));
    }

    @PreDestroy
    public void shutDown() {
        if (twitterStream != null) {
            LOG.info("Closing twitter stream connection");
            twitterStream.shutdown();
        }
    }
}
