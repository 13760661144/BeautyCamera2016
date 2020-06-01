package cn.poco.frame;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.ArrayList;

import cn.poco.advanced.AdvancedResMgr;
import cn.poco.resource.FrameExRes;
import cn.poco.resource.FrameRes;
import cn.poco.tianutils.ImageUtils;
import cn.poco.tianutils.ShareData;

public class FrameHandler extends Handler
{
	public static final int MSG_UPDATE_FRAME = 0x00000040; //更新边框

	protected Context mContext;
	protected Handler mUIHandler;

	public FrameHandler(Looper looper, Context context, Handler ui)
	{
		super(looper);

		mContext = context;
		mUIHandler = ui;
	}

	@Override
	public void handleMessage(Message msg)
	{
		switch(msg.what)
		{
			case MSG_UPDATE_FRAME:
			{
				FrameMsg params = (FrameMsg)msg.obj;
				msg.obj = null;
				int size = params.m_w > params.m_h ? params.m_w : params.m_h;
				//修复高分辨率边框模糊问题
				int min = ShareData.m_screenWidth * 4 / 3;
				if(size < min)
				{
					size = min;
				}
				if(params.m_frameInfo instanceof FrameExRes)
				{
					int color;
					if(params.m_color != null)
					{
						color = params.m_color;
					}
					else
					{
						color = ((FrameExRes)params.m_frameInfo).mMaskColor;
					}
					Object[] objs = MakeFrame(mContext, (FrameExRes)params.m_frameInfo, color, params.m_w, params.m_h, size, size);
					params.m_fThumb = (Bitmap)objs[0];
					params.m_rect = (RectF)objs[1];
				}
				else if(params.m_frameInfo instanceof FrameRes)
				{
					params.m_fThumb = cn.poco.advanced.ImageUtils.MakeFrame(mContext, params.m_frameInfo, params.m_w, params.m_h, size, size);
				}
				Message ui_msg = mUIHandler.obtainMessage();
				ui_msg.obj = params;
				ui_msg.what = MSG_UPDATE_FRAME;
				mUIHandler.sendMessage(ui_msg);
				break;
			}

			default:
				break;
		}
	}

	/**
	 * @return [0]bmp:Bitmap,[1]rect:Rect
	 */
	public static Object[] MakeFrame(Context context, FrameExRes info, int color, int thumbW, int thumbH, int frW, int frH)
	{
		Object[] out = null;

		Object[] objs = GetFrameExRes(info, (float)thumbW / (float)thumbH);
		Bitmap temp = cn.poco.imagecore.Utils.DecodeImage(context, objs[0], 0, -1, frW, frH);
		Canvas canvas = new Canvas(temp);
		canvas.drawColor(color, PorterDuff.Mode.SRC_IN);
		if(objs[1] != null)
		{
			Bitmap temp2 = cn.poco.imagecore.Utils.DecodeImage(context, objs[1], 0, -1, frW, frH);
			if(temp2 != null)
			{
				canvas.drawBitmap(temp2, 0, 0, null);
				temp2.recycle();
				temp2 = null;
			}
		}
		out = new Object[]{temp, objs[2]};
		return out;
	}

	/**
	 * @return [0]mask:Object, [1]frame:Object, nullable, [2]rect:RectF
	 */
	public static Object[] GetFrameExRes(FrameExRes info, float w_h_s)
	{
		Object[] out = null;

		ArrayList<Object> objs = new ArrayList<>();
		ArrayList<Object> objs2 = new ArrayList<>();
		ArrayList<RectF> objs3 = new ArrayList<>();
		ArrayList<Float> ss = new ArrayList<>();
		if(!AdvancedResMgr.IsNull(info.m4_3))
		{
			objs.add(info.m4_3);
			objs2.add(info.f4_3);
			objs3.add(new RectF(info.m4_3_x, info.m4_3_y, info.m4_3_x + info.m4_3_w, info.m4_3_y + info.m4_3_h));
			ss.add(4f / 3f);
		}
		if(!AdvancedResMgr.IsNull(info.m1_1))
		{
			objs.add(info.m1_1);
			objs2.add(info.f1_1);
			objs3.add(new RectF(info.m1_1_x, info.m1_1_y, info.m1_1_x + info.m1_1_w, info.m1_1_y + info.m1_1_h));
			ss.add(1f);
		}
		if(!AdvancedResMgr.IsNull(info.m3_4))
		{
			objs.add(info.m3_4);
			objs2.add(info.f3_4);
			objs3.add(new RectF(info.m3_4_x, info.m3_4_y, info.m3_4_x + info.m3_4_w, info.m3_4_y + info.m3_4_h));
			ss.add(3f / 4f);
		}

		int index = ImageUtils.GetScale(w_h_s, ss);
		if(index >= 0)
		{
			out = new Object[]{objs.get(index), objs2.get(index), objs3.get(index)};
		}

		return out;
	}

	public static class FrameMsg
	{
		// in
		public int m_w;
		public int m_h;
		public Object m_frameInfo;
		public Integer m_color;

		// out
		public Bitmap m_fThumb;
		public RectF m_rect;

		// in out
		public boolean m_resetPos;
	}
}
