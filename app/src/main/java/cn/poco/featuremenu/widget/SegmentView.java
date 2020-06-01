package cn.poco.featuremenu.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.poco.cloudalbumlibs.utils.NetWorkUtils;
import cn.poco.featuremenu.cell.FeatureCell;
import cn.poco.featuremenu.model.FeatureType;
import cn.poco.featuremenu.model.MenuFeature;
import cn.poco.login.UserMgr;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by Shine on 2017/9/6.
 */

public class SegmentView extends LinearLayout{
    public interface SegmentViewCallback {
        void onItemClick(View v, FeatureType feature);
    }

    private TextView mSegmentView;
    private GridView mGridView;
    private FeatureAdapter mFeatureAdapter;

    private String mSegmentTitle;
    private List<MenuFeature> mMenuFeatureList = new ArrayList<>();

    public SegmentView(Context context,String title) {
        super(context);
        this.setOrientation(VERTICAL);
        mSegmentTitle = title;
        initView();
    }

    private void initView() {
        mSegmentView = new TextView(getContext());
        mSegmentView.setText(mSegmentTitle);
        mSegmentView.setTextColor(0xff999999);
        mSegmentView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        mSegmentView.setPadding(ShareData.PxToDpi_xhdpi(28), 0, 0, 0);
        mSegmentView.setTypeface(mSegmentView.getTypeface(), Typeface.BOLD);
        mSegmentView.setGravity(Gravity.CENTER);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ShareData.PxToDpi_xhdpi(60));
        mSegmentView.setLayoutParams(params);
        this.addView(mSegmentView);

        mGridView = new GridView(getContext());
        mGridView.setWillNotDraw(false);
        mGridView.setVerticalScrollBarEnabled(false);
        mGridView.setPadding(0, ShareData.PxToDpi_xhdpi(1), 0, ShareData.PxToDpi_xhdpi(1));
        mGridView.setBackgroundColor(0xfff0f0f0);
        mGridView.setNumColumns(3);
        mGridView.setOverScrollMode(GridView.OVER_SCROLL_NEVER);

        mGridView.setHorizontalSpacing(ShareData.PxToDpi_xhdpi(1));
        mGridView.setVerticalSpacing(ShareData.PxToDpi_xhdpi(1));

        mFeatureAdapter = new FeatureAdapter(mMenuFeatureList);
        mGridView.setAdapter(mFeatureAdapter);
    }

    public void setInfo(List<MenuFeature> featureList) {
        int oldSize = mMenuFeatureList.size();
        this.mMenuFeatureList.clear();
        this.mMenuFeatureList.addAll(featureList);
        if (mGridView.getParent() == null) {
            int row = (int)(Math.ceil(mMenuFeatureList.size() / 3.0f));
            int height = row * ShareData.PxToDpi_xhdpi(200);
            LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            mGridView.setLayoutParams(params);
            this.addView(mGridView);
        } else {
            if (oldSize != mMenuFeatureList.size()) {
                int row = (int)(Math.ceil(mMenuFeatureList.size() / 3.0f));
                int height = row * ShareData.PxToDpi_xhdpi(200);
                mGridView.getLayoutParams().height = height;
                mGridView.requestLayout();
            }
        }
        mFeatureAdapter.notifyDataSetChanged();
    }

    private SegmentViewCallback mSegementCallback;
    public void setOnSegmentViewCallback(final SegmentViewCallback callback) {
        mSegementCallback = callback;
    }


    public void notifyItemChange() {
        mFeatureAdapter.notifyDataSetChanged();
    }

    private class FeatureAdapter extends BaseAdapter {
        private List<MenuFeature> mMenuList;

        public FeatureAdapter(List<MenuFeature> list) {
            this.mMenuList = list;
        }

        @Override
        public int getCount() {
            return mMenuList.size();
        }

        @Override
        public Object getItem(int position) {
            return mMenuList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final MenuFeature currentMenuFeature = mMenuList.get(position);
            if (convertView == null) {
                convertView = new FeatureCell(parent.getContext());
                GridView.LayoutParams params = new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(200));
                convertView.setLayoutParams(params);
            }

            final FeatureCell featureCell = (FeatureCell) convertView;
            featureCell.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentMenuFeature != null) {
                        MenuFeature.BadgeTip badgeTip = currentMenuFeature.getBadgeTip();
                        if (badgeTip != null) {
                            boolean isUserLogin = UserMgr.IsLogin(getContext(), null);
                            String userId;
                            if (isUserLogin) {
                                userId = SettingInfoMgr.GetSettingInfo(getContext()).GetPoco2Id(false);
                            } else {
                                userId = "";
                            }
                            badgeTip.clickAction(userId);
                            featureCell.hideBadgeView();
                        }
                        if ((currentMenuFeature.getFeature() == FeatureType.WALLET
                                || currentMenuFeature.getFeature() == FeatureType.CHAT
                                || currentMenuFeature.getFeature() == FeatureType.COMMENT)
                                && !NetWorkUtils.isNetworkConnected(getContext())) {
                            //网络断开连接时 不可用
                            Toast.makeText(getContext(), R.string.net_weak_tip, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    if (mSegementCallback != null) {
                        mSegementCallback.onItemClick(v, currentMenuFeature.getFeature());
                    }
                }
            });
            featureCell.setData(currentMenuFeature);
            return convertView;
        }
    }

}
