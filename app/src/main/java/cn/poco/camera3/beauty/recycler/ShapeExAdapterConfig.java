package cn.poco.camera3.beauty.recycler;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.recycleview.AbsConfig;
import cn.poco.tianutils.ShareData;

/**
 * @author lmx
 *         Created by lmx on 2017-12-08.
 */

public class ShapeExAdapterConfig extends AbsConfig
{
    public int def_open_sub_parent_offset_left;     //子列表展开，父item左边距
    public int def_open_sub_parent_left_margin;     //子列表展开，父item离子列表右边距

    public int def_title_top_margin;
    public int def_item_title_h;

    public int def_sub_item_w;
    public int def_sub_item_h;
    public int def_sub_item_title_top_margin;
    public int def_sub_top_padding;

    public int def_item_mask_icon_w;

    public ShapeSubItemDecoration def_shape_sub_item_decoration;
    public MLinearLayoutManager mLinearLayoutManager;

    public ShapeExAdapterConfig(Context context)
    {
        def_shape_sub_item_decoration = new ShapeSubItemDecoration();
        def_shape_sub_item_decoration.firstItemDivide = CameraPercentUtil.WidthPxToPercent(44 - 12 - 20);
        def_shape_sub_item_decoration.lastItemDivide = CameraPercentUtil.WidthPxToPercent(40 - 20);
        def_shape_sub_item_decoration.defaultItemDivide = CameraPercentUtil.WidthPxToPercent(62 - 20 * 2);
        mLinearLayoutManager = new MLinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
    }

    @Override
    public void InitData()
    {
        def_title_top_margin = CameraPercentUtil.HeightPxToPercent(4);
        def_item_title_h = CameraPercentUtil.HeightPxToPercent(28);

        def_item_w = CameraPercentUtil.WidthPxToPercent(124);
        def_item_h = CameraPercentUtil.HeightPxToPercent(124 + 14 + 28);
        def_item_l = CameraPercentUtil.WidthPxToPercent(30);
        def_parent_center_x = ShareData.m_screenRealWidth / 2;

        def_item_mask_icon_w = CameraPercentUtil.WidthPxToPercent(50);

        //recycler view 整体内边距
        def_parent_left_padding = CameraPercentUtil.WidthPxToPercent(34);
        def_parent_right_padding = def_parent_left_padding;
        // def_parent_top_padding = CameraPercentUtil.WidthPxToPercent((272 - 124 - 12 - 26) / 2);
        def_parent_top_padding = CameraPercentUtil.HeightPxToPercent(26 + 10 - 4);

        //108 - 68 = 40 , 40/2 = 20
        def_sub_item_w = CameraPercentUtil.WidthPxToPercent(108);
        // def_sub_item_w = CameraPercentUtil.WidthPxToPercent(68);
        def_sub_item_h = CameraPercentUtil.HeightPxToPercent(68 + 6 + 30);
        def_sub_item_title_top_margin = CameraPercentUtil.HeightPxToPercent(6);
        def_sub_top_padding = CameraPercentUtil.HeightPxToPercent(16);

        def_open_sub_parent_offset_left = CameraPercentUtil.WidthPxToPercent(10);
        def_open_sub_parent_left_margin = CameraPercentUtil.WidthPxToPercent(12);
    }

    @Override
    public void ClearAll()
    {
        def_shape_sub_item_decoration = null;
        mLinearLayoutManager = null;
    }

    public class MLinearLayoutManager extends LinearLayoutManager
    {
        public MLinearLayoutManager(Context context, int orientation, boolean reverseLayout)
        {
            super(context, orientation, reverseLayout);
        }

        private boolean canScroll = true;

        public void setCanScroll(boolean canScroll)
        {
            this.canScroll = canScroll;
        }

        @Override
        public boolean canScrollHorizontally()
        {
            return super.canScrollHorizontally() && canScroll;
        }
    }
}
