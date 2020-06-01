package cn.poco.arWish;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.circle.common.friendbytag.PageCloseListener;
import com.circle.ctrls.PullupRefreshListview;
import com.circle.utils.Utils;
import com.taotie.circle.CommunityLayout;

import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.advanced.ImageUtils;
import cn.poco.arWish.site.SearchKeyWordSite;
import cn.poco.communitylib.R;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;

/**
 * Created by wxf on 2017/1/19.
 */

public class SearchKeyWordPage extends IPage implements GeocodeSearch.OnGeocodeSearchListener, TextWatcher, PoiSearch.OnPoiSearchListener
{
	Context mContext;
	LayoutInflater mInflate;
	RelativeLayout layout;
	private String city = "";
	private AutoCompleteTextView mKeywordText;
	private PullupRefreshListview PoiListview;
	private GeocodeSearch geocoderSearch;
	RelativeLayout searchbar;
	TextView btn_cancel;
	LinearLayout without_info;
	ImageView clearText;
	RelativeLayout listbgk;
	LinearLayout gaode_search_bar_bgk;
	boolean isDoAni = false;
	Handler mHandler = new Handler();
	private SearchKeyWordSite mSite;

	public SearchKeyWordPage(Context context,BaseSite site)
	{
		super(context,site);
		mSite = (SearchKeyWordSite) site;
		mContext = context;

		mInflate = LayoutInflater.from(mContext);
		layout = (RelativeLayout)mInflate.inflate(R.layout.search_keyword, null);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addView(layout, params);

		gaode_search_bar_bgk = (LinearLayout)findViewById(R.id.gaode_search_bar_bgk);
		RelativeLayout.LayoutParams rParams = (RelativeLayout.LayoutParams)gaode_search_bar_bgk.getLayoutParams();
		rParams.width = Utils.getRealPixel2(568);
		gaode_search_bar_bgk.setLayoutParams(rParams);
		mKeywordText = (AutoCompleteTextView)findViewById(R.id.input_edittext);
		Utils.modifyEditTextCursorWithThemeColor(mKeywordText);
		searchbar = (RelativeLayout)layout.findViewById(R.id.search_bar_layout);
		listbgk = (RelativeLayout)layout.findViewById(R.id.listbgk);
		clearText = (ImageView)layout.findViewById(R.id.clearText);
		clearText.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Utils.showInput(mKeywordText);
				mKeywordText.setText("");
			}
		});
		without_info = (LinearLayout)layout.findViewById(R.id.without_info);
		btn_cancel = (TextView)layout.findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(isfinsh)
				{
					doDownAni();
					isfinsh = false;
				}
			}
		});
		btn_cancel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
		btn_cancel.setTextColor(ImageUtils.GetSkinColor());
		listbgk.setBackgroundColor(0x99dddddd);
		doUpAni();
	}

	private void doUpAni()
	{
		isDoAni = true;
		TranslateAnimation animation = new TranslateAnimation(0, 0, getResources().getDimension(R.dimen.custom_titlebar_height), 0);
		animation.setDuration(300);
		animation.setFillAfter(false);
		searchbar.startAnimation(animation);
		listbgk.startAnimation(animation);

		animation.setAnimationListener(new Animation.AnimationListener()
		{

			@Override
			public void onAnimationStart(Animation animation)
			{
			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{
			}

			@Override
			public void onAnimationEnd(Animation animation)
			{
				initView();
				isDoAni = false;
				mHandler.postDelayed(new Runnable()
				{
					@Override
					public void run()
					{
						mKeywordText.requestFocus();
					}
				}, 500);
			}
		});


		TranslateAnimation txtani = new TranslateAnimation(Utils.getRealPixel2(215), 0, 0, 0);
		txtani.setDuration(300);
		txtani.setFillAfter(false);
		mKeywordText.startAnimation(txtani);
	}

	void doDownAni()
	{
		if(aniListener != null)
		{
			aniListener.close(null);
		}

		mSite.onBack();
		return;
	}


	public void setCity(String c)
	{
		city = c;
	}

	private void initView()
	{
		PoiListview = (PullupRefreshListview)findViewById(R.id.inputlist);

		mKeywordText.addTextChangedListener(this);
		Utils.showInput(mKeywordText);

		geocoderSearch = new GeocodeSearch(mContext);
		geocoderSearch.setOnGeocodeSearchListener(this);

		mAdapter = new ListAdapter();
		PoiListview.setAdapter(mAdapter);
		PoiListview.setHasMore(false);
		PoiListview.reMoveLoadText();
		PoiListview.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if(isfinsh)
				{
					doDownAni();
					isfinsh = false;
				}
				return false;
			}
		});
		PoiListview.setCustomOnScrollListener(new AbsListView.OnScrollListener()
		{

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState)
			{
				// TODO Auto-generated method stub
				Utils.hideInput(mContext);
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
			{
				// TODO Auto-generated method stub

			}
		});

		PoiListview.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				if(mList == null || mList.size() <= 0 || position >= mList.size())
				{
					return;
				}
				if(mSite != null)
				{
					if(mList.get(position).getLatLonPoint() != null)
					{
						String city = mList.get(position).getCityName();
						String ad = mList.get(position).getAdName();
						String point = mList.get(position).getSnippet();
						String addr = "";
						if(point.equals(ad))
						{
							addr = city + point;
						}
						else
						{
							addr = city + (ad == null ? "" : ad) + point;
						}
//						mSite.onChooseLocation(mList.get(position).getLatLonPoint().getLatitude(), mList.get(position).getLatLonPoint().getLongitude(), mList.get(position).getTitle(), addr, 0);
						mSite.onChooseLocation(mList.get(position));
					}
					else
					{
						GeocodeQuery query = new GeocodeQuery(mList.get(position).getSnippet() + mList.get(position).getTitle(), mList.get(position).getAdCode());// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
						geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
					}
				}
			}
		});
		mKeywordText.setOnEditorActionListener(new TextView.OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
			{
				if(actionId == EditorInfo.IME_ACTION_SEARCH)
				{
					Utils.hideInput((Activity)mContext);
					if(KeyWord.length() <= 0)
					{
						return true;
					}
					if(!isLock)
					{
						if(!LastKeyWord.equals(KeyWord))
						{
							LastKeyWord = KeyWord;
							mList.clear();
							page = 0;
							PoiListview.isLoadingMore();
							listbgk.setBackgroundColor(0xfff0f0f0);
							isLock = true;
							searchPoi(KeyWord);
						}

					}
					PoiListview.setPullupRefreshListener(new PullupRefreshListview.PullupRefreshListener()
					{
						@Override
						public void onPullupRefresh()
						{
							if(KeyWord.length() <= 0)
							{
								PoiListview.setHasMore(false);
								PoiListview.refreshFinish();
								return;
							}
							if(!isLock)
							{
								isLock = true;
								searchPoi(KeyWord);
							}

						}
					});
					return true;
				}
				return false;
			}
		});
	}

	ListAdapter mAdapter;
	PoiSearch.Query query;
	PoiSearch poiSearch;
	String searchNearType = "地名地址信息|餐饮服务|生活服务|商务住宅";
	int page = 0;//返回的页码是从第0页开始的
	int pagesize = 20;//最多30个
	boolean isLock = false;
	String KeyWord = "";
	String LastKeyWord = "";

	private void searchPoi(String keyword)
	{
		if(city.length() <= 0)
		{
			city = CommunityLayout.curCity;
		}
		query = new PoiSearch.Query(keyword, searchNearType, city);//keyWord表示搜索字符串，
		query.setPageSize(pagesize);// 设置每页最多返回多少条poiitem
		query.setPageNum(page);//设置查询页码
		query.requireSubPois(false);
		poiSearch = new PoiSearch(mContext, query);
//		poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(mLat, mLon), 5000));//设置周边搜索的中心点以及半径
		poiSearch.setOnPoiSearchListener(this);
		poiSearch.searchPOIAsyn();
	}

	@Override
	public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i)
	{

	}

	@Override
	public void onGeocodeSearched(GeocodeResult result, int rCode)
	{
		if(rCode == AMapException.CODE_AMAP_SUCCESS)
		{
			if(result != null && result.getGeocodeAddressList() != null && result.getGeocodeAddressList().size() > 0)
			{
				GeocodeAddress address = result.getGeocodeAddressList().get(0);

				double lat = address.getLatLonPoint().getLatitude();
				double lon = address.getLatLonPoint().getLongitude();
				String name = address.getBuilding();
				String addre = address.getFormatAddress();
				PoiItem poiItem = new PoiItem("",address.getLatLonPoint(),name,addre);
				mSite.onChooseLocation(poiItem);
//				mSite.onChooseLocation(lat, lon, name.length() > 0 ? name : addre, addre, 0);
			}
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after)
	{

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count)
	{
		KeyWord = s.toString().trim();
		if(mAdapter != null)
		{
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void afterTextChanged(Editable s)
	{

	}

	@Override
	public void onPoiSearched(PoiResult poiResult, int i)
	{
		PoiListview.refreshFinish();
		PoiListview.setLoadTexVISI();
		if(poiResult.getPois() != null && poiResult.getPois().size() > 0)
		{
			mList.addAll(poiResult.getPois());
			page++;
			mAdapter.notifyDataSetChanged();
			PoiListview.setHasMore(true);
			PoiListview.setBackgroundColor(0xfff0f0f0);
			without_info.setVisibility(INVISIBLE);
			PoiListview.setOnTouchListener(null);
		}
		else if(poiResult.getPois() == null || poiResult.getPois().size() <= 0)
		{
			if(mList == null || mList.size() <= 0)
			{
				without_info.setVisibility(VISIBLE);
			}
			PoiListview.setHasMore(false);
		}
		isLock = false;
	}

	@Override
	public void onPoiItemSearched(PoiItem poiItem, int i)
	{

	}

	ArrayList<PoiItem> mList;

	class ListAdapter extends BaseAdapter
	{
		ViewHolder holder;

		ListAdapter()
		{
			mList = new ArrayList<>();
		}

		@Override
		public int getCount()
		{
			return mList.size();
		}

		@Override
		public Object getItem(int position)
		{
			return null;
		}

		@Override
		public long getItemId(int position)
		{
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			if(convertView == null)
			{
				convertView = mInflate.inflate(R.layout.search_near_item_layout, null);
				holder = new ViewHolder();
				holder.name = (TextView)convertView.findViewById(R.id.poi_field_id);
				holder.address = (TextView)convertView.findViewById(R.id.poi_value_id);
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder)convertView.getTag();
			}
			String addr = mList.get(position).getCityName() + mList.get(position).getSnippet();
			if(mKeywordText.getText().toString().length() > 0)
			{
				holder.name.setText(Utils.setHighLightText(mList.get(position).getTitle(), mKeywordText.getText().toString(), 0xff96a8d0, true));
				if(addr.length() > 0)
				{
					holder.address.setText(Utils.setHighLightText(addr, mKeywordText.getText().toString(), 0xff96a8d0, true));
					holder.address.setVisibility(VISIBLE);
				}
				else
				{
					holder.address.setVisibility(GONE);
				}
			}
			else
			{
				holder.name.setText(mList.get(position).getTitle());
				if(addr.length() > 0)
				{
					holder.address.setText(addr);
					holder.address.setVisibility(VISIBLE);
				}
				else
				{
					holder.address.setVisibility(GONE);
				}
			}
			return convertView;
		}
	}

	class ViewHolder
	{
		TextView name;
		TextView address;
	}

	@Override
	public void onClose()
	{
		Utils.hideInput(CommunityLayout.sContext);
		mHandler.removeCallbacksAndMessages(null);
		super.onClose();
	}

	boolean isfinsh = true;

	@Override
	public void SetData(HashMap<String, Object> params) {
		if(params != null && params.containsKey("cityCode")){
			setCity((String) params.get("cityCode"));
		}
	}

	@Override
	public void onBack()
	{
		if(isfinsh && !isDoAni)
		{
			doDownAni();
			isfinsh = false;
		}
	}

	PageCloseListener aniListener;

	public void setPageCloseListener(PageCloseListener l)
	{
		aniListener = l;
	}
}
