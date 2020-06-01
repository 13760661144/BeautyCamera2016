package cn.poco.mosaic;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import cn.poco.image.PocoOilMask;
import cn.poco.resource.MosaicRes;

public class MosicBeautifyTools
{
	/**
	 * 马赛克效果
	 *
	 * @param mosicBmp
	 * @param orgBmp
	 * @param x
	 * @param y
	 * @param size
	 * @param type
	 * @return
	 */
	public static Bitmap getMosicBmp(Bitmap mosicBmp, Bitmap orgBmp, int x, int y, int size, int type)
	{
		if(size < 0)
		{
			size = 0;
		}
		if(size > 100)
		{
			size = 100;
		}
		if(type == PocoOilMask.Vangogh)
		{
			PocoOilMask.OilsMask_Vangogh(mosicBmp, orgBmp, x, y, size);
		}
		else if(type == PocoOilMask.Crayon)
		{
			PocoOilMask.OilsMask_Crayon(mosicBmp, orgBmp, x, y, size);
		}
		else if(type == PocoOilMask.Charcoal)
		{

			PocoOilMask.OilsMask_Charcoal(mosicBmp, orgBmp, x, y, size);
		}
		else if(type == PocoOilMask.Splash)
		{
			PocoOilMask.OilsMask_Splash(mosicBmp, orgBmp, x, y, size);
		}
		else if(type == PocoOilMask.Sketches01)
		{
			PocoOilMask.OilsMask_sketches01(mosicBmp, orgBmp, x, y, size);
		}
		else if(type == PocoOilMask.Sketches02)
		{
			PocoOilMask.OilsMask_sketches02(mosicBmp, orgBmp, x, y, size);
		}
		else if(type == PocoOilMask.Sketches03)
		{
			PocoOilMask.OilsMask_sketches03(mosicBmp, orgBmp, x, y, size);
		}
		else if(type == PocoOilMask.Sketches04)
		{
			PocoOilMask.OilsMask_sketches04(mosicBmp, orgBmp, x, y, size);
		}
		return mosicBmp;
	}

	//	private PorterDuffXfermode dst_over_mode = new PorterDuffXfermode(PorterDuff.Mode.DST_OVER);
	//	private PorterDuffXfermode xor_mode = new PorterDuffXfermode(PorterDuff.Mode.XOR);

	public static final int PAINT_TYPE_TILE = 0;
	public static final int PAINT_TYPE_FILL = 1;

	/**
	 * 涂鸦效果
	 *
	 * @param destBmp   当前显示的图
	 * @param maskBmp   这次绘制的轨迹
	 * @param doodleBmp 涂鸦效果
	 * @param paintInfo 素材绘制方法 例如：填充、平铺 {@link #PAINT_TYPE_FILL}
	 * @return
	 */
	public static Bitmap getDoodleBmp(Bitmap destBmp, Bitmap maskBmp, Bitmap doodleBmp, MosaicRes paintInfo)
	{
		if(doodleBmp != null)
		{
			Canvas canvas = new Canvas(destBmp);
			canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
			Matrix mMatrix = new Matrix();
			Paint mPaint = new Paint();
			mPaint.setAntiAlias(true);
			mPaint.setFilterBitmap(true);
			mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
			canvas.drawBitmap(maskBmp, mMatrix, mPaint);

			if(paintInfo.m_paintType == MosaicRes.PAINT_TYPE_TILE)
			{
				int maskWidth = maskBmp.getWidth();
				int maskHeight = maskBmp.getHeight();
				//int paintBmpSize = maskWidth > maskHeight ? maskWidth / 15 : maskHeight / 15;
				int realPaintBmpWidth = doodleBmp.getWidth();
				int realPaintBmpHeight = doodleBmp.getHeight();

				float scaleWidth = 1f * maskWidth / 1024f;
				float scaleHeight = 1f * maskHeight / 1024f;
				float scale = scaleWidth < scaleHeight ? scaleWidth : scaleHeight;

				int paintBmpWidth = (int)(realPaintBmpWidth * scale);
				int paintBmpHeight = (int)(realPaintBmpHeight * scale);

				//个数分别+1，补正误差
				int widthCount = maskWidth / paintBmpWidth + 1;
				int heightCount = maskHeight / paintBmpHeight + 1;

				//float scale = 1f * paintBmpSize / paintBmp.getWidth();
				mPaint.reset();
				mPaint.setAntiAlias(true);
				mPaint.setFilterBitmap(true);
				mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
				for(int i = 0; i < widthCount; i++)
				{
					for(int j = 0; j < heightCount; j++)
					{
						mMatrix.reset();
						mMatrix.postScale(scale, scale);
						mMatrix.postTranslate(i * paintBmpWidth, j * paintBmpHeight);
						canvas.drawBitmap(doodleBmp, mMatrix, mPaint);
					}
				}
			}
			else
			{
				int maskWidth = maskBmp.getWidth();
				int maskHeight = maskBmp.getHeight();
				int paintWidth = doodleBmp.getWidth();
				int paintHeight = doodleBmp.getHeight();
				boolean horizontalOrVerticl = maskWidth > maskHeight;
				float scale = horizontalOrVerticl ? 1f * maskWidth / paintWidth : 1f * maskHeight / paintHeight;
				mMatrix.reset();
				int type = horizontalOrVerticl ? paintInfo.horizontal_fill : paintInfo.vertical_fill;
				switch(type)
				{
					case MosaicRes.POS_START:
					{
						if(horizontalOrVerticl)
						{
							mMatrix.postTranslate((maskWidth - paintWidth) / 2, 0);
							mMatrix.postScale(scale, scale, maskWidth / 2, 0);
						}
						else
						{
							mMatrix.postTranslate(0, (maskHeight - paintHeight) / 2);
							mMatrix.postScale(scale, scale, 0, maskHeight / 2);
						}
						break;
					}

					case MosaicRes.POS_CENTER:
					{
						mMatrix.postTranslate((maskWidth - paintWidth) / 2, (maskHeight - paintHeight) / 2);
						mMatrix.postScale(scale, scale, maskWidth / 2, maskHeight / 2);
						break;
					}

					case MosaicRes.POS_END:
					{
						if(horizontalOrVerticl)
						{
							mMatrix.postTranslate((maskWidth - paintWidth) / 2, maskHeight - paintHeight);
							mMatrix.postScale(scale, scale, maskWidth / 2, maskHeight);
						}
						else
						{
							mMatrix.postTranslate(maskWidth - paintWidth, (maskHeight - paintHeight) / 2);
							mMatrix.postScale(scale, scale, maskWidth, maskHeight / 2);
						}
						break;
					}

					default:
						break;
				}

				mPaint.reset();
				mPaint.setAntiAlias(true);
				mPaint.setFilterBitmap(true);
				mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
				canvas.drawBitmap(doodleBmp, mMatrix, mPaint);
			}
		}

		return destBmp;
	}

	public static Bitmap getRubberBmp(Bitmap destBmp, Bitmap orgBmp, Bitmap maskBmp)
	{
		Canvas canvas = new Canvas(destBmp);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
		Matrix mMatrix = new Matrix();
		Paint mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setFilterBitmap(true);
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
		canvas.drawBitmap(maskBmp, mMatrix, mPaint);

		mPaint.reset();
		mPaint.setAntiAlias(true);
		mPaint.setFilterBitmap(true);
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
		canvas.drawBitmap(orgBmp, mMatrix, mPaint);

		return destBmp;
	}

}
