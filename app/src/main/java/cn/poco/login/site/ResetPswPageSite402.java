package cn.poco.login.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.login.LoginAllAnim;

/**
 * 社区到注册，注册返回登录，登录到重置密码或第三方登录强制绑定手机的手机验证码页，到设置新密码页
 */

public class ResetPswPageSite402 extends ResetPswPageSite
{
	@Override
	public void successBind(HashMap<String,Object> datas, Context context)
	{
		LoginAllAnim.ReSetLoginAnimData();
		MyFramework.SITE_ClosePopup(context, null, Framework2.ANIM_NONE);
	}
}
