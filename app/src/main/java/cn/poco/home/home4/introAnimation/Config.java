package cn.poco.home.home4.introAnimation;

import static cn.poco.home.home4.utils.PercentUtil.HeightPxToPercent;
import static cn.poco.home.home4.utils.PercentUtil.RadiusPxToPercent;
import static cn.poco.tianutils.ShareData.m_screenHeight;
import static cn.poco.tianutils.ShareData.m_screenWidth;

/**
 * Created by lgd on 2016/12/8.
 */

public class Config {
    //initData 那里也要设置
    public static int RADIUS_BIG_CIRCLE ;
    public static int RADIUS_SMALL_CIRCLE ;
    public static int CENTER_X ;
    public static int CENTER_Y ;
    public static int START_X ;
    public static int START_Y ;
    public static int DISTANCE_DROP;
    public static int DISTANCE_SEGREGATE;        //分开的距离           106/53

    public static int DISTANCE_START_SEGREGATE;
    public static int DISTANCE_STOP_SEGREGATE ;
    //弓形
    public static int ARCH_WIDTH ;
    public static int ARCH_HEIGHT;
    public static int ARCH_MAX_HEIGHT;
    public static int ARCH_CENTER_X;
    public static int ARCH_CENTER_Y ;

    //主页使用center的相应位置
//	public final static int BEAUTY_LOGO_TOP_MARGIN = HeightPxToPercent(289);
//	public final static int CAMERA_CENTER_TOP_MARGIN = CENTER_Y+DISTANCE_SEGREGATE-m_screenHeight/2;
//	public final static int AD_CENTER_BOTTOM_MARGIN = m_screenHeight/2-(CENTER_Y-DISTANCE_SEGREGATE);
    public static int CAMERA_CENTER_TOP_MARGIN ;
    public static int AD_CENTER_BOTTOM_MARGIN ;

    // 渐显时间
    public static final int DURATION_FADE_OUT = 500;
    // 分离时间
    public static final int DURATION_SEGREGATE = 500;
    //渐隐
    public static final int DURATION_FADE_IN = 500;
    // 缩小下落时间
    public static final int DURATION_DROP = 400;
    // 弧形弹出时间
    public static final int DURATION_ARCH = 500;

    static{
        initData();
    }

    public static void initData() {
//        RADIUS_BIG_CIRCLE = HeightPxToPercent(123);
//        RADIUS_SMALL_CIRCLE = HeightPxToPercent(40);
        RADIUS_BIG_CIRCLE = RadiusPxToPercent(123);
        RADIUS_SMALL_CIRCLE = RadiusPxToPercent(40);

        CENTER_X = m_screenWidth / 2;
//        CENTER_Y = HeightPxToPercent(761);
        CENTER_Y = m_screenHeight/2+HeightPxToPercent(761 - 640);
        START_X = m_screenWidth / 2;
        START_Y = CENTER_Y + RADIUS_SMALL_CIRCLE;
        DISTANCE_DROP = m_screenHeight - START_Y + RADIUS_BIG_CIRCLE;
        DISTANCE_SEGREGATE = HeightPxToPercent(167);        //分开的距离           106/53

        DISTANCE_START_SEGREGATE = RADIUS_BIG_CIRCLE / 2;
        DISTANCE_STOP_SEGREGATE = RADIUS_BIG_CIRCLE + HeightPxToPercent(25);
        //弓形
        ARCH_WIDTH = RadiusPxToPercent(330);
        ARCH_HEIGHT = RadiusPxToPercent(93);
        ARCH_MAX_HEIGHT = RadiusPxToPercent(110);
        ARCH_CENTER_X = m_screenWidth / 2;
        ARCH_CENTER_Y = m_screenHeight;

        CAMERA_CENTER_TOP_MARGIN = HeightPxToPercent(288);
        AD_CENTER_BOTTOM_MARGIN = HeightPxToPercent(46);
    }


}