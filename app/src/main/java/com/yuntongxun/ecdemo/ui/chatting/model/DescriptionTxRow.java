/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.cloopen.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.yuntongxun.ecdemo.ui.chatting.model;

import android.content.Context;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.yuntongxun.ecdemo.R;
import com.yuntongxun.ecdemo.ui.chatting.ChattingActivity;
import com.yuntongxun.ecdemo.ui.chatting.holder.BaseHolder;
import com.yuntongxun.ecdemo.ui.chatting.holder.DescriptionViewHolder;
import com.yuntongxun.ecdemo.ui.chatting.view.CCPChattingFooter2;
import com.yuntongxun.ecdemo.ui.chatting.view.ChattingItemContainer;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.im.ECTextMessageBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author Jorstin Chan
 * @date 2014-4-17
 * @version 1.0
 */
public class DescriptionTxRow extends BaseChattingRow{

	public DescriptionTxRow(int type){
		super(type);
	}
	
	/* (non-Javadoc)
	 * @see com.hisun.cas.model.im.ChattingRow#buildChatView(android.view.LayoutInflater, android.view.View)
	 */
	@Override
	public View buildChatView(LayoutInflater inflater, View convertView) {
        //we have a don't have a converView so we'll have to create a new one
        if (convertView == null || ((BaseHolder)convertView.getTag()).getType() != mRowType) {
        	
        	convertView = new ChattingItemContainer(inflater, R.layout.chatting_item_to);

            //use the view holder pattern to save of already looked up subviews
        	DescriptionViewHolder holder = new DescriptionViewHolder(mRowType);
        	convertView.setTag(holder.initBaseHolder(convertView, false));
        }
		return convertView;
	}

	@Override
	public void buildChattingData(Context context, BaseHolder baseHolder,
			ECMessage msg, int position) {
		DescriptionViewHolder holder = (DescriptionViewHolder) baseHolder;
		if (msg != null) {
			String msgType = "";
			JSONArray jsonArray = null;
			if (!TextUtils.isEmpty(msg.getUserData())) try {
				JSONObject jsonObject = new JSONObject(msg.getUserData());
				msgType = jsonObject.getString(CCPChattingFooter2.TXT_MSGTYPE);
				jsonArray = jsonObject.getJSONArray(CCPChattingFooter2.MSG_DATA);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (TextUtils.equals(msgType, CCPChattingFooter2.FACETYPE)) {
				holder.getDescTextView().setBackgroundResource(0);
			} else {
				holder.getDescTextView().setBackgroundResource(R.drawable.chat_to_bg_normal);
			}
			ECTextMessageBody textBody = (ECTextMessageBody) msg.getBody();
			String msgTextString = textBody.getMessage();

			holder.getDescTextView().showMessage(msg.getId() + "", msgTextString, msgType, jsonArray);
			holder.getDescTextView().setMovementMethod(LinkMovementMethod.getInstance());
			View.OnClickListener onClickListener = ((ChattingActivity) context).mChattingFragment.getChattingAdapter().getOnClickListener();
			ViewHolderTag holderTag = ViewHolderTag.createTag(msg,
					ViewHolderTag.TagType.TAG_IM_TEXT, position);
			holder.getDescTextView().setTag(holderTag);
			holder.getDescTextView().setOnClickListener(onClickListener);
			setAutoLinkForTextView(holder.getDescTextView());
			getMsgStateResId(position, holder, msg, onClickListener);
		}
	}


	@Override
	public int getChatViewType() {
		return ChattingRowType.DESCRIPTION_ROW_TRANSMIT.ordinal();
	}
	
	@Override
	public boolean onCreateRowContextMenu(ContextMenu contextMenu,
			View targetView, ECMessage detail) {
		
		return false;
	}
	

}
