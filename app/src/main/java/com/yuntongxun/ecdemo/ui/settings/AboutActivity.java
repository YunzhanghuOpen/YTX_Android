package com.yuntongxun.ecdemo.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;

import com.yuntongxun.ecdemo.R;
import com.yuntongxun.ecdemo.common.utils.LogUtil;
import com.yuntongxun.ecdemo.ui.ECSuperActivity;

public class AboutActivity extends ECSuperActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();
        getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt,
                -1, null,
                null,
                getString(R.string.app_company), null, this);




    }


    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static boolean isPad2(Context activity) {
        TelephonyManager telephony = (TelephonyManager)activity.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
            return true;
        }else {
            return false;
        }
    }





    private  TextView tv;

    private void initViews() {

        tv=(TextView)findViewById(R.id.tv_open_web);
        tv.setOnClickListener(this);


    }



    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.btn_left:
                hideSoftKeyboard();
                finish();
                break;
            case R.id.tv_open_web:
                Intent intent=new Intent(this,WebAboutActivity.class);
                intent.putExtra("url","http://m.yuntongxun.com/qrcode/tiyan/tiyan.html?m_im");
                intent.putExtra("isFormAbout",true);
                startActivity(intent);


        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }


}
