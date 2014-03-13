package owg.engine.game;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import owg.engine.GameState;
/**
 * A depth-sorted list of {@link Entity<?>} objects.
 * The list delegates step and render methods to all of its members. 
 * It strives to achieve efficient and sensible handling of adding, removing and resorting instances while iterating.
 * */
public class SortedInstanceList implements GameState {
	private Entity<?>[] instances;
	private int numInstances;
	private int iterator;
	private Queue<Entity<?>> depthChangeQueue;
	
	/**Test code...*/
	public static void main(String[] args) {
		SortedInstanceList list = new SortedInstanceList();
		new Entity<SortedInstanceList>(list, 3);
		new Entity<SortedInstanceList>(list, 1);
		new Entity<SortedInstanceList>(list, 4);
		Entity<SortedInstanceList> rm1 = new Entity<SortedInstanceList>(list, -1);
		new Entity<SortedInstanceList>(list, 100);
		new Entity<SortedInstanceList>(list, 100);
		new Entity<SortedInstanceList>(list, 100);
		new Entity<SortedInstanceList>(list, 100);
		new Entity<SortedInstanceList>(list, 100);
		new Entity<SortedInstanceList>(list, 100);
		Entity<SortedInstanceList> r99_3 = new Entity<SortedInstanceList>(list, 99);
		Entity<SortedInstanceList> r99_1 = new Entity<SortedInstanceList>(list, 99);
		Entity<SortedInstanceList> r99_2 = new Entity<SortedInstanceList>(list, 99);
		new Entity<SortedInstanceList>(list, 101);
		new Entity<SortedInstanceList>(list, 101);
		new Entity<SortedInstanceList>(list, 101);
		Entity<SortedInstanceList> r1000 = new Entity<SortedInstanceList>(list, 1000);
		Entity<SortedInstanceList> r50 = new Entity<SortedInstanceList>(list, 50);
		list.removeInstance(r99_1);
		list.removeInstance(r99_2);
		list.removeInstance(r99_3);
		list.removeInstance(r50);
		list.removeInstance(rm1);
		list.removeInstance(r1000);
		
		System.out.println(list.toString());
	}
	/**Create an empty sortedInstanceList with a default initial capacity of 64.
	 * The initial capacity scales on demand, but the resize operation incurs a small overhead.*/
	public SortedInstanceList() {
		instances = new Entity<?>[64];
		numInstances = 0;
		depthChangeQueue = new LinkedList<Entity<?>>();
		iterator = 0;
	}

	@Override
	public void step() {
		for(iterator = 0; iterator < numInstances; iterator++) {
			instances[iterator].step();
		}
		updateInstanceDepths();
	}

	@Override
	public void render() {
		for(iterator = 0; iterator < numInstances; iterator++) {
			if(instances[iterator].isVisible())
				instances[iterator].render();
		}
		updateInstanceDepths();
	}
	/**
	 * Add an instance to the instance list.<br/>
	 * This method is called automatically by the Entity<?> constructor, and should not be called from elsewhere.<br/>
	 * It is legal and intended that this is called while the list is iterating through the step or render events, 
	 * but it is not thread safe.<br/>
	 * <br/> 
	 * The entity's new position will be the lowest possible index such that it fits into the depth order.<br/>
	 * The computation order follows this order, so an entity constructed by an instance with a depth that is
	 * lower than or equal to the new object's depth, then the new object will be omitted from the current iteration.
	 * @param e The Entity<?> to add to the list. <br/>
	 * The value of {@link Entity<?>#getNextDepth()} will be used to insert this instance at the proper index.<br/>
	 * It is important that the instance is not already in the list.<br/>
	 * {@link Entity<?>#depthUpdated()} Will be called when the method returns.
	 */
	void addInstance(Entity<?> e) {
		//Find a suitable slot for the instance with a binary search on depth
		int position = search(e.getNextDepth());
		//Increase the instance array if it cannot hold all the instances
		Entity<?>[] src = instances;
		if(numInstances >= instances.length) {
			instances = new Entity<?>[src.length*2];
			System.arraycopy(src, 0, instances, 0, position);
		}
		//Shift all instances with higher or equal depth up
		System.arraycopy(src, position, instances, position+1, numInstances-position);
		//Shift the iterator if we affected the future or current data
		if(iterator >= position)
			iterator++;

		//Insert the new instance
		instances[position] = e;
		//Increment instance count
		numInstances++;
		e.depthUpdated();
	}
	/**
	 * Signal to the list that the instance depth has changed.<br>
	 * <br>
	 * It is legal and intended that this is called while the list is iterating through the step or render events, 
	 * but it is not thread safe.<br/>
	 * <br>
	 * Note that the instance's position the the instance list is not actually changed until the end of the 
	 * current iteration. This is to ensure that all objects always receive their step/render events once
	 * and exactly once.<br>
	 * <br>
	 * It is legal, but not optimal to call this multiple times for an instance during the same iteration.
	 * The latest nextDepth value will be used in any case.
	 * @param entity entity The Entity<?> whose depth is changing.<br/> 
	 * The entity must already be in the list, or an IllegalStateException will be thrown.<br/>
	 * The entity's current list position must be in accordance with {@link Entity<?>#getCurrentDepth()}.<br/>
	 * The entity's new depth will be computed according to {@link Entity<?>#getNextDepth()}.<br/>
	 * {@link Entity<?>#depthUpdated()} Will be called when the depth changes, after the current iteration.
	 */
	void changeInstanceDepth(Entity<?> entity) {
		depthChangeQueue.offer(entity);
	}
	/**Call to update the instance list to reflect the new depth order. 
	 * Objects registered with {@link #changeInstanceDepth(Entity<?>)} will have their list positions changed.*/
	private void updateInstanceDepths() {
		Entity<?> e;
		while((e = depthChangeQueue.poll())!=null) {
			removeInstance(e);
			addInstance(e);
		}
	}

	/**
	 * Remove an instance that is known to be in the list.<br/>
	 * This method is called automatically by {@link Entity<?>#destroy()}.<br/>
	 * It is legal and intended that this is called while the list is iterating through the step or render events, 
	 * but it is not thread safe.<br/>
	 * Objects stop receiving step and render events immediately from the moment they are removed.
	 * @param entity The Entity<?> to remove.<br/> 
	 * The entity must already be in the list, or an IllegalStateException will be thrown.<br/>
	 * Furthermore, the entity's list position must be in accordance with its current depth value, as given by
	 * {@link Entity<?>#getCurrentDepth()}. 
	 */
	void removeInstance(Entity<?> entity) {
		int depth = entity.getCurrentDepth();
		//Find the lowest possible index where the instance might be
		int position = search(depth);
		while(instances[position] != entity) {
			position++;
			//It is impossible to encounter an instance with a different depth here.  
			if (instances[position].getCurrentDepth()!=depth) {
				throw new IllegalStateException("Corrupt instance list or missing entity: "+
						toString()+"- stranded on "+position+"while trying to find: "+entity.toString()+": "+depth);
			}
		}
		//Shift all instances with higher or equal depth down
		System.arraycopy(instances, position+1, instances, position, numInstances-(position+1));
		//Shift the iterator if we affected the future or current data
		if(iterator >= position)
			iterator--;
		
		//Decrement instance count
		numInstances--;
		//Null the instance above the upper bound
		instances[numInstances] = null;
	}
	/**Returns the index of the first object that has a depth that is not higher than the indicated depth.
	 * The first objects have the highest depth. 
	 * The returned index may be used to insert an element into the list, keeping it sorted,
	 * or to find the approximate location of a particular object in log(n) time.*/
	private int search(final int targetDepth) {
		//Base case
		if(numInstances == 0)
			return 0;
		//Compute the number of iterations it will take until we have visited all possible indices
		final int its = 1+(int)(Math.log(numInstances)/Math.log(2)+1e-10);
		
		boolean found = false;
		int minimum = 0;
		int maximum = numInstances;
		int it = 0;
		int pos = 0;
		//Binary search in a list reverse-sorted on depth
		binarySearch:
		while(it<its) {
			pos = (int)((minimum+maximum)/2);//Floor
			final int currentDepth = instances[pos].getCurrentDepth();
			//Check if this is the first element with a depth value that is no more than the target
			if (currentDepth <= targetDepth && (pos==0 || instances[pos-1].getCurrentDepth()>targetDepth)) {
				found = true;
				break binarySearch;
			}
			//If the depth here is too high, then we need to go further into the list.
			if(currentDepth>targetDepth)
				minimum = pos;
			else //If the depth is lower than or equal to the target, step closer to the start.
				maximum = pos;
			it+=1;
		}
		//If we did not meet the condition in the predetermined number of steps, 
		//then that must mean that we reached the end of the list, or that the list is not sorted
		if(!found) {
			if(pos == numInstances-1)
				pos++;
			else {
				throw new IllegalStateException("Corrupt instance list: "+toString()+"- landed on "+pos+
						"while trying to find: "+targetDepth);
			}
		}
		return pos;
	}
	/**Returns a new array containing the subset of the instance list that can be cast to the given type.*/
	public<T> T[] getInstances(Class<T> type) {
		ArrayList<T> r = new ArrayList<T>();
		for(int i = 0; i<numInstances; i++) {
			if(type.isInstance(instances[i]))
				r.add(type.cast(instances[i]));
		}
		@SuppressWarnings("unchecked")
		T[] returnVal = (T[]) Array.newInstance(type, r.size());
		r.toArray(returnVal);
		return returnVal;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(": ");
		if(numInstances == 0)
			sb.append("Empty");
		else {
			sb.append(instances[0].toString());
			sb.append(": ");
			sb.append(instances[0].getCurrentDepth());
			for(int i = 1; i<numInstances; i++) {
				sb.append(", ");
				sb.append(instances[i].toString());
				sb.append(": ");
				sb.append(instances[i].getCurrentDepth());
			}
		}
		return sb.toString();
	}
}
