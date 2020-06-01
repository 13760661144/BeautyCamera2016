package cn.poco.view.beauty;

import cn.poco.view.BaseView;

/**
 * 约定顺序：翻转-平移-放大-旋转
 * 
 * @author POCO
 * 
 */
public class ShapeEx extends BaseView.Shape
{
	public float MAX_SCALE = 2f;
	public float DEF_SCALE = 1f;
	public float MIN_SCALE = 0.5f;

	public float m_x;
	public float m_y;
	public float m_degree;
	public float m_scaleX;
	public float m_scaleY;
	public float m_flip;
	public int m_w;
	public int m_h;
	public float m_centerX;
	public float m_centerY;

	public void SetScaleXY(float scaleX, float scaleY)
	{
		if(scaleX > MAX_SCALE)
		{
			m_scaleX = MAX_SCALE;
		}
		else if(scaleX < MIN_SCALE)
		{
			m_scaleX = MIN_SCALE;
		}
		else
		{
			m_scaleX = scaleX;
		}

		if(scaleY > MAX_SCALE)
		{
			m_scaleY = MAX_SCALE;
		}
		else if(scaleY < MIN_SCALE)
		{
			m_scaleY = MIN_SCALE;
		}
		else
		{
			m_scaleY = scaleY;
		}
	}

	public void Set(ShapeEx item)
	{
		this.m_bmp = item.m_bmp;
		this.m_centerX = item.m_centerX;
		this.m_centerY = item.m_centerY;
		this.m_scaleX = item.m_scaleX;
		this.m_scaleY = item.m_scaleY;
		this.m_w = item.m_w;
		this.m_h = item.m_h;
		this.m_x = item.m_x;
		this.m_y = item.m_y;
		this.m_degree = item.m_degree;
		this.m_flip = item.m_flip;
		this.MAX_SCALE = item.MAX_SCALE;
		this.DEF_SCALE = item.DEF_SCALE;
		this.MIN_SCALE = item.MIN_SCALE;
		this.m_matrix = item.m_matrix;
		this.m_ex = item.m_ex;
	}

	public Object Clone()
	{
		ShapeEx item = null;

		try
		{
			item = this.getClass().getConstructor().newInstance();
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}

		if(item != null)
		{
			item.Set(this);
		}

		return item;
	}
}
