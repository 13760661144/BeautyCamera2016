package cn.poco.arWish;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.circle.ctrls.RoundedImageView;
import com.circle.utils.Utils;
import com.circle.utils.dn.DnImg;
import com.taotie.circle.LocationReader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.advanced.ImageUtils;
import cn.poco.arWish.FindWishInputPass.InputCallback;
import cn.poco.arWish.PullToRefreshLayout.OnRefreshListener;
import cn.poco.arWish.dataInfo.WishItemInfo;
import cn.poco.arWish.dataInfo.WishPageInfo;
import cn.poco.arWish.serviceAPI.ServiceAPI;
import cn.poco.arWish.site.NearWishesPageSite;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.widget.LoadingBar;
import my.beautyCamera.R;

/**
 * Created by Anson on 2018/1/18.
 */

public class NearWishesPage extends IPage
{
	private NearWishesPageSite mSite;

	public NearWishesPage(Context context, BaseSite site)
	{
		super(context, site);
		mSite = (NearWishesPageSite)site;
		initialize(context);
	}

	private ImageView mBackBtn;
	private TextView mTitle;
	private TextView mLocationTx, mLocationTips;
	private PullToRefreshLayout mRefreshLayout;
	private GridView mGridView;
	private ArrayList<WishItemInfo> mDatas = new ArrayList<WishItemInfo>();
	private GridAdapter mAdapter;

	private LoadingBar mLoadingBar;
	private DnImg mDnImg = new DnImg();
	private Handler mHandler = new Handler();
	private double mLon, mLat;
	private TextView mNoContent;
	private boolean isRefresh = false;
	private RelativeLayout mNoContentLay;
	private Button mPublishBtn;
	private String mAddress, mRangLocation;
	private FindWishInputPass mPWInputDlg;

	private void initialize(Context context)
	{
		LayoutParams fPrams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		RelativeLayout mainContain = new RelativeLayout(context);
		addView(mainContain, fPrams);

		RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(100));
		rParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		RelativeLayout topBar = new RelativeLayout(context);
		topBar.setBackgroundColor(0xfffafafa);
		mainContain.addView(topBar, rParams);
		topBar.setId(Utils.generateViewId());

		rParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rParams.addRule(RelativeLayout.CENTER_VERTICAL);
		rParams.topMargin = ShareData.PxToDpi_xhdpi(5);
		rParams.leftMargin = ShareData.PxToDpi_xhdpi(2);
		mBackBtn = new ImageView(context);
		mBackBtn.setScaleType(ScaleType.CENTER_CROP);
		mBackBtn.setImageResource(R.drawable.framework_back_btn);
		topBar.addView(mBackBtn, rParams);
		ImageUtils.AddSkin(getContext(), mBackBtn);
		mBackBtn.setOnTouchListener(mOnAnimationClickListener);

		rParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		mTitle = new TextView(getContext());
		mTitle.setTextColor(0xff333333);
		mTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
		mTitle.setText(R.string.arwish_findwish_title);
		topBar.addView(mTitle, rParams);

		rParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		rParams.addRule(RelativeLayout.BELOW, topBar.getId());
		RelativeLayout containLay = new RelativeLayout(context);
		containLay.setBackgroundColor(0xfffafafa);
		mainContain.addView(containLay, rParams);

		rParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mRefreshLayout = new PullToRefreshLayout(getContext());
		mRefreshLayout.setRefreshMode(PullToRefreshLayout.ABOVE_BELOW, true);
		mRefreshLayout.setOnRefreshListener(mOnRefreshListener);
		containLay.addView(mRefreshLayout,rParams);

		rParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		RelativeLayout locationLay = new RelativeLayout(context);
		mRefreshLayout.addView(locationLay,rParams);
		locationLay.setId(Utils.generateViewId());

		rParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rParams.topMargin = ShareData.PxToDpi_xhdpi(20);
		RelativeLayout layout1 = new RelativeLayout(context);
		locationLay.addView(layout1, rParams);
		layout1.setId(Utils.generateViewId());

		rParams = new RelativeLayout.LayoutParams(ShareData.PxToDpi_xhdpi(32), ShareData.PxToDpi_xhdpi(32));
		rParams.addRule(RelativeLayout.CENTER_VERTICAL);
		ImageView icon = new ImageView(context);
		icon.setScaleType(ScaleType.CENTER_CROP);
		icon.setImageResource(R.drawable.ar_location_icon);
		layout1.addView(icon, rParams);
		icon.setId(Utils.generateViewId());

		rParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rParams.addRule(RelativeLayout.CENTER_VERTICAL);
		rParams.addRule(RelativeLayout.RIGHT_OF, icon.getId());
		mLocationTx = new TextView(context);
		mLocationTx.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
		mLocationTx.setTextColor(0xff4c4c4c);
		mLocationTx.setText("");
		layout1.addView(mLocationTx, rParams);

		rParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rParams.addRule(RelativeLayout.BELOW, layout1.getId());
		rParams.topMargin = ShareData.PxToDpi_xhdpi(15);
		mLocationTips = new TextView(context);
		mLocationTips.setTextColor(0xff808080);
		mLocationTips.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
		mLocationTips.setText("");
		locationLay.addView(mLocationTips, rParams);


		rParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		rParams.addRule(RelativeLayout.BELOW, locationLay.getId());
		mGridView = new GridView(context);
		mGridView.setNumColumns(3);
		mGridView.setColumnWidth(ShareData.PxToDpi_xhdpi(232));
		mGridView.setVerticalSpacing(ShareData.PxToDpi_xhdpi(20));
		mGridView.setHorizontalSpacing(ShareData.PxToDpi_xhdpi(12));
		mGridView.setStretchMode(GridView.STRETCH_SPACING);
		mGridView.setGravity(Gravity.CENTER);
		mRefreshLayout.addView(mGridView, rParams);

		rParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		mNoContent = new TextView(context);
		mNoContent.setTextSize(16);
		containLay.addView(mNoContent, rParams);
		mNoContent.setVisibility(GONE);

		rParams = new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		rParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		mLoadingBar = new LoadingBar(context);
		mLoadingBar.setTextColor(0xff333333);
		mLoadingBar.setLoadMode(true);
		mainContain.addView(mLoadingBar, rParams);
		mLoadingBar.setVisibility(GONE);

		rParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rParams.topMargin = ShareData.PxToDpi_xhdpi(311);
		mNoContentLay = new RelativeLayout(context);
		containLay.addView(mNoContentLay, rParams);
		mNoContentLay.setVisibility(GONE);

		rParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		ImageView noCXImg = new ImageView(context);
		noCXImg.setScaleType(ScaleType.CENTER_CROP);
		noCXImg.setImageResource(R.drawable.ar_nocontent_icon);
		mNoContentLay.addView(noCXImg);
		noCXImg.setId(Utils.generateViewId());

		rParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rParams.addRule(RelativeLayout.BELOW, noCXImg.getId());
		rParams.topMargin = ShareData.PxToDpi_xhdpi(24);
		TextView noCXTx = new TextView(context);
		noCXTx.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
		noCXTx.setTextColor(0xff808080);
		noCXTx.setText(R.string.arwish_findwish_nowish);
		mNoContentLay.addView(noCXTx, rParams);
		noCXTx.setId(Utils.generateViewId());

		GradientDrawable btnBg = new GradientDrawable();
		btnBg.setColor(ImageUtils.GetSkinColor());
		btnBg.setCornerRadius(ShareData.PxToDpi_xhdpi(55));

		rParams = new RelativeLayout.LayoutParams(ShareData.PxToDpi_xhdpi(358), ShareData.PxToDpi_xhdpi(110));
		rParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rParams.addRule(RelativeLayout.BELOW, noCXTx.getId());
		rParams.topMargin = ShareData.PxToDpi_xhdpi(62);
		mPublishBtn = new Button(context);
		mPublishBtn.setBackground(btnBg);
		mPublishBtn.setText(R.string.arwish_findwish_publish);
		mPublishBtn.setOnTouchListener(mOnAnimationClickListener);
		mNoContentLay.addView(mPublishBtn, rParams);

		mAdapter = new GridAdapter();
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(new GridViewOnItemClickListener());

		mPWInputDlg = new FindWishInputPass((Activity)context, R.style.dialog);
		mPWInputDlg.setCallback(mCallBack);
	}

	private void setPageInfo(WishPageInfo pageInfo)
	{
		if(pageInfo != null)
		{
			mDatas.clear();
			mLocationTx.setText(mAddress);
			mRangLocation = pageInfo.rangLocation;
			mLocationTips.setText(mRangLocation);
			if(pageInfo.itemInfos != null)
			{
				mDatas.addAll(pageInfo.itemInfos);
				mAdapter.notifyDataSetChanged();
			}

		}
	}

	private void getPageInfo()
	{
		//添加定位(如有需要，需动态判断获取定位权限)
		doLocation();
	}

	private void doLocation()
	{
		if(!isRefresh)
		{
			mHandler.post(new Runnable()
			{
				@Override
				public void run()
				{
					mLoadingBar.setText(getResources().getString(R.string.arwish_findwish_location));
					mLoadingBar.setVisibility(VISIBLE);
				}
			});
		}

		new LocationReader().getLocation(getContext(), new LocationReader.OnCompletionLocationListener()
		{
			@Override
			public void onComplete(final double lon, final double lat, AMapLocation location, final int errorCode)
			{
				if(errorCode == 0)
				{
					mLat = lat;
					mLon = lon;
					if(location != null)
					{
						mAddress = location.getAoiName();
						if(mAddress == null)
						{
							mAddress = location.getStreet();
						}
						mLocationTx.setText(mAddress);
					}
					if(!isRefresh)
					{
						mLoadingBar.setText(getResources().getString(R.string.arwish_findwish_nearwish));
					}
					refreshData();
				}
				else
				{
					Log.i("getLocation", "errorCode:" + errorCode);
//					DialogUtils.showToast(getContext(), "获取当前位置失败", Toast.LENGTH_SHORT, 0);
					mLoadingBar.setVisibility(GONE);
					mRefreshLayout.setVisibility(GONE);
					mNoContentLay.setVisibility(GONE);

					mNoContent.setText(R.string.arwish_findwish_location_fail);
					mNoContent.setVisibility(VISIBLE);

				}
			}
		});
	}


	private int mIndex = 1;//加载起点；
	private final int PAGESIZE = 18;
	private boolean mHasMore = true;
	private boolean mLoading = false;

	private void loadMore()
	{
		if(mLoading == false && mHasMore == true)
		{
			mLoading = true;
			mIndex += 1;
			new Thread(new Runnable()
			{
				@Override
				public void run() {
					final WishPageInfo itemInfo = refreshDataProc();
					mLoading = true;
					mHandler.post(new Runnable()
					{

						@Override
						public void run() {
							mRefreshLayout.onRefreshFinish();
							if(mHasMore)
							{
								mRefreshLayout.setBottomEnable(true);
							}else
							{
								mRefreshLayout.setBottomEnable(false);
							}
							mHandler.postDelayed(new Runnable()
							{
								@Override
								public void run() {
									mLoading = false;
								}
							}, 100);
							if(itemInfo == null)
							{
								Toast.makeText(getContext(), R.string.arwish_findwish_load_fail, Toast.LENGTH_LONG).show();
								return;
							}
							if(itemInfo != null && itemInfo.itemInfos != null && itemInfo.itemInfos.size() > 0)
							{
								mDatas.addAll(itemInfo.itemInfos);
								mAdapter.notifyDataSetChanged();
							}
						}
					});
				}
			}).start();
		}
	}

	public void refreshData()
	{
		mIndex = 1;
		mHasMore = true;
		mRefreshLayout.setBottomEnable(true);
		if(mLoading == false)
		{
			mLoading = true;
			new Thread(new Runnable()
			{

				@Override
				public void run() {
					final WishPageInfo itemInfo = refreshDataProc();
					if(itemInfo != null)
					{
						if(itemInfo.itemInfos == null /*|| itemInfo.mGoodsInfos.size() < PAGESIZE*/)
						{
							mHasMore = false;
						}
					}
					mHandler.post(new Runnable()
					{
						@Override
						public void run() {
							mLoadingBar.setVisibility(GONE);
							mNoContent.setVisibility(GONE);
							mRefreshLayout.setVisibility(VISIBLE);
							mNoContentLay.setVisibility(GONE);

							mRefreshLayout.onRefreshFinish();
							isRefresh = false;
							if(itemInfo != null && itemInfo.itemInfos != null && itemInfo.itemInfos.size() > 0)
							{
								setPageInfo(itemInfo);
							}else
							{
								//没有内容
								mRefreshLayout.setVisibility(GONE);
								mNoContentLay.setVisibility(VISIBLE);
							}
						}

					});
				}

			}).start();
		}
	}

	private WishPageInfo refreshDataProc()
	{
		JSONObject postJson = new JSONObject();
		try {
//			postJson.put("lng", mLon);
//			postJson.put("lat", mLat);

			postJson.put("lng", "113.3198230");
			postJson.put("lat", "23.1208340");


			postJson.put("page", mIndex);
			postJson.put("page_size", PAGESIZE);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		WishPageInfo itemInfo = ServiceAPI.getWishPageInfo(getContext(), postJson);
		if(itemInfo != null && (itemInfo.itemInfos == null || itemInfo.itemInfos.size() == 0))
		{
			mHasMore = false;
		}
		mLoading = false;
		return itemInfo;
	}

	private WishPageInfo getDemoInfo()
	{
		WishPageInfo pageInfo = new WishPageInfo();
		pageInfo.loctionName = "泰恒大厦";
		pageInfo.rangLocation = "附近<100的祝福";
		if(mIndex > 5)
		{
			return pageInfo;
		}
		pageInfo.itemInfos = new ArrayList<>();
		for(int i = 0; i < PAGESIZE; i++)
		{
			WishItemInfo item = new WishItemInfo();
			item.imageUrl = "http://image19-d.yueus.com/yueyue/20160617/20160617232955_134297_352322_15742.jpg?726x1024_120";
			item.userIcon = "http://image19-d.yueus.com/yueyue/20160617/20160617232955_134297_352322_15740.jpg?706x1024_120";
			item.userName = "天真的老司机" +i;
			if(i%3 == 0)
			{
				item.isSecret = true;
			}
			pageInfo.itemInfos.add(item);
		}
		return pageInfo;
	}



	private class GridViewOnItemClickListener implements AdapterView.OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			mSite.m_myParams.put("data", mDatas);    //保存当前页数据，做现场还原
			mSite.m_myParams.put("index", position);
			mSite.m_myParams.put("loadindex", mIndex);
			mSite.m_myParams.put("lon", mLon);
			mSite.m_myParams.put("lat", mLat);
			mSite.m_myParams.put("location", mAddress);
			mSite.m_myParams.put("rang", mRangLocation);


			if(mDatas.get(position).isSecret)
			{
				if(mPWInputDlg != null)
				{
					mPWInputDlg.show();
				}
			}else{
				HashMap<String, Object> params = new HashMap<>();
				params.put("data", mDatas);
				params.put("index", position);
				params.put("loadindex", mIndex);
				params.put("lon", mLon);
				params.put("lat", mLat);
				params.put("location", mAddress);
				mSite.openFindPage(getContext(), params);
			}
		}
	}

	private InputCallback mCallBack = new InputCallback()
	{
		@Override
		public void onOk(String pwd)
		{
			//提交服务器验证密码


		}

		@Override
		public void onCancel()
		{

		}
	};



	private OnAnimationClickListener mOnAnimationClickListener = new OnAnimationClickListener()
	{
		@Override
		public void onAnimationClick(View v)
		{
			if(v == mBackBtn)
			{
				onBack();
			}else if(v == mPublishBtn)
			{
				mSite.OnOpenPublishWishPage(getContext());
			}
		}
	};

	private class GridAdapter extends BaseAdapter
	{

		@Override
		public int getCount()
		{
			return mDatas.size();
		}

		@Override
		public Object getItem(int position)
		{
			return mDatas.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			GridItem item;
			if(convertView == null)
			{
				item = new GridItem(getContext());
			}else
			{
				item = (GridItem)convertView;
			}
			item.setItemInfo(mDatas.get(position));
			return item;
		}
	}

	public class GridItem extends RelativeLayout
	{

		public GridItem(Context context)
		{
			super(context);
			initialize(context);
		}

		private RoundedImageView mImageView, mIconView;
		private WishItemInfo mItemInfo;

		private void initialize(Context context)
		{
			LayoutParams rParams = new LayoutParams(ShareData.PxToDpi_xhdpi(184),ShareData.PxToDpi_xhdpi(184));
			rParams.topMargin = ShareData.PxToDpi_xhdpi(45);
			rParams.leftMargin = rParams.rightMargin = ShareData.PxToDpi_xhdpi(24);
			mImageView = new RoundedImageView(context);
			mImageView.setOval(true);
			mImageView.setScaleType(ScaleType.CENTER_CROP);
			mImageView.setImageResource(R.drawable.defaultpic);
			addView(mImageView, rParams);
			mImageView.setId(Utils.generateViewId());

			rParams = new LayoutParams(ShareData.PxToDpi_xhdpi(58),ShareData.PxToDpi_xhdpi(58));
			rParams.addRule(RelativeLayout.ALIGN_RIGHT, mImageView.getId());
//			rParams.addRule(RelativeLayout.ALIGN_BOTTOM, mImageView.getId());
			rParams.topMargin = ShareData.PxToDpi_xhdpi(173);
			mIconView = new RoundedImageView(context);
			mIconView.setOval(true);
			mIconView.setScaleType(ScaleType.CENTER_CROP);
			mIconView.setBorderColor(0xffffffff);
			mIconView.setBorderWidth(ShareData.PxToDpi_xhdpi(4));
			mIconView.setImageResource(R.drawable.ar_default_usericon);
			addView(mIconView, rParams);


		}

		public void setItemInfo(final WishItemInfo itemInfo)
		{
			if(itemInfo != null && itemInfo != mItemInfo)
			{
				boolean equals = false;
				if(mItemInfo != null
						&& mItemInfo.imageUrl != null
						&& itemInfo.imageUrl != null
						&& itemInfo.imageUrl.equals(mItemInfo.imageUrl))
				{
					equals = true;
				}
				if(!equals && itemInfo.imageUrl != null && itemInfo.imageUrl.length() > 0)
				{
					mImageView.setImageBitmap(null);
					mImageView.setImageResource(R.drawable.defaultpic);
					mDnImg.dnImg(itemInfo.imageUrl, ShareData.PxToDpi_xhdpi(215), new DnImg.OnDnImgListener() {

						@Override
						public void onProgress(String url, int downloadedSize, int totalSize) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onFinish(String url, String file, Bitmap bmp) {

							if (url.equals(itemInfo.imageUrl)) {
								mImageView.setImageBitmap(bmp);
							}
						}
					});
				}
				if(itemInfo.userIcon != null && itemInfo.userIcon.length() > 0)
				{
					mDnImg.dnImg(itemInfo.userIcon, ShareData.PxToDpi_xhdpi(100), new DnImg.OnDnImgListener()
					{

						@Override
						public void onProgress(String url, int downloadedSize, int totalSize)
						{
							// TODO Auto-generated method stub

						}

						@Override
						public void onFinish(String url, String file, Bitmap bmp)
						{

							if(url.equals(itemInfo.userIcon))
							{
								mIconView.setImageBitmap(bmp);
							}
						}
					});
				}

				mItemInfo = itemInfo;
			}
		}
	}

	private OnRefreshListener mOnRefreshListener = new OnRefreshListener() {

		@Override
		public void onSlidingFinish() {
			mAdapter.notifyDataSetChanged();
		}

		@Override
		public void onRefresh() {
			isRefresh = true;
			getPageInfo();
		}

		@Override
		public void onLoadMore() {
			loadMore();
		}
	};


	@Override
	public void SetData(HashMap<String, Object> params)
	{
		if(mSite.m_myParams.containsKey("data"))
		{
			params = mSite.m_myParams;
			WishPageInfo pageInfo = new WishPageInfo();

			ArrayList<WishItemInfo> infos = (ArrayList<WishItemInfo>)params.get("data");
			if(infos != null)
			{
				pageInfo.itemInfos = infos;
			}
			mIndex = (int)params.get("loadindex");
			mLon = (double)params.get("lon");
			mLat = (double)params.get("lat");
			mAddress = (String)params.get("location");
			pageInfo.rangLocation = (String)params.get("rang");
			setPageInfo(pageInfo);

			int position = (int)params.get("index");
			if(position != 0)
			{
				mGridView.setSelection(position);
			}
			mSite.m_myParams.clear();
		}else
		{
			getPageInfo();
		}
	}

	@Override
	public void onBack()
	{
		mSite.OnBack(getContext());
	}

	@Override
	public void onClose()
	{
		if(mDnImg != null)
		{
			mDnImg.stopAll();
			mDnImg = null;
		}
		super.onClose();
	}


}
