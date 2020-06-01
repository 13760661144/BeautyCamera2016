package cn.poco.filter4.recycle;

import cn.poco.recycleview.AbsExConfig;
import cn.poco.tianutils.ShareData;

/**
 * Created by lgd on 2017/5/26.
 */

public class FilterConfig extends AbsExConfig
{
	public boolean isCamera = false;
	public int def_head_w;
	public int def_alphafr_leftMargin;

	public int def_original_bk_color = -1;

	@Override
	public void InitData()
	{
		def_item_w = ShareData.PxToDpi_xhdpi(146);
		def_item_h = ShareData.PxToDpi_xhdpi(187);
		def_sub_w = ShareData.PxToDpi_xhdpi(127);
		def_sub_h = ShareData.PxToDpi_xhdpi(164);  //37
		def_item_l = ShareData.PxToDpi_xhdpi(17);
		def_sub_l = ShareData.PxToDpi_xhdpi(17);
		def_parent_center_x = ShareData.m_screenWidth / 2;
		def_parent_right_padding = ShareData.PxToDpi_xhdpi(22);
		def_parent_left_padding = ShareData.PxToDpi_xhdpi(22);
		def_parent_bottom_padding = ShareData.PxToDpi_xhdpi(22);       // (232-188)/2

		def_head_w = ShareData.PxToDpi_xhdpi(109)-def_parent_left_padding-def_item_l;

		def_alphafr_leftMargin = ShareData.PxToDpi_xhdpi(20);
	}

	@Override
	public void ClearAll()
	{

	}
}
