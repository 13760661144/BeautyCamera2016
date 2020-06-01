package cn.poco.album.frame;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.poco.album.PhotoStore;
import cn.poco.album.adapter.FolderAdapter;
import cn.poco.album.adapter.PhotoAdapter;
import cn.poco.album.model.FolderInfo;
import cn.poco.album.model.PhotoInfo;
import cn.poco.album.site.AlbumSite;
import cn.poco.album.utils.ListItemDecoration;
import cn.poco.album.utils.PhotoGridDivide;
import cn.poco.tianutils.ShareData;

/**
 * Created by: fwc
 * Date: 2016/12/15
 */
public abstract class BaseFrame extends FrameLayout {

	protected Context mContext;
	protected AlbumSite mSite;

	protected PhotoStore mPhotoStore;
	protected int mFolderIndex = 0;

	protected RecyclerView mPhotoGridView;
	protected GridLayoutManager mLayoutManager;
	protected PhotoAdapter mPhotoAdapter;
	protected List<PhotoInfo> mItems;

	protected RecyclerView mFolderListView;
	protected FolderAdapter mFolderAdapter;

	protected RelativeLayout mTopLayout;

	protected boolean mShowFolder;

	private OnScrollListener mOnScrollListener;

	protected int mTopBarHeight;

	protected int mBottomBarHeight;

	protected boolean mLoading = false;

	public BaseFrame(Context context, AlbumSite site) {
		super(context);

		mContext = context;
		mSite = site;

		init();
	}

	protected void init() {
		mPhotoStore = PhotoStore.getInstance(mContext);
		mFolderIndex = PhotoStore.sLastFolderIndex;

		initDatas();

		initPhotoGridView();
		initFolderListView();
		initTopLayout();

		initViews();
		setData();
		setListener();
	}

	protected void initPhotoGridView() {
		mPhotoGridView = new RecyclerView(mContext) {
			@Override
			public boolean onTouchEvent(MotionEvent e) {
				if (e.getAction() == MotionEvent.ACTION_MOVE && isScrollToTop()) {
					getParent().requestDisallowInterceptTouchEvent(false);
				}
				return super.onTouchEvent(e);
			}
		};
		mPhotoGridView.setHasFixedSize(true);
		mLayoutManager = new GridLayoutManager(mContext, 3);
		mPhotoGridView.setLayoutManager(mLayoutManager);
		mPhotoGridView.setBackgroundColor(Color.WHITE);
		mPhotoGridView.addItemDecoration(new PhotoGridDivide(ShareData.PxToDpi_xhdpi(3), ShareData.PxToDpi_xhdpi(3), hasHeader()));
		mPhotoGridView.setOverScrollMode(OVER_SCROLL_NEVER);

		addLoadMoreListener();
	}

	protected void addLoadMoreListener() {
		mPhotoGridView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				if (!mShowFolder) {
					if (dy > 0) {
						// 类似上拉加载
						FolderInfo folderInfo = mPhotoStore.getFolderInfo(mFolderIndex);
						int last = mLayoutManager.findLastVisibleItemPosition();
						if (folderInfo.getCount() > mItems.size() && last >= mItems.size() - 6 && !mLoading) {
							mLoading = true;
							List<PhotoInfo> item = mPhotoStore.getPhotoInfos(folderInfo.getName(), mItems.size(), mPhotoStore.getCacheSize());
							mItems.addAll(item);
							mPhotoAdapter.notifyDataSetChanged();
							mLoading = false;
						}
					}

					if (mOnScrollListener != null) {
						mOnScrollListener.onScrolled(dx, dy);
					}
				}
			}
		});
	}

	protected void setOnScrollListener(OnScrollListener listener) {
		mOnScrollListener = listener;
	}

	protected void initPhotoItemsAndAdapter(boolean showEdit) {

		FolderInfo folderInfo = mPhotoStore.getFolderInfo(mFolderIndex);

		mItems = new ArrayList<>();
		mItems.addAll(mPhotoStore.getPhotoInfos(folderInfo.getName(), 0));
		mPhotoAdapter = new PhotoAdapter(mContext, mItems, showEdit);
		mPhotoGridView.setAdapter(mPhotoAdapter);
	}

	protected void initFolderListView() {
		mFolderListView = new RecyclerView(mContext);
		mFolderListView.setBackgroundColor(Color.WHITE);
		mFolderListView.setLayoutManager(new LinearLayoutManager(mContext));
		mFolderListView.setOverScrollMode(OVER_SCROLL_NEVER);
		mFolderListView.addItemDecoration(new ListItemDecoration(ShareData.PxToDpi_xhdpi(26), ListItemDecoration.VERTICAL));
	}

	protected void initFolderAdapter() {
		mFolderAdapter = new FolderAdapter(mContext, mPhotoStore.getFolderInfos());
		mFolderListView.setAdapter(mFolderAdapter);
	}

	protected void initTopLayout() {
		mTopLayout = new RelativeLayout(mContext);
		mTopLayout.setBackgroundColor(Color.WHITE);
		mTopLayout.setClickable(true);
	}

	public boolean isScrollToTop() {
		return !mPhotoGridView.canScrollVertically(-1);
	}

	public void changeSkin() {

	}

	protected abstract void initDatas();

	protected abstract void initViews();

	protected abstract void setData();

	protected abstract void setListener();

	public abstract boolean onBack();

	public void onPageResult(int siteID, HashMap<String, Object> params) {

	}

	protected boolean hasHeader() {
		return false;
	}

	protected interface OnScrollListener {
		void onScrolled(int dx, int dy);
	}

	public void notifyUpdate(int folderIndex) {

//		mPhotoStore.initFolderInfos();
//		mFolderAdapter.notifyDataSetChanged();

		mFolderIndex = folderIndex;
		FolderInfo folderInfo = mPhotoStore.getFolderInfo(folderIndex);
		mPhotoGridView.scrollToPosition(0);
		mItems.clear();
		mItems.addAll(mPhotoStore.getPhotoInfos(folderInfo.getName(), 0));
		mPhotoAdapter.notifyDataSetChanged();
	}

	public void onClose() {
		if(mPhotoGridView != null)
		{
			mPhotoGridView.setAdapter(null);
		}
	}
}
