package cn.poco.share.site;

import android.content.Context;
import android.os.Handler;
import android.view.View;

import java.util.HashMap;

import cn.poco.album.site.AlbumSite25;
import cn.poco.beautify4.site.Beautify4PageSite4;
import cn.poco.camera.CameraConfig;
import cn.poco.camera.CameraSetDataKey;
import cn.poco.camera.site.CameraPageSite;
import cn.poco.camera.site.CameraPageSite2;
import cn.poco.community.site.PublishOpusSite;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.home.home4.Home4Page;
import cn.poco.home.site.HomePageSite;
import cn.poco.login.site.BindPhonePageSite;
import cn.poco.login.site.LoginPageSite10;
import cn.poco.preview.site.PreviewImgPageSite;
import cn.poco.share.ShareBackAnimatorHolder;
import cn.poco.share.SharePage;

/**
 * 美颜到分享
 */
public class SharePageSite extends BaseSite
{
	public HomePageSite.CmdProc m_cmdProc;
	private Context m_context;

	public SharePageSite()
	{
		super(SiteID.SHARE);

		MakeCmdProc();
	}

	/**
	 * 注意构造函数调用
	 */
	protected void MakeCmdProc()
	{
		m_cmdProc = new HomePageSite.CmdProc();
	}

	@Override
	public IPage MakePage(Context context)
	{
		m_context = context;
		return new SharePage(context, this);
	}

	public void OnBack()
	{
//		MyFramework.SITE_Back(m_context, null, Framework2.ANIM_NONE);
		MyFramework.SITE_Back(m_context, null, BackHolder);
	}

	public void OnHome()
	{
		MyFramework.SITE_Open(m_context, true, HomePageSite.class, null, Framework2.ANIM_NONE);
	}

	public void onHome(){
		HashMap<String ,Object> param=new HashMap<>();
		param.put(Home4Page.KEY_CUR_MODE,Home4Page.CAMPAIGN);

		HashMap<String ,Object> data=new HashMap<>();
		data.put("openFriendPage",true);

		param.put(Home4Page.KEY_TOP_DATA,data);


		MyFramework.SITE_Open(m_context, true, HomePageSite.class, param, Framework2.ANIM_NONE);
	}

	public void OnCamera()
	{
		HashMap<String, Object> params = new HashMap<>();
		Class<? extends BaseSite> siteClass = CameraPageSite2.class;

		BaseSite baseSite = MyFramework.GetLinkSite(m_context, CameraPageSite.class);
		if (baseSite != null) {
			params = (HashMap<String, Object>) baseSite.m_inParams.clone();
		} else {
			CameraConfig.getInstance().initAll(m_context);
			params.put(CameraSetDataKey.KEY_START_MODE, CameraConfig.getInstance().getInt(CameraConfig.ConfigMap.LastCameraId));
			CameraConfig.getInstance().clearAll();
		}

		//记录 拍照-预览-保存的闪关灯模式，用于继续拍照流程
		if (m_inParams != null)
		{
			if (m_inParams.containsKey(CameraSetDataKey.KEY_CAMERA_FLASH_MODE))
			{
				params.put(CameraSetDataKey.KEY_CAMERA_FLASH_MODE, m_inParams.get(CameraSetDataKey.KEY_CAMERA_FLASH_MODE));
			}
			if (m_inParams.containsKey(CameraSetDataKey.KEY_CAMERA_FRONT_SPLASH_MASK))
			{
				params.put(CameraSetDataKey.KEY_CAMERA_FRONT_SPLASH_MASK, m_inParams.get(CameraSetDataKey.KEY_CAMERA_FRONT_SPLASH_MASK));
			}
		}
		MyFramework.SITE_Open(m_context, true, siteClass, params, Framework2.ANIM_NONE);
	}

	/**
	 * 从分享跳到美颜美化界面
	 *
	 * @param params img:RotationImg2
	 */
	public void OnBeautyFace(HashMap<String, Object> params)
	{
		HashMap<String, Object> temp = new HashMap<>();
		temp.put("imgs", params.get("img"));
		temp.put("only_one_pic", true);
		MyFramework.SITE_Open(m_context, Beautify4PageSite4.class, temp, Framework2.ANIM_NONE);
	}

	/**
	 * 分享到社区
	 * @param path 路径
	 * @param type 类型 1:图片 2:视频 3: gif
	 */
	public void onCommunity(Context context,String path,String content,int type){
		HashMap<String,Object> params=new HashMap<>();
		params.put("path",path);
		params.put("type",type);
		params.put("content",content);
		MyFramework.SITE_Popup(context, PublishOpusSite.class, params, Framework2.ANIM_NONE);
	}

	public void OnLogin(Context context) {
		MyFramework.SITE_Popup(context, LoginPageSite10.class, null, Framework2.ANIM_NONE);
	}

	public void onBindPhone(Context context){
		MyFramework.SITE_Popup(context, BindPhonePageSite.class, null, Framework2.ANIM_NONE);
	}

	public void onHome(Context context){
		HashMap<String ,Object> param=new HashMap<>();
		param.put(Home4Page.KEY_CUR_MODE,Home4Page.CAMPAIGN);

		HashMap<String ,Object> data=new HashMap<>();
		data.put("openFriendPage",true);

		param.put(Home4Page.KEY_TOP_DATA,data);


		MyFramework.SITE_Open(context, true, HomePageSite.class, param, Framework2.ANIM_NONE);
	}

	/**
	 * 打开图片预览
	 *
	 * @param picPath 图片本地路径
	 */
	public void OnPreview(String picPath)
	{
		HashMap<String, Object> params = new HashMap<>();
		params.put("img", picPath);
		MyFramework.SITE_Popup(m_context, PreviewImgPageSite.class, params, Framework2.ANIM_TRANSITION);
	}

	/**
	 * 编辑下一张
	 */
	public void OnBeautyFaceNext()
	{
		MyFramework.SITE_Popup(m_context, AlbumSite25.class, null, Framework2.ANIM_NONE);
	}

	private ShareBackAnimatorHolder BackHolder = new ShareBackAnimatorHolder()
	{
		@Override
		public void doAnimation(View oldView, View newView, final AnimatorListener lst)
		{
			lst.OnAnimationStart();
			Handler handler = new Handler();
			handler.postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					lst.OnAnimationEnd();
				}
			}, 1000);
		}
	};
}
