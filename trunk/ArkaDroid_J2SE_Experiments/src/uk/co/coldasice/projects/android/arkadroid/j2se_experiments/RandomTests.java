package uk.co.coldasice.projects.android.arkadroid.j2se_experiments;

public class RandomTests {

	private static double[] trailXs;
	private static double x;
	
	public static void main(String[] args) {
		trailXs = new double[] {1, 2, 3, 4, 5};
		debug();
		x = 123845;
		updateTrail();
		x = Math.random();
		updateTrail();
		x = Math.random();
		updateTrail();
		x = Math.random();
		updateTrail();
		x = Math.random();
		updateTrail();
	}
	
	private static void debug() {
		for (int i=0; i<trailXs.length; i++) {
			System.out.print(trailXs[i] + ", ");
		}
		System.out.println();
	}
	
	private static void updateTrail() {
		for (int i=trailXs.length-1; i>0; i--) {
			trailXs[i] = trailXs[i-1];
		}
		trailXs[0] = x;
		debug();
	}
	
}
