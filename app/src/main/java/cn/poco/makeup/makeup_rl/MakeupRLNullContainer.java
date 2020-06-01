package cn.poco.makeup.makeup_rl;

import android.content.Context;
import android.widget.FrameLayout;

import cn.poco.makeup.makeup_abs.BaseAlphaFrItem;
import cn.poco.makeup.makeup_abs.BaseItemWithAlphaFrMode;
import cn.poco.recycleview.AbsConfig;

public class MakeupRLNullContainer extends BaseAlphaFrItem {
    public MakeupRLNullContainer(Context context, AbsConfig config) {
        super(context, config);
    }

    @Override
    protected BaseItemWithAlphaFrMode initBaseItem() {
        return new MakeupRLNullItem(getContext(),m_config);
    }

    @Override
    protected FrameLayout initAlphaFr() {
        return null;
    }
}
