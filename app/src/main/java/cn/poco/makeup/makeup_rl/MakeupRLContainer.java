package cn.poco.makeup.makeup_rl;

import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;

import cn.poco.makeup.MySeekBar;
import cn.poco.makeup.makeup_abs.BaseAlphaFrItem;
import cn.poco.makeup.makeup_abs.BaseItemWithAlphaFrMode;
import cn.poco.recycleview.AbsConfig;
import cn.poco.tianutils.ShareData;


public class MakeupRLContainer extends BaseAlphaFrItem{
    public MakeupRLContainer(Context context, AbsConfig config) {
        super(context, config);
    }

    @Override
    protected BaseItemWithAlphaFrMode initBaseItem() {
        return new MakeupRLItem(getContext(),m_config);
    }

    @Override
    protected FrameLayout initAlphaFr() {
        FrameLayout alphaFr = new FrameLayout(getContext());
        MySeekBar seekBar = new MySeekBar(getContext());
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(480), LayoutParams.MATCH_PARENT);
        fl.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        fl.leftMargin = ShareData.PxToDpi_xhdpi(60);
        seekBar.setLayoutParams(fl);
        alphaFr.addView(seekBar);
        seekBar.setBackgroundColor(0x57000000);
        return alphaFr;
    }
}
