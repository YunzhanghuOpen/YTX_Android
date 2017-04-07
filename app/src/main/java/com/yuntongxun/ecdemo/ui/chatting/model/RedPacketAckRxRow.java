package com.yuntongxun.ecdemo.ui.chatting.model;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;

import com.yuntongxun.ecdemo.R;
import com.yuntongxun.ecdemo.common.CCPAppManager;
import com.yuntongxun.ecdemo.ui.chatting.holder.BaseHolder;
import com.yuntongxun.ecdemo.ui.chatting.holder.RedPacketAckViewHolder;
import com.yuntongxun.ecdemo.ui.chatting.redpacketutils.RedPacketUtil;
import com.yuntongxun.ecdemo.ui.chatting.view.ChattingItemContainer;
import com.yuntongxun.ecsdk.ECMessage;
import com.yunzhanghu.redpacketsdk.constant.RPConstant;

import org.json.JSONObject;

/**
 * Created by ustc on 2016/6/24.
 */
public class RedPacketAckRxRow extends BaseChattingRow {


    public RedPacketAckRxRow(int type) {
        super(type);
    }

    @Override
    public View buildChatView(LayoutInflater inflater, View convertView) {
        //we have a don't have a converView so we'll have to create a new one
        if (convertView == null) {
            convertView = new ChattingItemContainer(inflater, R.layout.chatting_item_redpacket_ack_from);
            //use the view holder pattern to save of already looked up subviews
            RedPacketAckViewHolder holder = new RedPacketAckViewHolder(mRowType);
            convertView.setTag(holder.initBaseHolder(convertView, true));
        }
        return convertView;
    }

    @Override
    public void buildChattingData(final Context context, BaseHolder baseHolder, ECMessage detail, int position) {
        RedPacketAckViewHolder holder = (RedPacketAckViewHolder) baseHolder;
        ECMessage message = detail;
        if (message != null && message.getType() == ECMessage.Type.TXT) {
            JSONObject jsonObject = RedPacketUtil.getInstance().isRedPacketAckMessage(message);
            if (jsonObject != null) {
                holder.getChattingAvatar().setVisibility(View.GONE);
                holder.getChattingUser().setVisibility(View.GONE);
                String currentUserId = CCPAppManager.getClientUser().getUserId();   //当前登陆用户id
                String receiveUserId = jsonObject.optString(RPConstant.EXTRA_RED_PACKET_RECEIVER_ID);//红包接收者id
                String receiveUserNick = jsonObject.optString(RPConstant.EXTRA_RED_PACKET_RECEIVER_NAME);//红包接收者昵称
                String sendUserId = jsonObject.optString(RPConstant.EXTRA_RED_PACKET_SENDER_ID);//红包发送者id
                String sendUserNick = jsonObject.optString(RPConstant.EXTRA_RED_PACKET_SENDER_NAME);//红包发送者昵称
                String text = "";
                //发送者和领取者都是自己-
                if (currentUserId.equals(receiveUserId) && currentUserId.equals(sendUserId)) {
                    text = context.getResources().getString(R.string.money_msg_take_money);
                } else if (currentUserId.equals(sendUserId)) {
                    //我仅仅是发送者
                    text = String.format(context.getResources().getString(R.string.money_msg_someone_take_money), receiveUserNick);
                } else if (currentUserId.equals(receiveUserId)) {
                    //我仅仅是接收者
                    text = String.format(context.getResources().getString(R.string.money_msg_take_someone_money), sendUserNick);
                }
                holder.getRedPacketAckMsgTv().setText(text);
            }
        }
    }

    @Override
    public int getChatViewType() {

        return ChattingRowType.REDPACKE_ROW_ACK_RECEIVED.ordinal();
    }

    @Override
    public boolean onCreateRowContextMenu(ContextMenu contextMenu, View targetView, ECMessage detail) {

        return false;
    }
}
