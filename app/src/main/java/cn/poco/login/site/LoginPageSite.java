package cn.poco.login.site;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.HashMap;

import cn.poco.framework.BaseSite;
import cn.poco.framework.FileCacheMgr;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;
import cn.poco.login.LoginAllAnim;
import cn.poco.login.LoginPage;
import cn.poco.login.LoginStyle;
import cn.poco.loginlibs.info.LoginInfo;
import cn.poco.utils.Utils;

//首页侧边栏登录进入
public class LoginPageSite extends BaseSite
{
	public LoginPageSite()
	{
		super(SiteID.LOGIN);
	}

	@Override
	public IPage MakePage(Context context)
	{
		return new LoginPage(context, this);
	}

	//点击进入选择国家和地区页面
	public void ChooseCountry(Context context)
	{
		MyFramework.SITE_Popup(context, ChooseCountryAreaCodePageSite.class, null, Framework2.ANIM_NONE);
	}

	//跳转到忘记密码的填写验证码页面
	public void LosePsw(HashMap<String, Object> datas,Context context)
	{
		HashMap<String, Object> params = new HashMap<>();
		String path = FileCacheMgr.GetLinePath();
		Bitmap bmp = (Bitmap)datas.get("img");
		if(Utils.SaveTempImg(bmp, path))
		{
			params.put("img", path);
		}
		params.put("info" ,datas.get("info"));
		MyFramework.SITE_Open(context,ResetLoginPswPageSite.class,params,Framework2.ANIM_NONE);
	}

	//跳转到注册流程的填写验证码页面
	public void creatAccount(HashMap<String, Object> datas,Context context)
	{
		HashMap<String, Object> params = new HashMap<>();
		String path = FileCacheMgr.GetLinePath();
		Bitmap bmp = (Bitmap)datas.get("img");
		if(Utils.SaveTempImg(bmp, path))
		{
			params.put("img", path);
		}
		MyFramework.SITE_Open(context,RegisterLoginPageSite.class,params,Framework2.ANIM_NONE);
	}


	//返回首页
	public void onBack(Context context)
	{
		LoginAllAnim.ReSetLoginAnimData();
		MyFramework.SITE_BackTo(context, HomePageSite.class,null,Framework2.ANIM_TRANSITION);
	}

	//登陆成功，回到首页
	public void loginSuccess(Context context)
	{
		LoginAllAnim.ReSetLoginAnimData();
		MyFramework.SITE_BackTo(context, HomePageSite.class, null, Framework2.ANIM_TRANSLATION_LEFT);
	}

	//第三方登陆没有绑定手机的，强制跳转到绑定手机页面
	public void thirdPartLoginOneStepFinish(Context context, LoginInfo info, LoginStyle.LoginBaseInfo baseInfo)
	{
		HashMap<String,Object> params = new HashMap<>();
		params.put("loginInfo",info);
		params.put("relogininfo",baseInfo);
		MyFramework.SITE_Open(context, BindPhonePageSite4.class,params,Framework2.ANIM_NONE);
	}
}
