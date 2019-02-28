package com.example.galleryservice.controllers;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.TopicPartition;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class GalleryServiceConsumer {


    private KafkaConsumer<String, String> kafkaConsumer;

    public GalleryServiceConsumer(String galleryTopic, Properties consumerProperties) {

        kafkaConsumer = new KafkaConsumer<>(consumerProperties);
       kafkaConsumer.subscribe(Arrays.asList(galleryTopic));
    }

    /**
     * This function will start a single worker thread per topic.
     * After creating the consumer object, we subscribed to a list of Kafka topics, in the constructor.
     * For this example, the list consists of only one topic. But you can give it a try with multiple topics.
     */
    public void runSingleWorker() {

        /*
         * We will start an infinite while loop, inside which we'll be listening to
         * new messages in each topic that we've subscribed to.
         */
        while(true) {

        	Duration tenSecond = Duration.ofSeconds(10);
        	
            ConsumerRecords<String, String> records = kafkaConsumer.poll(tenSecond);

            for (ConsumerRecord<String, String> record : records) {

            	 String message = record.value();

                 /*
                 Logging the received message to the console.
                  */
                 System.out.println("Received message from image service: " + message);
            }
        }
    }
}
