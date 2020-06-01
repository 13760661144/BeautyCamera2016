package cn.poco.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import cn.poco.tianutils.MakeBmpV2;

public class ImageUtil
{
	public static Bitmap createBitmapByColor(int color, int w, int h)
	{
		if(color == 0)
			return null;
		Bitmap out = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas canvas = new Canvas(out);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
		Paint pt = new Paint();
		pt.setAntiAlias(true);
		pt.setFilterBitmap(true);
		pt.setColor(color);
		pt.setStyle(Style.FILL);
		canvas.drawRect(0, 0, w, h, pt);
		return out;
	}

	public static Bitmap MakeDiffCornerRoundBmp(Bitmap bmp, float leftTopPx, float rightTopPx, float leftBottomPx, float rightBottomPx)
	{
		Bitmap out = null;
		int w = bmp.getWidth();
		int h = bmp.getHeight();

		if(bmp != null && bmp.getWidth() > 0 && bmp.getHeight() > 0 && w > 0 && h > 0)
		{
			out = Bitmap.createBitmap(w, h, Config.ARGB_8888);
			Canvas canvas = new Canvas(out);
			Paint pt = new Paint();
			pt.setColor(0xFFFFFFFF);
			pt.setAntiAlias(true);
			pt.setFilterBitmap(true);
			pt.setStyle(Style.FILL);
			Path path = RoundedRect(new RectF(0, 0, w, h), leftTopPx, rightTopPx, leftBottomPx, rightBottomPx);
			canvas.drawPath(path, pt);

			pt.reset();
			pt.setAntiAlias(true);
			pt.setFilterBitmap(true);
			pt.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
			Matrix m = new Matrix();
			float s;
			{
				float s1 = (float)w / (float)bmp.getWidth();
				float s2 = (float)h / (float)bmp.getHeight();
				s = s1 > s2 ? s1 : s2;
			}
			m.postTranslate((w - bmp.getWidth()) / 2f, (h - bmp.getHeight()) / 2f);
			m.postScale(s, s, w / 2f, h / 2f);
			canvas.drawBitmap(bmp, m, pt);
		}

		return out;
	}

	static public Path RoundedRect(RectF rect, float leftTopPx, float rightTopPx, float leftBottomPx, float rightBottomPx)
	{
		float left = rect.left;
		float top = rect.top;
		float right = rect.right;
		float bottom = rect.bottom;
		Path path = new Path();
		if (leftTopPx < 0) leftTopPx = 0;
		if (rightTopPx < 0) rightTopPx = 0;
		if (leftBottomPx < 0) leftBottomPx = 0;
		if (rightBottomPx < 0) rightBottomPx = 0;
		float width = right - left;
		float height = bottom - top;
		float widthMinusCorners = (width - rightTopPx - leftTopPx);
		float heightMinusCorners = (height - leftTopPx - leftBottomPx);

		path.moveTo(right, top + rightTopPx);
		path.rQuadTo(0, -rightTopPx, -rightTopPx, -rightTopPx);//top-right corner
		path.rLineTo(-widthMinusCorners, 0);
		path.rQuadTo(-leftTopPx, 0, -leftTopPx, leftTopPx); //top-left corner
		path.rLineTo(0, heightMinusCorners);

		widthMinusCorners = (width - rightBottomPx - leftBottomPx);
		heightMinusCorners = (height - rightBottomPx - rightTopPx);
		path.rQuadTo(0, leftBottomPx, leftBottomPx, leftBottomPx);//bottom-left corner
		path.rLineTo(widthMinusCorners, 0);
		path.rQuadTo(rightBottomPx, 0, rightBottomPx, -rightBottomPx); //bottom-right corner
		path.rLineTo(0, -heightMinusCorners);

		path.close();//Given close, last lineto can be removed.

		return path;
	}

	public static Bitmap makeCircleBmp(Bitmap bmp, int broderSize, int broderColor)
	{
		Bitmap out = null;
		if(bmp == null)
		{
			return out;
		}
		int w = bmp.getWidth() + broderSize * 2;
		int h = bmp.getHeight() + broderSize * 2;
		int bmpSize = w > h? h : w;
		out = Bitmap.createBitmap(bmpSize, bmpSize, Config.ARGB_8888);
		Canvas canvas = new Canvas(out);

		PaintFlagsDrawFilter pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
		canvas.setDrawFilter(pfd);

		Paint pt = new Paint();
		pt.setAntiAlias(true);
		pt.setFlags(Paint.ANTI_ALIAS_FLAG);
		pt.setStyle(Style.FILL);
		canvas.drawCircle(bmpSize / 2f, bmpSize / 2f, bmp.getWidth() / 2f, pt);

		pt.reset();
		pt.setAntiAlias(true);
		pt.setFilterBitmap(true);
		pt.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		Matrix m = new Matrix();
		m.postTranslate((w - bmp.getWidth()) / 2f, (h - bmp.getHeight()) / 2f);
		canvas.drawBitmap(bmp, m, pt);

		if(broderSize > 0)
		{
			pt.reset();
			pt.setColor(broderColor);
			pt.setStrokeWidth(broderSize);
			pt.setStyle(Style.STROKE);
			pt.setAntiAlias(true);
			pt.setFlags(Paint.ANTI_ALIAS_FLAG);
			canvas.drawCircle(bmpSize / 2f, bmpSize / 2f, bmp.getWidth() / 2f, pt);
		}
		return out;
	}

	public static Bitmap roundCrop(BitmapPool pool, Bitmap source, int roundSize)
	{
		if (source == null) return null;
		int size = Math.min(source.getWidth(), source.getHeight());
		int x = (source.getWidth() - size) / 2;
		int y = (source.getHeight() - size) / 2;
		Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);
		Bitmap result = pool.get(size, size, Config.ARGB_8888);
		if (result == null) {
			result = Bitmap.createBitmap(size, size, Config.ARGB_8888);
		}
		Canvas canvas = new Canvas(result);
		Paint paint = new Paint();
		paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
		paint.setAntiAlias(true);
		RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
		canvas.drawRoundRect(rectF, roundSize, roundSize, paint);
		return result;
	}

	public static Bitmap CreateShowBmp(Context context, Object obj, int width, int height) {
		Object img = obj;
		if(obj instanceof String)
		{
			File file = new File((String)obj);
			if(!file.exists())
			{
				try
				{
					InputStream is = context.getAssets().open((String)obj);
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					byte[] bytes = new byte[1024];
					while(is.read(bytes) != -1)
					{
						bout.write(bytes, 0, bytes.length);
					}
					img = bout.toByteArray();
					bout.close();
					is.close();
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		Bitmap temp = cn.poco.imagecore.Utils.DecodeImage(context, img, 0, -1, width, height);
		Bitmap out = MakeBmpV2.CreateBitmapV2(temp, 0, 0, -1, width, height, Config.ARGB_8888);
		if(temp != null && temp != out)
		{
			temp.recycle();
			temp = null;
		}
		return out;
	}

	public static Bitmap CreateCircleBmp(Context context, Object obj, int width, int height, int broderSize, int broderColor)
	{
		Bitmap out = null;
		width = width - broderSize;
		height = height - broderSize;
		Bitmap temp = CreateShowBmp(context, obj, width, height);
		if(temp != null)
		{
			out = makeCircleBmp(temp, broderSize, broderColor);
			if(temp != out)
			{
				temp.recycle();
			}
		}
		return out;
	}
}
