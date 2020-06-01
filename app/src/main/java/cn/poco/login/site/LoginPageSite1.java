package cn.poco.login.site;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.HashMap;

import cn.poco.framework.FileCacheMgr;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.login.LoginStyle;
import cn.poco.loginlibs.info.LoginInfo;
import cn.poco.utils.Utils;

//解锁界面登录
public class LoginPageSite1 extends LoginPageSite
{
	//关闭弹出的登陆页面
	@Override
	public void onBack(Context context)
	{
		MyFramework.SITE_ClosePopup(context, null, Framework2.ANIM_NONE);
	}

	//登陆成功，关闭弹出的登陆页面
	@Override
	public void loginSuccess(Context context)
	{
		MyFramework.SITE_ClosePopup(context, null, Framework2.ANIM_NONE);
	}

	@Override
	public void creatAccount(HashMap<String, Object> datas,Context context)
	{
		HashMap<String, Object> params = new HashMap<>();
		String path = FileCacheMgr.GetLinePath();
		Bitmap bmp = (Bitmap)datas.get("img");
		if(Utils.SaveTempImg(bmp, path))
		{
			params.put("img", path);
		}
		MyFramework.SITE_Open(context, RegisterLoginPageSite1.class, params, Framework2.ANIM_NONE);
	}


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
		MyFramework.SITE_Open(context,ResetLoginPswPageSite1.class,params,Framework2.ANIM_NONE);
	}

	@Override
	public void thirdPartLoginOneStepFinish(Context context, LoginInfo info,LoginStyle.LoginBaseInfo baseInfo) {
		HashMap<String,Object> params = new HashMap<>();
		params.put("loginInfo",info);
		params.put("relogininfo",baseInfo);
		MyFramework.SITE_Open(context, BindPhonePageSite5.class,params,Framework2.ANIM_NONE);
	}
}
