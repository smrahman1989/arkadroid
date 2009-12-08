package uk.co.coldasice.projects.mobile.lib.fpmath;

/**
 * Simple class which uses 32 bit ints, and 16:16 bits for fixed-point arithmetic
 * @author Seb
 *
 */
public class FPMath {

	public static final int PI = 205887;
	public static final int PI_BY_2 = PI*2;
	public static final int PI_OVER_180 = 1143;
	
	public static final int mul (int x, int y) {
		return (int)(((long)x * (long)y) >> 16);
	}
	
	public static final int div (int x, int y) {
		return (int)(((((long)x) << 32) / y) >> 16);
	}
	
	/**
	 * @param x angle in degrees
	 * @return
	 */
	public static final int sin (int fp) {
		return FPTrigTables.equate(fp, FPTrigTables.SINTAB);
	}
	
	public static final int cos (int x) {
		return FPTrigTables.equate(x, FPTrigTables.COSTAB);
	}
	
	public static final int toInt (int x) {
		return x >> 16;
	}
	
	public static final int getFrac (int x) {
		return x & 0xffffffff;
	}
	
	public static final int toFP (int x) {
		return x << 16;
	}
	
}
