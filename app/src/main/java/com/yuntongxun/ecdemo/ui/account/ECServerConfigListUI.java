package com.yuntongxun.ecdemo.ui.account;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yuntongxun.ecdemo.R;
import com.yuntongxun.ecdemo.common.dialog.ECListDialog;
import com.yuntongxun.ecdemo.common.utils.CommomUtil;
import com.yuntongxun.ecdemo.common.utils.ECPreferenceSettings;
import com.yuntongxun.ecdemo.common.utils.ECPreferences;
import com.yuntongxun.ecdemo.common.utils.ToastUtil;
import com.yuntongxun.ecdemo.core.comparator.ServerConfigBean;
import com.yuntongxun.ecdemo.storage.ServerConfigSqlManager;
import com.yuntongxun.ecdemo.ui.ECSuperActivity;
import com.yuntongxun.ecsdk.ECDevice;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luhuashan on 16/3/18.
 */
public class ECServerConfigListUI extends ECSuperActivity implements View.OnClickListener {
    private static final String TAG = "ECServerConfigListUI";

    /**
     * The sub Activity implement, set the Ui Layout
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.ec_serverconfig_list;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt,
                -1, null,null,
                "配置列表", null, this);
        initView();

    }
    private ListView lv;
    private List<ServerConfigBean> list;
    private ServerConfigAdapter adapter;

    private void initView() {
        lv= (ListView) findViewById(R.id.lv_serverconfig);
         list= ServerConfigSqlManager.queryServerConfigs();
        adapter=new ServerConfigAdapter(this,0,list);
        lv.setAdapter(adapter);
        lv.setOnItemLongClickListener(listener);
        operateArr=new ArrayList<String>();
        operateArr.add("删除该配置");
        operateArr.add("使用该配置");
        operateArr.add("修改该配置");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_left:
                hideSoftKeyboard();
                finish();
                break;
        }

    }

    private AdapterView.OnItemLongClickListener listener =new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                ServerConfigBean bean=list.get(i);
                showDialog(bean);


            return false;
        }
    };
    private  List<String> operateArr;
    private boolean showDialog(final ServerConfigBean bean) {
        ECListDialog dialog = new ECListDialog(this, operateArr
                );
        dialog.setOnDialogItemClickListener(new ECListDialog.OnDialogItemClickListener() {
            @Override
            public void onDialogItemClick(Dialog d, int position) {

                doOperate(bean,position);
            }
        });
        dialog.setTitle("选择操作类型");
        dialog.show();
        return true;
    }

    private void doOperate(ServerConfigBean bean,int position) {

        if(position==0){
            if(bean!=null) {
                ServerConfigSqlManager.deleteServerConfig(bean.getId());
                adapter.remove(bean);
                adapter.notifyDataSetChanged();

            }

        }else if(position==1){

            setItemServerConfig(bean);
        }else if(position==2){

            Intent intent = new Intent();
            //把返回数据存入Intent
            intent.putExtra("result", bean);
            //设置返回数据
           setResult(2, intent);
            //关闭Activity
            finish();


        }
    }

    private void setItemServerConfig(ServerConfigBean bean) {

        if(bean==null){
            return;
        }
        ECDevice.initServer(this, CommomUtil.setUpXml(bean.getConnectip(), bean.getConnectport(),
                bean.getLvsip(), bean.getLvsport(), bean.getFileip(), bean.getFileport()));

        try {
            ECPreferences.savePreference(
                    ECPreferenceSettings.SETTINGS_CUSTOM_APPKEY,
                    bean.getAppid(), true);

            ECPreferences.savePreference(
                    ECPreferenceSettings.SETTINGS_CUSTOM_TOKEN,
                    bean.getApptoken(), true);
            ECPreferences.savePreference(
                    ECPreferenceSettings.SETTINGS_SERVER_CUSTOM,
                    Boolean.TRUE, true);
            ToastUtil.showMessage("设置成功");

        } catch (InvalidClassException e) {
        }

    }


    public class ServerConfigAdapter extends ArrayAdapter<ServerConfigBean> {


        public ServerConfigAdapter(Context context, int textViewResourceId, List<ServerConfigBean> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view;
            ServerConfigHolder holder;
            if (convertView == null || convertView.getTag() == null) {
                view = getLayoutInflater().inflate(R.layout.voice_meeting_item, null);
                holder = new ServerConfigHolder();
                view.setTag(holder);

                holder.name = (TextView) view.findViewById(R.id.chatroom_name);
                holder.tips = (TextView) view.findViewById(R.id.chatroom_tips);
                holder.lock = (ImageView) view.findViewById(R.id.lock);
                holder.tvGo=(TextView)view.findViewById(R.id.goto_icon);
            } else {
                view = convertView;
                holder = (ServerConfigHolder) convertView.getTag();
            }

            ServerConfigBean bean = getItem(position);
            if (bean != null) {
                holder.name.setText(bean.getName());
                holder.tips.setVisibility(View.GONE);
                holder.lock.setVisibility(View.GONE);
                holder.tvGo.setVisibility(View.GONE);
            }

            return view;

        }

        class ServerConfigHolder {
            TextView name;
            TextView tips;
            TextView tvGo;
            ImageView lock;
        }
    }


}
