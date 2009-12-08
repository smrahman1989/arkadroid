package uk.co.coldasice.projects.mobile.lib.fpmath;

import uk.co.coldasice.projects.mobile.mobilerubix.types.Point;

public class FPMatrix {
	public static final int ONE = 1 << 16;
	public int xx, xy, xz, xo;
	public int yx, yy, yz, yo;
	public int zx, zy, zz, zo;

	/** Create a new unit matrix */
	public FPMatrix() {
		unit();
	}
	
	public FPMatrix copy() {
		FPMatrix ret = new FPMatrix();
		ret.xx = xx;
		ret.xy = xy;
		ret.xz = xz;
		ret.xo = xo;
		ret.yx = yx;
		ret.yy = yy;
		ret.yz = yz;
		ret.yo = yo;
		ret.zx = zx;
		ret.zy = zy;
		ret.zz = zz;
		ret.zo = zo;
		return ret;
	}

	/** Scale by f in all dimensions */
	public void scale(int f) {
		xx = FPMath.mul(xx, f);
		xy = FPMath.mul(xy, f);
		xz = FPMath.mul(xz, f);
		xo = FPMath.mul(xo, f);
		yx = FPMath.mul(yx, f);
		yy = FPMath.mul(yy, f);
		yz = FPMath.mul(yz, f);
		yo = FPMath.mul(yo, f);
		zx = FPMath.mul(zx, f);
		zy = FPMath.mul(zy, f);
		zz = FPMath.mul(zz, f);
		zo = FPMath.mul(zo, f);
	}

	/** Scale along each axis independently */
	public void scale(int xf, int yf, int zf) {
		xx = FPMath.mul(xx, xf);
		xy = FPMath.mul(xy, xf);
		xz = FPMath.mul(xz, xf);
		xo = FPMath.mul(xo, xf);
		yx = FPMath.mul(yx, yf);
		yy = FPMath.mul(yy, yf);
		yz = FPMath.mul(yz, yf);
		yo = FPMath.mul(yo, yf);
		zx = FPMath.mul(zx, zf);
		zy = FPMath.mul(zy, zf);
		zz = FPMath.mul(zz, zf);
		zo = FPMath.mul(zo, zf);
	}

	/** Translate the origin */
	public FPMatrix translate(int x, int y, int z) {
		xo += x;
		yo += y;
		zo += z;
		return this;
	}

	/** rotate theta degrees about the y axis */
	public void yrot(int fpRads) {
		int ct = FPMath.cos(fpRads);
		int st = FPMath.sin(fpRads);

		int Nxx = (FPMath.mul(xx, ct) + FPMath.mul(zx, st));
		int Nxy = (FPMath.mul(xy, ct) + FPMath.mul(zy, st));
		int Nxz = (FPMath.mul(xz, ct) + FPMath.mul(zz, st));
		int Nxo = (FPMath.mul(xo, ct) + FPMath.mul(zo, st));

		int Nzx = (FPMath.mul(zx, ct) - FPMath.mul(xx, st));
		int Nzy = (FPMath.mul(zy, ct) - FPMath.mul(xy, st));
		int Nzz = (FPMath.mul(zz, ct) - FPMath.mul(xz, st));
		int Nzo = (FPMath.mul(zo, ct) - FPMath.mul(xo, st));

		xo = Nxo;
		xx = Nxx;
		xy = Nxy;
		xz = Nxz;
		zo = Nzo;
		zx = Nzx;
		zy = Nzy;
		zz = Nzz;
	}

	/** rotate theta degrees about the x axis */
	public void xrot(int fpRads) {
		int ct = FPMath.cos(fpRads);
		int st = FPMath.sin(fpRads);

		int Nyx = (FPMath.mul(yx, ct) + FPMath.mul(zx, st));
		int Nyy = (FPMath.mul(yy, ct) + FPMath.mul(zy, st));
		int Nyz = (FPMath.mul(yz, ct) + FPMath.mul(zz, st));
		int Nyo = (FPMath.mul(yo, ct) + FPMath.mul(zo, st));

		int Nzx = (FPMath.mul(zx, ct) - FPMath.mul(yx, st));
		int Nzy = (FPMath.mul(zy, ct) - FPMath.mul(yy, st));
		int Nzz = (FPMath.mul(zz, ct) - FPMath.mul(yz, st));
		int Nzo = (FPMath.mul(zo, ct) - FPMath.mul(yo, st));

		yo = Nyo;
		yx = Nyx;
		yy = Nyy;
		yz = Nyz;
		zo = Nzo;
		zx = Nzx;
		zy = Nzy;
		zz = Nzz;
	}

	/** rotate theta degrees about the z axis */
	public void zrot(int fpRads) {
		int ct = FPMath.cos(fpRads);
		int st = FPMath.sin(fpRads);

		int Nyx = (FPMath.mul(yx, ct) + FPMath.mul(xx, st));
		int Nyy = (FPMath.mul(yy, ct) + FPMath.mul(xy, st));
		int Nyz = (FPMath.mul(yz, ct) + FPMath.mul(xz, st));
		int Nyo = (FPMath.mul(yo, ct) + FPMath.mul(xo, st));

		int Nxx = (FPMath.mul(xx, ct) - FPMath.mul(yx, st));
		int Nxy = (FPMath.mul(xy, ct) - FPMath.mul(yy, st));
		int Nxz = (FPMath.mul(xz, ct) - FPMath.mul(yz, st));
		int Nxo = (FPMath.mul(xo, ct) - FPMath.mul(yo, st));

		yo = Nyo;
		yx = Nyx;
		yy = Nyy;
		yz = Nyz;
		xo = Nxo;
		xx = Nxx;
		xy = Nxy;
		xz = Nxz;
	}

	/** Multiply this matrix by a second: M = M*R */
	public FPMatrix mult(FPMatrix rhs) {
		int lxx = FPMath.mul(xx, rhs.xx) + FPMath.mul(yx, rhs.xy) + FPMath.mul(zx, rhs.xz);
		int lxy = FPMath.mul(xy, rhs.xx) + FPMath.mul(yy, rhs.xy) + FPMath.mul(zy, rhs.xz);
		int lxz = FPMath.mul(xz, rhs.xx) + FPMath.mul(yz, rhs.xy) + FPMath.mul(zz, rhs.xz);
		int lxo = FPMath.mul(xo, rhs.xx) + FPMath.mul(yo, rhs.xy) + FPMath.mul(zo, rhs.xz) + rhs.xo;

		int lyx = FPMath.mul(xx, rhs.yx) + FPMath.mul(yx, rhs.yy) + FPMath.mul(zx, rhs.yz);
		int lyy = FPMath.mul(xy, rhs.yx) + FPMath.mul(yy, rhs.yy) + FPMath.mul(zy, rhs.yz);
		int lyz = FPMath.mul(xz, rhs.yx) + FPMath.mul(yz, rhs.yy) + FPMath.mul(zz, rhs.yz);
		int lyo = FPMath.mul(xo, rhs.yx) + FPMath.mul(yo, rhs.yy) + FPMath.mul(zo, rhs.yz) + rhs.yo;

		int lzx = FPMath.mul(xx, rhs.zx) + FPMath.mul(yx, rhs.zy) + FPMath.mul(zx, rhs.zz);
		int lzy = FPMath.mul(xy, rhs.zx) + FPMath.mul(yy, rhs.zy) + FPMath.mul(zy, rhs.zz);
		int lzz = FPMath.mul(xz, rhs.zx) + FPMath.mul(yz, rhs.zy) + FPMath.mul(zz, rhs.zz);
		int lzo = FPMath.mul(xo, rhs.zx) + FPMath.mul(yo, rhs.zy) + FPMath.mul(zo, rhs.zz) + rhs.zo;

		xx = lxx;
		xy = lxy;
		xz = lxz;
		xo = lxo;

		yx = lyx;
		yy = lyy;
		yz = lyz;
		yo = lyo;

		zx = lzx;
		zy = lzy;
		zz = lzz;
		zo = lzo;
		return this;
	}
	
//	public boolean isUnit() {
//		if (xo == 0 && xx == ONE && xy == 0  &&	xz == 0 &&
//				yo == 0 && yx == 0 && yy == ONE && yz == 0 &&
//				zo == 0 && zx == 0 && zy == 0 && zz == ONE) return true;
//		return false;
//	}

	/** Reinitialize to the unit matrix */
	public void unit() {
		xo = 0;
		xx = ONE;
		xy = 0;
		xz = 0;
		yo = 0;
		yx = 0;
		yy = ONE;
		yz = 0;
		zo = 0;
		zx = 0;
		zy = 0;
		zz = ONE;
	}

	/**
	 * Transform nvert points from v into tv. v contains the input coordinates
	 * in inting point. Three successive entries in the array constitute a
	 * point. tv ends up holding the transformed points as integers; three
	 * successive entries per point
	 */
	public void transform(int v[], int tv[], int nvert) {
		int lxx = xx, lxy = xy, lxz = xz, lxo = xo;
		int lyx = yx, lyy = yy, lyz = yz, lyo = yo;
		int lzx = zx, lzy = zy, lzz = zz, lzo = zo;
		for (int i = nvert * 3; (i -= 3) >= 0;) {
			int x = v[i];
			int y = v[i + 1];
			int z = v[i + 2];
			tv[i] = (FPMath.mul(x, lxx) + FPMath.mul(y, lxy) + FPMath.mul(z, lxz) + lxo);
			tv[i + 1] = (FPMath.mul(x, lyx) + FPMath.mul(y, lyy) + FPMath.mul(z, lyz) + lyo);
			tv[i + 2] = (FPMath.mul(x, lzx) + FPMath.mul(y, lzy) + FPMath.mul(z, lzz) + lzo);
		}
	}
	
	public void transform(Point[] v, Point[] tv, int nvert) {
		int lxx = xx, lxy = xy, lxz = xz, lxo = xo;
		int lyx = yx, lyy = yy, lyz = yz, lyo = yo;
		int lzx = zx, lzy = zy, lzz = zz, lzo = zo;
		for (int i = nvert; (i -= 1) >= 0;) {
			int x = v[i].coords[0];
			int y = v[i].coords[1];
			int z = v[i].coords[2];
			tv[i] = new Point(
				(FPMath.mul(x, lxx) + FPMath.mul(y, lxy) + FPMath.mul(z, lxz) + lxo),
				(FPMath.mul(x, lyx) + FPMath.mul(y, lyy) + FPMath.mul(z, lyz) + lyo),
				(FPMath.mul(x, lzx) + FPMath.mul(y, lzy) + FPMath.mul(z, lzz) + lzo)
				);
		}
	}

	public String toString() {
		return ("[" + xo + "," + xx + "," + xy + "," + xz + ";" + yo + "," + yx + "," + yy + "," + yz + ";" + zo + "," + zx + "," + zy + "," + zz + "]");
	}
}
