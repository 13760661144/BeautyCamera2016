package cn.poco.live.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by Administrator on 2018/1/9.
 */

public class BMTStream {
    public static final String PROTOCOL = "BMP10";

    private ArrayList<BMTPacket> mPacketBuffer = new ArrayList<BMTPacket>();
    private ArrayList<BMTPacket> mVideoHeaderBuffer = new ArrayList<BMTPacket>();
    private ArrayList<BMTPacket> mDUIHeaderBuffer = new ArrayList<BMTPacket>();
    private ArrayList<WeakReference<BMTPacketParser>> mPacketParsers = new ArrayList<WeakReference<BMTPacketParser>>();
    private boolean mClosed = false;
    private boolean mWorking = false;
    private int mVideoBufferSize = 10;
    private int mDiscardCount = 0;

    interface BMTPacketParser
    {
        void parse(BMTPacket p);
    }

    public void addPacketParser(BMTPacketParser p)
    {
        synchronized(mPacketParsers) {
            for(int i = 0; i < mPacketParsers.size(); i++) {
                WeakReference<BMTPacketParser> r = mPacketParsers.get(i);
                if(r.get() == p) {
                    return;
                }
            }
            mPacketParsers.add(new WeakReference<BMTPacketParser>(p));
        }
    }

    public void removePacketParser(BMTPacketParser p)
    {
        synchronized(mPacketParsers) {
            for(int i = 0; i < mPacketParsers.size(); i++) {
                WeakReference<BMTPacketParser> r = mPacketParsers.get(i);
                if(r.get() == p) {
                    mPacketParsers.remove(i);
                    break;
                }
            }
        }
    }

    public boolean isWorking()
    {
        return mWorking && mClosed == false;
    }

    public void writeStream(OutputStream os) throws Exception
    {
        mWorking = true;
        boolean sentVideoHeader = false;
        try {

            if(!mClosed) {
                writeHeader(os);
                synchronized(mDUIHeaderBuffer) {
                    for(int i = 0; i < mDUIHeaderBuffer.size(); i++) {
                        BMTPacket p = mDUIHeaderBuffer.get(i);
                        if(p != null) {
                            writeData(os, p.getData(), p.getType());
                        }
                    }
                }
                synchronized(mVideoHeaderBuffer) {
                    for(int i = 0; i < mVideoHeaderBuffer.size(); i++) {
                        BMTPacket p  = mVideoHeaderBuffer.get(i);
                        if(p != null) {
                            writeData(os, p.getData(), p.getType());
                        }
                        sentVideoHeader = true;
                    }
                }
            }

            int count = 0;
            while(!mClosed) {
                BMTPacket packet = readPacket();
                if(sentVideoHeader == false)
                {
                    synchronized(mVideoHeaderBuffer) {
                        if(mVideoHeaderBuffer.size() > 0)
                        {
                            for(int i = 0; i < mVideoHeaderBuffer.size(); i++) {
                                BMTPacket p  = mVideoHeaderBuffer.get(i);
                                if(p != null) {
                                    writeData(os, p.getData(), p.getType());
                                }
                                sentVideoHeader = true;
                            }
                        }
                    }
                }
                if(packet != null) {
                    writeData(os, packet.getData(), packet.getType());
                } else {
                    Thread.sleep(1);
                    count++;
                    if(count == 1000)
                    {
                        count = 0;
                        packet = new BMTPacket(BMTPacket.PACKET_KEEPALIVE, new byte[0]);
                        writeData(os, packet.getData(), packet.getType());
                    }
                }
            }
            mWorking = false;
        }
        catch(Exception e) {
            mWorking = false;
            throw e;
        }
    }

    public int getDiscardCount()
    {
        return mDiscardCount;
    }

    public void readStream(InputStream is) throws Exception
    {
        try {
            if(!mClosed) {
                readHeader(is);
            }
            while(!mClosed) {
                BMTPacket packet = readData(is);
                if(packet != null) {
                    ArrayList<WeakReference<BMTPacketParser>> list = new ArrayList<WeakReference<BMTPacketParser>>();
                    synchronized(mPacketParsers) {
                        list.addAll(mPacketParsers);
                    }
                    for(int i = 0; i < list.size(); i++) {
                        BMTPacketParser p = list.get(i).get();
                        if(p != null) {
                            p.parse(packet);
                        }
                    }
                } else {
                    break;
                }
            }
        }
        catch(Exception e) {
            throw e;
        }
    }

    public boolean handShake(InputStream is)  throws Exception
    {
        byte[] buffer = new byte[5];
        is.read(buffer);
        String who = new String(buffer);
        if(who.equals(PROTOCOL))
        {
            return true;
        }
        return false;
    }

    public void setVideoHeaderPacket(ArrayList<BMTPacket> packets)
    {
        synchronized(mVideoHeaderBuffer) {
            mVideoHeaderBuffer.clear();
            mVideoHeaderBuffer.addAll(packets);
        }
    }

    public void setDUIHeaderPacket(ArrayList<BMTPacket> packets)
    {
        synchronized(mDUIHeaderBuffer) {
            mDUIHeaderBuffer.clear();
            mDUIHeaderBuffer.addAll(packets);
        }
    }

    public void addPacket(int type, byte[] bytes)
    {
        BMTPacket p = new BMTPacket(type, bytes);
        addPacket(p);
    }

    public void addPacket(BMTPacket p)
    {
        if(p.getType() == BMTPacket.PACKET_VIDEO)
        {
            addVideoPicket(p);
            return;
        }

        synchronized(mPacketBuffer) {
            mPacketBuffer.add(p);
        }
    }

    public void clearVideoBuffer()
    {
        synchronized(mPacketBuffer) {
            for(int i = 0; i < mPacketBuffer.size(); i++) {
                BMTPacket p = mPacketBuffer.get(i);
                if(p.getType() == BMTPacket.PACKET_VIDEO)
                {
                    mPacketBuffer.remove(i);
                    i--;
                }
            }
        }
    }

    public void close()
    {
        mClosed = true;
    }

    private void readHeader(InputStream is)  throws Exception
    {
        byte[] buffer = new byte[4];
        is.read(buffer);
        int size = byteToInt(buffer);
        if(size > 0)
        {
            int readSize = 0;
            buffer = new byte[10240];
            while(readSize < size)
            {
                int s = is.read(buffer);
                if(s > 0)
                {
                    readSize += s;
                }
                else
                {
                    break;
                }
            }
        }
    }

    private BMTPacket readData(InputStream is)  throws Exception
    {
        byte[] buffer = new byte[4];
        is.read(buffer);
        int type = byteToInt(buffer);
        is.read(buffer);
        int size = byteToInt(buffer);
        int readSize = 0;
        byte[] data = null;
        if(size < 2*1024*1024)
        {
            data = new byte[size];
        }
        if(data != null) {
            buffer = new byte[10240];
            while(readSize < size)
            {
                int s = is.read(buffer, 0, size-readSize);
                if(s > 0)
                {
                    if(data != null)
                    {
                        System.arraycopy(buffer, 0, data, readSize, s);
                    }
                    readSize += s;
                }
                else
                {
                    break;
                }
            }
            BMTPacket packet = new BMTPacket(type, data);
            return packet;
        }
        return null;
    }

    private void writeHeader(OutputStream os) throws Exception
    {
        String header = "";
        byte[] headerData = header.getBytes();
        os.write(PROTOCOL.getBytes());
        os.write(intToByte(headerData.length));
        os.write(headerData);
    }

    private void writeData(OutputStream os, byte[] data, int type) throws Exception
    {
        if(data != null) {
            os.write(intToByte(type));//类型
            os.write(intToByte(data.length));//数据块大小
            os.write(data);//数据
        }
    }

    private BMTPacket readPacket()
    {
        synchronized(mPacketBuffer) {
            if(mPacketBuffer.size() > 0) {
                BMTPacket p = mPacketBuffer.get(0);
                mPacketBuffer.remove(0);
                return p;
            }
        }
        return null;
    }

    private void addVideoPicket(BMTPacket packet)
    {
        synchronized(mPacketBuffer) {
            int count = 0;
            for(int i = 0; i < mPacketBuffer.size(); i++) {
                BMTPacket p = mPacketBuffer.get(i);
                if(p.getType() == BMTPacket.PACKET_VIDEO)
                {
                    count++;
                }
            }
            if(count > mVideoBufferSize)
            {
                for(int i = 0; i < mPacketBuffer.size(); i++) {
                    BMTPacket p = mPacketBuffer.get(i);
                    if(p.getType() == BMTPacket.PACKET_VIDEO)
                    {
                        mPacketBuffer.remove(i);
                        mDiscardCount++;
                        break;
                    }
                }
            }
            mPacketBuffer.add(packet);
        }
    }

    private byte[] intToByte(int n)
    {
        byte[] t4 = new byte[4];
        t4[0] = (byte) (n&0x000000ff);
        t4[1] = (byte) ((n>>8)&0x000000ff);
        t4[2] = (byte) ((n>>16)&0x000000ff);
        t4[3] = (byte) ((n>>24)&0x000000ff);
        return t4;
    }

    private int byteToInt(byte[] b){
        if(b.length >= 4)
        {
            int i = (((int)b[0]) & 0xff)
                    |((((int)b[1]) << 8) & 0xff00)
                    |((((int)b[2]) << 16) & 0xff0000)
                    |((((int)b[3]) << 24) & 0xff000000);
            return i;
        }
        else if(b.length >= 2)
        {
            int i = (((int)b[0]) & 0xff)
                    |((((int)b[1]) << 8) & 0xff00);
            return i;
        }
        else
        {
            int i = (((int)b[0]) & 0xff);
            return i;
        }
    }
}
