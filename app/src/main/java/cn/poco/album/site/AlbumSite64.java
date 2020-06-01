package cn.poco.album.site;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import cn.poco.beautify4.UiMode;
import cn.poco.camera.CameraSetDataKey;
import cn.poco.camera.site.CameraPageSite65;
import cn.poco.filterBeautify.FilterBeautifyProcessor;
import cn.poco.framework.DataKey;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;
import cn.poco.makeup.site.MakeupSPageSite;


/**
 * 一键萌妆
 */
public class AlbumSite64 extends AlbumSite {

	public void onOpenCamera(Context context) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.putAll(CameraSetDataKey.GetMakeupAndTakePicture());
		params.put(CameraSetDataKey.KEY_HIDE_PHOTO_PICKER_BTN, true);
		params.put(CameraSetDataKey.KEY_IS_FILTER_BEAUTITY_PROCESS, true);//执行滤镜效果处理
		Object obj = m_inParams.get(DataKey.BEAUTIFY_DEF_OPEN_PAGE);
		if (obj != null && obj instanceof Integer)
		{
			if ((Integer) obj == UiMode.LVJING.GetValue())
			{
				//滤镜处理协议，只处理美形美颜
				params.put(CameraSetDataKey.KEY_FILTER_BEAUTITY_PROCESS_MASK, FilterBeautifyProcessor.ONLY_BEAUTY | FilterBeautifyProcessor.ONLY_SHAPE);
			}
			else if ((Integer) obj == UiMode.MEIYAN.GetValue())
			{
				//美颜处理协议，只处理美形滤镜
				params.put(CameraSetDataKey.KEY_FILTER_BEAUTITY_PROCESS_MASK, FilterBeautifyProcessor.ONLY_FILTER | FilterBeautifyProcessor.ONLY_SHAPE);
			}
			else
			{
				params.put(CameraSetDataKey.KEY_FILTER_BEAUTITY_PROCESS_MASK, FilterBeautifyProcessor.FILTER_BEAUTY);
			}
		}
		else
		{
			params.put(CameraSetDataKey.KEY_FILTER_BEAUTITY_PROCESS_MASK, FilterBeautifyProcessor.FILTER_BEAUTY);
		}
		params.put(DataKey.BEAUTIFY_DEF_OPEN_PAGE, m_inParams.get(DataKey.BEAUTIFY_DEF_OPEN_PAGE));
		params.put(DataKey.BEAUTIFY_DEF_SEL_URI, m_inParams.get(DataKey.BEAUTIFY_DEF_SEL_URI));
		MyFramework.SITE_Open(context, CameraPageSite65.class, params, Framework2.ANIM_NONE);
	}

	@Override
	public void onBack(Context context) {
		super.onBack(context);
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
		HomePageSite.CloneBusinessParams(temp, m_inParams);
		MyFramework.SITE_Open(context, MakeupSPageSite.class, temp, Framework2.ANIM_NONE);
	}

}
