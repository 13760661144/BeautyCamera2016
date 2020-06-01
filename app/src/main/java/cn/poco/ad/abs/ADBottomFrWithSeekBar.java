package cn.poco.ad.abs;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.widget.FrameLayout;

import cn.poco.home.home4.utils.PercentUtil;
import cn.poco.makeup.MySeekBar;

public class ADBottomFrWithSeekBar extends BottomFr {
    private MySeekBar m_seekBar;
    public ADBottomFrWithSeekBar(@NonNull Context context,AllCallBack cb) {
        super(context);
        m_cb = cb;
        addUI();
    }

    private void addUI()
    {
        m_seekBar = new MySeekBar(getContext());
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
        fl.leftMargin = PercentUtil.WidthPxToPercent(80);
        fl.rightMargin = PercentUtil.WidthPxToPercent(80);
        m_seekBar.setLayoutParams(fl);
        m_seekBar.setBackgroundColor(0x57000000);
        m_seekBar.setOnProgressChangeListener(m_onProgressChangeListener);
        m_bottomList.addView(m_seekBar);
        if(m_cb != null)
        {
            m_seekBar.setProgress(((AllCallBack)m_cb).getInitProgressValue());
        }
    }

    public void setSeekBarProgress(int value)
    {
        m_seekBar.setProgress(value);
    }

    private MySeekBar.OnProgressChangeListener m_onProgressChangeListener = new MySeekBar.OnProgressChangeListener() {
        @Override
        public void onProgressChanged(MySeekBar seekBar, int progress) {
            if(m_cb != null)
            {
                ((AllCallBack)m_cb).onProgressChanged(seekBar,progress);
            }
        }

        @Override
        public void onStartTrackingTouch(MySeekBar seekBar) {
            if(m_cb != null)
            {
                ((AllCallBack)m_cb).onStartTrackingTouch(seekBar);
            }
        }

        @Override
        public void onStopTrackingTouch(MySeekBar seekBar) {
            if(m_cb != null)
            {
                ((AllCallBack)m_cb).onStopTrackingTouch(seekBar);
            }
        }
    };

    public interface AllCallBack extends BottomFr.ClickCallBack
    {
        public void onProgressChanged(MySeekBar seekBar, int progress);

        public void onStartTrackingTouch(MySeekBar seekBar);

        public void onStopTrackingTouch(MySeekBar seekBar);

        public int getInitProgressValue();
    }
}
