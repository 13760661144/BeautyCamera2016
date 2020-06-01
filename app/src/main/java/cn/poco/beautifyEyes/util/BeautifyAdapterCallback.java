package cn.poco.beautifyEyes.util;

import android.graphics.Bitmap;

import cn.poco.beautify.BeautifyView2;

/**
 * Created by Shine on 2017/2/27.
 */

public class BeautifyAdapterCallback implements BeautifyView2.ControlCallback{

    @Override
    public void OnSelFaceIndex(int index) {

    }

    @Override
    public Bitmap MakeOutputImg(Object info, int outW, int outH) {
        return null;
    }

    @Override
    public void OnTouchAcne(float x, float y, float rw) {

    }

    @Override
    public void OnClickSlimTool(float x1, float y1, float x2, float y2, float rw) {

    }

    @Override
    public void OnResetSlimTool(float rw) {

    }

    @Override
    public void OnDragSlim(float x1, float y1, float x2, float y2, float rw) {

    }

    @Override
    public Bitmap MakeShowImg(Object info, int frW, int frH) {
        return null;
    }

    @Override
    public Bitmap MakeOutputPendant(Object info, int outW, int outH) {
        return null;
    }

    @Override
    public void OnTouchEyebrow(boolean isLeft) {

    }

    @Override
    public void OnTouchEye(boolean isLeft) {

    }

    @Override
    public void OnTouchCheek(boolean isLeft) {

    }

    @Override
    public void OnTouchLip() {

    }

    @Override
    public void OnTouchFoundation() {

    }

    @Override
    public void On3PosModify() {

    }

    @Override
    public void OnAllPosModify() {

    }

    @Override
    public void UpdateSonWin(Bitmap bmp, int x, int y) {

    }
}
