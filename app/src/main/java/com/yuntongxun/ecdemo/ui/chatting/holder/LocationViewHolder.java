package com.yuntongxun.ecdemo.ui.chatting.holder;

import com.yuntongxun.ecdemo.R;
import com.yuntongxun.ecdemo.common.base.CCPTextView;
import com.yuntongxun.ecdemo.ui.chatting.base.EmojiconTextView;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LocationViewHolder extends BaseHolder {

	public View chattingContent;
	/**
	 * TextView that display IMessage description.
	 */
	public EmojiconTextView descTextView;
	
	public RelativeLayout relativeLayout;
	public TextView readTv;
	
	/**
	 * @param type
	 */
	public LocationViewHolder(int type) {
		super(type);

	}
	
	public BaseHolder initBaseHolder(View baseView , boolean receive) {
		super.initBaseHolder(baseView);

		chattingTime = (TextView) baseView.findViewById(R.id.chatting_time_tv);
		chattingUser = (TextView) baseView.findViewById(R.id.chatting_user_tv);
		descTextView = (EmojiconTextView) baseView.findViewById(R.id.tv_location);
		checkBox = (CheckBox) baseView.findViewById(R.id.chatting_checkbox);
		chattingMaskView = baseView.findViewById(R.id.chatting_maskview);
		chattingContent = baseView.findViewById(R.id.chatting_content_area);
		relativeLayout=(RelativeLayout) baseView.findViewById(R.id.re_location);
//		if(!receive){
			readTv =(TextView) baseView.findViewById(R.id.tv_read_unread);
//		}
		if(receive) {
			type = 10;
			return this;
		}
		
		uploadState = (ImageView) baseView.findViewById(R.id.chatting_state_iv);
		progressBar = (ProgressBar) baseView.findViewById(R.id.uploading_pb);
		type = 11;
		return this;
	}

	/**
	 * {@link CCPTextView} Display imessage text 
	 * @return
	 */
	public EmojiconTextView getDescTextView() {
		if(descTextView == null) {
			descTextView = (EmojiconTextView) getBaseView().findViewById(R.id.chatting_content_itv);
		}
		return descTextView;
	}
	
	/**
	 * 
	 * @return
	 */
	public ImageView getChattingState() {
		if(uploadState == null) {
			uploadState = (ImageView) getBaseView().findViewById(R.id.chatting_state_iv);
		}
		return uploadState;
	}
	
	/**
	 * 
	 * @return
	 */
	public ProgressBar getUploadProgressBar() {
		if(progressBar == null) {
			progressBar = (ProgressBar) getBaseView().findViewById(R.id.uploading_pb);
		}
		return progressBar;
	}

	@Override
	public TextView getReadTv() {
		return readTv;
	}
}
