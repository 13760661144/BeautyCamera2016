package cn.poco.pgles;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static cn.poco.pgles.PGLTextureRotationUtil.TEXTURE_ROTATED_270;

public class PFColorFilterCmillia extends PFFilter {
	
	protected int mTexture1;
	protected FloatBuffer mTexture1buffer;
	
	public PFColorFilterCmillia(Context c) {
		this(c, "colorfilter001_Cmillia.fsh");
	}
	
	protected PFColorFilterCmillia(Context c, String fshfile) {
		super(c, "twoinputs.vsh", fshfile);
		mTexture1buffer = ByteBuffer
				.allocateDirect(TEXTURE_ROTATED_270.length * 4)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		mTexture1buffer.put(TEXTURE_ROTATED_270).position(0);
	}
	
	public void setTexture1(final int texture1) {
		runOnDraw(new Runnable() {
			@Override
			public void run() {
				mTexture1 = texture1;
			}
		});
	}
	
	@Override
	protected void onDrawArraysPre() {
		mTexture1buffer.position(0);
		activeAttribute("a_textureCoord1", 2, GLES20.GL_FLOAT, 0, mTexture1buffer);
		bindSampler("texture1", mTexture1);
	}
}
