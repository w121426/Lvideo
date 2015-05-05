package com.lvideo.videofile;

import java.io.Serializable;

import com.lvideo.component.LoadedImage;

public class Video implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -7920222595800367956L;
    private String title;
    private String path;
    private String jpgpath;
    private long duration;
    private LoadedImage image;
    /**  ”∆µ±ÍÃ‚∆¥“Ù  */
	private String title_key;

    /**
     * 
     */
    public Video() {
        super();
    }

    /**
     * @param title
     * @param path
     * @param jpgpath
     * @param duration
     * @param title_key
     */
    public Video(String title, String path,
            long duration, String title_key, String jpgpath) {
        super();
        this.title = title;
        this.path = path;
        this.duration = duration;
        this.title_key = title_key;
        this.jpgpath = jpgpath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
    public String getjpgPath() {
        return jpgpath;
    }

    public void setjpgPath(String jpgpath) {
        this.jpgpath = jpgpath;
    }
    
    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
    
    public LoadedImage getImage(){
    	return image;
    }
    
    public void setImage(LoadedImage image){
    	this.image = image;
    }
    
    public final String getTitle_key(){
    	return title_key;
    }
    
    public final void setTitle_key(String title_key){
    	this.title_key = title_key;
    }

}