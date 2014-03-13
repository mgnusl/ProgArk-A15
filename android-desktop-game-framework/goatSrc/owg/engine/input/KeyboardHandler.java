package owg.engine.input;

public abstract class KeyboardHandler {
	
	public static interface StringCallback<T> {
		public abstract void valueSet(T key, String value);
	}
	
	public KeyboardHandler() {
		//Get virtual key values from implementation
		for (VirtualKey p : VirtualKey.values())
			p.value = valueOf(p);
	}

	/**Get the native key code for the indicated virtual key*/
	protected abstract int valueOf(VirtualKey p);
	
	/**@param virtualKey The virtual-key code to check 
	 * @return Whether the indicated key is currently down on the hardware keyboard.*/
	public abstract boolean isDown(VirtualKey virtualKey);
	/**@param virtualKey The virtual-key code to check 
	 * @return Whether the indicated key was pressed on the hardware keyboard between the last and current step.
	public abstract boolean checkKeyPressed(Key key);*/
	public abstract boolean isPressed(VirtualKey virtualKey);
	/**@param virtualKey The virtual-key code to check 
	 * @return Whether the indicated key was released on the hardware keyboard between the last and current step. */
	public abstract boolean isReleased(VirtualKey virtualKey);
	/**Should be called at the end of each step to ensure that 
	 * pointer/keyboard press and release states are only true for the step when they occurred.*/
	public abstract void resetPressReleaseState();
	/**
	 * May be used to prompt the user to input a string. 
	 * A modal popup dialog will appear, and temporarily freeze the game.<br/>
	 * The value will be returned via the callback. 
	 * The callback call will be performed on the OpenGL thread, after the current render has completed.
	 * @param key An optional key that will be returned as-is in the callback.
	 * @param messageString The message displayed to the user.
	 * @param defaultValue The default value filled into the text input field.
	 * @param callback The object which should receive the callback.
	 */
	public abstract<T> void getUserInputString(T key, String messageString, String defaultValue, 
			StringCallback<T> callback);
}