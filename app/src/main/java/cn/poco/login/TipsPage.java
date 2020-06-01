package cn.poco.login;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.home.home4.Home4Page;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.DrawableUtils;
import my.beautyCamera.R;

public class TipsPage extends FrameLayout{


    protected LinearLayout m_centerLayout;
    protected TextView m_tips;
    protected TextView m_okBtn;
    protected TextView m_cancelBtn;
    protected TipsonClickListener m_cb;
    protected FrameLayout m_parent;
    public TipsPage(Context context) {
        super(context);
        init();
    }

    public TipsPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TipsPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        m_centerLayout = new LinearLayout(getContext());
        m_centerLayout.setOrientation(LinearLayout.VERTICAL);
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(569), LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.CENTER;
        m_centerLayout.setLayoutParams(fl);
        this.addView(m_centerLayout);
        m_centerLayout.setBackgroundDrawable(DrawableUtils.shapeDrawable(Color.WHITE,ShareData.PxToDpi_xhdpi(30)));
//        m_centerLayout.setBackgroundResource(R.drawable.login_tips_ok_bk);

        m_tips = new TextView(getContext());
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(450),ShareData.PxToDpi_xhdpi(185));
        ll.gravity = Gravity.CENTER_HORIZONTAL;
        m_tips.setLayoutParams(ll);
        m_centerLayout.addView(m_tips);
        m_tips.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16f);
        m_tips.setTextColor(0xff333333);
        m_tips.setGravity(Gravity.CENTER);

        m_okBtn = new TextView(getContext());
        ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(418),ShareData.PxToDpi_xhdpi(78));
        ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
//        ll.topMargin = ShareData.PxToDpi_xhdpi(45);
        m_okBtn.setLayoutParams(ll);
        m_centerLayout.addView(m_okBtn);
        m_okBtn.setTextColor(0xffffffff);
        m_okBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP,14f);
        m_okBtn.setText(getContext().getResources().getString(R.string.tipspage_logintips));
        m_okBtn.getPaint().setFakeBoldText(true);
        Bitmap bk = BitmapFactory.decodeResource(getResources(),R.drawable.login_tips_btn_bg);
        bk = ImageUtils.AddSkin(getContext(),bk);
        m_okBtn.setBackgroundDrawable(new BitmapDrawable(bk));
        m_okBtn.setGravity(Gravity.CENTER);
        m_okBtn.setOnClickListener(m_listener);

        m_cancelBtn = new TextView(getContext());
        ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        ll.topMargin = ShareData.PxToDpi_xhdpi(20);
        ll.bottomMargin = ShareData.PxToDpi_xhdpi(20);
        m_cancelBtn.setLayoutParams(ll);
        m_centerLayout.addView(m_cancelBtn);
        m_cancelBtn.setTextColor(0xff999999);
        m_cancelBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13f);
        m_cancelBtn.getPaint().setFakeBoldText(true);
        m_cancelBtn.setText(getContext().getResources().getString(R.string.tipspage_cancel));
        m_cancelBtn.setGravity(Gravity.CENTER);
        m_cancelBtn.setOnClickListener(m_listener);

        fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        this.setLayoutParams(fl);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    View.OnClickListener m_listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
           if(v == m_okBtn)
            {
                TipsPage.this.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(m_parent != null)
                        {
                            TipsPage.this.setVisibility(GONE);
                            TipsPage.this.setBackgroundDrawable(null);
                            m_parent.removeView(TipsPage.this);
                            TipsPage.this.removeAllViews();
                        }
                    }
                },0);
                if(m_cb != null)
                {
                    LoginAllAnim.ReSetLoginAnimData();
                    m_cb.onclickOk();
                }
            }
            else if(v == m_cancelBtn)
            {
//                resetAnim();
                TipsPage.this.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(m_parent != null)
                        {
                            TipsPage.this.setVisibility(GONE);
                            m_parent.removeView(TipsPage.this);
                        }
                    }
                },0);
                if(m_cb != null)
                {
                    m_cb.onclickCancel();
                }
            }
        }
    };

    public void dimissPage() {
        if (m_listener != null)
        {
            m_listener.onClick(m_cancelBtn);
        }
    }


    public void SetText(String content,String okText,String cancelText,int textHeight)
    {
        if(m_tips != null)
        {
            if(textHeight != -1)
            {
                LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) m_tips.getLayoutParams();
                ll.height = textHeight;
                m_tips.setLayoutParams(ll);
            }
            m_tips.setText(content);
        }
        if(m_okBtn != null)
        {
            m_okBtn.setText(okText);
        }

        if(m_cancelBtn != null)
        {
            m_cancelBtn.setText(cancelText);
        }

        if(okText == null)
        {
            if(m_okBtn != null)
            {
                m_okBtn.setVisibility(GONE);
            }
        }

        if(cancelText == null)
        {
            if(m_cancelBtn != null)
            {
                m_cancelBtn.setVisibility(GONE);
            }
        }
    }


    public void SetBackgroundBk(Bitmap bmp)
    {
        if(bmp != null)
        {
            this.setBackgroundDrawable(new BitmapDrawable(bmp));
        }
        else
        {
            if(Home4Page.s_maskBmpPath != null)
            {
                this.setBackgroundDrawable(new BitmapDrawable(cn.poco.imagecore.Utils.DecodeFile(Home4Page.s_maskBmpPath,null)));
            }
            else
            {
                this.setBackgroundResource(R.drawable.login_tips_all_bk);
            }
        }
    }

    public void showTips(FrameLayout parent,TipsonClickListener cb)
    {
        m_parent = parent;
        m_parent.addView(this);
        if(cb != null)
        m_cb = cb;
//        startAnim();
    }


    public void startAnim()
    {
        ObjectAnimator animator = ObjectAnimator.ofFloat(TipsPage.this,"translationY",-ShareData.m_screenHeight,0);
        animator.setDuration(300);
        animator.start();
    }


    private void resetAnim()
    {
        ObjectAnimator animator = ObjectAnimator.ofFloat(TipsPage.this,"translationY",0,-ShareData.m_screenHeight);
        animator.setDuration(300);
        animator.start();
    }

    public void ClearAll()
    {
        this.removeAllViews();
    }


    public interface TipsonClickListener
    {
        public void onclickOk();

        public void onclickCancel();
    }

}
