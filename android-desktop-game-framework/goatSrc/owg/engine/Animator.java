package owg.engine;

import java.io.PrintStream;

/**Provides a mechanism for generating ticks at a constant rate and without hogging the CPU, 
 * even with poor system timer resolution.*/
public abstract class Animator
{
	/**The ticks-per-second count that the animator will try to achieve.*/
	final int targetFPS;
	/**The system time when the animator generated the first tick.*/
	long startTime;
	/**The system time at the time of the most recent FPS update. 
	 * An FPS update happens once every {@link #updateInterval} ticks.*/
	long currentTime;
	/**The system time at the time of the previous FPS update before the most recent one. 
	 * An FPS update happens once every {@link #updateInterval} ticks.*/
	long lastTime;
	/**The interval between each FPS update, in ticks.
	 * @see #setUpdateFPSFrames(int, PrintStream)*/
	int updateInterval;
	/**Tick counter that resets every {@link #updateInterval} ticks.*/
	int updateCounter;
	/**The total number of ticks since the animator was started or reset.*/
	int totalCounter;
	/**Optional debug output stream.*/
	PrintStream outStream = null;
	/**The thread that generates the ticks.*/
	Thread thread = null;
	/**Whether the {@link #startTime} has been set. If false, the animator has not yet generated its 
	 * first tick since the last {@link #start()} or {@link #resetFPSCounter()}.*/
	boolean startTimeSet = false;
	/**Whether the animator has been started ( {@link #start()} ) and not stopped ( {@link #stop()} ) */
	volatile boolean isRunning = false;
	/**Whether the animator has been paused ( {@link #pause()} ) and not resumed ( {@link #resume()} )*/
	volatile boolean isPaused = false;

	/**
	 * Construct a new animator.
	 * @param fps The target ticks-per-second count that the animator will try to achieve.
	 * By default, the {@link #updateInterval} is also set to the fps value. 
	 * This can be changed with {@link #setUpdateFPSFrames(int, PrintStream)}.
	 */
	public Animator(int fps)
	{
		targetFPS = fps;
		updateInterval = fps;
	}
	/**
	 * @return The system time when the animator generated the first tick
	 * (after a call to {@link #start()} or {@link #resetFPSCounter()} ).<br/>
	 * The value is unspecified if the animator is not started.
	 */
	public synchronized long getFPSStartTime()
	{
		return startTime;
	}
	/**
	 * @return The most recently computed FPS count.<br/>
	 * Specifically, this is the average FPS in the period between the most recent FPS update and the one before that.
	 * <br/>An FPS update is an event where the actual FPS value is computed.
	 * It occurs once every {@link #updateInterval} ticks.<br/> 
	 * The default value for the update interval is the same as the target FPS, 
	 * which was initially set in the constructor. 
	 * The value can be changed by invoking {@link #setUpdateFPSFrames(int, PrintStream)}.
	 */
	public synchronized float getLastFPS()
	{
		if(currentTime == lastTime)
			return 0;
		else
			return (float)updateInterval * 1000 / (currentTime - lastTime);
	}
	/**@return the system time difference between the most recent FPS update and the one before that.
	 * @see #getLastFPS()*/
	public synchronized long getLastFPSPeriod()
	{
		return currentTime - lastTime;
	}
	/**@return the system time of the last FPS update.
	 * @see #getLastFPS()*/
	public synchronized long getLastFPSUpdateTime()
	{
		return currentTime;
	}
	/**@return The average FPS since the animator was started, or since the last reset({@link #resetFPSCounter()});
	 * @see #getLastFPS()*/
	public synchronized float getTotalFPS()
	{
		if(currentTime == startTime)
			return 0;
		else
			return (float)totalCounter * 1000 / (currentTime - startTime);
	}
	/**@return The system time difference from the animator was started, 
	 * or since the last reset({@link #resetFPSCounter()}), to the last FPS update.
	 * @see #getTotalFPS()*/
	public synchronized long getTotalFPSDuration()
	{
		return currentTime - startTime;
	}
	/**@return The total number of ticks since the animator was started, 
	 * or since the last reset({@link #resetFPSCounter()})
	 * @see #getTotalFPS()*/
	public synchronized int getTotalFPSFrames()
	{
		return totalCounter;
	}
	/**@return The interval between each FPS update, in ticks.
	 * @see #setUpdateFPSFrames(int, PrintStream)*/
	public synchronized int getUpdateFPSFrames()
	{
		return updateInterval;
	}
	/**Resets the timing state for the animator. This will have the following effects:<br/>
	 * -The FPS measurements will be set to 0. {@link #getLastFPS()}, {@link #getTotalFPS()} 
	 * and their related methods will return 0 until the next FPS update is reached.<br/>
	 * -The animator will forget about any recent abnormalities in the sleep/tick cycle,
	 * such as slow, blocking IO calls. Calling this method can relieve the catch-up effect
	 * exhibited after such events, but note that this must not be called under normal operation
	 * because it will negate the animator's ability to compensate for the poor system timer/scheduler 
	 * resolutions that exist on some systems.*/
	public synchronized void resetFPSCounter()
	{
		startTimeSet = false;
		startTime = 0;
		currentTime = 0;
		lastTime = 0;
		totalCounter = 0;
		updateCounter = 0;
	}
	 /**Set the interval between each FPS update, in ticks.
	 *  An FPS update is an event where the actual FPS value is computed.
	 *  The actual FPS value is given as the average FPS in this interval.<br/>
	 *  <br/>
	 *  Furthermore, the animator will attempt to compensate for poor scheduler resolution by 
	 *  rendering rapid successive frames if the thread has slept too much within this time frame.
	 *  This is both hardware and OS dependent, so it will not be a problem on all systems.<br/>
	 *  <br/>
	 *  A possibly undesired effect of this is that if a slow, blocking call(such as disk IO) 
	 *  is made on the renderer thread, then the animator will play catch up to try to reach the
	 *  target FPS on average within the limits of this interval.<br/>
	 *  <br/>
	 *  This effect can be negated after such a blocking call by calling {@link #resetFPSCounter()}
	 *  upon completion. It can also be reduced by setting the update interval to a lower value,
	 *  however this comes with the risk of a less accurate average FPS.
	  * @param frames The new FPS update interval, in ticks.
	  * @param out An optional output stream for debug messages. May be null.
	  */
	public synchronized void setUpdateFPSFrames(int frames, PrintStream out)
	{
		updateInterval = frames;
		outStream = out;
	}
	/**@return The thread that generates the ticks.*/
	public synchronized Thread getThread()
	{
		return thread;
	}
	/**@return Whether the animator is running and not paused.
	 * @see #isStarted()
	 * @see #isPaused()*/
	public synchronized boolean isAnimating()
	{
		return isRunning && ! isPaused;
	}
	/**@return Whether the animator is paused.
	 * @see #isRunning*/
	public synchronized boolean isPaused()
	{
		return isPaused;
	}
	/**@return Whether the animator is running(has been started and not stopped).
	 * @see #isAnimating()*/
	public synchronized boolean isStarted()
	{
		return isRunning;
	}
	/**Pause the animator(while it is running)
	 * @return Whether the animator was animating({@link #isAnimating()}) before this call.
	 * @see #resume()*/
	public synchronized boolean pause()
	{
		boolean wasPaused = isPaused;
		isPaused = true;
		return !wasPaused && isRunning;
	}
	
	/**Resume the animator(while it is paused)
	 * @return Whether the animator was running and not paused before this call.
	 * @see #pause()*/
	public synchronized boolean resume()
	{
		boolean wasPaused = isPaused;
		isPaused = false;
		return wasPaused && isRunning;
	}
	/**Start the animtor. This will create the animator thread and start it. 
	 * It will begin generating {@link #tick()}s.
	 * If the animator was already started, then this will do nothing.
	 * @return Whether the animator was not started before this call.*/
	public synchronized boolean start()
	{
		if(!isRunning)
		{
			isRunning = true;
			thread = new Thread()
			{
				@Override public void run()
				{
					while(isRunning)
					{
						if(isPaused)
						{
							try {Thread.sleep(Math.max(1,1000/targetFPS));}
							catch (InterruptedException e){System.err.println("AwesomeAnimator interrupted!?");}
						}
						else
						{
							synchronized (Animator.this)
							{
								if(!startTimeSet)
								{
									startTime = System.currentTimeMillis();
									startTimeSet = true;
									lastTime = startTime;
									currentTime = startTime;
								}
								if(updateCounter>=updateInterval)
								{
									lastTime = currentTime;
									currentTime = System.currentTimeMillis();
									updateCounter = 0;
									if(outStream != null)
										outStream.println("FPS: "+Animator.this.getLastFPS());
								}
								updateCounter++;
								totalCounter++;
							}

							tick();

							final long time;
							synchronized (Animator.this)
							{
								time = (currentTime+(updateCounter*1000)/targetFPS)-System.currentTimeMillis();
							}
							//the time for the next repaint should be currentTime + updateCounter*1000/targetFPS 
							try {Thread.sleep(Math.max(0, time));}
							catch (InterruptedException e){System.err.println("AwesomeAnimator interrupted!?");}
						}
					}
				}
			};
			thread.start();
			return true;
		}
		return false;
	}
	
	/**
	 * Called when the animator wants to render a frame.
	 */
	protected abstract void tick();

	/**Stop the animator. This will destroy the thread and cease all operation.
	 * Querying the animator's state while it is stopped results in arbitrary values.<br/>
	 * An animator may be {@link #start()}ed after it has been stopped.
	 * @return Whether the animator was running before this call.*/
	public synchronized boolean stop()
	{
		boolean wasStarted = isRunning;
		thread = null;
		isRunning = false;
		return wasStarted;
	}	
}
