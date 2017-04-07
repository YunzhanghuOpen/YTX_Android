/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.yuntongxun.ecdemo.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuntongxun.ecdemo.R;
import com.yuntongxun.ecdemo.common.CCPAppManager;
import com.yuntongxun.ecdemo.common.utils.DateUtil;
import com.yuntongxun.ecdemo.common.utils.DemoUtils;
import com.yuntongxun.ecdemo.common.utils.ECPreferenceSettings;
import com.yuntongxun.ecdemo.common.utils.ECPreferences;
import com.yuntongxun.ecdemo.common.utils.ResourceHelper;
import com.yuntongxun.ecdemo.storage.ContactSqlManager;
import com.yuntongxun.ecdemo.storage.ConversationSqlManager;
import com.yuntongxun.ecdemo.storage.GroupMemberSqlManager;
import com.yuntongxun.ecdemo.storage.GroupNoticeSqlManager;
import com.yuntongxun.ecdemo.ui.chatting.base.EmojiconTextView;
import com.yuntongxun.ecdemo.ui.chatting.model.Conversation;
import com.yuntongxun.ecdemo.ui.contact.ContactLogic;
import com.yuntongxun.ecdemo.ui.contact.ECContacts;
import com.yuntongxun.ecdemo.ui.group.GroupNoticeHelper;
import com.yuntongxun.ecsdk.ECMessage;

import java.util.ArrayList;



/**
 * @author 容联•云通讯
 * @date 2014-12-8
 * @version 4.0
 */
public class ConversationAdapter extends CCPListAdapter <Conversation> {

    private OnListAdapterCallBackListener mCallBackListener;
    int padding;
    private ColorStateList[] colorStateLists ;
    private String isAtSession ;
    private  Context context;
    /**
     * @param ctx
     */
    public ConversationAdapter(Context ctx , OnListAdapterCallBackListener listener) {
        super(ctx, new Conversation());
        context =ctx;
        mCallBackListener = listener;
        padding = ctx.getResources().getDimensionPixelSize(R.dimen.OneDPPadding);
        colorStateLists = new ColorStateList[]{
                ResourceHelper.getColorStateList(ctx, R.color.normal_text_color),
                ResourceHelper.getColorStateList(ctx, R.color.ccp_list_textcolor_three)
        };
    }


    @Override
    protected Conversation getItem(Conversation t, Cursor cursor) {
        Conversation conversation = new Conversation();
        conversation.setCursor(cursor);
        if(conversation.getUsername() != null && conversation.getUsername().endsWith("@priategroup.com")) {
            ArrayList<String> member = GroupMemberSqlManager.getGroupMemberID(conversation.getSessionId());
            if(member != null) {
                ArrayList<String> contactName = ContactSqlManager.getContactName(member.toArray(new String[]{}));
                if(contactName != null && !contactName.isEmpty()) {
                    String chatroomName = DemoUtils.listToString(contactName, ",");
                    conversation.setUsername(chatroomName);
                }
            }
        }
        return conversation;
    }

    /**
     * 会话时间
     * @param conversation
     * @return
     */
    protected final CharSequence getConversationTime(Conversation conversation) {
        if(conversation.getSendStatus() == ECMessage.MessageStatus.SENDING.ordinal()) {
            return mContext.getString(R.string.conv_msg_sending);
        }
        if(conversation.getDateTime() <= 0) {
            return "";
        }
        return DateUtil.getDateString(conversation.getDateTime(),
                DateUtil.SHOW_TYPE_CALL_LOG).trim();
    }

    /**
     * 根据消息类型返回相应的主题描述
     * @param conversation
     * @return
     */
    protected final CharSequence getConversationSnippet(Conversation conversation) {
        if(conversation == null) {
            return "";
        }
        if(GroupNoticeSqlManager.CONTACT_ID.equals(conversation.getSessionId())) {
            return GroupNoticeHelper.getNoticeContent(conversation.getContent());
        }

        String fromNickName = "";
        if (conversation.getSessionId().toUpperCase().startsWith("G")) {
            if(conversation.getContactId() != null && CCPAppManager.getClientUser() != null
                    && !conversation.getContactId().equals(CCPAppManager.getClientUser().getUserId())) {
                ECContacts contact = ContactSqlManager.getContact(conversation.getContactId());
                if (contact != null && contact.getNickname() != null) {
                    fromNickName = contact.getNickname() + ": ";
                } else {
                    fromNickName = conversation.getContactId() + ": ";
                }
            }
            //用字符串的方式的识别红包消息，改变会话页显示样式
            if (conversation.getMsgType() == ECMessage.Type.TXT.ordinal()) {
                String content = conversation.getContent();
                if ((content.contains("领取了") && content.contains("的红包")) || content.contains("红包消息")|| content.contains("容联云红包")) {

                    fromNickName = "";
                }
            }
        }

        // Android Demo 免打扰后需要显示未读条数
        if (!conversation.isNotice() && conversation.getUnreadCount() > 1) {
            fromNickName = "["+conversation.getUnreadCount()+"条]" + fromNickName;
        }


        if (conversation.getMsgType() == ECMessage.Type.VOICE.ordinal()) {
            return fromNickName + mContext.getString(R.string.app_voice);
        } else if(conversation.getMsgType() == ECMessage.Type.FILE.ordinal()) {
            return fromNickName + mContext.getString(R.string.app_file);
        } else if(conversation.getMsgType() == ECMessage.Type.IMAGE.ordinal()) {
            return fromNickName + mContext.getString(R.string.app_pic);
        } else if(conversation.getMsgType() == ECMessage.Type.VIDEO.ordinal()) {
            return fromNickName + mContext.getString(R.string.app_video);
        }else if(conversation.getMsgType()==ECMessage.Type.LOCATION.ordinal()){
            return fromNickName + mContext.getString(R.string.app_location);

        }
        String snippet = fromNickName + conversation.getContent();
        if(conversation.getSessionId().equals(isAtSession)) {
            return Html.fromHtml(mContext.getString(R.string.conversation_at, snippet));
        }
        return snippet;
    }

    /**
     * 根据消息发送状态处理
     * @param context
     * @param conversation
     * @return
     */
    public static Drawable getChattingSnippentCompoundDrawables(Context context ,Conversation conversation) {
        if(conversation.getSendStatus() == ECMessage.MessageStatus.FAILED.ordinal()) {
            return DemoUtils.getDrawables(context, R.drawable.msg_state_failed);
        } else if (conversation.getSendStatus() == ECMessage.MessageStatus.SENDING.ordinal()) {
            return DemoUtils.getDrawables(context, R.drawable.msg_state_sending);
        } else {
            return null;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
        ViewHolder mViewHolder;
        if(convertView == null || convertView.getTag() == null) {
            view = View.inflate(mContext , R.layout.conversation_item, null);

            mViewHolder = new ViewHolder();
            mViewHolder.user_avatar = (ImageView) view.findViewById(R.id.avatar_iv);
            mViewHolder.prospect_iv = (ImageView) view.findViewById(R.id.avatar_prospect_iv);
            mViewHolder.nickname_tv = (EmojiconTextView) view.findViewById(R.id.nickname_tv);
            mViewHolder.tipcnt_tv = (TextView) view.findViewById(R.id.tipcnt_tv);
            mViewHolder.update_time_tv = (TextView) view.findViewById(R.id.update_time_tv);
            mViewHolder.last_msg_tv = (EmojiconTextView) view.findViewById(R.id.last_msg_tv);
            mViewHolder.image_input_text = (ImageView) view.findViewById(R.id.image_input_text);
            mViewHolder.image_mute = (ImageView) view.findViewById(R.id.image_mute);
            view.setTag(mViewHolder);
        } else {
            view = convertView;
            mViewHolder = (ViewHolder) view.getTag();
        }

        Conversation conversation = getItem(position);
        if(conversation != null) {
            if(TextUtils.isEmpty(conversation.getUsername())) {
                mViewHolder.nickname_tv.setText(conversation.getSessionId());
            } else {
                mViewHolder.nickname_tv.setText(conversation.getUsername());
            }
            handleDisplayNameTextColor(mViewHolder.nickname_tv , conversation.getSessionId());
            mViewHolder.last_msg_tv.setText(getConversationSnippet(conversation));
            mViewHolder.last_msg_tv.setCompoundDrawables(getChattingSnippentCompoundDrawables(mContext, conversation), null, null, null);
            // 未读提醒设置
            setConversationUnread(mViewHolder, conversation);
            mViewHolder.image_input_text.setVisibility(View.GONE);
            mViewHolder.update_time_tv.setText(getConversationTime(conversation));
            if(conversation.getSessionId().toUpperCase().startsWith("G")) {
                Bitmap bitmap = ContactLogic.getChatroomPhoto(conversation.getSessionId());
                if(bitmap != null) {
                    mViewHolder.user_avatar.setImageBitmap(bitmap);
                    mViewHolder.user_avatar.setPadding(padding, padding, padding, padding);
                    mViewHolder.user_avatar.setBackgroundColor(Color.parseColor("#d5d5d5"));
                } else {
                    mViewHolder.user_avatar.setImageResource(R.drawable.group_head);
                    mViewHolder.user_avatar.setPadding(0, 0, 0, 0);
                    mViewHolder.user_avatar.setBackgroundDrawable(null);
                }
            } else {
                mViewHolder.user_avatar.setBackgroundDrawable(null);
                if(conversation.getMsgType() == 1000) {
                    mViewHolder.user_avatar.setImageResource(R.drawable.ic_launcher);
                } else {
                    ECContacts contact = ContactSqlManager.getContact(conversation.getSessionId());
                    mViewHolder.user_avatar.setImageBitmap(ContactLogic.getPhoto(contact.getRemark()));
                }
            }
            mViewHolder.image_mute.setVisibility(isNotice(conversation)? View.GONE :View.VISIBLE);
        }

        boolean isTop = ConversationSqlManager.querySessionisTopBySessionId(conversation.getSessionId());
        if(isTop&&!conversation.getSessionId().equals(GroupNoticeSqlManager.CONTACT_ID)){
            view.setBackgroundColor(mContext.getResources().getColor(R.color.list_bg_gaoliang));
        }else {
            view.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.list_item_selector));

        }

        return view;
    }

    private void handleDisplayNameTextColor(EmojiconTextView textView, String contactId) {
        if(ContactLogic.isCustomService(contactId)) {
            textView.setTextColor(colorStateLists[1]);
        } else {
            textView.setTextColor(colorStateLists[0]);
        }
    }

    /**
     * 设置未读图片显示规则
     * @param mViewHolder
     * @param conversation
     */
    private void setConversationUnread(ViewHolder mViewHolder, Conversation conversation) {
        String msgCount = conversation.getUnreadCount() > 100 ? "..." : String.valueOf(conversation.getUnreadCount());
        mViewHolder.tipcnt_tv.setText(msgCount);
        if(conversation.getUnreadCount() == 0) {
            mViewHolder.tipcnt_tv.setVisibility(View.GONE);
            mViewHolder.prospect_iv.setVisibility(View.GONE);
        } else if (conversation.isNotice()) {
            mViewHolder.tipcnt_tv.setVisibility(View.VISIBLE);
            mViewHolder.prospect_iv.setVisibility(View.GONE);
        } else {
            mViewHolder.prospect_iv.setVisibility(View.VISIBLE);
            mViewHolder.tipcnt_tv.setVisibility(View.GONE);
        }
    }

    static class ViewHolder {
        ImageView user_avatar;
        TextView tipcnt_tv;
        ImageView prospect_iv;
        EmojiconTextView nickname_tv;
        TextView update_time_tv;
        EmojiconTextView last_msg_tv;
        ImageView image_input_text;
        ImageView image_mute;
    }

    @Override
    protected void initCursor() {
        notifyChange();
    }

    @Override
    protected void notifyChange() {
        if(mCallBackListener != null) {
            mCallBackListener.OnListAdapterCallBack();
        }
        Cursor cursor = ConversationSqlManager.getConversationCursor();

        setCursor(cursor);
        isAtSession = ECPreferences.getSharedPreferences().getString(ECPreferenceSettings.SETTINGS_AT.getId() , "");
        super.notifyDataSetChanged();
    }

    private boolean isNotice(Conversation conversation) {
        if(conversation.getSessionId().toLowerCase().startsWith("g")) {
            return conversation.isNotice();
        }
        return true;
    }

}
