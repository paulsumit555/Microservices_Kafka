package com.example.imageservice;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StudentController
{
	
	@Autowired
	private Environment env;
	
	@Autowired
	Environment environment;
	
	
    @GetMapping("/demo1")
    public String getControllerName()
    {
    	System.out.println("$$$$$$  "+env.getProperty("local.server.port"));
        return  "hello"+env.getProperty("local.server.port");//;restTemplate.getForObject("http://db-service/demo1/", String.class) + "from db";
    }
    
    @RequestMapping("/images")
	public List<Image> getImages() {
    	
    	System.out.println("Inside MyRestController::backend...");
   	 
        String serverPort = environment.getProperty("local.server.port");
 
        System.out.println("Port : " + serverPort);
 
        System.out.println("Hello form Backend!!! " + " Host : localhost " + " :: Port : " + serverPort);
    	
    	
		List<Image> images = Arrays.asList(
			new Image(1, "Treehouse of Horror V", "https://www.imdb.com/title/tt0096697/mediaviewer/rm3842005760",serverPort),
			new Image(2, "The Town", "https://www.imdb.com/title/tt0096697/mediaviewer/rm3698134272",serverPort),
			new Image(3, "The Last Traction Hero", "https://www.imdb.com/title/tt0096697/mediaviewer/rm1445594112",serverPort));
		return images;
	}

   
}