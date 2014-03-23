package owg.engine.android;

import java.util.LinkedList;
import java.util.Queue;

import owg.engine.Engine;
import owg.engine.input.KeyboardHandler;
import owg.engine.input.VirtualKey;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;

import static owg.engine.input.VirtualKey.*;

public class KeyboardHandlerAndroid extends KeyboardHandler implements OnKeyListener {
	/**An upper bound for the numerical value of key events that we are going to process*/
	private static final int MAX_KEY_INDEX = 128;
	
	/**Log of all currently depressed keys*/
	private boolean[] keysDown;
	/**Log of all keys that have been pressed between the last step and this step*/
	private boolean[] keyPresses;
	/**Log of all keys that have been released between the last step and this step*/
	private boolean[] keyReleases;

	/**Android delivers input events at arbitrary times during execution.
	 * Thus, we need a secondary event queue to process all the input events that have arrived during a step.*/
	Queue<KeyEvent> inputQueue;
	
	public KeyboardHandlerAndroid(View activity) {
		keyPresses = new boolean[MAX_KEY_INDEX];
		keyReleases = new boolean[MAX_KEY_INDEX];
		keysDown = new boolean[MAX_KEY_INDEX];
		
		inputQueue = new LinkedList<KeyEvent>();
		
		activity.setOnKeyListener(this);
	}

	@Override
	public synchronized boolean onKey(View v, int keyCode, KeyEvent event) {
		inputQueue.offer(event);
		return true;
	}
	
	/**Generate a key pressed event for the given AWT key code*/
	public void keyPressed(int code)
	{
		if(code<MAX_KEY_INDEX)
		{
			if(!keysDown[code])
				keyPresses[code]=true;
			keysDown[code]=true;
		}
	}
	/**Generate a key released event for the given AWT key code*/
	public void keyReleased(int code)
	{
		if(code<MAX_KEY_INDEX)
		{
			keyReleases[code]=true;
			keysDown[code]=false;
		}
	}

	@Override
	public boolean isDown(VirtualKey virtualKey) {
		int c = virtualKey.value();
		if(c != -1) {
			return keysDown[c];
		}
		return false;
	}

	@Override
	public boolean isPressed(VirtualKey virtualKey) {
		int c = virtualKey.value();
		if(c != -1) {
			return keyPresses[c];
		}
		return false;
	}

	@Override
	public boolean isReleased(VirtualKey virtualKey) {
		int c = virtualKey.value();
		if(c != -1) {
			return keyReleases[c];
		}
		return false;
	}

	@Override
	public void resetPressReleaseState() {
		for(int i=0; i<MAX_KEY_INDEX; i++)
		{
			keyPresses[i]=false;
			keyReleases[i]=false;
		}
		pollEvents();
	}

	public synchronized void pollEvents() {
		KeyEvent event;
		while((event = inputQueue.poll()) != null) {
			if (event.getAction() == KeyEvent.ACTION_DOWN)
				keyPressed(event.getKeyCode());
			else if (event.getAction() == KeyEvent.ACTION_UP)
				keyReleased(event.getKeyCode());
		}
	}
	
	@Override
	protected int valueOf(VirtualKey key) {
		switch (key) {
		case VK_0:
			return KeyEvent.KEYCODE_0;
		case VK_1:
			return KeyEvent.KEYCODE_1;
		case VK_2:
			return KeyEvent.KEYCODE_2;
		case VK_3:
			return KeyEvent.KEYCODE_3;
		case VK_4:
			return KeyEvent.KEYCODE_4;
		case VK_5:
			return KeyEvent.KEYCODE_5;
		case VK_6:
			return KeyEvent.KEYCODE_6;
		case VK_7:
			return KeyEvent.KEYCODE_7;
		case VK_8:
			return KeyEvent.KEYCODE_8;
		case VK_9:
			return KeyEvent.KEYCODE_9;
		case VK_A:
			return KeyEvent.KEYCODE_A;
		case VK_B:
			return KeyEvent.KEYCODE_B;
		case VK_BACKSLASH:
			return KeyEvent.KEYCODE_BACKSLASH;
		case VK_BACKSPACE:
			return KeyEvent.KEYCODE_DEL;
		case VK_C:
			return KeyEvent.KEYCODE_C;
		case VK_COMMA:
			return KeyEvent.KEYCODE_COMMA;
		case VK_D:
			return KeyEvent.KEYCODE_D;
		case VK_DOWN:
			return KeyEvent.KEYCODE_DPAD_DOWN;
		case VK_E:
			return KeyEvent.KEYCODE_E;
		case VK_ENTER:
			return KeyEvent.KEYCODE_ENTER;
		case VK_EQUALS:
			return KeyEvent.KEYCODE_EQUALS;
		case VK_ESCAPE:
			return KeyEvent.KEYCODE_BACK;
		case VK_F:
			return KeyEvent.KEYCODE_F;
		case VK_F1:
			return -1;
		case VK_F10:
			return -1;
		case VK_F11:
			return -1;
		case VK_F12:
			return -1;
		case VK_F2:
			return -1;
		case VK_F3:
			return -1;
		case VK_F4:
			return -1;
		case VK_F5:
			return -1;
		case VK_F6:
			return -1;
		case VK_F7:
			return -1;
		case VK_F8:
			return -1;
		case VK_F9:
			return -1;
		case VK_G:
			return KeyEvent.KEYCODE_G;
		case VK_H:
			return KeyEvent.KEYCODE_H;
		case VK_I:
			return KeyEvent.KEYCODE_I;
		case VK_J:
			return KeyEvent.KEYCODE_J;
		case VK_K:
			return KeyEvent.KEYCODE_K;
		case VK_L:
			return KeyEvent.KEYCODE_L;
		case VK_LALT:
			return KeyEvent.KEYCODE_ALT_LEFT;
		case VK_LEFT:
			return KeyEvent.KEYCODE_DPAD_LEFT;
		case VK_LSHIFT:
			return KeyEvent.KEYCODE_SHIFT_LEFT;
		case VK_M:
			return KeyEvent.KEYCODE_M;
		case VK_MINUS:
			return KeyEvent.KEYCODE_MINUS;
		case VK_MULTIPLY:
			return KeyEvent.KEYCODE_STAR;
		case VK_N:
			return KeyEvent.KEYCODE_N;
		case VK_O:
			return KeyEvent.KEYCODE_O;
		case VK_OPERATING_SYSTEM:
			return KeyEvent.KEYCODE_HOME;
		case VK_P:
			return KeyEvent.KEYCODE_P;
		case VK_PERIOD:
			return KeyEvent.KEYCODE_PERIOD;
		case VK_PLUS:
			return KeyEvent.KEYCODE_PLUS;
		case VK_Q:
			return KeyEvent.KEYCODE_Q;
		case VK_R:
			return KeyEvent.KEYCODE_R;
		case VK_RALT:
			return KeyEvent.KEYCODE_ALT_RIGHT;
		case VK_RIGHT:
			return KeyEvent.KEYCODE_DPAD_RIGHT;
		case VK_RSHIFT:
			return KeyEvent.KEYCODE_SHIFT_RIGHT;
		case VK_S:
			return KeyEvent.KEYCODE_S;
		case VK_SLASH:
			return KeyEvent.KEYCODE_SLASH;
		case VK_SPACE:
			return KeyEvent.KEYCODE_SPACE;
		case VK_T:
			return KeyEvent.KEYCODE_T;
		case VK_TAB:
			return KeyEvent.KEYCODE_TAB;
		case VK_U:
			return KeyEvent.KEYCODE_U;
		case VK_UP:
			return KeyEvent.KEYCODE_DPAD_UP;
		case VK_V:
			return KeyEvent.KEYCODE_V;
		case VK_W:
			return KeyEvent.KEYCODE_W;
		case VK_X:
			return KeyEvent.KEYCODE_X;
		case VK_Y:
			return KeyEvent.KEYCODE_Y;
		case VK_Z:
			return KeyEvent.KEYCODE_Z;
		}
		throw new RuntimeException("Key: "+key+" not implemented for "+this);
	}

	@Override
	public<T> void getUserInputString(final T key, String messageString, String defaultValue, 
			final StringCallback<T> callback) {
		Context ctx = (Activity)Engine.entryPoint();
		Engine.scene().getAnimator().pause();
		final AlertDialog.Builder b = new AlertDialog.Builder(ctx);
		b.setTitle(messageString);
		b.setCancelable(true);
		final EditText input = new EditText(ctx);
		input.setSingleLine(true);
		input.setText(defaultValue);
		b.setView(input);
		
		class Exec {
			void exec(final String val) {
				Engine.glUtil().invokeLater(new Runnable(){
					@Override
					public void run() {
						callback.valueSet(key, val);
					}
				});
				Engine.scene().getAnimator().resume();
			}
		}
		final Exec exec = new Exec();
		
		b.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int whichButton) {  
				exec.exec(input.getText().toString());                  
			}  
		});
		b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				exec.exec(null);  
			}
		});
		b.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				exec.exec(null);
			}
		});
		new Handler(ctx.getMainLooper()).post(new Runnable(){
				@Override
				public void run() {
					b.show();
				}
		});
		
	}

}
