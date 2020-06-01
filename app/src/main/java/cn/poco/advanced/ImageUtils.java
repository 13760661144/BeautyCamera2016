package cn.poco.advanced;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

import cn.poco.album.model.PhotoInfo;
import cn.poco.camera.ImageFile2;
import cn.poco.camera.LayoutMode;
import cn.poco.camera.RotationImg2;
import cn.poco.resource.FrameRes;
import cn.poco.system.SysConfig;
import cn.poco.tianutils.MakeBmp;
import cn.poco.tianutils.MakeBmpV2;
import cn.poco.utils.Utils;

/**
 * Created by Raining on 2016/12/7.
 * 图片裁剪/处理方法
 */

public class ImageUtils
{
	public static Bitmap MakeHeadBmp(Bitmap bmp, int d, int border)
	{
		return MakeHeadBmp(bmp, d, border, 0xEEFFFFFF);
	}

	/**
	 * @param bmp
	 * @param d           直径
	 * @param border      正数:内边距,直径为d
	 *                    负数:外边距,直接为d-2×border
	 * @param borderColor
	 * @return
	 */
	public static Bitmap MakeHeadBmp(Bitmap bmp, int d, int border, int borderColor)
	{
		Bitmap out = null;

		if(bmp != null && d > 0)
		{
			int realD;
			if(border < 0)
			{
				realD = d - border - border;
			}
			else
			{
				realD = d;
			}
			float r = realD / 2f;

			out = Bitmap.createBitmap(realD, realD, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(out);
			Paint paint = new Paint();
			paint.setStyle(Paint.Style.FILL);
			paint.setAntiAlias(true);
			paint.setColor(0xFFFFFFFF);
			if(border < 0)
			{
				canvas.drawCircle(r, r, r + border, paint);
			}
			else
			{
				canvas.drawCircle(r, r, r, paint);
			}

			float scale1 = (float)d / bmp.getWidth();
			float scale2 = (float)d / bmp.getHeight();
			float scale = scale1 > scale2 ? scale1 : scale2;
			Matrix matrix = new Matrix();
			matrix.postTranslate((realD - bmp.getWidth()) / 2f, (realD - bmp.getHeight()) / 2f);
			matrix.postScale(scale, scale, r, r);
			paint.reset();
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
			paint.setFilterBitmap(true);
			canvas.drawBitmap(bmp, matrix, paint);

			if(border < 0 || border > 0 && border < d)
			{
				paint.reset();
				paint.setStyle(Paint.Style.FILL);
				paint.setColor(borderColor);
				paint.setAntiAlias(true);
				paint.setFilterBitmap(true);
				Path path = new Path();
				path.setFillType(Path.FillType.EVEN_ODD);
				path.addCircle(r, r, r, Path.Direction.CW);
				path.addCircle(r, r, r - Math.abs(border), Path.Direction.CW);
				canvas.drawPath(path, paint);
			}
		}

		return out;
	}

	/**
	 * 根据要求获取最合适比例
	 *
	 * @param context
	 * @param imgs
	 * @param layoutMode
	 * @return
	 */
	public static float GetImgScale(Context context, Object imgs, int layoutMode)
	{
		float scale = 0;

		Object data = imgs;
		int rotate = 0;
		if(imgs instanceof ImageFile2)
		{
			imgs = ((ImageFile2)imgs).GetRawImg();
		}
		if(imgs instanceof RotationImg2[])
		{
			data = ((RotationImg2[])imgs)[0].m_img;
			rotate = ((RotationImg2[])imgs)[0].m_degree;
		}
		rotate = rotate / 90 * 90;

		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		if(data instanceof String)
		{
			cn.poco.imagecore.Utils.DecodeFile((String)data, opts, false);
		}
		else if(data instanceof Integer)
		{
			BitmapFactory.decodeResource(context.getResources(), (Integer)data, opts);
		}
		else if(data instanceof byte[])
		{
			BitmapFactory.decodeByteArray((byte[])data, 0, ((byte[])data).length, opts);
		}
		int imgW = opts.outWidth;
		int imgH = opts.outHeight;

		if(rotate % 180 != 0)
		{
			imgW += imgH;
			imgH = imgW - imgH;
			imgW -= imgH;
		}

		switch(layoutMode)
		{
			case LayoutMode.LAYOUT_16_9:
				if(imgW > imgH)
				{
					scale = 16f / 9f;
				}
				else
				{
					scale = 9f / 16f;
				}
				break;

			case LayoutMode.LAYOUT_4_3:
				if(imgW > imgH)
				{
					scale = 4f / 3f;
				}
				else
				{
					scale = 3f / 4f;
				}
				break;

			case LayoutMode.LAYOUT_1_1:
				scale = 1f;
				break;

			default:
				scale = -1f;
				break;
		}

		return scale;
	}

	public static Object GetFrameRes(Object info, float w_h_s)
	{
		Object out = null;

		ArrayList<Object> objs = new ArrayList<Object>();
		ArrayList<Float> ss = new ArrayList<Float>();
		if(info instanceof FrameRes)
		{
			FrameRes finfo = (FrameRes)info;

			if(!AdvancedResMgr.IsNull(finfo.f16_9))
			{
				objs.add(finfo.f16_9);
				ss.add(16f / 9f);
			}
			if(!AdvancedResMgr.IsNull(finfo.f4_3))
			{
				objs.add(finfo.f4_3);
				ss.add(4f / 3f);
			}
			if(!AdvancedResMgr.IsNull(finfo.f1_1))
			{
				objs.add(finfo.f1_1);
				ss.add(1f);
			}
			if(!AdvancedResMgr.IsNull(finfo.f3_4))
			{
				objs.add(finfo.f3_4);
				ss.add(3f / 4f);
			}
			if(!AdvancedResMgr.IsNull(finfo.f9_16))
			{
				objs.add(finfo.f9_16);
				ss.add(9f / 16f);
			}
		}

		int index = cn.poco.tianutils.ImageUtils.GetScale(w_h_s, ss);
		if(index >= 0)
		{
			out = objs.get(index);
		}

		return out;
	}

	/**
	 * 生成边框
	 *
	 * @param context
	 * @param info
	 * @param thumbW
	 * @param thumbH
	 * @param frW
	 * @param frH
	 * @param params  [0]name [1]text
	 * @return
	 */
	public static Bitmap MakeFrame(Context context, Object info, int thumbW, int thumbH, int frW, int frH, String... params)
	{
		Bitmap out = null;

		if(info instanceof FrameRes)
		{
			Object img = GetFrameRes(info, (float)thumbW / (float)thumbH);
			Bitmap temp = cn.poco.imagecore.Utils.DecodeImage(context, img, 0, -1, frW, frH);
			if(temp == null)
			{
				RuntimeException ex = new RuntimeException("MyLog--frame Image does not exist! path:" + img);
				throw ex;
			}
			out = MakeBmp.CreateBitmap(temp, frW, frH, -1, 0, Bitmap.Config.ARGB_8888);
			temp.recycle();
			temp = null;
		}

		return out;
	}

	public static Bitmap MakeBmp(Context context, Object res, int frW, int frH)
	{
		Bitmap out = null;

		if(res instanceof RotationImg2[])
		{
			res = ((RotationImg2[])res)[0];
		}
		else if(res instanceof PhotoInfo)
		{
			res = Utils.Path2ImgObj(((PhotoInfo)res).getImagePath());
		}

		if(res instanceof RotationImg2)
		{
			int rotation = ((RotationImg2)res).m_degree;
			int flip = ((RotationImg2)res).m_flip;
			Bitmap temp = cn.poco.imagecore.Utils.DecodeImage(context, ((RotationImg2)res).m_img, rotation, -1, frW, frH);
			if(temp != null)
			{
				if(rotation == 0 && flip == MakeBmpV2.FLIP_NONE && temp.getWidth() <= frW && temp.getHeight() <= frH)
				{
					if(temp.isMutable())
					{
						out = temp;
					}
					else
					{
						out = temp.copy(Bitmap.Config.ARGB_8888, true);
						temp.recycle();
						temp = null;
					}
				}
				else
				{
					out = MakeBmpV2.CreateBitmapV2(temp, rotation, flip, -1, frW, frH, Bitmap.Config.ARGB_8888);
					temp.recycle();
					temp = null;
				}
			}
		}
		else if(res instanceof ImageFile2)
		{
			Bitmap temp = ((ImageFile2)res).MakeBmp(context, frW, frH);
			if(temp.isMutable())
			{
				out = temp;
			}
			else
			{
				out = temp.copy(Bitmap.Config.ARGB_8888, true);
				temp.recycle();
				temp = null;
			}
		}
		else if(res instanceof String)
		{
			out = cn.poco.imagecore.Utils.DecodeImage(context, res, 0, -1, frW, frH);
		}

		return out;
	}

	public static boolean AvailableImg(Object obj)
	{
		boolean out = false;

		if(obj instanceof RotationImg2)
		{
			obj = ((RotationImg2)obj).m_img;
		}
		else if(obj instanceof RotationImg2[])
		{
			obj = ((RotationImg2[])obj)[0].m_img;
		}
		else if(obj instanceof PhotoInfo)
		{
			obj = ((PhotoInfo)obj).getImagePath();
		}

		if(obj instanceof String)
		{
			File file = new File((String)obj);
			if(file.exists() && file.isFile() && file.length() > 0)
			{
				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inJustDecodeBounds = true;
				BitmapFactory.decodeFile((String)obj, opts);
				if(opts.outWidth > 0 && opts.outHeight > 0)
				{
					out = true;
				}
			}
		}

		return out;
	}

	public static void RemoveSkin(Context context, ImageView view)
	{
		if(view != null)
		{
			view.clearColorFilter();
		}
	}

	public static void AddSkin(Context context, ImageView view)
	{
		if(SysConfig.s_skinColor != 0)
		{
			AddSkinColor(context, view, SysConfig.s_skinColor);
		}
	}

	public static void AddSkin(Drawable drawable) {
        if (SysConfig.s_skinColor != 0) {
            drawable.setColorFilter(SysConfig.s_skinColor, PorterDuff.Mode.SRC_IN);
		}
	}

	public static void AddSkinColor(Context context, ImageView view, int color)
	{
		if(view != null)
		{
			view.setColorFilter(color, PorterDuff.Mode.SRC_IN);
		}
	}

	public static Bitmap AddSkin(Context context, Bitmap bmp)
	{
		Bitmap out = bmp;

		if(out != null && SysConfig.s_skinColor != 0)
		{
			if(!out.isMutable())
			{
				out = out.copy(Bitmap.Config.ARGB_8888, true);
			}
			Canvas canvas = new Canvas(out);
			canvas.drawColor(SysConfig.s_skinColor, PorterDuff.Mode.SRC_IN);
		}
		return out;
	}

	public static int GetSkinColor(int defaultColor, float alpha)
	{
		int color = SysConfig.s_skinColor;
		if(color == 0)
		{
			color = defaultColor;
		}

		if(alpha == 1.0f)
		{
			return color;
		}
		else
		{
			return (color & 0xffffff) | (((int)(255 * alpha)) << 24);
		}
	}

	public static int GetColorAlpha(@ColorInt int color, float alpha)
	{
		if (color == 0)
		{
			color = SysConfig.s_skinColor;
		}
		if (alpha == 1.0f)
		{
			return color;
		}
		else
		{
			return (color & 0xffffff) | (((int) (255 * alpha)) << 24);
		}
	}

	public static int GetSkinColor()
	{
		return SysConfig.s_skinColor;
	}

	/**
	 * @param defaultColor 按钮样式
	 */
	public static int GetSkinColor(int defaultColor)
	{
		if(SysConfig.s_skinColor == 0)
		{
			return defaultColor;
		}
		return SysConfig.s_skinColor;
	}

	/**
	 * @param defaultColor1 渐变1
	 */
	public static int GetSkinColor1(int defaultColor1)
	{
		if(SysConfig.s_skinColor1 == 0 || SysConfig.s_skinColor2 == 0)
		{
			return defaultColor1;
		}
		return SysConfig.s_skinColor1;
	}

	/**
	 * @param defaultColor2 渐变2
	 */
	public static int GetSkinColor2(int defaultColor2)
	{
		if(SysConfig.s_skinColor1 == 0 || SysConfig.s_skinColor2 == 0)
		{
			return defaultColor2;
		}
		return SysConfig.s_skinColor2;
	}


	public static void AddSkin(Context context, ImageView view, int defaultColor)
	{
		if(view != null)
		{
			if(SysConfig.s_skinColor != 0)
			{
				view.setColorFilter(SysConfig.s_skinColor, PorterDuff.Mode.SRC_IN);
			}
			else
			{
				view.setColorFilter(defaultColor, PorterDuff.Mode.SRC_IN);
			}
		}
	}

	public static Bitmap AddSkin(Context context, Bitmap bmp, int defaultColor)
	{
		Bitmap out = bmp;

		if(out != null)
		{
			if(!out.isMutable())
			{
				out = out.copy(Bitmap.Config.ARGB_8888, true);
			}
			Canvas canvas = new Canvas(out);

			if(SysConfig.s_skinColor == 0)
			{
				canvas.drawColor(defaultColor, PorterDuff.Mode.SRC_IN);
			}
			else
			{
				canvas.drawColor(SysConfig.s_skinColor, PorterDuff.Mode.SRC_IN);
			}
		}
		return out;
	}

	/**
	 *
	 * @param context
	 * @param bmp
	 * @param dstColor 混合色
	 * @return
	 */
	public static Bitmap AddSkin2(Context context, Bitmap bmp, int dstColor)
	{
		Bitmap out = bmp;

		if(out != null)
		{
			if(!out.isMutable())
			{
				out = out.copy(Bitmap.Config.ARGB_8888, true);
			}

			Canvas canvas = new Canvas(out);
			canvas.drawColor(dstColor, PorterDuff.Mode.SRC_IN);
		}
		return out;
	}
}









