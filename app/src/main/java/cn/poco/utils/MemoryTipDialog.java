package cn.poco.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.widget.AlertDialogV1;
import my.beautyCamera.R;

/**
 * Created by: fwc
 * Date: 2017/3/22
 */
public class MemoryTipDialog {

	private Context mContext;

	private LinearLayout mRoot;
	private TextView mMessage;
	private TextView mPositive;
	private TextView mNegative;
	private FrameLayout mPositiveLayout;

	private AlertDialogV1 mDialog;

	private Info mInfo;

	public MemoryTipDialog(Context context) {

		mContext = context;
	}

	private void apply(Info info) {

		mInfo = info;

		mDialog = new AlertDialogV1(mContext);
		mDialog.setCancelable(false);
//		Window window = mDialog.getWindow();
//		if (window != null) {
//			window.setDimAmount(0.5f);
//			window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//		}

		LinearLayout.LayoutParams params;

		if (!TextUtils.isEmpty(info.message)) {
			mRoot = new LinearLayout(mContext);
			mRoot.setOrientation(LinearLayout.VERTICAL);
			//mRoot.setBackgroundResource(R.drawable.cloud_album_dialog_bg);

			mMessage = new TextView(mContext);
			mMessage.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			mMessage.setTextColor(0xe5000000);
			mMessage.setIncludeFontPadding(false);
			mMessage.setLineSpacing(20, 1);
			mMessage.setGravity(Gravity.CENTER);
			params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER_HORIZONTAL;
			params.topMargin = ShareData.PxToDpi_xhdpi(61);
			mRoot.addView(mMessage, params);

			mMessage.setText(info.message);
		}

		if (!TextUtils.isEmpty(info.positive)) {
			mPositiveLayout = new FrameLayout(mContext);
			params = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(420), ShareData.PxToDpi_xhdpi(78));
			params.gravity = Gravity.CENTER_HORIZONTAL;
			params.topMargin = ShareData.PxToDpi_xhdpi(50);
			mRoot.addView(mPositiveLayout, params);
			{
				FrameLayout.LayoutParams params1;
				ImageView background = new ImageView(mContext);
				background.setImageResource(R.drawable.album_dialog_button_normal);
				ImageUtils.AddSkin(mContext, background);
				params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
				mPositiveLayout.addView(background, params1);

				mPositive = new TextView(mContext);
				mPositive.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				mPositive.setTextColor(Color.WHITE);
				mPositive.getPaint().setFakeBoldText(true);
				params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				params1.gravity = Gravity.CENTER;
				mPositiveLayout.addView(mPositive, params1);

				mPositive.setText(info.positive);
			}

			mPositiveLayout.setOnTouchListener(mOnTouchListener);
		}

		if (!TextUtils.isEmpty(info.negative)) {
			FrameLayout negativeLayout = new FrameLayout(mContext);
			params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(90));
			mRoot.addView(negativeLayout, params);
			{
				FrameLayout.LayoutParams params1;
				mNegative = new TextView(mContext);
				mNegative.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
				mNegative.setTextColor(0xff999999);
				mNegative.setPadding(ShareData.PxToDpi_xhdpi(32), ShareData.PxToDpi_xhdpi(16),
									 ShareData.PxToDpi_xhdpi(32), ShareData.PxToDpi_xhdpi(16));
				mNegative.setGravity(Gravity.CENTER);
				params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				params1.gravity = Gravity.CENTER;
				negativeLayout.addView(mNegative, params1);
				mNegative.setText(info.negative);
				mNegative.setOnTouchListener(mOnTouchListener);
			}
		} else {
			mRoot.setPadding(0, 0, 0, ShareData.PxToDpi_xhdpi(60));
		}

		LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(569), ViewGroup.LayoutParams.WRAP_CONTENT);
		mDialog.setRadius(ShareData.PxToDpi_xhdpi(32));
		mDialog.addContentView(mRoot, params1);
	}

	public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
		if (mDialog != null) {
			mDialog.setOnDismissListener(listener);
		}
	}

	public void show() {
		if (mDialog != null) {
			mDialog.show();
		}
	}

	public void dismiss() {
		if (mDialog != null) {
			mDialog.dismiss();
		}
	}

	public void setPositiveClickListener(OnDialogClick onDialogClick) {
		if (mInfo != null) {
			mInfo.positiveClick = onDialogClick;
		}
	}

	public void setNegativeClickListener(OnDialogClick onDialogClick) {
		if (mInfo != null) {
			mInfo.negativeClick = onDialogClick;
		}
	}

	private View.OnTouchListener mOnTouchListener = new OnAnimationClickListener() {
		@Override
		public void onAnimationClick(View v) {
			if (v == mPositiveLayout) {
				if (mInfo.positiveClick != null) {
					mInfo.positiveClick.onClick(mDialog);
				} else  {
					mDialog.dismiss();
				}
			} else if (v == mNegative) {
				if (mInfo.negativeClick != null) {
					mInfo.negativeClick.onClick(mDialog);
				} else {
					mDialog.dismiss();
				}
			}
		}

		@Override
		public void onTouch(View v) {

		}

		@Override
		public void onRelease(View v) {

		}
	};

	public static MemoryTipDialog shouldShowMemoryDialog(Context context, int type) {
		int info = Utils.CheckSDCard(context);
		if (info == Utils.SDCARD_OK) {
			return null;
		}

//		info = type;

		if ((type & info) != 0) {

			int messageRes = 0;
			int positiveRes = 0;
			int negativeRes = 0;
			switch (info) {
				case Utils.SDCARD_ERROR:
					messageRes = R.string.memory_error_message;
					positiveRes = R.string.memory_error_positive;
					break;
				case Utils.SDCARD_OUT_OF_SPACE:
					messageRes = R.string.memory_no_space_message;
					positiveRes = R.string.memory_no_space_positive;
					negativeRes = R.string.memory_no_space_negative;
					break;
				case Utils.SDCARD_WARNING:
					messageRes = R.string.memory_warning_message;
					positiveRes = R.string.memory_warning_positive;
					negativeRes = R.string.memory_warning_negative;
					break;
			}

			Builder builder = new Builder(context);
			builder.setMessage(messageRes);
			builder.setPositiveButton(positiveRes, null);
			if (negativeRes != 0) {
				builder.setNegativeButton(negativeRes, null);
			}

			return builder.build();
		}

		return null;
	}

	public static class Builder {

		private Context mContext;
		private Info mInfo;

		public Builder(Context context) {
			mContext = context;
			mInfo = new Info();
		}

		public Builder setMessage(@StringRes int message) {
			mInfo.message = mContext.getResources().getString(message);
			return this;
		}

		public Builder setPositiveButton(@StringRes int positive, @Nullable OnDialogClick onDialogClick) {
			mInfo.positive = mContext.getResources().getString(positive);
			mInfo.positiveClick = onDialogClick;
			return this;
		}

		public Builder setNegativeButton(@StringRes int negative, @Nullable OnDialogClick onDialogClick) {
			mInfo.negative = mContext.getResources().getString(negative);
			mInfo.negativeClick = onDialogClick;
			return this;
		}

		public Builder setDismissListener(DialogInterface.OnDismissListener listener) {
			mInfo.dismissListener = listener;
			return this;
		}

		public MemoryTipDialog build() {
			MemoryTipDialog dialog = new MemoryTipDialog(mContext);
			dialog.apply(mInfo);
			dialog.setOnDismissListener(mInfo.dismissListener);
			return dialog;
		}

		public void show() {
			build().show();
		}
	}

	private static class Info {
		String message;
		String positive;
		String negative;

		OnDialogClick positiveClick;
		OnDialogClick negativeClick;
		DialogInterface.OnDismissListener dismissListener;
	}

	public interface OnDialogClick {
		void onClick(AlertDialogV1 dialog);
	}
}
