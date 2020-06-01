package cn.poco.home.home4.widget;

import android.app.Activity;
import android.os.Bundle;

import cn.poco.tianutils.FullScreenDlg;

/**
 * Created by lgd on 2017/5/18.
 */
abstract public class AbsAdDialog extends FullScreenDlg
{
	public AbsAdDialog(Activity activity)
	{
		super(activity);
	}

	public AbsAdDialog(Activity activity, int theme) {
		super(activity, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		initUi();
	}

	protected abstract void initUi();

	protected CallBack callBack;

	public void setCallBack(CallBack callBack)
	{
		this.callBack = callBack;
	}

	public interface CallBack
	{
		void onNo();

		void onYes();

		void onAnimationEnd();

		void onSkinStart();
	}
}
