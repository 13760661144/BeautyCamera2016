package cn.poco.video;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;

import cn.poco.audio.AudioConfig;
import cn.poco.utils.FileUtil;

import static com.adnonstop.missionhall.utils.gz_Iutil.MissionHallEntryTip.mContext;

/**
 * @author lmx
 *         Created by lmx on 2017/7/26.
 */

public class VideoMixProcessorV2 implements Runnable
{
    private static final String TAG = "bbb";

    public static final int START = 1;
    public static final int FINISH = 1 << 1;
    public static final int ERROR = 1 << 2;

    public static final int ERROR_CODE_NO_VIDEOS = 0x12;
    public static final int ERROR_CODE_VIDEOS_MISSING = 0x14;

    private ProcessInfo mProcessInfo;   //合成参数
    private String mOutputFilePath;     //视频最终合成路径
    private float mOutputVideoDuration; //合成最终视频时长（单位：秒）

    private Handler mMainHandler;
    private OnProcessListener mListener;

    public final float FADEOUTMESC = 1.5f;

    private String mJoinVideoPath;                  //h264混合后的mp4
    private String mJoinAudioPath;                  //分段视频音频拼接

    private boolean isSilenceVideoAudio = false;    //视频音频静音

    private float mFpsA = 0F;                       //视频总和帧率
    private float mFpsE = 0F;                       //视频平均帧率

    private float mVDA = 0F;                        //视频总和时长（单位：秒）
    private float mADA = 0F;                        //音频总和时长（单位：秒）

    /**
     * @param processInfo    不可空
     * @param outputFilePath 不可空
     */
    public VideoMixProcessorV2(@NonNull ProcessInfo processInfo, @NonNull String outputFilePath)
    {
        this.mOutputFilePath = outputFilePath;
        this.mProcessInfo = processInfo;

        mMainHandler = new Handler(Looper.getMainLooper(), new Handler.Callback()
        {
            @Override
            public boolean handleMessage(Message msg)
            {
                switch (msg.what)
                {
                    case START:
                    {
                        if (mListener != null) mListener.onStart();
                    }
                    break;
                    case FINISH:
                    {
                        MixOutInfo info = (MixOutInfo) msg.obj;
                        msg.obj = null;
                        if (mListener != null) mListener.onFinish(info);
                    }
                    break;
                    case ERROR:
                    {
                        if (mListener != null) mListener.onError(msg.arg1);
                    }
                    break;
                }
                return true;
            }
        });
    }

    public void setOnProgressListener(OnProcessListener listener)
    {
        this.mListener = listener;
    }

    public void start()
    {
        new Thread(this).start();
    }

    @Override
    public void run()
    {
        long start = System.currentTimeMillis();
        Looper.prepare();


        if (this.mProcessInfo.m_video_paths == null || this.mProcessInfo.m_video_paths.length == 0)
        {
            //无视频
            clearCache();
            mMainHandler.obtainMessage(ERROR, ERROR_CODE_NO_VIDEOS, 0).sendToTarget();
            return;
        }


        for (String m_video_path : this.mProcessInfo.m_video_paths)
        {
            if (m_video_path == null || !FileUtils.isFileExists(m_video_path))
            {
                //视频丢失
                clearCache();
                mMainHandler.obtainMessage(ERROR, ERROR_CODE_VIDEOS_MISSING, 0).sendToTarget();
                return;
            }
        }

        if (mMainHandler != null)
        {
            mMainHandler.obtainMessage(START, null).sendToTarget();
        }

        //输出视频路径
        mOutputFilePath = checkVideoOutputPath();

        //清除缓存
        FileUtils.clearTempFiles();

        //音频缓存目录
        AudioConfig.setTempFolderPath(cn.poco.video.FileUtils.getTempDir() + File.separator);

        //原视频视频静音输出
        isSilenceVideoAudio = mProcessInfo.is_silence_play;

        int videoNums = this.mProcessInfo.m_video_paths.length;
        //Log.d(TAG, "VideoMixProcessorV2 --> run: 视频个数 " + videoNums);
        String videoRotation = "" + mProcessInfo.m_video_rotation;

        //分段
        ArrayList<BlockInfo> blockInfos = new ArrayList<>();
        int index = 0;
        for (String m_video_path : this.mProcessInfo.m_video_paths)
        {
            //文件不存在跳过
            if (!FileUtils.isFileExists(m_video_path))
            {
                index++;
                continue;
            }

            BlockInfo blockInfo = new BlockInfo();
            blockInfos.add(blockInfo);
            blockInfo.m_video_index = index++;
            blockInfo.m_source_video_path = m_video_path;
            blockInfo.is_slice_video_audio = isSilenceVideoAudio;

            //获取h264数据
            String h264_video_path = newTempH264File(Integer.toString(index));
            int result2 = NativeUtils.getH264FromFile(m_video_path, h264_video_path);
            if (result2 >= 0)
            {
                blockInfo.m_video_temp_h264_path = h264_video_path;
            }

            //抽取音轨
            if (!isSilenceVideoAudio)
            {
                String aac_audio_path = FileUtils.getTempPath(FileUtils.AAC_FORMAT);
                boolean result = VideoUtils.getAacFromVideo(m_video_path, aac_audio_path);
                if (result)
                {
                    blockInfo.m_source_audio_aac_path = aac_audio_path;
                }
            }

            //获取视频帧率
            float fps = VideoUtils.getVideoFrameRate(m_video_path);
            blockInfo.m_video_fps = fps;
            mFpsA += fps;

            //获取视频时长
            float videoDuration = (float) (VideoUtils.getDurationFromVideo(m_video_path) / 1000D);
            blockInfo.m_video_duration = videoDuration;
            mVDA += videoDuration;

            float audioDuration = 0;
            if (blockInfo.m_source_audio_aac_path != null && !blockInfo.is_slice_video_audio)
            {
                //获取音频时长
                audioDuration = (float) (VideoUtils.getDurationFromAudio(blockInfo.m_source_audio_aac_path) / 1000D);
                blockInfo.m_audio_duration = audioDuration;
                mADA += audioDuration;
            }

            //Log.d(TAG, "VideoMixProcessorV2 --> run: fps " + fps + " video_duration " + videoDuration + " audio_duration " + audioDuration);
        }
        mFpsE = mFpsA / blockInfos.size() * 1F;
        //Log.d(TAG, "VideoMixProcessorV2 --> run: mFpsA " + mFpsA + " mFpsE " + mFpsE + " mVDA " + mVDA + " mADA " + mADA);


        //h264拼接视频
        long s = System.currentTimeMillis();
        String tempH264File = newTempH264File("mix");
        for (BlockInfo blockInfo : blockInfos)
        {
            //h264视频文件
            if (blockInfo.m_video_temp_h264_path != null)
            {
                NativeUtils.mixH264(blockInfo.m_video_temp_h264_path, tempH264File);
            }
        }
        //Log.d(TAG, "VideoMixProcessorV2 --> run: h264拼接视频 " + (System.currentTimeMillis() - s) / 1000L);

        //h264混合成mp4视频
        String joinMp4Path = FileUtils.getTempPath(FileUtils.MP4_FORMAT);
//        int muxerMp4 = NativeUtils.muxerMp4(tempH264File, "", joinMp4Path, (int) mFpsE);
        int muxerMp4 = NativeUtils.muxerMp4WithRotation(tempH264File, "", joinMp4Path, (int) mFpsE, videoRotation);
        if (muxerMp4 < 0)
        {
            //Log.d(TAG, "VideoMixProcessorV2 --> run: h264混合成mp4视频 fail");
        }
        mVDA = (float) (VideoUtils.getDurationFromVideo(joinMp4Path) / 1000D);
        mJoinVideoPath = joinMp4Path;
        //Log.d(TAG, "VideoMixProcessorV2 --> run: h264混合成mp4视频 " + mVDA);

        //aac分段音频拼接
        if (!isSilenceVideoAudio)
        {
            s = System.currentTimeMillis();
            //只有一个音频
            if (blockInfos.size() == 1)
            {
                mJoinAudioPath = blockInfos.get(0).m_source_audio_aac_path;
                double durationFromAudio = VideoUtils.getDurationFromAudio(mJoinAudioPath) / 1000D;
                mADA = (float) durationFromAudio;
                //Log.d(TAG, "VideoMixProcessorV2 --> run: 视频音频拼接后总长 " + mADA);
            }
            else
            {
                String audioJoinPath = FileUtils.getTempPath(FileUtils.AAC_FORMAT);
                //boolean result = AudioUtils.jointAuido(audioJoinPath, aacAudioPaths);
                //Log.d(TAG, "VideoMixProcessorV2 --> run: aac分段音频拼接 " + result + " " + (System.currentTimeMillis() - s) / 1000L);

                index = 0;
                float[] times = new float[blockInfos.size() * 2];
                ArrayList<String> aacAudioPaths = new ArrayList<>();
                for (BlockInfo blockInfo : blockInfos)
                {
                    if (blockInfo.m_source_audio_aac_path != null && blockInfo.m_audio_duration > 0)
                    {
                        aacAudioPaths.add(blockInfo.m_source_audio_aac_path);
                        times[index * 2] = 0;
                        times[index * 2 + 1] = blockInfo.m_audio_duration;
                    }
                    index++;
                }

                boolean result = VideoUtils.jointAacAudio(audioJoinPath, aacAudioPaths, times);

                if (result && FileUtils.isFileExists(audioJoinPath))
                {
                    mJoinAudioPath = audioJoinPath;
                    double durationFromAudio = VideoUtils.getDurationFromAudio(audioJoinPath) / 1000D;
                    mADA = (float) durationFromAudio;
                    //Log.d(TAG, "VideoMixProcessorV2 --> run: 视频音频拼接后总长 " + mADA);
                }
                else
                {
                    //Log.d(TAG, "VideoMixProcessorV2 --> run: aac分段音频拼接 fail");
                }
            }
        }

        //混音
        //Log.d(TAG, "VideoMixProcessorV2 --> run: 开始混音");
        s = System.currentTimeMillis();
        String handleMixMusic = handleMixMusic();
        if (!FileUtils.isFileExists(handleMixMusic))
        {
            //Log.d(TAG, "VideoMixProcessorV2 --> run: handleMixMusic return null");
        }
        //Log.d(TAG, "VideoMixProcessorV2 --> run: 结束混音 " + Long.toString((System.currentTimeMillis() - s) / 1000L));

        //混合
        try
        {
            //Log.d(TAG, "VideoMixProcessorV2 --> run: 开始混合");
            s = System.currentTimeMillis();
            boolean result = VideoUtils.muxerAudioVideo(mJoinVideoPath, handleMixMusic, mOutputFilePath, videoRotation);
            if (!result)
            {
                //Log.d(TAG, "VideoMixProcessorV2 --> run: 混合 失败");
                mOutputFilePath = mJoinVideoPath;
            }
        }
        catch (Throwable e)
        {
            mOutputFilePath = mJoinVideoPath;
            e.printStackTrace();
        }
        //Log.d(TAG, "VideoMixProcessorV2 --> run: 结束混合 " + Long.toString((System.currentTimeMillis() - s) / 1000L));

        //清理缓存
        clearCache();

        //Log.d(TAG, "VideoMixProcessorV2 --> run: 混合工程结束：" + Long.toString((System.currentTimeMillis() - start) / 1000L));

        if (FileUtils.isFileExists(mOutputFilePath))
        {
            try
            {
                mOutputVideoDuration = NativeUtils.getDurationFromFile(mOutputFilePath);
            }
            catch (Throwable t)
            {
                mOutputVideoDuration = mVDA;
            }
        }

        MixOutInfo outInfo = new MixOutInfo();
        outInfo.mPath = mOutputFilePath;
        outInfo.mDuration = mOutputVideoDuration * 1000F;

        if (mMainHandler != null)
        {
            mMainHandler.obtainMessage(FINISH, outInfo).sendToTarget();
        }
    }


    private String handleMixMusic()
    {
        //如果是asset文件，先拷贝到指定目录
        if (FileUtils.isAssetFile(mProcessInfo.m_bg_music_path))
        {
            String[] split = mProcessInfo.m_bg_music_path.split("file:///android_asset/");
            if (split.length == 2)
            {
                String dstPath = newTempAudioFile(split[1]);
                boolean b = FileUtil.assets2SD(mContext, split[1],
                        dstPath, true);
                if (b)
                {
                    mProcessInfo.m_bg_music_path = dstPath;
                }
            }
        }

        //关闭了视频声音
        if (this.isSilenceVideoAudio)
        {
            if (!FileUtils.isFileExists(mProcessInfo.m_bg_music_path))
            {
                //没背景音乐，返回空
                return null;
            }
            else
            {
                //裁剪背景音乐
                String clipBgMusic = clipBgMusic(false);                //裁剪后音乐
                String expandAudio = expandAudio(clipBgMusic, mVDA);    //延长空白音频
                //降音淡出
                expandAudio = volumeAdjust(expandAudio, (float) mProcessInfo.m_bg_volume_adjust);
                return audioFade(expandAudio);
            }
        }
        else
        {
            if (!FileUtils.isFileExists(mProcessInfo.m_bg_music_path))
            {
                //没背景音乐，处理视频音频
                mJoinAudioPath = expandAudio(mJoinAudioPath, mVDA);     //延长空白音频
                mJoinAudioPath = volumeAdjust(mJoinAudioPath, (float) mProcessInfo.m_video_volume_adjust);
                return mJoinAudioPath;
            }
            else
            {
                if (FileUtils.isFileExists(mJoinAudioPath))
                {
                    //混合
                    mJoinAudioPath = expandAudio(mJoinAudioPath, mVDA);     //延长空白音频
                    mJoinAudioPath = volumeAdjust(mJoinAudioPath, (float) mProcessInfo.m_video_volume_adjust);
                    String clipBgMusic = clipBgMusic(true);                 //裁剪后音乐
                    clipBgMusic = volumeAdjust(clipBgMusic, (float) mProcessInfo.m_bg_volume_adjust);
                    String mixMusic = mixMusic(mJoinAudioPath, clipBgMusic);//混合音频
                    return audioFade(mixMusic);                           //淡出
                }
                else
                {
                    //裁剪背景音乐
                    String clipBgMusic = clipBgMusic(false);                //裁剪后音乐
                    mVDA = (float) (VideoUtils.getDurationFromAudio(clipBgMusic) / 1000d);
                    String expandAudio = expandAudio(clipBgMusic, mVDA);    //延长空白音频
                    //降音淡出
                    expandAudio = volumeAdjust(expandAudio, (float) mProcessInfo.m_bg_volume_adjust);
                    return audioFade(expandAudio);
                }
            }
        }
    }

    /**
     * 裁剪背景音乐（时长以视频时长为基准）
     *
     * @param isExpand 如果裁剪失败，是否延长空白音频
     * @return
     */
    private String clipBgMusic(boolean isExpand)
    {
        float joinVideoDuration = mVDA;//拼接后视频时长

        //裁剪音频，可能会重复延长（根据视频时长）
        String tempBgAudioPath = newTempAudioFile(mProcessInfo.m_bg_music_path);
        double bgStart = mProcessInfo.m_bg_music_start / 1000D;
        if (!VideoUtils.clipAudioExpand(
                mProcessInfo.m_bg_music_path, tempBgAudioPath, true,
                bgStart, (bgStart + joinVideoDuration)))
        {
            //Log.d(TAG, "VideoMixProcessorV2 --> clipMusic: 裁剪音频，可能会重复延长（根据视频时长） 失败");
            //裁剪失败，恢复为未裁剪前的音频

            if (isExpand)
            {
                tempBgAudioPath = expandAudio(tempBgAudioPath, mVDA);
            }
            else
            {
                tempBgAudioPath = mProcessInfo.m_bg_music_path;
            }
        }
        return tempBgAudioPath;
    }


    /**
     * 延长空白音频
     *
     * @param inputPath     音频
     * @param videoDuration 视频时长（单位秒）
     */
    private String expandAudio(String inputPath, float videoDuration)
    {
        if (FileUtils.isFileExists(inputPath) && videoDuration > 1D)
        {
            long start = System.currentTimeMillis();
            float audioDuration = (float) (VideoUtils.getDurationFromAudio(inputPath) / 1000D);
            mADA = audioDuration;
            if (audioDuration < videoDuration)
            {
                String tempAudioPath = newTempAudioFile(inputPath);
                //Log.d(TAG, "VideoMixProcessorV2 --> expandAudio: 延长空白的音频 视频:" + videoDuration + ", 音频：" + audioDuration);
                if (VideoUtils.expandAudioDuration(inputPath, tempAudioPath, videoDuration + 0.1D, 0, audioDuration))
                {
                    mADA = (float) (VideoUtils.getDurationFromAudio(tempAudioPath) / 1000D);
                    //Log.d(TAG, "VideoMixProcessorV2 --> expandAudio: 延长空白的音频 " + (System.currentTimeMillis() - start) / 1000L);
                    return tempAudioPath;
                }
                else
                {
                    //Log.d(TAG, "VideoMixProcessorV2 --> expandAudio: 延长空白音频 失败 " + (System.currentTimeMillis() - start) / 1000L);
                }
            }
        }
        return inputPath;
    }


    /**
     * 降低音量（背景音乐） 淡入淡出处理
     *
     * @param inputPath
     * @return
     */
    private String audioFade(String inputPath)
    {
        if (FileUtils.isFileExists(inputPath))
        {
            long s = System.currentTimeMillis();
            if (mADA - FADEOUTMESC >= FADEOUTMESC)
            {
                double fadeOutMesc = calculateFadeoutMesc(mADA);
                String tempAudioPath2 = newTempAudioFile(inputPath);
                if (!VideoUtils.audioFade(inputPath, tempAudioPath2, (int) fadeOutMesc, (int) fadeOutMesc))
                {
                    //Log.d(TAG, "VideoMixProcessorV2 --> audioAdjust: 淡出处理 失败");
                    tempAudioPath2 = inputPath;
                }
                //Log.d(TAG, "VideoMixProcessorV2 --> audioAdjust: 淡出处理 " + (System.currentTimeMillis() - s) / 1000L);
                return tempAudioPath2;
            }
            else
            {
                //Log.d(TAG, "VideoMixProcessorV2 --> audioAdjust: 音频较短不适合淡出处理 ");
                return inputPath;
            }
        }
        return inputPath;
    }


    private String volumeAdjust(String inputPath, @FloatRange(from = 0.0f, to = 1.0f) float value)
    {
        if (!TextUtils.isEmpty(inputPath))
        {
            String tempAudioPath = newTempAudioFile(inputPath);
            if (!VideoUtils.volumeAdjust(inputPath, tempAudioPath, value))
            {
                //Log.d(TAG, "VideoMixProcessorV2 --> audioAdjust: 降低背景音乐音量 失败");
                tempAudioPath = inputPath;
            }
            return tempAudioPath;
        }
        return inputPath;
    }

    /**
     * 混合视频音频 和 背景音乐（音频时长必须大小一致）
     *
     * @param inputPath 主音频
     * @param bgPath    背景音频
     * @return null失败
     */
    private String mixMusic(String inputPath, String bgPath)
    {
        if (FileUtils.isFileExists(inputPath) && FileUtils.isFileExists(bgPath))
        {
            //混合视频音频和背景音频（音频时必须长大小一致）
            String mixOutPath = FileUtils.getTempPath(FileUtils.AAC_FORMAT);
            if (VideoUtils.mixBgAudioNew2(inputPath, 1, bgPath, 1, false, mixOutPath, 0, 1))
            {
                //Log.d(TAG, "VideoMixProcessorV2 --> mixMusic: 混合视频音频和背景音频（音频时长大小一致）" + (VideoUtils.getDurationFromAudio(mixOutPath) / 1000L));
                return mixOutPath;
            }
            else
            {
                //Log.d(TAG, "VideoMixProcessorV2 --> mixMusic: 混合视频音频和背景音频（音频时长大小一致）失败");
                FileUtils.delete(mixOutPath);
            }
        }
        return inputPath;
    }

    /**
     * 计算淡出时间时长
     *
     * @return 淡出时长（单位：秒）
     */
    public double calculateFadeoutMesc(float audioDuration)
    {
        double fadeOutMesc = FADEOUTMESC;//淡出时间（秒）
        if (audioDuration > fadeOutMesc)
        {
            if ((audioDuration % fadeOutMesc) > 0.2D)
            {
                double floor = Math.floor(audioDuration) - fadeOutMesc;
                fadeOutMesc = Math.round(audioDuration - floor);
            }
        }
        //Log.d(TAG, "VideoMixProcessorV2 --> calculateFadeoutMesc: 淡出: " + fadeOutMesc);
        return fadeOutMesc;
    }

    //清除缓存目录
    private void clearCache()
    {
        if (mProcessInfo.is_clear_temp_cache)
        {
            FileUtils.clearTempFiles();
        }
    }

    public void release()
    {
        if (mMainHandler != null)
        {
            mMainHandler.removeMessages(START);
            mMainHandler.removeMessages(ERROR);
            mMainHandler.removeMessages(FINISH);
        }

        mProcessInfo = null;
        mListener = null;
        mMainHandler = null;
    }

    public String checkVideoOutputPath()
    {
        if (TextUtils.isEmpty(mOutputFilePath))
        {
            mOutputFilePath = FileUtils.getVideoOutputSysPath();
        }
        return mOutputFilePath;
    }

    public String getOutputFilePath()
    {
        return mOutputFilePath;
    }

    /**
     * 根据后缀创建临时音频文件（AAC WAV MP3）
     *
     * @param file
     * @return
     */
    private String newTempAudioFile(String file)
    {
        return FileUtils.newTempAudioFile(file);
    }

    /**
     * 创建临时H264文件（.h264）
     *
     * @return
     */
    private String newTempH264File()
    {
        return newTempH264File(null);
    }

    private String newTempH264File(String suffix)
    {
        return FileUtils.getTempPath(FileUtils.H264_FORMAT, suffix);
    }

    public static class MixOutInfo
    {
        public String mPath;       //混合后输出路径
        public float mDuration;    //混合后时长（单位毫秒）
    }

    public static class BlockInfo
    {
        String m_source_video_path;      //原视频
        String m_source_audio_aac_path;  //原视频音轨（.aac）

        String m_video_temp_h264_path;   //缓存视频（.h264）

        float m_video_duration;          //视频时长（秒）
        float m_audio_duration;          //音频时长（秒）

        float m_video_fps;               //视频帧率（秒）

        boolean is_slice_video_audio;    //视频静音

        int m_video_index;               //视频下标
    }

    public interface OnProcessListener
    {
        public void onStart();

        public void onProgress(long timeStamp);

        public void onFinish(MixOutInfo outInfo);

        /**
         * @param what See {@link VideoMixProcessorV2#ERROR_CODE_NO_VIDEOS},{@link VideoMixProcessorV2#ERROR_CODE_VIDEOS_MISSING}
         */
        public void onError(int what);
    }
}
