package cn.poco.video;

import android.os.Environment;
import android.support.annotation.StringDef;
import android.text.TextUtils;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import cn.poco.system.SysConfig;
import cn.poco.tianutils.CommonUtils;
import cn.poco.utils.FileUtil;

/**
 * @author lmx
 *         Created by lmx on 2017/7/26.
 */

public class FileUtils
{
    public static final String AAC_FORMAT = ".aac";
    public static final String WAV_FORMAT = ".wav";
    public static final String PCM_FORMAT = ".pcm";
    public static final String MP3_FORMAT = ".mp3";
    public static final String MP4_FORMAT = ".mp4";
    public static final String H264_FORMAT = ".h264";

    @StringDef({AAC_FORMAT, WAV_FORMAT, PCM_FORMAT, MP3_FORMAT, MP4_FORMAT, H264_FORMAT})
    @Retention(RetentionPolicy.SOURCE)
    @interface Format
    {
    }

    public static String sVideoDir;
    private static String sTempDir;

    /**
     * 视频存储目录
     *
     * @return
     */
    public static String getVideoDir()
    {
        if (TextUtils.isEmpty(sVideoDir))
        {
            sVideoDir = SysConfig.GetAppPath() + File.separator + "video";
        }
        return sVideoDir;
    }

    /**
     * 视频混合临时缓存目录（隐藏）
     *
     * @return
     */
    public static String getTempDir()
    {
        if (TextUtils.isEmpty(sTempDir))
        {
            sTempDir = getVideoDir() + File.separator + ".temp";
        }

        return sTempDir;
    }


    /**
     * 清除所有临时文件
     */
    public static void clearTempFiles()
    {
        if (sTempDir != null && new File(sTempDir).exists())
        {
            FileUtil.deleteSDFile(FileUtils.sTempDir, false);
        }
        sTempDir = null;
    }


    /**
     * 清除录制视频临时文件
     */
    public static void clearVideoFiles()
    {
        if (sVideoDir != null && new File(sVideoDir).exists())
        {
            FileUtil.deleteSDFile(FileUtils.sVideoDir, false);
        }
        sVideoDir = null;
    }

    /**
     * 获取临时路径
     *
     * @param format 文件格式
     * @return 临时路径
     */
    public static String getTempPath(@Format String format)
    {
        // 确保文件夹存在
        sTempDir = getTempDir();
        CommonUtils.MakeFolder(sTempDir);
        return sTempDir + File.separator + UUID.randomUUID() + format;
    }


    /**
     * 获取临时路径
     *
     * @param format 文件格式
     * @return 临时路径
     */
    public static String getTempPath(@Format String format, String suffix)
    {
        // 确保文件夹存在
        sTempDir = getTempDir();
        CommonUtils.MakeFolder(sTempDir);
        return sTempDir + File.separator + UUID.randomUUID() + (!TextUtils.isEmpty(suffix) ? ("_" + suffix) : "") + format;
    }

    /**
     * 输出视频路径（系统camera/video目录下）
     *
     * @return
     */
    public static String getVideoOutputSysPath()
    {
        String dir = getVideoOutputSysDir();
        CommonUtils.MakeFolder(dir);
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String strDate = df.format(new Date());
        return dir + File.separator + SysConfig.GetAppFileName() + "_" + strDate + MP4_FORMAT;
    }


    /**
     * 输出视频路径目录（系统camera/video目录下）
     *
     * @return
     */
    public static String getVideoOutputSysDir()
    {
        String dir = cn.poco.cloudAlbum.utils.FileUtils.getPhotoSavePath();
        if (cn.poco.cloudAlbum.utils.FileUtils.isMeizuManufacturer())
        {
            //魅族设备视频存放目录
            File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            if (dcim != null)
            {
                dir = dcim.getAbsolutePath() + File.separator + "Video";
            }
            else
            {
                dir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "DCIM" + File.separator + "Video";
            }
        }
        CommonUtils.MakeFolder(dir);
        return dir;
    }

    /**
     * 文件是否存在
     *
     * @param path
     */
    public static boolean isFileExists(String path)
    {
        return FileUtil.isFileExists(path);
    }

    public static boolean isAssetFile(String fileName)
    {
        if (TextUtils.isEmpty(fileName))
        {
            return false;
        }

        if (fileName.startsWith("file:///android_asset"))
        {
            return true;
        }

        return false;
    }

    /**
     * 文件是否可读取
     *
     * @param path
     * @return
     */
    public static boolean isFileCanRead(String path)
    {
        if (!TextUtils.isEmpty(path))
        {
            try
            {
                File file = new File(path);
                if (file.exists() && file.canRead())
                {
                    return true;
                }
            }
            catch (Throwable t)
            {
                return false;
            }
        }
        return false;
    }


    public static boolean isFileValid(String path)
    {
        if (!TextUtils.isEmpty(path))
        {
            try
            {
                File file = new File(path);
                if (file.exists() && file.canRead() && file.length() > 1024 * 2)
                {
                    return true;
                }
            }
            catch (Throwable t)
            {
                return false;
            }
        }
        return false;
    }


    /**
     * 根据后缀创建临时音频文件（AAC WAV MP3）
     *
     * @param file
     * @return
     */
    public static String newTempAudioFile(String file)
    {
        String result = null;
        if (TextUtils.isEmpty(file)) return null;

        if (file.endsWith(FileUtils.AAC_FORMAT))
        {
            result = FileUtils.getTempPath(FileUtils.AAC_FORMAT);
        }
        else if (file.endsWith(FileUtils.WAV_FORMAT))
        {
            result = FileUtils.getTempPath(FileUtils.WAV_FORMAT);
        }
        else if (file.endsWith(FileUtils.MP3_FORMAT))
        {
            result = FileUtils.getTempPath(FileUtils.MP3_FORMAT);
        }
        return result;
    }


    /**
     * 复制文件
     *
     * @param inputPath  源文件路劲
     * @param outputPath 目标路径
     * @return
     */
    public static boolean copyFile(String inputPath, String outputPath)
    {
        return FileUtil.copySDFile(inputPath, outputPath);
    }

    /**
     * 删除SD卡中的文件或目录
     *
     * @param path
     * @param deleteParent true为删除父目录
     * @return
     */
    public static boolean deleteSDFile(String path, boolean deleteParent)
    {
        return FileUtil.deleteSDFile(path, deleteParent);
    }


    /**
     * 删除指定文件，如果指定文件时目录，需要先删除该目录下的所有文件才能删除该目录
     *
     * @param path 文件路径
     * @return 删除是否成功
     */
    public static boolean delete(String path)
    {
        if (!TextUtils.isEmpty(path))
        {
            File file = new File(path);
            if (!file.exists())
            {
                return false;
            }

            if (file.isDirectory())
            {
                String[] filePaths = file.list();
                for (String filePath : filePaths)
                {
                    delete(path + "/" + filePath);
                }
            }

            return file.delete();
        }

        return false;
    }
}
