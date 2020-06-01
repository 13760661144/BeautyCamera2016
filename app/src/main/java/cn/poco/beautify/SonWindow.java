package cn.poco.beautify;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;

/**
 * bmp的大小需要一样
 * 
 * @author POCO
 * 
 */
public class SonWindow extends View
{
	protected Bitmap m_bmp;
	protected int m_x;
	protected int m_y;

	public SonWindow(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
	}

	public SonWindow(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public SonWindow(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	private Matrix temp_matrix = new Matrix();

	@Override
	protected void onDraw(Canvas canvas)
	{
		if(m_bmp != null && !m_bmp.isRecycled())
		{
			temp_matrix.reset();
			temp_matrix.postTranslate(m_x, m_y);
			canvas.drawBitmap(m_bmp, temp_matrix, null);
		}
	}

	public void SetData(Bitmap bmp, int x, int y)
	{
		m_x = x;
		m_y = y;
		m_bmp = bmp;

		this.invalidate();
	}

	public void ClearAll()
	{
		m_bmp = null;

		invalidate();
	}
}
