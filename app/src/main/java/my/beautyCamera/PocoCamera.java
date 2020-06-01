package my.beautyCamera;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.webkit.WebView;
import android.widget.Toast;

import com.adnonstop.admasterlibs.AdUtils;
import com.adnonstop.admasterlibs.data.AbsBootAdRes;
import com.circle.utils.dn.DnFile;
import com.taotie.circle.CommunityLayout;
import com.taotie.circle.Constant;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.adMaster.BootAd;
import cn.poco.adMaster.HomeAd;
import cn.poco.album.PhotoStore;
import cn.poco.album.site.AlbumSite;
import cn.poco.beautify4.site.Beautify4PageSite;
import cn.poco.beautify4.site.Beautify4PageSite100;
import cn.poco.bootimg.site.BootImgPageSite;
import cn.poco.camera.BrightnessUtils;
import cn.poco.camera.CameraSetDataKey;
import cn.poco.camera.site.CameraPageSite100;
import cn.poco.camera.site.CameraPageSite102;
import cn.poco.camera.site.CameraPageSite200;
import cn.poco.cloudAlbum.TransportImgs;
import cn.poco.framework.AnimatorHolder;
import cn.poco.framework.BaseFwActivity;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.MyFramework2App;
import cn.poco.framework2.Framework2;
import cn.poco.home.home4.Home4Page;
import cn.poco.home.home4.introAnimation.Config;
import cn.poco.home.site.HomePageSite;
import cn.poco.resource.ResourceMgr;
import cn.poco.setting.SettingInfo;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.statistics.TongJi2;
import cn.poco.system.FolderMgr;
import cn.poco.system.SysConfig;
import cn.poco.system.TagMgr;
import cn.poco.tianutils.ImageUtils;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.site.activity.MainActivitySite;

import static cn.poco.home.home4.Home4Page.KEY_CUR_MODE;

/**
 * Created by Raining on 2017/8/30.
 * 支持多activity
 */

public class PocoCamera extends BaseFwActivity<MainActivitySite>
{
	protected boolean mQuit = false;

	@Override
	protected void InitStaticOnce(@Nullable Bundle savedInstanceState)
	{
		super.InitStaticOnce(savedInstanceState);

		ShareData.InitData(this);
		//设置下载器参数
		DnFile.setDefaultCachePath(FolderMgr.getInstance().IMAGE_CACHE_PATH);
		DnFile.setDefaultCacheSize(Constant.WEBCACHESIZE);
		DnFile.setDefaultThreadPool(3);

		//获取一次user agent
		try
		{
			WebView view = new WebView(this);
			AdUtils.USER_AGENT = view.getSettings().getUserAgentString();
			//System.out.println(sUA);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}

		//冲印删除缓存
		try
		{
			File file = new File(FolderMgr.getInstance().PRINTER_PATH);
			if(file.exists())
			{
				FileUtils.deleteDirectory(file);
			}
			file.mkdirs();
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}

		//加载一次素材
		ResourceMgr.PreInit(this.getApplicationContext());
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				ResourceMgr.Init(PocoCamera.this);
			}
		}).start();
	}

	@Override
	protected void InitData(@Nullable Bundle savedInstanceState)
	{
		super.InitData(savedInstanceState);

		if(mSite == null)
		{
			mSite = new MainActivitySite();
		}

		getWindow().setFormat(PixelFormat.TRANSLUCENT);//解决打开SurfaceView时闪屏的bug

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);

        /*IPage page = GetTopPage();
        if (page != null && page instanceof Home4Page) {
            //首页内单独处理虚拟键问题

        } else {
            if (hasFocus) {
                //ShareData.hideStatusAndNavigation(this);
            }
        }*/
	}

	@Override
	protected void InitFinal(@Nullable Bundle savedInstanceState)
	{
		super.InitFinal(savedInstanceState);

		try
		{
			TongJi2.IAmLive(this);

			TransportImgs.sLoadTransportInfo = true;

			getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, mContentObserver);
			//读取相册的数据，注意，不能放在Application里面初始化，因为涉及语言问题
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					PhotoStore.getInstance(PocoCamera.this).initFolderInfos(false);
				}
			}).start();

			//商业全局统计
			HomeAd.SendGlobalAdTj(this);

			//初始化社区
			CommunityLayout.init(this);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected void onAppMapGate(Context context, Bundle savedInstanceState, boolean newActivity)
	{
		Intent intent = getIntent();
		if(intent != null)
		{
			String action = intent.getAction();
			if(action != null)
			{
				if(action.equals(Intent.ACTION_EDIT) || action.equals(Intent.ACTION_VIEW) || action.equals(Intent.ACTION_SEND))
				{
					String path = null;
					Uri uri = intent.getData();
					if(uri == null)
					{
						Bundle bundle = intent.getExtras();
						if(bundle != null)
						{
							Object obj = bundle.get(Intent.EXTRA_STREAM);
							if(obj instanceof Uri)
							{
								uri = (Uri)obj;
							}
						}
					}
					if(uri != null)
					{
						//web版打开社区协议头
						if("beautycamerasns".equals(uri.getScheme()))
						{
							//直接打开社区
							HashMap<String, Object> temp = new HashMap<>();
							temp.put(Home4Page.KEY_COMMUNITY_URI, uri);
							temp.put(KEY_CUR_MODE, Home4Page.CAMPAIGN);
							SITE_Open(context, true, HomePageSite.class, temp, Framework2.ANIM_NONE);
							return;
						}
						if("beautycamera".equals(uri.getScheme()))
						{
							String open = uri.getQueryParameter("open");
							if(open != null && open.length() > 0)
							{
								//打开首页传命令进去
								HashMap<String, Object> temp = new HashMap<>();
								temp.put("cmd", "beautycamera://open=" + open + "&source=third");
								SITE_Open(context, true, HomePageSite.class, temp, Framework2.ANIM_NONE);
								return;
							}
						}
						else
						{
							if(uri.toString().startsWith("file:"))
							{
								path = uri.getPath();
							}
							else
							{
								Cursor c = getContentResolver().query(uri, null, null, null, null);
								if(c != null)
								{
									if(c.moveToFirst())
									{
										int id = c.getColumnIndex(MediaStore.Images.Media.DATA);
										if(id != -1)
										{
											path = c.getString(id);
										}
									}
									c.close();
								}
							}
						}
					}
					if(path != null && ImageUtils.IsImageFile(path))
					{
						if(action.equals(Intent.ACTION_SEND))
						{
							//分享当正常流程直接进入美化
							HashMap<String, Object> temp = new HashMap<String, Object>();
							temp.put("imgs", AlbumSite.MakeRotationImg(new String[]{path}, true));
							temp.put("only_one_pic", true);
							SITE_Open(context, true, Beautify4PageSite.class, temp, Framework2.ANIM_NONE);
							return;
						}
						else
						{
							//第三方调用,直接进入美化
							HashMap<String, Object> temp = new HashMap<String, Object>();
							temp.put(MyFramework.EXTERNAL_CALL_TYPE, MyFramework.EXTERNAL_CALL_TYPE_EDIT);
							temp.put("imgs", AlbumSite.MakeRotationImg(new String[]{path}, true));
							temp.put("only_one_pic", true);
							SITE_Open(context, true, Beautify4PageSite100.class, temp, Framework2.ANIM_NONE);
							return;
						}
					}
					else
					{
						Toast.makeText(getApplicationContext(), "无效的图片文件", Toast.LENGTH_LONG).show();
					}
				}
				else if(action.equals(MediaStore.ACTION_IMAGE_CAPTURE) || action.equals(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA))
				{
					Uri uri = null;
					Bundle bundle = intent.getExtras();
					if(bundle != null)
					{
						uri = bundle.getParcelable(MediaStore.EXTRA_OUTPUT);
					}
					//第三方调用,直接去拍照
					HashMap<String, Object> temp = new HashMap<>();
					temp.put(MyFramework.EXTERNAL_CALL_TYPE, MyFramework.EXTERNAL_CALL_TYPE_CAMERA);
					if(uri != null)
					{
						temp.put(MyFramework.EXTERNAL_CALL_IMG_SAVE_URI, uri);
					}
					temp.putAll(CameraSetDataKey.GetExternalTakePicture());
					SITE_Open(context, true, CameraPageSite100.class, temp, Framework2.ANIM_NONE);
					return;
				}
				else if(action.equals(MediaStore.ACTION_VIDEO_CAPTURE))
				{
					Uri uri = null;
					Bundle bundle = intent.getExtras();
					if(bundle != null)
					{
						uri = bundle.getParcelable(MediaStore.EXTRA_OUTPUT);
					}
					//第三方调用,直接去录视频
					HashMap<String, Object> temp = new HashMap<>();
					temp.put(MyFramework.EXTERNAL_CALL_TYPE, MyFramework.EXTERNAL_CALL_TYPE_CAMERA_VIDEO);
					if(uri != null)
					{
						temp.put(MyFramework.EXTERNAL_CALL_IMG_SAVE_URI, uri);
					}
					temp.putAll(CameraSetDataKey.GetExternalTakeVideo());
					SITE_Open(context, true, CameraPageSite102.class, temp, Framework2.ANIM_NONE);
					return;
				}
			}
		}

		if(newActivity)
		{
			ArrayList<BaseSite> arr = mFramework.GetCurrentSiteList();
			if(arr != null && arr.size() > 0)
			{
				mFramework.onCreate(context, savedInstanceState);
			}
			else
			{
				//直接打开拍照
				SettingInfo info = SettingInfoMgr.GetSettingInfo(context);
				if(info.GetOpenCameraState())
				{
					HashMap<String, Object> params = new HashMap<String, Object>();
					params.put(CameraSetDataKey.KEY_START_MODE, 1);
					SITE_Open(context, true, CameraPageSite200.class, params, Framework2.ANIM_NONE);
				}
				else
				{
					HashMap<String, Object> params = new HashMap<>();
					AbsBootAdRes res = BootAd.GetOneBootRes(context);
					if(res != null)
					{
						params.put("boot_img", res);
					}
					params.put("auto_close", true);
					params.put("open_anim", true);
					params.put("has_market_logo", true);
					SITE_Open(context, true, BootImgPageSite.class, params, null);
				}
			}
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldW, int oldH)
	{
		super.onSizeChanged(w, h, oldW, oldH);

		ShareData.InitData(PocoCamera.this, true);
		Config.initData();
	}

	@Override
	protected void onPause()
	{
		SysConfig.Save(this);
		TagMgr.Save(this);
		SettingInfoMgr.Save(this);

		super.onPause();
	}

	@Override
	protected void onDestroy()
	{
		getContentResolver().unregisterContentObserver(mContentObserver);
		TongJi2.AddOnlineDeadCount(this, null);
		if(BrightnessUtils.getInstance() != null)
		{
			BrightnessUtils.getInstance().clearAll();
		}
		super.onDestroy();

		if(mQuit)
		{
			MyFramework2App.getInstance().quit();
			System.exit(0);
		}
	}

	@Override
	public void onBackPressed()
	{
		IPage page = GetTopPage();
		if(page != null)
		{
			page.onBack();
		}
	}

	@Override
	public void SITE_Open(Context context, Class<? extends BaseSite> siteClass, HashMap<String, Object> params, int animType)
	{
		boolean newLink = false;
		//System.out.println("String是Object的父类:" + String.class.isAssignableFrom(Object.class)); //false
		//System.out.println("Object是String的父类:" + Object.class.isAssignableFrom(String.class)); //true
		//System.out.println("Object和Object相同:" + Object.class.isAssignableFrom(Object.class)); //true
		if(HomePageSite.class.isAssignableFrom(siteClass))
		{
			newLink = true;
		}
		super.SITE_Open(context, newLink, siteClass, params, animType);
	}

	@Override
	public void SITE_OpenAndClosePopup(Context context, Class<? extends BaseSite> siteClass, HashMap<String, Object> params, int animType)
	{
		boolean newLink = false;
		if(HomePageSite.class.isAssignableFrom(siteClass))
		{
			newLink = true;
		}
		super.SITE_OpenAndClosePopup(context, newLink, siteClass, params, animType);
	}

	@Override
	public void SITE_BackTo(Context context, Class<? extends BaseSite> siteClass, HashMap<String, Object> params, int animType)
	{
		int currentIndex = mFramework.GetCurrentIndex();
		ArrayList<BaseSite> siteList = mFramework.GetCurrentSiteList();
		if(currentIndex == 0 && (siteList == null || siteList.size() < 2))
		{
			//退出软件
			mQuit = true;
			finish();
		}
		else
		{
			super.SITE_BackTo(context, siteClass, params, animType);
		}
	}

	@Override
	public void SITE_BackTo(Context context, Class<? extends BaseSite> siteClass, HashMap<String, Object> params, AnimatorHolder holder)
	{
		int currentIndex = mFramework.GetCurrentIndex();
		ArrayList<BaseSite> siteList = mFramework.GetCurrentSiteList();
		if(currentIndex == 0 && (siteList == null || siteList.size() < 2))
		{
			//退出软件
			mQuit = true;
			finish();
		}
		else
		{
			super.SITE_BackTo(context, siteClass, params, holder);
		}
	}

	private ContentObserver mContentObserver = new ContentObserver(null)
	{
		@Override
		public void onChange(boolean selfChange, Uri uri)
		{
			PhotoStore.getInstance(PocoCamera.this).clearCache();
			if(!PhotoStore.getInstance(PocoCamera.this).isLoading())
			{
				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						PhotoStore.getInstance(PocoCamera.this).initFolderInfos(true);
					}
				}).start();
			}
		}
	};
}
