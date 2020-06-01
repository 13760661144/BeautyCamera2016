package cn.poco.beautify;

public enum SlimType
{
	DRAG(0),
	TOOL(1),
	;

	private final int m_value;

	SlimType(int value)
	{
		m_value = value;
	}

	public int GetValue()
	{
		return m_value;
	}

	public static SlimType GetType(int value)
	{
		SlimType out = DRAG;

		SlimType[] list = values();
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
