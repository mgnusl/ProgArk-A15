package com.devikaas.monoball.ingame.view;

import com.devikaas.monoball.ingame.model.CameraModel;
import com.devikaas.monoball.ingame.model.Player;

import owg.engine.Engine;
import owg.engine.graphics.Sprite2D;
import owg.engine.graphics.SpriteFontRenderer;
import owg.engine.util.Compass;
import owg.engine.util.V3F;

import com.devikaas.monoball.ingame.model.BallGameModel;

/**Displays textual info about the game*/
public class HUDView implements Renderable {
	private SpriteFontRenderer font;
	private BallGameModel model;
    private Sprite2D counter;
    private int stepsLimit = 30;
    private float growSpeed = 1.5f;

	public HUDView(BallGameModel model) {
		this.model = model;
		font = new SpriteFontRenderer(Engine.sprites().get("font"), 1, 1);
        counter = Engine.sprites().get("numbers");
	}

	@Override
	public void render(float alpha) {
        if (!model.isTimeout()) {
            // Get steps left for player
            int stepsLeft = model.getAlarm().get(BallGameModel.ALARM_PLAYTIME_INDEX);
            int secondsLeft = stepsLeft / Engine.getDefaultTickRate();

            CameraModel cam = model.getCamera();

            V3F camLoc = cam.getInterpolatedLocation(alpha);

            Player p = model.getCurrentPlayer();
            String playerInfo = p.getName() + "\n" + p.getScore();

            if (model.isReversed()) {
                font.render("Time:\n" + secondsLeft, cam.getWidth(), camLoc.y() + cam.getHeight(), Compass.NORTHWEST, 1, 1, (float) Math.PI);
                font.render(playerInfo, 0, camLoc.y() + cam.getHeight(), Compass.NORTHEAST, 1, 1, (float) Math.PI);
            } else {
                font.render("Time:\n" + secondsLeft, 0, camLoc.y());
                font.render(playerInfo, cam.getWidth(), camLoc.y(), Compass.NORTHEAST, 1, 1, 0);
            }


        } else {
            // Get steps left in timeout
            int stepsLeft = model.getAlarm().get(BallGameModel.ALARM_TIMEOUT_INDEX);
            int secondsLeft = stepsLeft / Engine.getDefaultTickRate();

            CameraModel cam = model.getCamera();

            V3F camLoc = cam.getInterpolatedLocation(alpha);


            float scale = 1 - (float)(stepsLeft % Engine.getDefaultTickRate()) / (float)Engine.getDefaultTickRate();

            if (model.isReversed()) {
                counter.render(secondsLeft, new V3F(cam.getWidth() / 2, camLoc.y() + (cam.getHeight() / 2), 0),
                        Compass.CENTER, scale, scale, 0);

            } else {
                counter.render(secondsLeft, new V3F(cam.getWidth() / 2, camLoc.y() + (cam.getHeight() / 2), 0),
                        Compass.CENTER, scale, scale, (float)Math.PI);

            }


        }




	}

}
