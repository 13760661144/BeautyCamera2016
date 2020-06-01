package cn.poco.home.home4;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.poco.adMaster.ShareAdBanner;
import cn.poco.advanced.ImageUtils;
import cn.poco.beautify4.UiMode;
import cn.poco.campaignCenter.api.CampaignApi;
import cn.poco.campaignCenter.manager.ConnectionsManager;
import cn.poco.campaignCenter.model.CampaignInfo;
import cn.poco.framework.BaseSite;
import cn.poco.framework.DataKey;
import cn.poco.framework.IPage;
import cn.poco.home.home4.utils.PercentUtil;
import cn.poco.home.home4.widget.EntryPageScrollView;
import cn.poco.home.home4.widget.RoundColorDrawable;
import cn.poco.home.site.BeautyEntryPageSite;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.system.AppInterface;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.widget.recycle.EntryPageAdapter;
import cn.poco.widget.recycle.EntryPageItemDecoration;
import my.beautyCamera.R;

/**
 * Created by lgd on 2017/12/13.
 */

public class BeautyEntryPage extends IPage implements IActivePage {
    private final int itemFirstMargin = ShareData.PxToDpi_xhdpi(21);
//    private final int itemFirstMargin = PercentUtil.HeightPxxToPercent(21);
    private final int itemWidth = (ShareData.m_screenWidth - itemFirstMargin) * 2 / 9;
    private final int scrollMaxWidth = 4 * itemWidth;
    private final int indexMinWidth = ShareData.PxToDpi_xhdpi(14);
    private final int indexMaxWidth = ShareData.PxToDpi_xhdpi(116);
    private final int indexMinAlpha = (int) (0.16 * 255f);
    private final int indexMaxAlpha = (int) (0.4 * 255f);
//    private final int topH = ShareData.PxToDpi_xhdpi(373);
    private final int topH = PercentUtil.HeightPxToPercent(373);
 //   private final int recycleItemLeftMargin = ShareData.PxToDpi_xhdpi(20);
    private final int recycleItemLeftMargin = PercentUtil.WidthPxToPercent(20);
    private final int firstScrollOffset = ShareData.PxToDpi_xhdpi(20);
    private final int[] beautyImgs = {
            R.drawable.beauty_entry_slimming,
            R.drawable.beauty_entry_face,
            R.drawable.beauty_entry_make_up,
            R.drawable.beauty_entry_acne_treatment,

            R.drawable.beauty_entry_nose,
            R.drawable.beauty_entry_slimming_nose,
            R.drawable.beauty_entry_smile,
            R.drawable.beauty_entry_tooth,

    };
    private final int[] beautifyImags = {
            R.drawable.beauty_entry_eye,
            R.drawable.beauty_entry_eye_bag,
            R.drawable.beauty_entry_showy_eye,
            R.drawable.beauty_entry_rise,
            R.drawable.beauty_entry_qface,


    };
    private final int[] beautyToolImage = {
            R.drawable.beatry_entry_tailer,
            R.drawable.beauty_entry_filter,
            R.drawable.beauty_entry_rahmen,
            R.drawable.beauty_entry_mosaic,
            R.drawable.beauty_entry_chartlet,
            R.drawable.beauty_entry_magic,
            R.drawable.beauty_entry_clouded_glass

    };
    private final int OVER_SCROLL_NEVER = 2;
    private List<CampaignInfo> mItemDataList = new ArrayList<>();
    private String TAG = "BeautyEntryPage";
    private BeautyEntryPageSite mSite;
    private Context mContext;
    private RecyclerView recyclerView;
    private LinearLayout mContainer;
    private LinearLayout rootLayoutTop;
    private LinearLayout rootLayoutBotton;
    private LinearLayout beautyToolLayout;
    private String[] beautyText = {
            getResources().getString(R.string.beautify4page_shoushen_btn),
            getResources().getString(R.string.beautify4page_meiyan_btn),
            getResources().getString(R.string.beautify4page_caizhuang_btn),
            getResources().getString(R.string.beautify4page_qudou_btn),

            getResources().getString(R.string.beautify4page_gaobiliang_btn),
            getResources().getString(R.string.beautify4page_shoubi_btn),
            getResources().getString(R.string.beautify4page_weixiao_btn),
            getResources().getString(R.string.beautify4page_meiya_btn)
    };
    private String[] beautifyText = {
            getResources().getString(R.string.beautify4page_dayan_btn),
            getResources().getString(R.string.beautify4page_quyandai_btn),
            getResources().getString(R.string.beautify4page_liangyan_btn),
            getResources().getString(R.string.beautify4page_zenggao_btn),

            getResources().getString(R.string.beautify4page_yijianmengzhuang_btn),

    };
    private String[] beautyToolText = {
            getResources().getString(R.string.entry_beauty_tailor),
            getResources().getString(R.string.beautify4page_lvjing_btn),
            getResources().getString(R.string.beautify4page_xiangkuang_btn),
            getResources().getString(R.string.beautify4page_masaike_btn),
            getResources().getString(R.string.beautify4page_tietu_btn),
            getResources().getString(R.string.beautify4page_zhijianmofa_btn),
            getResources().getString(R.string.beautify4page_maoboli_btn),
    };
    private ImageView backView;
    OnAnimationClickListener mOnClickListener = new OnAnimationClickListener() {
        @Override
        public void onAnimationClick(View v) {
            if (v == backView) {
                MyBeautyStat.onClickByRes(R.string.首页_快捷修图_主页面_退出);
                mSite.OnBack(getContext());
            } else {

                HashMap<String, Object> map;
                UiMode uiMode = null;
                map = new HashMap<>();
                //根据id判断  R.drawable.xxx,  文字可能是英文
                int id = (int) v.getTag();
                switch (id) {
                    case R.drawable.beauty_entry_eye:
                        MyBeautyStat.onClickByRes(R.string.首页_快捷修图_主页面_大眼);
                        uiMode = UiMode.DAYAN;
                        break;
                    case R.drawable.beauty_entry_eye_bag:
                        MyBeautyStat.onClickByRes(R.string.首页_快捷修图_主页面_祛眼袋);
                        uiMode = UiMode.QUYANDAI;
                        break;
                    case R.drawable.beauty_entry_showy_eye:
                        MyBeautyStat.onClickByRes(R.string.首页_快捷修图_主页面_亮眼);
                        uiMode = UiMode.LIANGYAN;
                        break;
                    case R.drawable.beauty_entry_rise:
                        MyBeautyStat.onClickByRes(R.string.首页_快捷修图_主页面_增高);
                        uiMode = UiMode.ZENGGAO;
                        break;
                    case R.drawable.beauty_entry_qface:
                        MyBeautyStat.onClickByRes(R.string.首页_快捷修图_主页面_一键萌妆);
                        uiMode = UiMode.YIJIANMENGZHUANG;
                        break;
                    case R.drawable.beauty_entry_slimming:
                        MyBeautyStat.onClickByRes(R.string.首页_快捷修图_主页面_瘦脸瘦身);
                        uiMode = UiMode.SHOUSHEN;
                        break;
                    case R.drawable.beauty_entry_face:
                        MyBeautyStat.onClickByRes(R.string.首页_快捷修图_主页面_美颜);
                        uiMode = UiMode.MEIYAN;
                        break;
                    case R.drawable.beauty_entry_make_up:
                        MyBeautyStat.onClickByRes(R.string.首页_快捷修图_主页面_彩妆);
                        uiMode = UiMode.CAIZHUANG;
                        break;
                    case R.drawable.beauty_entry_acne_treatment:
                        MyBeautyStat.onClickByRes(R.string.首页_快捷修图_主页面_祛痘);
                        uiMode = UiMode.QUDOU;
                        break;
                    case R.drawable.beauty_entry_nose:
                        MyBeautyStat.onClickByRes(R.string.首页_快捷修图_主页面_高鼻梁);
                        uiMode = UiMode.GAOBILIANG;
                        break;
                    case R.drawable.beauty_entry_slimming_nose:
                        MyBeautyStat.onClickByRes(R.string.首页_快捷修图_主页面_瘦鼻);
                        uiMode = UiMode.SHOUBI;
                        break;
                    case R.drawable.beauty_entry_smile:
                        MyBeautyStat.onClickByRes(R.string.首页_快捷修图_主页面_微笑);
                        uiMode = UiMode.WEIXIAO;
                        break;
                    case R.drawable.beauty_entry_tooth:
                        MyBeautyStat.onClickByRes(R.string.首页_快捷修图_主页面_美牙);
                        uiMode = UiMode.MEIYA;
                        break;
                    case R.drawable.beatry_entry_tailer:
                        MyBeautyStat.onClickByRes(R.string.首页_快捷修图_主页面_裁剪);
                        uiMode = UiMode.CLIP;
                        break;
                    case R.drawable.beauty_entry_filter:
                        MyBeautyStat.onClickByRes(R.string.首页_快捷修图_主页面_滤镜);
                        uiMode = UiMode.LVJING;
                        break;
                    case R.drawable.beauty_entry_rahmen:
                        MyBeautyStat.onClickByRes(R.string.首页_快捷修图_主页面_相框);
                        uiMode = UiMode.XIANGKUANG;
                        break;
                    case R.drawable.beauty_entry_mosaic:
                        MyBeautyStat.onClickByRes(R.string.首页_快捷修图_主页面_马赛克);
                        uiMode = UiMode.MASAIKE;
                        break;
                    case R.drawable.beauty_entry_chartlet:
                        MyBeautyStat.onClickByRes(R.string.首页_快捷修图_主页面_贴图);
                        uiMode = UiMode.TIETU;
                        break;
                    case R.drawable.beauty_entry_magic:
                        MyBeautyStat.onClickByRes(R.string.首页_快捷修图_主页面_指尖魔法);
                        uiMode = UiMode.ZHIJIANMOFA;
                        break;
                    case R.drawable.beauty_entry_clouded_glass:
                        MyBeautyStat.onClickByRes(R.string.首页_快捷修图_主页面_毛玻璃);
                        uiMode = UiMode.MAOBOLI;
                        break;
                }
                map.put(DataKey.BEAUTIFY_DEF_OPEN_PAGE, uiMode.GetValue());
                mSite.onAlbum(mContext, map);
            }


        }
    };
    private View beautyIndexLeft;
    private View beautyIndexRight;
    private View beautyToolIndexLeft;
    private View beautyToolIndexRight;
    private FrameLayout topLayout;
    private EntryPageAdapter entryPageAdapter;
    private EntryPageAdapter.OnItemClickListener onItemClickListener = new EntryPageAdapter.OnItemClickListener() {
        @Override
        public void onClick(int position, CampaignInfo info, boolean isImageLoadComplete)
        {
            if(isImageLoadComplete)
            {
                ShareAdBanner.SendTj(getContext(), info.getBannerTjUrl());
                MyBeautyStat.onBanner(info.getStatisticId());
                mSite.onBanner(getContext(), info);
            }
        }
    };
    private ConnectionsManager.RequestDelegate mGetDataDelagete = new ConnectionsManager.RequestDelegate() {
        @Override
        public void run(Object response, ConnectionsManager.NetWorkError error) {
            // 成功获取服务器数据
            if (error == null) {
                String json = null;
                CampaignApi campaignApi = (CampaignApi) response;
                if (campaignApi != null) {
                    if (entryPageAdapter != null && campaignApi.mCustomBeautyData != null && campaignApi.mCustomBeautyData.size() > 0) {
                        json = campaignApi.json;
                        entryPageAdapter.setDates(campaignApi.mCustomBeautyData);
                    }
                }
                TagMgr.SetTagValue(getContext(), Tags.HOME_BEAUTY_ENTRY_JSON, json);
            }
            // 网络原因，无法获取数据


        }
    };

    public BeautyEntryPage(Context context, BaseSite site) {
        super(context, site);
        mSite = (BeautyEntryPageSite) site;
        mContext = context;
        initUI();
        String json = TagMgr.GetTagValue(getContext(), Tags.HOME_BEAUTY_ENTRY_JSON);
        if (!TextUtils.isEmpty(json)) {
            CampaignApi campaignApi = new CampaignApi();
            if (campaignApi.DecodeData(json)) {
                mGetDataDelagete.run(campaignApi, null);
            }
        }
        ConnectionsManager.getInstacne().getCampaignInfo("4", "1", "100", AppInterface.GetInstance(getContext()), mGetDataDelagete);
    }

    private void initUI() {

        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, topH);
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.bottomMargin = PercentUtil.HeightPxToPercent(907);
        {//运营位
            topLayout = new FrameLayout(mContext);
            topLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSite.OnBack(getContext());
                }
            });
            addView(topLayout, layoutParams);
            {
                layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
               // layoutParams.bottomMargin = ShareData.PxToDpi_xhdpi(40);
                if(ShareData.m_screenHeight == ShareData.m_screenRealHeight)
                {
                    layoutParams.bottomMargin = PercentUtil.HeightPxToPercent(40);
                }
                else
                {
                    layoutParams.bottomMargin = PercentUtil.HeightPxToPercent(25);
                }
                recyclerView = new RecyclerView(mContext);
               // recyclerView.setPadding(ShareData.PxToDpi_xhdpi(40), 0, ShareData.PxToDpi_xhdpi(40), 0);
                recyclerView.setPadding(PercentUtil.WidthPxToPercent(40), 0,PercentUtil.WidthPxToPercent(40), 0);
                recyclerView.setClipToPadding(false);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                recyclerView.addItemDecoration(new EntryPageItemDecoration(recycleItemLeftMargin));
                recyclerView.setOverScrollMode(OVER_SCROLL_NEVER);
                recyclerView.setLayoutManager(linearLayoutManager);
                entryPageAdapter = new EntryPageAdapter(mContext);
                entryPageAdapter.setOnItemClickListener(onItemClickListener);
                recyclerView.setAdapter(entryPageAdapter);
                topLayout.addView(recyclerView, layoutParams);
            }
        /*    View bk = new View(getContext());
            bk.setBackgroundColor(Color.WHITE);
            bk.getBackground().setAlpha((int) (0.9f * 255));
            layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.BOTTOM;
          //  layoutParams.topMargin = topH;
            addView(bk, layoutParams);*/
        }


        FrameLayout frameLayout = new FrameLayout(getContext());
        frameLayout.setBackgroundColor(Color.WHITE);
        frameLayout.getBackground().setAlpha((int) (0.9f * 255));
        layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM;
        //layoutParams.topMargin = topH;
        addView(frameLayout,layoutParams);

        {//美颜
            TextView textBeauty = new TextView(mContext);
            textBeauty.setTextColor(0xff999999);
            textBeauty.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.BOTTOM;
        //    layoutParams.setMargins(ShareData.PxToDpi_xhdpi(34), ShareData.PxToDpi_xhdpi(397), 0, ShareData.PxToDpi_xhdpi(32));
            layoutParams.setMargins(PercentUtil.WidthPxToPercent(34), PercentUtil.HeightPxToPercent(30), 0, PercentUtil.HeightPxToPercent(852));
            textBeauty.setText(R.string.entry_beauty_face);
            frameLayout.addView(textBeauty, layoutParams);

            EntryPageScrollView entryPageScrollView = new EntryPageScrollView(mContext);
            entryPageScrollView.setOverScrollMode(OVER_SCROLL_NEVER);
            entryPageScrollView.setScrollListener(new EntryPageScrollView.ScrollListener() {
                @Override
                public void onScrollChanged(int scrollX) {
                    float percent = scrollX * 1.0f / scrollMaxWidth;
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) beautyIndexLeft.getLayoutParams();
                    params.width = (int) (indexMaxWidth - percent * (indexMaxWidth - indexMinWidth));
                    beautyIndexLeft.requestLayout();
                    beautyIndexLeft.getBackground().setAlpha((int) (indexMaxAlpha - percent * (indexMaxAlpha - indexMinAlpha)));

                    params = (LinearLayout.LayoutParams) beautyIndexRight.getLayoutParams();
                    params.width = (int) (indexMinWidth + percent * (indexMaxWidth - indexMinWidth));
                    beautyIndexRight.requestLayout();
                    beautyIndexRight.getBackground().setAlpha((int) (indexMinAlpha + percent * (indexMaxAlpha - indexMinAlpha)));
                }
            });
            entryPageScrollView.setHorizontalScrollBarEnabled(false);
            {
                mContainer = new LinearLayout(mContext);
                mContainer.setMinimumWidth(ShareData.m_screenWidth + scrollMaxWidth);
                mContainer.setOrientation(LinearLayout.VERTICAL);
                rootLayoutTop = addLineItems(beautyImgs, beautyText);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                mContainer.addView(rootLayoutTop, lp);

                rootLayoutBotton = addLineItems(beautifyImags, beautifyText);
                lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                //lp.topMargin = ShareData.PxToDpi_xhdpi(68);
                lp.topMargin = PercentUtil.HeightPxToPercent(68);
                mContainer.addView(rootLayoutBotton, lp);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                entryPageScrollView.addView(mContainer, params);
                entryPageScrollView.setMaxScroll(scrollMaxWidth);

            }

            layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
           // layoutParams.setMargins(0, ShareData.PxToDpi_xhdpi(458), 0, 0);
            layoutParams.setMargins(0, 0, 0, PercentUtil.HeightPxToPercent(535));
            layoutParams.gravity = Gravity.BOTTOM;
            frameLayout.addView(entryPageScrollView, layoutParams);


            LinearLayout indexParent = new LinearLayout(getContext());
            indexParent.setGravity(Gravity.CENTER);
            indexParent.setOrientation(LinearLayout.HORIZONTAL);
            layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
          //  layoutParams.topMargin = ShareData.PxToDpi_xhdpi(802);
            layoutParams.bottomMargin = PercentUtil.HeightPxToPercent(465);
        //    layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            frameLayout.addView(indexParent, layoutParams);
            {
                RoundColorDrawable drawable1 = new RoundColorDrawable(ImageUtils.GetSkinColor());
                drawable1.setAlpha(indexMaxAlpha);
                LinearLayout.LayoutParams lp;
                beautyIndexLeft = new View(getContext());
                beautyIndexLeft.setBackgroundDrawable(drawable1);
                lp = new LinearLayout.LayoutParams(indexMaxWidth, ShareData.PxToDpi_xhdpi(10));
                lp.gravity = Gravity.CENTER;
                indexParent.addView(beautyIndexLeft, lp);

                RoundColorDrawable drawable2 = new RoundColorDrawable(ImageUtils.GetSkinColor());
                drawable2.setAlpha(indexMinAlpha);
                beautyIndexRight = new View(getContext());
                beautyIndexRight.setBackgroundDrawable(drawable2);
                lp = new LinearLayout.LayoutParams(indexMinWidth, ShareData.PxToDpi_xhdpi(10));
                lp.leftMargin = ShareData.PxToDpi_xhdpi(8);
                lp.gravity = Gravity.CENTER;
                indexParent.addView(beautyIndexRight, lp);
            }
        }

        //美化
        {
            TextView textBeautyTool = new TextView(mContext);
            textBeautyTool.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            textBeautyTool.setTextColor(0xff999999);
            layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.BOTTOM;
            //layoutParams.setMargins(ShareData.PxToDpi_xhdpi(40), ShareData.PxToDpi_xhdpi(846), 0, 0);
            layoutParams.setMargins(PercentUtil.WidthPxToPercent(40), 0, 0, PercentUtil.HeightPxToPercent(408));
            textBeautyTool.setText(R.string.entry_beauty_tool);
            frameLayout.addView(textBeautyTool, layoutParams);

            EntryPageScrollView entryPageScrollView_tool = new EntryPageScrollView(mContext);
            entryPageScrollView_tool.setHorizontalScrollBarEnabled(false);
            entryPageScrollView_tool.setOverScrollMode(OVER_SCROLL_NEVER);
            entryPageScrollView_tool.setMaxScroll(scrollMaxWidth);
            entryPageScrollView_tool.setScrollListener(new EntryPageScrollView.ScrollListener() {
                @Override
                public void onScrollChanged(int scrollX) {
                    float percent = scrollX * 1.0f / scrollMaxWidth;
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) beautyToolIndexLeft.getLayoutParams();
                    params.width = (int) (indexMaxWidth - percent * (indexMaxWidth - indexMinWidth));
                    beautyToolIndexLeft.requestLayout();
                    beautyToolIndexLeft.getBackground().setAlpha((int) (indexMaxAlpha - percent * (indexMaxAlpha - indexMinAlpha)));

                    params = (LinearLayout.LayoutParams) beautyToolIndexRight.getLayoutParams();
                    params.width = (int) (indexMinWidth + percent * (indexMaxWidth - indexMinWidth));
                    beautyToolIndexRight.requestLayout();
                    beautyToolIndexRight.getBackground().setAlpha((int) (indexMinAlpha + percent * (indexMaxAlpha - indexMinAlpha)));
                }
            });
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            params.setMargins(0, ShareData.PxToDpi_xhdpi(903), 0, 0);
            params.gravity = Gravity.BOTTOM;
            params.setMargins(0, 0, 0, PercentUtil.HeightPxToPercent(274));
            frameLayout.addView(entryPageScrollView_tool, params);
            {
                beautyToolLayout = addLineItems(beautyToolImage, beautyToolText);
                beautyToolLayout.setMinimumWidth(ShareData.m_screenWidth + scrollMaxWidth);
                FrameLayout.LayoutParams toolLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                entryPageScrollView_tool.addView(beautyToolLayout, toolLayoutParams);
            }

            LinearLayout indexParent = new LinearLayout(getContext());
            indexParent.setGravity(Gravity.CENTER);
            indexParent.setOrientation(LinearLayout.HORIZONTAL);
            layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
//            layoutParams.topMargin = ShareData.PxToDpi_xhdpi(1068);
            layoutParams.bottomMargin = PercentUtil.HeightPxToPercent(206);
           // layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            frameLayout.addView(indexParent, layoutParams);
            {
                RoundColorDrawable drawable1 = new RoundColorDrawable(ImageUtils.GetSkinColor());
                drawable1.setAlpha(indexMaxAlpha);
                LinearLayout.LayoutParams lp;
                beautyToolIndexLeft = new View(getContext());
                beautyToolIndexLeft.setBackgroundDrawable(drawable1);
              //  lp = new LinearLayout.LayoutParams(indexMaxWidth, ShareData.PxToDpi_xhdpi(10));
                lp = new LinearLayout.LayoutParams(indexMaxWidth,PercentUtil.HeightPxToPercent(10));
                lp.gravity = Gravity.CENTER;
                indexParent.addView(beautyToolIndexLeft, lp);

                RoundColorDrawable drawable2 = new RoundColorDrawable(ImageUtils.GetSkinColor());
                drawable2.setAlpha(indexMinAlpha);
                beautyToolIndexRight = new View(getContext());
                beautyToolIndexRight.setBackgroundDrawable(drawable2);
              //  lp = new LinearLayout.LayoutParams(indexMinWidth, ShareData.PxToDpi_xhdpi(10));
                lp = new LinearLayout.LayoutParams(indexMinWidth,PercentUtil.HeightPxToPercent(10));
               // lp.leftMargin = ShareData.PxToDpi_xhdpi(8);
                lp.leftMargin = PercentUtil.WidthPxToPercent(8);
                lp.gravity = Gravity.CENTER;
                indexParent.addView(beautyToolIndexRight, lp);
            }

        }

        {
          //  FrameLayout.LayoutParams backParams = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(120), ShareData.PxToDpi_xhdpi(120));
            FrameLayout.LayoutParams backParams = new FrameLayout.LayoutParams(PercentUtil.WidthPxToPercent(120),PercentUtil.HeightPxToPercent(120));
            backView = new ImageView(mContext);
            backView.setOnTouchListener(mOnClickListener);
            backParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
//            backParams.setMargins(0, 0, 0, ShareData.PxToDpi_xhdpi(24));
            backView.setImageResource(R.drawable.beauty_entry_back_btn);
            backParams.setMargins(0, 0, 0, PercentUtil.HeightPxToPercent(24));
            frameLayout.addView(backView, backParams);

        }

    }

    private LinearLayout addLineItems(int[] imgs, String[] texts) {
        LinearLayout parent = new LinearLayout(getContext());
        parent.setPadding(itemFirstMargin, 0, 0, 0);
        parent.setOrientation(LinearLayout.HORIZONTAL);
        int size = Math.min(imgs.length, texts.length);
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(itemWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        ll.gravity = Gravity.CENTER;
        for (int i = 0; i < size; i++) {
            Drawable drawable = getResources().getDrawable(imgs[i]);
            drawable.setColorFilter(ImageUtils.GetSkinColor(), PorterDuff.Mode.SRC_IN);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            TextView textView = new TextView(getContext());
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12f);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(ImageUtils.GetSkinColor());
            textView.setText(texts[i]);
            //textView.setCompoundDrawablePadding(ShareData.PxToDpi_xhdpi(8));
            textView.setCompoundDrawablePadding(PercentUtil.HeightPxToPercent(8));
            textView.setCompoundDrawables(null, drawable, null, null);
            textView.setSingleLine();
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setTag(imgs[i]);
            textView.setOnTouchListener(mOnClickListener);
            textView.setLayoutParams(ll);
            parent.addView(textView);
        }
        return parent;
    }


    @Override
    public void SetData(HashMap<String, Object> params) {
        if (mSite.m_myParams.containsKey("position") && mSite.m_myParams.containsKey("offset")) {
            int position = (int) mSite.m_myParams.get("position");
            int offset = (int) mSite.m_myParams.get("offset");
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            layoutManager.scrollToPositionWithOffset(position, offset);
        } else {
            resetPosition();
        }
    }

    public void setBk(Bitmap bk) {
        setBackgroundColor(Color.TRANSPARENT);
        if (bk == null) {
            if (!TextUtils.isEmpty(Home4Page.s_maskBmpPath)) {
                this.setBackgroundDrawable(new BitmapDrawable(cn.poco.imagecore.Utils.DecodeFile(Home4Page.s_maskBmpPath, null)));
            } else {
                this.setBackgroundResource(R.drawable.login_tips_all_bk);
            }
        } else {
            setBackgroundDrawable(new BitmapDrawable(bk));
        }
    }

    @Override
    public void onBack() {
        MyBeautyStat.onClickByRes(R.string.首页_快捷修图_主页面_退出);
        mSite.OnBack(getContext());
    }

    public void savePosition() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int position = layoutManager.findFirstVisibleItemPosition();
        if (position >= 0) {
            View firstVisiableChildView = layoutManager.findViewByPosition(position);
            int offset = firstVisiableChildView.getLeft() - recyclerView.getPaddingLeft();
            offset -= position == 0 ? 0 : recycleItemLeftMargin;
            mSite.m_myParams.put("offset", offset);
            mSite.m_myParams.put("position", position);
        }
    }

    private void resetPosition() {
        mSite.m_myParams.remove("offset");
        mSite.m_myParams.remove("position");
        if (recyclerView == null) {
            return;
        }
        recyclerView.stopScroll();
        recyclerView.setAlpha(0);
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        layoutManager.scrollToPositionWithOffset(0, 0);
        if (entryPageAdapter.getItemsW() >= ShareData.m_screenWidth) {
            recyclerView.setTranslationX(firstScrollOffset);
        } else {
            recyclerView.setTranslationX(0);
        }
    }

    @Override
    public void onPageActive(int lastActiveMode) {
        MyBeautyStat.onPageStartByRes(R.string.快捷修图_首页_主页面);
        if (lastActiveMode == Home4Page.HOME) {
            if (entryPageAdapter.getItemsW() >= ShareData.m_screenWidth) {
                recyclerView.animate().setInterpolator(new AccelerateDecelerateInterpolator()).translationX(0).alpha(1).setDuration(300).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        recyclerView.setHorizontalScrollBarEnabled(true);
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        recyclerView.setHorizontalScrollBarEnabled(false);
                    }
                }).start();
            }
        }
    }

    @Override
    public void onPageInActive(int nextActiveMode) {
        MyBeautyStat.onPageEndByRes(R.string.快捷修图_首页_主页面);
        if (nextActiveMode == Home4Page.NONE) {
            savePosition();
        } else {
            resetPosition();
        }
    }

    @Override
    public void setUiEnable(boolean uiEnable) {

    }


}
