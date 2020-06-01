package cn.poco.beautify4.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;

import cn.poco.PhotoPicker.ImageViewer;
import cn.poco.advanced.ImageUtils;
import cn.poco.beautify4.Beautify4Page;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.MakeBmp;
import cn.poco.tianutils.UndoRedoDataMgr;

/**
 * Created by Raining on 2016/12/14.
 * 重写加载图片部分
 */

public class MyImageViewer extends ImageViewer
{
	public MyImageViewer(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public MyImageViewer(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public MyImageViewer(Context context)
	{
		super(context);
	}

	@Override
	protected Bitmap decodeFile(ImageViewer.CacheImage img)
	{
		String image = img.imgInfo.getImagePath();
		Object obj = null;
		synchronized(Beautify4Page.CACHE_THREAD_LOCK)
		{
			UndoRedoDataMgr mgr = Beautify4Page.sCacheDatas.get(image);
			if(mgr != null)
			{
				obj = mgr.GetCurrentData();
			}
		}
		if(image != null)
		{
			img.image = image;
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			cn.poco.imagecore.Utils.DecodeFile(image, opts, true);
			opts.inJustDecodeBounds = false;
			int max = opts.outHeight > opts.outWidth ? opts.outHeight : opts.outWidth;
			opts.inSampleSize = max / mMinSize;
			Bitmap bmp;
			if(obj != null)
			{
				bmp = ImageUtils.MakeBmp(getContext(), obj, mMinSize, mMinSize);
			}
			else
			{
				bmp = cn.poco.imagecore.Utils.DecodeFile(image, opts, false);
				//bmp = BitmapFactory.decodeFile(image, opts);
				if(bmp != null)
				{
					int rotation = 0;
					if(opts.outMimeType == null || opts.outMimeType.equals("image/jpeg"))
					{
						int[] imei = CommonUtils.GetImgInfo(image);
						rotation = imei[0];
					}
					if(bmp.getWidth() > mMinSize || bmp.getHeight() > mMinSize || rotation != 0)
					{
						bmp = MakeBmp.CreateBitmap(bmp, mMinSize, mMinSize, -1, rotation, Bitmap.Config.ARGB_8888);
					}
				}
			}
			if(bmp == null)
			{
				//System.out.println("decode " + image + " fail");
			}
			else
			{
				img.width = bmp.getWidth();
				img.height = bmp.getHeight();
			}
			return bmp;
		}
		return null;
	}

	@Override
	public Bitmap decodeBigImage(String imgFile, float maxMem)
	{
		int maxMemory = (int)(Runtime.getRuntime().maxMemory() * maxMem);
		int maxSize = (int)(Math.sqrt(maxMemory / 4) / 2);

		int limitSize = mMaxBitmapWidth < mMaxBitmapHeight ? mMaxBitmapWidth : mMaxBitmapHeight;
		if(limitSize > 0)
		{
			if(maxSize > limitSize)
			{
				maxSize = limitSize;
			}
		}

		Object obj = null;
		synchronized(Beautify4Page.CACHE_THREAD_LOCK)
		{
			UndoRedoDataMgr mgr = Beautify4Page.sCacheDatas.get(imgFile);
			if(mgr != null)
			{
				obj = mgr.GetCurrentData();
			}
		}
		if(obj == null)
		{
			obj = imgFile;
		}
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;
		if(obj instanceof String)
		{
			BitmapFactory.decodeFile((String)obj, opt);
		}
		Bitmap bmp = ImageUtils.MakeBmp(getContext(), obj, maxSize, maxSize);
		int rotation = 0;
		if(opt.outMimeType != null && opt.outMimeType.equals("image/jpeg"))
		{
			int[] imei = CommonUtils.GetImgInfo((String)obj);
			rotation = imei[0];
		}
		if(bmp.getWidth() > maxSize || bmp.getHeight() > maxSize || rotation != 0)
		{
			bmp = MakeBmp.CreateBitmap(bmp, maxSize, maxSize, -1, rotation, Bitmap.Config.ARGB_8888);
		}
		return bmp;
	}
}
