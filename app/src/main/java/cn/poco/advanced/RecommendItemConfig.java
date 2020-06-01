package cn.poco.advanced;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;

import cn.poco.tianutils.ImageUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.tsv100.ItemConfig;
import my.beautyCamera.R;

public class RecommendItemConfig extends ItemConfig
{
	protected Context m_context;

	public int def_item_width = 0;
	public int def_item_height = 0;
	public int def_item_left = 0;
	public int def_item_right = 0;

	public int def_img_res = 0;
	public int def_img_color = 0;
	public int def_bk_out_res = 0;
	public int def_bk_over_res = 0;
	public int def_bk_out_color = 0;
	public int def_bk_over_color = 0;
	public int def_bk_x = 0; //item 选中背景的x偏移
	public int def_bk_y = 0;
	public int def_bk_w = 0; //item 背景的宽
	public int def_bk_h = 0;
	public int def_img_x = 0;//item 显示图片的x偏移
	public int def_img_y = 0;
	public int def_img_w = 0;//item 显示图片的宽
	public int def_img_h = 0;
	public float def_img_round_size = 0; //显示图片的圆角大小
	public boolean def_show_title = false; //是否显示title文字
	public boolean def_draw_logo_divide_line = false;//是否绘制下载更多分割虚线
	public boolean def_draw_img_bg = false;//是否画img背景
	public int def_draw_img_bg_color = 0;//画img背景color
	public int def_title_color_out = 0xFF606060; //title文字的颜色
	public int def_title_color_over = 0xFF606060; //title文字的颜色
	public float def_title_size = 0;
	public int def_title_bottom_margin = 0;
	public int def_anim_time = 500;

	public int def_state_x = 0;
	public int def_state_y = 0;
	public int def_state_w = 0;
	public int def_state_h = 0;
	public int def_wait_res = 0;
	public int def_loading_res = 0;
	public int def_ready_res = 0;
	public int def_new_res = 0;
	public int def_lock_x = 0;
	public int def_lock_y = 0;
	public int def_lock_res = 0;
	public int def_download_more_num_res = 0;
	public int def_download_more_num_size = 0;
	public boolean def_loading_anim = false;
	public int def_loading_mask_color = 0;

	public int def_recommend_res = 0;
	public int def_download_complete_res = 0;

	public Bitmap m_defImgBmp; //未加载图片完成前使用的图片
	public Bitmap m_outBkBmp;
	public Bitmap m_overBkBmp;
	public Bitmap m_drawImgBkBmp;//图片资源的背景

	public Bitmap m_readyBmp;
	public Bitmap m_newBmp;
	public Bitmap m_waitBmp;
	public Bitmap m_loadingBmp;
	public Bitmap m_lockBmp;
	public Bitmap m_downloadMoreBkBmp;

	public PaintFlagsDrawFilter temp_filter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
	public Paint temp_paint = new Paint();
	public Matrix temp_matrix = new Matrix();

	public RecommendItemConfig(Context context, boolean isShowTitle)
	{
		m_context = context;

		ShareData.InitData((Activity)m_context);

		int numOffsetW = 0;
		int numOffsetH = 0;
		def_item_width = ShareData.PxToDpi_xhdpi(128) + numOffsetW;
		def_item_height = ShareData.PxToDpi_xhdpi(128) + numOffsetH;
		def_img_color = 0x80FFFFFF;
		def_bk_over_color = 0xFF19B593;
		def_item_left = ShareData.PxToDpi_xhdpi(8);
		def_item_right = def_item_left;
		def_bk_x = 0;
		def_bk_y = numOffsetH;
		def_bk_w = def_item_width - numOffsetW;
		def_bk_h = def_item_height - numOffsetH;
		def_img_x = ShareData.PxToDpi_xhdpi(4);
		def_img_y = ShareData.PxToDpi_xhdpi(4) + numOffsetH;
		def_img_w = ShareData.PxToDpi_xhdpi(120);
		def_img_h = def_img_w;
		def_img_round_size = ShareData.PxToDpi_xhdpi(12);
		def_show_title = isShowTitle;
		if(isShowTitle)
		{
			def_item_height = ShareData.PxToDpi_xhdpi(155) + numOffsetH;

			def_title_size = ShareData.PxToDpi_xhdpi(20);
			def_title_color_out = 0xFFFFFFFF;
			def_title_color_over = 0xFF19B593;
			def_title_bottom_margin = ShareData.PxToDpi_xhdpi(4);
		}
		def_state_x = def_img_x;
		def_state_y = def_img_y;
		def_state_w = def_img_w;
		def_state_h = def_img_h;
		def_wait_res = R.drawable.sticker_loading;
		def_loading_res = R.drawable.sticker_loading;
		def_ready_res = R.drawable.sticker_download;
		def_new_res = R.drawable.sticker_new;
		//def_download_item_res = R.drawable.photofactory_download_logo;
		//def_num_bk_res = R.drawable.photofactory_download_num_bk;
		//def_num_x = svc.def_item_width - ShareData.PxToDpi_xhdpi(41);
		//def_num_y = 0;
		//def_num_text_size = ShareData.PxToDpi_hdpi(12);
		def_lock_x = def_item_width - ShareData.PxToDpi_xhdpi(32);
		def_lock_y = 0;
		def_lock_res = R.drawable.sticker_lock;
		def_download_more_num_res = R.drawable.photofactory_download_num_bk;
		def_download_more_num_size = ShareData.PxToDpi_xhdpi(18);

		def_recommend_res = R.drawable.sticker_recom;
		def_download_complete_res = R.drawable.sticker_new;
	}

	public void InitData()
	{
		ClearAll();

		if(def_img_res != 0)
		{
			m_defImgBmp = ImageUtils.MakeResRoundBmp(m_context, def_img_res, def_img_w, def_img_h, def_img_round_size);
		}
		else if(def_img_color != 0)
		{
			m_defImgBmp = ImageUtils.MakeColorRoundBmp(def_img_color, def_img_w, def_img_h, def_img_round_size);
		}
		if(def_bk_out_res != 0)
		{
			m_outBkBmp = ImageUtils.MakeResRoundBmp(m_context, def_bk_out_res, def_bk_w, def_bk_h, def_img_round_size);
		}
		else if(def_bk_out_color != 0)
		{
			m_outBkBmp = ImageUtils.MakeColorRoundBmp(def_bk_out_color, def_bk_w, def_bk_h, def_img_round_size);
		}
		if(def_bk_over_res != 0)
		{
			m_overBkBmp = ImageUtils.MakeResRoundBmp(m_context, def_bk_over_res, def_bk_w, def_bk_h, def_img_round_size);
		}
		else if(def_bk_over_color != 0)
		{
			m_overBkBmp = ImageUtils.MakeColorRoundBmp(def_bk_over_color, def_bk_w, def_bk_h, def_img_round_size);
		}
		if(def_draw_img_bg && def_draw_img_bg_color != 0)
		{
			m_drawImgBkBmp = ImageUtils.MakeColorRoundBmp(def_draw_img_bg_color, def_img_w, def_img_h, 0);
		}
	}

	public void ClearAll()
	{
		if(m_defImgBmp != null)
		{
			m_defImgBmp.recycle();
			m_defImgBmp = null;
		}
		if(m_outBkBmp != null)
		{
			m_outBkBmp.recycle();
			m_outBkBmp = null;
		}
		if(m_overBkBmp != null)
		{
			m_overBkBmp.recycle();
			m_overBkBmp = null;
		}

		if(m_readyBmp != null)
		{
			m_readyBmp.recycle();
			m_readyBmp = null;
		}
		if(m_newBmp != null)
		{
			m_newBmp.recycle();
			m_newBmp = null;
		}
		if(m_waitBmp != null)
		{
			m_waitBmp.recycle();
			m_waitBmp = null;
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
		if(m_drawImgBkBmp != null && !m_drawImgBkBmp.isRecycled())
		{
			m_drawImgBkBmp.recycle();
			m_drawImgBkBmp = null;
		}
	}
}
