package cn.poco.gifEmoji;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.util.HashMap;

import cn.poco.camera.BrightnessUtils;
import cn.poco.camera.CameraSetDataKey;
import cn.poco.camera3.ui.PreviewBackMsgToast;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.gifEmoji.site.GifEmojiPageSite;
import cn.poco.gifEmoji.site.GifEmojiPageSite1;
import cn.poco.imagecore.ImageUtils;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.SysConfig;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.FileUtil;
import cn.poco.utils.WaitAnimDialog;
import cn.poco.video.NativeUtils;
import my.beautyCamera.R;

/**
 * Created by zwq on 2017/05/27 10:15.<br/><br/>
 */

public class GifEmojiPage extends IPage implements OnPreviewControlListener
{
    private GifEmojiPageSite mSite;
    private String mMp4Path;
    private int mDuration;
    private int mGifWidth;
    private int mGifHeight;
    private int mOrientation;
    public GifPreviewView mPreview;
    public GifEmojiSharePage mSharePage;
    private WaitAnimDialog mWaitAnimDialog;
    public Bitmap m_background;

    public String mGIFDirPath;
    public String mLastGIFCaption;
    public String mResTongjiId = null;

    private Thread mThread;
    private Handler mUIHandler;
    private final int MSG_CREATE_GIF_START = 1;
    private final int MSG_CREATE_GIF_SUCCEED = 2;

    private boolean isDoSaved;//是否保存过

    private String mFinallyPath;
    private DrawFilter mFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    public GifEmojiPage(final Context context, BaseSite site)
    {
        super(context, site);
        TongJiUtils.onPageStart(getContext(), R.string.表情包_预览);
        MyBeautyStat.onPageStartByRes(R.string.拍照_表情包预览页_主页面);
        mSite = (GifEmojiPageSite) site;

        mUIHandler = new Handler(Looper.getMainLooper())
        {
            @Override
            public void handleMessage(Message msg)
            {
                if (msg == null) return;

                switch (msg.what)
                {
                    case MSG_CREATE_GIF_START:
                    {
                        setWaitUI(true, "正在保存...");
                        break;
                    }
                    case MSG_CREATE_GIF_SUCCEED:
                    {
                        if (mSite instanceof GifEmojiPageSite1)
                        {
                            ((GifEmojiPageSite1) mSite).save(context, mFinallyPath == null ? (String) msg.obj : mFinallyPath);
                            return;
                        }
                        mPreview.mVideo.pause();
                        TongJiUtils.onPageStart(getContext(), R.string.表情包_分享);

                        setWaitUI(false, "");

                        isDoSaved = true;
                        initSharePage(mFinallyPath == null ? (String) msg.obj : mFinallyPath);
                    }
                }
            }
        };

        initGIFDir();
        initView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        TongJiUtils.onPageResume(getContext(), R.string.表情包_预览);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        TongJiUtils.onPagePause(getContext(), R.string.表情包_预览);
    }

    private void initGIFDir()
    {
        mGIFDirPath = SysConfig.GetAppPath() + File.separator + ".gif";
        DeleteGIFTempDir();
    }

    public void DeleteGIFTempDir()
    {
        FileUtil.deleteSDFile(mGIFDirPath, false);
    }

    private void initView()
    {
        mPreview = new GifPreviewView(getContext());
        mPreview.SetOnPreviewControlListener(this);
        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mPreview, params);

        mWaitAnimDialog = new WaitAnimDialog((Activity) getContext());
        mWaitAnimDialog.SetGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, (int) (ShareData.m_screenHeight * (538 * 1f / 1280)));
    }

    private void initSharePage(String path)
    {
        if (mSharePage == null)
        {
            mSharePage = new GifEmojiSharePage(getContext(), path, mResTongjiId, mSite.m_cmdProc);
            mSharePage.setAlpha(0);
            mSharePage.setClickable(true);
            mSharePage.setLongClickable(true);

            mSharePage.setBackOnClickListener(new GifEmojiSharePage.BackOnClickListener()
            {
                @Override
                public void back()
                {
                    TongJiUtils.onPageEnd(getContext(), R.string.表情包_分享);

                    mPreview.mVideo.resume();
                    mSharePage.ClearMemory();
                    removeView(mSharePage);
                    mSharePage = null;
                }

                @Override
                public void home()
                {
                    mSharePage.ClearMemory();
                    removeView(mSharePage);
                    mSharePage = null;
                    mSite.OnHome(getContext());
                }

                @Override
                public void preview()
                {
                    File file = new File(mGIFDirPath);
                    if (file.exists())
                    {
                        String[] childFilePath = file.list();
                        if (childFilePath != null && childFilePath.length > 0)
                        {
                            String gifPath = childFilePath[0];
                            if (!TextUtils.isEmpty(gifPath) && mSite != null)
                            {
                                //mSite.OnPreview(getContext(), mGIFDirPath + File.separator + gifPath, false);

                                GifEmojiSharePreviewPage.PreviewData previewData = new GifEmojiSharePreviewPage.PreviewData();
                                previewData.videoPath = mMp4Path;
                                previewData.titleTxt = mLastGIFCaption;
                                mSite.OnPreview(getContext(), previewData);
                            }
                        }
                    }
                    else
                    {
                        Toast.makeText(getContext(), R.string.preview_pic_delete, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void camera()
                {
                    if (mSite != null)
                    {
                        mSite.OnCamera(getContext());
                    }
                }

                @Override
                public void onCommunity()
                {
                    File file = new File(mGIFDirPath);
                    if (file.exists())
                    {
                        String[] childFilePath = file.list();
                        if (childFilePath != null && childFilePath.length > 0)
                        {
                            String gifPath = childFilePath[0];
                            if (!TextUtils.isEmpty(gifPath) && mSite != null)
                            {
                                mSite.onCommunity(getContext(), mGIFDirPath + File.separator + gifPath);
                            }
                        }
                    }
                    else
                    {
                        Toast.makeText(getContext(), R.string.preview_pic_delete, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onLogin()
                {
                    if (mSite != null)
                    {
                        mSite.OnLogin(getContext());
                    }
                }

                @Override
                public void onBindPhone()
                {
                    if (mSite != null)
                    {
                        mSite.OnBindPhone(getContext());
                    }
                }

                @Override
                public void onHomeCommunity()
                {
                    if (mSite != null)
                    {
                        mSite.OnHomeCommunity(getContext());
                    }
                }
            });

            if (m_background != null)
            {
                mSharePage.setBackground(m_background);
            }
            else
            {
                mSharePage.setBackgroundColor(Color.WHITE);
            }

            FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            this.addView(mSharePage, params);
        }
    }

    @Override
    public void SetData(HashMap<String, Object> params)
    {
        if (params != null)
        {
            if (params.containsKey("mp4Path"))
            {
                mMp4Path = (String) params.get("mp4Path");
            }
            if (params.containsKey("duration"))
            {
                mDuration = (Integer) params.get("duration");
            }
            if (params.containsKey("width"))
            {
                mGifWidth = (Integer) params.get("width");
            }
            if (params.containsKey("height"))
            {
                mGifHeight = (Integer) params.get("height");
            }
            if (params.containsKey("orientation"))
            {
                mOrientation = (Integer) params.get("orientation");
            }
            if (params.containsKey("res_tj_id"))
            {
                mResTongjiId = (String) params.get("res_tj_id");
            }
        }

        if (mMp4Path != null && !mMp4Path.equals(""))
        {
            mPreview.playVideo(mMp4Path);

//            mPreview.mSaveView.showAnim();
        }
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (mSharePage != null)
        {
            mSharePage.onActivityResult(requestCode, resultCode, data);
        }
        return super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPageResult(int siteID, HashMap<String, Object> params)
    {
        if (mSharePage != null)
        {
            mSharePage.onPageResult(siteID, params);
        }
        super.onPageResult(siteID, params);
    }

    @Override
    public void onBack()
    {
        if (mSharePage != null)
        {
            mSharePage.onBack();
            return;
        }
        if (mPreview.mIsShowEditPage)
        {
            mPreview.CloseEditPageAnim();
            return;
        }

        if (!isDoSaved)
        {
            PreviewBackMsgToast toast = new PreviewBackMsgToast();
            toast.setMsg(getResources().getString(R.string.cancel_save)).show(getContext());

//            Toast toast = Toast.makeText(getContext(), R.string.cancel_save, Toast.LENGTH_SHORT);
//            int topPaddingHeight = RatioBgUtils.getTopPaddingHeight(1f);
//            topPaddingHeight += ShareData.m_screenRealWidth * 1f / 1f + CameraPercentUtil.HeightPxToPercent(60);
//            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, topPaddingHeight);
//            toast.show();
        }

        if (mSite != null)
        {
            mSite.onBack(getContext(), getBackParams());
        }
    }


    private HashMap<String, Object> getBackParams()
    {
        HashMap<String, Object> params = new HashMap<>();
        params.put(CameraSetDataKey.KEY_IS_SHOW_STICKER_SELECTOR, false);
        return params;
    }

    @Override
    public void onClose()
    {
        MyBeautyStat.onPageEndByRes(R.string.拍照_表情包预览页_主页面);
        TongJiUtils.onPageEnd(getContext(), R.string.表情包_预览);
        if (mSharePage != null)
        {
            mSharePage.setBackOnClickListener(null);
        }

        mPreview.ClearMemory();
        removeView(mPreview);
        mPreview = null;
        DeleteGIFTempDir();
        m_background = null;

        if (mThread != null)
        {
            mThread.interrupt();
            mThread = null;
        }

        mUIHandler.removeMessages(MSG_CREATE_GIF_START);
        mUIHandler.removeMessages(MSG_CREATE_GIF_SUCCEED);
        mUIHandler = null;

        mWaitAnimDialog.dismiss();
        mWaitAnimDialog.cancel();
        mWaitAnimDialog = null;

        super.onClose();
    }

    /**
     * GIF预览返回回调
     */
    @Override
    public void onPreviewBack()
    {
        this.onBack();
    }

    /**
     * GIF预览保存回调
     */
    @Override
    public void onPreviewSave(final String text)
    {
        //回复默认亮度
        BrightnessUtils instance = BrightnessUtils.getInstance();
        if (instance != null)
        {
            instance.setContext(getContext()).unregisterBrightnessObserver();
            instance.resetToDefault();
            instance.clearAll();
        }
        if (mUIHandler != null)
        {
            mUIHandler.sendEmptyMessage(MSG_CREATE_GIF_START);
        }

        mThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                File dir = new File(mGIFDirPath);
                dir.mkdirs();

                String[] file = dir.list();
                if (mLastGIFCaption == null)
                {
                    if (file.length == 0) // 第一次保存
                    {
                        GetOutputGIF(dir.getPath(), text);
                    }
                    else
                    {
                        if (text != null) // 第一次保存
                        {
                            DeleteGIFTempDir(); // 先删除前一个
                            GetOutputGIF(dir.getPath(), text);
                        }
                    }
                }
                else if (!mLastGIFCaption.equals(text))// 修改了字幕之后再保存
                {
                    DeleteGIFTempDir(); // 先删除前一个
                    GetOutputGIF(dir.getPath(), text);
                }

                file = dir.list();
                if (file.length > 0)
                {
                    String path = mGIFDirPath + File.separator + file[0];

                    if (mLastGIFCaption == null || !mLastGIFCaption.equals(text))
                    {
                        mFinallyPath = GifEmojiSharePage.saveEmoji(getContext(), path);
                    }

                    if (mUIHandler != null)
                    {
                        Message msg = mUIHandler.obtainMessage();
                        msg.what = MSG_CREATE_GIF_SUCCEED;
                        msg.obj = path;
                        mUIHandler.sendMessage(msg);
                    }
                }
                mLastGIFCaption = text;
            }
        });
        mThread.start();
    }

    private boolean GetOutputGIF(@NonNull String saveDirPath, String text)
    {
        int previewGifWidth = CameraPercentUtil.WidthPxToPercent(540); // 视频预览宽高
        int previewGifHeight = CameraPercentUtil.WidthPxToPercent(540);

        int previewWaterWidth = CameraPercentUtil.WidthPxToPercent(170); // 水印预览宽高
        int previewWaterHeight = CameraPercentUtil.HeightPxToPercent(70);

        // 1.画bitmap
        Bitmap topLayer = Bitmap.createBitmap(mGifWidth, mGifHeight, Bitmap.Config.ARGB_8888);
        Bitmap watermark = BitmapFactory.decodeResource(getResources(), R.drawable.gif_watermark);
        Canvas canvas = new Canvas(topLayer);
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        paint.setAntiAlias(true);
        Matrix matrix = new Matrix();

        // 预览 540*540 压缩至 240*240 的比例
        float scaleX = (previewWaterWidth * 1f / watermark.getWidth()) * (mGifWidth * 1f / previewGifWidth);
        float scaleY = (previewWaterHeight * 1f / watermark.getHeight()) * (mGifHeight * 1f / previewGifHeight);

        float scale = Math.min(scaleX, scaleY);
        matrix.postScale(scale, scale);
        matrix.postTranslate(mGifWidth - watermark.getWidth() * scale, 0);
        canvas.drawBitmap(watermark, matrix, paint);

        if (text != null)
        {
            // 2.draw text 描边
            Rect rect = new Rect();
            paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scale * 27f, getResources().getDisplayMetrics()));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            paint.setColor(Color.BLACK);
            paint.getTextBounds(text, 0, text.length(), rect);
            int textW = rect.width();
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(text, (mGifWidth - textW) / 2f, mGifHeight - (36 * 1f / 540) * mGifHeight, paint);

            // 3.draw text 填充内容
            paint.reset();
            paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scale * 27f, getResources().getDisplayMetrics()));
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL);
            paint.setFilterBitmap(true);
            paint.setAntiAlias(true);
            paint.setShadowLayer(scale * CameraPercentUtil.WidthPxToPercent(5), scale * CameraPercentUtil.WidthPxToPercent(1), scale * CameraPercentUtil.WidthPxToPercent(1), 0x40000000);
            paint.getTextBounds(text, 0, text.length(), rect);
            textW = rect.width();
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(text, (mGifWidth - textW) / 2f, mGifHeight - (36 * 1f / 540) * mGifHeight, paint);
        }

        // 4.保存gif
        int size = NativeUtils.getFrameNumFromFile(mMp4Path); // 视频总帧数
        int duration = 100;
        int obtain = 0; // 取帧的 index
        int skip = 0; // 跳帧的 index
        int maxSize = 0; // gif 允许的最大帧数
        int repair = 0; // 补帧
        int realSize = 0; // gif 真实帧数
        if (mDuration != 0)
        {
            if (mDuration > 3000)
            {
                mDuration = 3000;
            }
            int time = mDuration;
            if (time > 2600)
            {// 9fps
                time = 2600;
            }
            maxSize = Math.round(time / 100.0f);
            realSize = maxSize;
            if (size > maxSize)
            {
                int residue = realSize - 2; // 保留一头一尾, 剩余需要取的帧数
                size -= 2; // 除去一头一尾, 视频剩余帧数

                int dc = size - residue; // 需要剔除的总帧数
                int group_size = dc; // 以 dc 为组数
                int item_size_for_dc_group = size / group_size; // 求得每组有多少帧，后续只需每组剔除1帧

                if (item_size_for_dc_group == 1) // 每组只有1帧 ex: size > 44
                {
                    //特殊 ex: size <= 50
                    item_size_for_dc_group = (int) (size * 1f / residue); // 用 剩余可取的帧数作为组数，求得每组有多少帧，后续在每组里取1帧
                    group_size = residue; // 组数
                    int re = size - group_size * item_size_for_dc_group; // 是否有剩余不足一组的

                    //特殊 ex: size > 50
                    if (re > (int) (size * 0.08f)) // 超过剩余帧数 的 8%, 调整取帧数量
                    {
                        // 再算一次，向上取整
                        item_size_for_dc_group = (int) Math.ceil(size * 1f / residue);
                        group_size = size / item_size_for_dc_group;
                        re = size - group_size * item_size_for_dc_group;
                    }

                    if (re != 0 && group_size < residue)
                    {
                        int DIF = residue - group_size;
                        repair = DIF >= re ? re : DIF;// 需要补的帧数
                    }

                    obtain = item_size_for_dc_group; // 需要获取的帧 index
                    realSize = group_size + repair + 2; // 真实帧数
                }
                else // ex: size <= 44
                {
                    skip = item_size_for_dc_group;// 需要剔除的帧 index
                    repair = residue - group_size * (item_size_for_dc_group - 1); // 需要补的帧数
                    realSize = dc * (skip - 1) + repair + 2; // 真实帧数
                }

                size += 2; // 还原视频总帧数
            }
            duration = mDuration / realSize; // 每帧持续的时长
        }

        String path = System.currentTimeMillis() + ".gif";
        int initResult = ImageUtils.WriteGif_Step1Path(saveDirPath + File.separator + path, mGifWidth, mGifHeight);
        if (initResult == 0)
        {
            Matrix mt = new Matrix();
            Paint p = new Paint();
            int count = 0;
            Bitmap gifBmp = null;
            Bitmap bmp = null;
            for (int i = 0; i < size; i++)
            {
                gifBmp = NativeUtils.getNextFrameBitmapFromFile(mMp4Path, 0);

                if (gifBmp == null) continue;

                if (i > 0 && i < size - 1)
                {
                    if (count >= realSize - 1)
                    {
                        continue;
                    }

                    if (repair != 0 && i > size - 2 - repair)
                    {

                    }
                    else if (obtain != 0)
                    {
                        if (i % obtain != 0)
                        {
                            continue;
                        }
                    }
                    else if (skip != 0)
                    {
                        if (i % skip == 0)
                        {
                            continue;
                        }
                    }
                }

                if (count >= realSize)
                {
                    continue;
                }

                count += 1;

                if (gifBmp.isMutable())
                {
                    bmp = gifBmp;
                }
                else
                {
                    bmp = gifBmp.copy(Bitmap.Config.ARGB_8888, true);
                }
                if (bmp == null || bmp.isRecycled())
                {
                    continue;
                }
                gifBmp = bmp;

                bmp = Bitmap.createBitmap(mGifWidth, mGifHeight, Bitmap.Config.ARGB_8888);
                canvas = new Canvas(bmp);
                canvas.setDrawFilter(mFilter);
                mt.reset();

                if (mOrientation > 0 /*&& count > 1*/)
                {
                    mt.setRotate(mOrientation, gifBmp.getWidth() / 2, gifBmp.getHeight() / 2);
                }
                mt.postScale(mGifWidth * 1.0f / gifBmp.getWidth(), mGifHeight * 1.0f / gifBmp.getHeight());
                p.reset();
                p.setAntiAlias(true);
                p.setFilterBitmap(true);
                canvas.drawBitmap(gifBmp, mt, p);

                mt.reset();
                p.reset();
                p.setAntiAlias(true);
                p.setFilterBitmap(true);
                canvas.drawBitmap(topLayer, mt, p);

                if (count == 1)
                {
                    m_background = bmp.copy(Bitmap.Config.ARGB_8888, true);
                }

                ImageUtils.WriteGif_Step2AddImage(bmp, duration);
            }

            ImageUtils.WriteGif_Step3Finish();
        }

        NativeUtils.cleanVideoGroupByIndex(0);
        return true;
    }

    private void setWaitUI(boolean flag, String str)
    {
        if (flag)
        {
            if (mWaitAnimDialog != null)
            {
                if (str != null && !str.equals("") && str.trim().length() > 0)
                {
                    mWaitAnimDialog.SetText(str);
                }
                mWaitAnimDialog.show();
            }
        }
        else
        {
            if (mWaitAnimDialog != null)
            {
                mWaitAnimDialog.hide();
            }
        }
    }
}
