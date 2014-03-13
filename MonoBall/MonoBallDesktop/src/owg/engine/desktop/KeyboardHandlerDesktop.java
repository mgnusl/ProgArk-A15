package owg.engine.desktop;

import static owg.engine.Engine.scene;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import owg.engine.Engine;
import owg.engine.input.KeyboardHandler;
import owg.engine.input.VirtualKey;

public class KeyboardHandlerDesktop extends KeyboardHandler implements KeyEventDispatcher, FocusListener {
	/**An upper bound for the numerical value of key events that we are going to process*/
	private static final int MAX_KEY_INDEX = 128;
	/**Extra space for right shift and right alt, since these do not have distinct keycodes in AWT*/
	private static final int NUM_RKEYS = 2;
	private static final int VK_RALT = MAX_KEY_INDEX;
	private static final int VK_RSHIFT = MAX_KEY_INDEX+1;
	
	private final AWTFocusHandler focusHandler;

	/**Log of all currently depressed keys*/
	private final boolean[] keysDown;
	/**Log of all keys that have been pressed between the last step and this step*/
	private final boolean[] keyPresses;
	/**Log of all keys that have been released between the last step and this step*/
	private final boolean[] keyReleases;
	
	public KeyboardHandlerDesktop(AWTFocusHandler focusHandler) {
		super();
		this.focusHandler = focusHandler;
		focusHandler.addFocusListener(this);
		
		keyPresses = new boolean[MAX_KEY_INDEX+NUM_RKEYS];
		keyReleases = new boolean[MAX_KEY_INDEX+NUM_RKEYS];
		keysDown = new boolean[MAX_KEY_INDEX+NUM_RKEYS];
		
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
	}
	@Override
	public boolean isDown(VirtualKey virtualKey) {
		int keyIndex = virtualKey.value();
		return keysDown[keyIndex];
	}
	@Override
	public boolean isPressed(VirtualKey virtualKey) {
		int keyIndex = virtualKey.value();
		return keyPresses[keyIndex];
	}
	@Override
	public boolean isReleased(VirtualKey virtualKey) {
		int keyIndex = virtualKey.value();
		return keyReleases[keyIndex];
	}

	/**Generate a key pressed event for the given AWT key code*/
	public void keyPressed(int code) {
		if(code<MAX_KEY_INDEX) {
			if(!keysDown[code])
				keyPresses[code]=true;
			keysDown[code]=true;
		}
	}
	/**Generate a key released event for the given AWT key code*/
	public void keyReleased(int code) {
		if(code<MAX_KEY_INDEX) {
			keyReleases[code]=true;
			keysDown[code]=false;
		}
	}
	@Override
	public void resetPressReleaseState() {
		for(int i=0; i<MAX_KEY_INDEX; i++)
		{
			keyPresses[i]=false;
			keyReleases[i]=false;
		}
	}
	

	/**Process all keyboard events received by the JVM.*/
	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		if(e.getID() == KeyEvent.KEY_PRESSED) {
			//Don't process key presses that are performed in 
			//modal dialogues, text fields, or other keyboard traversable ui elements used in the application.
			if(!focusHandler.isModalDialogFocused() && 
					!(focusHandler.getLastFocusedComponent() instanceof JTextField) && 
					!(focusHandler.getLastFocusedComponent() instanceof JTextArea) &&
					!(focusHandler.getLastFocusedComponent() instanceof JComboBox)) {
				int c = e.getKeyCode();
				if(c == KeyEvent.VK_ALT && e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT)
					c = VK_RALT;
				else if(c == KeyEvent.VK_SHIFT && e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT)
					c = VK_RSHIFT;
				keyPressed(c);
			}
		}
		else if(e.getID() == KeyEvent.KEY_RELEASED)
			keyReleased(e.getKeyCode());
		//Don't consume the event.
		return false;
	}
	
	private void clearState() {
		for(int i = 0; i<MAX_KEY_INDEX+NUM_RKEYS; i++) {
			if(keysDown[i])
				keyReleased(i);
		}
	}
	@Override
	public void focusGained(FocusEvent e) {
		//NOP
	}
	@Override
	public void focusLost(FocusEvent e) {
		//
		clearState();
	}
	@Override
	protected int valueOf(VirtualKey key) {
		switch (key) {
		case VK_0:
			return KeyEvent.VK_0;
		case VK_1:
			return KeyEvent.VK_1;
		case VK_2:
			return KeyEvent.VK_2;
		case VK_3:
			return KeyEvent.VK_3;
		case VK_4:
			return KeyEvent.VK_4;
		case VK_5:
			return KeyEvent.VK_5;
		case VK_6:
			return KeyEvent.VK_6;
		case VK_7:
			return KeyEvent.VK_7;
		case VK_8:
			return KeyEvent.VK_8;
		case VK_9:
			return KeyEvent.VK_9;
		case VK_A:
			return KeyEvent.VK_A;
		case VK_B:
			return KeyEvent.VK_B;
		case VK_BACKSLASH:
			return KeyEvent.VK_BACK_SLASH;
		case VK_BACKSPACE:
			return KeyEvent.VK_BACK_SPACE;
		case VK_C:
			return KeyEvent.VK_C;
		case VK_COMMA:
			return KeyEvent.VK_COMMA;
		case VK_D:
			return KeyEvent.VK_D;
		case VK_DOWN:
			return KeyEvent.VK_DOWN;
		case VK_E:
			return KeyEvent.VK_E;
		case VK_ENTER:
			return KeyEvent.VK_ENTER;
		case VK_EQUALS:
			return KeyEvent.VK_EQUALS;
		case VK_ESCAPE:
			return KeyEvent.VK_ESCAPE;
		case VK_F:
			return KeyEvent.VK_F;
		case VK_F1:
			return KeyEvent.VK_F1;
		case VK_F10:
			return KeyEvent.VK_F10;
		case VK_F11:
			return KeyEvent.VK_F11;
		case VK_F12:
			return KeyEvent.VK_F12;
		case VK_F2:
			return KeyEvent.VK_F2;
		case VK_F3:
			return KeyEvent.VK_F3;
		case VK_F4:
			return KeyEvent.VK_F4;
		case VK_F5:
			return KeyEvent.VK_F5;
		case VK_F6:
			return KeyEvent.VK_F6;
		case VK_F7:
			return KeyEvent.VK_F7;
		case VK_F8:
			return KeyEvent.VK_F8;
		case VK_F9:
			return KeyEvent.VK_F9;
		case VK_G:
			return KeyEvent.VK_G;
		case VK_H:
			return KeyEvent.VK_H;
		case VK_I:
			return KeyEvent.VK_I;
		case VK_J:
			return KeyEvent.VK_J;
		case VK_K:
			return KeyEvent.VK_K;
		case VK_L:
			return KeyEvent.VK_L;
		case VK_LALT:
			return KeyEvent.VK_ALT;
		case VK_LEFT:
			return KeyEvent.VK_LEFT;
		case VK_LSHIFT:
			return KeyEvent.VK_SHIFT;
		case VK_M:
			return KeyEvent.VK_M;
		case VK_MINUS:
			return KeyEvent.VK_MINUS;
		case VK_MULTIPLY:
			return KeyEvent.VK_MULTIPLY;
		case VK_N:
			return KeyEvent.VK_N;
		case VK_O:
			return KeyEvent.VK_O;
		case VK_OPERATING_SYSTEM:
			return KeyEvent.VK_WINDOWS;
		case VK_P:
			return KeyEvent.VK_P;
		case VK_PERIOD:
			return KeyEvent.VK_PERIOD;
		case VK_PLUS:
			return KeyEvent.VK_PLUS;
		case VK_Q:
			return KeyEvent.VK_Q;
		case VK_R:
			return KeyEvent.VK_R;
		case VK_RALT:
			return KeyEvent.VK_ALT;
		case VK_RIGHT:
			return KeyEvent.VK_RIGHT;
		case VK_RSHIFT:
			return KeyEvent.VK_SHIFT;
		case VK_S:
			return KeyEvent.VK_S;
		case VK_SLASH:
			return KeyEvent.VK_SLASH;
		case VK_SPACE:
			return KeyEvent.VK_SPACE;
		case VK_T:
			return KeyEvent.VK_T;
		case VK_TAB:
			return KeyEvent.VK_TAB;
		case VK_U:
			return KeyEvent.VK_U;
		case VK_UP:
			return KeyEvent.VK_UP;
		case VK_V:
			return KeyEvent.VK_V;
		case VK_W:
			return KeyEvent.VK_W;
		case VK_X:
			return KeyEvent.VK_X;
		case VK_Y:
			return KeyEvent.VK_Y;
		case VK_Z:
			return KeyEvent.VK_Z;
		}
		throw new RuntimeException("Key: "+key+" not implemented for "+this);
	}
	@Override
	public<T> void getUserInputString(final T key, final String messageString, final String defaultValue, 
			final StringCallback<T> callback) {
		scene().getAnimator().pause();
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				final String val = JOptionPane.showInputDialog(null, messageString, defaultValue);
				Engine.glUtil().invokeLater(new Runnable(){
					@Override
					public void run() {
						callback.valueSet(key, val);
					}
				});
				scene().getAnimator().resume();
			}
		});
	}
}
