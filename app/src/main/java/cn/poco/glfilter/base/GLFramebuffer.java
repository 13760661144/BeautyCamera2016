package cn.poco.glfilter.base;

import android.opengl.GLES20;

import java.nio.Buffer;

/**
 * Created by zwq on 2016/07/26 17:33.<br/><br/>
 */
public class GLFramebuffer {

    private int mTextureNum;
    private int mWidth, mHeight;
    private SubGLFramebuffer[] mFramebufferArr;

    private int mCurrentTextureIndex = -1;
    private int mPreviousTextureIndex = -1;
    private boolean mHasBindFramebuffer;
    private boolean mHasBind;

    private static class SubGLFramebuffer {
        private int mWidth, mHeight;
        private int[] mFrameBuffers;
        private int[] mFrameBuffersTextures;
        private int[] mColorBuffers;
        private int[] mDepthBuffers;
        private int[] mStencilBuffers;

        public SubGLFramebuffer(int width, int height) {
            this(width, height, false, false, false);
        }

        public SubGLFramebuffer(int width, int height, boolean color, boolean depth, boolean stencil) {
            this(width, height, color, depth, stencil, GLES20.GL_RGBA);
        }

        public SubGLFramebuffer(int width, int height, int format) {
            this(width, height, false, false, false, format);
        }

        public SubGLFramebuffer(int width, int height, boolean color, boolean depth, boolean stencil, int format) {
            mWidth = width;
            mHeight = height;

            mFrameBuffers = new int[1];
            mFrameBuffersTextures = new int[1];
            if (color) {
                mColorBuffers = new int[1];
            }
            if (depth) {
                mDepthBuffers = new int[1];
            }
            if (stencil) {
                mStencilBuffers = new int[1];
            }
            generateBufferAndTexture(0, format, format, GLES20.GL_UNSIGNED_BYTE, null);
        }

        public int getWidth() {
            return mWidth;
        }

        public int getHeight() {
            return mHeight;
        }

        private void generateBufferAndTexture(int index, int internalFormat, int format, int type, Buffer pixels) {
            GLES20.glGenTextures(1, mFrameBuffersTextures, index);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameBuffersTextures[index]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, internalFormat, mWidth, mHeight, 0, format, type, pixels);

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            //framebuffer
            GLES20.glGenFramebuffers(1, mFrameBuffers, index);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[index]);
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, mFrameBuffersTextures[index], 0);

            if (mColorBuffers != null) {
                GLES20.glGenRenderbuffers(1, mColorBuffers, index);
                GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, mColorBuffers[index]);
                GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_RGBA4, mWidth, mHeight);
            }
            if (mDepthBuffers != null) {
                GLES20.glGenRenderbuffers(1, mDepthBuffers, 0);
                GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, mDepthBuffers[index]);
                GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, mWidth, mHeight);
            }
            if (mStencilBuffers != null) {
                GLES20.glGenRenderbuffers(1, mStencilBuffers, index);
                GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, mStencilBuffers[index]);
                GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_STENCIL_INDEX8, mWidth, mHeight);
            }

            // bind RenderBuffers to FrameBuffer object
            if (mColorBuffers != null) {
                GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_RENDERBUFFER, mColorBuffers[index]);
            }
            if (mDepthBuffers != null) {
                GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, mDepthBuffers[index]);
            }
            if (mStencilBuffers != null) {
                GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_STENCIL_ATTACHMENT, GLES20.GL_RENDERBUFFER, mStencilBuffers[index]);
            }

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
        }

        public boolean bind(boolean clear) {
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
            if (clear) {
                GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_STENCIL_BUFFER_BIT);
            }
            return true;
        }

        public void unbind() {
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        }

        public int getTextureId() {
            return mFrameBuffersTextures[0];
        }

        public void destroy() {
            if (mFrameBuffersTextures != null) {
                GLES20.glDeleteTextures(mFrameBuffersTextures.length, mFrameBuffersTextures, 0);
                mFrameBuffersTextures = null;
            }
            if (mFrameBuffers != null) {
                GLES20.glDeleteFramebuffers(mFrameBuffers.length, mFrameBuffers, 0);
                mFrameBuffers = null;
            }
        }
    }

    public GLFramebuffer(int width, int height) {
        this(1, width, height);
    }

    public GLFramebuffer(int textureNum, int width, int height) {
        this(textureNum, width, height, false, false, false);
    }

    public GLFramebuffer(int textureNum, int width, int height, boolean color, boolean depth, boolean stencil) {
        this(textureNum, width, height, color, depth, stencil, GLES20.GL_RGBA);
    }

    public GLFramebuffer(int textureNum, int width, int height, int format) {
        this(textureNum, width, height, false, false, false, format);
    }

    /**
     * @param textureNum 纹理数量
     * @param width
     * @param height
     */
    public GLFramebuffer(int textureNum, int width, int height, boolean color, boolean depth, boolean stencil, int format) {
        mTextureNum = textureNum;
        mWidth = width;
        mHeight = height;

        if (mTextureNum < 1) {
            mTextureNum = 1;
        }
        mFramebufferArr = new SubGLFramebuffer[mTextureNum];
        for (int i = 0; i < mTextureNum; i++) {
            mFramebufferArr[i] = new SubGLFramebuffer(mWidth, mHeight, color, depth, stencil, format);
        }
    }

    public int getTextureNum() {
        return mTextureNum;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public void reset() {
        mCurrentTextureIndex = -1;
        mPreviousTextureIndex = -1;
        mHasBindFramebuffer = false;
        mHasBind = true;
    }

    public int getCurrentTextureIndex() {
        return mCurrentTextureIndex;
    }

    public boolean bindByIndex(int index, boolean clear) {
        return bindByIndex(index, clear, true);
    }

    public boolean bindByIndex(int index, boolean clear, boolean reset) {
        if (index < 0) {
            index = 0;
        } else if (index >= mTextureNum) {
            index = mTextureNum - 1;
        }
        mFramebufferArr[index].bind(clear);

        if (reset) {
            mPreviousTextureIndex = mCurrentTextureIndex;
            mCurrentTextureIndex = index;
            mHasBindFramebuffer = true;
            mHasBind = true;
        }
        return true;
    }

    public boolean bindNext(boolean clear) {
        return bindByIndex((mCurrentTextureIndex + 1) % mTextureNum, clear);
    }

    public boolean bind(boolean clear) {
        return bindByIndex(0, clear);
    }

    public boolean rebind(boolean clear) {
        return bindByIndex(mCurrentTextureIndex, clear, false);
    }

    public boolean hasBindFramebuffer() {
        return mHasBindFramebuffer;
    }

    public void setHasBind(boolean bind) {
        mHasBind = bind;
    }

    public int getTextureIdByIndex(int index) {
        if (index < 0) {
            index = 0;
        } else if (index >= mTextureNum) {
            index = mTextureNum - 1;
        }
        return mFramebufferArr[index].getTextureId();
    }

    public int getTextureId() {
        return getTextureIdByIndex(0);
    }

    public int getCurrentTextureId() {
        return getTextureIdByIndex(mCurrentTextureIndex);
    }

    public int getPreviousTextureId() {
        if (mPreviousTextureIndex < 0) {
            if (mHasBind) {
                mHasBind = false;
                mPreviousTextureIndex = mCurrentTextureIndex;
            }
            return -1;
        } else {
            if (!mHasBind) {
                mPreviousTextureIndex = mCurrentTextureIndex;
            }
        }
        mHasBind = false;
        return getTextureIdByIndex(mPreviousTextureIndex);
    }

    public void destroy() {
        mHasBindFramebuffer = false;
        mHasBind = false;
        if (mFramebufferArr != null) {
            for (int i = 0; i < mFramebufferArr.length; i++) {
                mFramebufferArr[i].destroy();
                mFramebufferArr[i] = null;
            }
            mFramebufferArr = null;
        }
    }
}
