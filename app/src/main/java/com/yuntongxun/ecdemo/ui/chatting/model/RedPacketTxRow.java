package com.yuntongxun.ecdemo.ui.chatting.model;

import android.content.Context;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;

import com.yuntongxun.ecdemo.R;
import com.yuntongxun.ecdemo.ui.chatting.ChattingActivity;
import com.yuntongxun.ecdemo.ui.chatting.holder.BaseHolder;
import com.yuntongxun.ecdemo.ui.chatting.holder.RedPacketViewHolder;
import com.yuntongxun.ecdemo.ui.chatting.redpacketutils.RedPacketUtil;
import com.yuntongxun.ecdemo.ui.chatting.view.ChattingItemContainer;
import com.yuntongxun.ecsdk.ECMessage;
import com.yunzhanghu.redpacketsdk.constant.RPConstant;

import org.json.JSONObject;

/**
 * Created by ustc on 2016/6/24.
 */
public class RedPacketTxRow extends BaseChattingRow {

    public RedPacketTxRow(int type) {
        super(type);
    }

    /* (non-Javadoc)
     * @see com.hisun.cas.model.im.ChattingRow#buildChatView(android.view.LayoutInflater, android.view.View)
     */
    @Override
    public View buildChatView(LayoutInflater inflater, View convertView) {
        //we have a don't have a converView so we'll have to create a new one
        if (convertView == null || ((BaseHolder) convertView.getTag()).getType() != mRowType) {

            convertView = new ChattingItemContainer(inflater, R.layout.chatting_item_redpacket_to);

            //use the view holder pattern to save of already looked up subviews
            RedPacketViewHolder holder = new RedPacketViewHolder(mRowType);
            convertView.setTag(holder.initBaseHolder(convertView, false));
        }
        return convertView;
    }

    @Override
    public void buildChattingData(Context context, BaseHolder baseHolder, ECMessage message, int position) {
        if (message != null && message.getType() == ECMessage.Type.TXT) {
            RedPacketViewHolder holder = (RedPacketViewHolder) baseHolder;
            JSONObject jsonObject = RedPacketUtil.getInstance().isRedPacketMessage(message);
            if (jsonObject != null) {
                //清除文本框，和加载progressdialog
                String greeting = jsonObject.optString(RPConstant.MESSAGE_ATTR_RED_PACKET_GREETING);
                String sponsorName = jsonObject.optString(RPConstant.MESSAGE_ATTR_RED_PACKET_SPONSOR_NAME);
                String packetType = jsonObject.optString(RPConstant.MESSAGE_ATTR_RED_PACKET_TYPE);
                holder.getGreetingTv().setText(greeting);
                holder.getSponsorNameTv().setText(sponsorName);
                if (!TextUtils.isEmpty(packetType) && TextUtils.equals(packetType, RPConstant.RED_PACKET_TYPE_GROUP_EXCLUSIVE)) {
                    holder.getPacketTypeTv().setVisibility(View.VISIBLE);
                    holder.getPacketTypeTv().setText(context.getResources().getString(R.string.exclusive_red_packet));
                } else {
                    holder.getPacketTypeTv().setVisibility(View.GONE);
                }
                ViewHolderTag holderTag = ViewHolderTag.createTag(message, ViewHolderTag.TagType.TAG_IM_REDPACKET, position);
                View.OnClickListener onClickListener = ((ChattingActivity) context).mChattingFragment.getChattingAdapter().getOnClickListener();
                holder.getBubble().setTag(holderTag);
                holder.getBubble().setOnClickListener(onClickListener);
            }
        }


    }


    @Override
    public int getChatViewType() {
        return ChattingRowType.REDPACKE_ROW_TO.ordinal();
    }

    @Override
    public boolean onCreateRowContextMenu(ContextMenu contextMenu,
                                          View targetView, ECMessage detail) {

        return false;
    }


}
