package cn.poco.album.view;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by: fwc
 * Date: 2016/11/22
 */
public class PhotoView extends RelativeLayout {

	private Context mContext;

	public ImageView image;
	private ImageView mEditView;
	private BorderView mBorderView;
	public ImageView lookBig;

	public PhotoView(Context context) {
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

		mEditView = new ImageView(mContext);
		mEditView.setImageResource(R.drawable.album_edit);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params.rightMargin = ShareData.PxToDpi_xhdpi(14);
		params.bottomMargin = ShareData.PxToDpi_xhdpi(14);
		addView(mEditView, params);
		mEditView.setVisibility(GONE);

		mBorderView = new BorderView(mContext);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		addView(mBorderView, params);
		mBorderView.setVisibility(GONE);

		lookBig = new ImageView(mContext);
		lookBig.setImageResource(R.drawable.album_look_big_photo);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		addView(lookBig, params);
		lookBig.setVisibility(GONE);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	}

	public void setEditView(boolean edit) {
		if (edit) {
			mEditView.setVisibility(VISIBLE);
		} else {
			mEditView.setVisibility(GONE);
		}
	}

	public void setSelected(boolean selected) {
		if (selected) {
			mBorderView.setVisibility(VISIBLE);
		} else {
			mBorderView.setVisibility(GONE);
		}
	}
}
