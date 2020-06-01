package cn.poco.arWish;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.text.method.PasswordTransformationMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.circle.utils.Utils;

import cn.poco.advanced.ImageUtils;
import cn.poco.tianutils.FullScreenDlg;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.CommonUI;
import my.beautyCamera.R;

/**
 * Created by Anson on 2018/1/23.
 */

public class FindWishInputPass extends FullScreenDlg
{
	private EditText mPWInput;
	private TextView mBtn1Text;
	private FrameLayout mBtn1;
	private TextView mBtn2Text;
	private FrameLayout mBtn2;
	private View mBtnLine;
	private RelativeLayout mBottomBar;
	private InputCallback mCallback;

	public FindWishInputPass(Activity activity)
	{
		super(activity);
		init(activity);
	}

	public FindWishInputPass(Activity activity, int theme)
	{
		super(activity, theme);
		init(activity);
	}

	private void init(Activity activity)
	{
		this.setCanceledOnTouchOutside(false);
		ShareData.InitData(activity);
		FrameLayout.LayoutParams fParams = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(500), ShareData.PxToDpi_xhdpi(250));
		fParams.gravity = Gravity.CENTER;
		AddView(getContentView(activity), fParams);
	}

	private View getContentView(Context context)
	{
		LayoutParams rParams = new LayoutParams(ShareData.PxToDpi_xhdpi(500), ShareData.PxToDpi_xhdpi(250));
		RelativeLayout mainLay = new RelativeLayout(context);
		GradientDrawable drawable = new GradientDrawable();
		drawable.setCornerRadius(ShareData.PxToDpi_xhdpi(10));
		drawable.setColor(0xffffffff);
		mainLay.setBackground(drawable);
		mainLay.setLayoutParams(rParams);

		rParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		rParams.setMargins(ShareData.PxToDpi_xhdpi(20), ShareData.PxToDpi_xhdpi(20), ShareData.PxToDpi_xhdpi(20),0);
		TextView tips = new TextView(context);
		tips.setTextColor(0xff333333);
		tips.setGravity(Gravity.CENTER);
		tips.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
		tips.setText("该祝福设置了口令密码，请输入口令后访问");
		tips.setId(Utils.generateViewId());
		mainLay.addView(tips, rParams);


		rParams = new LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(100));
		rParams.setMargins(ShareData.PxToDpi_xhdpi(20), ShareData.PxToDpi_xhdpi(20), ShareData.PxToDpi_xhdpi(20),0);
		rParams.addRule(RelativeLayout.BELOW, tips.getId());
		mPWInput = new EditText(getContext());
		mPWInput.setBackgroundColor(0x00000000);
		mPWInput.setPadding(0, 0, ShareData.PxToDpi_xhdpi(5), 0);
		mPWInput.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f);
		mPWInput.setTextColor(0xff000000);
		mPWInput.setHintTextColor(0xffb3b3b3);
		mPWInput.setHint(getContext().getResources().getString(R.string.arwish_findwish_inputpw));
		mPWInput.setSingleLine();
		mPWInput.setKeyListener(new NumberKeyListener() {
			@Override
			protected char[] getAcceptedChars() {
				String passwordRuleStr = getContext().getString(R.string.rule_password);
				return passwordRuleStr.toCharArray();
			}

			@Override
			public int getInputType() {
				return InputType.TYPE_TEXT_VARIATION_PASSWORD;
			}
		});
		mPWInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
		mPWInput.setTypeface(Typeface.MONOSPACE, 0);
		mPWInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
		mainLay.addView(mPWInput, rParams);
		mPWInput.setId(Utils.generateViewId());
		mPWInput.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				/*if(mPWInput != null)
				{
					if(mPWInput.length() >= 8)
					{
						centerOkLogin.setEnabled(true);
					}
					else
					{
						centerOkLogin.setEnabled(false);
					}
				}*/

			}

			@Override
			public void afterTextChanged(Editable s) {
				/*if(mPWInput != null)
				{
					if(mPWInput.length() >= 8)
					{
						centerOkLogin.setEnabled(true);
					}
					else
					{
						centerOkLogin.setEnabled(false);
					}
				}*/
			}
		});
		CommonUI.modifyEditTextCursor(mPWInput, ImageUtils.GetSkinColor(0xffe75988));

		rParams = new LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(90));
		rParams.addRule(RelativeLayout.BELOW, mPWInput.getId());
//		rParams.topMargin = ShareData.PxToDpi_xhdpi(50);
		mBottomBar = new RelativeLayout(context);
		mainLay.addView(mBottomBar, rParams);
		mBottomBar.setMinimumHeight(ShareData.PxToDpi_xhdpi(15));

		rParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		rParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		LinearLayout btnHolder = new LinearLayout(context);
		mBottomBar.addView(btnHolder, rParams);

		GradientDrawable normal = new GradientDrawable();
		normal.setCornerRadius(ShareData.PxToDpi_xhdpi(12));
		normal.setColor(0xffffffff);
		GradientDrawable press = new GradientDrawable();
		press.setCornerRadius(ShareData.PxToDpi_xhdpi(12));
		press.setColor(0xfff5f5f5);

		LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		lparams.weight = 1;
		mBtn1 = new FrameLayout(context);
		btnHolder.addView(mBtn1, lparams);
		mBtn1.setOnClickListener(mOnClickListener);

		FrameLayout.LayoutParams fparams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mBtn1Text = new TextView(context);
		mBtn1.addView(mBtn1Text, fparams);
		mBtn1Text.setGravity(Gravity.CENTER);
		mBtn1Text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		mBtn1Text.setTextColor(0xff333333);
		mBtn1Text.setText("确定");

		lparams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		lparams.weight = 1;
		mBtn2 = new FrameLayout(context);
		btnHolder.addView(mBtn2, lparams);
		mBtn2.setOnClickListener(mOnClickListener);

		fparams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mBtn2Text = new TextView(context);
		mBtn2.addView(mBtn2Text, fparams);
		mBtn2Text.setGravity(Gravity.CENTER);
		mBtn2Text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		mBtn2Text.setTextColor(0xff333333);
		mBtn2Text.setText("取消");

		rParams = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
		View line = new View(context);
		line.setBackgroundColor(context.getResources().getColor(R.color.line_color));
		mBottomBar.addView(line, rParams);

		rParams = new LayoutParams(1, LayoutParams.MATCH_PARENT);
		rParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		mBtnLine = new View(context);
		mBtnLine.setBackgroundColor(context.getResources().getColor(R.color.line_color));
		mBottomBar.addView(mBtnLine, rParams);

		return mainLay;
	}

	private View.OnClickListener mOnClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if (v == mBtn1) {
				if (mCallback != null) {
					mCallback.onOk(mPWInput.getText().toString());
				}
				dismiss();

			} else if (v == mBtn2) {
				if (mCallback != null) {
					mCallback.onCancel();
				}
				dismiss();
			}
		}
	};

	public void setCallback(InputCallback callback)
	{
		mCallback = callback;
	}

	public interface InputCallback{
		public void onOk(String pwd);
		public void onCancel();
	}


}
