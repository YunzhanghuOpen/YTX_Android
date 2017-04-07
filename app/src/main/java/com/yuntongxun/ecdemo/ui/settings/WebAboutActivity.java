package com.yuntongxun.ecdemo.ui.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebView;

import com.yuntongxun.ecdemo.R;
import com.yuntongxun.ecdemo.common.base.OverflowAdapter;
import com.yuntongxun.ecdemo.common.base.OverflowHelper;
import com.yuntongxun.ecdemo.common.utils.BitmapUtil;
import com.yuntongxun.ecdemo.common.utils.CheckUtil;
import com.yuntongxun.ecdemo.common.utils.DemoUtils;
import com.yuntongxun.ecdemo.common.utils.FileAccessor;
import com.yuntongxun.ecdemo.common.utils.ToastUtil;
import com.yuntongxun.ecdemo.core.ECAsyncTask;
import com.yuntongxun.ecdemo.pojo.RichTextBean;
import com.yuntongxun.ecdemo.ui.ECSuperActivity;
import com.yuntongxun.ecdemo.ui.SDKCoreHelper;
import com.yuntongxun.ecdemo.ui.contact.MobileContactSelectActivity;
import com.yuntongxun.ecdemo.ui.contact.SelectContactUI;
import com.yuntongxun.ecdemo.ui.group.BaseSearch;
import com.yuntongxun.ecdemo.ui.group.CreateGroupActivity;
import com.yuntongxun.ecdemo.ui.group.ECDiscussionActivity;
import com.yuntongxun.ecdemo.ui.interphone.InterPhoneListActivity;
import com.yuntongxun.ecdemo.ui.meeting.MeetingListActivity;
import com.yuntongxun.ecdemo.ui.videomeeting.VideoconferenceConversation;

import android.webkit.WebSettings.TextSize;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

public class WebAboutActivity extends ECSuperActivity implements View.OnClickListener, PlatformActionListener {

    private OverflowHelper mOverflowHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url=getIntent().getStringExtra("url");
        isFromAbout=getIntent().getBooleanExtra("isFormAbout",false);
        initResViews();
        ShareSDK.initSDK(this);


        saveDefaultImgToSDCard();



        getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt,
                R.drawable.btn_style_green, null,
                "分享",
                "下载", null, this);
        mOverflowHelper = new OverflowHelper(this);
    }


    public void saveDefaultImgToSDCard(){
        String imagePath = FileAccessor.IMESSAGE_RICH_TEXT + "/" + DemoUtils.md5(BitmapUtil.ATTACT_ICON)+".jpg";
        File file = new File(imagePath);
        if(!file.exists()) {
            try {
                file.createNewFile();
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.attachment_icon);
                BitmapUtil.saveBitmapToLocalSDCard(bitmap, BitmapUtil.ATTACT_ICON);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    private  boolean isFromAbout;

    private OverflowAdapter.OverflowItem[] mItems;


    private void controlPlusSubMenu() {
        if (mOverflowHelper == null) {
            return;
        }

        if (mOverflowHelper.isOverflowShowing()) {
            mOverflowHelper.dismiss();
            return;
        }

        if(mItems == null) {
            initOverflowItems();
        }

        mOverflowHelper.setOverflowItems(mItems);
        mOverflowHelper
                .setOnOverflowItemClickListener(mOverflowItemCliclListener);
        mOverflowHelper.showAsDropDown(findViewById(R.id.text_right));
    }

    void initOverflowItems() {

            if(isFromAbout){
                mItems = new OverflowAdapter.OverflowItem[1];
                mItems[0] = new OverflowAdapter.OverflowItem(
                        "分享");

            }else {
                mItems = new OverflowAdapter.OverflowItem[2];
                mItems[0] = new OverflowAdapter.OverflowItem(
                        "转发");
                mItems[1] = new OverflowAdapter.OverflowItem(
                        "分享");

            }




    }
    private final AdapterView.OnItemClickListener mOverflowItemCliclListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            controlPlusSubMenu();

            OverflowAdapter.OverflowItem overflowItem= mItems[position];
            String title=overflowItem.getTitle();
            url=mWebView.getUrl();

            if ("转发".equals(title)) {
            startActivity(new Intent(WebAboutActivity.this, SelectContactUI.class).putExtra("url",mWebView.getUrl()));

            } else if ("分享".equals(title)) {
                startShareAction();

            }
        }

    };


    private  String url;
    ShareDialog  shareDialog;
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_left:
                hideSoftKeyboard();
                finish();
                break;


            case R.id.text_right:

                controlPlusSubMenu();
                break;

            default:
                break;
        }

    }

    private void  shareToWeChat(String title,String text,String imgurl,String url){

        ShareParams sp = new ShareParams();
        sp.setShareType(Platform.SHARE_WEBPAGE);//非常重要：一定要设置分享属性
        sp.setTitle(title);  //分享标题
        sp.setText(text);   //分享文本
        sp.setImageUrl(imgurl);//网络图片rul
        sp.setUrl(url);   //网友点进链接后，可以看到分享的详情

        //3、非常重要：获取平台对象
        Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
        wechat.setPlatformActionListener(WebAboutActivity.this); // 设置分享事件回调
        // 执行分享
        wechat.share(sp);

    }
    private void  shareToWeChatFriend(String title,String text,String imgurl,String url){

        ShareParams sp = new ShareParams();
        sp.setShareType(Platform.SHARE_WEBPAGE);//非常重要：一定要设置分享属性
        sp.setTitle(title);  //分享标题
        sp.setText(text);   //分享文本
        sp.setImageUrl(imgurl);//网络图片rul
        sp.setUrl(url);   //网友点进链接后，可以看到分享的详情

        //3、非常重要：获取平台对象
        Platform wechat = ShareSDK.getPlatform(WechatMoments.NAME);
        wechat.setPlatformActionListener(WebAboutActivity.this); // 设置分享事件回调
        // 执行分享
        wechat.share(sp);

    }

    private void startShareAction() {
        shareDialog = new ShareDialog(this);
        shareDialog.setCancelButtonOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                shareDialog.dismiss();

            }
        });
        shareDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                if (arg2==0) {
                    if(isFromAbout){
                        shareToWeChat("容联云通讯","国内顶级云通讯平台,功能全,技术强,集成快","http://www.yuntongxun.com/front/images/im_img4.png","http://m.yuntongxun.com/qrcode/tiyan/tiyan.html?m_im");
                    }else {
                       new ParseHtmlTask(WebAboutActivity.this).execute();
                        position=0;
                    }

                } else if (arg2==1) {
                    if(isFromAbout){
                        shareToWeChatFriend("容联云通讯", "国内顶级云通讯平台,功能全,技术强,集成快", "http://www.yuntongxun.com/front/images/im_img4.png", "http://m.yuntongxun.com/qrcode/tiyan/tiyan.html?m_im");
                    }else {
                        new ParseHtmlTask(WebAboutActivity.this).execute();
                        position=1;
                    }
                }
                shareDialog.dismiss();

            }
        });
    }


    private  int position=0;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_web_about_url;
    }

    private WebView mWebView;

    private void initResViews() {
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setSavePassword(false);
        mWebView.getSettings().setSaveFormData(false);
        mWebView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.getSettings().setGeolocationEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        mWebView.loadUrl(url);
    }




    @Override
    public void onCancel(Platform arg0, int arg1) {//回调的地方是子线程，进行UI操作要用handle处理
        handler.sendEmptyMessage(5);

    }

    @Override
    public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {//回调的地方是子线程，进行UI操作要用handle处理
          if (arg0.getName().equals(Wechat.NAME)) {
            handler.sendEmptyMessage(1);
        } else if (arg0.getName().equals(WechatMoments.NAME)) {
            handler.sendEmptyMessage(3);
        }

    }



    Document doc = null;
    RichTextBean richTextBean;
    private class ParseHtmlTask extends ECAsyncTask {


        public ParseHtmlTask(Context context) {
            super(context);
        }


        @Override
        protected Object doInBackground(Object[] params) {

            try {
                doc = Jsoup.connect(url).timeout(10000).get();
                richTextBean = new RichTextBean();
                richTextBean.setUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (doc != null) {
                getShareBean(doc);
            }
            return richTextBean;
        }
        @Override
        protected void onPostExecute(Object o) {
            dismissCommonPostingDialog();
            if(o!=null&&o instanceof RichTextBean) {
                RichTextBean bean = (RichTextBean) o;
                if (position == 0) {
                    shareToWeChat(bean.getTitle(), bean.getDesc(), bean.getPicUrl(), bean.getUrl());
                }else {
                    shareToWeChatFriend(bean.getTitle(), bean.getDesc(), bean.getPicUrl(), bean.getUrl());

                }
            }
        }


    }

    private void getShareBean(Document doc) {
        if (null != doc) {


            Elements element = doc.select("[src]");

            if(element!=null&&element.size()>0) {
                for (Element src : element) {
                    if (src.tagName().equals("img")) {

                        String imgUrl = src.attr("abs:src");
                        if (CheckUtil.isVailUrl(imgUrl)) {
                            richTextBean.setPicUrl(imgUrl);
                            break;
                        }
                    }
                }
            }

            Elements elementMeta = doc.getElementsByTag("meta");

            Elements titleArr = doc.getElementsByTag("title");

             if(titleArr!=null&&titleArr.size()>0){
                 String title = doc.getElementsByTag("title").first().text();
                 richTextBean.setTitle(title);
             }else {
                 richTextBean.setTitle("标题");
             }
            for(Element s :elementMeta){
                if(s!=null) {
                    if (s.hasAttr("name") && s.attr("name").equals("Description")) {
                        richTextBean.setDesc(s.attr("content"));
                        break;
                    }
                }
            }
//            for (Element str : element) {
//                Elements linkText = str.select("[src]");
//                String imgUrl = linkText.attr("src");
//                if(CheckUtil.isVailUrl(imgUrl)){
//                    richTextBean.setPicUrl(imgUrl);
//                    break;
//                }
//            }
        }

    }


        @Override
        public void onError(Platform arg0, int arg1, Throwable arg2) {//回调的地方是子线程，进行UI操作要用handle处理
            arg2.printStackTrace();
            Message msg = new Message();
            msg.what = 6;
            msg.obj = arg2.getMessage();
            handler.sendMessage(msg);
        }

        Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {


                    case 2:
                        Toast.makeText(getApplicationContext(), "微信分享成功", Toast.LENGTH_LONG).show();
                        break;
                    case 3:
                        Toast.makeText(getApplicationContext(), "微信朋友圈分享成功", Toast.LENGTH_LONG).show();
                        break;
                    case 4:
                        break;

                    case 5:
                        Toast.makeText(getApplicationContext(), "取消分享", Toast.LENGTH_LONG).show();
                        break;
                    case 6:
                        Toast.makeText(getApplicationContext(), "分享失败" + msg.obj, Toast.LENGTH_LONG).show();
                        break;

                    default:
                        break;
                }
            }

        };

}
