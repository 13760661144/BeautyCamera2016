package cn.poco.video;

import android.annotation.TargetApi;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.poco.audio.AacEnDecoder;
import cn.poco.audio.AudioUtils;
import cn.poco.audio.CommonUtils;
import cn.poco.audio.GenerateMuteAudio;
import cn.poco.audio.MP3DeEncode;
import cn.poco.audio.PcmAfade;
import cn.poco.audio.PcmMix;
import cn.poco.audio.PcmWav;
import cn.poco.audio.Resample;
import cn.poco.audio.SoundJoint;
import cn.poco.audio.soundclip.WavClip;
import cn.poco.video.decoder.AudioDecoderCore;

import static cn.poco.video.NativeUtils.mixVideoSegment;


/**
 * 音视频混合utils（部分调用{@link cn.poco.audio.AudioUtils} 和 {@link cn.poco.audio.CommonUtils}工程）
 *
 * @author lmx
 *         Created by lmx on 2017/7/26.
 */

public class VideoUtils
{
    private static final String TAG = "bbb";

    /**
     * 视频声音和音频混合，输出音轨文件
     *
     * @param videoPath       视频路径
     * @param volume          视频音量，音量范围[0,1], 1是原始音量大小，小于1则使音量变小，0为无声
     * @param bgMusicPath     背景音频路径
     * @param volume          背景音频音量，音量范围[0,1], 1是原始音量大小，小于1则使音量变小，0为无声
     * @param isRepeat        背景音乐是否重复播放
     * @param outputPath      音频输出路径
     * @param bgMusicStartEnd 背景音频列表对应的起始、终止位置在主文件的百分比，范围：0~1 (size >= 2)
     * @return
     */
    public static boolean mixBgAudioNew(String videoPath, double volume, String bgMusicPath, double bgvolume, boolean isRepeat, String outputPath, double... bgMusicStartEnd)
    {
        if (!FileUtils.isFileExists(videoPath) || outputPath == null) return false;

        if (bgMusicStartEnd == null || bgMusicStartEnd.length < 2) return false;

        //抽取视频文件的音频流转成aac音轨
        String videoAacTempPath = FileUtils.getTempPath(FileUtils.AAC_FORMAT);
        boolean result = getAacFromVideo(videoPath, videoAacTempPath);
        File file = new File(videoAacTempPath);
        if (!result || !file.exists() || file.length() == 0)
        {
            //抽取音频流失败
            FileUtils.delete(videoAacTempPath);
            return false;
        }

        //Log.d(TAG, "VideoMixProcessor --> handleMixMusic: 原视频音频长度："
        //        + (VideoUtils.getDurationFromAudio(videoAacTempPath) / 1000D)
        //        + " 背景音频长度：" + (VideoUtils.getDurationFromAudio(bgMusicPath) / 1000D));

        //背景音乐文件路径
        ArrayList<String> bgMusicPathList = new ArrayList<>();
        bgMusicPathList.add(bgMusicPath);

        //背景音乐音量
        ArrayList<Double> bgMusicVolumeList = new ArrayList<>();
        bgMusicVolumeList.add(bgvolume);


//        ArrayList<double[]> bgmse = new ArrayList<>();
//        double[] values = new double[bgMusicStartEnd.length];
//        for (int i = 0; i < bgMusicStartEnd.length; i++)
//        {
//            values[i] = bgMusicStartEnd[i];
//        }
//        bgmse.add(values);
//        result = AudioUtils.mixAudio(videoAacTempPath, volume, outputPath, bgMusicPathList, bgmse, bgMusicVolumeList);
        result = mixAudioNew(videoAacTempPath, volume, outputPath, bgMusicPathList, bgMusicVolumeList, isRepeat, bgMusicStartEnd);
        if (!result)
        {
            return false;
        }

        return true;
    }


    /**
     * 视频声音和音频混合，输出音轨文件（两条音频时长大小一致）
     *
     * @param inputPath       主音频（视频音频路径）
     * @param volume          视频音量，音量范围[0,1], 1是原始音量大小，小于1则使音量变小，0为无声
     * @param bgMusicPath     背景音频路径
     * @param volume          背景音频音量，音量范围[0,1], 1是原始音量大小，小于1则使音量变小，0为无声
     * @param isRepeat        背景音乐是否重复播放
     * @param outputPath      音频输出路径
     * @param bgMusicStartEnd 背景音频列表对应的起始、终止位置在主文件的百分比，范围：0~1 (size >= 2)
     * @return
     */
    public static boolean mixBgAudioNew2(String inputPath, double volume, String bgMusicPath, double bgvolume, boolean isRepeat, String outputPath, double... bgMusicStartEnd)
    {
        if (outputPath == null) return false;

        if (bgMusicStartEnd == null || bgMusicStartEnd.length < 2) return false;

        //抽取视频文件的音频流转成aac音轨

        /*Log.d(TAG, "VideoUtils --> mixBgAudioNew2: 原视频音频长度："
                + (VideoUtils.getDurationFromAudio(inputPath) / 1000D)
                + " 背景音频长度：" + (VideoUtils.getDurationFromAudio(bgMusicPath) / 1000D));*/

        //背景音乐文件路径
        ArrayList<String> bgMusicPathList = new ArrayList<>();
        bgMusicPathList.add(bgMusicPath);

        //背景音乐音量
        ArrayList<Double> bgMusicVolumeList = new ArrayList<>();
        bgMusicVolumeList.add(bgvolume);

        return mixAudioNew(inputPath, volume, outputPath, bgMusicPathList, bgMusicVolumeList, isRepeat, bgMusicStartEnd);
    }


    /**
     * 音频文件混音（格式WAV MP3 AAC）（音量从[0,1], 1是原始音量大小，小于1则使音量变小,0为无声）
     *
     * @param inputFilePath   主音频文件路径
     * @param volume          主音频的音量
     * @param outputFilePath  输出音频文件路径
     * @param bgMusicPathList 要在主文件上混入声音的音频文件列表
     * @param bgVolumeList    bgmMusicPathList的音量
     * @param isRepeat        背景音乐是否重复播放
     * @param bgMusicStartEnd bgMusicPathList的基于主音频文件混入起始位置列表
     * @return
     */
    public static boolean mixAudioNew(String inputFilePath, double volume, String outputFilePath, ArrayList<String> bgMusicPathList, ArrayList<Double> bgVolumeList, boolean isRepeat, double... bgMusicStartEnd)
    {
        if (bgMusicPathList == null || bgMusicPathList.size() == 0 || bgMusicStartEnd == null || bgMusicPathList.size() * 2 != bgMusicStartEnd.length)
        {
            return false;
        }

        int commonSampleRate;
        int commonChannels;

        //获取主音频采样率和通道
        int mainFileSampleRate = CommonUtils.getAudioSampleRate(inputFilePath);
        int mianFileChannels = CommonUtils.getAudioChannels(inputFilePath);

        if (mainFileSampleRate > 0)
        {
            commonSampleRate = mainFileSampleRate;
        }
        else
        {
            return false;
        }

        if (mianFileChannels > 0)
        {
            commonChannels = mianFileChannels;
        }
        else
        {
            return false;
        }

        //解码主音频文件
        String inputFilePcm = FileUtils.getTempPath(FileUtils.PCM_FORMAT);
        if (!CommonUtils.getAudioPcm(inputFilePath, inputFilePcm))
        {
            return false;
        }

        List<String> bgListPcm = new ArrayList<String>();
        List<Integer> bgSampleRateList = new ArrayList<Integer>();
        List<Integer> bgChannelsList = new ArrayList<Integer>();

        for (int i = 0; i < bgMusicPathList.size(); i++)
        {
            int tempSR = CommonUtils.getAudioSampleRate(bgMusicPathList.get(i));
            int tempC = CommonUtils.getAudioChannels(bgMusicPathList.get(i));
            bgSampleRateList.add(tempSR);
            bgChannelsList.add(tempC);

            if (tempSR < 1)
            {
                return false;
            }

            if (tempC < 1)
            {
                return false;
            }

            String tempPcm = FileUtils.getTempPath(FileUtils.PCM_FORMAT);
            boolean isOk = CommonUtils.getAudioPcm(bgMusicPathList.get(i), tempPcm);
            if (!isOk)
            {
                return false;
            }

            // do resample
            if (tempSR == commonSampleRate)
            {   // same sample rate
                if (tempC == commonChannels)
                {
                    bgListPcm.add(tempPcm);
                }
                else
                {
                    String temp = FileUtils.getTempPath(FileUtils.PCM_FORMAT);
                    Resample.doReChannels(tempPcm, temp, tempC, commonChannels);
                    bgListPcm.add(temp);
                }
            }
            else
            {   //diff sample rate
                if (tempC == commonChannels)
                {
                    String temp = FileUtils.getTempPath(FileUtils.PCM_FORMAT);
                    Resample.doResample(tempPcm, temp, tempSR, commonSampleRate);
                    bgListPcm.add(temp);
                }
                else
                {   // diff sample rate  and channels
                    String temps = FileUtils.getTempPath(FileUtils.PCM_FORMAT);
                    Resample.doResample(tempPcm, temps, tempSR, commonSampleRate);
                    String tempc = FileUtils.getTempPath(FileUtils.PCM_FORMAT);
                    Resample.doReChannels(temps, tempc, tempC, commonChannels);
                    bgListPcm.add(tempc);
                }
            }
        }
        String outPcm = FileUtils.getTempPath(FileUtils.PCM_FORMAT);
        boolean misRet = mixAudioPcm(inputFilePcm, volume, outPcm, bgListPcm, bgVolumeList, isRepeat, bgMusicStartEnd);
        if (!misRet)
        {
            return false;
        }

        return CommonUtils.encodeAudio(outPcm, outputFilePath, commonSampleRate, commonChannels);
    }

    /**
     * 混合音频，支持调节音量比重（音量从[0,1], 1是原始音量大小，小于1则使音量变小，0为无声）
     *
     * @param inputFilePath   音频输入路径（PCM）
     * @param volume          主音频的音量
     * @param outputFilePath  音频输出路径（PCM）
     * @param bgMusicPathList 背景音乐路径列表（PCM）
     * @param bgVolumeList    背景音乐的音量
     * @param isRepeat        背景音乐是否重复播放
     * @param bgMusicStartEnd 背景音频列表对应的起始、终止位置在主文件的百分百，如0.234.
     * @return 是否成功
     */
    private static boolean mixAudioPcm(String inputFilePath,
                                       double volume,
                                       String outputFilePath,
                                       List<String> bgMusicPathList,
                                       List<Double> bgVolumeList,
                                       boolean isRepeat,
                                       double... bgMusicStartEnd)
    {
        if (bgMusicPathList == null || bgMusicPathList.size() == 0
                || bgMusicStartEnd == null || bgMusicStartEnd.length == 0
                || bgMusicPathList.size() * 2 != bgMusicStartEnd.length
                || bgMusicPathList.size() != bgVolumeList.size())
        {
            return false;
        }

        String tempMixPcm;

        List<String> tempMixPcmList = new ArrayList<>();
        double start, end;

        if (bgMusicPathList.size() == 1)
        {
            int result;
            tempMixPcm = outputFilePath;
            start = bgMusicStartEnd[0];
            end = bgMusicStartEnd[1];
            if (isRepeat)
            {
                result = PcmMix.mixPcmVloAdjustRepeat(inputFilePath, bgMusicPathList.get(0), tempMixPcm, start, end, volume, bgVolumeList.get(0));
            }
            else
            {
                result = PcmMix.mixPcmVloAdjust(inputFilePath, bgMusicPathList.get(0), tempMixPcm, start, end, volume, bgVolumeList.get(0));
            }
            if (result < 0)
            {
                FileUtils.delete(tempMixPcm);
                return false;
            }
        }
        else
        {
            for (int i = 0; i < bgMusicPathList.size(); i++)
            {
                tempMixPcm = FileUtils.getTempPath(FileUtils.PCM_FORMAT);
                int reslut;

                start = bgMusicStartEnd[i * 2];
                end = bgMusicStartEnd[i * 2 + 1];

                if (i == 0)
                {
                    tempMixPcmList.add(tempMixPcm);
                    if (isRepeat)
                    {
                        reslut = PcmMix.mixPcmVloAdjustRepeat(inputFilePath, bgMusicPathList.get(0), tempMixPcm, start, end, volume, bgVolumeList.get(0));
                    }
                    else
                    {
                        reslut = PcmMix.mixPcmVloAdjust(inputFilePath, bgMusicPathList.get(0), tempMixPcm, start, end, volume, bgVolumeList.get(0));
                    }
                    if (reslut < 0)
                    {
                        return false;
                    }
                }
                else
                {
                    if (i == bgMusicPathList.size() - 1)
                    {
                        tempMixPcm = outputFilePath;
                    }
                    else
                    {
                        tempMixPcmList.add(tempMixPcm);
                    }
                    if (isRepeat)
                    {
                        reslut = PcmMix.mixPcmVloAdjustRepeat(tempMixPcmList.get(i - 1), bgMusicPathList.get(i), tempMixPcm, start, end, 1, bgVolumeList.get(i));
                    }
                    else
                    {
                        reslut = PcmMix.mixPcmVloAdjust(tempMixPcmList.get(i - 1), bgMusicPathList.get(i), tempMixPcm, start, end, 1, bgVolumeList.get(i));
                    }
                    if (reslut < 0)
                    {
                        FileUtils.delete(tempMixPcmList.get(i - 1));
                        return false;
                    }
                }
            }

            for (String s : tempMixPcmList)
            {
                FileUtils.delete(s);
            }
        }
        return true;
    }


    /**
     * 调用底层混合音视频
     *
     * @param videoPath  原视频
     * @param aacPath    aac文件路径 如果aac为空，则混合的视频无声
     * @param outputPath 视频输出路径
     * @param rotation   视频旋转角度
     * @return 是否成功
     */
    public static boolean muxerAudioVideo(String videoPath, String aacPath, String outputPath, String rotation)
    {
        if (!FileUtils.isFileExists(videoPath) || outputPath == null)
        {
            //Log.d(TAG, "VideoUtils --> replaceAudio: the video path is not correct.");
            return false;
        }

        String aacTempPath = FileUtils.getTempPath(FileUtils.AAC_FORMAT);

        if (TextUtils.isEmpty(aacPath))
        {
            // 去掉音频信息
            aacTempPath = "";
        }
        else
        {
            //音频转换aac
            if (!changeToAac(aacPath, aacTempPath))
            {
                FileUtils.delete(aacTempPath);
                aacTempPath = "";
            }
        }

        int result = NativeUtils.muxerMp4WithRotation(videoPath, aacTempPath, outputPath, 0, rotation);
        if (result < 0)
        {
            FileUtils.delete(outputPath);
            return false;
        }

        if (!TextUtils.isEmpty(aacTempPath))
        {
            FileUtils.delete(aacTempPath);
        }
        return true;
    }


    /**
     * 将音频文件转换为aac格式
     *
     * @param audioPath  音频文件路径（WAV MP3 AAC）
     * @param outputPath aac文件输出路径
     * @return 是否成功
     */
    public static boolean changeToAac(String audioPath, String outputPath)
    {
        if (!FileUtils.isFileExists(audioPath) || outputPath == null)
        {
            //Log.d(TAG, "VideoUtils --> changeToAac: the audio path is not correct.");
            return false;
        }

        if (audioPath.endsWith(FileUtils.AAC_FORMAT))
        {
            FileUtils.copyFile(audioPath, outputPath);
            return true;
        }

        String pcmTempPath = FileUtils.getTempPath(FileUtils.PCM_FORMAT);
        int[] params = new int[2];
        boolean result = audioDecode2(audioPath, pcmTempPath, params);
        if (result)
        {
            result = AacEnDecoder.encodeAAC(params[0], params[1], 16, pcmTempPath, outputPath) >= 0;

            if (!result)
            {
                FileUtils.delete(outputPath);
            }
        }

        FileUtils.delete(pcmTempPath);

        return result;
    }


    /**
     * 裁剪音频
     *
     * @param audioInputPath  音频流路径
     * @param audioOutputPath 裁剪后输出路径
     * @param start           裁剪开始时间，单位s
     * @param end             裁剪结束时间，单位s
     */
    public static boolean clipAudio(String audioInputPath, String audioOutputPath, double start, double end)
    {
        if (!FileUtils.isFileExists(audioInputPath) || audioOutputPath == null)
        {
            //Log.d(TAG, "VideoUtils --> clipAudio: the video path is not correct.");
        }

        //时间戳不对
        if (start < 0 || end < 0 || start >= end)
        {
            //Log.d(TAG, "VideoUtils --> clipAudio: the time is not correct.");
            return false;
        }

        ArrayList<String> outPath = new ArrayList<>();
        outPath.add(audioOutputPath);

        ArrayList<double[]> timeS = new ArrayList<>();
        double[] values = {start, end};
        timeS.add(values);

        return clipAudio(audioInputPath, outPath, timeS);
    }

    /**
     * 裁剪音频 （支持WAV AAC MP3任意输入输出格式）
     *
     * @param inputFilePath
     * @param outputFileList
     * @param timestamps     裁剪时间起始点[起点][终点] 单位:秒
     * @return
     */
    public static boolean clipAudio(String inputFilePath, List<String> outputFileList, List<double[]> timestamps)
    {
        boolean result;
        if (outputFileList.size() != timestamps.size())
        {
            return false;
        }

        int blockNum = outputFileList.size();

        String inputWavFilePath = FileUtils.getTempPath(FileUtils.WAV_FORMAT);

        result = CommonUtils.audioToWav(inputFilePath, inputWavFilePath);
        if (!result)
        {
            FileUtils.delete(inputWavFilePath);
            return false;
        }

        List<String> tempWavFileList = new ArrayList<>();
        for (int i = 0; i < blockNum; i++)
        {
            tempWavFileList.add(FileUtils.getTempPath(FileUtils.WAV_FORMAT));
        }

        result = WavClip.clip(inputWavFilePath, tempWavFileList, timestamps);
        if (!result)
        {
            FileUtils.delete(inputWavFilePath);
            for (String s : tempWavFileList)
            {
                FileUtils.delete(s);
            }

            return false;
        }
        for (int i = 0; i < blockNum; i++)
        {
            result = CommonUtils.wavToAudio(tempWavFileList.get(i), outputFileList.get(i));
            if (!result)
            {
                FileUtils.delete(tempWavFileList.get(i));
                return false;
            }
        }
        return true;
    }


    /**
     * 裁剪音频，基于底层
     *
     * @param audioInputPath  音频流路径
     * @param audioOutputPath 裁剪后输出路径
     * @param isRepeat        是否重复，主要音频时长小于总时长
     * @param start           裁剪开始时间，单位s
     * @param end             裁剪结束时间，单位s
     * @return
     */
    public static boolean clipAudioExpand(String audioInputPath, String audioOutputPath, boolean isRepeat, double start, double end)
    {
        if (!FileUtils.isFileExists(audioInputPath) || audioOutputPath == null)
        {
            //Log.d(TAG, "VideoUtils --> clipAudio: the video path is not correct.");
        }

        //时间戳不对
        if (start < 0 || end < 0 || start >= end)
        {
            //Log.d(TAG, "VideoUtils --> clipAudio: the time is not correct.");
            return false;
        }

        //Log.d(TAG, "VideoUtils --> clipAudioExpand: start:" + start + " end:" + end);

        if (!isRepeat)
        {
            return clipAudio(audioInputPath, audioOutputPath, start, end);
        }


        //秒
        double durationFromAudio = getDurationFromAudio(audioInputPath) / 1000D;
        if (end - start <= durationFromAudio)
        {
            return clipAudio(audioInputPath, audioOutputPath, start, end);
        }
        else
        {
            double allDuration = end - start;
            return clipAudioRepeat(audioInputPath, audioOutputPath, durationFromAudio, allDuration);
        }
    }


    /**
     * 裁剪音频，基于FFmpeg FIXME bug
     *
     * @param audioInputPath  音频流路径
     * @param audioOutputPath 裁剪后输出路径
     * @param start           裁剪开始时间，单位s
     * @param end             裁剪结束时间，单位s
     * @return
     */
    public static boolean clipAudioExpand2(String audioInputPath, String audioOutputPath, double start, double end)
    {
        if (!FileUtils.isFileExists(audioInputPath) || audioOutputPath == null)
        {
            //Log.d(TAG, "VideoUtils --> clipAudio: the video path is not correct.");
        }

        //时间戳不对
        if (start < 0 || end < 0 || start >= end)
        {
            //Log.d(TAG, "VideoUtils --> clipAudio: the time is not correct.");
            return false;
        }

        //Log.d(TAG, "VideoUtils --> clipAudioExpand: start:" + start + " end:" + end);

        //秒
        double durationFromAudio = getDurationFromAudio(audioInputPath) / 1000D;
        if (end - start <= durationFromAudio)
        {
            if (end <= durationFromAudio)
            {
                float result = mixVideoSegment(audioInputPath, audioOutputPath, (float) start, (float) end);
                NativeUtils.endMixing();
                return result >= 0;
            }
            else
            {
                String tempAudio = FileUtils.newTempAudioFile(audioInputPath);
                float result = mixVideoSegment(audioInputPath, tempAudio, (float) start, (float) durationFromAudio);
                NativeUtils.endMixing();
                if (result >= 0)
                {
                    double offsetDuration = end - durationFromAudio;
                    boolean b = AudioUtils.expandAudioDuration(tempAudio, audioOutputPath, (end - start), 0, offsetDuration);
                    FileUtils.delete(tempAudio);
                    return b;
                }
                else
                {
                    return false;
                }
            }
        }
        else
        {
            double allDuration = end - start;
            double excessDuration = allDuration % durationFromAudio;

            boolean needExcess = false;
            //不是整数倍
            if (excessDuration > 0)
            {
                needExcess = true;
            }

            int block;
            if (excessDuration == 0)
            {
                block = (int) (allDuration / durationFromAudio);
            }
            else
            {
                block = (int) (allDuration / durationFromAudio) + 1;
            }
            //Log.d(TAG, "VideoUtils --> clipAudioExpand2: block " + block + ", excessDuration " + excessDuration + ", start " + start + ", end " + end);


            for (int i = 0; i < block; i++)
            {
                float result = NativeUtils.mixVideoSegment(audioInputPath, audioOutputPath, (float) start, (float) end);
                if (result < 0)
                {
                    NativeUtils.endMixing();
                    return false;
                }

                if (needExcess && i == block - 1)
                {
                    result = NativeUtils.mixVideoSegment(audioInputPath, audioOutputPath, (float) start, (float) (start + excessDuration));
                    if (result < 0)
                    {
                        NativeUtils.endMixing();
                        return false;
                    }
                }
            }
            NativeUtils.endMixing();
            return true;
        }
    }

    /**
     * 延长重复音乐 （WAV MP3 AAC 任意格式输入输出）
     *
     * @param audioInputPath  音频流路径  WAV MP3 AAC格式
     * @param audioOutputPath 裁剪后输出路径
     * @param audioDuration   输入音频的时长 （单位 秒）
     * @param allDuration     延长后总时长 （单位 秒）
     * @return
     */
    public static boolean clipAudioRepeat(String audioInputPath, String audioOutputPath, double audioDuration, double allDuration)
    {
        if (allDuration <= audioDuration)
        {
            //Log.d(TAG, "VideoUtils --> clipAudioRepeat: output duraion < file dration");
            return false;
        }

        boolean result;
        int ret;

        double excessDuration = allDuration % audioDuration;

        String rawAudioWav = FileUtils.getTempPath(FileUtils.WAV_FORMAT);
        String endBlockWav = FileUtils.getTempPath(FileUtils.WAV_FORMAT);
        String finalWav = FileUtils.getTempPath(FileUtils.WAV_FORMAT);

        result = CommonUtils.audioToWav(audioInputPath, rawAudioWav);
        if (!result)
        {
            //Log.d(TAG, "VideoUtils --> clipAudioRepeat: CommonUtils.audioToWav");
            return false;
        }

        //不是整数倍
        if (excessDuration > 0)
        {
            result = WavClip.clip(new File(rawAudioWav), new File(endBlockWav), 0, excessDuration);
            if (!result)
            {
                //Log.d(TAG, "VideoUtils --> clipAudioRepeat: WavClip.clip");
                return false;
            }
        }

        int block;
        if (excessDuration == 0)
        {
            block = (int) (allDuration / audioDuration);
        }
        else
        {
            block = (int) (allDuration / audioDuration) + 1;
        }

        if (block == 2)
        {
            if (excessDuration > 0)
            {
                ret = SoundJoint.joint(rawAudioWav, endBlockWav, finalWav);
                if (ret < 0)
                {
                    //Log.d(TAG, "VideoUtils --> clipAudioRepeat: SoundJoint.joint");
                    return false;
                }
            }
            else
            {
                ret = SoundJoint.joint(rawAudioWav, rawAudioWav, finalWav);
                if (ret < 0)
                {
                    //Log.d(TAG, "VideoUtils --> clipAudioRepeat: SoundJoint.joint");
                    return false;
                }
            }
        }

        if (block > 2)
        {
            List<String> fileList = new ArrayList<String>();
            if (excessDuration > 0)
            {
                for (int i = 0; i < block - 1; i++)
                {
                    fileList.add(rawAudioWav);
                }
                fileList.add(endBlockWav);
                ret = SoundJoint.joint(finalWav, fileList);
                if (ret < 0)
                {
                    //Log.d(TAG, "VideoUtils --> clipAudioRepeat: SoundJoint.joint");
                    return false;
                }
            }
            else
            {
                for (int i = 0; i < block; i++)
                {
                    fileList.add(rawAudioWav);
                }
                ret = SoundJoint.joint(finalWav, fileList);
                if (ret < 0)
                {
                    //Log.d(TAG, "VideoUtils --> clipAudioRepeat: SoundJoint.joint");
                    return false;
                }
            }
        }

        result = CommonUtils.wavToAudio(finalWav, audioOutputPath);
        if (!result)
        {
            //Log.d(TAG, "VideoUtils --> clipAudioRepeat: CommonUtils.wavToAudio");
            return false;
        }
        return true;
    }


    /**
     * 音频淡入淡出操作
     *
     * @param audioPath
     * @param outputPath
     * @param durationIn  淡入时间（单位 秒）
     * @param durationOut 淡出时间（单位 秒）
     * @return
     */
    public static boolean audioFade(String audioPath, String outputPath, int durationIn, int durationOut)
    {
        if (!FileUtils.isFileExists(audioPath) || outputPath == null)
        {
            return false;
        }

        String tempPcm = FileUtils.getTempPath(FileUtils.PCM_FORMAT);
        String tempOutPcm = FileUtils.getTempPath(FileUtils.PCM_FORMAT);

        int sampleRate = CommonUtils.getAudioSampleRate(audioPath);
        int channels = CommonUtils.getAudioChannels(audioPath);

        boolean result = CommonUtils.getAudioPcm(audioPath, tempPcm);
        if (!result || sampleRate < 1 || channels < 1)
        {
            FileUtils.delete(tempPcm);
            return false;
        }

        //淡入
        if (PcmAfade.afadein(tempPcm, tempOutPcm, durationIn, sampleRate, 16, channels) < 0)
        {
            FileUtils.delete(tempPcm);
            FileUtils.delete(tempOutPcm);
            return false;
        }

        FileUtils.delete(tempPcm);

        String tempFadeOutPcm = tempOutPcm;
        tempOutPcm = FileUtils.getTempPath(FileUtils.PCM_FORMAT);
        //淡出
        if (PcmAfade.afadeout(tempFadeOutPcm, tempOutPcm, durationOut, sampleRate, 16, channels) < 0)
        {
            FileUtils.delete(tempFadeOutPcm);
            FileUtils.delete(tempOutPcm);
            return false;
        }


        FileUtils.delete(tempFadeOutPcm);
        result = CommonUtils.encodeAudio(tempOutPcm, outputPath, sampleRate, channels);
        if (!result)
        {
            FileUtils.delete(tempOutPcm);
            return false;
        }

        FileUtils.delete(tempOutPcm);
        return true;
    }


    /**
     * 音频淡出处理
     *
     * @param audioPath  音频文件路径
     * @param outputPath 音频输出路径
     * @param duration   持续时间，单位s
     * @return 是否成功
     */
    public static boolean audioFadeOut(String audioPath, String outputPath, int duration)
    {
        if (!FileUtils.isFileExists(audioPath) || outputPath == null)
        {
            //Log.d(TAG, "VideoUtils --> audioFade: the audio path is not correct.");
            return false;
        }

        String tempPcm = FileUtils.getTempPath(FileUtils.PCM_FORMAT);
        String tempOutPcm = FileUtils.getTempPath(FileUtils.PCM_FORMAT);

        int sampleRate = CommonUtils.getAudioSampleRate(audioPath);
        int channels = CommonUtils.getAudioChannels(audioPath);

        boolean result = CommonUtils.getAudioPcm(audioPath, tempPcm);
        if (!result || sampleRate < 1 || channels < 1)
        {
            FileUtils.delete(tempPcm);
            return false;
        }

        if (PcmAfade.afadeout(tempPcm, tempOutPcm, duration, sampleRate, 16, channels) < 0)
        {
            FileUtils.delete(tempPcm);
            FileUtils.delete(tempOutPcm);
            return false;
        }

        FileUtils.delete(tempPcm);
        result = CommonUtils.encodeAudio(tempOutPcm, outputPath, sampleRate, channels);
        if (!result)
        {
            FileUtils.delete(tempOutPcm);
            return false;
        }

        FileUtils.delete(tempOutPcm);
        return true;
    }

    /**
     * 音频淡入处理
     *
     * @param audioPath  音频文件路径
     * @param outputPath 音频输出路径
     * @param duration   持续时间，单位s
     * @return 是否成功
     */
    public static boolean audioFadeIn(String audioPath, String outputPath, int duration)
    {
        if (!FileUtils.isFileExists(audioPath) || outputPath == null)
        {
            //Log.d(TAG, "VideoUtils --> audioFade: the audio path is not correct.");
            return false;
        }

        String tempPcm = FileUtils.getTempPath(FileUtils.PCM_FORMAT);
        String tempOutPcm = FileUtils.getTempPath(FileUtils.PCM_FORMAT);

        int sampleRate = CommonUtils.getAudioSampleRate(audioPath);
        int channels = CommonUtils.getAudioChannels(audioPath);

        boolean result = CommonUtils.getAudioPcm(audioPath, tempPcm);
        if (!result || sampleRate < 1 || channels < 1)
        {
            FileUtils.delete(tempPcm);
            return false;
        }

        if (PcmAfade.afadein(tempPcm, tempOutPcm, duration, sampleRate, 16, channels) < 0)
        {
            FileUtils.delete(tempPcm);
            FileUtils.delete(tempOutPcm);
            return false;
        }

        FileUtils.delete(tempPcm);
        result = CommonUtils.encodeAudio(tempOutPcm, outputPath, sampleRate, channels);
        if (!result)
        {
            FileUtils.delete(tempOutPcm);
            return false;
        }

        FileUtils.delete(tempOutPcm);
        return true;
    }


    /**
     * 音量调节(WAV ,AAC ,MP3)
     *
     * @param inputFile
     * @param outputFile
     * @param volume     范围：[0,1],原声：1
     * @return
     */
    public static boolean volumeAdjust(String inputFile, String outputFile, double volume)
    {

        String tempPcm = FileUtils.getTempPath(FileUtils.PCM_FORMAT);
        String tempOutPcm = FileUtils.getTempPath(FileUtils.PCM_FORMAT);

        int sampleRate = CommonUtils.getAudioSampleRate(inputFile);
        int channels = CommonUtils.getAudioChannels(inputFile);

        boolean result;
        result = CommonUtils.getAudioPcm(inputFile, tempPcm);
        if (!result || sampleRate < 1 || channels < 1)
        {
            //Log.d(TAG, "VideoUtils --> volumeAdjust: decode input file fail");
            return false;
        }

        int ret = -1;
        ret = PcmMix.volAdjust(tempPcm, tempOutPcm, volume);

        if (ret < 0)
        {
            //Log.d(TAG, "VideoUtils --> volumeAdjust: o volume adjust pcm fail");
            return false;
        }

        result = CommonUtils.encodeAudio(tempOutPcm, outputFile, sampleRate, channels);
        if (!result)
        {
            //Log.d(TAG, "VideoUtils --> volumeAdjust: encode pcm file fail");
            return false;
        }
        return true;
    }

    /**
     * 填充空白延长音频文件,WAV MP3 AAC
     *
     * @param inputPath  WAV MP3 AAC 任意格式，必须包含正确后缀
     * @param outputPath WAV MP3 AAC 任意格式，必须包含正确后缀
     * @param duration   延长后音频文件总时长 （单位：秒）
     * @param start      原有声音频文件在延长后的文件的某一开始位置 （单位：秒）
     * @param end        原有声音频文件在延长后的文件的某一结束位置 （单位：秒）
     * @return
     */
    public static boolean expandAudioDuration(String inputPath, String outputPath, double duration, double start, double end)
    {
        int sampleRate = CommonUtils.getAudioSampleRate(inputPath);
        int channels = CommonUtils.getAudioChannels(inputPath);

        double statDuration = start;
        double endDuration = duration - end;

        if (sampleRate < 1 || channels < 1)
        {
            return false;
        }

        if (start == 0 && end == duration)
        {
            return false;
        }

        //在后面扩大
        if (start == 0)
        {
            String endPartWav = FileUtils.getTempPath(FileUtils.WAV_FORMAT);
            boolean ret = GenerateMuteAudio.generteMuteWav(sampleRate, channels, 16, endDuration, endPartWav);
            if (!ret)
            {
                FileUtils.delete(endPartWav);
                return false;
            }

            String inputPartWav = FileUtils.getTempPath(FileUtils.WAV_FORMAT);
            ret = CommonUtils.audioToWav(inputPath, inputPartWav);
            if (!ret)
            {
                FileUtils.delete(inputPartWav);
                return false;
            }

            String outputWav = FileUtils.getTempPath(FileUtils.WAV_FORMAT);
            int result = SoundJoint.joint(inputPartWav, endPartWav, outputWav);
            if (result < 0)
            {
                FileUtils.delete(inputPartWav);
                FileUtils.delete(endPartWav);
                FileUtils.delete(outputWav);
                return false;
            }

            ret = CommonUtils.wavToAudio(outputWav, outputPath);
            if (!ret)
            {
                FileUtils.delete(outputWav);
                return false;
            }

        }//在前面扩大
        else if (end == duration)
        {
            String startPartWav = FileUtils.getTempPath(FileUtils.WAV_FORMAT);
            boolean ret = GenerateMuteAudio.generteMuteWav(sampleRate, channels, 16, statDuration, startPartWav);
            if (!ret)
            {
                FileUtils.delete(startPartWav);
                return false;
            }

            String inputPartWav = FileUtils.getTempPath(FileUtils.WAV_FORMAT);
            ret = CommonUtils.audioToWav(inputPath, inputPartWav);
            if (!ret)
            {
                FileUtils.delete(inputPartWav);
                return false;
            }

            String outputWav = FileUtils.getTempPath(FileUtils.WAV_FORMAT);
            int result = SoundJoint.joint(startPartWav, inputPartWav, outputWav);
            if (result < 0)
            {
                FileUtils.delete(startPartWav);
                FileUtils.delete(inputPartWav);
                FileUtils.delete(outputWav);
                return false;
            }

            ret = CommonUtils.wavToAudio(outputWav, outputPath);
            if (!ret)
            {
                FileUtils.delete(outputWav);
                return false;
            }

        }
        else//两头扩大
        {
            String startPartWav = FileUtils.getTempPath(FileUtils.WAV_FORMAT);
            boolean ret = GenerateMuteAudio.generteMuteWav(sampleRate, channels, 16, statDuration, startPartWav);
            if (!ret)
            {
                FileUtils.delete(startPartWav);
                return false;
            }
            String endPartWav = FileUtils.getTempPath(FileUtils.WAV_FORMAT);
            ret = GenerateMuteAudio.generteMuteWav(sampleRate, channels, 16, endDuration, endPartWav);
            if (!ret)
            {
                FileUtils.delete(endPartWav);
                return false;
            }

            String inputPartWav = FileUtils.getTempPath(FileUtils.WAV_FORMAT);
            ret = CommonUtils.audioToWav(inputPath, inputPartWav);
            if (!ret)
            {
                FileUtils.delete(inputPartWav);
                return false;
            }

            String outputWav = FileUtils.getTempPath(FileUtils.WAV_FORMAT);
            List<String> inputWavList = new ArrayList<>();
            inputWavList.add(startPartWav);
            inputWavList.add(inputPartWav);
            inputWavList.add(endPartWav);
            int result = SoundJoint.joint(outputWav, inputWavList);
            if (result < 0)
            {
                FileUtils.delete(startPartWav);
                FileUtils.delete(inputPartWav);
                FileUtils.delete(endPartWav);
                FileUtils.delete(outputWav);
                return false;
            }

            ret = CommonUtils.wavToAudio(outputWav, outputPath);
            if (!ret)
            {
                FileUtils.delete(outputWav);
                return false;
            }
        }

        return true;
    }


    /**
     * 音频片段拼接成一段音频AAC
     *
     * @param outPutPath 输出路径
     * @param inputPaths 输入路径
     * @param times      起止时间，start，end ； start， end 排序（单位 秒）
     * @return
     */
    public static boolean jointAacAudio(String outPutPath, List<String> inputPaths, float... times)
    {
        if (TextUtils.isEmpty(outPutPath) || inputPaths == null || inputPaths.size() == 0)
        {
            return false;
        }

        if (times == null || times.length == 0)
        {
            return false;
        }

        if (inputPaths.size() * 2 != times.length)
        {
            return false;
        }

        for (int i = 0, size = inputPaths.size(); i < size; i++)
        {
            float start = times[i * 2];
            float end = times[i * 2 + 1];

            if (start < 0 || end < 0 || start >= end)
            {
                return false;
            }

            String inputPath = inputPaths.get(i);
            if (FileUtils.isFileCanRead(inputPath))
            {
                NativeUtils.mixVideoSegment(inputPath, outPutPath, start, end);
            }
        }
        NativeUtils.endMixing();
        return true;
    }

    /**
     * 把音频解码为pcm数据，软解码
     *
     * @param audioPath    音频文件路径
     * @param outputPath   pcm文件输出路径
     * @param outputParams 可选输出参数，大小为2，第一个是采样率，第二个是声道数
     * @return 是否成功
     */
    public static boolean audioDecode2(String audioPath, String outputPath, @Nullable int[] outputParams)
    {
        if (!FileUtils.isFileExists(audioPath) || outputPath == null) return false;

        if (audioPath.endsWith(FileUtils.PCM_FORMAT))
        {
            FileUtils.copyFile(audioPath, outputPath);
            return true;
        }

        if (audioPath.endsWith(FileUtils.AAC_FORMAT))
        {
            if (outputParams != null)
            {
                //采样率 & 声道
                outputParams[0] = MP3DeEncode.getSamplerate(audioPath);
                outputParams[1] = MP3DeEncode.getChannels(audioPath);
            }
            //解码aac为pcm
            int result = AacEnDecoder.decodeAAC1(audioPath, outputPath);
            return result >= 0;
        }

        if (audioPath.endsWith(FileUtils.MP3_FORMAT))
        {
            if (outputParams != null)
            {
                //采样率 & 声道
                outputParams[0] = MP3DeEncode.getSamplerate(audioPath);
                outputParams[1] = MP3DeEncode.getChannels(audioPath);
            }
            //解码mp3为pcm
            int result = MP3DeEncode.decode(audioPath, outputPath);
            return result >= 0;
        }

        if (audioPath.endsWith(FileUtils.WAV_FORMAT))
        {
            if (outputParams != null)
            {
                //采样率 & 声道
                outputParams[0] = MP3DeEncode.getSamplerate(audioPath);
                outputParams[1] = MP3DeEncode.getChannels(audioPath);
            }
            return PcmWav.wavToPcm(audioPath, outputPath) >= 0;
        }

        return false;
    }

    /**
     * 把音频解码为pcm数据，硬解码
     *
     * @param audioPath    音频文件路径
     * @param outputPath   pcm文件输出路径
     * @param outputParams 可选输出参数，大小为2，第一个是采样率，第二个是声道数
     * @return 是否成功
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static boolean audioDecode(String audioPath, String outputPath, @Nullable int[] outputParams)
    {
        if (!FileUtils.isFileExists(audioPath) || outputPath == null)
        {
            //Log.d(TAG, "VideoUtils --> audioDecode: the audio path is not correct.");
            return false;
        }

        if (audioPath.endsWith(FileUtils.PCM_FORMAT))
        {
            FileUtils.copyFile(audioPath, outputPath);
            return true;
        }

        boolean result = false;
        AudioDecoderCore audioDecoder = new AudioDecoderCore(audioPath, outputPath, outputParams);
        try
        {
            audioDecoder.prepare();
            audioDecoder.start();

            result = true;
        }
        catch (IOException e)
        {
            e.printStackTrace();

        }
        finally
        {
            audioDecoder.release();
        }

        return result;
    }


    /**
     * 获取视频中的aac音轨
     *
     * @param videoPath  视频路径
     * @param aacOutPath 视频aac音频路径
     * @return
     */
    public static final boolean getAacFromVideo(String videoPath, String aacOutPath)
    {
        if (!FileUtils.isFileExists(videoPath) || TextUtils.isEmpty(aacOutPath))
        {
            return false;
        }

        int result = NativeUtils.getAACFromVideo(videoPath, aacOutPath);
        return result >= 0;
    }


    /**
     * 获取视频时长 单位毫秒
     *
     * @param videoPath
     * @return
     */
    public static double getDurationFromVideo(String videoPath)
    {
        double out = 0D;
        if (!FileUtils.isFileExists(videoPath))
        {
            return out;
        }
        out = NativeUtils.getDurationFromFile(videoPath) * 1000D;
        return out;
    }


    /**
     * 获取音频时长（WAV MP3 AAC） 单位毫秒
     * FIXME 高采样率音频获取时长有问题
     *
     * @param audioPath
     * @return
     */
    public static double getDurationFromAudio(String audioPath)
    {
        /*double out = 0D;
        if (!FileUtils.isFileExists(audioPath))
        {
            //Log.d(TAG, "VideoUtils --> getDurationFromAudio: the audio path is not correct.");
            return out;
        }

        //采样率 & 声道
        int sampleRate = CommonUtils.getAudioSampleRate(audioPath);
        int chanels = CommonUtils.getAudioChannels(audioPath);


        String tempPcm = FileUtils.getTempPath(FileUtils.PCM_FORMAT);
        boolean result = CommonUtils.getAudioPcm(audioPath, tempPcm);
        if (!result)
        {
            return -1D;
        }
        out = CommonUtils.getPcmDuration(sampleRate, new File(tempPcm).length(), 16, chanels);

        FileUtils.delete(tempPcm);
        return out * 1000D;*/

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try
        {
            mmr.setDataSource(audioPath);
            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            return Long.valueOf(duration);
        }
        catch (Throwable throwable)
        {
            throwable.printStackTrace();
            return NativeUtils.getDurationFromFile(audioPath) * 1000D;
        }
        finally
        {
            mmr.release();
        }
    }

    /**
     * 获取视频帧率
     *
     * @param videoPath 视频路径
     * @return 视频帧率
     */
    public static float getVideoFrameRate(String videoPath)
    {
        if (!FileUtils.isFileExists(videoPath))
        {
            return 0F;
        }
        return NativeUtils.getFPSFromFile(videoPath);
    }
}



