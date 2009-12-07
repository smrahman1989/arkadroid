package uk.co.coldasice.projects.android.arkadroid.sprites;

import uk.co.coldasice.projects.android.arkadroid.controllers.GameRenderer;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;


public class SpriteBrick extends Sprite {
	private ShapeDrawable colour;
	
	public SpriteBrick(Drawable drawable, GameRenderer renderer, int brickColour) {
		super(drawable, renderer);
		colour = new ShapeDrawable(new RectShape());
		colour.getPaint().setColor(0x66000000+brickColour);
	}

	@Override
	protected void postDraw(Canvas canv) {
		this.colour.setBounds((int)x+3, (int)y+2, (int)(x+w)-3, (int)(y+h)-3);
		this.colour.draw(canv);
	}

	@Override
	protected void preDraw(Canvas canv) {
		// nothing needed at the moment
	}
	
}
