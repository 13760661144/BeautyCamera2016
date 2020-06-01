package cn.poco.filterManage;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.poco.MaterialMgr2.BaseItemInfo;
import cn.poco.MaterialMgr2.MgrUtils;
import cn.poco.advanced.ImageUtils;
import cn.poco.filterManage.adapter.FilterMoreAdapter;
import cn.poco.filterManage.model.FilterInfo;
import cn.poco.filterManage.site.FilterMoreSite;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;
import cn.poco.home.home4.Home4Page;
import cn.poco.resource.FilterRes;
import cn.poco.resource.FilterResMgr2;
import cn.poco.resource.LockRes;
import cn.poco.resource.ThemeRes;
import cn.poco.resource.ThemeResMgr2;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

/**
 * Created by: fwc
 * Date: 2017/3/29
 * 滤镜下载更多页面
 */
public class FilterMorePage extends IPage {

	private Context mContext;
	private FilterMoreSite mSite;

	private boolean mUiEnabled = false;

	private int mTopHeight;
	public static int sMaxDelta;

	private FrameLayout mTopLayout;
	private ImageView mBackView;
	private TextView mTitleView;
	private LinearLayout mManageLayout;

	private RecyclerView mRecyclerView;
	private LinearLayoutManager mLayoutManager;
	private FilterMoreAdapter mAdapter;
	private List<FilterInfo> mItems;

	private boolean mChange = false;

	//是否调用自直播助手page
	private boolean mFromLivePage = false;

	public FilterMorePage(Context context, BaseSite site) {
		super(context, site);
		mContext = context;
		mSite = (FilterMoreSite) site;
		if (mSite.m_inParams != null) {
			//判断是否来自直播助手
			if (mSite.m_inParams.containsKey("from_live_page")) {
				mFromLivePage = (boolean) mSite.m_inParams.get("from_live_page");
			}
		}
		if (mFromLivePage) {
			MyBeautyStat.onPageStartByRes(R.string.直播助手_美颜美型页_滤镜下载页);
		} else {
			TongJiUtils.onPageStart(getContext(), R.string.下载更多);
		}

		initDatas();
		initViews();
		initAdapter();
		initListeners();
	}

	private void initDatas() {
		mTopHeight = ShareData.PxToDpi_xhdpi(96);
		sMaxDelta = ShareData.PxToDpi_xhdpi(173);
	}

	private void initViews() {

		if (!TextUtils.isEmpty(Home4Page.s_maskBmpPath)) {
			setBackgroundDrawable(new BitmapDrawable(cn.poco.imagecore.Utils.DecodeFile(Home4Page.s_maskBmpPath, null)));
		} else {
			setBackgroundResource(R.drawable.login_tips_all_bk);
		}

		LayoutParams params;

		// 白色遮罩层
		View mask = new View(getContext());
		mask.setBackgroundColor(0xb2ffffff);
		params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		mask.setLayoutParams(params);
		addView(mask);

		mRecyclerView = new RecyclerView(mContext);
		mRecyclerView.setPadding(0, mTopHeight, 0, 0);
		mRecyclerView.setClipToPadding(false);
		mLayoutManager = new LinearLayoutManager(mContext);
		mRecyclerView.setLayoutManager(mLayoutManager);
		mRecyclerView.setOverScrollMode(OVER_SCROLL_NEVER);
		mRecyclerView.setHasFixedSize(true);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		addView(mRecyclerView, params);

		mTopLayout = new FrameLayout(mContext);
		mTopLayout.setBackgroundColor(0xf4ffffff);
		mTopLayout.setClickable(true);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mTopHeight);
		addView(mTopLayout, params);
		{
			mBackView = new ImageView(mContext);
			mBackView.setImageResource(R.drawable.framework_back_btn);
			ImageUtils.AddSkin(getContext(), mBackView);
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER_VERTICAL;
			mTopLayout.addView(mBackView, params);

			mTitleView = new TextView(mContext);
			mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
			mTitleView.setTextColor(0xe6000000);
			mTitleView.setText(R.string.material_filter);
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER;
			mTopLayout.addView(mTitleView, params);

			mManageLayout = new LinearLayout(mContext);
			mManageLayout.setOrientation(LinearLayout.HORIZONTAL);
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
			params.rightMargin = ShareData.PxToDpi_xhdpi(28);
			mTopLayout.addView(mManageLayout, params);
			{
				LinearLayout.LayoutParams params1;
				ImageView manageIcon = new ImageView(mContext);
				manageIcon.setImageResource(R.drawable.new_material_manage_btn);
				ImageUtils.AddSkin(getContext(), manageIcon);
				params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				params1.gravity = Gravity.CENTER_VERTICAL;
				mManageLayout.addView(manageIcon, params1);

				TextView manageText = new TextView(mContext);
				manageText.setTextColor(ImageUtils.GetSkinColor(0xffe75988));
				manageText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
				manageText.setText(R.string.material_manage);
				params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				params1.leftMargin = ShareData.PxToDpi_xhdpi(10);
				mManageLayout.addView(manageText, params1);
			}
		}
	}

	private void initListeners() {
		mBackView.setOnTouchListener(mClickListener);
		mManageLayout.setOnTouchListener(mClickListener);

		mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				int first = mLayoutManager.findFirstVisibleItemPosition();
				int last = mLayoutManager.findLastVisibleItemPosition();

				View view;
				FilterMoreAdapter.ItemView itemView;

				for (int i = 0; i < mItems.size(); i++) {
					if (i < first) {
						mItems.get(i).addY(sMaxDelta);
					} else if (i > last) {
						mItems.get(i).resetY();
					} else {
						if (mItems.get(i).getRatio() == 0f) {
							mItems.get(i).setRatio(sMaxDelta * 1f / (((i + 1 - first) * ShareData.PxToDpi_xhdpi(374))));
						}
					}
				}

				for (int i = first; i <= last; i++) {
					view = mLayoutManager.findViewByPosition(i);
					if (view instanceof FilterMoreAdapter.ItemView) {
						itemView = (FilterMoreAdapter.ItemView) view;
						mItems.get(i).addY(dy * mItems.get(i).getRatio());
						itemView.setDy(mItems.get(i).getDy());
					}
				}
			}
		});

		mAdapter.setOnItemClickListener(new FilterMoreAdapter.OnItemClickListener() {
			@Override
			public void onClick(View view, int position) {
				if (mUiEnabled) {
					// 避免同时触发
					mUiEnabled = false;
					HashMap<String, Object> params = new HashMap<>();
					params.put("theme_res", mItems.get(position).themeRes);
					mSite.onFilterDetails(getContext(), params);
				}
			}
		});
	}

	private OnAnimationClickListener mClickListener = new OnAnimationClickListener() {
		@Override
		public void onAnimationClick(View v) {

			if (!mUiEnabled) {
				return;
			}

			if (v == mBackView) {
				mUiEnabled = false;
				HashMap<String, Object> params = new HashMap<>();
				params.put("is_change", mChange);
				mSite.onBack(getContext(), params);
			} else if (v == mManageLayout) {
				HashMap<String, Object> params = null;
				if (mFromLivePage) {
					params = new HashMap<>();
					params.put("from_live_page", true);
				}
				mSite.onFilterManagePage(getContext(), params);
			}
		}

		@Override
		public void onTouch(View v) {

		}

		@Override
		public void onRelease(View v) {

		}
	};

	@Override
	public void SetData(HashMap<String, Object> params) {
		mUiEnabled = true;
	}

	private void initAdapter() {
		mItems = new ArrayList<>();
		initItems();

		mAdapter = new FilterMoreAdapter(mContext, mItems);
		mRecyclerView.setAdapter(mAdapter);
	}

	private void initItems() {
		ArrayList<ThemeRes> themeRess = ThemeResMgr2.getInstance().GetAllResArr();
		ThemeRes themeRes;
		ArrayList<FilterRes> ress;
		FilterInfo item;
		if(themeRess != null && themeRess.size() > 0) {
			int themeLen = themeRess.size();
			for (int i = 0; i < themeLen; i++) {
				themeRes = themeRess.get(i);
				if (themeRes != null && !themeRes.m_isHide) {
					ress = FilterResMgr2.getInstance().GetResArr(themeRes.m_filterIDArr, false);
					if (ress != null && !ress.isEmpty()) {
						int state = MgrUtils.checkGroupDownloadState(ress, null);
						if (state != BaseItemInfo.COMPLETE) {
							item = new FilterInfo();
							item.themeRes = themeRes;
							item.name = themeRes.m_filterName;
							if (themeRes.m_filter_theme_icon_url != null && themeRes.m_filter_theme_icon_url.length > 0) {
								item.image = themeRes.m_filter_theme_icon_url[0];
							}
							item.uri = themeRes.m_id;
							item.ress.addAll(ress);
							item.state = state;
							item.progress = MgrUtils.getM_completeCount() / ress.size() * 100;
							LockRes lockRes = MgrUtils.unLockTheme(themeRes.m_id);
							if (lockRes != null && lockRes.m_shareType != LockRes.SHARE_TYPE_NONE && TagMgr.CheckTag(getContext(), Tags.THEME_UNLOCK + themeRes.m_id)) {
								item.lock = true;
							}
							mItems.add(item);
						}
					}
				}
			}
		}
	}

	@Override
	public void onBack() {
		if (mUiEnabled) {
			mUiEnabled = false;
			HashMap<String, Object> params = new HashMap<>();
			params.put("is_change", mChange);
			mSite.onBack(getContext(), params);
		}
	}

	@Override
	public void onResume() {
		if (!mFromLivePage) {
			TongJiUtils.onPageResume(getContext(), R.string.下载更多);
		}
	}

	@Override
	public void onPause() {
		if (!mFromLivePage) {
			TongJiUtils.onPagePause(getContext(), R.string.下载更多);
		}
	}

	@Override
	public void onClose() {
		mAdapter = null;
		mRecyclerView.setAdapter(null);
		Glide.get(getContext()).clearMemory();
		if (mFromLivePage) {
			MyBeautyStat.onPageEndByRes(R.string.直播助手_美颜美型页_滤镜下载页);
		} else {
			TongJiUtils.onPageEnd(getContext(), R.string.下载更多);
		}
	}

	@Override
	public void onPageResult(int siteID, HashMap<String, Object> params) {
		mUiEnabled = true;

		if (siteID == SiteID.FILTER_MANAGE && params != null) {
			if (params.containsKey("is_delete")) {
				mChange = true;
				mItems.clear();
				initItems();
				mAdapter.notifyDataSetChanged();
			}
		} else if (siteID == SiteID.FILTER_DETAIL && params != null) {
			if (params.containsKey("is_download")) {
				mChange = true;
				mItems.clear();
				initItems();
				mAdapter.notifyDataSetChanged();
			}
		}
	}
}
