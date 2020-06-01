package cn.poco.dynamicSticker.v2;

/**
 * Created by zwq on 2017/09/28 15:45.<br/><br/>
 */

public class StickerSubResDesc {

    private String type;//名称  //bone_left_eye、bone_right_eye、bone_mouth
    private int a;//最小幅度
    private int b;//最大幅度

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }
}
