package cn.poco.beautify;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelbase.BaseResp;

import java.util.ArrayList;

import cn.poco.MaterialMgr2.MgrUtils;
import cn.poco.credits.Credit;
import cn.poco.image.filter;
import cn.poco.login.UserMgr;
import cn.poco.resource.BaseRes;
import cn.poco.resource.BrushRes;
import cn.poco.resource.BrushResMgr2;
import cn.poco.resource.DecorateRes;
import cn.poco.resource.DecorateResMgr2;
import cn.poco.resource.DownloadMgr;
import cn.poco.resource.FilterRes;
import cn.poco.resource.FilterResMgr2;
import cn.poco.resource.FrameExRes;
import cn.poco.resource.FrameExResMgr2;
import cn.poco.resource.FrameRes;
import cn.poco.resource.FrameResMgr2;
import cn.poco.resource.GlassRes;
import cn.poco.resource.GlassResMgr2;
import cn.poco.resource.IDownload;
import cn.poco.resource.LimitRes;
import cn.poco.resource.LockRes;
import cn.poco.resource.MakeupComboResMgr2;
import cn.poco.resource.MakeupRes;
import cn.poco.resource.MosaicRes;
import cn.poco.resource.MosaicResMgr2;
import cn.poco.resource.RecommendRes;
import cn.poco.resource.ResType;
import cn.poco.resource.ThemeRes;
import cn.poco.setting.SettingInfo;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.share.SharePage;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.tianutils.MakeBmp;
import my.beautyCamera.R;
import my.beautyCamera.wxapi.SendWXAPI;

public class RecomDisplayMgr {
    protected RecomDisplayUIV2 m_view;

    protected BaseRes m_res;
    protected int m_resType;

    protected MyDownloadCallback m_dlcb;
    protected MyWXCallback m_wxcb;
    protected CreditCallback m_creditcb;
    protected RecomDisplayUIV2.Callback m_recomcb;
    protected Callback m_cb;

    protected Context m_context;

    protected LockRes m_themeLockInfo;
    protected boolean m_canClick = false;
    protected boolean m_recycle = false;

    public interface Callback {
        public void UnlockSuccess(BaseRes res);

        public void OnCloseBtn();

        public void OnBtn(int state);

        public void OnClose();

        public void OnLogin();
    }

    public interface ExCallback extends Callback
    {
        void onWXCancel();
    }

    public RecomDisplayMgr(Context context, BaseRes res, int resType, Callback cb) {
        this(context, cb);
        m_res = res;
        m_resType = resType;
    }

    public RecomDisplayMgr(@NonNull Context context, Callback cb)
    {
        m_context = context;
        m_cb = cb;
        m_recomcb = new RecomDisplayUIV2.Callback() {
            @Override
            public void OnCloseBtn() {
                OnCancel(true);

                if (m_cb != null) {
                    m_cb.OnCloseBtn();
                }
            }

            @Override
            public void OnBtn(int state, boolean unlock) {
                switch (state) {
                    case RecomDisplayUIV2.BTN_STATE_UNLOCK: {
                        if (m_res instanceof LockRes) {
                            switch (((LockRes) m_res).m_shareType) {
                                case LockRes.SHARE_TYPE_MARKET: {
                                    if (!unlock)
                                    {
                                        if (m_view != null)
                                        {
                                            m_view.ShowChooseUnlockType(true);
                                        }
                                    }
                                    else
                                    {
                                        OpenMarket(m_context, m_resType, m_res.m_id);
                                        if (m_view != null)
                                        {
                                            m_view.SetBtnState(RecomDisplayUIV2.BTN_STATE_DOWNLOAD);
                                        }
                                        if (m_recomcb != null)
                                        {
                                            m_recomcb.OnBtn(RecomDisplayUIV2.BTN_STATE_DOWNLOAD, unlock);
                                        }
                                        if (m_cb != null)
                                        {
                                            m_cb.UnlockSuccess(m_res);
                                        }
                                    }
                                    break;
                                }

                                case LockRes.SHARE_TYPE_WEIXIN: {
                                    if (!unlock) {
                                        if (m_view != null) {
                                            m_view.ShowChooseUnlockType(true);
                                        }
                                    } else if (m_wxcb != null) {
                                        if (m_wxcb.mIsSharing) break;
                                        m_wxcb.mIsSharing = true;
                                        String url = null;
                                        if (((LockRes) m_res).m_shareLink != null && ((LockRes) m_res).m_shareLink.length() > 0) {
                                            url = ((LockRes) m_res).m_shareLink;
                                        }
                                        SharePage.unlockResourceByWeiXin(m_context, ((LockRes) m_res).m_shareContent, url, MakeWXLogo(m_context, ((LockRes) m_res).m_shareImg), m_wxcb);
                                    }
                                    break;
                                }
                                default:
                                    break;
                            }
                        } else if (m_res instanceof RecommendRes) {
                            if (m_themeLockInfo != null) {
                                switch (m_themeLockInfo.m_shareType) {
                                    case LockRes.SHARE_TYPE_MARKET: {
                                        if (!unlock)
                                        {
                                            if (m_view != null)
                                            {
                                                m_view.ShowChooseUnlockType(true);
                                            }
                                        }
                                        else
                                        {
                                            OpenMarket(m_context, m_resType, m_res.m_id);
                                            if (m_view != null)
                                            {
                                                m_view.SetBtnState(RecomDisplayUIV2.BTN_STATE_DOWNLOAD);
                                            }
                                            if (m_recomcb != null)
                                            {
                                                m_recomcb.OnBtn(RecomDisplayUIV2.BTN_STATE_DOWNLOAD, unlock);
                                            }
                                            if (m_cb != null)
                                            {
                                                m_cb.UnlockSuccess(m_res);
                                            }
                                        }
                                        break;
                                    }

                                    case LockRes.SHARE_TYPE_WEIXIN: {
                                        if (!unlock) {
                                            if (m_view != null) {
                                                m_view.ShowChooseUnlockType(true);
                                            }
                                        } else {
                                            String url = null;
                                            if (m_themeLockInfo.m_shareLink != null && m_themeLockInfo.m_shareLink.length() > 0) {
                                                url = m_themeLockInfo.m_shareLink;
                                            }
                                            SharePage.unlockResourceByWeiXin(m_context, m_themeLockInfo.m_name, url, MakeWXLogo(m_context, m_themeLockInfo.m_shareImg), m_wxcb);
                                        }
                                        break;
                                    }

                                    default:
                                        break;
                                }
                            }
                        }
                        break;
                    }

                    case RecomDisplayUIV2.BTN_STATE_DOWNLOAD: {
                        if (m_res != null) {
                            if (m_resType == ResType.FRAME.GetValue()) {
                                ThemeRes themeRes = MgrUtils.getThemeRes(m_context, m_res.m_id);
                                if (themeRes != null) {
                                    ArrayList<FrameRes> arr = FrameResMgr2.getInstance().GetResArr2(themeRes.m_frameIDArr, false);
                                    int size = arr.size();
                                    IDownload[] ress;
                                    FrameRes frame;
                                    if (themeRes.m_type == BaseRes.TYPE_NETWORK_URL) {
                                        ress = new IDownload[size + 1];
                                        for (int i = 0; i < size; i++) {
                                            frame = arr.get(i);
                                            ress[i] = frame;
                                        }
                                        ress[size] = themeRes;
                                    } else {
                                        ress = new IDownload[size];
                                        for (int i = 0; i < size; i++) {
                                            frame = arr.get(i);
                                            ress[i] = frame;
                                        }
                                    }
                                    if (ress.length > 0) {
                                        if (m_view != null) {
                                            m_view.SetBtnState(RecomDisplayUIV2.BTN_STATE_LOADING);
                                        }
                                        DownloadMgr.getInstance().DownloadRes(ress, false, m_dlcb);
                                    } else {
                                        OnCancel(true);
                                    }
                                }
                            } else if (m_resType == ResType.FRAME2.GetValue()) {
                                ThemeRes themeRes = MgrUtils.getThemeRes(m_context, m_res.m_id);
                                if (themeRes != null) {
                                    ArrayList<FrameExRes> arr = FrameExResMgr2.getInstance().GetResArr(themeRes.m_sFrameIDArr, false);
                                    int size = arr.size();
                                    IDownload[] ress;
                                    FrameExRes frame;
                                    if (themeRes.m_type == BaseRes.TYPE_NETWORK_URL) {
                                        ress = new IDownload[size + 1];
                                        for (int i = 0; i < size; i++) {
                                            frame = arr.get(i);
                                            ress[i] = frame;
                                        }
                                        ress[size] = themeRes;
                                    } else {
                                        ress = new IDownload[size];
                                        for (int i = 0; i < size; i++) {
                                            frame = arr.get(i);
                                            ress[i] = frame;
                                        }
                                    }
                                    if (ress.length > 0) {
                                        if (m_view != null) {
                                            m_view.SetBtnState(RecomDisplayUIV2.BTN_STATE_LOADING);
                                        }
                                        DownloadMgr.getInstance().DownloadRes(ress, false, m_dlcb);
                                    } else {
                                        OnCancel(true);
                                    }
                                }
                            } else if (m_resType == ResType.BRUSH.GetValue()) {
                                ThemeRes themeRes = MgrUtils.getThemeRes(m_context, m_res.m_id);
                                if (themeRes != null) {
                                    ArrayList<BrushRes> arr = BrushResMgr2.getInstance().GetResArr(themeRes.m_brushIDArr, false);
                                    int size = arr.size();
                                    IDownload[] ress;
                                    BrushRes brush;
                                    if (themeRes.m_type == BaseRes.TYPE_NETWORK_URL) {
                                        ress = new IDownload[size + 1];
                                        for (int i = 0; i < size; i++) {
                                            brush = arr.get(i);
                                            ress[i] = brush;
                                        }
                                        ress[size] = themeRes;
                                    } else {
                                        ress = new IDownload[size];
                                        for (int i = 0; i < size; i++) {
                                            brush = arr.get(i);
                                            ress[i] = brush;
                                        }
                                    }
                                    if (ress.length > 0) {
                                        if (m_view != null) {
                                            m_view.SetBtnState(RecomDisplayUIV2.BTN_STATE_LOADING);
                                        }
                                        DownloadMgr.getInstance().DownloadRes(ress, false, m_dlcb);
                                    } else {
                                        OnCancel(true);
                                    }
                                }
                            } else if (m_resType == ResType.MAKEUP_GROUP.GetValue()) {
                                ThemeRes themeRes = MgrUtils.getThemeRes(m_context, m_res.m_id);
                                if (themeRes != null) {
                                    ArrayList<MakeupRes> arr = MakeupComboResMgr2.getInstance().GetResArr(themeRes.m_makeupIDArr);
                                    int size = arr.size();
                                    IDownload[] ress;
                                    MakeupRes makeup;
                                    if (themeRes.m_type == BaseRes.TYPE_NETWORK_URL) {
                                        ress = new IDownload[size + 1];
                                        for (int i = 0; i < size; i++) {
                                            makeup = arr.get(i);
                                            ress[i] = makeup;
                                        }
                                        ress[size] = themeRes;
                                    } else {
                                        ress = new IDownload[size];
                                        for (int i = 0; i < size; i++) {
                                            makeup = arr.get(i);
                                            ress[i] = makeup;
                                        }
                                    }
                                    if (ress.length > 0) {
                                        if (m_view != null) {
                                            m_view.SetBtnState(RecomDisplayUIV2.BTN_STATE_LOADING);
                                        }
                                        DownloadMgr.getInstance().DownloadRes(ress, false, m_dlcb);
                                    } else {
                                        OnCancel(true);
                                    }
                                }
                            } else if (m_resType == ResType.MOSAIC.GetValue()) {
                                ThemeRes themeRes = MgrUtils.getThemeRes(m_context, m_res.m_id);
                                if (themeRes != null) {
                                    ArrayList<MosaicRes> arr = MosaicResMgr2.getInstance().GetResArr(themeRes.m_mosaicIDArr);
                                    int size = arr.size();
                                    IDownload[] ress;
                                    MosaicRes mosaic;
                                    if (themeRes.m_type == BaseRes.TYPE_NETWORK_URL) {
                                        ress = new IDownload[size + 1];
                                        for (int i = 0; i < size; i++) {
                                            mosaic = arr.get(i);
                                            ress[i] = mosaic;
                                        }
                                        ress[size] = themeRes;
                                    } else {
                                        ress = new IDownload[size];
                                        for (int i = 0; i < size; i++) {
                                            mosaic = arr.get(i);
                                            ress[i] = mosaic;
                                        }
                                    }
                                    if (ress.length > 0) {
                                        if (m_view != null) {
                                            m_view.SetBtnState(RecomDisplayUIV2.BTN_STATE_LOADING);
                                        }
                                        DownloadMgr.getInstance().DownloadRes(ress, false, m_dlcb);
                                    } else {
                                        OnCancel(true);
                                    }
                                }
                            } else if (m_resType == ResType.GLASS.GetValue()) {
                                ThemeRes themeRes = MgrUtils.getThemeRes(m_context, m_res.m_id);
                                if (themeRes != null) {
                                    ArrayList<GlassRes> arr = GlassResMgr2.getInstance().GetResArr(themeRes.m_glassIDArr);
                                    int size = arr.size();
                                    IDownload[] ress;
                                    GlassRes glass;
                                    if (themeRes.m_type == BaseRes.TYPE_NETWORK_URL) {
                                        ress = new IDownload[size + 1];
                                        for (int i = 0; i < size; i++) {
                                            glass = arr.get(i);
                                            ress[i] = glass;
                                        }
                                        ress[size] = themeRes;
                                    } else {
                                        ress = new IDownload[size];
                                        for (int i = 0; i < size; i++) {
                                            glass = arr.get(i);
                                            ress[i] = glass;
                                        }
                                    }
                                    if (ress.length > 0) {
                                        if (m_view != null) {
                                            m_view.SetBtnState(RecomDisplayUIV2.BTN_STATE_LOADING);
                                        }
                                        DownloadMgr.getInstance().DownloadRes(ress, false, m_dlcb);
                                    } else {
                                        OnCancel(true);
                                    }
                                }
                            } else if (m_resType == ResType.DECORATE.GetValue()) {
                                ThemeRes themeRes = MgrUtils.getThemeRes(m_context, m_res.m_id);
                                if (themeRes != null) {
                                    ArrayList<DecorateRes> arr = DecorateResMgr2.getInstance().GetResArr2(themeRes.m_decorateIDArr, false);
                                    int size = arr.size();
                                    IDownload[] ress;
                                    DecorateRes decorateRes;
                                    if (themeRes.m_type == BaseRes.TYPE_NETWORK_URL) {
                                        ress = new IDownload[size + 1];
                                        for (int i = 0; i < size; i++) {
                                            decorateRes = arr.get(i);
                                            ress[i] = decorateRes;
                                        }
                                        ress[size] = themeRes;
                                    } else {
                                        ress = new IDownload[size];
                                        for (int i = 0; i < size; i++) {
                                            decorateRes = arr.get(i);
                                            ress[i] = decorateRes;
                                        }
                                    }
                                    if (ress.length > 0) {
                                        if (m_view != null) {
                                            m_view.SetBtnState(RecomDisplayUIV2.BTN_STATE_LOADING);
                                        }
                                        DownloadMgr.getInstance().DownloadRes(ress, false, m_dlcb);
                                    } else {
                                        OnCancel(true);
                                    }
                                }
                            } else if(m_resType == ResType.FILTER.GetValue())
                            {
                                ThemeRes themeRes = MgrUtils.getThemeRes(m_context, m_res.m_id);
                                if (themeRes != null) {
                                    ArrayList<FilterRes> arr = FilterResMgr2.getInstance().GetResArr(themeRes.m_filterIDArr);
                                    int size = arr.size();
                                    IDownload[] ress;
                                    FilterRes filterRes;
                                    if (themeRes.m_type == BaseRes.TYPE_NETWORK_URL) {
                                        ress = new IDownload[size + 1];
                                        for (int i = 0; i < size; i++) {
                                            filterRes = arr.get(i);
                                            ress[i] = filterRes;
                                        }
                                        ress[size] = themeRes;
                                    } else {
                                        ress = new IDownload[size];
                                        for (int i = 0; i < size; i++) {
                                            filterRes = arr.get(i);
                                            ress[i] = filterRes;
                                        }
                                    }
                                    if (ress.length > 0) {
                                        if (m_view != null) {
                                            m_view.SetBtnState(RecomDisplayUIV2.BTN_STATE_LOADING);
                                        }
                                        DownloadMgr.getInstance().DownloadRes(ress, false, m_dlcb);
                                    } else {
                                        OnCancel(true);
                                    }
                                }
                            } else {
                                OnCancel(true);
                            }
                        }
                        break;
                    }
                    case RecomDisplayUIV2.BTN_STATE_LIMIT_DOWNLOAD: {
                        OnCancel(true);
                        if (m_cb != null) {
                            m_cb.UnlockSuccess(m_res);
                        }
                    }
                    default:
                        break;
                }

                if (m_cb != null) {
                    m_cb.OnBtn(state);
                }
            }

            @Override
            public void OnCredit(String credit) {
                if (m_canClick) {
                    m_canClick = false;
                    int c = -1;
                    if (credit != null && credit.length() > 0) {
                        c = Integer.parseInt(credit);
                    }
                    if (c >= 60) {
                        if (m_res instanceof LockRes) {
                            if (m_view != null) {
                                m_view.ConsumeCredit(m_context, m_res.m_id, m_creditcb);
                            }
                        } else if (m_res instanceof RecommendRes) {
                            if (m_themeLockInfo != null && m_view != null) {
                                m_view.ConsumeCredit(m_context, m_themeLockInfo.m_id, m_creditcb);
                            }
                        }
                    } else {
                        if (c < 0 && !UserMgr.IsLogin(m_context,null)) {
                            if (m_cb != null) {
                                m_cb.OnLogin();
                            }
                        } else {
                            if (m_view != null) {
                                m_view.showToast("当前积分余额不足！");
                            }
                        }
                    }
                }
            }

            @Override
            public void OnClose() {
//                ClearCB();
//                m_recycle = true;
                if (m_cb != null) {
                    m_cb.OnClose();
                }
            }
        };

        m_view = new RecomDisplayUIV2((Activity) m_context, m_recomcb);
    }

    public Context getContext()
    {
        return m_context;
    }

    public BaseRes getData()
    {
        return m_res;
    }

    public void setCallback(Callback cb) {
        m_cb = cb;
    }

    protected void InitCB()
    {
        if(m_res != null)
        {
            m_dlcb = new MyDownloadCallback(this, m_res.m_id, m_resType);
            m_wxcb = new MyWXCallback(this, m_res.m_id, m_resType);
            m_creditcb = new CreditCallback(this, m_res.m_id, m_resType);
        }
    }

    protected void ClearCB()
    {
        if (m_dlcb != null) {
            m_dlcb.ClearAll();
            m_dlcb = null;
        }
        if (m_wxcb != null) {
            m_wxcb.ClearAll();
            m_wxcb = null;
        }
        if (m_creditcb != null) {
            m_creditcb.ClearAll();
            m_creditcb = null;
        }
    }

    public void onPause()
    {
        if (m_wxcb != null && m_wxcb.mIsSharing)
        {
            m_wxcb.mIsSharing = false;
        }
    }

    public void onResume()
    {
        //todo nothing
    }

    public void SetDatas(BaseRes res, int resType)
    {
        m_recycle = false;
        m_res = res;
        m_resType = resType;
        ClearCB();
        InitCB();

        m_view.SetImg(null);
        m_view.ResetCanCelBtnText();
        UpdateCredit();
        if (m_res != null) {
            if (m_res.m_type == BaseRes.TYPE_NETWORK_URL) {
                //下载资源
                m_view.SetImgState(RecomDisplayUIV2.IMG_STATE_LOADING);

                DownloadMgr.getInstance().DownloadRes(m_res, m_dlcb);
            } else {
                m_view.SetImgState(RecomDisplayUIV2.IMG_STATE_COMPLETE);

                Object img;
                if (m_res instanceof LockRes) {
                    img = ((LockRes) m_res).m_showImg;
                    if (img == null && !TextUtils.isEmpty(((LockRes) m_res).url_showImg)) {
                        img = ((LockRes) m_res).url_showImg;
                    }
                    m_view.SetImg(img);
                } else if (m_res instanceof RecommendRes) {
                    img = ((RecommendRes) m_res).m_showImg;
                    if (img == null && !TextUtils.isEmpty(((RecommendRes) m_res).url_showImg)) {
                        img = ((RecommendRes) m_res).url_showImg;
                    }
                    m_view.SetImg(img);
                } else if (m_res instanceof LimitRes) {
                    img = ((LimitRes) m_res).mLimitExplainThumb;
                    if (img == null && !TextUtils.isEmpty(((LimitRes) m_res).mLimitExplainThumbUrl)) {
                        img = ((LimitRes) m_res).mLimitExplainThumbUrl;
                    }
                    m_view.SetImg(img);
                    m_view.setLimitRemainingImg(((LimitRes) m_res).mLimitExplainRemainingThumb);
                }
            }
        } else {
            m_view.SetImgState(RecomDisplayUIV2.IMG_STATE_LOADING);
        }
        if (m_res instanceof LockRes) {
            switch (((LockRes) m_res).m_shareType) {
                case LockRes.SHARE_TYPE_MARKET:
                    m_view.SetWeixinTip(R.string.unlock_share_to_market);
                    m_view.SetBtnState(RecomDisplayUIV2.BTN_STATE_UNLOCK);
                    break;
                case LockRes.SHARE_TYPE_WEIXIN:
                    m_view.SetWeixinTip(R.string.unlock_share_to_weixin);
                    m_view.SetBtnState(RecomDisplayUIV2.BTN_STATE_UNLOCK);
                    break;

                default:
                    m_view.SetWeixinTip(R.string.unlock_share_to_weixin);
                    m_view.SetBtnState(RecomDisplayUIV2.BTN_STATE_DOWNLOAD);
                    break;
            }
        } else if (m_res instanceof RecommendRes) {
            m_view.SetBtnState(RecomDisplayUIV2.BTN_STATE_DOWNLOAD);

            m_themeLockInfo = MgrUtils.unLockTheme(m_res.m_id);
            if (m_themeLockInfo != null) {
                DownloadMgr.getInstance().DownloadRes(m_themeLockInfo, null);
                if (TagMgr.CheckTag(m_context, Tags.THEME_UNLOCK + m_res.m_id) && m_themeLockInfo.m_shareType != LockRes.SHARE_TYPE_NONE) {
                    m_view.SetBtnState(RecomDisplayUIV2.BTN_STATE_UNLOCK);
                }
                switch (m_themeLockInfo.m_shareType)
                {
                    case LockRes.SHARE_TYPE_MARKET:
                        m_view.SetWeixinTip(R.string.unlock_share_to_market);
                        break;
                    case LockRes.SHARE_TYPE_WEIXIN:
                    default:
                        m_view.SetWeixinTip(R.string.unlock_share_to_weixin);
                        break;
                }
            }
        } else if (m_res instanceof LimitRes) {    //限量
            m_view.SetBtnState(RecomDisplayUIV2.BTN_STATE_LIMIT_DOWNLOAD);
        }

        if (m_view != null) {
            if (m_res instanceof LockRes) {
                m_view.SetContent(m_res.m_name, ((LockRes) m_res).m_showContent);
            } else if (m_res instanceof RecommendRes) {
                m_view.SetContent(m_res.m_name, ((RecommendRes) m_res).m_showContent);
            } else if (m_res instanceof LimitRes) {
                m_view.SetContent(((LimitRes) m_res).mLimitExplainTitle, ((LimitRes) m_res).mLimitExplainContent);
            }
        }
    }

	/**
    /**
     *
     * @param bmp
     * @param needGlass 是否需要做毛玻璃
     */
    public void SetBk(Bitmap bmp, boolean needGlass)
    {
        if(m_view != null)
        {
            if(needGlass)
            {
                bmp = filter.fakeGlassBeauty(bmp, 0x33000000);
            }
            m_view.SetBk(bmp);
        }
    }

    public void SetBk(int color) {
        if(m_view != null)
        {
            m_view.SetBk(color);
        }
    }


    public void Create(FrameLayout parent) {
        Create(parent, 0);
    }

    /**
     * @param uiType 0:分享解锁UI 1:限量UI
     */
    public void Create(FrameLayout parent, int uiType) {
        if (m_view != null) {
            m_view.CreateUI(parent, uiType);
            UpdateCredit();
        }
    }

    public void Show(FrameLayout fr) {
        if (m_view != null && !IsShow()) {
            m_view.Show(fr);
        }
    }

    public void Show() {
        if (m_view != null && !IsShow()) {
            m_view.Show();
        }
    }

    public boolean IsShow() {
        boolean out = false;

        if (m_view != null) {
            out = m_view.IsShow();
        }

        return out;
    }

    public void UpdateCredit() {
        m_canClick = true;
        if (!UserMgr.IsLogin(m_context,null)) {
            if (m_view != null) {
                m_view.unLogin();
                return;
            }
        }
        if (m_view != null) {
            String credit = null;
            SettingInfo info = SettingInfoMgr.GetSettingInfo(m_context);
            if (info != null) {
                credit = info.GetPoco2Credit();
            }
            if (credit == null) {
                credit = "";
            }
            m_view.SetCredit(credit);
        }
    }

    public boolean IsRecycle() {
        return m_recycle;
    }

    public void OnCancel(boolean hasAnim) {
        if (m_view != null) {
            m_view.OnCancel(hasAnim);
        }
    }

    /**
     * LockRes和RecommendRes用单个下载，资源素材用数组下载，不然有BUG
     */
    protected static class MyDownloadCallback implements DownloadMgr.Callback2 {
        public RecomDisplayMgr m_thiz;
        public int m_themeID;
        public int m_type;

        public MyDownloadCallback(RecomDisplayMgr thiz, int themeID, int type) {
            m_thiz = thiz;
            m_themeID = themeID;
            m_type = type;
        }

        @Override
        public void OnProgress(int downloadId, IDownload res, int progress) {
        }

        @Override
        public void OnComplete(int downloadId, IDownload res) {
            if (res instanceof LockRes) {
                if (m_thiz != null) {
                    m_thiz.m_view.SetImgState(RecomDisplayUIV2.IMG_STATE_COMPLETE);
                    Object img = ((LockRes) res).m_showImg;
                    if (img == null && !TextUtils.isEmpty(((LockRes) res).url_showImg)) {
                        img = ((LockRes) res).url_showImg;
                    }
                    m_thiz.m_view.SetImg(img);
                }
            } else if (res instanceof RecommendRes) {
                if (m_thiz != null) {
                    m_thiz.m_view.SetImgState(RecomDisplayUIV2.IMG_STATE_COMPLETE);
                    Object img = ((RecommendRes) res).m_showImg;
                    if (img == null && !TextUtils.isEmpty(((RecommendRes) res).url_showImg)) {
                        img = ((RecommendRes) res).url_showImg;
                    }
                    m_thiz.m_view.SetImg(img);
                }
            } else if (res instanceof LimitRes) {
                if (m_thiz != null) {
                    m_thiz.m_view.SetImgState(RecomDisplayUIV2.IMG_STATE_COMPLETE);
                    Object img = ((LimitRes) res).mLimitExplainThumb;
                    if (img == null && !TextUtils.isEmpty(((LimitRes) res).mLimitExplainThumbUrl)) {
                        img = ((LimitRes) res).mLimitExplainThumbUrl;
                    }
                    m_thiz.m_view.SetImg(img);
                    m_thiz.m_view.setLimitRemainingImg(((LimitRes) res).mLimitExplainRemainingThumb);
                }
            }
        }

        @Override
        public void OnFail(int downloadId, IDownload res) {
        }

        @Override
        public void OnGroupComplete(int downloadId, IDownload[] resArr) {
            //添加到显示列表和new
            if (m_type == ResType.FRAME.GetValue()) {
                /*ArrayList<Integer> ids = new ArrayList<Integer>();
				if(resArr != null)
				{
					Object obj;
					for(int i = 0; i < resArr.length; i++)
					{
						obj = resArr[i];
						if(obj instanceof FrameRes)
						{
							ids.add(((FrameRes)obj).m_id);
						}
					}
				}
				int len = ids.size();
				if(len > 0)
				{
					int[] is = new int[len];
					for(int i = 0; i < len; i++)
					{
						is[i] = ids.get(i);
					}
					ResourceMgr.AddFrameId(is);
					ResourceMgr.AddFrameNewFlag(PocoCamera.main, is);
				}*/
                if (m_thiz == null) return;

                FrameResMgr2.getInstance().AddGroupId(m_themeID);
                FrameResMgr2.getInstance().AddGroupNewFlag(m_thiz.getContext(), m_themeID);

                if (m_thiz != null && m_thiz.m_view != null) {
                    if (resArr != null && resArr.length > 0 && resArr[0] != null) {
                        m_thiz.m_view.AddCredit(m_thiz.getContext(), m_type, m_themeID, ((BaseRes) resArr[0]).m_id);
                    }
                }

                //清理推荐
                ClearRecomFrameFlag(m_thiz.getContext(), m_themeID);
            } else if (m_type == ResType.FRAME2.GetValue()) {

                if (m_thiz == null) return;

                ArrayList<Integer> ids = new ArrayList<Integer>();
                if (resArr != null) {
                    for (IDownload obj : resArr) {
                        if (obj instanceof FrameExRes) {
                            ids.add(((FrameExRes) obj).m_id);
                        }
                    }
                }
                int len = ids.size();
                int[] is = null;
                if (len > 0) {
                    is = new int[len];
                    for (int i = 0; i < len; i++) {
                        is[i] = ids.get(i);
                    }
                }
                if (is != null) {
                    FrameExResMgr2.getInstance().AddId(is);
                    FrameExResMgr2.getInstance().AddNewFlag(m_thiz.getContext(), is);
                }

                if (m_thiz != null && m_thiz.m_view != null) {
                    if (resArr != null && resArr.length > 0 && resArr[0] != null) {
                        m_thiz.m_view.AddCredit(m_thiz.getContext(), m_type, m_themeID, ((BaseRes) resArr[0]).m_id);
                    }
                }

                //清理推荐
                ClearRecomSimpleFrameFlag(m_thiz.getContext(), m_themeID);
            } else if (m_type == ResType.BRUSH.GetValue()) {
                if (m_thiz == null) return;

                BrushResMgr2.getInstance().AddGroupId(m_themeID);
                BrushResMgr2.getInstance().AddGroupNewFlag(m_thiz.getContext(), m_themeID);

                if (m_thiz != null && m_thiz.m_view != null) {
                    if (resArr != null && resArr.length > 0 && resArr[0] != null) {
                        m_thiz.m_view.AddCredit(m_thiz.getContext(), m_type, m_themeID, ((BaseRes) resArr[0]).m_id);
                    }
                }
                //清理推荐
                ClearRecomBrushFlag(m_thiz.getContext(), m_themeID);
            } else if (m_type == ResType.MAKEUP_GROUP.GetValue()) {
				/*ArrayList<Integer> ids = new ArrayList<Integer>();
				if(resArr != null)
				{
					Object obj;
					for(int i = 0; i < resArr.length; i++)
					{
						obj = resArr[i];
						if(obj instanceof MakeupRes)
						{
							ids.add(((MakeupRes)obj).m_id);
						}
					}
				}
				int len = ids.size();
				if(len > 0)
				{
					int[] is = new int[len];
					for(int i = 0; i < len; i++)
					{
						is[i] = ids.get(i);
					}
					ResourceMgr.AddMakeupGroupId(m_themeID);
					ResourceMgr.AddMakeupComboNewFlag(PocoCamera.main, is);
				}*/
                if (m_thiz == null) return;

                MakeupComboResMgr2.getInstance().AddId(m_themeID);
                MakeupComboResMgr2.getInstance().AddNewFlag(m_thiz.getContext(), m_themeID);

                if (m_thiz != null && m_thiz.m_view != null) {
                    if (resArr != null && resArr.length > 0 && resArr[0] != null) {
                        m_thiz.m_view.AddCredit(m_thiz.getContext(), m_type, m_themeID, ((BaseRes) resArr[0]).m_id);
                    }
                }
                //清理推荐
                ClearRecomMakeupFlag(m_thiz.getContext(), m_themeID);
            } else if (m_type == ResType.MOSAIC.GetValue()) {
                if (m_thiz == null) return;

                ArrayList<Integer> ids = new ArrayList<Integer>();
                if (resArr != null) {
                    Object obj;
                    for (IDownload aResArr : resArr)
                    {
                        obj = aResArr;
                        if (obj instanceof MosaicRes)
                        {
                            ids.add(((MosaicRes) obj).m_id);
                        }
                    }
                }
                int len = ids.size();
                if (len > 0) {
                    int[] is = new int[len];
                    for (int i = 0; i < len; i++) {
                        is[i] = ids.get(i);
                    }

                    MosaicResMgr2.getInstance().AddId(is);
                    MosaicResMgr2.getInstance().AddNewFlag(m_thiz.getContext(), is);
                }

                if (m_thiz != null && m_thiz.m_view != null) {
                    if (resArr != null && resArr.length > 0 && resArr[0] != null) {
                        m_thiz.m_view.AddCredit(m_thiz.getContext(), m_type, m_themeID, ((BaseRes) resArr[0]).m_id);
                    }
                }
                //清理推荐
                ClearRecomMosaicFlag(m_thiz.getContext(), m_themeID);
            } else if (m_type == ResType.GLASS.GetValue()) {
                if (m_thiz == null) return;

                ArrayList<Integer> ids = new ArrayList<>();
                if (resArr != null) {
                    Object obj;
                    for (IDownload aResArr : resArr)
                    {
                        obj = aResArr;
                        if (obj instanceof GlassRes)
                        {
                            ids.add(((GlassRes) obj).m_id);
                        }
                    }
                }
                int len = ids.size();
                if (len > 0) {
                    int[] is = new int[len];
                    for (int i = 0; i < len; i++) {
                        is[i] = ids.get(i);
                    }

                    GlassResMgr2.getInstance().AddId(is);
                    GlassResMgr2.getInstance().AddNewFlag(m_thiz.getContext(), is);
                }

                if (m_thiz != null && m_thiz.m_view != null) {
                    if (resArr != null && resArr.length > 0 && resArr[0] != null) {
                        m_thiz.m_view.AddCredit(m_thiz.getContext(), m_type, m_themeID, ((BaseRes) resArr[0]).m_id);
                    }
                }

                //清理推荐
                ClearRecomGlassFlag(m_thiz.getContext(), m_themeID);
            }else if (m_type == ResType.DECORATE.GetValue()) {

                if (m_thiz == null) return;

                DecorateResMgr2.getInstance().AddGroupId(m_themeID);
                DecorateResMgr2.getInstance().AddGroupNewFlag(m_thiz.getContext(), m_themeID);

                if (m_thiz != null && m_thiz.m_view != null) {
                    if (resArr != null && resArr.length > 0 && resArr[0] != null) {
                        m_thiz.m_view.AddCredit(m_thiz.getContext(), m_type, m_themeID, ((BaseRes) resArr[0]).m_id);
                    }
                }

                //清理推荐
                ClearRecomDecorateFlag(m_thiz.getContext(), m_themeID);
            }else if (m_type == ResType.FILTER.GetValue()) {
                if (m_thiz == null) return;

                FilterResMgr2.getInstance().AddGroupId(m_themeID);
                FilterResMgr2.getInstance().AddGroupNewFlag(m_thiz.getContext(), m_themeID);

                if (m_thiz != null && m_thiz.m_view != null) {
                    if (resArr != null && resArr.length > 0 && resArr[0] != null) {
                        m_thiz.m_view.AddCredit(m_thiz.getContext(), m_type, m_themeID, ((BaseRes) resArr[0]).m_id);
                    }
                }

                //清理推荐
                ClearRecomFilterFlag(m_thiz.getContext(), m_themeID);
            }

            if (m_thiz != null && m_thiz.m_view != null) {
                m_thiz.m_view.OnCancel(true);
            }
        }

        @Override
        public void OnGroupFail(int downloadId, IDownload[] resArr) {
            if (m_thiz != null && m_thiz.m_view != null) {
                m_thiz.m_view.SetBtnState(RecomDisplayUIV2.BTN_STATE_DOWNLOAD);
            }
        }

        @Override
        public void OnGroupProgress(int downloadId, IDownload[] resArr, int progress) {
        }

        public void ClearAll() {
            m_thiz = null;
        }
    }

    protected static class MyWXCallback implements SendWXAPI.WXCallListener {
        public RecomDisplayMgr m_thiz;
        public int m_themeID;
        public int m_type;
        public boolean mIsSharing;

        public MyWXCallback(RecomDisplayMgr thiz, int themeID, int type) {
            m_thiz = thiz;
            m_themeID = themeID;
            m_type = type;
            mIsSharing = false;
        }

        @Override
        public void onCallFinish(int result) {
            mIsSharing = false;
            if (result != BaseResp.ErrCode.ERR_USER_CANCEL) {
                if (m_type == ResType.FRAME.GetValue() || m_type == ResType.FRAME2.GetValue() || m_type == ResType.MAKEUP_GROUP.GetValue() || m_type == ResType.MOSAIC.GetValue() || m_type == ResType.GLASS.GetValue() || m_type == ResType.BRUSH.GetValue()) {
                    ClearThemeLockFlag(m_thiz.getContext(), m_themeID);
                }

                if (m_thiz != null && m_thiz.m_view != null) {
                    m_thiz.m_view.SetBtnState(RecomDisplayUIV2.BTN_STATE_DOWNLOAD);
                    if (m_thiz.m_recomcb != null) {
                        m_thiz.m_recomcb.OnBtn(RecomDisplayUIV2.BTN_STATE_DOWNLOAD, true);
                    }
                    if (m_thiz.m_cb != null) {
                        m_thiz.m_cb.UnlockSuccess(m_thiz.m_res);
                    }
                }
            }
            else if (result == BaseResp.ErrCode.ERR_USER_CANCEL && m_thiz !=  null && m_thiz.m_cb instanceof ExCallback)
            {
                ((ExCallback) m_thiz.m_cb).onWXCancel();
            }
        }

        public void ClearAll() {
            m_thiz = null;
            mIsSharing = false;
        }
    }

    public static class CreditCallback implements Credit.Callback {
        public RecomDisplayMgr m_thiz;
        public int m_themeID;
        public int m_type;

        public CreditCallback(RecomDisplayMgr thiz, int themeID, int type) {
            m_thiz = thiz;
            m_themeID = themeID;
            m_type = type;
        }

        @Override
        public void OnSuccess(String msg) {
            if (m_type == ResType.FRAME.GetValue() || m_type == ResType.FRAME2.GetValue() || m_type == ResType.MAKEUP_GROUP.GetValue() || m_type == ResType.MOSAIC.GetValue() || m_type == ResType.GLASS.GetValue() || m_type == ResType.BRUSH.GetValue() || m_type == ResType.FILTER.GetValue()) {
                ClearThemeLockFlag(m_thiz.getContext(), m_themeID);
            }

            if (m_thiz != null && m_thiz.m_view != null) {
                m_thiz.UpdateCredit();
                m_thiz.m_view.SetBtnState(RecomDisplayUIV2.BTN_STATE_DOWNLOAD);
                if (m_thiz.m_recomcb != null) {
                    m_thiz.m_recomcb.OnBtn(RecomDisplayUIV2.BTN_STATE_DOWNLOAD, true);
                }
                if (m_thiz.m_cb != null) {
                    m_thiz.m_cb.UnlockSuccess(m_thiz.m_res);
                }
            }
        }

        @Override
        public void OnFailed(String msg) {
            if (m_thiz != null) {
                m_thiz.m_view.showToast(msg);
                m_thiz.m_canClick = true;
            }
        }

        public void ClearAll() {
            m_thiz = null;
        }
    }

    /**
     * 清理主题锁
     *
     * @param themeID
     */
    public static void ClearThemeLockFlag(Context context, int themeID) {
        TagMgr.SetTag(context, Tags.THEME_UNLOCK + themeID);
        TagMgr.Save(context);
    }

    /**
     * 清理推荐标志
     *
     * @param themeID
     */
    public static void ClearRecomMakeupFlag(Context context, int themeID) {
        TagMgr.SetTag(context, Tags.BEAUTY_RECOMMEND_MAKEUPCOMBO + themeID);
        TagMgr.Save(context);
    }

    /**
     * 清理推荐标志
     *
     * @param themeID
     */
    public static void ClearRecomMosaicFlag(Context context, int themeID) {
        TagMgr.SetTag(context, Tags.ADV_RECO_MOSAIC_FLAG + themeID);
        TagMgr.Save(context);
    }

    /**
     * 清理推荐标志
     *
     * @param themeID
     */
    public static void ClearRecomGlassFlag(Context context, int themeID) {
        TagMgr.SetTag(context, Tags.ADV_RECO_GLASS_FLAG + themeID);
        TagMgr.Save(context);
    }

    /**
     * 清理推荐标志
     *
     * @param themeID
     */
    public static void ClearRecomFrameFlag(Context context, int themeID) {
        TagMgr.SetTag(context, Tags.ADV_RECO_FRAME_FLAG + themeID);
        TagMgr.Save(context);
    }

    /**
     * 清理推荐标志
     *
     * @param themeID
     */
    public static void ClearRecomSimpleFrameFlag(Context context, int themeID) {
        TagMgr.SetTag(context, Tags.ADV_RECO_SIMPLE_FRAME_FLAG + themeID);
        TagMgr.Save(context);
    }

    /**
     * 清理推荐标志
     *
     * @param themeID
     */
    public static void ClearRecomBrushFlag(Context context, int themeID) {
        TagMgr.SetTag(context, Tags.ADV_RECO_BRUSH_FLAG + themeID);
        TagMgr.Save(context);
    }

    public static void ClearRecomDecorateFlag(Context context, int themeID) {
        TagMgr.SetTag(context, Tags.ADV_RECO_PENDANT_DOWNLOAD_ID + themeID);
        TagMgr.Save(context);
    }

    public static void ClearRecomFilterFlag(Context context, int themeID) {
        TagMgr.SetTag(context, Tags.ADV_RECO_FILTER_FLAG + themeID);
        TagMgr.Save(context);
    }

    public static void OpenMarket(Context context, int resType, int themeID) {
        try {
            Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Throwable e) {
            Toast.makeText(context, R.string.not_installed_app_store, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        if (resType == ResType.FRAME.GetValue() || resType == ResType.FRAME2.GetValue() || resType == ResType.MAKEUP_GROUP.GetValue() || resType == ResType.MOSAIC.GetValue() || resType == ResType.GLASS.GetValue() || resType == ResType.BRUSH.GetValue() || resType == ResType.FILTER.GetValue()) {
            ClearThemeLockFlag(context, themeID);
        }
    }

    public static Bitmap MakeWXLogo(Context context, Object res) {
        Bitmap out = null;

        if (res != null) {
            out = cn.poco.imagecore.Utils.DecodeImage(context, res, 0, -1, -1, -1);
        }
        if (out == null) {
            out = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        }
        if (out != null) {
            if (out.getWidth() > 180 || out.getHeight() > 180) {
                out = MakeBmp.CreateBitmap(out, 180, 180, -1, 0, Config.ARGB_8888);
            }
        }

        return out;
    }

    public void ClearAllaa() {

        m_themeLockInfo = null;
        m_context = null;
        m_recycle = true;

        ClearCB();
        if(m_view != null)
        {
            m_view.ClearAll();
            m_view = null;
        }
    }
}
