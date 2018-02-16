package ray1.surface;

import ray1.IntersectionRecord;
import ray1.Ray;
import egl.math.Vector3;
import egl.math.Vector3d;

/**
 * Represents a sphere as a center and a radius.
 *
 * @author ags
 */
public class Sphere extends Surface {
  
  /** The center of the sphere. */
  protected final Vector3 center = new Vector3();
  public void setCenter(Vector3 center) { this.center.set(center); }
  
  /** The radius of the sphere. */
  protected float radius = 1.0f;
  public void setRadius(float radius) { this.radius = radius; }
  
  protected final double M_2PI = 2 * Math.PI;
  
  public Sphere() { }
  
  /**
   * Tests this surface for intersection with ray. If an intersection is found
   * record is filled out with the information about the intersection and the
   * method returns true. It returns false otherwise and the information in
   * outRecord is not modified.
   *
   * @param outRecord the output IntersectionRecord
   * @param ray the ray to intersect
   * @return true if the surface intersects the ray
   */
  public boolean intersect(IntersectionRecord outRecord, Ray rayIn) {
    // TODO#A2: fill in this function.
	  //calculate point map to line
	  double u=new Vector3d(center).clone().sub(rayIn.origin).dot(rayIn.direction);
	  Vector3d dis=new Vector3d(center).clone().sub(rayIn.origin).sub(rayIn.direction.clone().mul(u));
	  if(u<0)return false;
	  if(dis.clone().dot(dis)>radius*radius)return false;
	  else {
		  double middle=u/2;
		  double end=u;
		  double start=0;
		  while(u-middle>1E-5) {
			  Vector3d pos=rayIn.origin.clone().add(rayIn.direction.clone().mul(middle));
			  Vector3d offset=pos.clone().sub(center);
			  double distance=offset.clone().dot(offset);
			  if(Math.abs(distance-radius*radius)<1E-5) {
				  //postion!
				  return true;
			  }
			  if(distance<radius*radius) {
				  end=middle;
			  }
			  else {
				  start=middle;
			  }
			  middle=(end+start)/2;
		  }
		  //postion!
	  }
	  return true;
  }
  
  /**
   * @see Object#toString()
   */
  public String toString() {
    return "sphere " + center + " " + radius + " " + shader + " end";
  }

}