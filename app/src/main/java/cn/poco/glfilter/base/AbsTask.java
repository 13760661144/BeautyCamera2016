package cn.poco.glfilter.base;

/**
 * Created by zwq on 2017/04/26 14:02.<br/><br/>
 */
public abstract class AbsTask implements Runnable {

    private String mTaskName;
    private Thread mThread;
    private boolean mIsRun;
    private int mState;//0:初始状态, 1:处理中, 2:处理完成

    /**
     * 耗时的任务在此方法内执行
     */
    public abstract void runOnThread();

    public abstract void executeTaskCallback();

    public AbsTask() {

    }

    public AbsTask(String taskName) {
        mTaskName = taskName;
    }

    public final void start() {
        if (!mIsRun && mThread == null && mState == 0) {
            if (mTaskName == null) {
                mTaskName = this.getClass().getSimpleName();
            }
            mThread = new Thread(this, mTaskName);
            mThread.start();
            mIsRun = true;
        }
    }

    @Override
    public void run() {
        mState = 1;
        runOnThread();
        mState = 2;
    }

    public final boolean isFinish() {
        return mState == 2;
    }

    public void clearAll() {
        mTaskName = null;
        mThread = null;
    }

}
