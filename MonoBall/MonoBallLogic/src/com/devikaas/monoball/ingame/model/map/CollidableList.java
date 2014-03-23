package com.devikaas.monoball.ingame.model.map;

import java.util.ArrayList;

import com.devikaas.monoball.ingame.model.Steppable;

public class CollidableList implements Steppable {
	private final ArrayList<Collidable> list;
	private final MapModel map;
	
	
	public CollidableList(MapModel map) {
		list = new ArrayList<Collidable>();
		this.map = map;
	}
	
	public void addCollidable(Collidable l) {
		list.add(l);
	}
	
	@Override
	public void step() {
		for(Collidable l : list) {
			float yMin = l.getLocation().y()-l.getRadius()+Math.min(0, l.getSpeed().y())-Row.ROW_HEIGHT;
			float yMax = l.getLocation().y()+l.getRadius()+Math.max(0, l.getSpeed().y())+Row.ROW_HEIGHT;
			Row[] rows = map.getAccessibleRows(yMin, yMax);
			
			for(Row r : rows) {
				for(Block b : r.getBlocks()) {
					b.evaluateSurface(l);
				}
			}
			map.getLeftEdge().evaluateLine(l);
			map.getRightEdge().evaluateLine(l);
			
			for(Row r : rows) {
				for(Block b : r.getBlocks()) {
					b.evaluateEndpoints(l);
				}
			}
		}
	}

}
