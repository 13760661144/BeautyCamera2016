package cn.poco.share;

import android.content.Context;

import my.beautyCamera.R;

public class Constant
{
	//public static final String sinaConsumerKey = "2059939591";
	public static String getSinaConsumerKey(Context context)
	{
		return context.getResources().getString(R.string.sina_app_key);
	}

	//public static final String sinaConsumerSecret = "4bb869136045525a8426d6a24b299293";
	public static String getSinaConsumerSecret(Context context)
	{
		return context.getResources().getString(R.string.sina_app_secret);
	}

	public static final String sinaUserId = "2387289477";
	public static final String qqConsumerKey = "a3d0d9ebd1a34889aba5b0f9ede609b1";
	public static final String qqConsumerSecret = "d641f607026474733ac77f54189f9aff";

	//public final static String qzoneAppKey = "100734819";
	public static String getQzoneAppKey(Context context)
	{
		return context.getResources().getString(R.string.tencent_app_id);
	}

	//public final static String qzoneAppSerect = "dc9fcf673d6186d9685cd1589eeef94a";
	public static String getQzoneAppSerect(Context context)
	{
		return context.getResources().getString(R.string.tencent_app_secret);
	}

	//public final static String renrenAppKey = "3ca36727dfc94338b91155a8336a9148";
	//public final static String renrenAppSecret = "8ac812a0a54f4482ba51972581f477cc";

	//public final static String facebookAppKey = "297615880292869";
	//public final static String facebookAppSecret = "a399bd5b5721354b2930f81e972b1152";

	//public final static String tumblrAppKey = "kCOA5hupMfKBAYKXotPOW0r7UpRU8ncVZwSiEENnOxyyM9BOI4";
	//public final static String tumblrAppSecret = "ewCVEcFEFm46RwoBMr6btAA5gVFmMv9opSWUxLLcUxIYJTdNV1";

	//public final static String doubanAppKey = "04bd07072cec69e90e37151586c53dbb";
	//public final static String doubanAppSecret = "ded091c4138ae91d";

	//public final static String twitterAppKey = "Boyew7YdSoVERzxUesVEdQ";
	//public final static String twitterAppSecret = "ScmhbkbGGYPYiaEaNKLClJmVMz03Lhkgy6VO1N4S4";

	//public final static String weixinAppId = "wx7a5a2c298900b133";
	public static String getWeixinAppId(Context context)
	{
		return context.getResources().getString(R.string.weixin_app_id);
	}

	//public final static String weixinAppSecret = "96b3ff3f02693fb56203891d11e8a87a";
	public static String getWeixinAppSecret(Context context)
	{
		return context.getResources().getString(R.string.weixin_app_secret);
	}

	//public final static String yixinAppId = "yx72b6edd0514f4955a562f4929bbfd1cc";

	//public final static String domobPublisherID = "56OJw4QYuNKOfOcEwS";
	//public final static String domobPlacementID = "16TLu3OaAp-t4NUfWn7TLSUk";

	//public static final String POCO_CTYPE = "poco_beautycamera_android";
	public static final String POCO_CTYPE_NUMBER = "15";

	public static final String URL_JING = "http://img-m.poco.cn/mypoco/mtmpfile/API/poco_camera/topic_list.xml";
}
