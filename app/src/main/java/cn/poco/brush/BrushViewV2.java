package cn.poco.brush;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;

import java.util.ArrayList;

import cn.poco.graffiti.GraffitiViewV2;
import cn.poco.tianutils.ImageUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.tianutils.SimpleHorizontalListView;

public class BrushViewV2 extends GraffitiViewV2
{
	protected boolean m_isClick;
	protected int CLICK_SIZE; //判断为click状态的最大size

	public BrushViewV2(Context context, int frW, int frH, Callback cb)
	{
		super(context, frW, frH, cb);

		ShareData.InitData((Activity)context);

		CLICK_SIZE = ShareData.PxToDpi_xhdpi(30);
	}

	protected float down_x;
	protected float down_y;

	@Override
	protected void OnDown(float x, float y)
	{
		m_isTouch = true;
		m_isClick = true;
		down_x = x;
		down_y = y;
	}

	@Override
	protected void OnMove(float x, float y)
	{
		if(m_isTouch)
		{
			if(m_isClick)
			{
				if(ImageUtils.Spacing(down_x - x, down_y - y) > CLICK_SIZE)
				{
					m_isClick = false;

					float rX = GetX(x);
					float rY = GetY(y);
					m_pc.Reset();
					m_pc.AddPoint(rX, rY);
					DrawMaks(rX, rY);
					this.invalidate();
				}
				return;
			}

			float rX = GetX(x);
			float rY = GetY(y);
			ArrayList<PointF> pos = m_pc.AddPoint(rX, rY);
			int len = pos.size();
			PointF temp;
			for(int i = 0; i < len; i++)
			{
				temp = pos.get(i);
				DrawMaks(temp.x, temp.y);
			}
			this.invalidate();
		}
	}

	@Override
	protected void OnUp(float x, float y)
	{
		m_isTouch = false;

		if(m_isClick)
		{
			m_isClick = false;

			float rX = GetX(x);
			float rY = GetY(y);
			DrawMaks2(rX, rY);
			this.invalidate();
		}
	}

	/**
	 * 拖动的时候用
	 *
	 * @param x
	 * @param y
	 */
	protected void DrawMaks2(float x, float y)
	{
		Bitmap bmp = GetMask();
		if(bmp != null && !bmp.isRecycled())
		{
			float cx = bmp.getWidth() / 2f;
			float cy = bmp.getHeight() / 2f;

			temp_paint.reset();
			temp_paint.setFilterBitmap(true);
			temp_paint.setAntiAlias(true);
			temp_paint.setXfermode(temp_mode);
			temp_matrix.reset();
			temp_matrix.postScale(m_resScale, m_resScale, cx, cy);
			temp_matrix.postRotate(GetRotation(), cx, cy);
			temp_matrix.postTranslate(x - cx, y - cy);
			m_bmpCanvas.drawBitmap(bmp, temp_matrix, temp_paint);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
		widthMeasureSpec = SimpleHorizontalListView.GetMyMeasureSpec(0, widthMeasureSpec);
		heightMeasureSpec = SimpleHorizontalListView.GetMyMeasureSpec(0, heightMeasureSpec);
		int w = MeasureSpec.getSize(widthMeasureSpec);
		int h = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(w, h);

        UpdateUI(w, h);
	}

    public void UpdateUI(int w, int h)
    {
        if(m_img != null)
        {
            m_frW = w;
            m_frH = h;

            m_img.m_x = (m_frW - m_img.m_w) / 2f;
            m_img.m_y = (m_frH - m_img.m_h) / 2f;
            {
                float scale1 = (float)m_frW / (float)m_img.m_w;
                float scale2 = (float)m_frH / (float)m_img.m_h;
                m_img.m_scaleX = (scale1 > scale2) ? scale2 : scale1;
                m_img.m_scaleY = m_img.m_scaleX;
            }
            this.invalidate();
        }
    }
}
