package cn.poco.video;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * @author lmx
 *         Created by lmx on 2017/8/15.
 */

public class AudioStore
{
    public static ArrayList<AudioInfo> GetAudioInfos(Context context)
    {
        return GetAudioInfos(context, false);
    }

    public static ArrayList<AudioInfo> GetAudioInfos(Context context, boolean getAudioSampleRate)
    {
        ArrayList<AudioInfo> out = new ArrayList<>();

        String[] projection = {
                MediaStore.Audio.Media._ID,             //0
                MediaStore.Audio.Media.TITLE,           //1
                MediaStore.Audio.Media.DISPLAY_NAME,    //2
                MediaStore.Audio.Media.DATA,            //3
                MediaStore.Audio.Media.ALBUM_ID,        //4
                MediaStore.Audio.Media.ALBUM,           //5
                MediaStore.Audio.Media.ARTIST,          //6
                MediaStore.Audio.Media.DURATION,        //7
                MediaStore.Audio.Media.SIZE,            //8
                MediaStore.Audio.Media.DATE_ADDED,      //9
                MediaStore.Audio.Media.IS_MUSIC         //10
        };

        Cursor cursor = null;
        int isMusic;
        String path;

        cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        );

        if (cursor != null && cursor.moveToFirst())
        {
            int columnsIndex[] = new int[projection.length];
            for (int i = 0; i < projection.length; i++)
            {
                columnsIndex[i] = cursor.getColumnIndex(projection[i]);
            }

            AudioInfo audioInfo = null;
            SimpleDateFormat format = new SimpleDateFormat("mm:ss", Locale.getDefault());

            do
            {
                // 是否为音乐，魅族手机上始终为0
                isMusic = cursor.getInt(columnsIndex[10]);
                path = cursor.getString(columnsIndex[3]);
                if ((IsFlyme() || isMusic != 0) && IsSupport(path))
                {
                    audioInfo = new AudioInfo();
                    audioInfo.setId(cursor.getLong(columnsIndex[0]));
                    audioInfo.setTitle(cursor.getString(columnsIndex[1]));
                    audioInfo.setDisplayName(cursor.getString(columnsIndex[2]));
                    audioInfo.setPath(path);
                    audioInfo.setAlbumId(cursor.getLong(columnsIndex[4]));
                    audioInfo.setCoverPath(GetCoverPath(context, audioInfo.getAlbumId()));
                    audioInfo.setAlbum(cursor.getString(columnsIndex[5]));
                    audioInfo.setArtist(cursor.getString(columnsIndex[6]));
                    audioInfo.setDuration(cursor.getLong(columnsIndex[7]));
                    audioInfo.setSize(cursor.getLong(columnsIndex[8]));
                    audioInfo.setAddTime(cursor.getLong(columnsIndex[9]));
                    audioInfo.setTimeFormat(format.format(new Date(audioInfo.getDuration())));
                    if (getAudioSampleRate)
                    {
                        //获取采样率
                        //FIXME BUG FIXME
                        //audioInfo.setSampleRate(CommonUtils.getAudioSampleRate(path));
                    }

                    //过滤时间较短的音频
                    if (audioInfo.getDuration() / 1000 > 1 &&
                            (path.endsWith(".aac") || path.endsWith(".mp3") || path.endsWith(".wav")))
                    {
                        out.add(audioInfo);
                    }
                }
            }
            while (cursor.moveToNext());
        }
        return out;
    }

    /**
     * 查询专辑封面图片uri
     */
    private static String GetCoverPath(Context context, long albumId)
    {
        String uri = null;
        Cursor cursor = null;
        try
        {
            cursor = context.getContentResolver().query(
                    Uri.parse("content://media/external/audio/albums/" + albumId),
                    new String[]{"album_art"}, null, null, null);

            if (cursor != null)
            {
                cursor.moveToNext();
                uri = cursor.getString(0);
            }
        }
        catch (Throwable throwable)
        {
            throwable.printStackTrace();
        }
        finally
        {
            if (cursor != null)
            {
                cursor.close();
            }
        }
        return uri;
    }

    /**
     * 是否是魅族系统
     */
    public static boolean IsFlyme()
    {
        return "meizu".equalsIgnoreCase(Build.MANUFACTURER);
    }

    /**
     * 是否匹配 AAC、WAV、MP3 格式音频
     *
     * @param path
     * @return
     */
    public static boolean IsSupport(String path)
    {
        if (TextUtils.isEmpty(path) || !new File(path).exists())
        {
            return false;
        }
        return path.endsWith(FileUtils.AAC_FORMAT) || path.endsWith(FileUtils.WAV_FORMAT) || path.endsWith(FileUtils.MP3_FORMAT);
    }

    public static class AudioInfo
    {
        private long mId;               // id
        private String mTitle;          // 标题
        private String mDisplayName;    // 名称
        private String mPath;           // 路径
        private long mAlbumId;          // 专辑id
        private String mAlbum;          // 专辑
        private String mCoverPath;      // 封面图
        private String mArtist;         // 艺术家
        private long mDuration;         // 时长
        private long mSize;             // 大小
        private long mAddTime;          // 添加时间
        private long mSampleRate;       // 采样率
        private String mTimeFormat;     // 时间格式

        public long getId()
        {
            return mId;
        }

        public void setId(long id)
        {
            mId = id;
        }

        public String getTitle()
        {
            return mTitle;
        }

        public void setTitle(String title)
        {
            mTitle = title;
        }

        public String getDisplayName()
        {
            return mDisplayName;
        }

        public void setDisplayName(String displayName)
        {
            mDisplayName = displayName;
        }

        public String getPath()
        {
            return mPath;
        }

        public void setPath(String path)
        {
            mPath = path;
        }

        public long getAlbumId()
        {
            return mAlbumId;
        }

        public void setAlbumId(long albumId)
        {
            mAlbumId = albumId;
        }

        public String getAlbum()
        {
            return mAlbum;
        }

        public void setAlbum(String album)
        {
            mAlbum = album;
        }

        public String getArtist()
        {
            return mArtist;
        }

        public void setArtist(String artist)
        {
            mArtist = artist;
        }

        public long getDuration()
        {
            return mDuration;
        }

        public void setDuration(long duration)
        {
            mDuration = duration;
        }

        public long getSize()
        {
            return mSize;
        }

        public void setSize(long size)
        {
            mSize = size;
        }

        public String getCoverPath()
        {
            return mCoverPath;
        }

        public void setCoverPath(String coverPath)
        {
            mCoverPath = coverPath;
        }

        public long getAddTime()
        {
            return mAddTime;
        }

        public void setAddTime(long addTime)
        {
            mAddTime = addTime;
        }

        public long getSampleRate()
        {
            return mSampleRate;
        }

        public void setSampleRate(long mSampleRate)
        {
            this.mSampleRate = mSampleRate;
        }

        public String getTimeFormat()
        {
            return mTimeFormat;
        }

        public void setTimeFormat(String mTimeFormat)
        {
            this.mTimeFormat = mTimeFormat;
        }

        @Override
        public String toString()
        {
            return "AudioInfo{" +
                    "mId=" + mId +
                    ", mTitle='" + mTitle + '\'' +
                    ", mDisplayName='" + mDisplayName + '\'' +
                    ", mPath='" + mPath + '\'' +
                    ", mAlbumId=" + mAlbumId +
                    ", mAlbum='" + mAlbum + '\'' +
                    ", mCoverPath='" + mCoverPath + '\'' +
                    ", mArtist='" + mArtist + '\'' +
                    ", mDuration=" + mDuration +
                    ", mSize=" + mSize +
                    ", mAddTime=" + mAddTime +
                    ", mSampleRate=" + mSampleRate +
                    ", mTimeFormat='" + mTimeFormat + '\'' +
                    '}';
        }
    }
}
