package com.example.galleryservice.model;

import java.util.ArrayList;
import java.util.List;

public class Gallery {

	
	int id;
	List<Image> images = new ArrayList<Image>();
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<Image> getImages() {
		return images;
	}
	public void setImages(List<Image> images) {
		this.images = images;
	}
}
