package cn.poco.live.server;

import android.media.MediaCodec;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/12/1.
 */

public class BMTVideo {
    public static final int PACKET_VIDEO = 1;

    private byte[] mMediaHeader;
    private byte[] mKeyFrame;

    public void setSPSPPSData(ByteBuffer sps, ByteBuffer pps)
    {
        if(sps == null || pps == null)
        {
            return;
        }
        byte[] spsData = new byte[sps.limit()];
        sps.get(spsData);
        sps.clear();
        byte[] ppsData = new byte[pps.limit()];
        pps.get(ppsData);
        pps.clear();

        int size = spsData.length + ppsData.length;
        byte[] data = new byte[size];
        System.arraycopy(spsData, 0, data, 0, spsData.length);
        System.arraycopy(ppsData, 0, data, spsData.length, ppsData.length);
        setSPSPPSData(data);
    }

    public void setSPSPPSData(byte[] header)
    {
        mMediaHeader = header;
        updateHeader();
    }

    public void writeSampleData(ByteBuffer buffer, MediaCodec.BufferInfo info)
    {
        byte[] data = new byte[info.size];
        buffer.get(data);
        buffer.clear();
        writeSampleData(data);
    }

    public void writeSampleData(byte[] data)
    {
        if(data[0] == 0 && data[1] == 0 && data[2] == 0 && data[3] == 1 && (data[4]&0x1f) == 5)
        {
            mKeyFrame = data;
            updateHeader();
        }
        BMTServer.getInstance().getStream().addPacket(PACKET_VIDEO, data);
    }

    private void updateHeader()
    {
        ArrayList<BMTPacket> packets = new ArrayList<BMTPacket>();
        if(mMediaHeader != null) {
            packets.add(new BMTPacket(PACKET_VIDEO, mMediaHeader));
        }
        if(mKeyFrame != null)
        {
            packets.add(new BMTPacket(PACKET_VIDEO, mKeyFrame));
        }
        BMTServer.getInstance().getStream().setVideoHeaderPacket(packets);
    }
}

