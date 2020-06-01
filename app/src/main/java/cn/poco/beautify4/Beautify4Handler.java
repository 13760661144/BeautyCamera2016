package cn.poco.beautify4;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import cn.poco.framework.FileCacheMgr;
import cn.poco.utils.Utils;

/**
 * Created by Raining on 2016/12/13.
 * 美化页线程
 */

public class Beautify4Handler extends Handler
{
	//public static final int MSG_INIT = 0x00000001;
	public static final int MSG_CACHE = 0x00000002; //保存临时文件

	private Context mContext;
	private Handler mUIHandler;

	public Beautify4Handler(Looper looper, Context context, Handler ui)
	{
		super(looper);

		mContext = context;
		mUIHandler = ui;
	}

	@Override
	public void handleMessage(Message msg)
	{
		Message ui_msg;

		switch(msg.what)
		{
			/*case MSG_INIT:
			{
				InitMsg params = (InitMsg)msg.obj;
				msg.obj = null;

				if(params.m_imgs instanceof ImageFile2)
				{
					((ImageFile2)params.m_imgs).SaveImg2(mContext);
				}

				if(params.m_thumb != null)
				{
					String path = FileCacheMgr.GetLinePath();
					if(Utils.SaveTempImg(params.m_thumb, path))
					{
						params.m_tempPath = path;
					}
				}

				ui_msg = mUIHandler.obtainMessage();
				ui_msg.obj = params;
				ui_msg.what = MSG_INIT;
				mUIHandler.sendMessage(ui_msg);
				break;
			}*/

			case MSG_CACHE:
			{
				CmdMsg params = (CmdMsg)msg.obj;
				msg.obj = null;

				if(params.m_thumb != null)
				{
					String path = FileCacheMgr.GetAppPath();
					if(Utils.SaveImg(mContext, params.m_thumb, path, 100, false) != null)
					{
						params.m_tempPath = path;
					}
				}

				ui_msg = mUIHandler.obtainMessage();
				ui_msg.obj = params;
				ui_msg.what = MSG_CACHE;
				mUIHandler.sendMessage(ui_msg);
				break;
			}
		}
	}

	public static class InitMsg extends CmdMsg
	{
		// in
		public Object m_imgs;//可修改为object
	}

	public static class CmdMsg
	{
		// in
		public Bitmap m_thumb;

		// out
		public String m_tempPath;

		public Object m_info;
	}
}
