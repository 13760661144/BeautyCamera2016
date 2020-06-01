package cn.poco.video.encoder;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by zwq on 2016/06/22 11:25.<br/><br/>
 */
@SuppressLint("NewApi")
public class MediaAudioEncoder extends MediaEncoder {

    private static final String TAG = MediaAudioEncoder.class.getName();
    private static final boolean DEBUG = false;
    private static final String MIME_TYPE = "audio/mp4a-latm";
    private static final int SAMPLE_RATE = 44100;    // 44.1[KHz] is only setting guaranteed to be available on all devices.44100、22050、11025、4000、8000
    private static final int CHANNEL_COUNT = 1;
    private static final int CHANNEL_MODE = AudioFormat.CHANNEL_IN_MONO;//CHANNEL_IN_STEREO
    private static final int BIT_RATE = 128000;//64000  128000
    public static final int SAMPLES_PER_FRAME = 1024;    // AAC, bytes/frame/channel
    public static final int FRAMES_PER_BUFFER = 25;    // AAC, frame/buffer/sec

    private AudioThread mAudioThread = null;
    private long presentationTimeUs;

    public MediaAudioEncoder(final MediaMuxerWrapper muxer, final MediaEncoderListener listener) {
        super(muxer, listener);
        mMediaTypeStr = "audio ";
    }

    @Override
    public boolean prepare() throws IOException {
        if (mMediaCodec != null) {
            return false;
        }
        if (DEBUG) Log.d(TAG, "prepare:");
        mTrackIndex = -1;
        mMuxerStarted = mIsEOS = false;
        // prepare MediaCodec for AAC encoding of audio data from inernal mic.
//        final MediaCodecInfo audioCodecInfo = selectAudioCodec(MIME_TYPE);
//        if (audioCodecInfo == null) {
//            Log.e(TAG, "Unable to find an appropriate codec for " + MIME_TYPE);
//            return;
//        }
//        if (DEBUG) Log.i(TAG, "selected codec: " + audioCodecInfo.getName());

        final MediaFormat audioFormat = MediaFormat.createAudioFormat(MIME_TYPE, SAMPLE_RATE, CHANNEL_COUNT);
        audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        audioFormat.setInteger(MediaFormat.KEY_CHANNEL_MASK, CHANNEL_MODE);
        audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
//        /*
//        BITRATE_MODE_CQ，它表示完全不控制码率，尽最大可能保证图像质量；
//        BITRATE_MODE_CBR，它表示编码器会尽量把输出码率控制为设定值，即我们前面提到的“不为所动”；
//        BITRATE_MODE_VBR，它表示编码器会根据图像内容的复杂度（实际上是帧间变化量的大小）来动态调整输出码率，图像复杂则码率高，图像简单则码率低；
//        * */
//        audioFormat.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_VBR);
//        audioFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 16384);//1024 * 16
//        audioFormat.setLong(MediaFormat.KEY_DURATION, (long)durationInMs );

//        final int input_size = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_MODE, AudioFormat.ENCODING_PCM_16BIT);
//        audioFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, input_size);//3584

        if (DEBUG) Log.i(TAG, "format: " + audioFormat);
        mMediaCodec = MediaCodec.createEncoderByType(MIME_TYPE);
        mMediaCodec.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mMediaCodec.start();
        if (DEBUG) Log.d(TAG, "prepare finishing");
        if (mListener != null) {
            try {
                mListener.onPrepared(this);
            } catch (final Exception e) {
                e.printStackTrace();
                Log.e(TAG, "prepare:", e);
            }
        }
        return true;
    }

    @Override
    public void startRecording() {
        super.startRecording();
        // create and execute audio capturing thread using internal mic
        if (mAudioThread == null) {
            mAudioThread = new AudioThread("AudioEncodeThread");
            mAudioThread.start();
        }
    }

    @Override
    protected void release() {
        if (DEBUG) Log.d(TAG, "release");
        mAudioThread = null;
        super.release();
    }

    private static final int[] AUDIO_SOURCES = new int[]{
            MediaRecorder.AudioSource.REMOTE_SUBMIX,
            MediaRecorder.AudioSource.MIC,
            MediaRecorder.AudioSource.DEFAULT,
            MediaRecorder.AudioSource.CAMCORDER,
            MediaRecorder.AudioSource.VOICE_COMMUNICATION,
            MediaRecorder.AudioSource.VOICE_RECOGNITION,
    };

    /**
     * Thread to capture audio data from internal mic as uncompressed 16bit PCM data
     * and write them to the MediaCodec encoder
     */
    private class AudioThread extends Thread {

        public AudioThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
            try {
                final int min_buffer_size = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_MODE, AudioFormat.ENCODING_PCM_16BIT);
                int buffer_size = SAMPLES_PER_FRAME * FRAMES_PER_BUFFER;
                if (buffer_size < min_buffer_size) {
                    buffer_size = ((min_buffer_size / SAMPLES_PER_FRAME) + 1) * SAMPLES_PER_FRAME * 2;
                }
                AudioRecord audioRecord = null;
                for (final int source : AUDIO_SOURCES) {
//                    Log.i(TAG, "source:"+source+", buffer_size:"+buffer_size);
                    try {
                        audioRecord = new AudioRecord(source, SAMPLE_RATE, CHANNEL_MODE, AudioFormat.ENCODING_PCM_16BIT, buffer_size);
                        if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
                            audioRecord = null;
                        }
                    } catch (final Exception e) {
                        e.printStackTrace();
                        audioRecord = null;
                    }
                    if (audioRecord != null) break;
                }
                if (audioRecord != null) {
                    try {
                        if (mIsCapturing) {
                            if (DEBUG) Log.i(TAG, "AudioThread:start audio recording buffer_size:"+buffer_size);
                            final ByteBuffer buf = ByteBuffer.allocateDirect(SAMPLES_PER_FRAME);
                            int readBytes;
                            audioRecord.startRecording();
//                            Log.i(TAG, "start...");
                            try {
                                for (; mIsCapturing && !mRequestStop && !mIsEOS; ) {
                                    // read audio data from internal mic
//                                    Log.i(TAG, "reading...");
                                    buf.clear();
                                    readBytes = audioRecord.read(buf, SAMPLES_PER_FRAME);
                                    if (readBytes > 0) {
                                        // set audio data to encoder
                                        buf.position(readBytes);
                                        buf.flip();
                                        long timePTSUs = getPTSUs();
                                        if (timePTSUs <= prevOutputPTSUs) {
                                            prevOutputPTSUs += prevPTSUsStep;
                                        } else {
                                            prevOutputPTSUs = timePTSUs;
                                        }
                                        encode(buf, readBytes, prevOutputPTSUs);
                                        //encode(buf, readBytes, getPTSUs());
                                        frameAvailableSoon();
                                    }
                                }
                                frameAvailableSoon();
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                audioRecord.stop();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        audioRecord.release();
                    }
                } else {
                    Log.e(TAG, "failed to initialize AudioRecord");
                }
            } catch (final Exception e) {
                e.printStackTrace();
                Log.e(TAG, "AudioThread#run", e);
            }
            if (DEBUG) Log.d(TAG, "AudioThread:finished");
        }
    }

    /**
     * select the first codec that match a specific MIME type
     *
     * @param mimeType
     * @return
     */
    private static final MediaCodecInfo selectAudioCodec(final String mimeType) {
        if (DEBUG) Log.i(TAG, "selectAudioCodec:");

        MediaCodecInfo result = null;
        // get the list of available codecs
        final int numCodecs = MediaCodecList.getCodecCount();
        LOOP:
        for (int i = 0; i < numCodecs; i++) {
            final MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (!codecInfo.isEncoder()) {    // skipp decoder
                continue;
            }
            final String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (DEBUG) Log.i(TAG, "supportedType:" + codecInfo.getName() + ",MIME=" + types[j]);
                if (types[j].equalsIgnoreCase(mimeType)) {
                    if (result == null) {
                        result = codecInfo;
                        break LOOP;
                    }
                }
            }
        }
        return result;
    }

}
