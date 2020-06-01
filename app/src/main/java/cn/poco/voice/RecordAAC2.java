package cn.poco.voice;

import android.media.MediaRecorder;

public class RecordAAC2
{
	protected MediaRecorder m_recorder;

	public RecordAAC2(String filepath)
	{
		try
		{
			if(android.os.Build.VERSION.SDK_INT >= 16)
			{
				m_recorder = new MediaRecorder();
				m_recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				m_recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
				m_recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
				m_recorder.setAudioSamplingRate(44100);
				m_recorder.setOutputFile(filepath);
				m_recorder.prepare();
			}
		}
		catch(Throwable e)
		{
			ClearAll();
			e.printStackTrace();
		}
	}

	public boolean IsOk()
	{
		return m_recorder != null;
	}

	public boolean start()
	{
		boolean out = false;
		try
		{
			if(m_recorder != null)
			{
				m_recorder.start();

				out = true;
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		return out;
	}

	public void stop()
	{
		try
		{
			if(m_recorder != null)
			{
				m_recorder.stop();
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}

		ClearAll();
	}

	public void ClearAll()
	{
		try
		{
			if(m_recorder != null)
			{
				m_recorder.release();
				m_recorder = null;
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
}
