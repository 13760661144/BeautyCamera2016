package cn.poco.share;

import android.os.Handler;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ShareSendBlog {
	/**
	 * 所有微博发送的核心类;
	 */
	public static final int POST_TIMEOUT = 6000 * 10;
	private HashMap<String, Object> mSendPocoActParams = null;
	private SendBlogListener mSendPocoActListener = null;
	
	public interface SendBlogListener{void onSendFinish(boolean success, String errMsg, String weiboID);};

	/**************************************
	 * 
	 * 发POCO活动;
	 * 
	 **************************************/
	public void sendPocoActivities(HashMap<String, Object> params, SendBlogListener l)
	{
		mSendPocoActListener = l;
		mSendPocoActParams = params;
		//异步发送不用多线程执行
//		final HashMap<String, String> response = new HashMap<String, String>();
//		final String postStr = (String)mSendPocoActParams.get("postStr");
//		final String file = (String)mSendPocoActParams.get("file");
//		final String postUrl = (String)mSendPocoActParams.get("postUrl");
//		final Boolean sendStatus=sendPocoActivities(postUrl,postStr, file, response);
//		final String err = response.get("msg");
//		if(mSendPocoActListener != null)
//		{			
//			mSendPocoActListener.onSendFinish(sendStatus, err, null);							
//		}
		final Handler handler = new Handler();
		
		//同步发送		
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				final HashMap<String, String> response = new HashMap<String, String>();
				final String postStr = (String)mSendPocoActParams.get("postStr");
				final String file = (String)mSendPocoActParams.get("file");
				final String postUrl = (String)mSendPocoActParams.get("postUrl");
				final Boolean sendStatus = sendPocoActivities(postUrl,postStr, file, response);
				final String err = response.get("msg");
				if(mSendPocoActListener != null)
				{
					handler.post(new Runnable()
					{
						@Override
						public void run() 
						{
							mSendPocoActListener.onSendFinish(sendStatus, err, null);
						}
					});
				}
			}
		}).start();
	}
	
	public boolean sendPocoActivities(String postUrl,String postStr, String file, HashMap<String, String> response)
	{
		if(postStr == null) return false;
			
		final String BOUNDARY = java.util.UUID.randomUUID().toString();
		final String RETURN = "\r\n";
		final String PREFIX = "--";
		final String CHARSET = "UTF-8";
		HttpURLConnection conn = null;
    	try 
    	{
			HashMap<String,String> values = new HashMap<String,String>();
			values.put("post_str", postStr);
			
			URL _url = new URL(postUrl);
			conn = (HttpURLConnection)_url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setConnectTimeout(POST_TIMEOUT);
			conn.setReadTimeout(POST_TIMEOUT);
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Charsert", CHARSET);
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
			DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, String> entry : values.entrySet()) {
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(RETURN);
				sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + RETURN);
				sb.append("Content-Type: text/plain; charset=" + CHARSET + RETURN);
				sb.append("Content-Transfer-Encoding: 8bit" + RETURN);
				sb.append(RETURN);
				sb.append(entry.getValue());
				sb.append(RETURN);
			}
			outStream.write(sb.toString().getBytes());

			if (file != null) {
				StringBuilder sb1 = new StringBuilder();
				sb1.append(PREFIX);
				sb1.append(BOUNDARY);
				sb1.append(RETURN);
				sb1.append("Content-Disposition: form-data; name=\"opus\"; filename=\"" + file + "\"" + RETURN);
				sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET + RETURN);
				sb1.append(RETURN);
				outStream.write(sb1.toString().getBytes());
				InputStream is = new FileInputStream(file);
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					outStream.write(buffer, 0, len);
				}
				is.close();
				outStream.write(RETURN.getBytes());

				outStream.write((PREFIX + BOUNDARY + PREFIX + RETURN).getBytes());
				outStream.flush();
			}

			int responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				//解析返回的数据
				InputStreamReader isReader = new InputStreamReader(conn.getInputStream(), "UTF-8");
				BufferedReader reader = new BufferedReader(isReader);
				String line;
				StringBuilder strb = new StringBuilder();
				while ((line = reader.readLine()) != null) {
					strb.append(line);
				}
				String str = strb.toString();
				isReader.close();
				reader.close();
	    	    if(str != null && str.length() > 0)
	    	    {
	    	    	JSONObject json = new JSONObject(str);
	    	    	json = json.getJSONObject("Result");
					if(json != null)
					{
						String code = json.getString("ResultCode");
						String err = json.getString("ResultMessage");
						response.put("code", code);
						response.put("msg", err);
						if(code != null && code.equals("0"))
						{
							conn.disconnect();
							return true;
						}
					}
	    	    }
	    	    else {
	    	    	response.put("msg", "无法获取服务器返回数据");
	    	    }
			} else {
				//System.out.println("发送活动：服务器返还不为200，responseCode="+responseCode);
				response.put("code", Integer.toString(responseCode));
    	    	response.put("msg", "连接服务器失败");
			}
			outStream.close();
    	} catch (Exception ex) {
    		response.put("msg", "连接服务器失败");
    		ex.printStackTrace();
    	} finally {
    		if(conn != null) conn.disconnect();
    	}
		return false;
	}
	
	private HashMap<String, String> parseResult(InputStream is) throws Exception{
		
		InputStreamReader isReader = new InputStreamReader(is, "UTF-8");
		BufferedReader reader = new BufferedReader(isReader);
		String line;
		StringBuilder sb = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		String strXml = sb.toString();
		HashMap<String,String> nodes = new HashMap<String,String>();
		nodes.put("code", getNodeText(strXml, "result"));
		nodes.put("msg", getNodeText(strXml, "message"));
		nodes.put("id", getNodeText(strXml, "blog-id"));
		isReader.close();
		reader.close();
		return nodes;
	}
	
	private String getNodeText(String xml, String name)
	{
		if(xml == null) 
			return "";
		String s = "<"+name+">";
		String e = "</"+name+">";
		int pos1 = xml.indexOf(s);
		int pos2 = xml.indexOf(e);
		if(pos1 != -1 && pos2 != -1)
		{
			pos1 += s.length();
			return xml.substring(pos1, pos2);
		}
		return "";
	}

	public String md5ToHexString(byte[] b) {
		char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
	        'a', 'b', 'c', 'd', 'e', 'f' };
		StringBuilder sb = new StringBuilder(b.length * 2);  
		for (int i = 0; i < b.length; i++) {  
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);  
			sb.append(HEX_DIGITS[b[i] & 0x0f]);  
		}
		return sb.toString();  
	}
}
