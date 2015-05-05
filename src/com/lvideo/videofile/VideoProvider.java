package com.lvideo.videofile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lvideo.json.JsonFunctions;
import com.lvideo.util.AppLog;
import com.lvideo.util.PinyinUtils;

import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class VideoProvider implements AbstructProvider {
    private Context context;
	private ProgressDialog pDialog;
	JSONArray videolist = null;
	List<Video> list = null;
	JsonFunctions jsonfunctions = new JsonFunctions();
    public VideoProvider(Context context) {
        this.context = context;
    }
    
    @Override
    public List<Video> getList() {
        list = new ArrayList<Video>();
        if (context != null) {
        	try {
        		// get videolists from JSON and parse them. Wait until async task is complete before continuing
    			new Getvideolists().execute().get();
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (ExecutionException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        }
        return list;
    }
    private class Getvideolists extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(context);
                pDialog.setMessage("Fetching videolists..");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
        }

        /**
         * Getting data
         * */
        protected String doInBackground(String... args) {
        	
        	JSONObject json = new JSONObject();
        	json = jsonfunctions.getvideolist();
        	Log.d("Create Response", json.toString());
        	try {

                // Lobbies element is an array of JSON objects
                //JSONObject lobbyList = json.getJSONObject(TAG_LOBBIES);
                videolist = json.getJSONArray("videolist");
                // looping through All Lobbies
                for(int i = 0; i < videolist.length(); i++){
                	JSONObject temp = jsonfunctions.getvideolisturl(videolist.getJSONObject(i).getString("url"));
                	// Storing each JSON item in a variable
                    String title = temp.getString("title");
                    String url = temp.getString("url");
                    int sec = temp.getInt("sec");
                    int min = temp.getInt("min");
                    String urljpg = temp.getString("urljpg");
                    
                    long duration = sec * 1000 + min * 60 * 1000;
                    String title_key = "A";
                    if (title != null && title.length() > 0) {
                    	try {
							title_key = PinyinUtils.chineneToSpell(title.charAt(0) + "");
						} catch (BadHanyuPinyinOutputFormatCombination e) {
							e.printStackTrace();
						}
					}
                    AppLog.e("dd",title_key);
                    Video video = new Video(title, url, duration, title_key, urljpg);
                    list.add(video);
                }
            } catch (JSONException e) {
            	e.printStackTrace();
            }
                        
                        
            return null;
        }
        
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
                // dismiss the dialog once done
                pDialog.dismiss();
             

                // display the toast notification containing the returned JSON message
                if (file_url != null){
                   	Toast.makeText(context, file_url, Toast.LENGTH_LONG).show();
                }
                
        }
	}

}