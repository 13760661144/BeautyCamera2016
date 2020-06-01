package cn.poco.filterManage;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutionException;

import cn.poco.MaterialMgr2.BaseItem;
import cn.poco.MaterialMgr2.BaseItemInfo;
import cn.poco.MaterialMgr2.RoundProgressBar;
import cn.poco.advanced.ImageUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

import static cn.poco.filterManage.FilterPager.FilterPage;

/**
 * Created by: fwc
 * Date: 2017/3/29
 */
public class FilterItem extends LinearLayout {

	private int mLeftMargin;

	private FrameLayout mTopLayout;
	private TextView mFilterText;
	private FrameLayout mDownloadBtn;
	private ImageView mDownloadBg;
	private ImageView mUnlockIcon;
	private TextView mDownloadText;
	private RoundProgressBar mProgressBar;

	private OnFilterItemClick mOnFilterItemClick;

	private BaseItem.OnBaseItemCallback mCallback;
	private BaseItemInfo mData;

	private FilterPager mFilterPager;

	private LoadImageTask mLoadImageTask;

	public FilterItem(Context context) {
		super(context);

		mLeftMargin = (int)(28 / 720f * ShareData.m_screenWidth);

		setOrientation(VERTICAL);
//		setLayerType(LAYER_TYPE_SOFTWARE, null);
		initViews();
	}

	public void setData(BaseItemInfo info, boolean showLock, BaseItem.OnBaseItemCallback callback) {
		mData = info;
		mCallback = callback;

		if (info.m_state == BaseItemInfo.LOADING) {
			if (mCallback != null) {
				mCallback.OnDownload(FilterItem.this, mData, false);
			}
		}
		//加解锁icon
		if (info.m_lock && showLock) {
			mUnlockIcon.setVisibility(View.VISIBLE);
		} else {
			mUnlockIcon.setVisibility(View.GONE);
		}

		mLoadImageTask = new LoadImageTask(this);
		mLoadImageTask.execute();
	}

	public String[] getImageUrl() {
		if (mData != null) {
			return mData.m_themeRes.m_filter_theme_icon_url;
		}

		return null;
	}

	public String[] geFilterName() {
		if (mData != null) {
			return mData.m_themeRes.m_filter_theme_name;
		}

		return null;
	}

	private void initViews() {
		LayoutParams params;
		setPadding(0, 0, 0, ShareData.PxToDpi_xhdpi(40));

		mTopLayout = new FrameLayout(getContext());
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(88));
		addView(mTopLayout, params);
		{
			FrameLayout.LayoutParams params1;

			mFilterText = new TextView(getContext());
			mFilterText.setTextColor(0xd8000000);
			mFilterText.setText(R.string.material_filter);
			mFilterText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
			params1 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			params1.gravity = Gravity.CENTER_VERTICAL;
			params1.leftMargin = mLeftMargin;
			mTopLayout.addView(mFilterText, params1);

			mDownloadBtn = new FrameLayout(getContext());
			params1 = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(110), ShareData.PxToDpi_xhdpi(46));
			params1.gravity = Gravity.CENTER_VERTICAL | Gravity.END;
			params1.rightMargin = mLeftMargin / 2 + ShareData.PxToDpi_xhdpi(14);
			mTopLayout.addView(mDownloadBtn, params1);
			{
				mDownloadBg = new ImageView(getContext());
				mDownloadBg.setImageResource(R.drawable.new_material4_need_download);
				ImageUtils.AddSkin(getContext(), mDownloadBg);
				params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
				mDownloadBtn.addView(mDownloadBg, params);

				LinearLayout layout = new LinearLayout(getContext());
				layout.setOrientation(HORIZONTAL);
				layout.setGravity(Gravity.CENTER);
				params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				params1.gravity = Gravity.CENTER;
				mDownloadBtn.addView(layout, params1);
				{
					mUnlockIcon = new ImageView(getContext());
					mUnlockIcon.setVisibility(GONE);
					mUnlockIcon.setImageResource(R.drawable.download_more_lock_icon);
					params = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					params.gravity = Gravity.CENTER;
					params.rightMargin = ShareData.PxToDpi_xhdpi(6);
					layout.addView(mUnlockIcon, params);

					mDownloadText = new TextView(getContext());
					mDownloadText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
					mDownloadText.setTextColor(0xffffffff);
					mDownloadText.setGravity(Gravity.CENTER);
					mDownloadText.getPaint().setFakeBoldText(true);
					mDownloadText.setText(R.string.material_download);
					params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					params.gravity = Gravity.CENTER;
					layout.addView(mDownloadText, params);

					mProgressBar = new RoundProgressBar(getContext(), ShareData.PxToDpi_xhdpi(26), ShareData.PxToDpi_xhdpi(26));
					mProgressBar.setVisibility(View.GONE);
					mProgressBar.setMax(100);
					mProgressBar.SetProgressBgColor(0x66FFFFFF);
					params = new LayoutParams(ShareData.PxToDpi_xhdpi(26), ShareData.PxToDpi_xhdpi(26));
					params.gravity = Gravity.CENTER;
					layout.addView(mProgressBar, params);
				}
			}
		}

		mDownloadBtn.setOnTouchListener(mOnTouchListener);

		mFilterPager = new FilterPager(getContext());
		mFilterPager.setRadius(ShareData.PxToDpi_xhdpi(10));
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.leftMargin = params.rightMargin = ShareData.PxToDpi_xhdpi(38);
		addView(mFilterPager, params);
		mFilterPager.setVisibility(INVISIBLE);

		mFilterPager.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mOnFilterItemClick != null) {
					mOnFilterItemClick.onClickDetailItem(v);
				}
			}
		});
	}

	private OnTouchListener mOnTouchListener = new OnAnimationClickListener() {

		@Override
		public void onAnimationClick(View v) {
			if (v == mDownloadBtn) {
				if (mData != null && mData.m_state != BaseItemInfo.LOADING) {
					if (mData.m_state == BaseItemInfo.COMPLETE && mCallback != null) {
						mCallback.OnUse(mData, 0);
					} else if(mCallback != null) {
						mCallback.OnDownload(FilterItem.this, mData, true);
					}
				}
			}
		}

		@Override
		public void onTouch(View v) {

		}

		@Override
		public void onRelease(View v) {

		}
	};

	public void setDownloadBtnState(int state, int progress) {

		if (state == BaseItemInfo.PREPARE) {
			mProgressBar.setVisibility(View.GONE);
			mDownloadText.setText(R.string.material_download);
			mDownloadText.setVisibility(VISIBLE);
			ImageUtils.AddSkin(getContext(), mDownloadBg);
			mDownloadBg.setImageResource(R.drawable.new_material4_need_download);
		} else if (state == BaseItemInfo.LOADING) {
			mProgressBar.setProgress(progress);
			mProgressBar.setVisibility(View.VISIBLE);
			mDownloadText.setVisibility(GONE);
			ImageUtils.RemoveSkin(getContext(), mDownloadBg);
			mDownloadBg.setImageResource(R.drawable.new_material4_downloading_complete);
		} else if(state == BaseItemInfo.COMPLETE) {
			mProgressBar.setVisibility(View.GONE);
			mDownloadText.setText(R.string.material_use);
			mDownloadText.setVisibility(VISIBLE);
			ImageUtils.RemoveSkin(getContext(), mDownloadBg);
			mDownloadBg.setImageResource(R.drawable.new_material4_downloading_complete);
		} else if(state == BaseItemInfo.CONTINUE) {
			mProgressBar.setVisibility(View.GONE);
			mDownloadText.setText(R.string.material_download_continue);
			mDownloadText.setVisibility(VISIBLE);
			ImageUtils.AddSkin(getContext(), mDownloadBg);
			mDownloadBg.setImageResource(R.drawable.new_material4_need_download);
		}
	}

	public void setOnFilterItemClick(OnFilterItemClick onFilterItemClick) {
		mOnFilterItemClick = onFilterItemClick;
	}

	public interface OnFilterItemClick {
		void onClickDetailItem(View view);
	}

	@Override
	protected void onDetachedFromWindow() {
		if (mLoadImageTask != null && !mLoadImageTask.isCancelled()) {
			mLoadImageTask.cancel(true);
			mLoadImageTask.release();
			mLoadImageTask = null;
		}
		super.onDetachedFromWindow();
	}

	private static class LoadImageTask extends AsyncTask<Void, FilterPage, Void> {

		private Context mContext;

		private WeakReference<FilterItem> mReference;

		private String[] mUrls;
		private String[] mNames;

		public LoadImageTask(FilterItem filterItem) {
			mReference = new WeakReference<>(filterItem);
			mUrls = filterItem.getImageUrl();
			mNames = filterItem.geFilterName();
			mContext = filterItem.getContext();
		}

		@Override
		protected void onProgressUpdate(FilterPage... values) {
			if (values != null && values.length > 0 && mReference != null) {
				FilterItem item = mReference.get();
				if (item != null) {
					if (item.mFilterPager.getVisibility() != VISIBLE) {
						item.mFilterPager.addItem(values[0], true);
						item.mFilterPager.setVisibility(VISIBLE);
					} else {
						item.mFilterPager.addItem(values[0], false);
					}
				}
			}
		}

		@Override
		protected Void doInBackground(Void... params) {

			int width = ShareData.m_screenWidth - ShareData.PxToDpi_xhdpi(76);
			FilterPage page;

			if (mUrls == null || mNames == null) {
				return null;
			}

			int size = Math.min(mUrls.length, mNames.length);

			for (int i = 0; i < size; i++) {
				try {
					Bitmap bitmap = Glide.with(mContext).load(mUrls[i]).asBitmap().into(width, width).get();
					if (bitmap != null) {
						page = new FilterPage();
						page.bitmap = bitmap;
						page.name = mNames[i];
						publishProgress(page);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}

			return null;
		}

		public void release() {
			mContext = null;
			mUrls = null;
			mReference.clear();
			mReference = null;
		}
	}
}
