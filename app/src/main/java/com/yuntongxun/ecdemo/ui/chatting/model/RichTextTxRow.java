package com.yuntongxun.ecdemo.ui.chatting.model;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuntongxun.ecdemo.R;
import com.yuntongxun.ecdemo.ui.chatting.ChattingActivity;
import com.yuntongxun.ecdemo.ui.chatting.holder.BaseHolder;
import com.yuntongxun.ecdemo.ui.chatting.holder.RichTextViewHolder;
import com.yuntongxun.ecdemo.ui.chatting.view.ChattingItemContainer;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.im.ECPreviewMessageBody;

public class RichTextTxRow extends BaseChattingRow {

	public RichTextTxRow(int type) {
		super(type);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View buildChatView(LayoutInflater inflater, View convertView) {
		 if (convertView == null ) {
	            convertView = new ChattingItemContainer(inflater, R.layout.chatting_item_rich_text_to);
	            RichTextViewHolder holder = new RichTextViewHolder(mRowType);
	            convertView.setTag(holder.initBaseHolder(convertView, true));
	        } 
			return convertView;
	}

	@Override
	public int getChatViewType() {
		// TODO Auto-generated method stub
		return ChattingRowType.RICH_TEXT_ROW_TO.ordinal();
	}

	@Override
	public boolean onCreateRowContextMenu(ContextMenu contextMenu,
			View targetView, ECMessage detail) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void buildChattingData(Context context, BaseHolder baseHolder,
			ECMessage detail, int position) {

		final RichTextViewHolder holder = (RichTextViewHolder) baseHolder;
		ECMessage message = detail;
		if(message != null) {
			ViewHolderTag holderTag = ViewHolderTag.createTag(detail,
					ViewHolderTag.TagType.TAG_IM_RICH_TEXT, position);
			ECPreviewMessageBody textBody = (ECPreviewMessageBody) message.getBody();
			holder.descTextView.setText(textBody.getTitle());
			holder.descTextView.setTextColor(Color.BLACK);
			holder.tvUrl.setText(textBody.getUrl());
			View.OnClickListener onClickListener = ((ChattingActivity) context).mChattingFragment.getChattingAdapter().getOnClickListener();
			getMsgStateResId(position, holder, detail, onClickListener);

			if(!TextUtils.isEmpty(textBody.getLocalUrl())){
				ImageLoader.getInstance().displayImage("file://"+textBody.getLocalUrl(),holder.imageView);
			}


			holder.relativeLayout.setTag(holderTag);
			holder.relativeLayout.setOnClickListener(onClickListener);
		}

		
	}

}
