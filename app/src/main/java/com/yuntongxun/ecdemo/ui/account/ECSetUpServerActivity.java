package com.yuntongxun.ecdemo.ui.account;

import java.io.InvalidClassException;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.yuntongxun.ecdemo.R;
import com.yuntongxun.ecdemo.common.utils.CommomUtil;
import com.yuntongxun.ecdemo.common.utils.ECPreferenceSettings;
import com.yuntongxun.ecdemo.common.utils.ECPreferences;
import com.yuntongxun.ecdemo.common.utils.ToastUtil;
import com.yuntongxun.ecdemo.core.comparator.ServerConfigBean;
import com.yuntongxun.ecdemo.storage.ServerConfigSqlManager;
import com.yuntongxun.ecdemo.ui.ECSuperActivity;
import com.yuntongxun.ecsdk.ECDevice;

public class ECSetUpServerActivity extends ECSuperActivity implements
		OnClickListener {

	private EditText etConnect;
	private EditText etConnectPort;
	private EditText etLVS;
	private EditText etLVSPort;
	private EditText etFile;
	private EditText etFilePort;
	private EditText etAppid;
	private EditText etAppToken;
	private Button buSetIp;
	private Button buSetApp;
	private Button buSetAll;
	private Button buAddServerList;
	private Button buQueryServerList;
	private Button buModify;
	private EditText etSetName;

	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.setup_server_layout;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt,
				R.drawable.btn_style_green, null, "重置配置",
				getString(R.string.setup_server), null, this);

		initViews();
		ServerConfigSqlManager.getInstance();
	}

	private void initViews() {

		etConnect = (EditText) findViewById(R.id.setup_connect);
		etConnectPort = (EditText) findViewById(R.id.setup_connect_port);
		etLVS = (EditText) findViewById(R.id.setup_lvs);
		etLVSPort = (EditText) findViewById(R.id.setup_lvs_port);
		etFile = (EditText) findViewById(R.id.setup_fileserver);
		etFilePort = (EditText) findViewById(R.id.setup_fileserver_port);
		etAppid = (EditText) findViewById(R.id.setup_appid);
		etAppToken = (EditText) findViewById(R.id.setup_apptoken);
		etSetName = (EditText) findViewById(R.id.setup_name);
		buSetApp=(Button)findViewById(R.id.set_app_button);
		buSetIp=(Button)findViewById(R.id.set_ip_button);
		buModify=(Button)findViewById(R.id.bu_modify_item);

		buSetAll= (Button) findViewById(R.id.set_all_button);
		buAddServerList= (Button) findViewById(R.id.bu_add_setupserver);
		buQueryServerList= (Button) findViewById(R.id.bu_query_serverlist);
		buSetApp.setOnClickListener(this);
		buSetIp.setOnClickListener(this);
		buSetAll.setOnClickListener(this);
		buAddServerList.setOnClickListener(this);
		buQueryServerList.setOnClickListener(this);
		buModify.setOnClickListener(this);


	}

	private void setApp(){

		String appkey = etAppid.getText().toString().trim();
		String apptoken = etAppToken.getText().toString().trim();
		if (TextUtils.isEmpty(appkey) || TextUtils.isEmpty(apptoken)
				) {
			ToastUtil.showMessage("请输入完整信息!");
			return;
		}
		try {
			ECPreferences.savePreference(
					ECPreferenceSettings.SETTINGS_CUSTOM_APPKEY,
					appkey, true);

			ECPreferences.savePreference(
					ECPreferenceSettings.SETTINGS_CUSTOM_TOKEN,
					apptoken, true);
			ECPreferences.savePreference(
					ECPreferenceSettings.SETTINGS_SERVER_CUSTOM,
					Boolean.TRUE, true);
			ToastUtil.showMessage("保存成功");

		} catch (InvalidClassException e) {
			e.printStackTrace();
		}
	}

	private void setIp(){
		String connect = etConnect.getText().toString().trim();
		String connectPort = etConnectPort.getText().toString().trim();
		String lvs = etLVS.getText().toString().trim();
		String lvsport = etLVSPort.getText().toString().trim();
		String file = etFile.getText().toString().trim();
		String fileport = etFilePort.getText().toString().trim();

		if (TextUtils.isEmpty(connect) || TextUtils.isEmpty(connectPort)
				|| TextUtils.isEmpty(lvs) || TextUtils.isEmpty(lvsport)
				|| TextUtils.isEmpty(file) || TextUtils.isEmpty(fileport)) {

			ToastUtil.showMessage("请输入完整信息!");
			return;
		}
		ECDevice.initServer(this, CommomUtil.setUpXml(connect, connectPort,
				lvs, lvsport, file, fileport));
		ToastUtil.showMessage("保存成功");

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bu_add_setupserver:

			logicAddServerItem();

			break;
		case R.id.bu_query_serverlist:


			startActivityForResult(new Intent(this, ECServerConfigListUI.class),1);
			break;
		case R.id.set_all_button:

			setApp();
			setIp();

			break;

		case R.id.bu_modify_item:

			doModifyServerItem();
			break;

		case R.id.btn_left:
			hideSoftKeyboard();
			finish();
			break;

			case R.id.set_app_button:
				setApp();
				break;
			case R.id.set_ip_button:
			setIp();
					break;

		case R.id.text_right:

		ECDevice.resetServer(this);
			try {
				ECPreferences.savePreference(
						ECPreferenceSettings.SETTINGS_CUSTOM_APPKEY,
						"20150314000000110000000000000010", true);

				ECPreferences.savePreference(
						ECPreferenceSettings.SETTINGS_CUSTOM_TOKEN,
						"17E24E5AFDB6D0C1EF32F3533494502B", true);
				ECPreferences.savePreference(
						ECPreferenceSettings.SETTINGS_SERVER_CUSTOM,
						Boolean.TRUE, true);
				ToastUtil.showMessage("重置成功");

			} catch (InvalidClassException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}

	private void doModifyServerItem() {

		String appkey = etAppid.getText().toString().trim();
		String apptoken = etAppToken.getText().toString().trim();
		String connect = etConnect.getText().toString().trim();
		String connectPort = etConnectPort.getText().toString().trim();
		String lvs = etLVS.getText().toString().trim();
		String lvsport = etLVSPort.getText().toString().trim();
		String file = etFile.getText().toString().trim();
		String fileport = etFilePort.getText().toString().trim();
		String etName = etSetName.getText().toString().trim();


		if(TextUtils.isEmpty(appkey)||TextUtils.isEmpty(apptoken)||TextUtils.isEmpty(connect)
				||TextUtils.isEmpty(connectPort)||TextUtils.isEmpty(lvs)||TextUtils.isEmpty(lvsport)
				||TextUtils.isEmpty(file)||TextUtils.isEmpty(fileport)||TextUtils.isEmpty(etName)
				){

			ToastUtil.showMessage("请输入完整信息!");
			return;

		}
		ServerConfigBean serverConfigBean=new ServerConfigBean();
		serverConfigBean.setAppid(appkey);
		serverConfigBean.setApptoken(apptoken);
		serverConfigBean.setName(etName);
		serverConfigBean.setConnectip(connect);
		serverConfigBean.setConnectport(connectPort);
		serverConfigBean.setLvsip(lvs);
		serverConfigBean.setLvsport(lvsport);
		serverConfigBean.setFileip(file);
		serverConfigBean.setFileport(fileport);

		long result= ServerConfigSqlManager.updateServerConfig(serverConfigBean,backUpServerBean.getName());

		if(result>0){
			ToastUtil.showMessage("修改成功");
		}else {
			ToastUtil.showMessage("修改失败");
		}

	}

	private  ServerConfigBean backUpServerBean;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode==1&&resultCode==2){

			ServerConfigBean bean= (ServerConfigBean)data.getSerializableExtra("result");
			if(bean==null){
				return;
			}
			backUpServerBean=bean;
			etAppid.setText(bean.getAppid());
			etAppToken.setText(bean.getApptoken());
			etConnect.setText(bean.getConnectip());
			etConnectPort.setText(bean.getConnectport());
			etLVS.setText(bean.getLvsip());
			etLVSPort.setText(bean.getLvsport());
			etFile.setText(bean.getFileip());
			etFilePort.setText(bean.getFileport());
			etSetName.setText(bean.getName());
			buModify.setVisibility(View.VISIBLE);
		}

	}

	private void loginQueryServerItems() {

	}

	private void logicAddServerItem() {

		String appkey = etAppid.getText().toString().trim();
		String apptoken = etAppToken.getText().toString().trim();
		String connect = etConnect.getText().toString().trim();
		String connectPort = etConnectPort.getText().toString().trim();
		String lvs = etLVS.getText().toString().trim();
		String lvsport = etLVSPort.getText().toString().trim();
		String file = etFile.getText().toString().trim();
		String fileport = etFilePort.getText().toString().trim();
		String etName = etSetName.getText().toString().trim();


		if(TextUtils.isEmpty(appkey)||TextUtils.isEmpty(apptoken)||TextUtils.isEmpty(connect)
				||TextUtils.isEmpty(connectPort)||TextUtils.isEmpty(lvs)||TextUtils.isEmpty(lvsport)
				||TextUtils.isEmpty(file)||TextUtils.isEmpty(fileport)||TextUtils.isEmpty(etName)
				){

			ToastUtil.showMessage("请输入完整信息!");
			return;

		}

		if(ServerConfigSqlManager.isConfigItemExist(etName)){
			ToastUtil.showMessage("该配置名称已经存在");
			return;
		}

		ServerConfigBean serverConfigBean=new ServerConfigBean();
		serverConfigBean.setAppid(appkey);
		serverConfigBean.setApptoken(apptoken);
		serverConfigBean.setName(etName);
		serverConfigBean.setConnectip(connect);
		serverConfigBean.setConnectport(connectPort);
		serverConfigBean.setLvsip(lvs);
		serverConfigBean.setLvsport(lvsport);
		serverConfigBean.setFileip(file);
		serverConfigBean.setFileport(fileport);
		long result = ServerConfigSqlManager.insertServerConfig(serverConfigBean);
		if(result>0){
			ToastUtil.showMessage("保存成功");
		}else {
			ToastUtil.showMessage("保存失败");
		}


	}

}
