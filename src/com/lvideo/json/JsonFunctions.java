package com.lvideo.json;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonFunctions {

	private JSONParser jsonParser;
	private static String urlgetvideolist = "http://ec2-54-68-97-146.us-west-2.compute.amazonaws.com/whois/videolists.json";
	private static String urlgetMessage = "http://ec2-54-68-97-146.us-west-2.compute.amazonaws.com/whois/lists.json";
	private static String urlpostMessage = "http://ec2-54-68-97-146.us-west-2.compute.amazonaws.com/whois/lists.json";
	
	// constructor
    public JsonFunctions(){
        jsonParser = new JSONParser();
    }
     
    /**
     * function get list of Lobbies
     * */
    public JSONObject videolist(){
        JSONObject json = jsonParser.getHttpRequest(urlgetvideolist, 0);
        return json;
    }
    public JSONObject getvideolist(){
        return videolist();
    }
    public JSONObject getvideolisturl(String url){
        JSONObject json = jsonParser.getHttpRequest(url, 0);
        return json;
    }
    public JSONObject getMessage(){
        JSONObject json = jsonParser.getHttpRequest(urlgetMessage, 1);
        return json;
    }
    /**
     * function get message of one video
     * @param title
     * @param time
     * @param message
     * @throws JSONException 
     * */
    public JSONObject postMessage(String title, String message, int time) throws JSONException{
    	//Building Parameters
    	JSONObject params = new JSONObject();
        params.put("title", title);
        params.put("infor", message);
        params.put("time", ""+time);

        // getting JSON Object
        // NOTE: The create_user URL accepts POST method
        JSONObject json = jsonParser.makeHttpRequest(urlpostMessage,
                        "POST", params.toString());
        return json;
    }
	

}