package cn.poco.camera3.cb;

import java.util.Observable;

/**
 * @author Gxx
 *      Created by Gxx on 2017/8/23.
 */

public class UIObservable extends Observable
{
    private int mLastMsg = UIObserver.MSG_UNLOCK_UI;

    public void notifyObservers(int msg)
    {
        if (mLastMsg != msg)
        {
            mLastMsg = msg;
            setChanged();
        }
        super.notifyObservers(msg);
    }

    public boolean isLock() {
        return mLastMsg == UIObserver.MSG_LOCK_UI;
    }
}
