package cn.poco.community.site;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.util.HashMap;

import cn.poco.album.AlbumPage;
import cn.poco.album.site.AlbumSite80;
import cn.poco.banner.BannerCore3;
import cn.poco.camera.CameraSetDataKey;
import cn.poco.camera.site.CameraPageSite;
import cn.poco.camera.site.CameraPageSite300;
import cn.poco.camera3.config.shutter.ShutterConfig;
import cn.poco.campaignCenter.model.CampaignInfo;
import cn.poco.community.CommunityPage;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.BaseActivitySite;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;
import cn.poco.login.site.BindPhonePageSite;
import cn.poco.login.site.RegisterLoginPageSite402;
import cn.poco.login.site.ResetLoginPswPageSite;
import cn.poco.login.site.activity.LoginActivitySite;
import cn.poco.loginlibs.info.LoginInfo;

/**
 * 用户发布图片详情site
 * 用于显示用户发布的作品的site
 * Created by lgh on 2017/7/13.
 */
public class CommunitySite extends BaseSite
{
	public HomePageSite.CmdProc m_cmdProc;
	public final static String SCROLL_POSITION = "scrollPosition";
	public final static String ITEM_OPEN_INDEX = "itemOpenIndex";

	/**
	 * 派生类必须实现一个XXXSite()的构造函数
	 *
	 */
	public CommunitySite()
	{
		super(SiteID.COMMUNITY_SDK);
		MakeCmdProc();
	}

	@Override
	public IPage MakePage(Context context)
	{
		return new CommunityPage(context, this);
	}

	public void onBack(Context context) {
		MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
	}


	public void onClickCampaignItem(Context context, CampaignInfo campaignInfo) {
		BannerCore3.ExecuteCommand(context, campaignInfo.getOpenUrl(), m_cmdProc, campaignInfo);
	}

	public void onRestoreState(Context context) {
		if (m_myParams != null && m_myParams.containsKey(ITEM_OPEN_INDEX)) {
			Object value = m_myParams.get(ITEM_OPEN_INDEX);
			m_myParams.remove(ITEM_OPEN_INDEX);
			if (value instanceof CampaignInfo) {
				CampaignInfo lastCampaignInfo = (CampaignInfo) value;
				BannerCore3.ExecuteCommand(context, lastCampaignInfo.getOpenUrl(), m_cmdProc, lastCampaignInfo);
			}
		}
	}


	public void clearItemIndexMemory() {
		if (m_myParams != null && m_myParams.containsKey(ITEM_OPEN_INDEX)) {
			m_myParams.remove(ITEM_OPEN_INDEX);
		}
	}

	/**
	 * 打开忘记密码页面
	 * web版打开
	 * @param context
	 */
	public void openResetPwdPage(Context context){
		MyFramework.SITE_Popup(context, ResetLoginPswPageSite.class, null, Framework2.ANIM_NONE);
	}


	/**
	 * 社区参加活动调用
	 * 根据协议url进行后续操作
	 * @param context
	 * @param url 协议url
	 */
	public void onJoinActivity(Context context,String url)
	{
		BannerCore3.ExecuteCommand(context, url, m_cmdProc,"community");
	}
	/**
	 * 打开协议url
	 * 正常打开协议url
	 * @param context
	 * @param url 协议url
	 */
	public void openUrl(Context context,String url)
	{
		BannerCore3.ExecuteCommand(context, url, m_cmdProc);
	}


	/**
	 * 登录
	 * @param maskBmp
	 */
	public void onLogin(Context context,String maskBmp)
	{
		if(context != null)
		{
			Intent intent = new Intent();
			intent.putExtra("type", "login");
			BaseActivitySite.setClass(intent, context, LoginActivitySite.class);
			((Activity)context).startActivityForResult(intent, LoginActivitySite.REQUEST_CODE);
			((Activity)context).overridePendingTransition(0,0);
		}
	}

	/**
	 * 打开注册页面
	 * @param context
	 */
	public void onRegistration(Context context)
	{
		if(context!=null)
		{
			MyFramework.SITE_Popup(context, RegisterLoginPageSite402.class, null, Framework2.ANIM_NONE);

		}
	}

	/**
	 * 绑定手机
	 * @param lInfo
	 */
	public void onBindPhone(Context context,LoginInfo lInfo){
		HashMap<String, Object> datas = new HashMap<>();
		datas.put("loginInfo", lInfo);
//		datas.put("img", maskBmp);
		datas.put("isHide", false);
		MyFramework.SITE_Popup(context, BindPhonePageSite.class, datas, Framework2.ANIM_TRANSLATION_LEFT);
	}

	/**
	 * 打开相册
	 * @param params
	 */
	public void openPhotoPicker(Context context,HashMap<String, Object> params) {
		if (params == null) {
			params = new HashMap<>();
		}else{
			if(params.containsKey("mode")){
				if("single".equals(params.get("mode"))){
					params.put("mode", AlbumPage.SINGLE);
				}else{
					params.put("mode",AlbumPage.REPEAT);
				}
			}
		}
		params.put("from_camera", true);
		params.put("min",1);
		params.put("max",9);
		MyFramework.SITE_Popup(context, AlbumSite80.class, params, Framework2.ANIM_TRANSLATION_BOTTOM);
	}

	/**
	 * 打开镜头 不需要美化,必须是popup
	 * 完成后在onPageResult返回路径
	 * @param params type 镜头类型  默认是拍照 type=image是拍照  type=video是拍视频
	 */
	public void openCamera(Context context,HashMap<String,Object> params)
	{
		if(params==null){
			params=new HashMap<>();
		}
		// TODO: 2017/9/12 调用镜头
		if(params.containsKey("type") && "video".equals(params.get("type").toString())){
			//视频
			params.put("from_camera", true);
			params.put("isOnlyOneMode", true);
			params.put(CameraSetDataKey.KEY_HIDE_PHOTO_PICKER_BTN, true);
			params.put(CameraSetDataKey.KEY_CAMERA_SHOW_TAB_TYPE, ShutterConfig.TabType.VIDEO);
			params.put(CameraSetDataKey.KEY_CAMERA_SELECT_TAB_TYPE, ShutterConfig.TabType.VIDEO);
			params.put(CameraSetDataKey.KEY_VIDEO_RECORD_TIME_TYPE, ShutterConfig.VideoDurationType.DURATION_TEN_SEC);
			params.put(CameraSetDataKey.KEY_VIDEO_RECORD_MIN_DURATION, 2000L);
			params.put(CameraSetDataKey.KEY_VIDEO_MULTI_SECTION_ENABLE, false);
			params.put(CameraSetDataKey.KEY_IS_SHOW_STICKER_SELECTOR, true);
			MyFramework.SITE_Popup(context, CameraPageSite300.class, params, Framework2.ANIM_TRANSLATION_TOP);
		}else if(params.containsKey("type") && "all".equals(params.get("type").toString())){
			//所有
			params.put("from_camera", true);
			params.put(CameraSetDataKey.KEY_HIDE_PHOTO_PICKER_BTN, true);
			params.put(CameraSetDataKey.KEY_IS_FILTER_BEAUTITY_PROCESS, true);
//			params.put(CameraSetDataKey.KEY_FILTER_BEAUTITY_PROCESS_MASK, FilterBeautifyProcessor.FILTER_BEAUTY);
//			params.put(CameraSetDataKey.KEY_CAMERA_SHOW_TAB_TYPE, ShutterConfig.TabType.PHOTO | ShutterConfig.TabType.CUTE |ShutterConfig.TabType.VIDEO |ShutterConfig.TabType.GIF);
			params.put(CameraSetDataKey.KEY_CAMERA_SELECT_TAB_TYPE, ShutterConfig.TabType.PHOTO);
			params.put(CameraSetDataKey.KEY_START_MODE, 1);
			params.put(CameraSetDataKey.KEY_VIDEO_RECORD_TIME_TYPE, ShutterConfig.VideoDurationType.DURATION_TEN_SEC);
			params.put(CameraSetDataKey.KEY_VIDEO_RECORD_MIN_DURATION, 2000L);
			params.put(CameraSetDataKey.KEY_VIDEO_MULTI_SECTION_ENABLE, false);
//			params.put(CameraSetDataKey.KEY_IS_SHOW_STICKER_SELECTOR, true);
			params.put(CameraSetDataKey.KEY_CAMERA_SHOW_TAB_TYPE, ShutterConfig.ALL_TYPE);
			MyFramework.SITE_Popup(context, CameraPageSite300.class, params, Framework2.ANIM_TRANSLATION_TOP);
		}else{
			//拍照
			params.put("from_camera", true);
			params.put(CameraSetDataKey.KEY_HIDE_PHOTO_PICKER_BTN, true);
			//params.put(CameraSetDataKey.KEY_IS_FILTER_BEAUTITY_PROCESS, true);
			//params.put(CameraSetDataKey.KEY_FILTER_BEAUTITY_PROCESS_MASK, FilterBeautifyProcessor.FILTER_BEAUTY);
			params.put(CameraSetDataKey.KEY_CAMERA_SHOW_TAB_TYPE, ShutterConfig.TabType.PHOTO | ShutterConfig.TabType.CUTE);
			params.put(CameraSetDataKey.KEY_CAMERA_SELECT_TAB_TYPE, ShutterConfig.TabType.PHOTO);
			MyFramework.SITE_Popup(context, CameraPageSite300.class, params, Framework2.ANIM_TRANSLATION_TOP);
		}
	}

	/**
	 * 社区有新消息
	 */
	public void onSystemMessage(int type, int count){
		// TODO: 2017/9/12 社区有新消息时回调
	}

	/**
	 * 社区有新消息
	 */
	public void onChatMessage(int type, int count){
		// TODO: 2017/9/12 社区有新消息时回调
	}

	public void onCommunityMessage(int type, int count) {

	}

	public void openPageByMessageType(int type) {

	}


	protected void MakeCmdProc()
	{
		m_cmdProc = new HomePageSite.CmdProc();
	}

}
