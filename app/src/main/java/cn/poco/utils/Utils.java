package cn.poco.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.adnonstop.admasterlibs.AdUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.poco.camera.RotationImg2;
import cn.poco.framework.MyFramework2App;
import cn.poco.imagecore.ImageUtils;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.MakeBmp;
import cn.poco.tianutils.NetCore2;
import cn.poco.tianutils.ShareData;
import cn.poco.video.NativeUtils;

public class Utils
{


	/**
	 * m_img需要自行转换
	 *
	 * @param path
	 * @return
	 */
	public static RotationImg2 Path2ImgObj(String path)
	{
		RotationImg2 out = new RotationImg2();
		out.m_orgPath = path;
		out.m_img = path;
		int[] vs = CommonUtils.GetImgInfo(path);
		out.m_degree = vs[0];
		out.m_flip = vs[1];

		return out;
	}

	/**
	 * 插入数据库;
	 *
	 * @param picPath
	 * @return
	 */
	protected synchronized static Uri InsertImgToSys(Context context, String picPath)
	{
		File file = new File(picPath);
		if(!file.exists())
		{
			return null;
		}
		ContentResolver resolver = context.getContentResolver();
		int degree = Path2ImgObj(picPath).m_degree;
		long dateTaken = System.currentTimeMillis();
		long size = file.length();
		String fileName = file.getName();
		ContentValues values = new ContentValues(7);
		values.put(Images.Media.DATE_TAKEN, dateTaken);//时间;
		values.put(Images.Media.DATE_MODIFIED, dateTaken / 1000);//时间;
		values.put(Images.Media.DATE_ADDED, dateTaken / 1000);//时间;
		values.put(ImageColumns.DATA, picPath);//路径;
		values.put(Images.Media.DISPLAY_NAME, fileName);//文件名;
		values.put(Images.Media.ORIENTATION, degree);//角度;
		values.put(Images.Media.SIZE, size);//图片的大小;
		Uri uri = null;
		try
		{
			if(resolver != null)
			{
				uri = resolver.insert(Images.Media.EXTERNAL_CONTENT_URI, values);
			}
		}
		catch(Throwable th)
		{
		}
		return uri;
	}

	public synchronized static Uri InsertVideoToSys(@NonNull Context context, String videoPath)
	{
		Uri uri = null;
		File file = new File(videoPath);
		if (file.exists())
		{
			long size = file.length();
			String fileName = file.getName();
			long dateTaken = System.currentTimeMillis();

			ContentValues values = new ContentValues();
			values.put(MediaStore.Video.Media.DATA, videoPath); 				// 路径;
			values.put(MediaStore.Video.Media.TITLE, fileName); 				// 标题;
			values.put(MediaStore.Video.Media.SIZE, size); 						// 视频大小;
			values.put(MediaStore.Video.Media.DATE_TAKEN, dateTaken); 			// 时间;
			values.put(MediaStore.Video.Media.DISPLAY_NAME, fileName);			// 文件名;
			values.put(MediaStore.Video.Media.DATE_MODIFIED, dateTaken / 1000);	// 修改时间;
			values.put(MediaStore.Video.Media.DATE_ADDED, dateTaken / 1000); 	// 添加时间;
			values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");			// 数据格式
			float duration = 0;
			try
			{
				duration = NativeUtils.getDurationFromFile(videoPath);
			}
			catch (Throwable e)
			{
				e.printStackTrace();
			}
			values.put(MediaStore.Video.Media.DURATION, duration * 1000L);				// 视频时长

			ContentResolver resolver = context.getContentResolver();
			if (resolver != null)
			{
				try
				{
					uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
				}
				catch (Throwable e)
				{
					e.printStackTrace();
					uri = null;
				}
			}
		}
		return uri;
	}

	public synchronized static void FileScan(final Context context, final String file)
	{
		if(Looper.myLooper() != Looper.getMainLooper())
		{
			if(context instanceof Activity)
			{
				((Activity)context).runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						FileScan2(context, file);
					}
				});
			}
			else if(MyFramework2App.getInstance().getActivity() != null)
			{
				MyFramework2App.getInstance().getActivity().runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						FileScan2(context, file);
					}
				});
			}
		}
		else
		{
			FileScan2(context, file);
		}
	}

	/**
	 * 必须在主线程调用
	 */
	public synchronized static void FileScan2(Context context, String file)
	{
		//System.out.println("FileScan : " + file);
		if(file == null) return;
		Uri data = InsertImgToSys(context, file); //修改时间:2013年8月27日,先插入数据库;
		if(data == null)
		{
			data = Uri.parse("file://" + file);
		}
		File external = Environment.getExternalStorageDirectory();
		if(external == null) return;
		String externalDir = external.getPath();
		if(externalDir == null) return;
		if(file.startsWith(externalDir) == false) return;
		if(context != null)
		{
			//ImageStore.clearCache();
			// FIXME: 2016/12/13
			//context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));
		}
	}

	protected static String MakePhotoName(float scale_w_h, int mode)
	{
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
		String strDate = df.format(date);
		String strRand = Integer.toString((int)(Math.random() * 100));
		if(strRand.length() < 4)
		{
			strRand = "0000".substring(strRand.length()) + strRand;
		}
//		if(PocoCamera.main == null)
//		{
//			return strDate + strRand + ".jpg";
//		}
		int layout = 0;
		float r4_3 = 4.0f / 3.0f;
		float r3_4 = 3.0f / 4.0f;
		float r16_9 = 16.0f / 9.0f;
		float r9_16 = 9.0f / 16.0f;
		float r = scale_w_h;
		if(r > 0.95 && r < 1.05) layout = 3;
		else if(r > r4_3 - 0.05f && r < r4_3 + 0.05f) layout = 1;
		else if(r > r16_9 - 0.05f && r < r16_9 + 0.05f) layout = 4;
		else if(r > r3_4 - 0.05f && r < r3_4 + 0.05f) layout = 2;
		else if(r > r9_16 - 0.05f && r < r9_16 + 0.05f) layout = 5;
		//if(PocoCamera.main.getLayoutMode() == 6)
		//{
		//	layout = 6;
		//}

		String color = "000";
		String decorate = "000";
		String frame = "000";
		/*if(info != null)
		{
			if(info.effect != -1)
			{
				color = Integer.toString(info.effect & 0x0fff, 16);
				if(color.length() < 3)
				{
					color = "000".substring(color.length()) + color;
				}
			}
			if(info.frame != -1)
			{
				frame = Integer.toString(info.frame & 0x0fff, 16);
				if(frame.length() < 3)
				{
					frame = "000".substring(frame.length()) + frame;
				}
			}
		}*/

		String strMode = Integer.toHexString(mode);
		String type = strMode + Integer.toString(layout);
		type += "-" + color;
		type += decorate;
		type += frame;
		String str = "NA" + strDate + strRand + "-" + type + ".jpg";
		return str;
	}

	/**
	 * 构造一个图片保存的路径
	 *
	 * @param scale_w_h w/h比例
	 * @return
	 */
	public static String MakeSavePhotoPath(Context context, float scale_w_h)
	{
		String out = SettingInfoMgr.GetSettingInfo(context).GetPhotoSavePath();

		out += File.separator + MakePhotoName(scale_w_h, 0);

		return out;
	}

	/**
	 * 临时图片,保存为FASTBMP格式
	 *
	 * @param bmp
	 * @param path
	 * @return
	 */
	public static boolean SaveTempImg(Bitmap bmp, String path)
	{
		boolean out = false;

		if(bmp != null)
		{
			Bitmap temp = bmp;
			if(!bmp.isMutable() || bmp.getConfig() != Config.ARGB_8888)
			{
				temp = bmp.copy(Config.ARGB_8888, true);
			}

			//if(ImageUtils.WriteFastBmp(temp, 100, path) == 0)
			//2016/12/15 图片太大直接保持bmp也是很慢所以保存为jpg
			if(ImageUtils.WriteJpg(temp, 100, path) == 0)
			{
				out = true;
			}
			if(temp != bmp)
			{
				temp.recycle();
				temp = null;
			}
		}

		return out;
	}

	/**
	 * 保存图片并更新相册
	 *
	 * @param context
	 * @param bmp
	 * @param path    为null时自动创建一个路径
	 * @param quality 质量(1-100)
	 * @return
	 */
	public static String SaveImg(Context context, Bitmap bmp, @Nullable String path, int quality)
	{
		return SaveImg(context, bmp, path, quality, true);
	}

	/**
	 * 保存图片
	 *
	 * @param context
	 * @param bmp
	 * @param path        为null时自动创建一个路径
	 * @param quality     质量(1-100)
	 * @param updateAlbum
	 * @return
	 */
	public static String SaveImg(Context context, Bitmap bmp, @Nullable String path, int quality, boolean updateAlbum)
	{
		String out = null;

		if(context != null && bmp != null && bmp.getWidth() > 0 && bmp.getHeight() > 0)
		{
			String tempPath = path;
			if(tempPath == null)
			{
				tempPath = MakeSavePhotoPath(context, (float)bmp.getWidth() / (float)bmp.getHeight());
			}

			Bitmap temp = bmp;
			if(!bmp.isMutable() || bmp.getConfig() != Config.ARGB_8888)
			{
				temp = bmp.copy(Config.ARGB_8888, true);
			}

			if(ImageUtils.WriteJpg(temp, quality, tempPath) == 0)
			{
				out = tempPath;
				if(updateAlbum)
				{
					FileScan(context, out);
				}
			}
			if(temp != bmp)
			{
				temp.recycle();
				temp = null;
			}
		}

		return out;
	}

	/**
	 * 主要用于触发统计
	 *
	 * @param url
	 */
	public static void UrlTrigger(final Context context, final String url)
	{
		if(context != null && url != null && url.length() > 0)
		{
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					String myUrl = AdUtils.AdDecodeUrl(context, url);
					NetCore2 net = new MyNetCore(context);
					net.HttpGet(myUrl);
					net.ClearAll();
				}
			}).start();
		}
	}

	private static final long MAX_SHARE_PHOTO_SIZE = 500 * 1024;

	/**
	 * 获取小于{@link Utils#MAX_SHARE_PHOTO_SIZE}的图片,如果太大会保存为另一张图片返回(图片保存目录和正常流程保存的目录一样)
	 *
	 * @param ac
	 * @param path
	 * @return
	 */
	public static String MakeMinSizePhoto(Activity ac, String path)
	{
		String out = path;

		if(path != null)
		{
			ShareData.InitData(ac);

			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, opts);
			if(opts.outMimeType != null)
			{
				boolean newPic = false;

				if(opts.outMimeType.equals("image/jpeg") || opts.outMimeType.equals("image/png"))
				{
					File file = new File(path);
					if(file.length() > MAX_SHARE_PHOTO_SIZE)
					{
						newPic = true;
					}
				}
				else if(ImageUtils.CheckIfFastBmp(path) != 0)
				{
					newPic = true;
				}

				if(newPic)
				{
					Bitmap temp = cn.poco.imagecore.Utils.DecodeFile(path, null, true);
					if(temp != null)
					{
						Bitmap bmp = MakeBmp.CreateBitmap(temp, ShareData.m_screenHeight, ShareData.m_screenHeight, -1, 0, Config.ARGB_8888);
						temp.recycle();
						temp = null;
						String tempPath = MakeSavePhotoPath(ac, (float)bmp.getWidth() / (float)bmp.getHeight());
						if(ImageUtils.WriteJpg(bmp, 95, tempPath) == 0)
						{
							out = tempPath;
						}
					}
				}
			}
		}

		return out;
	}

	//照片加日期
	public static void attachDate(Bitmap bmp)
	{
		/*String time = DateFormat.getDateInstance(DateFormat.MEDIUM).format(new Date());
		Paint p = new Paint();
		p.setAntiAlias(true);
		//p.setColor(0xFFFFFF00);
		p.setTextSize(ShareData.PxToDpi_hdpi(16));
		p.setFakeBoldText(true);
		int textWidth = (int)(p.measureText(time) + 0.5f);
		int textHeight = (int)(p.descent() - p.ascent());
		int paintSize = 16;
		//确保文字在图片的右下角
		while(textWidth > bmp.getWidth() / 3)
		{
			paintSize--;
			p.setTextSize(ShareData.PxToDpi_hdpi(paintSize));
			p.setAntiAlias(true);
			textWidth = (int)(p.measureText(time) + 0.5f);
			textHeight = (int)(p.descent() - p.ascent());
		}

		int x = bmp.getWidth() - textWidth;
		int y = bmp.getHeight() - textHeight;
		x = x >= 0 ? x : 0;
		y = y >= 0 ? y : 0;

		int w = bmp.getWidth() - x;
		int h = bmp.getHeight() - y;
		if(w > 90) w = 90;
		if(h > 14) h = 14;
		int len = w * h;
		int[] pixels = new int[len];
		bmp.getPixels(pixels, 0, w, x, y, w, h);
		int r = 0, g = 0, b = 0;
		for(int i = 0; i < len; i++)
		{
			int pixel = pixels[i];
			r += (pixel & 0x00ff0000) >> 16;
			g += (pixel & 0x0000ff00) >> 8;
			b += (pixel & 0x000000ff);
		}
		//int color = (~((r/len)<<16|(g/len)<<8|(b/len)))|0xff000000;
		int color = (float)(r / len + g / len + b / len) / (float)765 > 0.5 ? 0xff000000 : 0xffffffff;
		p.setColor(color);

		Canvas c = new Canvas(bmp);
		c.drawText(time, x, y, p);*/

		PhotoMark.drawDataLeft(bmp);
	}

	public static Bitmap takeSmallViewScreenShot(View view, float scal)
	{
		Bitmap b = null;
		if(view.getWidth() > 0 && view.getHeight() > 0)
		{
			try
			{
				// View是你需要截图的View
				Bitmap cacheBmp = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
				view.draw(new Canvas(cacheBmp));
				if(cacheBmp != null && !cacheBmp.isRecycled())
				{
					b = Bitmap.createBitmap((int)(cacheBmp.getWidth() * scal), (int)(cacheBmp.getHeight() * scal), Config.ARGB_8888);
					Matrix matrix = new Matrix();
					matrix.setScale(scal, scal);
					Canvas cs = new Canvas(b);
					cs.drawBitmap(cacheBmp, matrix, null);
					cacheBmp.recycle();
					cacheBmp = null;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return b;
	}

	public static final int SDCARD_ERROR = 1; //sdcard错误
	public static final int SDCARD_OUT_OF_SPACE = 2; //空间不足
	public static final int SDCARD_WARNING = 4; //空间少于提示值
	public static final int SDCARD_OK = 8;

	public static int CheckSDCard(Context context)
	{
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
			int size = CommonUtils.GetSDCardAvailableSize();
			if(size < 20)
			{
				return SDCARD_OUT_OF_SPACE;
			}
			else if(size < 200)
			{
				return SDCARD_WARNING;
			}
			else
			{
				return SDCARD_OK;
			}
		}
		else
		{
			return SDCARD_ERROR;
		}
	}

	public static boolean CheckIsGif(String path)
	{
		if(path == null) return false;
		boolean out = false;
		try
		{
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, opts);
			opts.inJustDecodeBounds = false;
			if(opts.outMimeType != null)
			{
				if(opts.outMimeType.equals("image/gif"))
				{
					out = true;
				}
			}
		}
		catch(Throwable r)
		{
			out = false;
		}
		return out;
	}
}