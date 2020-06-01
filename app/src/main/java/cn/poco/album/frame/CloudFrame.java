package cn.poco.album.frame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.poco.advanced.ImageUtils;
import cn.poco.album.PhotoStore;
import cn.poco.album.adapter.FolderAdapter;
import cn.poco.album.adapter.PhotoAdapter;
import cn.poco.album.model.FolderInfo;
import cn.poco.album.model.PhotoInfo;
import cn.poco.album.site.AlbumSite;
import cn.poco.cloudalbumlibs.utils.T;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.widget.AlertDialogV1;
import my.beautyCamera.R;

/**
 * Created by: fwc
 * Date: 2016/12/19
 */
public class CloudFrame extends BaseFrame {

	private int mMin;
	private int mMax;

	private LinearLayout mLeftLayout;
	private TextView mAlbumText;
	private TextView mTitleView;

	private TextView mCancelText;

	private RelativeLayout mBottomLayout;
	private TextView mSelectTip;
	private FrameLayout mUploadButton;
	private BigPhotoFrame mBigPhotoFrame;

	private List<String> mSelectedPhotos;

	private FrameLayout mHeader;
	private TextView mCloudAlbumName;

	private float mStartY;
	private int mStartHeight;

	private boolean mOpenBigPhoto = false;
	private AnimatorSet mOpenBigFrameAnimator;
	private AnimatorSet mCloseBigFrameAnimator;

	private long mFreeVolume;
	private long mSize;

	public CloudFrame(Context context, AlbumSite site, String albumName, long freeVolume, int min, int max) {
		super(context, site);

		mPhotoStore.addLoadCompleteListener(loadCompleteListener);

		mPhotoAdapter.setOnPhotoItemClickListener(mOnPhotoItemClickListener);
		mPhotoAdapter.setCloudMode();
		mCloudAlbumName.setText(getResources().getString(R.string.upload_to_cloud_album_tip, albumName));

		mMin = min;
		mMax = max;

		mFreeVolume = freeVolume;

		updateState();
	}

	@Override
	protected void initDatas() {
		mTopBarHeight = ShareData.PxToDpi_xhdpi(90);
		mBottomBarHeight = ShareData.PxToDpi_xhdpi(98);

		mShowFolder = true;
	}

	@Override
	protected void initViews() {
		LayoutParams params;

		mFolderListView.setPadding(0, ShareData.PxToDpi_xhdpi(24), 0, ShareData.PxToDpi_xhdpi(24));
		mFolderListView.setClipToPadding(false);
		mFolderListView.setHasFixedSize(true);
		params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.topMargin = mTopBarHeight;
		params.bottomMargin = mBottomBarHeight;
		addView(mFolderListView, params);

		mHeader = new FrameLayout(mContext);
		mHeader.setBackgroundColor(0xfff1f1f1);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(64));
		params.topMargin = mTopBarHeight;
		addView(mHeader, params);
		{
			mCloudAlbumName = new TextView(mContext);
			mCloudAlbumName.setTextColor(0xff333333);
			mCloudAlbumName.setMaxLines(1);
			mCloudAlbumName.setEllipsize(TextUtils.TruncateAt.END);
			mCloudAlbumName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
			FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
																			ViewGroup.LayoutParams.WRAP_CONTENT);
			params1.gravity = Gravity.CENTER;
			params1.leftMargin = params1.rightMargin = ShareData.PxToDpi_xhdpi(16);
			mHeader.addView(mCloudAlbumName, params1);
		}
		mHeader.setVisibility(INVISIBLE);

		params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.topMargin = mTopBarHeight + ShareData.PxToDpi_xhdpi(64);
		params.bottomMargin = mBottomBarHeight;
		addView(mPhotoGridView, params);
		mPhotoGridView.setVisibility(INVISIBLE);
		// 解决闪屏问题
		((SimpleItemAnimator)mPhotoGridView.getItemAnimator()).setSupportsChangeAnimations(false);

		mBigPhotoFrame = new BigPhotoFrame(mContext);
		params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.topMargin = mTopBarHeight + ShareData.PxToDpi_xhdpi(64);
		params.bottomMargin = mBottomBarHeight;
		addView(mBigPhotoFrame, params);
		mBigPhotoFrame.setVisibility(INVISIBLE);

		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mTopBarHeight);
		addView(mTopLayout, params);
		{
			RelativeLayout.LayoutParams params1;

			mLeftLayout = new LinearLayout(mContext);
			mLeftLayout.setOrientation(LinearLayout.HORIZONTAL);
			mLeftLayout.setPadding(ShareData.PxToDpi_xhdpi(16), 0, ShareData.PxToDpi_xhdpi(16), 0);
			params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
			mTopLayout.addView(mLeftLayout, params1);
			{
				LinearLayout.LayoutParams params2;

				ImageView backView = new ImageView(mContext);
				backView.setImageResource(R.drawable.album_back);
				ImageUtils.AddSkin(getContext(), backView);
				params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				params2.gravity = Gravity.CENTER_VERTICAL;
				mLeftLayout.addView(backView, params2);

				mAlbumText = new TextView(mContext);
				mAlbumText.setText(R.string.album);
				mAlbumText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
				mAlbumText.setTextColor(ImageUtils.GetSkinColor(0xffe75988));
				params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				params2.gravity = Gravity.CENTER_VERTICAL;
				mLeftLayout.addView(mAlbumText, params2);
				mAlbumText.setVisibility(GONE);
			}

			mTitleView = new TextView(mContext);
			mTitleView.setTextColor(0xe5000000);
			mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
			params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params1.addRule(RelativeLayout.CENTER_IN_PARENT);
			mTopLayout.addView(mTitleView, params1);

			mCancelText = new TextView(mContext);
			mCancelText.setText(R.string.cancel);
			mCancelText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			mCancelText.setTextColor(ImageUtils.GetSkinColor(0xffe75988));
			mCancelText.setGravity(Gravity.CENTER);
			mCancelText.setPadding(ShareData.PxToDpi_xhdpi(28), 0, ShareData.PxToDpi_xhdpi(28), 0);
			params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
			params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			mTopLayout.addView(mCancelText, params1);
			mCancelText.setVisibility(GONE);

			View line = new View(mContext);
			line.setBackgroundColor(0xffcccccc);
			params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(1));
			params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			mTopLayout.addView(line, params1);
		}

		mBottomLayout = new RelativeLayout(mContext);
		mBottomLayout.setBackgroundColor(Color.WHITE);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mBottomBarHeight);
		params.gravity = Gravity.BOTTOM;
		addView(mBottomLayout, params);
		{
			RelativeLayout.LayoutParams params1;

			View line = new View(mContext);
			line.setBackgroundColor(0xffcccccc);
			params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(1));
			mBottomLayout.addView(line, params1);

			mSelectTip = new TextView(mContext);
			mSelectTip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
			mSelectTip.setTextColor(0xff666666);
			params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params1.leftMargin = ShareData.PxToDpi_xhdpi(20);
			params1.addRule(RelativeLayout.CENTER_VERTICAL);
			mBottomLayout.addView(mSelectTip, params1);

			mUploadButton = new FrameLayout(mContext);
			params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params1.rightMargin = ShareData.PxToDpi_xhdpi(20);
			params1.addRule(RelativeLayout.CENTER_VERTICAL);
			params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			mBottomLayout.addView(mUploadButton, params1);
			{
				LayoutParams params2;

				ImageView imageView = new ImageView(mContext);
				imageView.setImageResource(R.drawable.album_upload_bg);
				ImageUtils.AddSkin(mContext, imageView);
				params2 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				mUploadButton.addView(imageView, params2);

				TextView textView = new TextView(mContext);
				textView.setTextColor(Color.WHITE);
				textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				textView.setText(R.string.upload);
				params2 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				params2.gravity = Gravity.CENTER;
				mUploadButton.addView(textView, params2);
			}
		}
	}

	@Override
	protected void setData() {

		initPhotoItemsAndAdapter(false);

		initFolderAdapter();

		mSelectedPhotos = new ArrayList<>();

		mTitleView.setText(R.string.album);
	}

	private PhotoStore.ILoadComplete loadCompleteListener = new PhotoStore.ILoadComplete() {
		@Override
		public void onCompleted(final List<FolderInfo> folderInfos, boolean update) {
			if (update && mFolderAdapter != null && !folderInfos.isEmpty() && folderInfos != mPhotoStore.getFolderInfos()) {
				((Activity)mContext).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mPhotoStore.setFolderInfos(folderInfos);
						mFolderAdapter.notifyDataSetChanged();
					}
				});
			}
		}
	};

	@Override
	protected void setListener() {

		mLeftLayout.setOnTouchListener(new OnAnimationClickListener() {
			@Override
			public void onAnimationClick(View v) {
				onBack();
			}

			@Override
			public void onTouch(View v) {

			}

			@Override
			public void onRelease(View v) {

			}
		});

		mFolderAdapter.setOnFolderItemClickListener(new FolderAdapter.OnFolderItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				openPhotos(view, position);
			}
		});

		mUploadButton.setOnTouchListener(new OnAnimationClickListener() {
			@Override
			public void onAnimationClick(View v) {

				int size = mSelectedPhotos.size();
				if (size < mMin) {
					String tip = mContext.getString(R.string.less_min_photo_tip, mMin);
					T.showShort(mContext, tip);
				} else if (size > mMax) {
					String tip = mContext.getString(R.string.over_max_photo_tip, mMax);
					T.showShort(mContext, tip);
				} else {

					calculateSize();
					if (mFreeVolume - mSize < 0) {
						showHasNoMemoryDialog();
					} else {
						Map<String, Object> map = new HashMap<>();
						String[] imgs = new String[size];
						for (int i = 0; i < imgs.length; i++) {
							imgs[i] = mSelectedPhotos.get(i);
						}

						mPhotoStore.setSelected(false);

						map.put("imgs", imgs);
						map.put("size", mSize);
						mSite.onPhotoSelected(getContext(), map);
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

		mPhotoAdapter.setOnLookBigListener(new PhotoAdapter.OnLookBigListener() {
			@Override
			public void onClick(View view, int position) {

				if (!mOpenBigPhoto && (mCloseBigFrameAnimator == null || !mCloseBigFrameAnimator.isRunning())) {
					mOpenBigPhoto = true;
					openBigPhotoFrame(view, position);
				}
			}
		});

		mBigPhotoFrame.setOnPhotoSelectListener(new BigPhotoFrame.OnPhotoSelectListener() {
			@Override
			public boolean onSelect(int position) {
				mOnPhotoItemClickListener.onItemClick(position);
				return mAddSuccrss;
			}
		});
		mBigPhotoFrame.setOnLoadListener(new BigPhotoFrame.OnLoadListener() {
			@Override
			public void onLoad(List<PhotoInfo> photoInfos) {
				mItems.addAll(photoInfos);
				mPhotoAdapter.notifyDataSetChanged();
			}
		});

		mCancelText.setOnTouchListener(new OnAnimationClickListener() {
			@Override
			public void onAnimationClick(View v) {
				mSite.onBack(getContext());
			}

			@Override
			public void onTouch(View v) {

			}

			@Override
			public void onRelease(View v) {

			}
		});
	}

	private void openBigPhotoFrame(View view, final int position) {

		float startX = view.getX();
		float startY = view.getY();
		int startWidth = view.getWidth();
		int startHeight = view.getHeight();

		float endX = 0;
		float endY = mTopBarHeight + ShareData.PxToDpi_xhdpi(64);
		int endWidth = mBigPhotoFrame.getWidth();
		int endHeight = mBigPhotoFrame.getHeight();

		float startScaleX = startWidth * 1.0f / endWidth;
		float startScaleY = startHeight * 1.0f / endHeight;

		mBigPhotoFrame.setPivotX(0);
		mBigPhotoFrame.setPivotY(0);

		ObjectAnimator xAnimator = ObjectAnimator.ofFloat(mBigPhotoFrame, "x", startX, endX);
		ObjectAnimator yAnimator = ObjectAnimator.ofFloat(mBigPhotoFrame, "y", startY + endY, endY);
		ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(mBigPhotoFrame, "scaleX", startScaleX, 1f);
		ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(mBigPhotoFrame, "scaleY", startScaleY, 1f);
		ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(mBigPhotoFrame, "alpha", 0, 1);

		mOpenBigFrameAnimator = new AnimatorSet();
		mOpenBigFrameAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
		mOpenBigFrameAnimator.setDuration(300);
		mOpenBigFrameAnimator.play(xAnimator).with(yAnimator).with(scaleXAnimator)
				.with(scaleYAnimator).with(alphaAnimator);
		mOpenBigFrameAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				mBigPhotoFrame.setData(mFolderIndex, mItems, position);
				mBigPhotoFrame.setVisibility(VISIBLE);
			}
		});
		mOpenBigFrameAnimator.start();
	}

	public void closeBigPhotoFrame(View view) {

		if (view != null) {
			float startX = 0;
			float startY = mTopBarHeight + ShareData.PxToDpi_xhdpi(64);
			int startWidth = mBigPhotoFrame.getWidth();
			int startHeight = mBigPhotoFrame.getHeight();

			float endX = view.getX();
			float endY = view.getY();
			int endWidth = view.getWidth();
			int endHeight = view.getHeight();

			float endScaleX = endWidth * 1.0f / startWidth;
			float endScaleY = endHeight * 1.0f / startHeight;

			mBigPhotoFrame.setPivotX(0);
			mBigPhotoFrame.setPivotY(0);

			ObjectAnimator xAnimator = ObjectAnimator.ofFloat(mBigPhotoFrame, "x", startX, endX);
			ObjectAnimator yAnimator = ObjectAnimator.ofFloat(mBigPhotoFrame, "y", startY, endY + startY);
			ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(mBigPhotoFrame, "scaleX", 1f, endScaleX);
			ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(mBigPhotoFrame, "scaleY", 1f, endScaleY);
			ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(mBigPhotoFrame, "alpha", 1, 0);

			mCloseBigFrameAnimator = new AnimatorSet();
			mCloseBigFrameAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
			mCloseBigFrameAnimator.setDuration(300);
			mCloseBigFrameAnimator.play(xAnimator).with(yAnimator).with(scaleXAnimator)
					.with(scaleYAnimator).with(alphaAnimator);
			mCloseBigFrameAnimator.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mBigPhotoFrame.setVisibility(INVISIBLE);
					mBigPhotoFrame.setScaleX(1f);
					mBigPhotoFrame.setScaleY(1f);
				}
			});
			mCloseBigFrameAnimator.start();
		}
	}

	private boolean mAddSuccrss = true;

	private PhotoAdapter.OnPhotoItemClickListener mOnPhotoItemClickListener = new PhotoAdapter.OnPhotoItemClickListener() {
		@Override
		public void onItemClick(int position) {

			PhotoInfo photoInfo = mItems.get(position);
			if (photoInfo.isSelected()) {
				photoInfo.setSelected(false);
				mSelectedPhotos.remove(photoInfo.getImagePath());

				mPhotoAdapter.notifyItemChanged(position);
				updateState();
				mAddSuccrss = false;
			} else {
				if (mSelectedPhotos.size() >= mMax) {
					String tip = mContext.getResources().getString(R.string.over_max_photo_tip, mMax);
					T.showShort(mContext, tip);
					mAddSuccrss = false;
				} else {
					photoInfo.setSelected(true);
					mSelectedPhotos.add(photoInfo.getImagePath());
					mPhotoAdapter.notifyItemChanged(position);
					updateState();
					mAddSuccrss = true;
				}
			}

		}
	};

	private void openPhotos(View folderItem, int position) {

		if (!mShowFolder) {
			return;
		}

		mShowFolder = false;

		if (mFolderIndex != position) {
			FolderInfo folderInfo = mPhotoStore.getFolderInfo(position);
			mItems.clear();
			mItems.addAll(mPhotoStore.getPhotoInfos(folderInfo.getName(), 0));
			for (PhotoInfo info : mItems) {
				if (mSelectedPhotos.contains(info.getImagePath())) {
					info.setSelected(true);
				}
			}
			mPhotoAdapter.notifyDataSetChanged();

			mFolderIndex = position;
		}
		mPhotoGridView.scrollToPosition(0);

		mStartY = folderItem.getY();
		mStartHeight = folderItem.getHeight();

		float endY = mPhotoGridView.getY();
		int endHeight = mPhotoGridView.getHeight();

		float ratio = mStartHeight * 1f / endHeight;

		mPhotoGridView.setPivotY(0);
		ObjectAnimator yAnimator = ObjectAnimator.ofFloat(mPhotoGridView, "y", mStartY, endY);
		ObjectAnimator scaleAnimator = ObjectAnimator.ofFloat(mPhotoGridView, "scaleY", ratio, 1);
		ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(mPhotoGridView, "alpha", 0, 1);
		ObjectAnimator alphaAnimator2 = ObjectAnimator.ofFloat(mFolderListView, "alpha", 1, 0);
		ObjectAnimator alphaAnimator3 = ObjectAnimator.ofFloat(mHeader, "alpha", 0, 1);
		ObjectAnimator yAnimator2 = ObjectAnimator.ofFloat(mHeader, "translationY", -mHeader.getHeight(), 0);
		AnimatorSet set = new AnimatorSet();
		set.setDuration(300);
		set.setInterpolator(new AccelerateDecelerateInterpolator());
		set.play(alphaAnimator).with(yAnimator).with(scaleAnimator).with(alphaAnimator2)
				.with(alphaAnimator3).with(yAnimator2);
		set.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				mHeader.setVisibility(VISIBLE);
				mPhotoGridView.setVisibility(VISIBLE);
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				mFolderListView.setVisibility(INVISIBLE);
				mAlbumText.setVisibility(VISIBLE);
				mTitleView.setText(mPhotoStore.getFolderInfo(mFolderIndex).getName());
			}
		});
		set.start();
	}

	private void closePhotos() {

		if (mShowFolder) {
			return;
		}

		mShowFolder = true;

		final float endY = mPhotoGridView.getY();
		int endHeight = mPhotoGridView.getHeight();

		float ratio = mStartHeight * 1f / endHeight;

		mPhotoGridView.setPivotY(0);
		ObjectAnimator yAnimator = ObjectAnimator.ofFloat(mPhotoGridView, "y", endY, mStartY);
		ObjectAnimator scaleAnimator = ObjectAnimator.ofFloat(mPhotoGridView, "scaleY", 1, ratio);
		ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(mPhotoGridView, "alpha", 1, 0);
		ObjectAnimator alphaAnimator2 = ObjectAnimator.ofFloat(mFolderListView, "alpha", 0, 1);
		ObjectAnimator alphaAnimator3 = ObjectAnimator.ofFloat(mHeader, "alpha", 1, 0);
		ObjectAnimator yAnimator2 = ObjectAnimator.ofFloat(mHeader, "translationY", 0, -mHeader.getHeight());
		AnimatorSet set = new AnimatorSet();
		set.setDuration(300);
		set.setInterpolator(new AccelerateDecelerateInterpolator());
		set.play(alphaAnimator).with(yAnimator).with(scaleAnimator).with(alphaAnimator2)
				.with(alphaAnimator3).with(yAnimator2);
		set.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				mFolderListView.setVisibility(VISIBLE);
				mAlbumText.setVisibility(GONE);
				mTitleView.setText(R.string.album);
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				mPhotoGridView.setVisibility(INVISIBLE);
				mHeader.setVisibility(INVISIBLE);
				mHeader.setTranslationY(0);
				mPhotoGridView.setY(endY);
			}
		});
		set.start();
	}

	@Override
	public boolean onBack() {
		if (mShowFolder) {
			mSite.onBack(getContext());
			return false;
		} else if (mOpenBigPhoto) {
			if (mOpenBigFrameAnimator == null || !mOpenBigFrameAnimator.isRunning()) {
				mOpenBigPhoto = false;
				final int position = mBigPhotoFrame.getCurrentPosition();
				mLayoutManager.scrollToPosition(position);
				mBigPhotoFrame.post(new Runnable() {
					@Override
					public void run() {
						closeBigPhotoFrame(mLayoutManager.findViewByPosition(position));
					}
				});
			}
		} else {
			closePhotos();
			return true;
		}

		return true;
	}

	@Override
	public void onClose() {
		super.onClose();

		mPhotoStore.removeLoadCompleteListener(loadCompleteListener);
	}

	private void updateState() {
		if (mSelectedPhotos.isEmpty()) {
			mCancelText.setVisibility(GONE);
		} else {
			mCancelText.setVisibility(VISIBLE);
		}

		if (mSelectedPhotos.size() < mMin) {
			mUploadButton.setAlpha(0.2f);
			mUploadButton.setEnabled(false);
		} else {
			mUploadButton.setAlpha(1);
			mUploadButton.setEnabled(true);
		}

		mSelectTip.setText(getTip(mSelectedPhotos.size()));
	}

	private String getTip(int hasSelected) {
		return mContext.getResources().getQuantityString(R.plurals.has_select_photo, hasSelected, hasSelected);
	}

	private void showHasNoMemoryDialog() {
		final AlertDialogV1 dialogV1 = new AlertDialogV1(mContext);
		Window window = dialogV1.getWindow();
		if (window != null) {
			window.setDimAmount(0.3f);
			window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		}
		View view = LayoutInflater.from(mContext).inflate(cn.poco.cloudalbumlibs.R.layout.cloud_album_single_dialog_layout, null);

		TextView message = (TextView)view.findViewById(R.id.tv_message);
		View okView = view.findViewById(R.id.fl_ok);
		ImageView okButtonBg = (ImageView)view.findViewById(R.id.iv_button_bg);
		TextView okButton = (TextView)view.findViewById(R.id.tv_ok);

		ImageUtils.AddSkin(mContext, okButtonBg);
		message.setText(R.string.cloud_album_has_not_memory);
		okButton.setText(R.string.cloud_album_ok);
		okView.setOnTouchListener(new OnAnimationClickListener() {
			@Override
			public void onAnimationClick(View v) {
				dialogV1.dismiss();
			}

			@Override
			public void onTouch(View v) {

			}

			@Override
			public void onRelease(View v) {

			}
		});

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(569), ShareData.PxToDpi_xhdpi(324));
		dialogV1.setRadius(ShareData.PxToDpi_xhdpi(32));
		dialogV1.addContentView(view, params);
		dialogV1.show();
	}

	private void calculateSize() {
		File file;
		mSize = 0;
		for (String path : mSelectedPhotos) {
			file = new File(path);
			if (file.exists()) {
				mSize += file.length();
			}
		}
	}
}
