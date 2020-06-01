package cn.poco.business;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.adnonstop.admasterlibs.data.AbsChannelAdRes;

import org.json.JSONObject;

import cn.poco.statistics.TongJi2;
import my.beautyCamera.R;

public class ActSignUpDialog extends Dialog
{

	public ActSignUpDialog(Context context, boolean cancelable, OnCancelListener cancelListener)
	{
		super(context, cancelable, cancelListener);
	}

	public ActSignUpDialog(Context context, int theme)
	{
		super(context, theme);
	}

	public ActSignUpDialog(Context context)
	{
		super(context);
	}

	public interface OkListener
	{
		void onOk(JSONObject postStr);
	}

	private OkListener mOkListener;
	private ActSignUpView mSignUpView;
	private Bitmap mBk;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		RelativeLayout container = new RelativeLayout(getContext());
		setContentView(container, params);
		if(mBk != null)
		{
			container.setBackgroundDrawable(new BitmapDrawable(mBk));
		}
		else
		{
			container.setBackgroundColor(0x80000000);
		}

		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		mSignUpView = new ActSignUpView(getContext());
		container.addView(mSignUpView, params);
		mSignUpView.mDialog = this;

		TongJi2.AddCountByRes(getContext(), R.integer.商业_介绍页_填资料);
	}

	public void setBk(Bitmap bk)
	{
		mBk = bk;
	}

	@Override
	public void dismiss()
	{
		mBk = null;
		super.dismiss();
	}

	public void setBusinessRes(AbsChannelAdRes res)
	{
		mSignUpView.setBusinessRes(res);
	}

	public void setBusinessRes(AbsChannelAdRes.GatePageData pageData)
	{
		mSignUpView.setBusinessRes(pageData);
	}

	public void setOkListener(OkListener listener)
	{
		mOkListener = listener;
	}

	public void onOk(JSONObject postStr)
	{
		if(mOkListener != null)
		{
			mOkListener.onOk(postStr);
		}
		cancel();
	}
}
