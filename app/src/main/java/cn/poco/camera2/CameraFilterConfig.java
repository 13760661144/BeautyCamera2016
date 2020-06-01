package cn.poco.camera2;

import cn.poco.filter4.recycle.FilterConfig;
import cn.poco.tianutils.ShareData;

/**
 * 适应固定高度config
 *
 * @author lmx
 *         Created by lmx on 2017/6/14.
 */

public class CameraFilterConfig extends FilterConfig
{
    @Override
    public void InitData()
    {
        def_item_w = ShareData.PxToDpi_xhdpi(146);
        def_item_h = ShareData.PxToDpi_xhdpi(187);
        def_sub_w = ShareData.PxToDpi_xhdpi(127);
        def_sub_h = ShareData.PxToDpi_xhdpi(164);                                            //37
        def_item_l = ShareData.PxToDpi_xhdpi(17);
        def_sub_l = ShareData.PxToDpi_xhdpi(17);

        def_parent_center_x = ShareData.m_screenWidth / 2;                                  //recycleView的中心点     用户setSelect直接跳转位置确定
        def_parent_right_padding = ShareData.PxToDpi_xhdpi(22);
        def_parent_left_padding = ShareData.PxToDpi_xhdpi(22);
        def_parent_bottom_padding = ShareData.PxToDpi_xhdpi(22);
        def_parent_top_padding = def_parent_bottom_padding;

        def_head_w = ShareData.PxToDpi_xhdpi(109) - def_parent_left_padding - def_item_l;
        def_alphafr_leftMargin = ShareData.PxToDpi_xhdpi(20);
    }

    @Override
    public void ClearAll()
    {
        super.ClearAll();
    }
}
