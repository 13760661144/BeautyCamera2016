package cn.poco.glfilter.composite;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * Created by zwq on 2017/04/25 18:33.<br/><br/>
 */
public class CompositeData {

    public int mMaskTextureId;
    public int mCompositeMode;
    public float mAlpha;
    public FloatBuffer mTextureBuffer;
    public int mElementsCount = -1;
    public ByteBuffer mIndexBuffer;

    /**
     * @param maskTextureId 光效或暗角纹理id
     * @param compositeMode 混合模式
     * @param alpha         透明度
     * @param textureBuffer 纹理顶点数据
     * @param elementsCount 顶点数量（用于glDrawElements时有效）
     * @param indexBuffer   顶点下标（用于glDrawElements时有效）
     */
    public void setData(int maskTextureId, int compositeMode, float alpha, FloatBuffer textureBuffer, int elementsCount, ByteBuffer indexBuffer) {
        mMaskTextureId = maskTextureId;
        mCompositeMode = compositeMode;
        mAlpha = alpha;
        mTextureBuffer = textureBuffer;
        mElementsCount = elementsCount;
        mIndexBuffer = indexBuffer;
    }

    public void release() {
        mTextureBuffer = null;
        mIndexBuffer = null;
    }
}
