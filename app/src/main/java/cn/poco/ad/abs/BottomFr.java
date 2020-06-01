package cn.poco.ad.abs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cn.poco.advanced.ImageUtils;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

public class BottomFr extends LinearLayout
{
    public FrameLayout m_bottomBar;
    public ImageView m_backBtn;
    public ImageView m_okBtn;
    protected FrameLayout m_bottomList;
    protected int m_bottomListHeight = ShareData.PxToDpi_xhdpi(232);
    protected int m_bottomBarHeight = ShareData.PxToDpi_xhdpi(88);
    protected ClickCallBack m_cb;
    protected boolean m_uiEnable = true;
    public BottomFr(@NonNull Context context) {
        super(context);
        initUI();
    }

    public void setClickCallBack(ClickCallBack clickCallBack)
    {
        m_cb = clickCallBack;
    }

    public void setUIEnable(boolean flag)
    {
        m_uiEnable = flag;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(!m_uiEnable)
        {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void initUI()
    {
        this.setOrientation(LinearLayout.VERTICAL);
        FrameLayout.LayoutParams fl;
        m_bottomBar = new FrameLayout(getContext());
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,m_bottomBarHeight);
        m_bottomBar.setLayoutParams(ll);
        m_bottomBar.setBackgroundColor(0xe6ffffff);
        this.addView(m_bottomBar);
        {
            m_backBtn = new ImageView(getContext());
            m_backBtn.setImageResource(R.drawable.beautify_cancel);
            fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            fl.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
            fl.leftMargin = ShareData.PxToDpi_xhdpi(22);
            m_backBtn.setLayoutParams(fl);
            m_backBtn.setOnClickListener(m_onclickListener);
            m_bottomBar.addView(m_backBtn);

            m_okBtn = new ImageView(getContext());
            m_okBtn.setImageResource(R.drawable.beautify_ok);
            fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            fl.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
            fl.rightMargin = ShareData.PxToDpi_xhdpi(24);
            m_okBtn.setLayoutParams(fl);
            m_okBtn.setOnClickListener(m_onclickListener);
            m_bottomBar.addView(m_okBtn);
            ImageUtils.AddSkin(getContext(),m_okBtn);

            m_bottomList = new FrameLayout(getContext());
            ll = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,m_bottomListHeight);
            m_bottomList.setLayoutParams(ll);
            this.addView(m_bottomList);
        }
    }

    private View.OnClickListener m_onclickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(m_cb != null)
            {
                if (v == m_okBtn)
                {
                    m_cb.onOk();
                }
                else if (v == m_backBtn)
                {
                    m_cb.onCancel();
                }
            }
        }
    };

    public interface ClickCallBack
    {
        public void onOk();
        public void onCancel();
    }
}
