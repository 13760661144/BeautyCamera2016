package cn.poco.makeup.makeup1;


import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;

import cn.poco.makeup.MySeekBar;
import cn.poco.makeup.makeup_abs.BaseItemWithAlphaFrContainer;
import cn.poco.recycleview.AbsExConfig;
import cn.poco.recycleview.BaseGroup;
import cn.poco.recycleview.BaseItem;
import cn.poco.tianutils.ShareData;

public class Makeup1ListItem extends BaseItemWithAlphaFrContainer {
    public Makeup1ListItem(Context context, AbsExConfig config) {
        super(context, config);
    }

    @Override
    public FrameLayout initAlphaFr(Context context, AbsExConfig config, MySeekBar.OnProgressChangeListener cb) {
        FrameLayout alphaFr = new FrameLayout(getContext());
        MySeekBar seekBar = new MySeekBar(getContext());
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(480), LayoutParams.MATCH_PARENT);
        fl.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        fl.leftMargin = ShareData.PxToDpi_xhdpi(60);
        seekBar.setLayoutParams(fl);
        alphaFr.addView(seekBar);
        seekBar.setOnProgressChangeListener(cb);
        seekBar.setBackgroundColor(0x57000000);
        return alphaFr;
    }

    @Override
    public BaseGroup initGroupView() {
        return new Makeup1Group(getContext(),mConfig);
    }

    @Override
    public BaseItem initItemView() {
        return new Makeup1SubItem(getContext(), (Makeup1ListConfig) mConfig);
    }

}
