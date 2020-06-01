package cn.poco.pendant.view;

import android.content.Context;
import android.os.Looper;
import android.os.MessageQueue;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import cn.poco.advanced.ImageUtils;
import cn.poco.pendant.PendantPage;
import cn.poco.pendant.site.PendantSite;
import cn.poco.resource.DecorateGroupRes;
import cn.poco.resource.DecorateResMgr2;
import cn.poco.resource.ResType;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

/**
 * Created by: fwc
 * Date: 2016/11/18
 */
public class SelectPanel extends LinearLayout {

	private Context mContext;

	private RelativeLayout mTopLayout;
	private ImageView mCancelView;
	private ImageView mOkView;
	private ShopItem mShopView;
	private MyRecyclerView mRecyclerView;
	private LinearLayoutManager mLayoutManager;
	private TitleAdapter mTitleAdapter;
	private List<Title> mTitles;

	private FrameLayout mContentView;
	private LinearLayout mDotLayout;

	private ViewPager mViewPager;
	private ResAdapter mResAdapter;
	private ArrayList<DecorateGroupRes> mGroupRes;

	private ArrayList<ImageView> mDotViews;

	private int mGroupIndex = 0;
	private int mPageIndex = 0;

	private PendantSite mSite;

	private int mTopBarHeight;
	private int mImagePadding;
	private int mShopItemWidth;

	private DecorateGroupRes mRecommendRes;
	private PageView mPageView;

	private View mLefeEdge;
	private View mRightEdge;

	private OnTitleItemClickListener mTitleItemClickListener;
	private boolean mDown = false;

	private float mLastX;

	private boolean mUiEnable = true;

	public SelectPanel(Context context, PendantSite site) {
		super(context);
		mContext = context;
		mSite = site;

		initDatas();
		initViews();
	}

	private void initDatas() {
		mTopBarHeight = ShareData.PxToDpi_xhdpi(88);
		mImagePadding = ShareData.PxToDpi_xhdpi(22);
		mShopItemWidth = ShareData.PxToDpi_xhdpi(78);
	}

	/**
	 * 初始化View
	 */
	private void initViews() {

		setOrientation(VERTICAL);

		LayoutParams params;
		mTopLayout = new RelativeLayout(mContext);
		mTopLayout.setGravity(Gravity.CENTER_VERTICAL);
		mTopLayout.setBackgroundColor(0xe6ffffff);
		params = new LayoutParams(LayoutParams.MATCH_PARENT, mTopBarHeight);
		addView(mTopLayout, params);
		{
			RelativeLayout.LayoutParams params1;
			mCancelView = new ImageView(mContext);
			mCancelView.setImageResource(R.drawable.beautify_cancel);
			mCancelView.setPadding(mImagePadding, 0, mImagePadding, 0);
			params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
			params1.addRule(RelativeLayout.CENTER_VERTICAL);
			mTopLayout.addView(mCancelView, params1);

			mShopView = new ShopItem(mContext);
			params1 = new RelativeLayout.LayoutParams(mShopItemWidth, ViewGroup.LayoutParams.MATCH_PARENT);
			params1.addRule(RelativeLayout.CENTER_VERTICAL);
			params1.leftMargin = ShareData.PxToDpi_xhdpi(94);
			mTopLayout.addView(mShopView, params1);

			mRecyclerView = new MyRecyclerView(mContext);
			mRecyclerView.setOverScrollMode(OVER_SCROLL_NEVER);
			mRecyclerView.setBackgroundColor(0);
			mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
			mRecyclerView.setLayoutManager(mLayoutManager);
			mRecyclerView.setHasFixedSize(true);
			mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

				@Override
				public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
					shouldShowEdge();
				}
			});
//			mRecyclerView.setOnMyScrollListener(new MyRecyclerView.OnMyScrollListener() {
//				@Override
//				public void onScroll(float dx) {
//
//					if (dx > 0) {
//						// 从左向右滑
//
//					} else {
//						// 从右向左滑
//						RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams((int)(mShopItemWidth + dx), ViewGroup.LayoutParams.MATCH_PARENT);
//						params2.addRule(RelativeLayout.CENTER_VERTICAL);
//						params2.leftMargin = ShareData.PxToDpi_xhdpi(94);
//						mShopView.setLayoutParams(params2);
//					}
//				}
//			});
			((SimpleItemAnimator)mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
			params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			params1.addRule(RelativeLayout.CENTER_VERTICAL);
			params1.rightMargin = ShareData.PxToDpi_xhdpi(94);
			params1.leftMargin = ShareData.PxToDpi_xhdpi(176);
			mTopLayout.addView(mRecyclerView, params1);

			mLefeEdge = new View(mContext);
			mLefeEdge.setBackgroundResource(R.drawable.pendant_list_left);
			params1 = new RelativeLayout.LayoutParams(ShareData.PxToDpi_xhdpi(100), ViewGroup.LayoutParams.MATCH_PARENT);
			params1.addRule(RelativeLayout.CENTER_VERTICAL);
			params1.leftMargin = ShareData.PxToDpi_xhdpi(176);
			mTopLayout.addView(mLefeEdge, params1);

			mRightEdge = new View(mContext);
			mRightEdge.setBackgroundResource(R.drawable.pendant_list_right);
			params1 = new RelativeLayout.LayoutParams(ShareData.PxToDpi_xhdpi(100), ViewGroup.LayoutParams.MATCH_PARENT);
			params1.addRule(RelativeLayout.CENTER_VERTICAL);
			params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			params1.rightMargin = ShareData.PxToDpi_xhdpi(94);
			mTopLayout.addView(mRightEdge, params1);

			mOkView = new ImageView(mContext);
			mOkView.setImageResource(R.drawable.beautify_ok);
			mOkView.setPadding(mImagePadding, 0, mImagePadding, 0);
			ImageUtils.AddSkin(getContext(), mOkView);
			params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
			params1.addRule(RelativeLayout.CENTER_VERTICAL);
			params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			mTopLayout.addView(mOkView, params1);
		}
		mContentView = new FrameLayout(mContext);
		params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addView(mContentView, params);
		{
			FrameLayout.LayoutParams params1;
			mViewPager = new ViewPager(mContext);
			params1 = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			mContentView.addView(mViewPager, params1);

			mDotLayout = new LinearLayout(mContext);
			mDotLayout.setOrientation(HORIZONTAL);
			params1 = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params1.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
			params1.bottomMargin = ShareData.PxToDpi_xhdpi(18);
			mContentView.addView(mDotLayout, params1);
		}

		initListener();
	}

	private void shouldShowEdge() {
		if (mLayoutManager.findFirstVisibleItemPosition() == 0) {
			if (mLefeEdge.getVisibility() == VISIBLE) {
				mLefeEdge.setVisibility(GONE);
			}
		} else if (mLefeEdge.getVisibility() == GONE) {
			mLefeEdge.setVisibility(VISIBLE);
		}

		if (mLayoutManager.findLastVisibleItemPosition() == mTitles.size() - 1) {
			if (mRightEdge.getVisibility() == VISIBLE) {
				mRightEdge.setVisibility(GONE);
			}
		} else if (mRightEdge.getVisibility() == GONE) {
			mRightEdge.setVisibility(VISIBLE);
		}
	}

	/**
	 * 初始化监听器
	 */
	private void initListener() {
		mShopView.setOnTouchListener(new OnAnimationClickListener() {
			@Override
			public void onAnimationClick(View v) {
				TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_贴图_下载更多);
				MyBeautyStat.onClickByRes(R.string.美颜美图_贴图页面_主页面_下载更多);
				mSite.openDownloadMore(getContext(), ResType.DECORATE);
			}

			@Override
			public void onTouch(View v) {

			}

			@Override
			public void onRelease(View v) {

			}
		});

		mViewPager.addOnPageChangeListener(mPageChangeListener);
	}

	public void setOnCancelListener(final OnClickListener listener) {
		if (listener != null && mCancelView != null) {
			mCancelView.setOnTouchListener(new OnAnimationClickListener() {
				@Override
				public void onAnimationClick(View v) {
					TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_贴图_取消);
					MyBeautyStat.onClickByRes(R.string.美颜美图_贴图页面_主页面_取消);
					listener.onClick(v);
				}

				@Override
				public void onTouch(View v) {

				}

				@Override
				public void onRelease(View v) {

				}
			});
		}
	}

	public void setOnOkListener(final OnClickListener listener) {
		if (listener != null && mOkView != null) {
			mOkView.setOnTouchListener(new OnAnimationClickListener() {
				@Override
				public void onAnimationClick(View v) {
					TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_贴图_确认);
					MyBeautyStat.onClickByRes(R.string.美颜美图_贴图页面_主页面_确认);
					listener.onClick(v);
				}

				@Override
				public void onTouch(View v) {

				}

				@Override
				public void onRelease(View v) {

				}
			});
		}
	}

	public void setDecorateGroupRes(ArrayList<DecorateGroupRes> res, PageView.OnClickRes onClickRes,
									OnItemLongClickListener onItemLongClickListener,
									OnTitleItemClickListener onTitleItemClickListener,
									boolean initial) {
		mGroupRes = res;

		// 初始化adapter
		mResAdapter = new ResAdapter(mContext, mGroupRes, onClickRes);
		mViewPager.setAdapter(mResAdapter);

		// 不注释高度变小会有问题
//		mGroupIndex = 0;
		mPageIndex = 0;

		mTitleItemClickListener = onTitleItemClickListener;

		if (initial) {
			mTitles = new ArrayList<>();
			Title title;
			for (DecorateGroupRes groupRes : res) {
				title = new Title();
				title.title = groupRes.m_name;
				title.isSelected = false;
				mTitles.add(title);
			}

			if (mTitles.size() > mGroupIndex) {
				mTitles.get(mGroupIndex).isSelected = true;
			}
			mTitleAdapter = new TitleAdapter(mTitles);
			mTitleAdapter.setOnItemClickListener(mOnItemClickListener);
			mTitleAdapter.setOnItemLongClickListener(onItemLongClickListener);
			mRecyclerView.setAdapter(mTitleAdapter);
		}

		if (mGroupIndex > 0) {
			final int index = mGroupIndex;
			mGroupIndex = 0;
			mOnItemClickListener.onItemClick(null, index);
		} else {
			if (mGroupRes.size() > mGroupIndex) {
				generateDots(getPageCount(mGroupRes.get(mGroupIndex).m_group.size()), mPageIndex);
			}
		}
	}

	/**
	 * 通过下标设置是否New状态
	 *
	 * @param index 下标
	 * @param isNew 是否New状态
	 */
	public void setNewByIndex(int index, boolean isNew) {
		if (mTitles != null && mTitles.size() > index) {
			Title title = mTitles.get(index);
			title.isNew = isNew;
			mTitleAdapter.notifyItemChanged(index);
		}
	}

	public void onPageBack() {
		mRecyclerView.post(new Runnable() {
			@Override
			public void run() {
				mRecyclerView.scrollToPosition(mGroupIndex);
			}
		});
//		mRecyclerView.scrollToPosition(mGroupIndex);
	}

	private void scrollToCenter(int position) {
		View view = mLayoutManager.findViewByPosition(position);
		if (view != null) {
			float center = mRecyclerView.getWidth() / 2f;
			float viewCenter = view.getX() + view.getWidth() / 2f;
			mRecyclerView.smoothScrollBy((int)(viewCenter - center), 0);
		} else {
			mRecyclerView.scrollToPosition(0);
		}
	}


	public void addRecommend(DecorateGroupRes recommend, boolean once, boolean isLock) {

		mRecommendRes = recommend;

		mGroupRes.add(0, recommend);
		mResAdapter.notifyDataSetChanged();

		Title title = new Title();
		title.title = recommend.m_name;
		title.isSelected = false;
		title.isRecommend = true;
		title.isLock = isLock;
		mTitles.add(0, title);
		mTitleAdapter.notifyItemInserted(0);
		mGroupIndex++;
		if (mGroupIndex < mTitles.size() && once) {
			mTitles.get(mGroupIndex).isSelected = true;
			mTitleAdapter.notifyItemChanged(mGroupIndex);
			generateDots(getPageCount(mGroupRes.get(mGroupIndex).m_group.size()), 0);
			mViewPager.setCurrentItem(getPagerPosition(mGroupIndex, 0), false);
			// 尽可能显示推荐位
//			mRecyclerView.scrollToPosition(0);
//			mRecyclerView.post(new Runnable() {
//				@Override
//				public void run() {
//					mRecyclerView.scrollToPosition(mGroupIndex);
//				}
//			});
		}
	}

	public void scrollTo(int groupIndex) {

		if (groupIndex >= mTitles.size()) {
			groupIndex = 0;
		}

		if (groupIndex >= 0 && groupIndex < mTitles.size()) {

			mTitles.get(mGroupIndex).isSelected = false;
			mTitles.get(groupIndex).isSelected = true;
			mTitleAdapter.notifyItemChanged(mGroupIndex);
			mTitleAdapter.notifyItemChanged(groupIndex);
			generateDots(getPageCount(mGroupRes.get(groupIndex).m_group.size()), 0);
			mGroupIndex = groupIndex;
			mViewPager.setCurrentItem(getPagerPosition(groupIndex, 0), false);
			// 尽可能显示推荐位
//			mRecyclerView.scrollToPosition(0);
			mRecyclerView.scrollToPosition(groupIndex);
			final int finalGroupIndex = groupIndex;
			Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
				@Override
				public boolean queueIdle() {
					scrollToCenter(finalGroupIndex);
					return false;
				}
			});
		} else {
			mRecyclerView.scrollToPosition(0);
		}
	}

	private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

		}

		@Override
		public void onPageSelected(int position) {
			shouldShowEdge();
			final int[] index = mapRealPosition(position);
			if (mGroupIndex == index[0]) { // 同一组
				changeDots(index[1]);
			} else { // 不同组，更换Dots
                if (mGroupIndex >= mTitles.size() || index[0] >= mTitles.size()) {
                    return;
                }
				generateDots(getPageCount(mGroupRes.get(index[0]).m_group.size()), index[1]);

                mTitles.get(mGroupIndex).isSelected = false;
                mTitles.get(index[0]).isSelected = true;
                mTitles.get(index[0]).isNew = false;
				mTitleAdapter.notifyItemChanged(mGroupIndex);
				mTitleAdapter.notifyItemChanged(index[0]);

				//删除new状态
				DecorateResMgr2.getInstance().DeleteGroupNewFlag(getContext(), mGroupRes.get(index[0]).m_id);
			}

//			mRecyclerView.scrollToPosition(index[0]);
			scrollToCenter(index[0]);

			mGroupIndex = index[0];
			mPageIndex = index[1];
		}

		@Override
		public void onPageScrollStateChanged(int state) {

		}
	};

	/**
	 * 生成dots
	 *
	 * @param count dot的数目
	 */
	private void generateDots(int count, int pageIndex) {
		if (mDotViews == null) {
			mDotViews = new ArrayList<>();
		}

		mDotViews.clear();
		mDotLayout.removeAllViews();

		ImageView imageView;
		LinearLayout.LayoutParams params;

		for (int i = 0; i < count; i++) {
			imageView = new ImageView(mContext);
			if (i == pageIndex) {
				imageView.setImageResource(R.drawable.pendant_page_dot_selected);
			} else {
				imageView.setImageResource(R.drawable.pendant_page_dot_default);
			}
			params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

			if (i != 0) {
				params.leftMargin = ShareData.PxToDpi_xhdpi(8);
			}
			mDotViews.add(imageView);
			mDotLayout.addView(imageView, params);
		}
	}

	public void setDown(boolean down) {
		mDown = down;
	}

	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(View view, int position) {
			if (!mUiEnable) {
				return;
			}

			final int lastPosition = mGroupIndex;
			if (mGroupIndex != position) {
				int pagerPosition = getPagerPosition(position, 0);

				mViewPager.setCurrentItem(pagerPosition, false);
			}

			if (mTitleItemClickListener != null) {
				mTitleItemClickListener.onItemClick(view, position, lastPosition);
			}
		}
	};

	public void setUiEnable(boolean uiEnable) {
		mUiEnable = uiEnable;
	}

	public void setNotDownloadNumber(int number) {
		mShopView.setNumber(number);
	}

	/**
	 * 更换dot
	 *
	 * @param pageIndex 当前选中页的下标
	 */
	private void changeDots(int pageIndex) {
		if (mPageIndex != pageIndex && pageIndex < mDotViews.size() && mPageIndex < mDotViews.size()) {
			mDotViews.get(mPageIndex).setImageResource(R.drawable.pendant_page_dot_default);
			mDotViews.get(pageIndex).setImageResource(R.drawable.pendant_page_dot_selected);
		}
	}

	/**
	 * 将ViewPager当前位置映射为分组的下标和当前组的页面下标
	 *
	 * @param position ViewPager当前位置
	 * @return int[2]
	 */
	private int[] mapRealPosition(int position) {
		int[] out = new int[2];

		int realPosition = 0, index = position;
		int pageCount;
		for (DecorateGroupRes res : mGroupRes) {
			pageCount = getPageCount(res.m_group.size());
			if (position - pageCount >= 0) {
				position -= pageCount;
				realPosition++;
			} else {
				index = position;
				break;
			}
		}

		out[0] = realPosition;
		out[1] = index;

		return out;
	}

	public int getGroupIndex() {
		return mGroupIndex;
	}

	/**
	 * 根据分组的下标和当前组的页面下标获取ViewPager的位置
	 *
	 * @param groupIndex 分组的下标
	 * @param pageIndex  当前组的页面下标
	 * @return ViewPager的位置
	 */
	private int getPagerPosition(int groupIndex, int pageIndex) {

		if (groupIndex >= mGroupRes.size()) {
			groupIndex = mGroupRes.size() - 1;
		}

		int result = 0;
		DecorateGroupRes res;
		for (int i = 0; i < groupIndex; i++) {
			res = mGroupRes.get(i);
			result += getPageCount(res.m_group.size());
		}
		result += pageIndex;
		return result;
	}

	private class ResAdapter extends PagerAdapter {

		private Context mContext;
		private ArrayList<DecorateGroupRes> mItems;
		private PageView.OnClickRes mOnClickRes;

		private ResAdapter(Context context, ArrayList<DecorateGroupRes> items, PageView.OnClickRes onClickRes) {
			mContext = context;
			mItems = items;
			mOnClickRes = onClickRes;
		}

		@Override
		public int getCount() {
			int count = 0;
			for (DecorateGroupRes res : mItems) {
				count += getPageCount(res.m_group.size());
			}
			return count;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {

			int realPosition = 0, index = position;
			int pageCount;
			for (DecorateGroupRes item : mItems) {
				pageCount = getPageCount(item.m_group.size());
				if (position - pageCount >= 0) {
					position -= pageCount;
					realPosition++;
				} else {
					index = position;
					break;
				}
			}

			DecorateGroupRes item = mItems.get(realPosition);
			PageView pageView = new PageView(mContext, item, realPosition, index);
			pageView.setOnClickRes(mOnClickRes);
			pageView.setOnClickListener(null);

			if (item == mRecommendRes) {
				pageView.setAlpha(0.4f);
				mPageView = pageView;
				mPageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (mOnClickRes != null) {
							mOnClickRes.onClick(0, 0, null);
						}
					}
				});
			} else {
				pageView.setAlpha(1f);
			}

			ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			container.addView(pageView, params);

			return pageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			if (mPageView == object) {
				mPageView = null;
			}
			container.removeView((View)object);
		}
	}

	public void setProgress(float progress) {
		if (mPageView != null) {
			float alpha = 0.4f + progress / 100f * 0.6f;
			mPageView.setAlpha(alpha);
		}
	}

	public void hideProgress() {
		if (mPageView != null) {
			mPageView.setAlpha(1f);
		}
	}

	public void deleteItem(int position) {
		if (position >= 0 && position < mTitles.size()) {

			mGroupRes.remove(position);
			mResAdapter.notifyDataSetChanged();

			if (mGroupIndex > position) {
				mGroupIndex--;
			} else if (mGroupIndex == position) {
				int groupIndex = mGroupIndex + 1;
				if (mTitles.size() <= groupIndex) {
					groupIndex = position - 1;
				}

				if (groupIndex < 0) {
					groupIndex = 0;
				}

				if (!mTitles.isEmpty()) {
					mTitles.get(groupIndex).isSelected = true;
					mTitleAdapter.notifyItemChanged(groupIndex);
					scrollToCenter(groupIndex);
//					mRecyclerView.scrollToPosition(groupIndex);

					generateDots(getPageCount(mGroupRes.get(groupIndex).m_group.size()), 0);
				}
			}

			mTitles.remove(position);
			mTitleAdapter.notifyItemRemoved(position);
			mViewPager.setCurrentItem(getPagerPosition(mGroupIndex, 0), false);
		}
	}

	private class TitleAdapter extends RecyclerView.Adapter<ViewHolder> {

		private List<Title> mItems;

		private OnItemClickListener mListener;

		private OnItemLongClickListener mLongListener;

		TitleAdapter(List<Title> items) {
			mItems = items;
		}

		private void setOnItemClickListener(OnItemClickListener listener) {
			mListener = listener;
		}

		private void setOnItemLongClickListener(OnItemLongClickListener longListener) {
			mLongListener = longListener;
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
			TitleItem item = new TitleItem(parent.getContext());
			item.setLayoutParams(params);
			return new ViewHolder(item);
		}

		@Override
		public void onBindViewHolder(final ViewHolder holder, int position) {
			TitleItem item = (TitleItem)holder.itemView;
			item.title.setText(mItems.get(position).title);
			if (mItems.get(position).isSelected) {
				item.showIndicator(true);
				item.title.setTextColor(ImageUtils.GetSkinColor(0xffe75988));

				if (mDown) {
					item.changeItem();
				}
			} else {
				item.showIndicator(false);
				item.title.setTextColor(0x99000000);
			}

			if (mItems.get(position).isRecommend) {

				if (mItems.get(position).isLock) {
					item.mRecommendImage.setImageResource(R.drawable.sticker_lock);
				} else {
					item.mRecommendImage.setImageResource(R.drawable.sticker_recom);
				}
				item.mRecommendImage.setVisibility(VISIBLE);
				item.mNewImage.setVisibility(GONE);
			} else {
				item.mRecommendImage.setVisibility(GONE);

				if (mItems.get(position).isNew) {
					if (position == mGroupIndex) {
						mItems.get(position).isNew = false;
						DecorateResMgr2.getInstance().DeleteGroupNewFlag(getContext(), mGroupRes.get(position).m_id);
						item.mNewImage.setVisibility(GONE);
					} else {
						item.mNewImage.setVisibility(VISIBLE);
					}
				} else {
					item.mNewImage.setVisibility(GONE);
				}
			}


			item.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mListener != null) {
						mListener.onItemClick(v, holder.getAdapterPosition());
					}
				}
			});

//			item.setOnLongClickListener(new OnLongClickListener() {
//				@Override
//				public boolean onLongClick(View v) {
//					if (mLongListener != null) {
//						int position = holder.getAdapterPosition();
//						mLongListener.onItemLongClick(v, position, mItems.get(position).title,
//													  mItems.get(position).isRecommend);
//					}
//					return true;
//				}
//			});

		}

		@Override
		public int getItemCount() {
			return mItems.size();
		}
	}

	private static class ViewHolder extends RecyclerView.ViewHolder {

		private ViewHolder(View itemView) {
			super(itemView);
		}
	}

	/**
	 * 根据当前分组的资源大小获得最终页数
	 *
	 * @param size 当前分组的资源大小
	 * @return pageCount页数
	 */
	private static int getPageCount(int size) {
		int pageCount = 0;
		if (size > 0) {
			pageCount = size / PendantPage.PAGE_COUNT;
			if (size % PendantPage.PAGE_COUNT != 0) {
				pageCount++;
			}
		}

		return pageCount;
	}

	private interface OnItemClickListener {
		void onItemClick(View view, int position);
	}

	public interface OnItemLongClickListener {
		void onItemLongClick(View view, int position, String title, boolean isRecommend);
	}

	public interface OnTitleItemClickListener {
		void onItemClick(View view, int position, int lastPosition);
	}

	private static class Title {
		String title;
		boolean isSelected;
		boolean isNew;
		boolean isRecommend;
		boolean isLock;
	}
}
