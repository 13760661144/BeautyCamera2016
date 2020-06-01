package cn.poco.credits;

import android.content.Context;
import android.content.pm.PackageInfo;

/**
 * 工具方法集合
 */
public class Utils
{

    /**
     * 是否安装了参数中的包名的应用
     */
    public static boolean isInstalled(Context context, String pkgName) {
        boolean result = false;
        if (context != null) {
            try {
                PackageInfo info = context.getPackageManager().getPackageInfo(pkgName, 0);
                if (info != null) {
                    result = true;
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}
