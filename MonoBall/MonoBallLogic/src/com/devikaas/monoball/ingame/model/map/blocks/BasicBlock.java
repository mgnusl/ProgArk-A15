package com.devikaas.monoball.ingame.model.map.blocks;

import owg.engine.util.Compass;
import owg.engine.util.Kryo;
import owg.engine.util.V3F;

import com.devikaas.monoball.ingame.model.SpriteModel;
import com.devikaas.monoball.ingame.model.map.Block;
import com.devikaas.monoball.ingame.model.map.Collidable;
import com.devikaas.monoball.ingame.model.map.Row;
import com.devikaas.monoball.ingame.model.map.SolidLine;

public class BasicBlock implements Block, SpriteModel {
    public final static char TYPE = 'a';
    private float friction;
	
	private V3F topLeft;
	private float width, height;
	protected SolidLine[] lines;
	public BasicBlock(Row row, float xOffset, float width) {
		if (Float.isInfinite(xOffset) || Float.isNaN(xOffset))
			throw new IllegalArgumentException("Invalid offset: "+xOffset);
			
		row.pushBlock(this);
		
		this.topLeft = row.getLocation().clone().add(xOffset, 0, 0);
		this.width = width;
		this.height = Row.ROW_HEIGHT;
        this.friction = 1;
		
		lines = new SolidLine[4];
		V3F topRight = topLeft.clone().add(width, 0, 0);
		V3F bottomLeft = topLeft.clone().add(0, height, 0);
		V3F bottomRight = topLeft.clone().add(width, height, 0);
		lines[0] = new SolidLine(topRight, topLeft, friction);
		lines[1] = new SolidLine(topLeft, bottomLeft, friction);
		lines[2] = new SolidLine(bottomLeft, bottomRight, friction);
		lines[3] = new SolidLine(bottomRight, topRight, friction);
	}
	
	@Kryo
	protected BasicBlock() {
	}

	@Override
	public String getSprite() {
		return "block-basic";
	}

	@Override
	public int getSubimage() {
		return 0;
	}

	@Override
	public V3F getSpriteLocation(float alpha) {
		return topLeft;
	}

	@Override
	public Compass getOrientation() {
		return Compass.NORTHWEST;
	}

	@Override
	public float getXScale() {
		return width/64f;
	}

	@Override
	public float getYScale() {
		return height/64f;
	}

	@Override
	public float getAngle() {
		return 0;
	}

	@Override
	public void evaluateSurface(Collidable subject) {
		for(SolidLine l : lines)
			l.evaluateLine(subject);
	}

	@Override
	public void evaluateEndpoints(Collidable subject) {
		for(SolidLine l : lines)
			l.evaluateEndpoints(subject);
	}

    public void setFriction(float friction) {
        this.friction = friction;
    }

}
