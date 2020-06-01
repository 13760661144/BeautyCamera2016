package cn.poco.filterManage;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.poco.MaterialMgr2.BaseItemInfo;
import cn.poco.MaterialMgr2.MgrUtils;
import cn.poco.MaterialMgr2.UnLockMgr;
import cn.poco.advanced.ImageUtils;
import cn.poco.beautify4.UiMode;
import cn.poco.credits.Credit;
import cn.poco.filterManage.adapter.FilterDetailAdapter;
import cn.poco.filterManage.model.FilterInfo;
import cn.poco.filterManage.site.FilterDetailSite;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;
import cn.poco.home.home4.Home4Page;
import cn.poco.resource.AbsDownloadMgr;
import cn.poco.resource.BaseRes;
import cn.poco.resource.DownloadMgr;
import cn.poco.resource.FilterRes;
import cn.poco.resource.FilterResMgr2;
import cn.poco.resource.FrameRes;
import cn.poco.resource.IDownload;
import cn.poco.resource.LockRes;
import cn.poco.resource.ResType;
import cn.poco.resource.ThemeRes;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.MemoryTipDialog;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

/**
 * Created by: fwc
 * Date: 2017/3/27
 * 滤镜素材详情页
 */
public class FilterDetailPage extends IPage {

	private int mBottomBarmHeight;

	private Context mContext;
	private FilterDetailSite mSite;

	private ImageView mBackView;

	private RecyclerView mRecyclerView;
	private FilterDetailAdapter mAdapter;
	private List<FilterInfo> mItems;

	private FrameLayout mBottomLayout;
	private FrameLayout mDownloadLayout;
	private ImageView mDownloadBg;
	private ImageView mLoading;
	private TextView mDownloadText;

	private Animation mLoadingAnimation;

	private boolean mUiEnabled = false;

	private ThemeRes mThemeRes;
	private ArrayList<FilterRes> mFilterResList;

	private int mState = BaseItemInfo.PREPARE;
	private boolean mLock = false;
	private int mProgress = 0;

	private MemoryTipDialog mDownloadFailedDlg;

	public FilterDetailPage(Context context, BaseSite site) {
		super(context, site);

		TongJiUtils.onPageStart(getContext(), R.string.素材中心_详情_滤镜);

		mContext = context;
		mSite = (FilterDetailSite)site;

		initDatas();
		initViews();
		initAnimators();
		initListeners();
	}

	/**
	 * 设置参数
	 *
	 * @param params 传入的数据
	 *               theme_res: ThemeRes 主题资源
	 */
	@Override
	public void SetData(HashMap<String, Object> params) {

		Object o = params.get("theme_res");
		if (o instanceof ThemeRes) {
			mThemeRes = (ThemeRes)o;
		}

		if (mThemeRes != null) {
			mFilterResList = FilterResMgr2.getInstance().GetResArr(mThemeRes.m_filterIDArr, false);
			mItems = new ArrayList<>();
			FilterInfo item = new FilterInfo();
			item.image = mThemeRes.m_filter_theme_icon_url != null && mThemeRes.m_filter_theme_icon_url.length > 0? mThemeRes.m_filter_theme_icon_url[0] : null;
			item.name = mThemeRes.m_filterName;
			item.description = mThemeRes.m_filterDetail;
			mItems.add(item);

			LockRes lockRes = MgrUtils.unLockTheme(mThemeRes.m_id);
			if (lockRes != null && lockRes.m_shareType != LockRes.SHARE_TYPE_NONE && TagMgr.CheckTag(mContext, Tags.THEME_UNLOCK + mThemeRes.m_id)) {
				mLock = true;
			}

			if (mFilterResList != null && !mFilterResList.isEmpty()) {

				for (FilterRes res : mFilterResList) {
					item = new FilterInfo();
					item.image = res.m_listThumbUrl;
					if (TextUtils.isEmpty(item.image)) {
						item.image = res.m_listThumbRes.toString();
					}
					item.name = res.m_name;
					mItems.add(item);
				}

				mState = MgrUtils.checkGroupDownloadState(mFilterResList, null);
				mProgress = 100 * MgrUtils.getM_completeCount() / mThemeRes.m_filterIDArr.length;
			}
			mAdapter = new FilterDetailAdapter(mContext, mItems, mThemeRes.m_filter_mask_color);
			mRecyclerView.setAdapter(mAdapter);
		}

		setDownloadBtnState(mState);

		mUiEnabled = true;
	}

	@Override
	public void onBack() {
		if (mUiEnabled) {
			mUiEnabled = false;

			HashMap<String, Object> params = null;
			if (mState == BaseItemInfo.COMPLETE) {
				params = new HashMap<>();
				params.put("is_download", true);
			}
			mSite.onBack(getContext(), params);
		}
	}

	@Override
	public void onResume() {
		TongJiUtils.onPageResume(getContext(), R.string.素材中心_详情_滤镜);
	}

	@Override
	public void onPause() {
		TongJiUtils.onPagePause(getContext(), R.string.素材中心_详情_滤镜);
	}

	@Override
	public void onClose() {
		mLoading.clearAnimation();

		if (mMyDownloadCB != null) {
			mMyDownloadCB.ClearAll();
			mMyDownloadCB = null;
		}

		mAdapter = null;
		mRecyclerView.setAdapter(null);
		Glide.get(getContext()).clearMemory();

		TongJiUtils.onPageEnd(getContext(), R.string.素材中心_详情_滤镜);
	}

	private void initDatas() {
		mBottomBarmHeight = ShareData.PxToDpi_xhdpi(96);
	}

	private void initViews() {
		LayoutParams params;

		if (!TextUtils.isEmpty(Home4Page.s_maskBmpPath)) {
			setBackgroundDrawable(new BitmapDrawable(cn.poco.imagecore.Utils.DecodeFile(Home4Page.s_maskBmpPath, null)));
		} else {
			setBackgroundResource(R.drawable.login_tips_all_bk);
		}

		// 白色遮罩层
		View mask = new View(getContext());
		mask.setBackgroundColor(0xb2ffffff);
		params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		mask.setLayoutParams(params);
		addView(mask);

		mRecyclerView = new RecyclerView(mContext);
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setPadding(0, 0, 0, mBottomBarmHeight);
		mRecyclerView.setClipToPadding(false);
		mRecyclerView.setOverScrollMode(OVER_SCROLL_NEVER);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		addView(mRecyclerView, params);

		mBackView = new ImageView(mContext);
		mBackView.setImageResource(R.drawable.business_btn_back);
		mBackView.setPadding(ShareData.PxToDpi_xhdpi(28), ShareData.PxToDpi_xhdpi(28), 0, 0);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		addView(mBackView, params);

		mBottomLayout = new FrameLayout(mContext);
		mBottomLayout.setBackgroundColor(0xf4ffffff);
		mBottomLayout.setClickable(true);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mBottomBarmHeight);
		params.gravity = Gravity.BOTTOM;
		addView(mBottomLayout, params);
		{
			mDownloadLayout = new FrameLayout(mContext);
			params = new LayoutParams(ShareData.PxToDpi_xhdpi(270), ShareData.PxToDpi_xhdpi(76));
			params.gravity = Gravity.CENTER;
			mBottomLayout.addView(mDownloadLayout, params);
			{
				mDownloadBg = new ImageView(mContext);
				mDownloadBg.setImageResource(R.drawable.new_material4_downloadall);
				mDownloadBg.setScaleType(ImageView.ScaleType.FIT_XY);
				ImageUtils.AddSkin(getContext(), mDownloadBg);
				params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
				mDownloadLayout.addView(mDownloadBg, params);

				mDownloadText = new TextView(getContext());
				mDownloadText.setTextColor(Color.WHITE);
				mDownloadText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				mDownloadText.setText(R.string.material_download);
				params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				params.gravity = Gravity.CENTER;
				mDownloadLayout.addView(mDownloadText, params);

				mLoading = new ImageView(mContext);
				mLoading.setImageResource(R.drawable.filter_loading);
				params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				params.gravity = Gravity.CENTER;
				mDownloadLayout.addView(mLoading, params);
				mLoading.setVisibility(INVISIBLE);
			}
		}

		MemoryTipDialog.Builder builder = new MemoryTipDialog.Builder(mContext);
		builder.setMessage(R.string.download_failed_content).setPositiveButton(R.string.know, null);
		mDownloadFailedDlg = builder.build();
	}

	private void initAnimators() {
		mLoadingAnimation = new RotateAnimation(0f, 359.9f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		mLoadingAnimation.setDuration(500);
		mLoadingAnimation.setInterpolator(new LinearInterpolator());
		mLoadingAnimation.setRepeatCount(Animation.INFINITE);
		mLoadingAnimation.setRepeatMode(Animation.RESTART);
	}

	private void initListeners() {

		mBackView.setOnTouchListener(mClickListener);

		mDownloadLayout.setOnTouchListener(mClickListener);
	}

	@Override
	public void onPageResult(int siteID, HashMap<String, Object> params) {

		if (siteID == SiteID.LOGIN || siteID == SiteID.REGISTER_DETAIL || siteID == SiteID.RESETPSW) {
			if (mUnLockMgr != null) {
				mUnLockMgr.UpdateCredit();
			}
		}
	}

	protected UnLockMgr mUnLockMgr;

	private void unLock() {
		TongJi2.AddCountByRes(getContext(), R.integer.素材中心_解锁);
		if (mUnLockMgr != null && mUnLockMgr.IsRecycle()) {
			mUnLockMgr = null;
		}

		if (mUnLockMgr == null) {
			mUnLockMgr = new UnLockMgr(getContext(), MgrUtils.unLockTheme(mThemeRes.m_id), new UnLockMgr.Callback() {
				@Override
				public void UnlockSuccess(BaseRes res) {
					TagMgr.SetTag(getContext(), Tags.THEME_UNLOCK + res.m_id);
					TongJi2.AddCountByRes(getContext(), R.integer.素材中心_解锁_成功);
					Toast.makeText(getContext(), R.string.unlock_success, Toast.LENGTH_SHORT).show();
					mLock = false;
					MgrUtils.AddDownloadTj(ResType.FILTER, mThemeRes);
					downloadGroupMgr();
				}

				@Override
				public void OnCloseBtn() {

				}

				@Override
				public void OnBtn(int state) {

				}

				@Override
				public void OnClose() {
					mUnLockMgr.OnCancel(true);
				}

				@Override
				public void OnLogin() {
					mSite.onLogin(getContext());
				}
			});
			mUnLockMgr.Create();
			mUnLockMgr.SetBk(CommonUtils.GetScreenBmp((Activity)getContext(), ShareData.m_screenWidth / 4, ShareData.m_screenHeight / 4), true);
			mUnLockMgr.Show(FilterDetailPage.this);
		}
	}

	private OnAnimationClickListener mClickListener = new OnAnimationClickListener() {
		@Override
		public void onAnimationClick(View v) {

			if (!mUiEnabled) {
				return;
			}

			if (v == mBackView) {
				mUiEnabled = false;
				HashMap<String, Object> params = null;
				if (mState == BaseItemInfo.COMPLETE) {
					params = new HashMap<>();
					params.put("is_download", true);
				}
				mSite.onBack(getContext(), params);
			} else if (v == mDownloadLayout) {
				if (mState == BaseItemInfo.CONTINUE || mState == BaseItemInfo.PREPARE) {
					if (mLock && TagMgr.CheckTag(getContext(), Tags.THEME_UNLOCK + mThemeRes.m_id)) {
						unLock();
					} else {
						mLock = false;
						MgrUtils.AddDownloadTj(ResType.FILTER, mThemeRes);
						downloadGroupMgr();
					}
				} else if (mState == BaseItemInfo.COMPLETE) {
					// 马上使用
					if (mFilterResList != null && mFilterResList.size() > 0) {
						HashMap<String, Object> params = new HashMap<>();
						params.put("material_type", UiMode.LVJING.GetValue());
						params.put("material_id", mThemeRes.m_filterIDArr[0]);
						mSite.onResourceUse(getContext(), params);
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

	public void downloadGroupMgr() {

		if (null == mFilterResList) return;

		mState = BaseItemInfo.LOADING;

		setDownloadBtnState(mState);

		if (mFilterResList == null || mFilterResList.size() < mThemeRes.m_filterIDArr.length) {
			IDownload[] ress = new IDownload[mThemeRes.m_filterIDArr.length + 1];
			int[] ressIds = new int[mThemeRes.m_filterIDArr.length];
			for (int i = 0; i < mThemeRes.m_filterIDArr.length; i++) {
				BaseRes baseRes = new FrameRes();
				baseRes.m_id = mThemeRes.m_filterIDArr[i];
				baseRes.m_type = BaseRes.TYPE_NETWORK_URL;
				ress[i] = baseRes;
				ressIds[i] = baseRes.m_id;
			}
			ress[mThemeRes.m_filterIDArr.length] = mThemeRes;
			AbsDownloadMgr.DownloadGroupInfo myInfo = DownloadMgr.getInstance().DownloadRes(ress, false, mMyDownloadCB);
			mMyDownloadCB.setDatas(ressIds, ResType.FILTER, mThemeRes.m_id, myInfo.m_id);
		} else {
			ArrayList<? extends BaseRes> itemInfos = mFilterResList;
			int len = itemInfos.size();
			IDownload[] ress = new IDownload[len + 1];
			int[] ressIds = new int[len];
			for (int i = 0; i < len; i++) {
				BaseRes itemInfo = itemInfos.get(i);
				if (itemInfo != null) {
					ress[i] = itemInfo;
					ressIds[i] = itemInfo.m_id;
				}
			}
			//主题必须放最后
			ress[len] = mThemeRes; //主题也需要下载
			AbsDownloadMgr.DownloadGroupInfo myInfo = DownloadMgr.getInstance().DownloadRes(ress, false, mMyDownloadCB);
			mMyDownloadCB.setDatas(ressIds, ResType.FILTER, mThemeRes.m_id, myInfo.m_id);
		}
	}

	private MgrUtils.MyDownloadCB mMyDownloadCB = new MgrUtils.MyDownloadCB(new MgrUtils.MyCB() {
		@Override
		public void OnFail(int downloadId, IDownload res) {

		}

		@Override
		public void OnGroupFailed(int downloadId, IDownload[] resArr) {

			if (mProgress > 0 && mProgress < mFilterResList.size()) {
				mState = BaseItemInfo.CONTINUE;
			} else {
				mState = BaseItemInfo.PREPARE;
			}
			if (mDownloadFailedDlg != null) {
				mDownloadFailedDlg.show();
			}
			setDownloadBtnState(mState);
		}

		@Override
		public void OnComplete(int downloadId, IDownload res) {
		}

		@Override
		public void OnProgress(int downloadId, IDownload[] resArr, int progress) {
			mState = BaseItemInfo.LOADING;
			mProgress = progress;

		}

		@Override
		public void OnGroupComplete(int downloadId, IDownload[] resArr) {

			mState = BaseItemInfo.COMPLETE;
			mProgress = resArr.length;
			setDownloadBtnState(mState);

			String params = Credit.APP_ID + Credit.FILTER + mThemeRes.m_filterIDArr[0];
			Credit.CreditIncome(params, getContext(), R.integer.积分_首次使用新素材);
			params = Credit.APP_ID + Credit.THEME + mThemeRes.m_id;
			Credit.CreditIncome(params, getContext(), R.integer.积分_首次使用新素材);
		}

	});

	private void setDownloadBtnState(int state) {

		if (state == BaseItemInfo.LOADING) {
			mDownloadLayout.setOnTouchListener(null);
			mDownloadBg.setColorFilter(0xff08c350, PorterDuff.Mode.SRC_IN);
			mDownloadText.setVisibility(INVISIBLE);

			if (mLoading.getVisibility() != VISIBLE) {
				mLoading.setVisibility(VISIBLE);
				mLoading.startAnimation(mLoadingAnimation);
			}

		} else if (state == BaseItemInfo.COMPLETE) {
			mDownloadBg.setColorFilter(0xff08c350, PorterDuff.Mode.SRC_IN);
			mDownloadLayout.setOnTouchListener(mClickListener);
			if (mLoading.getVisibility() == VISIBLE) {
				mLoading.clearAnimation();
				mLoading.setVisibility(INVISIBLE);
			}

			mDownloadText.setVisibility(VISIBLE);
			mDownloadText.setText(R.string.material_use);
		} else {
			ImageUtils.AddSkin(mContext, mDownloadBg);
			mDownloadLayout.setOnTouchListener(mClickListener);
			mDownloadText.setTextColor(Color.WHITE);

			if (mLoading.getVisibility() == VISIBLE) {
				mLoading.clearAnimation();
				mLoading.setVisibility(INVISIBLE);
			}
			mDownloadText.setText(R.string.material_download);
			if (mDownloadText.getVisibility() != VISIBLE) {
				mDownloadText.setVisibility(VISIBLE);
			}
		}
	}
}
