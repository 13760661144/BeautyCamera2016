package cn.poco.share;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

public class LoginFrame extends FrameLayout
{
	public LoginFrame(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context);
	}

	public LoginFrame(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	public LoginFrame(Context context) {
		super(context);
		initialize(context);
	}
	
	protected FrameLayout mBtnLogin;
	protected ImageView mBtnClose;
	protected TextView mBtnRegister;
	protected TextView	mTxTitle;
	protected EditText mAccount;
	protected EditText mPsw;
	protected ImageView mShowPsw;
	protected ImageView mLoginBG;
	protected TextView mLoginText;
	protected LinearLayout mBtnUseOtherAccount;
	protected ImageView mBtnQzone;
	protected ImageView mBtnSina;
	protected ImageView mLoading;
	protected String mStrDefaultAccount = getContext().getResources().getString(R.string.pocologin_login_hint_account);
	protected String mStrDefaultPsw = getContext().getResources().getString(R.string.pocologin_hint_password);

	private boolean showPsw = false;
	private boolean accountInput = false;
	private boolean passwordInput = false;
	private boolean loginEnable = false;
	private boolean loading = false;

	public LoginDialog mDialog = null;

	protected void setLoading(boolean loading)
	{
		this.loading = loading;
		if(loading)
		{
			mLoginText.setVisibility(View.GONE);
			mLoading.setVisibility(View.VISIBLE);
			mAccount.setEnabled(false);
			mPsw.setEnabled(false);
			LoginDialog.rotateAnime(mLoading);
		}
		else
		{
			mLoginText.setVisibility(View.VISIBLE);
			mLoading.clearAnimation();
			mLoading.setVisibility(View.GONE);
			mAccount.setEnabled(true);
			mPsw.setEnabled(true);
		}
	}

	protected void initialize(Context context)
	{
		FrameLayout.LayoutParams params;
		LinearLayout.LayoutParams ll;

		ImageView top_bg = new ImageView(context);
		top_bg.setBackgroundColor(Color.WHITE);
		params = new LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(626));
		params.gravity = Gravity.LEFT | Gravity.TOP;
		addView(top_bg, params);

		mBtnClose = new ImageView(context);
		mBtnClose.setImageResource(R.drawable.framework_back_btn);
		params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.LEFT | Gravity.TOP;
		params.topMargin = ShareData.PxToDpi_xhdpi(5);
		params.leftMargin = ShareData.PxToDpi_xhdpi(2);
		addView(mBtnClose, params);
		ImageUtils.AddSkin(getContext(), mBtnClose);
		mBtnClose.setOnTouchListener(new OnAnimationClickListener()
		{
			@Override
			public void onAnimationClick(View v)
			{
				if(loading) return;
				if(mDialog != null) mDialog.cancel();
			}

			@Override
			public void onTouch(View v){}

			@Override
			public void onRelease(View v){}
		});

		mTxTitle = new TextView(context);
		mTxTitle.setTextColor(0xff333333);
		mTxTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
		mTxTitle.setText(getContext().getResources().getString(R.string.pocologin_login_title));
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
		params.topMargin = ShareData.PxToDpi_xhdpi(87);
		addView(mTxTitle, params);

		LinearLayout accountFrame = new LinearLayout(context);
		accountFrame.setOrientation(LinearLayout.HORIZONTAL);
		params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.topMargin = ShareData.PxToDpi_xhdpi(184);
		params.leftMargin = ShareData.PxToDpi_xhdpi(28);
		addView(accountFrame, params);
		{
			ImageView icon = new ImageView(context);
			icon.setImageResource(R.drawable.beauty_login_name_logo);
			ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			ll.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
			accountFrame.addView(icon, ll);

			mAccount = new EditText(context);
			mAccount.setBackgroundDrawable(null);
			mAccount.setSingleLine();
			mAccount.setHint(mStrDefaultAccount);
			mAccount.setTextColor(0xff000000);
			mAccount.setHintTextColor(0xffb2b2b2);
			mAccount.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			mAccount.setHint(mStrDefaultAccount);
			mAccount.setGravity(Gravity.CENTER_VERTICAL);
			mAccount.setPadding(ShareData.PxToDpi_xhdpi(14), 0, 0, 0);
			ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(485), ShareData.PxToDpi_xhdpi(90));
			ll.gravity = Gravity.LEFT | Gravity.TOP;
			accountFrame.addView(mAccount, ll);
			mAccount.addTextChangedListener(new TextWatcher()
			{
				@Override
				public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
				{
					int length = mAccount.getText().length();
					if(length > 0) accountInput = true;
					else accountInput = false;
					checkButtonClickEnable();
				}

				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

				@Override
				public void afterTextChanged(Editable arg0) {}
			});
		}

		ImageView line1 = new ImageView(context);
		line1.setBackgroundColor(0xffececec);
		params = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.topMargin = ShareData.PxToDpi_xhdpi(274);
		addView(line1, params);

		LinearLayout passwordFrame = new LinearLayout(context);
		passwordFrame.setOrientation(LinearLayout.HORIZONTAL);
		params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.topMargin = ShareData.PxToDpi_xhdpi(274);
		params.leftMargin = ShareData.PxToDpi_xhdpi(28);
		addView(passwordFrame, params);
		{
			ImageView icon = new ImageView(context);
			icon.setImageResource(R.drawable.beauty_login_comfir_psw);
			ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			ll.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
			passwordFrame.addView(icon, ll);

			mPsw = new EditText(context);
			mPsw.setBackgroundDrawable(null);
			mPsw.setSingleLine();
			mPsw.setTextColor(0xff000000);
			mPsw.setHintTextColor(0xffb2b2b2);
			mPsw.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			mPsw.setHint(mStrDefaultPsw);
			mPsw.setGravity(Gravity.CENTER_VERTICAL);
			mPsw.setPadding(ShareData.PxToDpi_xhdpi(14), 0, 0, 0);
			mPsw.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(485), ShareData.PxToDpi_xhdpi(90));
			ll.gravity = Gravity.LEFT | Gravity.TOP;
			passwordFrame.addView(mPsw, ll);
			mPsw.addTextChangedListener(new TextWatcher()
			{
				@Override
				public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
				{
					int length = mPsw.getText().length();
					if(length > 0) passwordInput = true;
					else passwordInput = false;
					checkButtonClickEnable();
				}

				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

				@Override
				public void afterTextChanged(Editable arg0) {}
			});

			mShowPsw = new ImageView(context);
			mShowPsw.setImageResource(R.drawable.userinfo_psw_hide_out);
			ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			ll.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
			ll.leftMargin = ShareData.PxToDpi_xhdpi(76);
			passwordFrame.addView(mShowPsw, ll);
			mShowPsw.setOnClickListener(mClickListener);
		}

		ImageView line2 = new ImageView(context);
		line2.setBackgroundColor(0xffececec);
		params = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.topMargin = ShareData.PxToDpi_xhdpi(364);
		addView(line2, params);

		mBtnLogin = new FrameLayout(context);
		params = new LayoutParams(ShareData.PxToDpi_xhdpi(540), ShareData.PxToDpi_xhdpi(78));
		params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
		params.topMargin = ShareData.PxToDpi_xhdpi(444);
		mBtnLogin.setOnClickListener(mClickListener);
		addView(mBtnLogin, params);
		{
			mLoginBG = new ImageView(getContext());
			mLoginBG.setImageResource(R.drawable.share_bindpoco_enable_bg);
			mLoginBG.setScaleType(ImageView.ScaleType.FIT_XY);
			params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			params.gravity = Gravity.LEFT | Gravity.TOP;
			mBtnLogin.addView(mLoginBG, params);

			mLoginText = new TextView(context);
			mLoginText.setTextColor(Color.WHITE);
			mLoginText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			mLoginText.setText(getContext().getResources().getString(R.string.pocologin_login_button));
			params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER;
			mBtnLogin.addView(mLoginText, params);

			mLoading = new ImageView(getContext());
			mLoading.setImageResource(R.drawable.beauty_login_loading_logo);
			mLoading.setScaleType(ImageView.ScaleType.FIT_XY);
			params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER;
			mBtnLogin.addView(mLoading, params);
			mLoading.setVisibility(View.GONE);
		}

		mBtnRegister = new TextView(context);
		int color = ImageUtils.GetSkinColor(0xffe75988);
		mBtnRegister.setTextColor(color);
		mBtnRegister.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
		mBtnRegister.setText(getContext().getResources().getString(R.string.pocologin_free_registration));
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
		params.topMargin = ShareData.PxToDpi_xhdpi(560);
		mBtnRegister.setOnClickListener(mClickListener);
		addView(mBtnRegister, params);

		LinearLayout dividingLine = new LinearLayout(context);
		dividingLine.setOrientation(LinearLayout.HORIZONTAL);
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		params.bottomMargin = ShareData.PxToDpi_xhdpi(239);
		addView(dividingLine, params);
		{
			ImageView line3 = new ImageView(context);
			line3.setBackgroundColor(0x4dffffff);
			ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(268), 1);
			ll.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
			dividingLine.addView(line3, ll);

			TextView text = new TextView(context);
			text.setTextColor(0xcdffffff);
			text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
			text.setText("or");
			ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			ll.gravity = Gravity.TOP | Gravity.LEFT;
			ll.leftMargin = ShareData.PxToDpi_hdpi(20);
			dividingLine.addView(text, ll);

			ImageView line4 = new ImageView(context);
			line4.setBackgroundColor(0x4dffffff);
			ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(268), 1);
			ll.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
			ll.leftMargin = ShareData.PxToDpi_hdpi(20);
			dividingLine.addView(line4, ll);
		}

		mBtnUseOtherAccount = new LinearLayout(context);
		mBtnUseOtherAccount.setOrientation(LinearLayout.HORIZONTAL);
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		params.bottomMargin = ShareData.PxToDpi_xhdpi(64);
		addView(mBtnUseOtherAccount, params);
		{
			mBtnQzone = new ImageView(context);
			mBtnQzone.setImageResource(R.drawable.share_bindpoco_qzone_out);
			ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mBtnUseOtherAccount.addView(mBtnQzone, ll);
			mBtnQzone.setOnClickListener(mClickListener);

			mBtnSina = new ImageView(context);
			mBtnSina.setImageResource(R.drawable.share_bindpoco_sina_out);
			ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			ll.leftMargin = ShareData.PxToDpi_hdpi(53);
			mBtnUseOtherAccount.addView(mBtnSina, ll);
			mBtnSina.setOnClickListener(mClickListener);
		}
	}
	
	public void clear()
	{
		mBtnRegister.setOnClickListener(null);
		mBtnClose.setOnClickListener(null);
		mBtnLogin.setOnClickListener(null);
		mAccount.addTextChangedListener(null);
		mPsw.addTextChangedListener(null);
		removeAllViews();
	}
	
	public void hideOtherBlogButton()
	{
		mBtnUseOtherAccount.setVisibility(View.GONE);
	}
	
	protected OnClickListener mClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if(loading) return;

			if(v == mBtnLogin)
			{
				String account = mAccount.getText().toString();
				account = account.trim();
				String pass = mPsw.getText().toString();
				AlertDialog dlg = new AlertDialog.Builder(getContext()).create();
				dlg.setButton(AlertDialog.BUTTON_POSITIVE, getContext().getResources().getString(R.string.ensure), (DialogInterface.OnClickListener)null);
				if(account.length() <= 0 || account.equals(mStrDefaultAccount))
				{
					dlg.setTitle(getContext().getResources().getString(R.string.tips));
					dlg.setMessage(getContext().getResources().getString(R.string.pocologin_invalid_account));
					dlg.show();
					return;
				}
				if(pass.length() <= 0 || pass.equals(mStrDefaultPsw))
				{
					dlg.setTitle(getContext().getResources().getString(R.string.tips));
					dlg.setMessage(getContext().getResources().getString(R.string.pocologin_invalid_password));
					dlg.show();
					return;
				}
				if(mDialog != null)
				{
					int pos = account.indexOf('@');
					if(pos != -1)
					{
						String s1 = account.substring(0, pos);
						String s2 = account.substring(pos);
						account = s1 + s2.toLowerCase();
					}
					mDialog.onLogin(account, pass);
				}
			}
			else if(v == mBtnRegister)
			{
				if(mDialog != null)
				{
					mDialog.onRegister();
				}
			}
			else if(v == mBtnQzone)
			{
				if(mDialog != null)
				{
					mDialog.useOtherAccount(SharePage.QZONE);
				}
			}
			else if(v == mBtnSina)
			{
				if(mDialog != null)
				{
					mDialog.useOtherAccount(SharePage.SINA);
				}
			}
			else if(v == mShowPsw)
			{
				if(showPsw)
				{
					showPsw = false;
					mPsw.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
					mShowPsw.setImageResource(R.drawable.userinfo_psw_hide_out);
				}
				else
				{
					showPsw = true;
					mPsw.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					mShowPsw.setImageResource(R.drawable.userinfo_psw_show_out);
				}
				mPsw.setSelection(mPsw.getText().length());
			}
		}
	};

	private void checkButtonClickEnable()
	{
		if(accountInput && passwordInput)
		{
			if(!loginEnable)
			{
				loginEnable = true;
				mLoginBG.setImageResource(R.drawable.photofactory_noface_help_btn);
				ImageUtils.AddSkin(getContext(), mLoginBG);
			}
		}
		else
		{
			if(loginEnable)
			{
				loginEnable = false;
				mLoginBG.setImageResource(R.drawable.share_bindpoco_enable_bg);
			}
		}
	}
}
