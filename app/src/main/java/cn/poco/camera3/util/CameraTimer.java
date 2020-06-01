package cn.poco.camera3.util;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

/**
 * @author Gxx
 *         Created by Gxx on 2017/8/29.
 *         计时器
 */

public class CameraTimer
{
    interface TimerStatus
    {
        int START = 1;
        int FINISH = 1 << 1;
        int CANCEL = 1 << 2;
        int RESTART = 1 << 3;
    }

    private HandlerThread mThread;
    private Handler mThreadHandle;

    private long mMillisInFuture;
    private long mCountDownInterval;

    private Timer mTimer;
    private TimerEventListener mEventListener;

    public CameraTimer(long millisInFuture, long countDownInterval, TimerEventListener listener)
    {
        mMillisInFuture = millisInFuture;
        mCountDownInterval = countDownInterval;
        mEventListener = listener;

        mThread = new HandlerThread("CountDownThread");
        mThread.start();

        mThreadHandle = new Handler(mThread.getLooper())
        {
            @Override
            public void handleMessage(Message msg)
            {
                if (msg != null)
                {
                    switch (msg.what)
                    {
                        case TimerStatus.START:
                        {
                            mTimer = new Timer(mMillisInFuture, mCountDownInterval);
                            mTimer.start();
                            break;
                        }

                        case TimerStatus.CANCEL:
                        {
                            if (mTimer != null)
                            {
                                mTimer.cancel();
                            }
                            break;
                        }

                        case TimerStatus.RESTART:
                        {
                            if (mTimer != null)
                            {
                                mTimer.start();
                            }
                            break;
                        }

                        case TimerStatus.FINISH:
                        {
                            if (mEventListener != null)
                            {
                                mEventListener.onFinish();
                            }
                        }
                    }
                }
            }
        };
    }

    public void start()
    {
        if (mThreadHandle != null)
        {
            mThreadHandle.sendEmptyMessage(mTimer == null ? TimerStatus.START : TimerStatus.RESTART);
        }
    }

    public void cancel()
    {
        if (mThreadHandle != null)
        {
            mThreadHandle.sendEmptyMessage(TimerStatus.CANCEL);
        }
    }

    public void clearAll()
    {
        mEventListener = null;

        if (mTimer != null)
        {
            mTimer.cancel();
            mTimer = null;
        }

        if (mThreadHandle != null)
        {
            mThreadHandle.removeMessages(TimerStatus.START);
            mThreadHandle.removeMessages(TimerStatus.CANCEL);
            mThreadHandle.removeMessages(TimerStatus.RESTART);
            mThreadHandle.removeMessages(TimerStatus.FINISH);
            mThreadHandle = null;
        }

        if (mThread != null)
        {
            mThread.interrupt();
            mThread.quit();
            mThread = null;
        }
    }

    private class Timer extends CountDownTimer
    {
        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        Timer(long millisInFuture, long countDownInterval)
        {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished)
        {
        }

        @Override
        public void onFinish()
        {
            if (mThreadHandle != null)
            {
                mThreadHandle.sendEmptyMessage(TimerStatus.FINISH);
            }
        }
    }

    public interface TimerEventListener
    {
        void onFinish();
    }
}
