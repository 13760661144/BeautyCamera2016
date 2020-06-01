package cn.poco.beautify4;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.text.TextUtils;

import cn.poco.camera.RotationImg2;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.tianutils.CommonUtils;
import cn.poco.utils.Utils;
import my.beautyCamera.R;

/**
 * Created by: fwc
 * Date: 2017/4/7
 */
public class JaneEntry {

	public static final int SUCCESS = 0;
	public static final int UPDATE = 1;
	public static final int DOWNLOAD = 2;

	/**
	 * 检查简拼app
	 *
	 * @param context 上下文
	 * @return
	 */
	public static int getAppState(Context context) {

		int out = SUCCESS;

		try {
			PackageInfo info = context.getPackageManager().getPackageInfo("cn.poco.jane", 0);
			int versionCode = info.versionCode;
			if (versionCode >= 45) {
				out = SUCCESS;
			} else {
				out = UPDATE;
			}

		} catch (Throwable e) {
			out = DOWNLOAD;
		}

		return out;
	}

	/**
	 * 前往简拼
	 */
	public static void gotoJane(Context context, String image, boolean addTongJi) {
		switch (JaneEntry.getAppState(context)) {
			case JaneEntry.DOWNLOAD:
			case JaneEntry.UPDATE:
				if (addTongJi) {
					TongJi2.AddCountByRes(context, R.integer.修图_素材美化_拼图_马上下载);
					MyBeautyStat.onClickByRes(R.string.美颜美图_拼图页面_主页面_马上下载);
				}
				CommonUtils.OpenBrowser(context, "http://wap.adnonstop.com/jane/share/share.php");
				break;
			case JaneEntry.SUCCESS:
				if (addTongJi) {
					TongJi2.AddCountByRes(context, R.integer.修图_素材美化_拼图_马上使用);
					MyBeautyStat.onClickByRes(R.string.美颜美图_拼图页面_主页面_马上使用);
				}
				if (!TextUtils.isEmpty(image)) {
					String[] imgs = new String[1];
					RotationImg2 img2 = Utils.Path2ImgObj(image);
					int[] ds = new int[1];
					int[] fs = new int[1];

					imgs[0] = img2.m_orgPath;
					ds[0] = img2.m_degree;
					fs[0] = img2.m_flip;

					try {
						Intent intent = new Intent();
						intent.setAction("cn.poco.jane.puzzle");
						intent.putExtra("imgs", imgs);
						intent.putExtra("rotations", ds);
						intent.putExtra("flip", fs);
						intent.putExtra("package", context.getApplicationContext().getPackageName());
						context.startActivity(intent);
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
				break;
		}
	}
}
