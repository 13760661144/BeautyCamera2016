//package cn.poco.exception;
//
//import android.content.Context;
//import android.os.Handler;
//import android.os.Looper;
//
//import java.util.ArrayList;
//import java.util.Date;
//
//import cn.poco.tianutils.NetCore2;
//
//public class UploadException
//{
//	/**
//	 *
//	 * 异步发送，无需开线程
//	 *
//	 * @param context
//	 * @param data
//	 */
//	public static void AsyncUpload(final Context context, final String data)
//	{
//		Handler handler = new Handler(Looper.getMainLooper());
//		handler.post(new Runnable()
//		{
//			@Override
//			public void run()
//			{
//				final String xml = ExceptionData.GetExceptionXML(context, data);
//				new Thread(new Runnable()
//				{
//					@Override
//					public void run()
//					{
//						if(xml != null)
//						{
//							NetCore2.FormData formData = new NetCore2.FormData();
//							formData.m_name = "file1";
//							Date date = new Date();
//							formData.m_filename = String.format("%d%02d%02d%02d%02d%02d", date.getYear() + 1900, date.getMonth() + 1, date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds()) + "_" + data.hashCode() + ".xml";
//							//System.out.println(formData.m_filename);
//							formData.m_data = xml.getBytes();
//							ArrayList<NetCore2.FormData> arr_post = new ArrayList<NetCore2.FormData>();
//							arr_post.add(formData);
//
//							NetCore2 net = new NetCore2();
//							//NetCore2.NetMsg msg =
//							net.HttpPost(ExceptionService.UPLOAD_URL, null, arr_post);
//							net.ClearAll();
//							//System.out.println(msg.m_stateCode);
//						}
//					}
//				}).start();
//			}
//		});
//	}
//}
