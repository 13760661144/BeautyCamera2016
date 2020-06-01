package cn.poco.arWish;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.circle.ctrls.PullupRefreshListview;
import com.circle.utils.DialogUtils;
import com.circle.utils.Utils;
import com.taotie.circle.CommunityLayout;
import com.taotie.circle.LocationReader;

import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.advanced.ImageUtils;
import cn.poco.arWish.site.SearchNearSite;
import cn.poco.framework.BaseSite;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

/**
 * Created by wxf on 2017/1/18.
 */


public class SearchNearPage extends cn.poco.framework.IPage implements PoiSearch.OnPoiSearchListener {
    private ImageView mBackBtn;
    private TextView mOkTx;
    private RelativeLayout mSearchBar;
    Context mContext;
    LayoutInflater mInflate;
    PullupRefreshListview listView;
    ListAdapter aListAdapter;
    double mLastLat = 0;
    double mLastLon = 0;
    private PoiItem mChoosePoi;
    String mLastName = "";
    String mLastAddress = "";
    boolean hasLastInfo = false;
    boolean isFinish = false;
    private SearchNearSite searchNearSite;

    public SearchNearPage(Context context, BaseSite site) {
        super(context, site);
        mContext = context;
        searchNearSite = (SearchNearSite) site;
        initView();
    }

    public void setCurPositionInfo(double lat, double lon, String name, String address, boolean hasInfo) {
        hasLastInfo = hasInfo;
        if (!hasLastInfo) {
            return;
        }
        mLastLat = lat;
        mLastLon = lon;
        if (name != null) {
            mLastName = name;
        }
        if (address != null) {
            mLastAddress = address;
        }
    }


    private void initView() {
        Utils.hideInput(mContext);

        mInflate = LayoutInflater.from(mContext);

        LinearLayout contentLinear = new LinearLayout(getContext());
        contentLinear.setOrientation(LinearLayout.VERTICAL);
        addView(contentLinear, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(100));
        RelativeLayout topBar = new RelativeLayout(getContext());
        topBar.setBackgroundColor(0xfffafafa);
        contentLinear.addView(topBar, lParams);
        topBar.setId(Utils.generateViewId());

        RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        rParams.addRule(RelativeLayout.CENTER_VERTICAL);
        rParams.topMargin = ShareData.PxToDpi_xhdpi(5);
        rParams.leftMargin = ShareData.PxToDpi_xhdpi(2);
        mBackBtn = new ImageView(getContext());
        mBackBtn.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mBackBtn.setImageResource(R.drawable.framework_back_btn);
        topBar.addView(mBackBtn, rParams);
        ImageUtils.AddSkin(getContext(), mBackBtn);
        mBackBtn.setOnTouchListener(mOnAnimationClickListener);

        rParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        rParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        TextView mTitle = new TextView(getContext());
        mTitle.setTextColor(0xff333333);
        mTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        mTitle.setText("我在这里");
        topBar.addView(mTitle, rParams);


        rParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        rParams.addRule(RelativeLayout.CENTER_VERTICAL);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rParams.rightMargin = ShareData.PxToDpi_xhdpi(26);
        mOkTx = new TextView(getContext());
        mOkTx.setTextColor(0xff333333);
        mOkTx.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        mOkTx.setText("完成");
        mOkTx.setTextColor(ImageUtils.GetSkinColor());
        mOkTx.setOnTouchListener(mOnAnimationClickListener);
        topBar.addView(mOkTx, rParams);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xfff0f0f0);
        bg.setCornerRadius(ShareData.PxToDpi_xhdpi(23));

        lParams = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(698), ShareData.PxToDpi_xhdpi(56));
        lParams.gravity = Gravity.CENTER;
        lParams.topMargin = ShareData.PxToDpi_xhdpi(10);
        lParams.bottomMargin = ShareData.PxToDpi_xhdpi(10);
        mSearchBar = new RelativeLayout(getContext());
        mSearchBar.setOnTouchListener(mOnAnimationClickListener);
        mSearchBar.setBackground(bg);
        contentLinear.addView(mSearchBar, lParams);

        rParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        rParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        LinearLayout searchLinear = new LinearLayout(getContext());
        searchLinear.setGravity(Gravity.CENTER_VERTICAL);
        searchLinear.setOrientation(LinearLayout.HORIZONTAL);
        mSearchBar.addView(searchLinear, rParams);

        lParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        ImageView searchIcon = new ImageView(getContext());
        searchIcon.setImageResource(R.drawable.ar_location_search_icon);
        searchLinear.addView(searchIcon, lParams);

        lParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lParams.leftMargin = ShareData.PxToDpi_xhdpi(12);
        TextView mSearchTips = new TextView(getContext());
        mSearchTips.setTextColor(0xff9a9a9a);
        mSearchTips.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        mSearchTips.setText("搜索附近位置");
        mSearchTips.setTextColor(0xffE65A87);
        searchLinear.addView(mSearchTips, lParams);

        lParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        listView = new PullupRefreshListview(getContext());
        listView.setVerticalScrollBarEnabled(false);
        listView.setDividerHeight(0);
        listView.setBackgroundColor(Color.WHITE);
        contentLinear.addView(listView, lParams);

        aListAdapter = new ListAdapter();
        listView.setAdapter(aListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mLists.size() > 0 && position < mLists.size() && position > 0)//不选第一个，第一个为地级市
                {
                    if (hasLastInfo && position == 1 && !mCurCityCode.equals(mLastName)) {

                    } else {
                        String city = mLists.get(position).getCityName();
                        String ad = mLists.get(position).getAdName();
                        String point = mLists.get(position).getSnippet();
                        String addr;
                        if (point.equals(ad)) {
                            addr = city + point;
                        } else {
                            addr = city + (ad == null ? "" : ad) + point;
                        }
                        mLastName = mLists.get(position).getTitle();
                        mChoosePoi = mLists.get(position);
                        aListAdapter.notifyDataSetChanged();
//						searchNearSite.onChooseLocation(mLists.get(position).getLatLonPoint().getLatitude(), mLists.get(position).getLatLonPoint().getLongitude(), mLists.get(position).getTitle(), addr, 0);
                    }
                }
            }
        });

        DoLocation();
    }


    @Override
    public void SetData(HashMap<String, Object> params) {

    }

    @Override
    public void onBack() {
        searchNearSite.onBack();
    }

    private OnAnimationClickListener mOnAnimationClickListener = new OnAnimationClickListener() {
        @Override
        public void onAnimationClick(View v) {
            if (v == mBackBtn) {
                searchNearSite.onBack();
            } else if (v == mOkTx) {
                if (mChoosePoi != null) {
                    searchNearSite.onChooseLocation(mChoosePoi);
                }
            } else if (v == mSearchBar) {
                searchNearSite.goToKeySearch(mCurCityCode);
            }
        }
    };

    private void DoLocation() {
        if (CommunityLayout.curLat != -1 && CommunityLayout.curLon != -1 && !TextUtils.isEmpty(CommunityLayout.curCity)) {
            setCurLocationInfo(CommunityLayout.curLon, CommunityLayout.curLat, CommunityLayout.curCity);
        } else {
            new LocationReader().getLocation(mContext, new LocationReader.OnLocationListenerVerCity() {
                @Override
                public void onComplete(final double lon, final double lat, final String city, final int errorCode) {
                    if (errorCode == 0) {
                        setCurLocationInfo(lon, lat, city);
                    } else {
                        DialogUtils.showToast(mContext, "获取当前位置失败", Toast.LENGTH_SHORT, 0);
                        listView.setHasMore(false);
                    }
                }
            });
        }
    }

    private void setCurLocationInfo(double lon, double lat, String city) {
        mLon = lon;
        mLat = lat;
        mCurCityCode = city;
        CommunityLayout.curLat = mLat;
        CommunityLayout.curLon = mLon;
        CommunityLayout.curCity = mCurCityCode;
        if (!isLock) {
            isLock = true;
            searchPoi();
        }
        listView.setPullupRefreshListener(new PullupRefreshListview.PullupRefreshListener() {
            @Override
            public void onPullupRefresh() {
                if (!isLock && !isFinish) {
                    isLock = true;
                    searchPoi();
                    listView.isLoadingMore();
                }
            }
        });
    }

    PoiSearch.Query query;
    PoiSearch poiSearch;
    String searchNearType = "地名地址信息|餐饮服务|生活服务|商务住宅";
    String mCurCityCode = "";
    double mLat, mLon;
    int page = 1;//返回的页码是从第0页开始的
    int pagesize = 20;//最多30个
    boolean isLock = false;

    private void searchPoi() {
        query = new PoiSearch.Query("", searchNearType, mCurCityCode);//keyWord表示搜索字符串，
        query.setPageSize(pagesize);// 设置每页最多返回多少条poiitem
        query.setPageNum(page);//设置查询页码
        query.requireSubPois(true);
        poiSearch = new PoiSearch(mContext, query);
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(mLat, mLon), 5000));//设置周边搜索的中心点以及半径
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    ArrayList<PoiItem> mLists = new ArrayList<>();

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        if (isFinish) return;
        listView.refreshFinish();
        if (poiResult.getPois() != null && poiResult.getPois().size() > 0) {
            if (page == 1) {
                PoiItem poiItem = new PoiItem("190104", new LatLonPoint(mLat, mLon), mCurCityCode, "");//190104为地级市编码
                mLists.add(poiItem);
                if (hasLastInfo && !mCurCityCode.equals(mLastName)) {
                    PoiItem lastPoitem = new PoiItem("", new LatLonPoint(mLastLat, mLastLon), mLastName, mLastAddress);
                    mLists.add(lastPoitem);
                }
            }
            mLists.addAll(removeSamePoitem(poiResult.getPois()));
            if (TextUtils.isEmpty(mLastName) && mLists != null && mLists.size() > 1) {
                mLastName = mLists.get(1).getTitle();
                mChoosePoi = mLists.get(1);
            }
            page++;
            aListAdapter.notifyDataSetChanged();
        }
        isLock = false;
    }

    private ArrayList<PoiItem> removeSamePoitem(ArrayList<PoiItem> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getTitle().equals(mLastName)) {
                list.remove(i);
                break;
            }
        }
        return list;
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    private class ListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mLists.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null || !(convertView instanceof ListItem)) {
                convertView = new ListItem(getContext());
            }
            ((ListItem) convertView).setItemInfo(mLists.get(position));
            return convertView;
        }
    }

    private class ListItem extends RelativeLayout{

        private TextView mName;
        private TextView mAddress;
        private ImageView mCheckbox;

        public ListItem(Context context) {
            super(context);
            initUI();
        }

        private void initUI() {

            setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(110)));
            setBackground(getResources().getDrawable(R.drawable.location_items_layout_bg_selector));

            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            params.rightMargin = ShareData.PxToDpi_xhdpi(26);
            mCheckbox = new ImageView(getContext());
            mCheckbox.setId(Utils.generateViewId());
            mCheckbox.setImageResource(R.drawable.ar_location_choose_icon);
            mCheckbox.setVisibility(INVISIBLE);
            ImageUtils.AddSkin(getContext(), mCheckbox);
            addView(mCheckbox, params);

            params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            params.addRule(RelativeLayout.LEFT_OF,mCheckbox.getId());
            params.leftMargin = ShareData.PxToDpi_xhdpi(24);
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            addView(linearLayout, params);

            LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            mName = new TextView(getContext());
            mName.setTextColor(0xff333333);
            mName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            mName.setSingleLine();
            mName.setEllipsize(TextUtils.TruncateAt.END);
            linearLayout.addView(mName, lParams);

            lParams = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            lParams.topMargin = ShareData.PxToDpi_xhdpi(8);
            mAddress = new TextView(getContext());
            mAddress.setTextColor(0xff919191);
            mAddress.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
            mAddress.setSingleLine();
            mAddress.setEllipsize(TextUtils.TruncateAt.END);
            linearLayout.addView(mAddress, lParams);

            params = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            View line = new View(getContext());
            line.setBackgroundColor(0x15000000);
            addView(line, params);
        }

        public void setItemInfo(PoiItem info) {
            if (info == null) {
                return;
            }

            mName.setText(info.getTitle());
            if (mChoosePoi == info) {
                mCheckbox.setVisibility(VISIBLE);
            } else {
                mCheckbox.setVisibility(INVISIBLE);
            }
            if (info.getSnippet().length() > 0) {
                mAddress.setText(info.getSnippet());
                mAddress.setVisibility(VISIBLE);
            } else {
                mAddress.setVisibility(GONE);
            }
        }
    }


    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClose() {
        isFinish = true;
        if (poiSearch != null) {
            poiSearch.setOnPoiSearchListener(null);
            poiSearch = null;
        }
        if (listView != null) {
            listView.setAdapter(null);
        }
        aListAdapter = null;

        super.onClose();
    }
}
