package cn.poco.camera.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.album.site.AlbumSite20;
import cn.poco.camera.BrightnessUtils;
import cn.poco.filterBeautify.site.FilterBeautifyPageSite;
import cn.poco.filterManage.site.FilterMoreSite;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.gifEmoji.site.GifEmojiPageSite;
import cn.poco.lightApp06.site.BeautyPhotoPageSite;
import cn.poco.lightApp06.site.BeautyVideoPageSite;
import cn.poco.login.site.LoginPageSite1;
import cn.poco.login.site.LoginPageSite11;
import cn.poco.resource.ResType;
import cn.poco.webview.site.WebViewPageSite;

/**
 * 拍照到滤镜，参数的设置请查看CameraPage类的SetData方法的参数说明
 */
public class CameraPageSite extends BaseSite {

    public CameraPageSite() {
        super(SiteID.CAMERA);
    }

    @Override
    public IPage MakePage(Context context) {
        return new cn.poco.camera3.CameraPageV3(context, this);
    }

    public void resetDefaultBrightness(Context context)
    {
        //恢复手机亮度，清除数据
        BrightnessUtils instance = BrightnessUtils.getInstance();
        if (instance != null)
        {
            instance.setContext(context).unregisterBrightnessObserver();
            instance.resetToDefault();
            instance.clearAll();
        }
    }

    public void onBack(Context context) {
        onBack(context, false);
    }

    public void onBack(Context context, boolean hasAnim) {
//        MyFramework.SITE_Back(context, null, hasAnim ? Framework2.ANIM_NONE : Framework2.ANIM_TRANSLATION_LEFT);
        MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
    }

    /**
     * 打开相册
     *
     * @param params color_filter_id 滤镜id(int)
     */
    public void openPhotoPicker(Context context, HashMap<String, Object> params) {
        if (params == null) {
            params = new HashMap<String, Object>();
        }
        params.put("from_camera", true);
        MyFramework.SITE_Open(context, AlbumSite20.class, params, Framework2.ANIM_NONE);
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
        temp.put("from_camera", true);
        MyFramework.SITE_Open(context, FilterBeautifyPageSite.class, temp, Framework2.ANIM_NONE);
    }

    /**
     * 萌妆照预览
     *
     * @param context
     * @param params
     */
    public void openCutePhotoPreviewPage(Context context, HashMap<String, Object> params) {
        MyFramework.SITE_Open(context, BeautyPhotoPageSite.class, params, Framework2.ANIM_NONE);
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
        MyFramework.SITE_Open(context, BeautyVideoPageSite.class, params, Framework2.ANIM_NONE);
    }

    public void userLogin(Context context) {
        MyFramework.SITE_Popup(context, LoginPageSite1.class, null, Framework2.ANIM_NONE);
    }

    public void uploadShapeLogin(Context context) {
        MyFramework.SITE_Popup(context, LoginPageSite11.class, null, Framework2.ANIM_NONE);
    }

    public void showLimitResInfo(Context context, HashMap<String, Object> params) {
        MyFramework.SITE_Open(context, WebViewPageSite.class, params, Framework2.ANIM_TRANSLATION_LEFT);
    }

    /**
     * @param params url 网络地址
     */
    public void openCameraPermissionsHelper(Context context, HashMap<String, Object> params) {
        MyFramework.SITE_Popup(context, WebViewPageSite.class, params, Framework2.ANIM_TRANSLATION_TOP);
    }

    /**
     * 推荐位下载更多
     *
     * @param resType
     */
    public void openDownloadMoreFilter(Context context, ResType resType) {
        MyFramework.SITE_Popup(context, FilterMoreSite.class, null, Framework2.ANIM_NONE);
    }

    /**
     * @param params mp4Path    mp4文件路径(String) <br/>
     *               duration   时长(int) <br/>
     *               frameList  gif帧集合(ArrayList<Bitmap>) <br/>
     *               width      宽(int) <br/>
     *               height     高(int) <br/>
     */
    public void openGifEditPage(Context context, HashMap<String, Object> params) {
        MyFramework.SITE_Open(context, GifEmojiPageSite.class, params, Framework2.ANIM_NONE);
    }

    public void showTip(Context context) {

    }

}
