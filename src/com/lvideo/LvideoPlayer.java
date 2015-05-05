package com.lvideo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lvideo.adapter.LvideoListViewAdapter;
import com.lvideo.adapter.MessageListViewAdapter;
import com.lvideo.json.JsonFunctions;
import com.lvideo.message.Msg;
import com.lvideo.util.AppLog;
import com.lvideo.util.PinyinUtils;
import com.lvideo.videofile.Video;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class LvideoPlayer extends Activity implements OnCompletionListener, OnInfoListener {

	private String mPath;
	private String mTitle;
	private VideoView mVideoView;
	private View mVolumeBrightnessLayout;
	private ImageView mOperationBg;
	private ImageView mOperationPercent;
	private AudioManager mAudioManager;
	/** 最大声音  */
	private int mMaxVolume;
	/** 当前声音  */
	private int mVolume = -1;
	/** 当前亮度  */
	private float mBrightness = -1f;
	/** 当前缩放模式  */
	private int mLayout = VideoView.VIDEO_LAYOUT_ZOOM;
	private GestureDetector mGestureDetector;
	private MediaController mMediaController;
	private View mLoadingView;
	private Video playVideoFile;
	ListView mMessageListView;
	MessageListViewAdapter mMessageListViewAdapter;
	private ProgressDialog pDialog;
	JSONArray messagelist = null;
	List<Msg> list = null;
	JsonFunctions jsonfunctions = new JsonFunctions();
	int listSize;
    private Button send;  
    private Button refresh; 
    private Button exit;
    private EditText msg;
    private View mMessageLayout;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ~~~ 检测Vitamio是否解压解码包
		if (!LibsChecker.checkVitamioLibs(this))
			return;

		// ~~~ 获取播放地址和标题
		Bundle bundle = getIntent().getExtras();
		playVideoFile = (Video)bundle.getSerializable("video");
		mPath = playVideoFile.getPath();
		mTitle = playVideoFile.getTitle();
		list = new ArrayList<Msg>();
		setContentView(R.layout.videoview);
		try {
    		// get videolists from JSON and parse them. Wait until async task is complete before continuing
			new Getmessagelists().execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        listSize = list.size();
        Log.d("load", ""+listSize);
        mMessageLayout = findViewById(R.id.operation_msg_list);
        mMessageListViewAdapter = new MessageListViewAdapter(this, list);
        mMessageListView = (ListView)findViewById(R.id.msg_list);
        if (mMessageListViewAdapter == null)
        	Log.d("load", "1");
        if (mMessageListView == null)
        	Log.d("load", "2");
        mMessageListView.setAdapter(mMessageListViewAdapter);
        mMessageListView.getOnItemClickListener();
        loadmessage();
        send = (Button) findViewById(R.id.send);
        refresh = (Button) findViewById(R.id.refresh);
        exit = (Button) findViewById(R.id.exit);
        msg = (EditText) findViewById(R.id.editText1);
		send.setOnClickListener(mylistener);  
        refresh.setOnClickListener(mylistener);  
        exit.setOnClickListener(mylistener); 
        Log.d("post","OnClickListener");
        
		// ~~~ 绑定控件
		
		mVideoView = (VideoView) findViewById(R.id.surface_view);
		mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);
		mOperationBg = (ImageView) findViewById(R.id.operation_bg);
		mOperationPercent = (ImageView) findViewById(R.id.operation_percent);
		mLoadingView = findViewById(R.id.video_loading);

		// ~~~ 绑定事件
		mVideoView.setOnCompletionListener(this);
		mVideoView.setOnInfoListener(this);

		// ~~~ 绑定数据
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		if (mPath.startsWith("http:"))
			mVideoView.setVideoURI(Uri.parse(mPath));
		else
			mVideoView.setVideoPath(mPath);

		//设置显示名称
		mMediaController = new MediaController(this);
		mMediaController.setFileName(mTitle);
		mVideoView.setMediaController(mMediaController);
		mVideoView.requestFocus();

		mGestureDetector = new GestureDetector(this, new MyGestureListener());
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}
	
	private OnClickListener mylistener = new OnClickListener() {  
        
        @Override  
        public void onClick(View v) {  Log.d("post","onClick");
            switch (v.getId()) {  
            case R.id.send:  
            	Log.d("post","sned");
            	new Postmessage().execute();
                break;  
            case R.id.refresh: 
            	list.clear();
        		try {
            		// get videolists from JSON and parse them. Wait until async task is complete before continuing
        			new Getmessagelists().execute().get();
        		} catch (InterruptedException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		} catch (ExecutionException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
            	Log.d("post","refresh");
            	loadmessage();
                break;
            case R.id.exit:  
            	Log.d("post","exit");
            	mMessageLayout.setVisibility(View.GONE);
                break;  
            default:  
                break;  
            }     
        }  
    };  
    private void loadmessage() {
    	mMessageListViewAdapter.notifyDataSetChanged();
		
	}


	private class Getmessagelists extends AsyncTask<String, String, String> {


        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(LvideoPlayer.this);
                pDialog.setMessage("Fetching messages..");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
        }

        /**
         * Getting data
         * */
        protected String doInBackground(String... args) {
        	
        	JSONObject json = new JSONObject();
        	json = jsonfunctions.getMessage();
        	Log.d("Create Response", json.toString());
        	try {

                // Lobbies element is an array of JSON objects
                //JSONObject lobbyList = json.getJSONObject(TAG_LOBBIES);
        		messagelist = json.getJSONArray("messagelist");
                // looping through All Lobbies
                for(int i = 0; i < messagelist.length(); i++){
                	JSONObject temp = messagelist.getJSONObject(i);
                	// Storing each JSON item in a variable
                    String title = temp.getString("title");
                    String msg = temp.getString("infor");
                    long duration = (long) temp.getInt("time") * 1000;
                    Msg message = new Msg(msg, duration, title);
                    if (title.equals(mTitle))
                    	list.add(message);
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
                   	Toast.makeText(LvideoPlayer.this, file_url, Toast.LENGTH_LONG).show();
                }
                
        }
	}

    private class Postmessage extends AsyncTask<String, String, String> {


        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(LvideoPlayer.this);
                pDialog.setMessage("Post message..");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
        }

        /**
         * Getting data
         * */
        protected String doInBackground(String... args) {
            String str_title = mTitle;
            String str_message = msg.getText().toString();
            int int_duration = (int) mVideoView.getCurrentPosition() / 1000;
            if (str_message == null || "".equals(str_message))
            	return null;
            Log.d("post", str_message+" "+str_title+" "+int_duration);
            JSONObject json;
			try {
				json = jsonfunctions.postMessage(str_title, str_message, int_duration);
				Log.d("Create Response", json.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
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
                   	Toast.makeText(LvideoPlayer.this, file_url, Toast.LENGTH_LONG).show();
                }
                
        }
	}   
    @Override
	protected void onPause() {
		super.onPause();
		if (mVideoView != null)
			mVideoView.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mVideoView != null)
			mVideoView.resume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mVideoView != null)
			mVideoView.stopPlayback();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mGestureDetector.onTouchEvent(event))
			return true;

		// 处理手势结束
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_UP:
			endGesture();
			break;
		}

		return super.onTouchEvent(event);
	}

	/** 手势结束  */
	private void endGesture() {
		mVolume = -1;
		mBrightness = -1f;

		// 隐藏
		mDismissHandler.removeMessages(0);
		mDismissHandler.sendEmptyMessageDelayed(0, 500);
	}

	private class MyGestureListener extends SimpleOnGestureListener {

		/** 双击  */
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			float mOldX = e.getX();
			DisplayMetrics disp = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(disp);
			int windowWidth = disp.widthPixels;
			if (mOldX < windowWidth / 2)
			{
				if (mLayout == VideoView.VIDEO_LAYOUT_ZOOM)
					mLayout = VideoView.VIDEO_LAYOUT_ORIGIN;
				else
					mLayout++;
				if (mVideoView != null)
					mVideoView.setVideoLayout(mLayout, 0);
			}
			else
			{
				Log.d("show", "run");
				mMessageLayout.setVisibility(View.VISIBLE);
			}
				
			return true;
		}

		/** 滑动 */
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			float mOldX = e1.getX(), mOldY = e1.getY();
			int y = (int) e2.getRawY();
			DisplayMetrics disp = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(disp);
			int windowWidth = disp.widthPixels;
			int windowHeight = disp.heightPixels;

			if (mOldX > windowWidth * 4.0 / 5)// 右边滑动
				onVolumeSlide((mOldY - y) / windowHeight);
			else if (mOldX < windowWidth / 5.0)// 左边滑动
				onBrightnessSlide((mOldY - y) / windowHeight);

			return super.onScroll(e1, e2, distanceX, distanceY);
		}
	}

	/** 定时隐藏  */
	private Handler mDismissHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mVolumeBrightnessLayout.setVisibility(View.GONE);
		}
	};

	/**
	 * 滑动改变声音大小
	 * 
	 * @param percent
	 */
	private void onVolumeSlide(float percent) {
		if (mVolume == -1) {
			mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			if (mVolume < 0)
				mVolume = 0;

			// 显示
			mOperationBg.setImageResource(R.drawable.video_volumn_bg);
			mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
		}

		int index = (int) (percent * mMaxVolume) + mVolume;
		if (index > mMaxVolume)
			index = mMaxVolume;
		else if (index < 0)
			index = 0;

		// 变更声音
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

		// 变更进度条
		ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
		lp.width = findViewById(R.id.operation_full).getLayoutParams().width * index / mMaxVolume;
		mOperationPercent.setLayoutParams(lp);
	}

	/**
	 * 滑动改变亮度
	 * 
	 * @param percent
	 */
	private void onBrightnessSlide(float percent) {
		if (mBrightness < 0) {
			mBrightness = getWindow().getAttributes().screenBrightness;
			if (mBrightness <= 0.00f)
				mBrightness = 0.50f;
			if (mBrightness < 0.01f)
				mBrightness = 0.01f;

			// 显示
			mOperationBg.setImageResource(R.drawable.video_brightness_bg);
			mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
		}
		WindowManager.LayoutParams lpa = getWindow().getAttributes();
		lpa.screenBrightness = mBrightness + percent;
		if (lpa.screenBrightness > 1.0f)
			lpa.screenBrightness = 1.0f;
		else if (lpa.screenBrightness < 0.01f)
			lpa.screenBrightness = 0.01f;
		getWindow().setAttributes(lpa);

		ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
		lp.width = (int) (findViewById(R.id.operation_full).getLayoutParams().width * lpa.screenBrightness);
		mOperationPercent.setLayoutParams(lp);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (mVideoView != null)
			mVideoView.setVideoLayout(mLayout, 0);
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCompletion(MediaPlayer player) {
		finish();
	}

	private void stopPlayer() {
		if (mVideoView != null)
			mVideoView.pause();
	}

	private void startPlayer() {
		if (mVideoView != null)
			mVideoView.start();
	}

	private boolean isPlaying() {
		return mVideoView != null && mVideoView.isPlaying();
	}

	/** 是否需要自动恢复播放，用于自动暂停，恢复播放 */
	private boolean needResume;

	@Override
	public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
		switch (arg1) {
		case MediaPlayer.MEDIA_INFO_BUFFERING_START:
			//开始缓存，暂停播放
			if (isPlaying()) {
				stopPlayer();
				needResume = true;
			}
			mLoadingView.setVisibility(View.VISIBLE);
			break;
		case MediaPlayer.MEDIA_INFO_BUFFERING_END:
			//缓存完成，继续播放
			if (needResume)
				startPlayer();
			mLoadingView.setVisibility(View.GONE);
			break;
		case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
			//显示 下载速度
			AppLog.e("download rate:" + arg2);
			//mListener.onDownloadRateChanged(arg2);
			break;
		}
		return true;
	}
}
