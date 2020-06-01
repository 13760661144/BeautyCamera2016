package cn.poco.makeup.makeup1;

import cn.poco.makeup.makeup_abs.AbsAlphaConfig;
import cn.poco.tianutils.ShareData;


public class Makeup1ListConfig extends AbsAlphaConfig {

    @Override
    public void InitData() {
        def_parent_top_padding = ShareData.PxToDpi_xhdpi(10);
        def_parent_bottom_padding = ShareData.PxToDpi_xhdpi(10);
        def_item_l = ShareData.PxToDpi_xhdpi(12);
        def_item_w = ShareData.PxToDpi_xhdpi(110);
        def_item_h = ShareData.PxToDpi_xhdpi(140);
        def_sub_l = ShareData.PxToDpi_xhdpi(22);
        def_sub_padding_l = ShareData.PxToDpi_xhdpi(8);
        def_sub_padding_r = ShareData.PxToDpi_xhdpi(18);
        def_sub_w = ShareData.PxToDpi_xhdpi(110);
        def_sub_h = ShareData.PxToDpi_xhdpi(120);
        def_parent_left_padding = ShareData.PxToDpi_xhdpi(16);
        def_parent_right_padding = ShareData.PxToDpi_xhdpi(16);
        def_parent_center_x = (int) (ShareData.m_screenWidth/2f);

        m_alphaFr_Item_left = ShareData.PxToDpi_xhdpi(22);
    }

    @Override
    public void ClearAll() {

    }
}
