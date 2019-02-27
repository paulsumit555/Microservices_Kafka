package com.example.imageservice;

public class Image {

	int id;
	String title;
	String url;
	String serverPort;
	
	
	public Image(int id, String title, String url,String serverPort) {
		super();
		this.id = id;
		this.title = title;
		this.url = url;
		this.serverPort = serverPort;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getServerPort() {
		return serverPort;
	}
	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}
	
}
