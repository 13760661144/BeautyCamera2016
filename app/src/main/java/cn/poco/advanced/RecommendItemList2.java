package cn.poco.advanced;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;

import cn.poco.tianutils.ImageUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.tsv.AsynImgLoader;
import cn.poco.tsv.AsynImgLoader.Item;
import cn.poco.tsv100.FastHSVCore100;
import cn.poco.tsv100.FastItemList100;
import my.beautyCamera.R;

public class RecommendItemList2 extends RecommendItemList
{
	public RecommendItemList2(Context context)
	{
		super(context);
	}

	public RecommendItemList2(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public RecommendItemList2(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}

	@Override
	public void InitData(RecommendItemConfig config)
	{
		m_itemConfig = config;
		m_itemConfig.InitData();

		m_loader = new AsynImgLoader(new AsynImgLoader.ControlCallback()
		{
			@Override
			public void PopImg(int uri)
			{
				RecommendItemList2.this.invalidate();
			}

			@Override
			public Bitmap MakeBmp(Item item)
			{
				Bitmap out = null;

				if(item != null && m_itemConfig != null)
				{
					if(item.m_res instanceof Integer)
					{
						out = BitmapFactory.decodeResource(getResources(), (Integer)item.m_res);
					}
					else if(item.m_res instanceof String)
					{
						out = BitmapFactory.decodeFile((String)item.m_res);
					}
					if(out != null)
					{
						Bitmap temp = out;
						out = ImageUtils.MakeRoundBmp(temp, m_itemConfig.def_img_w, m_itemConfig.def_img_h, m_itemConfig.def_img_round_size);
						if(out != temp)
						{
							temp.recycle();
							temp = null;
						}
					}
				}

				return out;
			}
		});
		m_loader.SetQueueSize(GetShowNum(ShareData.m_screenWidth, m_itemConfig.def_item_left + m_itemConfig.def_item_width + m_itemConfig.def_item_right) + 3);
	}

	@Override
	public void AutoMeasureSize()
	{
		super.AutoMeasureSize();
		if(m_frW > 0)
		{
			m_frW += m_itemConfig.def_item_left;
		}
	}

	/*@Override
	protected void OnItemClick(int index, float x, float y)
	{
		FastHSVCore100.ItemInfo info = m_infoList.get(index);
		if(info instanceof DownloadAndRecommendItemInfo)
		{
			if(((DownloadAndRecommendItemInfo)info).IsRecommendItem(x, y))
			{
				((ControlCallback)m_cb).OnRecommendItem((DownloadAndRecommendItemInfo)info);
				return;
			}
		}
		super.OnItemClick(index, x, y);
	}*/

	public static class RecommendExItemInfo extends ItemInfo
	{
		public static final int RECOM_ITEM_URI = 0xfffffe1;

		protected Object[] m_logos;
		protected String[] m_names;
		protected Bitmap m_logoBmp;
		protected Bitmap m_recommendLogoBmp;

		public RecommendExItemInfo(RecommendItemConfig config)
		{
			super(config);

			m_uri = RECOM_ITEM_URI;
		}

		@Override
		public void OnDraw(FastHSVCore100 list, Canvas canvas, int index)
		{
			final RecommendItemConfig2 config = (RecommendItemConfig2)m_config;
			final Paint paint = config.temp_paint;
			final int item_left = config.def_item_left;
			final int item_height = config.def_item_height;
			final int img_w = config.def_img_w;
			final int img_h = config.def_img_h;
			final int img_x = config.def_img_x;
			final int img_y = config.def_img_y;
			final int bk_x = config.def_bk_x;
			final int bk_y = config.def_bk_y;

			canvas.setDrawFilter(config.temp_filter);

			//画背景
			Bitmap bmp = config.m_outBkBmp;
			if(bmp != null)
			{
				paint.reset();
				paint.setAntiAlias(true);
				paint.setFilterBitmap(true);
				canvas.drawBitmap(bmp, item_left + bk_x, bk_y, paint);
			}

			//画图片背景
			if(config.def_draw_img_bg && config.def_draw_img_bg_color != 0 && config.m_drawImgBkBmp != null && !config.m_drawImgBkBmp.isRecycled())
			{
				paint.reset();
				paint.setAntiAlias(true);
				paint.setFilterBitmap(true);
				canvas.drawBitmap(config.m_drawImgBkBmp, item_left + img_x, img_y, paint);
			}

			//画图片
			if(m_logoBmp == null)
			{
				m_logoBmp = cn.poco.imagecore.Utils.DecodeImage(list.getContext(), m_logos[0], 0, -1, -1, -1);
			}
			if(m_logoBmp != null && m_logoBmp.getWidth() > 0 && m_logoBmp.getHeight() > 0)
			{
				paint.reset();
				paint.setAntiAlias(true);
				paint.setFilterBitmap(true);

				temp_matrix.reset();
				temp_matrix.postScale(img_w / (float)m_logoBmp.getWidth(), img_h / (float)m_logoBmp.getHeight());
				temp_matrix.postTranslate(item_left + img_x, img_y);
				canvas.drawBitmap(m_logoBmp, temp_matrix, paint);
				//canvas.drawBitmap(m_logoBmp, item_left + img_x, img_y, paint);
			}

			//画文字
			if(config.def_show_title && m_names[0] != null)
			{
				paint.reset();
				paint.setAntiAlias(true);
				paint.setFilterBitmap(true);
				paint.setTextSize(config.def_title_size);
				paint.setColor(config.def_title_color_out);
				float w = paint.measureText(m_names[0]);
				canvas.drawText(m_names[0], item_left + img_x + (img_w - w) / 2, item_height - config.def_title_bottom_margin, paint);
			}

			//画推荐星
			if(m_recommendLogoBmp == null)
			{
				m_recommendLogoBmp = BitmapFactory.decodeResource(list.getContext().getResources(), ((RecommendItemConfig2)m_config).def_recommend_res);
			}
			if(m_recommendLogoBmp != null)
			{
				canvas.drawBitmap(m_recommendLogoBmp, item_left + img_w - m_recommendLogoBmp.getWidth(), img_y, null);
			}
		}

		protected Matrix temp_matrix = new Matrix();

		public void SetLogos(Object[] arr, String[] names)
		{
			m_logos = arr;
			m_names = names;
			if(m_logoBmp != null)
			{
				m_logoBmp.recycle();
				m_logoBmp = null;
			}
		}
	}

	public static class DownloadAndRecommendItemInfo extends ItemInfo
	{
		public static final int DOWNLOAD_RECOM_ITEM_URI = DownloadItemInfo.DOWNLOAD_ITEM_URI;

		protected static final int BLUR_COLOR = 0x20000000;
		protected static final int BLUR_R = 6;
		protected static final int OFFSET = 4;
		protected static final int SPACE_W = 11;

		protected Activity m_ac;
		protected Object[] m_logos;
		protected String[] m_names;
		protected Bitmap m_logoBmp;
		protected int m_num;

		public DownloadAndRecommendItemInfo(Activity ac, RecommendItemConfig2 config)
		{
			super(config);

			m_ac = ac;
			m_uri = DOWNLOAD_RECOM_ITEM_URI;
		}

		@Override
		public void OnDraw(FastHSVCore100 list, Canvas canvas, int index)
		{
			if(m_logoBmp == null)
			{
				m_logoBmp = MakeLogoBmp(list);
			}
			if(m_logoBmp != null)
			{
				canvas.drawBitmap(m_logoBmp, ((RecommendItemConfig)m_config).def_item_left, 0, null);
			}
		}

		public void SetLogos(Object[] arr, String[] names)
		{
			m_logos = arr;
			m_names = names;
			if(m_logoBmp != null)
			{
				m_logoBmp.recycle();
				m_logoBmp = null;
			}
		}

		public void SetNum(int num)
		{
			m_num = num;
			if(m_logoBmp != null)
			{
				m_logoBmp.recycle();
				m_logoBmp = null;
			}
		}

		protected Bitmap MakeLogoBmp(FastHSVCore100 list)
		{
			final RecommendItemConfig2 config = (RecommendItemConfig2)m_config;
			final Paint paint = config.temp_paint;
			final int item_w = config.def_item_width;
			final int item_h = config.def_item_height;
			final int img_h = config.def_img_h;

			Bitmap out = Bitmap.createBitmap(item_w, item_h, Config.ARGB_8888);

			if(m_logos != null && m_logos.length > 1)
			{
				//画底下颜色矩形
				Canvas canvas = new Canvas(out);
				paint.reset();
				paint.setAntiAlias(true);
				paint.setFilterBitmap(true);
				paint.setStyle(Paint.Style.FILL);
				paint.setColor(config.def_bk_out_color);
				//canvas.drawRect(0, img_h + 2, item_w, item_h, paint);
				canvas.drawRect(0, img_h, item_w, item_h, paint);
				//下载更多
				if(m_num != 0)
				{
					Bitmap temp = null;
					temp = BitmapFactory.decodeResource(m_ac.getResources(), R.drawable.photofactory_download_logo2);
					temp = cn.poco.advanced.ImageUtils.AddSkin(list.getContext(), temp);//主题颜色
					paint.reset();
					paint.setAntiAlias(true);
					paint.setFilterBitmap(true);
					int x = ShareData.PxToDpi_xhdpi(18);
					//int y = (item_h - (img_h + 2) - temp.getHeight()) / 2 + img_h + 2;
					int y = (item_h - img_h - temp.getHeight()) / 2 + img_h;
					canvas.drawBitmap(temp, x, y, paint);
					paint.setColor(cn.poco.advanced.ImageUtils.GetSkinColor(0xff3dbb9d));
					paint.setTextSize(ShareData.PxToDpi_xhdpi(22));
					x = ShareData.PxToDpi_xhdpi(54);
					//y = img_h + 2 + ShareData.PxToDpi_xhdpi(32);
					y = img_h + ShareData.PxToDpi_xhdpi(32);
					canvas.drawText(m_ac.getResources().getString(R.string.recommend_download_more), x, y, paint);
					//下载个数
					x = ShareData.PxToDpi_xhdpi(32);
					//y = img_h + 2 + ShareData.PxToDpi_xhdpi(2);
					y = img_h + ShareData.PxToDpi_xhdpi(2);
					temp = BitmapFactory.decodeResource(m_ac.getResources(), R.drawable.material_not_download_tip);
					canvas.drawBitmap(temp, x, y, paint);
					paint.reset();
					paint.setAntiAlias(true);
					paint.setFilterBitmap(true);
					paint.setColor(0xFFFFFFFF);
					paint.setTextSize(ShareData.PxToDpi_xhdpi(12));
					paint.setTextAlign(Align.CENTER);
					Paint.FontMetrics fontMetrics = paint.getFontMetrics();
					canvas.drawText(Integer.toString(m_num), x + temp.getWidth() / 2f, y + temp.getHeight() / 2f + Math.abs(fontMetrics.ascent * 0.8f) / 2f, paint);
				}

				/*if(config.def_draw_logo_divide_line) {
					//画分割线
					paint.reset();
					paint.setAntiAlias(true);
					paint.setFilterBitmap(true);
					paint.setStyle(Paint.Style.STROKE);
					paint.setColor(config.def_bk_out_color);
					paint.setStrokeWidth(3);
					PathEffect effects = new DashPathEffect(new float[]{ShareData.PxToDpi_xhdpi(16), ShareData.PxToDpi_xhdpi(6)}, 1);
					paint.setPathEffect(effects);
					canvas.drawLine(0, img_h, item_w, img_h, paint);
				}*/
				//画图
				paint.reset();
				paint.setAntiAlias(true);
				paint.setFilterBitmap(true);
				paint.setStyle(Paint.Style.FILL);
				paint.setColor(0x33ffffff);
				canvas.drawRect(0, 0, item_w, img_h, paint);
				//int len = m_logos.length;
				int len = m_logos.length - 1;
				if(len > 3)
				{
					len = 3;
				}
				int subW = ShareData.PxToDpi_xhdpi(110);
				int allSubW = subW;
				int sw = ShareData.PxToDpi_xhdpi(SPACE_W);
				for(int i = 0; i < len - 1; i++)
				{
					allSubW += sw;
				}
				allSubW += BLUR_R;
				float ex = (item_w - allSubW) / 2f + (len - 1) * sw;
				float ey = ex;
				for(int i = len - 1; i > -1; i--)
				{
					//DrawSubBmp(canvas, m_logos[i], ex, ey, subW, subW);
					DrawSubBmp(canvas, m_logos[i + 1], ex, ey, subW, subW);
					ex -= sw;
					ey -= sw;
				}
			}
			else
			{
				//就画一个下载更多
				Canvas canvas = new Canvas(out);
				//canvas.drawColor(config.def_bk_out_color);
				Bitmap temp = null;
				temp = BitmapFactory.decodeResource(m_ac.getResources(), R.drawable.photofactory_download_logo);
				temp = cn.poco.advanced.ImageUtils.AddSkin(list.getContext(), temp);
				float x = (item_w - temp.getWidth()) / 2f;
				float y = (item_h - temp.getHeight()) / 2f;
				float deviation = ShareData.PxToDpi_xhdpi(8); //整体上移浮动误差
				y -= deviation;
				paint.reset();
				paint.setAntiAlias(true);
				paint.setFilterBitmap(true);
				canvas.drawBitmap(temp, x, y, paint);

				if(m_num != 0)
				{
					if(config.m_downloadMoreBkBmp == null)
					{
						config.m_downloadMoreBkBmp = BitmapFactory.decodeResource(m_ac.getResources(), config.def_download_more_num_res);
					}
					if(config.m_downloadMoreBkBmp != null)
					{
						x += temp.getWidth() - config.m_downloadMoreBkBmp.getWidth() * 1.15f;
						x += ShareData.PxToDpi_xhdpi(10);
						y -= ShareData.PxToDpi_xhdpi(3);
						canvas.drawBitmap(config.m_downloadMoreBkBmp, x, y, paint);

						paint.reset();
						paint.setAntiAlias(true);
						paint.setFilterBitmap(true);
						paint.setColor(0xFFFFFFFF);
						paint.setTextSize(config.def_download_more_num_size);
						paint.setTextAlign(Align.CENTER);
						Paint.FontMetrics fontMetrics = paint.getFontMetrics();
						canvas.drawText(Integer.toString(m_num), x + config.m_downloadMoreBkBmp.getWidth() / 2f, y + config.m_downloadMoreBkBmp.getHeight() / 2f + Math.abs(fontMetrics.ascent * 0.80f) / 2f, paint);
					}
				}
				paint.reset();
				paint.setAntiAlias(true);
				paint.setFilterBitmap(true);
				paint.setColor(cn.poco.advanced.ImageUtils.GetSkinColor(0xffe75988));
				paint.setTextSize(ShareData.PxToDpi_xhdpi(22));
				x = (item_w - temp.getWidth()) / 2f;
				x -= ShareData.PxToDpi_xhdpi(3);//减少误差，居中icon
				y = img_h + ShareData.PxToDpi_xhdpi(6);
				y -= deviation;
				canvas.drawText(m_ac.getResources().getString(R.string.recommend_material_more), x, y, paint);
			}

			return out;
		}

		private void DrawSubBmp(Canvas canvas, Object res, float x, float y, int w, int h)
		{
			Bitmap bmp = cn.poco.imagecore.Utils.DecodeImage(m_ac, res, 0, -1, -1, -1);
			if(bmp != null)
			{
				//阴影
				Paint paint = new Paint();
				paint.setAntiAlias(true);
				paint.setFilterBitmap(true);
				paint.setColor(BLUR_COLOR);
				paint.setStyle(Paint.Style.FILL);
				paint.setShadowLayer(BLUR_R, ShareData.PxToDpi_xhdpi(OFFSET), ShareData.PxToDpi_xhdpi(OFFSET), BLUR_COLOR);
				canvas.drawRect(x, y, x + w, x + h, paint);

				//画图
				Matrix matrix = new Matrix();
				matrix.postScale((float)w / (float)bmp.getWidth(), (float)h / (float)bmp.getHeight());
				matrix.postTranslate(x, y);
				paint.reset();
				paint.setAntiAlias(true);
				paint.setFilterBitmap(true);
				canvas.drawBitmap(bmp, matrix, paint);
			}
		}

		public boolean IsRecommendItem(float x, float y)
		{
			boolean out = false;

			if(m_logos != null && m_logos.length > 0)
			{
				if(y < ((RecommendItemConfig2)m_config).def_img_h)
				{
					out = true;
				}
			}

			return out;
		}
	}

	public interface ControlCallback extends FastItemList100.ControlCallback
	{
		//void OnRecommendItem(DownloadAndRecommendItemInfo info);
	}
}
