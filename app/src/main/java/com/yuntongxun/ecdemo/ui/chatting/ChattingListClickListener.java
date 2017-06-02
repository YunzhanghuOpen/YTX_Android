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
package com.yuntongxun.ecdemo.ui.chatting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.yuntongxun.ecdemo.common.CCPAppManager;
import com.yuntongxun.ecdemo.common.utils.CheckUtil;
import com.yuntongxun.ecdemo.common.utils.FileAccessor;
import com.yuntongxun.ecdemo.common.utils.MediaPlayTools;
import com.yuntongxun.ecdemo.storage.IMessageSqlManager;
import com.yuntongxun.ecdemo.storage.ImgInfoSqlManager;
import com.yuntongxun.ecdemo.ui.chatting.model.ViewHolderTag;
import com.yuntongxun.ecdemo.ui.chatting.redpacketutils.RedPacketUtil;
import com.yuntongxun.ecdemo.ui.settings.WebAboutActivity;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.ECMessage.Direction;
import com.yuntongxun.ecsdk.ECMessage.Type;
import com.yuntongxun.ecsdk.im.ECFileMessageBody;
import com.yuntongxun.ecsdk.im.ECPreviewMessageBody;
import com.yuntongxun.ecsdk.im.ECTextMessageBody;
import com.yuntongxun.ecsdk.im.ECVideoMessageBody;
import com.yuntongxun.ecsdk.im.ECVoiceMessageBody;
import com.yunzhanghu.redpacketsdk.bean.RedPacketInfo;
import com.yunzhanghu.redpacketsdk.constant.RPConstant;
import com.yunzhanghu.redpacketui.utils.RPRedPacketUtil;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 处理聊天消息点击事件响应
 *
 * @author Jorstin Chan@容联•云通讯
 * @version 4.0
 * @date 2014-12-10
 */
public class ChattingListClickListener implements View.OnClickListener {

    /**
     * 聊天界面
     */
    private ChattingActivity mContext;

    public ChattingListClickListener(ChattingActivity activity, String userName) {
        mContext = activity;
    }

    @Override
    public void onClick(View v) {
        ViewHolderTag holder = (ViewHolderTag) v.getTag();
        ECMessage iMessage = holder.detail;

        switch (holder.type) {
            case ViewHolderTag.TagType.TAG_VIEW_FILE:
                if (iMessage.getType() == Type.VIDEO) {
                    ECVideoMessageBody videoBody = (ECVideoMessageBody) iMessage.getBody();
                    File file = new File(FileAccessor.getFilePathName(), videoBody.getFileName());

                    if (file.exists()) {
                        if (iMessage.getDirection() == Direction.RECEIVE && CCPAppManager.getClientUser().getUserId().equals(iMessage.getForm())) {
                            CCPAppManager.doViewFilePrevieIntent(mContext, file.getAbsolutePath());
                        } else {
                            CCPAppManager.doViewFilePrevieIntent(mContext, videoBody.getLocalUrl());
                        }

                    } else {
                        mContext.mChattingFragment.showProcessDialog();
                        videoBody.setLocalUrl(new File(FileAccessor.getFilePathName(), videoBody.getFileName()).getAbsolutePath());
                        ECDevice.getECChatManager().downloadMediaMessage(iMessage, IMChattingHelper.getInstance());
                    }
                    return;
                }
                ECFileMessageBody body = (ECFileMessageBody) holder.detail.getBody();
                CCPAppManager.doViewFilePrevieIntent(mContext, body.getLocalUrl());
                break;

            case ViewHolderTag.TagType.TAG_VOICE:
                if (iMessage == null) {
                    return;
                }
                MediaPlayTools instance = MediaPlayTools.getInstance();
                final ChattingListAdapter2 adapterForce = mContext.mChattingFragment.getChattingAdapter();
                if (instance.isPlaying()) {
                    instance.stop();
                }
                if (adapterForce.mVoicePosition == holder.position) {
                    adapterForce.mVoicePosition = -1;
                    adapterForce.notifyDataSetChanged();
                    return;
                }

                instance.setOnVoicePlayCompletionListener(new MediaPlayTools.OnVoicePlayCompletionListener() {

                    @Override
                    public void OnVoicePlayCompletion() {
                        adapterForce.mVoicePosition = -1;
                        adapterForce.notifyDataSetChanged();
                    }
                });
                ECVoiceMessageBody voiceBody = (ECVoiceMessageBody) holder.detail.getBody();
                String fileLocalPath = voiceBody.getLocalUrl();
                instance.playVoice(fileLocalPath, false);
                adapterForce.setVoicePosition(holder.position);
                adapterForce.notifyDataSetChanged();

                break;

            case ViewHolderTag.TagType.TAG_VIEW_PICTURE:
                if (iMessage != null) {
                    List<String> msgids = IMessageSqlManager.getImageMessageIdSession(mContext.mChattingFragment.getmThread());
                    if (msgids == null || msgids.isEmpty()) {
                        return;
                    }
                    int position = 0;
                    ArrayList<ViewImageInfo> urls = (ArrayList<ViewImageInfo>) ImgInfoSqlManager.getInstance().getViewImageInfos(msgids);
                    if (urls == null || urls.isEmpty()) {
                        return;
                    }
                    for (int i = 0; i < urls.size(); i++) {
                        if (urls.get(i) != null && urls.get(i).getMsgLocalId().equals(iMessage.getMsgId())) {
                            position = i;
                            break;
                        }
                    }
                    String picMsgId = msgids.get(position);
                    CCPAppManager.startChattingImageViewAction(mContext, position, urls, picMsgId);
                    ImageGralleryPagerActivity.isFireMsg = IMessageSqlManager.isFireMsg(iMessage.getMsgId());
                    msgids.clear();
                }
                break;

            case ViewHolderTag.TagType.TAG_RESEND_MSG:

                mContext.mChattingFragment.doResendMsgRetryTips(iMessage, holder.position);
                break;
            case ViewHolderTag.TagType.TAG_IM_LOCATION:

                CCPAppManager.startShowBaiDuMapAction(mContext, iMessage);
                break;

            case ViewHolderTag.TagType.TAG_IM_RICH_TEXT:
                doClickRichTextAction(iMessage);
                break;

            case ViewHolderTag.TagType.TAG_IM_TEXT:

                ECTextMessageBody textBody = (ECTextMessageBody) iMessage.getBody();
                String content = textBody.getMessage();

                if (TextUtils.isEmpty(content)) {
                    return;
                }
                content = content.trim();
                if (content.startsWith("www.") || content.startsWith("http://") || content.startsWith("https://")) {
                    startWebActivity(content);
                }

                break;
            case ViewHolderTag.TagType.TAG_IM_REDPACKET:

                JSONObject jsonRedPacket = RedPacketUtil.getInstance().isRedPacketMessage(iMessage);
                final ProgressDialog progressDialog = new ProgressDialog(mContext);
                progressDialog.setCanceledOnTouchOutside(false);
                RedPacketInfo redPacketInfo = new RedPacketInfo();
                redPacketInfo.redPacketId = jsonRedPacket.optString(RPConstant.MESSAGE_ATTR_RED_PACKET_ID);
                redPacketInfo.redPacketType = jsonRedPacket.optString(RPConstant.MESSAGE_ATTR_RED_PACKET_TYPE);
                if (iMessage.getDirection() == Direction.RECEIVE) {
                    redPacketInfo.messageDirect = RPConstant.MESSAGE_DIRECT_RECEIVE;
                } else {
                    redPacketInfo.messageDirect = RPConstant.MESSAGE_DIRECT_SEND;
                }
                if (mContext.mChattingFragment.isPeerChat()) {//群聊
                    redPacketInfo.chatType = RPConstant.CHAT_TYPE_GROUP;
                } else {//单聊
                    redPacketInfo.chatType = RPConstant.CHAT_TYPE_SINGLE;
                }
                RPRedPacketUtil.getInstance().openRedPacket(redPacketInfo.redPacketId,redPacketInfo.redPacketType, mContext, new RPRedPacketUtil.RPOpenPacketCallback() {

                    @Override
                    public void onSuccess(RedPacketInfo redPacketInfo) {
                        mContext.mChattingFragment.sendRedPacketAckMessage(redPacketInfo.senderId,redPacketInfo.senderNickname);
                    }

                    @Override
                    public void showLoading() {
                        progressDialog.show();
                    }

                    @Override
                    public void hideLoading() {
                        progressDialog.hide();
                    }

                    @Override
                    public void onError(String code, String message) {
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                    }
                });

                break;
            default:
                break;
        }
    }


    private void doClickRichTextAction(ECMessage iMessage) {

        ECPreviewMessageBody body = (ECPreviewMessageBody) iMessage.getBody();
        String url = body.getUrl();
        if (!CheckUtil.isVailUrl(url)) {
            return;
        }
        startWebActivity(url);
    }


    private void startWebActivity(String url) {

        Intent intent = new Intent(mContext, WebAboutActivity.class);
        intent.putExtra("url", url);
        mContext.startActivity(intent);

    }


}
