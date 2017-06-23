package com.yuntongxun.ecdemo.ui.chatting.redpacketutils;

import android.text.TextUtils;
import android.util.Log;

import com.yuntongxun.ecdemo.common.CCPAppManager;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECGroupManager;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.SdkErrorCode;
import com.yuntongxun.ecsdk.im.ECGroupMember;
import com.yunzhanghu.redpacketsdk.RPGroupMemberListener;
import com.yunzhanghu.redpacketsdk.RPValueCallback;
import com.yunzhanghu.redpacketsdk.RedPacket;
import com.yunzhanghu.redpacketsdk.bean.RPUserBean;
import com.yunzhanghu.redpacketsdk.constant.RPConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class RedPacketUtil {
    private static RedPacketUtil mRedPacketUtil;

    public static RedPacketUtil getInstance() {
        if (mRedPacketUtil == null) {
            synchronized (RedPacketUtil.class) {
                if (mRedPacketUtil == null) {
                    mRedPacketUtil = new RedPacketUtil();
                }

            }
        }
        return mRedPacketUtil;
    }

    /**
     * 设置专属红包
     * @param groupId
     */
    public void setGroupMember(String groupId) {
        ECGroupManager groupManager = ECDevice.getECGroupManager();
        // 调用获取群组成员接口，设置结果回调
        groupManager.queryGroupMembers(groupId,
                new ECGroupManager.OnQueryGroupMembersListener() {
                    @Override
                    public void onQueryGroupMembersComplete(ECError error, final List members) {
                        if (error.errorCode == SdkErrorCode.REQUEST_SUCCESS && members != null) {
                            // 获取群组成员成功
                            // 将群组成员信息更新到本地缓存中（sqlite） 通知UI更新
                            //如果不需要专属红包可以去掉
                            RedPacket.getInstance().setRPGroupMemberListener(new RPGroupMemberListener() {
                                @Override
                                public void getGroupMember(String s, RPValueCallback<List<RPUserBean>> rpValueCallback) {
                                    List<RPUserBean> userBeanList = new ArrayList<>();
                                    for (int i = 0; i < members.size(); i++) {
                                        RPUserBean userBean = new RPUserBean();
                                        ECGroupMember member = (ECGroupMember) members.get(i);
                                        userBean.userId = member.getVoipAccount();
                                        if (userBean.userId.equals(CCPAppManager.getUserId())) {
                                            continue;
                                        }

                                        if (member != null) {
                                            userBean.userAvatar = "none";
                                            userBean.userNickname = TextUtils.isEmpty(member.getDisplayName()) ? member.getVoipAccount() : member.getDisplayName();
                                        } else {
                                            userBean.userNickname = userBean.userId;
                                            userBean.userAvatar = "none";
                                        }
                                        userBeanList.add(userBean);
                                    }
                                    rpValueCallback.onSuccess(userBeanList);
                                }
                            });
                            return;
                        }
                        // 群组成员获取失败
                        Log.e("ECSDK_Demo", "sync group detail fail " + ", errorCode=" + error.errorCode);

                    }

                }
        );
    }

    /**
     * 是否红包消息
     *
     * @param message
     * @return
     */
    public JSONObject isRedPacketMessage(ECMessage message) {
        JSONObject rpJSON = null;
        if (message.getType() == ECMessage.Type.TXT) {
            // 设置内容
            String extraData = message.getUserData();
            if (!TextUtils.isEmpty(extraData)) {
                try {
                    JSONObject jsonObject = new JSONObject(extraData);
                    if (jsonObject.has(RPConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE) && jsonObject.getBoolean(RPConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE)) {
                        rpJSON = jsonObject;
                    }
                } catch (JSONException e) {
                    Log.e("JSONException", e.toString());
                }
            }
        }
        return rpJSON;
    }

    /**
     * 是否回执消息
     *
     * @param message
     * @return
     */
    public JSONObject isRedPacketAckMessage(ECMessage message) {
        JSONObject jsonRedPacketAck = null;
        if (message.getType() == ECMessage.Type.TXT) {
            // 设置内容
            String extraData = message.getUserData();
            if (!TextUtils.isEmpty(extraData)) {
                try {
                    JSONObject jsonObject = new JSONObject(extraData);
                    if (jsonObject.has(RPConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE)
                            && jsonObject.optBoolean(RPConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE)) {
                        jsonRedPacketAck = jsonObject;
                    }
                } catch (JSONException e) {
                    Log.e("JSONException", e.toString());
                }
            }
        }
        return jsonRedPacketAck;
    }


    /**
     * 是否是自己的回执消息消息
     *
     * @param message
     * @return
     */
    public boolean isMyAckMessage(ECMessage message, String currentUserId) {
        boolean isMyselfAckMsg = true;
        JSONObject jsonObject = isRedPacketAckMessage(message);
        if (jsonObject != null) {
            String receiverId = jsonObject.optString(RPConstant.MESSAGE_ATTR_RED_PACKET_RECEIVER_ID);//红包接收者id
            String senderId = jsonObject.optString(RPConstant.MESSAGE_ATTR_RED_PACKET_SENDER_ID);//红包发送者id
            //发送者和领取者都不是自己
            if (!currentUserId.equals(receiverId) && !currentUserId.equals(senderId)) {
                isMyselfAckMsg = false;
            }
        }
        return isMyselfAckMsg;
    }
    /**
     * 是否转账消息
     *
     * @param message
     * @return
     */
    public JSONObject isTransferMsg(ECMessage message) {
        JSONObject jsonTransfer = null;
        if (message.getType() == ECMessage.Type.TXT) {
            // 设置内容
            String extraData = message.getUserData();
            if (!TextUtils.isEmpty(extraData)) {
                try {
                    JSONObject jsonObject = new JSONObject(extraData);
                    if (jsonObject.has(RPConstant.MESSAGE_ATTR_IS_TRANSFER_PACKET_MESSAGE)
                            && jsonObject.getBoolean(RPConstant.MESSAGE_ATTR_IS_TRANSFER_PACKET_MESSAGE)) {
                        jsonTransfer = jsonObject;
                    }
                } catch (JSONException e) {
                    Log.e("JSONException", e.toString());
                }
            }
        }
        return jsonTransfer;
    }
}
