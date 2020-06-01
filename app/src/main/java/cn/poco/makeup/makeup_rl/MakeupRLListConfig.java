package cn.poco.makeup.makeup_rl;


import cn.poco.makeup.makeup_abs.AbsAlphaConfig;
import cn.poco.tianutils.ShareData;

public class MakeupRLListConfig extends AbsAlphaConfig {

    @Override
    public void InitData() {
        def_parent_top_padding = ShareData.PxToDpi_xhdpi(10);
        def_parent_bottom_padding = ShareData.PxToDpi_xhdpi(10);
        m_alphaFr_Item_left = ShareData.PxToDpi_xhdpi(20);
        def_item_l = ShareData.PxToDpi_xhdpi(17);
        def_item_w = ShareData.PxToDpi_xhdpi(110);
        def_item_h = ShareData.PxToDpi_xhdpi(140);
        def_parent_left_padding = ShareData.PxToDpi_xhdpi(17);
        def_parent_right_padding = ShareData.PxToDpi_xhdpi(17);
        def_parent_center_x = (int) (ShareData.m_screenWidth/2f);
    }

    @Override
    public void ClearAll() {

    }
}
