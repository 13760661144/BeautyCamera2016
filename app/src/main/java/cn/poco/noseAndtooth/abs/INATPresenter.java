package cn.poco.noseAndtooth.abs;


import android.graphics.Bitmap;

public interface INATPresenter {
    public String getTitle();
    public int getIconRes();
    public Bitmap getCurBmp();
    public Bitmap getOrgBmp();
    public void setImage(Object object);
    public void faceCheck();
    public void setProgress(int progress);
    public int getCurProgress();
    public int getInitProgress();
    public void sendEffectMsg();
    public void back();
    public void ok();
    public void Clear();
    public void onTitleBtn(boolean isHide);
    public void onCompareBtn();
    public void onExit();
}
