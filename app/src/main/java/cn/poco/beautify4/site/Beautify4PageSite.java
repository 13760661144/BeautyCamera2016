package cn.poco.beautify4.site;

import android.content.Context;
import android.os.Handler;
import android.view.View;

import java.util.HashMap;

import cn.poco.beautify4.Beautify4Page;
import cn.poco.beautify4.UiMode;
import cn.poco.framework.AnimatorHolder;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;
import cn.poco.share.site.SharePageSite;

/**
 * Created by Raining on 2016/11/15.
 * 4.0美化页面,正常流程
 */
public class Beautify4PageSite extends BaseSite
{
	public CmdProc mCmdProc;

	public Beautify4PageSite()
	{
		super(SiteID.BEAUTY4);

		MakeCmdProc();
	}

	protected void MakeCmdProc()
	{
		mCmdProc = new CmdProc();
	}

	@Override
	public IPage MakePage(Context context)
	{
		return new Beautify4Page(context, this);
	}

	public void OnBack(Context context)
	{
		MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
	}

	/**
	 * @param params img:Bitmap/RotationImg2<br/>
	 */
	public void OnSave(Context context, HashMap<String, Object> params)
	{
		//MyFramework.SITE_Open(context, SharePageSite.class, params, Framework2.ANIM_NONE);
		MyFramework.SITE_Open(context, false, SharePageSite.class, params, new AnimatorHolder()
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
				}, 500);
			}
		});
	}

	/*public static void CloneOpenParams(HashMap<String, Object> dst, HashMap<String, Object> src)
	{
		if(src != null && dst != null)
		{
			dst.put(DataKey.BEAUTIFY_DEF_OPEN_PAGE, src.get(DataKey.BEAUTIFY_DEF_OPEN_PAGE));
			dst.put(DataKey.BEAUTIFY_DEF_SEL_URI, src.get(DataKey.BEAUTIFY_DEF_SEL_URI));
		}
	}*/

	public static class CmdProc extends HomePageSite.CmdProc
	{
		@Override
		public void OpenPage(Context context, int code, String... args)
		{
			IPage page = MyFramework.GetCurrentPage(context, Beautify4Page.class);
			if(page != null && page instanceof Beautify4Page)
			{
				int selUri = 0;
				if(args != null && args.length > 0)
				{
					try
					{
						selUri = Integer.parseInt(args[0]);
					}
					catch(Throwable e)
					{
						e.printStackTrace();
					}
				}
				switch(code)
				{
					case 0:
						((Beautify4Page)page).setSelect(UiMode.BEAUTIFY.GetValue(), selUri);
						return;
					case 1:
						((Beautify4Page)page).setSelect(UiMode.SHOUSHEN.GetValue(), selUri);
						return;
					case 2:
						((Beautify4Page)page).setSelect(UiMode.CAIZHUANG.GetValue(), selUri);
						return;
					case 3:
						((Beautify4Page)page).setSelect(UiMode.LVJING.GetValue(), selUri);
						return;
					case 4:
						((Beautify4Page)page).setSelect(UiMode.NORMAL.GetValue(), selUri);
						return;
					case 5:
						((Beautify4Page)page).setSelect(UiMode.CLIP.GetValue(), selUri);
						return;
					case 6:
						((Beautify4Page)page).setSelect(UiMode.XIANGKUANG.GetValue(), selUri);
						return;
					case 7:
						((Beautify4Page)page).setSelect(UiMode.TIETU.GetValue(), selUri);
						return;
					case 8:
						((Beautify4Page)page).setSelect(UiMode.MAOBOLI.GetValue(), selUri);
						return;
					case 9:
						((Beautify4Page)page).setSelect(UiMode.MASAIKE.GetValue(), selUri);
						return;
					case 10:
						((Beautify4Page)page).setSelect(UiMode.ZHIJIANMOFA.GetValue(), selUri);
						return;
					case 26:
						((Beautify4Page)page).setSelect(UiMode.QUDOU.GetValue(), selUri);
						return;
					case 27:
						((Beautify4Page)page).setSelect(UiMode.QUYANDAI.GetValue(), selUri);
						return;
					case 28:
						((Beautify4Page)page).setSelect(UiMode.LIANGYAN.GetValue(), selUri);
						return;
					case 29:
						((Beautify4Page)page).setSelect(UiMode.DAYAN.GetValue(), selUri);
						return;
					case 30:
						((Beautify4Page)page).setSelect(UiMode.GAOBILIANG.GetValue(), selUri);
						return;
					case 31:
						((Beautify4Page)page).setSelect(UiMode.WEIXIAO.GetValue(), selUri);
						return;
				}
			}
			super.OpenPage(context, code, args);
		}
	}
}
