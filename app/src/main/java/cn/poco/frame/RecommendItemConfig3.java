package cn.poco.frame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;

import cn.poco.advanced.ImageUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.transitions.TweenLite;
import cn.poco.tsv100.ItemConfig;
import my.beautyCamera.R;

public class RecommendItemConfig3 extends ItemConfig
{
	protected Context m_context;

	public int def_item_w = 0;
	public int def_item_h = 0;
	public int def_img_w = 0; //主img宽
	public int def_img_h = 0; //主img高
	public int def_sub_img_w = 0; //子img宽
	public int def_sub_img_h = 0; //子img高
	public int def_item_l = 0; //主item左边距
	public int def_item_r = 0; //主item右边距
	public int def_item_l2 = 0; //子item左边距
	public int def_img_color = 0;
	public int def_bk_out_color = 0;
	public int def_bk_over_color = 0;
	public int def_sub_sel_w = 0; //子item选中框宽度
	public int def_sub_sel_color = 0; //子item选中框颜色

	public float def_title_size = 0;
	public int def_anim_time = 600;
	public int def_anim_type = TweenLite.EASING_QUINT | TweenLite.EASE_OUT;
	public int def_title_color_out = 0xff737373; //title文字的颜色
	public int def_title_color_over = 0xffffffff; //title文字的颜色
	public int def_title_bottom_margin = 0;
	public int def_download_more_num_res = 0;
	public int def_download_more_num_size = 0;
	public int def_recommend_res = 0;
	public int def_download_complete_res = 0;
	public boolean def_draw_logo_divide_line = false;//是否绘制下载更多分割虚线

	public Bitmap m_readyBmp;
	//public Bitmap m_waitBmp;
	public Bitmap m_newBmp;
	public Bitmap m_loadingBmp;
	public Bitmap m_lockBmp;
	public Bitmap m_downloadMoreBkBmp;

	public PaintFlagsDrawFilter temp_filter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
	public Paint temp_paint = new Paint();
	public Matrix temp_matrix = new Matrix();
	public Path temp_path = new Path();

	public RecommendItemConfig3(Context context)
	{
		m_context = context;

		def_item_w = ShareData.PxToDpi_xhdpi(160);
		def_item_h = ShareData.PxToDpi_xhdpi(206);
		def_img_w = ShareData.PxToDpi_xhdpi(160);
		def_img_h = ShareData.PxToDpi_xhdpi(160);
		def_sub_img_w = ShareData.PxToDpi_xhdpi(150);
		def_sub_img_h = ShareData.PxToDpi_xhdpi(150);
		//def_item_l = ShareData.PxToDpi_xhdpi(6);
		def_item_l = ShareData.PxToDpi_xhdpi(12);
		def_item_r = 0;
		def_item_l2 = ShareData.PxToDpi_xhdpi(20);
		def_img_color = 0x80FFFFFF;
		def_bk_over_color = ImageUtils.GetSkinColor(0x66e75988);
		def_bk_out_color = 0x66ffffff;
		def_sub_sel_w = ShareData.PxToDpi_xhdpi(4);
		def_sub_sel_color = ImageUtils.GetSkinColor(0x66e75988);

		def_title_size = ShareData.PxToDpi_xhdpi(22);
		def_title_bottom_margin = ShareData.PxToDpi_xhdpi(14);

		def_download_more_num_res = R.drawable.photofactory_download_num_bk;
		def_download_more_num_size = ShareData.PxToDpi_xhdpi(18);

		def_recommend_res = R.drawable.sticker_recom;
		def_download_complete_res = R.drawable.sticker_new;
	}

	@Override
	public void InitData()
	{
		ClearAll();
	}

	@Override
	public void ClearAll()
	{
		//if(m_maskBmp != null)
		//{
		//	m_maskBmp.recycle();
		//	m_maskBmp = null;
		//}
		if(m_readyBmp != null)
		{
			m_readyBmp.recycle();
			m_readyBmp = null;
		}
		//if(m_waitBmp != null)
		//{
		//	m_waitBmp.recycle();
		//	m_waitBmp = null;
		//}
		if(m_newBmp != null)
		{
			m_newBmp.recycle();
			m_newBmp = null;
		}
		if(m_loadingBmp != null)
		{
			m_loadingBmp.recycle();
			m_loadingBmp = null;
		}
		if(m_lockBmp != null)
		{
			m_lockBmp.recycle();
			m_lockBmp = null;
		}
		if(m_downloadMoreBkBmp != null)
		{
			m_downloadMoreBkBmp.recycle();
			m_downloadMoreBkBmp = null;
		}
	}
}
