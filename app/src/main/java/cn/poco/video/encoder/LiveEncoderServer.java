package cn.poco.video.encoder;

import android.media.MediaCodec;

import java.nio.ByteBuffer;

import cn.poco.live.server.BMTServer;
import cn.poco.live.server.BMTVideo;

/**
 * Created by zwq on 2018/01/19 14:01.<br/><br/>
 * 直播助手视频编码
 */
public class LiveEncoderServer {

    private boolean mInit;
    private BMTVideo mH264Stream;
    private boolean mCanStopServer;

    public void init() {
        if (mInit) {
            return;
        }
        if (mH264Stream == null) {
            mH264Stream = new BMTVideo();
        }
        if (mH264Stream != null) {
            BMTServer.getInstance().startServer(8984);
            mInit = true;
            mCanStopServer = true;
        }
    }

    public boolean isConnected() {
        if (mInit) {
            return BMTServer.getInstance().isConnected();
        }
        return false;
    }

    public void setCanStopServer(boolean canStopServer) {
        mCanStopServer = canStopServer;
    }

    public boolean isCanStopServer() {
        return mCanStopServer;
    }

    public void setSpsPpsData(ByteBuffer sps, ByteBuffer pps) {
        if (mH264Stream != null) {
            mH264Stream.setSPSPPSData(sps, pps);
        }
    }

    public void writeSampleData(ByteBuffer buffer, MediaCodec.BufferInfo info) {
        if (mH264Stream != null) {
            mH264Stream.writeSampleData(buffer, info);
        }
    }

    public void stopServer() {
        if (mH264Stream != null && mInit) {
            mInit = false;
            BMTServer.getInstance().stopServer();
            mH264Stream = null;
        }
    }
}
