package cn.poco.advanced;

import android.content.Context;

import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * 用于毛玻璃和马赛克
 */
public class RecommendItemConfig2 extends RecommendItemConfig
{
	public RecommendItemConfig2(Context context, boolean isShowTitle)
	{
		super(context, isShowTitle);

//		int numOffsetW = 0;
//		int numOffsetH = 0;
//		def_item_width = ShareData.PxToDpi_xhdpi(160) + numOffsetW;
//		def_item_height = ShareData.PxToDpi_xhdpi(206) + numOffsetH;
//		def_img_color = 0x80FFFFFF;
//		def_bk_over_color = ImageUtils.GetSkinColor(0x66e75988);
//		def_bk_out_color = 0x66ffffff;
//		def_item_left = ShareData.PxToDpi_xhdpi(12);
//		//def_item_left = ShareData.PxToDpi_xhdpi(6);
//		//def_item_right = def_item_left;
//		def_item_right = 0;
//		def_img_x = 0;
//		def_img_y = 0 + numOffsetH;
//		def_img_w = ShareData.PxToDpi_xhdpi(160);
//		def_img_h = def_img_w;
//		def_bk_x = 0;
//		def_bk_y = numOffsetH + def_img_h;
//		def_bk_w = def_item_width - numOffsetW;
//		def_bk_h = def_item_height - numOffsetH - def_img_h;
//		def_img_round_size = 0;
//		def_show_title = isShowTitle;
//		if(isShowTitle)
//		{
//			//def_item_height = ShareData.PxToDpi_xhdpi(155) + numOffsetH;
//			def_title_size = ShareData.PxToDpi_xhdpi(22);
//			def_title_color_out = 0xFF737373;
//			def_title_color_over = 0xffffffff;
//			def_title_bottom_margin = ShareData.PxToDpi_xhdpi(14);
//		}
//		def_state_x = def_img_x;
//		def_state_y = def_img_y;
//		def_state_w = def_img_w;
//		def_state_h = def_img_h;
//		def_lock_x = def_item_width - ShareData.PxToDpi_xhdpi(32);
//		def_lock_y = 0;
//		def_lock_res = R.drawable.sticker_lock;
//		def_download_more_num_res = R.drawable.photofactory_download_num_bk;
//		def_download_more_num_size = ShareData.PxToDpi_xhdpi(18);
//		def_ready_res = R.drawable.sticker_download;
//		def_new_res = R.drawable.sticker_new;

		int numOffsetW = 0;
		int numOffsetH = 0;
		def_item_width = ShareData.PxToDpi_xhdpi(146) + numOffsetW;
		def_item_height = ShareData.PxToDpi_xhdpi(187) + numOffsetH;
		def_img_color = 0x80FFFFFF;
		def_bk_over_color = ImageUtils.GetSkinColor(0x66e75988);
		def_bk_out_color = 0x66ffffff;
		def_item_left = ShareData.PxToDpi_xhdpi(17);
		def_item_right = 0;
		def_img_x = 0;
		def_img_y = 0 + numOffsetH;
		def_img_w = ShareData.PxToDpi_xhdpi(146);
		def_img_h = def_img_w;
		def_bk_x = 0;
		def_bk_y = numOffsetH + def_img_h;
		def_bk_w = def_item_width - numOffsetW;
		def_bk_h = def_item_height - numOffsetH - def_img_h;
		def_img_round_size = 0;
		def_show_title = isShowTitle;
		if(isShowTitle)
		{
			//def_item_height = ShareData.PxToDpi_xhdpi(155) + numOffsetH;
			def_title_size = ShareData.PxToDpi_xhdpi(20);
			def_title_color_out = 0xFF737373;
			def_title_color_over = 0xffffffff;
			def_title_bottom_margin = ShareData.PxToDpi_xhdpi(14);     //（42-20）/2+22
		}
		def_state_x = def_img_x;
		def_state_y = def_img_y;
		def_state_w = def_img_w;
		def_state_h = def_img_h;
		def_lock_x = def_item_width - ShareData.PxToDpi_xhdpi(32);
		def_lock_y = 0;
		def_lock_res = R.drawable.sticker_lock;
		def_download_more_num_res = R.drawable.photofactory_download_num_bk;
		def_download_more_num_size = ShareData.PxToDpi_xhdpi(18);
		def_ready_res = R.drawable.sticker_download;
		def_new_res = R.drawable.sticker_new;
	}

	public RecommendItemConfig2(Context context, boolean isShowTitle, boolean isDrawImgBg)
	{
		this(context, isShowTitle);
		def_draw_img_bg = isDrawImgBg;
		def_draw_img_bg_color = 0x14000000;// alpha 8%
	}
}
