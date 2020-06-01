package cn.poco.video.music;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.io.File;
import java.io.FileOutputStream;

import cn.poco.audio.soundclip.CheapSoundFile;

/**
 * WAV格式音频波形绘制
 * Created by menghd on 2017/6/9 0009.
 */

public class WaveBitmapFactory
{
    private Paint mPaint;
    private CheapSoundFile mSoundFile;
    private int[] mLenByZoomLevel;
    private double[][] mValuesByZoomLevel;
    private double[] mZoomFactorByZoomLevel;
    private int[] mHeightsAtThisZoomLevel;
    private int mZoomLevel;
    private int mOffset;
    private int state = 0;

    private int waveLineSpan = 10;
    private int waveColor = Color.WHITE;
    private int mWidth;
    private int mHeight;
    private int paintStroke = 5;
    private double mZoom = 1;

    public WaveBitmapFactory(int width, int height)
    {
        mPaint = new Paint();
        mPaint.setAntiAlias(false);
        mSoundFile = null;
        mLenByZoomLevel = null;
        mValuesByZoomLevel = null;
        mHeightsAtThisZoomLevel = null;
        mOffset = 0;
        mWidth = width;
        mHeight = height;
    }

    public void setSoundFile(CheapSoundFile soundFile)
    {
        mSoundFile = soundFile;
        computeDoublesForAllZoomLevels();
        mHeightsAtThisZoomLevel = null;
    }

    /**
     * @param file wav音频路径
     * @return
     */
    public boolean setSoundFilePath(String file)
    {
        try
        {
            mSoundFile = CheapSoundFile.create(file, null);
            if (mSoundFile == null)
            {
                return false;
            }
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            return false;
        }

        computeDoublesForAllZoomLevels();
        mHeightsAtThisZoomLevel = null;
        return true;
    }


    /**
     * @param span 波形间隔
     */
    public void setWaveLineSpan(int span)
    {
        waveLineSpan = span;
    }

    /**
     * @param color 波形颜色
     */
    public void setWaveColor(int color)
    {
        waveColor = color;
    }

    public void setZoom(double zoom)
    {
        mZoom = zoom;
    }

    private int maxPos()
    {
        return mLenByZoomLevel[mZoomLevel];
    }

    private Bitmap bitmap;

    public WaveInfo getData()
    {
        WaveInfo info;

        if (state == 1)
        {
            mSoundFile = null;
            state = 0;
            return null;
        }

        if (mHeightsAtThisZoomLevel == null)
        {
            computeIntsForThisZoomLevel();
        }
        info = new WaveInfo();
        info.mHeightsAtThisZoomLevel = mHeightsAtThisZoomLevel;
        info.max = maxPos();
        return info;
    }

    /**
     * 开始画图
     */
    public void draw()
    {
        bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        if (state == 1)
        {
            mSoundFile = null;
            state = 0;
            return;
        }

        if (mHeightsAtThisZoomLevel == null)
        {
            computeIntsForThisZoomLevel();
        }

        int start = mOffset;
        int ctr = mHeight / 2;

        for (int i = 0; i < maxPos(); i += waveLineSpan)
        {
            mPaint.setColor(waveColor);
            mPaint.setStrokeWidth(paintStroke);

            drawWaveformLine(
                    canvas, i,
                    (int) (ctr - mHeightsAtThisZoomLevel[start + i] * mZoom),
                    (int) (ctr + 1 + mHeightsAtThisZoomLevel[start + i] * mZoom),
                    mPaint);
        }
    }

    protected void drawWaveformLine(Canvas canvas, int x, int y0, int y1, Paint paint)
    {

        int pos = maxPos();
        float rat = ((float) mWidth / pos);
        canvas.drawLine((int) (x * rat), y0, (int) (x * rat), y1, paint);
    }

    /**
     * @return 获取画好的bitmap
     */
    public Bitmap getBitmap()
    {
        return bitmap;
    }


    private void computeDoublesForAllZoomLevels()
    {
        int numFrames = mSoundFile.getNumFrames();
        int[] frameGains = mSoundFile.getFrameGains();
        double[] smoothedGains = new double[numFrames];
        if (numFrames == 1)
        {
            smoothedGains[0] = frameGains[0];
        }
        else if (numFrames == 2)
        {
            smoothedGains[0] = frameGains[0];
            smoothedGains[1] = frameGains[1];
        }
        else if (numFrames > 2)
        {
            smoothedGains[0] = (double) ((frameGains[0] / 2.0) + (frameGains[1] / 2.0));
            for (int i = 1; i < numFrames - 1; i++)
            {
                smoothedGains[i] = (double) ((frameGains[i - 1] / 3.0) + (frameGains[i] / 3.0) + (frameGains[i + 1] / 3.0));
            }
            smoothedGains[numFrames - 1] = (double) ((frameGains[numFrames - 2] / 2.0) + (frameGains[numFrames - 1] / 2.0));
        }

        double maxGain = 1.0;
        for (int i = 0; i < numFrames; i++)
        {
            if (smoothedGains[i] > maxGain)
            {
                maxGain = smoothedGains[i];
            }
        }
        double scaleFactor = 1.0;
        if (maxGain > 255.0)
        {
            scaleFactor = 255 / maxGain;
        }

        maxGain = 0;
        int gainHist[] = new int[256];
        for (int i = 0; i < numFrames; i++)
        {
            int smoothedGain = (int) (smoothedGains[i] * scaleFactor);
            if (smoothedGain < 0)
            {
                smoothedGain = 0;
            }
            if (smoothedGain > 255)
            {
                smoothedGain = 255;
            }
            if (smoothedGain > maxGain)
            {
                maxGain = smoothedGain;
            }

            gainHist[smoothedGain]++;
        }

        double minGain = 0;
        int sum = 0;
        while (minGain < 255 && sum < numFrames / 20)
        {
            sum += gainHist[(int) minGain];
            minGain++;
        }

        sum = 0;
        while (maxGain > 2 && sum < numFrames / 100)
        {
            sum += gainHist[(int) maxGain];
            maxGain--;
        }
        if (maxGain <= 50)
        {
            maxGain = 80;
        }
        else if (maxGain > 50 && maxGain < 120)
        {
            maxGain = 142;
        }
        else
        {
            maxGain += 10;
        }


        double[] heights = new double[numFrames];
        double range = maxGain - minGain;
        for (int i = 0; i < numFrames; i++)
        {
            double value = (smoothedGains[i] * scaleFactor - minGain) / range;
            if (value < 0.0)
            {
                value = 0.0;
            }
            if (value > 1.0)
            {
                value = 1.0;
            }
            heights[i] = value * value;
        }

        mLenByZoomLevel = new int[5];
        mZoomFactorByZoomLevel = new double[5];
        mValuesByZoomLevel = new double[5][];

        mLenByZoomLevel[0] = numFrames * 2;
        mZoomFactorByZoomLevel[0] = 2.0;
        mValuesByZoomLevel[0] = new double[mLenByZoomLevel[0]];
        if (numFrames > 0)
        {
            mValuesByZoomLevel[0][0] = 0.5 * heights[0];
            mValuesByZoomLevel[0][1] = heights[0];
        }
        for (int i = 1; i < numFrames; i++)
        {
            mValuesByZoomLevel[0][2 * i] = 0.5 * (heights[i - 1] + heights[i]);
            mValuesByZoomLevel[0][2 * i + 1] = heights[i];
        }

        mLenByZoomLevel[1] = numFrames;
        mValuesByZoomLevel[1] = new double[mLenByZoomLevel[1]];
        mZoomFactorByZoomLevel[1] = 1.0;
        for (int i = 0; i < mLenByZoomLevel[1]; i++)
        {
            mValuesByZoomLevel[1][i] = heights[i];
        }

        for (int j = 2; j < 5; j++)
        {
            mLenByZoomLevel[j] = mLenByZoomLevel[j - 1] / 2;
            mValuesByZoomLevel[j] = new double[mLenByZoomLevel[j]];
            mZoomFactorByZoomLevel[j] = mZoomFactorByZoomLevel[j - 1] / 2.0;
            for (int i = 0; i < mLenByZoomLevel[j]; i++)
            {
                mValuesByZoomLevel[j][i] = 0.5 * (mValuesByZoomLevel[j - 1][2 * i] + mValuesByZoomLevel[j - 1][2 * i + 1]);
            }
        }
        if (numFrames > 5000)
        {
            mZoomLevel = 3;
        }
        else if (numFrames > 1000)
        {
            mZoomLevel = 2;
        }
        else if (numFrames > 300)
        {
            mZoomLevel = 1;
        }
        else
        {
            mZoomLevel = 0;
        }

    }

    private void computeIntsForThisZoomLevel()
    {

        int halfHeight = (mHeight / 2) - 1;
        mHeightsAtThisZoomLevel = new int[mLenByZoomLevel[mZoomLevel]];
        for (int i = 0; i < mLenByZoomLevel[mZoomLevel]; i++)
        {
            mHeightsAtThisZoomLevel[i] = (int) (mValuesByZoomLevel[mZoomLevel][i] * halfHeight);
            if (mHeightsAtThisZoomLevel[i] > halfHeight * 0.5 && (i < mLenByZoomLevel[mZoomLevel] * 0.05 || i > mLenByZoomLevel[mZoomLevel] * 0.95))
            {
                mHeightsAtThisZoomLevel[i] = 0;
            }
        }
    }

    public static void saveBitmap(Bitmap bitmap, String path)
    {
        File f = new File(path);
        if (f.exists())
        {
            f.delete();
        }
        try
        {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public static class WaveInfo
    {
        public int[] mHeightsAtThisZoomLevel;
        public int max;
    }
}
