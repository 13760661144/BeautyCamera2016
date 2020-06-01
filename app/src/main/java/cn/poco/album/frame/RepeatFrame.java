package cn.poco.album.frame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.poco.advanced.ImageUtils;
import cn.poco.album.adapter.FolderAdapter;
import cn.poco.album.adapter.PhotoAdapter;
import cn.poco.album.adapter.SelectedAdapter;
import cn.poco.album.model.FolderInfo;
import cn.poco.album.site.AlbumSite;
import cn.poco.album.utils.ListItemDecoration;
import cn.poco.cloudalbumlibs.utils.CloudAlbumDialog;
import cn.poco.cloudalbumlibs.utils.T;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

/**
 * Created by: fwc
 * Date: 2016/12/15
 */
public class RepeatFrame extends BaseFrame {

	private int mMin;
	private int mMax;

	private LinearLayout mLeftLayout;
	private TextView mAlbumText;
	private TextView mTitleView;

	private LinearLayout mSelectPanel;
	private TextView mSelectTip;
	private FrameLayout mAddButton;
	private RecyclerView mSelectedList;

	private SelectedAdapter mSelectedAdapter;
	private List<String> mSelectedPhotos;

	private float mStartY;
	private int mStartHeight;

	private CloudAlbumDialog mTipDialig;
	private String mSelectedPhotoPath;

	public RepeatFrame(Context context, AlbumSite site, int min, int max, boolean repeatTip) {
		super(context, site);

		mMin = min;
		mMax = max;

		if (repeatTip) {
			mTipDialig = new CloudAlbumDialog(mContext,
												   ViewGroup.LayoutParams.WRAP_CONTENT,
												   ViewGroup.LayoutParams.WRAP_CONTENT);
			ImageUtils.AddSkin(mContext, mTipDialig.getOkButtonBg());
			mTipDialig.setCancelButtonText(R.string.cancel)
					.setOkButtonText(R.string.add)
					.setMessage(R.string.repeat_message)
					.setListener(new CloudAlbumDialog.OnButtonClickListener() {
						@Override
						public void onOkButtonClick() {
							mTipDialig.dismiss();
							addPhoto();
						}

						@Override
						public void onCancelButtonClick() {
							mTipDialig.dismiss();
						}
					});
		}

		// 放在这里，等相关成员变量初始化完成
		updateState();
	}

	public void setSelectedList(List<String> selectedList) {
		if (selectedList != null && !selectedList.isEmpty()) {
			mSelectedPhotos.addAll(selectedList);
			mSelectedAdapter.notifyItemRangeInserted(0, selectedList.size());

			updateState();
		}
	}

	@Override
	protected void initDatas() {
		mTopBarHeight = ShareData.PxToDpi_xhdpi(90);
		mBottomBarHeight = ShareData.PxToDpi_xhdpi(298);

		mShowFolder = true;
	}

	@Override
	protected void initViews() {
		LayoutParams params;

		mFolderListView.setPadding(0, ShareData.PxToDpi_xhdpi(11) + mTopBarHeight, 0, ShareData.PxToDpi_xhdpi(11));
		mFolderListView.setClipToPadding(false);
		mFolderListView.setHasFixedSize(true);
		params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.bottomMargin = mBottomBarHeight;
		addView(mFolderListView, params);

		mPhotoGridView.setPadding(0, mTopBarHeight, 0, 0);
		mPhotoGridView.setClipToPadding(false);
		params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.bottomMargin = mBottomBarHeight;
		addView(mPhotoGridView, params);
		mPhotoGridView.setVisibility(INVISIBLE);

		mTopLayout.setAlpha(0.96f);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mTopBarHeight);
		addView(mTopLayout, params);
		{
			RelativeLayout.LayoutParams params1;

			mLeftLayout = new LinearLayout(mContext);
			mLeftLayout.setOrientation(LinearLayout.HORIZONTAL);
			mLeftLayout.setPadding(ShareData.PxToDpi_xhdpi(18), 0, ShareData.PxToDpi_xhdpi(18), 0);
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

//			View line = new View(mContext);
//			line.setBackgroundColor(0xffeeeeee);
//			params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(1));
//			params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//			mTopLayout.addView(line, params1);
		}

		mSelectPanel = new LinearLayout(mContext);
		mSelectPanel.setOrientation(LinearLayout.VERTICAL);
		mSelectPanel.setBackgroundColor(Color.WHITE);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mBottomBarHeight);
		params.gravity = Gravity.BOTTOM;
		addView(mSelectPanel, params);
		{
			LinearLayout.LayoutParams params1;

			View line = new View(mContext);
			line.setBackgroundColor(0xffeeeeee);
			params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(1));
			mSelectPanel.addView(line, params1);

			RelativeLayout selectBar = new RelativeLayout(mContext);
			params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(88));
			mSelectPanel.addView(selectBar, params1);
			{
				RelativeLayout.LayoutParams params2;

				mSelectTip = new TextView(mContext);
				mSelectTip.setTextColor(Color.BLACK);
				mSelectTip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				params2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				params2.leftMargin = ShareData.PxToDpi_xhdpi(30);
				params2.addRule(RelativeLayout.CENTER_VERTICAL);
				selectBar.addView(mSelectTip, params2);

				mAddButton = new FrameLayout(mContext);
				params2 = new RelativeLayout.LayoutParams(ShareData.PxToDpi_xhdpi(140), ShareData.PxToDpi_xhdpi(60));
				params2.rightMargin = ShareData.PxToDpi_xhdpi(30);
				params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				params2.addRule(RelativeLayout.CENTER_VERTICAL);
				selectBar.addView(mAddButton, params2);
				{
					LayoutParams params3;

					ImageView imageView = new ImageView(mContext);
					imageView.setImageResource(R.drawable.album_add_button_bg);
					params3 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
					ImageUtils.AddSkin(mContext, imageView);
					mAddButton.addView(imageView, params3);

					TextView textView = new TextView(mContext);
					textView.setTextColor(Color.WHITE);
					textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
					textView.setText(R.string.album_add_photo);
					textView.getPaint().setFakeBoldText(true);
					params3 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					params3.gravity = Gravity.CENTER;
					mAddButton.addView(textView, params3);
				}
			}

			mSelectedList = new RecyclerView(mContext);
			params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			mSelectedList.setPadding(ShareData.PxToDpi_xhdpi(10), 0, ShareData.PxToDpi_xhdpi(10), 0);
			mSelectedList.setClipToPadding(false);
			mSelectedList.setHasFixedSize(true);
			mSelectedList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
			mSelectedList.addItemDecoration(new ListItemDecoration(ShareData.PxToDpi_xhdpi(10), ListItemDecoration.HORIZONTAL));
			mSelectPanel.addView(mSelectedList, params1);
		}
	}

	@Override
	protected void setData() {

		initPhotoItemsAndAdapter(false);
		initFolderAdapter();

		mTitleView.setText(R.string.album);

		mSelectedPhotos = new ArrayList<>();
		mSelectedAdapter = new SelectedAdapter(mContext, mSelectedPhotos);
		mSelectedList.setAdapter(mSelectedAdapter);
	}

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

		mPhotoAdapter.setOnPhotoItemClickListener(new PhotoAdapter.OnPhotoItemClickListener() {
			@Override
			public void onItemClick(int position) {
				mSelectedPhotoPath = mItems.get(position).getImagePath();
				if (mSelectedPhotos.contains(mSelectedPhotoPath) && mTipDialig != null) {
					mTipDialig.show();
				} else {
					addPhoto();
				}
			}
		});

		mSelectedAdapter.setOnDeleteClickListener(new SelectedAdapter.OnDeleteClickListener() {
			@Override
			public void onDelete(View view, int position) {
				mSelectedPhotos.remove(position);
				try {
					mSelectedAdapter.notifyItemRemoved(position);
				} catch (Exception e) {
					e.printStackTrace();
				}
				updateState();
			}
		});

		mAddButton.setOnTouchListener(new OnAnimationClickListener() {
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
					Map<String, Object> map = new HashMap<>();
					String[] imgs = new String[size];
					for (int i = 0; i < imgs.length; i++) {
						imgs[i] = mSelectedPhotos.get(i);
					}

					map.put("imgs", imgs);
					mSite.onPhotoSelected(getContext(), map);
				}
			}

			@Override
			public void onTouch(View v) {

			}

			@Override
			public void onRelease(View v) {

			}
		});
	}

	private void addPhoto() {
		if (mSelectedPhotos.size() >= mMax) {
			String tip = mContext.getResources().getString(R.string.over_max_photo_tip, mMax);
			T.showShort(mContext, tip);
		} else {
			mSelectedPhotos.add(mSelectedPhotoPath);
			mSelectedAdapter.notifyItemInserted(mSelectedPhotos.size() - 1);
			mSelectedList.scrollToPosition(mSelectedPhotos.size() - 1);
			updateState();
		}
	}

	private void openPhotos(View folderItem, int position) {

		if (!mShowFolder) {
			return;
		}

		mShowFolder = false;

		if (mFolderIndex != position) {
			FolderInfo folderInfo = mPhotoStore.getFolderInfo(position);
			mItems.clear();
			mItems.addAll(mPhotoStore.getPhotoInfos(folderInfo.getName(), 0));
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
		AnimatorSet set = new AnimatorSet();
		set.setDuration(300);
		set.setInterpolator(new AccelerateDecelerateInterpolator());
		set.play(alphaAnimator).with(yAnimator).with(scaleAnimator).with(alphaAnimator2);
		set.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
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
		AnimatorSet set = new AnimatorSet();
		set.setDuration(300);
		set.setInterpolator(new AccelerateDecelerateInterpolator());
		set.play(alphaAnimator).with(yAnimator).with(scaleAnimator).with(alphaAnimator2);
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
		} else {
			closePhotos();
			return true;
		}
	}

	private String getTip(int hasSelected, int max) {
		return mContext.getResources().getQuantityString(R.plurals.repeat_select_tip, hasSelected, hasSelected, max);
	}

	private void updateState() {
		if (mSelectedPhotos.size() < mMin) {
			mAddButton.setAlpha(0.2f);
			mAddButton.setEnabled(false);
		} else {
			mAddButton.setAlpha(1);
			mAddButton.setEnabled(true);
		}

		mSelectTip.setText(getTip(mSelectedPhotos.size(), mMax));
	}

}
