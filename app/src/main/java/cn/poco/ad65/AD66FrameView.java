package cn.poco.ad65;


import android.content.Context;
import android.graphics.Canvas;

import cn.poco.frame.FrameView;
import cn.poco.graphics.ShapeEx;

public class AD66FrameView extends FrameView {
    public AD66FrameView(Context context, int frW, int frH) {
        super(context, frW, frH);
    }

    public void SetFrame2(ShapeEx item,boolean isReset) {
        super.SetFrame2(item);
        if(isReset)
        {
           if(m_img != null)
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

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
}
