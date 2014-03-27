package com.devikaas.monoball.ingame.view;

import com.devikaas.monoball.ingame.controller.SystemKeyController;
import com.devikaas.monoball.ingame.model.CameraModel;
import com.devikaas.monoball.ingame.model.Player;

import owg.engine.Engine;
import owg.engine.graphics.Sprite2D;
import owg.engine.graphics.SpriteFontRenderer;
import owg.engine.util.Compass;
import owg.engine.util.V3F;

import com.devikaas.monoball.ingame.model.BallGameModel;


import static owg.engine.Engine.sprites;

/**Displays textual info about the game*/
public class HUDView implements Renderable {
	private SpriteFontRenderer font;
	private BallGameModel model;
    private Sprite2D heartSprite;

    private float heartAspectRatio = 0.5f;
    private float heartRightOffset = 5f;
    private float heartSpacing = 5f;
    private float heartTopOffset = 40f;

	public HUDView(BallGameModel model) {
		this.model = model;
		font = new SpriteFontRenderer(Engine.sprites().get("font"), 1, 1);


        heartSprite = sprites().get("heart");
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

            //Render hearts representing lives
            float heartStartY =camLoc.y()+heartTopOffset;
            float heartSpace = heartSprite.getHeight()*heartAspectRatio+heartSpacing;

            for(int i=0;i<p.getLives();i++){
                heartSprite.render(0,
                        new V3F(cam.getWidth()-heartRightOffset,
                                heartStartY+(i*heartSpace)
                                ,0),
                        Compass.NORTHEAST,
                        heartAspectRatio,
                        heartAspectRatio,
                        0);
            }

		}else{
			font.render("Time:\n" + secondsLeft, cam.getWidth(), camLoc.y() + cam.getHeight(), Compass.NORTHWEST, 1, 1, (float)Math.PI);
			Player p = model.getCurrentPlayer();
			String playerInfo = p.getName() +"\n"+ p.getScore();
			font.render(playerInfo, 0, camLoc.y() + cam.getHeight(), Compass.NORTHEAST, 1, 1, (float)Math.PI);

            //Render hearts representing lives
            float heartStartY = camLoc.y()+cam.getHeight()-heartTopOffset;
            float heartSpace = heartSprite.getHeight()*heartAspectRatio+heartSpacing;

            for(int i=0;i<p.getLives();i++){
                heartSprite.render(0,
                        new V3F(0+heartRightOffset,
                                heartStartY-(i*heartSpace)
                                ,0),
                        Compass.NORTHEAST,
                        heartAspectRatio*-1,
                        heartAspectRatio*-1,
                        0);
            }
		}


	}

}
