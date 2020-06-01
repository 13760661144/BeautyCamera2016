package cn.poco.ad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import cn.poco.advanced.ImageUtils;
import cn.poco.system.SysConfig;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.Utils;

public class ImageHandler extends Handler
{
	public static final int MSG_INIT = 0x0001;
	public static final int MSG_SAVE = 0x0004; //保存
	public static final int MSG_UPDATE_FRAME = 0x0006; //更新边框
	public static final int MSG_ADVANCED_PAGE = 0x0008;//跳转高级美化
	public static final int MSG_RESTORE = 0x0010;//返回编辑

	protected Context m_context;
	protected Handler m_UIHandler;

	public ImageHandler(Looper looper, Context context, Handler ui)
	{
		super(looper);
		m_context = context;
		m_UIHandler = ui;
	}

	@Override
	public void handleMessage(Message msg)
	{
		Message uiMsg;
		switch(msg.what)
		{
			case MSG_INIT:
			{
				Bitmap maskBitmap = cn.poco.beautify.ImageProcessor.ConversionImgColorNew(m_context, false, ((Bitmap)msg.obj).copy(Config.ARGB_8888, true), msg.arg1);
				Bitmap outBmp = cn.poco.beautify.ImageProcessor.DrawMask2((Bitmap)msg.obj, maskBitmap, msg.arg2);
				maskBitmap.recycle();
				maskBitmap = null;
				uiMsg = m_UIHandler.obtainMessage();
				uiMsg.obj = outBmp;
				uiMsg.what = MSG_INIT;
				m_UIHandler.sendMessage(uiMsg);
				break;
			}
			case MSG_UPDATE_FRAME:
			{
				FrameMsg params = (FrameMsg)msg.obj;
				msg.obj = null;

				if(params.m_frameInfo != null)
				{
					int size = params.m_w > params.m_h ? params.m_w : params.m_h;
					//修复高分辨率边框模糊问题
					int min = ShareData.m_screenWidth * 4 / 3;
					if(size < min)
					{
						size = min;
					}
					if(params.m_name == null && params.m_text == null)
					{
						params.m_fThumb = ImageUtils.MakeFrame(m_context, params.m_frameInfo, params.m_w, params.m_h, size, size);
					}
					else
					{
						if(params.m_name == null)
						{
							params.m_name = "";
						}
						if(params.m_text == null)
						{
							params.m_text = "";
						}
						params.m_fThumb = ImageUtils.MakeFrame(m_context, params.m_frameInfo, params.m_w, params.m_h, size, size, params.m_name, params.m_text);
					}

					uiMsg = m_UIHandler.obtainMessage();
					uiMsg.obj = params;
					uiMsg.what = MSG_UPDATE_FRAME;
					m_UIHandler.sendMessage(uiMsg);
				}
				break;
			}
			case MSG_ADVANCED_PAGE:
			{
				int DEF_IMG_SIZE = SysConfig.GetPhotoSize(m_context);
				Bitmap outBitmap = ImageUtils.MakeBmp(m_context, msg.obj, DEF_IMG_SIZE, DEF_IMG_SIZE);
				Bitmap maskBitmap = cn.poco.beautify.ImageProcessor.ConversionImgColorNew(m_context, false, outBitmap.copy(Config.ARGB_8888, true), msg.arg1);
				cn.poco.beautify.ImageProcessor.DrawMask2(outBitmap, maskBitmap, msg.arg2);
				maskBitmap.recycle();
				maskBitmap = null;

				String pic = "";
				try
				{
					pic = Utils.SaveImg(m_context, outBitmap, null, 100);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

				uiMsg = m_UIHandler.obtainMessage();
				uiMsg.obj = pic;
				uiMsg.what = MSG_ADVANCED_PAGE;
				m_UIHandler.sendMessage(uiMsg);
				break;
			}
			case MSG_RESTORE:
			{
				RestoreMsg params = (RestoreMsg)msg.obj;

				Bitmap maskBitmap = cn.poco.beautify.ImageProcessor.ConversionImgColorNew(m_context, false, (params.m_orgBmp).copy(Config.ARGB_8888, true), params.m_effect);
				cn.poco.beautify.ImageProcessor.DrawMask2(params.m_orgBmp, maskBitmap, params.m_alpha);
				maskBitmap.recycle();
				params.m_beautifyBmp = params.m_orgBmp;

				if(params.m_frameInfo != null)
				{
					int size = params.m_w > params.m_h ? params.m_w : params.m_h;
					//修复高分辨率边框模糊问题
					int min = ShareData.m_screenWidth * 4 / 3;
					if(size < min)
					{
						size = min;
					}
					if(params.m_name == null && params.m_text == null)
					{
						params.m_frameBmp = ImageUtils.MakeFrame(m_context, params.m_frameInfo, params.m_w, params.m_h, size, size);
					}
					else
					{
						params.m_frameBmp = ImageUtils.MakeFrame(m_context, params.m_frameInfo, params.m_w, params.m_h, size, size, params.m_name, params.m_text);
					}
				}

				uiMsg = m_UIHandler.obtainMessage();
				uiMsg.obj = params;
				uiMsg.what = MSG_RESTORE;
				m_UIHandler.sendMessage(uiMsg);
				break;
			}
			default:
				break;
		}
	}

	public static class FrameMsg
	{
		// in
		public int m_w;
		public int m_h;
		public Object m_frameInfo;
		public String m_name;
		public String m_text;

		// out
		public Bitmap m_fThumb;
	}

	public static class RestoreMsg
	{
		//in
		public Bitmap m_orgBmp;
		public int m_effect;
		public int m_alpha;

		public int m_w;
		public int m_h;
		public Object m_frameInfo;
		public String m_name;
		public String m_text;

		//out
		public Bitmap m_beautifyBmp;
		public Bitmap m_frameBmp;
	}

}
