package cn.poco.featuremenu.page;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.sina.weibo.sdk.constant.WBConstants;
import com.tencent.mm.opensdk.modelbase.BaseResp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.poco.adMaster.data.ClickAdRes;
import cn.poco.blogcore.QzoneBlog2;
import cn.poco.campaignCenter.utils.ToastUtil;
import cn.poco.campaignCenter.widget.share.ShareActionSheet;
import cn.poco.campaignCenter.widget.share.ShareIconView;
import cn.poco.campaignCenter.widget.share.ShareInfoType;
import cn.poco.featuremenu.cell.ActivityCell;
import cn.poco.featuremenu.cell.AdCell;
import cn.poco.featuremenu.cell.LoadMoreStateCell;
import cn.poco.featuremenu.cell.NavigationCell;
import cn.poco.featuremenu.manager.AppFeatureManager;
import cn.poco.featuremenu.model.FeatureType;
import cn.poco.featuremenu.model.MenuFeature;
import cn.poco.featuremenu.site.FeatureMenuSite;
import cn.poco.featuremenu.widget.MenuTopBar;
import cn.poco.featuremenu.widget.SegmentView;
import cn.poco.framework.BaseSite;
import cn.poco.framework.EventCenter;
import cn.poco.framework.EventID;
import cn.poco.framework.FileCacheMgr;
import cn.poco.framework.IPage;
import cn.poco.home.home4.Home4Page;
import cn.poco.home.home4.IActivePage;
import cn.poco.share.ShareTools;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJiUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.FileUtil;
import my.beautyCamera.R;

import static my.beautyCamera.R.string.beautyService;
import static my.beautyCamera.R.string.moreSupport;

/**
 * Created by Shine on 2017/9/6.
 */

public class FeatureMenuPage extends IPage implements EventCenter.OnEventListener,IActivePage
{
    public interface FeatureMenuCallback {
        void onAvatarClick();
        void onBusinessActivityClick(String clickProtocol);
        void onFeatureClick(FeatureType feature);
        void onAdItemClick(int adPosition, String clickProtocol);
    }

    private static final String SHARE_FRIEND_LINK = "http://a.app.qq.com/o/simple.jsp?pkgname=my.beautyCamera";

    private MenuTopBar mTopBar;
    private ActivityCell mActivityCell;

    private ScrollView mScrollView;
    private LinearLayout mViewContainer;
    private SegmentView mBeautyServiceView, mPersonalizedServiceView, mMoreSupportView;
    private NavigationCell mNavigationCell;
    private AdCell mAdCell;

    // 分享控件
    private ShareActionSheet mShareActionSheet;
    private ShareTools mShareTools;

    private Context mContext;
    private FeatureMenuCallback mCallback;
    private FeatureMenuSite mMenuSite;


    public FeatureMenuPage(Context context, BaseSite site) {
        super(context, site);
        this.setBackgroundColor(Color.WHITE);
        mMenuSite = (FeatureMenuSite) site;
        mContext = context;
        initData();
        initTools();
        initView();
        addSkin();
    }


    private List<ShareIconView.ShareIconInfo> mShareInfoList = new ArrayList<>();
    final String [] names = new String [] {getResources().getString(R.string.friends_circle), getResources().getString(R.string.wechat_friends),getResources().getString(R.string.QQZoneAlias),
            getResources().getString(R.string.QQFriends), getResources().getString(R.string.sina_weibo), getResources().getString(R.string.Facebook), getResources().getString(R.string.Instagram), getResources().getString(R.string.Twitter)};

    final int [] resId = new int [] {R.drawable.sharewithfriends_friends_circle, R.drawable.sharewithfriends_wechat_friend, R.drawable.sharewithfriends_qq_zone,
            R.drawable.sharewithfriends_qq_friends, R.drawable.sharewithfriends_sina_weibo, R.drawable.sharewithfriends_facebook, R.drawable.sharewithfriends_instagram, R.drawable.sharewithfriends_twitter};
    private void initData() {
        initSharedAppInfo();
        saveSharedImageToSdCard();
    }

    private void initSharedAppInfo() {
        ShareIconView.ShareIconInfo wechatFriendCircle = new ShareIconView.ShareIconInfo();
        wechatFriendCircle.name = names[0];
        wechatFriendCircle.resId = resId[0];
        wechatFriendCircle.mShareType = ShareInfoType.FRIEND_CIRCLE;
        mShareInfoList.add(wechatFriendCircle);

        ShareIconView.ShareIconInfo wechatFriends = new ShareIconView.ShareIconInfo();
        wechatFriends.name = names[1];
        wechatFriends.resId = resId[1];
        wechatFriends.mShareType = ShareInfoType.WECHAT_FRIENDS;
        mShareInfoList.add(wechatFriends);

        ShareIconView.ShareIconInfo qqZone = new ShareIconView.ShareIconInfo();
        qqZone.name = names[2];
        qqZone.resId = resId[2];
        qqZone.mShareType = ShareInfoType.Q_ZONE;
        mShareInfoList.add(qqZone);

        ShareIconView.ShareIconInfo qq = new ShareIconView.ShareIconInfo();
        qq.name = names[3];
        qq.resId = resId[3];
        qq.mShareType = ShareInfoType.QQ;
        mShareInfoList.add(qq);

        ShareIconView.ShareIconInfo sinaWeibo = new ShareIconView.ShareIconInfo();
        sinaWeibo.name = names[4];
        sinaWeibo.resId = resId[4];
        sinaWeibo.mShareType = ShareInfoType.SINA_WEIBO;
        mShareInfoList.add(sinaWeibo);

        ShareIconView.ShareIconInfo facebook = new ShareIconView.ShareIconInfo();
        facebook.name = names[5];
        facebook.resId = resId[5];
        facebook.mShareType = ShareInfoType.FACE_BOOK;
        mShareInfoList.add(facebook);

        ShareIconView.ShareIconInfo instgram = new ShareIconView.ShareIconInfo();
        instgram.name = names[6];
        instgram.resId = resId[6];
        instgram.mShareType = ShareInfoType.INSTAGRAM;
        mShareInfoList.add(instgram);

        ShareIconView.ShareIconInfo twitter = new ShareIconView.ShareIconInfo();
        twitter.name = names[7];
        twitter.resId = resId[7];
        twitter.mShareType = ShareInfoType.TWITTER;
        mShareInfoList.add(twitter);
    }


    private void initTools() {
        EventCenter.addListener(this);
        mShareTools = new ShareTools(mContext);
        mShareTools.needAddIntegral(false);
    }


    private void initView() {
        mScrollView = new ScrollView(mContext);
        mScrollView.setVerticalScrollBarEnabled(false);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT);
        params.topMargin = ShareData.PxToDpi_xxhdpi(120);
        mScrollView.setLayoutParams(params);
        this.addView(mScrollView);

        mViewContainer = new LinearLayout(mContext);
        mViewContainer.setOrientation(LinearLayout.VERTICAL);
        LayoutParams paramsContainer = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mViewContainer.setLayoutParams(paramsContainer);
        mScrollView.addView(mViewContainer);

        // 顶部bar
        mTopBar = new MenuTopBar(getContext(), false);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(166));
        mTopBar.setLayoutParams(params1);
        mViewContainer.addView(mTopBar);
        mTopBar.setMenuTopBarCallback(new MenuTopBar.MenuTopBarCallback() {
            @Override
            public void onAvatarClick() {
                if (mCallback != null) {
                    mCallback.onAvatarClick();
                }
            }

            @Override
            public void onCreditBtnClick() {
                if (mCallback != null) {
                    mCallback.onFeatureClick(FeatureType.MYCREDIT);
                }
            }
        });

        mActivityCell = new ActivityCell(mContext);
        params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(80));
        mActivityCell.setLayoutParams(params1);
        mActivityCell.setOnClickListener(mPageOnClickListener);
        mViewContainer.addView(mActivityCell);
        mActivityCell.setOnCancelClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCell.sUserCancel = true;
                mActivityCell.setVisibility(View.GONE);
                mActivityCell.requestLayout();
            }
        });
        mActivityCell.setVisibility(View.GONE);

        // 美人服务
        mBeautyServiceView = new SegmentView(mContext, mContext.getString(beautyService));
        mBeautyServiceView.setOnSegmentViewCallback(mSegmentViewCallback);
        params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mBeautyServiceView.setLayoutParams(params1);
        mViewContainer.addView(mBeautyServiceView);

        // 个性&管理
        mPersonalizedServiceView = new SegmentView(mContext,mContext.getString(R.string.personalityManagement));
        mPersonalizedServiceView.setOnSegmentViewCallback(mSegmentViewCallback);
        params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPersonalizedServiceView.setLayoutParams(params1);
        mViewContainer.addView(mPersonalizedServiceView);

        // 更多支持
        mMoreSupportView = new SegmentView(mContext, mContext.getString(moreSupport));
        mMoreSupportView.setOnSegmentViewCallback(mSegmentViewCallback);
        params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mMoreSupportView.setLayoutParams(params1);
        mViewContainer.addView(mMoreSupportView);

        mAdCell = new AdCell(mContext);
        mAdCell.setTitle(mContext.getString(R.string.recommendationForYou));
        params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mAdCell.setLayoutParams(params1);
        mAdCell.setAdCellCallback(new AdCell.AdCellCallback() {
            @Override
            public void onClickAdItem(int adPosition, String adUrl) {
                if (mCallback != null) {
                    mCallback.onAdItemClick(adPosition, adUrl);
                }
            }
        });
        mViewContainer.addView(mAdCell);

        LoadMoreStateCell loadMoreStateCell = new LoadMoreStateCell(mContext);
//        loadMoreStateCell.setText(mContext.getString(R.string.feature_menu_noMoreData));
        params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(30));
        loadMoreStateCell.setLayoutParams(params1);
        mViewContainer.addView(loadMoreStateCell);

        Map<AppFeatureManager.FeatureGroup, List<MenuFeature>> featureMap = AppFeatureManager.getInstance().getLeftMenuFeature(mFeatureManagerCallback);
        AppFeatureManager.getInstance().getAdInfo(mContext);

        if (featureMap.containsKey(AppFeatureManager.FeatureGroup.BeautyService)) {
            List<MenuFeature> beautyServiceList = featureMap.get(AppFeatureManager.FeatureGroup.BeautyService);
            if (beautyServiceList.size() > 0) {
                mBeautyServiceView.setInfo(beautyServiceList);
            } else {
                mBeautyServiceView.setVisibility(View.GONE);
            }
        }

        if (featureMap.containsKey(AppFeatureManager.FeatureGroup.PersonalizedService)) {
            List<MenuFeature> personalizedServiceList = featureMap.get(AppFeatureManager.FeatureGroup.PersonalizedService);
            if (personalizedServiceList.size() > 0) {
                mPersonalizedServiceView.setInfo(personalizedServiceList);
            }
        }

        if (featureMap.containsKey(AppFeatureManager.FeatureGroup.MoreSupport)) {
            List<MenuFeature> moreSupportList = featureMap.get(AppFeatureManager.FeatureGroup.MoreSupport);
            if (moreSupportList.size() > 0) {
                mMoreSupportView.setInfo(moreSupportList);
            }
        }

        mNavigationCell = new NavigationCell(mContext);
        FrameLayout.LayoutParams navigationParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mNavigationCell.setLayoutParams(navigationParams);
        mNavigationCell.setNavigationClickListener(mPageOnClickListener);
        this.addView(mNavigationCell, 0);

        mShareActionSheet = new ShareActionSheet(mContext, R.style.waitDialog, false);

        ShareIconView shareIconView = new ShareIconView.ShareIconViewBuilder(mContext, 5).shareInfoList(mShareInfoList).itemOnClickListener(mShareClickListener).create();
        mShareActionSheet.setShareIconView(shareIconView);
    }

    @Override
    public void SetData(HashMap<String, Object> params) {

    }

    public void notifySkinChange() {
        addSkin();
        mNavigationCell.changeSkin();
        mTopBar.changeSkin();
        mBeautyServiceView.notifyItemChange();
        mPersonalizedServiceView.notifyItemChange();
        mMoreSupportView.notifyItemChange();
    }

    public void addSkin() {
        mActivityCell.addSkin();
    }

    public void setMenuPageCallback(FeatureMenuCallback callback) {
        this.mCallback = callback;
    }


    @Override
    public void onBack() {
        mMenuSite.OnBack(getContext());
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
        CloseWaitDlg();
        visitHangZhouServer();
        TongJiUtils.onPageResume(getContext(), R.string.个人中心);
    }

    @Override
    public void onPageResult(int siteID, HashMap<String, Object> params) {
        super.onPageResult(siteID, params);
        ensureScrollbarPosition();
        visitHangZhouServer();
    }


    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        mShareTools.onActivityResult(requestCode, resultCode, data);
        return super.onActivityResult(requestCode, resultCode, data);
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
        mSegmentViewCallback = null;
        mShareClickListener = null;
        mShareCallback = null;
        mPageOnClickListener = null;
        mTopBar.clear();
        mActivityCell.clear();
        EventCenter.removeListener(this);
        AppFeatureManager.getInstance().clear(mFeatureManagerCallback);
        mFeatureManagerCallback = null;
    }


    public void onPageActive(int lastActiveMode)
    {
        TongJiUtils.onPageStart(getContext(), R.string.个人中心);
        MyBeautyStat.onPageStartByRes(R.string.个人中心_首页_主页面);
        mActivityCell.resumeSpeakerAnimation();
        if (lastActiveMode == Home4Page.HOME) {
            // 每次从主页拉出个人中心，都调用一次杭州任务大厅的接口
            // 每次从主页拉出个人中心，都调用一次杭州福利社的接口
            // 每次从主页拉出个人中心, 都刷新一下积分
            visitHangZhouServer();
        }
    }

    @Override
    public void onPageInActive(int nextActiveMode)
    {
        ensureScrollbarPosition();
        MyBeautyStat.onPageEndByRes(R.string.个人中心_首页_主页面);
        TongJiUtils.onPageEnd(getContext(), R.string.个人中心);
        mActivityCell.clear();
    }

    @Override
    public void setUiEnable(boolean uiEnable)
    {

    }


    @Override
    public void onEvent(int eventId, Object[] params) {
        if (eventId == EventID.HOMEPAGE_UPDATE_MENU_AVATAR) {
            mTopBar.updateUiDependOnState();
            visitHangZhouServer();
            AppFeatureManager.getInstance().Invalidate();
            AppFeatureManager.getInstance().getCurrentFeatureList(mContext);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mShareActionSheet != null && mShareActionSheet.isShowing()) {
            mShareActionSheet.dismiss();
        }
        return super.onTouchEvent(event);
    }

    public void visitHangZhouServer() {
        AppFeatureManager.getInstance().visitHangZhouServer(mContext);
        if (mTopBar != null) {
            mTopBar.refreshCredit();
        }
    }


    private SegmentView.SegmentViewCallback mSegmentViewCallback = new SegmentView.SegmentViewCallback() {
        @Override
        public void onItemClick(View v, FeatureType feature) {
            if (feature == FeatureType.CHAT) {
                AppFeatureManager.getInstance().clearAllUnreadChatInfo();
            } else if (feature == FeatureType.SHAREWITHFRIENDS)  {
                mShareActionSheet.show();
            }

            if (mCallback != null && feature != null) {
                mCallback.onFeatureClick(feature);
            }
        }
    };

    private String mActivityUrl;
    private AppFeatureManager.LeftMenuFeatureCallback mFeatureManagerCallback = new AppFeatureManager.LeftMenuFeatureCallback() {

        @Override
        public void onUpdateBeautyServiceFeature(final List<MenuFeature> beautyServiceList) {
            if (beautyServiceList != null && beautyServiceList.size() > 0) {
                if (mBeautyServiceView.getVisibility() != View.VISIBLE) {
                    mBeautyServiceView.setVisibility(View.VISIBLE);
                }
                mBeautyServiceView.setInfo(beautyServiceList);
                mBeautyServiceView.notifyItemChange();
            }
        }

        @Override
        public void onUpdateLeftMenuFeature(Map<AppFeatureManager.FeatureGroup, List<MenuFeature>> menuGroup) {
            boolean show = AppFeatureManager.getInstance().showCreditButton();
            mTopBar.setCreditButtonVisibility(show);
            if (menuGroup.containsKey(AppFeatureManager.FeatureGroup.BeautyService)) {
                List<MenuFeature> beautyServiceList = menuGroup.get(AppFeatureManager.FeatureGroup.BeautyService);
                List<MenuFeature> personalizedServiceList = menuGroup.get(AppFeatureManager.FeatureGroup.PersonalizedService);
                List<MenuFeature> moreSupportList = menuGroup.get(AppFeatureManager.FeatureGroup.MoreSupport);

                if (beautyServiceList != null && beautyServiceList.size() > 0) {
                    if (mBeautyServiceView.getVisibility() != View.VISIBLE) {
                        mBeautyServiceView.setVisibility(View.VISIBLE);
                    }
                    mBeautyServiceView.setInfo(beautyServiceList);
                    mBeautyServiceView.notifyItemChange();
                } else {
                    mBeautyServiceView.setVisibility(View.GONE);
                }

                if (personalizedServiceList != null && personalizedServiceList.size() > 0) {
                    if (mPersonalizedServiceView.getVisibility() != View.VISIBLE) {
                        mPersonalizedServiceView.setVisibility(View.VISIBLE);
                    }
                    mPersonalizedServiceView.setInfo(personalizedServiceList);
                    mPersonalizedServiceView.notifyItemChange();
                } else {
                    mPersonalizedServiceView.setVisibility(View.GONE);
                }

                if (moreSupportList != null && moreSupportList.size() > 0) {
                    if (mMoreSupportView.getVisibility() != View.VISIBLE) {
                        mMoreSupportView.setVisibility(View.VISIBLE);
                    }
                    mMoreSupportView.setInfo(moreSupportList);
                    mMoreSupportView.notifyItemChange();
                } else {
                    mMoreSupportView.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void onUpdateAdInfo(List<ClickAdRes> clickAdRes, boolean isSidebarText) {
            if (clickAdRes != null) {
                List<AdCell.AdItem> adItemList = new ArrayList<>();
                for (ClickAdRes resItem : clickAdRes) {
                    if (isSidebarText) {
                        mActivityCell.setBusinessActivityTitle(resItem.mTitle);
                        mActivityUrl = resItem.mClick;
                    } else {
                        AdCell.AdItem item = new AdCell.AdItem();
                        if (resItem.url_adm != null && resItem.url_adm.length > 0) {
                            item.mCoverImage = resItem.url_adm[0];
                            item.mAdUrl = resItem.mClick;
                            item.mClickTjs = resItem.mClickTjs;
                            item.mTjId = resItem.m_tjId;
                            adItemList.add(item);
                        }
                    }
                }
                if (!isSidebarText) {
                    AdCell.AdItem[] urlArray = new AdCell.AdItem[adItemList.size()];
                    mAdCell.setAdInfo(adItemList.toArray(urlArray));
                } else{
                    if (mActivityCell.getVisibility() != VISIBLE && !ActivityCell.sUserCancel) {
                        mActivityCell.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    };

    private View.OnClickListener mPageOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == mActivityCell) {
                if (mCallback != null) {
                    mCallback.onBusinessActivityClick(mActivityUrl);
                }
            } else if (v == mNavigationCell.mNavigationBackView) {
                mMenuSite.OnBack(mContext);
            }
        }
    };

    private OnClickListener mShareClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            ShowWaitDlg();
            Object tagObject = v.getTag();
            if (tagObject instanceof Integer) {
                int tag = (Integer) tagObject;
                String shareTitle = getResources().getString(R.string.share_with_friends_title);
                switch (tag) {
                    case ShareInfoType.FRIEND_CIRCLE :
                        mShareTools.sendUrlToWeiXin(sImgPathToDomesticApp, SHARE_FRIEND_LINK, shareTitle, null, false, mShareCallback);
                        break;
                    case ShareInfoType.WECHAT_FRIENDS :
                        mShareTools.sendUrlToWeiXin(sImgPathToDomesticApp, SHARE_FRIEND_LINK, shareTitle, null, true, mShareCallback);
                        break;
                    case ShareInfoType.Q_ZONE :
                        mShareTools.sendUrlToQzone(sImgPathToDomesticApp, shareTitle, null, SHARE_FRIEND_LINK, mShareCallback);
                        break;
                    case ShareInfoType.QQ :
                        mShareTools.sendUrlToQQ(shareTitle, null, sImgPathToDomesticApp, SHARE_FRIEND_LINK, mShareCallback);
                        break;
                    case ShareInfoType.SINA_WEIBO : {
                        // 因为希望新浪微博分享出去的图片想要清晰的大图，所以这里也用了这张图片
                        mShareTools.sendToSinaBySDK(shareTitle + " " + SHARE_FRIEND_LINK, sImgPathToForeignSocialMedia, mShareCallback);
                        break;
                    }

                    case ShareInfoType.FACE_BOOK : {
                        Bitmap bitmap = BitmapFactory.decodeFile(sImgPathToInstagramApp);
                        if (bitmap != null) {
                            mShareTools.sendToFacebook(bitmap, mShareCallback);
                        }
                        break;
                    }

                    case ShareInfoType.TWITTER : {
                        mShareTools.sendToTwitter(sImgPathToForeignSocialMedia, shareTitle + " " + SHARE_FRIEND_LINK);
                        CloseWaitDlg();
                        break;
                    }

                    case ShareInfoType.INSTAGRAM : {
                        mShareTools.sendToInstagram(sImgPathToInstagramApp);
                        CloseWaitDlg();
                        break;
                    }

                    default: {

                    }
                }
            }
        }
    };

    private ShareTools.SendCompletedListener mShareCallback = new ShareTools.SendCompletedListener() {
        @Override
        public void getResult(Object result) {
            CloseWaitDlg();
            int shareResult = (Integer)result;
            switch (shareResult) {
                // 发送成功
                // 微信朋友圈，微信好友, 新浪微博,Facebook
                case BaseResp.ErrCode.ERR_OK :
                    // QQ空间, QQ好友
                case QzoneBlog2.SEND_SUCCESS : {
                    ToastUtil.showToast(getContext(), getResources().getString(R.string.share_successfully));
                    break;
                }

                // 用户取消发送
                // 微信朋友圈，微信好友
                case BaseResp.ErrCode.ERR_USER_CANCEL :
                    // 新浪微博, Facebook
                case WBConstants.ErrorCode.ERR_CANCEL :
                    // QQ空间，QQ好友
                case QzoneBlog2.SEND_CANCEL : {
                    ToastUtil.showToast(getContext(), getResources().getString(R.string.user_cancel_share));
                    break;
                }

                // 分享失败
                // 微信朋友圈，微信好友
                case BaseResp.ErrCode.ERR_AUTH_DENIED :
                    // QQ空间，QQ好友
                case QzoneBlog2.SEND_FAIL:
                    // 新浪微博, Facebook
                case WBConstants.ErrorCode.ERR_FAIL: {
                    Toast.makeText(getContext(), getContext().getString(R.string.fail_to_share), Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    };


    private ProgressDialog mProgressDialog;
    /**
     * 把要分享到各大社交平台的缩略图先保存到指定的路径
     */
    private static String sImgPathToDomesticApp, sImgPathToForeignSocialMedia, sImgPathToInstagramApp;
    private static final String RES_DIRECTORY= "shareWithFriends";
    private static final String RES_IMGPATH_DOMESTICAPP = "sharewithfriends_todomesticapp.jpg";
    private static final String RES_IMGPATH_FOREIGNAPP = "sharewithfriends_toforeignapp.jpg";
    private static final String RES_IMGPATH_INSTAGRAM = "sharewithfriends_toinstagram.jpg";
    private void saveSharedImageToSdCard () {
        if (sImgPathToDomesticApp == null) {
            sImgPathToDomesticApp = FileCacheMgr.GetAppPath();
            final String resPath = RES_DIRECTORY.concat(File.separator).concat(RES_IMGPATH_DOMESTICAPP);
            FileUtil.assets2SD(mContext, resPath, sImgPathToDomesticApp, true);
        }

        if (sImgPathToForeignSocialMedia == null) {
            sImgPathToForeignSocialMedia = FileCacheMgr.GetAppPath();
            final String resPath = RES_DIRECTORY.concat(File.separator).concat(RES_IMGPATH_FOREIGNAPP);
            FileUtil.assets2SD(mContext, resPath, sImgPathToForeignSocialMedia, true);
        }

        if (sImgPathToInstagramApp == null) {
            sImgPathToInstagramApp = FileCacheMgr.GetAppPath();
            final String resPath = RES_DIRECTORY.concat(File.separator).concat(RES_IMGPATH_INSTAGRAM);
            FileUtil.assets2SD(mContext, resPath, sImgPathToInstagramApp, true);
        }
    }

    /**
     * 展示加载对话框
     */
    private void ShowWaitDlg()
    {
        if(mProgressDialog == null)
        {
            mProgressDialog = ProgressDialog.show(getContext(), "", getContext().getString(R.string.loading));
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        mProgressDialog.show();
    }

    /**
     * 关闭加载对话框
     */
    public void CloseWaitDlg()
    {
        if(mProgressDialog != null)
        {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            mProgressDialog = null;
        }
    }

    private void ensureScrollbarPosition() {
        if (mScrollView != null) {
            mScrollView.scrollTo(0, 0);
        }
    }


}
