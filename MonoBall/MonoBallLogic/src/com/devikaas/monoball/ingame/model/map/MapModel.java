package com.devikaas.monoball.ingame.model.map;

import java.util.ArrayList;

import com.devikaas.monoball.ingame.model.BallGameModel;
import com.devikaas.monoball.ingame.model.Steppable;

import owg.engine.util.Calc;
import owg.engine.util.Kryo;
import owg.engine.util.V3F;

public class MapModel implements Steppable {
	public static final float MAP_WIDTH = 320;
	public static final float MAP_X = 0;
	
	private final BallGameModel ballGameModel;
	
	/**Global list of rows.*/
	private final ArrayList<Row> rows;
	/**The index in the row list where the row at y position 0 will be.*/
	int originIndex;
	
	/**The map generator which will spawn new rows on demand.*/
	private final MapGenerator generator;
	/**The edges of the map, used to prevent objects from leaving the game area.*/
	private final SolidLine leftEdge, rightEdge;
	
	@Kryo
	private MapModel() {
		ballGameModel=null;generator=null;rows=null;rightEdge=null;leftEdge=null;
	}
	
	/**Create a new, empty map model.
	 * The generator will be queried lazily to spawn the map.*/
	public MapModel(BallGameModel ballGameModel, MapGenerator generator) {
		this.ballGameModel = ballGameModel;
		this.generator = generator;
		
		rows = new ArrayList<Row>();
		originIndex = 0;
		
		leftEdge =  new SolidLine(	new V3F(MAP_X, ballGameModel.getCamera().getHeight(), 0),
									new V3F(MAP_X, 0, 0), 
									1);
		rightEdge = new SolidLine(	new V3F(MAP_X+MAP_WIDTH, 0, 0), 
									new V3F(MAP_X+MAP_WIDTH, ballGameModel.getCamera().getHeight(), 0), 
									1);
	}
	/**Pushes a row to the top or bottom of the row list. This is called automatically by the row constructor.*/
	void pushRow(Row r, boolean bottom) {
		if(bottom)
			rows.add(r);
		else {
			rows.add(0, r);
			originIndex++;
		}
	}
	
	/**Returns all rows that might be accessible given the indicated minimum and maximum y coordinates in world space.*/
	public Row[] getAccessibleRows(float minY, float maxY) {
		//Transpose the coordinates down to be in line with the row list
		minY += Row.ROW_HEIGHT*originIndex;
		maxY += Row.ROW_HEIGHT*originIndex;
		
		int minIndex = Calc.clamp((int)(minY/Row.ROW_HEIGHT), 0, rows.size());
		int maxIndex = Calc.clamp(1+(int)(maxY/Row.ROW_HEIGHT), 0, rows.size());
		
		Row[] r = new Row[maxIndex-minIndex];
		rows.subList(minIndex, maxIndex).toArray(r);
		return r;
	}
	/**Returns the index in the row list where the row at y position 0 will be.
	 * Rows before this index will be above 0, rows after will be below 0 in world space.*/
	public int getOriginIndex() {
		return this.originIndex;
	}
	/**Returns the total number of rows that have been generated into the row list.*/
	public int getNumRows() {
		return this.rows.size();
	}
	@Override
	public void step() {
		//Move the edge lines to match the camera y-position
		leftEdge.getLocation().y(ballGameModel.getCamera().getCurrentLocation().y()+ballGameModel.getCamera().getHeight());
		rightEdge.getLocation().y(ballGameModel.getCamera().getCurrentLocation().y());
		
		while(ballGameModel.getCamera().getCurrentLocation().y() < getMinimumGeneratedY())
			generator.generateChunk(this, false);
		while(ballGameModel.getCamera().getCurrentLocation().y()+ballGameModel.getCamera().getHeight() > getMaximumGeneratedY())
			generator.generateChunk(this, true);
	}
	/**Returns the y-position of the upper edge of the uppermost generated row in world space.*/
	private float getMinimumGeneratedY() {
		return -originIndex*Row.ROW_HEIGHT;
	}
	/**Returns the y-position of the <b>lower</b> edge of the lowermost generated row in world space.*/
	private float getMaximumGeneratedY() {
		return rows.size()-originIndex*Row.ROW_HEIGHT;
	}
	/**Returns a line covering the left edge of the map.*/
	public SolidLine getLeftEdge() {
		return leftEdge;
	}
	
	/**Returns a line covering the right edge of the map.*/
	public SolidLine getRightEdge() {
		return rightEdge;
	}
}
