
package owg.engine.android;

import java.util.LinkedList;
import java.util.Queue;

import owg.engine.input.PointerHandler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class PointerHandlerAndroid extends PointerHandler implements OnTouchListener {
	/**The number of current touch manipulation locations*/
	private byte numPointerButtonsDown;
	private byte numPointerButtonPresses;
	private byte numPointerButtonReleases;
	
	/**Android delivers input events at arbitrary times during execution.
	 * Thus, we need a secondary event queue to process all the input events that have arrived during a step.*/
	Queue<MotionEvent> inputQueue;
	
	/**The last recorded position of the pointer in the OpenGL canvas(in pixels, from top-left)*/
	private float lastPointerX,lastPointerY;
	
	public PointerHandlerAndroid(View activity) {
		lastPointerX = 0;
		lastPointerY = 0;
		numPointerButtonsDown = 0;
		numPointerButtonPresses = 0;
		numPointerButtonReleases = 0;
		
		inputQueue = new LinkedList<MotionEvent>();
		
		activity.setOnTouchListener(this);
	}

	@Override
	public synchronized boolean onTouch(View v, MotionEvent event) {
		inputQueue.offer(event);
		return true;
	}
	
	public synchronized void pollEvents() {
		MotionEvent event;
		while((event = inputQueue.poll()) != null) {
            if (event.getAction() != MotionEvent.ACTION_UP) {

                if (numPointerButtonsDown == 0)
                    numPointerButtonPresses = 1;
                numPointerButtonsDown = 1;
            } else {

                if (numPointerButtonsDown == 1) {
                    numPointerButtonReleases = 1;
                }
                numPointerButtonsDown = 0;
            }

            if(event.getAction() != MotionEvent.ACTION_DOWN) {
				lastPointerX = event.getX();
				lastPointerY = event.getY();
			}
		}
	}

	@Override
	public void resetPressReleaseState() {
		numPointerButtonPresses = 0;
		numPointerButtonReleases = 0;
		pollEvents();
	}

	@Override
	public boolean isPointerButtonDown() {
		return numPointerButtonsDown > 0;
	}

	@Override
	public boolean isPointerButtonBeingPressed() {
		return numPointerButtonPresses > 0;
	}

	@Override
	public boolean isPointerButtonBeingReleased() {
		return numPointerButtonReleases > 0;
	}

	@Override
	public float getLastPointerX() {
		return lastPointerX;
	}

	@Override
	public float getLastPointerY() {
		return lastPointerY;
	}
}