package cn.poco.album.frame;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.poco.advanced.ImageUtils;
import cn.poco.album.PhotoStore;
import cn.poco.album.model.FolderInfo;
import cn.poco.album.model.PhotoInfo;
import cn.poco.cloudalbumlibs.utils.ImageLoader;
import cn.poco.cloudalbumlibs.view.ZoomImageView;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

/**
 * Created by: fwc
 * Date: 2017/1/6
 */
public class BigPhotoFrame extends FrameLayout {

	private Context mContext;

	private ViewPager mViewPager;
	private BigPhotoAdapter mAdapter;

	private ImageView mUnselectImage;
	private ImageView mSelectedImage;

	private List<PhotoInfo> mItems;
	private int mPosition;

	private OnPhotoSelectListener mSelectListener;

	private OnLoadListener mLoadListener;

	private int mFolderIndex;

	public BigPhotoFrame(Context context) {
		super(context);

		mContext = context;
		mItems = new ArrayList<>();

		initViews();
	}

	private void initViews() {

		setBackgroundColor(Color.WHITE);
		setClickable(true);

		LayoutParams params;
		mViewPager = new ViewPager(mContext);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		addView(mViewPager, params);

		mUnselectImage = new ImageView(mContext);
		mUnselectImage.setImageResource(R.drawable.album_big_unselect);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.BOTTOM | Gravity.END;
		params.rightMargin = ShareData.PxToDpi_xhdpi(28);
		params.bottomMargin = ShareData.PxToDpi_xhdpi(90);
		addView(mUnselectImage, params);
		mUnselectImage.setVisibility(GONE);
		mUnselectImage.setOnTouchListener(new OnAnimationClickListener() {
			@Override
			public void onAnimationClick(View v) {
				if (mSelectListener != null) {
					if(mSelectListener.onSelect(mPosition)) {
						mUnselectImage.setVisibility(GONE);
						mSelectedImage.setVisibility(VISIBLE);
					}
				}
			}

			@Override
			public void onTouch(View v) {

			}

			@Override
			public void onRelease(View v) {

			}
		});

		mSelectedImage = new ImageView(mContext);
		mSelectedImage.setImageResource(R.drawable.album_big_selected);
		ImageUtils.AddSkin(mContext, mSelectedImage);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.BOTTOM | Gravity.END;
		params.rightMargin = ShareData.PxToDpi_xhdpi(28);
		params.bottomMargin = ShareData.PxToDpi_xhdpi(90);
		addView(mSelectedImage, params);
		mSelectedImage.setVisibility(GONE);
		mSelectedImage.setOnTouchListener(new OnAnimationClickListener() {
			@Override
			public void onAnimationClick(View v) {
				mSelectedImage.setVisibility(GONE);
				mUnselectImage.setVisibility(VISIBLE);
				if (mSelectListener != null) {
					mSelectListener.onSelect(mPosition);
				}
			}

			@Override
			public void onTouch(View v) {

			}

			@Override
			public void onRelease(View v) {

			}
		});

		mAdapter = new BigPhotoAdapter();
		mViewPager.setAdapter(mAdapter);
		mViewPager.addOnPageChangeListener(mPageChangeListener);
	}

	public void setData(int folderIndex, List<PhotoInfo> items, int position) {
		mFolderIndex = folderIndex;
		mItems.clear();
		mItems.addAll(items);
		mPosition = position;
		if (mItems.get(position).isSelected()) {
			mSelectedImage.setVisibility(VISIBLE);
			mUnselectImage.setVisibility(GONE);
		} else {
			mSelectedImage.setVisibility(GONE);
			mUnselectImage.setVisibility(VISIBLE);
		}
		mAdapter.notifyDataSetChanged();
		mViewPager.setCurrentItem(mPosition, false);
	}

	private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

		}

		@Override
		public void onPageSelected(int position) {
			mPosition = position;

			if (mItems.get(position).isSelected()) {
				mSelectedImage.setVisibility(VISIBLE);
				mUnselectImage.setVisibility(GONE);
			} else {
				mSelectedImage.setVisibility(GONE);
				mUnselectImage.setVisibility(VISIBLE);
			}

			PhotoStore photoStore = PhotoStore.getInstance(mContext);
			FolderInfo folderInfo = photoStore.getFolderInfo(mFolderIndex);
			if (photoStore.shouldReloadData(mFolderIndex, mPosition)) {

				List<PhotoInfo> item = photoStore.getPhotoInfos(folderInfo.getName(), mItems.size(),
																photoStore.getCacheSize());
				mItems.addAll(item);
				mAdapter.notifyDataSetChanged();

				if (mLoadListener != null) {
					mLoadListener.onLoad(item);
				}
			}
		}

		@Override
		public void onPageScrollStateChanged(int state) {

		}
	};

	private class BigPhotoAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mItems.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {

			ZoomImageView zoomImageView = new ZoomImageView(mContext);
			ImageLoader.displayImage(mContext, mItems.get(position).getImagePath(), zoomImageView);

			container.addView(zoomImageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

			return zoomImageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			View view = (View) object;
			container.removeView(view);
			container.clearDisappearingChildren();
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
	}

	public void setOnLoadListener(OnLoadListener onLoadListener) {
		mLoadListener = onLoadListener;
	}

	public void onBack() {
		mViewPager.removeOnPageChangeListener(mPageChangeListener);
		mItems.clear();
	}

	public void setOnPhotoSelectListener(OnPhotoSelectListener listener) {
		mSelectListener = listener;
	}

	public interface OnPhotoSelectListener {
		boolean onSelect(int position);
	}

	public interface OnLoadListener {
		void onLoad(List<PhotoInfo> photoInfos);
	}

	public int getCurrentPosition() {
		return mPosition;
	}
}
