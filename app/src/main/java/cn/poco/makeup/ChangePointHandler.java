package cn.poco.makeup;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import cn.poco.tianutils.ProcessQueue;

/**
 * Created by Raining on 2017/2/24.
 */

public class ChangePointHandler extends Handler
{
	public static final int MSG_CYC_QUEUE = 0x00000008; // 处理消息队列
	public static final int MSG_UPDATE_UI = 0x00000010; //更新界面
	public static final int MSG_CANCEL = 0x00000020; //取消

	protected Context m_context;
	protected Handler m_UIHandler;
	protected ChangePointPage.ChangePointCallback mCb;

	public ProcessQueue m_queue;

	public ChangePointHandler(Looper looper, Context context, Handler ui, ChangePointPage.ChangePointCallback cb)
	{
		super(looper);

		m_context = context;
		m_UIHandler = ui;
		mCb = cb;

		m_queue = new ProcessQueue();
		m_queue.SetQueueSize(1);
	}

	@Override
	public void handleMessage(Message msg)
	{
		Message ui_msg;

		switch(msg.what)
		{
			case MSG_CYC_QUEUE:
			{
				CmdMsg item = (CmdMsg)m_queue.GetItem();
				if(item != null && mCb != null)
				{
					item.m_thumb = mCb.DoChange(item.m_orgBmp);

					ui_msg = m_UIHandler.obtainMessage();
					ui_msg.obj = item;
					ui_msg.what = MSG_UPDATE_UI;
					m_UIHandler.sendMessage(ui_msg);
				}
				break;
			}

			case MSG_CANCEL:
			{
				//清理
				m_queue.ClearAll();
				mCb = null;

				ui_msg = m_UIHandler.obtainMessage();
				ui_msg.what = msg.what;
				m_UIHandler.sendMessage(ui_msg);
				break;
			}
		}
	}

	public static class CmdMsg
	{
		//in
		public int m_time; //当前的时间
		public Bitmap m_orgBmp;

		//out
		public Bitmap m_thumb;
	}
}
