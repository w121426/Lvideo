package com.lvideo.adapter;

import java.util.List;
import com.lvideo.R;
import com.lvideo.message.Msg;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MessageListViewAdapter extends BaseAdapter{

	List<Msg> listMessages;
	int local_postion = 0;
	boolean imageChage = false;
	private LayoutInflater mLayoutInflater;
	public MessageListViewAdapter(Context context, List<Msg> listMessages){
		mLayoutInflater = LayoutInflater.from(context);
		this.listMessages = listMessages;
	}
	@Override
	public int getCount() {
		return listMessages.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.comment_list_view, null);
			holder.message = (TextView)convertView.findViewById(R.id.message);
			holder.time = (TextView)convertView.findViewById(R.id.message_time);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder)convertView.getTag();
		}
			holder.message.setText(listMessages.get(position).getMessage());//ms
			long min = listMessages.get(position).getDuration() /1000 / 60;
			long sec = listMessages.get(position).getDuration() /1000 % 60;
			holder.time.setText(min+" : "+sec);
		
		return convertView;
	}

	public final class ViewHolder{
		public TextView message;
		public TextView time;
	}
}
