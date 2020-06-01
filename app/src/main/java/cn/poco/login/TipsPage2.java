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

public class TipsPage2 extends FrameLayout{

    protected LinearLayout m_centerLayout;
    protected TextView m_title;
    protected TextView m_tips;
    protected TextView m_okBtn;
    protected TextView m_cancelBtn;
    protected TipsonClickListener m_cb;
    protected FrameLayout m_parent;
    public TipsPage2(Context context) {
        super(context);
        init();
    }

    public TipsPage2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TipsPage2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        m_centerLayout = new LinearLayout(getContext());
        m_centerLayout.setOrientation(LinearLayout.VERTICAL);
        LayoutParams fl = new LayoutParams(ShareData.PxToDpi_xhdpi(569), LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.CENTER;
        m_centerLayout.setLayoutParams(fl);
        this.addView(m_centerLayout);
        m_centerLayout.setBackgroundDrawable(DrawableUtils.shapeDrawable(Color.WHITE,ShareData.PxToDpi_xhdpi(30)));
//        m_centerLayout.setBackgroundResource(R.drawable.login_tips_ok_bk);

        m_title = new TextView(getContext());
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.gravity = Gravity.CENTER_HORIZONTAL;
        ll.topMargin = ShareData.PxToDpi_xhdpi(30);
        m_title.setLayoutParams(ll);
        m_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP,17.5f);
        m_title.setTextColor(0xff333333);
        m_centerLayout.addView(m_title);

        m_tips = new TextView(getContext());
        ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(460),LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.gravity = Gravity.CENTER_HORIZONTAL;
        ll.topMargin = ShareData.PxToDpi_xhdpi(20);
        ll.bottomMargin = ShareData.PxToDpi_xhdpi(45);
        m_tips.setLayoutParams(ll);
        m_centerLayout.addView(m_tips);
        m_tips.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15f);
        m_tips.setTextColor(0xff333333);
        m_tips.setGravity(Gravity.CENTER);

        m_okBtn = new TextView(getContext());
        ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(418),ShareData.PxToDpi_xhdpi(78));
        ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
//        ll.topMargin = ShareData.PxToDpi_xhdpi(45);
        ll.bottomMargin = ShareData.PxToDpi_xhdpi(45);
        m_okBtn.setLayoutParams(ll);
        m_centerLayout.addView(m_okBtn);
        m_okBtn.setTextColor(0xffffffff);
        m_okBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP,14f);
        m_okBtn.setText(getContext().getResources().getString(R.string.tipspage2_sure));
        m_okBtn.getPaint().setFakeBoldText(true);
        Bitmap bk = BitmapFactory.decodeResource(getResources(),R.drawable.login_tips_btn_bg);
        bk = ImageUtils.AddSkin(getContext(),bk);
        m_okBtn.setBackgroundDrawable(new BitmapDrawable(bk));
        m_okBtn.setGravity(Gravity.CENTER);
        m_okBtn.setOnClickListener(m_listener);

        fl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        this.setLayoutParams(fl);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    OnClickListener m_listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
           if(v == m_okBtn)
            {
                TipsPage2.this.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(m_parent != null)
                        {
                            TipsPage2.this.setVisibility(GONE);
                            TipsPage2.this.setBackgroundDrawable(null);
                            TipsPage2.this.removeAllViews();
                            m_parent.removeView(TipsPage2.this);
                        }
                    }
                },0);
                if(m_cb != null)
                {
                    m_cb.onclickOk();
                }
            }
            else if(v == m_cancelBtn)
            {
//                resetAnim();
                TipsPage2.this.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(m_parent != null)
                        {
                            TipsPage2.this.setVisibility(GONE);
                            m_parent.removeView(TipsPage2.this);
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


    public void SetText(String content,String okText,String titleText)
    {
        if(m_tips != null && content != null && content.length() > 0)
        {
            m_tips.setText(content);
        }
        if(m_okBtn != null && okText != null && okText.length() > 0)
        {
            m_okBtn.setText(okText);
        }

        if(m_title != null &&titleText != null && titleText.length() > 0)
        {
            m_title.setText(titleText);
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
        ObjectAnimator animator = ObjectAnimator.ofFloat(TipsPage2.this,"translationY",-ShareData.m_screenHeight,0);
        animator.setDuration(300);
        animator.start();
    }


    private void resetAnim()
    {
        ObjectAnimator animator = ObjectAnimator.ofFloat(TipsPage2.this,"translationY",0,-ShareData.m_screenHeight);
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
