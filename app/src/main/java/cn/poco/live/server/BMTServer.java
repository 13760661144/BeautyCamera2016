package cn.poco.live.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/12/20.
 */

public class BMTServer {
    private int mPort;
    private boolean mClosed = false;
    private boolean mRunning = false;
    private BMTStream mBMTStream;
    private Socket mClient = null;
    private ServerSocket mServerSocket = null;
    private ArrayList<OnConnectListener> mOnConnectListeners = new ArrayList<OnConnectListener>();
    private ArrayList<OnStreamChangeListener> mOnStreamChangeListeners = new ArrayList<OnStreamChangeListener>();
    private static BMTServer sBMTServer = null;

    public interface OnConnectListener
    {
        void onConnected();
        void onDisconnected();
    }

    public interface OnStreamChangeListener
    {
        void onStreamCreated(BMTStream stream);
        void onStreamRelease(BMTStream stream);
    }

    private BMTServer()
    {
    }

    public static BMTServer getInstance()
    {
        if(sBMTServer == null)
        {
            sBMTServer = new BMTServer();
        }
        return sBMTServer;
    }

    public void startServer(int port)
    {
        mClosed = false;
        if(mRunning == false) {
            mRunning = true;
            mPort = port;
            new Thread(mSocketConnection).start();
        }
    }

    public boolean isConnected()
    {
        return mClient != null;
    }

    public BMTStream getStream()
    {
        return getStream(false);
    }

    public void addOnConnectListener(OnConnectListener l)
    {
        if(mOnStreamChangeListeners.contains(l) == false)
        {
            mOnConnectListeners.add(l);
        }
    }

    public void removeOnConnectListener(OnConnectListener l)
    {
        mOnConnectListeners.remove(l);
    }

    public void addOnStreamChangeListener(OnStreamChangeListener l)
    {
        if(mOnStreamChangeListeners.contains(l) == false)
        {
            mOnStreamChangeListeners.add(l);
        }
    }

    public void removeOnStreamChangeListener(OnStreamChangeListener l)
    {
        mOnStreamChangeListeners.remove(l);
    }

    public void stopServer() {
        mClosed = true;
        releaseStream();
        try {
            if(mServerSocket != null) {
                mServerSocket.close();
                mServerSocket = null;
            }
            if(mClient != null) {
                mClient.close();
                mClient = null;
            }
        } catch(Exception e) {
        }
        mRunning = false;
    }

    private Runnable mSocketConnection = new Runnable() {
        @Override
        public void run() {
            if(mClosed){
                mRunning = false;
                return;
            }

            ServerSocket server = null;
            try {
                if(mServerSocket != null) {
                    mServerSocket.close();
                    mServerSocket = null;
                }
                if(mClient != null) {
                    mClient.close();
                    mClient = null;
                }
                server = new ServerSocket();
                mServerSocket = server;
                InetSocketAddress address = new InetSocketAddress(mPort);
                server.bind(address);
                while(!mClosed) {
                    handleSocket(server.accept());
                    Thread.sleep(1);
                }
            } catch(Exception err) {
                err.printStackTrace();
            }
            mRunning = false;
        }
    };

    private void handleSocket(final Socket socket)
    {
        if(mClient != null)
        {
            try {
                mClient.close();
                mClient = null;
            }
            catch(Exception e)
            {}
        }
        mClient = socket;

        new Thread(new Runnable() {
            @Override
            public void run() {
                handleSession(socket);
            }
        }).start();
    }

    private void onConnected()
    {
        ArrayList<OnConnectListener> list = new ArrayList<OnConnectListener>();
        synchronized(mOnConnectListeners)
        {
            list.addAll(mOnConnectListeners);
        }
        for(OnConnectListener l : list)
        {
            l.onConnected();
        }
    }

    private void onDisconnected()
    {
        ArrayList<OnConnectListener> list = new ArrayList<OnConnectListener>();
        synchronized(mOnConnectListeners)
        {
            list.addAll(mOnConnectListeners);
        }
        for(OnConnectListener l : list)
        {
            l.onDisconnected();
        }
    }

    private void onStreamCreated(BMTStream stream)
    {
        ArrayList<OnStreamChangeListener> list = new ArrayList<OnStreamChangeListener>();
        synchronized(mOnStreamChangeListeners)
        {
            list.addAll(mOnStreamChangeListeners);
        }
        for(OnStreamChangeListener l : list)
        {
            l.onStreamCreated(stream);
        }
    }

    private void onStreamRelease(BMTStream stream)
    {
        ArrayList<OnStreamChangeListener> list = new ArrayList<OnStreamChangeListener>();
        synchronized(mOnStreamChangeListeners)
        {
            list.addAll(mOnStreamChangeListeners);
        }
        for(OnStreamChangeListener l : list)
        {
            l.onStreamRelease(stream);
        }
    }

    private void handleSession(Socket socket)
    {
        boolean notifiedConnected = false;
        OutputStream os = null;
        InputStream is = null;
        BMTStream stream = getStream(true);
        stream.clearVideoBuffer();
        try {
            os = socket.getOutputStream();
            is = socket.getInputStream();
            if(stream.handShake(is)) {
                notifiedConnected = true;
                onConnected();
                final InputStream finalIs = is;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        handleInput(finalIs);
                    }
                }).start();
                stream.writeStream(os);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        try {
            if(os != null){
                os.close();
            }
            if(is != null){
                is.close();
            }
            socket.close();
            if(mClient == socket){
                mClient = null;
            }
        }
        catch(Exception e)
        {}

        if(notifiedConnected)
        {
            onDisconnected();
        }
    }

    private void handleInput(InputStream is)
    {
        BMTStream stream = getStream();
        try {
            stream.readStream(is);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private BMTStream getStream(boolean newStream)
    {
        if(mBMTStream == null || newStream) {
            releaseStream();
            mBMTStream = new BMTStream();
            onStreamCreated(mBMTStream);
        }
        return mBMTStream;
    }

    private void releaseStream()
    {
        if(mBMTStream != null) {
            mBMTStream.close();
            onStreamRelease(mBMTStream);
            mBMTStream = null;
        }
    }
}