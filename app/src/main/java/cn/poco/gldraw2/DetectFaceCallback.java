package cn.poco.gldraw2;

import java.util.ArrayList;

import cn.poco.image.PocoFace;

/**
 * Created by zwq on 2017/07/26 20:49.<br/><br/>
 */

public interface DetectFaceCallback {
    void onDetectResult(ArrayList<PocoFace> faces, int viewWidth, int viewHeight);
}
