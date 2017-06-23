# 容联云Demo红包SDK集成文档

## 集成概述
* 红包SDK分为两个版本，即钱包版红包SDK与支付宝版红包SDK。
* 使用钱包版红包SDK的用户，可以使用银行卡支付或支付宝支付等第三方支付来发红包；收到的红包金额会进入到钱包余额，并支持提现到绑定的银行卡。
* 使用支付宝版红包SDK的用户，发红包仅支持支付宝支付；收到的红包金额即时入账至绑定的支付宝账号。
* 请选择希望接入的版本并下载对应的SDK进行集成，钱包版红包SDK与支付宝版红包SDK集成方式相同。
* 需要注意的是如果已经集成了钱包版红包SDK，暂不支持切换到支付宝版红包SDK（两个版本不支持互通）。
* 容联云Demo中使用redPacketlibrary模块集成了红包SDK相关红能。

## redPacketlibrary介绍

* redPacketlibrary是在容联云Demo中集成红包功能的模块，开发者可参考该模块中的方法集成红包功能。建议开发者以模块的形式集成红包功能，便于项目的更新和维护。
* **注意此library仅支持非支付宝UI开源版本使用。**
## redPacketlibrary目录说明

* libs ：包含了集成红包功能所依赖的jar包。
* res  ：包含了聊天页面中的资源文件（例如红包消息卡片，回执消息的UI等）。

## 支付宝UI开源版本
* git clone git@github.com:YunzhanghuOpen/YTX_Android.git
* cd YTX_Android
* git checkout redpack-ali-open
* cd redpacketui-open 
* git submodule init
* git submodule update
* **开源版没有redpacketlibrary，红包使用相关的工具类移步到app里面的redpacketutils包下面。资源文件以YTX开头**
## 红包SDK的更新
* 以支付宝版红包SDK为例，修改com.yunzhanghu.redpacket:redpacket-alipay:1.1.2中的1.1.2为已发布的更高版本(例如1.1.3)，同步之后即可完成红包SDK的更新。

# 开始集成

## 添加对红包工程的依赖

* 在工程的build.gradle(Top-level build file)添加远程仓库地址
```java
allprojects {
    repositories {
        jcenter()
        maven {
            url "https://raw.githubusercontent.com/YunzhanghuOpen/redpacket-maven-repo/master/release"
        }
    }
}
```
* YTXAndroidDemo的build.gradle中
```java
//非开源版
compile project(':redpacketlibrary')
//支付宝UI开源
compile project(':redpacketui-open')
```    
* 在redpacketlibrary的build.gradle中

* 钱包版配置如下

```java
dependencies {
    compile fileTree(include: ['*.jar'], dir: 'libs')
    compile 'com.android.support:support-v4:23.0.1'
    compile 'com.android.support:recyclerview-v7:25.3.1'
    compile 'com.yunzhanghu.redpacket:redpacket-wallet:3.5.0'
}
```
* 支付宝版配置如下

```java
dependencies {
    compile fileTree(include: ['*.jar'], dir: 'libs')
    compile 'com.android.support:support-v4:23.0.1'
    compile 'com.android.support:recyclerview-v7:25.3.1'
    compile 'com.yunzhanghu.redpacket:redpacket-alipay:2.0.1'
}
```
## 初始化红包SDK

* 在Application的onCreate()方法中
```java
//初始化红包上下文
RedPacket.getInstance().initRedPacket(this, RPConstant.AUTH_METHOD_YTX, new RPInitRedPacketCallback() {
                @Override
                public void initTokenData(RPValueCallback<TokenData> rpValueCallback) {
                    //不需要实现
                }

                @Override
                public RedPacketInfo initCurrentUserSync() {
                     // 这里需要同步设置当前用户id、昵称和头像url
                   RedPacketInfo redPacketInfo = new RedPacketInfo();
                   redPacketInfo.currentUserId = "yunzhanghu";
                   redPacketInfo.currentAvatarUrl = "testURL";
                   redPacketInfo.currentNickname = "yunzhanghu001";
                   return redPacketInfo;
                }
            });
// 打开Log开关，正式发布需要关闭
RedPacket.getInstance().setDebugMode(true); 
```
* **initRedPacket(context, authMethod, callback) 参数说明**

| 参数名称       | 参数类型             | 参数说明  | 必填         |
| ---------- | ----------------------- | ----- | ---------- |
| context    | Context                 | 上下文   | 是          |
| authMethod | String                  | 授权类型  | 是**（见注1）** |
| callback   | RPInitRedPacketCallback | 初始化接口 | 是          |  

* **RPInitRedPacketCallback 接口说明**

| **initTokenData(RPValueCallback<TokenData> callback)** |
| :---------------------------------------- |
| **该方法用于初始化TokenData，在进入红包相关页面、红包Token不存在或红包Token过期时调用。容联云SDK已实现，开发者不需要实现** |
| **initCurrentUserSync()**                |
| **该方法用于初始化当前用户信息，在进入红包相关页面时调用，需同步获取。**   |

## 添加红包和转账入口
* **转账仅支持钱包版**
* 在AppPanelControl中添加红包和转账入口

```java
public int[] cap = new int[] { ...,R.string.attach_red_packet,...};
public int[] capVoip = new int[] { ...,R.string.attach_red_packet,...};
private Capability getCapability(int resid) {
		Capability capability = null;
		switch (resid) {
			...
			//红包按钮
			case R.string.attach_red_packet:
				capability = new Capability(getContext().getString(
						R.string.attach_red_packet), R.drawable.ytx_chat_redpacket_selector);
				break;
		        //转账按钮
			case R.string.attach_transfer:
				capability = new Capability(getContext().getString(
						R.string.attach_transfer), R.drawable.ytx_chat_transfer_selector);
				break;
			default:
				break;
		}
		capability.setId(resid);
		return capability;
	}
```

* 在AppPanel中设置红包入口监听事件

```java
final AppGrid.OnCapabilityItemClickListener mCapabilityItemClickListener= new AppGrid.OnCapabilityItemClickListener() {
  @Override
  public void onPanleItemClick(int index, int capabilityId , String capabilityName) {
	if(mAppPanelItemClickListener == null) {
		return;
	}
	switch (capabilityId) {
	...
	case R.string.attach_red_packet:
	mAppPanelItemClickListener.OnSelectRedPacketClick();
	break;
        case R.string.attach_transfer:
	mAppPanelItemClickListener.OnSelectTransferClick();
	break;
	default:
	break;
		}
	}
};
public interface OnAppPanelItemClickListener {
   void OnTakingPictureClick();
   void OnSelectImageClick();
   void OnSelectFileClick();
   void OnSelectVoiceClick();
   void OnSelectVideoClick();
   void OnSelectFireMsgClick();
   void OnSelectFireLocationClick();
   void OnSelectRedPacketClick();
   void OnSelectTransferClick();
}
```
* 在CCPChattingFooter2中设置
```java
final private AppPanel.OnAppPanelItemClickListener mAppPanelItemClickListener = new AppPanel.OnAppPanelItemClickListener() {
  ...
  @Override
  public void OnSelectRedPacketClick() {
   // TODO Auto-generated method stub
   if (mChattingPanelClickListener != null) {
         mChattingPanelClickListener.OnSelectRedPacketRequest();
       }
    }
    @Override
  public void OnSelectTransferClick() {
    if (mChattingPanelClickListener != null) {
          mChattingPanelClickListener.OnSelectTransferRequest();
       }
    }
};
public interface OnChattingPanelClickListener {
   void OnTakingPictureRequest();
   void OnSelectImageReuqest();
   void OnSelectFileRequest();
   void OnSelectVoiceRequest();
   void OnSelectVideoRequest();
   void OnSelectFireMsg();
   void OnSelectLocationRequest();
   void OnSelectRedPacketRequest();
   void OnSelectTransferRequest();
}
```
## 发送红包
* 在ChattingFragment中点击红包入口打开发送红包页面
```java
private class OnOnChattingPanelImpl implements CCPChattingFooter2.OnChattingPanelClickListener {  
   @Override
   public void OnSelectRedPacketRequest() {//红包

      int type = RPConstant.RP_ITEM_TYPE_SINGLE;
      RedPacketInfo redPacketInfo = new RedPacketInfo();
      if (!isPeerChat()) {
          redPacketInfo.receiverId = mRecipients;
          redPacketInfo.receiverNickname = mUsername;
          redPacketInfo.receiverAvatarUrl = "none";//容联云没有头像url，开发者设置自己app的头像url
          redPacketInfo.chatType = 1;
          type = RPConstant.RP_ITEM_TYPE_SINGLE;
        } else {
          redPacketInfo.receiverId = mRecipients;
          redPacketInfo.receiverNickname = mUsername;
          redPacketInfo.receiverAvatarUrl = "none";//容联云没有头像url，开发者设置自己app的头像url
          redPacketInfo.chatType = 1;
          type = RPConstant.RP_ITEM_TYPE_SINGLE;
        }
       RPRedPacketUtil.getInstance().startRedPacket(getActivity(), type, redPacketInfo, new RPSendPacketCallback() {
         @Override
        public void onSendPacketSuccess(RedPacketInfo redPacketInfo) {
            handleSendRedPacketMessage(redPacketInfo);
         }
         @Override
         public void onGenerateRedPacketId(String s) {

         }
        });
        hideBottomPanel();
     }
}
```
* 在ChattingFragment中添加专属红包（不需要忽略）
```java
private void initSettings(String mRecipients) {
   if (isPeerChat()) {
       ECGroup ecGroup = GroupSqlManager.getECGroup(mRecipients);
       if (ecGroup != null) {
           setActionBarTitle(ecGroup.getName() != null ? ecGroup.getName() : ecGroup.getGroupId());
           SpannableString charSequence = setNewMessageMute(!ecGroup.isNotice());
           if (charSequence != null) {
               getTopBarView().setTitle(charSequence);
             }
          }
       //增加专属红包
       RedPacketUtil.getInstance().setGroupMember(ecGroup.getGroupId());
     }
}
```
## 拆红包
* 在ChattingListClickListener中拆红包
```java
public void onClick(View v) {
  ViewHolderTag holder = (ViewHolderTag) v.getTag();
  ECMessage iMessage = holder.detail;
  switch (holder.type) {
    case ViewHolderTag.TagType.TAG_IM_REDPACKET:
        JSONObject jsonRedPacket = RedPacketUtil.getInstance().isRedPacketMessage(iMessage);
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setCanceledOnTouchOutside(false);
        RedPacketInfo redPacketInfo = new RedPacketInfo();
        redPacketInfo.redPacketId = jsonRedPacket.optString(RPConstant.MESSAGE_ATTR_RED_PACKET_ID);
	redPacketInfo.redPacketType = jsonRedPacket.optString(RPConstant.MESSAGE_ATTR_RED_PACKET_TYPE);
	//mContext为FragmentActivity
	//钱包版
        RPRedPacketUtil.getInstance().openRedPacket(redPacketInfo, mContext, new RPRedPacketUtil.RPOpenPacketCallback() {
                  @Override
                    public void onSuccess(RedPacketInfo redPacketInfo) {
                        mContext.mChattingFragment.sendRedPacketAckMessage(redPacketInfo.senderId, redPacketInfo.senderNickname);

                    }
		    
                    @Override
                    public void showLoading() {
                        progressDialog.show();
                    }

                    @Override
                    public void hideLoading() {
                        progressDialog.hide();
                    }

                    @Override
                    public void onError(String code, String message) {
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                    }
                });
         //支付宝版
         RPRedPacketUtil.getInstance().openRedPacket(redPacketInfo.redPacketId,redPacketInfo.redPacketType, mContext, 
	          new    RPRedPacketUtil.RPOpenPacketCallback() {
		  
                    @Override
                    public void onSuccess(RedPacketInfo redPacketInfo) {
                        mContext.mChattingFragment.sendRedPacketAckMessage(redPacketInfo.senderId,redPacketInfo.senderNickname);
                    }

                    @Override
                    public void showLoading() {
                        progressDialog.show();
                    }

                    @Override
                    public void hideLoading() {
                        progressDialog.hide();
                    }

                    @Override
                    public void onError(String code, String message) {
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                    }
                });
              break;
            default:
                break;
      } 
}
```
## 发送转账（钱包版）
* 在ChattingFragment中点击转账入口打开发送转账页面
```java
private class OnOnChattingPanelImpl implements CCPChattingFooter2.OnChattingPanelClickListener {  
  @Override
    public void OnSelectTransferRequest() {
      RedPacketInfo redPacketInfo = new RedPacketInfo();
      redPacketInfo.receiverId = mRecipients;
      redPacketInfo.receiverNickname = mUsername;
      redPacketInfo.receiverAvatarUrl = "none";//容联云没有头像url，开发者设置自己app的头像url
      RPRedPacketUtil.getInstance().startRedPacket(getActivity(), RPConstant.RP_ITEM_TYPE_TRANSFER, redPacketInfo, new    RPSendPacketCallback() {
            @Override
            public void onSendPacketSuccess(RedPacketInfo redPacketInfo) {
                 handleSendTransferMessage(redPacketInfo);
            }
            @Override
            public void onGenerateRedPacketId(String s) {

           }
       });
}
```
## 进入转账详情页面（钱包版）
* 在ChattingListClickListener中打开转账详情页面
```java
public void onClick(View v) {
  ViewHolderTag holder = (ViewHolderTag) v.getTag();
  ECMessage iMessage = holder.detail;
  switch (holder.type) {
  case ViewHolderTag.TagType.TAG_IM_TRANSFER:
       //打开转账
        JSONObject jsonTransfer = RedPacketUtil.getInstance().isTransferMsg(iMessage);
        String amount = jsonTransfer.optString(RPConstant.MESSAGE_ATTR_TRANSFER_AMOUNT);//转账金额
        String time = jsonTransfer.optString(RPConstant.MESSAGE_ATTR_TRANSFER_TIME);//转账时间
        String messageDirect;
        if (iMessage.getDirection() == ECMessage.Direction.RECEIVE) {//接受者
               messageDirect = RPConstant.MESSAGE_DIRECT_RECEIVE;
           } else {//发送者
               messageDirect = RPConstant.MESSAGE_DIRECT_SEND;
        }
        RedPacketInfo data = new RedPacketInfo();
        data.messageDirect = messageDirect;
        data.redPacketAmount = amount;//转账金额
        data.transferTime = time;//转账时间
       RPRedPacketUtil.getInstance().openTransferPacket(mContext, data);
        break;
        default:
           break;
      }
}
```
## 回执消息
* 在IMChattingHelper中删除不是自己的回执消息
```java
private synchronized void postReceiveMessage(ECMessage msg,boolean showNotice) {
// 接收到的IM消息，根据IM消息类型做不同的处理
// IM消息类型：ECMessage.Type

 if (msg.getType() == Type.TXT) {
   if (!RedPacketUtil.getInstance().isMyAckMessage(msg, CCPAppManager.getClientUser().getUserId())) {
	IMessageSqlManager.delSingleMsg(msg.getMsgId());
	return;
   }
   JSONObject jsonObject = RedPacketUtil.getInstance().isRedPacketAckMessage(msg);
   if (jsonObject != null) {
    //改写回执红包消息的内容
    String currentUserId = CCPAppManager.getClientUser().getUserId();//当前登陆用户id
    String receiveUserId = jsonObject.optString(RPConstant.MESSAGE_ATTR_RED_PACKET_RECEIVER_ID);//红包接收者id
    String receiveUserNick = jsonObject.optString(RPConstant.MESSAGE_ATTR_RED_PACKET_RECEIVER_NICKNAME);//红包接收者昵称
    String sendUserId = jsonObject.optString(RPConstant.MESSAGE_ATTR_RED_PACKET_SENDER_ID);//红包发送者id
    String sendUserNick = jsonObject.optString(RPConstant.MESSAGE_ATTR_RED_PACKET_SENDER_NICKNAME);//红包发送者昵称
    String text = "";
    //发送者和领取者都是自己-
    if (currentUserId.equals(receiveUserId) && currentUserId.equals(sendUserId)) {
	text = CCPAppManager.getContext().getResources().getString(R.string.money_msg_take_money);
	} else if (currentUserId.equals(sendUserId)) {
	//我仅仅是发送者
       text = String.format(CCPAppManager.getContext().getResources().getString(R.string.money_msg_someone_take_money),receiveUserNick);
       } else if (currentUserId.equals(receiveUserId)) {
       //我仅仅是接收者
       text = String.format(CCPAppManager.getContext().getResources().getString(R.string.money_msg_take_someone_money), sendUserNick);
       }
	ECTextMessageBody msgBody = new ECTextMessageBody(text);
	msg.setBody(msgBody);
        //设置为不提醒
	showNotice = false;
    }
    //转账
    JSONObject transferObject = RedPacketUtil.getInstance().isTransferMsg(msg);
    if (transferObject != null) {
        //改写转账消息的内容
        String money = transferObject.optString(RPConstant.EXTRA_TRANSFER_AMOUNT);//转账金额
	String text = "[转账]向你转账" + money + "元";
	ECTextMessageBody msgBody = new ECTextMessageBody(text.toString());
	msg.setBody(msgBody);
	//设置为不提醒
	showNotice = true;
     }
}
   ...		
}
```
## 红包消息及回执消息处理
* 在ConversationAdapter中去掉会话列表里面的红包、回执消息显示发送者
```java
protected final CharSequence getConversationSnippet(Conversation conversation) {
      
 String fromNickName = "";
 if (conversation.getSessionId().toUpperCase().startsWith("G")) {
     if(conversation.getContactId() != null && CCPAppManager.getClientUser() != null
          && !conversation.getContactId().equals(CCPAppManager.getClientUser().getUserId())) {
          ECContacts contact = ContactSqlManager.getContact(conversation.getContactId());
          if (contact != null && contact.getNickname() != null) {
              fromNickName = contact.getNickname() + ": ";
          } else {
              fromNickName = conversation.getContactId() + ": ";
            }
         }
       //用字符串的方式的识别红包消息，改变会话页显示样式
       if (conversation.getMsgType() == ECMessage.Type.TXT.ordinal()) {
          String content = conversation.getContent();
       if ((content.contains("领取了") && content.contains("的红包")) || content.contains("红包消息")|| content.contains("容联云红包")) {

             fromNickName = "";
           }
       }
   }  
     return snippet;
}
```
## 进入零钱页（钱包版）
```java
RPRedPacketUtil.getInstance().startChangeActivity(getActivity());
```
## 进入红包记录页(支付宝版)
```java
RPRedPacketUtil.getInstance().startRecordActivity(context)
```
## 兼容Android7.0以上系统（钱包版）

* Android 7.0强制启用了被称作StrictMode的策略，带来的影响就是你的App对外无法暴露file://类型URI了。
* 如果你使用Intent携带这样的URI去打开外部App(比如：打开系统相机拍照)，那么会抛出FileUriExposedException异常。
* 由于钱包版SDK中有上传身份信息的功能，该功能调用了系统相机拍照，为了兼容Android 7.0以上系统，使用了FileProvider。
* 为保证红包SDK声明的FileProvider唯一，且不与其他应用中的FileProvider冲突，需要在App的build.gradle中增加resValue。

* 示例如下：
```java
defaultConfig {
  applicationId "your applicationId"
  minSdkVersion androidMinSdkVersion
  targetSdkVersion androidTargetSdkVersion
  resValue "string", "rp_provider_authorities_name","${applicationId}.FileProvider"
}
```
* 如果你的应用中也定义了FileProvider，会报合并清单文件错误，需要你在定义的FileProvider中添加tools:replace="android:authorities" 、 tools:replace="android:resource"

* 示例如下：

```java
<provider
   android:name="android.support.v4.content.FileProvider"
   tools:replace="android:authorities"
   android:authorities="包名.FileProvider"
   android:exported="false"
   android:grantUriPermissions="true">
   <meta-data
     android:name="android.support.FILE_PROVIDER_PATHS"
     tools:replace="android:resource"
     android:resource="@xml/rc_file_path" />
</provider>
```
## detachView接口

* RPRedPacketUtil.getInstance().detachView()

* 在拆红包方法所在页面销毁时调用，可防止内存泄漏。

* 调用示例(以ChatFragment为例)
```java
@Override
public void onDestroy() {
    super.onDestroy();
    RPRedPacketUtil.getInstance().detachView();
}
```
## 拆红包音效
* 在assets目录下添加open_packet_sound.mp3或者open_packet_sound.wav文件即可(文件大小不要超过1M)。
