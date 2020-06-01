package cn.poco.gldraw;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.opengl.GLES11Ext;
import android.text.TextUtils;
import android.view.Surface;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

import cn.poco.glfilter.base.GlUtil;

/**
 * Created by zwq on 2016/09/02 10:40.<br/><br/>
 */
public class VideoRendererTexture {

    private static final String TAG = "VideoRendererTexture";

    private Context mContext;
    private int mVideoTextureId;
    private SurfaceTexture mSurfaceTexture;
    private Surface mSurface;
    private boolean initSuccess;
    private float[] mVideoSTMatrix;
    private boolean mIsStartSuccess;
    private MediaPlayer mMediaPlayer;

    private Object mVideoRes;
    private FileInputStream mFileInputStream;
    private MyFileDescriptor mMyFileDescriptor;
    private int mVideoWidth;
    private int mVideoHeight;
    private boolean mInvalidFrame;
    private MediaMetadataRetriever mediaMetadataRetriever;

    public static class MyFileDescriptor {
        private AssetFileDescriptor mAssetFileDescriptor;
        private FileDescriptor mFileDescriptor;
        private long offset;
        private long length;

        public MyFileDescriptor(FileDescriptor fileDescriptor) {
            if (fileDescriptor == null) {
                throw new IllegalArgumentException("FileDescriptor is null");
            }
            mFileDescriptor = fileDescriptor;
            offset = 0;
            length = 0x7ffffffffffffffL;
        }

        public MyFileDescriptor(AssetFileDescriptor assetFileDescriptor) {
            if (assetFileDescriptor == null) {
                throw new IllegalArgumentException("AssetFileDescriptor is null");
            }
            mAssetFileDescriptor = assetFileDescriptor;
            mFileDescriptor = assetFileDescriptor.getFileDescriptor();
            offset = assetFileDescriptor.getStartOffset();
            length = assetFileDescriptor.getLength();
        }

        public FileDescriptor getFileDescriptor() {
            return mFileDescriptor;
        }

        public long getStartOffset() {
            return offset;
        }

        public long getLength() {
            return length;
        }

        public boolean valid() {
            return mFileDescriptor == null ? false : mFileDescriptor.valid();
        }

        public void close() {
            if (mAssetFileDescriptor != null) {
                try {
                    mAssetFileDescriptor.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void release() {
            mAssetFileDescriptor = null;
            mFileDescriptor = null;
        }
    }

    public VideoRendererTexture(Context context) {
        mContext = context;
        mVideoTextureId = createTextureId();
        try {
            mSurfaceTexture = new SurfaceTexture(mVideoTextureId);
            initSuccess = true;
            mVideoSTMatrix = new float[16];
            if (mSurfaceTexture != null) {
                mSurface = new Surface(mSurfaceTexture);
            }
        } catch (Exception e) {
            e.printStackTrace();
            initSuccess = false;
        }
    }

    public int createTextureId() {
        return GlUtil.createTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
    }

    public int getTextureId() {
        return mVideoTextureId;
    }

    public float[] getSTMatrix() {
        return mVideoSTMatrix;
    }

    private void initMediaPlayer() {
        if (mMediaPlayer == null && mSurface != null) {
            mMediaPlayer = new MediaPlayer();
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.setSurface(mSurface);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    try {
//                        Log.i(TAG, "MediaPlayer prepared and start playing");
                        mMediaPlayer.start();
                        mIsStartSuccess = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    public MyFileDescriptor getMyFileDescriptor(Context context, Object videoRes) {
        MyFileDescriptor myFileDescriptor = null;
        if (videoRes != null) {
            try {
                if (videoRes instanceof Integer) {
                    AssetFileDescriptor mAssetFileDescriptor = context.getResources().openRawResourceFd((Integer) videoRes);
                    myFileDescriptor = new MyFileDescriptor(mAssetFileDescriptor);
                } else if (videoRes instanceof String) {
                    String videoPath = videoRes.toString();
                    if (TextUtils.isEmpty(videoPath)) {
                        throw new IllegalArgumentException("video res path is null");
                    }
                    if (videoPath.startsWith("/")) {
                        mFileInputStream = new FileInputStream(videoPath);
                        if (mFileInputStream != null) {
                            myFileDescriptor = new MyFileDescriptor(mFileInputStream.getFD());
                        }
                    } else {
                        AssetFileDescriptor mAssetFileDescriptor = context.getAssets().openFd(videoPath);
                        myFileDescriptor = new MyFileDescriptor(mAssetFileDescriptor);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Could not open video: "+e.getMessage());
            }
        }
        return myFileDescriptor;
    }

    public void startPlayer() {
        if (initSuccess && !mIsStartSuccess) {
            initMediaPlayer();
            if (mMediaPlayer != null) {
                try {
                    if (mMyFileDescriptor != null && mMyFileDescriptor.valid()) {
                        mMediaPlayer.setDataSource(mMyFileDescriptor.getFileDescriptor(), mMyFileDescriptor.getStartOffset(), mMyFileDescriptor.getLength());
                        if (mFileInputStream != null) {
                            mFileInputStream.close();
                            mFileInputStream = null;
                        }
                        mMediaPlayer.prepare();
                    }
                } catch (Exception e) {
                    e.printStackTrace();//start player fail
                }
            }
        }
    }

    public boolean isPlaying() {
        if (mMediaPlayer != null && mIsStartSuccess) {
            if (!mInvalidFrame) {
                if (mMediaPlayer.getCurrentPosition() > 0) {
                    mInvalidFrame = true;
                }
            }
            if (mInvalidFrame && mMediaPlayer.isPlaying()) {
                return true;
            }
        }
        return false;
    }

    public void stopPlayer() {
        if (mIsStartSuccess && mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mIsStartSuccess = false;
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mInvalidFrame = false;
        }
    }

    /**
     * 视频文件
     *
     * @param videoRes
     */
    public void setVideoRes(Object videoRes, boolean start) {
        if (videoRes == mVideoRes) {
            return;
        }
        mVideoRes = videoRes;
        if (mMyFileDescriptor != null) {
            mMyFileDescriptor.release();
            mMyFileDescriptor = null;
        }
        mVideoWidth = 0;
        mVideoHeight = 0;
        if (mediaMetadataRetriever != null) {
            mediaMetadataRetriever.release();
            mediaMetadataRetriever = null;
        }

        stopPlayer();
        mMyFileDescriptor = getMyFileDescriptor(mContext, videoRes);
        if (mMyFileDescriptor != null) {
            initMediaMetaData();
            if (start) {
                startPlayer();
            }
        }
    }

    public void setVideoRes(Object videoRes) {
        setVideoRes(videoRes, true);
    }

    public void updateTextureImage() {
        if (initSuccess && mSurfaceTexture != null) {
            mSurfaceTexture.updateTexImage();
            mSurfaceTexture.getTransformMatrix(mVideoSTMatrix);
        }
    }

    private MediaMetadataRetriever initMediaMetaData() {
        if (mediaMetadataRetriever == null) {
            if (mMyFileDescriptor != null && mMyFileDescriptor.valid()) {
                mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(mMyFileDescriptor.getFileDescriptor(), mMyFileDescriptor.getStartOffset(), mMyFileDescriptor.getLength());
            }
            if (mediaMetadataRetriever != null) {
                try {
                    String width = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                    String height = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                    mVideoWidth = Integer.parseInt(width);
                    mVideoHeight = Integer.parseInt(height);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return mediaMetadataRetriever;
    }

    public int getVideoWidth() {
        initMediaMetaData();
        return mVideoWidth;
    }

    public int getVideoHeight() {
        initMediaMetaData();
        return mVideoHeight;
    }

    public Bitmap getFrameBitmap() {
        Bitmap bitmap = null;
        if (mIsStartSuccess && mMediaPlayer != null && mMediaPlayer.isPlaying()) {
//            FFmpegMediaMetadataRetriever mediaMetadataRetriever = null;//不支持assets/raw目录文件
//            if (mMyFileDescriptor != null) {
//                mediaMetadataRetriever = new FFmpegMediaMetadataRetriever();
//                try {
//                    if (mMyFileDescriptor.getStartOffset() > 0) {
//                        mediaMetadataRetriever.setDataSource(mMyFileDescriptor.getFileDescriptor(), mMyFileDescriptor.getStartOffset(), mMyFileDescriptor.getLength());
//                    } else {
//                        mediaMetadataRetriever.setDataSource(mVideoRes.toString());
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
            initMediaMetaData();
            if (mediaMetadataRetriever != null) {
//                long milliseconds = mMediaPlayer.getCurrentPosition();//毫秒
//                Log.i("bbb", "milliseconds:" + milliseconds);
//                Log.i("bbb", "width:" + mediaMetadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));

//                bitmap = mediaMetadataRetriever.getFrameAtTime(milliseconds * 1000L, MediaMetadataRetriever.OPTION_CLOSEST);
                bitmap = mediaMetadataRetriever.getFrameAtTime();
//                bitmap = mediaMetadataRetriever.getFrameAtTime(milliseconds * 1000L, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
//                bitmap = mediaMetadataRetriever.getFrameAtTime(milliseconds * 1000L, MediaMetadataRetriever.OPTION_PREVIOUS_SYNC);

                mediaMetadataRetriever.release();
                mediaMetadataRetriever = null;
            }
        }
        return bitmap;
    }

    public void release() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        if (mSurface != null) {
            mSurface.release();
            mSurface = null;
        }
        mContext = null;
        mVideoTextureId = 0;
        initSuccess = false;
        mVideoSTMatrix = null;
        mIsStartSuccess = false;

        mVideoRes = null;
        if (mMyFileDescriptor != null) {
            mMyFileDescriptor.release();
            mMyFileDescriptor = null;
        }
        mVideoWidth = 0;
        mVideoHeight = 0;
        mInvalidFrame = false;
        if (mediaMetadataRetriever != null) {
            mediaMetadataRetriever.release();
            mediaMetadataRetriever = null;
        }
    }
}
