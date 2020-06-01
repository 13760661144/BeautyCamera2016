package cn.poco.live.server;

import android.content.res.Resources;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2018/1/10.
 */

public class BMTResPacket extends BMTPacket {
    private Object mResFileOrId = -1;
    private WeakReference<Resources> mResource;

    public BMTResPacket() {
        super(BMTPacket.PACKET_RES, null);
    }

    public void setResource(Resources res, Object fileOrId) {
        mResource = new WeakReference<Resources>(res);
        mResFileOrId = fileOrId;
    }

    @Override
    public byte[] getData() {
        if (mResource != null && mResource.get() != null && mResFileOrId != null) {
            InputStream is = null;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            if (mResFileOrId instanceof Integer) {
                is = mResource.get().openRawResource((Integer) mResFileOrId);
            } else {
                try {
                    is = new FileInputStream((String) mResFileOrId);
                } catch (Exception e) {
                }
            }
            if (is != null) {
                byte[] buffer = new byte[10240];
                String name = null;
                if (mResFileOrId instanceof Integer) {
                    name = String.valueOf((Integer) mResFileOrId);
                } else {
                    name = (String) mResFileOrId;
                }
                try {
                    byte[] bytes = name.getBytes();
                    for (int i = 0; i < bytes.length; i++) {
                        buffer[i] = bytes[i];
                    }
                    bos.write(buffer, 0, 255);
                    while (true) {
                        int size = is.read(buffer);
                        bos.write(buffer, 0, size);
                        if (size <= 0) {
                            break;
                        }
                    }
                } catch (Exception e) {
                }
                byte[] data = bos.toByteArray();
                try {
                    is.close();
                } catch (Exception e) {
                }
                try {
                    bos.close();
                } catch (Exception e) {
                }
                return data;
            }
        }
        return null;
    }
}
