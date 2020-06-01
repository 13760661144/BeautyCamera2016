//package cn.poco.exception;
//
//import android.app.Service;
//import android.content.Intent;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Looper;
//import android.os.Message;
//import android.util.Log;
//
//import java.io.File;
//import java.io.FilenameFilter;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//public class ExceptionService extends Service
//{
//	private final String LOG_PATH = "error_log";
//	public static final String UPLOAD_URL = "http://diy.poco.cn/livephoto/beautycamera/android_upload_err.php";
//
//	private boolean m_isRun;
//	private MyHandler m_mainHandler;
//
//	@Override
//	public void onCreate()
//	{
//		super.onCreate();
//
//		m_isRun = false;
//		m_mainHandler = new MyHandler(Looper.myLooper());
//	}
//
//	@Override
//	public IBinder onBind(Intent intent)
//	{
//		return null;
//	}
//
//	@Override
//	public int onStartCommand(Intent intent, int flags, int startId)
//	{
//		Log.d("tian", "[Service onStartCommand]");
//
//		if(intent != null && !m_isRun)
//		{
//			m_isRun = true;
//			StartUpload();
//		}
//
//		return super.onStartCommand(intent, flags, startId);
//	}
//
//	private void StartUpload()
//	{
//		new Thread(new Runnable()
//		{
//			@Override
//			public void run()
//			{
//				File filesPath = getDir(LOG_PATH, MODE_PRIVATE);
//				FilenameFilter filter = new FilenameFilter()
//				{
//					@Override
//					public boolean accept(File dir, String filename)
//					{
//						return filename.endsWith(".xml");
//					}
//
//				};
//				String[] names = filesPath.list(filter);
//
//				HashMap<String, File> files = new HashMap<String, File>();
//				if(names != null)
//				{
//					for(int i=0; i<names.length; i++)
//					{
//						files.put(names[i], new File(filesPath+File.separator+names[i]));
//					}
//				}
//
//				try
//				{
//					Log.d("tian","[ExceptionService][StartUpload]"+UploadFile.Post(UPLOAD_URL, files));
//
//					for(Map.Entry<String, File> file:files.entrySet())
//					{
//						file.getValue().delete();
//					}
//				}
//				catch(IOException e)
//				{
//					Log.d("tian","[ExceptionService][StartUpload] IOException");
//				}
//
//				Message msg = m_mainHandler.obtainMessage();
//				msg.what = 100;
//				m_mainHandler.sendMessage(msg);
//			}
//		}).start();
//	}
//
//	private class MyHandler extends Handler
//	{
//		public MyHandler(Looper lo)
//		{
//			super(lo);
//		}
//
//		public void handleMessage(Message msg)
//		{
//			switch(msg.what)
//			{
//				case 100:
//					Log.d("tian","[ExceptionService]service quit!");
//					ExceptionService.this.stopSelf();
//					System.exit(0);
//					break;
//				default:
//					break;
//			}
//		}
//	}
//}
