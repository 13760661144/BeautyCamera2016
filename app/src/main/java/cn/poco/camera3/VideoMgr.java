package cn.poco.camera3;

import android.text.TextUtils;

import java.util.ArrayList;

import cn.poco.utils.FileUtil;

/**
 * Created by zwq on 2017/08/25 17:17.<br/><br/>
 */

public class VideoMgr {

    public static class SubVideo {
        public String mPath;
        public long mDuration;
    }

    private long mTargetDuration;
    private ArrayList<SubVideo> mVideoList;
    private long mRecordDuration;
    private int mVideoOrientation = -1;

    private ArrayList<Integer> mVideoSumProgressArr;
    private ArrayList<Integer> mVideoProgressAllAngleArr;
    private int mVideoProgressSum;
    private boolean mProcessLastSecond = false;

    public ArrayList<Integer> getVideoProgressSumArr() {
        return mVideoSumProgressArr;
    }

    public ArrayList<Integer> getVideoProgressAllAngleArr() {
        return mVideoProgressAllAngleArr;
    }

    public int getVideoProgressSum() {
        return mVideoProgressSum;
    }

    public void calculateProgressAngle() {
        if (mVideoList != null && !mVideoList.isEmpty()) {

            int space = mTargetDuration == 10 * 1000 ? 2 : 1;

            int size = mVideoList.size();
            if (mVideoSumProgressArr == null) {
                mVideoSumProgressArr = new ArrayList<>();
            } else {
                mVideoSumProgressArr.clear();
            }
            if (mVideoProgressAllAngleArr == null) {
                mVideoProgressAllAngleArr = new ArrayList<>();
            } else {
                mVideoProgressAllAngleArr.clear();
            }
            int startAngle = -90;
            int angle = 0;
            mVideoProgressSum = 0;
            mVideoProgressAllAngleArr.add(startAngle);
            if (size == 1) {
                angle = Math.round(mRecordDuration * 1.0f / mTargetDuration * 360);
                if (mRecordDuration < mTargetDuration - 500) {
                    mVideoSumProgressArr.add(angle - space);
                    startAngle += angle;
                    mVideoProgressAllAngleArr.add(startAngle);
                } else {
                    mVideoSumProgressArr.add(angle);
                }
                mVideoProgressSum += angle;

            } else if (size > 1) {
                SubVideo subVideo = null;
                long dur = 0;
                int allAngle = 0;
                for (int i = 0; i < size; i++) {
                    subVideo = mVideoList.get(i);
                    if (subVideo == null) continue;
                    dur = subVideo.mDuration;

                    if (i == size - 1) {
                        if (mRecordDuration > mTargetDuration - 5
                                || (mProcessLastSecond && mTargetDuration - mRecordDuration < 1000)) {
                            dur = mTargetDuration - (mRecordDuration - dur);
                        }
                    }
                    angle = Math.round(dur * 1.0f / mTargetDuration * 360);
                    allAngle += angle;
                    if (i == size - 1) {
                        if (allAngle > 350) {
                            angle = 360 - (allAngle - angle);
                        }
                    }
                    startAngle += angle;
                    mVideoSumProgressArr.add(angle - space);
                    mVideoProgressAllAngleArr.add(startAngle);
                    mVideoProgressSum += angle;
                }

            }
        }
    }

    /**
     * 处理最后一秒的状态
     * @param process
     */
    public void setProcessLastSecond(boolean process) {
        mProcessLastSecond = process;
    }

    public boolean processLaseSecond() {
        return mProcessLastSecond;
    }

    /**
     * @param time 毫秒
     */
    public void setTargetDuration(long time) {
        mTargetDuration = time;
    }

    public long getTargetDuration() {
        return mTargetDuration;
    }

    public void setVideoOrientation(int orientation) {
        if (mVideoOrientation == -1 || mVideoList == null || mVideoList.isEmpty()) {
            mVideoOrientation = orientation;
        }
    }

    public int getVideoOrientation() {
        return mVideoOrientation == -1 ? 0 : mVideoOrientation;
    }

    public ArrayList<SubVideo> getVideoList() {
        return mVideoList;
    }

    public SubVideo getSubVideo(int index) {
        if (mVideoList != null && index >= 0 && index < mVideoList.size()) {
            return mVideoList.get(index);
        }
        return null;
    }

    public int getVideoNum() {
        if (mVideoList != null) {
            return mVideoList.size();
        }
        return 0;
    }

    public long getRecordDuration() {
        return mRecordDuration;
    }

    public String getDurationStr() {
        if (mRecordDuration > 700 && mRecordDuration < 1000) {
            return getDurationStr(1000);
        }
        return getDurationStr(mRecordDuration);
    }

    public String getDurationStr(long duration) {
        if (duration > 10) {
            if (duration > mTargetDuration) {
                duration = mTargetDuration;
            } else if (duration > mTargetDuration - 5) {
                duration = mTargetDuration;
            }
            long millSec = (duration % 1000) / 10;
            long sec = duration / 1000 % 60;
            long min = duration / 60000;
            if (duration > 950 && duration < 1000) {
                millSec = 0;
                sec = 1;
            }
            return getTimeStr(min, "'", false, false) + getTimeStr(sec, "''", mTargetDuration > 60000 && (min > 0 || sec >= 10), min > 0) + getTimeStr(millSec, "", true, sec > 0);
        }
        return null;
    }

    private String getTimeStr(long time, String symbol, boolean align, boolean alignZero) {
        if (time > 0 || alignZero) {
            if (align && time < 10) {
                return "0" + time + symbol;
            }
            return time + symbol;
        }
        return "";
    }

    public boolean isRecordFinish() {
        if (mTargetDuration != 0 && (mRecordDuration >= mTargetDuration - 300)) {
            return true;
        }
        return false;
    }

    public void add(SubVideo subVideo) {
        if (subVideo == null || TextUtils.isEmpty(subVideo.mPath) || subVideo.mDuration == 0)
            return;
        if (mVideoList == null) {
            mVideoList = new ArrayList<>();
        }
        synchronized (mVideoList) {
            mVideoList.add(subVideo);
            mRecordDuration += subVideo.mDuration;
        }
    }

    public long remove() {
        if (mVideoList == null || mVideoList.isEmpty()) return 0;

        synchronized (mVideoList) {
            int videoNum = mVideoList.size();
            if (videoNum > 0) {
                SubVideo subVideo = mVideoList.remove(videoNum - 1);
                if (subVideo != null) {
                    FileUtil.deleteSDFile(subVideo.mPath);
                    mRecordDuration -= subVideo.mDuration;
                }
                if (mVideoList.isEmpty()) {
                    mVideoOrientation = -1;
                }
            }
        }
        return mRecordDuration;
    }

    public long removeIndex(int index) {
        if (mVideoList == null || mVideoList.isEmpty()) return 0;

        synchronized (mVideoList) {
            int videoNum = mVideoList.size();
            if (videoNum > 0 && videoNum > index && index >= 0) {
                SubVideo subVideo = mVideoList.remove(index);
                if (subVideo != null) {
                    FileUtil.deleteSDFile(subVideo.mPath);
                    mRecordDuration -= subVideo.mDuration;
                }
                if (mVideoList.isEmpty()) {
                    mVideoOrientation = -1;
                }
            }
        }
        return mRecordDuration;
    }

    public void clearAll() {
        if (mVideoList == null || mVideoList.isEmpty()) return;
        for (SubVideo subVideo : mVideoList) {
            if (subVideo != null) {
                FileUtil.deleteSDFile(subVideo.mPath);
            }
        }
        mVideoList.clear();
        mVideoList = null;
        mRecordDuration = 0;
        mVideoOrientation = -1;
    }
}
