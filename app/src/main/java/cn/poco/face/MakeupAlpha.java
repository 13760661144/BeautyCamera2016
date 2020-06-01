package cn.poco.face;

/**
 * Created by Raining on 2016/12/28.
 * 彩妆套装透明度
 */

public class MakeupAlpha
{
	public int m_eyebrowAlpha;
	public int m_eyeAlpha;
	public int m_kohlAlpha;
	public int m_eyelashUpAlpha;
	public int m_eyelashDownAlpha;
	public int m_eyelineUpAlpha;
	public int m_eyelineDownAlpha;
	public int m_cheekAlpha;
	public int m_lipAlpha;
	public int m_foundationAlpha;

	public MakeupAlpha()
	{
		Reset();
	}

	public void Reset()
	{
		m_eyebrowAlpha = 60;
		m_eyeAlpha = 60;
		m_kohlAlpha = 60;
		m_eyelashUpAlpha = 60;
		m_eyelashDownAlpha = 60;
		m_eyelineUpAlpha = 60;
		m_eyelineDownAlpha = 60;
		m_cheekAlpha = 60;
		m_lipAlpha = 60;
		m_foundationAlpha = 50;
	}

	public void Set(MakeupAlpha item)
	{
		m_eyebrowAlpha = item.m_eyebrowAlpha;
		m_eyeAlpha = item.m_eyeAlpha;
		m_kohlAlpha = item.m_kohlAlpha;
		m_eyelashUpAlpha = item.m_eyelashUpAlpha;
		m_eyelashDownAlpha = item.m_eyelashDownAlpha;
		m_eyelineUpAlpha = item.m_eyelineUpAlpha;
		m_eyelineDownAlpha = item.m_eyelineDownAlpha;
		m_cheekAlpha = item.m_cheekAlpha;
		m_lipAlpha = item.m_lipAlpha;
		m_foundationAlpha = item.m_foundationAlpha;
	}

	protected MakeupAlpha Clone()
	{
		MakeupAlpha out = null;

		out = new MakeupAlpha();
		out.Set(this);

		return out;
	}
}
