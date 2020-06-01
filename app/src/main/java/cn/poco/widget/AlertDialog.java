package cn.poco.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.circle.utils.Utils;

import my.beautyCamera.R;


public class AlertDialog {

	public AlertDialog(Context context, AttributeSet attrs, int defStyle) {
		initDialog(context);
	}

	public AlertDialog(Context context, AttributeSet attrs) {
		initDialog(context);
	}

	public AlertDialog(Context context) {
		initDialog(context);
	}

	private Dialog mDialog;
	private ContentView mContentView;

	private void initDialog(Context context) {
		mDialog = new Dialog(context, R.style.dialog);
		Window window = mDialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.width = Utils.getScreenW();
		lp.alpha = 0.97f;
		lp.dimAmount = 0.7f;
		window.setAttributes(lp);
		window.setGravity(Gravity.CENTER_HORIZONTAL);
		mContentView = new ContentView(context);
		mDialog.setContentView(mContentView);

		mDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				if (mOnDismissListener != null) {
					mOnDismissListener.onDismiss(dialog);
				}
			}
		});
	}

	public void setCanceledOnTouchOutside(boolean cancel) {
		mDialog.setCanceledOnTouchOutside(cancel);
	}
	
	public void setOnCancelListener(OnCancelListener listener)
	{
		mDialog.setOnCancelListener(listener);
	}

	private OnDismissListener mOnDismissListener;

	public void setOnDismissListener(OnDismissListener l) {
		mOnDismissListener = l;
	}

	public void show() {
		mDialog.show();
		// mContentView.startAnimation(AnimationUtils.loadAnimation(mContentView.getContext(),
		// R.anim.animated_scale_in));
	}

	public void dismiss() {
		mDialog.dismiss();
		// mContentView.startAnimation(AnimationUtils.loadAnimation(mContentView.getContext(),
		// R.anim.animated_scale_out));
	}

	public void setContentView(View view) {
		mContentView.setContentView(view);
	}

	public void setContentViewSize(int width, int heigth) {
		mContentView.setContentViewSize(width, heigth);
	}

	public void setNegativeButton(String text, OnClickListener l) {
		mContentView.setNegativeButton(text, l);
	}

	public void setPositiveButton(String text, OnClickListener l) {
		mContentView.setPositiveButton(text, l);
	}

	public void setPositiveButtonBackgroundColor(int clrNormal, int clrPress) {
		mContentView.setPositiveButtonBackgroundColor(clrNormal, clrPress);
	}

	public void setNegativeButtonBackgroundColor(int clrNormal, int clrPress) {
		mContentView.setNegativeButtonBackgroundColor(clrNormal, clrPress);
	}

	public void setPositiveButtonColor(ColorStateList colors) {
		mContentView.setPositiveButtonColor(colors);
	}

	public void setNegativeButtonColor(ColorStateList colors) {
		mContentView.setNegativeButtonColor(colors);
	}

	public void setPositiveButtonColor(int color) {
		mContentView.setPositiveButtonColor(color);
	}

	public void setNegativeButtonColor(int color) {
		mContentView.setNegativeButtonColor(color);
	}

	public void setPositiveTextColor(int color) {
		mContentView.setPositiveTextColor(color);
	}

	public void setNegativeTextColor(int color) {
		mContentView.setNegativeTextColor(color);
	}

	public void setClickDismissEnabled(boolean dismissAble) {
		mContentView.setClickDismissEnabled(dismissAble);
	}

	public void setPositiveButtonEnabled(boolean enabled) {
		mContentView.setPositiveButtonEnabled(enabled);
	}

	public void setNegativeButtonEnabled(boolean enabled) {
		mContentView.setNegativeButtonEnabled(enabled);
	}

	public void setText(CharSequence text1, CharSequence text2) {
		mContentView.setText(text1, text2);
	}

	public void setPriceText(String text1, String text2) {
		mContentView.setPriceText(text1, text2);
	}

	public void setMessage(CharSequence msg) {
		mContentView.setText("", msg);
	}
	
	public void setMessage(CharSequence msg,int textSize) {
		mContentView.setText("",0, msg,textSize);
	}

	public void setPriceMessage(String msg) {
		mContentView.setPriceText("", msg);
	}

	public void setText(CharSequence text1, int size1, CharSequence text2, int size2) {
		mContentView.setText(text1, size1, text2, size2);
	}

	public void setText(CharSequence text1, int size1, int gravity1, CharSequence text2, int size2, int gravity2) {
		mContentView.setText(text1, size1, gravity1, text2, size2, gravity2);
	}

	public void setPriceText(String text1, int size1, String text2, int size2) {
		mContentView.setPriceText(text1, size1, text2, size2);
	}

	public void setPriceText(String text1, int size1, int gravity1, String text2, int size2, int gravity2) {
		mContentView.setPriceText(text1, size1, gravity1, text2, size2, gravity2);
	}

	private class ContentView extends RelativeLayout {

		private LinearLayout mContent;
		private LinearLayout mContentView;
		private TextView mBtn1Text;
		private FrameLayout mBtn1;
		private TextView mBtn2Text;
		private FrameLayout mBtn2;
		private View mBtnLine;
		private RelativeLayout mBottomBar;
		private OnClickListener mPositiveClickListener;
		private OnClickListener mNegativeClickListener;
		private ScrollView mScrollView;
		private boolean mClickDismissAble = true;

		public ContentView(Context context) {
			super(context);
			initialize(context);
		}

		public void setContentViewSize(int width, int heigth) {
			LayoutParams params = (LayoutParams) mContentView.getLayoutParams();
			params.width = width;
			params.height = heigth;
			mContentView.setLayoutParams(params);
			mContentView.invalidate();
		}

		public void setClickDismissEnabled(boolean enabled) {
			mClickDismissAble = enabled;
		}

		@SuppressWarnings("deprecation")
		private void initialize(Context context) {
			int width = (int) (Utils.getScreenW() - Utils.getRealPixel2(180));

			// setBackgroundColor(0x99000000);
			setBackgroundColor(Color.TRANSPARENT);

			LayoutParams params = new LayoutParams(width, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.CENTER_IN_PARENT);
			mContentView = new LinearLayout(context);
			addView(mContentView, params);

			mContentView.setOrientation(LinearLayout.VERTICAL);

			GradientDrawable drawable = new GradientDrawable();
			drawable.setColor(0xffffffff);
			drawable.setCornerRadius(Utils.getRealPixel2(12));
			mContentView.setBackgroundDrawable(drawable);

			LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			mScrollView = new ScrollView(context);
			mContentView.addView(mScrollView, lparams);
			mScrollView.setVerticalFadingEdgeEnabled(false);

			lparams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, Utils.getRealPixel2(90));
			mBottomBar = new RelativeLayout(context);
			mContentView.addView(mBottomBar, lparams);
			mBottomBar.setMinimumHeight(Utils.getRealPixel2(15));
			mBottomBar.setVisibility(GONE);

			params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			params.addRule(RelativeLayout.CENTER_IN_PARENT);
			LinearLayout btnHolder = new LinearLayout(context);
			mBottomBar.addView(btnHolder, params);

			GradientDrawable normal = new GradientDrawable();
			normal.setCornerRadius(Utils.getRealPixel2(12));
			normal.setColor(0xffffffff);
			GradientDrawable press = new GradientDrawable();
			press.setCornerRadius(Utils.getRealPixel2(12));
			press.setColor(0xfff5f5f5);

			lparams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			lparams.weight = 1;
			mBtn1 = new FrameLayout(context);
			btnHolder.addView(mBtn1, lparams);
			mBtn1.setOnClickListener(mOnClickListener);
			mBtn1.setVisibility(GONE);

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
			mBtn2.setVisibility(GONE);

			fparams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			mBtn2Text = new TextView(context);
			mBtn2.addView(mBtn2Text, fparams);
			mBtn2Text.setGravity(Gravity.CENTER);
			mBtn2Text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			mBtn2Text.setTextColor(0xff333333);
			mBtn2Text.setText("取消");

			params = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
			View line = new View(context);
			line.setBackgroundColor(getResources().getColor(R.color.line_color));
			mBottomBar.addView(line, params);

			params = new LayoutParams(1, LayoutParams.MATCH_PARENT);
			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
			mBtnLine = new View(context);
			mBtnLine.setBackgroundColor(getResources().getColor(R.color.line_color));
			mBottomBar.addView(mBtnLine, params);

			mContent = new LinearLayout(context);
			mScrollView.addView(mContent);
			mContent.setOrientation(LinearLayout.VERTICAL);
		}

		public void setNegativeButton(String text, OnClickListener l) {
			mNegativeClickListener = l;
			if (text != null && text.length() > 0) {
				mBottomBar.setVisibility(VISIBLE);
				mBtn1.setVisibility(VISIBLE);
				mBtn1Text.setText(text);
				mBtnLine.setVisibility(mBtn2.getVisibility());
				setNegativeButtonBackground();
				if (mBtn2.getVisibility() == VISIBLE) {
					setPositiveButtonBackground();
				}
			} else if (mBtn2.getVisibility() != VISIBLE) {
				mBottomBar.setVisibility(GONE);
			}
		}

		public void setPositiveButton(String text, OnClickListener l) {
			mPositiveClickListener = l;
			if (text != null && text.length() > 0) {
				mBottomBar.setVisibility(VISIBLE);
				mBtn2.setVisibility(VISIBLE);
				mBtn2Text.setText(text);
				mBtnLine.setVisibility(mBtn1.getVisibility());
				setPositiveButtonBackground();
				if (mBtn1.getVisibility() == VISIBLE) {
					setNegativeButtonBackground();
				}
			} else if (mBtn1.getVisibility() != VISIBLE) {
				mBottomBar.setVisibility(GONE);
			}
		}

		private int mPositiveNormalColor = 0xffffffff, mPositivePressColor = 0xfff5f5f5, mNegativeNormalColor = 0xffffffff, mNegativePressColor = 0xfff5f5f5;

		public void setPositiveButtonBackgroundColor(int clrNormal, int clrPress) {
			mPositiveNormalColor = clrNormal;
			mPositivePressColor = clrPress;
			setPositiveButtonBackground();
			setNegativeButtonBackground();
		}

		@SuppressWarnings("deprecation")
		private void setPositiveButtonBackground() {
			GradientDrawable normal = new GradientDrawable();
			normal.setColor(mPositiveNormalColor);
			GradientDrawable press = new GradientDrawable();
			press.setColor(mPositivePressColor);
			int radi = Utils.getRealPixel2(12);
			if (mBtn1.getVisibility() == VISIBLE) {
				normal.setCornerRadii(new float[] { 0, 0, 0, 0, radi, radi, 0, 0 });
				press.setCornerRadii(new float[] { 0, 0, 0, 0, radi, radi, 0, 0 });
			} else {
				normal.setCornerRadii(new float[] { 0, 0, 0, 0, radi, radi, radi, radi });
				press.setCornerRadii(new float[] { 0, 0, 0, 0, radi, radi, radi, radi });
			}
			StateListDrawable stateDrawable = Utils.newSelector(getContext(),normal, press);
			mBtn2.setBackgroundDrawable(stateDrawable);
		}

		public void setNegativeButtonBackgroundColor(int clrNormal, int clrPress) {
			mNegativeNormalColor = clrNormal;
			mNegativePressColor = clrPress;
			setNegativeButtonBackground();
			setPositiveButtonBackground();
		}

		@SuppressWarnings("deprecation")
		private void setNegativeButtonBackground() {
			GradientDrawable normal = new GradientDrawable();
			normal.setColor(mNegativeNormalColor);
			GradientDrawable press = new GradientDrawable();
			press.setColor(mNegativePressColor);
			int radi = Utils.getRealPixel2(12);
			if (mBtn2.getVisibility() == VISIBLE) {
				normal.setCornerRadii(new float[] { 0, 0, 0, 0, 0, 0, radi, radi });
				press.setCornerRadii(new float[] { 0, 0, 0, 0, 0, 0, radi, radi });
			} else {
				normal.setCornerRadii(new float[] { 0, 0, 0, 0, radi, radi, radi, radi });
				press.setCornerRadii(new float[] { 0, 0, 0, 0, radi, radi, radi, radi });
			}
			StateListDrawable stateDrawable = Utils.newSelector(getContext(),normal, press);
			mBtn1.setBackgroundDrawable(stateDrawable);
		}

		public void setPositiveButtonColor(ColorStateList colors) {
			mBtn2Text.setTextColor(colors);
		}

		public void setNegativeButtonColor(ColorStateList colors) {
			mBtn1Text.setTextColor(colors);
		}

		public void setPositiveButtonColor(int color) {
			mBtn2Text.setTextColor(color);
		}

		public void setNegativeButtonColor(int color) {
			mBtn1Text.setTextColor(color);
		}

		public void setPositiveTextColor(int color) {
			if (mBtn2 != null) {
				mBtn2Text.setTextColor(color);
			}
		}

		public void setNegativeTextColor(int color) {
			if (mBtn1 != null) {
				mBtn1Text.setTextColor(color);
			}
		}

		public void setPositiveButtonEnabled(boolean enabled) {
			if (mBtn2 != null) {
				mBtn2.setEnabled(enabled);
				if (enabled) {
					mBtn2Text.setTextColor(0xff333333);
				} else {
					mBtn2Text.setTextColor(0xffaaaaaa);
				}
			}
		}

		public void setNegativeButtonEnabled(boolean enabled) {
			if (mBtn1 != null) {
				mBtn1.setEnabled(enabled);
				if (enabled) {
					mBtn1Text.setTextColor(0xff333333);
				} else {
					mBtn1Text.setTextColor(0xffaaaaaa);
				}
			}
		}

		public void setText(CharSequence text1, CharSequence text2) {
			setText(text1, 18, text2, 14);
		}

		public void setText(CharSequence text1, int size1, CharSequence text2, int size2) {
			setText(text1, size1, Gravity.CENTER_HORIZONTAL, text2, size2, Gravity.CENTER_HORIZONTAL);
		}

		public void setText(CharSequence text1, int size1, int gravity1, CharSequence text2, int size2, int gravity2) {
			mContent.removeAllViews();
			TextView tv1 = new TextView(getContext());
			tv1.setTextColor(0xff333333);
			tv1.setGravity(gravity1);
			tv1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size1);
			TextView tv2 = new TextView(getContext());
			tv2.setTextColor(0xff333333);
			tv2.setGravity(gravity2);
			tv2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size2);
			if (text1 != null && text1.length() > 0 && text2 != null && text2.length() > 0) {
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				params.topMargin = Utils.getRealPixel2(60);
				params.leftMargin = Utils.getRealPixel2(60);
				params.rightMargin = Utils.getRealPixel2(60);
				tv1.setText(text1);
				mContent.addView(tv1, params);

				params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				params.topMargin = Utils.getRealPixel2(60);
				params.leftMargin = Utils.getRealPixel2(60);
				params.rightMargin = Utils.getRealPixel2(60);
				tv2.setText(text2);
				mContent.addView(tv2, params);
			} else if (text1 != null && text1.length() > 0) {
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				params.topMargin = Utils.getRealPixel2(80);
				params.leftMargin = Utils.getRealPixel2(60);
				params.rightMargin = Utils.getRealPixel2(60);
				tv1.setText(text1);
				mContent.addView(tv1, params);
			} else if (text2 != null && text2.length() > 0) {
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				params.topMargin = Utils.getRealPixel2(80);
				params.leftMargin = Utils.getRealPixel2(60);
				params.rightMargin = Utils.getRealPixel2(60);
				tv2.setText(text2);
				mContent.addView(tv2, params);
			}

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, Utils.getRealPixel2(60));
			View empty = new View(getContext());
			mContent.addView(empty, params);
		}

		public void setPriceText(String text1, String text2) {
			setPriceText(text1, 18, text2, 14);
		}

		public void setPriceText(String text1, int size1, String text2, int size2) {
			setPriceText(text1, size1, Gravity.CENTER_HORIZONTAL, text2, size2, Gravity.CENTER_HORIZONTAL);
		}

		public void setPriceText(String text1, int size1, int gravity1, String text2, int size2, int gravity2) {
			mContent.removeAllViews();
			NumberHighlightTextView tv1 = new NumberHighlightTextView(getContext());
			tv1.setTextColor(0xff333333);
			tv1.setGravity(gravity1);
			tv1.setHighlightTextColor(0xfffe9920);
			tv1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size1);
			NumberHighlightTextView tv2 = new NumberHighlightTextView(getContext());
			tv2.setTextColor(0xff333333);
			tv2.setGravity(gravity2);
			tv2.setHighlightTextColor(0xfffe9920);
			tv2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size2);
			if (text1 != null && text1.length() > 0 && text2 != null && text2.length() > 0) {
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				params.topMargin = Utils.getRealPixel2(60);
				params.leftMargin = Utils.getRealPixel2(60);
				params.rightMargin = Utils.getRealPixel2(60);
				tv1.setPriceHightlightText(text1);
				mContent.addView(tv1, params);

				params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				params.topMargin = Utils.getRealPixel2(60);
				params.leftMargin = Utils.getRealPixel2(60);
				params.rightMargin = Utils.getRealPixel2(60);
				tv2.setPriceHightlightText(text2);
				mContent.addView(tv2, params);
			} else if (text1 != null && text1.length() > 0) {
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				params.topMargin = Utils.getRealPixel2(80);
				params.leftMargin = Utils.getRealPixel2(60);
				params.rightMargin = Utils.getRealPixel2(60);
				tv1.setPriceHightlightText(text1);
				mContent.addView(tv1, params);
			} else if (text2 != null && text2.length() > 0) {
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				params.topMargin = Utils.getRealPixel2(80);
				params.leftMargin = Utils.getRealPixel2(60);
				params.rightMargin = Utils.getRealPixel2(60);
				tv2.setPriceHightlightText(text2);
				mContent.addView(tv2, params);
			}

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, Utils.getRealPixel2(60));
			View empty = new View(getContext());
			mContent.addView(empty, params);
		}

		public void setContentView(View view) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			setContentView(view, params);
		}

		public void setContentView(View view, LinearLayout.LayoutParams params) {
			mContent.removeAllViews();
			mContent.addView(view, params);
		}

		private OnClickListener mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (arg0 == mBtn1) {
					if (mNegativeClickListener != null) {
						mNegativeClickListener.onClick(ContentView.this);
					}
					if (mClickDismissAble) {
						dismiss();
					}

				} else if (arg0 == mBtn2) {
					if (mPositiveClickListener != null) {
						mPositiveClickListener.onClick(ContentView.this);
					}
					if (mClickDismissAble) {
						dismiss();
					}
				}
			}

		};
	}

}
