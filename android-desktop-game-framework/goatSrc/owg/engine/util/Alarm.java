package owg.engine.util;

import java.util.Arrays;
/**Provides an array of timers with discrete resolution.
 * The timers should be updated from the step event by the host, via a call to {@link #step()} on every game tick.
 * Upon reaching 0, the timer will trigger an {@link AlarmTriggerable#alarm(int)} event in its host. */
public class Alarm {
	/**An object capable of hosting an {@link Alarm}*/
	public static interface AlarmTriggerable {
		/**Called when the alarm with the indicated index reaches 0.*/
		void alarm(int index);
	}
	
	public final int[] alarm;
	public final AlarmTriggerable target;
	
	@Kryo
	private Alarm() {
		target=null;alarm=null;
	}
	public Alarm(int length, AlarmTriggerable t) {
		alarm = new int[length];
		target = t;
	}
	
	public void step() {
        for(int i=0; i<alarm.length; i++) {
            if(alarm[i]!=0) {
                alarm[i]--;
                if(alarm[i]<=0) {
                    alarm[i]=0;
                    target.alarm(i);
				}
			}
		}
	}
	/**Sets the alarm with the indicated index to go off in the indicated number of {@link #step()}s*/
	public void set(int index, int steps) {
		alarm[index] = steps;
	}
	
	public int get(int index) {
		return alarm[index];
	}
	
	public void stop(int index) {
		set(index, 0);
	}
	
	public void stopAll() {
		Arrays.fill(alarm, 0);
	}
}
