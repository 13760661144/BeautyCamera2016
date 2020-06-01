package cn.poco.makeup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

//彩妆广告显示框的布局
public class ADShowFr extends FrameLayout
{
    public static final int ADCLICKCLOSE = 1;
    public static final int ADCLICKMORE = 2;
    private Context m_context;
    private ADShowLayoutData m_data;
    public FrameLayout m_adFr;
    public ImageView m_adView;
    public ImageView m_adCancelBtn;
    public ImageView m_adMoreBtn;
    public ADFrOnClickCB m_cb;
    public ADShowFr(@NonNull Context context, ADShowLayoutData data, ADFrOnClickCB cb) {
        super(context);
        m_context = context;
        m_data = data;
        m_cb = cb;
        initUI();
    }

    public void initUI()
    {
        if(m_data != null)
        {
            this.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            FrameLayout.LayoutParams fl;
            if(m_data.m_imgShowHeight != 0 && m_data.m_imgShowWidth != 0)
            {
                m_adFr = new FrameLayout(getContext());
                fl = new FrameLayout.LayoutParams(m_data.m_imgShowWidth,m_data.m_imgShowHeight);
//                fl.gravity = Gravity.CENTER;
                if(fl.gravity != Gravity.CENTER)
                {
                    fl.gravity = m_data.m_gravity;
                    fl.leftMargin = m_data.m_imgShowLeftMargin;
                    fl.topMargin = m_data.m_imgShowTopMargin;
                }
                else
                {
                    fl.gravity = Gravity.CENTER;
                }
                m_adFr.setLayoutParams(fl);
                this.addView(m_adFr);
            }

            {
                m_adView = new ImageView(getContext());
                fl = new FrameLayout.LayoutParams(m_data.m_imgShowWidth,m_data.m_imgShowHeight);
                fl.gravity = Gravity.CENTER;
                m_adView.setLayoutParams(fl);
                m_adView.setScaleType(ImageView.ScaleType.CENTER);
                m_adFr.addView(m_adView);

                if(m_data.m_closeBtnWidth != 0 && m_data.m_closeBtnHeight != 0)
                {
                    m_adCancelBtn = new ImageView(getContext());
                    fl = new FrameLayout.LayoutParams(m_data.m_closeBtnWidth,m_data.m_closeBtnHeight);
                    fl.gravity = Gravity.TOP | Gravity.RIGHT;
                    fl.rightMargin = m_data.m_closeBtnRightMagin;
                    fl.topMargin = m_data.m_closeBtnTopMagin;
                    m_adCancelBtn.setLayoutParams(fl);
                    m_adFr.addView(m_adCancelBtn);
                    m_adCancelBtn.setOnClickListener(m_onclickLisener);
                }

                if(m_data.m_moreBtnWidth != 0 && m_data.m_moreBtnHeight != 0)
                {
                    m_adMoreBtn = new ImageView(getContext());
                    fl = new FrameLayout.LayoutParams(m_data.m_moreBtnWidth,m_data.m_closeBtnHeight);
                    fl.gravity = Gravity.LEFT | Gravity.TOP;
                    fl.topMargin = m_data.m_moreBtnTopMagin;
                    fl.leftMargin = m_data.m_moreBtnLeftMagin;
                    m_adMoreBtn.setLayoutParams(fl);
                    m_adFr.addView(m_adMoreBtn);
                    m_adMoreBtn.setOnClickListener(m_onclickLisener);
                }
            }
        }
    }

    public void setBGColor(int color)
    {
        if(color != 0)
        {
            this.setBackgroundColor(color);
        }
    }

    public void setImageRes(int res)
    {
        if(m_adView != null && res != -1)
        {
            m_adView.setImageResource(res);
        }
    }


    View.OnClickListener m_onclickLisener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(m_cb != null)
            {
                if(v == m_adCancelBtn)
                {
                    m_cb.OnClick(ADCLICKCLOSE);
                }
                else if(v == m_adMoreBtn)
                {
                    m_cb.OnClick(ADCLICKMORE);
                }
            }
        }
    };

    public static class ADShowLayoutData
    {
        public int m_closeBtnWidth;//关闭按钮的宽
        public int m_closeBtnHeight;//关闭按钮的高
        public int m_closeBtnRightMagin;//关闭按钮距离父view右边的距离
        public int m_closeBtnTopMagin;//关闭按钮距离父view顶部的距离

        public int m_moreBtnWidth;//了解更多按钮的宽
        public int m_moreBtnHeight;//了解更多按钮的高
        public int m_moreBtnLeftMagin;//了解更多按钮离父view左边的距离
        public int m_moreBtnTopMagin;//了解更多按钮离父view顶部的距离

        public int m_imgShowWidth;//图片展示的宽度
        public int m_imgShowHeight;//图片展示的高度

        public int m_imgShowLeftMargin;//图片距离屏幕左边的距离
        public int m_imgShowTopMargin;//图片距离屏幕顶部的距离

        public int m_gravity = Gravity.CENTER;//图片的对齐方式
    }

    public interface ADFrOnClickCB
    {
        public void OnClick(int flag);
    }
}
