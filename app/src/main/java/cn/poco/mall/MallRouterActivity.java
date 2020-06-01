package cn.poco.mall;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.adnonstop.beautymall.constant.BeautyUser;
import com.adnonstop.beautymall.constant.KeyConstant;
import com.adnonstop.beautymall.ui.activities.FlashSaleActivity;
import com.adnonstop.beautymall.ui.activities.ProjectDetailsActivity;
import com.adnonstop.beautymall.ui.activities.goodsDetails.GoodsDetailsActivity;
import com.adnonstop.beautymall.ui.activities.homepage.NewBeautyMallHomeActivity;
import com.adnonstop.hzbeautycommonlib.Constant.CommonConstant;

import java.util.List;

import cn.poco.framework.RequestCode;
import cn.poco.login.UserMgr;
import cn.poco.setting.SettingInfoMgr;
import my.beautyCamera.BaseActivity;
import my.beautyCamera.PocoCamera;
import my.beautyCamera.R;

/**
 * Created by Raining on 2017/7/19.
 */

public class MallRouterActivity extends BaseActivity
{
	public static final int REQUEST_CODE = RequestCode.MALL_ROUTER;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		initData();
	}

	@Override
	protected void onRestart()
	{
		super.onRestart();
		if(KeyConstant.isFromWeb)
		{
			initData();
		}
		KeyConstant.isFromWeb = true;
	}

	protected void initData()
	{
		//android.os.Debug.waitForDebugger();
		BeautyUser.userId = null;
		BeautyUser.telNumber = null;
		if(UserMgr.IsLogin(this, null))
		{
			BeautyUser.userId = SettingInfoMgr.GetSettingInfo(this).GetPoco2Id(false);
		}
		String telNum = null;
		if(UserMgr.IsLogin(this, null))
		{
			telNum = SettingInfoMgr.GetSettingInfo(this).GetPoco2Phone();
		}
		if(telNum != null && telNum.length() > 4)
		{
			BeautyUser.telNumber = telNum;
		}

//        //区分是去专题详情还是商品详情
		Intent intent = getIntent();
		Bundle bundle = new Bundle();
		boolean b = isExistMainActivity(PocoCamera.class);

		if(Intent.ACTION_VIEW.equals(intent.getAction()))
		{
			Uri data = intent.getData();
			String goodsId = data.getQueryParameter("goodsId");
			String topicId = data.getQueryParameter("topicId");
			String HomePageIdentify = data.getQueryParameter("HomePageIdentify");
			String activityId = data.getQueryParameter("activityId");
			if(goodsId != null && goodsId.length() != 0)
			{
				//跳转到商品详情的方法
				try
				{
					bundle.putLong(KeyConstant.GOODS_ID, Long.parseLong(goodsId.trim()));
				}
				catch(Throwable e)
				{
					e.printStackTrace();
				}
				goToActivity(GoodsDetailsActivity.class, bundle);

			}
			if(topicId != null && topicId.length() != 0)
			{
				//跳转到专题详情的方法
				try
				{
					bundle.putLong(KeyConstant.TOPIC_ID, Long.parseLong(topicId.trim()));
				}
				catch(Throwable e)
				{
					e.printStackTrace();
				}
				goToActivity(ProjectDetailsActivity.class, bundle);
			}
			if(HomePageIdentify != null && HomePageIdentify.length() != 0)
			{
				Intent beautyMallHomePageActivityIntent = new Intent(this, NewBeautyMallHomeActivity.class);
				startActivityForResult(beautyMallHomePageActivityIntent, REQUEST_CODE);
			}
			if(activityId != null && activityId.length() != 0)
			{
				Intent flashSaleActivityIntent = new Intent(this, FlashSaleActivity.class);
				flashSaleActivityIntent.putExtra(CommonConstant.ACTIVITYID, activityId);
				startActivityForResult(flashSaleActivityIntent, REQUEST_CODE);
			}
			if(b)
			{
				finish();
			}

		}

	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(REQUEST_CODE == requestCode)
		{
			if(!isExistMainActivity(PocoCamera.class))
			{
				gotoHome();
			}
		}
		finish();
	}

	private boolean isExistMainActivity(Class<?> cls)
	{
		Intent intent = new Intent(this, cls);
		ComponentName cmpName = intent.resolveActivity(getPackageManager());
		boolean flag = false;
		if(cmpName != null)
		{ // 说明系统中存在这个activity
			ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
			List<ActivityManager.RunningTaskInfo> taskInfoList = am.getRunningTasks(10);
			for(ActivityManager.RunningTaskInfo taskInfo : taskInfoList)
			{
				Log.e("jj", "taskInfo: " + taskInfo.baseActivity + "\t" + taskInfo.topActivity + "\t" + taskInfo.numRunning);
				if(taskInfo.baseActivity.equals(cmpName))
				{
					// 说明它已经启动了
					//判断栈内有几个activity，如果只有一个代表新启动的activity
					//if(taskInfo.numRunning != 1)
					//{
					flag = true;
					break;  //跳出循环，优化效率
					//}
				}
			}
		}
		return flag;
	}

	public void goToActivity(Class activity, Bundle bundle)
	{
		Intent intent = new Intent(this, activity);
		if(bundle != null && bundle.size() != 0)
		{
			intent.putExtra(KeyConstant.BASE_DATA, bundle);
		}
		startActivityForResult(intent, REQUEST_CODE);
		overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
	}

	public void gotoHome()
	{
		startActivity(new Intent(this, PocoCamera.class));
	}
}
