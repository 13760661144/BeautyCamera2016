package cn.poco.ad66;

import android.content.Context;
import android.support.annotation.NonNull;

import cn.poco.recycleview.AbsConfig;
import cn.poco.tianutils.ShareData;

public class AD66BottomFr2 extends AD66BottomFr
{

    public AD66BottomFr2(@NonNull Context context) {
        super(context);
    }

    @Override
    public AbsConfig getConfig() {
        return new AbsConfig() {
            @Override
            public void InitData() {
                def_item_w = ShareData.PxToDpi_xhdpi(120);
                def_item_h = ShareData.PxToDpi_xhdpi(154);
                def_parent_center_x = (int) (ShareData.m_screenWidth/2f);
            }

            @Override
            public void ClearAll() {

            }
        };
    }
}
