package cn.poco.arWish;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils.TruncateAt;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.circle.ctrls.RoundedImageView;
import com.circle.utils.Utils;
import com.circle.utils.dn.DnImg;
import com.circle.utils.dn.DnImg.OnDnImgListener;
import com.taotie.circle.Configure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import cn.poco.advanced.ImageUtils;
import cn.poco.arWish.dataInfo.WishItemInfo;
import cn.poco.arWish.dataInfo.WishPageInfo;
import cn.poco.arWish.serviceAPI.ServiceAPI;
import cn.poco.arWish.site.FindWishPageSite;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.system.FolderMgr;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

/**
 * Created by Anson on 2018/1/19.
 */

public class FindWishPage extends IPage
{
	private FindWishPageSite mSite;

	public FindWishPage(Context context, BaseSite site)
	{
		super(context, site);
		mSite = (FindWishPageSite)site;
		initialize(context);
	}

	private ImageView mBackBtn;
	private TextView mTitle;
	private TextView mLocationTx;
	private ViewPager mViewPager;
	private Button mFindBtn;
	private ArrayList<WishItemInfo> mDatas = new ArrayList<WishItemInfo>();
	private WishItemInfo mCurrentItem;
	private ViewPagerAdapter mAdapter;
	private DnImg mDnImg = new DnImg();
	private int currentIndex;
	private Handler mHandler = new Handler();
	private double mLon, mLat;
	private String mLocationName;
	private RelativeLayout mViewPagerContainer;


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

		rParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rParams.addRule(RelativeLayout.BELOW, topBar.getId());
		rParams.topMargin = ShareData.PxToDpi_xhdpi(130);
		RelativeLayout layout1 = new RelativeLayout(context);
		mainContain.addView(layout1, rParams);
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

		rParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(200));
		rParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		RelativeLayout bottomLay = new RelativeLayout(context);
		mainContain.addView(bottomLay, rParams);
		bottomLay.setId(Utils.generateViewId());

		rParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		rParams.bottomMargin = ShareData.PxToDpi_xhdpi(52);
		TextView tips = new TextView(context);
		tips.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		tips.setTextColor(0xff666666);
		tips.setText(R.string.arwish_findwish_findtips);
		bottomLay.addView(tips, rParams);
		tips.setId(Utils.generateViewId());

		GradientDrawable btnBg = new GradientDrawable();
		btnBg.setColor(ImageUtils.GetSkinColor());
		btnBg.setCornerRadius(ShareData.PxToDpi_xhdpi(55));

		rParams = new RelativeLayout.LayoutParams(ShareData.PxToDpi_xhdpi(360), ShareData.PxToDpi_xhdpi(110));
		rParams.addRule(RelativeLayout.ABOVE, tips.getId());
		rParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rParams.bottomMargin = ShareData.PxToDpi_xhdpi(15);
		mFindBtn = new Button(context);
		mFindBtn.setBackground(btnBg);
		mFindBtn.setText(R.string.arwish_findwish_findbtn);
		bottomLay.addView(mFindBtn, rParams);
		mFindBtn.setOnTouchListener(mOnAnimationClickListener);
		mFindBtn.setId(Utils.generateViewId());

		rParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		rParams.addRule(RelativeLayout.BELOW, layout1.getId());
		rParams.addRule(RelativeLayout.ABOVE, bottomLay.getId());
		mViewPagerContainer = new RelativeLayout(context);
		mViewPagerContainer.setClipChildren(false);
		mainContain.addView(mViewPagerContainer, rParams);

		rParams = new RelativeLayout.LayoutParams(ShareData.PxToDpi_xhdpi(500), RelativeLayout.LayoutParams.MATCH_PARENT);
		rParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		mViewPager = new ViewPager(context);
		mViewPager.setOffscreenPageLimit(3);
		mViewPager.setClipChildren(false);
//		mViewPager.setPageMargin(Utils.getRealPixel2(14));
		mViewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
		mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
		mViewPagerContainer.addView(mViewPager, rParams);

		mViewPagerContainer.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// dispatch the events to the ViewPager, to solve the problem that we can swipe only the middle view.
				return mViewPager.dispatchTouchEvent(event);
			}
		});

		mAdapter = new ViewPagerAdapter();
		mViewPager.setAdapter(mAdapter);
		mViewPager.addOnPageChangeListener(new MyOnPageChangeListener());

	}

	private class PageItem extends RelativeLayout{

		public PageItem(Context context)
		{
			super(context);
			init(context);
		}

		public RoundedImageView mImageView, mIconView;
		public TextView mUserName;
		public ImageView mMaskView;

		private void init(Context context)
		{
			setGravity(Gravity.CENTER);

			LayoutParams rParams = new LayoutParams(ShareData.PxToDpi_xhdpi(437), ShareData.PxToDpi_xhdpi(437));
			rParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
			RelativeLayout imageLay = new RelativeLayout(context);
			addView(imageLay, rParams);
			imageLay.setId(Utils.generateViewId());

			rParams = new LayoutParams(ShareData.PxToDpi_xhdpi(434), ShareData.PxToDpi_xhdpi(434));
			rParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			mImageView = new RoundedImageView(context);
			mImageView.setOval(true);
			mImageView.setScaleType(ScaleType.CENTER_CROP);
			mImageView.setImageResource(R.drawable.defaultpic);
			imageLay.addView(mImageView, rParams);

			rParams = new LayoutParams(ShareData.PxToDpi_xhdpi(437), ShareData.PxToDpi_xhdpi(437));
			rParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			mMaskView = new RoundedImageView(context);
			mMaskView.setScaleType(ScaleType.CENTER_CROP);
			mMaskView.setImageResource(R.drawable.ar_find_image_mask);
			mMaskView.setId(Utils.generateViewId());
			imageLay.addView(mMaskView, rParams);

			rParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			rParams.addRule(RelativeLayout.BELOW, imageLay.getId());
			rParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
			rParams.topMargin = ShareData.PxToDpi_xhdpi(77);
			RelativeLayout userLay = new RelativeLayout(context);
			addView(userLay, rParams);

			rParams = new LayoutParams(ShareData.PxToDpi_xhdpi(68), ShareData.PxToDpi_xhdpi(68));
			rParams.addRule(RelativeLayout.CENTER_VERTICAL);
			mIconView = new RoundedImageView(context);
			mIconView.setOval(true);
			mIconView.setScaleType(ScaleType.CENTER_CROP);
			mIconView.setImageResource(R.drawable.ar_default_usericon);
			mIconView.setId(Utils.generateViewId());
			userLay.addView(mIconView, rParams);

			rParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			rParams.addRule(RelativeLayout.RIGHT_OF, mIconView.getId());
			rParams.addRule(RelativeLayout.CENTER_VERTICAL);
			rParams.leftMargin = ShareData.PxToDpi_xhdpi(15);
			mUserName = new TextView(context);
			mUserName.setFilters(new InputFilter[]{new LengthFilter(10)});
			mUserName.setEllipsize(TruncateAt.END);
			mUserName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
			mUserName.setTextColor(0xff333333);
			mUserName.setText("");
			userLay.addView(mUserName, rParams);
		}


		private void setItemInfo(final WishItemInfo itemInfo)
		{
			if(itemInfo != null)
			{
				mUserName.setText(itemInfo.userName);
				if(itemInfo.imageUrl != null && itemInfo.imageUrl.length() > 0)
				{
					mImageView.setImageBitmap(null);
					mImageView.setImageResource(R.drawable.defaultpic);
					mDnImg.dnImg(itemInfo.imageUrl, ShareData.PxToDpi_xhdpi(500), new DnImg.OnDnImgListener()
					{

						@Override
						public void onProgress(String url, int downloadedSize, int totalSize)
						{
							// TODO Auto-generated method stub

						}

						@Override
						public void onFinish(String url, String file, Bitmap bmp)
						{

							if(url.equals(itemInfo.imageUrl))
							{
								mImageView.setImageBitmap(bmp);
							}
						}
					});
				}
				if(itemInfo.userIcon != null && itemInfo.userIcon.length() > 0)
				{
					mDnImg.dnImg(itemInfo.userIcon, ShareData.PxToDpi_xhdpi(100), new DnImg.OnDnImgListener() {

						@Override
						public void onProgress(String url, int downloadedSize, int totalSize) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onFinish(String url, String file, Bitmap bmp) {

							if (url.equals(itemInfo.userIcon)) {
								mIconView.setImageBitmap(bmp);
							}
						}
					});
				}

			}

		}

		public void setMaskViewVisible(boolean visible)
		{
			if(visible)
			{
				mMaskView.setVisibility(VISIBLE);
			}else
			{
				mMaskView.setVisibility(GONE);
			}
		}
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

	private WishPageInfo refreshDataProc()
	{
		JSONObject postJson = new JSONObject();
		try {
			postJson.put("user_id", Configure.getLoginUid());
			postJson.put("access_token", Configure.getLoginToken()); // 为在售状态
			postJson.put("lon", mLon);
			postJson.put("lat", mLat);
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
		if(mIndex > 50)
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
			pageInfo.itemInfos.add(item);
		}
		return pageInfo;
	}

	/**
	 * 实现的原理是，在当前显示页面放大至原来的MAX_SCALE
	 * 其他页面才是正常的的大小MIN_SCALE
	 */
	class ZoomOutPageTransformer implements ViewPager.PageTransformer {
		//		private static final float MAX_SCALE = 1.1f;
//		private static final float MIN_SCALE = 1.0f;//0.85f
		private static final float MIN_ALPHA = 0.6f;
		private static final float MAX_SCALE = 1.0f;
		private static final float MIN_SCALE = 0.9f;

		@Override
		public void transformPage(View view, float position) {
			if (position < -1){
				view.setScaleX(MIN_SCALE);
				view.setScaleY(MIN_SCALE);
				view.setAlpha(MIN_ALPHA);
				if(view instanceof PageItem)
				{
					((PageItem)view).setMaskViewVisible(false);
				}
			} else if (position <= 1) //a页滑动至b页 ； a页从 0.0 -1 ；b页从1 ~ 0.0
			{ // [-1,1]
				float scaleFactor =  MIN_SCALE+(1-Math.abs(position))*(MAX_SCALE-MIN_SCALE);

				//每次滑动后进行微小的移动目的是为了防止在三星的某些手机上出现两边的页面为显示的情况
				if(position>0){
					view.setTranslationX(-scaleFactor*2);
				}else if(position<0){
					view.setTranslationX(scaleFactor*2);
				}
				view.setScaleX(scaleFactor);
				view.setScaleY(scaleFactor);
				float alphaFactor = MIN_ALPHA + (1-Math.abs(position))*(1-MIN_ALPHA);
				view.setAlpha(alphaFactor);
				if(view instanceof PageItem)
				{
					if(alphaFactor == 1.0)
					{
						((PageItem)view).setMaskViewVisible(true);
					}else
					{
						((PageItem)view).setMaskViewVisible(false);
					}

				}
			} else
			{ // (1,+Infinity]

				view.setScaleX(MIN_SCALE);
				view.setScaleY(MIN_SCALE);
				view.setAlpha(MIN_ALPHA);
				if(view instanceof PageItem)
				{
					((PageItem)view).setMaskViewVisible(false);
				}
			}
		}

	}



	private class ViewPagerAdapter extends PagerAdapter
	{

		//view复用
		private LinkedList<PageItem> mViewCache = null;

		public ViewPagerAdapter() {
			mViewCache = new LinkedList<PageItem>();
		}

		public void clearCache()
		{
			mViewCache.clear();
		}


		@Override
		public int getCount() {
			return mDatas.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return (view == object);
		}

		@Override
		public int getItemPosition(Object object)
		{
			Object obj = ((View)object).getTag();
			for(int i = 0; i < mDatas.size(); i++)
			{
				if(obj == mDatas.get(i))
					return i;
			}
			return POSITION_NONE;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			PageItem item = null;
			if (mViewCache.size() == 0) {
				item = new PageItem(getContext());

			} else {
				item = mViewCache.removeFirst();
			}

			item.setItemInfo(mDatas.get(position));
			item.setTag(mDatas.get(position));
//			item.setOnClickListener(mOnPageItemClick);
			((ViewPager)container).addView(item);
			return item;

		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager)container).removeView((PageItem)object);
			mViewCache.add((PageItem) object);
		}
	}

	public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

		@Override
		public void onPageSelected(int position) {
			currentIndex = position;
			if(mDatas != null && position < mDatas.size())
			{
				mCurrentItem = mDatas.get(position);
			}
			if(mDatas.size() > 5 && position > mDatas.size() -5)
			{
				//加载更多
				loadMore();
			}
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			if (mViewPagerContainer != null) {
				mViewPagerContainer.invalidate();
			}
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	}

	private OnAnimationClickListener mOnAnimationClickListener = new OnAnimationClickListener()
	{
		@Override
		public void onAnimationClick(View v)
		{
			if(v == mFindBtn)
			{
				//loading
				if(mCurrentItem != null)
				{
					mDnImg.dnImg(mCurrentItem.imageUrl, Utils.getRealPixel2(200), new OnDnImgListener()
					{
						@Override
						public void onProgress(String url, int downloadedSize, int totalSize)
						{

						}

						@Override
						public void onFinish(String url, String file, Bitmap bmp)
						{
							mSite.m_myParams.put("data", mDatas);    //保存当前页数据，做现场还原
							mSite.m_myParams.put("index", currentIndex);
							mSite.m_myParams.put("loadindex", mIndex);
							mSite.m_myParams.put("lon", mLon);
							mSite.m_myParams.put("lat", mLat);
							mSite.m_myParams.put("location",mLocationName);


							HashMap<String, Object> params = new HashMap<>();
							JSONObject paramsJson = new JSONObject();
							try
							{
								JSONArray jsonArray = new JSONArray();
								JSONObject imgJs = new JSONObject();
								imgJs.put("image", file);
								imgJs.put("name", "arname");
								jsonArray.put(imgJs);
								paramsJson.put("images", jsonArray);
							}
							catch(JSONException e)
							{
								e.printStackTrace();
							}
							String paramsStr = writeJsonFile(paramsJson.toString());

							params.put("json_path", paramsStr);
							params.put("wishinfo", mCurrentItem);
							params.put("file", file);
							params.put("location",mLocationName);

                            params.put("pageType", 1);
							mSite.openARCamera(getContext(), params);
						}
					});
				}
			}else if(v == mBackBtn)
			{
				onBack();
			}
		}
	};

	private String  writeJsonFile(String json)
	{
		if(!new File(FolderMgr.getInstance().PATH_AR_SETTING).exists())
		{
			new File(FolderMgr.getInstance().PATH_AR_SETTING).mkdirs();
		}
		String file = FolderMgr.getInstance().PATH_AR_SETTING + "/targets.json";
		if(new File(file).exists())
		{
			new File(file).delete();
		}
		try
		{
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(json.toString().getBytes());
			fos.close();
		}
		catch(Exception e)
		{
		}
		return file;
	}

	/**
	 * data 附近的祝福页已加载的数据
	 * index 用户点击的item位置
	 * loadindex 附近的祝福页已加载到第几个数据
	 * lon, lat，经纬度
	 * @param params
	 */

	@Override
	public void SetData(HashMap<String, Object> params)
	{
		if(mSite.m_myParams.containsKey("data"))
		{
			params = mSite.m_myParams;
		}
		if(params != null)
		{
			mIndex = (int)params.get("loadindex");
			mLon = (double)params.get("lon");
			mLat = (double)params.get("lat");
			mLocationName = (String)params.get("location");
			mLocationTx.setText(mLocationName);
			ArrayList<WishItemInfo> infos = (ArrayList<WishItemInfo>)params.get("data");
			if(infos != null)
			{
				mDatas = infos;
				mAdapter.notifyDataSetChanged();
				if(infos.size() > 5)
				{
					mHasMore = true;
				}else{
					mHasMore = false;
				}
			}
			int position = (int)params.get("index");
			if(position != 0)
			{
				mViewPager.setCurrentItem(position, false);
			}
		}
		mSite.m_myParams.clear();
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
