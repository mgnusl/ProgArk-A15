package owg.engine.desktop;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.Timer;

public class AWTFocusHandler implements PropertyChangeListener {
	/**Utility method for getting the nearest parent window of a component*/
	private static Window findWindow(Component c) {
		if (c == null) 
			return JOptionPane.getRootFrame();
		else if (c instanceof Window) 
			return (Window) c; 
		else 
			return findWindow(c.getParent());
	}
	
	private ArrayList<FocusListener> focusListeners;
	/**The component that currently owns the keyboard focus*/
	private Component currentComponent;
	/**Whether the application currently has the keyboard focus.*/
	private boolean applicationHasFocus;
	/**Whether a modal dialog owned by the application currently has the keyboard focus*/
	private boolean modalFocus;
	/**AWT will fire a focus lost event when a container loses focus, 
	 * but there is no immediate way to determine whether the focus is being transfered to 
	 * another container that the application owns.<br/>
	 * <br/>
	 * This timer exists to determine whether this is the case, by taking the time from a focus lost event
	 * to the next focus gained event. If more than 33ms passes in between these events, then this is 
	 * interpreted as the application losing focus, and we can generate a more meaningful event.*/
	private Timer focusLostTimer;

	public AWTFocusHandler() {
		//This timer is used to detect when all of our windows lose focus.
		//It fails if the focus switch between our components takes more than 33 ms, which is enormously unlikely.
		focusLostTimer = new Timer(0,new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(applicationHasFocus)
					applicationFocusLost();
			}});
		focusLostTimer.setRepeats(false);
		focusLostTimer.setInitialDelay(33);
		
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(this);
		
		focusListeners = new ArrayList<FocusListener>(); 
	}

	/**This is called when the application gains focus.*/
	private void applicationFocusGained() {
		applicationHasFocus = true;
		for(FocusListener l : focusListeners)
			l.focusGained(new FocusEvent(currentComponent, FocusEvent.FOCUS_GAINED));
	}
	/**This is called when the application loses focus.*/
	private void applicationFocusLost() {
		applicationHasFocus = false;
		for(FocusListener l : focusListeners)
			l.focusLost(new FocusEvent(currentComponent, FocusEvent.FOCUS_LOST));
	}

	/**Here we apply some unreasonably complicated logic to determine whether the application still has focus.
	 * In addition, we determine whether a modal dialog is currently on top.*/
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (focusLostTimer!=null && e.getPropertyName().equals("focusOwner")) {
			//Focus changed
			modalFocus=false;
			if (e.getNewValue()==null)
				focusLostTimer.start();//Signal that the current component has been set to null
			else {
				focusLostTimer.stop();//Signal that the current component is no longer null
				Object o = e.getNewValue();
				if (o instanceof Component) {
					currentComponent = (Component) o;
					Window w = findWindow(currentComponent);
					if(w instanceof Dialog) {
						if (((Dialog)w).isModal())
							modalFocus=true;
					}
				}
				if(!applicationHasFocus)
					applicationFocusGained();
			}
		}
	}
	
	public void addFocusListener(FocusListener l) {
		
	}
	
	public boolean isModalDialogFocused() {
		return modalFocus;
	}
	public boolean isApplicationFocused() {
		return applicationHasFocus;
	}
	public Component getLastFocusedComponent() {
		return currentComponent;
	}
}
