package com.yuntongxun.ecdemo.ui.chatting;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yuntongxun.ecdemo.R;
import com.yuntongxun.ecdemo.common.CCPAppManager;
import com.yuntongxun.ecdemo.common.utils.NetUtils;
import com.yuntongxun.ecdemo.common.utils.ToastUtil;
import com.yuntongxun.ecdemo.common.view.XListView;
import com.yuntongxun.ecdemo.storage.IMessageSqlManager;
import com.yuntongxun.ecdemo.ui.ECSuperActivity;
import com.yuntongxun.ecdemo.ui.group.SearchGroupActivity;
import com.yuntongxun.ecsdk.ECChatManager;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.ECReadMessageMember;
import com.yuntongxun.ecsdk.SdkErrorCode;
import com.yuntongxun.ecsdk.im.ECGroup;
import com.yuntongxun.ecsdk.meeting.ECMeetingMember;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luhuashan on 16/6/18.
 */
public class ECMessageFeedUI extends ECSuperActivity implements View.OnClickListener,XListView.IXListViewListener, NetUtils.ResultCallBack {
    @Override
    protected int getLayoutId() {
        return R.layout.message_feed;
    }

    private XListView lv;

    public static ECMessage message;

    private FeedBackAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        type = getIntent().getIntExtra("type",1);
        if(type==1){
            getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt,
                    -1, null,
                    null,
                    "已读列表", null, this);

        }else if(type==2){
            getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt,
                    -1, null,
                    null,
                    "未读列表", null, this);

        }
        lv =(XListView) findViewById(R.id.feed_list);
        lv.setPullLoadEnable(true);
        lv.setXListViewListener(this);
        adapter =new FeedBackAdapter(this);
        lv.setAdapter(adapter);
        lv.setPullRefreshEnable(false);

        addData(index);
    }
    private  int type;


    private StringEntity buildBody(String msgId,int index,int type)throws Exception{
        JSONObject object = new JSONObject();
        object.put("appId",CCPAppManager.getClientUser().getAppKey());
        object.put("msgId",msgId);
        object.put("pageSize",50);
        object.put("userName",CCPAppManager.getClientUser().getUserId());
        object.put("pageNo",index);
        object.put("type",type);
        Log.e("aaaaaaaa",object.toString());
        StringEntity s =new StringEntity(object.toString(),"UTF-8");
        return  s;
    }
    private Handler handler  = new Handler(){

        @Override
        public void handleMessage(Message msg) {

               final String result = (String)msg.obj;

            if(TextUtils.isEmpty(result)){
                onLoad();
                return;
            }

            if(!TextUtils.isEmpty(result)){
                onLoad();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String code =jsonObject.getString("statusCode");
                    if("000000".equalsIgnoreCase(code)){
                        if(jsonObject.has("result")){
                            JSONArray array = jsonObject.getJSONArray("result");
                            if(array!=null&&array.length()>0){
                                ArrayList<ECReadMessageMember> haveArray = new ArrayList();
                                for(int i = 0; i < array.length(); ++i) {
                                    JSONObject device = array.getJSONObject(i);
                                    ECReadMessageMember feedBack = toMessageFeedBack(device.toString());
                                    if(feedBack != null) {
                                        haveArray.add(feedBack);
                                    }
                                    if(haveArray!=null&&haveArray.size()>0&&type==1){
                                        IMessageSqlManager.updateMsgReadCount(message.getMsgId(), haveArray.size());
                                    }
                                }
                                adapter.addAll(haveArray);
                            }
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btn_left:
                hideSoftKeyboard();
                finish();
                break;
        }
    }
    private  int index =1;

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {
        index++;
        addData(index);

    }

    private void addData(int index) {
        StringEntity s = null;
        try {
            s = buildBody(message.getMsgId(),index,type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(s!=null) {
            NetUtils.init(s, this);
            new Thread(NetUtils.getInstance()).start();
        }
    }

    /** 停止刷新， */
    private void onLoad() {
        lv.stopRefresh();
        lv.stopLoadMore();
        lv.setRefreshTime("刚刚");
    }

    @Override
    public void onSuccess(String reslut) {

        Message msg  = new Message();
        msg.obj =reslut;

        handler.sendMessage(msg);
    }

    public static ECReadMessageMember toMessageFeedBack(String message) {
        if(!TextUtils.isEmpty(message)) {
            try {
                ECReadMessageMember e = new ECReadMessageMember();
                JSONObject jsonObject = new JSONObject(message);
                if(jsonObject.has("useracc")) {
                    e.setAccount(jsonObject.getString("useracc"));
                }

                if(jsonObject.has("time")) {
                    e.setTimestamp(jsonObject.getString("time"));
                }
                return e;
            } catch (JSONException var3) {
            }
        }
        return null;
    }


    private  class FeedBackAdapter extends ArrayAdapter<ECReadMessageMember>{


        public FeedBackAdapter(Context ctx) {
            super(ctx, 0);
        }

        public void setData(List<ECReadMessageMember> data) {
            clear();
            if(data != null) {
                for(ECReadMessageMember item : data) {
                    add(item);
                }
            }
            notifyDataSetChanged();
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
               View v = View.inflate(ECMessageFeedUI.this,R.layout.message_feedback_item,null);
                   TextView accountTv= (TextView) v.findViewById(R.id.messageAccount);
                    ECReadMessageMember item =(ECReadMessageMember) getItem(position);

            accountTv.setText(item.getAccount());
            return v;
        }
    }
}
