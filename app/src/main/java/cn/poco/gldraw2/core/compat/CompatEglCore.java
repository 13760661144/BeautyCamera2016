/*
 * Copyright 2013 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.poco.gldraw2.core.compat;

import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLExt;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

import cn.poco.gldraw2.core.EglCoreAbs;

/**
 * Core EGL state (display, context, config).
 * <p>
 * The EGLContext must only be attached to one thread at a time.  This class is not thread-safe.
 */
public class CompatEglCore extends EglCoreAbs<EGLContext, EGLConfig, EGLSurface> {
    private static final String TAG = "CompatEglCore";

    private int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

    /**
     * Constructor flag: ask for GLES3, fall back to GLES2 if not available.  Without this
     * flag, GLES2 is used.
     */
    public static final int FLAG_TRY_GLES3 = 0x02;

    private EGL10 mEgl;
    private EGLDisplay mEGLDisplay = EGL10.EGL_NO_DISPLAY;
    private EGLContext mEGLContext = EGL10.EGL_NO_CONTEXT;
    private EGLConfig mEGLConfig = null;
    private int mGlVersion = -1;

    private EGLSurface mEGLSurface;

    /**
     * Prepares EGL display and context.
     * <p>
     * Equivalent to EglCore(null, 0).
     */
    public CompatEglCore() {
        this(null, 0);
    }

    /**
     * Prepares EGL display and context.
     * <p>
     *
     * @param sharedContext The context to share, or null if sharing is not desired.
     * @param flags         Configuration bit flags, e.g. FLAG_TRY_GLES3.
     */
    public CompatEglCore(EGLContext sharedContext, int flags) {
        if (mEGLDisplay != EGL10.EGL_NO_DISPLAY) {
            throw new RuntimeException("EGL already set up");
        }

        if (sharedContext == null) {
            sharedContext = EGL10.EGL_NO_CONTEXT;
        }
        mEgl = (EGL10) EGLContext.getEGL();
        if (mEgl == null) {
            throw new RuntimeException("init EGL10 Object fail");
        }

        mEGLDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        if (mEGLDisplay == EGL10.EGL_NO_DISPLAY) {
            throw new RuntimeException("unable to get EGL10 display");
        }

        int[] version = new int[2];
        if (!mEgl.eglInitialize(mEGLDisplay, version)) {
            mEGLDisplay = null;
            throw new RuntimeException("unable to initialize EGL10");
        }

        // Try to get a GLES3 context, if requested.
        if ((flags & FLAG_TRY_GLES3) != 0) {
            //Log.d(TAG, "Trying GLES 3");
            EGLConfig config = getConfig(flags, 3);
            if (config != null) {
                int[] attrib3_list = {
                        EGL_CONTEXT_CLIENT_VERSION, 3,
                        EGL10.EGL_NONE
                };
                EGLContext context = mEgl.eglCreateContext(mEGLDisplay, config, sharedContext, attrib3_list);

                if (mEgl.eglGetError() == EGL10.EGL_SUCCESS) {
                    //Log.d(TAG, "Got GLES 3 config");
                    mEGLConfig = config;
                    mEGLContext = context;
                    mGlVersion = 3;
                }
            }
        }
        if (mEGLContext == EGL10.EGL_NO_CONTEXT) {  // GLES 2 only, or GLES 3 attempt failed
            //Log.d(TAG, "Trying GLES 2");
            EGLConfig config = getConfig(flags, 2);
            if (config == null) {
                throw new RuntimeException("Unable to find a suitable EGLConfig");
            }
            int[] attrib2_list = {
                    EGL_CONTEXT_CLIENT_VERSION, 2,
                    EGL10.EGL_NONE
            };
            EGLContext context = mEgl.eglCreateContext(mEGLDisplay, config, sharedContext, attrib2_list);
            checkEglError("eglCreateContext");
            mEGLConfig = config;
            mEGLContext = context;
            mGlVersion = 2;
        }

        // Confirm with query.
        int[] values = new int[1];
        mEgl.eglQueryContext(mEGLDisplay, mEGLContext, EGL_CONTEXT_CLIENT_VERSION, values);
        //Log.d(TAG, "EGLContext created, client version " + values[0]);
    }

    /**
     * Finds a suitable EGLConfig.
     *
     * @param flags   Bit flags from constructor.
     * @param version Must be 2 or 3.
     */
    private EGLConfig getConfig(int flags, int version) {
        int renderableType = EGL14.EGL_OPENGL_ES2_BIT;
        if (version >= 3) {
            renderableType |= EGLExt.EGL_OPENGL_ES3_BIT_KHR;
        }

        // The actual surface is generally RGBA or RGBX, so situationally omitting alpha
        // doesn't really help.  It can also lead to a huge performance hit on glReadPixels()
        // when reading into a GL_RGBA buffer.

        int[] attribList = {
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_ALPHA_SIZE, 8,
                //EGL10.EGL_DEPTH_SIZE, 16,
                //EGL10.EGL_STENCIL_SIZE, 8,
                EGL10.EGL_RENDERABLE_TYPE, renderableType,
                EGL10.EGL_NONE, 0,      // placeholder for recordable [@-3]
                EGL10.EGL_NONE
        };

        int[] numConfigs = new int[1];
        EGLConfig[] configs = new EGLConfig[3];

        if (!mEgl.eglChooseConfig(mEGLDisplay, attribList, configs, 1, numConfigs)) {
            Log.w(TAG, "unable to find RGB8888 / " + version + " EGLConfig, trying lower setting now...");

            int[] mMinimumSpec = {
                    EGL10.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                    //EGL10.EGL_SURFACE_TYPE, EGL10.EGL_WINDOW_BIT,
                    //EGL10.EGL_TRANSPARENT_TYPE, EGL10.EGL_NONE,
                    EGL10.EGL_NONE
            };

            mEgl.eglChooseConfig(mEGLDisplay, mMinimumSpec, configs, 3, numConfigs);
            attribList = chooseConfig(configs, flags, renderableType);
            if (!mEgl.eglChooseConfig(mEGLDisplay, attribList, configs, 1, numConfigs)) {
                Log.w(TAG, "Not any EGLConfig available.");
                return null;
            }
            return configs[0];
        }
       //Log.w(TAG, "Using RGB8888 / " + version + " EGLConfig.");
        return configs[0];
    }

    public int[] chooseConfig(EGLConfig[] configs, int flags, int renderableType) {
        //int bestR = Integer.MAX_VALUE, bestG = Integer.MAX_VALUE,
        //        bestB = Integer.MAX_VALUE, bestA = Integer.MAX_VALUE;
        int mAlphaSize = 0, mBlueSize = 0, mGreenSize = 0, mRedSize = 0;

        for (EGLConfig config : configs) {
            int[] attributes = new int[4];
            mEgl.eglGetConfigAttrib(mEGLDisplay, config, EGL10.EGL_RED_SIZE, attributes);
            mEgl.eglGetConfigAttrib(mEGLDisplay, config, EGL10.EGL_GREEN_SIZE, attributes);
            mEgl.eglGetConfigAttrib(mEGLDisplay, config, EGL10.EGL_BLUE_SIZE, attributes);
            mEgl.eglGetConfigAttrib(mEGLDisplay, config, EGL10.EGL_ALPHA_SIZE, attributes);
            // Log.w(TAG, " Testing R:" + attributes[0] + " G:" + attributes[1] + " B:" + attributes[2] + " A:" + attributes[3]);

            if (//attributes[0] <= bestR && attributes[1] <= bestG && attributes[2] <= bestB && attributes[3] <= bestA &&
                    attributes[0] >= mRedSize && attributes[1] >= mGreenSize && attributes[2] >= mBlueSize && attributes[3] >= mAlphaSize) {
                mRedSize = attributes[0];
                mGreenSize = attributes[1];
                mBlueSize = attributes[2];
                mAlphaSize = attributes[3];
            }
        }
        //Log.w(TAG, " EGLConfig: Using R:" + mRedSize + " G:" + mGreenSize + " B:" + mBlueSize + " A:" + mAlphaSize);
        int[] attribList = {
                EGL10.EGL_RED_SIZE, mRedSize,
                EGL10.EGL_GREEN_SIZE, mGreenSize,
                EGL10.EGL_BLUE_SIZE, mBlueSize,
                EGL10.EGL_ALPHA_SIZE, mAlphaSize,
                EGL10.EGL_RENDERABLE_TYPE, renderableType,
                EGL10.EGL_NONE, 0,      // placeholder for recordable [@-3]
                EGL10.EGL_NONE
        };
        return attribList;
    }

    /**
     * Discards all resources held by this class, notably the EGL context.  This must be
     * called from the thread where the context was created.
     * <p>
     * On completion, no context will be current.
     */
    public void release() {
        if (mEGLDisplay != EGL10.EGL_NO_DISPLAY) {
            // Android is unusual in that it uses a reference-counted EGLDisplay.  So for
            // every eglInitialize() we need an eglTerminate().
            mEgl.eglMakeCurrent(mEGLDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE,
                    EGL10.EGL_NO_CONTEXT);
            mEgl.eglDestroyContext(mEGLDisplay, mEGLContext);
            /*mEgl.eglReleaseThread();*/ //this method is hide
            mEgl.eglTerminate(mEGLDisplay);
        }

        mEGLDisplay = EGL10.EGL_NO_DISPLAY;
        mEGLContext = EGL10.EGL_NO_CONTEXT;
        mEGLConfig = null;

        mEgl = null;
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (mEGLDisplay != EGL10.EGL_NO_DISPLAY) {
                // We're limited here -- finalizers don't run on the thread that holds
                // the EGL state, so if a surface or context is still current on another
                // thread we can't fully release it here.  Exceptions thrown from here
                // are quietly discarded.  Complain in the log file.
                Log.w(TAG, "WARNING: EglCore was not explicitly released -- state may be leaked");
                release();
            }
        } finally {
            super.finalize();
        }
    }

    /**
     * Destroys the specified surface.  Note the EGLSurface won't actually be destroyed if it's
     * still current in a context.
     */
    public void releaseSurface(EGLSurface eglSurface) {
        mEgl.eglDestroySurface(mEGLDisplay, eglSurface);
    }

    public void releaseSurface() {
        releaseSurface(mEGLSurface);
        mEGLSurface = EGL10.EGL_NO_SURFACE;
    }

    /**
     * Creates an EGL surface associated with a Surface.
     * <p>
     * If this is destined for MediaCodec, the EGLConfig should have the "recordable" attribute.
     */
    public EGLSurface createWindowSurface(Object surface) {
        if (!(surface instanceof SurfaceView) && !(surface instanceof SurfaceHolder)
                && !(surface instanceof Surface) && !(surface instanceof SurfaceTexture)) {
            throw new RuntimeException("invalid surface: " + surface);
        }

        /*
        //http://www.netmite.com/android/mydroid/frameworks/base/opengl/java/com/google/android/gles_jni/EGLImpl.java
        if (!(surface instanceof SurfaceView) && !(surface instanceof SurfaceHolder)) {
            throw new java.lang.UnsupportedOperationException(
                    "eglCreateWindowSurface() can only be called with an instance of " +
                            "SurfaceView or SurfaceHolder at the moment, this will be fixed later.");
        }
        */

        // Create a window surface, and attach it to the Surface we received.
        int[] surfaceAttribs = {
                EGL10.EGL_NONE
        };
        EGLSurface eglSurface = mEgl.eglCreateWindowSurface(mEGLDisplay, mEGLConfig, surface, surfaceAttribs);
        checkEglError("eglCreateWindowSurface");
        if (eglSurface == null) {
            throw new RuntimeException("surface was null");
        }
        mEGLSurface = eglSurface;
        return eglSurface;
    }

    /**
     * Creates an EGL surface associated with an offscreen buffer.
     */
    public EGLSurface createOffscreenSurface(int width, int height) {
        int[] surfaceAttribs = {
                EGL10.EGL_WIDTH, width,
                EGL10.EGL_HEIGHT, height,
                EGL10.EGL_NONE
        };
        EGLSurface eglSurface = mEgl.eglCreatePbufferSurface(mEGLDisplay, mEGLConfig, surfaceAttribs);
        checkEglError("eglCreatePbufferSurface");
        if (eglSurface == null) {
            throw new RuntimeException("surface was null");
        }
        return eglSurface;
    }

    /**
     * Makes our EGL context current, using the supplied surface for both "draw" and "read".
     */
    public void makeCurrent(EGLSurface eglSurface) {
        if (mEGLDisplay == EGL10.EGL_NO_DISPLAY) {
            // called makeCurrent() before create?
            Log.d(TAG, "NOTE: makeCurrent w/o display");
        }
        if (!mEgl.eglMakeCurrent(mEGLDisplay, eglSurface, eglSurface, mEGLContext)) {
            throw new RuntimeException("eglMakeCurrent failed");
        }
    }

    public void makeCurrent() {
        makeCurrent(mEGLSurface);
    }

    /**
     * Makes our EGL context current, using the supplied "draw" and "read" surfaces.
     */
    public void makeCurrent(EGLSurface drawSurface, EGLSurface readSurface) {
        if (mEGLDisplay == EGL10.EGL_NO_DISPLAY) {
            // called makeCurrent() before create?
            Log.d(TAG, "NOTE: makeCurrent w/o display");
        }
        if (!mEgl.eglMakeCurrent(mEGLDisplay, drawSurface, readSurface, mEGLContext)) {
            throw new RuntimeException("eglMakeCurrent(draw,read) failed");
        }
    }

    /**
     * Makes no context current.
     */
    public void makeNothingCurrent() {
        if (!mEgl.eglMakeCurrent(mEGLDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_CONTEXT)) {
            throw new RuntimeException("eglMakeCurrent failed");
        }
    }

    /**
     * Calls eglSwapBuffers.  Use this to "publish" the current frame.
     *
     * @return false on failure
     */
    public boolean swapBuffers(EGLSurface eglSurface) {
        return mEgl.eglSwapBuffers(mEGLDisplay, eglSurface);
    }

    public boolean swapBuffers() {
        return swapBuffers(mEGLSurface);
    }

    /**
     * Sends the presentation time stamp to EGL.  Time is expressed in nanoseconds.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void setPresentationTime(EGLSurface eglSurface, long nanosecond) {
        //EGLExt.eglPresentationTimeANDROID(mEGLDisplay, eglSurface, nanosecond);
    }

    /**
     * Returns true if our context and the specified surface are current.
     */
    public boolean isCurrent(EGLSurface eglSurface) {
        return mEGLContext.equals(mEgl.eglGetCurrentContext()) &&
                eglSurface.equals(mEgl.eglGetCurrentSurface(EGL10.EGL_DRAW));
    }

    /**
     * Performs a simple surface query.
     */
    public int querySurface(EGLSurface eglSurface, int what) {
        int[] value = new int[1];
        mEgl.eglQuerySurface(mEGLDisplay, eglSurface, what, value);
        return value[0];
    }

    /**
     * Queries a string value.
     */
    public String queryString(int what) {
        return mEgl.eglQueryString(mEGLDisplay, what);
    }

    /**
     * Returns the GLES version this context is configured for (currently 2 or 3).
     */
    public int getGlVersion() {
        return mGlVersion;
    }

    /**
     * Checks for EGL errors.  Throws an exception if an error has been raised.
     */
    private void checkEglError(String msg) {
        int error;
        if ((error = mEgl.eglGetError()) != EGL10.EGL_SUCCESS) {
            throw new RuntimeException(msg + ": EGL error: 0x" + Integer.toHexString(error));
        }
    }
}
