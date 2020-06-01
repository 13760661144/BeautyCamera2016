//package cn.poco.exception;
//
//import android.util.Log;
//
//import java.io.BufferedReader;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.Map;
//
//public class UploadFile
//{
//	// 上传代码，第一个参数，为要使用的URL，第二个参数为要上传的文件，可以上传多个文件，这根据需要页定
//	public static String Post(String actionUrl, Map<String, File> files) throws IOException
//	{
//		Log.d("tian", "[UploadFile][Post]");
//		String BOUNDARY = java.util.UUID.randomUUID().toString();
//		String PREFIX = "--";
//		String LINEND = "\r\n";
//		String MULTIPART_FROM_DATA = "multipart/form-data";
//		String CHARSET = "UTF-8";
//
//		URL uri = new URL(actionUrl);
//		HttpURLConnection conn = (HttpURLConnection)uri.openConnection();
//		conn.setReadTimeout(30 * 1000);
//		conn.setConnectTimeout(60 * 1000);
//		conn.setDoInput(true); // 允许输入
//		conn.setDoOutput(true); // 允许输出
//		conn.setUseCaches(false); // 不允许使用缓存
//		conn.setRequestMethod("POST"); // Post方式
//		conn.setRequestProperty("connection", "keep-alive");
//		conn.setRequestProperty("Charset", "UTF-8");
//		conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);
//
//		DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
//
//		// 发送文件数据
//		if(files != null)
//		{
//			int i = 0;
//			for(Map.Entry<String, File> file:files.entrySet())
//			{
//				i++;
//				StringBuilder sb1 = new StringBuilder();
//				sb1.append(PREFIX);
//				sb1.append(BOUNDARY);
//				sb1.append(LINEND);
//				sb1.append("Content-Disposition: form-data; name=\"file" + i + "\"; filename=\"" + file.getKey() + "\"" + LINEND);
//				sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
//				sb1.append(LINEND);
//				Log.d("tian", sb1.toString());
//				outStream.write(sb1.toString().getBytes());
//				InputStream is = new FileInputStream(file.getValue());
//				byte[] buffer = new byte[1024];
//				int len = 0;
//				while((len = is.read(buffer)) != -1)
//				{
//					outStream.write(buffer, 0, len);
//				}
//				is.close();
//				outStream.write(LINEND.getBytes());
//			}
//		}
//
//		// 请求结束标志
//		byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
//		outStream.write(end_data);
//		outStream.flush();
//
//		// 得到响应码
//		int res = conn.getResponseCode();
//		InputStream in = conn.getInputStream();
//		InputStreamReader isReader = new InputStreamReader(in);
//		BufferedReader bufReader = new BufferedReader(isReader);
//		String line = null;
//		String data = "OK.";
//		while((line = bufReader.readLine()) != null)
//		{
//			data += line;
//		}
//		outStream.close();
//		conn.disconnect();
//		if(res == 200)
//		{
//			return data;
//		}
//		else
//		{
//			return "Unauthorized";
//		}
//	}
//}
