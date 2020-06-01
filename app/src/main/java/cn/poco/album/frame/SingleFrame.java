package cn.poco.album.frame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;

import cn.poco.advanced.ImageUtils;
import cn.poco.album.PhotoStore;
import cn.poco.album.adapter.FolderAdapter;
import cn.poco.album.adapter.PhotoAdapter;
import cn.poco.album.model.FolderInfo;
import cn.poco.album.model.PhotoInfo;
import cn.poco.album.site.AlbumSite;
import cn.poco.beautify.BeautifyResMgr2;
import cn.poco.framework.DataKey;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.DrawableUtils;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.utils.WaitAnimDialog;
import my.beautyCamera.R;

import static cn.poco.utils.DrawableUtils.colorPressedDrawable;

/**
 * Created by: fwc
 * Date: 2016/12/15
 */
public class SingleFrame extends BaseFrame {

	private int mTouchSlop;

	private ImageView mBackView;
	private ImageView mCameraView;

	private RelativeLayout mBottomLayout;
	private ImageView mFolderImage;
	private TextView mFolderName;
	private ImageView mIndicator;
	private View mLine;

	private int mBgHeight;
	private View mBg;
	private Bitmap mBgBitmap;
	private View mTransparentBg;

	private View mHeader;

	private int mHasScrollY;

	private AnimatorSet mOpenFolderAnimator;
	private AnimatorSet mCloseFolderAnimator;

	private boolean mFromCamera = false;

	private int mColorFilterId = 0;

	private WaitAnimDialog.WaitAnimView mWaitAnimView;

	private boolean mUiEnabled = true;

	private boolean hasSelectPhoto = false;

	public SingleFrame(Context context, AlbumSite site, boolean restore, boolean fromCamera) {
		super(context, site);

		mPhotoStore.addLoadCompleteListener(loadCompleteListener);

		if (restore) {
//			mPhotoStore.initFolderInfos();
//			mFolderAdapter.notifyDataSetChanged();
			FolderInfo folderInfo = mPhotoStore.getFolderInfo(mFolderIndex);
			String path = folderInfo.getCover();
			if (TextUtils.isEmpty(path)) {
				mFolderImage.setImageResource(R.drawable.cloudalbum_default_placeholder);
			} else {
				// 去除gif
				Glide.with(mContext).load(path).asBitmap().into(mFolderImage);
			}
			mFolderName.setText(folderInfo.getName());

			if (PhotoStore.sPosition + 15 >= mItems.size()) {
				while (PhotoStore.sPosition + 15 >= mItems.size() && PhotoStore.sPosition < folderInfo.getCount()) {
					List<PhotoInfo> item = mPhotoStore.getPhotoInfos(folderInfo.getName(), mItems.size(), mPhotoStore.getCacheSize());

					if (mItems.size() + item.size() > folderInfo.getCount()) {
						for (int i = 0; i < folderInfo.getCount() - mItems.size(); i++) {
							mItems.add(item.get(i));
						}
						break;
					} else {
						mItems.addAll(item);
					}
				}
				mPhotoAdapter.notifyDataSetChanged();
			}
			mLayoutManager.scrollToPositionWithOffset(PhotoStore.sPosition, PhotoStore.sOffset);
		}
//		} else if (sScrollOffset != 0){
//			mLayoutManager.scrollToPositionWithOffset(0, -sScrollOffset);
//		}

		if (fromCamera) {
			mCameraView.setVisibility(GONE);
			mFromCamera = true;
		}
	}

	@Override
	protected void init() {
		mPhotoStore = PhotoStore.getInstance(mContext);
		mFolderIndex = PhotoStore.sLastFolderIndex;

		initDatas();

		initPhotoGridView();
		initFolderListView();
		initTopLayout();

		initViews();
//		setData();
//		setListener();
	}

	@Override
	protected void initDatas() {
//		mTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
		mTouchSlop = 3;

		mTopBarHeight = ShareData.PxToDpi_xhdpi(90);
		mBottomBarHeight = ShareData.PxToDpi_xhdpi(100);
		mBgHeight = ShareData.PxToDpi_xhdpi(197);

	}

	@Override
	protected void initViews() {

		LayoutParams params;

		mPhotoGridView.setPadding(0, mTopBarHeight, 0, mBottomBarHeight);
		mPhotoGridView.setClipToPadding(false);
		params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addView(mPhotoGridView, params);

		mTopLayout.setBackgroundColor(0xf4ffffff);
		params = new LayoutParams(LayoutParams.MATCH_PARENT, mTopBarHeight);
		addView(mTopLayout, params);
		{
			RelativeLayout.LayoutParams params1;
			mBackView = new ImageView(mContext);
			mBackView.setImageResource(R.drawable.framework_back_btn);
			mBackView.setPadding(ShareData.PxToDpi_xhdpi(2), 0, ShareData.PxToDpi_xhdpi(2), 0);
			ImageUtils.AddSkin(getContext(), mBackView);
			params1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
			params1.addRule(RelativeLayout.CENTER_VERTICAL);
			params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			mTopLayout.addView(mBackView, params1);

			mCameraView = new ImageView(mContext);
			mCameraView.setImageResource(R.drawable.album_camera);
			mCameraView.setPadding(ShareData.PxToDpi_xhdpi(2), 0, ShareData.PxToDpi_xhdpi(2), 0);
			//mCameraView.setPadding(ShareData.PxToDpi_xhdpi(24), 0, ShareData.PxToDpi_xhdpi(24), 0);
			ImageUtils.AddSkin(getContext(), mCameraView);
			params1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
			params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			params1.addRule(RelativeLayout.CENTER_VERTICAL);
			mTopLayout.addView(mCameraView, params1);
		}

		mBg = new View(mContext);
		mBg.setClickable(true);
		mBg.setVisibility(INVISIBLE);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		addView(mBg, params);

		mTransparentBg = new View(mContext);
		mTransparentBg.setBackgroundColor(0x33000000);
		mTransparentBg.setClickable(true);
		mTransparentBg.setVisibility(INVISIBLE);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		addView(mTransparentBg, params);

		mHeader = new View(mContext);
		mHeader.setClickable(true);
		mHeader.setBackgroundColor(Color.WHITE);
		mHeader.setVisibility(INVISIBLE);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(24));
		params.topMargin = mBgHeight;
		addView(mHeader, params);

		mFolderListView.setPadding(0, 0, 0, mBottomBarHeight);
		mFolderListView.setClipToPadding(false);
		params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.BOTTOM;
		params.topMargin = ShareData.PxToDpi_xhdpi(221);
		addView(mFolderListView, params);

		mFolderListView.setVisibility(INVISIBLE);

		mBottomLayout = new RelativeLayout(mContext);
		Drawable drawable = colorPressedDrawable(0xf4ffffff, 0xf4e3e3e3);
		DrawableUtils.setBackground(mBottomLayout, drawable);
		mBottomLayout.setClickable(true);
		params = new LayoutParams(LayoutParams.MATCH_PARENT, mBottomBarHeight);
		params.gravity = Gravity.BOTTOM;
		addView(mBottomLayout, params);
		{
			RelativeLayout.LayoutParams params1;
			LinearLayout linearLayout = new LinearLayout(mContext);
			linearLayout.setOrientation(LinearLayout.HORIZONTAL);
			params1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			params1.addRule(RelativeLayout.CENTER_VERTICAL);
			params1.leftMargin = ShareData.PxToDpi_xhdpi(24);
			mBottomLayout.addView(linearLayout, params1);
			{
				LinearLayout.LayoutParams params2;
				mFolderImage = new ImageView(mContext);
				mFolderImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
				params2 = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(68), ShareData.PxToDpi_xhdpi(68));
				params2.gravity = Gravity.CENTER_VERTICAL;
				linearLayout.addView(mFolderImage, params2);

				mFolderName = new TextView(mContext);
				mFolderName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
				mFolderName.setTextColor(Color.BLACK);
				params2 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params2.gravity = Gravity.CENTER_VERTICAL;
				params2.leftMargin = ShareData.PxToDpi_xhdpi(24);
				linearLayout.addView(mFolderName, params2);
			}

			mIndicator = new ImageView(mContext);
			mIndicator.setImageResource(R.drawable.album_up);
			ImageUtils.AddSkin(getContext(), mIndicator);
			params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			params1.addRule(RelativeLayout.CENTER_VERTICAL);
			params1.rightMargin = ShareData.PxToDpi_xhdpi(20);
			mBottomLayout.addView(mIndicator, params1);
		}

		mLine = new View(mContext);
		mLine.setBackgroundColor(Color.BLACK);
		mLine.setAlpha(0.2f);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
		params.gravity = Gravity.BOTTOM;
		params.bottomMargin = mBottomBarHeight;
		addView(mLine, params);
		mLine.setVisibility(INVISIBLE);

		mWaitAnimView = new WaitAnimDialog.WaitAnimView(mContext);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		addView(mWaitAnimView, params);
		mUiEnabled = false;

		mFolderImage.setImageResource(R.drawable.cloudalbum_default_placeholder);
		mFolderName.setText(R.string.system_album);
	}

	private PhotoStore.ILoadComplete loadCompleteListener = new PhotoStore.ILoadComplete() {
		@Override
		public void onCompleted(final List<FolderInfo> folderInfos, boolean update) {
			if (!update || mFolderAdapter == null) {
				((Activity)mContext).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mWaitAnimView.setVisibility(GONE);
						setData();
						setListener();

						mUiEnabled = true;
					}
				});
			} else if (!folderInfos.isEmpty() && folderInfos != mPhotoStore.getFolderInfos()){
				((Activity)mContext).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mPhotoStore.setFolderInfos(folderInfos);
						mFolderAdapter.notifyDataSetChanged();
						FolderInfo folderInfo = mPhotoStore.getFolderInfo(mFolderIndex);
						String path = folderInfo.getCover();
						if (TextUtils.isEmpty(path)) {
							mFolderImage.setImageResource(R.drawable.cloudalbum_default_placeholder);
						} else {
							Glide.with(mContext).load(path).asBitmap().into(mFolderImage);
						}
					}
				});
			}
		}
	};

	@Override
	protected void setData() {
//		mPhotoStore.clearCache();
		initPhotoItemsAndAdapter(true);
		initFolderAdapter();

		FolderInfo folderInfo = mPhotoStore.getFolderInfo(mFolderIndex);
		String path = folderInfo.getCover();
		if (TextUtils.isEmpty(path)) {
			mFolderImage.setImageResource(R.drawable.cloudalbum_default_placeholder);
		} else {
			Glide.with(mContext).load(path).asBitmap().into(mFolderImage);
		}
		mFolderName.setText(folderInfo.getName());
	}

	@Override
	protected void setListener() {
		setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrolled(int dx, int dy) {

//				sScrollOffset += dy;

				if (!mUiEnabled) {
					return;
				}

				if (dy > 0 && -mHasScrollY < mTopBarHeight) {
					// 手指从下向上滑动
					// 还没完全消失
					mHasScrollY -= dy;
					if (mTopBarHeight <= -mHasScrollY) {
						mHasScrollY = -mTopBarHeight;
					}

				} else if (dy < 0 && mHasScrollY < 0) {
					// 手指从上往下滑动

					if (-mHasScrollY < mTopBarHeight) {
						// 还没完全消失
						mHasScrollY -= dy;
						if (mHasScrollY >= 0) {
							mHasScrollY = 0;
						}
					} else if (Math.abs(dy) > mTouchSlop) {
						// 快速返回
						mHasScrollY = 0;
						mTopLayout.animate().translationY(0).setDuration(100);
						return;
					}
				}
				mTopLayout.setTranslationY(mHasScrollY);
			}
		});

		mPhotoAdapter.setOnPhotoItemClickListener(new PhotoAdapter.OnPhotoItemClickListener() {
			@Override
			public void onItemClick(int position) {

				if (!mUiEnabled || hasSelectPhoto) {
					return;
				}

				hasSelectPhoto = true;

				TongJi2.AddCountByRes(mContext, R.integer.相册_选中照片);
				MyBeautyStat.onClickByRes(R.string.选相册_选相册_主页面_选中照片);

				PhotoInfo photoInfo = mPhotoAdapter.getItem(position);

				PhotoStore.sPosition = mLayoutManager.findFirstVisibleItemPosition();
				View view = mLayoutManager.findViewByPosition(PhotoStore.sPosition);
				if (view != null) {
					PhotoStore.sOffset = view.getTop() - mTopBarHeight;
				}

				String folderName = mPhotoStore.getFolderInfo(mFolderIndex).getName();

				HashMap<String, Object> map = new HashMap<>();
				String[] imgs = new String[] {photoInfo.getImagePath()};
				PhotoStore.sLastFolderIndex = mFolderIndex;
				map.put("imgs", imgs);
				map.put("folder_name", folderName);
				map.put("index", mPhotoStore.getPhotoInfoIndex(folderName, photoInfo));
				map.put("from_camera", mFromCamera);
				map.put(DataKey.COLOR_FILTER_ID, mColorFilterId);
				mSite.onPhotoSelected(getContext(), map);
			}
		});

		mFolderAdapter.setOnFolderItemClickListener(new FolderAdapter.OnFolderItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				if (!mUiEnabled) {
					return;
				}

				if (position != mFolderIndex) {
                    TongJi2.AddCountByRes(getContext(), R.integer.相册_选文件夹);
					MyBeautyStat.onClickByRes(R.string.选相册_选相册_主页面_选文件夹);
					updatePhotos(position);
				} else {
					closeFolderList();
				}
			}
		});

		mBottomLayout.setOnTouchListener(new OnAnimationClickListener() {
			@Override
			public void onAnimationClick(View v) {
				if (!mUiEnabled) {
					return;
				}

				if (!mShowFolder && mPhotoStore.getFolderInfos().size() > 1) {
					openFolderList();
				} else if (mShowFolder) {
					closeFolderList();
				}
			}

			@Override
			public void onTouch(View v) {

			}

			@Override
			public void onRelease(View v) {

			}
		});

		mBackView.setOnTouchListener(new OnAnimationClickListener() {
			@Override
			public void onAnimationClick(View v) {
				if (!mUiEnabled) {
					return;
				}

				TongJi2.AddCountByRes(mContext, R.integer.相册_返回按钮);
				MyBeautyStat.onClickByRes(R.string.选相册_选相册_主页面_返回按钮);
				onBack();
			}

			@Override
			public void onTouch(View v) {

			}

			@Override
			public void onRelease(View v) {

			}
		});

		mCameraView.setOnTouchListener(new OnAnimationClickListener() {
			@Override
			public void onAnimationClick(View v) {
				if (!mUiEnabled) {
					return;
				}

                TongJi2.AddCountByRes(getContext(), R.integer.相册_拍照);
				MyBeautyStat.onClickByRes(R.string.选相册_选相册_主页面_拍照);
				mSite.onOpenCamera(getContext());
			}

			@Override
			public void onTouch(View v) {

			}

			@Override
			public void onRelease(View v) {

			}
		});

		mTransparentBg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mUiEnabled) {
					return;
				}

				closeFolderList();
			}
		});

		mTopLayout.setOnTouchListener(new OnAnimationClickListener() {
			@Override
			public void onAnimationClick(View v) {
				if (!mUiEnabled) {
					return;
				}

				mPhotoGridView.smoothScrollToPosition(0);
			}

			@Override
			public void onTouch(View v) {

			}

			@Override
			public void onRelease(View v) {

			}
		});
	}

	/**
	 * 打开文件夹列表
	 */
	private void openFolderList() {

		if ((mOpenFolderAnimator != null && mOpenFolderAnimator.isRunning()) ||
				(mCloseFolderAnimator != null && mCloseFolderAnimator.isRunning())) {
			return;
		}

		mShowFolder = true;
		mPhotoGridView.setEnabled(false);
		mIndicator.setImageResource(R.drawable.album_down);

		generateBgBitmap();

		mTopLayout.clearAnimation();

		restoreTopLayout();

		int animatorHeight = mFolderListView.getHeight();

		ObjectAnimator yAnimator1 = ObjectAnimator.ofFloat(mFolderListView, "translationY", animatorHeight, 0);
		ObjectAnimator yAnimator2 = ObjectAnimator.ofFloat(mHeader, "translationY", animatorHeight, 0);
		ObjectAnimator alphaAnimator1 = ObjectAnimator.ofFloat(mBg, "alpha", 0, 1);
		ObjectAnimator alphaAnimator2 = ObjectAnimator.ofFloat(mTransparentBg, "alpha", 0, 1);
		ObjectAnimator alphaAnimator3 = ObjectAnimator.ofFloat(mLine, "alpha", 0, 0.2f);

		mOpenFolderAnimator = new AnimatorSet();
		mOpenFolderAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
		mOpenFolderAnimator.setDuration(200);
		mOpenFolderAnimator.play(yAnimator1).with(yAnimator2)
				.with(alphaAnimator1).with(alphaAnimator2)
				.with(alphaAnimator3);
		mOpenFolderAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				mFolderListView.setVisibility(VISIBLE);
				mHeader.setVisibility(VISIBLE);
				mBg.setVisibility(VISIBLE);
				mTransparentBg.setVisibility(VISIBLE);
				mLine.setVisibility(VISIBLE);
			}
		});
		mOpenFolderAnimator.start();
	}

	/**
	 * 关闭文件夹列表
	 */
	private void closeFolderList() {

		if ((mOpenFolderAnimator != null && mOpenFolderAnimator.isRunning()) ||
				(mCloseFolderAnimator != null && mCloseFolderAnimator.isRunning())) {
			return;
		}

		mShowFolder = false;
		mIndicator.setImageResource(R.drawable.album_up);

		int animatorHeight = mFolderListView.getHeight();

		ObjectAnimator yAnimator1 = ObjectAnimator.ofFloat(mFolderListView, "translationY", 0, animatorHeight);
		ObjectAnimator yAnimator2 = ObjectAnimator.ofFloat(mHeader, "translationY", 0, animatorHeight);
		ObjectAnimator alphaAnimator1 = ObjectAnimator.ofFloat(mBg, "alpha", 1, 0);
		ObjectAnimator alphaAnimator2 = ObjectAnimator.ofFloat(mTransparentBg, "alpha", 1, 0);
		ObjectAnimator alphaAnimator3 = ObjectAnimator.ofFloat(mLine, "alpha", 0.2f, 0);

		mCloseFolderAnimator = new AnimatorSet();
		mCloseFolderAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
		mCloseFolderAnimator.setDuration(200);
		mCloseFolderAnimator.play(yAnimator1).with(yAnimator2)
				.with(alphaAnimator1).with(alphaAnimator2)
				.with(alphaAnimator3);
		mCloseFolderAnimator.addListener(new AnimatorListenerAdapter() {

			@Override
			public void onAnimationEnd(Animator animation) {
				mFolderListView.setVisibility(INVISIBLE);
				mHeader.setVisibility(INVISIBLE);
				mBg.setVisibility(INVISIBLE);
				mTransparentBg.setVisibility(INVISIBLE);
				mLine.setVisibility(INVISIBLE);
				mPhotoGridView.setEnabled(true);
			}
		});
		mCloseFolderAnimator.start();
	}

	/**
	 * 更新图片数据
	 *
	 * @param position 文件夹位置
	 */
	private void updatePhotos(int position) {
		FolderInfo folderInfo = mPhotoStore.getFolderInfo(position);
		mItems.clear();
		mItems.addAll(mPhotoStore.getPhotoInfos(folderInfo.getName(), 0));
		mPhotoAdapter.notifyDataSetChanged();
		String path = folderInfo.getCover();
		if (TextUtils.isEmpty(path)) {
			mFolderImage.setImageResource(R.drawable.cloudalbum_default_placeholder);
		} else {
			Glide.with(mContext).load(path).asBitmap().into(mFolderImage);
		}
		mFolderName.setText(folderInfo.getName());
		restoreTopLayout();

		closeFolderList();

		mFolderIndex = position;
		PhotoStore.sLastFolderIndex = mFolderIndex;
		mPhotoGridView.scrollToPosition(0);
	}

	@Override
	public void changeSkin() {
		ImageUtils.AddSkin(getContext(), mBackView);
		ImageUtils.AddSkin(getContext(), mCameraView);
		ImageUtils.AddSkin(getContext(), mIndicator);
	}

	private void restoreTopLayout() {
		if (mHasScrollY < 0) {
			mHasScrollY = 0;
			mTopLayout.setTranslationY(0);
		}
	}

	@Override
	public boolean onBack() {
		if (mShowFolder) {
			closeFolderList();
			return true;
		} else {
			PhotoStore.sPosition = 0;
			PhotoStore.sOffset = 0;
			if (mOpenFolderAnimator != null) {
				mOpenFolderAnimator.removeAllListeners();
			}

			if (mCloseFolderAnimator != null) {
				mCloseFolderAnimator.removeAllListeners();
			}

			mSite.onBack(getContext());
			return false;
		}
	}

	@Override
	public void onClose() {
		super.onClose();
		mPhotoStore.removeLoadCompleteListener(loadCompleteListener);
	}

	@Override
	public void onPageResult(int siteID, HashMap<String, Object> params) {
		super.onPageResult(siteID, params);

		hasSelectPhoto = false;
	}

	@Override
	public void notifyUpdate(int folderIndex) {
		super.notifyUpdate(folderIndex);
		FolderInfo folderInfo = mPhotoStore.getFolderInfo(folderIndex);
		String path = folderInfo.getCover();
		if (TextUtils.isEmpty(path)) {
			mFolderImage.setImageResource(R.drawable.cloudalbum_default_placeholder);
		} else {
			Glide.with(mContext).load(path).asBitmap().into(mFolderImage);
		}
		mFolderName.setText(folderInfo.getName());

		mTopLayout.setTranslationY(0);
		mHasScrollY = 0;
	}

	@Override
	public boolean isScrollToTop() {
		return super.isScrollToTop() && !mShowFolder;
	}

	private void generateBgBitmap() {

		if (mBgBitmap == null) {
			int width = ShareData.m_screenWidth / 4;
			int height = ShareData.m_screenHeight / 4;

			Bitmap tempBmp = CommonUtils.GetScreenBmp((Activity)mContext, width, height);

			mBgBitmap = BeautifyResMgr2.MakeBkBmp(tempBmp, width, height);

			mBg.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), mBgBitmap));

			tempBmp.recycle();
		}
	}

	public void SetColorFilterId(int colorFilterId) {
		mColorFilterId = colorFilterId;
	}
}
