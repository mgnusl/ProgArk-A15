package owg.engine.desktop;

import javax.media.opengl.GLAnimatorControl;
import javax.media.opengl.GLAutoDrawable;

import owg.engine.Animator;

public class AnimatorDesktop extends Animator implements GLAnimatorControl {
	private GLAutoDrawable drawable;

	public AnimatorDesktop(int fps, GLAutoDrawable drawable) {
		super(fps);
		this.drawable = drawable;
		drawable.setAnimator(this);
	}

	@Override
	protected void tick() {
		drawable.display();
	}

	@Override
	public synchronized void remove(GLAutoDrawable drawable) {
		if(drawable == this.drawable) {
			drawable.setAnimator(null);
			this.drawable = null;
			stop();
		}
	}

	/**Note: Multiple drawables are not supported. This will replace the current animated drawable!*/
	@Override
	public void add(GLAutoDrawable drawable) {
		if (this.drawable != null && this.drawable.getAnimator() == this)
			this.drawable.setAnimator(null);
		this.drawable = drawable;
		if(drawable != null) {
			this.drawable.setAnimator(this);
			resetFPSCounter();
		}
		else {
			stop();
		}
	}
}
