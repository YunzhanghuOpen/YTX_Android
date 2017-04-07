package com.yuntongxun.ecdemo.ui.voip;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;

import com.yuntongxun.ecdemo.R;
import com.yuntongxun.ecdemo.common.utils.DemoUtils;
import com.yuntongxun.ecdemo.common.utils.ECNotificationManager;
import com.yuntongxun.ecdemo.common.utils.LogUtil;
import com.yuntongxun.ecdemo.ui.ECSuperActivity;
import com.yuntongxun.ecdemo.ui.SDKCoreHelper;
import com.yuntongxun.ecdemo.ui.videomeeting.ECGlobalConstants;
import com.yuntongxun.ecdemo.ui.videomeeting.MultiVideoconference;
import com.yuntongxun.ecdemo.ui.voip.ECCallHeadUILayout.OnSendDTMFDelegate;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECVoIPCallManager;
import com.yuntongxun.ecsdk.VideoRatio;
import com.yuntongxun.ecsdk.platformtools.ECHandlerHelper;

/**
 * com.yuntongxun.ecdemo.ui.voip in ECDemo_Android
 * Created by Jorstin on 2015/7/3.
 */
public abstract class ECVoIPBaseActivity extends ECSuperActivity
        implements VoIPCallHelper.OnCallEventNotifyListener , ECCallControlUILayout.OnCallControlDelegate , OnSendDTMFDelegate {

    private static final String TAG = "ECSDK_Demo.ECVoIPBaseActivity";

    /**昵称*/
    public static final String EXTRA_CALL_NAME = "con.yuntongxun.ecdemo.VoIP_CALL_NAME";
    /**通话号码*/
    public static final String EXTRA_CALL_NUMBER = "con.yuntongxun.ecdemo.VoIP_CALL_NUMBER";
    /**呼入方或者呼出方*/
    public static final String EXTRA_OUTGOING_CALL = "con.yuntongxun.ecdemo.VoIP_OUTGOING_CALL";
    /**VoIP呼叫*/
    public static final String ACTION_VOICE_CALL = "con.yuntongxun.ecdemo.intent.ACTION_VOICE_CALL";
    /**Video呼叫*/
    public static final String ACTION_VIDEO_CALL = "con.yuntongxun.ecdemo.intent.ACTION_VIDEO_CALL";
    public static final String ACTION_CALLBACK_CALL = "con.yuntongxun.ecdemo.intent.ACTION_VIDEO_CALLBACK";

    /**通话昵称*/
    protected String mCallName;
    /**通话号码*/
    protected String mCallNumber;
    protected String mPhoneNumber;
    protected String mMeetingNo;
    protected int mMeetingType;
    boolean isConnect = false;
    /**是否来电*/
    protected boolean mIncomingCall = false;
    /**呼叫唯一标识号*/
    protected String mCallId;
    /**VoIP呼叫类型（音视频）*/
    protected ECVoIPCallManager.CallType mCallType;
    /**透传号码参数*/
    private static final String KEY_TEL = "tel";
    /**透传名称参数*/
    private static final String KEY_NAME = "nickname";
    private static final String KEY_CONFIG = "confid";
    private static final String KEY_CONFIG_TYPE = "conftype";
    private static final String KEY_CONFIG_SUD = "sud";
    protected ECCallHeadUILayout mCallHeaderView;
    protected ECCallControlUILayout mCallControlUIView;
    public AudioManager mAudioManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAudioManager = ((AudioManager) getSystemService(Context.AUDIO_SERVICE));
        if(init()) {
            return ;
        }

        if(mCallType == null) {
            mCallType = ECVoIPCallManager.CallType.VOICE;
        }

        getTopBarView().setVisibility(View.GONE);
        initProwerManager();
    }
    
    private Intent sIntent;
    
    

    private boolean init() {
    	if(getIntent()==null){
    		return true;
    	}
    	sIntent=getIntent();

        mIncomingCall = !(getIntent().getBooleanExtra(EXTRA_OUTGOING_CALL, false));
        mCallType = (ECVoIPCallManager.CallType) getIntent().getSerializableExtra(ECDevice.CALLTYPE);

        if(mIncomingCall) {
            // 透传信息
            String[] infos = getIntent().getExtras().getStringArray(ECDevice.REMOTE);
            if (infos != null && infos.length > 0) {
                for (String str : infos) {
                    if (str.startsWith(KEY_TEL)) {
                        mPhoneNumber = DemoUtils.getLastwords(str, "=");
                    } else if (str.startsWith(KEY_NAME)) {
                        mCallName = DemoUtils.getLastwords(str, "=");

                        // 如果有以下两个值说明是会议邀请来电
                    } else if(str.startsWith(KEY_CONFIG)) {
                        mMeetingNo = DemoUtils.getLastwords(str, "=");
                    } else if(str.startsWith(KEY_CONFIG_TYPE)) {
                        mMeetingType = Integer.parseInt(DemoUtils.getLastwords(str, "="));
                    }else if (str.startsWith(KEY_CONFIG_SUD)){
                        LogUtil.d(TAG,"get invitemeeting sud = "+DemoUtils.getLastwords(str,"="));
                    }
                }
            }
        }

        if(mMeetingNo != null && mMeetingType == 2) {
            Intent intent = new Intent(ECVoIPBaseActivity.this , MultiVideoconference.class);
            intent.putExtra(ECGlobalConstants.CONFERENCE_ID, mMeetingNo);
            intent.putExtra("com.voice.demo.ccp.VIDEO_CREATE", "");
            intent.putExtra("com.voice.demo.ccp.VIDEO_CALL_INVITE", getIntent().getStringExtra(ECDevice.CALLID));
            intent.putExtra(ECGlobalConstants.CHATROOM_NAME, mMeetingNo);
            startActivity(intent);
            super.finish();
            return true;
        }

        if(!VoIPCallHelper.mHandlerVideoCall && mCallType == ECVoIPCallManager.CallType.VIDEO) {
            VoIPCallHelper.mHandlerVideoCall = true;
            Intent mVideoIntent = new Intent(this , VideoActivity.class);
            mVideoIntent.putExtras(getIntent().getExtras());
            mVideoIntent.putExtra(VoIPCallActivity.EXTRA_OUTGOING_CALL , false);
            startActivity(mVideoIntent);
            super.finish();
            return true;
        }
        return false;

    }

    /**
     * 收到的VoIP通话事件通知是否与当前通话界面相符
     * @return 是否正在进行的VoIP通话
     */
    protected boolean isEqualsCall(String callId) {
        return (!TextUtils.isEmpty(callId) && callId.equals(mCallId));
    }

    /**
     * 是否需要做界面更新
     * @param callId
     * @return
     */
    protected boolean needNotify(String callId) {
        return !(isFinishing() || !isEqualsCall(callId));
    }

    @Override
    protected void onResume() {
        super.onResume();
        enterIncallMode();
        VoIPCallHelper.setOnCallEventNotifyListener(this);
        ECNotificationManager.cancelCCPNotification(ECNotificationManager.CCP_NOTIFICATOIN_ID_CALLING);
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseWakeLock();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(VoIPCallHelper.isHoldingCall()) {
            ECNotificationManager.showCallingNotification(this,mCallType);
        }
    }


    @Override
    protected void onDestroy() {
    	super.onDestroy();
    }

    @Override
    public void onViewAccept(ECCallControlUILayout controlPanelView, ImageButton view) {
        if(controlPanelView != null) {///
            controlPanelView.setControlEnable(false);
        }
        VoIPCallHelper.acceptCall(mCallId);
        mCallControlUIView.setCallDirect(ECCallControlUILayout.CallLayout.INCALL);
        mCallHeaderView.setCallTextMsg(R.string.ec_voip_calling_accepting);
        
    }

    @Override
    public void onViewRelease(ECCallControlUILayout controlPanelView, ImageButton view) {
        if(controlPanelView != null) {
            controlPanelView.setControlEnable(false);
        }
        VoIPCallHelper.releaseCall(mCallId);
    }

    @Override
    public void onViewReject(ECCallControlUILayout controlPanelView, ImageButton view) {
        if(controlPanelView != null) {
            controlPanelView.setControlEnable(false);
        }
        VoIPCallHelper.rejectCall(mCallId);
    }

    @Override
    public void onVideoRatioChanged(VideoRatio videoRatio) {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.e("setintent");
        ECHandlerHelper.removeCallbacksRunnOnUI(OnCallFinish);
        setIntent(intent);
//        setIntent(sIntent);
        if(init()) {
            return ;
        }

        if(mCallType == null) {
            mCallType = ECVoIPCallManager.CallType.VOICE;
        }
    }

    @Override
    public void finish() {
            ECHandlerHelper.postDelayedRunnOnUI(OnCallFinish , 3000);
    }
    public void hfFinish() {
    	ECHandlerHelper.postDelayedRunnOnUI(OnCallFinish , 0);
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 获取音频类型
        int streamType = ECDevice.getECVoIPSetupManager().getStreamType();
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            // 调小音量
            adjustStreamVolumeDown(streamType);
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            // 调大音量
            adjustStreamVolumeUo(streamType);
            return true;
        }

		return super.onKeyDown(keyCode, event);
	}
    /**
     * 向下 调整音量
     * @param streamType 类型
     */
    public final void adjustStreamVolumeDown(int streamType) {
        if (this.mAudioManager != null)
            this.mAudioManager.adjustStreamVolume(streamType,AudioManager.ADJUST_LOWER,
                    AudioManager.FX_FOCUS_NAVIGATION_UP);
    }

    /**
     * 向上 调整音量
     * @param streamType 类型
     */
    public final void adjustStreamVolumeUo(int streamType) {
        if (this.mAudioManager != null)
            this.mAudioManager.adjustStreamVolume(streamType,AudioManager.ADJUST_RAISE,
                    AudioManager.FX_FOCUS_NAVIGATION_UP);
    }

    /**
     * 延时关闭界面
     */
    final Runnable OnCallFinish = new Runnable() {
        public void run() {
            ECVoIPBaseActivity.super.finish();
        }
    };
    
    @Override
	public void sendDTMF(char c) {

		SDKCoreHelper.getVoIPCallManager().sendDTMF(mCallId, c);
	}
}
