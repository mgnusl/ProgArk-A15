package owg.engine.android;

import android.opengl.GLSurfaceView;
import owg.engine.Animator;

public class AnimatorAndroid extends Animator {
	public GLSurfaceView drawable;
	
	public AnimatorAndroid(int fps, GLSurfaceView drawable) {
		super(fps);
		this.drawable = drawable;
	}

	@Override
	protected void tick() {
		drawable.requestRender();
	}

}
