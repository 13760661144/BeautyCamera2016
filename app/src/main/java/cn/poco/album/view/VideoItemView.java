package cn.poco.album.view;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.poco.tianutils.ShareData;

/**
 * Created by: fwc
 * Date: 2016/11/22
 */
public class VideoItemView extends FrameLayout {

	private Context mContext;

	public ImageView image;
	public TextView duration;

	public VideoItemView(Context context) {
		super(context);
		mContext = context;
		setClickable(true);
		setWillNotDraw(false);

		initViews();
	}

	private void initViews() {
		image = new ImageView(mContext);
		image.setScaleType(ImageView.ScaleType.CENTER_CROP);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addView(image, params);

		duration = new TextView(mContext);
		duration.setTextColor(Color.WHITE);
		duration.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.END | Gravity.BOTTOM;
		params.rightMargin = ShareData.PxToDpi_xhdpi(12);
		params.bottomMargin = ShareData.PxToDpi_xhdpi(6);
		addView(duration, params);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	}
}
