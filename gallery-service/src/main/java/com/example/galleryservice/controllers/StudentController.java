package com.example.galleryservice.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.galleryservice.model.Gallery;
import com.example.galleryservice.model.Image;
import com.example.galleryservice.model.Student;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
public class StudentController {

	@Autowired
	Environment environment;

	@Autowired
	private RestTemplate restTemplate;

	@Value("${kafka.bootstrap.servers}")
	private String kafkaBootstrapServers;

	@Value("${kafka.topic.image}")
	private String imageTopic;
	
	@Value("${kafka.topic.gallery}")
	private String galleryTopic;

	@Value("${zookeeper.groupId}")
	private String zookeeperGroupId;

	@GetMapping("/name")
	public String getControllerName() {
		System.out.println("Inside MyRestController::backend...");

		String serverPort = environment.getProperty("local.server.port");

		System.out.println("Port : " + serverPort);

		return "Hello form Backend!!! " + " Host : localhost " + " :: Port : " + serverPort;
	}

	@GetMapping("/students/{student_id}")
	public Student getStudentById(@PathVariable("student_id") Integer studentId) {
		return new Student(1, "Chathuranga", "Bsc", "Sri Lanka");
	}

	@HystrixCommand(fallbackMethod = "callMovieDetailsFallBack")
	@RequestMapping("/{id}")
	public Gallery getGallery(@PathVariable final int id) {
		// create gallery object
		Gallery gallery = new Gallery();
		gallery.setId(id);

		// get list of available images
/*		List<Image> images = restTemplate.getForObject("http://image-service/images/", List.class);
		gallery.setImages(images);
		System.out.println(images);*/
		preparationForSendingMessage(id);
		
		return gallery;
	}

	private void preparationForSendingMessage(Integer id) {
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

		
		processComsumer(producerProperties);
		
		/*
		 * Creating a Kafka Producer object with the configuration above.
		 */
		KafkaProducer<String, String> producer = new KafkaProducer<>(producerProperties);

		JSONObject jsonObject = new JSONObject();
		try {
			
			jsonObject.put("id", id);
			sendTestMessagesToKafka(producer,jsonObject.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("successfully");
		
	}

	private void processComsumer(Properties producerProperties) {

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
		
		/*
    	 * Creating a thread to listen to the kafka topic
    	 */
    	Thread kafkaConsumerThread = new Thread(() -> {

    	    GalleryServiceConsumer simpleKafkaConsumer = new GalleryServiceConsumer(
    	    		galleryTopic,
    	            consumerProperties
    	    );

    	    simpleKafkaConsumer.runSingleWorker();
    	});

    	
    	 /* Starting the first thread.*/
    	 
    	kafkaConsumerThread.start();

		
		
	}

	/**
	 * Function to send some test messages to Kafka. We'll get the Kafka producer
	 * object as a parameter to this function. We'll generate some test messages,
	 * both simple strings and JSON objects, in a couple of loops inside the
	 * function. We'll send these test messages to the topic in Kafka.
	 *
	 * @param producer The Kafka producer we created in the run() method earlier.
	 */
	private void sendTestMessagesToKafka(KafkaProducer<String, String> producer,String payload) {
		 /*
        We'll now serialize the JSON object we created above, and send it to the same topic in Kafka,
        using the same function we used earlier.
        You can use any JSON library for this, just make sure it serializes your objects properly.
        A popular alternative to the one I've used is Gson.
         */
		producer.send(new ProducerRecord<>(imageTopic, payload));
	}

	public Gallery callMovieDetailsFallBack(int id) {
		Gallery gallery = new Gallery();
		gallery.setId(1);
		List<Image> images = Arrays.asList(
				new Image(1, "Treehouse of Horror V", "https://www.imdb.com/title/tt0096697/mediaviewer/rm3842005760"),
				new Image(2, "The Town", "https://www.imdb.com/title/tt0096697/mediaviewer/rm3698134272"), new Image(3,
						"The Last Traction Hero", "https://www.imdb.com/title/tt0096697/mediaviewer/rm1445594112"));

		gallery.setImages(images);
		return gallery;
	}

	@GetMapping("/courses/{course_id}/students")
	public List<Student> getStudentsByCourses(@PathVariable("course_id") Integer courseId) {
		List<Student> studentList = new ArrayList<>();

		studentList.add(new Student(1, "Chathuranga", "Bsc", "Sri Lanka"));
		studentList.add(new Student(2, "Darshana", "Sun Certified", "Sri Lanka"));
		return studentList;
	}

	@GetMapping("/departments/{department_id}/courses/{course_id}/students")
	public List<Student> getStudentsByDepartmentCourses(@PathVariable("department_id") Integer departmentId,
			@PathVariable("course_id") Integer courseId) {
		List<Student> studentList = new ArrayList<>();

		studentList.add(new Student(1, "Chathuranga", "Bsc", "Sri Lanka"));
		studentList.add(new Student(2, "Darshana", "Sun Certified", "Sri Lanka"));
		studentList.add(new Student(3, "Tennakoon", "Zend Certified", "Sri Lanka"));
		return studentList;
	}
}