package cn.poco.noseAndtooth.abs;


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

import java.util.HashMap;

import cn.poco.advanced.ImageUtils;
import cn.poco.beautify.SonWindow;
import cn.poco.beautify4.Beautify4Page;
import cn.poco.camera3.ui.FixPointView;
import cn.poco.cloudalbumlibs.utils.CloudAlbumDialog;
import cn.poco.face.FaceDataV2;
import cn.poco.filterPendant.MyStatusButton;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.home.home4.utils.PercentUtil;
import cn.poco.image.filter;
import cn.poco.makeup.MakeupUIHelper;
import cn.poco.makeup.MySeekBar;
import cn.poco.makeup.SeekBarTipsView;
import cn.poco.statistics.TongJiUtils;
import cn.poco.tianutils.FullScreenDlg;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.CommonUI;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.utils.WaitAnimDialog;
import cn.poco.view.beauty.BeautyCommonViewEx;
import cn.poco.view.beauty.MakeUpViewEx;
import cn.poco.view.beauty.MakeUpViewEx1;
import my.beautyCamera.R;

import static cn.poco.face.FaceDataV2.RAW_POS_MULTI;


public abstract class AbsNoseAndToothPage extends IPage implements INATPage{
    protected MakeUpViewEx1 m_view;
    protected LinearLayout m_bottomFr;
    protected FrameLayout m_bottomBar;
    protected FrameLayout m_bottomList;
    protected MySeekBar m_seekBar;
    protected ImageView m_compareBtn;
//    protected ImageView mFixView;
    private FixPointView mFixView;
    private ImageView m_backBtn;
    private ImageView m_okBtn;
    private MyStatusButton m_titleBtn;
    private MakeupUIHelper.ChangePointFr m_changepointFr;
    private int m_frW;
    private int m_frH;
    private int m_finalFrH;
    private int m_bottomBarHeight;
    private int m_bottomListHeight;
    private boolean m_multiFaceUiEnable = true;
    private boolean m_uiEnabled = true;
    private WaitAnimDialog m_waitDlg;
    private boolean m_isChangePointing;
    private boolean m_posModify;
    private boolean m_isHasEffect;
    private boolean m_ischangePointAnimResetStart;
    private int m_imgH;
    private int m_viewH;
    private ImageView m_multifaceBtn;
    protected TextView m_multifaceTips;
    protected SeekBarTipsView m_seekBarTips;
    private FullScreenDlg m_noFaceHelpFr;
    private boolean isFold = false;
    private SonWindow m_sonWin;
    private INATPresenter m_flow;

    public boolean mChange = false;
    public CloudAlbumDialog mExitDialog;

    public AbsNoseAndToothPage(Context context, BaseSite site) {
        super(context, site);
        initData();
        m_flow = getPresenter(context,site);
        initUI();
    }

    private void initData()
    {
        m_frW = ShareData.m_screenWidth;
        m_frW -= m_frW % 2;
        m_frH = ShareData.m_screenHeight - ShareData.PxToDpi_xhdpi(320);
        m_frH -= m_frH % 2;
        m_bottomBarHeight = ShareData.PxToDpi_xhdpi(88);
        m_bottomListHeight = ShareData.PxToDpi_xhdpi(232);
    }

    @Override
    public void finishFaceCheck() {
        if(RAW_POS_MULTI != null)
        {
            if(!FaceDataV2.CHECK_FACE_SUCCESS)
            {
                MakeNoFaceHelp();
                if(m_noFaceHelpFr != null)
                {
                    m_noFaceHelpFr.show();
                }
            }
            else
            {
                if(RAW_POS_MULTI.length > 1)
                {
                    m_multiFaceUiEnable = false;
                    setScaleAnim(mFixView, false);
                    showSelectFace();
                }
                else
                {
                    m_view.m_faceIndex = 0;
                    m_multiFaceUiEnable = true;
                    m_flow.sendEffectMsg();
                }

                if(mFixView != null)
                {
                    mFixView.showJitterAnimAccordingStatus();
                }
            }
        }
        m_uiEnabled = true;
        SetWaitUI(false,"");
    }

    @Override
    public void updateBmp(Bitmap bmp) {
        m_isHasEffect = true;
        if(!m_isChangePointing)
        {
            if (m_flow != null && m_flow.getCurProgress() == 0) {
                // progress == 0 不显示
            } else {
                setScaleAnim(m_compareBtn,false);
            }
        }
        m_view.setImage(bmp);
        m_uiEnabled = true;
        if (m_flow != null && m_flow.getCurProgress() == 0) {
            mChange = false;
        } else {
            mChange = true;
        }
        SetWaitUI(false,"");
    }

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
                        m_isChangePointing = true;
                        FaceDataV2.sFaceIndex = 0;
                        m_view.m_faceIndex = 0;
                        m_view.copyFaceData();
                        m_view.Data2UI();
                        m_view.setMode(BeautyCommonViewEx.MODE_FACE);
                        m_view.m_showPosFlag = MakeUpViewEx1.POS_THREE;
                        m_view.m_touchPosFlag = m_view.m_showPosFlag;
                        m_view.DoFixedPointAnim();
                        setChangepointUI(true);
                        m_changepointFr.setUIFlag(MakeupUIHelper.ChangePointFr.CHECK_TRHEE);
                    }
                }
            });
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
            endView = ((float) (m_bottomListHeight)) / 2f;

            startCheck = 0f;
            endCheck = endView;
        }
        else
        {
            startBottom = (float) (m_bottomListHeight);
            endBottom = 0f;

            startView = ((float) (m_bottomListHeight)) / 2f;
            endView = 0f;

            startCheck = startView;
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
                        m_view.InitAnimDate(m_frW,m_finalFrH,m_frW,m_finalFrH + (m_bottomListHeight ));
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
        ObjectAnimator object3 = ObjectAnimator.ofFloat(mFixView, "translationY", startCheck, endCheck);
        ObjectAnimator object5 = ObjectAnimator.ofFloat(m_bottomFr, "translationY", startBottom, endBottom);
        animatorSet.playTogether(object1, object3,object5);
        if(FaceDataV2.RAW_POS_MULTI != null && FaceDataV2.RAW_POS_MULTI.length > 1)
        {
            ObjectAnimator object4 = ObjectAnimator.ofFloat(m_multifaceBtn, "translationY", startCheck, endCheck);
            animatorSet.playTogether(object4);
        }
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();
    }

    private void showSelectFace()
    {
        setScaleAnim(m_multifaceBtn, true);
        setScaleAnim(mFixView, true);
        setScaleAnim(m_compareBtn, true);
        m_multifaceTips.setVisibility(VISIBLE);
        m_bottomFr.setVisibility(GONE);
        m_view.setMode(BeautyCommonViewEx.MODE_SEL_FACE);
    }

    private void initUI()
    {
        m_view = new MakeUpViewEx1(getContext(),m_viewCB);
        m_view.def_fix_face_res = new int[]{R.drawable.beautify_fix_face_eye, R.drawable.beautify_fix_face_eye, R.drawable.beautify_fix_face_mouth};
        m_view.def_fix_eyebrow_res = new int[]{R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point};
        m_view.def_fix_eye_res = new int[]{R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_face_eye};
        m_view.def_fix_cheek_res = new int[]{R.drawable.beautify_fix_point, R.drawable.beautify_fix_point};
        m_view.def_fix_lip_res = new int[]{R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point, R.drawable.beautify_fix_point};
        m_view.def_stroke_width = ShareData.PxToDpi_xhdpi(2);
        if(m_view.def_stroke_width < 1)
        {
            m_view.def_stroke_width = 1;
        }
        m_view.InitFaceRes();
        m_view.m_faceIndex = FaceDataV2.sFaceIndex;

        m_finalFrH = m_frH;
        if((ShareData.m_screenHeight - m_bottomBarHeight - m_bottomListHeight) < m_frH)
        {
            m_finalFrH = (ShareData.m_screenHeight - m_bottomBarHeight - m_bottomListHeight);
        }
        LayoutParams fl = new LayoutParams(m_frW, m_finalFrH);
        fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        m_view.setLayoutParams(fl);
        this.addView(m_view, 0);

        {
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
                m_backBtn.setOnTouchListener(m_clickListener);
                m_bottomBar.addView(m_backBtn);

                m_okBtn = new ImageView(getContext());
                m_okBtn.setImageResource(R.drawable.beautify_ok);
                fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                fl.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                fl.rightMargin = ShareData.PxToDpi_xhdpi(24);
                m_okBtn.setLayoutParams(fl);
                m_okBtn.setOnTouchListener(m_clickListener);
                m_bottomBar.addView(m_okBtn);
                ImageUtils.AddSkin(getContext(),m_okBtn);

                m_titleBtn = new MyStatusButton(getContext());
                m_titleBtn.setData(m_flow.getIconRes(),m_flow.getTitle());
                m_titleBtn.setBtnStatus(true,false);
                fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                fl.gravity = Gravity.CENTER;
                m_titleBtn.setLayoutParams(fl);
                m_bottomBar.addView(m_titleBtn);
                m_titleBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(m_uiEnabled)
                        {
                            if(v == m_titleBtn && m_multiFaceUiEnable)
                            {
                                isFold = !isFold;
                                showBottomViewAnim(isFold);
                                m_flow.onTitleBtn(isFold);
                            }
                        }
                    }
                });
            }

            m_bottomList = new FrameLayout(getContext())
            {
                @Override
                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    if(!m_multiFaceUiEnable || !m_uiEnabled)
                    {
                        return true;
                    }
                    return super.onInterceptTouchEvent(ev);
                }
            };
            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,m_bottomListHeight);
            m_bottomList.setLayoutParams(ll);
            m_bottomFr.addView(m_bottomList);
            {
                m_seekBar = new MySeekBar(getContext());
                fl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
                fl.leftMargin = PercentUtil.WidthPxToPercent(80);
                fl.rightMargin = PercentUtil.WidthPxToPercent(80);
                m_seekBar.setLayoutParams(fl);
                m_seekBar.setBackgroundColor(0x57000000);
                m_seekBar.setOnProgressChangeListener(m_onProgressChangeListener);
                m_bottomList.addView(m_seekBar);
                m_seekBar.setProgress(m_flow.getInitProgress());
            }
        }

        mFixView = new FixPointView(getContext());
//        mFixView.setPadding(ShareData.PxToDpi_xhdpi(15),ShareData.PxToDpi_xhdpi(15),ShareData.PxToDpi_xhdpi(15),ShareData.PxToDpi_xhdpi(15));
        fl = new LayoutParams(ShareData.PxToDpi_xhdpi(80), ShareData.PxToDpi_xhdpi(80));
        fl.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        fl.rightMargin = ShareData.PxToDpi_xhdpi(24);
        fl.bottomMargin = m_bottomListHeight + m_bottomBarHeight + ShareData.PxToDpi_xhdpi(24);
        mFixView.setLayoutParams(fl);
        this.addView(mFixView);
//        mFixView.setBackgroundDrawable(new BitmapDrawable(BitmapFactory.decodeResource(getResources(),R.drawable.beautify_white_circle_bg)));
//        mFixView.setImageBitmap(ImageUtils.AddSkin(getContext(),BitmapFactory.decodeResource(getResources(),R.drawable.beautify_fix_by_hand)));
//        ImageUtils.AddSkin(getContext(),mFixView);
        mFixView.setOnTouchListener(m_clickListener);
        mFixView.setVisibility(GONE);

        m_multifaceBtn = new ImageView(getContext());
        m_multifaceBtn.setPadding(ShareData.PxToDpi_xhdpi(15),ShareData.PxToDpi_xhdpi(15),ShareData.PxToDpi_xhdpi(15),ShareData.PxToDpi_xhdpi(15));
        fl = new LayoutParams(ShareData.PxToDpi_xhdpi(80),ShareData.PxToDpi_xhdpi(80));
        fl.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        fl.rightMargin = ShareData.PxToDpi_xhdpi(120);
        fl.bottomMargin = m_bottomListHeight + m_bottomBarHeight + ShareData.PxToDpi_xhdpi(24);
        m_multifaceBtn.setLayoutParams(fl);
        m_multifaceBtn.setImageResource(R.drawable.beautify_makeup_multiface_icon);
        m_multifaceBtn.setImageBitmap(ImageUtils.AddSkin(getContext(),BitmapFactory.decodeResource(getResources(),R.drawable.beautify_makeup_multiface_icon)));
//        ImageUtils.AddSkin(getContext(),m_multifaceBtn);
        m_multifaceBtn.setBackgroundDrawable(new BitmapDrawable(BitmapFactory.decodeResource(getResources(),R.drawable.beautify_white_circle_bg)));
        m_multifaceBtn.setVisibility(GONE);
        m_multifaceBtn.setOnTouchListener(m_clickListener);
        this.addView(m_multifaceBtn);

        //多人脸选择提示语
        m_multifaceTips = new TextView(getContext());
        fl = new LayoutParams(ShareData.PxToDpi_xhdpi(560),ShareData.PxToDpi_xhdpi(80));
        fl.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        fl.topMargin = m_frH - ShareData.PxToDpi_xhdpi(80) - ShareData.PxToDpi_xhdpi(17);
        m_multifaceTips.setLayoutParams(fl);
        m_multifaceTips.setVisibility(GONE);
        this.addView(m_multifaceTips);
        m_multifaceTips.setBackgroundResource(R.drawable.beautify_makeup_multiface_tips_bk);
        m_multifaceTips.setText(R.string.makeup_multiface_tips);
        m_multifaceTips.setGravity(Gravity.CENTER);
        m_multifaceTips.setTextColor(Color.BLACK);

        m_seekBarTips = new SeekBarTipsView(getContext());
        fl = new LayoutParams(ShareData.PxToDpi_xhdpi(120),ShareData.PxToDpi_xhdpi(120));
        fl.gravity = Gravity.BOTTOM | Gravity.LEFT;
        m_seekBarTips.setLayoutParams(fl);
        this.addView(m_seekBarTips);
        m_seekBarTips.setVisibility(GONE);

        m_compareBtn = new AppCompatImageView(getContext())
        {
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                    {
                        m_flow.onCompareBtn();
                        m_view.setImage(m_flow.getOrgBmp());
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    {
                        if(m_flow.getCurBmp() != null && !m_flow.getCurBmp().isRecycled())
                        {
                            m_view.setImage(m_flow.getCurBmp());
                        }
                        break;
                    }
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


        m_changepointFr = MakeupUIHelper.showChangePointFr(this,m_changepointCB,
                MakeupUIHelper.ChangePointFr.CHECK_INDEX_EYEBROW,
                MakeupUIHelper.ChangePointFr.CHECK_INDEX_EYE_L,
                MakeupUIHelper.ChangePointFr.CHECK_INDEX_NOSE,
                MakeupUIHelper.ChangePointFr.CHECK_INDEX_LIP,
                MakeupUIHelper.ChangePointFr.CHECK_INDEX_CHEEK);
        m_changepointFr.setVisibility(GONE);

        m_sonWin = new SonWindow(getContext());
        fl = new LayoutParams(LayoutParams.MATCH_PARENT, ShareData.m_screenWidth/3);
        fl.gravity = Gravity.TOP | Gravity.LEFT;
        m_sonWin.setLayoutParams(fl);
        this.addView(m_sonWin);
        m_waitDlg = new WaitAnimDialog((Activity)getContext());
    }

    private void RelayoutSeekBarTipsPos(MySeekBar mySeekBar,int progress,int maxProgress)
    {
        LayoutParams temp = (LayoutParams) m_seekBarTips.getLayoutParams();
        temp.bottomMargin = m_bottomListHeight - ShareData.PxToDpi_xhdpi(20);
        int[] dst = new int[2];
        mySeekBar.getLocationOnScreen(dst);
        temp.leftMargin = (int) ((int) (dst[0] + mySeekBar.getCurCiclePos()) - m_seekBarTips.getWidth()/2f);
        m_seekBarTips.setText("" + progress);
        m_seekBarTips.setLayoutParams(temp);
    }

    MakeupUIHelper.ChangePointFr.ChangePointFrCallBack m_changepointCB = new MakeupUIHelper.ChangePointFr.ChangePointFrCallBack() {
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
            if(m_uiEnabled && m_changepointFr != null)
            {
                if(v == m_changepointFr.m_checkBackBtn)
                {
                    m_isChangePointing = false;
                    m_view.m_showPosFlag = 0;
                    m_view.m_touchPosFlag = m_view.m_showPosFlag;
                    m_view.ResetAnim();
                    setChangepointUI(false);
                    m_view.reSetFaceData();
                    if(m_posModify)
                    {
                        m_flow.sendEffectMsg();
                        m_posModify = false;
                    }
                    m_view.setMode(BeautyCommonViewEx.MODE_NORMAL);
                    m_ischangePointAnimResetStart = true;
                }
                else if(v == m_changepointFr.m_checkThreeBackBtn)
                {
                    FaceDataV2.CHECK_FACE_SUCCESS = true;
                    m_isChangePointing = false;
                    m_changepointCB.onClick(m_changepointFr.m_checkBackBtn);
                }
                else if(v == m_changepointFr.m_changePointOkBtn)
                {
                    if(!FaceDataV2.CHECK_FACE_SUCCESS || !FaceDataV2.sIsFix)
                    {
                        if(m_posModify)
                        {
                            FaceDataV2.sIsFix = true;
                            float[] faceAll = RAW_POS_MULTI[m_view.m_faceIndex].getFaceFeaturesMakeUp();
                            float[] faceData = RAW_POS_MULTI[m_view.m_faceIndex].getFaceRect();
                            filter.reFixPtsCosmetic(faceData, faceAll, m_flow.getOrgBmp());
                            RAW_POS_MULTI[m_view.m_faceIndex].setFaceRect(faceData);
                            RAW_POS_MULTI[m_view.m_faceIndex].setMakeUpFeatures(faceAll);
                        }
                    }

                    FaceDataV2.CHECK_FACE_SUCCESS = true;
                    m_isChangePointing = false;
                    m_view.m_showPosFlag = 0;
                    m_view.m_touchPosFlag = m_view.m_showPosFlag;
                    if(m_flow.getOrgBmp() != null)
                    {
                        FaceDataV2.Raw2Ripe(m_flow.getOrgBmp().getWidth(),m_flow.getOrgBmp().getHeight());
                    }
                    m_view.ResetAnim();
                    setChangepointUI(false);
                    m_posModify = false;
                    m_view.setMode(BeautyCommonViewEx.MODE_NORMAL);
                    m_ischangePointAnimResetStart = true;
                }
                else if(v == m_changepointFr.m_checkThreeOkBtn)
                {
                    TongJiUtils.onPageEnd(getContext(),R.string.定点);
                    if(!FaceDataV2.CHECK_FACE_SUCCESS || !FaceDataV2.sIsFix)
                    {
                        if(m_posModify)
                        {
                            FaceDataV2.sIsFix = true;
                            float[] faceAll = RAW_POS_MULTI[m_view.m_faceIndex].getFaceFeaturesMakeUp();
                            float[] faceData = RAW_POS_MULTI[m_view.m_faceIndex].getFaceRect();
                            filter.reFixPtsBShapes(getContext(), m_view.m_faceIndex, faceData, faceAll, m_flow.getOrgBmp());
                            RAW_POS_MULTI[m_view.m_faceIndex].setFaceRect(faceData);
                            RAW_POS_MULTI[m_view.m_faceIndex].setMakeUpFeatures(faceAll);
                        }
                    }
                    FaceDataV2.CHECK_FACE_SUCCESS = true;
                    m_isChangePointing = false;
                    m_view.m_showPosFlag = 0;
                    m_view.m_touchPosFlag = m_view.m_showPosFlag;
                    if(m_flow.getOrgBmp() != null)
                    {
                        FaceDataV2.Raw2Ripe(m_flow.getOrgBmp().getWidth(),m_flow.getOrgBmp().getHeight());
                    }
                    m_view.Data2UI();
                    m_view.ResetAnim();
                    m_view.invalidate();
                    setChangepointUI(false);

                    m_posModify = false;
                    m_view.setMode(BeautyCommonViewEx.MODE_NORMAL);
                    m_ischangePointAnimResetStart = true;
                }
            }
        }
    };

    private void setChangepointUI(boolean isChangePoint) {
        if (isChangePoint)
        {
            m_changepointFr.setVisibility(VISIBLE);
            m_bottomFr.setVisibility(GONE);
            setScaleAnim(m_multifaceBtn, true);
            setScaleAnim(mFixView, true);
            setScaleAnim(m_compareBtn, true);
        }
        else
        {
            m_bottomFr.setVisibility(VISIBLE);
            m_changepointFr.setVisibility(GONE);
        }
    }

    @Override
    public void SetData(HashMap<String, Object> params) {
        if(params != null)
        {
            if(params.get(Beautify4Page.PAGE_ANIM_IMG_H) != null)
            {
                m_imgH = (int) params.get(Beautify4Page.PAGE_ANIM_IMG_H);
            }

            if(params.get(Beautify4Page.PAGE_ANIM_VIEW_H) != null)
            {
                m_viewH = (int) params.get(Beautify4Page.PAGE_ANIM_VIEW_H);
            }

            if(params.get("imgs") != null)
            {
                m_flow.setImage(params.get("imgs"));
            }
            ShowStartAnim();
            faceCheck();
        }
    }

    private void faceCheck()
    {
        if(FaceDataV2.sFaceIndex == -1 || FaceDataV2.FACE_POS_MULTI == null)
        {
            m_flow.faceCheck();
        }
        else
        {
            if(mFixView != null)
            {
                mFixView.showJitterAnimAccordingStatus();
            }
            m_flow.sendEffectMsg();
        }
    }

    private void ShowStartAnim()
    {
        if(m_viewH > 0 && m_imgH > 0)
        {
            int tempStartY = (int) (ShareData.PxToDpi_xhdpi(90) + (m_viewH - m_frH)/2f);
            float scale1 = (float)m_frW / (float)m_flow.getOrgBmp().getWidth();
            float scale2 = (float)m_frH / (float)m_flow.getOrgBmp().getHeight();
            float tempImgH =  m_flow.getOrgBmp().getHeight() * ((scale1 > scale2) ? scale2 : scale1);
            float scale = m_imgH/tempImgH;
            ShowViewAnim(m_view,tempStartY,0,scale,1f,m_frW,m_frH);
        }
        else
        {
            if(mFixView != null)
            {
                setScaleAnim(mFixView, false);
            }
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
            animatorSet.setDuration(300);
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSet.playTogether(object1, object2, object3, object4);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (view != null) {
                        view.clearAnimation();
                    }
                    if(mFixView != null && m_multiFaceUiEnable)
                    {
                        setScaleAnim(mFixView, false);
                    }

                    if(m_multifaceBtn != null && FaceDataV2.sFaceIndex >=0 && RAW_POS_MULTI != null && RAW_POS_MULTI.length > 1)
                    {
                        setScaleAnim(m_multifaceBtn, false);
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

    MySeekBar.OnProgressChangeListener m_onProgressChangeListener = new MySeekBar.OnProgressChangeListener() {
        @Override
        public void onProgressChanged(MySeekBar seekBar, int progress) {
            if (progress == 0)  {
                if (m_compareBtn.getVisibility() != View.GONE) {
                    m_compareBtn.setVisibility(GONE);
                }
            } else {
                if (m_compareBtn.getVisibility() != View.VISIBLE) {
                    m_compareBtn.setVisibility(VISIBLE);
                }
            }

            RelayoutSeekBarTipsPos(seekBar,progress,100);
        }

        @Override
        public void onStartTrackingTouch(MySeekBar seekBar) {
            m_seekBarTips.setVisibility(VISIBLE);
            RelayoutSeekBarTipsPos(seekBar,seekBar.getProgress(),100);
        }

        @Override
        public void onStopTrackingTouch(MySeekBar seekBar) {
            mChange = true;
            m_seekBarTips.setVisibility(GONE);
            m_flow.setProgress(seekBar.getProgress());
            m_flow.sendEffectMsg();
        }
    };


    protected OnAnimationClickListener m_clickListener = new OnAnimationClickListener() {
        @Override
        public void onAnimationClick(View v) {
            if(m_uiEnabled)
            {
                if(v == m_backBtn)
                {
                    m_flow.back();
                }
                else if(v == m_okBtn)
                {
                    m_flow.ok();
                }
                else if(v == mFixView)
                {
                    mFixView.modifyStatus();
                    m_isChangePointing = true;
                    m_posModify = false;
                    m_view.copyFaceData();
                    setChangepointUI(true);
                    onCheckBtnOnclick(m_changepointFr);
                }
                else if(v == m_multifaceBtn)
                {
                    m_multiFaceUiEnable = false;
                    showSelectFace();
                    m_view.Restore();
                }
            }
        }

        @Override
        public void onTouch(View v) {

        }

        @Override
        public void onRelease(View v) {

        }
    };

    public abstract void onCheckBtnOnclick(MakeupUIHelper.ChangePointFr changePointFr);

    MakeUpViewEx1.ControlCallback m_viewCB = new MakeUpViewEx.ControlCallback() {
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
                m_sonWin.SetData(bitmap,(int)x,(int)y);
            }
        }

        @Override
        public void On3PosModify() {
            m_posModify = true;
            if(m_isHasEffect)
            {
                m_flow.sendEffectMsg();
            }
        }

        @Override
        public void OnAllPosModify() {
            m_posModify = true;
            m_flow.sendEffectMsg();
        }

        @Override
        public void onTouchWatermark() {

        }

        @Override
        public void onFingerUp() {

        }

        @Override
        public void OnSelFaceIndex(int index) {
            if(m_view != null)
            {
                m_uiEnabled = true;
                m_multiFaceUiEnable = true;
                m_view.m_faceIndex = index;
                FaceDataV2.sFaceIndex = index;
                m_view.setMode(BeautyCommonViewEx.MODE_NORMAL);
                m_view.DoSelFaceAnim();
                setScaleAnim(m_multifaceBtn, false);
                setScaleAnim(mFixView, false);
                if(m_isHasEffect)
                {
                    setScaleAnim(m_compareBtn,false);
                }
                m_multifaceTips.setVisibility(GONE);
                m_bottomFr.setVisibility(VISIBLE);
            }

            setSeekBarValue();

            if(!m_isHasEffect)
            {
                m_uiEnabled = false;
                AbsNoseAndToothPage.this.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        m_flow.sendEffectMsg();
                    }
                },400);
            }
        }

        @Override
        public void OnAnimFinish() {
            if(m_ischangePointAnimResetStart)
            {
                if(m_isHasEffect)
                {
                    setScaleAnim(m_compareBtn,false);
                }
                if(FaceDataV2.RAW_POS_MULTI != null && FaceDataV2.RAW_POS_MULTI.length > 1)
                {
                    setScaleAnim(m_multifaceBtn,false);
                }
                setScaleAnim(mFixView,false);
                m_ischangePointAnimResetStart = false;

                if(!m_isHasEffect)
                {
                    m_flow.sendEffectMsg();
                }
            }
        }
    };

    private void setSeekBarValue()
    {
        m_seekBar.setProgress(m_flow.getCurProgress());
    }


    @Override
    public void onBack() {
        m_clickListener.onAnimationClick(m_backBtn);
    }

    @Override
    public void setViewImage(Bitmap bmp) {
        m_view.setImage(bmp);
    }

    @Override
    public void showWaitUI() {
        SetWaitUI(true,"");
    }

    @Override
    public HashMap<String, Object> getBackAnimParams() {
            HashMap<String, Object> params = new HashMap<>();
            params.put(Beautify4Page.PAGE_BACK_ANIM_VIEW_TOP_MARGIN, (m_view.getHeight() - m_finalFrH)/2f);
            params.put(Beautify4Page.PAGE_BACK_ANIM_IMG_H, m_view.getImgHeight());
        return params;
    }

    @Override
    public void onClose() {
        if(m_waitDlg != null)
        {
            m_waitDlg.dismiss();
            m_waitDlg = null;
        }
        m_flow.Clear();
        clearExitDialog();
    }

    @Override
    public boolean isChanged()
    {
        return mChange;
    }

    @Override
    public void showExitDialog()
    {
        if (mExitDialog == null) {
            mExitDialog = new CloudAlbumDialog(getContext(),
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            ImageUtils.AddSkin(getContext(), mExitDialog.getOkButtonBg());
            mExitDialog.setCancelButtonText(R.string.cancel)
                    .setOkButtonText(R.string.ensure)
                    .setMessage(R.string.confirm_back)
                    .setListener(new CloudAlbumDialog.OnButtonClickListener() {
                        @Override
                        public void onOkButtonClick() {
                            if (mExitDialog != null) {
                                mExitDialog.dismiss();
                            }
                            if (m_flow != null) {
                                m_flow.onExit();
                            }
                        }

                        @Override
                        public void onCancelButtonClick() {
                            if (mExitDialog != null) {
                                mExitDialog.dismiss();
                            }
                        }
                    });
        }
        mExitDialog.show();
    }

    protected void clearExitDialog()
    {
        if (mExitDialog != null)
        {
            mExitDialog.dismiss();
            mExitDialog.setListener(null);
            mExitDialog = null;
        }
    }

    public abstract INATPresenter getPresenter(Context context, BaseSite site);
}
