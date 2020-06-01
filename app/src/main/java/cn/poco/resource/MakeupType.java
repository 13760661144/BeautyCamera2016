package cn.poco.resource;

public enum MakeupType
{
	/**
	 * 五官分类必须每种类型占一位,而且有层次关系,在上面的数值越大,只能用31位,最高位不能用
	 * 左边0x55555555,右边0xAAAAAAAA
	 */
	NONE(-1), //默认值
	NONE_V2(0x00000000), //无(界面)
	FOUNDATION(0x00000001), //粉底(界面)
	LIP(0x00000002), //唇彩(界面)
	CHEEK_L(0x00000040), //腮红(界面)
	CHEEK_R(0x00000080),
	TATTOO(0x00000200), //纹身(界面)
	KOHL_L(0x00000400), //眼影(界面)
	KOHL_R(0x00000800),
	EYELINER_DOWN_L(0x00001000), //下眼线
	EYELINER_DOWN_R(0x00002000),
	EYELINER_UP_L(0x00004000), //上眼线(界面)
	EYELINER_UP_R(0x00008000),
	EYEBROW_L(0x00010000), //眉毛(界面)
	EYEBROW_R(0x00020000),
	EYELASH_DOWN_L(0x00100000), //下睫毛
	EYELASH_DOWN_R(0x00200000),
	EYELASH_UP_L(0x00400000), //上睫毛(界面)
	EYELASH_UP_R(0x00800000),
	EYE_L(0x01000000), //美瞳(界面)
	EYE_R(0x02000000),
	ASET(0x07FFFFFF), //界面
	CHECK_FACE(0x11111111), //界面(三点)
	CHECK_ALL(0x22222222), //界面(全部点)
	NOSE_l(0x1<<23),//定点界面鼻子
	CHIN(0x1<<24),//定点界面下巴
	;

	private final int m_value;

	MakeupType(int value)
	{
		m_value = value;
	}

	public int GetValue()
	{
		return m_value;
	}

	public static MakeupType GetType(int value)
	{
		MakeupType out = NONE;

		MakeupType[] list = values();
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