package cn.poco.album.site;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import cn.poco.beautify4.UiMode;
import cn.poco.beautify4.site.Beautify4PageSite7;
import cn.poco.camera.CameraSetDataKey;
import cn.poco.camera.site.CameraPageSite305;
import cn.poco.filterBeautify.FilterBeautifyProcessor;
import cn.poco.framework.DataKey;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;

/**
 * 社区->调用美化美颜功能
 *
 * @author lmx
 *         Created by lmx on 2017/11/2.
 */

public class AlbumSite300 extends AlbumSite
{
    @Override
    public void onPhotoSelected(Context context, Map<String, Object> params)
    {
        //选图 修图 保存后直接跳转到社区，取消后退到选图页
        HashMap<String, Object> temp = new HashMap<>();
        temp.put("imgs", MakeRotationImg((String[]) params.get("imgs"), true));
        temp.put(DataKey.BEAUTIFY_DEF_OPEN_PAGE, m_inParams.get(DataKey.BEAUTIFY_DEF_OPEN_PAGE));
        temp.put(DataKey.BEAUTIFY_DEF_SEL_URI, m_inParams.get(DataKey.BEAUTIFY_DEF_SEL_URI));
        temp.put("index", params.get("index"));
        temp.put("folder_name", params.get("folder_name"));
        temp.put("goto_save", true);
        temp.put("show_exit_dialog", false);
        MyFramework.SITE_Open(context, Beautify4PageSite7.class, temp, Framework2.ANIM_NONE);
    }

    @Override
    public void onOpenCamera(Context context)
    {
        HashMap<String, Object> params = new HashMap<>();
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
        MyFramework.SITE_Open(context, CameraPageSite305.class, params, Framework2.ANIM_NONE);
    }
}
