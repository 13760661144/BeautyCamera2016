package cn.poco.camera.site;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.util.HashMap;

import cn.poco.framework.MyFramework;

/**
 * Created by POCO on 2017/6/8.
 * 外部调用,拍视频
 */

public class CameraPageSite101 extends CameraPageSite
{
    /**
     * 动态贴纸视频预览
     *
     * @param params <br/>
     *               type 1：视频预览 0：拍照
     *               color_filter_id 滤镜id(int) <br/>
     *               width           视频宽(int) <br/>
     *               height          视频高(int) <br/>
     *               mp4_path        mp4文件路径(String) <br/>
     *               record_obj      视频录制对象(MyRecordVideo) <br/>
     */
    @Override
    public void openVideoPreviewPage(Context context, HashMap<String, Object> params)
    {
        int resultCode = Activity.RESULT_CANCELED;
        Intent data = new Intent();

        if (params != null)
        {
            int type = -1;
            String mp4Path = null;

            if (params.containsKey("type"))
            {
                type = (Integer) params.get("type");
            }

            switch (type)
            {
                case 0: //拍照
                {
                    //TODO 第三方调用动态贴纸拍照回调，暂未实现
                    break;
                }
                case 1: //视频
                {
                    if (params.containsKey("mp4_path"))
                    {
                        mp4Path = (String) params.get("mp4_path");
                    }
                    break;
                }
                default:
                {
                    resultCode = Activity.RESULT_CANCELED;
                    break;
                }
            }


            if (type == 1 && mp4Path != null)
            {
                int callType = (Integer) m_inParams.get(MyFramework.EXTERNAL_CALL_TYPE);
                switch (callType)
                {
                    case MyFramework.EXTERNAL_CALL_TYPE_CAMERA_VIDEO:
                    {
                        Object obj = m_inParams.get(MyFramework.EXTERNAL_CALL_IMG_SAVE_URI);
                        if (obj instanceof Uri)
                        {
                            //暂时不指定uri
                            Uri uri = (Uri) obj;
                            String absolutePath = new File(uri.toString()).getAbsolutePath();
                            if (absolutePath.endsWith(".mp4"))
                            {
                                data.setData((Uri) obj);
                            }
                            else
                            {
                                data.setData(Uri.fromFile(new File(mp4Path)));
                            }
                            resultCode = Activity.RESULT_OK;
                        }
                        else
                        {
                            data.setData(Uri.fromFile(new File(mp4Path)));
                            resultCode = Activity.RESULT_OK;
                        }
                        break;
                    }
                    default:
                    {
                        break;
                    }
                }
            }
        }
        MyFramework.SITE_Finish(context, resultCode, data);
    }
}
