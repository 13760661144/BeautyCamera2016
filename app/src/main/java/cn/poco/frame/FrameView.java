package cn.poco.frame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

import java.util.ArrayList;

import cn.poco.display.CoreViewV3;
import cn.poco.graphics.ShapeEx;
import cn.poco.tianutils.ImageUtils;

public class FrameView extends CoreViewV3
{
	protected RectF mRect;

	public FrameView(Context context, int frW, int frH)
	{
		super(context, frW, frH);
	}

	public void SetFrame(Object info, Bitmap bmp, RectF rect, boolean resetPos)
	{
		super.SetFrame(info, bmp);

		mRect = rect;
		if(resetPos)
		{
			if(mRect != null)
			{
				if(m_img != null && m_frame != null)
				{
					float fw2 = m_frame.m_centerX * m_frame.m_scaleX;
					float fh2 = m_frame.m_centerY * m_frame.m_scaleY;
					float x = m_frame.m_x + m_frame.m_centerX - fw2;
					float y = m_frame.m_y + m_frame.m_centerY - fh2;
					float w = Math.abs(mRect.right - mRect.left) * m_frame.m_w * m_frame.m_scaleX;
					float h = Math.abs(mRect.bottom - mRect.top) * m_frame.m_h * m_frame.m_scaleY;
					float cx = x + (mRect.left + mRect.right) * fw2;
					float cy = y + (mRect.top + mRect.bottom) * fh2;
					m_img.m_x = cx - m_img.m_centerX;
					m_img.m_y = cy - m_img.m_centerY;
					{
						float scale1 = w / m_img.m_w;
						float scale2 = h / m_img.m_h;
						m_img.m_scaleX = (scale1 > scale2) ? scale1 : scale2;
						m_img.m_scaleY = m_img.m_scaleX;
					}
					m_img.m_degree = 0;
				}
			}
			else
			{
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
		}
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		if(m_drawable && m_viewport.m_w > 0 && m_viewport.m_h > 0)
		{
			DrawToCanvas(canvas, m_operateMode);
		}
	}

	/**
	 * 白色边框
	 *
	 * @param info
	 */
	public void SetWhiteFrame(Object info)
	{
		m_frame = new ShapeEx();
		m_frame.m_w = 1024;
		m_frame.m_h = 1024;
		m_frame.m_centerX = (float)m_frame.m_w / 2f;
		m_frame.m_centerY = (float)m_frame.m_h / 2f;
		m_frame.m_x = (float)m_origin.m_w / 2f - m_frame.m_centerX;
		m_frame.m_y = (float)m_origin.m_h / 2f - m_frame.m_centerY;
		{
			float scale1 = (float)m_origin.m_w / (float)m_frame.m_w;
			float scale2 = (float)m_origin.m_h / (float)m_frame.m_h;
			m_frame.m_scaleX = (scale1 > scale2) ? scale2 : scale1;
			m_frame.m_scaleY = m_frame.m_scaleX;
		}
		m_frame.m_ex = info;

		UpdateViewport();

		if(m_img != null && m_frame != null)
		{
			m_img.m_x = m_origin.m_centerX - m_img.m_centerX;
			m_img.m_y = m_origin.m_centerY - m_img.m_centerY;
			{
				float scale1 = m_viewport.m_w * m_viewport.m_scaleX / m_img.m_w;
				float scale2 = m_viewport.m_h * m_viewport.m_scaleY / m_img.m_h;
				m_img.m_scaleX = (scale1 < scale2) ? scale1 : scale2;
				m_img.m_scaleY = m_img.m_scaleX;
			}
			m_img.m_degree = 0;

			ArrayList<Float> arr = new ArrayList<Float>();
			arr.add(1f);
			arr.add(3f / 4f);
			arr.add(4f / 3f);
			arr.add(9f / 16f);
			arr.add(16f / 9f);
			int index = ImageUtils.GetScale((float)m_img.m_w / (float)m_img.m_h, arr);
			if(index == 0)
			{
				m_img.m_scaleX *= 0.9f;
				m_img.m_scaleY *= 0.9f;
			}
		}
	}

	/*private float[] rect_src = new float[4];
	private float[] rect_dst = new float[4];

	@Override
	protected void DrawToCanvas(Canvas canvas, int mode)
	{
		super.DrawToCanvas(canvas, mode);

		if(mRect != null && m_frame != null && m_isTouch)
		{
			float w = m_frame.m_w * m_frame.m_scaleX;
			float h = m_frame.m_h * m_frame.m_scaleY;
			float x = m_frame.m_x + m_frame.m_centerX - w / 2f;
			float y = m_frame.m_y + m_frame.m_centerY - h / 2f;
			float l = w * mRect.left;
			float t = h * mRect.top;
			float r = w * mRect.right;
			float b = h * mRect.bottom;
			rect_src[0] = x + l;
			rect_src[1] = y + t;
			rect_src[2] = x + r;
			rect_src[3] = y + b;
			GetShowPos(rect_dst, rect_src);
			temp_paint.reset();
			temp_paint.setColor(0xffff0000);
			temp_paint.setStrokeWidth(ShareData.PxToDpi_xxhdpi(3));
			temp_paint.setStyle(Paint.Style.STROKE);
			canvas.drawRect(rect_dst[0], rect_dst[1], rect_dst[2], rect_dst[3], temp_paint);
		}
	}*/
}
