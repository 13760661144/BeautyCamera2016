package cn.poco.taskCenter.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;
import cn.poco.login.site.LoginPageSite5;
import cn.poco.taskCenter.TaskCenterPage;
import cn.poco.webview.site.WebViewPageSite2;

/**
 * 任务大厅
 */
public class TaskCenterPageSite extends BaseSite
{
	public CmdProc m_cmdProc;

	public TaskCenterPageSite()
	{
		super(SiteID.TASK_CENTER);

		MakeCmdProc();
	}

	/**
	 * 注意构造函数调用
	 */
	protected void MakeCmdProc()
	{
		m_cmdProc = new CmdProc();
	}

	@Override
	public IPage MakePage(Context context)
	{
		return new TaskCenterPage(context, this);
	}

	public void OnBack(Context context)
	{
		MyFramework.SITE_Back(context, null, Framework2.ANIM_TRANSLATION_LEFT);
	}

	public void OpenWebView(String url,Context context)
	{
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("url", url);
		MyFramework.SITE_Popup(context, WebViewPageSite2.class, params, Framework2.ANIM_NONE);
	}

	public void toLoginPage(Context context)
	{
		MyFramework.SITE_Open(context, LoginPageSite5.class, null, Framework2.ANIM_NONE);
	}

	public static class CmdProc extends HomePageSite.CmdProc
	{

	}
}
