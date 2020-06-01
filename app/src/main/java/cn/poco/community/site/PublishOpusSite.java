package cn.poco.community.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.camera.CameraSetDataKey;
import cn.poco.camera.site.CameraPageSite304;
import cn.poco.camera3.config.shutter.ShutterConfig;
import cn.poco.community.PublishOpusPage;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;

/**
 *
 * Created by lgh on 2017/8/24.
 */
public class PublishOpusSite extends BaseSite
{
	private Context mContext;

	public PublishOpusSite()
	{
		super(SiteID.PUBLISH_OPUS_PAGE);
	}

	@Override
	public IPage MakePage(Context context)
	{
		this.mContext=context;
		return new PublishOpusPage(context,this);
	}

	public void onBack(Context context,boolean b){
		HashMap<String, Object> param=new HashMap<>();
		param.put("isSuccess",b);
		MyFramework.SITE_Back(context, param, Framework2.ANIM_NONE);
	}

	/**
	 * 打开镜头 不需要美化,必须是popup
	 * 完成后在onPageResult返回路径
	 * @param params type 镜头类型  默认是拍照 type=image是拍照  type=video是拍视频
	 */
	public void openCamera(Context context,HashMap<String,Object> params)
	{
		if(params==null){
			params=new HashMap<>();
		}
		//拍照
		params.put("from_camera", true);
		params.put(CameraSetDataKey.KEY_HIDE_PHOTO_PICKER_BTN, true);
		//params.put(CameraSetDataKey.KEY_IS_FILTER_BEAUTITY_PROCESS, true);
		//params.put(CameraSetDataKey.KEY_FILTER_BEAUTITY_PROCESS_MASK, FilterBeautifyProcessor.FILTER_BEAUTY);
		params.put(CameraSetDataKey.KEY_CAMERA_SHOW_TAB_TYPE, ShutterConfig.TabType.PHOTO | ShutterConfig.TabType.CUTE);
		params.put(CameraSetDataKey.KEY_CAMERA_SELECT_TAB_TYPE, ShutterConfig.TabType.PHOTO);
		MyFramework.SITE_Popup(context, CameraPageSite304.class, params, Framework2.ANIM_TRANSLATION_TOP);
	}

}
