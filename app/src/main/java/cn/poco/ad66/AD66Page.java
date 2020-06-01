package cn.poco.ad66;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.AppCompatImageView;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.HashMap;

import cn.poco.ad.abs.ADAbsBottomFrWithRY;
import cn.poco.ad66.imp.IAD66UI;
import cn.poco.ad66.site.AD66PageSite;
import cn.poco.advanced.ImageUtils;
import cn.poco.beautify.SonWindow;
import cn.poco.face.FaceDataV2;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.image.filter;
import cn.poco.makeup.ChangePointPage;
import cn.poco.makeup.MakeupUIHelper;
import cn.poco.makeup.MySeekBar;
import cn.poco.makeup.SeekBarTipsView;
import cn.poco.tianutils.FullScreenDlg;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.CommonUI;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.utils.Utils;
import cn.poco.utils.WaitAnimDialog;
import cn.poco.view.beauty.BeautyCommonViewEx;
import cn.poco.view.beauty.MakeUpViewEx1;
import my.beautyCamera.R;

import static cn.poco.face.FaceDataV2.RAW_POS_MULTI;

//兰蔻
public class AD66Page extends IPage implements IAD66UI {

    public static final int FILTER_MODE = 1;
    public static final int COLOR_MODE = 2;
    public int m_curMode = FILTER_MODE;
    private AD66Presenter m_presenter;
    private MakeUpViewEx1 m_view;
    private MakeupUIHelper.ChangePointFr m_changepointFr;
    private ImageView m_compareBtn;//对比按钮
    private FrameLayout m_checkBtnFr;
    private ImageView m_checkBtn;//定点按钮
    protected WaitAnimDialog m_waitDlg;
    private MySeekBar m_seekBar;
    private SeekBarTipsView m_seekBarTips;
    private AD66BottomFr m_bottomFr1;
    private AD66BottomFr2 m_bottomFr2;
    private boolean m_posModify;
    private boolean m_isBackForChangePoint;//用于判断动画是否是因为从定点ui返回到正常ui要做的恢复动画
    private SonWindow m_sonWin;
    private int m_frW;
    private int m_frH;
    private ImageView m_tipsImg;//提示的图片
    private boolean m_isHasEffect = false;
    private int m_bottomBarHeight;
    private boolean m_uiEnabled = true;
    private FullScreenDlg m_noFaceHelpFr;
    private boolean m_adTipsShowOnce = false;
    private boolean m_IsinitColorAlpha = false;
    public AD66Page(Context context, BaseSite site) {
        super(context, site);
        m_presenter = new AD66Presenter(context, (AD66PageSite) site,this);
        initData();
        initUI();
    }

    @Override
    public void SetData(HashMap<String, Object> params) {
        if(params != null)
        {
            if (params.get("imgs") != null) {
                m_presenter.setImage(params.get("imgs"));
            } else {
                RuntimeException ex = new RuntimeException("MyLog--Input params is null!");
                throw ex;
            }
        }

         if(!m_adTipsShowOnce)
        {
            m_adTipsShowOnce = true;
            showTipsFr();
        }
    }

    private void initData()
    {
        m_bottomBarHeight = ShareData.PxToDpi_xhdpi(88);
        m_frW = ShareData.m_screenWidth;
        m_frH = (int) (m_frW*4/3f);
    }


    private void initUI()
    {
        m_view = new MakeUpViewEx1(getContext(),m_cb);
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
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(m_frW,m_frH);
        fl.gravity = Gravity.TOP;
        m_view.setLayoutParams(fl);
        this.addView(m_view);

        m_changepointFr = MakeupUIHelper.showChangePointFr(AD66Page.this,m_changepointCB,
                MakeupUIHelper.ChangePointFr.CHECK_INDEX_NOSE);
        m_changepointFr.setVisibility(GONE);

        m_bottomFr1 = new AD66BottomFr(getContext());
        fl = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        m_bottomFr1.setLayoutParams(fl);
        this.addView(m_bottomFr1);
        m_bottomFr1.setClickCallBack(m_btcb);
        m_bottomFr1.setItemInfos(m_presenter.getItemInfos1());
//        m_bottomFr1.SetselectIndex(m_presenter.getCurSelectIndex1());


        m_bottomFr2 = new AD66BottomFr2(getContext());
        fl = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        m_bottomFr2.setLayoutParams(fl);
        this.addView(m_bottomFr2);
        m_bottomFr2.setClickCallBack(m_btcb);
        m_bottomFr2.setVisibility(GONE);
        m_bottomFr2.setItemInfos(m_presenter.getItemInfos2());
//        m_bottomFr2.SetselectIndex(m_presenter.getCurSelectIndex2());

        m_seekBar = new MySeekBar(getContext());
        fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(480), ShareData.PxToDpi_xhdpi(50));
        fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        fl.bottomMargin = ShareData.PxToDpi_xhdpi(232) + m_bottomBarHeight + ShareData.PxToDpi_xhdpi(18);
        m_seekBar.setLayoutParams(fl);
        m_seekBar.setBackgroundColor(0x57000000);
        m_seekBar.setOnProgressChangeListener(m_onProgressChangeListener);
        this.addView(m_seekBar);
        m_seekBar.setProgress(m_presenter.getProgressValue());
        m_seekBar.setVisibility(GONE);

        m_compareBtn = new AppCompatImageView(getContext())
        {
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                    {
                        m_view.setImage(m_presenter.getOrgBmp());
                    }
                    break;
                    case MotionEvent.ACTION_UP:
                        if(m_curMode == COLOR_MODE)
                        {
                             if(m_presenter.getCurBmp() != null && !m_presenter.getCurBmp().isRecycled())
                              {
                                    m_view.setImage(m_presenter.getCurBmp());
                              }
                        }
                        else if(m_curMode == FILTER_MODE)
                        {
                            if(m_presenter.getFilterBmp() != null && !m_presenter.getFilterBmp().isRecycled())
                            {
                                m_view.setImage(m_presenter.getFilterBmp());
                            }
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


        m_checkBtnFr = new FrameLayout(getContext());
        fl = new LayoutParams(ShareData.PxToDpi_xhdpi(80), ShareData.PxToDpi_xhdpi(80));
        fl.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        fl.rightMargin = ShareData.PxToDpi_xhdpi(24);
        fl.bottomMargin = ShareData.PxToDpi_xhdpi(232) + m_bottomBarHeight + ShareData.PxToDpi_xhdpi(24);
        m_checkBtnFr.setLayoutParams(fl);
        this.addView(m_checkBtnFr);
        m_checkBtnFr.setBackgroundDrawable(new BitmapDrawable(BitmapFactory.decodeResource(getResources(),R.drawable.beautify_white_circle_bg)));
        m_checkBtnFr.setOnTouchListener(m_animClickListener);
        m_checkBtnFr.setVisibility(GONE);

        m_checkBtn = new ImageView(getContext());
        fl = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.CENTER;
        m_checkBtn.setLayoutParams(fl);
        m_checkBtnFr.addView(m_checkBtn);
        m_checkBtn.setImageResource(R.drawable.beautify_fix_by_hand);
        ImageUtils.AddSkin(getContext(),m_checkBtn);

        m_seekBarTips = new SeekBarTipsView(getContext());
        fl = new LayoutParams(ShareData.PxToDpi_xhdpi(120),ShareData.PxToDpi_xhdpi(120));
        fl.gravity = Gravity.BOTTOM | Gravity.LEFT;
        m_seekBarTips.setLayoutParams(fl);
        this.addView(m_seekBarTips);
        m_seekBarTips.setVisibility(GONE);

        m_sonWin = new SonWindow(getContext());
        fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ShareData.m_screenWidth / 3);
        fl.gravity = Gravity.TOP | Gravity.LEFT;
        m_sonWin.setLayoutParams(fl);
        this.addView(m_sonWin);

        m_waitDlg = new WaitAnimDialog((Activity)getContext());
    }

    private AD66BottomFr.ClickCallBack m_btcb = new ADAbsBottomFrWithRY.ClickCallBack() {
        @Override
        public void onItemClick(Object object, int index) {
            if(m_curMode == FILTER_MODE)
            {
                if(m_presenter.getCurSelectIndex1() != index)
                {
                    Utils.UrlTrigger(getContext(),"http://cav.adnonstop.com/cav/fe0a01a3d9/0073002457/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
                    m_presenter.changeStyleIndex(index);
                }
            }
            else if(m_curMode == COLOR_MODE)
            {
                if(m_presenter.getCurSelectIndex2() != index)
                {
                     if(index != 0)
                {
                    Utils.UrlTrigger(getContext(),"http://cav.adnonstop.com/cav/fe0a01a3d9/0073002437/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
                }
                m_presenter.changeColorIndex(index);
                if(index == 0)
                {
                    setVisibilityOrNot(false,m_seekBar,m_compareBtn,m_checkBtnFr);
                }
                else
                {
//                    if(!m_IsinitColorAlpha)
//                    {
//                        m_IsinitColorAlpha = true;
//                        if(index < 5)
//                        {
//                            m_presenter.changeMakeupAlpha(70);
//                        }
//                        else
//                        {
//                            m_presenter.changeMakeupAlpha(80);
//                        }
//                    }
                    setVisibilityOrNot(true,m_seekBar,m_compareBtn,m_checkBtnFr);
                    m_seekBar.setProgress(m_presenter.getColorProgressValue());
                }
                }
            }
        }

        @Override
        public void onOk() {
            if(m_uiEnabled)
            {
                m_presenter.onOk();
                if(m_curMode == FILTER_MODE)
                {
                    m_curMode = COLOR_MODE;
                    setVisibilityOrNot(false,m_bottomFr1,m_seekBar,m_compareBtn,m_checkBtnFr);
                    setVisibilityOrNot(true,m_bottomFr2);
                    m_presenter.reSetAlphas();
                    m_presenter.changeColorIndex(-1);
                    m_bottomFr2.SetselectIndex(1);
                }
                else if(m_curMode == COLOR_MODE)
                {

                }
            }
        }

        @Override
        public void onCancel() {
            if(m_uiEnabled)
            {
                m_presenter.onBack();
                if(m_curMode == FILTER_MODE)
                {

                }
                else if(m_curMode == COLOR_MODE)
                {
                    m_seekBar.setProgress(m_presenter.getProgressValue());
                    m_view.setImage(m_presenter.getFilterBmp());
                    m_IsinitColorAlpha = false;
                    setVisibilityOrNot(false,m_bottomFr2,m_checkBtnFr);
                    setVisibilityOrNot(true,m_bottomFr1,m_seekBar,m_compareBtn);
                    m_curMode = FILTER_MODE;
                    m_presenter.clearLipEffectBmp();
                }
            }
        }
    };


    //显示提示图片
    private void showTipsFr()
    {
       if(m_tipsImg != null)
            {
                this.removeView(m_tipsImg);
                m_tipsImg = null;
            }

            m_tipsImg = new ImageView(getContext());
            FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            m_tipsImg.setLayoutParams(fl);
            this.addView(m_tipsImg);
            m_tipsImg.setBackgroundColor(0xcc000000);
//            m_tipsImg.setImageResource(R.drawable.ad66_tipsbmp);
            m_tipsImg.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.UrlTrigger(getContext(),"http://cav.adnonstop.com/cav/fe0a01a3d9/0073003008/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
                    closeTipsImg();
                    m_presenter.facecheck();
                }
            });
    }

    private void closeTipsImg()
    {
        if(m_tipsImg != null)
            {
                this.removeView(m_tipsImg);
                m_tipsImg = null;
            }
    }

    MySeekBar.OnProgressChangeListener m_onProgressChangeListener = new MySeekBar.OnProgressChangeListener() {
        @Override
        public void onProgressChanged(MySeekBar seekBar, int progress) {
            RelayoutSeekBarTipsPos(seekBar,seekBar.getProgress(),100);
        }

        @Override
        public void onStartTrackingTouch(MySeekBar seekBar) {
            m_seekBarTips.setVisibility(VISIBLE);
            RelayoutSeekBarTipsPos(seekBar,seekBar.getProgress(),100);
        }

        @Override
        public void onStopTrackingTouch(MySeekBar seekBar) {
               m_seekBarTips.setVisibility(GONE);
            //根据当前是滤镜的模式还是彩妆的模式调不同的方法
            if(m_curMode == FILTER_MODE)
            {
                  m_presenter.changeFilterAlpha(seekBar.getProgress());
                m_presenter.sendAD66Effect_filter();
            }
            else if(m_curMode == COLOR_MODE)
            {
                 m_presenter.changeMakeupAlpha(seekBar.getProgress());
                m_presenter.sendAD66Effect_makeup();
            }
        }
    };

     private void RelayoutSeekBarTipsPos(MySeekBar mySeekBar,int progress,int maxProgress)
    {
        LayoutParams temp = (LayoutParams) m_seekBarTips.getLayoutParams();
        int[] dst = new int[2];
        mySeekBar.getLocationOnScreen(dst);
        temp.bottomMargin = (ShareData.m_screenHeight - dst[1]) + ShareData.PxToDpi_xhdpi(25);
        temp.leftMargin = (int) ((int) (dst[0] + mySeekBar.getCurCiclePos()) - m_seekBarTips.getWidth()/2f);
        m_seekBarTips.setText("" + progress);
        m_seekBarTips.setLayoutParams(temp);
    }


    //改变定点的ui的按钮的点击回调
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
            if(v == m_changepointFr.m_checkBackBtn)
            {
                m_view.m_showPosFlag = 0;
                m_view.m_touchPosFlag = m_view.m_showPosFlag;
                m_view.ResetAnim();
                m_view.reSetFaceData();
                if(m_posModify && m_isHasEffect && m_curMode == COLOR_MODE)
                {
                    m_presenter.sendAD66Effect_makeup();
                }
                m_posModify = false;
                m_view.setMode(BeautyCommonViewEx.MODE_NORMAL);
                m_isBackForChangePoint = true;
                changeUIByState(false);
            }
            else if(v == m_changepointFr.m_changePointOkBtn)
            {
                FaceDataV2.CHECK_FACE_SUCCESS = true;
                m_view.m_showPosFlag = 0;
                m_view.m_touchPosFlag = m_view.m_showPosFlag;
                if(m_presenter.getOrgBmp() != null)
                {
                    FaceDataV2.Raw2Ripe(m_presenter.getOrgBmp().getWidth(),m_presenter.getOrgBmp().getHeight());
                }
                m_view.ResetAnim();
                m_posModify = false;
                m_view.setMode(BeautyCommonViewEx.MODE_NORMAL);
                m_isBackForChangePoint = true;
                changeUIByState(false);
            }
            else if(v == m_changepointFr.m_checkThreeBackBtn)
            {
                FaceDataV2.CHECK_FACE_SUCCESS = true;
                m_changepointCB.onClick(m_changepointFr.m_checkBackBtn);
//                showTipsFr();
                m_bottomFr1.SetselectIndex(0);
            }
            else if(v == m_changepointFr.m_checkThreeOkBtn)
            {
                if(!FaceDataV2.CHECK_FACE_SUCCESS || !FaceDataV2.sIsFix)
                {
                    //没有检测到人脸的情况下，第一次改变定点，点确认之后的操作。
                    if(m_posModify)
                    {
                        FaceDataV2.sIsFix = true;
                        float[] faceAll = RAW_POS_MULTI[m_view.m_faceIndex].getFaceFeaturesMakeUp();
                        float[] faceData = RAW_POS_MULTI[m_view.m_faceIndex].getFaceRect();
                        filter.reFixPtsBShapes(getContext(), m_view.m_faceIndex, faceData, faceAll, m_presenter.getOrgBmp());
                        RAW_POS_MULTI[m_view.m_faceIndex].setFaceRect(faceData);
                        RAW_POS_MULTI[m_view.m_faceIndex].setMakeUpFeatures(faceAll);
                    }
                }
                FaceDataV2.CHECK_FACE_SUCCESS = true;
                m_view.m_showPosFlag = 0;
                m_view.m_touchPosFlag = m_view.m_showPosFlag;
                if(m_presenter.getOrgBmp() != null)
                {
                    FaceDataV2.Raw2Ripe(m_presenter.getOrgBmp().getWidth(),m_presenter.getOrgBmp().getHeight());
                }
                m_view.Data2UI();
                m_view.ResetAnim();
                m_view.invalidate();
                m_posModify = false;
                m_view.setMode(BeautyCommonViewEx.MODE_NORMAL);
                m_isBackForChangePoint = true;
                changeUIByState(false);
//                showTipsFr();
                m_bottomFr1.SetselectIndex(0);
            }
            else if(v == m_changepointFr.m_checkLipBtn)
            {
                doAnim(ChangePointPage.CHECK_INDEX_LIP);
                m_changepointFr.setUIFlag(MakeupUIHelper.ChangePointFr.CHECK_INDEX_LIP);
            }
            else if(v == m_changepointFr.m_checkCheekBtn)
            {
                doAnim(ChangePointPage.CHECK_INDEX_CHEEK);
                m_changepointFr.setUIFlag(MakeupUIHelper.ChangePointFr.CHECK_INDEX_CHEEK1);
            }
            else if(v == m_changepointFr.m_checkEyeBtnL)
            {
                doAnim(ChangePointPage.CHECK_INDEX_EYE_L);
                m_changepointFr.setUIFlag(MakeupUIHelper.ChangePointFr.CHECK_INDEX_EYE_L);
            }
            else if(v == m_changepointFr.m_checkEyebrowBtn)
            {
                doAnim(ChangePointPage.CHECK_INDEX_EYEBROW);
                m_changepointFr.setUIFlag(MakeupUIHelper.ChangePointFr.CHECK_INDEX_EYEBROW);
            }

        }
    };


    //显示选择不同人脸部位的动画
    public void doAnim(int flag)
    {
        m_view.Data2UI();
        m_view.setMode(BeautyCommonViewEx.MODE_MAKEUP);
        switch (flag)
        {
            case ChangePointPage.CHECK_TRHEE:
                m_view.setMode(BeautyCommonViewEx.MODE_FACE);
                m_view.m_showPosFlag = MakeUpViewEx1.POS_THREE;
                m_view.m_touchPosFlag = m_view.m_showPosFlag;
                m_view.DoFixedPointAnim();
                break;
            case ChangePointPage.CHECK_INDEX_EYE_L:
                m_view.m_showPosFlag = MakeUpViewEx1.POS_EYE;
                m_view.m_touchPosFlag = m_view.m_showPosFlag;
                m_view.DoFixedPointAnim();
                break;
            case ChangePointPage.CHECK_INDEX_EYEBROW:
                m_view.m_showPosFlag = MakeUpViewEx1.POS_EYEBROW;
                m_view.m_touchPosFlag = m_view.m_showPosFlag;
                m_view.DoFixedPointAnim();
                break;
            case ChangePointPage.CHECK_INDEX_LIP:
                m_view.m_showPosFlag = MakeUpViewEx1.POS_LIP;
                m_view.m_touchPosFlag = m_view.m_showPosFlag;
                m_view.DoFixedPointAnim();
                break;
            case ChangePointPage.CHECK_INDEX_CHEEK:
                m_view.m_showPosFlag = MakeUpViewEx1.POS_CHEEK;
                m_view.m_touchPosFlag = m_view.m_showPosFlag;
                m_view.DoFixedPointAnim();
                break;
        }
    }

    //根据是否是修改定点状态，显示不同的ui。
    private void changeUIByState(boolean isChangPointPage)
    {
        if(isChangPointPage)
        {
            setVisibilityOrNot(true,m_changepointFr);
            setVisibilityOrNot(false,m_bottomFr1,m_bottomFr2,m_seekBar,m_compareBtn,m_checkBtnFr);
        }
        else
        {
            if(m_isHasEffect)
            {
                setVisibilityOrNot(true,m_seekBar);
            }
            setVisibilityOrNot(true,m_checkBtnFr);
            if(m_curMode == FILTER_MODE)
            {
                setVisibilityOrNot(true,m_bottomFr1);
            }
            else if(m_curMode == COLOR_MODE)
            {
                setVisibilityOrNot(true,m_bottomFr2);
            }
            setVisibilityOrNot(false,m_changepointFr);
        }
    }

    private OnAnimationClickListener m_animClickListener = new OnAnimationClickListener() {
        @Override
        public void onAnimationClick(View v) {
            if(v == m_checkBtnFr)
            {
                m_posModify = false;
                //定点前要调用，复制一份人脸数据，点取消修改定点的时候，恢复原来的定点。
                m_view.copyFaceData();
                m_view.Data2UI();
                m_view.setMode(BeautyCommonViewEx.MODE_MAKEUP);
                m_view.m_showPosFlag = MakeUpViewEx1.POS_LIP;
                m_view.m_touchPosFlag = m_view.m_showPosFlag;
                m_view.DoFixedPointAnim();
                m_changepointFr.setUIFlag(MakeupUIHelper.ChangePointFr.CHECK_INDEX_LIP);
                changeUIByState(true);
            }
        }
    };

    private MakeUpViewEx1.ControlCallback m_cb = new MakeUpViewEx1.ControlCallback() {
        @Override
        public void OnSelFaceIndex(int index) {

        }

        @Override
        public void OnAnimFinish() {
            //等从定点ui返回正常ui的恢复动画做完之后，在显示出部分控件，因为之前有个bug是因为恢复动画没做完，又再次点击了定点按钮，会有bug
            if(m_isBackForChangePoint)
            {
                m_isBackForChangePoint = false;
                if(m_presenter.getCurBmp() != null)
                {
                    setVisibilityOrNot(true,m_compareBtn);
                }
            }
        }

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
            if(m_curMode == COLOR_MODE)
            {
                if(m_isHasEffect)
            {
                //改变定点实时做效果
                m_presenter.sendAD66Effect_makeup();
            }
            }
        }

        @Override
        public void onTouchWatermark() {

        }

        @Override
        public void onFingerUp() {

        }
    };


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

    private void setVisibilityOrNot(boolean isShow,View... views)
    {
        if(views != null && views.length > 0)
        {
            for (View view : views) {
                if(view != null)
                {
                    if(isShow)
                    {
                        view.setVisibility(VISIBLE);
                    }
                    else
                    {
                        view.setVisibility(GONE);
                    }
                }
            }
        }
    }

    @Override
    public void onBack() {
        m_btcb.onCancel();
    }

    @Override
    public void ShowWaitUI() {
        SetWaitUI(true,"");
    }

    //人脸识别之后的操作
    @Override
    public void finishFaceCheck() {
        m_uiEnabled = true;
        FaceDataV2.sFaceIndex = 0;
        SetWaitUI(false,"");
        if(FaceDataV2.CHECK_FACE_SUCCESS == true)
        {
//            showTipsFr();
//            m_presenter.sendAD66Effect();
            m_bottomFr1.SetselectIndex(0);
        }
        else
        {
            //没有检测到人脸，弹出提示弹框
            MakeNoFaceHelp();
            if(m_noFaceHelpFr != null)
            {
                m_noFaceHelpFr.show();
            }
        }
    }


    @Override
    public void setImageBmp(Bitmap bmp) {
        if(bmp != null)
        {
            m_view.setImage(bmp);
        }
    }


    //在线程做完动画之后，更新view的图片
    @Override
    public void updateBmp(Bitmap bmp) {
        m_view.setImage(bmp);
        SetWaitUI(false,"");
        if(m_isHasEffect == false && m_curMode == FILTER_MODE)
        {
            m_isHasEffect = true;
            setVisibilityOrNot(true,m_seekBar,m_compareBtn);
        }
//        if(!m_adTipsShowOnce)
//        {
//            m_adTipsShowOnce = true;
//            showTipsFr();
//        }
    }

    @Override
    public void dismissWaitUI() {
        SetWaitUI(false,"");
    }

    @Override
    public int getCurMode() {
        return m_curMode;
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
                        if(m_noFaceHelpFr != null)
                        {
                            m_noFaceHelpFr.dismiss();
                            m_noFaceHelpFr = null;
                        }
                        //定点前要调用，复制一份人脸数据，点取消修改定点的时候，恢复原来的定点。
                        m_view.copyFaceData();
                        m_view.setMode(BeautyCommonViewEx.MODE_FACE);
                        m_view.m_showPosFlag = MakeUpViewEx1.POS_THREE;//显示三个定点的模式
                        m_view.m_touchPosFlag = m_view.m_showPosFlag;
                        m_view.DoFixedPointAnim();
                        m_changepointFr.setUIFlag(MakeupUIHelper.ChangePointFr.CHECK_TRHEE);
                        changeUIByState(true);
                    }
                }
            });
        }
    }

    @Override
    public void onClose() {
        if(m_waitDlg != null)
        {
            m_waitDlg.dismiss();
            m_waitDlg = null;
        }
        m_presenter.ClearAll();
    }
}
