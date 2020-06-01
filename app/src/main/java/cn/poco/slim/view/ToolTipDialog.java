package cn.poco.slim.view;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.widget.AlertDialogV1;
import my.beautyCamera.R;

/**
 * Created by: fwc
 * Date: 2017/1/17
 */
public class ToolTipDialog {

	private Context mContext;

	private AlertDialogV1 mDialog;

	public ToolTipDialog(Context context) {
		mContext = context;
		init();
	}

	private void init() {
		mDialog = new AlertDialogV1(mContext);
		Window window = mDialog.getWindow();
		if (window != null) {
			window.setDimAmount(0.5f);
			window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

			window.setWindowAnimations(R.style.popUpAnimation2);
		}

		FrameLayout frameLayout = new FrameLayout(mContext);
		FrameLayout.LayoutParams params;
		{
			ImageView imageView = new ImageView(mContext);
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setImageResource(R.drawable.beauty_slim_tip_bg);
			params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(498));
			frameLayout.addView(imageView, params);

			FrameLayout bottomLayout = new FrameLayout(mContext);
			bottomLayout.setBackgroundResource(R.drawable.display_bottom_bg);
			params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(285));
			params.gravity = Gravity.BOTTOM;
			frameLayout.addView(bottomLayout, params);
			{
				FrameLayout center = new FrameLayout(mContext);
				params = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(450), ShareData.PxToDpi_xhdpi(80));
				params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
				params.bottomMargin = ShareData.PxToDpi_xhdpi(40);
				bottomLayout.addView(center, params);
				{
					imageView = new ImageView(mContext);
					imageView.setImageResource(R.drawable.album_dialog_button_normal);
					ImageUtils.AddSkin(mContext, imageView);
					params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
					center.addView(imageView, params);

					TextView textView = new TextView(mContext);
					textView.setText(R.string.known);
					textView.setTextColor(Color.WHITE);
					textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
					textView.getPaint().setFakeBoldText(true);
					params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					params.gravity = Gravity.CENTER;
					center.addView(textView, params);
				}
				center.setOnTouchListener(new OnAnimationClickListener() {
					@Override
					public void onAnimationClick(View v) {
						mDialog.dismiss();
					}

					@Override
					public void onTouch(View v) {

					}

					@Override
					public void onRelease(View v) {

					}
				});

				imageView = new ImageView(mContext);
				imageView.setImageResource(R.drawable.beauty_slim_tip_text);
				params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
				params.bottomMargin = ShareData.PxToDpi_xhdpi(146);
				bottomLayout.addView(imageView, params);
			}
		}

		LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(568),
																		  ShareData.PxToDpi_xhdpi(748));
		mDialog.setRadius(ShareData.PxToDpi_xhdpi(30));
		mDialog.addContentView(frameLayout, params1);
	}

	public void show() {
		mDialog.show();
	}

	public void dismiss() {
		mDialog.dismiss();
	}
}
