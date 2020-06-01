package cn.poco.makeup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.widget.AppCompatImageView;
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
import java.util.HashMap;

import cn.poco.MaterialMgr2.site.DownloadMorePageSite;
import cn.poco.advanced.ImageUtils;
import cn.poco.beautify.AsetUnlock;
import cn.poco.beautify.BeautifyResMgr2;
import cn.poco.beautify.MakeupASetType;
import cn.poco.beautify.RecomDisplayMgr;
import cn.poco.beautify.RecommendMakeupItemList;
import cn.poco.beautify.SonWindow;
import cn.poco.beautify4.Beautify4Page;
import cn.poco.camera.RotationImg2;
import cn.poco.camera3.ui.FixPointView;
import cn.poco.cloudalbumlibs.utils.CloudAlbumDialog;
import cn.poco.face.FaceDataV2;
import cn.poco.face.FaceLocalData;
import cn.poco.filterPendant.MyStatusButton;
import cn.poco.framework.BaseSite;
import cn.poco.framework.DataKey;
import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;
import cn.poco.image.filter;
import cn.poco.makeup.makeup1.Makeup1ExAdapter;
import cn.poco.makeup.makeup1.Makeup1ListConfig;
import cn.poco.makeup.makeup1.Makeup1RecyclerView;
import cn.poco.makeup.makeup_abs.AbsAlphaFrExAdapter;
import cn.poco.makeup.makeup_rl.MakeupRLAdapter;
import cn.poco.makeup.makeup_rl.MakeupRLListConfig;
import cn.poco.makeup.makeup_rl.MakeupRLRecyclerView;
import cn.poco.makeup.site.MakeupPageSite;
import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.BaseExAdapter;
import cn.poco.resource.BaseRes;
import cn.poco.resource.DownloadMgr;
import cn.poco.resource.IDownload;
import cn.poco.resource.LockRes;
import cn.poco.resource.LockResMgr2;
import cn.poco.resource.MakeupComboResMgr2;
import cn.poco.resource.MakeupGroupRes;
import cn.poco.resource.MakeupRes;
import cn.poco.resource.MakeupResMgr2;
import cn.poco.resource.MakeupType;
import cn.poco.resource.RecommendRes;
import cn.poco.resource.ResType;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.SysConfig;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.FullScreenDlg;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.CommonUI;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.utils.Utils;
import cn.poco.utils.WaitAnimDialog;
import cn.poco.view.beauty.BeautyCommonViewEx;
import cn.poco.view.beauty.MakeUpViewEx.ControlCallback;
import cn.poco.view.beauty.MakeUpViewEx1;
import my.beautyCamera.R;

import static cn.poco.face.FaceDataV2.RAW_POS_MULTI;
import static cn.poco.face.FaceDataV2.sFaceIndex;
import static cn.poco.makeup.ChangePointPage.CHECK_INDEX_CHEEK;
import static cn.poco.makeup.ChangePointPage.CHECK_INDEX_EYEBROW;
import static cn.poco.makeup.ChangePointPage.CHECK_INDEX_EYE_L;
import static cn.poco.makeup.ChangePointPage.CHECK_INDEX_LIP;
import static cn.poco.makeup.ChangePointPage.CHECK_TRHEE;
import static cn.poco.makeup.ChangePointPage.CHECK_INDEX_NOSE;
import static cn.poco.makeup.MakeupHandler.MAKEUP;
import static cn.poco.resource.MakeupType.EYELASH_DOWN_L;
import static cn.poco.resource.MakeupType.EYELASH_UP_L;
import static cn.poco.resource.MakeupType.EYELINER_DOWN_L;
import static cn.poco.resource.MakeupType.EYELINER_UP_L;
import static cn.poco.resource.MakeupType.FOUNDATION;

/**
 * 彩妆页面
 */
public class MakeupPage extends IPage {

    private static final int MAKEUP_PAGE = 1;
    private static final int CHANGEPOINT_PAGE_THREE = 2;
    private static final int CHANGEPOINT_PAGE_MULTI = 3;
    private final String ADURL = "http://cav.adnonstop.com/cav/f2d4e8870c/0060303008/?url=https://detail.m.tmall.com/item.htm?id=539789114974";

    private int DEF_IMG_SIZE;
    protected int m_frW;
    protected int m_frH;
    protected int m_bottomBarHeight;
    protected int m_bottomListHeight;
    protected int m_bottomTypeListHeight;
    protected MakeUpViewEx1 m_viewNew;

    protected Makeup1RecyclerView m_asetListNew;//主题彩妆列表
    protected MakeupRLRecyclerView m_replaceListNew;//彩妆局部列表
    protected MakeupTypeList m_makeupTypeList;//彩妆局部分类和主题切换的列表
    protected ArrayList<MakeupTypeList.BaseItem> m_makeupTypeItems = new ArrayList<>();

    protected ArrayList<AbsAlphaFrExAdapter.ItemInfo> m_aSetDatasNew = new ArrayList<>();
    protected ArrayList<MakeupRLAdapter.ItemInfo> m_rlDatasNew1 = new ArrayList<>();

    protected LinearLayout m_bottomFr;
    protected FrameLayout m_bottomBar;
    protected FrameLayout m_bottomList;
    protected ImageView m_backBtn;
    protected ImageView m_okBtn;
    protected TextView m_makeupText;
//    protected FrameLayout mFixView;
    private FixPointView mFixView;
    protected FrameLayout m_multifaceFr;
    protected ImageView m_multifaceBtn;//多人脸选择
    protected TextView m_multifaceTips;
//    protected ImageView m_checkBtn;//定点按钮
    protected ImageView m_compareBtn;//对比按钮
    protected MakeupPageSite m_site;

    protected Bitmap m_org;
    protected Bitmap m_curBmp;
    public FaceLocalData m_faceLocalData = null;
//    public static FaceLocalData m_faceLocalData = null;

    protected HandlerThread m_thread;
    protected MakeupHandler m_makeupHandler;
    protected Handler m_uiHandler;

    private boolean isFold = false;
    protected boolean m_uiEnabled;
    protected WaitAnimDialog m_waitDlg;

    private int m_curMakeupType = MakeupTypeList.MakeupTypeItem.ALL;
    private int m_curSubIndex = -1;
    private RotationImg2 m_imgInfo;
    private SeekBarTipsView m_seekBarTips;

    private MakeupViewExCB m_cbNew;
    private BtnOnclickLinsener m_onClickListener;
    private BtnOnclickListenerNew m_onclickListenerNew;
    private FullScreenDlg m_noFaceHelpFr;

    //彩妆推荐位弹窗
    protected RecomDisplayMgr m_recomView;

    //定点
    protected MakeupUIHelper.ChangePointFr m_changePointFr;
    protected int m_curChangePointType = -1;

    protected SonWindow m_sonWin;

    protected int[] m_records = new int[9];
    protected boolean m_isChangePointing = false;

    protected boolean m_multiFaceUiEnable = true;

    protected int m_themeUri = -1;//进入彩妆页面要打开的选中的主题uri

    //记录每个人脸当前选中的彩妆套装
    protected int[] m_asetUriRecords;

    private boolean m_posModify;//是否改变过定点


    public static final String VIEWH = "viewH";
    public static final String IMGH = "imgH";

    private int m_imgH = 0;
    private int m_viewH = 0;

    private HashMap<Integer, Integer>[] m_tongjiIDArrs;//记录要统计的id
    private int m_asetTongjiFlag = -110123232;

    private MyStatusButton m_titleBtn;

    private boolean m_shouldAddfirstTheme = false;//进入彩妆，多人的情况下，选择人脸之后，是否要打开相应的主题，只有进入彩妆之后第一次选择人脸之后有可能要打开相应的主题。

    private boolean m_isFaceChecked = true;
    private boolean m_isStartAnimFinished = true;
    private Makeup1ExAdapter m_asetAdapter;
    private int m_orgBmpWidth;
    private int m_orgBmpHeight;

    private boolean m_isChange = false;
    private CloudAlbumDialog m_exitDialog;

    //private final String TAG = "彩妆";
    public MakeupPage(Context context, BaseSite site) {
        super(context, site);
        m_site = (MakeupPageSite) site;
        initData();
        initUI();
        TongJiUtils.onPageStart(getContext(), R.string.彩妆);
        MyBeautyStat.onPageStartByRes(R.string.美颜美图_彩妆页面_主页面);
    }

    /**
     * @param params imgs 图片资源 RotationImg2[]/Bitmap形式
     */
    @Override
    public void SetData(HashMap<String, Object> params) {
        if (params != null) {
            if (params.get(Beautify4Page.PAGE_ANIM_IMG_H) != null) {
                m_imgH = (int) params.get(Beautify4Page.PAGE_ANIM_IMG_H);
            }

            if (params.get(Beautify4Page.PAGE_ANIM_VIEW_H) != null) {
                m_viewH = (int) params.get(Beautify4Page.PAGE_ANIM_VIEW_H);
            }

            if (params.get(DataKey.BEAUTIFY_DEF_SEL_URI) != null) {
                m_themeUri = (int) params.get(DataKey.BEAUTIFY_DEF_SEL_URI);
            }
            if (params.get("imgs") != null) {
                setImg(params.get("imgs"));
            }
        }
    }

    private void initData() {
        m_uiEnabled = true;
        //m_frW = ShareData.m_screenWidth;
        //m_frH = m_frW * 4 / 3;

        m_frW = ShareData.m_screenWidth;
        m_frW -= m_frW % 2;
        m_frH = ShareData.m_screenHeight - ShareData.PxToDpi_xhdpi(320);
        m_frH -= m_frH % 2;

        m_bottomBarHeight = ShareData.PxToDpi_xhdpi(88);
        m_bottomListHeight = ShareData.PxToDpi_xhdpi(162);
        m_bottomTypeListHeight = ShareData.PxToDpi_xhdpi(70);

        DEF_IMG_SIZE = SysConfig.GetPhotoSize(getContext());

        //int frH = m_frW * 4 / 3;
        //frH -= frH % 2;
        //if (frH > m_frH) {
        //    frH = m_frH;
        //}
        //m_frW += 2;//为了去白边

        m_cbNew = new MakeupViewExCB();
        m_onClickListener = new BtnOnclickLinsener();
        m_onclickListenerNew = new BtnOnclickListenerNew();

        m_uiHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg != null) {
                    switch (msg.what) {
                        case MakeupHandler.CHECK_FACE: {
                            if (FaceDataV2.RAW_POS_MULTI != null) {
                                if (FaceLocalData.getInstance() == null) {
                                    m_faceLocalData = FaceLocalData.getNewInstance(FaceDataV2.RAW_POS_MULTI.length);
                                } else {
                                    m_faceLocalData = FaceLocalData.getInstance();
                                }
                                m_asetUriRecords = new int[FaceDataV2.FACE_POS_MULTI.length];
                                initAsetUriRecord();
                                m_tongjiIDArrs = new HashMap[FaceDataV2.FACE_POS_MULTI.length];
                                initTongjiIDArrs();

                                if (FaceDataV2.FACE_POS_MULTI.length > 1) {
                                    m_multiFaceUiEnable = false;
                                    setEnableForList(false);
                                    setScaleAnim(mFixView, false);
                                    showSelectFace();
                                } else {
                                    m_viewNew.m_faceIndex = 0;
                                    m_multiFaceUiEnable = true;
                                }

                                if (!FaceDataV2.CHECK_FACE_SUCCESS) {
                                    MakeNoFaceHelp();
                                    if (m_noFaceHelpFr != null) {
                                        m_noFaceHelpFr.show();
                                    }
                                } else {
                                    if (m_themeUri != -1) {
                                        if (FaceDataV2.FACE_POS_MULTI != null && FaceDataV2.FACE_POS_MULTI.length > 1) {
                                            m_shouldAddfirstTheme = true;
                                        } else {
                                            SetSelectByUri(m_themeUri);
                                        }
                                    }
                                    if(mFixView != null)
                                    {
                                        mFixView.showJitterAnimAccordingStatus();
                                    }
                                }
                            }


                            m_isFaceChecked = true;
                            isFinishedStartAll();
                            SetWaitUI(false, "");
                            break;
                        }
                        case MAKEUP: {
                            if (msg.obj != null) {
                                if (m_curBmp != null) {
                                    m_curBmp.recycle();
                                    m_curBmp = null;
                                }
                                m_curBmp = (Bitmap) msg.obj;
                                if (m_viewNew != null) {
                                    m_viewNew.setImage(m_curBmp);
                                }
                            }
                            m_isChange = true;
                            SetWaitUI(false, "");
                            if (isHasMakeupData()) {
                                setScaleAnim(m_compareBtn, false);
                            } else {
                                setScaleAnim(m_compareBtn, true);
                            }
                            break;
                        }
                    }
                }
            }
        };
        m_thread = new HandlerThread("makeup");
        m_thread.start();
        m_makeupHandler = new MakeupHandler(m_uiHandler, m_thread.getLooper(), getContext());

        if (DownloadMgr.getInstance() != null) {
            DownloadMgr.getInstance().AddDownloadListener(m_downloadLst);
        }
    }

    private int m_finalFrH;

    private void initUI() {
        m_viewNew = new MakeUpViewEx1(getContext(), m_cbNew);
        m_viewNew.def_fix_face_res = new int[]{R.drawable.beautify_fix_face_eye, R.drawable.beautify_fix_face_eye, R.drawable.beautify_fix_face_mouth};
        m_viewNew.def_fix_eyebrow_res = new int[]{R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point};
        m_viewNew.def_fix_eye_res = new int[]{R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_face_eye};
        m_viewNew.def_fix_cheek_res = new int[]{R.drawable.beautify_fix_point, R.drawable.beautify_fix_point};
        m_viewNew.def_fix_lip_res = new int[]{R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point};
        m_viewNew.def_fix_nose_res = new int[]{R.drawable.beautify_fix_point,R.drawable.beautify_fix_point};
        m_viewNew.def_stroke_width = ShareData.PxToDpi_xhdpi(2);
        if (m_viewNew.def_stroke_width < 1) {
            m_viewNew.def_stroke_width = 1;
        }
        m_viewNew.InitFaceRes();

        m_finalFrH = m_frH;
        if ((ShareData.m_screenHeight - m_bottomBarHeight - m_bottomTypeListHeight - m_bottomListHeight) < m_frH) {
            m_finalFrH = (ShareData.m_screenHeight - m_bottomBarHeight - m_bottomTypeListHeight - m_bottomListHeight);
        }
        LayoutParams fl = new LayoutParams(m_frW, m_finalFrH);
        fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        m_viewNew.setLayoutParams(fl);
        this.addView(m_viewNew, 0);

        {
            m_bottomFr = new LinearLayout(getContext()) {
//                @Override
//                public boolean onInterceptTouchEvent(MotionEvent ev) {
//                    if(!m_multiFaceUiEnable)
//                    {
//                        return true;
//                    }
//                    return super.onInterceptTouchEvent(ev);
//                }
            };
            m_bottomFr.setOrientation(LinearLayout.VERTICAL);
            fl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            fl.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            m_bottomFr.setLayoutParams(fl);
            this.addView(m_bottomFr);

            m_bottomBar = new FrameLayout(getContext());
            fl = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, m_bottomBarHeight);
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
                m_backBtn.setOnTouchListener(m_onclickListenerNew);
                m_bottomBar.addView(m_backBtn);

                m_okBtn = new ImageView(getContext());
                m_okBtn.setImageResource(R.drawable.beautify_ok);
                fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                fl.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                fl.rightMargin = ShareData.PxToDpi_xhdpi(24);
                m_okBtn.setLayoutParams(fl);
                m_okBtn.setOnTouchListener(m_onclickListenerNew);
                m_bottomBar.addView(m_okBtn);
                ImageUtils.AddSkin(getContext(), m_okBtn);

                m_titleBtn = new MyStatusButton(getContext());
                m_titleBtn.setData(R.drawable.beautify_makeup_icon, getResources().getString(R.string.makeup_title));
                m_titleBtn.setBtnStatus(true, false);
                fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                fl.gravity = Gravity.CENTER;
                m_titleBtn.setLayoutParams(fl);
                m_bottomBar.addView(m_titleBtn);
                m_titleBtn.setOnClickListener(m_onClickListener);
            }

            m_bottomList = new FrameLayout(getContext()) {
                @Override
                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    if (!m_multiFaceUiEnable || !m_uiEnabled) {
                        return true;
                    }
                    return super.onInterceptTouchEvent(ev);
                }
            };
            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, m_bottomListHeight);
            m_bottomList.setLayoutParams(ll);
            m_bottomFr.addView(m_bottomList);
            {

                m_makeupTypeList = new MakeupTypeList(getContext())
                {
                    @Override
                    public boolean dispatchTouchEvent(MotionEvent ev) {
                        if (!m_multiFaceUiEnable || !m_uiEnabled) {
                            return false;
                        }
                        return super.dispatchTouchEvent(ev);
                    }
                };
                m_makeupTypeList.setBackgroundColor(0xb2ffffff);
                ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, m_bottomTypeListHeight);
                m_makeupTypeList.setLayoutParams(ll);
                m_makeupTypeItems = getMakeupTypeItems();
                m_makeupTypeList.SetData(m_makeupTypeItems, m_makeupTypeListCB);
                m_bottomFr.addView(m_makeupTypeList);

                m_seekBarTips = new SeekBarTipsView(getContext());
                fl = new LayoutParams(ShareData.PxToDpi_xhdpi(120), ShareData.PxToDpi_xhdpi(120));
                fl.gravity = Gravity.BOTTOM | Gravity.LEFT;
                m_seekBarTips.setLayoutParams(fl);
                this.addView(m_seekBarTips);
                m_seekBarTips.setVisibility(GONE);
            }
        }

        mFixView = new FixPointView(getContext());
        fl = new LayoutParams(ShareData.PxToDpi_xhdpi(80), ShareData.PxToDpi_xhdpi(80));
        fl.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        fl.rightMargin = ShareData.PxToDpi_xhdpi(24);
        fl.bottomMargin = m_bottomListHeight + m_bottomBarHeight + m_bottomTypeListHeight + ShareData.PxToDpi_xhdpi(24);
        mFixView.setLayoutParams(fl);
        this.addView(mFixView);
//        mFixView.setBackgroundDrawable(new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.beautify_white_circle_bg)));
        mFixView.setOnTouchListener(m_onclickListenerNew);
        mFixView.setVisibility(GONE);


//        m_checkBtn = new ImageView(getContext());
//        fl = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        fl.gravity = Gravity.CENTER;
//        m_checkBtn.setLayoutParams(fl);
//        mFixView.addView(m_checkBtn);
//        m_checkBtn.setImageResource(R.drawable.beautify_fix_by_hand);
//        ImageUtils.AddSkin(getContext(), m_checkBtn);

        m_multifaceFr = new FrameLayout(getContext());
        fl = new LayoutParams(ShareData.PxToDpi_xhdpi(80), ShareData.PxToDpi_xhdpi(80));
        fl.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        fl.rightMargin = ShareData.PxToDpi_xhdpi(120);
        fl.bottomMargin = m_bottomListHeight + m_bottomBarHeight + m_bottomTypeListHeight + ShareData.PxToDpi_xhdpi(24);
        m_multifaceFr.setLayoutParams(fl);
        this.addView(m_multifaceFr);
        m_multifaceFr.setVisibility(GONE);
        m_multifaceFr.setBackgroundDrawable(new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.beautify_white_circle_bg)));
        m_multifaceFr.setOnTouchListener(m_onclickListenerNew);

        m_multifaceBtn = new ImageView(getContext());
        fl = new LayoutParams(ShareData.PxToDpi_xhdpi(50), ShareData.PxToDpi_xhdpi(50));
        fl.gravity = Gravity.CENTER;
        m_multifaceBtn.setLayoutParams(fl);
        m_multifaceFr.addView(m_multifaceBtn);
        m_multifaceBtn.setImageResource(R.drawable.beautify_makeup_multiface_icon);
        ImageUtils.AddSkin(getContext(), m_multifaceBtn);


        //多人脸选择提示语
        m_multifaceTips = new TextView(getContext());
        fl = new LayoutParams(ShareData.PxToDpi_xhdpi(560), ShareData.PxToDpi_xhdpi(80));
        fl.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        fl.topMargin = m_frH - ShareData.PxToDpi_xhdpi(80) - ShareData.PxToDpi_xhdpi(17);
        m_multifaceTips.setLayoutParams(fl);
        m_multifaceTips.setVisibility(GONE);
        this.addView(m_multifaceTips);
        m_multifaceTips.setBackgroundResource(R.drawable.beautify_makeup_multiface_tips_bk);
        m_multifaceTips.setText(R.string.makeup_multiface_tips);
        m_multifaceTips.setGravity(Gravity.CENTER);
        m_multifaceTips.setTextColor(Color.BLACK);

        m_compareBtn = new AppCompatImageView(getContext()) {
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        MyBeautyStat.onClickByRes(R.string.美颜美图_彩妆页面_主页面_对比按钮);
                        TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_彩妆_对比按钮);
                        m_viewNew.setImage(m_org);
                    }
                    break;
                    case MotionEvent.ACTION_UP:
                        if (m_curBmp != null && !m_curBmp.isRecycled()) {
                            m_viewNew.setImage(m_curBmp);
                        }
                        break;
                }
                return true;
            }
        };
        fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.TOP | Gravity.RIGHT;
        fl.topMargin = ShareData.PxToDpi_xhdpi(10);
        fl.rightMargin = ShareData.PxToDpi_xhdpi(15);
        m_compareBtn.setLayoutParams(fl);
        this.addView(m_compareBtn);
        m_compareBtn.setImageResource(R.drawable.beautify_compare);
        m_compareBtn.setVisibility(GONE);

        //定点界面
//        m_changePointFr = MakeupUIHelper.showChangePointFr(MakeupPage.this, m_changePointCB, MakeupUIHelper.ChangePointFr.CHECK_INDEX_NOSE);
        m_changePointFr = MakeupUIHelper.showChangePointFr(MakeupPage.this, m_changePointCB);
        m_changePointFr.setVisibility(GONE);
        m_waitDlg = new WaitAnimDialog((Activity) getContext());
        m_waitDlg.SetGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, m_bottomBarHeight + m_bottomListHeight + m_bottomTypeListHeight + ShareData.PxToDpi_xhdpi(20));

        m_recomView = new RecomDisplayMgr(getContext(), new RecomDisplayMgr.Callback() {
            @Override
            public void UnlockSuccess(BaseRes res) {

            }

            @Override
            public void OnCloseBtn() {

            }

            @Override
            public void OnBtn(int state) {

            }

            @Override
            public void OnClose() {

            }

            @Override
            public void OnLogin() {
                m_site.OnLogin(getContext());
            }
        });
        m_recomView.Create(MakeupPage.this);

        m_sonWin = new SonWindow(getContext());
        fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ShareData.m_screenWidth / 3);
        fl.gravity = Gravity.TOP | Gravity.LEFT;
        m_sonWin.setLayoutParams(fl);
        this.addView(m_sonWin);

        initAsetList();
        initReplaceListUI(1);
        m_replaceListNew.setVisibility(GONE);
        m_asetAdapter.SetSelectNullItem();
    }

    MakeupUIHelper.ChangePointFr.ChangePointFrCallBack m_changePointCB = new MakeupUIHelper.ChangePointFr.ChangePointFrCallBack() {
        @Override
        public void onCheckedChanged(boolean isChecked, boolean fromUser) {
            if (m_uiEnabled && fromUser) {
                if (isChecked) {
                    m_viewNew.m_moveAllFacePos = true;
                    m_viewNew.invalidate();
                } else {
                    m_viewNew.m_moveAllFacePos = false;
                    m_viewNew.invalidate();
                }
            }
        }

        @Override
        public void onClick(View v) {
            if (m_uiEnabled && m_changePointFr != null) {
                if (v == m_changePointFr.m_checkBackBtn) {
                    TongJiUtils.onPageEnd(getContext(), R.string.定点);
                    m_isChangePointing = false;
                    m_viewNew.m_showPosFlag = 0;
                    m_viewNew.m_touchPosFlag = m_viewNew.m_showPosFlag;
                    m_viewNew.ResetAnim();
                    SetUIByPage(MAKEUP_PAGE);
                    m_viewNew.reSetFaceData();
                    if (m_posModify) {
                        SendMakeupMsg();
                        m_posModify = false;
                    }
                    m_viewNew.setMode(BeautyCommonViewEx.MODE_NORMAL);
                    m_ischangePointAnimResetStart = true;
                } else if (v == m_changePointFr.m_checkThreeBackBtn) {
                    TongJiUtils.onPageEnd(getContext(), R.string.定点);
                    FaceDataV2.CHECK_FACE_SUCCESS = true;
                    m_isChangePointing = false;
                    m_changePointCB.onClick(m_changePointFr.m_checkBackBtn);
                } else if (v == m_changePointFr.m_changePointOkBtn) {
                    TongJiUtils.onPageEnd(getContext(), R.string.定点);
                    if (!FaceDataV2.CHECK_FACE_SUCCESS || !FaceDataV2.sIsFix) {
                        if (m_posModify) {
                            FaceDataV2.sIsFix = true;
                            float[] faceAll = RAW_POS_MULTI[m_viewNew.m_faceIndex].getFaceFeaturesMakeUp();
                            float[] faceData = FaceDataV2.RAW_POS_MULTI[m_viewNew.m_faceIndex].getFaceRect();
                            filter.reFixPtsCosmetic(faceData, faceAll, m_org);
                            FaceDataV2.RAW_POS_MULTI[m_viewNew.m_faceIndex].setFaceRect(faceData);
                            FaceDataV2.RAW_POS_MULTI[m_viewNew.m_faceIndex].setMakeUpFeatures(faceAll);
                        }
                    }

                    FaceDataV2.CHECK_FACE_SUCCESS = true;
                    m_isChangePointing = false;
                    m_viewNew.m_showPosFlag = 0;
                    m_viewNew.m_touchPosFlag = m_viewNew.m_showPosFlag;
                    if (m_org != null) {
                        FaceDataV2.Raw2Ripe(m_orgBmpWidth, m_orgBmpHeight);
                    }
                    m_viewNew.ResetAnim();
                    SetUIByPage(MAKEUP_PAGE);
                    m_posModify = false;
                    m_viewNew.setMode(BeautyCommonViewEx.MODE_NORMAL);
                    m_ischangePointAnimResetStart = true;
                } else if (v == m_changePointFr.m_checkThreeOkBtn) {
                    TongJiUtils.onPageEnd(getContext(), R.string.定点);
                    if (!FaceDataV2.CHECK_FACE_SUCCESS || !FaceDataV2.sIsFix) {
                        if (m_posModify) {
                            FaceDataV2.sIsFix = true;
                            float[] faceAll = RAW_POS_MULTI[m_viewNew.m_faceIndex].getFaceFeaturesMakeUp();
                            float[] faceData = FaceDataV2.RAW_POS_MULTI[m_viewNew.m_faceIndex].getFaceRect();
                            filter.reFixPtsBShapes(getContext(), m_viewNew.m_faceIndex, faceData, faceAll, m_org);
                            FaceDataV2.RAW_POS_MULTI[m_viewNew.m_faceIndex].setFaceRect(faceData);
                            FaceDataV2.RAW_POS_MULTI[m_viewNew.m_faceIndex].setMakeUpFeatures(faceAll);
                        }
                    }
                    FaceDataV2.CHECK_FACE_SUCCESS = true;
                    m_isChangePointing = false;
                    m_viewNew.m_showPosFlag = 0;
                    m_viewNew.m_touchPosFlag = m_viewNew.m_showPosFlag;
                    if (m_org != null) {
                        FaceDataV2.Raw2Ripe(m_orgBmpWidth, m_orgBmpHeight);
                    }
                    m_viewNew.Data2UI();
                    m_viewNew.ResetAnim();
                    m_viewNew.invalidate();
                    SetUIByPage(MAKEUP_PAGE);
                    //打开相应的主题
                    if (m_themeUri != -1) {
                        SetSelectByUri(m_themeUri);
                    }
                    m_posModify = false;
                    m_viewNew.setMode(BeautyCommonViewEx.MODE_NORMAL);
                    m_ischangePointAnimResetStart = true;
                } else if (v == m_changePointFr.m_checkLipBtn) {
                    doAnim(ChangePointPage.CHECK_INDEX_LIP);
                    m_changePointFr.setUIFlag(MakeupUIHelper.ChangePointFr.CHECK_INDEX_LIP);
                    m_curChangePointType = ChangePointPage.CHECK_INDEX_LIP;
                    SetUIByPage(CHANGEPOINT_PAGE_MULTI);
                } else if (v == m_changePointFr.m_checkCheekBtn) {
                    doAnim(ChangePointPage.CHECK_INDEX_CHEEK);
                    SetUIByPage(CHANGEPOINT_PAGE_MULTI);
                    m_changePointFr.setUIFlag(MakeupUIHelper.ChangePointFr.CHECK_INDEX_CHEEK1);
                    m_curChangePointType = ChangePointPage.CHECK_INDEX_CHEEK;
                } else if (v == m_changePointFr.m_checkEyeBtnL) {
                    doAnim(ChangePointPage.CHECK_INDEX_EYE_L);
                    m_changePointFr.setUIFlag(MakeupUIHelper.ChangePointFr.CHECK_INDEX_EYE_L);
                    SetUIByPage(CHANGEPOINT_PAGE_MULTI);
                    m_curChangePointType = ChangePointPage.CHECK_INDEX_EYE_L;
                } else if (v == m_changePointFr.m_checkEyebrowBtn) {
                    doAnim(ChangePointPage.CHECK_INDEX_EYEBROW);
                    m_changePointFr.setUIFlag(MakeupUIHelper.ChangePointFr.CHECK_INDEX_EYEBROW);
                    SetUIByPage(CHANGEPOINT_PAGE_MULTI);
                    m_curChangePointType = ChangePointPage.CHECK_INDEX_EYEBROW;
                }
                else if (v == m_changePointFr.m_checkNoseBtn)
                {
                    doAnim(ChangePointPage.CHECK_INDEX_NOSE);
                    m_changePointFr.setUIFlag(MakeupUIHelper.ChangePointFr.CHECK_INDEX_NOSE);
                    SetUIByPage(CHANGEPOINT_PAGE_MULTI);
                    m_curChangePointType = ChangePointPage.CHECK_INDEX_NOSE;
                }
            }

        }
    };


    private void initAsetList() {
        if (m_asetListNew != null) {
            this.removeView(m_asetListNew);
            m_asetListNew = null;
        }

        m_aSetDatasNew = BeautifyResMgr2.GetAsetRes1(getContext());
        Makeup1ListConfig config = new Makeup1ListConfig();
        m_asetAdapter = new Makeup1ExAdapter(getContext(), config);
        m_asetAdapter.SetData(m_aSetDatasNew);
        m_asetAdapter.setOnItemClickListener(m_asetCB);
        m_asetListNew = new Makeup1RecyclerView(getContext(), m_asetAdapter);
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        this.addView(m_asetListNew, fl);
    }


    private MakeupRLAdapter m_rlAdapter1;

    private void initReplaceListUI(int type) {
        if (m_replaceListNew != null) {
            this.removeView(m_replaceListNew);
            m_replaceListNew = null;
        }
        m_rlDatasNew1 = getMakeupRessNew(type);
        MakeupRLListConfig config = new MakeupRLListConfig();
        m_rlAdapter1 = new MakeupRLAdapter(config);
        m_rlAdapter1.SetData(m_rlDatasNew1);
        m_rlAdapter1.setOnItemClickListener(m_rlCB);
        m_replaceListNew = new MakeupRLRecyclerView(getContext(), m_rlAdapter1);
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        this.addView(m_replaceListNew, fl);
    }

    private boolean m_isYSLChoose = false;//是否选择ysl商业的效果，在美化页保存的时候用到是否要发送统计
    Makeup1ExAdapter.OnItemClickListener m_asetCB = new Makeup1ExAdapter.OnItemClickListener() {
        @Override
        public void OnItemDown(BaseExAdapter.ItemInfo info, int parentIndex, int subIndex) {

        }

        @Override
        public void OnItemUp(BaseExAdapter.ItemInfo info, int parentIndex, int subIndex) {

        }

        @Override
        public void OnItemClick(BaseExAdapter.ItemInfo info, int parentIndex, int subIndex) {
            if (!(m_uiEnabled && m_multiFaceUiEnable)) {
                return;
            }
            Makeup1ExAdapter.ItemInfo itemInfo = (Makeup1ExAdapter.ItemInfo) info;
            if (info == null) {
                return;
            }
            if (subIndex > 0) {
                int selUri = itemInfo.m_uris[subIndex];
                if (m_aSetSelUri != selUri) {
                    recordAsetUriByFaceIndex(selUri);
                    switch (selUri) {
                        case MakeupASetType.MAKEUP_NONE: {
                            break;
                        }
                        default: {
                            //重置套装alpha
                            if (sFaceIndex > -1 && m_faceLocalData != null && m_faceLocalData.m_faceNum > 0) {
                                m_faceLocalData.m_asetAlpha_multi[sFaceIndex] = 100;
                            }

                            MakeupRes temp = ((MakeupGroupRes) itemInfo.m_ex).m_group.get(subIndex - 1);

                            if (temp != null) {
                                if (m_tongjiIDArrs != null && FaceDataV2.sFaceIndex < m_tongjiIDArrs.length && m_tongjiIDArrs[FaceDataV2.sFaceIndex] != null) {
                                    m_tongjiIDArrs[FaceDataV2.sFaceIndex].put(m_asetTongjiFlag, temp.m_tjId);
                                }
                                SetMakeupData(((MakeupGroupRes) itemInfo.m_ex).m_group.get(subIndex - 1));
                                SendMakeupMsg();
                                //ysl商业
                                if(MakeupADUtil.isYSLMakeup(temp.m_id))
                                {
                                    m_isYSLChoose = true;
                                  Utils.UrlTrigger(getContext(), "http://cav.adnonstop.com/cav/fe0a01a3d9/0071802970/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
                                }
                                else
                                {
                                    m_isYSLChoose = false;
                                }

                                //兰蔻商业统计
                                if(MakeupADUtil.isLCMakeup(temp.m_id))
                                {
                                    Utils.UrlTrigger(getContext(), "http://cav.adnonstop.com/cav/fe0a01a3d9/0073003081/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
                                }

                            }
                            break;
                        }
                    }
                    m_aSetSelUri = selUri;
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
            Makeup1ExAdapter.ItemInfo itemInfo = (Makeup1ExAdapter.ItemInfo) info;
            if (itemInfo == null) {
                return;
            }
            //Clalen商业统计
            int groupID = itemInfo.m_uris[0];
            if (groupID == 1275) {
                Utils.UrlTrigger(getContext(), "http://cav.adnonstop.com/cav/fe0a01a3d9/0060302970/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
            }

            //兰蔻商业统计
            if(groupID == 1419)
            {
                Utils.UrlTrigger(getContext(), "http://cav.adnonstop.com/cav/fe0a01a3d9/0073002970/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
            }

            boolean asetFlag = false;
            if ((asetFlag = ((groupID == AsetUnlock.ASET_LOCK_URI1 && TagMgr.CheckTag(getContext(), Tags.BEAUTY_ASET_LOCK1)) || (groupID == AsetUnlock.ASET_LOCK_URI2 && TagMgr.CheckTag(getContext(), Tags.BEAUTY_ASET_LOCK2)))) && LockResMgr2.getInstance().m_unlockCaiZhuang != LockRes.SHARE_TYPE_NONE) {

            } else {
                if (asetFlag) {
                    //注意需求是否要解锁全部
                    TagMgr.SetTag(getContext(), Tags.BEAUTY_ASET_LOCK1);
                    TagMgr.SetTag(getContext(), Tags.BEAUTY_ASET_LOCK2);
                }
                DeleteNewFlag(itemInfo);
            }

            if (((AbsAlphaFrExAdapter.ItemInfo) info).m_ex != null) {
                MakeupGroupRes temp = ((MakeupGroupRes) ((AbsAlphaFrExAdapter.ItemInfo) info).m_ex);
                if (temp != null && temp.m_tjId != 0) {
                    TongJi2.AddCountById(temp.m_tjId + "");
                }
            }
        }

        @Override
        public void onAlphaFrShowStart(MySeekBar mySeekBar) {
            int tempProgress = m_faceLocalData.m_asetAlpha_multi[sFaceIndex];
            mySeekBar.setProgress(tempProgress);
        }

        @Override
        public void onAlphaFrFinishLayout(MySeekBar mySeekBar) {
        }

        @Override
        public void onProgressChanged(MySeekBar seekBar, int progress) {
            RelayoutSeekBarTipsPos(seekBar, progress, 100);
        }

        @Override
        public void onStartTrackingTouch(MySeekBar seekBar) {
            m_seekBarTips.setVisibility(VISIBLE);
            RelayoutSeekBarTipsPos(seekBar, seekBar.getProgress(), 100);
        }

        @Override
        public void onStopTrackingTouch(MySeekBar seekBar) {
            m_seekBarTips.setVisibility(GONE);
            m_faceLocalData.m_asetAlpha_multi[sFaceIndex] = seekBar.getProgress();
            SendMakeupMsg();
        }

        @Override
        public void onItemClickNullItem() {
            if (m_aSetSelUri != 0) {
                m_faceLocalData.m_makeupDatas_multi[sFaceIndex].clear();
                SendMakeupMsg();

                m_aSetSelUri = 0;

                if (m_tongjiIDArrs != null && FaceDataV2.sFaceIndex < m_tongjiIDArrs.length && m_tongjiIDArrs[FaceDataV2.sFaceIndex] != null) {
                    m_tongjiIDArrs[FaceDataV2.sFaceIndex].remove(m_asetTongjiFlag);
                    m_tongjiIDArrs[FaceDataV2.sFaceIndex].clear();
                }
                recordAsetUriByFaceIndex(m_aSetSelUri);
                m_isYSLChoose = false;
            }
        }

        @Override
        public void onItemClickDownloadItem(AbsAlphaFrExAdapter.ItemInfo itemInfo, int index) {
            MyBeautyStat.onClickByRes(R.string.美颜美图_彩妆页面_主页面_下载更多);
            m_site.OnDownloadMore(getContext(), ResType.MAKEUP_GROUP);
        }

        @Override
        public void onItemClickRecomItem(AbsAlphaFrExAdapter.ItemInfo itemInfo, int index) {
            //推荐位
            ArrayList<RecommendRes> ress = (ArrayList<RecommendRes>) (itemInfo.m_ex);
            RecommendRes recommendRes = null;
            if (ress != null && ress.size() > 0) {
                recommendRes = ress.get(0);
            }

            if (recommendRes != null) {
                if (m_recomView != null) {
                    m_recomView.SetBk(CommonUtils.GetScreenBmp((Activity) getContext(), (int) (ShareData.m_screenWidth / 8f), (int) (ShareData.m_screenHeight / 8f)), true);
                    m_recomView.SetDatas(recommendRes, ResType.MAKEUP_GROUP.GetValue());
                    m_recomView.Show(MakeupPage.this);
                }
            }
            MyBeautyStat.onClickByRes(R.string.美颜美图_彩妆页面_主页面_主题彩妆推荐位);
            TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_主题彩妆_推荐位);
        }
    };


    MakeupRLAdapter.ItemAllCallBack m_rlCB = new MakeupRLAdapter.ItemAllCallBack() {
        @Override
        public void OnItemDown(AbsAdapter.ItemInfo info, int index) {

        }

        @Override
        public void OnItemUp(AbsAdapter.ItemInfo info, int index) {

        }

        @Override
        public void OnItemClick(AbsAdapter.ItemInfo info, int index) {
            if (m_uiEnabled && m_multiFaceUiEnable) {
                MakeupRes temp = null;

                if (info != null) {
                    if (info instanceof MakeupRLAdapter.ItemInfo) {
                        temp = (MakeupRes) ((MakeupRLAdapter.ItemInfo) info).m_ex;
                    } else if (info instanceof MakeupRLAdapter.NullItemInfo) {
                        temp = (MakeupRes) ((MakeupRLAdapter.NullItemInfo) info).m_ex;
                    }
                }
                if (m_curSubIndex != index) {
                    m_curSubIndex = index;
                    m_records[m_curMakeupType] = index;
                    m_rlAdapter1.scrollToCenterByIndex2(index);
                    if (temp != null && temp.m_groupRes != null && temp.m_groupRes.length > 0) {
                        MakeupType type = MakeupType.GetType(temp.m_groupRes[0].m_makeupType);
                        switch (type) {
                            case EYELINER_UP_L: {
                                BeautifyResMgr2.DelType(m_faceLocalData.m_makeupDatas_multi[sFaceIndex], type);
                                BeautifyResMgr2.DelType(m_faceLocalData.m_makeupDatas_multi[sFaceIndex], EYELINER_DOWN_L);
                                break;
                            }
                            case EYELASH_UP_L: {
                                BeautifyResMgr2.DelType(m_faceLocalData.m_makeupDatas_multi[sFaceIndex], type);
                                BeautifyResMgr2.DelType(m_faceLocalData.m_makeupDatas_multi[sFaceIndex], EYELASH_DOWN_L);
                                break;
                            }
                            default:
                                BeautifyResMgr2.DelType(m_faceLocalData.m_makeupDatas_multi[sFaceIndex], type);
                                break;
                        }

                        if (m_tongjiIDArrs != null && FaceDataV2.sFaceIndex < m_tongjiIDArrs.length && m_tongjiIDArrs[FaceDataV2.sFaceIndex] != null) {
                            m_tongjiIDArrs[FaceDataV2.sFaceIndex].put(temp.m_groupRes[0].m_makeupType, temp.m_tjId);
                        }

                        for (int i = 0; i < temp.m_groupRes.length; i++) {
                            if (temp.m_id != 0) {
                                AddMakeupItem(temp.m_groupRes[i]);
                            }
                        }
                    }
                    SendMakeupMsg();
                }
            }
        }

        @Override
        public void onAlphaFrShowStart(MySeekBar mySeekBar) {
            int tempProgress = 0;
            switch (m_curMakeupType) {
                case MakeupTypeList.MakeupTypeItem.ALL:
                    tempProgress = m_faceLocalData.m_asetAlpha_multi[sFaceIndex];
                    break;
                case MakeupTypeList.MakeupTypeItem.CHEEK_L:
                    tempProgress = m_faceLocalData.m_makeupAlphas_multi[sFaceIndex].m_cheekAlpha;
                    break;
                case MakeupTypeList.MakeupTypeItem.EYE_L:
                    tempProgress = m_faceLocalData.m_makeupAlphas_multi[sFaceIndex].m_eyeAlpha;
                    break;
                case MakeupTypeList.MakeupTypeItem.EYEBROW_L:
                    tempProgress = m_faceLocalData.m_makeupAlphas_multi[sFaceIndex].m_eyebrowAlpha;
                    break;
                case MakeupTypeList.MakeupTypeItem.EYELASH_UP_L:
                    tempProgress = m_faceLocalData.m_makeupAlphas_multi[sFaceIndex].m_eyelashUpAlpha;
                    break;
                case MakeupTypeList.MakeupTypeItem.EYELINER_UP_L:
                    tempProgress = m_faceLocalData.m_makeupAlphas_multi[sFaceIndex].m_eyelineUpAlpha;
                    break;
                case MakeupTypeList.MakeupTypeItem.FOUNDATION:
                    tempProgress = m_faceLocalData.m_makeupAlphas_multi[sFaceIndex].m_foundationAlpha;
                    break;
                case MakeupTypeList.MakeupTypeItem.KOHL_L:
                    tempProgress = m_faceLocalData.m_makeupAlphas_multi[sFaceIndex].m_kohlAlpha;
                    break;
                case MakeupTypeList.MakeupTypeItem.LIP:
                    tempProgress = m_faceLocalData.m_makeupAlphas_multi[sFaceIndex].m_lipAlpha;
                    break;
            }
            if (mySeekBar != null) {
                mySeekBar.setProgress(tempProgress);
            }
        }

        @Override
        public void onSeekBarLayoutFinish(MySeekBar mySeekBar) {

        }

        @Override
        public void onProgressChanged(MySeekBar mySeekBar, int progress) {
            RelayoutSeekBarTipsPos(mySeekBar, progress, 100);
        }

        @Override
        public void onStartTrackingTouch(MySeekBar seekBar) {
            m_seekBarTips.setVisibility(VISIBLE);
            RelayoutSeekBarTipsPos(seekBar, seekBar.getProgress(), 100);
        }

        @Override
        public void onStopTrackingTouch(MySeekBar seekBar) {
            m_seekBarTips.setVisibility(GONE);
            switch (m_curMakeupType) {
                case MakeupTypeList.MakeupTypeItem.CHEEK_L:
                    m_faceLocalData.m_makeupAlphas_multi[sFaceIndex].m_cheekAlpha = seekBar.getProgress();
                    break;
                case MakeupTypeList.MakeupTypeItem.EYE_L:
                    m_faceLocalData.m_makeupAlphas_multi[sFaceIndex].m_eyeAlpha = seekBar.getProgress();
                    break;
                case MakeupTypeList.MakeupTypeItem.EYEBROW_L:
                    m_faceLocalData.m_makeupAlphas_multi[sFaceIndex].m_eyebrowAlpha = seekBar.getProgress();
                    break;
                case MakeupTypeList.MakeupTypeItem.EYELASH_UP_L:
                    m_faceLocalData.m_makeupAlphas_multi[sFaceIndex].m_eyelashUpAlpha = seekBar.getProgress();
                    m_faceLocalData.m_makeupAlphas_multi[sFaceIndex].m_eyelashDownAlpha = seekBar.getProgress();
                    break;
                case MakeupTypeList.MakeupTypeItem.EYELINER_UP_L:
                    m_faceLocalData.m_makeupAlphas_multi[sFaceIndex].m_eyelineUpAlpha = seekBar.getProgress();
                    m_faceLocalData.m_makeupAlphas_multi[sFaceIndex].m_eyelineDownAlpha = seekBar.getProgress();
                    break;
                case MakeupTypeList.MakeupTypeItem.FOUNDATION:
                    m_faceLocalData.m_makeupAlphas_multi[sFaceIndex].m_foundationAlpha = seekBar.getProgress();
                    break;
                case MakeupTypeList.MakeupTypeItem.KOHL_L:
                    m_faceLocalData.m_makeupAlphas_multi[sFaceIndex].m_kohlAlpha = seekBar.getProgress();
                    break;
                case MakeupTypeList.MakeupTypeItem.LIP:
                    m_faceLocalData.m_makeupAlphas_multi[sFaceIndex].m_lipAlpha = seekBar.getProgress();
                    break;
            }
            SendMakeupMsg();
        }
    };


    public DownloadMgr.DownloadListener m_downloadLst = new DownloadMgr.DownloadListener() {
        @Override
        public void OnDataChange(int resType, int downloadId, IDownload[] resArr) {
            if (resArr != null && ((BaseRes) resArr[0]).m_type == BaseRes.TYPE_LOCAL_PATH) {
                if (resType == ResType.MAKEUP.GetValue()) {
//                    boolean isShow = true;
//
//                    if(m_asetListNew != null)
//                    {
//                        if(m_asetListNew.getVisibility() == GONE)
//                        {
//                            isShow = false;
//                        }
//                    }
//
//                    initAsetList();
//
//                    if(!isShow && m_asetListNew != null)
//                    {
//                        m_asetListNew.setVisibility(GONE);
//                    }

                    refreshAsetList();
                }
            }
        }
    };


    protected void MakeNoFaceHelp() {
        if (m_noFaceHelpFr == null) {
            m_noFaceHelpFr = CommonUI.MakeNoFaceHelpDlg((Activity) getContext(), new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (m_uiEnabled) {
                        if(mFixView != null)
                        {
                            mFixView.modifyStatus();
                        }
                        if (m_noFaceHelpFr != null) {
                            m_noFaceHelpFr.dismiss();
                            m_noFaceHelpFr = null;
                        }
                        m_isChangePointing = true;
                        m_viewNew.copyFaceData();
                        doAnim(ChangePointPage.CHECK_TRHEE);
                        m_changePointFr.setUIFlag(MakeupUIHelper.ChangePointFr.CHECK_TRHEE);
                        SetUIByPage(CHANGEPOINT_PAGE_THREE);
                        TranslateAnimVertical(m_changePointFr, (float) (ShareData.PxToDpi_xhdpi(234)), 0f);
                    }
                }
            });
        }
    }

    private void RelayoutSeekBarTipsPos(MySeekBar mySeekBar, int progress, int maxProgress) {
        LayoutParams temp = (LayoutParams) m_seekBarTips.getLayoutParams();
        temp.bottomMargin = m_bottomListHeight + m_bottomTypeListHeight - ShareData.PxToDpi_xhdpi(20);
        int[] dst = new int[2];
        mySeekBar.getLocationOnScreen(dst);
        temp.leftMargin = (int) ((int) (dst[0] + mySeekBar.getCurCiclePos()) - m_seekBarTips.getWidth() / 2f);
        m_seekBarTips.setText("" + progress);
        m_seekBarTips.setLayoutParams(temp);
    }


    MakeupTypeList.MakeupTypeListCallBack m_makeupTypeListCB = new MakeupTypeList.MakeupTypeListCallBack() {
        @Override
        public void ItemOnclick(View view, final int type, int index) {

            if (m_uiEnabled && m_multiFaceUiEnable) {
                if (m_curMakeupType != type) {
                    addMakeupTypeTongji(type);
                    m_curMakeupType = type;
//                    m_makeupTypeList.SetSelectIndex(index);
                    if (type == MakeupTypeList.MakeupTypeItem.ALL) {
                        m_asetListNew.setVisibility(VISIBLE);
                        m_replaceListNew.setVisibility(GONE);
                    } else {
                        m_asetListNew.setVisibility(GONE);
                        initReplaceListUI(type);
                        //恢复当前人脸之前的选项
                        SetSelectByMakeupPartListIndex();
                    }
                }

                //策神统计
                sendCeShenTongji(type);
            }
        }
    };


    private void sendCeShenTongji(int flag) {
        int out = -1;
        switch (flag) {
            case MakeupTypeList.MakeupTypeItem.ALL: {
                out = R.string.美颜美图_彩妆页面_主页面_主题妆容tab;
                break;
            }
            case MakeupTypeList.MakeupTypeItem.FOUNDATION: {
                out = R.string.美颜美图_彩妆页面_主页面_粉底tab;
                break;
            }
            case MakeupTypeList.MakeupTypeItem.CHEEK_L: {
                out = R.string.美颜美图_彩妆页面_主页面_腮红tab;
                break;
            }
            case MakeupTypeList.MakeupTypeItem.LIP: {
                out = R.string.美颜美图_彩妆页面_主页面_唇彩tab;
                break;
            }
            case MakeupTypeList.MakeupTypeItem.EYEBROW_L: {
                out = R.string.美颜美图_彩妆页面_主页面_眉毛tab;
                break;
            }
            case MakeupTypeList.MakeupTypeItem.KOHL_L: {
                out = R.string.美颜美图_彩妆页面_主页面_眼影tab;
                break;
            }
            case MakeupTypeList.MakeupTypeItem.EYELINER_UP_L: {
                out = R.string.美颜美图_彩妆页面_主页面_眼线tab;
                break;
            }
            case MakeupTypeList.MakeupTypeItem.EYELASH_UP_L: {
                out = R.string.美颜美图_彩妆页面_主页面_睫毛tab;
                break;
            }
            case MakeupTypeList.MakeupTypeItem.EYE_L: {
                out = R.string.美颜美图_彩妆页面_主页面_美瞳tab;
                break;
            }
        }
        if (out != -1) {
            MyBeautyStat.onClickByRes(out);
        }
    }


    private void addMakeupTypeTongji(int type) {
        switch (type) {
            case MakeupTypeList.MakeupTypeItem.ALL:
                TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_彩妆_主题妆容);
                break;
            case MakeupTypeList.MakeupTypeItem.FOUNDATION:
                TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_彩妆_粉底);
                break;
            case MakeupTypeList.MakeupTypeItem.CHEEK_L:
                TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_彩妆_腮红);
                break;
            case MakeupTypeList.MakeupTypeItem.LIP:
                TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_彩妆_唇彩);
                break;
            case MakeupTypeList.MakeupTypeItem.EYEBROW_L:
                TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_彩妆_眉毛);
                break;
            case MakeupTypeList.MakeupTypeItem.KOHL_L:
                TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_彩妆_眼影);
                break;
            case MakeupTypeList.MakeupTypeItem.EYELINER_UP_L:
                TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_彩妆_眼线);
                break;
            case MakeupTypeList.MakeupTypeItem.EYELASH_UP_L:
                TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_彩妆_睫毛);
                break;
            case MakeupTypeList.MakeupTypeItem.EYE_L:
                TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_彩妆_美瞳);
                break;
        }
    }

    private void SetSelectByMakeupPartListIndex() {
        MakeupPage.this.postDelayed(new Runnable() {
            @Override
            public void run() {
                int listSel = BeautifyResMgr2.GetSelIndexForMakeup1(m_faceLocalData.m_makeupDatas_multi[FaceDataV2.sFaceIndex], m_rlDatasNew1);
                if (listSel < 0) {
                    if (isHasCurTypeMakeupData(m_faceLocalData.m_makeupDatas_multi[FaceDataV2.sFaceIndex], m_curMakeupType)) {
                        listSel = -1;
                    } else {
                        listSel = 0;
                    }
                }
                m_curSubIndex = listSel;
                if (m_rlAdapter1 != null) {
                    int delaytime = 0;
                    if(m_rlAdapter1.alphaIsShow())
                    {
                        m_rlAdapter1.onCloseAlphaFr();
                        delaytime = 400;
                    }
                    final int selIndex = listSel;
                    MakeupPage.this.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            m_rlAdapter1.SetSelectIndex(selIndex);
                        }
                    },delaytime);

                }
            }
        }, 10);
    }

    private boolean isHasCurTypeMakeupData(ArrayList<MakeupRes.MakeupData> datas, int type) {
        boolean out = false;
        int makeuptype = MakeupType.NONE.GetValue();
        switch (type) {
            case MakeupTypeList.MakeupTypeItem.CHEEK_L: {
                makeuptype = MakeupType.CHEEK_L.GetValue();
                break;
            }
            case MakeupTypeList.MakeupTypeItem.EYE_L: {
                makeuptype = MakeupType.EYE_L.GetValue();
                break;
            }
            case MakeupTypeList.MakeupTypeItem.EYEBROW_L: {
                makeuptype = MakeupType.EYEBROW_L.GetValue();
                break;
            }
            case MakeupTypeList.MakeupTypeItem.EYELASH_UP_L: {
                makeuptype = EYELASH_UP_L.GetValue();
                break;
            }
            case MakeupTypeList.MakeupTypeItem.EYELINER_UP_L: {
                makeuptype = EYELINER_UP_L.GetValue();
                break;
            }
            case MakeupTypeList.MakeupTypeItem.FOUNDATION: {
                makeuptype = FOUNDATION.GetValue();
                break;
            }
            case MakeupTypeList.MakeupTypeItem.LIP: {
                makeuptype = MakeupType.LIP.GetValue();
                break;
            }
            case MakeupTypeList.MakeupTypeItem.KOHL_L: {
                makeuptype = MakeupType.KOHL_L.GetValue();
                break;
            }
        }

        if (datas != null && datas.size() > 0) {
            for (int i = 0; i < datas.size(); i++) {
                MakeupRes.MakeupData data = datas.get(i);
                if (data != null) {
                    if (data.m_makeupType == makeuptype) {
                        out = true;
                        break;
                    }
                }
            }
        }
        return out;
    }

    private ArrayList<MakeupTypeList.BaseItem> getMakeupTypeItems() {
        ArrayList<MakeupTypeList.BaseItem> out = new ArrayList<>();
        MakeupTypeList.MakeupTypeItem item = new MakeupTypeList.MakeupTypeItem(getContext());
        item.SetData(R.drawable.beautify_makeup_theme_icon, R.drawable.beautify_makeup_theme_icon, getResources().getString(R.string.makeup_theme_name));
        item.m_index = 0;
        item.m_type = MakeupTypeList.MakeupTypeItem.ALL;
        out.add(item);

        item = new MakeupTypeList.MakeupTypeItem(getContext());
        item.SetData(R.drawable.beautify_makeup_fendi_icon, R.drawable.beautify_makeup_fendi_icon, getResources().getString(R.string.makeup_fendi_name));
        item.m_index = 1;
        item.m_type = MakeupTypeList.MakeupTypeItem.FOUNDATION;
        out.add(item);

        item = new MakeupTypeList.MakeupTypeItem(getContext());
        item.SetData(R.drawable.beautify_makeup_saihong_icon, R.drawable.beautify_makeup_saihong_icon, getResources().getString(R.string.makeup_saihong_name));
        item.m_index = 2;
        item.m_type = MakeupTypeList.MakeupTypeItem.CHEEK_L;
        out.add(item);

        item = new MakeupTypeList.MakeupTypeItem(getContext());
        item.SetData(R.drawable.beautify_makeup_chuncai_icon, R.drawable.beautify_makeup_chuncai_icon, getResources().getString(R.string.makeup_chuncai_name));
        item.m_index = 3;
        item.m_type = MakeupTypeList.MakeupTypeItem.LIP;
        out.add(item);

        item = new MakeupTypeList.MakeupTypeItem(getContext());
        item.SetData(R.drawable.beautify_makeup_meimao_icon, R.drawable.beautify_makeup_meimao_icon, getResources().getString(R.string.makeup_meimao_name));
        item.m_index = 4;
        item.m_type = MakeupTypeList.MakeupTypeItem.EYEBROW_L;
        out.add(item);

        item = new MakeupTypeList.MakeupTypeItem(getContext());
        item.SetData(R.drawable.beautify_makeup_yanying_icon, R.drawable.beautify_makeup_yanying_icon, getResources().getString(R.string.makeup_yanying_name));
        item.m_index = 5;
        item.m_type = MakeupTypeList.MakeupTypeItem.KOHL_L;
        out.add(item);

        item = new MakeupTypeList.MakeupTypeItem(getContext());
        item.SetData(R.drawable.beautify_makeup_yanxian_icon, R.drawable.beautify_makeup_yanxian_icon, getResources().getString(R.string.makeup_yanxian_name));
        item.m_index = 6;
        item.m_type = MakeupTypeList.MakeupTypeItem.EYELINER_UP_L;
        out.add(item);

        item = new MakeupTypeList.MakeupTypeItem(getContext());
        item.SetData(R.drawable.beautify_makeup_jiemao_icon, R.drawable.beautify_makeup_jiemao_icon, getResources().getString(R.string.makeup_jiemao_name));
        item.m_index = 7;
        item.m_type = MakeupTypeList.MakeupTypeItem.EYELASH_UP_L;
        out.add(item);

        item = new MakeupTypeList.MakeupTypeItem(getContext());
        item.SetData(R.drawable.beautify_makeup_meitong_icon, R.drawable.beautify_makeup_meitong_icon, getResources().getString(R.string.makeup_meitong_name));
        item.m_index = 8;
        item.m_type = MakeupTypeList.MakeupTypeItem.EYE_L;
        out.add(item);

        return out;
    }

    private void setImg(Object params) {
        if (params instanceof RotationImg2[]) {
            m_imgInfo = ((RotationImg2[]) params)[0];
//           m_org = Utils.DecodeShowImage((Activity)getContext(), temp[0].m_img, rotation, -1, flip);
            /*Bitmap temp = Utils.DecodeImage(getContext(), m_imgInfo.m_img, m_imgInfo.m_degree, -1, m_frW, m_frH);
            m_org = MakeBmpV2.CreateBitmapV2(temp, m_imgInfo.m_degree, m_imgInfo.m_flip, -1, m_frW, m_frH, Bitmap.Config.ARGB_8888);
            if(temp != null)
            {
                temp.recycle();
                temp = null;
            }*/
            m_org = cn.poco.imagecore.Utils.DecodeFinalImage(getContext(), m_imgInfo.m_img, m_imgInfo.m_degree, -1, m_imgInfo.m_flip, DEF_IMG_SIZE, DEF_IMG_SIZE);
        } else if (params instanceof Bitmap) {
            m_org = (Bitmap) params;
        }

        if (m_org != null) {
            m_orgBmpWidth = m_org.getWidth();
            m_orgBmpHeight = m_org.getHeight();
            m_viewNew.setImage(m_org);
        }

        if (FaceDataV2.sFaceIndex == -1 || FaceDataV2.FACE_POS_MULTI == null) {
            SetWaitUI(true, "");
            m_uiEnabled = false;
            m_isFaceChecked = false;
            m_viewNew.m_faceIndex = FaceDataV2.sFaceIndex;
            Message msg = Message.obtain();
            msg.what = MakeupHandler.CHECK_FACE;
            msg.obj = m_org;
            m_makeupHandler.sendMessage(msg);
        } else {
            if (FaceDataV2.FACE_POS_MULTI != null) {
                if (FaceLocalData.getInstance() == null) {
                    m_faceLocalData = FaceLocalData.getNewInstance(FaceDataV2.FACE_POS_MULTI.length);
                } else {
                    m_faceLocalData = FaceLocalData.getInstance();
                }

                if (FaceDataV2.sFaceIndex != -1) {
                    m_viewNew.m_faceIndex = FaceDataV2.sFaceIndex;
                }

                m_asetUriRecords = new int[FaceDataV2.FACE_POS_MULTI.length];
                initAsetUriRecord();
                m_tongjiIDArrs = new HashMap[FaceDataV2.FACE_POS_MULTI.length];
                initTongjiIDArrs();
            }

            if(mFixView != null)
            {
                mFixView.showJitterAnimAccordingStatus();
            }

            //打开相应的主题
            if (m_themeUri != -1) {
                SetSelectByUri(m_themeUri);
            }
        }

        ShowStartAnim();
//        ShowBottomFrAnim();
    }

    /**
     * 人脸识别之后，初始化一个记录每个人脸要统计的id的集合，
     * 要记录的id是每个人脸选中的主题彩妆的统计id和局部选中的选项的统计id
     */
    private void initTongjiIDArrs() {
        for (int i = 0; i < FaceDataV2.FACE_POS_MULTI.length; i++) {
            m_tongjiIDArrs[i] = new HashMap<>();
        }
    }

    public void SetBmp(Bitmap bmp, Bitmap bk) {
        if (bmp != null) {
            setImg(bmp);
        }

        if (bk != null) {
            this.setBackgroundDrawable(new BitmapDrawable(bk));
        }
    }

    //页面开始动画结束和人脸识别全部结束才可点击
    private void isFinishedStartAll() {
        if (m_isStartAnimFinished && m_isFaceChecked) {
            m_uiEnabled = true;
        }
    }

    public void ShowViewAnim(final View view, int StartY, int EndY, float startScale, float endScale, int frW, int frH) {
        if (view != null) {
            AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator object1 = ObjectAnimator.ofFloat(view, "scaleX", startScale, endScale);
            ObjectAnimator object2 = ObjectAnimator.ofFloat(view, "scaleY", startScale, endScale);
            ObjectAnimator object3 = ObjectAnimator.ofFloat(view, "translationY", StartY, EndY);
            ObjectAnimator object4 = ObjectAnimator.ofFloat(m_bottomFr, "translationY", m_bottomBarHeight + m_bottomListHeight + m_bottomTypeListHeight, 0);
            ObjectAnimator object5 = ObjectAnimator.ofFloat(m_asetListNew, "translationY", m_bottomBarHeight + m_bottomListHeight + m_bottomTypeListHeight, 0);
            animatorSet.setDuration(300);
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSet.playTogether(object1, object2, object3, object4, object5);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (view != null) {
                        view.clearAnimation();
                    }
                    if (mFixView != null && m_multiFaceUiEnable) {
                        setScaleAnim(mFixView, false);
                    }

                    if (m_multifaceFr != null && FaceDataV2.sFaceIndex >= 0 && m_faceLocalData != null && m_faceLocalData.m_faceNum > 1) {
                        setScaleAnim(m_multifaceFr, false);
                    }
                    m_isStartAnimFinished = true;
                    isFinishedStartAll();
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    m_isStartAnimFinished = false;
                }
            });
            animatorSet.start();
        }
    }


    private class MakeupViewExCB implements ControlCallback {

        @Override
        public void OnTouchEyebrow(boolean isLeft) {
            if (m_viewNew != null) {
                m_makeupTypeList.SetSelectIndex(MakeupTypeList.MakeupTypeItem.getItemIndexByType(m_makeupTypeItems, MakeupTypeList.MakeupTypeItem.EYEBROW_L));
                m_viewNew.SetShowRectFlag();
            }
        }

        @Override
        public void OnTouchEye(boolean isLeft) {
            if (m_viewNew != null) {
                m_makeupTypeList.SetSelectIndex(MakeupTypeList.MakeupTypeItem.getItemIndexByType(m_makeupTypeItems, MakeupTypeList.MakeupTypeItem.EYELASH_UP_L));
                m_viewNew.SetShowRectFlag();
            }
        }

        @Override
        public void onFingerUp() {

        }

        @Override
        public void OnTouchCheek(boolean isLeft) {
            if (m_viewNew != null) {
                m_makeupTypeList.SetSelectIndex(MakeupTypeList.MakeupTypeItem.getItemIndexByType(m_makeupTypeItems, MakeupTypeList.MakeupTypeItem.CHEEK_L));
                m_viewNew.SetShowRectFlag();
            }
        }

        @Override
        public void OnTouchLip() {
            if (m_viewNew != null) {
                m_makeupTypeList.SetSelectIndex(MakeupTypeList.MakeupTypeItem.getItemIndexByType(m_makeupTypeItems, MakeupTypeList.MakeupTypeItem.LIP));
                m_viewNew.SetShowRectFlag();
            }
        }

        @Override
        public void OnTouchFoundation() {
            if (m_viewNew != null) {
                m_makeupTypeList.SetSelectIndex(MakeupTypeList.MakeupTypeItem.getItemIndexByType(m_makeupTypeItems, MakeupTypeList.MakeupTypeItem.FOUNDATION));
                m_viewNew.SetShowRectFlag();
            }
        }

        @Override
        public void UpdateSonWin(Bitmap bitmap, float x, float y) {
            if (m_sonWin != null) {
                m_sonWin.SetData(bitmap, (int) x, (int) y);
            }

        }

        @Override
        public void On3PosModify() {
            m_posModify = true;
        }

        @Override
        public void OnAllPosModify() {
            m_posModify = true;
            SendMakeupMsg();
        }

        @Override
        public void onTouchWatermark() {

        }


        @Override
        public void OnSelFaceIndex(int index) {
            if (m_viewNew != null) {
                m_uiEnabled = true;
                m_multiFaceUiEnable = true;
                setEnableForList(true);
                m_viewNew.m_faceIndex = index;
                FaceDataV2.sFaceIndex = index;
                m_viewNew.setMode(BeautyCommonViewEx.MODE_NORMAL);
                m_viewNew.DoSelFaceAnim();
                setScaleAnim(m_multifaceFr, false);
                setScaleAnim(mFixView, false);
                if (isHasMakeupData()) {
                    setScaleAnim(m_compareBtn, false);
                }
                m_multifaceTips.setVisibility(GONE);
                m_bottomFr.setVisibility(VISIBLE);

                if (m_shouldAddfirstTheme) {
                    m_shouldAddfirstTheme = false;
                    SetSelectByUri(m_themeUri);
                } else {
                    if (m_curMakeupType == MakeupTypeList.MakeupTypeItem.ALL) {
                        m_asetListNew.setVisibility(VISIBLE);
                    } else if (m_curMakeupType != -1) {
                        m_replaceListNew.setVisibility(VISIBLE);
                    }
                    //恢复当前人脸之前选择的主题套装
                    if (m_asetUriRecords != null && index < m_asetUriRecords.length) {
                        refreshBottomList(m_asetUriRecords[index]);
                    }

                    //恢复当前人脸之前选择的选项
                    SetSelectByMakeupPartListIndex();
                }
            }
        }

        @Override
        public void OnAnimFinish() {
            if (m_ischangePointAnimResetStart) {
                setScaleAnim(mFixView, false);
                if (FaceDataV2.RAW_POS_MULTI != null && FaceDataV2.RAW_POS_MULTI.length > 1) {
                    setScaleAnim(m_multifaceFr, false);
                }
                m_ischangePointAnimResetStart = false;
            }
        }
    }


    private boolean m_ischangePointAnimResetStart;//是否是从改变定点状态返回的动画

    //有点击动画的按钮点击监听
    private class BtnOnclickListenerNew extends OnAnimationClickListener {

        public int touchTag = -1;

        @Override
        public void onAnimationClick(View v) {
            if (m_uiEnabled) {
                if (v == m_backBtn) {
                    onBackBtn();
                } else if (v == m_okBtn && m_multiFaceUiEnable) {
                    MyBeautyStat.onClickByRes(R.string.美颜美图_彩妆页面_主页面_确认);
                    SendTongji();
                    TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_彩妆_确认);

                    //判断显示阿玛尼广告
                    int tempID = isShouldArManiShowAD();
                    if (tempID != -1) {
                        ShowAD(tempID);
                    } else {
                        MyBeautyStat.onUseMakeup(getTongjiMakeup());
                        HashMap<String, Object> params = new HashMap<>();
                        params.put("img", m_viewNew.getImage());
                        params.putAll(getBackAnimParam());
                        params.put("yslchoose",m_isYSLChoose);
                        m_site.onSave(getContext(), params);

                        if (m_org != null) {
                            m_org = null;
                        }
                    }
                } else if (v == m_multifaceFr && touchTag == m_multifaceFr.hashCode()) {
                    touchTag = -1;
                    m_multiFaceUiEnable = false;
                    setEnableForList(false);
                    showSelectFace();
                    m_viewNew.Restore();
                } else if (v == mFixView && touchTag == mFixView.hashCode()) {
                    touchTag = -1;
                    MyBeautyStat.onClickByRes(R.string.美颜美图_彩妆页面_主页面_手动定点);
                    TongJiUtils.onPageStart(getContext(), R.string.定点);
                    TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_彩妆_手动定点);
                    mFixView.modifyStatus();
                    m_posModify = false;
                    m_viewNew.copyFaceData();
                    m_isChangePointing = true;
                    switch (m_curMakeupType) {
                        case MakeupTypeList.MakeupTypeItem.FOUNDATION:
                        case MakeupTypeList.MakeupTypeItem.CHEEK_L:
                            m_changePointCB.onClick(m_changePointFr.m_checkCheekBtn);
                            break;
                        case MakeupTypeList.MakeupTypeItem.EYE_L:
                        case MakeupTypeList.MakeupTypeItem.EYELASH_UP_L:
                        case MakeupTypeList.MakeupTypeItem.EYELINER_UP_L:
                        case MakeupTypeList.MakeupTypeItem.KOHL_L:
                            m_changePointCB.onClick(m_changePointFr.m_checkEyeBtnL);
                            break;
                        case MakeupTypeList.MakeupTypeItem.EYEBROW_L:
                            m_changePointCB.onClick(m_changePointFr.m_checkEyebrowBtn);
                            break;
                        case MakeupTypeList.MakeupTypeItem.LIP:
                            m_changePointCB.onClick(m_changePointFr.m_checkLipBtn);
                            break;
                        default:
                            m_changePointCB.onClick(m_changePointFr.m_checkLipBtn);
                            break;
                    }
                    m_changePointFr.setVisibility(VISIBLE);
                    TranslateAnimVertical(m_changePointFr, (float) (ShareData.PxToDpi_xhdpi(320)), 0f);
                }
            }
        }

        @Override
        public void onTouch(View v) {
            touchTag = v.hashCode();
        }

        @Override
        public void onRelease(View v) {

        }
    }


    //策神统计
    private ArrayList<MyBeautyStat.Makeup> getTongjiMakeup() {
        ArrayList<MyBeautyStat.Makeup> out = new ArrayList<>();
        if (m_faceLocalData != null && FaceDataV2.sFaceIndex > -1 && FaceDataV2.sFaceIndex < m_faceLocalData.m_makeupDatas_multi.length) {
            if(m_faceLocalData.m_makeupDatas_multi[FaceDataV2.sFaceIndex] != null)
            {
                for (int i = 0; i < m_faceLocalData.m_makeupDatas_multi[FaceDataV2.sFaceIndex].size(); i++) {
                    MakeupRes.MakeupData makeupData = m_faceLocalData.m_makeupDatas_multi[FaceDataV2.sFaceIndex].get(i);
                    if (makeupData != null && isShowOnUI(makeupData.m_id)) {
                        MyBeautyStat.Makeup makeup = new MyBeautyStat.Makeup();
                        if (makeupData.m_makeupType == FOUNDATION.GetValue()) {
                            makeup.type = MyBeautyStat.MakeupType.粉底;
                            makeup.alpha = m_faceLocalData.m_makeupAlphas_multi[FaceDataV2.sFaceIndex].m_foundationAlpha;
                        } else if (makeupData.m_makeupType == MakeupType.CHEEK_L.GetValue()) {
                            makeup.type = MyBeautyStat.MakeupType.腮红;
                            makeup.alpha = m_faceLocalData.m_makeupAlphas_multi[FaceDataV2.sFaceIndex].m_cheekAlpha;
                        } else if (makeupData.m_makeupType == MakeupType.LIP.GetValue()) {
                            makeup.type = MyBeautyStat.MakeupType.唇彩;
                            makeup.alpha = m_faceLocalData.m_makeupAlphas_multi[FaceDataV2.sFaceIndex].m_lipAlpha;
                        } else if (makeupData.m_makeupType == MakeupType.EYEBROW_L.GetValue()) {
                            makeup.type = MyBeautyStat.MakeupType.眉毛;
                            makeup.alpha = m_faceLocalData.m_makeupAlphas_multi[FaceDataV2.sFaceIndex].m_eyebrowAlpha;
                        } else if (makeupData.m_makeupType == MakeupType.KOHL_L.GetValue()) {
                            makeup.type = MyBeautyStat.MakeupType.眼影;
                            makeup.alpha = m_faceLocalData.m_makeupAlphas_multi[FaceDataV2.sFaceIndex].m_kohlAlpha;
                        } else if (makeupData.m_makeupType == MakeupType.EYELINER_UP_L.GetValue()) {
                            makeup.type = MyBeautyStat.MakeupType.眼线;
                            makeup.alpha = m_faceLocalData.m_makeupAlphas_multi[FaceDataV2.sFaceIndex].m_eyelineUpAlpha;
                        } else if (makeupData.m_makeupType == MakeupType.EYELASH_UP_L.GetValue()) {
                            makeup.type = MyBeautyStat.MakeupType.睫毛;
                            makeup.alpha = m_faceLocalData.m_makeupAlphas_multi[FaceDataV2.sFaceIndex].m_eyelashUpAlpha;
                        } else if (makeupData.m_makeupType == MakeupType.EYE_L.GetValue()) {
                            makeup.type = MyBeautyStat.MakeupType.美瞳;
                            makeup.alpha = m_faceLocalData.m_makeupAlphas_multi[FaceDataV2.sFaceIndex].m_eyeAlpha;
                        }
                        if(makeup.type != null)
                        {
                            makeup.resTjId = String.valueOf(m_tongjiIDArrs[FaceDataV2.sFaceIndex].get(makeupData.m_makeupType));
                            out.add(makeup);
                        }
                    }
                }
            }

            if(m_asetUriRecords[FaceDataV2.sFaceIndex] != -1)
            {
                MyBeautyStat.Makeup makeup = new MyBeautyStat.Makeup();
                makeup.resTjId = String.valueOf(m_tongjiIDArrs[FaceDataV2.sFaceIndex].get(m_asetTongjiFlag));
                makeup.alpha = m_faceLocalData.m_asetAlpha_multi[FaceDataV2.sFaceIndex];
                makeup.type = MyBeautyStat.MakeupType.组合;
                out.add(makeup);
            }
        }

        return out;
    }

    private boolean isShowOnUI(int id)
    {
        boolean isShow = false;
        ArrayList<MakeupRes> allres = MakeupResMgr2.getInstance().sync_GetLocalRes(getContext(), null);
        if(allres != null && allres.size() > 0)
        {
            for(int i = 0; i < allres.size();i++)
            {
                if(HasId(allres.get(i).m_groupRes,id) != -1)
                {
                    isShow = true;
                    break;
                }
            }
        }
        return isShow;
    }


    public static int HasId(MakeupRes.MakeupData[] idArr, int id)
    {
        int out = -1;

        if(idArr != null)
        {
            int len = idArr.length;
            for(int i = 0; i < len; i++)
            {
                if(idArr[i].m_id == id)
                {
                    out = i;
                    break;
                }
            }
        }

        return out;
    }




    //没点击动画的按钮点击监听
    private class BtnOnclickLinsener implements OnClickListener {

        @Override
        public void onClick(View v) {
            if(m_uiEnabled)
            {
                if(v == m_titleBtn && m_multiFaceUiEnable)
                {
                    isFold = !isFold;
                    showBottomViewAnim(isFold);
                    if(isFold)
                    {
                        MyBeautyStat.onClickByRes(R.string.美颜美图_彩妆页面_主页面_收回bar);
                    }
                    else
                    {
                        MyBeautyStat.onClickByRes(R.string.美颜美图_彩妆页面_主页面_展开bar);
                    }
                }
            }
        }
    }

    private HashMap<String, Object> getBackAnimParam()
    {
        HashMap<String, Object> params = new HashMap<>();
        params.put(Beautify4Page.PAGE_BACK_ANIM_VIEW_TOP_MARGIN, (m_viewNew.getHeight() - m_finalFrH)/2f);
        params.put(Beautify4Page.PAGE_BACK_ANIM_IMG_H, m_viewNew.getImgHeight());
        return params;
    }


    //是否至少选了一个彩妆
    private boolean isHasMakeupData()
    {
        boolean out = false;
        if(m_faceLocalData != null)
        {
            if(m_faceLocalData.m_makeupDatas_multi != null && m_faceLocalData.m_makeupDatas_multi.length > 0)
            {
                for(int i = 0; i < m_faceLocalData.m_makeupDatas_multi.length; i++)
                {
                    ArrayList<MakeupRes.MakeupData> temp = m_faceLocalData.m_makeupDatas_multi[i];
                    if(temp.size() > 0)
                    {
                        out = true;
                        return out;
                    }
                }
            }
        }
        return out;
    }


    private int isHasADData()
    {
        int out = -1;
//        out = MakeupADUtil.isClalenAD(m_aSetSelUri);
//        if(out != -1)
//        {
//            out = m_aSetSelUri;
//            return out;
//        }

        int temp = isHasADUri2();
        if(temp != -1)
        {
            out = temp;
        }

        return out;
    }



    private int isHasADUri2()
    {
        int out = -1;
        if(m_faceLocalData.m_makeupDatas_multi != null && m_faceLocalData.m_makeupDatas_multi.length > 0)
        {
            for(int i = 0; i < m_faceLocalData.m_makeupDatas_multi.length; i++)
            {
                ArrayList<MakeupRes.MakeupData> temp = m_faceLocalData.m_makeupDatas_multi[i];
                for(int j = 0; j < temp.size(); j++)
                {
                    if(temp.get(j).m_makeupType == MakeupType.EYE_L.GetValue() && MakeupADUtil.isClalenAD(temp.get(j).m_id) != -1)
                    {
                        out = temp.get(j).m_id;
                        return out;
                    }
                }
            }
        }
        return out;
    }

        private void SendTongji()
        {
           if(m_tongjiIDArrs != null && m_tongjiIDArrs.length > 0)
           {
               for(int i = 0; i < m_tongjiIDArrs.length; i++)
               {
                   if(m_tongjiIDArrs[i] != null && m_tongjiIDArrs[i].size() > 0)
                   {
                       for(Integer key: m_tongjiIDArrs[i].keySet())
                       {
                           if(key == m_asetTongjiFlag)
                           {
                               if(m_tongjiIDArrs[i].get(key) != 0 &&  m_asetUriRecords[i] != RecommendMakeupItemList.NullItemInfo.NULL_ITEM_URI)
                               {
                                 TongJi2.AddCountById(m_tongjiIDArrs[i].get(key) + "");
                               }
                           }
                           else
                           {
                               if(m_tongjiIDArrs[i].get(key) != 0)
                               {
                                   TongJi2.AddCountById(m_tongjiIDArrs[i].get(key) + "");
                               }
                           }
                       }
                   }
               }
           }
        }


    //记录当前人脸的主题彩妆选项
    private void recordAsetUriByFaceIndex(int uri)
    {
        if(m_asetUriRecords != null && FaceDataV2.sFaceIndex < m_asetUriRecords.length)
        {
            m_asetUriRecords[FaceDataV2.sFaceIndex] = uri;
        }
    }

    private void onBackBtn()
    {

        if(m_adFr != null)
        {
            CloseAD();
            return;
        }

        if(m_recomView != null && m_recomView.IsShow())
        {
            m_recomView.OnCancel(true);
            return;
        }

        if(m_isChangePointing)
        {
            m_changePointCB.onClick(m_changePointFr.m_checkBackBtn);
            return;
        }

        if (m_isChange) {
            showExitDialog();
        } else {
            onExit();
        }
    }

    private void onExit()
    {
        MyBeautyStat.onClickByRes(R.string.美颜美图_彩妆页面_主页面_取消);
        TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_彩妆_取消);

        if(m_site != null)
        {
            HashMap<String,Object> params = new HashMap<String, Object>();
            params.put("img",m_org);
            params.putAll(getBackAnimParam());
            m_site.onBack(getContext(), params);
        }
    }

    private void setScaleAnim(final View view, boolean hide) {
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

    private void SetUIByPage(int page)
    {
        switch (page)
        {
            case MAKEUP_PAGE:
                m_bottomFr.setVisibility(VISIBLE);
                m_changePointFr.setVisibility(GONE);
                if (isHasMakeupData()) {
                    setScaleAnim(m_compareBtn, false);
                }
                if(m_curMakeupType == MakeupTypeList.MakeupTypeItem.ALL)
                {
                    m_asetListNew.setVisibility(VISIBLE);
                }
                else
                {
                    m_replaceListNew.setVisibility(VISIBLE);
                }
                break;
            case CHANGEPOINT_PAGE_THREE:
                m_changePointFr.setVisibility(VISIBLE);
                m_bottomFr.setVisibility(GONE);
                m_asetListNew.setVisibility(GONE);
                m_replaceListNew.setVisibility(GONE);
                setScaleAnim(m_multifaceFr, true);
                setScaleAnim(mFixView, true);
                setScaleAnim(m_compareBtn, true);
                break;
            case CHANGEPOINT_PAGE_MULTI:
                m_changePointFr.setVisibility(VISIBLE);
                m_bottomFr.setVisibility(GONE);
                m_asetListNew.setVisibility(GONE);
                m_replaceListNew.setVisibility(GONE);
                setScaleAnim(mFixView, true);
                setScaleAnim(m_compareBtn, true);
                setScaleAnim(m_multifaceFr, true);
                break;
        }
    }


    //初始化当前每个人脸选择的主题彩妆的uri
    private void initAsetUriRecord()
    {
        if(FaceDataV2.FACE_POS_MULTI != null)
        {
            if(m_asetUriRecords == null)
            {
                m_asetUriRecords = new int[FaceDataV2.FACE_POS_MULTI.length];
            }

            for(int i = 0; i < FaceDataV2.FACE_POS_MULTI.length;i ++)
            {
                m_asetUriRecords[i] = -1;
            }
        }
    }

    public void doAnim(int flag)
    {
        m_viewNew.Data2UI();
        m_viewNew.setMode(BeautyCommonViewEx.MODE_MAKEUP);
        switch (flag)
        {
            case CHECK_TRHEE:
                m_viewNew.setMode(BeautyCommonViewEx.MODE_FACE);
                m_viewNew.m_showPosFlag = MakeUpViewEx1.POS_THREE;
                m_viewNew.m_touchPosFlag = m_viewNew.m_showPosFlag;
                m_viewNew.DoFixedPointAnim();
                break;
            case CHECK_INDEX_EYE_L:
                m_viewNew.m_showPosFlag = MakeUpViewEx1.POS_EYE;
                m_viewNew.m_touchPosFlag = m_viewNew.m_showPosFlag;
                m_viewNew.DoFixedPointAnim();
                break;
            case CHECK_INDEX_EYEBROW:
                m_viewNew.m_showPosFlag = MakeUpViewEx1.POS_EYEBROW;
                m_viewNew.m_touchPosFlag = m_viewNew.m_showPosFlag;
                m_viewNew.DoFixedPointAnim();
                break;
            case CHECK_INDEX_LIP:
                m_viewNew.m_showPosFlag = MakeUpViewEx1.POS_LIP;
                m_viewNew.m_touchPosFlag = m_viewNew.m_showPosFlag;
                m_viewNew.DoFixedPointAnim();
                break;
            case CHECK_INDEX_CHEEK:
                m_viewNew.m_showPosFlag = MakeUpViewEx1.POS_CHEEK;
                m_viewNew.m_touchPosFlag = m_viewNew.m_showPosFlag;
                m_viewNew.DoFixedPointAnim();
                break;
            case CHECK_INDEX_NOSE:
                m_viewNew.m_showPosFlag = MakeUpViewEx1.POS_NOSE;
                m_viewNew.m_touchPosFlag = m_viewNew.m_showPosFlag;
                m_viewNew.DoFixedPointAnim();
                break;
        }
    }


    private void showSelectFace()
    {
        setScaleAnim(m_multifaceFr, true);
        setScaleAnim(mFixView, true);
        setScaleAnim(m_compareBtn, true);
        m_multifaceTips.setVisibility(VISIBLE);
        m_viewNew.setMode(BeautyCommonViewEx.MODE_SEL_FACE);
    }


    protected int m_aSetSelUri;

    private void DeleteNewFlag(AbsAlphaFrExAdapter.ItemInfo info)
    {
        //去除new状态
        if(info.m_style == AbsAlphaFrExAdapter.ItemInfo.Style.NEW)
        {
            int themeId = ((MakeupGroupRes)info.m_ex).m_id;
            MakeupComboResMgr2.getInstance().DeleteNewFlag(getContext(), themeId);

            int[] is = AbsAlphaFrExAdapter.GetSubIndexByUri(m_aSetDatasNew, themeId);
            if(is[0] >= 0)
            {
                m_aSetDatasNew.get(is[0]).m_style = AbsAlphaFrExAdapter.ItemInfo.Style.NORMAL;
            }
        }
    }




    private void SetMakeupData(MakeupRes data)
    {
        if(data != null)
        {
            //记录统计id
            if(data.m_groupObj != null && data.m_groupObj.length > 0)
            {
                for(int i = 0; i < data.m_groupObj.length; i++)
                {
                    MakeupRes temp = data.m_groupObj[i];
                    if(m_tongjiIDArrs != null && FaceDataV2.sFaceIndex < m_tongjiIDArrs.length && m_tongjiIDArrs[FaceDataV2.sFaceIndex] != null && temp != null && temp.m_groupRes != null && temp.m_groupRes.length > 0)
                    {
                        m_tongjiIDArrs[FaceDataV2.sFaceIndex].put(temp.m_groupRes[0].m_makeupType,temp.m_tjId);
                    }
                }
            }

            //s_makeupDatas.clear();
            if(m_faceLocalData != null)
            {
                m_faceLocalData.m_makeupDatas_multi[sFaceIndex].clear();

                if(data.m_groupRes != null)
                {
                    MakeupRes.MakeupData item;
                    for(int i = 0; i < data.m_groupRes.length; i++)
                    {
                        item = data.m_groupRes[i];
                        AddMakeupItem(item);

                        //重置透明度
                        int type = item.m_makeupType;
                        int alpha = item.m_defAlpha;
                        //判断是否有套装颜色
                        if(data.m_groupAlphas != null && data.m_groupAlphas.length > 0)
                        {
                            alpha = data.GetComboAlpha(type);
                        }
                        if(type == MakeupType.KOHL_L.GetValue())
                        {
                            m_faceLocalData.m_makeupAlphas_multi[sFaceIndex].m_kohlAlpha = alpha;
                        }
                        if(type == EYELINER_DOWN_L.GetValue())
                        {
                            m_faceLocalData.m_makeupAlphas_multi[sFaceIndex].m_eyelineDownAlpha = alpha;
                        }
                        if(type == EYELINER_UP_L.GetValue())
                        {
                            m_faceLocalData.m_makeupAlphas_multi[sFaceIndex].m_eyelineUpAlpha = alpha;
                        }
                        if(type == MakeupType.EYE_L.GetValue())
                        {
                            m_faceLocalData.m_makeupAlphas_multi[sFaceIndex].m_eyeAlpha = alpha;
                        }
                        if(type == EYELASH_DOWN_L.GetValue())
                        {
                            m_faceLocalData.m_makeupAlphas_multi[sFaceIndex].m_eyelashDownAlpha = alpha;
                        }
                        if(type == EYELASH_UP_L.GetValue())
                        {
                            m_faceLocalData.m_makeupAlphas_multi[sFaceIndex].m_eyelashUpAlpha = alpha;
                        }
                        if(type == MakeupType.EYEBROW_L.GetValue())
                        {
                            m_faceLocalData.m_makeupAlphas_multi[sFaceIndex].m_eyebrowAlpha = alpha;
                        }
                        if(type == MakeupType.LIP.GetValue())
                        {
                            m_faceLocalData.m_makeupAlphas_multi[sFaceIndex].m_lipAlpha = alpha;
                        }
                        if(type == MakeupType.CHEEK_L.GetValue())
                        {
                            m_faceLocalData.m_makeupAlphas_multi[sFaceIndex].m_cheekAlpha = alpha;
                        }
                        if(type == FOUNDATION.GetValue())
                        {
                            m_faceLocalData.m_makeupAlphas_multi[sFaceIndex].m_foundationAlpha = alpha;
                        }
                    }
                }
            }
        }
    }

    protected void SendMakeupMsg()
    {
//        if(!m_isChangePointing)
//        {
            SetWaitUI(true,"");
//        }
        MakeupHandler.MakeupMsg makeupMsg = new MakeupHandler.MakeupMsg();
        if(m_org != null && !m_org.isRecycled())
        {
            makeupMsg.m_bmp = m_org;
        }
        makeupMsg.m_faceLocalData = m_faceLocalData.Clone();
        Message msg = Message.obtain();
        msg.what = MAKEUP;
        msg.obj = makeupMsg;
        m_makeupHandler.sendMessage(msg);
    }


         private boolean AddMakeupItem(MakeupRes.MakeupData data)
         {
             boolean out = false;

             if(m_faceLocalData.m_makeupDatas_multi[sFaceIndex] != null && data != null)
             {
                 int index = BeautifyResMgr2.GetInsertIndex(m_faceLocalData.m_makeupDatas_multi[sFaceIndex], MakeupType.GetType(data.m_makeupType));
                 if(index >= 0)
                 {
                     m_faceLocalData.m_makeupDatas_multi[sFaceIndex].add(index, data);
                 }
             }

             return out;
         }


    private ArrayList<MakeupRLAdapter.ItemInfo> getMakeupRessNew(int type)
    {
        ArrayList<MakeupRLAdapter.ItemInfo> out = new ArrayList<>();
        ArrayList<MakeupRes> res = getRes(type);

        if(res != null && res.size() > 0)
        {
            for(int i = 0;i < res.size();i++)
            {
                MakeupRes temp = res.get(i);
                if(temp.m_id == 0)
                {
                    MakeupRLAdapter.NullItemInfo item1 = new MakeupRLAdapter.NullItemInfo(R.drawable.photofactory_makeup_item_null_out,temp.m_maskColor);
                    item1.m_ex = temp;
                    out.add(item1);
                }
                else
                {
                    MakeupRLAdapter.ItemInfo item2 = new MakeupRLAdapter.ItemInfo(temp.m_id,temp.m_thumb,temp.m_name);
                    item2.m_maskColor = temp.m_maskColor;
                    item2.m_ex = temp;
                    out.add(item2);
                }
            }
        }
        return out;
    }


    private ArrayList<MakeupRes> getRes(int type)
    {
        ArrayList<MakeupRes> res = new ArrayList<>();
        switch (type)
        {
            case MakeupTypeList.MakeupTypeItem.ALL:
                res = BeautifyResMgr2.GetRes(FOUNDATION,true);
                break;
            case MakeupTypeList.MakeupTypeItem.CHEEK_L:
                res = BeautifyResMgr2.GetRes(MakeupType.CHEEK_L,true);
                break;
            case MakeupTypeList.MakeupTypeItem.EYE_L:
                res = BeautifyResMgr2.GetRes(MakeupType.EYE_L,true);
                break;
            case MakeupTypeList.MakeupTypeItem.EYEBROW_L:
                res = BeautifyResMgr2.GetRes(MakeupType.EYEBROW_L,true);
                break;
            case MakeupTypeList.MakeupTypeItem.EYELASH_UP_L:
                res = BeautifyResMgr2.GetRes(EYELASH_UP_L,false);
                break;
            case MakeupTypeList.MakeupTypeItem.EYELINER_UP_L:
                res = BeautifyResMgr2.GetRes(EYELINER_UP_L,false);
                break;
            case MakeupTypeList.MakeupTypeItem.FOUNDATION:
                res = BeautifyResMgr2.GetRes(FOUNDATION,true);
                break;
            case MakeupTypeList.MakeupTypeItem.KOHL_L:
                res = BeautifyResMgr2.GetRes(MakeupType.KOHL_L,false);
                break;
            case MakeupTypeList.MakeupTypeItem.LIP:
                res = BeautifyResMgr2.GetRes(MakeupType.LIP,false);
                break;
        }
        return res;
    }


    private void ShowStartAnim()
    {
        if(m_viewH > 0 && m_imgH > 0)
        {
            int tempStartY = (int) (ShareData.PxToDpi_xhdpi(90) + (m_viewH - m_frH)/2f);
            float scale1 = (float)m_frW / (float)m_orgBmpWidth;
            float scale2 = (float)m_frH / (float)m_orgBmpHeight;
            float tempImgH =  m_orgBmpHeight * ((scale1 > scale2) ? scale2 : scale1);
            float scale = m_imgH/tempImgH;
            ShowViewAnim(m_viewNew,tempStartY,0,scale,1f,m_frW,m_frH);
        }
        else
        {
            if(mFixView != null)
            {
                setScaleAnim(mFixView, false);
            }
        }
    }


    private void showBottomViewAnim(final boolean isFold)
    {
        m_bottomFr.clearAnimation();
        m_viewNew.clearAnimation();

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
            endBottom = (float) (m_bottomListHeight + m_bottomTypeListHeight);

            startView = 0f;
            endView = ((float) (m_bottomListHeight + m_bottomTypeListHeight)) / 2f;

            startCheck = 0f;
            endCheck = endView;
        }
        else
        {
            startBottom = (float) (m_bottomListHeight + m_bottomTypeListHeight);
            endBottom = 0f;

            startView = ((float) (m_bottomListHeight + m_bottomTypeListHeight)) / 2f;
            endView = 0f;

            startCheck = startView;
            endCheck = 0f;
        }
        ObjectAnimator object1 = ObjectAnimator.ofFloat(m_bottomFr, "translationY", startBottom, endBottom);
        object1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float tempDis = (float) animation.getAnimatedValue();
                if(m_viewNew != null)
                {
                    LayoutParams fl = (LayoutParams) m_viewNew.getLayoutParams();
                    fl.height = (int) (m_finalFrH + tempDis);
                    m_viewNew.setLayoutParams(fl);
                }
            }
        });
        object1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(m_viewNew != null)
                {
                    if(isFold)
                    {
                        m_viewNew.InitAnimDate(m_frW,m_finalFrH,m_frW,m_finalFrH + (m_bottomListHeight + m_bottomTypeListHeight));
                    }
                    else
                    {
                        m_viewNew.InitAnimDate(m_frW,m_finalFrH + (m_bottomListHeight + m_bottomTypeListHeight),m_frW,m_finalFrH);
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
        ObjectAnimator object3 = ObjectAnimator.ofFloat(mFixView, "translationY", startCheck, endCheck);
        ObjectAnimator object4 = ObjectAnimator.ofFloat(m_multifaceFr, "translationY", startCheck, endCheck);
        ObjectAnimator object5 = ObjectAnimator.ofFloat(m_asetListNew, "translationY", startBottom, endBottom);
        ObjectAnimator object6 = ObjectAnimator.ofFloat(m_replaceListNew, "translationY", startBottom, endBottom);
        animatorSet.playTogether(object1, object3, object4,object5,object6);
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();
    }

    private void TranslateAnimVertical(View view,float startY,float endY)
    {
        if(view != null)
        {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view,"translationY",startY,endY);
            objectAnimator.setDuration(300);
            objectAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    m_uiEnabled = false;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    m_uiEnabled = true;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            objectAnimator.start();
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

    @Override
    public void onBack() {
        if(m_uiEnabled)
        {
            m_onclickListenerNew.onAnimationClick(m_backBtn);
        }
    }

    @Override
    public void onClose() {
        super.onClose();

        clearExitDialog();

        if(mFixView != null)
        {
            mFixView.clearAll();
        }

        if(m_noFaceHelpFr != null)
        {
            m_noFaceHelpFr.dismiss();
            m_noFaceHelpFr = null;
        }

        if(m_waitDlg != null)
        {
            m_waitDlg.dismiss();
            m_waitDlg = null;
        }

        if(m_downloadLst != null)
        {
            DownloadMgr.getInstance().RemoveDownloadListener(m_downloadLst);
        }

        if(m_makeupTypeList != null)
        {

        }

        if(m_thread != null)
        {
            m_thread.quit();
            m_thread = null;
        }

        if(m_recomView != null)
        {
            m_recomView.ClearAllaa();
            m_recomView = null;
        }

        FaceLocalData.ClearData();
        TongJiUtils.onPageEnd(getContext(), R.string.彩妆);
        MyBeautyStat.onPageEndByRes(R.string.美颜美图_彩妆页面_主页面);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(m_isChangePointing)
        {
              TongJiUtils.onPagePause(getContext(),R.string.定点);
        }
        else
        {
            TongJiUtils.onPagePause(getContext(),R.string.彩妆);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(m_isChangePointing)
        {
           TongJiUtils.onPageResume(getContext(),R.string.定点);
        }
        else
        {
            TongJiUtils.onPageResume(getContext(),R.string.彩妆);
        }
    }

    @Override
    public void onPageResult(int siteID, HashMap<String, Object> params) {
        super.onPageResult(siteID, params);
        if(siteID == SiteID.DOWNLOAD_MORE || siteID == SiteID.THEME_INTRO)
        {
            if(params != null)
            {

                if(params.get(DownloadMorePageSite.DOWNLOAD_MORE_DEL) != null)
                {
                    boolean isDelete = (boolean) params.get(DownloadMorePageSite.DOWNLOAD_MORE_DEL);
                    if(isDelete)
                    {
                        refreshAsetList();
                    }
                }

                if(params.get(DataKey.BEAUTIFY_DEF_SEL_URI) != null)
                {
                    int uri = (int) params.get(DataKey.BEAUTIFY_DEF_SEL_URI);
                    if(uri != -1)
                    {
                        //打开相应主题
                        SetSelectByUri(uri);
                    }
                }
            }
        }
        else if (siteID == SiteID.LOGIN || siteID == SiteID.REGISTER_DETAIL || siteID == SiteID.RESETPSW) {
            if (m_recomView != null) {
                m_recomView.UpdateCredit();
            }
        }
    }


    //更新主题彩妆的列表的选中状态
    private void refreshBottomList(final int uri)
    {
            this.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if(m_aSetSelUri != uri)
                    {
                            final int[] is = m_asetAdapter.GetSubIndexByUri(m_aSetDatasNew, uri);
                            if(is[0] >= 0)
                            {
                                m_aSetSelUri = uri;
                                if(m_asetAdapter.AlphaIsShow())
                                {
                                    m_asetAdapter.onCloseAlphaFr();
                                    MakeupPage.this.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            m_asetAdapter.SetSelectByIndex(is[0],is[1]);
                                        }
                                    },400);
                                }
                                else
                                {
                                    m_asetAdapter.SetSelectByIndex(is[0],is[1]);
                                }
                            }
                            else
                            {
                                int delayTime = 0;
                                if(m_asetAdapter.AlphaIsShow())
                                {
                                    delayTime = 400;
                                    m_asetAdapter.onCloseAlphaFr();
                                }
                                MakeupPage.this.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        m_aSetSelUri = 0;
                                        m_asetAdapter.SetSelectNullItem();
                                    }
                                },delayTime);
                            }

                    }
                    else
                    {
                        if(m_aSetSelUri != 0)
                        {
                            if(m_asetAdapter.AlphaIsShow())
                            {
                                m_asetAdapter.onCloseAlphaFr();
                            }
                        }
                    }
                }
            });
    }

    //刷新主题列表的信息
    private void refreshAsetList()
    {
        m_aSetDatasNew = BeautifyResMgr2.GetAsetRes1(getContext());
        m_asetAdapter.SetData(m_aSetDatasNew);
        if(m_aSetSelUri != -1)
        {
            final int[] is = m_asetAdapter.GetSubIndexByUri(m_aSetDatasNew, m_aSetSelUri);
            if(is[0] >= 0)
            {
                m_asetAdapter.SetSelectByIndex(is[0],is[1]);
                m_asetAdapter.notifyDataSetChanged();
            }
            else
            {
                if (m_aSetSelUri == 0 && is[0] == -1 && is[1] == -1) {
                    //推荐位 弹出下载对话框点下载后再次点确定 保持之前的选中状态
                } else {
                    m_asetAdapter.CancelSelect();
                }
                m_asetAdapter.notifyDataSetChanged();
            }
        }
        else
        {
            m_asetAdapter.notifyDataSetChanged();
        }

    }

    private void SetSelectByUri(final int uri)
    {
        this.post(new Runnable()
        {
            @Override
            public void run()
            {
                if(m_aSetSelUri != uri)
                {
                    if(m_asetCB != null && m_asetListNew != null)
                    {
                        int[] is = m_asetAdapter.GetSubIndexByUri(m_aSetDatasNew,uri);
                        if(is[0] >= 0)
                        {
                            m_asetAdapter.SetSelectByIndex(is[0],is[1]);

                            if(m_aSetDatasNew != null && m_aSetDatasNew.size() > is[0])
                            {
                                DeleteNewFlag(m_aSetDatasNew.get(is[0]));
                            }
                            m_asetAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

    //判断是否显示阿玛尼广告
    private int isShouldArManiShowAD()
    {
        int out = -1;
        if(m_faceLocalData.m_makeupDatas_multi != null && m_faceLocalData.m_makeupDatas_multi.length > 0)
        {
            for(int i = 0; i < m_faceLocalData.m_makeupDatas_multi.length; i++)
            {
                if(m_faceLocalData.m_makeupDatas_multi[i] != null && m_faceLocalData.m_makeupDatas_multi[i].size() > 0)
                {
                    for(int j = 0; j < m_faceLocalData.m_makeupDatas_multi[i].size();j++)
                    {
                        MakeupRes.MakeupData temp = m_faceLocalData.m_makeupDatas_multi[i].get(j);
                        if((temp.m_makeupType == MakeupType.LIP.GetValue()) && MakeupADUtil.isShouldShowArManiAD(temp.m_id))
                        {
                            out = temp.m_id;
                            break;
                        }
                    }
                }
            }
        }
        return out;
    }

    private ADShowFr m_adFr;//显示广告弹框的布局
    //显示广告
    private void ShowAD(int uri)
    {
        if(m_adFr == null)
        {
            ADShowFr.ADShowLayoutData data = new ADShowFr.ADShowLayoutData();
            data.m_imgShowWidth = ShareData.PxToDpi_xhdpi(550);
            data.m_imgShowHeight = ShareData.PxToDpi_xhdpi(360);
            data.m_gravity = Gravity.CENTER;
            data.m_closeBtnHeight = ShareData.PxToDpi_xhdpi(76);
            data.m_closeBtnWidth = ShareData.PxToDpi_xhdpi(76);
            data.m_closeBtnRightMagin = ShareData.PxToDpi_xhdpi(35);
            data.m_closeBtnTopMagin = ShareData.PxToDpi_xhdpi(1);
            m_adFr = new ADShowFr(getContext(), data, new ADShowFr.ADFrOnClickCB() {
                @Override
                public void OnClick(int flag) {
                    if(flag == ADShowFr.ADCLICKCLOSE)
                    {
                        CloseAD();
                    }
                }
            });
            LayoutParams fl = new LayoutParams(m_frW,m_finalFrH);
            m_adFr.setLayoutParams(fl);
            this.addView(m_adFr);
        }

        m_adFr.setImageRes(MakeupADUtil.getArManiResByUriAndTime(uri));
        m_multiFaceUiEnable = false;
        setEnableForList(false);
    }

    //设置主题和局部列表区域是否可点击
    private void setEnableForList(boolean isEnable)
    {
        if(m_asetListNew != null)
        {
            m_asetListNew.setUIEnable(isEnable);
        }

        if(m_replaceListNew != null)
        {
            m_replaceListNew.setUIEnable(isEnable);
        }
    }

    private void CloseAD()
    {
        if(m_adFr != null)
        {
            m_adFr.setVisibility(GONE);
            this.removeView(m_adFr);
            m_adFr = null;
        }
        m_multiFaceUiEnable = true;
        setEnableForList(true);
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

}
