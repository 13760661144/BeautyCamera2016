package cn.poco.pendant.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

import static cn.poco.tianutils.ShareData.PxToDpi_xhdpi;

/**
 * Created by: fwc
 * Date: 2016/11/21
 */
public class TitleItem extends FrameLayout {

	private Context mContext;

	public TextView title;

	private Paint mPaint;

	private boolean mShow = true;

	public ImageView mNewImage;

	public ImageView mRecommendImage;

	public ImageView mArrowView;

	public TitleItem(Context context) {
		super(context);

		mContext = context;
		initViews();

		setWillNotDraw(false);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(ImageUtils.GetSkinColor(0xffe75988));
	}

	private void initViews() {

		setPadding(PxToDpi_xhdpi(17), 0, PxToDpi_xhdpi(17), 0);
		setClipToPadding(false);

		cn.poco.cloudalbumlibs.utils.DrawableUtils.setBackground(this,
				cn.poco.cloudalbumlibs.utils.DrawableUtils.pressColorDrawable(0, mContext.getResources().getColor(R.color.white_pressed)));
		LayoutParams params;
		LinearLayout linearLayout = new LinearLayout(mContext);
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_VERTICAL;
		addView(linearLayout, params);
		{
			LinearLayout.LayoutParams params1;
			title = new TextView(mContext);
			title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
			title.setIncludeFontPadding(false);
			params1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params1.gravity = Gravity.CENTER_VERTICAL;
			linearLayout.addView(title, params);

			mArrowView = new ImageView(mContext);
			mArrowView.setImageResource(R.drawable.beauty_status_btn_arrow);
			ImageUtils.AddSkin(getContext(), mArrowView);
			params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params1.gravity = Gravity.CENTER_VERTICAL;
			params1.leftMargin = ShareData.PxToDpi_xhdpi(5);
			linearLayout.addView(mArrowView, params1);
			mArrowView.setVisibility(INVISIBLE);
		}

		mNewImage = new ImageView(mContext);
		mNewImage.setImageResource(R.drawable.sticker_new);
		mNewImage.setVisibility(GONE);
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.topMargin = PxToDpi_xhdpi(13);
		params.rightMargin = ShareData.PxToDpi_xhdpi(5);
		params.gravity = Gravity.END;
		addView(mNewImage, params);

		mRecommendImage = new ImageView(mContext);
		mRecommendImage.setImageResource(R.drawable.sticker_recom);
		mRecommendImage.setVisibility(GONE);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.topMargin = PxToDpi_xhdpi(13);
		params.rightMargin = ShareData.PxToDpi_xhdpi(5);
		params.gravity = Gravity.END;
		addView(mRecommendImage, params);
	}

	public void showIndicator(boolean show) {
		mShow = show;
		if (mShow) {
			mArrowView.setVisibility(VISIBLE);
			mArrowView.setRotation(0);
		} else {
			mArrowView.setVisibility(INVISIBLE);
		}
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mShow) {
			canvas.drawRect(0, getHeight() - PxToDpi_xhdpi(4),
							getWidth(), getHeight(), mPaint);
		}
	}

	public Animator getUpAnimator() {
		return ObjectAnimator.ofFloat(mArrowView, "rotation", 180, 0);
	}

	public Animator getDownAnimator() {
		return ObjectAnimator.ofFloat(mArrowView, "rotation", 0, 180);
	}

	public void showLine(boolean show) {
		mShow = show;
		invalidate();
	}

	public void changeItem() {
		mArrowView.setRotation(180);
		mShow = false;
		invalidate();
	}
}
