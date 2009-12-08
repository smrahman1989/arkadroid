package uk.co.coldasice.projects.mobile.lib.fpmath;

import uk.co.coldasice.projects.mobile.mobilerubix.types.Point;


public class FPCameraTransform {

	Point cameraPosition = new Point( 0,0,0 );
	Point cameraRotation = new Point( 0,0,0 );
	
	FPMatrix part1, part2, part3;
	
	public FPCameraTransform (Point pos, Point rot) {
		this.cameraPosition = pos;
		this.cameraRotation = rot;
		
		part1 = new FPMatrix();
		part2 = new FPMatrix();
		part3 = new FPMatrix();
	}
	
	public void rotate(int dx, int dy) {
		this.cameraRotation.coords[0] += dy;
		this.cameraRotation.coords[1] += dx;
	}
	
	public void move (int dx, int dy, int dz) {
		this.cameraPosition.coords[0] += dx;
		this.cameraPosition.coords[1] += dy;
		this.cameraPosition.coords[2] += dz;
	}
	
	public void transform(Point[] v_in, Point[] v_out, int nvert) {
		part1.unit();
		part2.unit();
		part3.unit();
		int cx = FPMath.cos(-1 * cameraRotation.coords[0]);
		int cy = FPMath.cos(-1 * cameraRotation.coords[1]);
		int cz = FPMath.cos(-1 * cameraRotation.coords[2]);
		
		int sx = FPMath.sin(-1 * cameraRotation.coords[0]);
		int sy = FPMath.sin(-1 * cameraRotation.coords[1]);
		int sz = FPMath.sin(-1 * cameraRotation.coords[2]);

		part1.xx =   FPMatrix.ONE; 	part1.xy =   0;		part1.xz =   0;
		part1.yx =   0; 	part1.yy =  cx;		part1.yz =  sx;
		part1.zx =   0; 	part1.zy = -sx;		part1.zz =  cx;
		
		part2.xx =  cy; 	part2.xy =   0;		part2.xz = -sy;
		part2.yx =   0; 	part2.yy =   FPMatrix.ONE;		part2.yz =   0;
		part2.zx =  sy; 	part2.zy =   0;		part2.zz =  cy;

		part3.xx =  cz; 	part3.xy =  sz;		part3.xz =   0;
		part3.yx = -sz; 	part3.yy =  cz;		part3.yz =   0;
		part3.zx =   0; 	part3.zy =   0;		part3.zz =   FPMatrix.ONE;
		
		Point[] vectors_to_transform = new Point[v_in.length];
		for (int i=0; i<vectors_to_transform.length; i++) {
			vectors_to_transform[i] = new Point(v_in[i]);
			vectors_to_transform[i].coords[0] -= cameraPosition.coords[0];
			vectors_to_transform[i].coords[1] -= cameraPosition.coords[1];
			vectors_to_transform[i].coords[2] -= cameraPosition.coords[2];
		}
		
		FPMatrix newMat = new FPMatrix();
		newMat.mult(part3);
		newMat.mult(part2);
		newMat.mult(part1);
		newMat.transform(vectors_to_transform, v_out, nvert);
	}
	
}
