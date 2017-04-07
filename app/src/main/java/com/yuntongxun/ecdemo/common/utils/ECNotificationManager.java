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
package com.yuntongxun.ecdemo.common.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;

import com.yuntongxun.ecdemo.R;
import com.yuntongxun.ecdemo.common.CCPAppManager;
import com.yuntongxun.ecdemo.storage.ConversationSqlManager;
import com.yuntongxun.ecdemo.storage.GroupNoticeSqlManager;
import com.yuntongxun.ecdemo.ui.LauncherActivity;
import com.yuntongxun.ecdemo.ui.voip.ECVoIPBaseActivity;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.ECVoIPCallManager;

import java.io.IOException;


/**
 * 状态栏通知
 *
 * @author Jorstin Chan@容联•云通讯
 * @version 4.0
 * @date 2015-1-4
 */
public class ECNotificationManager {

    public static final int CCP_NOTIFICATOIN_ID_CALLING = 0x1;

    public static final int NOTIFY_ID_PUSHCONTENT = 35;

    private Context mContext;

    private static NotificationManager mNotificationManager;

    public static ECNotificationManager mInstance;

    public static ECNotificationManager getInstance() {
        if (mInstance == null) {
            mInstance = new ECNotificationManager(CCPAppManager.getContext());
        }

        return mInstance;
    }

    MediaPlayer mediaPlayer = null;

    public void playNotificationMusic(String voicePath) throws IOException {
        //paly music ...
        AssetFileDescriptor fileDescriptor = mContext.getAssets().openFd(voicePath);
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
        mediaPlayer.prepare();
        mediaPlayer.setLooping(false);
        mediaPlayer.start();
    }


    private ECNotificationManager(Context context) {
        mContext = context;
    }

    public final void showCustomNewMessageNotification(Context context, String pushContent, String fromUserName, String sessionId, int lastMsgType) {
        LogUtil.w(LogUtil.getLogUtilsTag(ECNotificationManager.class),
                "showCustomNewMessageNotification pushContent： " + pushContent
                        + ", fromUserName: " + fromUserName + " ,sessionId: " + sessionId + " ,msgType: " + lastMsgType);

        Intent intent = new Intent(mContext, LauncherActivity.class);
        intent.putExtra("nofification_type", "pushcontent_notification");
        intent.putExtra("Intro_Is_Muti_Talker", true);
        intent.putExtra("Main_FromUserName", fromUserName);
        intent.putExtra("Main_Session", sessionId);
        intent.putExtra("MainUI_User_Last_Msg_Type", lastMsgType);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 35, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String tickerText = getTickerText(mContext, fromUserName, lastMsgType);
        int sessionUnreadCount = ConversationSqlManager.getInstance().qureySessionUnreadCount();
        int allSessionUnreadCount = ConversationSqlManager.getInstance().qureyAllSessionUnreadCount();
        String contentTitle = getContentTitle(context, sessionUnreadCount, fromUserName);
        String contentText = getContentText(context, sessionUnreadCount, allSessionUnreadCount, pushContent, lastMsgType);

        boolean shake = ECPreferences.getSharedPreferences().getBoolean(ECPreferenceSettings.SETTINGS_NEW_MSG_SHAKE.getId(), true);
        boolean sound = ECPreferences.getSharedPreferences().getBoolean(ECPreferenceSettings.SETTINGS_NEW_MSG_SOUND.getId(), true);
        int defaults;
        if ((sound && shake)) {
            defaults = Notification.DEFAULT_ALL;
            shake = false;
        } else if ((sound && !shake)) {
            defaults = Notification.DEFAULT_SOUND;
            shake = false;
        } else if (!sound && shake) {
            defaults = Notification.DEFAULT_VIBRATE;
            shake = true;
        } else if (!sound && !shake) {
            defaults = Notification.DEFAULT_LIGHTS;
            shake = true;
        } else {
            defaults = Notification.DEFAULT_ALL;
            shake = false;
        }

        Notification notification = NotificationUtil.buildNotification(context, R.drawable.title_bar_logo, defaults, shake, tickerText, contentTitle, contentText, null, pendingIntent);
        notification.flags = (Notification.FLAG_AUTO_CANCEL | notification.flags);
        ((NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFY_ID_PUSHCONTENT, notification);
    }


    /**
     * @param contex
     * @param fromUserName
     * @param msgType
     * @return
     */
    public final String getTickerText(Context contex, String fromUserName, int msgType) {
        if (msgType == ECMessage.Type.TXT.ordinal()) {
            return contex.getResources().getString(R.string.notification_fmt_one_txttype, fromUserName);
        } else if (msgType == ECMessage.Type.IMAGE.ordinal()) {
            return contex.getResources().getString(R.string.notification_fmt_one_imgtype, fromUserName);
        } else if (msgType == ECMessage.Type.VOICE.ordinal()) {
            return contex.getResources().getString(R.string.notification_fmt_one_voicetype, fromUserName);
        } else if (msgType == ECMessage.Type.FILE.ordinal()) {
            return contex.getResources().getString(R.string.notification_fmt_one_filetype, fromUserName);
        } else if (msgType == GroupNoticeSqlManager.NOTICE_MSG_TYPE) {
            return contex.getResources().getString(R.string.str_system_message_group_notice);
        } else {
            //return contex.getResources().getString(R.string.app_name);
            return contex.getPackageManager().getApplicationLabel(contex.getApplicationInfo()).toString();
        }

    }

    public final String getContentTitle(Context context, int sessionUnreadCount, String fromUserName) {
        if (sessionUnreadCount > 1) {
            return context.getString(R.string.app_name);
        }

        return fromUserName;
    }

    /**
     * @param context
     * @return
     */
    public final String getContentText(Context context, int sessionCount, int sessionUnread, String pushContent, int lastMsgType) {

        if (sessionCount > 1) {

            return context.getResources().getQuantityString(
                    R.plurals.notification_fmt_multi_msg_and_talker, 1,
                    sessionCount, sessionUnread);
        }

        if (sessionUnread > 1) {
            return context.getResources().getQuantityString(
                    R.plurals.notification_fmt_multi_msg_and_one_talker, sessionUnread, sessionUnread);
        }

        if (lastMsgType == ECMessage.Type.TXT.ordinal()) {
            return pushContent;
        } else if (lastMsgType == ECMessage.Type.FILE.ordinal()) {
            return context.getResources().getString(R.string.app_file);
        } else if (lastMsgType == ECMessage.Type.VOICE.ordinal()) {
            return context.getResources().getString(R.string.app_voice);
        } else if (lastMsgType == ECMessage.Type.IMAGE.ordinal()) {
            return context.getResources().getString(R.string.app_pic);
        } else {
            return pushContent;
        }

    }

    private void cancel() {
        NotificationManager notificationManager = (NotificationManager) CCPAppManager
                .getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            return;
        }
        notificationManager.cancel(0);
    }

    /**
     * 取消所有的状态栏通知
     */
    public final void forceCancelNotification() {
        cancel();
        NotificationManager notificationManager = (NotificationManager) CCPAppManager
                .getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            return;
        }
        notificationManager.cancel(NOTIFY_ID_PUSHCONTENT);

    }

    public final Looper getLooper() {
        return Looper.getMainLooper();
    }

    public final void showKickoffNotification(Context context, String kickofftext) {

        Intent intent = new Intent(context, LauncherActivity.class);
        intent.putExtra("nofification_type", "pushcontent_notification");
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 35, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(context)
                .setTicker(kickofftext)
                .setContentTitle(kickofftext)
                .setContentText("")
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent).build();
        ((NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFY_ID_PUSHCONTENT, notification);
    }

    /**
     * 后台呈现音视频呼叫Notification
     *
     * @param callType 呼叫类型
     */
    public static void showCallingNotification(Context context, ECVoIPCallManager.CallType callType) {
        try {
            getInstance().checkNotification();
            String topic = getInstance().mContext.getString(R.string.ec_voip_is_talking_tip);
            Intent intent;
            if (callType == ECVoIPCallManager.CallType.VIDEO) {
                intent = new Intent(ECVoIPBaseActivity.ACTION_VIDEO_CALL);
            } else {
                intent = new Intent(ECVoIPBaseActivity.ACTION_VOICE_CALL);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent contentIntent = PendingIntent.getActivity(getInstance().mContext,
                    R.string.app_name,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = new NotificationCompat.Builder(context)
                    .setTicker(topic)
                    .setContentTitle(topic)
                    .setContentText("")
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.title_bar_logo))
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(contentIntent).build();
            mNotificationManager.notify(CCP_NOTIFICATOIN_ID_CALLING, notification);
            mNotificationManager.cancel(NOTIFY_ID_PUSHCONTENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void checkNotification() {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        }

    }

    public static void cancelCCPNotification(int id) {
        getInstance().checkNotification();
        mNotificationManager.cancel(id);
    }
}
