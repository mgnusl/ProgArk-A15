package com.devikaas.monoball.ingame.view;

import com.devikaas.monoball.ingame.model.CameraModel;
import com.devikaas.monoball.ingame.model.Player;

import owg.engine.Engine;
import owg.engine.graphics.SpriteFontRenderer;
import owg.engine.util.Compass;
import owg.engine.util.V3F;

import com.devikaas.monoball.ingame.model.BallGameModel;

/**Displays textual info about the game*/
public class HUDView implements Renderable {
	private SpriteFontRenderer font;
	private BallGameModel model;

	public HUDView(BallGameModel model) {
		this.model = model;
		font = new SpriteFontRenderer(Engine.sprites().get("font"), 1, 1);

	}

	@Override
	public void render(float alpha) {
        // Get steps left for player
        int stepsLeft = model.getAlarm().get(BallGameModel.PLAYER_ALARM_INDEX);
        int secondsLeft = stepsLeft/Engine.getDefaultTickRate();

        CameraModel cam = model.getCamera();

        V3F camLoc = cam.getInterpolatedLocation(alpha);

		if(!model.isReversed()){
        	font.render("Time:\n" + secondsLeft, 0, camLoc.y());

			Player p = model.getCurrentPlayer();
			String playerInfo = p.getName() +"\n"+ p.getScore();
			font.render(playerInfo, cam.getWidth(), camLoc.y(),Compass.NORTHEAST, 1, 1, 0);
		}else{
			font.render("Time:\n" + secondsLeft, cam.getWidth(), camLoc.y() + cam.getHeight(), Compass.NORTHWEST, 1, 1, (float)Math.PI);
			Player p = model.getCurrentPlayer();
			String playerInfo = p.getName() +"\n"+ p.getScore();
			font.render(playerInfo, 0, camLoc.y() + cam.getHeight(), Compass.NORTHEAST, 1, 1, (float)Math.PI);
		}


	}

}
