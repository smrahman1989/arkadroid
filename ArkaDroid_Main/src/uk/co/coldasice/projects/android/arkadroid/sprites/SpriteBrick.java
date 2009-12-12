package uk.co.coldasice.projects.android.arkadroid.sprites;

import uk.co.coldasice.projects.android.arkadroid.controllers.GameRenderer;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;


public class SpriteBrick extends Sprite {
	private ShapeDrawable colour;
	
	private boolean killed = false;
	
	int alpha = 255;
	
	public SpriteBrick(Drawable drawable, GameRenderer renderer, int brickColour, double _x, double _y) {
		super(drawable, renderer, _x, _y);
		colour = new ShapeDrawable(new RectShape());
		colour.setBounds((int)x+3, (int)y+2, (int)(x+w)-3, (int)(y+h)-3);
		colour.getPaint().setColor(0x66000000+brickColour);
	}

	@Override
	protected void postDraw(Canvas canv) {
		this.colour.setAlpha(alpha);
		this.colour.draw(canv);
	}

	@Override
	protected void preDraw(Canvas canv) {
		// nothing needed at the moment
	}
	
	@Override
	public void draw(Canvas canv) {
		if (killed && alpha<0) return;
		//preDraw(canv);
		this.drawable.setBounds((int)x, (int)y, (int)(x+w), (int)(y+h));
		this.drawable.setAlpha(alpha);
		this.drawable.draw(canv);
		postDraw(canv);
		if (killed) alpha -=64;
	}
	
	public void kill() {
		killed = true;
	}
	
	public void unkill() {
		killed  = false;
		alpha = 255;
	}
	
	public boolean dead() {
		return killed;
	}
	
}
