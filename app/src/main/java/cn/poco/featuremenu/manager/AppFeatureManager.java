package cn.poco.featuremenu.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.adnonstop.admasterlibs.data.AbsAdRes;
import com.adnonstop.beautymall.commutils.BMCheckNewTopic;
import com.adnonstop.missionhall.utils.gz_Iutil.MissionHallEntryTip;
import com.adnonstop.resourcelibs.BaseResMgr;
import com.adnonstop.resourcelibs.CallbackHolder;
import com.adnonstop.resourcelibs.DataFilter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.poco.adMaster.ShareAdBanner;
import cn.poco.adMaster.SidebarAdBanner;
import cn.poco.adMaster.SidebarAdMaster;
import cn.poco.adMaster.SidebarText;
import cn.poco.adMaster.data.ClickAdRes;
import cn.poco.exception.MyApplication;
import cn.poco.featuremenu.model.AppFeature;
import cn.poco.featuremenu.model.FeatureType;
import cn.poco.featuremenu.model.MenuFeature;
import cn.poco.featuremenu.model.OtherFeature;
import cn.poco.featuremenu.util.MenuRule;
import cn.poco.framework.EventCenter;
import cn.poco.framework.EventID;
import cn.poco.holder.IMessageHolder;
import cn.poco.home.home4.MainPage;
import cn.poco.login.UserMgr;
import cn.poco.protocol.PocoProtocol;
import cn.poco.resource.DownloadMgr;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.system.AppInterface;
import cn.poco.system.ConfigIni;
import cn.poco.system.SysConfig;
import cn.poco.taskCenter.MissionHelper;
import cn.poco.tianutils.CommonUtils;
import my.beautyCamera.R;

/**
 * Created by Shine on 2017/9/7.
 */

public class AppFeatureManager extends BaseResMgr<AppFeature, ArrayList<AppFeature>> {
    public enum FeatureGroup {
        BeautyService,
        PersonalizedService,
        MoreSupport,
        Recommendation
    }

//    private HomePageFeatureCallBack mHomePageCallback;
//    private OtherFeatureCallBack mOtherFeatureCallback;
    private LeftMenuFeatureCallback mLeftMenuCallback;


    public interface IManager {

    }


    public interface LeftMenuFeatureCallback extends IManager{
        void onUpdateBeautyServiceFeature(List<MenuFeature> beautyServiceList);


        void onUpdateLeftMenuFeature(Map<FeatureGroup, List<MenuFeature>> menuGroup);


        void onUpdateAdInfo(List<ClickAdRes> clickAdRes, boolean isSidebarText);


    }
    public interface HomePageFeatureCallBack extends IManager{
        void onUpdateHomePageFeatureList(List<OtherFeature> featureList);

    }

    public interface OtherFeatureCallBack extends IManager{
        void onUpdateOtherFeatureList(List<OtherFeature> otherFeatureList);
    }




    private int mNewFlags = 0xFFFFFFFF;
    private int mTaskHallFlags = 0x0000001F;
    private int mWalletFlags = 0x0000002;
    private int mBeautyMallFlags = 0x00000004;
    private int mCloudAlbumFlags = 0x00000008;
    private int mPrintPhotoFlags = 0x00000010;
    private int mChatSystemFlags = 0x00000020;

    private static volatile AppFeatureManager sInstance = null;
    private Handler mMainthreadHandler;
    public static AppFeatureManager getInstance() {
        AppFeatureManager localInstance = sInstance;
        if (localInstance == null) {
            synchronized (AppFeatureManager.class) {
                localInstance = sInstance;
                if (localInstance == null) {
                    sInstance = localInstance = new AppFeatureManager();
                }
            }
        }
        return localInstance;
    }

    private AppFeatureManager() {
        mMainthreadHandler = new Handler(Looper.getMainLooper());
    }

    private Map<FeatureGroup, List<MenuFeature>> mMenuFeatureMap = new HashMap<>();
    // 美人相机功能的信息
    private List<AppFeature> mAppFeatureList = new ArrayList<>();
    // 美人相机左拉菜单功能的信息
    private List<MenuFeature> mBeautyServiceList = new ArrayList<>();

    private List<ClickAdRes> mSidebarTextList = new ArrayList<>();
    private List<ClickAdRes> mSidebarBanner = new ArrayList<>();


    private boolean mShowCredit;
    private void categoryFeature() {
        mBeautyServiceList.clear();
        mHomePageFeatureList.clear();
        mOtherFeatureList.clear();
        for (AppFeature feature : mAppFeatureList) {
            if (feature instanceof MenuFeature) {
                mBeautyServiceList.add((MenuFeature) feature);
            } else if (feature instanceof OtherFeature) {
                OtherFeature currntFeature = (OtherFeature) feature;
                if (feature.getFeature() == FeatureType.MYCREDIT) {
                    mShowCredit = feature.isUnlock().equals("yes") ? true : false;
                } else if (feature.getFeature() == FeatureType.HOMEPAGE_LEFT_TEXT || feature.getFeature() == FeatureType.HOMEPAGE_RIGHT_TEXT) {
                    mHomePageFeatureList.add(currntFeature);
                } else if (feature.getFeature() == FeatureType.WECHAT_LOGIN || feature.getFeature() == FeatureType.SINA_LOGIN || feature.getFeature() == FeatureType.REGISTER_LOGIN_TIPS) {
                    mOtherFeatureList.add(currntFeature);
                }
            }
        }
    }

    public boolean showCreditButton() {
        return mShowCredit;
    }


    private ArrayList<MenuFeature> mPersonalFeatureList = new ArrayList<>();
    private ArrayList<MenuFeature> mMoreSupportList = new ArrayList<>();

    // 用于标记是否应该显示推荐应用功能
    private boolean mShowAppRecomendadtion;

    private void getFixedMenuFeature(Context context) {
        mPersonalFeatureList.clear();
        mMoreSupportList.clear();

        // 个性管理部分
        MenuFeature customBeauty = new MenuFeature(FeatureType.CUSTOMBEAUTY);
        customBeauty.setTitle(context.getString(R.string.customBeauty));
        customBeauty.setFeatureIconResId(R.drawable.featuremenu_custombeauty);
        mPersonalFeatureList.add(customBeauty);

        MenuFeature changeSkin = new MenuFeature(FeatureType.CHANGETHEMESKIN);
        changeSkin.setTitle(context.getString(R.string.changeSkin));
        changeSkin.setFeatureIconResId(R.drawable.featuremenu_changeskin);
        mPersonalFeatureList.add(changeSkin);

        // item占位
        MenuFeature pendingFeature = new MenuFeature(FeatureType.NONE);
        pendingFeature.setIsPlaceHolder(true);
        mPersonalFeatureList.add(pendingFeature);
        mMenuFeatureMap.put(FeatureGroup.PersonalizedService, mPersonalFeatureList);

        // 更多支持
        MenuFeature generalSetting = new MenuFeature(FeatureType.GENERALSETTING);
        generalSetting.setTitle(context.getString(R.string.generalSetting));
        generalSetting.setFeatureIconResId(R.drawable.featuremenu_generalsetting);
        mMoreSupportList.add(generalSetting);

        MenuFeature shareWithFriends = new MenuFeature(FeatureType.SHAREWITHFRIENDS);
        shareWithFriends.setTitle(context.getString(R.string.shareWithFriends));
        shareWithFriends.setFeatureIconResId(R.drawable.featuremenu_sharewithfriends);
        mMoreSupportList.add(shareWithFriends);

        if (mShowAppRecomendadtion) {
            MenuFeature goodAppRecommendation = new MenuFeature(FeatureType.APPRECOMENDATION);
            goodAppRecommendation.setTitle(context.getString(R.string.appRecommendation));
            goodAppRecommendation.setFeatureIconResId(R.drawable.featuremenu_apprecommendation);
            mMoreSupportList.add(goodAppRecommendation);
        }

        MenuFeature rateUs = new MenuFeature(FeatureType.RATEUS);
        rateUs.setTitle(context.getString(R.string.rateUs));
        rateUs.setFeatureIconResId(R.drawable.featuremenu_rateus);
        mMoreSupportList.add(rateUs);

        MenuFeature helpAndFeedback = new MenuFeature(FeatureType.HELPANDFEEDBACK);
        helpAndFeedback.setTitle(context.getString(R.string.helpAndFeedback));
        helpAndFeedback.setFeatureIconResId(R.drawable.featuremenu_helpandfeedback);
        mMoreSupportList.add(helpAndFeedback);

        MenuFeature about = new MenuFeature(FeatureType.ABOUT);
        about.setTitle(context.getString(R.string.aboutUs));
        about.setFeatureIconResId(R.drawable.featuremenu_about);
        mMoreSupportList.add(about);

        //补占位item
        int len = (int)Math.ceil(mMoreSupportList.size() / 3f) * 3;
        for(int i = mMoreSupportList.size(); i<len; i++){
            pendingFeature = new MenuFeature(FeatureType.NONE);
            pendingFeature.setIsPlaceHolder(true);
            mMoreSupportList.add(pendingFeature);
        }
        mMenuFeatureMap.put(FeatureGroup.MoreSupport, mMoreSupportList);
    }


    public void getCurrentFeatureList (final Context context) {
        mAppFeatureList = this.sync_ar_GetCloudCacheRes(context, null, new CallbackHolder<>(new IMessageHolder.Callback<ArrayList<AppFeature>>() {
            @Override
            public void OnHandlerRun(final ArrayList<AppFeature> obj) {
                if (obj != null && obj.size() > 0) {
                    if (mMainthreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mAppFeatureList.clear();
                            mAppFeatureList.addAll(obj);
                            categoryFeature();
                            getFixedMenuFeature(context);
                            notifySubscriber();
                        }
                    }));
                }
            }
        }));
        categoryFeature();
        mMenuFeatureMap.put(FeatureGroup.BeautyService, mBeautyServiceList);
        getFixedMenuFeature(context);
    }


    private void notifySubscriber() {
        if (mLeftMenuCallback != null) {
            mLeftMenuCallback.onUpdateLeftMenuFeature(mMenuFeatureMap);
        }
        EventCenter.sendEvent(EventID.NOTIFY_HOMEPAGE_RIGHT_TIPS);
    }

    public Map<FeatureGroup, List<MenuFeature>> getLeftMenuFeature(LeftMenuFeatureCallback leftMenuFeatureCallback) {
        this.mLeftMenuCallback = leftMenuFeatureCallback;
        return mMenuFeatureMap;
    }

    private List<OtherFeature> mHomePageFeatureList = new ArrayList<>();
    public List<OtherFeature> getHomePageFeature() {
        return mHomePageFeatureList;
    }

    private List<OtherFeature> mOtherFeatureList = new ArrayList<>();
    public List<OtherFeature> getOtherFeature() {
        return mOtherFeatureList;
    }

    public void getAdInfo(Context context) {
        SidebarText sidebarText = new SidebarText(context);
        sidebarText.Run(mAdCallback);

        SidebarAdBanner sidebarAdBanner = new SidebarAdBanner(context);
        sidebarAdBanner.Run(mAdCallback);
    }


    private boolean mCommunityMessageGet, mSystemNotifyGet;

    private int mSystemMessageCount;
    public void setSystemMessage(int count) {
        this.mSystemMessageCount = count;
        mSystemNotifyGet = true;
        if (mChatFeature != null) {
            MenuFeature.BadgeTip redDotBadge;
            if (count > 0) {
                boolean result = MenuRule.showRedDotOrNumberTips(mSystemMessageCount, mChatUnreadMessage) == MenuRule.SHOW_RED_DOT_TIPS;
                if (result) {
                    redDotBadge = new MenuFeature.RedDotBadge();
                    mChatFeature.mShowBadge = true;
                    mChatFeature.setBadgeTip(redDotBadge);
                }
            } else {
                redDotBadge = mChatFeature.getBadgeTip();
                if (redDotBadge instanceof MenuFeature.RedDotBadge) {
                    mChatFeature.mShowBadge = false;
                }
            }

            if (mLeftMenuCallback != null) {
                mLeftMenuCallback.onUpdateBeautyServiceFeature(mBeautyServiceList);
            }

            if ((mNewFlags & mChatSystemFlags) != 0 && count > 0 && mChatFeature.isUnlock().equals("yes")) {
                MainPage.sIsLeftMenuHasNew = true;
                EventCenter.sendEvent(EventID.NOTIFY_HOMEPAGE_TIPS);
                mNewFlags = mNewFlags & ~mChatSystemFlags;
            }
        }

    }

    private int mUnreadCommunityMessageCount;
    public void setCommunityMessage(Context context, int count) {
        this.mUnreadCommunityMessageCount = count;
        mCommunityMessageGet = true;

        if (mCommunityMessageFeature != null) {
            MenuFeature.RedDotBadge numberTips = new MenuFeature.RedDotBadge();
            numberTips.badgeContent = String.valueOf(count);
            boolean isUserLogin = UserMgr.IsLogin(context, null);
            mCommunityMessageFeature.mShowBadge = count > 0 && isUserLogin? true : false;
            mCommunityMessageFeature.setBadgeTip(numberTips);

           if (mLeftMenuCallback != null) {
               mLeftMenuCallback.onUpdateBeautyServiceFeature(mBeautyServiceList);
           }

            if (count > 0 && (mCommunityMessageFeature.isUnlock().equals("yes"))) {
               if(sLastCommunityMessage != count)
               {
                   sLastCommunityMessage = count;
                   MainPage.sIsLeftMenuHasNew = true;
                   EventCenter.sendEvent(EventID.NOTIFY_HOMEPAGE_TIPS);
               }
            }
        }
    }
    private static int sLastCommunityMessage;


    private boolean mChatMessageGet;
    private int mChatUnreadMessage;
    public void setChatInfo(Context context, int count) {
        this.mChatUnreadMessage = count;
        mChatMessageGet = true;
        if (mChatFeature != null) {
            boolean isUserLogined = UserMgr.IsLogin(context, null);
            mChatFeature.mShowBadge = count > 0 && isUserLogined ? true : false;
            MenuFeature.NumberBadge numberBadge = new MenuFeature.NumberBadge();
            String content;
            if (mChatUnreadMessage > 99) {
                content = "99+";
            } else {
                content = String.valueOf(mChatUnreadMessage);
            }

            numberBadge.badgeContent = content;
            mChatFeature.setBadgeTip(numberBadge);
            if (mLeftMenuCallback != null) {
                mLeftMenuCallback.onUpdateBeautyServiceFeature(mBeautyServiceList);
            }

            if (count >= 0 && mChatFeature.isUnlock().equals("yes")) {
                EventCenter.sendEvent(EventID.NOTIFY_HOMEPAGE_CHAT, count);
            }
        }
    }

    public void clearAllUnreadChatInfo() {
        mChatUnreadMessage = 0;
    }

    private ShareAdBanner.Callback mAdCallback = new ShareAdBanner.Callback() {
        @Override
        public void ShowBanner(ArrayList<AbsAdRes> arr) {
            mSidebarTextList.clear();
            mSidebarBanner.clear();
            if (arr != null && arr.size() > 0) {
                boolean isSidebarText = arr.get(0).mPos.equals(SidebarAdMaster.POS_TEXT);
                List<ClickAdRes> tempList;
                for (AbsAdRes item : arr) {
                    if (item instanceof ClickAdRes) {
                        ClickAdRes clickAdRes = (ClickAdRes) item;
                        if (isSidebarText) {
                            mSidebarTextList.add(clickAdRes);
                        } else {
                            mSidebarBanner.add(clickAdRes);
                        }
                    }
                }
                tempList = isSidebarText ? mSidebarTextList : mSidebarBanner;

                if (mLeftMenuCallback != null) {
                    mLeftMenuCallback.onUpdateAdInfo(tempList, isSidebarText);
                }

            }
        }
    };




    @Override
    public int GetResArrSize(ArrayList<AppFeature> arr) {
       if (arr != null) {
           return arr.size();
       } else {
           return 0;
       }
    }

    @Override
    public ArrayList<AppFeature> MakeResArrObj() {
        return new ArrayList<>();
    }

    @Override
    public boolean ResArrAddItem(ArrayList<AppFeature> arr, AppFeature item) {
        if (arr != null && item != null) {
            return arr.add(item);
        }
        return false;
    }

    @Override
    protected void sync_raw_SaveSdcardRes(Context context, ArrayList<AppFeature> arr) {


    }

    @Override
    protected ArrayList<AppFeature> sync_raw_GetLocalRes(Context context, DataFilter filter) {
        return null;
    }


    @Override
    protected Object sync_raw_ReadSdcardData(Context context, DataFilter filter) {
        return null;
    }

    @Override
    protected ArrayList<AppFeature> sync_DecodeSdcardRes(Context context, DataFilter filter, Object data) {
        return null;
    }

    @Override
    protected Object sync_raw_ReadCloudData(Context context, DataFilter filter) {
        byte[] out = null;
        JSONObject jsonObject = new JSONObject();
        boolean isUserLogin = UserMgr.IsLogin(context, null);
        String userIdParams = null;
        String appChannel;
        if (isUserLogin) {
            userIdParams = SettingInfoMgr.GetSettingInfo(context).GetPoco2Id(false);
        }
        appChannel = ConfigIni.getMiniVer();
        try {
            if (!TextUtils.isEmpty(userIdParams)) {
                jsonObject.put("user_id", userIdParams);
            }
            if (!TextUtils.isEmpty(appChannel)) {
                jsonObject.put("channel", appChannel);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AppInterface delegate = AppInterface.GetInstance(context);
        out = PocoProtocol.Get(delegate.GetFeatureMenuData(), delegate.GetAppVer(), delegate.GetAppName(), false, delegate.GetMKey(), jsonObject, null);
        return out;
    }

    @Override
    protected Object sync_raw_ReadCloudCacheData(Context context, DataFilter filter) {
        byte[] out = null;
        try {
            out = CommonUtils.ReadFile(GetCloudCachePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

    @Override
    protected void sync_raw_WriteCloudData(Context context, DataFilter filter, Object data) {
        try
        {
            CommonUtils.SaveFile(GetCloudCachePath(), (byte[])data);
        }
        catch(Throwable e)
        {
            e.printStackTrace();
        }
    }

    private MenuFeature mTaskHallFeature, mBeautyMallFeature, mCommunityMessageFeature, mChatFeature;
    private boolean mTaskHallSwitchOn, mBeautyMallSwitchOn;

    @Override
    protected ArrayList<AppFeature> sync_DecodeCloudRes(Context context, DataFilter filter, Object data) {
        mTaskHallSwitchOn = false;
        mBeautyMallSwitchOn = false;
        ArrayList<AppFeature> result = new ArrayList<>();
        if (data instanceof byte[]) {
            try {
                JSONObject json = new JSONObject(new String((byte[])data));
                if (json.getInt("code") == 200) {
                    json = json.getJSONObject("data");
                    if (json.getInt("ret_code") == 0) {
                        json = json.getJSONObject("ret_data");
                        String sidebar = json.getString("sidebar");
                        JSONArray jsonArray = new JSONArray(sidebar);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject currentJsonObject = jsonArray.getJSONObject(i);
                            String unlock = currentJsonObject.getString("unlock");
                            String title = currentJsonObject.getString("title");
                            String describe = currentJsonObject.getString("describe");
                            String id = currentJsonObject.getString("id");
                            String tips = currentJsonObject.getString("tips");
                            String time = currentJsonObject.getString("time");

                            if ("photo".equals(id)) {
                                continue;
                            }

                            MenuFeature menuFeature = new MenuFeature(id);
                            menuFeature.setTitle(title);
                            menuFeature.setId(id);
                            menuFeature.setTime(time);
                            menuFeature.setUnlock(unlock);

                            if (unlock.equals("yes")) {
                                result.add(menuFeature);
                                if (menuFeature.getFeature() == FeatureType.TASKHALL) {
                                    mTaskHallSwitchOn = true;
                                    mTaskHallFeature = menuFeature;
                                    // 获取任务大厅消息之后
                                    if (mMissionHallInfoGet) {
                                        menuFeature.setDescribe(mMissionDescription);
                                        final boolean isUserLogin = UserMgr.IsLogin(context, null);
                                        someLogicForMissonHall(isUserLogin, mMissionDescription, mMissionBubbleTip, mIsAward);
                                    }
                                } else if (menuFeature.getFeature() == FeatureType.BEAUTYMALL) {
                                    mBeautyMallSwitchOn = true;
                                    if (mBeautyMallInfoGet) {
                                        MenuFeature.TextBadge beautyMallBadge = new MenuFeature.TextBadge();
                                        beautyMallBadge.badgeContent = mTipsTop;
                                        menuFeature.setBadgeTip(beautyMallBadge);
                                        menuFeature.mShowBadge = mIsBMDoorAppear;
                                        menuFeature.setDescribe(mTipsBottom);
                                    }
                                    mBeautyMallFeature = menuFeature;
                                } else if (menuFeature.getFeature() == FeatureType.WALLET) {
                                    // 钱包
                                } else {
                                    if (menuFeature.getFeature() == FeatureType.COMMENT) {
                                        // 显示社区消息数量
                                        if (mCommunityMessageGet && mUnreadCommunityMessageCount > 0) {
                                            MenuFeature.BadgeTip badgeTip = new MenuFeature.RedDotBadge();
                                            badgeTip.badgeContent = String.valueOf(mUnreadCommunityMessageCount);
                                            boolean isUserLogin = UserMgr.IsLogin(context, null);
                                            menuFeature.mShowBadge = isUserLogin;
                                            menuFeature.setBadgeTip(badgeTip);
                                        }
                                        mCommunityMessageFeature = menuFeature;
                                    } else if (menuFeature.getFeature() == FeatureType.CHAT) {
                                        boolean isUserLogined = UserMgr.IsLogin(context, null);
                                        if (mChatMessageGet) {
                                            if (!(mChatUnreadMessage > 0) && mSystemMessageCount > 0) {
                                                // 显示小红点
                                                MenuFeature.RedDotBadge badgeTip = new MenuFeature.RedDotBadge();
                                                menuFeature.mShowBadge = isUserLogined;
                                                menuFeature.setBadgeTip(badgeTip);
                                            } else if (mChatUnreadMessage > 0) {
                                                // 显示数量
                                                MenuFeature.NumberBadge numberBadge = new MenuFeature.NumberBadge();
                                                menuFeature.mShowBadge = isUserLogined;
                                                String content;
                                                if (mChatUnreadMessage > 99) {
                                                    content = "99+";
                                                } else {
                                                    content = String.valueOf(mChatUnreadMessage);
                                                }
                                                numberBadge.badgeContent = content;
                                                menuFeature.setBadgeTip(numberBadge);
                                            }
                                        } else if (mSystemNotifyGet){
                                            if (mChatUnreadMessage > 0) {
                                                // 显示数量
                                                MenuFeature.NumberBadge numberBadge = new MenuFeature.NumberBadge();
                                                menuFeature.mShowBadge = isUserLogined;
                                                numberBadge.badgeContent = String.valueOf(mChatUnreadMessage);
                                                menuFeature.setBadgeTip(numberBadge);
                                            } else if (mSystemMessageCount > 0){
                                                // 显示小红点
                                                MenuFeature.RedDotBadge badgeTip = new MenuFeature.RedDotBadge();
                                                menuFeature.mShowBadge = isUserLogined;
                                                menuFeature.setBadgeTip(badgeTip);
                                            }
                                        }
                                        mChatFeature = menuFeature;
                                    } else {
                                        boolean tipsExist = false;
                                        if (!TextUtils.isEmpty(tips)) {
                                            tipsExist = true;
                                            MenuFeature.TextBadge textBadge = new MenuFeature.TextBadge();
                                            textBadge.badgeContent = tips;
                                            menuFeature.setBadgeTip(textBadge);
                                            menuFeature.mShowBadge = true;
                                        }

                                        if (menuFeature.getFeature() == FeatureType.CLOUDALBUM) {
                                            if ((mNewFlags & mCloudAlbumFlags) != 0 && tipsExist) {
                                                MainPage.sIsLeftMenuHasNew = true;
                                                EventCenter.sendEvent(EventID.NOTIFY_HOMEPAGE_TIPS);
                                                mNewFlags = mNewFlags & ~mCloudAlbumFlags;
                                            }
                                        } else if (menuFeature.getFeature() == FeatureType.PHOTOPRINTED) {
                                            if ((mNewFlags & mPrintPhotoFlags) != 0 && tipsExist) {
                                                MainPage.sIsLeftMenuHasNew = true;
                                                EventCenter.sendEvent(EventID.NOTIFY_HOMEPAGE_TIPS);
                                                mNewFlags = mNewFlags & ~mPrintPhotoFlags;
                                            }
                                        }
                                    }
                                    menuFeature.setDescribe(describe);
                                }
                            }
                        }

                        int count = result.size();
                        int placeHolderCount = 0;
                        // 功能数量大于或者等于3个的时候
                        if (count >= 3) {
                            placeHolderCount = ((int)(Math.ceil(result.size() / 3.0f))) * 3 - result.size();
                        } else if (count > 0){
                            // 功能数量小于3个的时候
                            placeHolderCount = 3 - count;
                        }
                        for (int i = 0; i < placeHolderCount; i++) {
                            MenuFeature placeHolder = new MenuFeature(FeatureType.NONE);
                            placeHolder.setIsPlaceHolder(true);
                            result.add(placeHolder);
                        }

                        String other = json.getString("other");
                        jsonArray = new JSONArray(other);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject currentJsonObject = jsonArray.getJSONObject(i);
                            String unlock = currentJsonObject.getString("unlock");
                            String title = currentJsonObject.getString("title");
                            String describe = currentJsonObject.getString("describe");
                            String id = currentJsonObject.getString("id");
                            String tips = currentJsonObject.getString("tips");
                            String time = currentJsonObject.getString("time");
                            OtherFeature otherFeature = new OtherFeature(id);
                            otherFeature.setTitle(title);
                            otherFeature.setDescribe(describe);
                            otherFeature.setId(id);
                            otherFeature.setTime(time);
                            otherFeature.setUnlock(unlock);
                            otherFeature.setFeatureTips(tips);

                            if (unlock.equals("yes")) {
                                if (otherFeature.getFeature() != FeatureType.APPRECOMENDATION) {
                                    result.add(otherFeature);
                                } else {
                                    mShowAppRecomendadtion = true;
                                }
                            } else {
                                if (otherFeature.getFeature() == FeatureType.APPRECOMENDATION) {
                                    mShowAppRecomendadtion =  false;
                                }

                                if (otherFeature.getFeature() == FeatureType.MYCREDIT) {
                                    mShowCredit = false;
                                }
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    protected int GetCloudEventId() {
        return 0;
    }

    @Override
    protected int GetLocalEventId() {
        return 0;
    }

    @Override
    protected int GetSdcardEventId() {
        return 0;
    }


    private String GetCloudCachePath() {
        return DownloadMgr.getInstance().FEATURE_MENU_PATH + "/feature.xxxx";
    }


    private boolean mIsBMDoorAppear;
    private String mTipsTop, mTipsBottom;
    private boolean mBeautyMallInfoGet; // 用于标记是否获取了福利社的信息

    public void setBeautyMallRedDotInfo(boolean isBMDoorAppear, String tipsTop, String tipsBottom) {
        this.mIsBMDoorAppear = isBMDoorAppear;

        this.mTipsTop = tipsTop;
        this.mTipsBottom = tipsBottom;
        mBeautyMallInfoGet = true;

        if (mBeautyMallFeature != null) {
            MenuFeature.TextBadge beautyMallBadge = new MenuFeature.TextBadge();
            beautyMallBadge.badgeContent = mTipsTop;
            mBeautyMallFeature.setBadgeTip(beautyMallBadge);
            mBeautyMallFeature.mShowBadge = mIsBMDoorAppear;
            mBeautyMallFeature.setDescribe(mTipsBottom);
            // 刷新数据

            if (mLeftMenuCallback != null) {
                mLeftMenuCallback.onUpdateBeautyServiceFeature(mBeautyServiceList);
            }
        }
    }

    /**
     * 任务大厅开关是否打开
     * @return true为打开，false为关闭
     */
    public boolean isTaskHallFeatureOn() {
        return mTaskHallSwitchOn;
    }

    /**
     * 福利社开关是否打开
     * @return true为打开，false为关闭
     */
    public boolean isBeautyMallSwitchOn() {
        return mBeautyMallSwitchOn;
    }


    protected MissionHelper.BMCheckNewListener mBMCheckNewListener;
    private void refreshBeautyMall() {
        mBMCheckNewListener = new MissionHelper.BMCheckNewListener();
        mBMCheckNewListener.SetObj(this);
        BMCheckNewTopic.checkNewTopic(MyApplication.getInstance(), mBMCheckNewListener);
//        Toast.makeText(context, "调用福利社", Toast.LENGTH_SHORT).show();
    }

    public void visitHangZhouServer(Context context) {
        refreshBeautyMall();
        updateMissionHallInfo(context);
    }

    private boolean mMissionHallInfoGet;
    private String mMissionDescription;
    private String mMissionBubbleTip;
    private boolean mIsAward = false;

    private void updateMissionHallInfo(final Context context) {
        final boolean isUserLogin = UserMgr.IsLogin(context, null);
        String userId;
        if (isUserLogin) {
            userId = SettingInfoMgr.GetSettingInfo(context).GetPoco2Id(false);
        } else {
            userId = "";
        }

        MissionHallEntryTip.showTips(context, userId, "MISSIONHALL", "1", "beauty_camera", "ANDROID", SysConfig.GetAppVerNoSuffix(context), new MissionHallEntryTip.MissionEntryTipUpdateListener() {
            @Override
            public void updateData(String desc, String bubbleTip, boolean isAward) {
                mMissionHallInfoGet = true;
                mMissionDescription = desc;
                mMissionBubbleTip = bubbleTip;
                mIsAward = isAward;

                someLogicForMissonHall(isUserLogin, desc, bubbleTip, isAward);
                if (mTaskHallFeature != null) {
                    if (mLeftMenuCallback != null) {
                        mLeftMenuCallback.onUpdateBeautyServiceFeature(mBeautyServiceList);
                    }
                }
            }
        });
    }

    private void someLogicForMissonHall (boolean isLogined, String desc, String bubbleTip, boolean isAward){
        if (mTaskHallFeature != null) {
            MenuFeature.BadgeTip badgeTip;
            if (!TextUtils.isEmpty(desc)) {
                mTaskHallFeature.setDescribe(desc);
            }
            if (!TextUtils.isEmpty(bubbleTip)) {
                mTaskHallFeature.mShowBadge = true;
                if (isAward) {
                    // 登录才显示
                    badgeTip = new MenuFeature.RewardBadge();
                    if (!isLogined) {
                        mTaskHallFeature.mShowBadge = false;
                    }
                } else {
                    // 不用登录也可以显示
                    badgeTip = new MenuFeature.TaskBadge();
                }

                badgeTip.badgeContent = bubbleTip;
                mTaskHallFeature.setBadgeTip(badgeTip);

                if (mTaskHallFeature.mShowBadge) {
                    if ((mNewFlags & mTaskHallFlags) != 0) {
                        MainPage.sIsLeftMenuHasNew = true;
                        EventCenter.sendEvent(EventID.NOTIFY_HOMEPAGE_TIPS);
                        mNewFlags = mNewFlags & ~mTaskHallFlags;
                    }
                }
            } else {
                mTaskHallFeature.mShowBadge = false;
            }
        }
    }


    public void clear(IManager iManager) {
        if (iManager instanceof LeftMenuFeatureCallback) {
            mLeftMenuCallback = null;
        }
    }

}
