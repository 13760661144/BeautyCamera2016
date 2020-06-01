package cn.poco.video.decoder;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by: fwc
 * Date: 2017/4/24
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class AudioDecoderCore
{
    private static final String TAG = "AudioDecoderCore";

    private static final int TIMEOUT_USEC = 10000;

    private String mSource;

    private String mOutputPath;

    private MediaExtractor mMediaExtractor;
    private MediaCodec mDecoder;

    private boolean mPrepare = false;

    private int[] mOutputParams;

    public AudioDecoderCore(String source, String outputPath, @Nullable int[] outputParams)
    {
        mSource = source;
        mOutputPath = outputPath;

        mOutputParams = outputParams;
    }

    public boolean prepare() throws IOException
    {
        mMediaExtractor = new MediaExtractor();
        mMediaExtractor.setDataSource(mSource);

        int trackIndex = -1;
        for (int i = 0; i < mMediaExtractor.getTrackCount(); i++)
        {
            MediaFormat format = mMediaExtractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("audio/"))
            {
                trackIndex = i;
                break;
            }
        }

        if (trackIndex < 0)
        {
            return false;
        }

        MediaFormat mediaFormat = mMediaExtractor.getTrackFormat(trackIndex);

        int sampleRate = mediaFormat.containsKey(MediaFormat.KEY_SAMPLE_RATE) ?
                mediaFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE) : 44100;
        int channelCount = mediaFormat.containsKey(MediaFormat.KEY_CHANNEL_COUNT) ?
                mediaFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT) : 1;
        long duration = mediaFormat.containsKey(MediaFormat.KEY_DURATION) ? mediaFormat.getLong
                (MediaFormat.KEY_DURATION) : 0;
        String mime = mediaFormat.containsKey(MediaFormat.KEY_MIME) ? mediaFormat.getString(MediaFormat.KEY_MIME) : "";
        Log.d(TAG, "Track info: mime:" + mime + " 采样率sampleRate:" + sampleRate + " channels:" + channelCount + " duration:" + duration);

        if (mOutputParams != null)
        {
            mOutputParams[0] = sampleRate;
            mOutputParams[1] = channelCount;
        }

        mMediaExtractor.selectTrack(trackIndex);

        String mediaMime = mediaFormat.getString(MediaFormat.KEY_MIME);
        mDecoder = MediaCodec.createDecoderByType(mediaMime);
        mDecoder.configure(mediaFormat, null, null, 0);
        mDecoder.start();

        mPrepare = true;

        return true;
    }

    public void start()
    {

        if (!mPrepare)
        {
            throw new RuntimeException("AudioDecoder is not prepare");
        }

        FileOutputStream fout = null;

        ByteBuffer[] codecInputBuffers = mDecoder.getInputBuffers();
        ByteBuffer[] codecOutputBuffers = mDecoder.getOutputBuffers();

        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();

        boolean inputDone = false;
        boolean outputDone = false;

        int totalRawSize = 0;

        try
        {

            fout = new FileOutputStream(mOutputPath);

            while (!outputDone)
            {

                if (!inputDone)
                {
                    int inputBufIndex = mDecoder.dequeueInputBuffer(TIMEOUT_USEC);
                    if (inputBufIndex >= 0)
                    {
                        ByteBuffer dstBuf = codecInputBuffers[inputBufIndex];
                        int sampleSize = mMediaExtractor.readSampleData(dstBuf, 0);

                        if (sampleSize < 0)
                        {
                            Log.i(TAG, "saw input EOS.");
                            inputDone = true;
                            mDecoder.queueInputBuffer(inputBufIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        }
                        else
                        {
                            long presentationTimeUs = mMediaExtractor.getSampleTime();
                            mDecoder.queueInputBuffer(inputBufIndex, 0, sampleSize, presentationTimeUs, 0);
                            mMediaExtractor.advance();
                        }
                    }
                }

                int decoderStatus = mDecoder.dequeueOutputBuffer(info, TIMEOUT_USEC);

                if (decoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER)
                {
                    // no output available yet
                    Log.d(TAG, "no output from decoder available");
                }
                else if (decoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED)
                {
                    codecOutputBuffers = mDecoder.getOutputBuffers();
                    Log.i(TAG, "output buffers have changed.");
                }
                else if (decoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED)
                {
                    MediaFormat newFormat = mDecoder.getOutputFormat();
                    Log.i(TAG, "output format has changed to " + newFormat);
                }
                else if (decoderStatus < 0)
                {
                    throw new RuntimeException(
                            "unexpected result from decoder.dequeueOutputBuffer: " +
                                    decoderStatus);
                }
                else
                { // decoderStatus >= 0

                    // Simply ignore codec config buffers.
                    if ((info.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0)
                    {
                        Log.i(TAG, "audio decoder: codec config buffer");
                        info.size = 0;
                    }

                    if (info.size != 0)
                    {

                        ByteBuffer outBuf = codecOutputBuffers[decoderStatus];

                        outBuf.position(info.offset);
                        outBuf.limit(info.offset + info.size);
                        byte[] data = new byte[info.size];
                        outBuf.get(data);
                        totalRawSize += data.length;
                        fout.write(data);
                    }

                    mDecoder.releaseOutputBuffer(decoderStatus, false);

                    if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0)
                    {
                        Log.i(TAG, "saw output EOS.");
                        outputDone = true;
                    }
                }
            }

            Log.d(TAG, "totalRawSize: " + totalRawSize);

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (fout != null)
            {
                try
                {
                    fout.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public void release()
    {
        if (mDecoder != null)
        {
            mDecoder.stop();
            mDecoder.release();
            mDecoder = null;
        }

        if (mMediaExtractor != null)
        {
            mMediaExtractor.release();
            mMediaExtractor = null;
        }
    }
}
