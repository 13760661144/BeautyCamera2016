package cn.poco.featuremenu.util;

/**
 * Created by Simon Meng on 2017/10/12.
 * Guangzhou Beauty Information Technology Co.,Ltd
 */

public class MenuRule {
    public static final int UNDEFINED = -1;
    public static final int SHOW_NUMBER_TIPS = 0;
    public static final int SHOW_RED_DOT_TIPS = 1;



    public static int showRedDotOrNumberTips(int systemCount, int chatCount) {
        if (chatCount > 0) {
            return SHOW_NUMBER_TIPS;
        }

        if (systemCount > 0) {
            return SHOW_RED_DOT_TIPS;
        }
        return UNDEFINED;
    }












}
