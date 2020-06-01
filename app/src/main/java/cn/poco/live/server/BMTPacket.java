package cn.poco.live.server;

/**
 * Created by Administrator on 2018/1/10.
 */

public class BMTPacket {
    public static final int PACKET_VIDEO = 1;
    public static final int PACKET_RES = 3;
    public static final int PACKET_CMD = 4;
    public static final int PACKET_KEEPALIVE = 5;

    private int mType;
    private byte[] mData;

    public BMTPacket(int type, byte[] data) {
        mType = type;
        mData = data;
    }

    public int getType() {
        return mType;
    }

    public byte[] getData() {
        return mData;
    }
}
