package com.lvideo.json;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

// JSONParser class is modified code from http://www.mybringback.com/
public class JSONParser {

	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";

	// constructor
	public JSONParser() {

	}
	
    public JSONObject getJSONFromUrl(final String url) {

        // Making HTTP request
        try {
            // Construct the client and the HTTP request.
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            // Execute the POST request and store the response locally.
            HttpResponse httpResponse = httpClient.execute(httpPost);
            // Extract data from the response.
            HttpEntity httpEntity = httpResponse.getEntity();
            // Open an inputStream with the data content.
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // Create a BufferedReader to parse through the inputStream.
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            // Declare a string builder to help with the parsing.
            StringBuilder sb = new StringBuilder();
            // Declare a string to store the JSON object data in string form.
            String line = null;
            
            // Build the string until null.
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            
            // Close the input stream.
            is.close();
            // Convert the string builder data to an actual string.
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // Try to parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // Return the JSON Object.
        return jObj;

    }
    // new Get method need test
	public JSONObject getHttpRequest(String url, int tag) {

		// Making HTTP request
		try {
				// request method is GET
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(url);

				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}
		
        final char[] strChar = json.substring(0, 1).toCharArray();
        final char firstChar = strChar[0]; 
        int type = -1;
        if(firstChar == '{'){
            type = 0;
        }else if(firstChar == '['){
        	type = 1;
        }else{
        	type = 2;
        } 
		// try parse the string to a JSON object
		try {
			if (type == 1 && tag == 0)
			{
				jObj = new JSONObject();
				JSONArray array = new JSONArray(json);
				jObj.put("videolist", array);
			}
			else if (type == 1 && tag == 1)
			{
				jObj = new JSONObject();
				JSONArray array = new JSONArray(json);
				jObj.put("messagelist", array);
			}
			else
				jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		// return JSON String
		return jObj;

	}
    
    
	// function get json from url
	// by making HTTP POST or GET method
	public JSONObject makeHttpRequest(String url, String method,
			String params) {

		// Making HTTP request
		try {
			/*
			 * GET is idempotent: it is for obtaining a resource, without changing anything on the server. As a consequence it should be perfectly safe to resubmit a GET request.
				POST is not: it is for updating information on the server. It can therefore not be assumed that it is safe to re-submit the request which is why most browsers ask for confirmation when you hit refresh on a POST request.
				In terms of security, no difference. POST is more obscure, perhaps, but that's a very different thing. Security needs to be added at another layer, for example SSL.*/
			// check for request method
			if(method == "POST"){
				// request method is POST
				// defaultHttpClient
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);
				StringEntity entity = new StringEntity(params);
				entity.setContentType("application/json");
				httpPost.setEntity(entity);
				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				//Log.d("post in p", httpEntity.toString());
				is = httpEntity.getContent();
				
			}else if(method == "GET"){
				// request method is GET
				//DefaultHttpClient httpClient = new DefaultHttpClient();
				//String paramString = URLEncodedUtils.format(params, "utf-8");
				//url += "?" + paramString;
				//HttpGet httpGet = new HttpGet(url);

				//HttpResponse httpResponse = httpClient.execute(httpGet);
				//HttpEntity httpEntity = httpResponse.getEntity();
				//is = httpEntity.getContent();
			}			
			

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		// return JSON String
		return jObj;

	}
}
