package cn.poco.share;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.blogcore.PocoBlog;
import cn.poco.framework.MyFramework2App;
import cn.poco.statistics.TongJi2;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

public class LoginDialog extends Dialog
{
	public LoginDialog(Context context, int theme) 
	{
		super(context, theme);
		mContext = context;
	}
	
	private Context mContext;
	protected LoginFrame mContentView;
	protected OnLoginOkListener mLoginOkListener = null;
	protected ProgressDialog mProgressDialog = null;
	protected String mStrAccount;
	protected String mStrPsw;
	protected String mStrId;
	private Bitmap mBackground;
	private Bitmap mLoginBackground;

	private boolean loading = false;

	public void initialize(Bitmap background)
	{
		this.setCanceledOnTouchOutside(false);
		TongJi2.AddCountByRes(mContext, R.integer.分享_登录);
		mBackground = background;
		LayoutParams params = new LayoutParams(ShareData.m_screenWidth, ShareData.m_screenHeight);
		mContentView = new LoginFrame(mContext);
		if(mBackground != null && !mBackground.isRecycled()) mContentView.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), mBackground));
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
		mLoginOkListener = null;
		mBackground = null;
		System.gc();
		super.dismiss();
	}

	public void onRegister()
	{
		RegisterDialog regDlg = new RegisterDialog(mContext, R.style.notitledialog);
		regDlg.initialize(mContext, mBackground);
		regDlg.setOnRegisterOkListener(new RegisterDialog.OnRegisterOkListener() 
		{
			@Override
			public void onRegisterOk(String account, String psw, String id, Bitmap bg) {
//					mStrAccount = account;
//					mStrPsw = psw;
//					mStrId = id;
//					RegisterOkDialog regOkDlg = new RegisterOkDialog(mContext, R.style.dialog);
//					regOkDlg.setAccountInfo(mStrAccount, mStrPsw, mStrId);
//					regOkDlg.setOkListener(new RegisterOkDialog.OnOkListener()
//					{
//						@Override
//						public void onOk() {
//							if(mLoginOkListener != null)
//							{
//								mLoginOkListener.onLoginOk(SharePage.POCO, mStrAccount, mStrPsw, mStrId, null);
//							}
//						}
//					});
//					regOkDlg.show();

				final Dialog dlg = new Dialog(mContext, R.style.dialog);
				dlg.setContentView(registerOkDialog(account, bg, new View.OnClickListener()
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
		});
		regDlg.show();
		cancel();
	}

	
	public void onLogin(String strUserName, final String strPass)
	{
		final String strId;
		final String strEmailOrUnm;
		char b = strUserName.charAt(0);
		char e = strUserName.charAt(strUserName.length()-1);
		if(b >= 0x30 && b <= 0x39 && e >= 0x30 && e <= 0x39)
		{
			strId = strUserName;
			strEmailOrUnm = "";
			mStrAccount = strId;
		}
		else
		{
			strId = "";
			strEmailOrUnm = strUserName;
			mStrAccount = strEmailOrUnm;
		}
//		mProgressDialog = new ProgressDialog(mContext);
//		mProgressDialog.setMessage("正在登录...");
//		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//		mProgressDialog.show();
		mStrPsw = strPass;
		loading = true;
		mContentView.setLoading(loading);

		final Handler handler = new Handler();
		new Thread(new Runnable()
		{	
			@Override
			public void run() 
			{
				PocoBlog mPoco = new PocoBlog(mContext);
				mPoco.login(strId, strEmailOrUnm, strPass, false, new PocoBlog.OnLoginListener()
				{
					@Override
					public void onLogin(final String result, final String message, final String pocoId, final String nick) 
					{
						handler.post(new Runnable()
						{	
							@Override
							public void run()
							{
//								mProgressDialog.cancel();
								loading = false;
								mContentView.setLoading(loading);
								if(MyFramework2App.getInstance().getActivity() != null && MyFramework2App.getInstance().getActivity().isFinishing())
									return;
								if(result == "ok")
								{
									mStrId = pocoId;
									if(mLoginOkListener != null)
									{
										mLoginOkListener.onLoginOk(SharePage.POCO, mStrAccount, mStrPsw, mStrId, nick);
									}
									cancel();
								}
								else
								{
									final Dialog dlg = new Dialog(mContext, R.style.notitledialog);
									dlg.setContentView(dialogView(mContext, mContext.getResources().getString(R.string.pocologin_login_fail) + "\n" + message, getDialogBackgound(), new View.OnClickListener()
									{
										@Override
										public void onClick(View v)
										{
											dlg.dismiss();
										}
									}));
//									dlg.setOnDismissListener(new OnDismissListener()
//									{
//										@Override
//										public void onDismiss(DialogInterface dialog)
//										{
//											LoginDialog.this.show();
//										}
//									});
									dlg.setCanceledOnTouchOutside(false);
									dlg.show();
								}
							}
						});
					}
				});
			}
		}).start();;
		
//		hide();
	}

	public static View dialogView(Context context, String content, Bitmap bg, View.OnClickListener listener)
	{
		FrameLayout.LayoutParams fl;
		LinearLayout.LayoutParams ll;

		FrameLayout mainLayout = new FrameLayout(context);
		if(bg != null && !bg.isRecycled()) mainLayout.setBackgroundDrawable(new BitmapDrawable(context.getResources(), bg));
		else mainLayout.setBackgroundColor(0xb2ffffff);
		fl = new FrameLayout.LayoutParams(ShareData.m_screenWidth, ShareData.m_screenHeight);
		fl.gravity = Gravity.LEFT | Gravity.TOP;
		mainLayout.setLayoutParams(fl);

		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setBackgroundResource(R.drawable.share_dialog_bg);
		fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(570), ViewGroup.LayoutParams.WRAP_CONTENT);
		fl.gravity = Gravity.CENTER;
		mainLayout.addView(layout, fl);
		{
			TextView text = new TextView(context);
			text.setTextColor(0xff333333);
			text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			text.setText(content);
			text.setGravity(Gravity.CENTER);
			text.setLineSpacing(ShareData.PxToDpi_xhdpi(14), 1f);
			ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(418), FrameLayout.LayoutParams.WRAP_CONTENT);
			ll.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
			ll.topMargin = ShareData.PxToDpi_hdpi(40);
			layout.addView(text, ll);

			FrameLayout button = new FrameLayout(context);
			ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(418), ShareData.PxToDpi_xhdpi(78));
			ll.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
			ll.topMargin = ShareData.PxToDpi_xhdpi(32);
			ll.bottomMargin = ShareData.PxToDpi_xhdpi(60);
			ll.leftMargin = ShareData.PxToDpi_xhdpi(76);
			ll.rightMargin = ShareData.PxToDpi_xhdpi(76);
			button.setOnClickListener(listener);
			layout.addView(button, ll);
			{
				ImageView button_bg = new ImageView(context);
				button_bg.setImageResource(R.drawable.photofactory_noface_help_btn);
				button_bg.setScaleType(ImageView.ScaleType.FIT_XY);
				fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
				fl.gravity = Gravity.LEFT | Gravity.TOP;
				button.addView(button_bg, fl);
				ImageUtils.AddSkin(context, button_bg);

				TextView button_text = new TextView(context);
				button_text.setTextColor(Color.WHITE);
				button_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				button_text.setText(context.getResources().getString(R.string.ensure));
				TextPaint paint = button_text.getPaint();
				paint.setFakeBoldText(true);
				fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
				fl.gravity = Gravity.CENTER;
				button.addView(button_text, fl);
			}
		}
		return mainLayout;
	}

	private View registerOkDialog(String account, Bitmap bg, View.OnClickListener listener)
	{
		FrameLayout.LayoutParams fl;
		LinearLayout.LayoutParams ll;

		FrameLayout mainLayout = new FrameLayout(mContext);
		if(bg != null && !bg.isRecycled()) mainLayout.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), bg));
		else mainLayout.setBackgroundColor(0xb2ffffff);
		fl = new FrameLayout.LayoutParams(ShareData.m_screenWidth, ShareData.m_screenHeight);
		fl.gravity = Gravity.LEFT | Gravity.TOP;
		mainLayout.setLayoutParams(fl);

		LinearLayout layout = new LinearLayout(mContext);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setBackgroundResource(R.drawable.share_dialog_bg);
		fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(570), ViewGroup.LayoutParams.WRAP_CONTENT);
		fl.gravity = Gravity.CENTER;
		mainLayout.addView(layout, fl);
		{
			TextView title = new TextView(mContext);
			title.setTextColor(0xff333333);
			title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
			title.setText(mContext.getResources().getString(R.string.pocologin_register_success));
			title.setGravity(Gravity.CENTER);
			ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			ll.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
			ll.topMargin = ShareData.PxToDpi_hdpi(36);
			layout.addView(title, ll);

			TextView text = new TextView(mContext);
			text.setTextColor(0xff333333);
			text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
			text.setText(mContext.getResources().getString(R.string.pocologin_register_account_effective)+"\n"+account);
			text.setGravity(Gravity.CENTER);
			text.setLineSpacing(ShareData.PxToDpi_xhdpi(14), 1f);
			ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(418), FrameLayout.LayoutParams.WRAP_CONTENT);
			ll.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
			ll.topMargin = ShareData.PxToDpi_hdpi(10);
			layout.addView(text, ll);

			FrameLayout button = new FrameLayout(mContext);
			ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(418), ShareData.PxToDpi_xhdpi(78));
			ll.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
			ll.topMargin = ShareData.PxToDpi_xhdpi(34);
			ll.leftMargin = ShareData.PxToDpi_xhdpi(76);
			ll.rightMargin = ShareData.PxToDpi_xhdpi(76);
			button.setOnClickListener(listener);
			layout.addView(button, ll);
			{
				ImageView button_bg = new ImageView(mContext);
				button_bg.setImageResource(R.drawable.photofactory_noface_help_btn);
				button_bg.setScaleType(ImageView.ScaleType.FIT_XY);
				fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
				fl.gravity = Gravity.LEFT | Gravity.TOP;
				button.addView(button_bg, fl);
				ImageUtils.AddSkin(mContext, button_bg);

				TextView button_text = new TextView(mContext);
				button_text.setTextColor(Color.WHITE);
				button_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				button_text.setText(mContext.getResources().getString(R.string.ensure));
				TextPaint paint = button_text.getPaint();
				paint.setFakeBoldText(true);
				fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
				fl.gravity = Gravity.CENTER;
				button.addView(button_text, fl);
			}

			TextView mTxHelp = new TextView(mContext);
			mTxHelp.setTextColor(0xff999999);
			mTxHelp.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
			mTxHelp.setText(mContext.getResources().getString(R.string.pocologin_login_on_network));
			ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			ll.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
			ll.topMargin = ShareData.PxToDpi_hdpi(14);
			ll.bottomMargin = ShareData.PxToDpi_xhdpi(26);
			layout.addView(mTxHelp, ll);
		}
		return mainLayout;
	}

	public static void rotateAnime(View view)
	{
		if(view == null) return;

		RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rotate.setDuration(1000);
		rotate.setRepeatCount(Animation.INFINITE);
		view.startAnimation(rotate);
	}

	private Bitmap getDialogBackgound()
	{
		if(mLoginBackground == null || mLoginBackground.isRecycled())
		{
			Bitmap bmp = Bitmap.createBitmap(ShareData.m_screenWidth, ShareData.m_screenHeight, Bitmap.Config.ARGB_8888);
			mContentView.draw(new Canvas(bmp));
			mLoginBackground = SharePage.makeGlassBackground(bmp);
		}
		return mLoginBackground;
	}

	/**
	 * 使用其他账号登录Poco
	 * @param type 账号类型
	 */
	public void useOtherAccount(int type)
	{
		if(mLoginOkListener != null)
		{	
			mLoginOkListener.onLoginOk(type, null, null, null, null);
		}
		cancel();
	}
	
	public void setOnLoginOkListener(OnLoginOkListener listener)
	{
		mLoginOkListener = listener;
	}
	
	public interface OnLoginOkListener
	{
		void onLoginOk(int type, String account, String psw, String id, String nick);
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
