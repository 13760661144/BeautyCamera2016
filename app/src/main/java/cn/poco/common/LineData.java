package cn.poco.common;

/**
 * Created by Raining on 2016/12/9.
 */

public class LineData extends PointData
{
	public float m_x2;
	public float m_y2;

	public LineData(float x1, float y1, float x2, float y2, float r)
	{
		super(x1, y1, r);

		m_x2 = x2;
		m_y2 = y2;
	}
}
