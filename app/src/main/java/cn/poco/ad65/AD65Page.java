package cn.poco.ad65;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.AppCompatImageView;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.ad.abs.ADAbsAdapter;
import cn.poco.ad.abs.ADAbsBottomFrWithRY;
import cn.poco.ad.abs.ADBottomFrWithSeekBar;
import cn.poco.ad65.imp.IAD65UI;
import cn.poco.display.CoreViewV3;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.makeup.MySeekBar;
import cn.poco.makeup.SeekBarTipsView;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.Utils;
import cn.poco.utils.WaitAnimDialog;
import cn.poco.view.beauty.BeautyCommonViewEx;
import cn.poco.view.beauty.MakeUpViewEx;
import my.beautyCamera.R;

//佰草集
public class AD65Page extends IPage implements IAD65UI {
    public final static String KEYVALUE_BACK = "isBack";
    public final static String KEYVALUE_FRAMEINDEX = "frameIndex";
    public final static String KEYVALUE_PROGRESS = "progress";
    public final static int BEAUTY_PAGE = 1;
    public final static int FRAME_PAGE = 3;
    private int m_curPage = BEAUTY_PAGE;
    private int m_frW;
    private int m_frH;
    private MakeUpViewEx m_beautyView;
    private ADBottomFrWithSeekBar m_bottomFr1;
    private AD65BottomFr2 m_bottomFr2;
    private AD66FrameView m_frameView;
    private boolean m_uiEnable = true;
    private AD65Presenter m_presenter;
    private WaitAnimDialog m_waitDlg;
    private SeekBarTipsView m_seekBarTips;
    private ImageView m_compareBtn;
    private FrameLayout m_mainFr;
    private boolean m_isBack = false;
    private int m_curFrameIndex = -1;
    public AD65Page(Context context, BaseSite site) {
        super(context, site);
        m_presenter = new AD65Presenter(site,getContext(),this);
        initData();
        initUI();
    }

    private void initData()
    {
        m_frW = ShareData.m_screenWidth;
        m_frH = (int) (m_frW*4f/3f);
    }

    private void initUI()
    {
        m_mainFr = new FrameLayout(getContext());
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(m_frW,m_frH);
        fl.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        m_mainFr.setLayoutParams(fl);
        this.addView(m_mainFr);

        m_beautyView = new MakeUpViewEx(getContext(),null);
        fl = new FrameLayout.LayoutParams(m_frW + 4,m_frH + 4);
        fl.gravity = Gravity.CENTER;
        m_beautyView.setLayoutParams(fl);
        m_beautyView.setMode(BeautyCommonViewEx.MODE_NORMAL);
        m_mainFr.addView(m_beautyView);

        m_frameView = new AD66FrameView(getContext(),m_frW + 4,m_frH + 4);
        fl = new FrameLayout.LayoutParams(m_frW + 4,m_frH + 4);
        fl.gravity = Gravity.CENTER;
        m_frameView.setLayoutParams(fl);
        m_frameView.InitData(m_coreViewCB);
        m_frameView.SetOperateMode(CoreViewV3.MODE_FRAME);
        m_frameView.SetBkColor(0xffffffff);
        m_mainFr.addView(m_frameView);
        m_frameView.setVisibility(GONE);

        m_bottomFr1 = new ADBottomFrWithSeekBar(getContext(),m_bottomFr1CB);
        fl = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        m_bottomFr1.setLayoutParams(fl);
        this.addView(m_bottomFr1);

        m_bottomFr2 = new AD65BottomFr2(getContext());
        fl = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        m_bottomFr2.setLayoutParams(fl);
        this.addView(m_bottomFr2);
        m_bottomFr2.setClickCallBack(m_bottomFr2CB);
        m_bottomFr2.setVisibility(GONE);

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
                        m_beautyView.setImage(m_presenter.getOrgBmp());
                    }
                    break;
                    case MotionEvent.ACTION_UP:
                        if(m_presenter.getCurBmp() != null && !m_presenter.getCurBmp().isRecycled())
                        {
                            m_beautyView.setImage(m_presenter.getCurBmp());
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


        m_waitDlg = new WaitAnimDialog((Activity)getContext());
    }


    private AnimationDialog2 m_animDialog;
    public void showAnim() {
        if(m_animDialog != null)
        {
            m_animDialog.dismiss();
            m_animDialog = null;
        }
        m_uiEnable = false;
        m_animDialog = new AnimationDialog2((Activity) getContext(), new AnimationDialog2.Callback() {
            @Override
            public void OnClick() {

            }

            @Override
            public void OnAnimationEnd() {
                m_animDialog.dismiss();
                m_animDialog = null;
                m_uiEnable = true;
                m_presenter.changeEffectByProgress(m_presenter.getProgress());
            }
        });

        ArrayList<AnimationDialog2.AnimFrameData> datas = new ArrayList<>();
//        datas.add(new AnimationDialog2.AnimFrameData(R.drawable.ad65_anim1,400,false));
//        datas.add(new AnimationDialog2.AnimFrameData(R.drawable.ad65_anim2,400,false));
//        datas.add(new AnimationDialog2.AnimFrameData(R.drawable.ad65_anim3,400,false));
//        datas.add(new AnimationDialog2.AnimFrameData(R.drawable.ad65_anim4,400,false));
//        datas.add(new AnimationDialog2.AnimFrameData(R.drawable.ad65_anim5,400,false));
//        datas.add(new AnimationDialog2.AnimFrameData(R.drawable.ad65_anim6,400,false));
//        datas.add(new AnimationDialog2.AnimFrameData(R.drawable.ad65_anim7,1000,false));
        m_animDialog.SetGravity(Gravity.CENTER_HORIZONTAL);
        m_animDialog.SetTopMargin((int) ((m_frH - ShareData.PxToDpi_xhdpi(620))/2f));
        m_animDialog.SetData_xhdpi(datas);
        m_animDialog.show();
    }

    private void SwithUIByPage(int page)
    {
        switch (page)
        {
            case BEAUTY_PAGE:
            {
                SetViewVisibility(m_frameView,false);
                SetViewVisibility(m_bottomFr2,false);
                SetViewVisibility(m_beautyView,true);
                SetViewVisibility(m_bottomFr1,true);
                SetViewVisibility(m_compareBtn,true);
                break;
            }
            case FRAME_PAGE:
            {
                SetViewVisibility(m_beautyView,false);
                SetViewVisibility(m_bottomFr1,false);
                SetViewVisibility(m_compareBtn,false);
                SetViewVisibility(m_frameView,true);
                SetViewVisibility(m_bottomFr2,true);
                break;
            }
        }
    }

    public void SetViewVisibility(View view,boolean isShow)
    {
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

    private ADBottomFrWithSeekBar.AllCallBack m_bottomFr1CB = new ADBottomFrWithSeekBar.AllCallBack() {
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
            m_presenter.changeEffectByProgress(seekBar.getProgress());
        }

        @Override
        public int getInitProgressValue() {
            return m_presenter.getProgress();
        }

        @Override
        public void onOk() {
            onClickOk();
        }

        @Override
        public void onCancel() {
            onClickCancel();
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

    private ADAbsBottomFrWithRY.ClickCallBack m_bottomFr2CB = new ADAbsBottomFrWithRY.ClickCallBack() {
        @Override
        public void onItemClick(Object object, int index) {
            m_curFrameIndex = index;
            if(object instanceof ADAbsAdapter.ADNullItem)
            {
                m_frameView.SetFrame2(null,true);
                m_frameView.UpdateUI();
            }
            else
            {
                if(index == 0)
                {
                    Utils.UrlTrigger(getContext(), "http://cav.adnonstop.com/cav/fe0a01a3d9/0072702324/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
                }
                else if(index == 1)
                {
                    Utils.UrlTrigger(getContext(), "http://cav.adnonstop.com/cav/fe0a01a3d9/0072702430/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
                }
                m_frameView.SetFrame(null,m_presenter.getFrameRes(index),null,true);
                m_frameView.UpdateUI();
            }
        }

        @Override
        public void onOk() {
            onClickOk();
        }

        @Override
        public void onCancel() {
            onClickCancel();
        }
    };

    private void onClickOk()
    {
        if(m_uiEnable)
        {
            if(m_curPage == BEAUTY_PAGE)
            {
                m_presenter.onOk();
                m_curPage = FRAME_PAGE;
                SwithUIByPage(m_curPage);
                m_frameView.CreateViewBuffer();
                m_frameView.SetImg(null,m_presenter.getCurBmp());
                m_frameView.UpdateUI();
            }
            else if(m_curPage == FRAME_PAGE)
            {
                m_presenter.onOk();
            }
        }
    }

    private void onClickCancel()
    {
        if(m_uiEnable)
        {
            if(m_curPage == BEAUTY_PAGE)
            {
                m_presenter.onBack();
            }
            else if(m_curPage == FRAME_PAGE)
            {
                m_presenter.onBack();
                m_curPage = BEAUTY_PAGE;
                SwithUIByPage(m_curPage);
                m_beautyView.setImage(m_presenter.getCurBmp());
                m_bottomFr1.setSeekBarProgress(m_presenter.getProgress());
            }
        }
    }

    @Override
    public void SetData(HashMap<String, Object> params) {
        if(params != null)
        {
            if(params.get(KEYVALUE_BACK) != null)
            {
                m_isBack = (boolean) params.get(KEYVALUE_BACK);
            }
            if (params.get("imgs") != null) {
                m_presenter.setImage(params.get("imgs"));
                if(!m_isBack)
                {
                    showAnim();
                }
                else
                {
                    if(params.get(KEYVALUE_PROGRESS) != null)
                    {
                        m_presenter.changeEffectByProgress((Integer) params.get(KEYVALUE_PROGRESS));
                    }
                    if(params.get(KEYVALUE_FRAMEINDEX) != null)
                    {
                        m_bottomFr2.SetselectIndex((Integer) params.get(KEYVALUE_FRAMEINDEX));
                    }
                    m_curPage = FRAME_PAGE;
                SwithUIByPage(m_curPage);
                m_frameView.CreateViewBuffer();
                m_frameView.SetImg(null,m_presenter.getOrgBmp());
                m_frameView.UpdateUI();
                }
            } else {
                RuntimeException ex = new RuntimeException("MyLog--Input params is null!");
                throw ex;
            }
        }
    }

    @Override
    public void onBack() {
        onClickCancel();
    }

    @Override
    public void onClose() {
        m_presenter.clear();
    }

    @Override
    public void updateBmp(Bitmap bmp) {
        if(bmp != null)
        {
            if(m_curPage == BEAUTY_PAGE)
            {
                m_beautyView.setImage(bmp);
            }
            else if(m_curPage == FRAME_PAGE)
            {
                m_frameView.SetImg(null,bmp);
                m_frameView.UpdateUI();
            }
        }
    }

    @Override
    public void dismissWaitDlg() {
        SetWaitUI(false,"");
    }

    @Override
    public void showWaitDlg() {
        SetWaitUI(true,"");
    }

    @Override
    public int getCurPage() {
        return m_curPage;
    }

    @Override
    public Bitmap getOutputBmp() {
        return m_frameView.GetOutputBmp();
    }

    @Override
    public int getCurFrameIndex() {
        return m_curFrameIndex;
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

    private CoreViewV3.ControlCallback m_coreViewCB = new CoreViewV3.ControlCallback() {
        @Override
        public Bitmap MakeShowImg(Object info, int frW, int frH) {
            return null;
        }

        @Override
        public Bitmap MakeOutputImg(Object info, int outW, int outH) {
            return null;
        }

        @Override
        public Bitmap MakeShowFrame(Object info, int frW, int frH) {
            return null;
        }

        @Override
        public Bitmap MakeOutputFrame(Object info, int outW, int outH) {
            return null;
        }

        @Override
        public Bitmap MakeShowBK(Object info, int frW, int frH) {
            return null;
        }

        @Override
        public Bitmap MakeOutputBK(Object info, int outW, int outH) {
            return null;
        }

        @Override
        public Bitmap MakeShowPendant(Object info, int frW, int frH) {
            return null;
        }

        @Override
        public Bitmap MakeOutputPendant(Object info, int outW, int outH) {
            return null;
        }

        @Override
        public void SelectPendant(int index) {

        }
    };
}
