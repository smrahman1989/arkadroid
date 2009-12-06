package uk.co.coldasice.projects.android.arkadroid.j2se_experiments;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;

import uk.co.coldasice.projects.android.arkadroid.j2se_experiments.Experiments.ExperimentValues;
import uk.co.coldasice.projects.android.arkadroid.j2se_experiments.Experiments.Rect;

public class ExperimentCanvas extends Canvas {

	private static final long serialVersionUID = 1L;
	private ExperimentValues experimentValues;
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (experimentValues == null) return;
		double dx = experimentValues.ballDx;
		double dy = experimentValues.ballDy;
		Rect ball = experimentValues.ball;
		Rect brick = experimentValues.brick;
		g.setColor(Color.green);
		g.drawLine((int)(ball.left - dx*50), (int)(ball.top - dy*50), ball.left, ball.top);
		g.drawLine((int)(ball.left - dx*50), (int)(ball.bottom - dy*50), ball.left, ball.bottom);
		g.drawLine((int)(ball.right - dx*50), (int)(ball.top - dy*50), ball.right, ball.top);
		g.drawLine((int)(ball.right - dx*50), (int)(ball.bottom - dy*50), ball.right, ball.bottom);
		drawRect(Color.red, g, ball);
		drawRect(Color.blue, g, brick);
	}

	public static void drawRect (Color c, Graphics g, Rect r) {
		g.setColor(c);
		g.drawRect(r.left, r.top, r.right-r.left, r.bottom-r.top);
	}

	public void setActiveExperimentValues(ExperimentValues experimentValues) {
		this.experimentValues = experimentValues;
	}

}
