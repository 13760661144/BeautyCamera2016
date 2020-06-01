package cn.poco.camera;

import cn.poco.tianutils.MakeBmpV2;

/**
 * 操作顺序:旋转->翻转->裁剪
 * 
 * @author POCO
 *
 */
public class RotationImg2 implements Cloneable
{
	/**
	 * 图片的原始路径(一般情况下用不到,只作记录原始图片信息,软件处理过程中使用的图片都应该是m_img)
	 */
	public String m_orgPath = null;
	/**
	 * 转换为APP缓存后的路径
	 */
	public Object m_img = null;
	/**
	 * 旋转角度
	 */
	public int m_degree = 0;
	/**
	 * 翻转
	 */
	public int m_flip = MakeBmpV2.FLIP_NONE;

	public RotationImg2()
	{
	}

	/**
	 * 操作顺序:旋转->翻转->裁剪
	 * 
	 * @param img
	 * @param degree
	 * @param flip
	 *            翻转</br>
	 *            MakeBmpV2.FLIP_NONE</br>
	 *            MakeBmpV2.FLIP_H</br>
	 *            MakeBmpV2.FLIP_V</br>
	 */
	public RotationImg2(Object img, int degree, int flip)
	{
		if(img instanceof String)
		{
			m_orgPath = (String)img;
		}
		m_img = img;
		m_degree = degree;
		m_flip = flip;
	}

	public RotationImg2 Clone()
	{
		RotationImg2 out = null;
		try
		{
			out = (RotationImg2)this.clone();
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}

		return out;
	}
}
