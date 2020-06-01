package cn.poco.widget.recycle;

import cn.poco.recycleview.AbsExConfig;
import cn.poco.tianutils.ShareData;

/**
 * Created by lgd on 2017/5/9.
 */

public class RecommendExConfig extends AbsExConfig
{
	@Override
	public void InitData()
	{
		def_item_w = ShareData.PxToDpi_xhdpi(146);
		def_item_h = ShareData.PxToDpi_xhdpi(187);
		def_sub_w = ShareData.PxToDpi_xhdpi(137);
		def_sub_h = ShareData.PxToDpi_xhdpi(137);
		def_item_l = ShareData.PxToDpi_xhdpi(17);
		def_sub_l = ShareData.PxToDpi_xhdpi(17);
		def_parent_center_x = ShareData.m_screenWidth / 2;
		def_parent_left_padding = ShareData.PxToDpi_xhdpi(22);
		def_parent_right_padding = def_parent_left_padding ;
		def_parent_bottom_padding = ShareData.PxToDpi_xhdpi(22);       // (232-188)/2
		def_parent_top_padding = def_parent_bottom_padding;

	}

	@Override
	public void ClearAll()
	{

	}
}
