package com.yuntongxun.ecdemo.ui.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.yuntongxun.ecdemo.R;
import com.yuntongxun.ecdemo.common.dialog.IBaseAdapter;
import com.yuntongxun.ecdemo.common.utils.ECPreferenceSettings;
import com.yuntongxun.ecdemo.common.utils.ECPreferences;
import com.yuntongxun.ecdemo.ui.ECSuperActivity;
import com.yuntongxun.ecdemo.ui.SDKCoreHelper;
import com.yuntongxun.ecsdk.CameraCapability;
import com.yuntongxun.ecsdk.CameraInfo;
import com.yuntongxun.ecsdk.ECVoIPSetupManager;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.List;

public class SetRatioActivity extends ECSuperActivity implements OnClickListener {
	private ListView listView;
	private ArrayList<CameraCapability> list;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt,
				R.drawable.btn_style_green, null,
				getString(R.string.app_save_ratio),
				getString(R.string.app_set_ratio), null, this);

		listView = (ListView) findViewById(R.id.listView);
		  list = new ArrayList();



		ECVoIPSetupManager voIPSetupManager = SDKCoreHelper.getVoIPSetManager();
		if(voIPSetupManager==null){
			finish();
		}
		CameraInfo[] arr= voIPSetupManager.getCameraInfos();

		for(int i =0;i<arr.length;i++){
		   CameraCapability[] cArr = arr[i].caps;
			for(CameraCapability item:cArr){
				if(item.getWidth()*item.getHeight()<=480*320) {
					item.setCameraIndex(i);
					list.add(item);
				}
			}
		}

		listView.setAdapter(new RatioAdapter(this,list));

	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_ratio;
	}



	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_left:
				hideSoftKeyboard();
				finish();
				break;
			case R.id.text_right:
				int position = listView.getCheckedItemPosition();
				if(position!=-1){
					CameraCapability capability=list.get(position);
					String ratio = capability.getWidth()+"*"+capability.getHeight()+"*"+capability.cameraIndex;
					try {
						ECPreferences.savePreference(
                                ECPreferenceSettings.SETTINGS_RATIO_CUSTOM,
                                ratio, true);
						finish();
					} catch (InvalidClassException e) {
						e.printStackTrace();
					}
				}else {
					try {
						ECPreferences.savePreference(
								ECPreferenceSettings.SETTINGS_RATIO_CUSTOM,
								"", true);
						finish();
					} catch (InvalidClassException e) {
						e.printStackTrace();
					}
				}
				break;
			default:
				break;
		}
	}




	private class RatioAdapter extends IBaseAdapter<CameraCapability>{

		public RatioAdapter(Context ctx, List<CameraCapability> data) {
			super(ctx, data);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {


			if(convertView == null) {
				convertView = View.inflate(SetRatioActivity.this,android.R.layout.simple_list_item_single_choice , null);
			}
			CameraCapability item =(CameraCapability)getItem(position);
			String index = item.cameraIndex ==0?"后置":"前置";
			((TextView) convertView.findViewById(android.R.id.text1)).setText(item.getWidth()+"x"+item.getHeight()+index);
			return convertView;


		}
	}
}
