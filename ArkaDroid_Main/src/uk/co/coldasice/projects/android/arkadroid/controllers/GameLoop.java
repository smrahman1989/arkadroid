package uk.co.coldasice.projects.android.arkadroid.controllers;

import uk.co.coldasice.projects.android.arkadroid.controllers.GameState;
import uk.co.coldasice.projects.android.arkadroid.sprites.SpriteBrick;

public class GameLoop {
	private static final double PHYSICS_SPEED = 13.0;
	
	private GameState gameState;
	private boolean lastHitPaddle = false;
	double timediff;
	public long lastupdate;
	
	public GameLoop(GameState state) {
		this.gameState = state;
	}
	
	public void reset() {
		lastupdate = System.currentTimeMillis();
		lastHitPaddle = false;
	}

	public void updateGame(GameRenderer renderer) {
		long now = System.currentTimeMillis();
		timediff = (now - lastupdate) / PHYSICS_SPEED;
		lastupdate = now;
		
		if (!lastHitPaddle && gameState.ball.collidesWith(gameState.paddle)) {
			double ballMidx = gameState.ball.getMidX();
			double paddleMidx = gameState.paddle.getMidX();
			
			// get the diff between the mid points
			double diff = paddleMidx - ballMidx;
			if (paddleMidx - ballMidx == 0) {
				 gameState.ball.dy *= -1;
			}
			else {
				// if it's <0, that means spin the ball left a bit
				double maxSpin = gameState.paddle.getW() / 2;
				double spin = diff / maxSpin;
				// get the original ball speed
				double speed = gameState.ball.speed();
				double shootAngle = 90 - (90 * spin);
				double new_ballDy = Math.sin(Math.toRadians(shootAngle)) * speed;
				double new_ballDx = 0 - Math.cos(Math.toRadians(shootAngle)) * speed;
				
				gameState.ball.dx = new_ballDx;
				if (new_ballDy > 0) new_ballDy = -new_ballDy;
				gameState.ball.dy = new_ballDy;
			}
			// speed up the ball a bit
			if (gameState.ball.dx < 0) gameState.ball.dx -= 0.2;
			else gameState.ball.dx += 0.2;
			if (gameState.ball.dy < 0) gameState.ball.dy -= 0.2;
			else gameState.ball.dy += 0.2;
			lastHitPaddle=true;
		}
		
		boolean allDead = true;
		for (SpriteBrick brick: gameState.bricks) {
			if (brick.dead()) continue;
			allDead = false;
			if (brick.collidesWith(gameState.ball)) {
				// don't change the format of this; it's used in the j2se experiments bit
				// Log.d("updateGame() - deadbrick", "ballPos: " + spriteBall.getBounds() + ", brickPos: " + brick.getBounds() + ", ballDirection: " + ballDx + ", " + ballDy);
				gameState.ball.dy *= -1;
				brick.kill();
				// add one to the current run (how many bricks killed in a row)
				gameState.bricksKilledInARow++;
				lastHitPaddle = false;
				break;
			}
		}
		
		if (allDead) {
			gameState.winner = true;
			gameState.setPaused();
			return;
		}
		
		double ballDxForTime = gameState.ball.dx*timediff;
		double ballDyForTime = gameState.ball.dy*timediff;
		
		if (ballDxForTime > 0 && (gameState.ball.getX() + gameState.ball.getW() + ballDxForTime >= renderer.getW())){ gameState.ball.dx *= -1; lastHitPaddle = false; }
		else if (ballDxForTime < 0 && (gameState.ball.getX() + ballDxForTime <= 0)){ gameState.ball.dx *= -1; lastHitPaddle = false;}
		
		if (ballDyForTime < 0 && (gameState.ball.getY() + ballDyForTime <= 0)){ gameState.ball.dy *= -1; lastHitPaddle = false;}
		// ball died?
		if (ballDyForTime > 0 && (gameState.ball.getY() + gameState.ball.getH() > gameState.paddle.getY() + 2)) {
			gameState.setPaused();
		}

		gameState.ball.setX(gameState.ball.getX() + ballDxForTime);
		gameState.ball.setY(gameState.ball.getY() + ballDyForTime);
		// Log.d("ArkaDroidGameThread.updateGame()", "paddleDirection: " + paddleDirection + ", paddleDx_mag: " + paddleDx_mag);
		
		gameState.paddle.update(timediff);
	}

}
