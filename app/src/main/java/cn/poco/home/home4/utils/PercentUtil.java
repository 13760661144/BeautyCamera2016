package cn.poco.home.home4.utils;

import cn.poco.tianutils.ShareData;

/**
 * Created by lgd on 2016/12/30.
 */

public class PercentUtil
{
	/**
	 * 宽720为标准，获取百分比位置
	 * @return
	 */
	public static int WidthPxToPercent(int size)
	{
		return (int)(size*ShareData.m_screenRealWidth/720f+0.5f);
	}

	/**
	 *高1280为标准,获取百分比位置
	 */
	public static int HeightPxToPercent(int size)
	{
		return (int)(size*ShareData.m_screenRealHeight/1280f+0.5f);
	}

	/**
	 * 宽1080为标准，获取百分比位置
	 * @return
	 */
	public static int WidthPxxToPercent(int size)
	{
		return (int)(size*ShareData.m_screenWidth/1080f+0.5f);
	}

	/**
	 *高1920为标准,获取百分比位置
	 */
	public static int HeightPxxToPercent(int size)
	{
		return (int)(size*ShareData.m_screenHeight/1920f+0.5f);
	}

	/**
	 * 宽720 高1280为标准
	 *  根据 宽 高比例选择        比例大（平板）使用HeightPxToPercent比例小(长屏手机)使用WidthPxToPercent
	 * @param size
	 * @return
	 */
	public static int RadiusPxToPercent(int size)
	{
		float ratio = 9f/16;
		if(ShareData.m_ratio > ratio){
			return HeightPxToPercent(size);
		}else{
			return WidthPxToPercent(size);
		}
	}

	/**
	 * 宽1080 高1920 为标准
	 *  根据 宽 高比例选择        比例大（平板）使用HeightPxToPercent比例小(长屏手机)使用WidthPxToPercent
	 * @param size
	 * @return
	 */
	public static int RadiusPxxToPercent(int size)
	{
		float ratio = 9f/16;
		if(ShareData.m_ratio > ratio){
			return HeightPxxToPercent(size);
		}else{
			return WidthPxxToPercent(size);
		}
	}

    /**
     *高1920为标准,获取百分比位置
     */
    public static int RealHeightPxxToPercent(int size)
    {
        return (int)(size*ShareData.m_screenRealHeight/1920f+0.5f);
    }

	/**
	 *  ios 1334  750 坐标
	 */
	public static int TransformIosWidthPx(int size)
	{
		return (int) (size * ShareData.m_screenWidth /750 + 0.5f);
	}

    /**
     *  ios 1334  750 坐标
     */
	public static int TransformIosHeightPx(int size)
	{
		return (int) (size * ShareData.m_screenHeight /1334 + 0.5f);
	}
	/**
	 *   ios 1334  750 坐标
	 */
	public static int TransFormIosRadiusPx(int size)
	{
		float ratio = 9f/16;
		if(ShareData.m_ratio >= ratio){
			return TransformIosHeightPx(size);
		}else{
			return TransformIosWidthPx(size);
		}
	}

//	/**
//	 *   ios   2436 1334 坐标
//	 */
//	public static int TransFormIosRadiusPxx(int size)
//	{
//		float ratio = 9f/16;
//		if(ShareData.m_ratio > ratio){
//			return TransformIosWidthPxx(size);
//		}else{
//			return TransformIosHeightPxx(size);
//		}
//	}

//	/**
//	 * ios  2436 1334
//	 */
//	public static int TransformIosWidthPxx(int size)
//	{
//		return (int) (size * ShareData.m_screenWidth /1125 + 0.5f);
//	}
//
//	/**
//	 *  ios 2436  1125
//	 */
//	public static int TransformIosHeightPxx(int size)
//	{
//		return (int) (size * ShareData.m_screenHeight /2436 + 0.5f);
//	}

}
