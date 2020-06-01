package cn.poco.exception;

import android.content.Context;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;

import cn.poco.system.SysConfig;
import cn.poco.tianutils.CommonUtils;

public class CaughtExceptionHandler implements UncaughtExceptionHandler
{
	private static final String LOG_PATH = "error_log";

	private Context m_context;
	private UncaughtExceptionHandler m_defaultHandler;

	public void Init(Context context)
	{
		m_context = context;
		// 获取程序默认的异常处理
		m_defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 把自定义的异常处理替换默认的
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex)
	{
		HandleException(ex);

//		if(NetState.IsConnectNet(m_context))
//		{
//			Intent it = new Intent(m_context, ExceptionService.class);
//			m_context.startService(it);
//		}

		if(m_defaultHandler != null)
		{
			m_defaultHandler.uncaughtException(thread, ex);
		}
	}

	private boolean HandleException(Throwable ex)
	{
		String xml = ExceptionData.GetExceptionXML(m_context, ex);
		if(xml != null)
		{
			String filename = SaveFile(xml.getBytes());
			if(filename != null)
			{
				//System.out.println("[HandleException]SaveFile : " + filename);
				return true;
			}
		}

		return false;
	}

	// 保存文件
	private String SaveFile(byte[] xmlData)
	{
		try
		{
			Date date = new Date();
			String fileName = String.format("%d%02d%02d%02d%02d%02d", date.getYear() + 1900, date.getMonth() + 1, date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds()) + "_" + this.hashCode() + ".xml";
//			String path = m_context.getDir(LOG_PATH, Context.MODE_PRIVATE).getPath();
//			File saveFile = new File(path + File.separator + fileName);
//			FileOutputStream stream = new FileOutputStream(saveFile);
//			stream.write(xmlData);
//			stream.flush();
//			stream.close();

			//就保存最后一次错误
			CommonUtils.SaveFile(SysConfig.GetAppPath() + File.separator + "err.log", xmlData);

			return fileName;
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}

		return null;
	}
}
