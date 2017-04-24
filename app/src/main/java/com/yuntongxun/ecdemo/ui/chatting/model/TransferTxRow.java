package com.yuntongxun.ecdemo.ui.chatting.model;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;

import com.yuntongxun.ecdemo.R;
import com.yuntongxun.ecdemo.ui.chatting.ChattingActivity;
import com.yuntongxun.ecdemo.ui.chatting.holder.BaseHolder;
import com.yuntongxun.ecdemo.ui.chatting.holder.TransferViewHolder;
import com.yuntongxun.ecdemo.ui.chatting.redpacketutils.RedPacketUtil;
import com.yuntongxun.ecdemo.ui.chatting.view.ChattingItemContainer;
import com.yuntongxun.ecsdk.ECMessage;
import com.yunzhanghu.redpacketsdk.constant.RPConstant;

import org.json.JSONObject;

/**
 * Created by ustc on 2016/6/24.
 */
public class TransferTxRow extends BaseChattingRow {

    public TransferTxRow(int type) {
        super(type);
    }

    @Override
    public View buildChatView(LayoutInflater inflater, View convertView) {
        //we have a don't have a converView so we'll have to create a new one
        if (convertView == null || ((BaseHolder) convertView.getTag()).getType() != mRowType) {
            convertView = new ChattingItemContainer(inflater, R.layout.chatting_item_transfer_to);
            //use the view holder pattern to save of already looked up subviews
            TransferViewHolder holder = new TransferViewHolder(mRowType);
            convertView.setTag(holder.initBaseHolder(convertView, false));
        }
        return convertView;
    }

    @Override
    public void buildChattingData(Context context, BaseHolder baseHolder, ECMessage message, int position) {
        if (message != null && message.getType() == ECMessage.Type.TXT) {
            TransferViewHolder holder = (TransferViewHolder) baseHolder;
            JSONObject jsonObject = RedPacketUtil.getInstance().isTransferMsg(message);
            if (jsonObject != null) {
                //清除文本框，和加载progressdialog
                String amount = jsonObject.optString(RPConstant.EXTRA_TRANSFER_AMOUNT);
                holder.getAmountTv().setText(amount + "元");
                ViewHolderTag holderTag = ViewHolderTag.createTag(message, ViewHolderTag.TagType.TAG_IM_TRANSFER, position);
                View.OnClickListener onClickListener = ((ChattingActivity) context).mChattingFragment.getChattingAdapter().getOnClickListener();
                holder.getBubble().setTag(holderTag);
                holder.getBubble().setOnClickListener(onClickListener);
            }
        }

    }


    @Override
    public int getChatViewType() {
        return ChattingRowType.TRANSFER_ROW_TO.ordinal();
    }

    @Override
    public boolean onCreateRowContextMenu(ContextMenu contextMenu, View targetView, ECMessage detail) {

        return false;
    }


}
