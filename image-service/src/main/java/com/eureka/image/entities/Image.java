package com.eureka.image.entities;

public class Image {

	int id;
	String title;
	String url;
	public Image(int id, String title, String url) {
		super();
		this.id = id;
		this.title = title;
		this.url = url;
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
	
}
