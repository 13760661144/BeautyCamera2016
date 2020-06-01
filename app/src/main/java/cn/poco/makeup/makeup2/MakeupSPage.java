package cn.poco.makeup.makeup2;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import cn.poco.advanced.ImageUtils;
import cn.poco.beautify.EffectType;
import cn.poco.beautify.ImageProcessor;
import cn.poco.beautify.SonWindow;
import cn.poco.beautify4.Beautify4Page;
import cn.poco.camera.ImageFile2;
import cn.poco.camera.RotationImg2;
import cn.poco.camera3.ui.FixPointView;
import cn.poco.cloudalbumlibs.utils.CloudAlbumDialog;
import cn.poco.dynamicSticker.StickerType;
import cn.poco.face.FaceDataV2;
import cn.poco.face.MakeupAlpha;
import cn.poco.filter4.WatermarkAdapter;
import cn.poco.filter4.WatermarkItem;
import cn.poco.filterPendant.MyStatusButton;
import cn.poco.framework.BaseSite;
import cn.poco.framework.FileCacheMgr;
import cn.poco.framework.IPage;
import cn.poco.image.PocoFaceInfo;
import cn.poco.image.filter;
import cn.poco.image.filterori;
import cn.poco.makeup.MakeupUIHelper;
import cn.poco.makeup.MySeekBar;
import cn.poco.makeup.SeekBarTipsView;
import cn.poco.makeup.site.MakeupSPageSite;
import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.BaseExAdapter;
import cn.poco.resource.MakeupRes;
import cn.poco.resource.MakeupType;
import cn.poco.resource.WatermarkResMgr2;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.system.SysConfig;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.FullScreenDlg;
import cn.poco.tianutils.MakeBmp;
import cn.poco.tianutils.MakeBmpV2;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.CommonUI;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.utils.PhotoMark;
import cn.poco.utils.Utils;
import cn.poco.utils.WaitAnimDialog;
import cn.poco.view.beauty.BeautyCommonViewEx;
import cn.poco.view.beauty.MakeUpViewEx;
import cn.poco.view.beauty.MakeUpViewEx1;
import cn.poco.widget.recycle.RecommendExAdapter;
import my.beautyCamera.R;

import static cn.poco.face.FaceDataV2.RAW_POS_MULTI;
import static cn.poco.face.FaceDataV2.sFaceIndex;

/**
 * 一键萌妆页面
 */
public class MakeupSPage extends IPage {
    public static final String BACKFLAG = "backflag";
    public static final String BACKDATA = "backData";
    public static final String BACKISSHOULDADDEFFECT = "backisShouldAddEffect";
    public static final int FACECHECK = 1;
    public static final int MAKEUP2 = 2;
    public static final int BEAUTIFY = 3;
    public static final int SAVE = 4;

    public static final int CHANGEPOINT_THREE = 101;
    public static final int CHANGEPOINT_MANY = 102;
    public static final int NORMOL = 103;
    public int m_curMode = NORMOL;
    private MakeupSPageSite m_site;
    private MakeUpViewEx1 m_view;
    private LinearLayout m_bottomFr;
    private FrameLayout m_bottomBar;
    private FrameLayout m_bottomList;
    private ImageView m_backBtn;
    private ImageView m_okBtn;
    private MyStatusButton m_titleBtn;

    protected MakeupUIHelper.ChangePointFr m_changePointFr;//定点界面
//    protected FrameLayout mFixView;
    private FixPointView mFixView;
    protected FrameLayout m_multifaceFr;
    protected ImageView m_multifaceBtn;//多人脸选择
    protected TextView m_multifaceTips;
//    protected ImageView m_checkBtn;//定点按钮
    protected ImageView m_compareBtn;//对比按钮

    private boolean m_uiEnabled;
    private int m_frW;
    private int m_frH;
    private int m_bottomBarHeight;
    private int m_bottomListHeight;
    private int mBottomLayoutHeight;
    private int DEF_IMG_SIZE;
    private int SHOW_IMG_SIZE;//预览的时候用小图预览，因为大图预览做效果太慢
    private int m_finalFrH;
    private Object m_imgInfo;
    private Bitmap m_org;
    private Bitmap m_curBmp;
    private boolean isFold = false;
    private HandlerThread m_thread;
    private Handler m_handler;
    private Handler m_UIhanlder;
    private FaceDataBackups m_faceDataBackups = new FaceDataBackups();
    protected WaitAnimDialog m_waitDlg;

    private ArrayList<RecommendExAdapter.ItemInfo> m_ListInfoArr;

    private boolean m_partUIEnable = true;
    private boolean m_isShouldAddEffect = true;
    private Bitmap m_beautifyBmp;

    private Makeup2ListConfig m_makeup2Config = new Makeup2ListConfig(getContext());
    private Makeup2RecyclerView m_makeupList;
    private Makeup2Adapter m_makeup2Adapter;
    private SeekBarTipsView m_seekBarTips;
    private SonWindow m_sonWin;

    private boolean m_isComeFromBeautifyPage = false;//判断是否是从美化页的一键萌妆按钮进入
    private int m_imgH;
    private int m_viewH;
    private Makeup2LocalData m_Makeup2LocalData;
    private boolean m_isBack = false;//定制流程判断是否返回操作，正常美化流程没用到，可以忽略。
    private Makeup2LocalData m_backData;
    private boolean m_ischangePointAnimResetStart = false;
    private boolean m_bmpIsHasEffect = false;
    private boolean m_addDataMark = false;//保存画水印时是否要预留画时间的位置
    private boolean m_isHasWaterMark = false;

    private boolean m_isChange = false;
    private CloudAlbumDialog m_exitDialog;

    private BtnOnclickAnimaListener m_BtnOnclickAnimaListener;

    //水印标识alpha动画
    private boolean isDoWaterAlphaAnim = true;

    public MakeupSPage(Context context, BaseSite site) {
        super(context, site);
        m_site = (MakeupSPageSite) site;
        initData();
        initUI();
        MyBeautyStat.onPageStartByRes(R.string.美颜美图_一键萌妆页面_主页面);
    }

    /**
     * @param params
     */
    @Override
    public void SetData(HashMap<String, Object> params) {
        if (params != null) {

            if(params.get(Beautify4Page.PAGE_ANIM_IMG_H) != null)
            {
                m_imgH = (int) params.get(Beautify4Page.PAGE_ANIM_IMG_H);
            }

            if(params.get(Beautify4Page.PAGE_ANIM_VIEW_H) != null)
            {
                m_viewH = (int) params.get(Beautify4Page.PAGE_ANIM_VIEW_H);
            }

            if(m_imgH > 0 && m_viewH > 0)
            {
                m_isComeFromBeautifyPage = true;
            }

            if(params.get(BACKFLAG) != null && params.get(BACKFLAG) instanceof Boolean)
            {
                m_isBack = (boolean) params.get(BACKFLAG);
            }

            if(params.get(BACKDATA) != null && params.get(BACKDATA) instanceof Makeup2LocalData)
            {
                m_backData = (Makeup2LocalData) params.get(BACKDATA);
            }

//            if(params.get(BACKISSHOULDADDEFFECT) != null && params.get(BACKISSHOULDADDEFFECT) instanceof Boolean)
//            {
//                m_isShouldAddEffect = (boolean) params.get(BACKISSHOULDADDEFFECT);
//            }
                   Object o = params.get("add_date");
                    if(o != null && o instanceof Boolean)
                    {
                        m_addDataMark = (boolean)o;
                    }


            if (params.get("imgs") != null)
            {
                setImg(params.get("imgs"));
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private void initData() {
        m_uiEnabled = true;
        //m_frW = ShareData.m_screenWidth;
        //m_frH = m_frW * 4 / 3;

        m_frW = ShareData.m_screenWidth;
        m_frW -= m_frW % 2;
        m_frH = ShareData.m_screenHeight - ShareData.PxToDpi_xhdpi(320);
        m_frH -= m_frH % 2;

        m_bottomBarHeight = ShareData.PxToDpi_xhdpi(88);
        m_bottomListHeight = ShareData.PxToDpi_xhdpi(232);
        mBottomLayoutHeight = ShareData.PxToDpi_xhdpi(232);

        DEF_IMG_SIZE = SysConfig.GetPhotoSize(getContext());
        //SHOW_IMG_SIZE = ShareData.m_screenWidth * 4 / 3;

        SHOW_IMG_SIZE = m_frH;

        m_BtnOnclickAnimaListener = new BtnOnclickAnimaListener();


        //m_frW += 2;//为了去白边
        m_thread = new HandlerThread("makeup2");
        m_thread.start();
        m_UIhanlder = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg != null) {
                    switch (msg.what) {
                        case FACECHECK: {
                            if(m_isShouldAddEffect)
                            {
                                Message message = Message.obtain();
                                message.what = BEAUTIFY;
                                message.obj = m_org;
                                m_handler.sendMessage(message);
                            }
                            else
                            {
                                if (FaceDataV2.RAW_POS_MULTI != null) {
                                    if (FaceDataV2.CHECK_FACE_SUCCESS) {
                                        if (FaceDataV2.RAW_POS_MULTI.length > 1) {
                                            ShowMultiFacesTips();
                                            m_view.setMode(BeautyCommonViewEx.MODE_SEL_FACE);
                                            m_partUIEnable = false;
                                        } else {
                                            sFaceIndex = 0;
                                            m_view.m_faceIndex = 0;
                                            showOtherBtn();
                                            resetOldData();
                                        }
                                        if(mFixView != null)
                                        {
                                            mFixView.showJitterAnimAccordingStatus();
                                        }
                                    } else {
                                        MakeNoFaceHelp();
                                        if (m_noFaceHelpFr != null) {
                                            m_noFaceHelpFr.show();
                                        }
                                    }
                                }
                                if(m_Makeup2LocalData == null)
                                {
                                    if(FaceDataV2.RAW_POS_MULTI != null)
                                    {
                                        m_Makeup2LocalData = new Makeup2LocalData(FaceDataV2.RAW_POS_MULTI.length);
                                    }
                                    else
                                    {
                                        m_Makeup2LocalData = new Makeup2LocalData(1);
                                    }
                                    m_Makeup2LocalData.m_waterMarkId = SettingInfoMgr.GetSettingInfo(getContext()).GetPhotoWatermarkId(
                                            WatermarkResMgr2.getInstance().GetDefaultWatermarkId(getContext()));
                                }
                                SetWaitUI(false, "");
                                m_uiEnabled = true;
                            }
                            break;
                        }
                        case BEAUTIFY:
                        {
                            if(msg.obj != null)
                            {
                                m_beautifyBmp = (Bitmap) msg.obj;
                                if(m_beautifyBmp != null)
                                {
                                    m_view.setImage(m_beautifyBmp);
                                }
                            }

                            if(m_isBack)
                            {
                                resetOldData();
                                showOtherBtn();
                                return;
                            }

                            if (FaceDataV2.RAW_POS_MULTI != null) {
                                if (FaceDataV2.CHECK_FACE_SUCCESS) {
                                    if (FaceDataV2.RAW_POS_MULTI.length > 1) {
                                        ShowMultiFacesTips();
                                        m_view.setMode(BeautyCommonViewEx.MODE_SEL_FACE);
                                        m_partUIEnable = false;
                                    } else {
                                        sFaceIndex = 0;
                                        showOtherBtn();
                                        resetOldData();
                                    }
                                } else {
                                    MakeNoFaceHelp();
                                    if (m_noFaceHelpFr != null) {
                                        m_noFaceHelpFr.show();
                                    }
                                }
                            }
                            if(m_Makeup2LocalData == null)
                            {
                                m_Makeup2LocalData = new Makeup2LocalData(FaceDataV2.RAW_POS_MULTI.length);
                                m_Makeup2LocalData.m_waterMarkId = SettingInfoMgr.GetSettingInfo(getContext()).GetPhotoWatermarkId(
                                        WatermarkResMgr2.getInstance().GetDefaultWatermarkId(getContext()));
                            }
                            SetWaitUI(false, "");
                            m_uiEnabled = true;
                            m_isChange = true;
                            break;
                        }
                        case MAKEUP2: {
                            if (msg.obj != null) {
                                Bitmap temp = (Bitmap) msg.obj;
                                if (temp != null) {
                                    m_view.setImage(temp);
                                }
                            }

                            if(m_bmpIsHasEffect && !m_isHasWaterMark)
                            {
                                m_isHasWaterMark = true;
                                //加水印
                                m_view.setDrawWaterMark(true);
                                WatermarkItem watermarkItem = null;
                                if(mWatermarkResArr != null && mWatermarkResArr.size() > 0)
                                {
                                    for(int i = 0, size = mWatermarkResArr.size(); i < size; i++)
                                    {
                                        if(mWatermarkResArr.get(i) != null && mWatermarkResArr.get(i).mID == m_Makeup2LocalData.m_waterMarkId)
                                        {
                                            watermarkItem = mWatermarkResArr.get(i);
                                        }
                                    }
                                }

                                if(watermarkItem != null)
                                {
                                    if (isDoWaterAlphaAnim)
                                    {
                                        m_view.AddWaterMarkWithAnim(MakeBmpV2.DecodeImage(getContext(), watermarkItem.res, 0, -1, -1, -1, Bitmap.Config.ARGB_8888), watermarkItem.mID == WatermarkResMgr2.getInstance().GetNonWatermarkId(getContext()));
                                    }
                                    else
                                    {
                                        m_view.AddWaterMark(MakeBmpV2.DecodeImage(getContext(), watermarkItem.res, 0, -1, -1, -1, Bitmap.Config.ARGB_8888), watermarkItem.mID == WatermarkResMgr2.getInstance().GetNonWatermarkId(getContext()));
                                    }
                                    isDoWaterAlphaAnim = false;
                                }
                            }
                            SetWaitUI(false, "");
                            m_uiEnabled = true;
                            m_isChange = true;
                            setScaleAnim(m_compareBtn,false);
                            break;
                        }
                        case SAVE: {
                            if (msg.obj != null)
                            {
                                HashMap<String, Object> params = new HashMap<>();
                                String path = FileCacheMgr.GetLinePath();
                                Bitmap bmp = (Bitmap) msg.obj;
                                if(m_isComeFromBeautifyPage)
                                {
                                    params.put("img",bmp);
                                    params.putAll(getBackAnimParam());
                                    if(m_bmpIsHasEffect)
                                    {
                                        params.put("has_water_mark",false);
                                        params.put("water_mark_id",-1);
                                    }
                                }
                                else
                                {
                                    if (Utils.SaveTempImg(bmp, path))
                                    {
                                        params.put("img", path);
                                    }
                                    params.put("hide_button",true);
                                    params.put(BACKDATA,m_Makeup2LocalData);
                                }
                                m_site.onSave(getContext(), params);
                            }
                            if(m_bmpIsHasEffect)
                            {
                                SettingInfoMgr.Save(getContext());
                            }
                            SetWaitUI(false, "");
                            m_uiEnabled = true;
                            break;
                        }
                    }
                }
            }
        };

        m_handler = new Handler(m_thread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg != null) {
                    switch (msg.what) {
                        case FACECHECK: {
                            if (msg.obj != null) {
                                FaceDataV2.CheckFace(getContext(), (Bitmap) msg.obj);
                                Message tempMsg = Message.obtain();
                                tempMsg.what = FACECHECK;
                                m_UIhanlder.sendMessage(tempMsg);
                            }
                            break;
                        }
                        case BEAUTIFY:
                        {
                            if(msg.obj != null && msg.obj instanceof Bitmap)
                            {
                                Bitmap bmp = (Bitmap) msg.obj;
                                Bitmap tempBmp = ImageProcessor.ConversionImgColorNew(getContext(), false, bmp.copy(Bitmap.Config.ARGB_8888,true), EffectType.EFFECT_NEWBEE);
                                bmp = ImageProcessor.DrawMask2(bmp.copy(Bitmap.Config.ARGB_8888, true), tempBmp,75);
                                if(tempBmp != null)
                                {
                                    tempBmp = null;
                                }
                                Message message = Message.obtain();
                                message.what = BEAUTIFY;
                                message.obj = bmp;
                                m_UIhanlder.sendMessage(message);
                            }
                            break;
                        }
                        case MAKEUP2: {
                            Bitmap out = null;
                            Bitmap tempBmp = null;
                            if (msg.obj != null && msg.obj instanceof Makeup2Msg) {
                                Makeup2Msg item = (Makeup2Msg) msg.obj;
                                if (item.m_bmp != null && !item.m_bmp.isRecycled()) {
                                    tempBmp = item.m_bmp.copy(Bitmap.Config.ARGB_8888, true);
                                }
                                Makeup2LocalData localData = null;
                                if(item.m_loacalData != null)
                                {
                                    localData = item.m_loacalData;
                                }
                                if(localData != null)
                                {
                                    //彩妆
                                    {
                                        for (int i = 0; i < RAW_POS_MULTI.length; i++)
                                        {
                                            float alphaScale = localData.m_asetAlphas[i]/100f;
                                            ArrayList<MakeupRes.MakeupData> datas = localData.m_makeupDataArr[i];
                                            MakeupAlpha ma = localData.m_makeupAlphaArr[i];
                                            out = ImageProcessor.DoMakeup(getContext(), i, tempBmp, datas, new int[]{(int) (ma.m_eyebrowAlpha * alphaScale + 0.5f), (int) (ma.m_eyeAlpha * alphaScale + 0.5f), (int) (ma.m_kohlAlpha * alphaScale + 0.5f), (int) (ma.m_eyelashUpAlpha * alphaScale + 0.5f), (int) (ma.m_eyelashDownAlpha * alphaScale + 0.5f), (int) (ma.m_eyelineUpAlpha * alphaScale + 0.5f), (int) (ma.m_eyelineDownAlpha * alphaScale + 0.5f), (int) (ma.m_cheekAlpha * alphaScale + 0.5f), (int) (ma.m_lipAlpha * alphaScale + 0.5f), (int) (ma.m_foundationAlpha * alphaScale + 0.5f)});
                                        }
                                    }

                                    //变形
                                    {
                                            for(int i = 0; i < FaceDataV2.RAW_POS_MULTI.length; i++)
                                            {
                                                if(m_Makeup2LocalData.m_shapeTypes[i] != -1)
                                                {
                                                    out = filter.ShapeAll(out, FaceDataV2.RAW_POS_MULTI[i], m_Makeup2LocalData.m_shapeTypes[i]);
                                                }
                                            }
                                    }
                                    //加贴纸
                                    {
                                        if (m_Makeup2LocalData.m_DecalResArr != null && m_Makeup2LocalData.m_DecalResArr.length > 0 && out != null && !out.isRecycled())
                                        {
                                            addOther(out, m_Makeup2LocalData.m_DecalResArr);
                                        }
                                    }

                                    Message uiMsg = Message.obtain();
                                    if(out != null && !out.isRecycled())
                                    {
                                        m_curBmp = out;
                                        uiMsg.obj = out;
                                    }
                                    else
                                    {
                                        uiMsg.obj = item.m_bmp;
                                    }
                                    uiMsg.what = MAKEUP2;
                                    m_UIhanlder.sendMessage(uiMsg);
                                }
                            }
                            break;
                        }
                        case SAVE:
                        {
                            Bitmap out = null;
                            if(msg.obj instanceof Save2Msg)
                            {
                                Save2Msg save2Msg = (Save2Msg) msg.obj;
                                Makeup2LocalData localData = null;
                                if(save2Msg.m_loacalData != null)
                                {
                                    localData = save2Msg.m_loacalData;
                                }
                                Bitmap tempBmp = decodeBmp(save2Msg.m_imgInfo);
                                if(localData != null && tempBmp != null && !tempBmp.isRecycled())
                                {
                                    //彩妆
                                    {
                                        for (int i = 0; i < RAW_POS_MULTI.length; i++)
                                        {
                                            float alphaScale = localData.m_asetAlphas[i]/100f;
                                            ArrayList<MakeupRes.MakeupData> datas = localData.m_makeupDataArr[i];
                                            MakeupAlpha ma = localData.m_makeupAlphaArr[i];
                                            out = ImageProcessor.DoMakeup(getContext(), i, tempBmp, datas, new int[]{(int) (ma.m_eyebrowAlpha * alphaScale + 0.5f), (int) (ma.m_eyeAlpha * alphaScale + 0.5f), (int) (ma.m_kohlAlpha * alphaScale + 0.5f), (int) (ma.m_eyelashUpAlpha * alphaScale + 0.5f), (int) (ma.m_eyelashDownAlpha * alphaScale + 0.5f), (int) (ma.m_eyelineUpAlpha * alphaScale + 0.5f), (int) (ma.m_eyelineDownAlpha * alphaScale + 0.5f), (int) (ma.m_cheekAlpha * alphaScale + 0.5f), (int) (ma.m_lipAlpha * alphaScale + 0.5f), (int) (ma.m_foundationAlpha * alphaScale + 0.5f)});
                                        }
                                    }

                                    //变形
                                    {
                                        for(int i = 0; i < FaceDataV2.RAW_POS_MULTI.length; i++)
                                        {
                                            if(m_Makeup2LocalData.m_shapeTypes[i] != -1)
                                            {
                                                out = filter.ShapeAll(out, FaceDataV2.RAW_POS_MULTI[i], m_Makeup2LocalData.m_shapeTypes[i]);
                                            }
                                        }
                                    }

                                    //加贴纸
                                    {
                                        if (m_Makeup2LocalData.m_DecalResArr != null && m_Makeup2LocalData.m_DecalResArr.length > 0 && out != null && !out.isRecycled())
                                        {
                                            out = addOther(out, m_Makeup2LocalData.m_DecalResArr);
                                        }
                                    }
                                    //加水印
                                    {
                                        int waterMarkId = save2Msg.m_waterMarkId;
                                        if(waterMarkId != -1)
                                        {
                                            WatermarkItem watermarkItem = WatermarkResMgr2.getInstance().GetWaterMarkById(mWatermarkResArr,waterMarkId);
                                            if (watermarkItem != null && watermarkItem.mID != WatermarkResMgr2.getInstance().GetNonWatermarkId(getContext())) {
                                                Bitmap watermark = MakeBmpV2.DecodeImage(getContext(), watermarkItem.res, 0, -1, -1, -1, Bitmap.Config.ARGB_8888);
                                                PhotoMark.drawWaterMarkLeft(out, watermark, m_addDataMark);
                                                if(watermark != null)
                                                {
                                                    watermark = null;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            Message uiMsg = Message.obtain();
                            uiMsg.what = SAVE;
                            uiMsg.obj = out;
                            m_UIhanlder.sendMessage(uiMsg);
                            break;
                        }
                    }
                }
            }
        };
    }

    private Bitmap decodeBmp(Object params)
    {
        Bitmap out = null;
        if(params instanceof RotationImg2[])
        {
            RotationImg2 imgInfo = ((RotationImg2[]) params)[0];
            out = cn.poco.imagecore.Utils.DecodeFinalImage(getContext(), imgInfo.m_img, imgInfo.m_degree, -1, imgInfo.m_flip, DEF_IMG_SIZE, DEF_IMG_SIZE);
        }
        else if(params instanceof ImageFile2)
        {
            RotationImg2 imgInfo = ((ImageFile2) params).SaveImg2(getContext())[0];
            out = cn.poco.imagecore.Utils.DecodeFinalImage(getContext(), imgInfo.m_img, imgInfo.m_degree, -1, imgInfo.m_flip, DEF_IMG_SIZE, DEF_IMG_SIZE);
        }
        else if(params instanceof Bitmap)
        {
            out = (Bitmap) params;
        }
        return out;
    }

    //定制流程返回用到恢复之前的效果
    private void resetOldData()
    {
        if(m_isBack)
        {
            if(m_backData != null && m_backData.m_faceNum == RAW_POS_MULTI.length)
            {
                m_Makeup2LocalData = m_backData;
                if(m_Makeup2LocalData != null && m_Makeup2LocalData.m_asetUri.length > 0)
                {
                    for(int i = 0; i < m_Makeup2LocalData.m_asetUri.length; i++)
                    {
                        if(m_Makeup2LocalData.m_asetUri[i] != -1)
                        {
                            m_bmpIsHasEffect = true;
                            break;
                        }
                    }
                }
                SendMakeupMsg();
                m_makeup2Adapter.SetSelectByUri(m_Makeup2LocalData.m_asetUri[sFaceIndex]);
            }
            m_isBack = false;
        }
    }

    protected void SendMakeupMsg() {
        SetWaitUI(true, "");
        Makeup2Msg makeupMsg = new Makeup2Msg();
        if(m_beautifyBmp != null && !m_beautifyBmp.isRecycled())
        {
            makeupMsg.m_bmp = m_beautifyBmp;
        }
        else if (m_org != null && !m_org.isRecycled())
        {
            makeupMsg.m_bmp = m_org;
        }
        makeupMsg.m_loacalData = m_Makeup2LocalData;
        Message msg = Message.obtain();
        msg.what = MAKEUP2;
        msg.obj = makeupMsg;
        m_handler.sendMessage(msg);
    }

    protected void SendSaveMsg()
    {
        SetWaitUI(true, "");
        Save2Msg save2Msg = new Save2Msg();
        save2Msg.m_imgInfo = m_imgInfo;
        save2Msg.m_loacalData = m_Makeup2LocalData;
        if(!m_bmpIsHasEffect)
        {
            save2Msg.m_waterMarkId = WatermarkResMgr2.getInstance().GetNonWatermarkId(getContext());
        }
        else
        {
            save2Msg.m_waterMarkId = m_Makeup2LocalData.m_waterMarkId;
        }
        Message msg = Message.obtain();
        msg.what = SAVE;
        msg.obj = save2Msg;
        m_handler.sendMessage(msg);
    }


    private void initUI()
    {
        FrameLayout.LayoutParams fl;
        m_view = new MakeUpViewEx1(getContext(),m_cb);
        m_view.def_fix_face_res = new int[]{R.drawable.beautify_fix_face_eye, R.drawable.beautify_fix_face_eye, R.drawable.beautify_fix_face_mouth};
        m_view.def_fix_eyebrow_res = new int[]{R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point};
        m_view.def_fix_eye_res = new int[]{R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_face_eye};
        m_view.def_fix_cheek_res = new int[]{R.drawable.beautify_fix_point, R.drawable.beautify_fix_point,R.drawable.beautify_fix_point};
        m_view.def_fix_lip_res = new int[]{R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point};
        m_view.def_fix_nose_res = new int[]{R.drawable.beautify_fix_point,R.drawable.beautify_fix_point};
        m_view.def_stroke_width = ShareData.PxToDpi_xhdpi(2);
        if(m_view.def_stroke_width < 1)
        {
            m_view.def_stroke_width = 1;
        }
        m_view.InitFaceRes();
        m_view.m_faceIndex = FaceDataV2.sFaceIndex;

        m_finalFrH = m_frH;
        if((ShareData.m_screenHeight - m_bottomListHeight - m_bottomBarHeight) < m_frH)
        {
            m_finalFrH = (ShareData.m_screenHeight - m_bottomListHeight - m_bottomBarHeight);
        }
        fl = new LayoutParams(m_frW, m_finalFrH);
        fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        m_view.setLayoutParams(fl);
        this.addView(m_view, 0);

        InitListUI();

        m_bottomFr = new LinearLayout(getContext());
        m_bottomFr.setOrientation(LinearLayout.VERTICAL);
        fl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        m_bottomFr.setLayoutParams(fl);
        this.addView(m_bottomFr);

        m_bottomBar = new FrameLayout(getContext());
        fl = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,m_bottomBarHeight);
        fl.gravity = Gravity.CENTER_HORIZONTAL;
        m_bottomBar.setLayoutParams(fl);
        m_bottomBar.setBackgroundColor(0xe6ffffff);
        m_bottomFr.addView(m_bottomBar);
        {
            m_backBtn = new ImageView(getContext());
            m_backBtn.setImageResource(R.drawable.beautify_cancel);
            fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            fl.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
            fl.leftMargin = ShareData.PxToDpi_xhdpi(22);
            m_backBtn.setLayoutParams(fl);
            m_backBtn.setOnClickListener(m_clickListener);
            m_bottomBar.addView(m_backBtn);

            m_okBtn = new ImageView(getContext());
            m_okBtn.setImageResource(R.drawable.beautify_ok);
            fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            fl.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
            fl.rightMargin = ShareData.PxToDpi_xhdpi(24);
            m_okBtn.setLayoutParams(fl);
                m_okBtn.setOnClickListener(m_clickListener);
            m_bottomBar.addView(m_okBtn);
            ImageUtils.AddSkin(getContext(),m_okBtn);

            m_titleBtn = new MyStatusButton(getContext());
            m_titleBtn.setData(R.drawable.beautify_makeups_icon,getResources().getString(R.string.makeups_title));
            m_titleBtn.setBtnStatus(true,false);
            fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            fl.gravity = Gravity.CENTER;
            m_titleBtn.setLayoutParams(fl);
            m_bottomBar.addView(m_titleBtn);
            m_titleBtn.setOnClickListener(m_clickListener);
        }

        mFixView = new FixPointView(getContext());
        fl = new LayoutParams(ShareData.PxToDpi_xhdpi(80), ShareData.PxToDpi_xhdpi(80));
        fl.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        fl.rightMargin = ShareData.PxToDpi_xhdpi(24);
        fl.bottomMargin = m_bottomListHeight + m_bottomBarHeight + ShareData.PxToDpi_xhdpi(22);
        mFixView.setLayoutParams(fl);
        this.addView(mFixView);
//        mFixView.setBackgroundDrawable(new BitmapDrawable(BitmapFactory.decodeResource(getResources(),R.drawable.beautify_white_circle_bg)));
        mFixView.setOnTouchListener(m_BtnOnclickAnimaListener);
        mFixView.setVisibility(GONE);

//        m_checkBtn = new ImageView(getContext());
//        fl = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        fl.gravity = Gravity.CENTER;
//        m_checkBtn.setLayoutParams(fl);
//        mFixView.addView(m_checkBtn);
//        m_checkBtn.setImageResource(R.drawable.beautify_fix_by_hand);
//        ImageUtils.AddSkin(getContext(),m_checkBtn);

        m_multifaceFr = new FrameLayout(getContext());
        fl = new LayoutParams(ShareData.PxToDpi_xhdpi(80), ShareData.PxToDpi_xhdpi(80));
        fl.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        fl.rightMargin = ShareData.PxToDpi_xhdpi(120);
        fl.bottomMargin = m_bottomListHeight + m_bottomBarHeight + ShareData.PxToDpi_xhdpi(24);
        m_multifaceFr.setLayoutParams(fl);
        this.addView(m_multifaceFr);
        m_multifaceFr.setVisibility(GONE);
        m_multifaceFr.setBackgroundDrawable(new BitmapDrawable(BitmapFactory.decodeResource(getResources(),R.drawable.beautify_white_circle_bg)));
        m_multifaceFr.setOnTouchListener(m_BtnOnclickAnimaListener);

        m_multifaceBtn = new ImageView(getContext());
        fl = new LayoutParams(ShareData.PxToDpi_xhdpi(50),ShareData.PxToDpi_xhdpi(50));
        fl.gravity = Gravity.CENTER;
        m_multifaceBtn.setLayoutParams(fl);
        m_multifaceFr.addView(m_multifaceBtn);
        m_multifaceBtn.setImageResource(R.drawable.beautify_makeup_multiface_icon);
        ImageUtils.AddSkin(getContext(),m_multifaceBtn);

        m_bottomList = new FrameLayout(getContext())
        {
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                if(!m_partUIEnable)
                {
                    return true;
                }
                return super.onTouchEvent(event);
            }
        };
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,m_bottomListHeight);
        m_bottomList.setLayoutParams(ll);
        m_bottomFr.addView(m_bottomList);
        {

        }

        m_compareBtn = new AppCompatImageView(getContext())
        {
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN: {
                        MyBeautyStat.onClickByRes(R.string.美颜美图_一键萌装页面_主页面_对比按钮);
                        TongJi2.AddCountByRes(getContext(), R.integer.一键萌妆_对比按钮);
                        m_view.setImage(m_org);
                    }
                    break;
                    case MotionEvent.ACTION_UP:
                        if(m_curBmp != null && !m_curBmp.isRecycled())
                        {
                            m_view.setImage(m_curBmp);
                        }
                        else if(m_beautifyBmp != null && !m_beautifyBmp.isRecycled())
                        {
                            m_view.setImage(m_beautifyBmp);
                        }
                        break;
                }
                return true;
            }
        };
        fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.TOP | Gravity.RIGHT;
        fl.topMargin = ShareData.PxToDpi_xhdpi(10);
        fl.rightMargin = ShareData.PxToDpi_xhdpi(15);
        m_compareBtn.setLayoutParams(fl);
        this.addView(m_compareBtn);
        m_compareBtn.setImageResource(R.drawable.beautify_compare);
        m_compareBtn.setVisibility(GONE);


        m_seekBarTips = new SeekBarTipsView(getContext());
        fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(120),ShareData.PxToDpi_xhdpi(120));
        fl.gravity = Gravity.BOTTOM | Gravity.LEFT;
        m_seekBarTips.setLayoutParams(fl);
        this.addView(m_seekBarTips);
        m_seekBarTips.setVisibility(GONE);

        m_sonWin = new SonWindow(getContext());
        fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ShareData.m_screenWidth/3);
        fl.gravity = Gravity.TOP | Gravity.LEFT;
        m_sonWin.setLayoutParams(fl);
        this.addView(m_sonWin);

            mWaterMarkBottomFr = new LinearLayout(getContext());
            mWaterMarkBottomFr.setOrientation(LinearLayout.VERTICAL);
            mWaterMarkBottomFr.setVisibility(GONE);
            mWaterMarkBottomFr.setClickable(true);
            fl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            fl.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            mWaterMarkBottomFr.setLayoutParams(fl);
            this.addView(mWaterMarkBottomFr);
        {
            {
                FrameLayout mWaterMarkCenterFr = new FrameLayout(getContext());
                mWaterMarkCenterFr.setBackgroundColor(0xffffffff);
                fl = new LayoutParams(LayoutParams.MATCH_PARENT, m_bottomBarHeight); //88px
                fl.gravity = Gravity.CENTER_HORIZONTAL;
                mWaterMarkCenterFr.setLayoutParams(fl);
                mWaterMarkBottomFr.addView(mWaterMarkCenterFr);

                mWaterMarkCenterBtn = new MyStatusButton(getContext());
                mWaterMarkCenterBtn.setData(R.drawable.filterbeautify_watermark_icon, getContext().getString(R.string.filterpage_watermark));
                mWaterMarkCenterBtn.setBtnStatus(true, false);
                mWaterMarkCenterBtn.setOnClickListener(m_clickListener);
                fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                fl.gravity = Gravity.CENTER;
                mWaterMarkCenterBtn.setLayoutParams(fl);
                mWaterMarkCenterFr.addView(mWaterMarkCenterBtn);

                //水印fr
                mWaterMarkFr = new FrameLayout(getContext());
                fl = new LayoutParams(LayoutParams.MATCH_PARENT, mBottomLayoutHeight); //232px
                fl.gravity = Gravity.CENTER_HORIZONTAL;
                mWaterMarkBottomFr.addView(mWaterMarkFr, fl);
                InitWaterMarkUI();
            }
        }

        m_waitDlg = new WaitAnimDialog((Activity)getContext());
        m_waitDlg.SetGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL,m_bottomBarHeight + m_bottomListHeight + ShareData.PxToDpi_xhdpi(20));
    }


    private void InitWaterMarkUI()
    {
        mWatermarkRecyclerView = new RecyclerView(getContext());
        ((SimpleItemAnimator)mWatermarkRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mWatermarkRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mWatermarkRecyclerView.setHasFixedSize(true);
        mWatermarkRecyclerView.setBackgroundColor(Color.TRANSPARENT);
        LayoutParams fp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        fp.gravity = Gravity.CENTER_VERTICAL;
        mWaterMarkFr.addView(mWatermarkRecyclerView, fp);
        mWatermarkAdapter = new WatermarkAdapter(getContext());
        if(mWatermarkResArr == null)
        {
            mWatermarkResArr = WatermarkResMgr2.getInstance().sync_GetLocalRes(getContext(), null);
        }
        //temp
        mWatermarkAdapter.SetData(mWatermarkResArr);//水印数据集
        mWatermarkAdapter.setListener(new WatermarkAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(int position, WatermarkItem item)
            {
                m_view.setDrawWaterMark(true);
                m_Makeup2LocalData.m_waterMarkId = item.mID;
                SettingInfoMgr.GetSettingInfo(getContext()).SetPhotoWatermarkId(item.mID);
                if (isDoWaterAlphaAnim)
                {
                    m_view.AddWaterMarkWithAnim(MakeBmpV2.DecodeImage(getContext(), item.res, 0, -1, -1, -1, Bitmap.Config.ARGB_8888), item.mID == WatermarkResMgr2.getInstance().GetNonWatermarkId(getContext()));
                }
                else
                {
                    m_view.AddWaterMark(MakeBmpV2.DecodeImage(getContext(), item.res, 0, -1, -1, -1, Bitmap.Config.ARGB_8888), item.mID == WatermarkResMgr2.getInstance().GetNonWatermarkId(getContext()));
                }
                isDoWaterAlphaAnim = false;
                scroll2Center(position);
            }
        });
        mWatermarkRecyclerView.setAdapter(mWatermarkAdapter);
    }

    private LinearLayout mWaterMarkBottomFr;
    private MyStatusButton mWaterMarkCenterBtn;
    private FrameLayout mWaterMarkFr;
    private RecyclerView mWatermarkRecyclerView;
    private WatermarkAdapter mWatermarkAdapter;
    private Bitmap mWaterMaskBmp;
    private ArrayList<WatermarkItem> mWatermarkResArr;
    private boolean m_waterListShow = false;


    //打开或关闭水印的列表
    private void showWaterMarkFr(final boolean show)
    {
        if(show)
        {
            if (mWaterMaskBmp == null)
            {
                //水印素材区域毛玻璃处理
                Bitmap temp = CommonUtils.GetScreenBmp((Activity) getContext(), (int) (ShareData.m_screenWidth/2f), (int) (ShareData.m_screenHeight/2f));
                Bitmap out = MakeBmp.CreateFixBitmap(temp, temp.getWidth(), ShareData.PxToDpi_xhdpi(232), MakeBmp.POS_END, 0, Bitmap.Config.ARGB_8888);
                if (out != temp)
                {
                    temp.recycle();
                    temp = null;
                }
                mWaterMaskBmp = filter.fakeGlassBeauty(out, 0x99000000);//60%黑色
                mWaterMarkFr.setBackgroundDrawable(new BitmapDrawable(getResources(), mWaterMaskBmp));
            }
            mWaterMarkBottomFr.setVisibility(VISIBLE);
        }
        mWaterMarkCenterBtn.setBtnStatus(true, !show);
        mWatermarkAdapter.SetSelectedId(m_Makeup2LocalData.m_waterMarkId);
        mWatermarkAdapter.notifyDataSetChanged();

        float start = show ? ShareData.PxToDpi_xhdpi(320) : 0;
        float end = show ? 0 : ShareData.PxToDpi_xhdpi(320);
        final ObjectAnimator object = ObjectAnimator.ofFloat(mWaterMarkBottomFr, "translationY", start, end);
        object.setDuration(300);
        object.setInterpolator(new AccelerateDecelerateInterpolator());
        object.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                m_uiEnabled = false;
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                m_uiEnabled = true;
                if(!show)
                {
                    mWaterMarkBottomFr.setVisibility(GONE);
                }
                else
                {
                    mWatermarkRecyclerView.smoothScrollToPosition(mWatermarkAdapter.GetPosition());
                    mWaterMarkBottomFr.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            scroll2Center(mWatermarkAdapter.GetPosition());
                        }
                    }, 150);
                }
            }
        });
        object.start();
    }

    private void scroll2Center(int position)
    {
        if(mWatermarkRecyclerView != null)
        {
            View view = mWatermarkRecyclerView.getLayoutManager().findViewByPosition(position);
            if(view != null)
            {
                float center = mWatermarkRecyclerView.getWidth() / 2f;
                float viewCenter = view.getX() + view.getWidth() / 2f;
                mWatermarkRecyclerView.smoothScrollBy((int)(viewCenter - center), 0);
            }
        }
    }



    private boolean initFinish = false;
    private void showOtherBtn()
    {
        if(!initFinish)
        {
            if(m_isShouldAddEffect)
            {
                setScaleAnim(m_compareBtn,false);
            }
            setScaleAnim(mFixView,false);
            if(FaceDataV2.RAW_POS_MULTI.length > 1)
            {
                setScaleAnim(m_multifaceFr,false);
            }
            initFinish = true;
        }
    }


    public void ShowViewAnim(final View view, int StartY, int EndY,float startScale,float endScale,int frW,int frH) {
        if(view != null)
        {
            AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator object1 = ObjectAnimator.ofFloat(view, "scaleX", startScale, endScale);
            ObjectAnimator object2 = ObjectAnimator.ofFloat(view, "scaleY", startScale, endScale);
            ObjectAnimator object3 = ObjectAnimator.ofFloat(view, "translationY", StartY, EndY);
            ObjectAnimator object4 = ObjectAnimator.ofFloat(m_bottomFr, "translationY", m_bottomBarHeight + m_bottomListHeight, 0);
            ObjectAnimator object5 = ObjectAnimator.ofFloat(m_makeupList,"translationY",m_bottomListHeight,0);
            animatorSet.setDuration(300);
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSet.playTogether(object1, object2, object3, object4,object5);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (view != null) {
                        view.clearAnimation();
                    }
                }

                @Override
                public void onAnimationStart(Animator animation) {

                }
            });
            animatorSet.start();
        }
    }

    private void setScaleAnim(final View view, boolean hide) {
        if(view != null)
        {
            if (hide && view.getVisibility() == VISIBLE) {
                view.animate().scaleX(0).scaleY(0).setDuration(100).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(INVISIBLE);
                    }
                });
            } else if (!hide && view.getVisibility() != VISIBLE) {
                view.setScaleX(0);
                view.setScaleY(0);
                view.setVisibility(VISIBLE);
                view.animate().scaleX(1).scaleY(1).setDuration(100).setListener(null);
            }
        }
    }

    private void InitListUI()
    {
        m_makeup2Adapter = new Makeup2Adapter(m_makeup2Config);
        m_makeup2Adapter.setOnItemClickListener(m_itemClickListener);
        m_ListInfoArr = getAllRes();
        m_makeup2Adapter.SetData(m_ListInfoArr);
        m_makeupList = new Makeup2RecyclerView(getContext(),m_makeup2Adapter);
        LayoutParams fl = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.m_screenHeight);
        fl.gravity = Gravity.CENTER_HORIZONTAL| Gravity.BOTTOM;
        this.addView(m_makeupList, fl);
    }

    private void RelayoutSeekBarTipsPos(MySeekBar seekBar, int progress, int maxProgress)
    {
        FrameLayout.LayoutParams temp = (LayoutParams) m_seekBarTips.getLayoutParams();
        temp.bottomMargin = ShareData.PxToDpi_xhdpi(250) - ShareData.PxToDpi_xhdpi(20);
        temp.leftMargin = (int) ((int) (ShareData.PxToDpi_xhdpi(18 + 135) + seekBar.getLeft() + seekBar.getCurCiclePos()) - m_seekBarTips.getWidth()/2f);
        m_seekBarTips.setText("" + progress);
        m_seekBarTips.setLayoutParams(temp);
    }


    private ArrayList<RecommendExAdapter.ItemInfo> getAllRes()
    {
        ArrayList<RecommendExAdapter.ItemInfo> out = new ArrayList<>();
        RecommendExAdapter.ItemInfo itemInfo;
        ArrayList<Makeup2ResGroup> allDatas = readAllData();
        if(allDatas != null && allDatas.size() > 0)
        {
            for(int i = 0; i < allDatas.size(); i++)
            {
                Makeup2ResGroup group = allDatas.get(i);
                if(group.m_makeup2Ress != null && group.m_makeup2Ress.size() > 0)
                {
                    itemInfo = new RecommendExAdapter.ItemInfo();
                    int len = group.m_makeup2Ress.size() + 1;
                    int[] uris = new int[len];
                    Object[] logos = new Object[len];
                    String[] names = new String[len];
                    uris[0] = group.m_uri;
                    logos[0] = (int) group.m_thumb;
                    names[0] = group.m_name;
                    for(int j = 0; j < group.m_makeup2Ress.size();j++)
                    {
                        Makeup2Res res = group.m_makeup2Ress.get(j);
                        uris[j+1] = res.m_id;
                        logos[j+1] = (int) res.m_thumb;
                        names[j+1] = res.m_name;
                    }
                    itemInfo.setData(uris,logos,names,group);
                    out.add(itemInfo);
                }
            }
        }

        return out;
    }

    private int m_tempAlpha = 100;
    private Makeup2Adapter.OnItemClickListener m_itemClickListener = new Makeup2Adapter.OnItemClickListener() {
        @Override
        public void onProgressChange(MySeekBar seekBar, int progress) {
            RelayoutSeekBarTipsPos(seekBar,progress,100);
        }

        @Override
        public void onStartTrackingTouch(MySeekBar seekBar) {
            m_tempAlpha = m_Makeup2LocalData.m_asetAlphas[sFaceIndex];
            m_seekBarTips.setVisibility(VISIBLE);
            RelayoutSeekBarTipsPos(seekBar,seekBar.getProgress(),100);
        }

        @Override
        public void onStopTrackingTouch(MySeekBar seekBar) {
            m_seekBarTips.setVisibility(GONE);
            if(m_tempAlpha != seekBar.getProgress())
            {
                m_Makeup2LocalData.m_asetAlphas[sFaceIndex] = seekBar.getProgress();
                SendMakeupMsg();
            }
        }

        @Override
        public void onSeekBarStartShow(MySeekBar seekBar) {
            m_makeupList.setUIEnable(false);
            if(seekBar != null)
            {
                seekBar.setProgress(m_Makeup2LocalData.m_asetAlphas[sFaceIndex]);
            }
        }

        @Override
        public void onFinishLayoutAlphaFr(MySeekBar seekBar) {
            m_makeupList.setUIEnable(true);
        }

        @Override
        public void OnItemDown(BaseExAdapter.ItemInfo info, int parentIndex, int subIndex) {

        }

        @Override
        public void OnItemUp(BaseExAdapter.ItemInfo info, int parentIndex, int subIndex) {

        }

        @Override
        public void OnItemClick(BaseExAdapter.ItemInfo info, int parentIndex, int subIndex) {
            RecommendExAdapter.ItemInfo data = (RecommendExAdapter.ItemInfo) info;
            if(data.m_ex != null && data.m_ex instanceof Makeup2ResGroup)
            {
                Makeup2ResGroup group = (Makeup2ResGroup) data.m_ex;

                    if(group.m_makeup2Ress != null && (subIndex - 1) < group.m_makeup2Ress.size())
                    {
                        Makeup2Res res = group.m_makeup2Ress.get(subIndex - 1);
                        if(m_Makeup2LocalData.m_asetUri[sFaceIndex] != res.m_id)
                        {
                            m_bmpIsHasEffect = true;
                            m_Makeup2LocalData.m_asetAlphas[sFaceIndex] = 80;
                            m_Makeup2LocalData.setDecalRess(res.m_DecalRess);
                            m_Makeup2LocalData.setShapeType(res.m_shapeType);
                            m_Makeup2LocalData.SetMakeupData(res.m_makeupRes);
                            m_Makeup2LocalData.setAsetUri(res.m_id);
                            SendMakeupMsg();
                        }
                    }
            }
        }

        @Override
        public void OnItemDown(AbsAdapter.ItemInfo info, int index) {

        }

        @Override
        public void OnItemUp(AbsAdapter.ItemInfo info, int index) {

        }

        @Override
        public void OnItemClick(AbsAdapter.ItemInfo info, int index) {

        }
    };

    private void ShowMultiFacesTips()
    {
        //多人脸选择提示语
        m_multifaceTips = new TextView(getContext());
        LayoutParams fl = new LayoutParams(ShareData.PxToDpi_xhdpi(560),ShareData.PxToDpi_xhdpi(80));
        fl.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        fl.topMargin = m_frH - ShareData.PxToDpi_xhdpi(80) - ShareData.PxToDpi_xhdpi(17);
        m_multifaceTips.setLayoutParams(fl);
        this.addView(m_multifaceTips);
        m_multifaceTips.setBackgroundResource(R.drawable.beautify_makeup_multiface_tips_bk);
        m_multifaceTips.setText(R.string.makeup_multiface_tips);
        m_multifaceTips.setGravity(Gravity.CENTER);
        m_multifaceTips.setTextColor(Color.BLACK);
    }

    private void setImg(Object params)
    {
        if(params instanceof RotationImg2[])
        {
            m_isShouldAddEffect = false;
            m_imgInfo = params;
            RotationImg2 imgInfo = ((RotationImg2[]) params)[0];
            m_org = cn.poco.imagecore.Utils.DecodeFinalImage(getContext(), imgInfo.m_img, imgInfo.m_degree, -1, imgInfo.m_flip, SHOW_IMG_SIZE, SHOW_IMG_SIZE);
        }
        else if(params instanceof ImageFile2)
        {
            m_isShouldAddEffect = false;
            m_imgInfo = params;
            RotationImg2 imgInfo = ((ImageFile2) params).SaveImg2(getContext())[0];
            m_org = cn.poco.imagecore.Utils.DecodeFinalImage(getContext(), imgInfo.m_img, imgInfo.m_degree, -1, imgInfo.m_flip, SHOW_IMG_SIZE, SHOW_IMG_SIZE);
        }
        else if(params instanceof Bitmap)
        {
            m_imgInfo = params;
            m_isShouldAddEffect = false;
            m_org = (Bitmap) params;
            if(m_org != null && !m_org.isRecycled() && (m_org.getWidth() > SHOW_IMG_SIZE || m_org.getHeight() > SHOW_IMG_SIZE))
            {
                m_org = MakeBmpV2.CreateBitmapV2(m_org, 0, MakeBmpV2.FLIP_NONE, -1, SHOW_IMG_SIZE, SHOW_IMG_SIZE, Bitmap.Config.ARGB_8888);
            }
        }


        if(m_org != null)
        {
            m_view.setImage(m_org);
        }

        if(m_isComeFromBeautifyPage)
        {
            showStartAnim();
        }

        m_uiEnabled = false;
        SetWaitUI(true,"");
        if(m_isBack)
        {
            m_view.m_faceIndex = FaceDataV2.sFaceIndex;
            if(m_isShouldAddEffect)
            {
                Message message = Message.obtain();
                message.what = BEAUTIFY;
                message.obj = m_org;
                m_handler.sendMessage(message);
            }
            else
            {
                    resetOldData();
                    showOtherBtn();
            }
        }
        else if(FaceDataV2.RAW_POS_MULTI != null && FaceDataV2.RAW_POS_MULTI.length > 0 && m_isComeFromBeautifyPage)
        {
            if(m_Makeup2LocalData == null)
            {
                m_Makeup2LocalData = new Makeup2LocalData(FaceDataV2.RAW_POS_MULTI.length);
                m_Makeup2LocalData.m_waterMarkId = SettingInfoMgr.GetSettingInfo(getContext()).GetPhotoWatermarkId(
                        WatermarkResMgr2.getInstance().GetDefaultWatermarkId(getContext()));
            }
            if(FaceDataV2.sFaceIndex > -1)
            {
                m_view.m_faceIndex = FaceDataV2.sFaceIndex;
                showOtherBtn();
            }
            else
            {
                ShowMultiFacesTips();
                m_view.setMode(BeautyCommonViewEx.MODE_SEL_FACE);
                m_partUIEnable = false;
            }
            if(mFixView != null)
            {
                mFixView.showJitterAnimAccordingStatus();
            }
            SetWaitUI(false, "");
            m_uiEnabled = true;
        }
        else
        {
            FaceDataV2.ResetData();
            Message msg = Message.obtain();
            msg.what = FACECHECK;
            msg.obj = m_org;
            m_handler.sendMessage(msg);
        }
    }

    private void showStartAnim()
    {
        int tempStartY = (int) (ShareData.PxToDpi_xhdpi(90) + (m_viewH - m_frH)/2f);
        float scale1 = (float)m_frW / (float)m_org.getWidth();
        float scale2 = (float)m_frH / (float)m_org.getHeight();
        float tempImgH =  m_org.getHeight() * ((scale1 > scale2) ? scale2 : scale1);
        float scale = m_imgH/tempImgH;
        ShowViewAnim(m_view,tempStartY,0,scale,1f,m_frW,m_finalFrH);
    }

    private FullScreenDlg m_noFaceHelpFr;
    protected void MakeNoFaceHelp()
    {
        if(m_noFaceHelpFr == null)
        {
            m_noFaceHelpFr = CommonUI.MakeNoFaceHelpDlg((Activity)getContext(), new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(m_uiEnabled)
                    {
                        if(mFixView != null)
                        {
                            mFixView.modifyStatus();
                        }
                        if(m_noFaceHelpFr != null)
                        {
                            m_noFaceHelpFr.dismiss();
                            m_noFaceHelpFr = null;
                        }
                        FaceDataV2.sFaceIndex = 0;
                        m_view.m_faceIndex = 0;
                        m_faceDataBackups.backups();
                        doAnim(MakeupUIHelper.ChangePointFr.CHECK_TRHEE);
                        makeChangePointFr();
                        m_curMode = CHANGEPOINT_THREE;
                        switchUIByMode(m_curMode);
                        m_changePointFr.setUIFlag(MakeupUIHelper.ChangePointFr.CHECK_TRHEE);
                    }
                }
            });
        }
    }

    private void SetWaitUI(boolean flag, String title)
    {
        if(flag)
        {
            if(m_waitDlg != null)
            {
                m_waitDlg.show();
            }
        }
        else
        {
            if(m_waitDlg != null)
            {
                m_waitDlg.hide();
            }
        }
    }

    private class BtnOnclickAnimaListener extends OnAnimationClickListener
    {
        int touchTag = -1;
        @Override
        public void onAnimationClick(View v)
        {
            if (m_uiEnabled)
            {
                if(v == mFixView && touchTag == mFixView.hashCode())
                {
                    touchTag = -1;
                    mFixView.modifyStatus();
                    m_posModify = false;
                    makeChangePointFr();
                    m_curMode = CHANGEPOINT_MANY;
                    switchUIByMode(m_curMode);
                    m_changepointFrCB.onClick(m_changePointFr.m_checkLipBtn);
                    m_faceDataBackups.backups();
                    TongJi2.AddCountByRes(getContext(), R.integer.一键萌妆_手动定点);
                    MyBeautyStat.onClickByRes(R.string.美颜美图_一键萌装页面_主页面_手动定点);
                }
                else if(v == m_multifaceFr && touchTag == m_multifaceFr.hashCode())
                {
                    touchTag = -1;
                    m_partUIEnable = false;
                    setScaleAnim(m_multifaceFr,true);
                    setScaleAnim(mFixView,true);
                    setScaleAnim(m_compareBtn,true);
                    ShowMultiFacesTips();
                    m_view.setMode(BeautyCommonViewEx.MODE_SEL_FACE);
                    m_view.Restore();
                }
            }
        }

        @Override
        public void onTouch(View view)
        {
            touchTag = view.hashCode();
        }
    }

    View.OnClickListener m_clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(m_uiEnabled)
            {
                if(v == m_titleBtn && m_partUIEnable == true)
                {
                    isFold = !isFold;
                    showBottomViewAnim(isFold);
                    if(isFold)
                    {
                        MyBeautyStat.onClickByRes(R.string.美颜美图_一键萌装页面_主页面_收回bar);
                    }
                    else
                    {
                        MyBeautyStat.onClickByRes(R.string.美颜美图_一键萌装页面_主页面_展开bar);
                    }

                    TongJi2.AddCountByRes(getContext(), R.integer.一键萌妆);
                }
                else if(v == mWaterMarkCenterBtn && m_partUIEnable == true)
                {
                    m_waterListShow = false;
                    showWaterMarkFr(false);
                }
                else if(v == m_okBtn && m_partUIEnable == true)
                {
                    TongJi2.AddCountByRes(getContext(), R.integer.一键萌妆_确认);
                    MyBeautyStat.onClickByRes(R.string.美颜美图_一键萌装页面_主页面_确认);
                    sendTongji();
                    sendCSTongji();
                    SendSaveMsg();
                }
                else if(v == m_backBtn)
                {
                    if(m_curMode == CHANGEPOINT_THREE)
                    {
                        if(m_changePointFr != null && m_changepointFrCB != null)
                        {
                            m_changepointFrCB.onClick(m_changePointFr.m_checkThreeBackBtn);
                            return;
                        }
                    }
                    else if(m_curMode == CHANGEPOINT_MANY)
                    {
                        if(m_changePointFr != null && m_changepointFrCB != null)
                        {
                            m_changepointFrCB.onClick(m_changePointFr.m_checkBackBtn);
                            return;
                        }
                    }
                    MyBeautyStat.onClickByRes(R.string.美颜美图_一键萌装页面_主页面_取消);
                    TongJi2.AddCountByRes(getContext(), R.integer.一键萌妆_取消);
                    if (m_isChange) {
                        showExitDialog();
                    } else {
                        onExit();
                    }
                }
            }
        }
    };

    private void onExit()
    {
        if(m_isComeFromBeautifyPage)
        {
            HashMap<String,Object> params = new HashMap<String, Object>();
            if(m_imgInfo != null && m_imgInfo instanceof Bitmap)
            {
                params.put("img",(Bitmap)m_imgInfo);
                Bitmap temp = (Bitmap) m_imgInfo;
            }
            else
            {
                params.put("img",m_org);
            }
            params.putAll(getBackAnimParam());
            m_site.onBack(getContext(), params);
        }
        else
        {
            m_site.onBack(getContext(), null);
            FaceDataV2.ResetData();
        }
    }

    private void sendCSTongji()
    {
        if(m_Makeup2LocalData.m_asetUri.length > FaceDataV2.sFaceIndex && FaceDataV2.sFaceIndex > -1)
        {
            MyBeautyStat.onUseCosFace(getCosType(m_Makeup2LocalData.m_asetUri[FaceDataV2.sFaceIndex]),m_Makeup2LocalData.m_asetAlphas[FaceDataV2.sFaceIndex]);
        }
    }

    private MyBeautyStat.CosFaceType getCosType(int uri)
    {
        MyBeautyStat.CosFaceType cosFaceType = null;
        switch (uri)
        {
            case 1000001:
                cosFaceType = cosFaceType.小黑猫;
                break;
            case 1000002:
                cosFaceType = cosFaceType.糖果猫咪;
                break;
            case 1000003:
                cosFaceType = cosFaceType.天使猫;
                break;
            case 1000004:
                cosFaceType = cosFaceType.草莓兔;
                break;
            case 1000005:
                cosFaceType = cosFaceType.气球派对;
                break;
            case 1000006:
                cosFaceType = cosFaceType.山兔;
                break;
            case 1000007:
                cosFaceType = cosFaceType.乔巴;
                break;
            case 1000008:
                cosFaceType = cosFaceType.阿拉蕾;
                break;
            case 1000009:
                cosFaceType = cosFaceType.美少女;
                break;
            case 1000010:
                cosFaceType = cosFaceType.麋鹿妆;
                break;
            case 1000011:
                cosFaceType = cosFaceType.小恶魔;
                break;
            case 1000012:
                cosFaceType = cosFaceType.糖果猫咪;
                break;
            case 1000014:
                cosFaceType = cosFaceType.俊介君;
                break;
            case 1000015:
                cosFaceType = cosFaceType.糖果猫咪;
                break;
            case 1000016:
                cosFaceType = cosFaceType.嘻哈侠;
                break;
            case 1000013:
                cosFaceType = cosFaceType.迷糊汪;
                break;
        }
        return cosFaceType;
    }

    private HashMap<String, Object> getBackAnimParam()
    {
        HashMap<String, Object> params = new HashMap<>();
        params.put(Beautify4Page.PAGE_BACK_ANIM_VIEW_TOP_MARGIN, (m_view.getHeight() - m_finalFrH)/2f);
        params.put(Beautify4Page.PAGE_BACK_ANIM_IMG_H, m_view.getImgHeight());
        return params;
    }

    private void sendTongji()
    {
        if(m_Makeup2LocalData != null && m_Makeup2LocalData.m_asetUri.length > 0)
        {
           for(int i = 0 ; i < m_Makeup2LocalData.m_asetUri.length; i++)
           {
               if(m_Makeup2LocalData.m_asetUri[i] != -1)
               {
                   switch (m_Makeup2LocalData.m_asetUri[i])
                   {
                     case 1000001:
                         TongJi2.AddCountByRes(getContext(), R.integer.一键萌妆_小黑猫);
                           break;
                       case 1000002:
                           TongJi2.AddCountByRes(getContext(), R.integer.一键萌妆_糖果猫咪);
                           break;
                       case 1000003:
                           TongJi2.AddCountByRes(getContext(), R.integer.一键萌妆_天使猫);
                           break;
                       case 1000004:
                           TongJi2.AddCountByRes(getContext(), R.integer.一键萌妆_草莓兔);
                           break;
                       case 1000005:
                           TongJi2.AddCountByRes(getContext(), R.integer.一键萌妆_气球派对);
                           break;
                       case 1000006:
                           TongJi2.AddCountByRes(getContext(), R.integer.一键萌妆_山兔);
                           break;
                       case 1000007:
                           TongJi2.AddCountByRes(getContext(), R.integer.一键萌妆_乔巴);
                           break;
                       case 1000008:
                           TongJi2.AddCountByRes(getContext(), R.integer.一键萌妆_阿拉蕾);
                           break;
                       case 1000009:
                           TongJi2.AddCountByRes(getContext(), R.integer.一键萌妆_美少女);
                           break;
                       case 1000010:
                           TongJi2.AddCountByRes(getContext(), R.integer.一键萌妆_麋鹿妆);
                           break;
                       case 1000011:
                           TongJi2.AddCountByRes(getContext(), R.integer.一键萌妆_小恶魔);
                           break;
                       case 1000012:
                           TongJi2.AddCountByRes(getContext(), R.integer.一键萌妆_鹿公主);
                           break;
                       case 1000014:
                           TongJi2.AddCountByRes(getContext(), R.integer.一键萌妆_俊介君);
                           break;
                       case 1000015:
                           TongJi2.AddCountByRes(getContext(), R.integer.一键萌妆_甜心汪);
                           break;
                       case 1000016:
                           TongJi2.AddCountByRes(getContext(), R.integer.一键萌妆_嘻哈侠);
                           break;
                       case 1000013:
                           TongJi2.AddCountByRes(getContext(), R.integer.一键萌妆_迷糊汪);
                           break;
                   }
               }
           }
        }
    }

    private void makeChangePointFr()
    {
        if(m_changePointFr == null)
        {
            m_changePointFr = MakeupUIHelper.showChangePointFr(MakeupSPage.this,m_changepointFrCB);
        }
    }


    private boolean m_waterMarkFrIsShow = false;
    private void switchUIByMode(int mode)
    {
        switch (mode) {
            case CHANGEPOINT_THREE:
                if (m_changePointFr != null) {
                    m_changePointFr.setVisibility(VISIBLE);
                }
                if (m_bottomFr != null) {
                    m_bottomFr.setVisibility(GONE);
                }
                setScaleAnim(mFixView,true);
                setScaleAnim(m_compareBtn,true);
                if (m_multifaceFr != null && FaceDataV2.RAW_POS_MULTI.length > 1) {
                    setScaleAnim(m_multifaceFr,true);
                }

                if (m_makeupList != null) {
                    m_makeupList.setVisibility(GONE);
                }
                m_view.setDrawWaterMark(false);
                break;
            case CHANGEPOINT_MANY:
                if(m_changePointFr != null)
                {
                    m_changePointFr.setVisibility(VISIBLE);
                }
                if(m_bottomFr != null)
                {
                    m_bottomFr.setVisibility(GONE);
                }
                setScaleAnim(mFixView,true);
                setScaleAnim(m_compareBtn,true);
                if(m_multifaceFr != null && FaceDataV2.RAW_POS_MULTI.length > 1)
                {
                    setScaleAnim(m_multifaceFr,true);
                }
                if(m_org != null && !m_org.isRecycled() && m_view != null)
                {
                    m_view.setImage(m_org);
                }
                if(m_makeupList != null)
                {
                    m_makeupList.setVisibility(GONE);
                }

                if(mWaterMarkFr != null && mWaterMarkFr.getVisibility() == VISIBLE)
                {
                    m_waterMarkFrIsShow = true;
                    mWaterMarkFr.setVisibility(GONE);
                }
                m_view.setDrawWaterMark(false);
                break;
            case NORMOL:
                if(m_changePointFr != null)
                {
                    m_changePointFr.setVisibility(GONE);
                }
                if(m_bottomFr != null)
                {
                    m_bottomFr.setVisibility(VISIBLE);
                }
//                setScaleAnim(mFixView,false);
//                if(m_multifaceFr != null && FaceDataV2.FACE_POS_MULTI.length > 1)
//                {
//                    setScaleAnim(m_multifaceFr,false);
//                }
                if(m_curBmp != null && !m_curBmp.isRecycled() && m_view != null)
                {
                    m_view.setImage(m_curBmp);
                }
                else if(m_org != null && !m_org.isRecycled() && m_view != null)
                {
                    m_view.setImage(m_org);
                }
                if(m_makeupList != null)
                {
                    m_makeupList.setVisibility(VISIBLE);
                }
                if(mWaterMarkFr != null && m_waterMarkFrIsShow)
                {
                    mWaterMarkFr.setVisibility(VISIBLE);
                    m_waterMarkFrIsShow = false;
                }
                if(m_beautifyBmp != null || m_curBmp != null)
                {
                    setScaleAnim(m_compareBtn,false);
                }
                if(m_bmpIsHasEffect)
                {
                    m_view.setDrawWaterMark(true);
                }
                break;
        }
    }

    MakeupUIHelper.ChangePointFr.ChangePointFrCallBack m_changepointFrCB = new MakeupUIHelper.ChangePointFr.ChangePointFrCallBack() {
        @Override
        public void onCheckedChanged(boolean isChecked, boolean fromUser) {
            if(m_uiEnabled && fromUser)
            {
                if(isChecked)
                {
                    m_view.m_moveAllFacePos = true;
                    m_view.invalidate();
                }
                else
                {
                    m_view.m_moveAllFacePos = false;
                    m_view.invalidate();
                }
            }
        }

        @Override
        public void onClick(View v) {
            if(m_uiEnabled && m_changePointFr != null)
            {
                if (v == m_changePointFr.m_checkBackBtn)
                {
                    m_curMode = NORMOL;
                    switchUIByMode(NORMOL);
                    m_view.m_showPosFlag = 0;
                    m_view.m_touchPosFlag = m_view.m_showPosFlag;
                    m_view.ResetAnim();
                    m_faceDataBackups.restore();
                    m_ischangePointAnimResetStart = true;
                }
                else if (v == m_changePointFr.m_changePointOkBtn)
                {
                    m_curMode = NORMOL;
                    switchUIByMode(NORMOL);
                    m_view.m_showPosFlag = 0;
                    m_view.m_touchPosFlag = m_view.m_showPosFlag;
                    m_view.ResetAnim();
                    if (m_posModify) {
                        SendMakeupMsg();
                    }
                    m_ischangePointAnimResetStart = true;
                }
                else if (v == m_changePointFr.m_checkThreeBackBtn)
                {
                    FaceDataV2.CHECK_FACE_SUCCESS = true;
                    m_changepointFrCB.onClick(m_changePointFr.m_checkBackBtn);
                    showOtherBtn();
                    resetOldData();
                }
                else if (v == m_changePointFr.m_checkThreeOkBtn)
                {
                    m_curMode = NORMOL;
                    switchUIByMode(NORMOL);
                    m_view.m_showPosFlag = 0;
                    m_view.m_touchPosFlag = m_view.m_showPosFlag;
                    m_view.ResetAnim();
                    if (!FaceDataV2.CHECK_FACE_SUCCESS || !FaceDataV2.sIsFix)
                    {
                        FaceDataV2.sIsFix = true;
                        float[] faceAll = RAW_POS_MULTI[m_view.m_faceIndex].getFaceFeaturesMakeUp();
                        float[] faceData = RAW_POS_MULTI[m_view.m_faceIndex].getFaceRect();
                        filter.reFixPtsBShapes(getContext(), m_view.m_faceIndex, faceData, faceAll, m_org);
                        RAW_POS_MULTI[m_view.m_faceIndex].setFaceRect(faceData);
                        RAW_POS_MULTI[m_view.m_faceIndex].setMakeUpFeatures(faceAll);
                        if (m_org != null && !m_org.isRecycled())
                        {
                            FaceDataV2.Raw2Ripe(m_org.getWidth(), m_org.getHeight());
                        }
                        FaceDataV2.CHECK_FACE_SUCCESS = true;
                    }
                    showOtherBtn();
                    resetOldData();
                }
                else if (v == m_changePointFr.m_checkLipBtn)
                {
                    doAnim(MakeupUIHelper.ChangePointFr.CHECK_INDEX_LIP);
                    m_changePointFr.setUIFlag(MakeupUIHelper.ChangePointFr.CHECK_INDEX_LIP);
                }
                else if (v == m_changePointFr.m_checkEyeBtnL)
                {
                    doAnim(MakeupUIHelper.ChangePointFr.CHECK_INDEX_EYE_L);
                    m_changePointFr.setUIFlag(MakeupUIHelper.ChangePointFr.CHECK_INDEX_EYE_L);
                }
                else if (v == m_changePointFr.m_checkEyebrowBtn)
                {
                    doAnim(MakeupUIHelper.ChangePointFr.CHECK_INDEX_EYEBROW);
                    m_changePointFr.setUIFlag(MakeupUIHelper.ChangePointFr.CHECK_INDEX_EYEBROW);
                }
                else if (v == m_changePointFr.m_checkCheekBtn)
                {
                    doAnim(MakeupUIHelper.ChangePointFr.CHECK_INDEX_CHEEK);
                    m_changePointFr.setUIFlag(MakeupUIHelper.ChangePointFr.CHECK_INDEX_CHEEK);
                }
                else if (v == m_changePointFr.m_checkNoseBtn)
                {
                    doAnim(MakeupUIHelper.ChangePointFr.CHECK_INDEX_NOSE);
                    m_changePointFr.setUIFlag(MakeupUIHelper.ChangePointFr.CHECK_INDEX_NOSE);
                }
            }


        }
    };

    public void doAnim(int flag)
    {
        m_view.Data2UI();
        m_view.setMode(BeautyCommonViewEx.MODE_MAKEUP);
        switch (flag)
        {
            case MakeupUIHelper.ChangePointFr.CHECK_TRHEE:
                m_view.setMode(BeautyCommonViewEx.MODE_FACE);
                m_view.m_showPosFlag = MakeUpViewEx1.POS_THREE;
                m_view.m_touchPosFlag = m_view.m_showPosFlag;
                m_view.DoFixedPointAnim();
                break;
            case MakeupUIHelper.ChangePointFr.CHECK_INDEX_EYE_L:
                m_view.m_showPosFlag = MakeUpViewEx1.POS_EYE;
                m_view.m_touchPosFlag = m_view.m_showPosFlag;
                m_view.DoFixedPointAnim();
                break;
            case MakeupUIHelper.ChangePointFr.CHECK_INDEX_EYEBROW:
                m_view.m_showPosFlag = MakeUpViewEx1.POS_EYEBROW;
                m_view.m_touchPosFlag = m_view.m_showPosFlag;
                m_view.DoFixedPointAnim();
                break;
            case MakeupUIHelper.ChangePointFr.CHECK_INDEX_LIP:
                m_view.m_showPosFlag = MakeUpViewEx1.POS_LIP;
                m_view.m_touchPosFlag = m_view.m_showPosFlag;
                m_view.DoFixedPointAnim();
                break;
            case MakeupUIHelper.ChangePointFr.CHECK_INDEX_CHEEK:
                m_view.m_showPosFlag = MakeUpViewEx1.POS_CHEEK;
                m_view.m_touchPosFlag = m_view.m_showPosFlag;
                m_view.DoFixedPointAnim();
                break;
            case MakeupUIHelper.ChangePointFr.CHECK_INDEX_NOSE:
                m_view.m_showPosFlag = MakeUpViewEx1.POS_NOSE;
                m_view.m_touchPosFlag = m_view.m_showPosFlag;
                m_view.DoFixedPointAnim();
                break;
        }
    }

    private void showBottomViewAnim(final boolean isFold)
    {
        m_bottomFr.clearAnimation();
        m_view.clearAnimation();
        if(isFold)
        {
            m_titleBtn.setBtnStatus(true, true);
        }
        else
        {
            m_titleBtn.setBtnStatus(true, false);
        }

        AnimatorSet animatorSet = new AnimatorSet();
        float startBottom, endBottom, startView, endView, startCheck, endCheck;

        if (isFold)
        {
            startBottom = 0f;
            endBottom = (float) (m_bottomListHeight);

            startView = 0f;
            endView = ShareData.PxToDpi_xhdpi(218);

            startCheck = 0f;
            endCheck = m_bottomListHeight;
        }
        else
        {
            startBottom = (float) (m_bottomListHeight);
            endBottom = 0f;

            startView = ShareData.PxToDpi_xhdpi(228);
            endView = 0f;

            startCheck = m_bottomListHeight;
            endCheck = 0f;
        }
        ObjectAnimator object1 = ObjectAnimator.ofFloat(m_bottomFr, "translationY", startBottom, endBottom);
        object1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float tempDis = (float) animation.getAnimatedValue();
                if(m_view != null)
                {
                    LayoutParams fl = (LayoutParams) m_view.getLayoutParams();
                    fl.height = (int) (m_finalFrH + tempDis);
                    m_view.setLayoutParams(fl);
                }
            }
        });
        object1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(m_view != null)
                {
                    if(isFold)
                    {
                        m_view.InitAnimDate(m_frW,m_finalFrH,m_frW,m_finalFrH + (m_bottomListHeight));
                    }
                    else
                    {
                        m_view.InitAnimDate(m_frW,m_finalFrH + (m_bottomListHeight),m_frW,m_finalFrH);
                    }
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        ObjectAnimator object2 = ObjectAnimator.ofFloat(m_makeupList, "translationY", startView, endView);
        ObjectAnimator object3 = ObjectAnimator.ofFloat(mFixView,"translationY", startCheck,endCheck);
        ObjectAnimator object4 = null;
        if(m_multifaceFr != null && m_multifaceFr.getVisibility() == VISIBLE)
        {
            object4 = ObjectAnimator.ofFloat(m_multifaceFr,"translationY", startCheck,endCheck);
        }
        animatorSet.playTogether(object1,object2,object3);
        if(object1 != null)
        {
            animatorSet.playTogether(object4);
        }
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();
    }

    private boolean m_posModify;
    MakeUpViewEx1.ControlCallback m_cb = new MakeUpViewEx.ControlCallback() {
        @Override
        public void OnTouchEyebrow(boolean isLeft) {

        }

        @Override
        public void OnTouchEye(boolean isLeft) {

        }

        @Override
        public void OnTouchCheek(boolean isLeft) {

        }

        @Override
        public void OnTouchLip() {

        }

        @Override
        public void OnTouchFoundation() {

        }

        @Override
        public void UpdateSonWin(Bitmap bitmap, float x, float y) {
            if(m_sonWin != null)
            {
                m_sonWin.SetData(bitmap,(int) x,(int) y);
            }
        }

        @Override
        public void On3PosModify() {
            m_posModify = true;
        }

        @Override
        public void OnAllPosModify() {
            m_posModify = true;
        }

        @Override
        public void onTouchWatermark()
        {
            if(!m_waterListShow)
            {
                if(isFold)
                {
                    isFold = !isFold;
                    showBottomViewAnim(isFold);
                }
            }
                m_waterListShow = !m_waterListShow;
                showWaterMarkFr(m_waterListShow);
        }

        @Override
        public void onFingerUp()
        {
            if(m_waterListShow)
            {
                m_waterListShow = !m_waterListShow;
                showWaterMarkFr(m_waterListShow);
            }
        }

        @Override
        public void OnSelFaceIndex(final int index) {
            if(m_view != null)
            {
                m_uiEnabled = true;
                m_partUIEnable = true;
                m_view.m_faceIndex = index;
                sFaceIndex = index;
                m_view.setMode(BeautyCommonViewEx.MODE_NORMAL);
//                m_view.DoSelFaceAnim();
                GoneMultiFacesTips();
                m_bottomFr.setVisibility(VISIBLE);
                if(m_beautifyBmp != null || m_curBmp != null)
                {
                    setScaleAnim(m_compareBtn,false);
                }
                setScaleAnim(mFixView,false);
                if(FaceDataV2.RAW_POS_MULTI.length > 1)
                {
                    setScaleAnim(m_multifaceFr,false);
                }

                resetOldData();

                if(m_makeup2Adapter.IsShowAlphaFr())
                {
                    m_makeup2Adapter.closeAlphaFr();
                    MakeupSPage.this.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(m_Makeup2LocalData.m_asetUri[index] != -1)
                            {
                                m_makeup2Adapter.SetSelectByUri(m_Makeup2LocalData.m_asetUri[index]);
                            }
                            else
                            {
                                m_makeup2Adapter.CancelSelect();
                            }
                        }
                    },300);
                }
                else
                {
                    if(m_Makeup2LocalData.m_asetUri[index] != -1)
                    {
                        m_makeup2Adapter.SetSelectByUri(m_Makeup2LocalData.m_asetUri[index]);
                    }
                    else
                    {
                        m_makeup2Adapter.CancelSelect();
                    }
                }
            }
        }

        @Override
        public void OnAnimFinish() {
            if(m_ischangePointAnimResetStart)
            {
                setScaleAnim(mFixView,false);
                if(m_multifaceFr != null && FaceDataV2.RAW_POS_MULTI != null && FaceDataV2.RAW_POS_MULTI.length > 1)
                {
                    setScaleAnim(m_multifaceFr,false);
                }
                m_ischangePointAnimResetStart = false;
            }
        }
    };

    private void GoneMultiFacesTips()
    {
        if(m_multifaceTips != null)
        {
            m_multifaceTips.setBackgroundDrawable(null);
            m_multifaceTips.setVisibility(GONE);
            this.removeView(m_multifaceTips);
            m_multifaceTips = null;
        }
    }

    @Override
    public void onBack() {
       m_clickListener.onClick(m_backBtn);
    }

    @Override
    public void onClose() {
        if (m_thread != null)
        {
            m_thread.quit();
            m_thread = null;
        }
        if(m_org != null)
        {
            m_org = null;
        }

        if(m_curBmp != null)
        {
            m_curBmp = null;
        }

        if(m_beautifyBmp != null)
        {
            m_beautifyBmp = null;
        }

        if(m_waitDlg != null)
        {
            m_waitDlg.dismiss();
            m_waitDlg = null;
        }
        if(mFixView != null)
        {
            mFixView.clearAll();
        }
        SettingInfoMgr.Save(getContext());
        clearExitDialog();

        MyBeautyStat.onPageEndByRes(R.string.美颜美图_一键萌妆页面_主页面);
    }

    //列表Item的数据
    public static class Makeup2ResGroup
    {
        public Object m_thumb;//缩略图
        public String m_name;//名称
        public int m_maskColor;
        public int m_uri;
        public ArrayList<Makeup2Res> m_makeup2Ress;//子Item的集合
    }

    public static class Makeup2Res
    {
        public Object m_thumb;//子Item缩略图
        public String m_name;//子Item名称
        public int m_maskColor;//子Item选中状态的覆盖颜色
        public int m_id;
        public int m_shapeType = -1;//变形的类型
        public MakeupRes m_makeupRes;//彩妆的数据
        public ArrayList<DecalRes> m_DecalRess;//贴纸的数据
    }

    private ArrayList<Makeup2ResGroup> readAllData()
    {
        ArrayList<Makeup2ResGroup> out = new ArrayList<>();
        Makeup2ResGroup item = new Makeup2ResGroup();
        item.m_name = "汪星人";
        item.m_uri = 5;
        item.m_thumb = R.drawable.makeup2_theme_fadou;
        item.m_maskColor = 0xffffcf97;
        ArrayList<Makeup2Res> ress = new ArrayList<>();

        Makeup2Res makeup2Res = new Makeup2Res();
        makeup2Res.m_id = 1000013;
        makeup2Res.m_name = "迷糊汪";
        makeup2Res.m_thumb = R.drawable.makeup2_fadou_thumb_w1;
        makeup2Res.m_maskColor = item.m_maskColor;
        makeup2Res.m_shapeType = filter.ShapeType.CuteDog;
        MakeupRes makeupRes = new MakeupRes();
        MakeupRes.MakeupData[] datas = new MakeupRes.MakeupData[6];
        {
            datas[0] = new MakeupRes.MakeupData();
            datas[0].m_makeupType = MakeupType.EYE_L.GetValue();
            datas[0].m_defAlpha = 30;
            datas[0].m_ex = 1;
            datas[0].m_res = new Object[]{R.drawable.makeup2_fadou_w_meitong};

            datas[1] = new MakeupRes.MakeupData();
            datas[1].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f};
            datas[1].m_params = new float[]{0.5f,10.0f,1.0f,0f};
            datas[1].m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
            datas[1].m_defAlpha = 50;
            datas[1].m_ex = 1;
            datas[1].m_res = new Object[]{R.drawable.makeup2_fadou_w_shangjiemao1,R.drawable.makeup2_fadou_w_shangjiemao2};

            datas[2] = new MakeupRes.MakeupData();
            datas[2].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f};
            datas[2].m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
            datas[2].m_defAlpha = 80;
            datas[2].m_ex = 1;
            datas[2].m_res = new Object[]{R.drawable.makeup2_fadou_w_yanxian};

            datas[3] = new MakeupRes.MakeupData();
            datas[3].m_makeupType = MakeupType.LIP.GetValue();
            datas[3].m_defAlpha = 70;
            datas[3].m_ex = 41;
            datas[3].m_res = new Object[]{R.drawable.makeup2_fadou_w_chuncai};

            datas[4] = new MakeupRes.MakeupData();
            datas[4].m_defAlpha = 50;
            datas[4].m_ex = 38;
            datas[4].m_makeupType = MakeupType.KOHL_L.GetValue();
            datas[4].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f,160.0f,129.0f};
            datas[4].m_res = new Object[]{R.drawable.makeup2_fadou_w_yanying};

            datas[5] = new MakeupRes.MakeupData();
            datas[5].m_defAlpha = 75;
            datas[5].m_ex = 0xffffe4e4;
            datas[5].m_makeupType = MakeupType.FOUNDATION.GetValue();
        }
        makeupRes.m_groupRes = datas;
        makeup2Res.m_makeupRes = makeupRes;

        ArrayList<DecalRes> decalRess = new ArrayList<>();
        DecalRes res = new DecalRes();
        res.m_type = "head";
        res.m_scale = 1.8f;
        res.m_offset = new float[]{0f,-0.4f};
        res.m_res = R.drawable.__makeup2__baischangguihead0728;
        decalRess.add(res);

        res = new DecalRes();
        res.m_type = "eye";
        res.m_scale = 1.9f;
        res.m_offset = new float[]{0f, 0.25f};
        res.m_res = R.drawable.__makeup2__baischangguieye0732;
        decalRess.add(res);
        makeup2Res.m_DecalRess = decalRess;

        res = new DecalRes();
        res.m_type = "nose";
        res.m_scale = 0.85f;
        res.m_offset = new float[]{0.02f, -0.23f};
        res.m_res = R.drawable.__makeup2__baischangguinose0728;
        decalRess.add(res);

        res = new DecalRes();
        res.m_type = "shoulder";
        res.m_scale = 0.26f;
        res.m_offset = new float[]{0f, -0.8f};
        res.m_res = R.drawable.__makeup2__baischangguishloud20728;
        decalRess.add(res);
        ress.add(makeup2Res);

        makeup2Res = new Makeup2Res();
        makeup2Res.m_id = 1000016;
        makeup2Res.m_name = "嘻哈侠";
        makeup2Res.m_thumb = R.drawable.makeup2_fadou_thumb_b2;
        makeup2Res.m_maskColor = item.m_maskColor;
        makeup2Res.m_shapeType = filter.ShapeType.CuteDog;

        makeupRes = new MakeupRes();
        datas = new MakeupRes.MakeupData[7];
        {
            datas[0] = new MakeupRes.MakeupData();
            datas[0].m_makeupType = MakeupType.EYE_L.GetValue();
            datas[0].m_defAlpha = 30;
            datas[0].m_ex = 1;
            datas[0].m_res = new Object[]{R.drawable.makeup2_fadou_b_meitong};

            datas[1] = new MakeupRes.MakeupData();
            datas[1].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f};
            datas[1].m_params = new float[]{1.0f,0.0f,1.0f,-20f};
            datas[1].m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
            datas[1].m_defAlpha = 50;
            datas[1].m_ex = 1;
            datas[1].m_res = new Object[]{R.drawable.makeup2_fadou_b_shangjiemao1,R.drawable.makeup2_fadou_b_shangjiemao1};

            datas[2] = new MakeupRes.MakeupData();
            datas[2].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f};
            datas[2].m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
            datas[2].m_defAlpha = 50;
            datas[2].m_ex = 1;
            datas[2].m_res = new Object[]{R.drawable.makeup2_fadou_b_yanxian};

            datas[3] = new MakeupRes.MakeupData();
            datas[3].m_makeupType = MakeupType.LIP.GetValue();
            datas[3].m_defAlpha = 70;
            datas[3].m_ex = 41;
            datas[3].m_res = new Object[]{R.drawable.makeup2_fadou_b_chuncai};

            datas[4] = new MakeupRes.MakeupData();
            datas[4].m_defAlpha = 45;
            datas[4].m_ex = 38;
            datas[4].m_makeupType = MakeupType.KOHL_L.GetValue();
            datas[4].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f,160.0f,129.0f};
            datas[4].m_res = new Object[]{R.drawable.makeup2_fadou_b_yanying};

            datas[5] = new MakeupRes.MakeupData();
            datas[5].m_pos = new float[]{255.0f,118.0f,160.0f,129.0f,86.0f,101.0f};
            datas[5].m_makeupType = MakeupType.EYELASH_DOWN_L.GetValue();
            datas[5].m_defAlpha = 50;
            datas[5].m_ex = 1;
            datas[5].m_res = new Object[]{R.drawable.makeup2_fadou_b_xiajiemao};

            datas[6] = new MakeupRes.MakeupData();
            datas[6].m_defAlpha = 75;
            datas[6].m_ex = 0xffffe4e4;
            datas[6].m_makeupType = MakeupType.FOUNDATION.GetValue();
        }
        makeupRes.m_groupRes = datas;
        makeup2Res.m_makeupRes = makeupRes;

        decalRess = new ArrayList<>();
        res = new DecalRes();
        res.m_type = "head";
        res.m_scale = 1.75f;
        res.m_offset = new float[]{0f,-0.45f};
        res.m_res = R.drawable.__makeup2__heischangguihead08031;
        decalRess.add(res);

        res = new DecalRes();
        res.m_type = "eye";
        res.m_scale = 1.7f;
        res.m_offset = new float[]{0f, 0.65f};
        res.m_res = R.drawable.__makeup2__heischangguieye0802;
        decalRess.add(res);

        res = new DecalRes();
        res.m_type = "shoulder";
        res.m_scale = 0.3f;
        res.m_offset = new float[]{0f, -0.3f};
        res.m_res = R.drawable.__makeup2__heischangguishloud0802;
        decalRess.add(res);
        makeup2Res.m_DecalRess = decalRess;
        ress.add(makeup2Res);


        makeup2Res = new Makeup2Res();
        makeup2Res.m_id = 1000015;
        makeup2Res.m_name = "甜心汪";
        makeup2Res.m_thumb = R.drawable.makeup2_fadou_thumb_w2;
        makeup2Res.m_maskColor = item.m_maskColor;
        makeup2Res.m_shapeType = filter.ShapeType.CuteDog;

        makeupRes = new MakeupRes();
        datas = new MakeupRes.MakeupData[6];
        {
            datas[0] = new MakeupRes.MakeupData();
            datas[0].m_makeupType = MakeupType.EYE_L.GetValue();
            datas[0].m_defAlpha = 30;
            datas[0].m_ex = 1;
            datas[0].m_res = new Object[]{R.drawable.makeup2_fadou_w_meitong};

            datas[1] = new MakeupRes.MakeupData();
            datas[1].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f};
            datas[1].m_params = new float[]{0.5f,10.0f,1.0f,0f};
            datas[1].m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
            datas[1].m_defAlpha = 50;
            datas[1].m_ex = 1;
            datas[1].m_res = new Object[]{R.drawable.makeup2_fadou_w_shangjiemao1,R.drawable.makeup2_fadou_w_shangjiemao2};

            datas[2] = new MakeupRes.MakeupData();
            datas[2].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f};
            datas[2].m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
            datas[2].m_defAlpha = 80;
            datas[2].m_ex = 1;
            datas[2].m_res = new Object[]{R.drawable.makeup2_fadou_w_yanxian};

            datas[3] = new MakeupRes.MakeupData();
            datas[3].m_makeupType = MakeupType.LIP.GetValue();
            datas[3].m_defAlpha = 70;
            datas[3].m_ex = 41;
            datas[3].m_res = new Object[]{R.drawable.makeup2_fadou_w_chuncai};

            datas[4] = new MakeupRes.MakeupData();
            datas[4].m_defAlpha = 50;
            datas[4].m_ex = 38;
            datas[4].m_makeupType = MakeupType.KOHL_L.GetValue();
            datas[4].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f,160.0f,129.0f};
            datas[4].m_res = new Object[]{R.drawable.makeup2_fadou_w_yanying};

            datas[5] = new MakeupRes.MakeupData();
            datas[5].m_defAlpha = 75;
            datas[5].m_ex = 0xffffe4e4;
            datas[5].m_makeupType = MakeupType.FOUNDATION.GetValue();
        }
        makeupRes.m_groupRes = datas;
        makeup2Res.m_makeupRes = makeupRes;

        decalRess = new ArrayList<>();
        res = new DecalRes();
        res.m_type = "head";
        res.m_scale = 1.8f;
        res.m_offset = new float[]{0f,-0.6f};
        res.m_res = R.drawable.__makeup2__baisekaixhead0727;
        decalRess.add(res);

        res = new DecalRes();
        res.m_type = "eye";
        res.m_scale = 2.25f;
        res.m_offset = new float[]{0f, 0f};
        res.m_res = R.drawable.__makeup2__baisekaixeye0731;
        decalRess.add(res);

        res = new DecalRes();
        res.m_type = "nose";
        res.m_scale = 1.2f;
        res.m_offset = new float[]{0f, -0.1f};
        res.m_res = R.drawable.__makeup2__baisekaixnose10728;
        decalRess.add(res);

        makeup2Res.m_DecalRess = decalRess;
        ress.add(makeup2Res);


        makeup2Res = new Makeup2Res();
        makeup2Res.m_id = 1000014;
        makeup2Res.m_name = "俊介君";
        makeup2Res.m_thumb = R.drawable.makeup2_fadou_thumb_b1;
        makeup2Res.m_maskColor = item.m_maskColor;
        makeup2Res.m_shapeType = filter.ShapeType.CuteDog;

        makeupRes = new MakeupRes();
        datas = new MakeupRes.MakeupData[7];
        {
            datas[0] = new MakeupRes.MakeupData();
            datas[0].m_makeupType = MakeupType.EYE_L.GetValue();
            datas[0].m_defAlpha = 30;
            datas[0].m_ex = 1;
            datas[0].m_res = new Object[]{R.drawable.makeup2_fadou_b_meitong};

            datas[1] = new MakeupRes.MakeupData();
            datas[1].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f};
            datas[1].m_params = new float[]{1.0f,0.0f,1.0f,-20f};
            datas[1].m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
            datas[1].m_defAlpha = 50;
            datas[1].m_ex = 1;
            datas[1].m_res = new Object[]{R.drawable.makeup2_fadou_b_shangjiemao1,R.drawable.makeup2_fadou_b_shangjiemao1};

            datas[2] = new MakeupRes.MakeupData();
            datas[2].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f};
            datas[2].m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
            datas[2].m_defAlpha = 50;
            datas[2].m_ex = 1;
            datas[2].m_res = new Object[]{R.drawable.makeup2_fadou_b_yanxian};

            datas[3] = new MakeupRes.MakeupData();
            datas[3].m_makeupType = MakeupType.LIP.GetValue();
            datas[3].m_defAlpha = 70;
            datas[3].m_ex = 41;
            datas[3].m_res = new Object[]{R.drawable.makeup2_fadou_b_chuncai};

            datas[4] = new MakeupRes.MakeupData();
            datas[4].m_defAlpha = 45;
            datas[4].m_ex = 38;
            datas[4].m_makeupType = MakeupType.KOHL_L.GetValue();
            datas[4].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f,160.0f,129.0f};
            datas[4].m_res = new Object[]{R.drawable.makeup2_fadou_b_yanying};

            datas[5] = new MakeupRes.MakeupData();
            datas[5].m_pos = new float[]{255.0f,118.0f,160.0f,129.0f,86.0f,101.0f};
            datas[5].m_makeupType = MakeupType.EYELASH_DOWN_L.GetValue();
            datas[5].m_defAlpha = 50;
            datas[5].m_ex = 1;
            datas[5].m_res = new Object[]{R.drawable.makeup2_fadou_b_xiajiemao};

            datas[6] = new MakeupRes.MakeupData();
            datas[6].m_defAlpha = 75;
            datas[6].m_ex = 0xffffe4e4;
            datas[6].m_makeupType = MakeupType.FOUNDATION.GetValue();
        }
        makeupRes.m_groupRes = datas;
        makeup2Res.m_makeupRes = makeupRes;

        decalRess = new ArrayList<>();
        res = new DecalRes();
        res.m_type = "head";
        res.m_scale = 1.6f;
        res.m_offset = new float[]{0f,-0.5f};
        res.m_res = R.drawable.__makeup2__huangschangguihead0727;
        decalRess.add(res);

        res = new DecalRes();
        res.m_type = "eye";
        res.m_scale = 2f;
        res.m_offset = new float[]{0.01f, 0f};
        res.m_res = R.drawable.__makeup2__huangschanggueye0727;
        decalRess.add(res);

        res = new DecalRes();
        res.m_type = "nose";
        res.m_scale = 1.5f;
        res.m_offset = new float[]{0f, -0.05f};
        res.m_res = R.drawable.__makeup2__huangschangguinose0727;
        decalRess.add(res);

        res = new DecalRes();
        res.m_type = "shoulder";
        res.m_scale = 0.22f;
        res.m_offset = new float[]{0f, -0.3f};
        res.m_res = R.drawable.__makeup2__huangschangguishloud0727;
        decalRess.add(res);
        makeup2Res.m_DecalRess = decalRess;
        ress.add(makeup2Res);

        item.m_makeup2Ress = ress;
        out.add(item);

        item = new Makeup2ResGroup();
        item.m_name = "喵星人";
        item.m_uri = 1;
        item.m_thumb = R.drawable.makeup2_theme_cat;
        item.m_maskColor = 0xffbbaee6;
        ress = new ArrayList<>();
        makeup2Res = new Makeup2Res();
        makeup2Res.m_id = 1000001;
        makeup2Res.m_name = "小黑猫";
        makeup2Res.m_thumb = R.drawable.makeup2_cat1_thumb;
        makeup2Res.m_maskColor = item.m_maskColor;
        makeup2Res.m_shapeType = filter.ShapeType.CatFace;
        //小黑猫
        makeupRes = new MakeupRes();
        datas = new MakeupRes.MakeupData[8];
        {
            datas[0] = new MakeupRes.MakeupData();
            datas[0].m_makeupType = MakeupType.EYE_L.GetValue();
            datas[0].m_defAlpha = 35;
            datas[0].m_ex = 1;
            datas[0].m_res = new Object[]{R.drawable.makeup2_mao1_meitong};

            datas[1] = new MakeupRes.MakeupData();
            datas[1].m_pos = new float[]{206.0f,109.0f,121.0f,66.0f,49.0f,104.0f};
            datas[1].m_params = new float[]{1.0f,0.0f,1.0f,-35f};
            datas[1].m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
            datas[1].m_defAlpha = 60;
            datas[1].m_ex = 1;
            datas[1].m_res = new Object[]{R.drawable.makeup2_mao1_jiemao1,R.drawable.makeup2_mao1_jiemao2};

            datas[2] = new MakeupRes.MakeupData();
            datas[2].m_pos = new float[]{206.0f,109.0f,121.0f,66.0f,49.0f,104.0f};
            datas[2].m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
            datas[2].m_defAlpha = 80;
            datas[2].m_ex = 1;
            datas[2].m_res = new Object[]{R.drawable.makeup2_mao1_yanxian};

            datas[3] = new MakeupRes.MakeupData();
            datas[3].m_makeupType = MakeupType.LIP.GetValue();
            datas[3].m_defAlpha = 50;
            datas[3].m_ex = 41;
            datas[3].m_res = new Object[]{R.drawable.makeup2_mao1_chuncai};

            datas[4] = new MakeupRes.MakeupData();
            datas[4].m_defAlpha = 100;
            datas[4].m_ex = 1;
            datas[4].m_makeupType = MakeupType.KOHL_L.GetValue();
            datas[4].m_pos = new float[]{263.0f,134.0f,165.0f,77.0f,93.0f,118.0f,165.0f,141.0f};
            datas[4].m_res = new Object[]{R.drawable.makeup2_mao1_yanying};

            datas[5] = new MakeupRes.MakeupData();
            datas[5].m_pos = new float[]{206.0f,109.0f,122.0f,127.0f,49.0f,104.0f};
            datas[5].m_makeupType = MakeupType.EYELASH_DOWN_L.GetValue();
            datas[5].m_defAlpha = 60;
            datas[5].m_ex = 1;
            datas[5].m_res = new Object[]{R.drawable.makeup2_mao1_xiajiemao};

            datas[6] = new MakeupRes.MakeupData();
            datas[6].m_defAlpha = 50;
            datas[6].m_ex = 1;
            datas[6].m_makeupType = MakeupType.CHEEK_L.GetValue();
            datas[6].m_res = new Object[]{R.drawable.makeup2_mao1_saihong};

            datas[7] = new MakeupRes.MakeupData();
            datas[7].m_defAlpha = 75;
            datas[7].m_ex = 0xffffe4e4;
            datas[7].m_makeupType = MakeupType.FOUNDATION.GetValue();
        }
        makeupRes.m_groupRes = datas;
        makeup2Res.m_makeupRes = makeupRes;
        //小黑猫
        decalRess = new ArrayList<>();
        res = new DecalRes();
        res.m_type = "head";
        res.m_scale = 1.6f;
        res.m_offset = new float[]{-0.025f,-0.57f};
        res.m_res = R.drawable.__makeup2__maohead5191;
        decalRess.add(res);

        res = new DecalRes();
        res.m_type = "eye";
        res.m_scale = 1.5f;
        res.m_offset = new float[]{0.025f, 0.45f};
        res.m_res = R.drawable.__makeup2__maonose0608;
        decalRess.add(res);

        res = new DecalRes();
        res.m_type = "nose";
        res.m_scale = 2.2f;
        res.m_offset = new float[]{0f, 1.74f};
        res.m_res = R.drawable.__makeup2__maoshoulder5193;
        decalRess.add(res);
        makeup2Res.m_DecalRess = decalRess;
        ress.add(makeup2Res);


        makeup2Res = new Makeup2Res();
        makeup2Res.m_id = 1000002;
        makeup2Res.m_name = "糖果猫咪";
        makeup2Res.m_thumb = R.drawable.makeup2_cat2_thumb;
        makeup2Res.m_maskColor = item.m_maskColor;
        makeup2Res.m_shapeType = filter.ShapeType.CatFace;
        //糖果猫咪
        makeupRes = new MakeupRes();
        datas = new MakeupRes.MakeupData[8];
        {
            datas[0] = new MakeupRes.MakeupData();
            datas[0].m_id = 56688;
            datas[0].m_makeupType = MakeupType.EYE_L.GetValue();
            datas[0].m_defAlpha = 55;
            datas[0].m_ex = 1;
            datas[0].m_res = new Object[]{R.drawable.makeup2_mao3_meitong};

            datas[1] = new MakeupRes.MakeupData();
            datas[1].m_pos = new float[]{206.0f,109.0f,121.0f,66.0f,49.0f,104.0f};
            datas[1].m_params = new float[]{1.0f,0.0f,0.5f,15.0f};
            datas[1].m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
            datas[1].m_defAlpha = 70;
            datas[1].m_ex = 1;
            datas[1].m_res = new Object[]{R.drawable.makeup2_mao3_shangjiemao1,R.drawable.makeup2_mao3_shangjiemao2};

            datas[2] = new MakeupRes.MakeupData();
            datas[2].m_pos = new float[]{206.0f,109.0f,122.0f,127.0f,49.0f,104.0f};
            datas[2].m_makeupType = MakeupType.EYELASH_DOWN_L.GetValue();
            datas[2].m_defAlpha = 70;
            datas[2].m_ex = 1;
            datas[2].m_res = new Object[]{R.drawable.makeup2_mao3_xiajiemao};

            datas[3] = new MakeupRes.MakeupData();
            datas[3].m_pos = new float[]{206.0f,109.0f,121.0f,66.0f,49.0f,104.0f};
            datas[3].m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
            datas[3].m_defAlpha = 100;
            datas[3].m_ex = 1;
            datas[3].m_res = new Object[]{R.drawable.makeup2_mao3_yanxian};

//            datas[4] = new MakeupRes.MakeupData();
//            datas[4].m_pos = new float[]{192.0f,54.0f,93.0f,32.0f,93.0f,49.0f,32.0f,57.0f};
//            datas[4].m_makeupType = MakeupType.EYEBROW_L.GetValue();
//            datas[4].m_defAlpha = 60;
//            datas[4].m_ex = 1;
//            datas[4].m_res = new Object[]{R.drawable.makeup2_mao3_meimao};

            datas[4] = new MakeupRes.MakeupData();
            datas[4].m_makeupType = MakeupType.LIP.GetValue();
            datas[4].m_defAlpha = 35;
            datas[4].m_ex = 41;
            datas[4].m_res = new Object[]{R.drawable.makeup2_mao3_chuncai};

            datas[5] = new MakeupRes.MakeupData();
            datas[5].m_defAlpha = 70;
            datas[5].m_ex = 38;
            datas[5].m_makeupType = MakeupType.KOHL_L.GetValue();
            datas[5].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f,160.0f,129.0f};
            datas[5].m_res = new Object[]{R.drawable.makeup2_mao3_yanying};

            datas[6] = new MakeupRes.MakeupData();
            datas[6].m_defAlpha = 50;
            datas[6].m_ex = 1;
            datas[6].m_id = 33496;
            datas[6].m_makeupType = MakeupType.CHEEK_L.GetValue();
            datas[6].m_res = new Object[]{R.drawable.makeup2_mao3_saihong};

            datas[7] = new MakeupRes.MakeupData();
            datas[7].m_defAlpha = 75;
            datas[7].m_ex = 0xffffe4e4;
            datas[7].m_makeupType = MakeupType.FOUNDATION.GetValue();
        }
        makeupRes.m_groupRes = datas;
        makeup2Res.m_makeupRes = makeupRes;
        //糖果猫咪
        decalRess = new ArrayList<>();
        res = new DecalRes();
        res.m_type = "head";
        res.m_scale = 1.8f;
        res.m_offset = new float[]{-0.03f, -0.6f};
        res.m_res = R.drawable.__makeup2__tangguohead5192;
        decalRess.add(res);

        res = new DecalRes();
        res.m_type = "eye";
        res.m_scale = 2.65f;
        res.m_offset = new float[]{0f, 0f};
        res.m_res = R.drawable.__makeup2__tangguoframe5193;
        decalRess.add(res);

        res = new DecalRes();
        res.m_type = "nose";
        res.m_scale = 3.5f;
        res.m_offset = new float[]{0f, -0.05f};
        res.m_res = R.drawable.__makeup2__tangguonose5191;
        decalRess.add(res);
        makeup2Res.m_DecalRess = decalRess;
        ress.add(makeup2Res);



        makeup2Res = new Makeup2Res();
        makeup2Res.m_id = 1000003;
        makeup2Res.m_name = "天使猫";
        makeup2Res.m_thumb = R.drawable.makeup2_cat3_thumb;
        makeup2Res.m_maskColor = item.m_maskColor;
        makeup2Res.m_shapeType = filter.ShapeType.CatFace;
        makeupRes = new MakeupRes();
        datas = new MakeupRes.MakeupData[8];
        {
            datas[0] = new MakeupRes.MakeupData();
            datas[0].m_makeupType = MakeupType.EYE_L.GetValue();
            datas[0].m_defAlpha = 80;
            datas[0].m_ex = 1;
            datas[0].m_res = new Object[]{R.drawable.makeup2_mao2_meitong};

            datas[1] = new MakeupRes.MakeupData();
            datas[1].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f};
            datas[1].m_params = new float[]{1.0f,0.0f,0.5f,15.0f};
            datas[1].m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
            datas[1].m_defAlpha = 60;
            datas[1].m_ex = 1;
            datas[1].m_res = new Object[]{R.drawable.makeup2_mao2_jiemao,R.drawable.makeup2_mao2_shangjiemao};

            datas[2] = new MakeupRes.MakeupData();
            datas[2].m_pos = new float[]{255.0f,118.0f,160.0f,129.0f,86.0f,101.0f};
            datas[2].m_makeupType = MakeupType.EYELASH_DOWN_L.GetValue();
            datas[2].m_defAlpha = 60;
            datas[2].m_ex = 1;
            datas[2].m_res = new Object[]{R.drawable.makeup2_mao2_xiajiemao};

            datas[3] = new MakeupRes.MakeupData();
            datas[3].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f};
            datas[3].m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
            datas[3].m_defAlpha = 70;
            datas[3].m_ex = 1;
            datas[3].m_res = new Object[]{R.drawable.makeup2_mao2_yanxian};

            datas[4] = new MakeupRes.MakeupData();
            datas[4].m_pos = new float[]{211.0f,53.0f,98.0f,27.0f,98.0f,55.0f,16.0f,63.0f};
            datas[4].m_makeupType = MakeupType.EYEBROW_L.GetValue();
            datas[4].m_defAlpha = 40;
            datas[4].m_ex = 1;
            datas[4].m_res = new Object[]{R.drawable.makeup2_mao2_meimao};

            datas[5] = new MakeupRes.MakeupData();
            datas[5].m_makeupType = MakeupType.LIP.GetValue();
            datas[5].m_defAlpha = 100;
            datas[5].m_ex = 41;
            datas[5].m_res = new Object[]{R.drawable.makeup2_mao2_chuncai};

            datas[6] = new MakeupRes.MakeupData();
            datas[6].m_defAlpha = 40;
            datas[6].m_ex = 38;
            datas[6].m_makeupType = MakeupType.KOHL_L.GetValue();
            datas[6].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f,160.0f,129.0f};
            datas[6].m_res = new Object[]{R.drawable.makeup2_mao2_yanying};

            datas[7] = new MakeupRes.MakeupData();
            datas[7].m_defAlpha = 75;
            datas[7].m_ex = 0xffffe4e4;
            datas[7].m_makeupType = MakeupType.FOUNDATION.GetValue();
        }
        makeupRes.m_groupRes = datas;
        makeup2Res.m_makeupRes = makeupRes;
        decalRess = new ArrayList<>();
        res = new DecalRes();
        res.m_type = "head";
        res.m_scale = 2f;
        res.m_offset = new float[]{-0.025f, -0.55f};
        res.m_res = R.drawable.__makeup2__tianshimaohead5191;
        decalRess.add(res);

        res = new DecalRes();
        res.m_type = "shoulder";
        res.m_scale = 0.9f;
        res.m_offset = new float[]{0f, -0.6f};
        res.m_res = R.drawable.__makeup2__tianshimaoshoulder5193;
        decalRess.add(res);

        res = new DecalRes();
        res.m_type = "nose";
        res.m_scale = 3.7f;
        res.m_offset = new float[]{0f,0f};
        res.m_res = R.drawable.__makeup2__tianshimaonose5192;
        decalRess.add(res);
        makeup2Res.m_DecalRess = decalRess;
        ress.add(makeup2Res);
        item.m_makeup2Ress = ress;
        out.add(item);


        item = new Makeup2ResGroup();
        item.m_name = "兔纸酱";
        item.m_uri = 2;
        item.m_thumb = R.drawable.makeup2_theme_rabbit;
        item.m_maskColor = 0xfffcbbd1;
        ress = new ArrayList<>();
        makeup2Res = new Makeup2Res();
        makeup2Res.m_id = 1000004;
        makeup2Res.m_name = "草莓兔";
        makeup2Res.m_thumb = R.drawable.makeup2_rabbit1_thumb;
        makeup2Res.m_maskColor = item.m_maskColor;
        makeup2Res.m_shapeType = filter.ShapeType.RabbitFace;
        makeupRes = new MakeupRes();
        datas = new MakeupRes.MakeupData[8];
        {
            datas[0] = new MakeupRes.MakeupData();
            datas[0].m_makeupType = MakeupType.EYE_L.GetValue();
            datas[0].m_defAlpha = 45;
            datas[0].m_ex = 1;
            datas[0].m_res = new Object[]{R.drawable.makeup2_tu1_meitong};

            datas[1] = new MakeupRes.MakeupData();
            datas[1].m_pos = new float[]{206.0f,109.0f,121.0f,66.0f,49.0f,104.0f};
            datas[1].m_params = new float[]{1.0f,0.0f, 0.0f, 0.0f};
            datas[1].m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
            datas[1].m_defAlpha = 60;
            datas[1].m_ex = 1;
            datas[1].m_res = new Object[]{R.drawable.makeup2_tu1_shangjiemao,R.drawable.makeup2_tu1_shangjiemao};

            datas[2] = new MakeupRes.MakeupData();
            datas[2].m_pos = new float[]{206.0f,109.0f,122.0f,127.0f,49.0f,104.0f};
            datas[2].m_makeupType = MakeupType.EYELASH_DOWN_L.GetValue();
            datas[2].m_defAlpha = 60;
            datas[2].m_ex = 1;
            datas[2].m_res = new Object[]{R.drawable.makeup2_tu1_xiajiemao};

            datas[3] = new MakeupRes.MakeupData();
            datas[3].m_pos = new float[]{206.0f,109.0f,121.0f,66.0f,49.0f,104.0f};
            datas[3].m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
            datas[3].m_defAlpha = 100;
            datas[3].m_ex = 1;
            datas[3].m_res = new Object[]{R.drawable.makeup2_tu1_yanxian};

            datas[4] = new MakeupRes.MakeupData();
            datas[4].m_pos = new float[]{203.0f,67.0f,100.0f,24.0f,100.0f,53.0f,19.0f,61.0f};
            datas[4].m_makeupType = MakeupType.EYEBROW_L.GetValue();
            datas[4].m_defAlpha = 60;
            datas[4].m_ex = 1;
            datas[4].m_res = new Object[]{R.drawable.makeup2_tu1_meimao};

            datas[5] = new MakeupRes.MakeupData();
            datas[5].m_makeupType = MakeupType.LIP.GetValue();
            datas[5].m_defAlpha = 70;
            datas[5].m_ex = 41;
            datas[5].m_res = new Object[]{R.drawable.makeup2_tu1_chuncai};

            datas[6] = new MakeupRes.MakeupData();
            datas[6].m_defAlpha = 70;
            datas[6].m_ex = 38;
            datas[6].m_makeupType = MakeupType.KOHL_L.GetValue();
            datas[6].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f,160.0f,129.0f};
            datas[6].m_res = new Object[]{R.drawable.makeup2_tu1_yanying};

            datas[7] = new MakeupRes.MakeupData();
            datas[7].m_defAlpha = 75;
            datas[7].m_ex = 0xffffe4e4;
            datas[7].m_makeupType = MakeupType.FOUNDATION.GetValue();
        }
        makeupRes.m_groupRes = datas;
        makeup2Res.m_makeupRes = makeupRes;

        decalRess = new ArrayList<>();
        res = new DecalRes();
        res.m_type = "head";
        res.m_scale = 2.5f;
        res.m_offset = new float[]{0f,-0.65f};
        res.m_res = R.drawable.__makeup2__caomeiruantuhead5191;
        decalRess.add(res);

        res = new DecalRes();
        res.m_type = "nose";
        res.m_scale = 3.4f;
        res.m_offset = new float[]{0f, -0.29f};
        res.m_res = R.drawable.__makeup2__caomeiruantunose0608;
        decalRess.add(res);

        res = new DecalRes();
        res.m_type = "eye";
        res.m_scale = 2.8f;
        res.m_offset = new float[]{0f, -0.35f};
        res.m_res = R.drawable.__makeup2__caomeiruantuframe5193;
        decalRess.add(res);
        makeup2Res.m_DecalRess = decalRess;
        ress.add(makeup2Res);


        makeup2Res = new Makeup2Res();
        makeup2Res.m_id = 1000005;
        makeup2Res.m_name = "气球派对";
        makeup2Res.m_thumb = R.drawable.makeup2_rabbit2_thumb;
        makeup2Res.m_maskColor = item.m_maskColor;
        makeup2Res.m_shapeType = filter.ShapeType.RabbitFace;
        makeupRes = new MakeupRes();
        datas = new MakeupRes.MakeupData[7];
        {
            datas[0] = new MakeupRes.MakeupData();
            datas[0].m_id = 56688;
            datas[0].m_makeupType = MakeupType.EYE_L.GetValue();
            datas[0].m_defAlpha = 55;
            datas[0].m_ex = 1;
            datas[0].m_res = new Object[]{R.drawable.makeup2_tu2_meitong};

            datas[1] = new MakeupRes.MakeupData();
            datas[1].m_pos = new float[]{206.0f,109.0f,121.0f,66.0f,49.0f,104.0f};
            datas[1].m_params = new float[]{1.0f,0.0f,0.5f,15.0f};
            datas[1].m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
            datas[1].m_defAlpha = 60;
            datas[1].m_ex = 1;
            datas[1].m_res = new Object[]{R.drawable.makeup2_tu2_shangjiemao1,R.drawable.makeup2_tu2_shangjiemao2};

            datas[2] = new MakeupRes.MakeupData();
            datas[2].m_pos = new float[]{206.0f,109.0f,122.0f,127.0f,49.0f,104.0f};
            datas[2].m_makeupType = MakeupType.EYELASH_DOWN_L.GetValue();
            datas[2].m_defAlpha = 60;
            datas[2].m_ex = 1;
            datas[2].m_res = new Object[]{R.drawable.makeup2_tu2_xiajiemao};

            datas[3] = new MakeupRes.MakeupData();
            datas[3].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f};
            datas[3].m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
            datas[3].m_defAlpha = 60;
            datas[3].m_ex = 1;
            datas[3].m_res = new Object[]{R.drawable.makeup2_tu2_yanxian};

//            datas[4] = new MakeupRes.MakeupData();
//            datas[4].m_pos = new float[]{199.0f,62.0f,92.0f,25.0f,92.0f,55.0f,32.0f,64.0f};
//            datas[4].m_makeupType = MakeupType.EYEBROW_L.GetValue();
//            datas[4].m_defAlpha = 70;
//            datas[4].m_ex = 1;
//            datas[4].m_res = new Object[]{R.drawable.makeup2_tu2_meimao};

            datas[4] = new MakeupRes.MakeupData();
            datas[4].m_makeupType = MakeupType.LIP.GetValue();
            datas[4].m_defAlpha = 80;
            datas[4].m_ex = 41;
            datas[4].m_res = new Object[]{R.drawable.makeup2_tu2_chuncai};

            datas[5] = new MakeupRes.MakeupData();
            datas[5].m_defAlpha = 50;
            datas[5].m_ex = 34;
            datas[5].m_makeupType = MakeupType.KOHL_L.GetValue();
            datas[5].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f,160.0f,129.0f};
            datas[5].m_res = new Object[]{R.drawable.makeup2_tu2_yanying};

            datas[6] = new MakeupRes.MakeupData();
            datas[6].m_defAlpha = 75;
            datas[6].m_ex = 0xffffe4e4;
            datas[6].m_makeupType = MakeupType.FOUNDATION.GetValue();
        }
        makeupRes.m_groupRes = datas;
        makeup2Res.m_makeupRes = makeupRes;
        decalRess = new ArrayList<>();
        res = new DecalRes();
        res.m_type = "head";
        res.m_scale = 2.5f;
        res.m_offset = new float[]{0f, -0.15f};
        res.m_res = R.drawable.__makeup2__qiqiupaiduihead5191;
        decalRess.add(res);

        res = new DecalRes();
        res.m_type = "nose";
        res.m_scale = 3.7f;
        res.m_offset = new float[]{0f, -0.1f};
        res.m_res = R.drawable.__makeup2__qiqiupaiduinose0608;
        decalRess.add(res);
        makeup2Res.m_DecalRess = decalRess;
        ress.add(makeup2Res);

        makeup2Res = new Makeup2Res();
        makeup2Res.m_id = 1000006;
        makeup2Res.m_name = "山兔";
        makeup2Res.m_thumb = R.drawable.makeup2_rabbit3_thumb;
        makeup2Res.m_maskColor = item.m_maskColor;
        makeup2Res.m_shapeType = filter.ShapeType.RabbitFace;
        makeupRes = new MakeupRes();
        datas = new MakeupRes.MakeupData[8];
        {
            datas[0] = new MakeupRes.MakeupData();
            datas[0].m_makeupType = MakeupType.EYE_L.GetValue();
            datas[0].m_defAlpha = 60;
            datas[0].m_ex = 1;
            datas[0].m_res = new Object[]{R.drawable.makeup2_tu3_meitong};

            datas[1] = new MakeupRes.MakeupData();
            datas[1].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f};
            datas[1].m_params = new float[]{1.0f,0.0f,0.0f,0.0f};
            datas[1].m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
            datas[1].m_defAlpha = 60;
            datas[1].m_ex = 1;
            datas[1].m_res = new Object[]{R.drawable.makeup2_tu3_shangjiemao,R.drawable.makeup2_tu3_shangjiemao};

            datas[2] = new MakeupRes.MakeupData();
            datas[2].m_pos = new float[]{255.0f,118.0f,160.0f,129.0f,86.0f,101.0f};
            datas[2].m_makeupType = MakeupType.EYELASH_DOWN_L.GetValue();
            datas[2].m_defAlpha = 60;
            datas[2].m_ex = 1;
            datas[2].m_res = new Object[]{R.drawable.makeup2_tu3_xiajiemao};

            datas[3] = new MakeupRes.MakeupData();
            datas[3].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f};
            datas[3].m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
            datas[3].m_defAlpha = 80;
            datas[3].m_ex = 1;
            datas[3].m_res = new Object[]{R.drawable.makeup2_tu3_yanxian};

//            datas[4] = new MakeupRes.MakeupData();
//            datas[4].m_pos = new float[]{192.0f,57.0f,91.0f,25.0f,91.0f,52.0f,33.0f,59.0f};
//            datas[4].m_makeupType = MakeupType.EYEBROW_L.GetValue();
//            datas[4].m_defAlpha = 60;
//            datas[4].m_ex = 1;
//            datas[4].m_res = new Object[]{R.drawable.makeup2_tu3_meimao};

            datas[4] = new MakeupRes.MakeupData();
            datas[4].m_makeupType = MakeupType.LIP.GetValue();
            datas[4].m_defAlpha = 80;
            datas[4].m_ex = 41;
            datas[4].m_res = new Object[]{R.drawable.makeup2_tu3_chuncai};

            datas[5] = new MakeupRes.MakeupData();
            datas[5].m_defAlpha = 70;
            datas[5].m_ex = 38;
            datas[5].m_makeupType = MakeupType.KOHL_L.GetValue();
            datas[5].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f,160.0f,129.0f};
            datas[5].m_res = new Object[]{R.drawable.makeup2_tu3_yanying};

            datas[6] = new MakeupRes.MakeupData();
            datas[6].m_defAlpha = 40;
            datas[6].m_ex = 1;
            datas[6].m_makeupType = MakeupType.CHEEK_L.GetValue();
            datas[6].m_res = new Object[]{R.drawable.makeup2_tu3_saihong};

            datas[7] = new MakeupRes.MakeupData();
            datas[7].m_defAlpha = 75;
            datas[7].m_ex = 0xffffe4e4;
            datas[7].m_makeupType = MakeupType.FOUNDATION.GetValue();

        }
        makeupRes.m_groupRes = datas;
        makeup2Res.m_makeupRes = makeupRes;
        decalRess = new ArrayList<>();
        res = new DecalRes();
        res.m_type = "head";
        res.m_scale = 2.2f;
        res.m_offset = new float[]{-0f, -0.25f};
        res.m_res = R.drawable.__makeup2__shantuhead5191;
        decalRess.add(res);
        makeup2Res.m_DecalRess = decalRess;
        ress.add(makeup2Res);
        item.m_makeup2Ress = ress;
        out.add(item);


        item = new Makeup2ResGroup();
        item.m_name = "异次元";
        item.m_uri = 3;
        item.m_thumb = R.drawable.maekup2_theme_cartoon;
        item.m_maskColor = 0xff8aace4;
        ress = new ArrayList<>();
        makeup2Res = new Makeup2Res();
        makeup2Res.m_id = 1000007;
        makeup2Res.m_name = "乔巴";
        makeup2Res.m_thumb = R.drawable.makeup2_catoon1_thumb;
        makeup2Res.m_maskColor = item.m_maskColor;
        makeup2Res.m_shapeType = filter.ShapeType.CartoonFace;
        makeupRes = new MakeupRes();
        datas = new MakeupRes.MakeupData[7];
        {
            datas[0] = new MakeupRes.MakeupData();
            datas[0].m_makeupType = MakeupType.EYE_L.GetValue();
            datas[0].m_defAlpha = 80;
            datas[0].m_ex = 1;
            datas[0].m_res = new Object[]{R.drawable.makeup2_katong1_meitong};

            datas[1] = new MakeupRes.MakeupData();
            datas[1].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f};
            datas[1].m_params = new float[]{1.0f,0.0f,0.0f,0.0f};
            datas[1].m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
            datas[1].m_defAlpha = 60;
            datas[1].m_ex = 1;
            datas[1].m_res = new Object[]{R.drawable.makeup2_katong1_shangjiemao,R.drawable.makeup2_katong1_shangjiemao};

            datas[2] = new MakeupRes.MakeupData();
            datas[2].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f};
            datas[2].m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
            datas[2].m_defAlpha = 80;
            datas[2].m_ex = 1;
            datas[2].m_res = new Object[]{R.drawable.makeup2_katong1_yanxian};

//            datas[3] = new MakeupRes.MakeupData();
//            datas[3].m_pos = new float[]{197.0f,56.0f,100.0f,30.0f,100.0f,48.0f,34.0f,57.0f};
//            datas[3].m_makeupType = MakeupType.EYEBROW_L.GetValue();
//            datas[3].m_defAlpha = 60;
//            datas[3].m_ex = 1;
//            datas[3].m_res = new Object[]{R.drawable.makeup2_katong1_meimao};

            datas[3] = new MakeupRes.MakeupData();
            datas[3].m_makeupType = MakeupType.LIP.GetValue();
            datas[3].m_defAlpha = 80;
            datas[3].m_ex = 41;
            datas[3].m_res = new Object[]{R.drawable.makeup2_katong1_chuncai};

            datas[4] = new MakeupRes.MakeupData();
            datas[4].m_defAlpha = 60;
            datas[4].m_ex = 30;
            datas[4].m_makeupType = MakeupType.KOHL_L.GetValue();
            datas[4].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f,160.0f,129.0f};
            datas[4].m_res = new Object[]{R.drawable.makeup2_katong1_yanying};

            datas[5] = new MakeupRes.MakeupData();
            datas[5].m_defAlpha = 30;
            datas[5].m_ex = 1;
            datas[5].m_id = 33496;
            datas[5].m_makeupType = MakeupType.CHEEK_L.GetValue();
            datas[5].m_res = new Object[]{R.drawable.makeup2_katong1_saihong};

            datas[6] = new MakeupRes.MakeupData();
            datas[6].m_defAlpha = 75;
            datas[6].m_ex = 0xffffe4e4;
            datas[6].m_makeupType = MakeupType.FOUNDATION.GetValue();
        }
        makeupRes.m_groupRes = datas;
        makeup2Res.m_makeupRes = makeupRes;
        decalRess = new ArrayList<>();

        res = new DecalRes();
        res.m_type = "head";
        res.m_scale = 1.8f;
        res.m_offset = new float[]{0.28f, -0.45f};
        res.m_res = R.drawable.__makeup2__qiaobahead5191;
        decalRess.add(res);

        res = new DecalRes();
        res.m_type = "nose";
        res.m_scale = 0.4f;
        res.m_offset = new float[]{0f, 0f};
        res.m_res = R.drawable.__makeup2__qiaobanose5192;
        decalRess.add(res);

        res = new DecalRes();
        res.m_type = "shoulder";
        res.m_scale = 0.4f;
        res.m_offset = new float[]{0f, 0f};
        res.m_res = R.drawable.__makeup2__qiaobashoulder5193;
        decalRess.add(res);
        makeup2Res.m_DecalRess = decalRess;
        ress.add(makeup2Res);

        makeup2Res = new Makeup2Res();
        makeup2Res.m_id = 1000008;
        makeup2Res.m_name = "阿拉蕾";
        makeup2Res.m_thumb = R.drawable.makeup2_catoon2_thumb;
        makeup2Res.m_maskColor = item.m_maskColor;
        makeup2Res.m_shapeType = filter.ShapeType.CartoonFace;
        makeupRes = new MakeupRes();
        datas = new MakeupRes.MakeupData[6];
        {
            datas[0] = new MakeupRes.MakeupData();
            datas[0].m_makeupType = MakeupType.EYE_L.GetValue();
            datas[0].m_defAlpha = 40;
            datas[0].m_ex = 1;
            datas[0].m_res = new Object[]{R.drawable.makeup2_katong2_meitong};

            datas[1] = new MakeupRes.MakeupData();
            datas[1].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f};
//            datas[1].m_params = new float[]{1.0f,0.0f,1.0f,-20.0f};
            datas[1].m_params = new float[]{1.0f,0.0f,0.0f,0.0f};
            datas[1].m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
            datas[1].m_defAlpha = 70;
            datas[1].m_ex = 1;
            datas[1].m_res = new Object[]{R.drawable.makeup2_katong2_shangjiemao,R.drawable.makeup2_katong2_shangjiemao};

            datas[2] = new MakeupRes.MakeupData();
            datas[2].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f};
            datas[2].m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
            datas[2].m_defAlpha = 100;
            datas[2].m_ex = 1;
            datas[2].m_res = new Object[]{R.drawable.makeup2_katong2_yanxian};

//            datas[3] = new MakeupRes.MakeupData();
//            datas[3].m_pos = new float[]{205.0f,61.0f,95.0f,29.0f,95.0f,58.0f,34.0f,70.0f};
//            datas[3].m_makeupType = MakeupType.EYEBROW_L.GetValue();
//            datas[3].m_defAlpha = 70;
//            datas[3].m_ex = 1;
//            datas[3].m_res = new Object[]{R.drawable.makeup2_katong2_meimao};

            datas[3] = new MakeupRes.MakeupData();
            datas[3].m_makeupType = MakeupType.LIP.GetValue();
            datas[3].m_defAlpha = 100;
            datas[3].m_ex = 41;
            datas[3].m_res = new Object[]{R.drawable.makeup2_katong2_chuncai};

            datas[4] = new MakeupRes.MakeupData();
            datas[4].m_defAlpha = 60;
            datas[4].m_ex = 30;
            datas[4].m_makeupType = MakeupType.KOHL_L.GetValue();
            datas[4].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f,160.0f,129.0f};
            datas[4].m_res = new Object[]{R.drawable.makeup2_katong2_yanying};

            datas[5] = new MakeupRes.MakeupData();
            datas[5].m_defAlpha = 75;
            datas[5].m_ex = 0xffffe4e4;
            datas[5].m_makeupType = MakeupType.FOUNDATION.GetValue();
        }
        makeupRes.m_groupRes = datas;
        makeup2Res.m_makeupRes = makeupRes;
        decalRess = new ArrayList<>();
        res = new DecalRes();
        res.m_type = "head";
        res.m_scale = 2.8f;
        res.m_offset = new float[]{0f,-0.25f};
        res.m_res = R.drawable.__makeup2__alaleihead5191;
        decalRess.add(res);

        res = new DecalRes();
        res.m_type = "eye";
        res.m_scale = 1.7f;
        res.m_offset = new float[]{0f, 0.1f};
        res.m_res = R.drawable.__makeup2__alaleieye5192;
        decalRess.add(res);

        res = new DecalRes();
        res.m_type = "nose";
        res.m_scale = 3.5f;
        res.m_offset = new float[]{0f, -0.1f};
        res.m_res = R.drawable.__makeup2__alaleinose0608;
        decalRess.add(res);

        makeup2Res.m_DecalRess = decalRess;
        ress.add(makeup2Res);


        makeup2Res = new Makeup2Res();
        makeup2Res.m_id = 1000009;
        makeup2Res.m_name = "美少女";
        makeup2Res.m_thumb = R.drawable.makeup2_catoon3_thumb;
        makeup2Res.m_maskColor = item.m_maskColor;
        makeup2Res.m_shapeType = filter.ShapeType.CartoonFace;
        makeupRes = new MakeupRes();
        datas = new MakeupRes.MakeupData[9];
        {
            datas[0] = new MakeupRes.MakeupData();
            datas[0].m_makeupType = MakeupType.EYE_L.GetValue();
            datas[0].m_defAlpha = 50;
            datas[0].m_ex = 1;
            datas[0].m_res = new Object[]{R.drawable.makeup2_katong3_meitong};

            datas[1] = new MakeupRes.MakeupData();
            datas[1].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f};
//            datas[1].m_params = new float[]{1.0f,0.0f,0.5f,15.0f};
            datas[1].m_params = new float[]{1.0f,0.0f,0.0f,0.0f};
            datas[1].m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
            datas[1].m_defAlpha = 60;
            datas[1].m_ex = 1;
            datas[1].m_res = new Object[]{R.drawable.makeup2_katong3_shangejiemao,R.drawable.makeup2_katong3_shangejiemao};

            datas[2] = new MakeupRes.MakeupData();
            datas[2].m_pos = new float[]{255.0f,118.0f,160.0f,129.0f,86.0f,101.0f};
            datas[2].m_makeupType = MakeupType.EYELASH_DOWN_L.GetValue();
            datas[2].m_defAlpha = 60;
            datas[2].m_ex = 1;
            datas[2].m_res = new Object[]{R.drawable.makeup2_katong3_xiajiemao};

            datas[3] = new MakeupRes.MakeupData();
            datas[3].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f};
            datas[3].m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
            datas[3].m_defAlpha = 80;
            datas[3].m_ex = 1;
            datas[3].m_res = new Object[]{R.drawable.makeup2_katong3_yanxian};

            datas[4] = new MakeupRes.MakeupData();
            datas[4].m_pos = new float[]{216.0f,57.0f,104.0f,28.0f,104.0f,57.0f,22.0f,60.0f};
            datas[4].m_makeupType = MakeupType.EYEBROW_L.GetValue();
            datas[4].m_defAlpha = 60;
            datas[4].m_ex = 1;
            datas[4].m_res = new Object[]{R.drawable.makeup2_katong3_meimao};

            datas[5] = new MakeupRes.MakeupData();
            datas[5].m_makeupType = MakeupType.LIP.GetValue();
            datas[5].m_defAlpha = 60;
            datas[5].m_ex = 41;
            datas[5].m_res = new Object[]{R.drawable.makeup2_katong3_chuncai};

            datas[6] = new MakeupRes.MakeupData();
            datas[6].m_defAlpha = 50;
            datas[6].m_ex = 38;
            datas[6].m_makeupType = MakeupType.KOHL_L.GetValue();
            datas[6].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f,160.0f,129.0f};
            datas[6].m_res = new Object[]{R.drawable.makeup2_katong3_yanying};

            datas[7] = new MakeupRes.MakeupData();
            datas[7].m_defAlpha = 20;
            datas[7].m_ex = 1;
            datas[7].m_makeupType = MakeupType.CHEEK_L.GetValue();
            datas[7].m_res = new Object[]{R.drawable.makeup2_katong3_saihong};

            datas[8] = new MakeupRes.MakeupData();
            datas[8].m_defAlpha = 75;
            datas[8].m_ex = 0xffffe4e4;
            datas[8].m_makeupType = MakeupType.FOUNDATION.GetValue();
        }
        makeupRes.m_groupRes = datas;
        makeup2Res.m_makeupRes = makeupRes;
        decalRess = new ArrayList<>();
        res = new DecalRes();
        res.m_type = "head";
        res.m_scale = 2.8f;
        res.m_offset = new float[]{-0.01f, 0.22f};
        res.m_res = R.drawable.__makeup2__meishaonvhead0607;
        decalRess.add(res);

        res = new DecalRes();
        res.m_type = "nose";
        res.m_scale = 3.7f;
        res.m_offset = new float[]{-0.01f, 0.3f};
        res.m_res = R.drawable.__makeup2__meishaonvnose5192;
        decalRess.add(res);

        res = new DecalRes();
        res.m_type = "shoulder";
        res.m_scale = 0.41f;
        res.m_offset = new float[]{0f, 0.2f};
        res.m_res = R.drawable.__makeup2__meishaonvshoulder5193;
        decalRess.add(res);

        res = new DecalRes();
        res.m_type = "eye";
        res.m_scale = 2.5f;
        res.m_offset = new float[]{0f, 0f};
        res.m_res = R.drawable.__makeup2__meishaonvframe5194;
        decalRess.add(res);

        makeup2Res.m_DecalRess = decalRess;
        ress.add(makeup2Res);
        item.m_makeup2Ress = ress;
        out.add(item);

        item = new Makeup2ResGroup();
        item.m_name = "小角兽";
        item.m_uri = 4;
        item.m_thumb = R.drawable.makeup2_theme_deer;
        item.m_maskColor = 0xfff8b862;
        ress = new ArrayList<>();
        makeup2Res = new Makeup2Res();
        makeup2Res.m_id = 1000010;
        makeup2Res.m_name = "麋鹿";
        makeup2Res.m_thumb = R.drawable.makeup2_deer1_thumb;
        makeup2Res.m_maskColor = item.m_maskColor;
        makeup2Res.m_shapeType = filter.ShapeType.DeerFace;
        makeupRes = new MakeupRes();
        datas = new MakeupRes.MakeupData[6];
        {
            datas[0] = new MakeupRes.MakeupData();
            datas[0].m_id = 56688;
            datas[0].m_makeupType = MakeupType.EYE_L.GetValue();
            datas[0].m_defAlpha = 50;
            datas[0].m_ex = 1;
            datas[0].m_res = new Object[]{R.drawable.makeup2_lu1_meitong};

            datas[1] = new MakeupRes.MakeupData();
            datas[1].m_pos = new float[]{206.0f,109.0f,121.0f,66.0f,49.0f,104.0f};
            datas[1].m_params = new float[]{0.5f,10.0f,1.0f,0.0f};
            datas[1].m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
            datas[1].m_defAlpha = 60;
            datas[1].m_ex = 1;
            datas[1].m_res = new Object[]{R.drawable.makeup2_lu1_jiemao1,R.drawable.makeup2_lu1_jiemao2};

            datas[2] = new MakeupRes.MakeupData();
            datas[2].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f};
            datas[2].m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
            datas[2].m_defAlpha = 90;
            datas[2].m_ex = 1;
            datas[2].m_res = new Object[]{R.drawable.makeup2_lu1_yanxian};

//            datas[3] = new MakeupRes.MakeupData();
//            datas[3].m_pos = new float[]{195.0f,56.0f,94.0f,35.0f,94.0f,54.0f,33.0f,61.0f};
//            datas[3].m_makeupType = MakeupType.EYEBROW_L.GetValue();
//            datas[3].m_defAlpha = 60;
//            datas[3].m_ex = 1;
//            datas[3].m_res = new Object[]{R.drawable.makeup2_lu1_meimao};

            datas[3] = new MakeupRes.MakeupData();
            datas[3].m_makeupType = MakeupType.LIP.GetValue();
            datas[3].m_defAlpha = 25;
            datas[3].m_ex = 41;
            datas[3].m_res = new Object[]{R.drawable.makeup2_lu1_cuncai};

            datas[4] = new MakeupRes.MakeupData();
            datas[4].m_defAlpha = 50;
            datas[4].m_ex = 34;
            datas[4].m_makeupType = MakeupType.KOHL_L.GetValue();
            datas[4].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f,160.0f,129.0f};
            datas[4].m_res = new Object[]{R.drawable.makeup2_lu1_yanying};

            datas[5] = new MakeupRes.MakeupData();
            datas[5].m_defAlpha = 75;
            datas[5].m_ex = 0xffffe4e4;
            datas[5].m_makeupType = MakeupType.FOUNDATION.GetValue();
        }
        makeupRes.m_groupRes = datas;
        makeup2Res.m_makeupRes = makeupRes;
        decalRess = new ArrayList<>();
        res = new DecalRes();
        res.m_type = "head";
        res.m_scale = 2.2f;
        res.m_offset = new float[]{0f, -1f};
        res.m_res = R.drawable.__makeup2__headlu;
        decalRess.add(res);

        res = new DecalRes();
        res.m_type = "nose";
        res.m_scale = 3.6f;
        res.m_offset = new float[]{0f, -0.2f};
        res.m_res = R.drawable.__makeup2__milunose0608;
        decalRess.add(res);

        res = new DecalRes();
        res.m_type = "eye";
        res.m_scale = 2.6f;
        res.m_offset = new float[]{0f, -0.42f};
        res.m_res = R.drawable.__makeup2__miluframe0608;
        decalRess.add(res);
        makeup2Res.m_DecalRess = decalRess;
        ress.add(makeup2Res);

        makeup2Res = new Makeup2Res();
        makeup2Res.m_id = 1000011;
        makeup2Res.m_name = "小魔女";
        makeup2Res.m_thumb = R.drawable.makeup2_deer3_thumb;
        makeup2Res.m_maskColor = item.m_maskColor;
        makeup2Res.m_shapeType = filter.ShapeType.DeerFace;
        makeupRes = new MakeupRes();
        datas = new MakeupRes.MakeupData[7];
        {
            datas[0] = new MakeupRes.MakeupData();
            datas[0].m_id = 56688;
            datas[0].m_makeupType = MakeupType.EYE_L.GetValue();
            datas[0].m_defAlpha = 60;
            datas[0].m_ex = 1;
            datas[0].m_res = new Object[]{R.drawable.makeup2_lu2_meitong};

            datas[1] = new MakeupRes.MakeupData();
            datas[1].m_pos = new float[]{250.0f,123.0f,165.0f,80.0f,93.0f,118.0f};
            datas[1].m_params = new float[]{1.0f,0.0f,0.0f,0.0f};
            datas[1].m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
            datas[1].m_defAlpha = 60;
            datas[1].m_ex = 1;
            datas[1].m_res = new Object[]{R.drawable.makeup2_lu2_shangjiemao,R.drawable.makeup2_lu2_shangjiemao};

            datas[2] = new MakeupRes.MakeupData();
            datas[2].m_pos = new float[]{250.0f,123.0f,165.0f,141.0f,93.0f,118.0f};
            datas[2].m_makeupType = MakeupType.EYELASH_DOWN_L.GetValue();
            datas[2].m_defAlpha = 60;
            datas[2].m_ex = 1;
            datas[2].m_res = new Object[]{R.drawable.makeup2_lu2_xiajiemao};

            datas[3] = new MakeupRes.MakeupData();
            datas[3].m_pos = new float[]{219.0f,121.0f,120.0f,61.0f,48.0f,103.0f};
            datas[3].m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
            datas[3].m_defAlpha = 80;
            datas[3].m_ex = 1;
            datas[3].m_res = new Object[]{R.drawable.makeup2_lu2_yanxian};

//            datas[4] = new MakeupRes.MakeupData();
//            datas[4].m_pos = new float[]{191.0f,51.0f,94.0f,29.0f,94.0f,48.0f,33.0f,56.0f};
//            datas[4].m_makeupType = MakeupType.EYEBROW_L.GetValue();
//            datas[4].m_defAlpha = 60;
//            datas[4].m_ex = 1;
//            datas[4].m_res = new Object[]{R.drawable.makeup2_lu2_meimao};

            datas[4] = new MakeupRes.MakeupData();
            datas[4].m_makeupType = MakeupType.LIP.GetValue();
            datas[4].m_defAlpha = 80;
            datas[4].m_ex = 41;
            datas[4].m_res = new Object[]{R.drawable.makeup2_lu2_chuncai};

            datas[5] = new MakeupRes.MakeupData();
            datas[5].m_defAlpha = 80;
            datas[5].m_ex = 1;
            datas[5].m_makeupType = MakeupType.KOHL_L.GetValue();
            datas[5].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f,160.0f,129.0f};
            datas[5].m_res = new Object[]{R.drawable.makeup2_lu1_yanying};

            datas[6] = new MakeupRes.MakeupData();
            datas[6].m_defAlpha = 75;
            datas[6].m_ex = 0xffffe4e4;
            datas[6].m_makeupType = MakeupType.FOUNDATION.GetValue();
        }
        makeupRes.m_groupRes = datas;
        makeup2Res.m_makeupRes = makeupRes;
        decalRess = new ArrayList<>();
        res = new DecalRes();
        res.m_type = "head";
        res.m_scale = 1.8f;
        res.m_offset = new float[]{0f, -1.2f};
        res.m_res = R.drawable.__makeup2__xiaoemohead5191;
        decalRess.add(res);

        res = new DecalRes();
        res.m_type = "eye";
        res.m_scale = 1.2f;
        res.m_offset = new float[]{-0.025f, 0.1f};
        res.m_res = R.drawable.__makeup2__xiaoemoeye5192;
        decalRess.add(res);

        res = new DecalRes();
        res.m_type = "shoulder";
        res.m_scale = 0.9f;
        res.m_offset = new float[]{0.025f, -0.3f};
        res.m_res = R.drawable.__makeup2__xiaoemoshoulder5193;
        decalRess.add(res);
        makeup2Res.m_DecalRess = decalRess;
        ress.add(makeup2Res);

        makeup2Res = new Makeup2Res();
        makeup2Res.m_id = 1000012;
        makeup2Res.m_name = "鹿公主";
        makeup2Res.m_thumb = R.drawable.makeup2_deer2_thumb;
        makeup2Res.m_maskColor = item.m_maskColor;
        makeup2Res.m_shapeType = filter.ShapeType.DeerFace;
        makeupRes = new MakeupRes();
        datas = new MakeupRes.MakeupData[8];
        {
            datas[0] = new MakeupRes.MakeupData();
            datas[0].m_makeupType = MakeupType.EYE_L.GetValue();
            datas[0].m_defAlpha = 50;
            datas[0].m_ex = 1;
            datas[0].m_res = new Object[]{R.drawable.makeup2_lu3_meitong};

            datas[1] = new MakeupRes.MakeupData();
            datas[1].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f};
            datas[1].m_params = new float[]{1.0f,0.0f,0.0f,0.0f};
            datas[1].m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
            datas[1].m_defAlpha = 60;
            datas[1].m_ex = 1;
            datas[1].m_res = new Object[]{R.drawable.makeup2_lu3_shangjiemao,R.drawable.makeup2_lu3_shangjiemao};

            datas[2] = new MakeupRes.MakeupData();
            datas[2].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f};
            datas[2].m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
            datas[2].m_defAlpha = 100;
            datas[2].m_ex = 1;
            datas[2].m_res = new Object[]{R.drawable.makeup2_lu3_shangyanxian};

            datas[3] = new MakeupRes.MakeupData();
            datas[3].m_pos = new float[]{255.0f,118.0f,160.0f,129.0f,86.0f,101.0f};
            datas[3].m_makeupType = MakeupType.EYELINER_DOWN_L.GetValue();
            datas[3].m_defAlpha = 100;
            datas[3].m_ex = 1;
            datas[3].m_res = new Object[]{R.drawable.makeup2_lu3_xiayanxian};

            datas[4] = new MakeupRes.MakeupData();
            datas[4].m_makeupType = MakeupType.LIP.GetValue();
            datas[4].m_defAlpha = 100;
            datas[4].m_ex = 41;
            datas[4].m_res = new Object[]{R.drawable.makeup2_lu3_chuncai};

            datas[5] = new MakeupRes.MakeupData();
            datas[5].m_defAlpha = 60;
            datas[5].m_ex = 34;
            datas[5].m_makeupType = MakeupType.KOHL_L.GetValue();
            datas[5].m_pos = new float[]{255.0f,118.0f,160.0f,58.0f,86.0f,101.0f,160.0f,129.0f};
            datas[5].m_res = new Object[]{R.drawable.makeup2_lu3_yanying};

            datas[6] = new MakeupRes.MakeupData();
            datas[6].m_defAlpha = 75;
            datas[6].m_ex = 0xffffe4e4;
            datas[6].m_makeupType = MakeupType.FOUNDATION.GetValue();

            datas[7] = new MakeupRes.MakeupData();
            datas[7].m_defAlpha = 50;
            datas[7].m_ex = 20;
            datas[7].m_makeupType = MakeupType.EYEBROW_L.GetValue();
            datas[7].m_pos = new float[]{191.0f,51.0f,94.0f,29.0f,94.0f,48.0f,33.0f,56.0f};
            datas[7].m_res = new Object[]{R.drawable.__mak__1234121110867336};
        }
        makeupRes.m_groupRes = datas;
        makeup2Res.m_makeupRes = makeupRes;
        decalRess = new ArrayList<>();
        res = new DecalRes();
        res.m_type = "head";
        res.m_scale = 2.2f;
        res.m_offset = new float[]{0f, -0.45f};
        res.m_res = R.drawable.__makeup2__lugongzhuhead5191;
        decalRess.add(res);

        res = new DecalRes();
        res.m_type = "nose";
        res.m_scale = 3.2f;
        res.m_offset = new float[]{0f, -0.4f};
        res.m_res = R.drawable.__makeup2__lugongzhunose5192;
        decalRess.add(res);
        makeup2Res.m_DecalRess = decalRess;
        ress.add(makeup2Res);
        item.m_makeup2Ress = ress;
        out.add(item);

        return out;
    }

    public static class DecalRes
    {
        public String m_type;//类型，例如鼻子素材等
        public float m_scale;//缩放倍数
        public float[] m_offset;//素材x,y的偏移量的比例，偏移量x的计算：底层返回的画素材的区域的宽度和设计给的素材的缩放值相乘之后再用这个m_offset[0]相乘，得到的是x的偏移量。y的偏移量计算和x的计算方式一样。
        public int m_res;//素材
    }

    //加贴纸
    private Bitmap addOther(Bitmap srcBmp,ArrayList<DecalRes>[] ressArr)
    {
        if(RAW_POS_MULTI != null && RAW_POS_MULTI.length > 0 && srcBmp != null && !srcBmp.isRecycled())
        {
            Bitmap out = srcBmp;
            Canvas canvas = new Canvas(out);
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            ArrayList<Integer> sorss = sortMulti();
            for(int i = 0; i < sorss.size(); i++)
            {
                int index = sorss.get(i);
                if(ressArr != null && ressArr.length > index)
                {
                    ArrayList<DecalRes> ress = ressArr[index];
                    if(ress.size() > 0)
                    {
                        addReal(null,canvas,getDecalResByType(ress,StickerType.Frame),index,out);
                        //从底层拿到要画的位置的中心点，旋转角度等信息
                        float[] data = filterori.getSticterPosition(srcBmp.getWidth(),srcBmp.getHeight(), RAW_POS_MULTI[sorss.get(i)], StickerType.Head);
                        addReal(data,canvas,getDecalResByType(ress,StickerType.Head),index,out);
                        data = filterori.getSticterPosition(srcBmp.getWidth(),srcBmp.getHeight(), RAW_POS_MULTI[sorss.get(i)], StickerType.Eye);
                        addReal(data,canvas,getDecalResByType(ress,StickerType.Eye),index,out);
                        data = filterori.getSticterPosition(srcBmp.getWidth(),srcBmp.getHeight(), RAW_POS_MULTI[sorss.get(i)], StickerType.Nose);
                        addReal(data,canvas,getDecalResByType(ress,StickerType.Nose),index,out);
                        data = filterori.getSticterPosition(srcBmp.getWidth(),srcBmp.getHeight(), RAW_POS_MULTI[sorss.get(i)], StickerType.Shoulder);
                        addReal(data,canvas,getDecalResByType(ress,StickerType.Shoulder),index,out);
                    }
                }
            }
            return out;
        }
        return srcBmp;
    }

    //排序，画贴纸的时候，先画脸小的，再画脸大的，因为多人脸情况下，可能脸大的位置是在前面一点，所以先画脸小的，尽量避免脸小的素材画到了脸大的素材上面。
    private ArrayList<Integer> sortMulti()
    {
        ArrayList<Integer> out = new ArrayList<>();
        if(RAW_POS_MULTI != null && RAW_POS_MULTI.length > 0)
        {
            List<PocoFaceInfo> temps = new ArrayList<>();
            temps = java.util.Arrays.asList(RAW_POS_MULTI.clone());
            Collections.sort(temps, new Comparator<PocoFaceInfo>() {
                @Override
                public int compare(PocoFaceInfo o1, PocoFaceInfo o2) {
                    int out = 0;
                    float[] tmp1 = o1.getFaceRect();
                    float[] tmp2 = o2.getFaceRect();
                    float sqa1 = tmp1[8]*tmp1[9];
                    float sqa2 = tmp2[8]*tmp2[9];
                    if(sqa1 < sqa2)
                    {
                        out = -1;
                    }
                    else
                    {
                        out = 0;
                    }
                    return out;
                }
            });
            List<PocoFaceInfo> temp2 = java.util.Arrays.asList(RAW_POS_MULTI.clone());
            for(int i = 0; i < temps.size(); i++)
            {
                out.add(temp2.indexOf(temps.get(i)));
            }
        }
        return out;
    }


    private DecalRes getDecalResByType(ArrayList<DecalRes> ress,String type)
    {
        DecalRes res = null;
        if(ress != null && ress.size() > 0)
        {
            for(int i = 0; i < ress.size(); i++)
            {
                DecalRes temp = ress.get(i);
                if(temp.m_type == type)
                {
                    res = temp;
                    break;
                }
            }
        }
        return res;
    }

    /**
     * 画贴纸
     * @param data 底层返回的数据 data[0]：中心点x坐标，data[1]：中心点y坐标，data[2]:素材旋转的角度，data[3]：素材缩放后的宽度。
     * @param canvas
     * @param res 贴纸的相关数据
     * @param index 人脸Index
     * @param srcBmp
     */
    private void addReal(float[] data,Canvas canvas,DecalRes res,int index,Bitmap srcBmp)
    {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        if(res != null && res.m_type == "frame")
        {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(),res.m_res);
            float scale = (srcBmp.getWidth()*1.0f)/bmp.getWidth();
            Matrix matrix = new Matrix();
            matrix.postScale(scale,scale);
            canvas.drawBitmap(bmp,matrix,paint);
            return;
        }

        if(data != null && data.length == 4 && canvas != null && res != null)
        {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), res.m_res);
            float[] temp = data;
            float centerX = temp[0];
            float centerY = temp[1];
            float dg = (float) (temp[2]*180/Math.PI);
            float finalW = temp[3];//目标框的宽度
            float finalH = temp[3]*(bmp.getHeight()/(bmp.getWidth()*1.0f));//目标框的长度

            float[] offsets = res.m_offset;
            if(offsets != null && offsets.length == 2)
            {
                float offsetX = finalW*res.m_scale*(offsets[0]);//计算中心点x轴的偏移量
                float offsetY = finalH*res.m_scale*(offsets[1]);//计算中心点y轴的偏移量
                float tempCenterX = centerX + offsetX;
                float tempCenterY = centerY + offsetY;
                float[] src = new float[]{tempCenterX,tempCenterY};
                float[] dst = new float[2];

                Matrix matrix = new Matrix();
                if(res.m_type == "shoulder")
                {
                    float[] temps = filterori.getSticterPosition(srcBmp.getWidth(),srcBmp.getHeight(), RAW_POS_MULTI[index], StickerType.Nose);

                    matrix.postRotate(dg,temps[0],temps[1]);
                    matrix.mapPoints(dst,src);
                }
                else
                {
                    matrix.postRotate(dg,centerX,centerY);
                    matrix.mapPoints(dst,src);
                }

                centerX = dst[0];
                centerY = dst[1];
            }
            float startX = centerX - bmp.getWidth()/2f;
            float startY = centerY - bmp.getHeight()/2f;
            float scale = finalW/bmp.getWidth()*(res.m_scale);

            Matrix matrix1 = new Matrix();
//            Camera camera = new Camera();
//            camera.save();
//            float dg2 = (float) (FaceDataV2.RAW_POS_MULTI[index].getYaw()*(180.0f/Math.PI));
//            camera.rotateY(-dg2/2f);
//            camera.getMatrix(matrix1);
//            camera.restore();
//            matrix1.preTranslate(-bmp.getWidth()/2f,0);
//            matrix1.postTranslate(startX + bmp.getWidth()/2f,startY);
                matrix1.postTranslate(startX,startY);
                matrix1.postRotate(dg,centerX,centerY);
                matrix1.postScale(scale,scale,centerX,centerY);
                canvas.drawBitmap(bmp,matrix1,paint);
        }
    }

    private void showExitDialog()
    {
        if (m_exitDialog == null) {
            m_exitDialog = new CloudAlbumDialog(getContext(),
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            ImageUtils.AddSkin(getContext(), m_exitDialog.getOkButtonBg());
            m_exitDialog.setCancelButtonText(R.string.cancel)
                    .setOkButtonText(R.string.ensure)
                    .setMessage(R.string.confirm_back)
                    .setListener(new CloudAlbumDialog.OnButtonClickListener() {
                        @Override
                        public void onOkButtonClick() {
                            if (m_exitDialog != null) {
                                m_exitDialog.dismiss();
                            }
                            onExit();
                        }

                        @Override
                        public void onCancelButtonClick() {
                            if (m_exitDialog != null) {
                                m_exitDialog.dismiss();
                            }
                        }
                    });
        }
        m_exitDialog.show();
    }

    private void clearExitDialog()
    {
        if (m_exitDialog != null)
        {
            m_exitDialog.dismiss();
            m_exitDialog.setListener(null);
            m_exitDialog = null;
        }
    }


    public static class Makeup2Msg
    {
        public Bitmap m_bmp;
        public Makeup2LocalData m_loacalData;
    }

    public static class Save2Msg
    {
        public Object m_imgInfo;
        public Makeup2LocalData m_loacalData;
        public int m_waterMarkId = -1;
    }
}
