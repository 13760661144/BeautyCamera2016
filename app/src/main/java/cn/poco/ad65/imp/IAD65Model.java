package cn.poco.ad65.imp;

import android.graphics.Bitmap;

public interface IAD65Model {
    public interface ThreadCallBack
    {
        public void MakeBmpEffectFinish(Bitmap bmp);
    }
    public void setImage(Object object);

    public void changeBmpEffect(int progress);

    public Bitmap getFrameRes(int index);

    public Bitmap getCurBmp();

    public Bitmap getOrgBmp();

    public int getProgress();

    public void setThreadCallBack(ThreadCallBack callBack);

    public void clear();
}
