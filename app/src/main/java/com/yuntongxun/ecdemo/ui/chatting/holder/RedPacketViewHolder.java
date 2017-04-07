package com.yuntongxun.ecdemo.ui.chatting.holder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yuntongxun.ecdemo.R;
import com.yuntongxun.ecdemo.common.CCPAppManager;

/**
 * Created by ustc on 2016/6/24.
 */
public class RedPacketViewHolder extends BaseHolder {

    public View chattingContent;
    public TextView tv_money_greeting;
    public TextView tv_sponsor_name;
    public TextView tv_packet_type;
    public RelativeLayout bubble;
    /**
     * TextView that display IMessage description.
     */


    /**
     * @param type
     */
    public RedPacketViewHolder(int type) {
        super(type);

    }

    public BaseHolder initBaseHolder(View baseView, boolean receive) {
        super.initBaseHolder(baseView);

        chattingTime = (TextView) baseView.findViewById(R.id.chatting_time_tv);
        chattingUser = (TextView) baseView.findViewById(R.id.chatting_user_tv);
        tv_money_greeting = (TextView) baseView.findViewById(R.id.tv_money_greeting);
        tv_sponsor_name = (TextView) baseView.findViewById(R.id.tv_sponsor_name);
        tv_packet_type = (TextView) baseView.findViewById(R.id.tv_packet_type);
        bubble = (RelativeLayout) baseView.findViewById(R.id.bubble);
        checkBox = (CheckBox) baseView.findViewById(R.id.chatting_checkbox);
        chattingMaskView = baseView.findViewById(R.id.chatting_maskview);
        chattingContent = baseView.findViewById(R.id.chatting_content_area);
        if (receive) {
            type = 16;
            return this;
        }

//        uploadState = (ImageView) baseView.findViewById(R.id.chatting_state_iv);
//        progressBar = (ProgressBar) baseView.findViewById(R.id.uploading_pb);
        type = 17;
        return this;
    }

    /**
     * @return
     */
    public TextView getGreetingTv() {
        if (tv_money_greeting == null) {
            tv_money_greeting = (TextView) getBaseView().findViewById(R.id.tv_money_greeting);
        }
        return tv_money_greeting;
    }

    public TextView getSponsorNameTv() {
        if (tv_sponsor_name == null) {
            tv_sponsor_name = (TextView) getBaseView().findViewById(R.id.tv_sponsor_name);
        }
        return tv_sponsor_name;
    }

    public TextView getPacketTypeTv() {
        if (tv_packet_type == null) {
            tv_packet_type = (TextView) getBaseView().findViewById(R.id.tv_packet_type);
        }
        return tv_packet_type;
    }

    public RelativeLayout getBubble() {
        if (bubble == null) {
            bubble = (RelativeLayout) getBaseView().findViewById(R.id.bubble);
        }
        return bubble;
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

