package com.example.imageservice;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

public class ImageServiceConsumer {


    private KafkaConsumer<String, String> kafkaConsumer;
    
    private KafkaProducer<String, String> producer;
    
	@Value("${kafka.topic.gallery.thetechcheck}")
	private String galleryTopic;
    
	@Autowired
	Environment environment;

    public ImageServiceConsumer(String imageTopic,String galleryTopic, Properties consumerProperties,Properties producerProperties) {

        kafkaConsumer = new KafkaConsumer<>(consumerProperties);
        kafkaConsumer.subscribe(Arrays.asList(imageTopic));
        producer = new KafkaProducer<>(producerProperties);
        this.galleryTopic=galleryTopic;
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

                /*
                Whenever there's a new message in the Kafka topic, we'll get the message in this loop, as
                the record object.
                 */

                /*
                Getting the message as a string from the record object.
                 */
                String message = record.value();

                /*
                Logging the received message to the console.
                 */
                System.out.println("Received message from gallery service: " + message);

                JSONObject jsonObject = new JSONObject();
                String serverPort = "";//environment.getProperty("local.server.port");
                List<Image> images = Arrays.asList(
            			new Image(1, "Treehouse of Horror V", "https://www.imdb.com/title/tt0096697/mediaviewer/rm3842005760",serverPort),
            			new Image(2, "The Town", "https://www.imdb.com/title/tt0096697/mediaviewer/rm3698134272",serverPort),
            			new Image(3, "The Last Traction Hero", "https://www.imdb.com/title/tt0096697/mediaviewer/rm1445594112",serverPort));
                
                try {
                	 jsonObject.put("image", images);
                }catch(JSONException e) {
                	
                }
                
               producer.send(new ProducerRecord<>(galleryTopic, images.toString()));
            }
        }
    }
}
