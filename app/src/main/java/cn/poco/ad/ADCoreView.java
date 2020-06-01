package cn.poco.ad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;

import cn.poco.display.CoreViewV3;
import cn.poco.graphics.ShapeEx;

public class ADCoreView extends CoreViewV3
{
	public ADCoreView(Context context, int frW, int frH)
	{
		super(context, frW, frH);
		// TODO Auto-generated constructor stub
	}
	//区别：按最小边长填充，不留空白
	public void SetFrame3(Object info, Bitmap bmp)
	{
		super.SetFrame(info, bmp);

		if(m_img != null && m_frame != null)
		{
			m_img.m_x = m_origin.m_centerX - m_img.m_centerX;
			m_img.m_y = m_origin.m_centerY - m_img.m_centerY;
			{
				float scale1 = m_viewport.m_w * m_viewport.m_scaleX / m_img.m_w;
				float scale2 = m_viewport.m_h * m_viewport.m_scaleY / m_img.m_h;
				m_img.m_scaleX = (scale1 > scale2) ? scale1 : scale2;
				m_img.m_scaleY = m_img.m_scaleX;
			}
			m_img.m_degree = 0;
		}
	}

	public Bitmap GetOutputBmp2()
	{
		int size = m_img.m_w > m_img.m_h ? m_img.m_w : m_img.m_h;

		float whscale = (float)m_viewport.m_w / (float)m_viewport.m_h;
		float outW = size;
		float outH = outW / whscale;
		if(outH > size)
		{
			outH = size;
			outW = outH * whscale;
		}
		ShapeEx backup = (ShapeEx)m_origin.Clone();

		//设置输出位置
		m_origin.m_scaleX = outW / (float)m_viewport.m_w / m_viewport.m_scaleX;
		m_origin.m_scaleY = m_origin.m_scaleX;
		m_origin.m_x = (int)outW / 2f - (m_viewport.m_x + m_viewport.m_centerX - m_origin.m_centerX) * m_origin.m_scaleX - m_origin.m_centerX;
		m_origin.m_y = (int)outH / 2f - (m_viewport.m_y + m_viewport.m_centerY - m_origin.m_centerY) * m_origin.m_scaleY - m_origin.m_centerY;

		Bitmap outBmp = Bitmap.createBitmap((int)outW, (int)outH, Config.ARGB_8888);
		Canvas canvas = new Canvas(outBmp);
		canvas.setDrawFilter(temp_filter);

		Bitmap tempBmp;
		canvas.drawColor(m_bkColor);
		if(m_bk != null)
		{
			temp_paint.reset();
			temp_paint.setAntiAlias(true);
			temp_paint.setFilterBitmap(true);
			tempBmp = m_bk.m_bmp;
			BitmapShader shader = new BitmapShader(tempBmp, BitmapShader.TileMode.REPEAT, BitmapShader.TileMode.REPEAT);
			temp_paint.setShader(shader);
			canvas.drawRect(0, 0, outW, outH, temp_paint);
		}

		if(m_img != null)
		{
			tempBmp = m_img.m_bmp;
			GetOutputMatrix(temp_matrix, m_img, tempBmp);
			temp_paint.reset();
			temp_paint.setAntiAlias(true);
			temp_paint.setFilterBitmap(true);
			canvas.drawBitmap(tempBmp, temp_matrix, temp_paint);
		}

		if(m_frame != null)
		{
			tempBmp = m_frame.m_bmp;
			GetOutputMatrix(temp_matrix, m_frame, tempBmp);
			temp_paint.reset();
			temp_paint.setAntiAlias(true);
			temp_paint.setFilterBitmap(true);
			canvas.drawBitmap(tempBmp, temp_matrix, temp_paint);
		}

		int len = m_pendantArr.size();
		ShapeEx temp;
		for(int i = 0; i < len; i++)
		{
			temp = m_pendantArr.get(i);
			tempBmp = temp.m_bmp;
			GetOutputMatrix(temp_matrix, temp, tempBmp);
			temp_paint.reset();
			temp_paint.setAntiAlias(true);
			temp_paint.setFilterBitmap(true);
			canvas.drawBitmap(tempBmp, temp_matrix, temp_paint);
		}

		m_origin.Set(backup);

		return outBmp;
	}

	public Bitmap GetOutputBmp()
	{
		int size = m_img.m_w > m_img.m_h ? m_img.m_w : m_img.m_h;

		float whscale = (float)m_viewport.m_w / (float)m_viewport.m_h;
		float outW = size;
		float outH = outW / whscale;
		if(outH > size)
		{
			outH = size;
			outW = outH * whscale;
		}
		ShapeEx backup = (ShapeEx)m_origin.Clone();

		//设置输出位置
		m_origin.m_scaleX = outW / (float)m_viewport.m_w / m_viewport.m_scaleX;
		m_origin.m_scaleY = m_origin.m_scaleX;
		m_origin.m_x = (int)outW / 2f - (m_viewport.m_x + m_viewport.m_centerX - m_origin.m_centerX) * m_origin.m_scaleX - m_origin.m_centerX;
		m_origin.m_y = (int)outH / 2f - (m_viewport.m_y + m_viewport.m_centerY - m_origin.m_centerY) * m_origin.m_scaleY - m_origin.m_centerY;

		Bitmap outBmp = Bitmap.createBitmap((int)outW, (int)outH, Config.ARGB_8888);
		Canvas canvas = new Canvas(outBmp);
		canvas.setDrawFilter(temp_filter);

		Bitmap tempBmp;
		canvas.drawColor(m_bkColor);
		if(m_bk != null)
		{
			temp_paint.reset();
			temp_paint.setAntiAlias(true);
			temp_paint.setFilterBitmap(true);
			tempBmp = m_bk.m_bmp;
			BitmapShader shader = new BitmapShader(tempBmp, BitmapShader.TileMode.REPEAT, BitmapShader.TileMode.REPEAT);
			temp_paint.setShader(shader);
			canvas.drawRect(0, 0, outW, outH, temp_paint);
			tempBmp.recycle();
			tempBmp = null;
		}

		if(m_img != null)
		{
			tempBmp = m_img.m_bmp;
			GetOutputMatrix(temp_matrix, m_img, tempBmp);
			temp_paint.reset();
			temp_paint.setAntiAlias(true);
			temp_paint.setFilterBitmap(true);
			canvas.drawBitmap(tempBmp, temp_matrix, temp_paint);
			tempBmp.recycle();
			tempBmp = null;
		}

		if(m_frame != null)
		{
			tempBmp = m_frame.m_bmp;
			GetOutputMatrix(temp_matrix, m_frame, tempBmp);
			temp_paint.reset();
			temp_paint.setAntiAlias(true);
			temp_paint.setFilterBitmap(true);
			canvas.drawBitmap(tempBmp, temp_matrix, temp_paint);
			tempBmp.recycle();
			tempBmp = null;
		}

		int len = m_pendantArr.size();
		ShapeEx temp;
		for(int i = 0; i < len; i++)
		{
			temp = m_pendantArr.get(i);
			tempBmp = temp.m_bmp;
			GetOutputMatrix(temp_matrix, temp, tempBmp);
			temp_paint.reset();
			temp_paint.setAntiAlias(true);
			temp_paint.setFilterBitmap(true);
			canvas.drawBitmap(tempBmp, temp_matrix, temp_paint);
			tempBmp.recycle();
			tempBmp = null;
		}

		m_origin.Set(backup);

		return outBmp;
	}
	
}
