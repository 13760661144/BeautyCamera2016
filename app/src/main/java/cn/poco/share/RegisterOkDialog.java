package cn.poco.share;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.RelativeLayout.LayoutParams;

import cn.poco.blogcore.PocoBlog;
import cn.poco.tianutils.ShareData;

public class RegisterOkDialog extends Dialog{

	public RegisterOkDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		initialize(context);
	}

	public RegisterOkDialog(Context context, int theme) {
		super(context, theme);
		initialize(context);
	}

	public RegisterOkDialog(Context context) {
		super(context);
		initialize(context);
	}
	
	protected RegisterOkFrame mContentView;
	protected ProgressDialog mProgressDialog = null;
	protected OnOkListener mOkListener;
	protected String   mStrAccount;
	protected String   mStrPsw;
	protected String   mStrId;

	protected void initialize(Context context)
	{
		this.setCanceledOnTouchOutside(false);
		LayoutParams params = new LayoutParams(ShareData.PxToDpi_hdpi(437), ShareData.PxToDpi_hdpi(370));
		mContentView = new RegisterOkFrame(getContext());
		mContentView.mDialog = this;
		setContentView(mContentView, params);
	}
	
	public void onLogin(final String strUserName, final String strPass)
	{
		cancel();
		if(strUserName == null && strPass == null)
			return;

		mProgressDialog = new ProgressDialog(getContext());
		mProgressDialog.setMessage("正在登录...");
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.show();
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				String strId;
				String strEmail;
				if(strUserName.indexOf('@') != -1)
				{
					strId = "";
					strEmail = strUserName;
				}
				else
				{
					strId = strUserName;
					strEmail = "";
				}
				PocoBlog mPoco = new PocoBlog(getContext());
				mPoco.login(strId, strEmail, strPass, false, new PocoBlog.OnLoginListener()
				{
					@Override
					public void onLogin(String result, String message, String pocoId, String nick)
					{
						mProgressDialog.cancel();
						AlertDialog dlg = new AlertDialog.Builder(getContext()).create();
						if(result == "ok")
						{
							dlg.setTitle("提示");
							dlg.setMessage("登录成功");
							dlg.setButton(BUTTON_POSITIVE, "确定", (DialogInterface.OnClickListener)null);
							dlg.show();
							if(mOkListener != null)
							{
								mOkListener.onOk();
							}
						}
						else
						{
							dlg.setTitle("提示");
							dlg.setMessage("登录失败," + message);
							dlg.setButton(BUTTON_POSITIVE, "确定", (DialogInterface.OnClickListener)null);
							dlg.show();
						}
					}
				});
			}
		}).start();
	}
	
	public void setAccountInfo(String account, String psw, String id)
	{
		mStrAccount = account;
		mStrPsw = psw;
		mStrId = id;
		mContentView.setAccountInfo(account, psw, id);
	}
	
	public void setOkListener(OnOkListener listener)
	{
		mOkListener = listener;
	}
	
	public interface OnOkListener
	{
		void onOk();
	}
}
