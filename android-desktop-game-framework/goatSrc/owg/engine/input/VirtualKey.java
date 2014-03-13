package owg.engine.input;

/**Provides a cross-platform, type safe reference point for keyboard keys.
 * The KeyboardHandler implementation must provide the native value for each enum!*/
public enum VirtualKey {
	VK_1, VK_2, VK_3, VK_4, VK_5, VK_6, VK_7, VK_8, VK_9, VK_0,
	VK_Q, VK_W, VK_E, VK_R, VK_T, VK_Y, VK_U, VK_I, VK_O, VK_P,
	VK_A, VK_S, VK_D, VK_F, VK_G, VK_H, VK_J, VK_K, VK_L,
	VK_Z, VK_X, VK_C, VK_V, VK_B, VK_N, VK_M, 
	VK_COMMA, VK_PERIOD, VK_BACKSLASH,  
	VK_MINUS, VK_PLUS, VK_MULTIPLY, VK_SLASH, VK_EQUALS,
	VK_LSHIFT, VK_RSHIFT, VK_LALT, VK_RALT, VK_SPACE,
	VK_ESCAPE, VK_TAB, VK_ENTER, VK_BACKSPACE,
	VK_RIGHT, VK_UP, VK_DOWN, VK_LEFT,
	VK_OPERATING_SYSTEM,
	VK_F1, VK_F2, VK_F3, VK_F4, VK_F5, VK_F6, VK_F7, VK_F8, VK_F9, VK_F10, VK_F11, VK_F12;
	
	int value = -1;
	public int value() {
		return value;
	}
}
