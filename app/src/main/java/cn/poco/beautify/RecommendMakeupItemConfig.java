package cn.poco.beautify;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import cn.poco.tianutils.ShareData;
import cn.poco.transitions.TweenLite;
import cn.poco.tsv100.ItemConfig;
import my.beautyCamera.R;

public class RecommendMakeupItemConfig extends ItemConfig
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

	public float def_title_size = 0;
	public int def_anim_time = 600;
	public int def_anim_type = TweenLite.EASING_QUINT | TweenLite.EASE_OUT;
	public int def_title_color_out = 0xFFFFFFFF; //title文字的颜色
	public int def_title_color_over = 0xFFFFFFFF; //title文字的颜色
	public int def_title_bottom_margin = 0;
	public int def_sub_title_size = 0;
	//推荐位切角
	public int def_recomm_size = 0;
	public int def_download_more_num_res = 0;
	public int def_download_more_num_size = 0;

	public int def_recommend_res = 0;
	public int def_download_complete_res = 0;

	public Bitmap m_defSubImgBmp; //未加载图片完成前使用的图片
	public Bitmap m_maskBmp;
	public Bitmap m_readyBmp;
	//public Bitmap m_waitBmp;
	public Bitmap m_newBmp;
	public Bitmap m_loadingBmp;
	public Bitmap m_lockBmp;
	public Bitmap m_subItemSelBmp;
	public Bitmap m_subItemColorBmp;

	public PaintFlagsDrawFilter temp_filter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
	public Paint temp_paint = new Paint();
	public Matrix temp_matrix = new Matrix();
	public Path temp_path = new Path();

	public RecommendMakeupItemConfig(Context context)
	{
		m_context = context;

		ShareData.InitData((Activity)m_context);

		def_item_w = ShareData.PxToDpi_xhdpi(110);
		def_item_h = ShareData.PxToDpi_xhdpi(140);
		def_img_w = ShareData.PxToDpi_xhdpi(110);
		def_img_h = ShareData.PxToDpi_xhdpi(110);
		def_sub_img_w = ShareData.PxToDpi_xhdpi(110);
		def_sub_img_h = ShareData.PxToDpi_xhdpi(120);
		def_item_l = ShareData.PxToDpi_xhdpi(12);
		def_item_r = 0;
		def_item_l2 = ShareData.PxToDpi_xhdpi(20);

		def_title_size = ShareData.PxToDpi_xhdpi(18);
		def_title_bottom_margin = ShareData.PxToDpi_xhdpi(10);
		def_sub_title_size = ShareData.PxToDpi_xhdpi(30);

		def_recomm_size = ShareData.PxToDpi_xhdpi(6);
		def_download_more_num_res = R.drawable.photofactory_download_num_bk;
		def_download_more_num_size = ShareData.PxToDpi_xhdpi(18);

		def_recommend_res = R.drawable.sticker_recom;
		def_download_complete_res = R.drawable.sticker_new;
	}

	@Override
	public void InitData()
	{
		ClearAll();

		m_maskBmp = BitmapFactory.decodeResource(m_context.getResources(), R.drawable.photofactory_makeup_item_sub_mask);
		m_defSubImgBmp = Bitmap.createBitmap(def_sub_img_w, def_sub_img_h, Config.ARGB_8888);
		{
			Canvas canvas = new Canvas(m_defSubImgBmp);
			canvas.drawColor(0x80FFFFFF);
			Paint p = new Paint();
			p.setAntiAlias(true);
			p.setFilterBitmap(true);
			p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
			canvas.drawBitmap(m_maskBmp, new Matrix(), p);
		}
		m_subItemColorBmp = Bitmap.createBitmap(def_sub_img_w, def_sub_img_h, Config.ARGB_8888);
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
		if(m_defSubImgBmp != null)
		{
			m_defSubImgBmp.recycle();
			m_defSubImgBmp = null;
		}
		if(m_lockBmp != null)
		{
			m_lockBmp.recycle();
			m_lockBmp = null;
		}
		if(m_subItemSelBmp != null)
		{
			m_subItemSelBmp.recycle();
			m_subItemSelBmp = null;
		}
		if(m_subItemColorBmp != null)
		{
			m_subItemColorBmp.recycle();
			m_subItemColorBmp = null;
		}
	}
}
