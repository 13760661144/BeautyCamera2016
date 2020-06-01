package cn.poco.filterManage;

import android.content.Context;
import android.graphics.Color;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.poco.advanced.ImageUtils;
import cn.poco.album.utils.ListItemDecoration;
import cn.poco.filterManage.adapter.FilterManageAdapter;
import cn.poco.filterManage.model.FilterInfo;
import cn.poco.filterManage.site.FilterManageSite;
import cn.poco.framework.BaseSite;
import cn.poco.framework.EventCenter;
import cn.poco.framework.EventID;
import cn.poco.framework.IPage;
import cn.poco.home.home4.Home4Page;
import cn.poco.resource.FilterResMgr2;
import cn.poco.resource.GroupRes;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

import static cn.poco.album.utils.ListItemDecoration.VERTICAL;

/**
 * Created by: fwc
 * Date: 2017/3/29
 * 滤镜素材管理页
 */
public class FilterManagePage extends IPage {

	private Context mContext;
	private FilterManageSite mSite;

	private int mTopHeight;
	private int mBottomHeight;

	private FrameLayout mTopLayout;
	private ImageView mBackView;
	private TextView mSelectText;

	private RecyclerView mRecyclerView;
	private FilterManageAdapter mAdapter;
	private List<FilterInfo> mItems;

	private FrameLayout mBottomLayout;
	private LinearLayout mDeleteLayout;

	private boolean mUiEnable = false;

	private boolean mSelectAll = false;

	private boolean mIsDelete = false;

	private boolean mFromLivePage = false;

	public FilterManagePage(Context context, BaseSite site) {
		super(context, site);

		mContext = context;
		mSite = (FilterManageSite)site;

		if (mSite != null && mSite.m_inParams != null && mSite.m_inParams.containsKey("from_live_page")) {
			mFromLivePage = (boolean) mSite.m_inParams.get("from_live_page");
		}
		if (mFromLivePage) {
			MyBeautyStat.onPageStartByRes(R.string.直播助手_美颜美型页_滤镜管理页);
		} else {
			MyBeautyStat.onPageStartByRes(R.string.素材商店_滤镜管理_主页面);
			TongJiUtils.onPageStart(getContext(), R.string.素材中心_管理_详情);
		}

		initDatas();
		initViews();
		initAdapter();
		initListeners();
	}

	private void initDatas() {
		mTopHeight = ShareData.PxToDpi_xhdpi(96);
		mBottomHeight = ShareData.PxToDpi_xhdpi(100);
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
		mRecyclerView.setPadding(0, mTopHeight + ShareData.PxToDpi_xhdpi(20), 0, mBottomHeight + ShareData.PxToDpi_xhdpi(20));
		mRecyclerView.setClipToPadding(false);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
		mRecyclerView.addItemDecoration(new ListItemDecoration(ShareData.PxToDpi_xhdpi(20), VERTICAL));
		mRecyclerView.setOverScrollMode(OVER_SCROLL_NEVER);
		mRecyclerView.setHasFixedSize(true);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		addView(mRecyclerView, params);

		mTopLayout = new FrameLayout(mContext);
		mTopLayout.setClickable(true);
		mTopLayout.setBackgroundColor(0xf4ffffff);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mTopHeight);
		addView(mTopLayout, params);
		{
			mBackView = new ImageView(mContext);
			mBackView.setClickable(true);
			mBackView.setImageResource(R.drawable.framework_back_btn);
			ImageUtils.AddSkin(getContext(), mBackView);
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER_VERTICAL;
			mTopLayout.addView(mBackView, params);

			TextView titleView = new TextView(mContext);
			titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
			titleView.setTextColor(0xe6000000);
			titleView.setText(R.string.material_filter);
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER;
			mTopLayout.addView(titleView, params);

			mSelectText = new TextView(mContext);
			mSelectText.setTextColor(ImageUtils.GetSkinColor(0xffe75988));
			mSelectText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
			mSelectText.setText(R.string.material_manage_select_all);
			params = new LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER_VERTICAL | Gravity.END;
			params.rightMargin = ShareData.PxToDpi_xhdpi(28);
			mTopLayout.addView(mSelectText, params);
		}

		mBottomLayout = new FrameLayout(mContext);
		mBottomLayout.setClickable(true);
		mBottomLayout.setBackgroundColor(0xf4ffffff);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mBottomHeight);
		params.gravity = Gravity.BOTTOM;
		addView(mBottomLayout, params);
		{
			mDeleteLayout = new LinearLayout(mContext);
			mDeleteLayout.setOrientation(LinearLayout.HORIZONTAL);
			mDeleteLayout.setAlpha(0.1f);
			mDeleteLayout.setBackgroundResource(R.drawable.new_material4_delete);
			mDeleteLayout.setGravity(Gravity.CENTER);
			params = new LayoutParams(ShareData.PxToDpi_xhdpi(270), ShareData.PxToDpi_xhdpi(76));
			params.gravity = Gravity.CENTER;
			mBottomLayout.addView(mDeleteLayout, params);
			{
				LinearLayout.LayoutParams params1;

				ImageView deleteIcon = new ImageView(mContext);
				deleteIcon.setImageResource(R.drawable.new_material4_delete_icon);
				params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				mDeleteLayout.addView(deleteIcon, params1);

				TextView deleteText = new TextView(mContext);
				deleteText.setTextColor(Color.WHITE);
				deleteText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				deleteText.setText(R.string.material_manage_delete);
				params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				params1.leftMargin = ShareData.PxToDpi_xhdpi(8);
				mDeleteLayout.addView(deleteText, params1);
			}
		}
	}

	private void initAdapter() {
		mItems = new ArrayList<>();

		ArrayList<GroupRes> filterRess = FilterResMgr2.getInstance().GetDownloadedGroupResArr(mContext);
		if (filterRess != null && !filterRess.isEmpty()) {
			FilterInfo info;
			for (GroupRes res : filterRess) {
				info = new FilterInfo();
				info.themeRes = res.m_themeRes;
				if (res.m_themeRes.m_filter_theme_icon_url != null && res.m_themeRes.m_filter_theme_icon_url.length > 0) {
					info.image = res.m_themeRes.m_filter_theme_icon_url[0];
				}
				info.name = res.m_themeRes.m_filterName;
				mItems.add(info);
			}
		}

		if (mItems.isEmpty()) {
			mSelectText.setVisibility(GONE);
		}
		mAdapter = new FilterManageAdapter(mContext, mItems);
		mRecyclerView.setAdapter(mAdapter);
	}

	private void initListeners() {
		mBackView.setOnTouchListener(mOnTouchListener);
		mSelectText.setOnTouchListener(mOnTouchListener);

		mAdapter.setOnCheckChangeListener(new FilterManageAdapter.OnCheckChangeListener() {
			@Override
			public void onCheck(View view, int position, boolean checked) {
				int size = mItems.size();
				if (size > 0 && position >= 0 && position < size)
				{
					mItems.get(position).check = checked;
				}
				if (isAllSelected()) {
					mSelectText.setText(R.string.material_manage_cancel_select_all);
					mSelectAll = true;
				} else {
					mSelectText.setText(R.string.material_manage_select_all);
					mSelectAll = false;
				}

				setDeleteBtnState();
			}
		});
	}

	private void setDeleteBtnState() {
		if (hasSelected()) {
			mDeleteLayout.setOnTouchListener(mOnTouchListener);
			mDeleteLayout.setAlpha(1f);
		} else {
			mDeleteLayout.setOnTouchListener(null);
			mDeleteLayout.setAlpha(0.1f);
		}
	}

	private boolean isAllSelected() {
		for (FilterInfo info : mItems) {
			if (!info.check) {
				return false;
			}
		}

		return true;
	}

	private boolean hasSelected() {
		for (FilterInfo info : mItems) {
			if (info.check) {
				return true;
			}
		}
		return false;
	}

	private OnTouchListener mOnTouchListener = new OnAnimationClickListener() {
		@Override
		public void onAnimationClick(View v) {
			if (!mUiEnable) {
				return;
			}

			if (v == mBackView) {
				mUiEnable = false;
				if (mFromLivePage) {
					MyBeautyStat.onClickByRes(R.string.直播助手_美颜页_滤镜管理_返回);
				} else {
					MyBeautyStat.onClickByRes(R.string.素材商店_滤镜管理_主页面_滤镜管理_返回);
					TongJi2.AddCountByRes(mContext, R.integer.素材中心_管理_滤镜_返回);
				}
				HashMap<String, Object> params = null;
				if (mIsDelete) {
					params = new HashMap<>();
					params.put("is_delete", true);
				}
				mSite.onBack(getContext(), params);
			} else if (v == mSelectText) {
				if (!mFromLivePage) {
					TongJi2.AddCountByRes(mContext, R.integer.素材中心_管理_滤镜_全选);
					MyBeautyStat.onClickByRes(R.string.素材商店_滤镜管理_主页面_滤镜管理_全选);
				}
				if (mSelectAll) {
					mSelectAll = false;
					mSelectText.setText(R.string.material_manage_select_all);
					for (FilterInfo info : mItems) {
						info.check = false;
					}
					mAdapter.notifyDataSetChanged();
					if (mFromLivePage) {
						MyBeautyStat.onClickByRes(R.string.直播助手_美颜页_滤镜管理_全不选);
					}
				} else {
					mSelectAll = true;
					mSelectText.setText(R.string.material_manage_cancel_select_all);
					for (FilterInfo info : mItems) {
						info.check = true;
					}
					mAdapter.notifyDataSetChanged();
					if (mFromLivePage) {
						MyBeautyStat.onClickByRes(R.string.直播助手_美颜页_滤镜管理_全选);
					}
				}
				setDeleteBtnState();
			} else if (v == mDeleteLayout) {
				if (mFromLivePage) {
					MyBeautyStat.onClickByRes(R.string.直播助手_美颜页_滤镜管理_删除);
				} else {
					TongJi2.AddCountByRes(mContext, R.integer.素材中心_管理_滤镜_删除);
					MyBeautyStat.onClickByRes(R.string.素材商店_滤镜管理_主页面_滤镜管理_删除);
				}
				List<FilterInfo> selectedInfos = new ArrayList<>();
				List<Integer> indexs = new ArrayList<>();
				FilterInfo info;
				for (int i = 0; i < mItems.size(); i++) {
					info = mItems.get(i);
					if (info.check) {
						selectedInfos.add(info);
						indexs.add(i);
					}
				}

				if (!selectedInfos.isEmpty()) {
					GroupRes res;
					ArrayList<Integer> deleteIds = new ArrayList<>();
					for (FilterInfo info1 : selectedInfos) {
						res = new GroupRes();
						res.m_themeRes = info1.themeRes;
						for (int filterId : res.m_themeRes.m_filterIDArr)
						{
							deleteIds.add(filterId);
						}
						FilterResMgr2.getInstance().DeleteGroupRes(getContext(), res);
					}
					if (deleteIds.size() > 0) {
						EventCenter.sendEvent(EventID.NOTIFY_FILTERRES_DELETE, deleteIds);
					}

					mItems.removeAll(selectedInfos);
					int start = 0;
					for (int index : indexs) {
						mAdapter.notifyItemRemoved(index - start);
						start++;
					}

					mDeleteLayout.setOnTouchListener(null);
					mDeleteLayout.setAlpha(0.1f);

					if (mItems.isEmpty()) {
						mSelectText.setVisibility(GONE);
					} else {
						mSelectAll = false;
						mSelectText.setText(R.string.material_manage_select_all);
					}

					mIsDelete = true;
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

	@Override
	public void SetData(HashMap<String, Object> params) {

		mUiEnable = true;
	}

	@Override
	public void onBack() {
		if (mUiEnable) {
			mUiEnable = false;
			HashMap<String, Object> params = null;
			if (mIsDelete) {
				params = new HashMap<>();
				params.put("is_delete", true);
			}
			mSite.onBack(getContext(), params);
		}
	}

	@Override
	public void onResume() {
		if (!mFromLivePage) {
			TongJiUtils.onPageResume(getContext(), R.string.素材中心_管理_详情);
		}
	}

	@Override
	public void onPause() {
		if (!mFromLivePage) {
			TongJiUtils.onPagePause(getContext(), R.string.素材中心_管理_详情);
		}
	}

	@Override
	public void onClose() {
		if (mFromLivePage) {
			MyBeautyStat.onPageEndByRes(R.string.直播助手_美颜美型页_滤镜管理页);
		} else {
			MyBeautyStat.onPageEndByRes(R.string.素材商店_滤镜管理_主页面);
			TongJiUtils.onPageEnd(getContext(), R.string.素材中心_管理_详情);
		}
	}
}
