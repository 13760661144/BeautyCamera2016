package cn.poco.resource;

public enum ResType
{
	FRAME(0x2), //也用于界面类型判断
	CARD(0x4), //也用于界面类型判断
	DECORATE(0x8), //也用于界面类型判断
	WATERMARK(0x10),
	//PUZZLE_BK(0x10),
	//PUZZLE_TEMPLATE(0x20),
	THEME(0x40),
	FRAME2(0x80),
	LOCK(0x200),
	BANNER(0x800),
	TEXT(0x1000),
	//LIGHT_EFFECT(0x2000),
	AD_SKIN_CHANNEL(0x3000),
	AD_SKIN_CLICK(0x4000),
	AD_SKIN_FULLSCREEN(0x4500),
	AD_CLICK(0x5000),
	AD_CHANNEL(0x6000),
	AD_BOOT(0x7000),
	AD_FULLSCREEN(0x8000),
	//FONT(0x10000),
	MY_LOGO(0x20000),
	RECOMMEND(0x40000),
	//APP(0x80000),
	DECORATE_GROUP(0x100000),
	GLASS(0x200000), //也用于界面类型判断
	MAKEUP_GROUP(0x400000), //也用于界面类型判断
	MAKEUP(0x800000),
	MAKEUP_DATA(0x1000000),
	MOSAIC(0x2000000), //也用于界面类型判断
	VIDEO_FACE(0x4000000),
	VIDEO_FACE_GROUP(0x4000001),
	FRAME_GROUP(0x8000000),
	BRUSH(0x10000000),
	BRUSH_GROUP(0x20000000),
    LIMIT(0x40000000),//限量素材
	FILTER(0x80000000),//滤镜下载
	FILTER_GROUP(0x80000001),
	PRE_BGM(0x50000000),
	RESOURCE_RED_DOT(0x50000001),
	LIVE_RESOURCE_RED_DOT(0x50000002),
	LIVE_VIDEO_FACE(0x40000002),
	LIVE_VIDEO_FACE_GROUP(0x4000003),
	AR_NEW_YEAR(0x4000004), // 2018 新春ar活动
	;

	private final int m_value;

	ResType(int value)
	{
		m_value = value;
	}

	public int GetValue()
	{
		return m_value;
	}

	public static ResType GetType(int value)
	{
		ResType out = null;

		ResType[] list = values();
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
