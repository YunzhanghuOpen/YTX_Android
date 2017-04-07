package com.yuntongxun.ecdemo.ui.chatting.holder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yuntongxun.ecdemo.R;
import com.yuntongxun.ecdemo.common.CCPAppManager;

/**
 * Created by ustc on 2016/6/24.
 */
public class RedPacketAckViewHolder extends BaseHolder {

    public View chattingContent;
    public TextView tvMsg;
    /**
     * TextView that display IMessage description.
     */
    /**
     * @param type
     */
    public RedPacketAckViewHolder(int type) {
        super(type);

    }

    public BaseHolder initBaseHolder(View baseView, boolean receive) {
        super.initBaseHolder(baseView);
        chattingTime = (TextView) baseView.findViewById(R.id.chatting_time_tv);
        chattingUser = (TextView) baseView.findViewById(R.id.chatting_user_tv);
        checkBox = (CheckBox) baseView.findViewById(R.id.chatting_checkbox);
        chattingMaskView = baseView.findViewById(R.id.chatting_maskview);
        chattingContent = baseView.findViewById(R.id.chatting_content_area);
        tvMsg = (TextView) baseView.findViewById(R.id.tv_money_msg);
        if (receive) {
            type = 18;
        } else {
            type = 19;
        }
        return this;
    }

    /**
     * @return
     */
    public TextView getRedPacketAckMsgTv() {
        if (tvMsg == null) {
            tvMsg = (TextView) getBaseView().findViewById(R.id.tv_money_greeting);
        }
        return tvMsg;
    }

    /**
     * @return
     */
    public ProgressBar getUploadProgressBar() {
        if (progressBar == null) {
            progressBar = (ProgressBar) getBaseView().findViewById(R.id.uploading_pb);
        }
        return progressBar;
    }

    @Override
    public TextView getReadTv() {
        return new TextView(CCPAppManager.getContext());
    }

}

