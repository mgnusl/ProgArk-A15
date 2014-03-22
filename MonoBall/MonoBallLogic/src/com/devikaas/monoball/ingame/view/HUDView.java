package com.devikaas.monoball.ingame.view;

import com.devikaas.monoball.ingame.model.CameraModel;
import com.devikaas.monoball.ingame.model.Player;
import owg.engine.Engine;
import owg.engine.graphics.SpriteFontRenderer;

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
	public void render() {
        // Get steps left for player
        int stepsLeft = model.getAlarm().get(BallGameModel.PLAYER_ALARM_INDEX);
        int secondsLeft = stepsLeft/Engine.scene().getAnimator().getUpdateFPSFrames();

        CameraModel cam = model.getCamera();

        font.render("Time:\n" + secondsLeft, 0, cam.getLocation().y());

        Player p = model.getCurrentPlayer();
        String playerInfo = p.getName() +"\n"+ p.getScore();
        font.render(playerInfo, cam.getWidth()/2, cam.getLocation().y());
	}

}
