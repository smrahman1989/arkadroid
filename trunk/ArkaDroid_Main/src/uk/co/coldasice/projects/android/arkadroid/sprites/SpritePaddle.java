package uk.co.coldasice.projects.android.arkadroid.sprites;

import uk.co.coldasice.projects.android.arkadroid.ArkaDroidGameThread.Moving;
import uk.co.coldasice.projects.android.arkadroid.controllers.GameRenderer;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public class SpritePaddle extends Sprite {
	
	private double paddleDx_mag = 0;
	private Moving moving = Moving.NO;
	
	public SpritePaddle(Drawable drawable, GameRenderer renderer) {
		super(drawable, renderer);
	}
	
	public void movePaddle(Moving moving, double speed) {
		this.moving = moving;
		paddleDx_mag = speed;
	}

	@Override
	protected void postDraw(Canvas canv) { }

	@Override
	protected void preDraw(Canvas canv) { }

	public void update(double timediff) {
		if(moving==Moving.LEFT){
			setX(x - (paddleDx_mag*timediff));
			//paddleDx_mag = Math.max(0, paddleDx_mag - 1);
		}else if(moving==Moving.RIGHT){
			setX(x + (paddleDx_mag*timediff));
			//paddleDx_mag = Math.min(0, paddleDx_mag + 1);
		}		
	}
}
