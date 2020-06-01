package cn.poco.album.site;

import android.content.Context;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.poco.album.AlbumPage;
import cn.poco.beautify4.site.Beautify4PageSite;
import cn.poco.camera.CameraSetDataKey;
import cn.poco.camera.RotationImg2;
import cn.poco.camera.site.CameraPageSite5;
import cn.poco.framework.BaseSite;
import cn.poco.framework.FileCacheMgr;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.utils.Utils;

/**
 * Created by: fwc
 * Date: 2016/11/21
 * 美化选图
 */
public class AlbumSite extends BaseSite {

	public AlbumSite() {
		super(SiteID.ALBUM);
	}

	@Override
	public IPage MakePage(Context context) {
		return new AlbumPage(context, this);
	}

	public void onBack(Context context) {
		MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
	}

	/**
	 * 打开Camera
	 */
	public void onOpenCamera(Context context) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(CameraSetDataKey.KEY_START_MODE, 1);
        params.put(CameraSetDataKey.KEY_HIDE_PHOTO_PICKER_BTN, true);
		MyFramework.SITE_Open(context, CameraPageSite5.class, params, Framework2.ANIM_NONE);
	}

	/**
	 * 选择好图片后回调
	 *
	 * @param params 参数
	 *               imgs: String[] 图片的路径
	 */
	public void onPhotoSelected(Context context, Map<String, Object> params) {
		HashMap<String, Object> temp = new HashMap<>();
		temp.put("imgs", MakeRotationImg((String[])params.get("imgs"), true));
		temp.put("index", params.get("index"));
		temp.put("folder_name", params.get("folder_name"));
		temp.put("show_exit_dialog", false);
		MyFramework.SITE_Open(context, Beautify4PageSite.class, temp, Framework2.ANIM_NONE);
	}

	/**
	 * 把路径变为RotationImg2
	 *
	 * @param arr
	 * @return
	 */
	public static RotationImg2[] MakeRotationImg(String[] arr, boolean copy) {
		RotationImg2[] out = null;
		if (arr != null && arr.length > 0) {
			out = new RotationImg2[arr.length];
			for (int i = 0; i < arr.length; i++) {
				RotationImg2 temp = Utils.Path2ImgObj(arr[i]);
				if (copy) {
					temp.m_img = FileCacheMgr.GetLinePath();
					try {
						File destFile = new File((String)temp.m_img);
						File tempFile = destFile.getParentFile();
						if (tempFile != null && !tempFile.exists()) {
							tempFile.mkdirs();
						}
						FileUtils.copyFile(new File(arr[i]), destFile);
					} catch (Throwable e) {
						e.printStackTrace();
					}
				} else {
					temp.m_img = arr[i];
				}

				out[i] = temp;
			}
		}

		return out;
	}
}
