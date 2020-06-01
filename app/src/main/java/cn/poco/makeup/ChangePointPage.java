package cn.poco.makeup;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;

import cn.poco.advanced.ImageUtils;
import cn.poco.beautify.BeautifyView;
import cn.poco.beautify.SonWindow;
import cn.poco.camera.RotationImg2;
import cn.poco.face.FaceDataV2;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.image.filter;
import cn.poco.makeup.site.ChangePointPageSite;
import cn.poco.statistics.TongJi2;
import cn.poco.tianutils.ShareData;
import cn.poco.tianutils.Switch;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

import static cn.poco.beautify.BeautifyView.POS_THREE;
import static cn.poco.face.FaceDataV2.RAW_POS_MULTI;


/**
 * 修改人脸定点的页面
 */
public class ChangePointPage extends IPage {

    public static final String ISCHANGE = "ischangepoint";
    public static final String ISTHREEPAGE = "isThreePage";
    public final int THREE_PAGE = 10000;
    public final int DETAIL_PAGE = 10001;


    public static final int CHECK_INDEX_LIP = 0; //嘴唇
    public static final int CHECK_INDEX_EYE_L = 1;//眼睛
//    public static final int CHECK_INDEX_EYE_R = 2;//右眼
    public static final int CHECK_INDEX_EYEBROW = 3;//眉毛
    public static final int CHECK_INDEX_CHEEK = 4;//脸颊
    public static final int CHECK_INDEX_NOSE = 6;//鼻子

    public static final int CHECK_TRHEE = 5;

    protected int m_curSel = CHECK_TRHEE;
    protected int m_curPage = THREE_PAGE;
    protected int m_frW;
    protected int m_frH;
    protected int m_bottomBarHeight;
    protected int m_bottomListHeight;
    protected BeautifyView3 m_view;


    protected int m_clickType;//1 : 0k 2:cancel
    protected ImageView m_okBtn;//其他定点ok按钮
    protected ImageView m_backBtn;//其他定点返回按钮

    protected ImageView m_checkOkBtn;//三点定点ok按钮
    protected ImageView m_checkBackBtn;//三点定点返回按钮
    protected ChangePointPageSite m_site;

    protected Bitmap m_orgBmp;
    protected Bitmap m_showBmp;

    protected boolean m_uiEnabled;
    protected boolean m_isAniming = true;
    protected boolean m_cmdEnabled;

    protected TitleItem m_checkLipBtn;
    protected TitleItem m_checkEyeBtnL;
    protected TitleItem m_checkEyeBtnR;
    protected TitleItem m_checkEyebrowBtn;
    protected TitleItem m_checkCheekBtn;

    protected TextView m_checkContent;
    protected TextView m_checkTitle;
    protected ImageView m_checkLogo;
    protected LinearLayout m_selAllFr;
    protected Switch m_selAllCtrl;
    protected FrameLayout m_checkBar;
    private LinearLayout m_checkClassListlin;
    private FrameLayout m_classListBar;

    protected SonWindow m_sonWin;
    protected int m_showFaceIndex = -1;
    private ImageView m_line;

    private boolean m_isChange = false;

    private FrameLayout m_bottomFr;

    protected Handler m_UIHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            if(m_cmdEnabled)
            {
                switch(msg.what)
                {
                    case ChangePointHandler.MSG_UPDATE_UI:
                    {
                        ChangePointHandler.CmdMsg params = (ChangePointHandler.CmdMsg)msg.obj;
                        msg.obj = null;
                        if(m_view != null)
                        {
                            m_view.m_img.m_bmp = params.m_thumb;
                            m_view.UpdateUI();
                        }
                        break;
                    }

                    case ChangePointHandler.MSG_CANCEL:
                        //ChangePointPage.this.removeView(m_view);
                        //m_view.ReleaseMem();

                        if(m_clickType == 1)
                        {
                            onOk();
                        }
                        else if(m_clickType == 2)
                        {
                            onCancel();
                        }
                        break;
                }
            }
        }
    };
    protected HandlerThread m_imageThread;
    protected ChangePointHandler m_mainHandler;

    //private boolean m_posModify;
    protected ChangePointCallback mChangePointCallback;

    public interface ChangePointCallback
    {
        //线程调用
        public Bitmap DoChange(Bitmap orgBmp);
    }

    public ChangePointPage(Context context, BaseSite site) {
        super(context, site);
        m_site = (ChangePointPageSite) site;
        initData();
        initUI();
        TongJi2.StartPage(getContext(),getResources().getString(R.string.定点));
    }

    public static final String HIDE_LIP = "isHideLip";
    public static final String HIDE_EYE = "isHideEye";
    public static final String HIDE_EYEBROW = "isHideEyeBrow";
    public static final String HIDE_CHEEK = "isHideCheek";

	/**
     *
     * @param params
     *              org_img : Bitmap 原始图片
     *              imgs : Bitmap/String/RotationImg2[]显示的图片
     *              flag 定点类型  {@link ChangePointPage#CHECK_TRHEE}
     *              index 多个人脸时选择的人脸
     *              callback 改变定点后的回调{@link ChangePointPage.ChangePointCallback}
     *              isHideLip true 隐藏嘴唇选项
     *              isHideEye true 隐藏眼睛选项
     *              isHideEyeBrow true 隐藏眉毛选项
     *              isHideCheek true 隐藏脸颊选项
     */
    @Override
    public void SetData(HashMap<String, Object> params) {
        if(params != null)
        {
            if(params.get("index") != null)
            {
                int temp = (int) params.get("index");
                if(temp >= 0)
                {
                    m_showFaceIndex = (int) params.get("index");
                    m_view.m_faceIndex = m_showFaceIndex;
                }
            }

            if(params.get("callback")!= null)
            {
                mChangePointCallback = (ChangePointCallback)params.get("callback");

                m_imageThread = new HandlerThread("change_point_page_my_handler_thread");
                m_imageThread.start();
                m_mainHandler = new ChangePointHandler(m_imageThread.getLooper(), getContext(), m_UIHandler, mChangePointCallback);
            }

            if(params.get("org_img") != null)
            {
                m_orgBmp = (Bitmap)params.get("org_img");
            }

            if(params.get("imgs")!= null)
            {
                if(params.get("imgs") instanceof RotationImg2[])
                {
                    setImg(params);
                }
                else if(params.get("imgs") instanceof Bitmap)
                {
                    setImg((Bitmap)params.get("imgs"));
                }
                else if(params.get("imgs") instanceof String)
                {
                    setImg((String) params.get("imgs"));
                }
            }


            if(params.get("type") != null)
            {
                int flag = (int) params.get("type");
                m_curSel = flag;
                if(flag == CHECK_TRHEE)
                {
                    m_curPage = THREE_PAGE;
                    InitCheckBar(0);
                    TranslateAnimVertical(m_bottomFr,ShareData.PxToDpi_xhdpi(234),0);
                }
                else
                {
                    m_curPage = DETAIL_PAGE;
                    InitCheckBar(1);
                    SetSelCheckBarClass(flag);
                    SetSelCheckClassList(flag,flag);
                    TranslateAnimVertical(m_bottomFr,ShareData.PxToDpi_xhdpi(320),0);
                }
                doAnim(flag);
            }
            else
            {
                m_isAniming = false;
            }

            if(params.get(HIDE_LIP) != null )
            {
                if((params.get(HIDE_LIP) instanceof Boolean) && (boolean)(params.get(HIDE_LIP)) == true)
                {
                    hideItem(ChangePointPage.CHECK_INDEX_LIP);
                }
            }

            if(params.get(HIDE_EYE) != null )
            {
                if((params.get(HIDE_EYE) instanceof Boolean) && (boolean)(params.get(HIDE_EYE)) == true)
                {
                    hideItem(ChangePointPage.CHECK_INDEX_EYE_L);
                }
            }

            if(params.get(HIDE_CHEEK) != null )
            {
                if((params.get(HIDE_CHEEK) instanceof Boolean) && (boolean)(params.get(HIDE_CHEEK)) == true)
                {
                    hideItem(ChangePointPage.CHECK_INDEX_CHEEK);
                }
            }

            if(params.get(HIDE_EYEBROW) != null )
            {
                if((params.get(HIDE_EYEBROW) instanceof Boolean) && (boolean)(params.get(HIDE_EYEBROW)) == true)
                {
                    hideItem(ChangePointPage.CHECK_INDEX_EYEBROW);
                }
            }
        }
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


    private void hideItem(int type)
    {
        switch (type)
        {
            case ChangePointPage.CHECK_INDEX_LIP:
            if(m_checkLipBtn != null)
            {
                m_checkLipBtn.setVisibility(GONE);
            }
            break;

            case ChangePointPage.CHECK_INDEX_EYE_L:
                if(m_checkEyeBtnL != null)
                {
                    m_checkEyeBtnL.setVisibility(GONE);
                }
                break;

            case ChangePointPage.CHECK_INDEX_EYEBROW:
                if(m_checkEyebrowBtn != null)
                {
                    m_checkEyebrowBtn.setVisibility(GONE);
                }
                break;

            case ChangePointPage.CHECK_INDEX_CHEEK:
                if(m_checkCheekBtn != null)
                {
                    m_checkCheekBtn.setVisibility(GONE);
                }
                break;
        }
    }

    public void doAnim(int flag)
    {
        //做动画期间不能返回
        m_isAniming = true;
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                m_isAniming = false;
            }
        },m_view.def_face_anim_time + 20);
        m_view.Data2UI();
        if(m_view.getOperateMode() != BeautifyView.MODE_MAKEUP)
        {
            m_view.SetOperateMode(BeautifyView.MODE_MAKEUP);

            if(m_selAllCtrl.isChecked())
            {
                m_view.m_moveAllFacePos = true;
                m_view.UpdateUI();
            }
        }
        switch (flag)
        {
            case CHECK_TRHEE:
                m_curSel = CHECK_TRHEE;
                m_view.SetOperateMode(BeautifyView.MODE_FACE);
                m_view.m_showPosFlag = POS_THREE;
                m_view.m_touchPosFlag = m_view.m_showPosFlag;
                m_view.DoAnim2All();
                break;
            case CHECK_INDEX_EYE_L:
                m_curSel = CHECK_INDEX_EYE_L;
                m_view.m_showPosFlag = BeautifyView3.POS_EYE;
                m_view.m_touchPosFlag = BeautifyView3.POS_EYE;
                m_view.DoAnim2EyeL();
                break;
//            case CHECK_INDEX_EYE_R:
//                m_curSel = CHECK_INDEX_EYE_R;
//                m_view.m_showPosFlag = BeautifyView3.POS_EYE_R;
//                m_view.m_touchPosFlag = BeautifyView3.POS_EYE;
//                m_view.DoAnim2EyeR();
//                break;
            case CHECK_INDEX_EYEBROW:
                m_curSel = CHECK_INDEX_EYEBROW;
                m_view.m_showPosFlag = BeautifyView.POS_EYEBROW;
                m_view.m_touchPosFlag = m_view.m_showPosFlag;
                m_view.DoAnim2Eyebrow();
                break;
            case CHECK_INDEX_LIP:
                m_curSel = CHECK_INDEX_LIP;
                m_view.m_showPosFlag = BeautifyView.POS_LIP;
                m_view.m_touchPosFlag = m_view.m_showPosFlag;
                m_view.DoAnim2Mouth();
                break;
            case CHECK_INDEX_CHEEK:
                m_curSel = CHECK_INDEX_CHEEK;
                m_view.m_showPosFlag = BeautifyView.POS_CHEEK;
                m_view.m_touchPosFlag = m_view.m_showPosFlag;
                m_view.DoAnim2Cheek();
                break;
        }
        m_view.UpdateUI();
    }


    private void initData()
    {
        m_uiEnabled = true;
        m_cmdEnabled = true;
        m_frW = ShareData.m_screenWidth;
        m_frH = m_frW*4/3;

        int frH = m_frW * 4 / 3;
        frH -= frH % 2;
        if(frH > m_frH)
        {
            frH = m_frH;
        }
        m_frW += 2;//为了去白边
        m_bottomBarHeight = ShareData.PxToDpi_xhdpi(100);
        m_bottomListHeight = ShareData.PxToDpi_xhdpi(240);
    }

    private void initUI()
    {
        m_view = new BeautifyView3(getContext(), m_frW, m_frH);
        m_view.def_rotation_res = R.drawable.photofactory_pendant_rotation;
        m_view.def_delete_res = R.drawable.photofactory_pendant_delete;
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
        m_view.InitData(m_cb);
        FrameLayout.LayoutParams fl = new LayoutParams(m_frW, ShareData.m_screenHeight);
        fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        m_view.setLayoutParams(fl);
        this.addView(m_view, 0);
        m_view.SetOperateMode(BeautifyView.MODE_MAKEUP);


        m_bottomFr = new FrameLayout(getContext());
        fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,ShareData.PxToDpi_xhdpi(320));
        fl.gravity = Gravity.BOTTOM;
        m_bottomFr.setLayoutParams(fl);
        this.addView(m_bottomFr);


        m_classListBar = new FrameLayout(getContext());
        fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(94));
        fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        fl.bottomMargin = ShareData.PxToDpi_xhdpi(226);
        m_classListBar.setLayoutParams(fl);
        m_classListBar.setBackgroundColor(0xf4ffffff);
        m_bottomFr.addView(m_classListBar);


        m_checkClassListlin = new LinearLayout(getContext());
        m_checkClassListlin.setOrientation(LinearLayout.HORIZONTAL);
        fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        fl.gravity = Gravity.CENTER;
        m_checkClassListlin.setLayoutParams(fl);
        m_classListBar.addView(m_checkClassListlin);
        {
            m_checkLipBtn = new TitleItem(getContext());
            m_checkLipBtn.SetData(R.drawable.beautify_makeup_point_zuichun_icon,R.drawable.beautify_makeup_point_zuichun_icon_hover,getResources().getString(R.string.makeup_changepoint_zuichun_name));
            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(108), LinearLayout.LayoutParams.MATCH_PARENT);
            m_checkLipBtn.setLayoutParams(ll);
            m_checkClassListlin.addView(m_checkLipBtn);
            m_checkLipBtn.setOnClickListener(m_onClickListener);
//            m_checkLipBtn.setOnTouchListener(m_onClickListenerNew);

            m_checkEyeBtnL = new TitleItem(getContext());
            m_checkEyeBtnL.SetData(R.drawable.beautify_makeup_point_zuoyan_icon,R.drawable.beautify_makeup_point_zuoyan_icon_hover,getResources().getString(R.string.makeup_changepoint_zuoyan_name));
            ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(108), LinearLayout.LayoutParams.MATCH_PARENT);
            ll.leftMargin = ShareData.PxToDpi_xhdpi(5);
            m_checkEyeBtnL.setLayoutParams(ll);
            m_checkClassListlin.addView(m_checkEyeBtnL);
            m_checkEyeBtnL.setOnClickListener(m_onClickListener);
//            m_checkEyeBtnL.setOnTouchListener(m_onClickListenerNew);

//            m_checkEyeBtnR = new TitleItem(getContext());
//            m_checkEyeBtnR.SetData(R.drawable.beautify_makeup_point_youyan_icon,R.drawable.beautify_makeup_point_youyan_icon_hover,getResources().getString(R.string.makeup_changepoint_youyan_name));
//            ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(108), LinearLayout.LayoutParams.MATCH_PARENT);
//            m_checkEyeBtnR.setLayoutParams(ll);
//            m_checkClassListlin.addView(m_checkEyeBtnR);
//            m_checkEyeBtnR.setOnClickListener(m_onClickListener);

            m_checkEyebrowBtn = new TitleItem(getContext());
            m_checkEyebrowBtn.SetData(R.drawable.beautify_makeup_point_meimao_icon,R.drawable.beautify_makeup_point_meimao_icon_hover,getResources().getString(R.string.makeup_changepoint_meimao_name));
            ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(108), LinearLayout.LayoutParams.MATCH_PARENT);
            ll.leftMargin = ShareData.PxToDpi_xhdpi(5);
            m_checkEyebrowBtn.setLayoutParams(ll);
            m_checkClassListlin.addView(m_checkEyebrowBtn);
            m_checkEyebrowBtn.setOnClickListener(m_onClickListener);
//            m_checkEyebrowBtn.setOnTouchListener(m_onClickListenerNew);

            m_checkCheekBtn = new TitleItem(getContext());
            m_checkCheekBtn.SetData(R.drawable.beautify_makeup_point_lianjia_icon,R.drawable.beautify_makeup_point_lianjia_icon_hover,getResources().getString(R.string.makeup_chnagepoint_lianjia_name));
            ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(108), LinearLayout.LayoutParams.MATCH_PARENT);
            ll.leftMargin = ShareData.PxToDpi_xhdpi(5);
            m_checkCheekBtn.setLayoutParams(ll);
            m_checkClassListlin.addView(m_checkCheekBtn);
            m_checkCheekBtn.setOnClickListener(m_onClickListener);
//            m_checkCheekBtn.setOnTouchListener(m_onClickListenerNew);
        }

        m_backBtn = new ImageView(getContext());
        m_backBtn.setImageResource(R.drawable.beautify_cancel);
        fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        fl.leftMargin = ShareData.PxToDpi_xhdpi(22);
        m_backBtn.setLayoutParams(fl);
        m_classListBar.addView(m_backBtn);
//        m_backBtn.setOnClickListener(m_onClickListener);
        m_backBtn.setOnTouchListener(m_onClickListenerNew);

        m_okBtn = new ImageView(getContext());
        fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        fl.rightMargin = ShareData.PxToDpi_xhdpi(24);
        m_okBtn.setLayoutParams(fl);
        m_classListBar.addView(m_okBtn);
//        m_okBtn.setOnClickListener(m_onClickListener);
        m_okBtn.setOnTouchListener(m_onClickListenerNew);
        m_okBtn.setImageResource(R.drawable.beautify_ok);
        ImageUtils.AddSkin(getContext(),m_okBtn);


        m_checkBar = new FrameLayout(getContext());
        fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(226));
        fl.gravity = Gravity.LEFT | Gravity.BOTTOM;
        m_checkBar.setLayoutParams(fl);
        m_bottomFr.addView(m_checkBar);
        m_checkBar.setBackgroundColor(0xf4ffffff);

        m_line = new ImageView(getContext());
        fl = new FrameLayout.LayoutParams(ShareData.m_screenWidth,1);
        fl.gravity = Gravity.BOTTOM;
        fl.bottomMargin = ShareData.PxToDpi_xhdpi(226);
        m_line.setLayoutParams(fl);
        m_bottomFr.addView(m_line);
        m_line.setBackgroundColor(0xffcccccc);

        {
            m_checkContent = new TextView(getContext());
            m_checkContent.setTextColor(0xb2000000);
            m_checkContent.setGravity(Gravity.TOP | Gravity.LEFT);
            m_checkContent.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            m_checkContent.setLayoutParams(fl);
            m_checkBar.addView(m_checkContent);

            m_checkTitle = new TextView(getContext());
            m_checkTitle.setTextColor(0xff000000);
            m_checkTitle.setGravity(Gravity.TOP | Gravity.LEFT);
            m_checkTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            m_checkTitle.setSingleLine();
            fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            m_checkTitle.setLayoutParams(fl);
            m_checkBar.addView(m_checkTitle);

            m_checkLogo = new ImageView(getContext());
            m_checkLogo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            m_checkLogo.setLayoutParams(fl);
            m_checkBar.addView(m_checkLogo);

//            ImageUtils.AddSkin();
            m_selAllFr = new LinearLayout(getContext());
            m_selAllFr.setOrientation(LinearLayout.HORIZONTAL);
            //m_selAllFr.setPadding(ShareData.PxToDpi_xhdpi(30), 0, 0, 0);
            fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            fl.gravity = Gravity.LEFT | Gravity.BOTTOM;
            fl.leftMargin = ShareData.PxToDpi_xhdpi(250);
            fl.bottomMargin = ShareData.PxToDpi_xhdpi(27);
            m_selAllFr.setLayoutParams(fl);
            m_checkBar.addView(m_selAllFr);
            {
                m_selAllCtrl = new Switch(getContext());
                //thumbOnBk 选中状态Bitmap
                Bitmap temp = BitmapFactory.decodeResource(getResources(),R.drawable.beautify_changepoint_locked_bk_icon);
                Bitmap thumbOnBk = temp.copy(Bitmap.Config.ARGB_8888,true);
                if(temp != null)
                {
                    temp.recycle();
                    temp = null;
                }
                Canvas thumbOnCanvas = new Canvas(thumbOnBk);
                temp = BitmapFactory.decodeResource(getResources(),R.drawable.beautify_changepoint_locked_icon);
                Bitmap thumbOn = ImageUtils.AddSkin(getContext(),temp);
                if(temp != null)
                {
                    int startX = (int) ((thumbOnBk.getWidth() - thumbOn.getWidth())/2f);
                    int startY = (int) ((thumbOnBk.getHeight() - thumbOn.getHeight())/2f);
                    thumbOnCanvas.drawBitmap(thumbOn,startX,startY,null);
                }
                BitmapDrawable thumbOnDrawable = new BitmapDrawable(getResources(),thumbOnBk);

                // thumbOffBk 没选中状态Bitmap
                temp = BitmapFactory.decodeResource(getResources(),R.drawable.beautify_changepoint_lock_bk_icon);
                Bitmap thumbOffBk = temp.copy(Bitmap.Config.ARGB_8888,true);
                if(temp != null)
                {
                    temp.recycle();
                    temp = null;
                }
                Canvas thumbOffCanvas = new Canvas(thumbOffBk);
                temp = BitmapFactory.decodeResource(getResources(),R.drawable.beautify_changepoint_lock_icon);
                Bitmap thumbOff = temp;
                if(temp != null)
                {
                    int startX = (int) ((thumbOffBk.getWidth() - thumbOff.getWidth())/2f);
                    int startY = (int) ((thumbOffBk.getHeight() - thumbOff.getHeight())/2f);
                    thumbOffCanvas.drawBitmap(thumbOff,startX,startY,null);
                }
                BitmapDrawable thumbOffDrawable = new BitmapDrawable(getResources(),thumbOffBk);

                StateListDrawable drawable = new StateListDrawable();
                drawable.addState(new int[]{android.R.attr.state_checked},
                        thumbOnDrawable);
                drawable.addState(new int[]{-android.R.attr.state_pressed},
                        thumbOffDrawable);
                drawable.addState(new int[]{android.R.attr.state_pressed},thumbOnDrawable);

                m_selAllCtrl.setThumbDrawable(drawable);

//                m_selAllCtrl.setThumbResource(R.drawable.beautify_allsel_switch_thumb);
//                m_selAllCtrl.setTrackOffResource(R.drawable.beautify_allsel_switch_track_off);
//                m_selAllCtrl.setTrackOnResource(R.drawable.beautify_allsel_switch_track_on);

                Bitmap track_off = BitmapFactory.decodeResource(getResources(),R.drawable.beautify_allsel_switch_track_off_new);
                BitmapDrawable track_off_drawable = new BitmapDrawable(getResources(),track_off);
                m_selAllCtrl.setTrackOffDrawable(track_off_drawable);

                temp = BitmapFactory.decodeResource(getResources(),R.drawable.beautify_allsel_switch_track_on_new);
                Bitmap track_on = ImageUtils.AddSkin(getContext(),temp,0xffe75988);
                if(temp != null)
                {
                    temp.recycle();
                    temp = null;
                }
                BitmapDrawable track_on_drawable = new BitmapDrawable(getResources(),track_on);
                m_selAllCtrl.setTrackOnDrawable(track_on_drawable);

                m_selAllCtrl.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked, boolean fromUser)
                    {
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
                });
                LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(80), ShareData.PxToDpi_xhdpi(48));
                ll.gravity = Gravity.CENTER;
                m_selAllCtrl.setLayoutParams(ll);
                m_selAllFr.addView(m_selAllCtrl);

                TextView tex = new TextView(getContext());
                tex.setTextColor(0xFF999999);
                tex.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
                tex.setText(getResources().getString(R.string.makeup_changepoint_help_tips1));
                tex.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                tex.setSingleLine();
                ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                ll.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                ll.leftMargin = ShareData.PxToDpi_xhdpi(10);
                tex.setLayoutParams(ll);
                m_selAllFr.addView(tex);
            }

            m_checkOkBtn = new ImageView(getContext());
            fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            fl.gravity = Gravity.RIGHT | Gravity.TOP;
            fl.topMargin = ShareData.PxToDpi_xhdpi(20);
            fl.rightMargin = ShareData.PxToDpi_xhdpi(24);
            m_checkOkBtn.setLayoutParams(fl);
            m_checkBar.addView(m_checkOkBtn);
            m_checkOkBtn.setImageResource(R.drawable.beautify_ok);
//            m_checkOkBtn.setOnClickListener(m_onClickListener);
            m_checkOkBtn.setOnTouchListener(m_onClickListenerNew);
            ImageUtils.AddSkin(getContext(),m_checkOkBtn);

            m_checkBackBtn = new ImageView(getContext());
            fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            fl.gravity = Gravity.LEFT | Gravity.TOP;
            fl.topMargin = ShareData.PxToDpi_xhdpi(20);
            fl.leftMargin = ShareData.PxToDpi_xhdpi(22);
            m_checkBackBtn.setLayoutParams(fl);
            m_checkBar.addView(m_checkBackBtn);
            m_checkBackBtn.setImageResource(R.drawable.beautify_cancel);
//            m_checkBackBtn.setOnClickListener(m_onClickListener);
            m_checkBackBtn.setOnTouchListener(m_onClickListenerNew);
        }

        m_sonWin = new SonWindow(getContext());
        fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ShareData.m_screenWidth / 3);
        fl.gravity = Gravity.TOP | Gravity.LEFT;
        m_sonWin.setLayoutParams(fl);
        this.addView(m_sonWin);

    }

    private void InitCheckBar(int flag)
    {
        FrameLayout.LayoutParams fl;
        switch(flag)
        {
            case 0:
                fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(234));
                fl.gravity = Gravity.LEFT | Gravity.BOTTOM;
                m_checkBar.setLayoutParams(fl);

                m_checkLogo.setVisibility(View.GONE);
                m_selAllFr.setVisibility(View.GONE);
                m_classListBar.setVisibility(GONE);
                m_line.setVisibility(GONE);

                m_checkTitle.setText(getResources().getString(R.string.makeup_changepoint_title1));
                fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                fl.gravity = Gravity.LEFT | Gravity.TOP;
                fl.topMargin = ShareData.PxToDpi_xhdpi(108);
                fl.leftMargin = ShareData.PxToDpi_xhdpi(32);
                m_checkTitle.setLayoutParams(fl);

                m_checkContent.setText(getResources().getString(R.string.makeup_changepoint_title2));
                fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(600), ShareData.PxToDpi_xhdpi(70));
                fl.gravity = Gravity.LEFT | Gravity.TOP;
                fl.topMargin = ShareData.PxToDpi_xhdpi(148);
                fl.leftMargin = ShareData.PxToDpi_xhdpi(32);
                m_checkContent.setLayoutParams(fl);

                break;
            case 1:
                m_checkOkBtn.setVisibility(GONE);
                m_checkBackBtn.setVisibility(GONE);

                m_checkLogo.setVisibility(View.VISIBLE);
                fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                fl.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                fl.leftMargin = ShareData.PxToDpi_xhdpi(28);
                m_checkLogo.setLayoutParams(fl);

                m_selAllFr.setVisibility(View.VISIBLE);

                m_checkTitle.setText(getResources().getString(R.string.makeup_changepoint_title3));
                fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                fl.gravity = Gravity.LEFT | Gravity.TOP;
                fl.topMargin = ShareData.PxToDpi_xhdpi(30);
                fl.leftMargin = ShareData.PxToDpi_xhdpi(224);
                m_checkTitle.setLayoutParams(fl);

                fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(380), ShareData.PxToDpi_xhdpi(100));
                fl.gravity = Gravity.LEFT | Gravity.TOP;
                fl.topMargin = ShareData.PxToDpi_xhdpi(72);
                fl.leftMargin = ShareData.PxToDpi_xhdpi(224);
                m_checkContent.setLayoutParams(fl);

                break;
            default:
                break;
        }
    }


    private void setImg(String path)
    {
        if(path != null && path.length() > 0)
        {
            Bitmap bmp = cn.poco.imagecore.Utils.DecodeFile(path,null);
            if(bmp != null && !bmp.isRecycled())
            {
                setImg(bmp);
            }
        }
    }

    private void setImg(Bitmap bmp)
    {
        m_showBmp = bmp;
        m_view.SetImg(null,bmp);
        m_view.CreateViewBuffer(); //创建显示缓存
        m_view.UpdateUI();
        m_view.copyFaceData();
    }

    private void setImg(HashMap<String, Object> params)
    {
        Object info = params.get("imgs");

        if(info instanceof RotationImg2[])
        {
            RotationImg2[] temp = (RotationImg2[]) info;
            int rotation = temp[0].m_degree;
            int flip =  temp[0].m_flip;
            int layoutMode = -1;
            if(params.get("layout_mode") != null)
            {
                layoutMode = (int) params.get("layout_mode");
            }
            float scale = ImageUtils.GetImgScale(getContext(), info, layoutMode);
           m_showBmp = cn.poco.imagecore.Utils.DecodeShowImage((Activity)getContext(), temp[0].m_img, rotation, scale, flip);
        }

        if(m_showBmp != null)
        {
            m_view.SetImg(info, m_showBmp);
            m_view.CreateViewBuffer(); //创建显示缓存
            m_view.UpdateUI();
            m_view.copyFaceData();
        }
    }

    public void onOk()
    {
        if(!FaceDataV2.CHECK_FACE_SUCCESS || !FaceDataV2.sIsFix)
        {
            if(m_isChange)
            {
                FaceDataV2.sIsFix = true;
                float[] faceAll = RAW_POS_MULTI[m_view.m_faceIndex].getFaceFeaturesMakeUp();
                float[] faceData = FaceDataV2.RAW_POS_MULTI[m_view.m_faceIndex].getFaceRect();
                filter.reFixPtsCosmetic(faceData, faceAll, m_showBmp);
                FaceDataV2.RAW_POS_MULTI[m_view.m_faceIndex].setFaceRect(faceData);
                FaceDataV2.RAW_POS_MULTI[m_view.m_faceIndex].setMakeUpFeatures(faceAll);
            }
        }
        FaceDataV2.CHECK_FACE_SUCCESS = true;
        FaceDataV2.Raw2Ripe(m_view.m_img.m_w, m_view.m_img.m_h);
        m_view.Data2UI();
        m_view.DoAnim(new RectF(0, 0, 1, 1), m_view.def_face_anim_type, m_view.def_face_anim_time);
        m_view.UpdateUI();
        ChangePointPage.this.postDelayed(new Runnable() {
            @Override
            public void run() {
                HashMap<String,Object> params = new HashMap<String, Object>();
                params.put(ISCHANGE,m_isChange);
                params.put(ISTHREEPAGE,m_curPage == THREE_PAGE?true:false);
                m_site.onBack(params,getContext());
            }
        },0);

        m_view.m_moveAllFacePos = false;
    }

    public void onCancel()
    {
        FaceDataV2.CHECK_FACE_SUCCESS = true;
        m_view.reSetFaceData();
        m_view.m_moveAllFacePos = false;
        HashMap<String,Object> params = new HashMap<String, Object>();
        params.put(ISCHANGE,false);
        params.put(ISTHREEPAGE,m_curPage == THREE_PAGE?true:false);
        m_site.onBack(params,getContext());
    }

    public void sendQuitMsg()
    {
        Message sendMsg = m_mainHandler.obtainMessage();
        sendMsg.what = ChangePointHandler.MSG_CANCEL;
        m_mainHandler.sendMessage(sendMsg);
    }

    OnClickListener m_onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            if(!m_uiEnabled || m_isAniming)
            {
                return;
            }

//            if(v == m_okBtn)
//            {
//                m_uiEnabled = false;
//                m_clickType = 1;
//
//                if(mChangePointCallback == null || m_orgBmp == null)
//                {
//                    onOk();
//                }
//                else
//                {
//                    sendQuitMsg();
//                }
//            }
//            else if(v == m_checkOkBtn)
//            {
//                if(!FaceDataV2.CHECK_FACE_SUCCESS || !FaceDataV2.sIsFix)
//                {
//                    if(m_isChange)
//                    {
//                        FaceDataV2.sIsFix = true;
//                        float[] faceAll = RAW_POS_MULTI[m_view.m_faceIndex].getFaceFeaturesMakeUp();
//                        float[] faceData = FaceDataV2.RAW_POS_MULTI[m_view.m_faceIndex].getFaceRect();
//                        filter.reFixPtsBShapes(getContext(), m_view.m_faceIndex, faceData, faceAll, m_showBmp);
//                        FaceDataV2.RAW_POS_MULTI[m_view.m_faceIndex].setFaceRect(faceData);
//                        FaceDataV2.RAW_POS_MULTI[m_view.m_faceIndex].setMakeUpFeatures(faceAll);
//                    }
//                }
//
//                FaceDataV2.CHECK_FACE_SUCCESS = true;
//                FaceDataV2.Raw2Ripe(m_view.m_img.m_w, m_view.m_img.m_h);
//                m_view.Data2UI();
//                HashMap<String,Object> params = new HashMap<String, Object>();
//                params.put(ISCHANGE,m_isChange);
//                m_site.onBack(params);
//            }
//            else if(v == m_backBtn)
//            {
//                m_uiEnabled = false;
//                m_clickType = 2;
//
//                if(mChangePointCallback == null || m_orgBmp == null)
//                {
//                    onCancel();
//                }
//                else
//                {
//                    sendQuitMsg();
//                }
//            }
//            else if(v == m_checkBackBtn)
//            {
//                FaceDataV2.CHECK_FACE_SUCCESS = true;
//                m_view.reSetFaceData();
//                HashMap<String,Object> params = new HashMap<String, Object>();
//                params.put(ISCHANGE,false);
//                m_site.onBack(params);
//            }
             if(v == m_checkLipBtn)
            {
                if(m_curSel != CHECK_INDEX_LIP)
                {
                    SetSelCheckClassList(m_curSel,CHECK_INDEX_LIP);
                    SetSelCheckBarClass(CHECK_INDEX_LIP);
                    doAnim(CHECK_INDEX_LIP);
                    m_curSel = CHECK_INDEX_LIP;
                }
            }
            else if(v == m_checkCheekBtn)
            {
                if(m_curSel != CHECK_INDEX_CHEEK)
                {
                    SetSelCheckClassList(m_curSel,CHECK_INDEX_CHEEK);
                    SetSelCheckBarClass(CHECK_INDEX_CHEEK);
                    doAnim(CHECK_INDEX_CHEEK);
                    m_curSel = CHECK_INDEX_CHEEK;
                }
            }
            else if(v == m_checkEyebrowBtn)
            {
                if(m_curSel != CHECK_INDEX_EYEBROW)
                {

                    SetSelCheckClassList(m_curSel,CHECK_INDEX_EYEBROW);
                    SetSelCheckBarClass(CHECK_INDEX_EYEBROW);
                    doAnim(CHECK_INDEX_EYEBROW);
                    m_curSel = CHECK_INDEX_EYEBROW;
                }
            }
            else if(v == m_checkEyeBtnL)
            {
                if(m_curSel != CHECK_INDEX_EYE_L)
                {
                    SetSelCheckClassList(m_curSel,CHECK_INDEX_EYE_L);
                    SetSelCheckBarClass(CHECK_INDEX_EYE_L);
                    doAnim(CHECK_INDEX_EYE_L);
                    m_curSel = CHECK_INDEX_EYE_L;
                }
            }
//            else if(v == m_checkEyeBtnR)
//            {
//                if(m_curSel != CHECK_INDEX_EYE_R)
//                {
//                    SetSelCheckClassList(m_curSel,CHECK_INDEX_EYE_R);
//                    SetSelCheckBarClass(CHECK_INDEX_EYE_R);
//                    doAnim(CHECK_INDEX_EYE_R);
//                    m_curSel = CHECK_INDEX_EYE_R;
//                }
//            }
        }
    };

    OnAnimationClickListener m_onClickListenerNew = new OnAnimationClickListener() {
        @Override
        public void onAnimationClick(View v) {
            if(!m_uiEnabled || m_isAniming)
            {
                return;
            }

            if(v == m_okBtn)
            {
                m_uiEnabled = false;
                m_clickType = 1;

                if(mChangePointCallback == null || m_orgBmp == null)
                {
                    onOk();
                }
                else
                {
                    sendQuitMsg();
                }
            }
            else if(v == m_checkOkBtn)
            {
                if(!FaceDataV2.CHECK_FACE_SUCCESS || !FaceDataV2.sIsFix)
                {
                    if(m_isChange)
                    {
                        FaceDataV2.sIsFix = true;
                        float[] faceAll = RAW_POS_MULTI[m_view.m_faceIndex].getFaceFeaturesMakeUp();
                        float[] faceData = FaceDataV2.RAW_POS_MULTI[m_view.m_faceIndex].getFaceRect();
                        filter.reFixPtsBShapes(getContext(), m_view.m_faceIndex, faceData, faceAll, m_showBmp);
                        FaceDataV2.RAW_POS_MULTI[m_view.m_faceIndex].setFaceRect(faceData);
                        FaceDataV2.RAW_POS_MULTI[m_view.m_faceIndex].setMakeUpFeatures(faceAll);
                    }
                }

                FaceDataV2.CHECK_FACE_SUCCESS = true;
                FaceDataV2.Raw2Ripe(m_view.m_img.m_w, m_view.m_img.m_h);
                m_view.Data2UI();
                HashMap<String,Object> params = new HashMap<String, Object>();
                params.put(ISCHANGE,m_isChange);
                params.put(ISTHREEPAGE,m_curPage == THREE_PAGE?true:false);
                m_site.onBack(params,getContext());
            }
            else if(v == m_backBtn)
            {
                m_uiEnabled = false;
                m_clickType = 2;

                if(mChangePointCallback == null || m_orgBmp == null)
                {
                    onCancel();
                }
                else
                {
                    sendQuitMsg();
                }
            }
            else if(v == m_checkBackBtn)
            {
                FaceDataV2.CHECK_FACE_SUCCESS = true;
                m_view.reSetFaceData();
                HashMap<String,Object> params = new HashMap<String, Object>();
                params.put(ISCHANGE,false);
                params.put(ISTHREEPAGE,m_curPage == THREE_PAGE?true:false);
                m_site.onBack(params,getContext());
            }
        }

        @Override
        public void onTouch(View v) {

        }

        @Override
        public void onRelease(View v) {

        }
    };

    BeautifyView.ControlCallback m_cb = new BeautifyView.ControlCallback() {
        @Override
        public Bitmap MakeShowImg(Object info, int frW, int frH) {
            return null;
        }

        @Override
        public Bitmap MakeOutputImg(Object info, int outW, int outH) {
            return null;
        }

        @Override
        public Bitmap MakeOutputPendant(Object info, int outW, int outH) {
            return null;
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
        public void On3PosModify() {
            m_isChange = true;
        }

        @Override
        public void OnAllPosModify() {
            m_isChange = true;

            //修改了定点位置,做处理
            if(m_uiEnabled)
            {
                if(mChangePointCallback != null && m_orgBmp != null)
                {
                    ChangePointHandler.CmdMsg cmd = new ChangePointHandler.CmdMsg();
                    cmd.m_orgBmp = m_orgBmp;

                    m_mainHandler.m_queue.AddItem(cmd);
                    Message msg = m_mainHandler.obtainMessage();
                    msg.what = ChangePointHandler.MSG_CYC_QUEUE;
                    m_mainHandler.sendMessage(msg);
                }
            }
        }

        @Override
        public void UpdateSonWin(Bitmap bmp, int x, int y) {
            if(m_sonWin != null)
            {
                m_sonWin.SetData(bmp,x,y);
            }
        }

        @Override
        public void OnSelFaceIndex(int index) {

        }
    };


    /**
     * @param index CHECK_INDEX_LIP唇彩,CHECK_INDEX_EYE眼睛,CHECK_INDEX_EYEBROW眉毛,
     *              CHECK_INDEX_CHEEK脸颊
     */
    private void SetSelCheckClassList(int oldIndex,int index)
    {
        int outColor = 0xffffffff;
        int overColor = 0xff32bea0;
        float outSize = 14;
        float overSize = 17;
        switch(oldIndex)
        {
            case CHECK_INDEX_LIP:
                m_checkLipBtn.ClearChoose();
                break;
            case CHECK_INDEX_EYE_L:
                m_checkEyeBtnL.ClearChoose();
                break;
//            case CHECK_INDEX_EYE_R:
//                m_checkEyeBtnR.ClearChoose();
//                break;
            case CHECK_INDEX_EYEBROW:
                m_checkEyebrowBtn.ClearChoose();
                break;
            case CHECK_INDEX_CHEEK:
                m_checkCheekBtn.ClearChoose();
                break;
            default:
                break;
        }

        switch(index)
        {
            case CHECK_INDEX_LIP:
                m_checkLipBtn.SetChoose();
                break;
            case CHECK_INDEX_EYE_L:
                m_checkEyeBtnL.SetChoose();
                break;
//            case CHECK_INDEX_EYE_R:
//                m_checkEyeBtnR.SetChoose();
//                break;
            case CHECK_INDEX_EYEBROW:
                m_checkEyebrowBtn.SetChoose();
                break;
            case CHECK_INDEX_CHEEK:
                m_checkCheekBtn.SetChoose();
                break;
            default:
                break;
        }
    }



    private void SetSelCheckBarClass(int index)
    {
        Bitmap temp = null;
        switch(index)
        {
            case CHECK_INDEX_LIP:
                temp = BitmapFactory.decodeResource(getResources(),R.drawable.beautify_check_logo_lip);
                m_checkContent.setText(getResources().getString(R.string.makeup_changepoint_content1));
                break;
            case CHECK_INDEX_EYE_L:
//            case CHECK_INDEX_EYE_R:
                temp = BitmapFactory.decodeResource(getResources(), R.drawable.beautify_check_logo_eye);
                m_checkContent.setText(getResources().getString(R.string.makeup_chnagepoint_content2));
                break;
            case CHECK_INDEX_EYEBROW:
                temp = BitmapFactory.decodeResource(getResources(),R.drawable.beautify_check_logo_eyebrow);
                m_checkContent.setText(getResources().getString(R.string.makeup_changepoint_content3));
                break;
            case CHECK_INDEX_CHEEK:
                temp = BitmapFactory.decodeResource(getResources(), R.drawable.beautify_check_logo_cheek);
                m_checkContent.setText(getResources().getString(R.string.makeup_changepoint_content4));
                break;
            default:
                break;
        }

        if(temp != null)
        {
            ImageUtils.MakeHeadBmp(temp, temp.getWidth(), 0, 0);
            m_checkLogo.setImageBitmap(ImageUtils.MakeHeadBmp(temp, temp.getWidth(), 0, 0));
            if(temp != null)
            {
                temp = null;
            }
        }
    }

    @Override
    public void onClose()
    {
        m_uiEnabled = false;

        mChangePointCallback = null;
        if(m_imageThread != null)
        {
            m_imageThread.quit();
            m_imageThread = null;
        }

        TongJi2.EndPage(getContext(),getResources().getString(R.string.定点));
    }

    @Override
    public void onResume() {
        super.onResume();
        TongJi2.OnResume(getContext(),getResources().getString(R.string.定点));
    }

    @Override
    public void onPause() {
        super.onPause();
        TongJi2.OnPause(getContext(),getResources().getString(R.string.定点));
    }

    @Override
    public void onBack() {
        if(m_curSel == CHECK_TRHEE)
        {
//            m_onClickListener.onClick(m_checkBackBtn);
            m_onClickListenerNew.onAnimationClick(m_checkBackBtn);
        }
        else
        {
            m_onClickListenerNew.onAnimationClick(m_backBtn);
        }
    }

    public static class TitleItem extends FrameLayout
    {
        private int m_norRes = -1;
        private int m_hoverRes = -1;
        private ImageView m_img;
        private TextView m_title;
        private ImageView m_line;
        public TitleItem(Context context) {
            super(context);
        }

        public TitleItem(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public TitleItem(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public void SetData(int norRes,int hoverRes,String title)
        {
            m_norRes = norRes;
            m_hoverRes = hoverRes;
            LinearLayout main = new LinearLayout(getContext());
            main.setOrientation(LinearLayout.VERTICAL);
            FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            fl.gravity = Gravity.CENTER;
            main.setLayoutParams(fl);
            this.addView(main);

            m_img = new ImageView(getContext());
            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            ll.gravity = Gravity.CENTER_HORIZONTAL;
            m_img.setLayoutParams(ll);
            main.addView(m_img);
            if(norRes != -1)
            {
                m_img.setImageResource(norRes);
            }

            m_title = new TextView(getContext());
            ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            ll.gravity = Gravity.CENTER_HORIZONTAL;
            m_title.setLayoutParams(ll);
            m_title.setTextColor(Color.BLACK);
            m_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10f);
            main.addView(m_title);

            if(title != null && title.length() > 0)
            {
                m_title.setText(title);
            }

            m_line = new ImageView(getContext());
            fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,ShareData.PxToDpi_xhdpi(4));
            fl.gravity = Gravity.BOTTOM;
            m_line.setLayoutParams(fl);

            Bitmap lineBmp = Bitmap.createBitmap(1,ShareData.PxToDpi_xhdpi(4), Bitmap.Config.ARGB_8888);
            Canvas tempCanvas = new Canvas(lineBmp);
            Paint tempPaint = new Paint();
            tempPaint.setAntiAlias(true);
            tempCanvas.drawRect(new RectF(0,0,1,ShareData.PxToDpi_xhdpi(4)),tempPaint);
            ImageUtils.AddSkin(getContext(),lineBmp,0xffe75988);
            m_line.setImageBitmap(lineBmp);
            m_line.setScaleType(ImageView.ScaleType.FIT_XY);
            m_line.setVisibility(INVISIBLE);
            this.addView(m_line);

        }

        public void SetChoose()
        {
            if(m_img != null)
            {
                m_img.setImageResource(m_hoverRes);
                ImageUtils.AddSkin(getContext(),m_img,0xffe75988);
            }

            if(m_title != null)
            {
                m_title.setTextColor(ImageUtils.GetSkinColor(0xffe75988));
            }
            if(m_line != null)
            {
                m_line.setVisibility(VISIBLE);
            }
        }

        public void ClearChoose()
        {
            if(m_img != null)
            {
                ImageUtils.RemoveSkin(getContext(),m_img);
                m_img.setImageResource(m_norRes);
            }

            if(m_title != null)
            {
                m_title.setTextColor(Color.BLACK);
            }

            if(m_line != null)
            {
                m_line.setVisibility(INVISIBLE);
            }
        }
    }
}
