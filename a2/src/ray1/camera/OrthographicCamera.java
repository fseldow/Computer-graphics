package ray1.camera;

import egl.math.Vector3;
import egl.math.Vector3d;
import ray1.Ray;

public class OrthographicCamera extends Camera {

    //TODO#A2: create necessary new variables/objects here, including an orthonormal basis
    //          formed by three basis vectors and any other helper variables 
    //          if needed.
    Vector3 x,y,z;
    
    /**
     * Initialize the derived view variables to prepare for using the camera.
     */
    public void init() {
        // TODO#A2: Fill in this function.
        // 1) Set the 3 basis vectors in the orthonormal basis, 
        //    based on viewDir and viewUp
    	z=viewDir.clone().mul(-1).normalize();
    	x=viewUp.clone().cross(z).normalize();
    	y=z.clone().cross(x).normalize();
        // 2) Set up the helper variables if needed

    }

    /**
     * Set outRay to be a ray from the camera through a point in the image.
     *
     * @param outRay The output ray (not normalized)
     * @param inU The u coord of the image point (range [0,1])
     * @param inV The v coord of the image point (range [0,1])
     */
    public void getRay(Ray outRay, float inU, float inV) {
        // TODO#A2: Fill in this function.
        // 1) Transform inU so that it lies between [-viewWidth / 2, +viewWidth / 2] 
        //    instead of [0, 1]. Similarly, transform inV so that its range is
        //    [-vieHeight / 2, +viewHeight / 2]
    	inU = inU * (viewWidth) - viewWidth / 2;
    	inV = inV * (viewHeight) - viewHeight / 2;
        // 2) Set the origin field of outRay for an orthographic camera. 
        //    In an orthographic camera, the origin should depend on your transformed
        //    inU and inV and your basis vectors u and v.
    	Vector3d origin=new Vector3d(viewPoint.clone().add(x.clone().mul(inU)).add(y.clone().mul(inV)));
        // 3) Set the direction field of outRay for an orthographic camera.
    	Vector3d direction= new Vector3d(viewDir.clone());
  	
    	outRay.set(origin,direction.normalize());
    	outRay.makeOffsetRay();
    }

}
