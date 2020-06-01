package cn.poco.beautify4;

/**
 * Created by Raining on 2016/12/7.
 * 4.0美化,ui模式
 */

public enum UiMode
{
	NORMAL(0),
	CLIP(0x1),
	FACE(0x2),
	BEAUTIFY(0x4),

	MEIYAN(0x8),
	SHOUSHEN(0x10),
	QUDOU(0x20),
	QUYANDAI(0x40),
	LIANGYAN(0x80),
	DAYAN(0x100),
	GAOBILIANG(0x200),
	WEIXIAO(0x400),
	CAIZHUANG(0x800),
	MEIYA(0x801),
	SHOUBI(0x802),
	ZENGGAO(0X803),

	LVJING(0x1000),
	XIANGKUANG(0x2000),
	TIETU(0x4000),
	MAOBOLI(0x8000),
	MASAIKE(0x10000),
	ZHIJIANMOFA(0x20000),
	PINTU(0x40000),
	YIJIANMENGZHUANG(0x80000);

	private final int m_value;

	UiMode(int value)
	{
		m_value = value;
	}

	public int GetValue()
	{
		return m_value;
	}

	public static UiMode GetType(int value)
	{
		UiMode out = NORMAL;

		UiMode[] list = values();
		if(list != null)
		{
			for(int i = 0; i < list.length; i++)
			{
				if(list[i].GetValue() == value)
				{
					out = list[i];
					break;
				}
			}
		}

		return out;
	}
}
