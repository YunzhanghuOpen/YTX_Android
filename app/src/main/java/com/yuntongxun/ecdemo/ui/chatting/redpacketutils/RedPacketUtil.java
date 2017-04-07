package com.yuntongxun.ecdemo.ui.chatting.redpacketutils;

import android.text.TextUtils;
import android.util.Log;

import com.yuntongxun.ecsdk.ECMessage;
import com.yunzhanghu.redpacketsdk.constant.RPConstant;

import org.json.JSONException;
import org.json.JSONObject;


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
            String receiverId = jsonObject.optString(RPConstant.EXTRA_RED_PACKET_RECEIVER_ID);//红包接收者id
            String senderId = jsonObject.optString(RPConstant.EXTRA_RED_PACKET_SENDER_ID);//红包发送者id
            //发送者和领取者都不是自己
            if (!currentUserId.equals(receiverId) && !currentUserId.equals(senderId)) {
                isMyselfAckMsg = false;
            }
        }
        return isMyselfAckMsg;
    }



//    public static JSONObject isRedPacketMessage(ECMessage message) {
//        JSONObject rpJSON = null;
//        if (message.getType() == ECMessage.Type.TXT) {
//            // 设置内容
//            String extraData = message.getUserData();
//            if (extraData != null) {
//                try {
//                    JSONObject jsonObject = JSONObject.parseObject(extraData);
//                    if (jsonObject != null && jsonObject.containsKey(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE)
//                            && jsonObject.getBoolean(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE)) {
//                        rpJSON = jsonObject;
//                    }
//                } catch (JSONException e) {
//                    Log.e("JSONException", e.toString());
//                }
//            }
//        }
//        return rpJSON;
//    }
//
//    public static JSONObject isRedPacketAckMessage(ECMessage message) {
//        JSONObject jsonRedPacketAck = null;
//        if (message.getType() == ECMessage.Type.TXT) {
//            // 设置内容
//            String extraData = message.getUserData();
//            if (extraData != null) {
//                try {
//                    JSONObject jsonObject = JSONObject.parseObject(extraData);
//                    if (jsonObject != null && jsonObject.containsKey(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE) && jsonObject.getBoolean(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE)) {
//                        jsonRedPacketAck = jsonObject;
//                    }
//                } catch (JSONException e) {
//                    Log.e("JSONException", e.toString());
//                }
//            }
//        }
//        return jsonRedPacketAck;
//    }
//








//    public static boolean isRedAckMessage(ECMessage message) {
//        boolean is =false;
//        if (message.getType() == ECMessage.Type.TXT) {
//            // 设置内容
//            String extraData = message.getUserData();
//            if (extraData != null) {
//                try {
//                    JSONObject jsonObject = JSONObject.parseObject(extraData);
//                    if (jsonObject != null && jsonObject.containsKey(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE) && jsonObject.getBoolean(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE)) {
//                        is=true;
//                        return is;
//                    }
//                } catch (JSONException e) {
//
//                }
//            }
//        }
//        return is;
//    }
//    public boolean isMyAckMessage(ECMessage message, String currentUserId) {
//        boolean isMyselfAckMsg = true;
//        JSONObject jsonObject = isRedPacketAckMessage(message);
//        if (jsonObject != null) {
//            String receiverId = jsonObject.optString(RPConstant.EXTRA_RED_PACKET_RECEIVER_ID);//红包接收者id
//            String senderId = jsonObject.optString(RPConstant.EXTRA_RED_PACKET_SENDER_ID);//红包发送者id
//            //发送者和领取者都不是自己
//            if (!currentUserId.equals(receiverId) && !currentUserId.equals(senderId)) {
//                isMyselfAckMsg = false;
//            }
//        }
//        return isMyselfAckMsg;
//    }
    //是不是回执消息，不是发送者也不是接收者
//    public static boolean isRedAckMessage2(ECMessage message) {
//        boolean is =false;
//        if (message.getType() == ECMessage.Type.TXT) {
//            // 设置内容
//            String extraData = message.getUserData();
//            if (extraData != null) {
//                try {
//                    JSONObject jsonObject = JSONObject.parseObject(extraData);
//                    if (jsonObject != null && jsonObject.containsKey(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE) && jsonObject.getBoolean(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE)) {
//                           String a = jsonObject.getString(RedPacketConstant.EXTRA_RED_PACKET_SENDER_ID);
//                           String b = jsonObject.getString(RedPacketConstant.EXTRA_RED_PACKET_RECEIVER_ID);
//                        if(!CCPAppManager.getClientUser().getUserId().equalsIgnoreCase(a)&&!CCPAppManager.getClientUser().getUserId().equalsIgnoreCase(b)){
//                            is =true;
//                        }
//                        return  is;
//
//                    }
//                } catch (JSONException e) {
//
//                }
//            }
//        }
//        return is;
//    }
////    是不是红包发送者
//    public static boolean isRedPacketAckOtherMessage(ECMessage message) {
//        boolean isToSelf =false;
//        String  userId ="";
//        JSONObject jsonRedPacketAck = null;
//        if (message.getType() == ECMessage.Type.TXT) {
//            // 设置内容
//            String extraData = message.getUserData();
//            if (extraData != null) {
//                try {
//                    JSONObject jsonObject = JSONObject.parseObject(extraData);
//                    if (jsonObject != null && jsonObject.containsKey(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE) && jsonObject.getBoolean(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE)) {
//                        jsonRedPacketAck = jsonObject;
//                    }
//                    if(jsonRedPacketAck!=null&&jsonRedPacketAck.containsKey(RedPacketConstant.EXTRA_RED_PACKET_SENDER_ID)){
//                        userId =jsonRedPacketAck.getString(RedPacketConstant.EXTRA_RED_PACKET_SENDER_ID);
//                    }
//                    if(CCPAppManager.getClientUser().getUserId().equalsIgnoreCase(userId)){
//                        isToSelf =true;
//                    }
//                } catch (JSONException e) {
//                    return  false;
//                }
//            }
//        }
//        return isToSelf;
//    }
//    //是不是 群消息发送者和  自己领取自己的红包
//    public static boolean isGroupAckMessage(ECMessage message) {
//        boolean isMyselfAckMsg = false;
//        JSONObject jsonObject = isRedPacketAckMessage(message);
//        if (jsonObject != null) {
//            String receiverId = jsonObject.getString(RedPacketConstant.EXTRA_RED_PACKET_RECEIVER_ID);//红包接收者id
//            String senderId = jsonObject.getString(RedPacketConstant.EXTRA_RED_PACKET_SENDER_ID);//红包发送者id
//            //发送者和领取者都不是自己
//            if (isPeerChat(message)&&receiverId.equalsIgnoreCase(senderId)&&receiverId.equalsIgnoreCase(CCPAppManager.getClientUser().getUserId())) {
//                isMyselfAckMsg = true;
//            }
//        }
//        return isMyselfAckMsg;
//    }
//
//    public static boolean isPeerChat(ECMessage msg) {
//        return msg != null && msg.getSessionId().toLowerCase().startsWith("g");
//    }


}
