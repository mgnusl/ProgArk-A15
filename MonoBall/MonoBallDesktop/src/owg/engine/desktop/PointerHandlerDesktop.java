package owg.engine.desktop;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import owg.engine.input.PointerHandler;

public class PointerHandlerDesktop extends PointerHandler implements MouseListener, MouseMotionListener, FocusListener {
	/**The number of currently depressed mouse buttons*/
	private byte numPointerButtonsDown;
	private byte numPointerButtonPresses;
	private byte numPointerButtonReleases;
	
	/**The last recorded position of the pointer in the OpenGL canvas(in pixels, from top-left)*/
	private int lastPointerX,lastPointerY;

	public PointerHandlerDesktop(AWTFocusHandler focusHandler, Component canvas) {
		super();
		lastPointerX = 0;
		lastPointerY = 0;
		numPointerButtonsDown = 0;
		numPointerButtonPresses = 0;
		numPointerButtonReleases = 0;
		
		canvas.addMouseMotionListener(this);
		canvas.addMouseListener(this);
		
		focusHandler.addFocusListener(this);
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		//NOP(Not a useful abstraction)
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		mouseMoved(e);
	}
	@Override
	public void mouseExited(MouseEvent e) {
		mouseMoved(e);
	}
	@Override
	public void mousePressed(MouseEvent e) {
		numPointerButtonPresses ++;
		numPointerButtonsDown ++;
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		numPointerButtonReleases ++;
		numPointerButtonsDown --;
		if(numPointerButtonsDown < 0)
			numPointerButtonsDown = 0;
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		mouseMotion(e);
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		mouseMotion(e);
	}
	private void mouseMotion(MouseEvent e) {
		lastPointerX = e.getX();
		lastPointerY = e.getY();
	}
	@Override
	public void resetPressReleaseState() {
		numPointerButtonPresses = 0;
		numPointerButtonReleases = 0;
	}
	private void clearState() {
		while(numPointerButtonsDown > 0)
			mouseReleased(null);
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
	@Override
	public void focusGained(FocusEvent e) {
		//NOP
	}
	@Override
	public void focusLost(FocusEvent e) {
		clearState();
	}
}
