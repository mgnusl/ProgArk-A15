package owg.engine.input;

public abstract class PointerHandler {
	/**Should be called at the end of each step to ensure that 
	 * pointer press and release states are only true for the step when they occurred.*/
	public abstract void resetPressReleaseState();
	/**Return whether some pointer device has a pressed button, or some area is being manipulated on a touchscreen.*/
	public abstract boolean isPointerButtonDown();
	/**Returns whether some pointer device press event was generated between the previous and current step.*/
	public abstract boolean isPointerButtonBeingPressed();
	/**Returns whether some pointer device release event was generated between the previous and current step.*/
	public abstract boolean isPointerButtonBeingReleased();
	/**Return the last recorded location of some pointing device being manipulated. 
	 * This is given in pixels.*/
	public abstract float getLastPointerX();
	/**Return the last recorded location of some pointing device being manipulated.
	 * This is given in pixels.*/
	public abstract float getLastPointerY();

}