package cn.poco.statistics;

import android.content.Context;
import android.support.annotation.StringRes;

import com.baidu.mobstat.StatService;

/**
 * Created by: fwc
 * Date: 2017/4/7
 */
public class TongJiUtils {

	public static void onPageStart(Context context, @StringRes int res) {
		String tag = context.getResources().getString(res);
		StatService.onPageStart(context, tag);
		TongJi2.StartPage(context, tag);
	}

	public static void onPageEnd(Context context, @StringRes int res) {
		String tag = context.getResources().getString(res);
		StatService.onPageEnd(context, tag);
		TongJi2.EndPage(context, tag);
	}

	public static void onPageResume(Context context, @StringRes int res) {
		TongJi2.OnResume(context, context.getResources().getString(res));
	}

	public static void onPagePause(Context context, @StringRes int res) {
		TongJi2.OnPause(context, context.getResources().getString(res));
	}
}
