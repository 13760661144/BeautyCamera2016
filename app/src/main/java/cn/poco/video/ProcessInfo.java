package cn.poco.video;

/**
 * @author lmx
 *         Created by lmx on 2017/7/26.
 */

public class ProcessInfo {
    //background music params
    public double m_bg_music_start = 0D;            //背景音乐起始时间（单位毫秒）
    public double m_bg_music_end = 0D;              //背景音乐结束时间（单位毫秒）
    public String m_bg_music_path;                  //背景音乐路径
    public double m_bg_volume_adjust = 0.6D;        //背景音乐音量调节范围：[0,1],原声：1
    public double m_video_volume_adjust = 1f;       //视频录音音量调节范围：[0,1],原声：1

    //video params
    public String m_video_path;                     //视频路径
    public boolean is_silence_play;                 //视频是否静音输出
    public String[] m_video_paths;                  //视频路径（多个）

    public boolean is_clear_temp_cache = true;      //清除缓存文件

    public int m_video_rotation;                    //视频旋转角度
}
