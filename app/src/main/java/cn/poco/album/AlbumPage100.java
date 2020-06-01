package cn.poco.album;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.advanced.ImageUtils;
import cn.poco.album.adapter.VideoAdapter;
import cn.poco.album.model.VideoInfo;
import cn.poco.album.site.AlbumSite100;
import cn.poco.album.utils.PhotoGridDivide;
import cn.poco.arWish.ArVideoAlbumPreviewPage;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.utils.WaitAnimDialog;
import my.beautyCamera.R;

/**
 * Created by: fwc
 * Date: 2017/9/5
 */
public class AlbumPage100 extends IPage {

	private Context mContext;
	private AlbumSite100 mSite;

	private VideoStore mVideoStore;

	private RecyclerView mPhotoGridView;
	private GridLayoutManager mLayoutManager;
	private VideoAdapter mPhotoAdapter;
	private ArrayList<VideoInfo> mItems;

	private ArVideoAlbumPreviewPage mPreviewPage;

	private int mTopBarHeight;

	private int mTouchSlop;

	private FrameLayout mTopLayout;
	private ImageView mBackView;
	private TextView mTitle;
//	private FrameLayout mContinueLayout;
	private int mCornerRadius;

	private int mHasScrollY;

	private WaitAnimDialog.WaitAnimView mWaitAnimView;

	private boolean mUiEnabled;

	/**
	 * 避免重复选择
	 */
	private boolean hasSelectPhoto = false;
	private int mDefSystemUiVisibility = -1;

	public AlbumPage100(Context context, BaseSite site) {
		super(context, site);

		MyBeautyStat.onPageStartByRes(R.string.ar祝福_送祝福_打开相册选视频);
		mContext = context;
		mSite = (AlbumSite100)site;
		initScreen();
		init();
	}

	private void initScreen()
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
			View decorView = ((Activity) getContext()).getWindow().getDecorView();
			mDefSystemUiVisibility = decorView.getSystemUiVisibility();
			decorView.setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_FULLSCREEN
							| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		}
		((Activity) getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	private void resetScreen()
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
			View decorView = ((Activity) getContext()).getWindow().getDecorView();
			if (mDefSystemUiVisibility != -1)
			{
				decorView.setSystemUiVisibility(mDefSystemUiVisibility);
			}
		}
		((Activity) getContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	private void init() {

		mVideoStore = VideoStore.getInstance(mContext);
		mVideoStore.init(mContext);

		mTouchSlop = 3;
		mTopBarHeight = ShareData.PxToDpi_xhdpi(90);
		mCornerRadius = ShareData.PxToDpi_xhdpi(25);

		initViews();
	}

	private void initViews() {
		LayoutParams params;

		mPhotoGridView = new RecyclerView(mContext);
		mPhotoGridView.setHasFixedSize(true);
		mLayoutManager = new GridLayoutManager(mContext, 3);
		mPhotoGridView.setLayoutManager(mLayoutManager);
		mPhotoGridView.setBackgroundColor(Color.WHITE);
		mPhotoGridView.addItemDecoration(new PhotoGridDivide(ShareData.PxToDpi_xhdpi(3), ShareData.PxToDpi_xhdpi(3), false));
		mPhotoGridView.setOverScrollMode(OVER_SCROLL_NEVER);
		mPhotoGridView.setPadding(0, mTopBarHeight, 0, 0);
		mPhotoGridView.setClipToPadding(false);
		params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addView(mPhotoGridView, params);

		mTopLayout = new FrameLayout(mContext);
		mTopLayout.setBackgroundColor(0xf4ffffff);
		mTopLayout.setClickable(true);
		params = new LayoutParams(LayoutParams.MATCH_PARENT, mTopBarHeight);
		addView(mTopLayout, params);
		{
			mBackView = new ImageView(mContext);
			mBackView.setImageResource(R.drawable.framework_back_btn);
			mBackView.setPadding(ShareData.PxToDpi_xhdpi(2), 0, ShareData.PxToDpi_xhdpi(2), 0);
			ImageUtils.AddSkin(getContext(), mBackView);
			params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
			params.gravity = Gravity.CENTER_VERTICAL;
			mTopLayout.addView(mBackView, params);

			mTitle = new TextView(mContext);
			mTitle.setTextColor(0xff333333);
			mTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			mTitle.setText(R.string.video_album_title);
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER;
			mTopLayout.addView(mTitle, params);

//			mContinueLayout = new FrameLayout(mContext);
//			ViewCompat.setBackground(mContinueLayout, DrawableUtils.shapeDrawable(ImageUtils.GetSkinColor(0xffe75988), mCornerRadius));
//			params = new LayoutParams(ShareData.PxToDpi_xhdpi(108), mCornerRadius * 2);
//			params.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
//			params.rightMargin = ShareData.PxToDpi_xhdpi(28);
//			mTopLayout.addView(mContinueLayout, params);
//			{
//				TextView continueText = new TextView(mContext);
//				continueText.setText(R.string.go_on);
//				continueText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
//				continueText.setTextColor(Color.WHITE);
//				params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//				params.gravity = Gravity.CENTER;
//				mContinueLayout.addView(continueText, params);
//			}
		}

		mWaitAnimView = new WaitAnimDialog.WaitAnimView(mContext);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		addView(mWaitAnimView, params);
		mUiEnabled = false;

		mPreviewPage = new ArVideoAlbumPreviewPage(getContext(), mSite);
		mPreviewPage.setVisibility(GONE);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		addView(mPreviewPage, params);
	}

	@Override
	public void SetData(HashMap<String, Object> params) {
		mVideoStore.addOnCompletedListener(mOnCompletedListener);
	}

	private boolean isOpenPreviewPage() {
		return mPreviewPage != null && mPreviewPage.getVisibility() == VISIBLE && mPreviewPage.getTranslationX() == 0;
	}

	@Override
	public void onBack() {
		if (isOpenPreviewPage()){
			if (mPreviewPage.isPreviewVideo())
			{
				mPreviewPage.stopVideo();
				mPreviewPage.hideVideoView();
				return;
			}
			mPreviewPage.showPreviewPage(false);
			return;
		}

		mSite.onBack(mContext);
	}

	@Override
	public void onClose() {
		if (mPreviewPage != null)
		{
			mPreviewPage.clearVideo();
		}
		resetScreen();
		mVideoStore.removeOnCompletedListener(mOnCompletedListener);
		MyBeautyStat.onPageEndByRes(R.string.ar祝福_送祝福_打开相册选视频);
	}

	@Override
	public void onPageResult(int siteID, HashMap<String, Object> params) {
		hasSelectPhoto = false;
	}

	public void setData() {
		mItems = new ArrayList<>();
		mItems.addAll(mVideoStore.getVideoInfoList());
		mPhotoAdapter = new VideoAdapter(mContext, mItems);
		mPhotoGridView.setAdapter(mPhotoAdapter);

		if (mPreviewPage != null)
		{
			mPreviewPage.setData(mItems);
		}
	}

	@SuppressWarnings("all")
	private void setListener() {

		mPhotoGridView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
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

		mPhotoAdapter.setOnPhotoItemClickListener(new VideoAdapter.OnPhotoItemClickListener() {

			@Override
			public void onItemClick(int position) {

				if (!mUiEnabled || hasSelectPhoto) {
					return;
				}

				hasSelectPhoto = true;

				VideoInfo info = mItems.get(position);

//				PhotoStore.sPosition = mLayoutManager.findFirstVisibleItemPosition();
//				View view = mLayoutManager.findViewByPosition(PhotoStore.sPosition);
//				if (view != null) {
//					PhotoStore.sOffset = view.getTop() - mTopBarHeight;
//				}

//				HashMap<String, Object> params = new HashMap<>();
//				params.put("data", info.getPath());
//
//				mSite.onVideoSelected(params);

				if (mPreviewPage != null)
				{
					mPreviewPage.setSelectedIndex(position);
					mPreviewPage.showPreviewPage(true);
				}

				hasSelectPhoto = false;
			}
		});

		mBackView.setOnTouchListener(new OnAnimationClickListener() {
			@Override
			public void onAnimationClick(View v) {
				if (!mUiEnabled) {
					return;
				}

				onBack();
			}

			@Override
			public void onTouch(View v) {

			}

			@Override
			public void onRelease(View v) {

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

	@Override
	public void onPause()
	{
		super.onPause();
		if (mPreviewPage != null)
		{
			mPreviewPage.pauseVideo();
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		if (mPreviewPage != null)
		{
			mPreviewPage.resumeVideo();
		}
	}

	private void restoreTopLayout() {
		if (mHasScrollY < 0) {
			mHasScrollY = 0;
			mTopLayout.setTranslationY(0);
		}
	}

	private VideoStore.OnCompletedListener mOnCompletedListener = new VideoStore.OnCompletedListener() {

		@Override
		public void onCompleted() {
			((Activity)mContext).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mWaitAnimView.setVisibility(GONE);
					setData();
					setListener();

					mUiEnabled = true;
				}
			});
		}
	};
}
