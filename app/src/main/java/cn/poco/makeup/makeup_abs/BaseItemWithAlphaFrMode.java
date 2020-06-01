package cn.poco.makeup.makeup_abs;


import android.content.Context;
import android.support.annotation.NonNull;

import cn.poco.recycleview.BaseItem;

public abstract class BaseItemWithAlphaFrMode extends BaseItem implements IAlphaMode{
    private boolean m_isSelect;
    public BaseItemWithAlphaFrMode(@NonNull Context context) {
        super(context);
    }


    @Override
    public void onSelected() {
        m_isSelect = true;
    }

    @Override
    public void onUnSelected() {
        m_isSelect = false;
    }

    public boolean IsSelected()
    {
        return m_isSelect;
    }
}
