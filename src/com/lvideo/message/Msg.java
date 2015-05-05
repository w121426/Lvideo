package com.lvideo.message;

import java.io.Serializable;

public class Msg implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -7920222595800367956L;
    private String message;
    private long duration;
	private String title;

    /**
     * 
     */
    public Msg() {
        super();
    }

    /**
     * @param title
     * @param duration
     * @param title
     */
    public Msg(String message, long duration, String title) {
        super();
        this.message = message;
        this.duration = duration;
        this.title = title;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
    
}