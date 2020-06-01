package cn.poco.camera.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.album.site.AlbumSite81;
import cn.poco.filterBeautify.site.FilterBeautifyPageSite200;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.gifEmoji.site.GifEmojiPageSite1;
import cn.poco.lightApp06.site.BeautyPhotoPageSite;
import cn.poco.lightApp06.site.BeautyVideoPageSite1;

/**
 * 社区活动到动态贴纸
 */
public class CameraPageSite301 extends CameraPageSite {

    @Override
    public void onBack(Context context) {
        resetDefaultBrightness(context);
        //社区活动返回到详情页
        MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
    }

    /**
     * 拍照
     *
     * @param params <br/>
     *               color_filter_id 滤镜id(int) <br/>
     *               img_file        数据保存对象(ImageFile2) <br/>
     *               camera_mode     镜头模式(int) <br/>
     *               layout_mode     布局模式(int) <br/>
     */
    public void onTakePicture(Context context, HashMap<String, Object> params) {
        HashMap<String, Object> temp = new HashMap<>();
        temp.putAll(params);
        temp.put("imgs", params.get("img_file"));
        temp.put("show_share_btn", false);
        MyFramework.SITE_Open(context, FilterBeautifyPageSite200.class, temp, Framework2.ANIM_NONE);
    }

    /**
     * 打开相册
     *
     * @param params color_filter_id 滤镜id(int)
     */
    public void openPhotoPicker(Context context, HashMap<String, Object> params) {
        resetDefaultBrightness(context);
        if (params == null) {
            params = new HashMap<>();
        }
        params.put("from_camera", true);
        MyFramework.SITE_Open(context, AlbumSite81.class, params, Framework2.ANIM_NONE);
    }

    public void openCutePhotoPreviewPage(Context context, HashMap<String, Object> params)
    {
        resetDefaultBrightness(context);

        // ------------ 跳转到预览页 ------------
        if (params == null)
        {
            params = new HashMap<>();
        }
        params.put("community", true);
        MyFramework.SITE_Open(context, BeautyPhotoPageSite.class, params, Framework2.ANIM_NONE);

        // ------------ 直接保存跳转到社区 ------------
        /*if (params != null)
        {
            params.putAll(CameraPageSite300.makeCircleExtra(params));

            Bitmap bitmap = (Bitmap) params.get("bmp");

            boolean hasWaterMark = false;
            if (params.containsKey("has_water_mark"))
            {
                hasWaterMark = (Boolean) params.get("has_water_mark");
            }
            if (hasWaterMark) {
                WatermarkItem watermarkItem = WatermarkResMgr2.getInstance().GetWaterMarkById(
                        SettingInfoMgr.GetSettingInfo(context).GetPhotoWatermarkId(WatermarkResMgr2.getInstance().GetDefaultWatermarkId(context)));
                if (watermarkItem != null && watermarkItem.res != null)
                {
                    boolean addDateState = SettingInfoMgr.GetSettingInfo(context).GetAddDateState();
                    PhotoMark.drawWaterMarkLeft(bitmap, MakeBmpV2.DecodeImage(context,
                            watermarkItem.res, 0, -1, -1, -1, Bitmap.Config.ARGB_8888), addDateState);
                }
            }
            String path = FileCacheMgr.GetLinePath();
            String cacheStr = Utils.SaveImg(context, bitmap, path, 100, false);
            if (cacheStr == null)
            {
                ImageFile2 imgFile = new ImageFile2();
                try
                {
                    byte[] imgData = ImageUtils.JpgEncode(bitmap, 100);
                    imgFile.SetData(context, imgData, 0, 0, -1);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    return;
                }
                RotationImg2[] rotationImg2s = imgFile.SaveTemp(context);
                cacheStr = rotationImg2s[0].m_img.toString();
            }
            HashMap<String, Object> temp = new HashMap<>();
            temp.put("imgPath", new String[]{cacheStr});
            MyFramework.SITE_BackTo(context, HomePageSite.class, temp, Framework2.ANIM_NONE);
        }*/
    }

    /**
     * 动态贴纸视频预览
     *
     * @param params <br/>
     *               color_filter_id 滤镜id(int) <br/>
     *               width           视频宽(int) <br/>
     *               height          视频高(int) <br/>
     *               mp4_path        mp4文件路径(String) <br/>
     *               record_obj      视频录制对象(MyRecordVideo) <br/>
     */
    public void openVideoPreviewPage(Context context, HashMap<String, Object> params) {
        resetDefaultBrightness(context);
        params.put("save_jump", true);
        MyFramework.SITE_Open(context, BeautyVideoPageSite1.class, params, Framework2.ANIM_NONE);
    }


    /**
     * @param params mp4Path    mp4文件路径(String) <br/>
     *               duration   时长(int) <br/>
     *               frameList  gif帧集合(ArrayList<Bitmap>) <br/>
     *               width      宽(int) <br/>
     *               height     高(int) <br/>
     */
    public void openGifEditPage(Context context, HashMap<String, Object> params) {
        params.putAll(CameraPageSite300.makeCircleExtra(params));
        MyFramework.SITE_Open(context, GifEmojiPageSite1.class, params, Framework2.ANIM_NONE);
    }
}
