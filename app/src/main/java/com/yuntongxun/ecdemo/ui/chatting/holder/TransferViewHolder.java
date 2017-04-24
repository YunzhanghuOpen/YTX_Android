package com.yuntongxun.ecdemo.ui.chatting.holder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yuntongxun.ecdemo.R;
import com.yuntongxun.ecdemo.common.CCPAppManager;

/**
 * Created by desert on 2017/4/24.
 */
public class TransferViewHolder extends BaseHolder {

    public View chattingContent;
    public TextView tv_transfer_amount;
    public RelativeLayout bubble;

    /**
     * @param type
     */
    public TransferViewHolder(int type) {
        super(type);

    }

    public BaseHolder initBaseHolder(View baseView, boolean receive) {
        super.initBaseHolder(baseView);

        chattingTime = (TextView) baseView.findViewById(R.id.chatting_time_tv);
        chattingUser = (TextView) baseView.findViewById(R.id.chatting_user_tv);
        tv_transfer_amount = (TextView) baseView.findViewById(R.id.tv_transfer_amount);
        bubble = (RelativeLayout) baseView.findViewById(R.id.bubble);
        checkBox = (CheckBox) baseView.findViewById(R.id.chatting_checkbox);
        chattingMaskView = baseView.findViewById(R.id.chatting_maskview);
        chattingContent = baseView.findViewById(R.id.chatting_content_area);
        if (receive) {
            type = 20;
            return this;
        }
        type = 21;
        return this;
    }

    /**
     * @return
     */
    public TextView getAmountTv() {
        if (tv_transfer_amount == null) {
            tv_transfer_amount = (TextView) getBaseView().findViewById(R.id.tv_transfer_amount);
        }
        return tv_transfer_amount;
    }

    public RelativeLayout getBubble() {
        if (bubble == null) {
            bubble = (RelativeLayout) getBaseView().findViewById(R.id.bubble);
        }
        return bubble;
    }

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

