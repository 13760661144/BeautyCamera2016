package cn.poco.advanced;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;

import java.util.ArrayList;

import cn.poco.tianutils.ImageUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.tsv.AsynImgLoader;
import cn.poco.tsv.AsynImgLoader.Item;
import cn.poco.tsv100.FastHSVCore100;
import cn.poco.tsv100.FastItemList100;
import my.beautyCamera.R;

public class RecommendItemList extends FastItemList100
{
	protected RecommendItemConfig m_itemConfig;
	protected AsynImgLoader m_loader;

	public RecommendItemList(Context context)
	{
		super(context);
	}

	public RecommendItemList(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public RecommendItemList(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}

	public void InitData(RecommendItemConfig config)
	{
		m_itemConfig = config;
		m_itemConfig.InitData();

		m_loader = new AsynImgLoader(new AsynImgLoader.ControlCallback()
		{
			@Override
			public void PopImg(int uri)
			{
				RecommendItemList.this.invalidate();
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

	protected int GetShowNum(int viewW, int itemW)
	{
		if(itemW > 0)
		{
			return (int)Math.ceil((float)(viewW + itemW) / itemW);
		}
		else
		{
			return 0;
		}
	}

	public int SetItemStyleByIndex(int index, ItemInfo.Style style)
	{
		int out = -1;
		if(m_infoList != null && m_infoList.size() > index)
		{
			ItemInfo info = (ItemInfo)m_infoList.get(index);
			info.m_style = style;
			out = index;
			this.invalidate();
		}
		return out;
	}

	public int SetItemStyleByUri(int uri, ItemInfo.Style style)
	{
		ArrayList<?> temp = m_infoList;
		int out = GetIndex((ArrayList<FastItemList100.ItemInfo>)temp, uri);
		if(out >= 0)
		{
			ItemInfo info = (ItemInfo)m_infoList.get(out);
			info.m_style = style;
			this.invalidate();
		}

		return out;
	}

	public void Lock(int uri)
	{
		ItemInfo info = (ItemInfo)this.GetItemInfoByUri(uri);
		if(info != null)
		{
			info.m_isLock = true;
		}
		this.invalidate();
	}

	public void Unlock(int uri)
	{
		ItemInfo info = (ItemInfo)this.GetItemInfoByUri(uri);
		if(info != null)
		{
			info.m_isLock = false;
		}
		this.invalidate();
	}

	public void ScrollToCenter(boolean hasAnim)
	{
		if(m_currentSel >= 0)
		{
			super.ScrollToCenter(m_currentSel, hasAnim);
		}
	}

	/**
	 * 必须调用否则内存泄漏
	 */
	@Override
	public void ClearAll()
	{
		super.ClearAll();

		if(m_itemConfig != null)
		{
			m_itemConfig.ClearAll();
			//m_itemConfig = null;
		}
		if(m_loader != null)
		{
			m_loader.ClearAll();
			m_loader = null;
		}
		if(m_infoList != null)
		{
			int len = m_infoList.size();
			ItemInfo info;
			for(int i = 0; i < len; i++)
			{
				info = (ItemInfo)m_infoList.get(i);
				info.ClearAll();
			}
			m_infoList.clear();
		}
	}


	public static class ItemInfo extends FastItemList100.ItemInfo
	{
		public Object m_logo;
		public String m_name;

		public Object m_ex;

		public enum Style
		{
			//正常
			NORMAL(0),
			//需要下载
			NEED_DOWNLOAD(1),
			//下载中
			LOADING(2),
			//等待下载
			WAIT(3),
			//下载失败
			FAIL(4),
			//新下载
			NEW(5);

			private final int m_value;

			Style(int value)
			{
				m_value = value;
			}

			public int GetValue()
			{
				return m_value;
			}
		}

		public Style m_style = Style.NORMAL;
		public boolean m_isLock = false;

		public ItemInfo(RecommendItemConfig config)
		{
			super(config);
		}

		@Override
		public int GetCurrentW(FastHSVCore100 list)
		{
			return ((RecommendItemConfig)m_config).def_item_left + ((RecommendItemConfig)m_config).def_item_width + ((RecommendItemConfig)m_config).def_item_right;
		}

		@Override
		public int GetCurrentH(FastHSVCore100 list)
		{
			return ((RecommendItemConfig)m_config).def_item_height;
		}

		@Override
		public int GetFixW(FastHSVCore100 list)
		{
			return GetCurrentW(list);
		}

		@Override
		public int GetFixH(FastHSVCore100 list)
		{
			return GetCurrentH(list);
		}

		@Override
		public void OnDraw(FastHSVCore100 list, Canvas canvas, int index)
		{
			final RecommendItemConfig config = (RecommendItemConfig)m_config;
			final Paint paint = config.temp_paint;
			final int item_left = config.def_item_left;
			final int item_height = config.def_item_height;
			final int img_w = config.def_img_w;
			final int img_h = config.def_img_h;
			final int img_x = config.def_img_x;
			final int img_y = config.def_img_y;
			final int bk_x = config.def_bk_x;
			final int bk_y = config.def_bk_y;

			canvas.save();
			canvas.setDrawFilter(config.temp_filter);

			//画背景
			Bitmap bmp = null;
			if(((RecommendItemList)list).m_currentSel == index)
			{
				bmp = config.m_overBkBmp;
			}
			else
			{
				bmp = config.m_outBkBmp;
			}
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
			bmp = ((RecommendItemList)list).m_loader.GetImg(m_uri, true);
			if(bmp == null)
			{
				((RecommendItemList)list).m_loader.PushImg(m_uri, m_logo);
				bmp = config.m_defImgBmp;
			}
			if(bmp != null)
			{
				paint.reset();
				paint.setAntiAlias(true);
				paint.setFilterBitmap(true);
				canvas.drawBitmap(bmp, item_left + img_x, img_y, paint);
			}

			//画文字
			if(config.def_show_title && m_name != null)
			{
				paint.reset();
				paint.setAntiAlias(true);
				paint.setFilterBitmap(true);
				paint.setTextSize(config.def_title_size);
				if(((RecommendItemList)list).m_currentSel == index)
				{
					paint.setColor(config.def_title_color_over);
				}
				else
				{
					paint.setColor(config.def_title_color_out);
				}
				float w = paint.measureText(m_name);
				canvas.drawText(m_name, item_left + img_x + (img_w - w) / 2, item_height - config.def_title_bottom_margin, paint);
			}

			//画下载状态
			switch(m_style)
			{
				case NEED_DOWNLOAD:
				{
					if(config.m_readyBmp == null && config.def_ready_res != 0)
					{
						Bitmap temp = BitmapFactory.decodeResource(list.getResources(), config.def_ready_res);
						/*config.m_readyBmp = ImageUtils.MakeRoundBmp(temp, config.def_state_w, config.def_state_h, config.def_img_round_size);
						if(config.m_readyBmp != temp)
						{
							temp.recycle();
							temp = null;
						}*/
						config.m_readyBmp = temp;
					}
					bmp = config.m_readyBmp;
					break;
				}
				case NEW:
				{
					if(config.m_newBmp == null && config.def_new_res != 0)
					{
						Bitmap temp = BitmapFactory.decodeResource(list.getResources(), config.def_new_res);
						/*config.m_newBmp = ImageUtils.MakeRoundBmp(temp, config.def_state_w, config.def_state_h, config.def_img_round_size);
						if(config.m_newBmp != temp)
						{
							temp.recycle();
							temp = null;
						}*/
						config.m_newBmp = temp;
					}
					bmp = config.m_newBmp;
					break;
				}
				case LOADING:
				{
					if(config.m_loadingBmp == null && config.def_loading_res != 0)
					{
						Bitmap temp = BitmapFactory.decodeResource(list.getResources(), config.def_loading_res);
						/*config.m_loadingBmp = ImageUtils.MakeRoundBmp(temp, config.def_state_w, config.def_state_h, config.def_img_round_size);
						if(config.m_loadingBmp != temp)
						{
							temp.recycle();
							temp = null;
						}*/
						config.m_loadingBmp = temp;
					}
					bmp = config.m_loadingBmp;
					if(config.def_loading_anim && bmp != null)
					{
						if(config.def_loading_mask_color != 0)
						{
							paint.reset();
							paint.setColor(config.def_loading_mask_color);
							canvas.drawRect(item_left + img_x, img_y, item_left + img_x + img_w, img_y + img_h, paint);
						}
						paint.reset();
						paint.setAntiAlias(true);
						paint.setFilterBitmap(true);
						config.temp_matrix.reset();
						config.temp_matrix.postRotate((System.currentTimeMillis() % 1000) / 1000f * 360f, bmp.getWidth() / 2f, bmp.getHeight() / 2f);
						config.temp_matrix.postTranslate(item_left + config.def_state_x, config.def_state_y);
						canvas.drawBitmap(bmp, config.temp_matrix, paint);

						list.invalidate();
						bmp = null;
					}
					break;
				}
				case WAIT:
				{
					if(config.m_waitBmp == null && config.def_wait_res != 0)
					{
						Bitmap temp = BitmapFactory.decodeResource(list.getResources(), config.def_wait_res);
						/*config.m_waitBmp = ImageUtils.MakeRoundBmp(temp, config.def_state_w, config.def_state_h, config.def_img_round_size);
						if(config.m_waitBmp != temp)
						{
							temp.recycle();
							temp = null;
						}*/
						config.m_waitBmp = temp;
					}
					bmp = config.m_waitBmp;
					break;
				}
				default:
					bmp = null;
					break;
			}

			if(bmp != null)
			{
				paint.reset();
				paint.setAntiAlias(true);
				paint.setFilterBitmap(true);
//				canvas.drawBitmap(bmp, item_left + config.def_state_x, config.def_state_y, paint);
				canvas.drawBitmap(bmp, item_left + config.def_item_width - bmp.getWidth(), 0, null);
			}

			//画锁
			if(m_isLock && config.def_lock_res != 0)
			{
				if(config.m_lockBmp == null)
				{
					config.m_lockBmp = BitmapFactory.decodeResource(list.getResources(), config.def_lock_res);
				}

				if(config.m_lockBmp != null)
				{
//					canvas.drawBitmap(config.m_lockBmp, item_left + config.def_lock_x, config.def_lock_y, null);
					canvas.drawBitmap(config.m_lockBmp, item_left + config.def_item_width - config.m_lockBmp.getWidth(), 0, null);
				}
			}
			canvas.restore();
		}

		@Override
		public boolean IsThouch(FastHSVCore100 list, float x, float y)
		{
			boolean out = false;

			final RecommendItemConfig config = (RecommendItemConfig)m_config;
			float tx = x - m_x;
			if(tx > config.def_item_left && tx < config.def_item_left + config.def_item_width)
			{
				out = true;
			}

			return out;
		}

		public void ClearAll()
		{
		}
	}

	public static class NullItemInfo extends ItemInfo
	{

		public NullItemInfo(RecommendItemConfig config) {
			super(config);
		}

		@Override
		public void OnDraw(FastHSVCore100 list, Canvas canvas, int index) {
			final RecommendItemConfig config = (RecommendItemConfig)m_config;
			final Paint paint = config.temp_paint;
			final int item_left = config.def_item_left;
			final int item_height = config.def_item_height;
			final int img_w = config.def_img_w;
			final int img_h = config.def_img_h;
			final int img_x = config.def_img_x;
			final int img_y = config.def_img_y;
			final int bk_x = config.def_bk_x;
			final int bk_y = config.def_bk_y;

			canvas.save();
			canvas.setDrawFilter(config.temp_filter);

			//画背景
			Bitmap bmp = null;
//			if(((RecommendItemList)list).m_currentSel == index)
//			{
//				bmp = config.m_overBkBmp;
//			}
				bmp = config.m_outBkBmp;
			if(bmp != null)
			{
				paint.reset();
				paint.setAntiAlias(true);
				paint.setFilterBitmap(true);
				canvas.drawBitmap(bmp, item_left, 0, paint);
			}

			//画图片
			bmp = ((RecommendItemList)list).m_loader.GetImg(m_uri, true);
			if(bmp == null)
			{
				((RecommendItemList)list).m_loader.PushImg(m_uri, m_logo);
				bmp = config.m_defImgBmp;
			}
			if(bmp != null)
			{
				paint.reset();
				paint.setAntiAlias(true);
				paint.setFilterBitmap(true);
				int x = item_left + (img_w - ShareData.PxToDpi_xhdpi(52))/2;
				int y = (item_height - ShareData.PxToDpi_xhdpi(52))/2;
				RectF rectF = new RectF(x,y,x + ShareData.PxToDpi_xhdpi(52),y + ShareData.PxToDpi_xhdpi(52));
				canvas.drawBitmap(bmp,null,rectF, paint);
			}

			if(((RecommendItemList)list).m_currentSel == index)
			{
				paint.reset();
				paint.setAntiAlias(true);
				paint.setColor(0xffe75988);
				paint.setStyle(Paint.Style.STROKE);
				paint.setStrokeWidth(ShareData.PxToDpi_xhdpi(4));
//				RectF rectF = new RectF(item_left + ShareData.PxToDpi_xhdpi(2),ShareData.PxToDpi_xhdpi(2),item_left + img_w - ShareData.PxToDpi_xhdpi(2),item_height - ShareData.PxToDpi_xhdpi(2));
//				canvas.drawRect(rectF,paint);

				RectF rectF = new RectF(ShareData.PxToDpi_xhdpi(2),ShareData.PxToDpi_xhdpi(2),img_w - ShareData.PxToDpi_xhdpi(2),item_height - ShareData.PxToDpi_xhdpi(2));
				Bitmap tempBmp = Bitmap.createBitmap(img_w,item_height,Config.ARGB_8888);
				Canvas tempCanvas = new Canvas(tempBmp);
				tempCanvas.drawRect(rectF,paint);
				cn.poco.advanced.ImageUtils.AddSkin(list.getContext(),tempBmp);
				canvas.drawBitmap(tempBmp,item_left,0,null);
				if(tempBmp != null)
				{
					tempBmp.recycle();
					tempBmp = null;
				}
			}

			canvas.restore();
		}
	}

	public static class DownloadItemInfo extends ItemInfo
	{
		public static final int DOWNLOAD_ITEM_URI = 0xFFFFFFF1;

		protected Activity m_ac;
		//protected Bitmap m_downloadMoreTip;
		protected int m_num = 0;
		public boolean m_hasRecommend = false;

		public DownloadItemInfo(Activity ac, RecommendItemConfig config)
		{
			super(config);

			m_ac = ac;
			m_uri = DOWNLOAD_ITEM_URI;
		}

		@Override
		public void OnDraw(FastHSVCore100 list, Canvas canvas, int index)
		{
			final RecommendItemConfig config = (RecommendItemConfig)m_config;
			final Paint paint = config.temp_paint;
			final int item_left = config.def_item_left;
			final int img_w = config.def_img_w;
			//final int img_h = config.def_img_h;
			final int img_x = config.def_img_x;
			final int img_y = config.def_img_y;

			if(m_hasRecommend)
			{
				//画图片
				Bitmap bmp = ((RecommendItemList)list).m_loader.GetImg(m_uri, true);
				if(bmp == null)
				{
					((RecommendItemList)list).m_loader.PushImg(m_uri, m_logo);
					bmp = config.m_defImgBmp;
				}
				if(bmp != null)
				{
					paint.reset();
					paint.setAntiAlias(true);
					paint.setFilterBitmap(true);
					canvas.drawBitmap(bmp, 0, img_y, paint);
				}
			}
			else
			{
				super.OnDraw(list, canvas, index);
			}
			if(m_num != 0)
			{
				if(config.m_downloadMoreBkBmp == null)
				{
					config.m_downloadMoreBkBmp = BitmapFactory.decodeResource(list.getResources(), config.def_download_more_num_res);
				}
				if(config.m_downloadMoreBkBmp != null)
				{
					paint.reset();
					paint.setAntiAlias(true);
					paint.setFilterBitmap(true);
					float x;
					float y;
					if(m_hasRecommend)
					{
						x = img_x + img_w - config.m_downloadMoreBkBmp.getWidth() * 1.25f;
						y = img_y + config.m_downloadMoreBkBmp.getHeight() / 4f;
						canvas.drawBitmap(config.m_downloadMoreBkBmp, x, y, paint);
					}
					else
					{
						x = item_left + img_x + img_w - config.m_downloadMoreBkBmp.getWidth() * 1.25f;
						y = img_y + config.m_downloadMoreBkBmp.getHeight() / 4f;
						canvas.drawBitmap(config.m_downloadMoreBkBmp, x, y, paint);
					}

					paint.reset();
					paint.setAntiAlias(true);
					paint.setFilterBitmap(true);
					paint.setColor(0xFFFFFFFF);
					paint.setTextSize(config.def_download_more_num_size);
					paint.setTextAlign(Align.CENTER);
					Paint.FontMetrics fontMetrics = paint.getFontMetrics();
					canvas.drawText(Integer.toString(m_num), x + config.m_downloadMoreBkBmp.getWidth() / 2f, y + config.m_downloadMoreBkBmp.getHeight() / 2f + Math.abs(fontMetrics.ascent * 0.9f) / 2f, paint);
				}
			}
		}

		@Override
		public int GetCurrentW(FastHSVCore100 list)
		{
			if(m_hasRecommend)
			{
				return ((RecommendItemConfig)m_config).def_item_width + ((RecommendItemConfig)m_config).def_item_right;
			}
			else
			{
				return super.GetCurrentW(list);
			}
		}

		@Override
		public int GetFixW(FastHSVCore100 list)
		{
			return GetCurrentW(list);
		}

		@Override
		public boolean IsThouch(FastHSVCore100 list, float x, float y)
		{
			if(m_hasRecommend)
			{
				boolean out = false;

				float tx = x - m_x;
				if(tx < ((RecommendItemConfig)m_config).def_item_width)
				{
					out = true;
				}

				return out;
			}
			else
			{
				return super.IsThouch(list, x, y);
			}
		}

		/*public void SetDownloadMoreTipItem(Object res)
		{
			if(m_downloadMoreTip != null)
			{
				m_downloadMoreTip.recycle();
				m_downloadMoreTip = null;
			}

			if(res instanceof Bitmap)
			{
				m_downloadMoreTip = (Bitmap)res;
			}
			else if(res instanceof String)
			{
				m_downloadMoreTip = MakeBmpV2.DecodeXHDpiResource(m_ac, res);
			}
			else if(res instanceof Integer)
			{
				m_downloadMoreTip = BitmapFactory.decodeResource(m_ac.getResources(), (Integer)res);
			}
		}*/

		public void SetNum(int num)
		{
			//System.out.println("num: " + num);
			m_num = num;
		}

		@Override
		public void ClearAll()
		{
			/*if(m_downloadMoreTip != null)
			{
				m_downloadMoreTip.recycle();
				m_downloadMoreTip = null;
			}*/
		}
	}

	public static class RecommendItemInfo extends ItemInfo
	{
		public static final int RECOMMEND_ITEM_URI = 0xFFFFFFF3;
		protected static final int BLUR_COLOR = 0x40000000;
		protected static final int BLUR_W = 6;
		protected static final int SPLIT_W = 2;
		protected static final int SPACE_W = 12;

		protected Activity m_ac;
		protected Object[] m_logos;
		protected Bitmap m_logoBmp;

		protected int m_w = -1;

		public RecommendItemInfo(Activity ac, RecommendItemConfig config)
		{
			super(config);

			m_ac = ac;
			m_uri = RECOMMEND_ITEM_URI;
		}

		@Override
		public void OnDraw(FastHSVCore100 list, Canvas canvas, int index)
		{
			if(m_logoBmp == null)
			{
				m_logoBmp = MakeLogoBmp();
			}
			if(m_logoBmp != null)
			{
				canvas.drawBitmap(m_logoBmp, (((RecommendItemConfig)m_config).def_item_left) << 1, 0, null);
			}
		}

		public void SetLogos(Object[] arr)
		{
			m_logos = arr;

			m_w = -1;
			if(m_logoBmp != null)
			{
				m_logoBmp.recycle();
				m_logoBmp = null;
			}
		}

		protected Bitmap MakeLogoBmp()
		{
			Bitmap out = null;

			if(m_logos != null && m_logos.length > 0)
			{
				final RecommendItemConfig config = (RecommendItemConfig)m_config;
				final int item_height = config.def_item_height;
				final int img_w = config.def_img_w;
				final int img_h = config.def_img_h;
				final int img_y = config.def_img_y;

				int item_w = GetBmpW();
				if(item_w > 0)
				{
					out = Bitmap.createBitmap(item_w, item_height, Config.ARGB_8888);
					Canvas canvas = new Canvas(out);
					//画分割线
					{
						Bitmap temp = BitmapFactory.decodeResource(m_ac.getResources(), R.drawable.photofactory_download_logo_split);
						if(temp != null)
						{
							int x = out.getWidth() - temp.getWidth();
							int y = img_y;
							canvas.drawBitmap(temp, x, y, null);
						}
					}

					//画背景
					{
						int w = out.getWidth() - ShareData.PxToDpi_xhdpi(SPLIT_W);
						int h = img_h;
						int x = 0;
						int y = img_y;
						Paint pt = new Paint();
						pt.setStyle(Paint.Style.FILL);
						pt.setColor(0x80FFFFFF);
						pt.setAntiAlias(true);
						canvas.drawRoundRect(new RectF(x, y, x + w, y + h), config.def_img_round_size, config.def_img_round_size, pt);
					}

					//画图
					float s = 106f / 120f;
					int endW = ShareData.PxToDpi_xhdpi(SPLIT_W) + ShareData.PxToDpi_xhdpi(SPACE_W);
					int len = m_logos.length;
					if(len > 3)
					{
						len = 3;
					}
					if(len >= 3)
					{
						int w = (int)(img_w * s * s + 0.5f);
						int h = (int)(img_h * s * s + 0.5f);
						int x = out.getWidth() - endW - w;
						int y = img_y + (img_h - h) / 2;

						//画阴影
						Paint pt = new Paint();
						pt.setStyle(Paint.Style.FILL);
						pt.setColor(BLUR_COLOR);
						pt.setStrokeCap(Paint.Cap.SQUARE);
						pt.setStrokeJoin(Paint.Join.MITER);
						pt.setMaskFilter(new BlurMaskFilter(ShareData.PxToDpi_xhdpi(BLUR_W), BlurMaskFilter.Blur.OUTER));
						canvas.drawRect(x, y, x + w, y + h, pt);

						//画图
						Bitmap temp = MakeLogoBmp1(m_logos[2], w, h, config.def_img_round_size);
						if(temp != null)
						{
							canvas.drawBitmap(temp, x, y, null);
						}

						endW += ShareData.PxToDpi_xhdpi(SPACE_W);
					}
					if(len >= 2)
					{
						int w = (int)(img_w * s + 0.5f);
						int h = (int)(img_h * s + 0.5f);
						int x = out.getWidth() - endW - w;
						int y = img_y + (img_h - h) / 2;

						//画阴影
						Paint pt = new Paint();
						pt.setStyle(Paint.Style.FILL);
						pt.setColor(BLUR_COLOR);
						pt.setStrokeCap(Paint.Cap.SQUARE);
						pt.setStrokeJoin(Paint.Join.MITER);
						pt.setMaskFilter(new BlurMaskFilter(ShareData.PxToDpi_xhdpi(BLUR_W), BlurMaskFilter.Blur.OUTER));
						canvas.drawRect(x, y, x + w, y + h, pt);

						//画图
						Bitmap temp = MakeLogoBmp1(m_logos[1], w, h, config.def_img_round_size);
						if(temp != null)
						{
							canvas.drawBitmap(temp, x, y, null);
						}

						endW += ShareData.PxToDpi_xhdpi(SPACE_W);
					}
					{
						int w = img_w;
						int h = img_h;
						int x = 0;
						int y = img_y;

						//画阴影
						Paint pt = new Paint();
						pt.setStyle(Paint.Style.FILL);
						pt.setColor(BLUR_COLOR);
						pt.setStrokeCap(Paint.Cap.SQUARE);
						pt.setStrokeJoin(Paint.Join.MITER);
						pt.setMaskFilter(new BlurMaskFilter(ShareData.PxToDpi_xhdpi(BLUR_W), BlurMaskFilter.Blur.OUTER));
						canvas.drawRect(w - 10, y + ShareData.PxToDpi_xhdpi(BLUR_W), w, y + h - ShareData.PxToDpi_xhdpi(BLUR_W), pt);

						//画图
						Bitmap temp = MakeLogoBmp1(m_logos[0], w, h, config.def_img_round_size);
						if(temp != null)
						{
							canvas.drawBitmap(temp, x, y, null);
						}
					}
				}
			}

			return out;
		}

		protected Bitmap MakeLogoBmp1(Object res, int w, int h, float px)
		{
			Bitmap out = null;

			if(res instanceof Integer)
			{
				out = BitmapFactory.decodeResource(m_ac.getResources(), (Integer)res);
			}
			else if(res instanceof String)
			{
				out = BitmapFactory.decodeFile((String)res);
			}
			if(out != null)
			{
				Bitmap temp = out;

				out = Bitmap.createBitmap(w, h, Config.ARGB_8888);
				Canvas canvas = new Canvas(out);
				Paint pt = new Paint();
				pt.setColor(0xFFFFFFFF);
				pt.setAntiAlias(true);
				pt.setFilterBitmap(true);
				pt.setStyle(Paint.Style.FILL);
				if(px > 0)
				{
					canvas.drawRoundRect(new RectF(0, 0, w << 1, h), px, px, pt);
				}
				else
				{
					canvas.drawRect(0, 0, w, h, pt);
				}

				pt.reset();
				pt.setAntiAlias(true);
				pt.setFilterBitmap(true);
				pt.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
				Matrix m = new Matrix();
				float s;
				{
					float s1 = (float)w / (float)temp.getWidth();
					float s2 = (float)h / (float)temp.getHeight();
					s = s1 > s2 ? s1 : s2;
				}
				m.postTranslate((w - temp.getWidth()) / 2f, (h - temp.getHeight()) / 2f);
				m.postScale(s, s, w / 2f, h / 2f);
				canvas.drawBitmap(temp, m, pt);

				if(out != temp)
				{
					temp.recycle();
					temp = null;
				}
			}

			return out;
		}

		public int GetBmpW()
		{
			int out = 0;

			if(m_logos != null && m_logos.length > 0)
			{
				out += ((RecommendItemConfig)m_config).def_img_w;
				int len = m_logos.length;
				if(len > 3)
				{
					len = 3;
				}
				if(len == 3)
				{
					out += ShareData.PxToDpi_xhdpi(SPACE_W) * 3;
				}
				else if(len == 2)
				{
					out += ShareData.PxToDpi_xhdpi(SPACE_W) * 2;
				}
				else
				{
					out += ShareData.PxToDpi_xhdpi(SPACE_W);
				}
				out += ShareData.PxToDpi_xhdpi(SPLIT_W);
			}

			return out;
		}

		@Override
		public int GetCurrentW(FastHSVCore100 list)
		{
			if(m_w < 0)
			{
				m_w = 0;
				if(m_logos != null && m_logos.length > 0)
				{
					m_w += (((RecommendItemConfig)m_config).def_item_left) << 1;
					m_w += GetBmpW();
				}
			}
			return m_w;
		}

		@Override
		public int GetFixW(FastHSVCore100 list)
		{
			return GetCurrentW(list);
		}

		@Override
		public boolean IsThouch(FastHSVCore100 list, float x, float y)
		{
			boolean out = false;

			float tx = x - m_x;
			if(tx > ((RecommendItemConfig)m_config).def_item_left && tx < ((RecommendItemConfig)m_config).def_item_left + GetBmpW())
			{
				out = true;
			}

			return out;
		}

		@Override
		public void ClearAll()
		{
			if(m_logoBmp != null)
			{
				m_logoBmp.recycle();
				m_logoBmp = null;
			}
		}
	}
}
