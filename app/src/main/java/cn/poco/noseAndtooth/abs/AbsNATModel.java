package cn.poco.noseAndtooth.abs;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import cn.poco.face.FaceDataV2;
import cn.poco.face.FaceLocalData;


public abstract class AbsNATModel implements INATModel {
    public static final int FACECHECK = 1;
    public static final int EFFECT = 2;
    private Bitmap m_org;
    private Bitmap m_curBmp;
    private Handler m_threadHandler;
    private Handler m_uiHandler;
    private HandlerThread m_thread;
    private Context m_context;
    private INATModel.ThreadCallBack m_cb;
    public AbsNATModel(Context context)
    {
        m_context = context;
        init();
    }

    @Override
    public void setUpdateBmpCB(ThreadCallBack cb) {
        m_cb = cb;
    }

    private void init()
    {
        m_uiHandler = new Handler();
        m_thread = new HandlerThread("nat");
        m_thread.start();
        m_threadHandler = new Handler(m_thread.getLooper())
        {
            @Override
            public void handleMessage(Message msg) {
                if(msg != null)
                {
                    switch (msg.what)
                    {
                        case FACECHECK:
                        {
                            if(msg.obj != null)
                            {
                                FaceDataV2.CheckFace(m_context, (Bitmap) msg.obj);
                            }
                            if(m_cb != null)
                            {
                                m_uiHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        m_cb.finishFaceCheck();
                                    }
                                });
                            }
                            break;
                        }
                        case EFFECT:
                        {
                            Bitmap out = null;
                            if(msg.obj != null)
                            {
                                Bitmap temp = (Bitmap) msg.obj;
                                  out = makeEffect(temp.copy(Bitmap.Config.ARGB_8888,true));

                            }
                            if(out != null)
                            {
                                m_curBmp = out;
                            }
                            if(m_cb != null)
                            {
                                final Bitmap finalOut = out;
                                m_uiHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        m_cb.updateBmp(finalOut);
                                    }
                                });
                            }
                            break;
                        }
                    }
                }
            }
        };
    }


    @Override
    public void setOriInfo(Object object) {
        if(object instanceof Bitmap)
        {
            m_org = (Bitmap) object;
        }
    }

    public abstract Bitmap makeEffect(Bitmap bmp);

    @Override
    public void makeEffectBmp() {
        if(m_threadHandler != null)
        {
            Message message = Message.obtain();
            message.what = EFFECT;
            message.obj = m_org;
            m_threadHandler.sendMessage(message);
        }
    }

    @Override
    public void facecheck() {
        Message msg = Message.obtain();
        msg.what = FACECHECK;
        msg.obj = m_org;
        m_threadHandler.sendMessage(msg);
    }

    protected FaceLocalData getFaceLocalData()
    {
        if(FaceLocalData.getInstance() == null)
        {
            return FaceLocalData.getNewInstance(FaceDataV2.RAW_POS_MULTI.length);
        }
        else
        {
            return FaceLocalData.getInstance();
        }
    }

    @Override
    public void Clear() {
        if(m_org != null)
        {
            m_org = null;
        }

        if(m_curBmp != null)
        {
            m_curBmp = null;
        }

        if(m_thread != null)
        {
            m_thread.quit();
        }

        if(FaceLocalData.getInstance() != null)
        {
            FaceLocalData.ClearData();
        }
    }

    @Override
    public Bitmap getOrgBmp() {
        return m_org;
    }

    @Override
    public Bitmap getCurBmp() {
        return m_curBmp;
    }
}
