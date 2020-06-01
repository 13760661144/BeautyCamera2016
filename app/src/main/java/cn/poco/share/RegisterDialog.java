package cn.poco.share;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout.LayoutParams;

import cn.poco.blogcore.PocoBlog;
import cn.poco.statistics.TongJi2;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

public class RegisterDialog extends Dialog
{
	public RegisterDialog(Context context, int theme)
	{
		super(context, theme);
		mContext = context;
	}
	
	private Context mContext;
	protected RegisterFrame mContentView;
	protected OnRegisterOkListener mRegisterOkListener = null;
	protected ProgressDialog mProgressDialog = null;
	protected String mStrAccount;
	protected String mStrPsw;
	protected String mStrId;
	private Bitmap mBackground;

	private boolean loading = false;

	protected void initialize(Context context, Bitmap background)
	{
		this.setCanceledOnTouchOutside(false);
		LayoutParams params = new LayoutParams(ShareData.m_screenWidth, ShareData.m_screenHeight);
		mContentView = new RegisterFrame(context);
		if(background != null && !background.isRecycled()) mContentView.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), background));
		else mContentView.setBackgroundColor(0xb2ffffff);
		mContentView.mDialog = this;
		setContentView(mContentView, params);
		SharePage.initBlogConfig(getContext());
		getWindow().setWindowAnimations(R.style.pocoLoginDialogAnimation);
	}
	
	@Override
	protected void onStop() {
		mContentView.clear();
		super.onStop();
	}

	@Override
	public void dismiss()
	{
		if(mProgressDialog != null)
		{
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
		mRegisterOkListener = null;
		super.dismiss();
	}

	public void onRegister(final String strUserName, final String strPass)
	{
		mStrAccount = strUserName;
		mStrPsw = strPass;
//		mProgressDialog = new ProgressDialog(mContext);
//		mProgressDialog.setMessage("注册中...");
//		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//		mProgressDialog.show();

		loading = true;
		mContentView.setLoading(loading);
		TongJi2.AddCountByRes(mContext, R.integer.分享_登录_注册);
		final Handler handler = new Handler();
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				PocoBlog mPoco = new PocoBlog(mContext);
				mPoco.registerPocoId(strUserName, strPass, new PocoBlog.OnRegisterPocoIdListener()
				{
					@Override
					public void onRegisterPocoId(final String result, final String message, final String pocoId)
					{
						handler.post(new Runnable()
						{
							@Override
							public void run()
							{
//								mProgressDialog.cancel();
								loading = false;
								mContentView.setLoading(loading);
								if(result != null && result.equals("ok"))
								{
									TongJi2.AddCountByRes(mContext, R.integer.分享_登录_注册_成功);
									mStrId = pocoId;
									if(mRegisterOkListener != null)
									{
										mRegisterOkListener.onRegisterOk(mStrAccount, mStrPsw, mStrId, getDialogBackgound());
									}
									cancel();
								}
								else
								{
//									AlertDialog dlg = new AlertDialog.Builder(mContext).create();
//									dlg.setTitle("提示");
//									dlg.setMessage("注册失败,"+message);
//									dlg.setButton(AlertDialog.BUTTON_NEGATIVE, "确定", new OnClickListener()
//									{
//										@Override
//										public void onClick(DialogInterface dialog, int which) {
////											RegisterDialog.this.show();
//										}
//									});
//									dlg.show();
									final Dialog dlg = new Dialog(mContext, R.style.notitledialog);
									dlg.setContentView(LoginDialog.dialogView(mContext, mContext.getResources().getString(R.string.pocologin_register_fail) + "\n" + message, getDialogBackgound(), new View.OnClickListener()
									{
										@Override
										public void onClick(View v)
										{
											dlg.dismiss();
										}
									}));
									dlg.setCanceledOnTouchOutside(false);
									dlg.show();
								}
							}
						});
					}
				});
			}
		}).start();
	}

	private Bitmap getDialogBackgound()
	{
		if(mBackground == null || mBackground.isRecycled())
		{
			Bitmap bmp = Bitmap.createBitmap(ShareData.m_screenWidth, ShareData.m_screenHeight, Bitmap.Config.ARGB_8888);
			mContentView.draw(new Canvas(bmp));
			mBackground = SharePage.makeGlassBackground(bmp);
		}
		return mBackground;
	}

	public void setOnRegisterOkListener(OnRegisterOkListener listener)
	{
		mRegisterOkListener = listener;
	}
	
	public interface OnRegisterOkListener
	{
		void onRegisterOk(String account, String pass, String id, Bitmap bg);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(event.getRepeatCount() == 0)
		{
			switch(keyCode)
			{
				case KeyEvent.KEYCODE_BACK:
					if(loading)	return true;
					break;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
