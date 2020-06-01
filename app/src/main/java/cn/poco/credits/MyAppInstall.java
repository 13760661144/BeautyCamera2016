package cn.poco.credits;

import android.content.Context;

import cn.poco.scorelibs.AppName;
import cn.poco.scorelibs.IAppInstall;
import my.beautyCamera.R;

/**
 * Created by Raining on 2017/8/29.
 */

public class MyAppInstall implements IAppInstall
{
	@Override
	public void OnInstall(Context context, String pkgName, AppName name)
	{
		switch(name)
		{
			case MRXJ:
				Credit.CreditIncome(context, context.getResources().getInteger(R.integer.积分_下载_美人相机) + "");
				break;
			case JP:
				Credit.CreditIncome(context, context.getResources().getInteger(R.integer.积分_下载_简拼) + "");
				break;
			case JK:
				Credit.CreditIncome(context, context.getResources().getInteger(R.integer.积分_下载_简客) + "");
				break;
			case YX:
				break;
			case QZXJ:
				Credit.CreditIncome(context, context.getResources().getInteger(R.integer.积分_下载_亲子相机) + "");
				break;
			case TPHCQ:
				Credit.CreditIncome(context, context.getResources().getInteger(R.integer.积分_下载_图片合成器) + "");
				break;
			case XNXJ:
				break;
			case ZYQ:
				break;
			case XMRE:
				break;
			case POCO:
				Credit.CreditIncome(context, context.getResources().getInteger(R.integer.积分_下载_POCO相机) + "");
				break;
		}
	}
}
