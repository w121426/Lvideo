package com.lvideo;

import io.vov.vitamio.LibsChecker;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.lvideo.adapter.LvideoListViewAdapter;
import com.lvideo.component.LoadedImage;
import com.lvideo.videofile.AbstructProvider;
import com.lvideo.videofile.Video;
import com.lvideo.videofile.VideoProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Viedolist extends Activity{

	public Viedolist instance = null;
	ListView mLvideoListView;
	LvideoListViewAdapter mLvideoListViewAdapter;
	List<Video> listVideos;
	private TextView first_letter_overlay;
	private ImageView alphabet_scroller; //×ÖÄ¸¹ö¶¯²éÑ¯±í
	int videoSize;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!LibsChecker.checkVitamioLibs(this))
			return;
		setContentView(R.layout.activity_viedolist);
		instance = this;
		AbstructProvider provider = new VideoProvider(instance);
        listVideos = provider.getList();
        videoSize = listVideos.size();
        Log.d("load", ""+videoSize);
        mLvideoListViewAdapter = new LvideoListViewAdapter(this, listVideos);
		mLvideoListView = (ListView)findViewById(R.id.jievideolistfile);
		mLvideoListView.setAdapter(mLvideoListViewAdapter);
		mLvideoListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Intent intent = new Intent();
				intent.setClass(Viedolist.this, LvideoPlayer.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("video", listVideos.get(position));
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		loadImages();
	    first_letter_overlay = (TextView)findViewById(R.id.first_letter_overlay);
        alphabet_scroller = (ImageView)findViewById(R.id.alphabet_scroller);
        alphabet_scroller.setClickable(true);
        alphabet_scroller.setOnTouchListener(asOnTouch);
	}
	/**
	 * Load images.
	 */
	private void loadImages() {
		@SuppressWarnings("deprecation")
		final Object data = getLastNonConfigurationInstance();
		if (data == null) {
			new LoadImagesFromWeb().execute();
		} else {
			final LoadedImage[] photos = (LoadedImage[]) data;
			if (photos.length == 0) {
				new LoadImagesFromWeb().execute();
			}
			for (LoadedImage photo : photos) {
				addImage(photo);
			}
		}
	}
	private void addImage(LoadedImage... value) {
		for (LoadedImage image : value) {
			mLvideoListViewAdapter.addPhoto(image);
			mLvideoListViewAdapter.notifyDataSetChanged();
		}
	}
	@Override
	public Object onRetainNonConfigurationInstance() {
		final ListView grid = mLvideoListView;
		final int count = grid.getChildCount();
		final LoadedImage[] list = new LoadedImage[count];

		for (int i = 0; i < count; i++) {
			final ImageView v = (ImageView) grid.getChildAt(i);
			list[i] = new LoadedImage(
					((BitmapDrawable) v.getDrawable()).getBitmap());
		}

		return list;
	}
	/** 
	    * »ñÈ¡ÊÓÆµËõÂÔÍ¼ 
	    * @param jpgPath 
	    * @param width 
	    * @param height 
	    * @param kind 
	    * @return 
	    */  
	   private Bitmap getVideoThumbnail(String jpgPath, int width , int height, int kind){  
	    Bitmap bitmap = null;  
	    URL myFileUrl = null;   
        try {   
            myFileUrl = new URL(jpgPath);   
        } catch (MalformedURLException e) {   
            e.printStackTrace();   
        }   
        try {   
            HttpURLConnection conn = (HttpURLConnection) myFileUrl   
              .openConnection();   
            conn.setDoInput(true);   
            conn.connect();   
            InputStream is = conn.getInputStream();   
            bitmap = BitmapFactory.decodeStream(is);   
            is.close();   
        } catch (IOException e) {   
              e.printStackTrace();   
        }    
	    bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
	    return bitmap;  
	   }  

	class LoadImagesFromWeb extends AsyncTask<Object, LoadedImage, Object> {
		@Override
		protected Object doInBackground(Object... params) {
			Bitmap bitmap = null;
			for (int i = 0; i < videoSize; i++) {
				Log.d("video", ""+listVideos.get(i).getjpgPath());
				bitmap = getVideoThumbnail(listVideos.get(i).getjpgPath(), 120, 120, Thumbnails.MINI_KIND);
				if (bitmap != null) {
					publishProgress(new LoadedImage(bitmap));
				}
			}
			return null;
		}
		
		@Override
		public void onProgressUpdate(LoadedImage... value) {
			addImage(value);
		}
	}
	/**
	 * A-Z
	 */
	private OnTouchListener asOnTouch = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:// 0
				alphabet_scroller.setPressed(true);
				first_letter_overlay.setVisibility(View.VISIBLE);
				mathScrollerPosition(event.getY());
				break;
			case MotionEvent.ACTION_UP:// 1
				alphabet_scroller.setPressed(false);
				first_letter_overlay.setVisibility(View.GONE);
				break;
			case MotionEvent.ACTION_MOVE:
				mathScrollerPosition(event.getY());
				break;
			}
			return false;
		}
	};

	/**
	 * ÏÔÊ¾×Ö·û
	 * 
	 * @param y
	 */
	private void mathScrollerPosition(float y) {
		int height = alphabet_scroller.getHeight();
		float charHeight = height / 28.0f;
		char c = 'A';
		if (y < 0)
			y = 0;
		else if (y > height)
			y = height;

		int index = (int) (y / charHeight) - 1;
		if (index < 0)
			index = 0;
		else if (index > 25)
			index = 25;

		String key = String.valueOf((char) (c + index));
		first_letter_overlay.setText(key);

		int position = 0;
		if (index == 0)
			mLvideoListView.setSelection(0);
		else if (index == 25)
			mLvideoListView.setSelection(mLvideoListViewAdapter.getCount() - 1);
		else {
			int size = listVideos.size();
			for (int i = 0; i < size; i++) {
				if (listVideos.get(i).getTitle_key().startsWith(key)) {
					mLvideoListView.setSelection(position);
					break;
				}
				position++;
			}
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
}