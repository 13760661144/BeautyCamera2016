package my.beautyCamera.wxapi;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;

public class SendWXAPI {
	public interface WXCallListener{
		void onCallFinish(int result);
	}
	
	private static ArrayList<WXCallListener> sWXCallListeners = new ArrayList<WXCallListener>();
	
	public static void dispatchResult(final int result)
	{
		synchronized(sWXCallListeners)
		{
			if(sWXCallListeners.size() > 0)
			{
				Handler handler = new Handler(Looper.getMainLooper());
				for(WXCallListener l : sWXCallListeners)
				{
					if(l != null)
					{
						final WXCallListener listener = l;
						handler.post(new Runnable()
						{
							@Override
							public void run() {
								listener.onCallFinish(result);
							}
						});
					}
				}
			}
		}
	}
	
	public static void addListener(WXCallListener l)
	{
		synchronized(sWXCallListeners)
		{
			sWXCallListeners.add(l);
		}
	}
	
	public static void removeAllListener()
	{
		synchronized(sWXCallListeners)
		{
			sWXCallListeners.clear();
		}
	}
	
	public static void removeListener(WXCallListener l)
	{
		synchronized(sWXCallListeners)
		{
			if(sWXCallListeners.size() > 0)
			{
				for(int i = 0; i < sWXCallListeners.size(); i++)
				{
					if(l == sWXCallListeners.get(i))
					{
						sWXCallListeners.remove(i);
						i--;
					}
				}
			}
		}
	}
}
