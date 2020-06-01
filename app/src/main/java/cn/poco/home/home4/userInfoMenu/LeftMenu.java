package cn.poco.home.home4.userInfoMenu;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.HashMap;

import cn.poco.advanced.ImageUtils;
import cn.poco.campaignCenter.utils.ClickEffectUtil;
import cn.poco.framework.IPage;
import cn.poco.home.home4.userInfoMenu.Cells.DividerCell;
import cn.poco.home.home4.userInfoMenu.Cells.EmptyCell;
import cn.poco.home.home4.userInfoMenu.Cells.MenuCellHorizontal;
import cn.poco.home.home4.userInfoMenu.Cells.UserInfoCell;
import cn.poco.login.UserMgr;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.ConfigIni;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by shine on 2016/10/13.
 */
public class LeftMenu extends IPage {

    private LinearLayout mParent;

    public enum MenuItem {
        AVATAR(0),
        EDITBTN(1),
        USERNAMEPOSITION(2),

        TASKHALL(3),
        CLOUDALBUM(4),
        PRINTINGPHOTOS(5),
        CHANGETHEMESKIN(6),
        SETTING(7),
        GOODAPPRECOMMENDATION(8),
        RATEUS(9),

        MYCREDIT(10),
        WALLET(11),

        BEAUTYMALL(12),
        ABOUTUS(13),
        SHAREWITHFRIENDS(14);
        private int mIndex;

        MenuItem (int index) {
            this.mIndex = index;
        }

        public int getIndex() {
            return mIndex;
        }
    }

    public interface MySpaceMenuDelegate {
        void onClickMenuItem(MenuItem item);
    }

    private UserInfoCell mUserInfoCell;

    private ListView mMenuListView;
    private MenuListAdapter mMenuListAdapter;
    private MySpaceMenuDelegate mDelegate;

    private int mRowCount = 0;
    private int mDividerCellRow1;
    private int mEmptyCellRow1;

    // 主题换肤
    private int mChangeThemeSkinningRow;
    private int mEmptyCellRowSupplement;

    private int mDividerCellRow2 = -1;
    private int mEmptyCellRow2 = -1;
    // 美人商城
    private int mBeautyMall = -1;
    // 任务大厅
    private int mTaskHallRow = -1;


    private int mEmptyCellRow3 = -1;
    private int mDividerCellRow3;
    private int mEmptyCellRow4;

    // 照片冲印
    private int mPrintPhotosRow;
    // 云相册
    private int mCloudAlbumRow;

    private int mEmptyCellRow5;
    private int mDividerCellRow4;
    private int mEmptyCellRow6;

    // 相机设置
    private int mSettingRow;

    // 分享好友
    private int mShareWithFriends;

    // App评分
    private int mRateUsRow;
    // 精品推荐
    private int mGoodAppRecommendationRow = -1;
    // 关于我们
    private int mAboutUs;

    private int mEmptyCellRow7;

    public static final String NOTIFY_USER_VALUE = "false";
    private boolean mShowRecommendation;

    private boolean mSkinNew;
    private boolean mTaskHallNew;
    private boolean mShowBeautyMallNew;

    private int mViewFlags = 0x00000000;

    // 显示积分
    public static int SHOW_CREDIT = 0x00000001;
    // 显示钱包
    public static int SHOW_WALLET = 0x00000002;
    // 显示福利社
    public static int SHOW_BEAUTY_MALL = 0x00000004;
    // 显示任务大厅
    public static int SHOW_TASK_MALL = 0x00000008;

    public LeftMenu(Context context) {
        super(context,null);
        initData();
        initView(context);
        restoreMenuListPosition();
    }

    private void initData() {
        String result = TagMgr.GetTagValue(getContext(), Tags.NOTIFY_CHANGE_APP_SKIN_FEATURE);
        if (TextUtils.isEmpty(result) || !result.equals(NOTIFY_USER_VALUE)) {
            mSkinNew = true;
        } else {
            mSkinNew = false;
        }
        mShowRecommendation = !ConfigIni.hideAppMarket;

        String showCredit = TagMgr.GetTagValue(getContext(), Tags.NET_TAG_HZ_CREDIT);
        if(showCredit == null || showCredit.equals("on")) {
            mViewFlags |= SHOW_CREDIT;
        }

        String showWallet = TagMgr.GetTagValue(getContext(), Tags.NET_TAG_HZ_WALLET);
        if (showWallet == null || showWallet.equals("on")) {
            mViewFlags |= SHOW_WALLET;
        }

        String showBeautyMall = TagMgr.GetTagValue(getContext(), Tags.NET_TAG_HZ_MALL);
        if (showBeautyMall == null || showBeautyMall.equals("on")) {
            mViewFlags |= SHOW_BEAUTY_MALL;
        }

        String showMissionHall = TagMgr.GetTagValue(getContext(), Tags.NET_TAG_HZ_MISSION);
        if (showMissionHall == null || showMissionHall.equals("on")) {
            mViewFlags |= SHOW_TASK_MALL;
        }
    }

    private void initView(Context context) {
        this.setBackgroundColor(Color.WHITE);
        mParent = new LinearLayout(getContext());
        LayoutParams fl = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mParent,fl);

        LinearLayout.LayoutParams params;
        mParent.setOrientation(LinearLayout.VERTICAL);
        String loginedAvatarUrl = "";
        String userNick = "";
        boolean isLogin = UserMgr.IsLogin(context,null);

        if (isLogin) {
            loginedAvatarUrl = SettingInfoMgr.GetSettingInfo(context).GetPoco2HeadUrl();
            userNick = SettingInfoMgr.GetSettingInfo(context).GetPocoNick();
        }
        mUserInfoCell = new UserInfoCell(context, isLogin, loginedAvatarUrl, userNick);
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(440));
        mUserInfoCell.setLayoutParams(params);
        mParent.addView(mUserInfoCell);

        if ((mViewFlags & SHOW_CREDIT) == 0) {
            mUserInfoCell.getCreditBtn().setVisibility(View.GONE);
        }

        if ((mViewFlags & SHOW_WALLET) == 0) {
            mUserInfoCell.getMyWalletBtn().setVisibility(View.GONE);
        }
        initListView();
    }

    private void initListView() {
        mDividerCellRow1 = mRowCount++;
        mEmptyCellRow1 = mRowCount++;

        mChangeThemeSkinningRow = mRowCount++;
        mEmptyCellRowSupplement = mRowCount++;

        boolean noneShow = false;

        if (!((mViewFlags & SHOW_BEAUTY_MALL) != 0 || (mViewFlags & SHOW_TASK_MALL) != 0)) {
            noneShow = true;
        }

        if (!noneShow) {
            mDividerCellRow2 = mRowCount++;
            mEmptyCellRow2 = mRowCount++;
        }

        if ((mViewFlags & SHOW_BEAUTY_MALL) != 0) {
            mBeautyMall = mRowCount++;
        }

        if ((mViewFlags & SHOW_TASK_MALL) != 0) {
            mTaskHallRow = mRowCount++;
        }

        if (!noneShow) {
            mEmptyCellRow3 = mRowCount++;
        }
        mDividerCellRow3 = mRowCount++;
        mEmptyCellRow4 = mRowCount++;

        mPrintPhotosRow = mRowCount++;
        mCloudAlbumRow = mRowCount++;

        mEmptyCellRow5 = mRowCount++;
        mDividerCellRow4 = mRowCount++;
        mEmptyCellRow6 = mRowCount++;

        mSettingRow = mRowCount++;
        mShareWithFriends = mRowCount++;
        mRateUsRow = mRowCount++;
        if (mShowRecommendation) {
            mGoodAppRecommendationRow = mRowCount++;
        }
        mAboutUs = mRowCount++;
        mEmptyCellRow7 = mRowCount++;

        mMenuListView = new ListView(this.getContext());
        mMenuListView.setDivider(null);
        mMenuListView.setDividerHeight(0);
        mMenuListView.setVerticalScrollBarEnabled(false);

        final LayoutParams params3 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mMenuListView.setLayoutParams(params3);
        mMenuListAdapter = new MenuListAdapter(this.getContext());
        mMenuListView.setAdapter(mMenuListAdapter);
        mParent.addView(mMenuListView);
        initListenerAndSkin();
    }


    private void initListenerAndSkin() {
        if (mUserInfoCell.getUserAvatar() != null) {
            mUserInfoCell.getUserAvatar().setOnClickListener(clickListener);
        }
        if (mUserInfoCell.getDefaultAvatar() != null) {
            mUserInfoCell.getDefaultAvatar().setOnClickListener(clickListener);
        }

        if (mUserInfoCell.getEditBtn() != null) {
            mUserInfoCell.getEditBtn().setOnClickListener(clickListener);
        }

        if (mUserInfoCell.getUserName() != null) {
            mUserInfoCell.getUserName().setOnClickListener(clickListener);
        }

        if (mUserInfoCell.getCreditBtn() != null) {
            mUserInfoCell.getCreditBtn().setOnClickListener(clickListener);
        }

        if (mUserInfoCell.getMyWalletBtn() != null) {
            mUserInfoCell.getMyWalletBtn().setOnClickListener(clickListener);
        }
        addSkin();
    }

    public void setDelegate(MySpaceMenuDelegate delegate) {
        this.mDelegate = delegate;
    }

    public void setTaskHallNewIconVisibility(boolean show) {
        mTaskHallNew = show;
        mMenuListAdapter.notifyDataSetChanged();
    }

    /**
     * 设置是否显示福利社的红点
     * @param show 显示为true, 不显示为false;
     */
    public void setBeautyMallNewIconVisibility(boolean show) {
        if(mShowBeautyMallNew != show) {
            mShowBeautyMallNew = show;
            mMenuListAdapter.notifyDataSetChanged();
        }
    }

    /**
     *  查询目前是否显示福利社的红点
     * @return 显示为true, 不显示为false;
     */
    public boolean isShowingBeautyMallNew() {
        return mShowBeautyMallNew;
    }

    private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mDelegate != null) {
                if (v == mUserInfoCell.getUserAvatar()) {
                    mDelegate.onClickMenuItem(MenuItem.AVATAR);
                } else if (v == mUserInfoCell.getDefaultAvatar()) {
                    mDelegate.onClickMenuItem(MenuItem.AVATAR);
                } else if (v == mUserInfoCell.getEditBtn()) {
                    mDelegate.onClickMenuItem(MenuItem.EDITBTN);
                } else if (v == mUserInfoCell.getUserName() && mUserInfoCell.getUserName().getTag().equals("empty")) {
                    mDelegate.onClickMenuItem(MenuItem.USERNAMEPOSITION);
                } else if (v == mUserInfoCell.getCreditBtn()) {
                    mDelegate.onClickMenuItem(MenuItem.MYCREDIT);
                } else if (v == mUserInfoCell.getMyWalletBtn()) {
                    mDelegate.onClickMenuItem(MenuItem.WALLET);
                }
            }
        }
    };

    private class MenuListAdapter extends BaseAdapter {
        private Context mContext;

        public MenuListAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getViewTypeCount() {
            return 3;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == mEmptyCellRow1 || position == mEmptyCellRow2 || position == mEmptyCellRow3 || position == mEmptyCellRow4 || position == mEmptyCellRow5 || position == mEmptyCellRow6 || position == mEmptyCellRow7 || position == mEmptyCellRowSupplement) {
                return 0;
            } else if (position == mDividerCellRow1 || position == mDividerCellRow2 || position == mDividerCellRow3 || position == mDividerCellRow4) {
                return 1;
            } else {
                return 2;
            }
        }

        @Override
        public boolean isEnabled(int position) {
            if (!(position == mDividerCellRow1 || position == mDividerCellRow2 || position == mDividerCellRow3 || position == mDividerCellRow4)
                    && !(position == mEmptyCellRow1 || position == mEmptyCellRow2 || position == mEmptyCellRow3 || position == mEmptyCellRow4 || position == mEmptyCellRow5 || position == mEmptyCellRow6 || position == mEmptyCellRow7 || position == mEmptyCellRowSupplement)) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            ListView.LayoutParams params = null;
            if (type == 0) {
                if (position == mRowCount - 1) {
                    convertView = new EmptyCell(mContext, ShareData.PxToDpi_xhdpi(40));
                } else {
                    convertView = new EmptyCell(mContext, ShareData.PxToDpi_xhdpi(10));
                }
                convertView.setClickable(false);
                params = new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            } else if (type == 1) {
                convertView = new DividerCell(mContext);
                params = new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(1));
                convertView.setClickable(false);
            } else if (type == 2) {
                convertView = new MenuCellHorizontal(mContext);
                ClickEffectUtil.addViewClickEffect(convertView, 0xffffffff, 0xfff0f0f0);
                MenuCellHorizontal cell = (MenuCellHorizontal) convertView;
                if (position == mBeautyMall) {
                    cell.setTextAndIcon(R.string.beautyMall, R.drawable.homes_menu_beautymall);
                    cell.setTag(MenuItem.BEAUTYMALL.mIndex);
                    if (mShowBeautyMallNew) {
                        cell.setRedDotIcon(R.drawable.homes_menu_reddot_icon);
                    } else {
                        cell.setRedDotIconVisibility(View.GONE);
                    }
                } else if (position == mTaskHallRow) {
                    cell.setTextAndIcon(R.string.taskHall, R.drawable.homes_menu_taskhall);
                    cell.setTag(MenuItem.TASKHALL.mIndex);
                    if (mTaskHallNew) {
                        cell.setBadgeIcon(R.drawable.homes_menu_new_icon);
                    } else {
                        cell.setBadgeIconVisibility(View.GONE);
                    }
                } else if (position == mCloudAlbumRow) {
                    cell.setTextAndIcon(R.string.cloudAlbum, R.drawable.homes_menu_cloudalbum);
                    cell.setTag(MenuItem.CLOUDALBUM.mIndex);
                } else if (position == mPrintPhotosRow) {
                    cell.setTextAndIcon(R.string.printingPhotos, R.drawable.homes_menu_printingphotos);
                    cell.setTag(MenuItem.PRINTINGPHOTOS.mIndex);
                } else if (position == mChangeThemeSkinningRow) {
                    cell.setTextAndIcon(R.string.changeSkin, R.drawable.homes_menu_changeskin);
                    if (mSkinNew) {
                        cell.setBadgeIcon(R.drawable.homes_menu_new_icon);
                    } else {
                        cell.setBadgeIconVisibility(View.GONE);
                    }
                    cell.setTag(MenuItem.CHANGETHEMESKIN.mIndex);
                } else if (position == mSettingRow) {
                    cell.setTextAndIcon(R.string.setting, R.drawable.homes_menu_setting);
                    cell.setTag(MenuItem.SETTING.mIndex);
                } else if (position == mAboutUs) {
                    cell.setTextAndIcon(R.string.aboutUs, R.drawable.homes_menu_aboutus);
                    cell.setTag(MenuItem.ABOUTUS.mIndex);
                } else if (position == mGoodAppRecommendationRow) {
                    cell.setTextAndIcon(R.string.appRecommendation, R.drawable.homes_menu_apprecommendation);
                    cell.setTag(MenuItem.GOODAPPRECOMMENDATION.mIndex);
                } else if (position == mRateUsRow) {
                    cell.setTextAndIcon(R.string.rateUs, R.drawable.homes_menu_rateus);
                    cell.setTag(MenuItem.RATEUS.mIndex);
                } else if (position == mShareWithFriends) {
                    cell.setTextAndIcon(R.string.shareWithFriends, R.drawable.homes_menu_sharewithfriends);
                    cell.setTag(MenuItem.SHAREWITHFRIENDS.mIndex);
                }
                cell.addSkin(mContext);
                params = new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(102));

                if (mDelegate != null) {
                    cell.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDelegate.onClickMenuItem(MenuItem.values()[(Integer) v.getTag()]);
                            saveMenuListPosition();
                        }
                    });
                }

            }
            convertView.setLayoutParams(params);
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public int getCount() {
            return mRowCount;
        }
    }

    private void addSkin() {
        if (mUserInfoCell.getEditBtn() != null) {
            ImageUtils.AddSkin(getContext(), mUserInfoCell.getEditBtn().mBackground);
        }

        if (mUserInfoCell.getCreditBtn() != null) {
            ImageUtils.AddSkin(getContext(), mUserInfoCell.getCreditBtn().mIconImage);
        }

        if (mUserInfoCell.getMyWalletBtn() != null) {
            ImageUtils.AddSkin(getContext(), mUserInfoCell.getMyWalletBtn().mIconImage);
        }
    }

    public void notifySkinChange() {
        addSkin();
        initData();
        mMenuListAdapter.notifyDataSetChanged();
    }

    public void clear() {
        mUserInfoCell.clear();
    }

    @Override
    public void SetData(HashMap<String, Object> params) {

    }

    @Override
    public void onStart() {
        super.onStart();
        TongJiUtils.onPageStart(getContext(), R.string.个人中心);
        MyBeautyStat.onPageStartByRes(R.string.个人中心_首页_主页面);
    }

    @Override
    public void onResume() {
        super.onResume();
        TongJiUtils.onPageResume(getContext(), R.string.个人中心);
    }

    @Override
    public void onPause() {
        super.onPause();
        TongJiUtils.onPagePause(getContext(), R.string.个人中心);
    }

    @Override
    public void onStop() {
        super.onStop();
        MyBeautyStat.onPageEndByRes(R.string.个人中心_首页_主页面);
        TongJiUtils.onPageEnd(getContext(), R.string.个人中心);
    }

    @Override
    public void onClose() {
        clear();
    }

    @Override
    public void onPageResult(int siteID, HashMap<String, Object> params) {
        super.onPageResult(siteID, params);
        restoreMenuListPosition();
    }

    @Override
    public void onBack() {

    }

    private static MenuListMemory mMenuMemory = new MenuListMemory();
    /**
     * 保存菜单滑动view的位置
     */
    private void saveMenuListPosition() {
        if (mMenuListView != null) {
            mMenuMemory.mFirstVisiblePosition = mMenuListView.getFirstVisiblePosition();
            View firstChild = mMenuListView.getChildAt(0);
            if (firstChild != null) {
                mMenuMemory.mFirstChildPosition = firstChild.getTop();
            }
        }
    }

    /**
     * 恢复菜单滑动view的位置
     */
    public void restoreMenuListPosition() {
        if (mMenuListView != null && mMenuMemory.mFirstVisiblePosition != -1) {
            int top = (mMenuMemory.mFirstChildPosition - mMenuListView.getPaddingTop());
            mMenuListView.setSelectionFromTop(mMenuMemory.mFirstVisiblePosition, top);
            mMenuMemory.mFirstVisiblePosition = -1;
            mMenuMemory.mFirstChildPosition = 0;
        }
    }

    // 用来描述记录功能菜单属性的object
    private static class MenuListMemory {
        private int mFirstVisiblePosition = -1;
        private int mFirstChildPosition;
    }


}
