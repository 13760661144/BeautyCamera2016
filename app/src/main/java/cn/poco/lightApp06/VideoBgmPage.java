package cn.poco.lightApp06;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import cn.poco.camera3.info.PreviewBgmInfo;
import cn.poco.camera3.ui.bgm.BgmUI;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.dynamicSticker.StickerHelper;
import cn.poco.dynamicSticker.StickerMediaPlayer;
import cn.poco.filterPendant.MyStatusButton;
import cn.poco.resource.DownloadMgr;
import cn.poco.resource.PreviewBgmResMgr2;
import cn.poco.statistics.TongJi2;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.FileUtil;
import cn.poco.video.AudioStore;
import cn.poco.video.FileUtils;
import my.beautyCamera.R;

/**
 * @author lmx
 *         Created by lmx on 2017/7/25.
 */

public class VideoBgmPage extends FrameLayout implements StickerMediaPlayer.OnMediaPlayerListener, BgmUI.OnBgmUIControlCallback
{
    //music
    private StickerMediaPlayer mMusicPlayer;
    private boolean isPreparedToStart = true;
    private boolean isLoopingMusic = true;
    private String mMusicPath;

    //ui
    private BgmUI mBgmUI;
    private View mMaskView;

    //callback
    private VideoBgmPage.Callback mCallback;

    private String tempAssetPath;
    private Toast mToast;

    public VideoBgmPage(@NonNull Context context)
    {
        super(context);
        initView();
    }

    private void initView()
    {
        tempAssetPath = DownloadMgr.getInstance().PREVIEW_BGM_PATH + File.separator + "temp_asset";

        mBgmUI = new BgmUI(getContext());
        mBgmUI.SetOnBgmUIControlCB(this);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(320));
        params.gravity = Gravity.CENTER_HORIZONTAL;
        this.addView(mBgmUI, params);

        mMaskView = new View(getContext());
        mMaskView.setClickable(true);
        mMaskView.setLongClickable(true);
        mMaskView.setVisibility(GONE);
        params = new LayoutParams(LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(320));
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        this.addView(mMaskView, params);
    }

    public void setCallback(VideoBgmPage.Callback mCallback)
    {
        this.mCallback = mCallback;
    }

    public void setBtnClickable(boolean able)
    {
        if (mBgmUI != null)
        {
            mBgmUI.setBtnClickable(able);
        }
    }

    public void setVolume(float volume)
    {
        if (mMusicPlayer != null)
        {
            mMusicPlayer.setVolume(volume);
        }
    }

    public void setFold(boolean isFold)
    {
        if (mBgmUI != null)
        {
            MyStatusButton bmgCenterBtn = mBgmUI.getBmgCenterBtn();
            if (bmgCenterBtn != null)
            {
                bmgCenterBtn.setBtnStatus(true, isFold);
            }
        }
    }

    public boolean isFold()
    {
        if (mBgmUI != null && mBgmUI.getBmgCenterBtn() != null)
        {
            return mBgmUI.getBmgCenterBtn().isDown();
        }
        return true;
    }

    public void setUIEnable(boolean enable)
    {
        mMaskView.setVisibility(enable ? GONE : VISIBLE);
    }

    public void setLoopingMusic(boolean isLoop)
    {
        this.isLoopingMusic = isLoop;
        if (mMusicPlayer != null)
        {
            mMusicPlayer.setLooping(isLoop);
        }
    }

    public void setPreparedToStart(boolean preparedToStart)
    {
        isPreparedToStart = preparedToStart;
    }

    public void initMusicPlayer()
    {
        if (mMusicPlayer == null)
        {
            mMusicPlayer = new StickerMediaPlayer(getContext());
            mMusicPlayer.setLooping(this.isLoopingMusic);
            mMusicPlayer.setListener(this);
        }
    }

    /**
     * 背景音乐路径
     *
     * @return null 无音乐
     */
    public String getMusicPath()
    {
        return mMusicPath;
    }

    public long getMusicDuration()
    {
        if (mMusicPlayer != null)
        {
            return (long) mMusicPlayer.getDuration();
        }
        return 0;
    }

    /**
     * @param musicPath 1、sdcard 文件 2、asset music目录下音频文件
     */
    public void setMusicPath(String musicPath)
    {
        this.mMusicPath = musicPath;

        try
        {
            if (!TextUtils.isEmpty(musicPath))
            {
                if (mMusicPlayer == null)
                {
                    initMusicPlayer();
                }

                if (StickerHelper.isAssetFile(getContext(), musicPath))
                {

                    String[] split = musicPath.split("file:///android_asset/");
                    if (split.length == 2)
                    {
                        AssetManager assets = getContext().getAssets();
                        AssetFileDescriptor assetFileDescriptor = assets.openFd(split[1]);
                        mMusicPlayer.setDataSource(assetFileDescriptor);
                    }
                }
                else if (new File(musicPath).exists())
                {
                    Uri uri = Uri.parse(musicPath);
                    mMusicPlayer.setDataSource(uri);
                }
            }
            else
            {
                if (mMusicPlayer != null)
                {
                    mMusicPlayer.release();
                }
            }
        }
        catch (Throwable t)
        {

        }
    }

    public void seekTo(int mesc, boolean resume)
    {
        if (mMusicPlayer != null)
        {
            mMusicPlayer.seekTo(mesc, resume);
        }
    }

    public void seekTo(int mesc)
    {
        seekTo(mesc, true);
    }

    public void setCurrentPosition(int mesc)
    {
        if (mMusicPlayer != null)
        {
            mMusicPlayer.setCurrentPos(mesc);
        }
    }

    public void onStart()
    {
        if (mMusicPlayer != null && !mMusicPlayer.isPlaying())
        {
            float volume = 1f;
            if (mCallback != null)
            {
                volume = mCallback.getVolume();
            }
            mMusicPlayer.setVolume(volume, volume);
            mMusicPlayer.start();

            if (mCallback != null)
            {
                mCallback.onMediaStart();
            }
        }
    }


    public void onPause()
    {
        if (mMusicPlayer != null)
        {
            mMusicPlayer.pause();
        }
    }

    public void onResume()
    {
        if (mMusicPlayer != null)
        {
            mMusicPlayer.resume(false);
        }
    }

    public void onStop()
    {
        if (mMusicPlayer != null)
        {
            mMusicPlayer.release();
        }
    }

    public void clear()
    {
        if (mMusicPlayer != null)
        {
            mMusicPlayer.release();
            mMusicPlayer.setListener(null);
            mMusicPlayer.clear();
        }
        if (mBgmUI != null)
        {
            mBgmUI.ClearMemory();
        }

        FileUtils.delete(tempAssetPath);

        mBgmUI = null;
        mMusicPlayer = null;

        if (mToast != null)
        {
            mToast.cancel();
            mToast = null;
        }
    }


    public void updateAdapterInfo(AudioStore.AudioInfo audioInfo, int info_id)
    {
        if (mBgmUI != null)
        {
            mBgmUI.updateAdapterInfo(audioInfo, info_id);
        }
    }

    public void insertBgmLocalInfo(AudioStore.AudioInfo audioInfo)
    {
        if (mBgmUI != null)
        {
            mBgmUI.insertBgmLocalInfo(audioInfo);
        }
    }

    @Override
    public void onClickCenterBtn(MyStatusButton centerView)
    {
        if (mCallback != null)
        {
            boolean fold = isFold();
            if (!fold) //已展开
            {
                TongJi2.AddCountByRes(centerView.getContext(), R.integer.拍照_动态贴纸_录像_预览_收起音乐列表);
                mCallback.onFold(true);//收缩
            }
        }
    }

    @Override
    public void onBgmItemClick(PreviewBgmInfo info)
    {
        if (mCallback != null)
        {
            mCallback.onSelectMusic(info != null && info.getId() == PreviewBgmResMgr2.BGM_INFO_NON_ID);
        }

        if (info == null)
        {
            setMusicPath(null);
            return;
        }

        if (info.getRes() != null && info.getRes() instanceof String)
        {
            if (StickerHelper.isAssetFile(getContext(), (String) info.getRes()))
            {
                //先删除旧包
                //asset拷贝到SD卡
                String path = getAsset2SDPath(info);
                boolean b = FileUtil.assets2SD(getContext(), getAssetPath(info.getResName()),
                        path, true);
                if (b)
                {
                    setMusicPath(path + File.separator + info.getResName());
                }
                else
                {
                    setMusicPath(null);
                }
            }
            else
            {
                setMusicPath((String) info.getRes());
            }
        }
        else
        {
            if (info.getId() == PreviewBgmResMgr2.BGM_INFO_LOCAL_ID)
            {
                if (mCallback != null)
                {
                    mCallback.openLocalMusic();
                }
            }
            else
            {
                //无音乐
                setMusicPath(null);
            }
        }
    }

    @Override
    public void onOpenClipView(PreviewBgmInfo info)
    {
        if (mCallback != null)
        {
            mCallback.openClipView(info);
        }
    }

    private void initToast()
    {
        if (mToast == null)
        {
            mToast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
            TextView tv = new TextView(getContext());

            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            tv.setTextColor(Color.WHITE);
            tv.setText(R.string.bgm_down_load_failed);
            tv.setGravity(Gravity.CENTER);
            tv.setBackgroundResource(R.drawable.gif_edit_toast_bk);
            tv.getBackground().setAlpha((int) (255 * 0.8f));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ShareData.PxToDpi_xhdpi(80));
            tv.setLayoutParams(params);
            mToast.setView(tv);

            mToast.setGravity(Gravity.CENTER, 0, 0);
        }
    }

    @Override
    public void onDownloadFailed()
    {
        initToast();
        mToast.show();
    }

    private String getAssetPath(String resName)
    {
        if (!TextUtils.isEmpty(resName))
        {
            //FIXME 内置bgm asset目录
            return "music" + File.separator + resName;
        }
        return null;
    }

    private String getAsset2SDPath(PreviewBgmInfo info)
    {
        if (info != null)
        {
            File file = new File(tempAssetPath);
            if (!file.exists())
            {
                file.mkdirs();
            }
            return tempAssetPath;
        }
        return null;
    }

    @Override
    public void onCompletion(StickerMediaPlayer stickerMediaPlayer, MediaPlayer mediaPlayer, long completeMillis)
    {

    }

    @Override
    public void onPrepared(StickerMediaPlayer stickerMediaPlayer, MediaPlayer mediaPlayer)
    {
        if (stickerMediaPlayer != null && stickerMediaPlayer.isPrepared() && isPreparedToStart)
        {
            onStart();
        }
    }

    @Override
    public boolean onError(StickerMediaPlayer stickerMediaPlayer, MediaPlayer mp, int what, int extra)
    {
        return false;
    }

    @Override
    public void onSeekComplete(StickerMediaPlayer stickerMediaPlayer, MediaPlayer mediaPlayer)
    {

    }

    @Override
    public void onStart(StickerMediaPlayer stickerMediaPlayer, MediaPlayer mediaPlayer, long startMillis)
    {

    }

    public static interface Callback
    {
        public void onFold(boolean isFold);

        public float getVolume();

        public void onMediaStart();

        public void openLocalMusic();

        public void openClipView(PreviewBgmInfo info);

        public void onSelectMusic(boolean isNonMusic);
    }
}
