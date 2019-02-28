package com.example.imageservice;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ImageServiceApplication implements CommandLineRunner{
	
	@Value("${kafka.topic.image}")
    private String imageTopicName;

    @Value("${kafka.bootstrap.servers}")
    private String kafkaBootstrapServers;

    @Value("${zookeeper.groupId}")
    private String zookeeperGroupId;

    @Value("${zookeeper.host}")
    String zookeeperHost;
    
    @Value("${kafka.topic.gallery}")
    private String galleryTopic;

	public static void main(String[] args) {
		SpringApplication.run(ImageServiceApplication.class, args);
		//processComsumer();
	}
	
	
	
	 @Override
	    public void run(String... args) {

		/*
    	 * Defining Kafka consumer properties.
    	 */
    	Properties consumerProperties = new Properties();
    	consumerProperties.put("bootstrap.servers", kafkaBootstrapServers);
    	consumerProperties.put("group.id", zookeeperGroupId);
    	consumerProperties.put("zookeeper.session.timeout.ms", "6000");
    	consumerProperties.put("zookeeper.sync.time.ms","2000");
    	consumerProperties.put("auto.commit.enable", "false");
    	consumerProperties.put("auto.commit.interval.ms", "1000");
    	consumerProperties.put("consumer.timeout.ms", "-1");
    	consumerProperties.put("max.poll.records", "1");
    	consumerProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    	consumerProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    	
    	Properties propertiesForProducer = propertiesForProducer();
		System.out.println("GalleryTopic :-"+galleryTopic +"  "+kafkaBootstrapServers +" "+zookeeperGroupId);
		/*
    	 * Creating a thread to listen to the kafka topic
    	 */
    	Thread kafkaConsumerThread = new Thread(() -> {

    	    ImageServiceConsumer simpleKafkaConsumer = new ImageServiceConsumer(
    	    		imageTopicName,galleryTopic,
    	            consumerProperties,propertiesForProducer
    	    );

    	    simpleKafkaConsumer.runSingleWorker();
    	});

    	/*
    	 * Starting the first thread.
    	 */
    	kafkaConsumerThread.start();

		
		
	}
	
	private Properties propertiesForProducer(){
		/*
		 * Defining producer properties.
		 */
		Properties producerProperties = new Properties();
		producerProperties.put("bootstrap.servers", kafkaBootstrapServers);
		producerProperties.put("acks", "all");
		producerProperties.put("retries", 0);
		producerProperties.put("batch.size", 16384);
		producerProperties.put("linger.ms", 1);
		producerProperties.put("buffer.memory", 33554432);
		producerProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		producerProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		return producerProperties;

		
	}

}

