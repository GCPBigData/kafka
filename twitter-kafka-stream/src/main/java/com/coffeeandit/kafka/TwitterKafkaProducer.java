package com.coffeeandit.kafka;

import java.util.Properties;

import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterKafkaProducer {

	private static final String topic = "twitter-topic";

	public static void main(String[] args) throws InterruptedException {


		//PUT YOUR KEYS HERE
		String accessToken = "";
		String accessTokenSecret = "";
		String consumerKey = "";
		String consumerKeySecret= "";

		Properties properties = new Properties();
		properties.put("metadata.broker.list", "localhost:9092");
		properties.put("serializer.class", "kafka.serializer.StringEncoder");
		properties.put("client.id","camus");
		ProducerConfig producerConfig = new ProducerConfig(properties);
		kafka.javaapi.producer.Producer<String, String> producer = new kafka.javaapi.producer.Producer<String, String>(
				producerConfig);

		ConfigurationBuilder configurationBuider = new ConfigurationBuilder();
		configurationBuider.setDebugEnabled(true)
				.setOAuthConsumerKey(consumerKey)
				.setOAuthConsumerSecret(consumerKeySecret)
				.setOAuthAccessToken(accessToken)
				.setOAuthAccessTokenSecret(accessTokenSecret);

		TwitterFactory tf = new TwitterFactory(configurationBuider.build());
		twitter4j.Twitter twitter = tf.getInstance();

		while(true) {
			Query query = new Query("goLOUD");
			QueryResult result = null;
			try {
				result = twitter.search(query);
			} catch (TwitterException e) {
				e.printStackTrace();
			}
			for (Status status : result.getTweets()) {
				System.out.println("Enviando tweet para o kafka... "+status.getId());
				KeyedMessage<String, String> message = null;
				message = new KeyedMessage<String, String>(topic, status.getText());
				producer.send(message);
			}

			Thread.sleep(10000);
		}
	}
}
