package cn.poco.ad;

import android.app.Activity;

import java.util.ArrayList;

import cn.poco.tianutils.ItemListV5;
import cn.poco.tianutils.ShareData;

public class MyItemListV5 extends ItemListV5
{

	public MyItemListV5(Activity context)
	{
		super(context);

		MyInit(context);
	}

	protected void MyInit(Activity context)
	{
		ShareData.InitData(context);

		int numX = ShareData.PxToDpi_xhdpi(20);
		int numY = ShareData.PxToDpi_xhdpi(20);

		def_item_width = ShareData.PxToDpi_xhdpi(128) + numX;
		def_item_height = ShareData.PxToDpi_xhdpi(128) + numY;
		def_anim_size = def_item_width;
//		def_bk_over_color = 0xFF19B593;
		def_item_left = ShareData.PxToDpi_xhdpi(4);
		def_item_right = def_item_left;
		//def_bk_out_res = R.drawable.photofactory_sp_bk_out;
		//def_bk_over_res = R.drawable.photofactory_sp_bk_over;
		def_bk_x = 0;
		def_bk_y = numY;
		def_bk_w = def_item_width - numX;
		def_bk_h = def_item_height - numY;
		def_img_x = ShareData.PxToDpi_xhdpi(4);
		def_img_y = ShareData.PxToDpi_xhdpi(4) + numY;
		def_img_w = ShareData.PxToDpi_xhdpi(120);
		def_img_h = def_img_w;
		def_img_round_size = ShareData.PxToDpi_xhdpi(12);
		def_move_size = ShareData.PxToDpi_hdpi(30);
		//def_title_size = ShareData.PxToDpi(18);
		def_show_title = false;
	}

	public static ItemListV5 makeItemList1(Activity context, ArrayList<?> resList, boolean hasTitle, ControlCallback cb)
	{
		ItemListV5 itemList = new ItemListV5(context);

		int numOffsetW = 0;
		int numOffsetH = 0;
		itemList.def_item_width = ShareData.PxToDpi_xhdpi(186) + numOffsetW;
		itemList.def_item_height = ShareData.PxToDpi_xhdpi(246) + numOffsetH;
//		itemList.def_img_color = 0x80FFFFFF;
		itemList.def_bk_over_color = 0x6618cea7;
		itemList.def_bk_out_color = 0xff000000;
		itemList.def_item_left = 0;
		itemList.def_item_right = ShareData.PxToDpi_xhdpi(18);
		itemList.def_bk_x = 0;
		itemList.def_bk_y = ShareData.PxToDpi_xhdpi(186);
		itemList.def_bk_w = itemList.def_item_width - numOffsetW;
		itemList.def_bk_h = itemList.def_item_height - numOffsetH;
		itemList.def_img_x = 0;
		itemList.def_img_y = 0;
		itemList.def_img_w = ShareData.PxToDpi_xhdpi(186);
		itemList.def_img_h = itemList.def_img_w;
		itemList.def_move_size = ShareData.PxToDpi_hdpi(30);
		itemList.def_show_title = hasTitle;
		if(hasTitle)
		{
			itemList.def_title_size = ShareData.PxToDpi_xhdpi(20);
			itemList.def_title_color_out = 0xFF737373;
			itemList.def_title_color_over = 0xffffffff;
			itemList.def_title_bottom_margin = ShareData.PxToDpi_xhdpi(22);
		}

		itemList.InitData(cb);
		itemList.SetData(resList);
		return itemList;
	}
}
