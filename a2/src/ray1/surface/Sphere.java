package ray1.surface;

import ray1.IntersectionRecord;
import ray1.Ray;
import egl.math.Vector2;
import egl.math.Vector2d;
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
  private Vector2d getUV(Vector3d related_position){
	  double u,v;
	  double x,y,z;
	  x=related_position.x;
	  y=related_position.y;
	  z=related_position.z;
	  v=Math.asin(y)+M_2PI/4;
	  double new_r=Math.sqrt(1-y*y);
	  u=(Math.acos(x/new_r)-M_2PI/2)/M_2PI;
	  Vector2d ret=new Vector2d(u,v);
	  return ret;
  }
  
  public boolean intersect(IntersectionRecord outRecord, Ray rayIn) {
    // TODO#A2: fill in this function.
	  //calculate point map to line
	  double u1,u2,u;
	  Vector3d d=rayIn.direction;
	  Vector3d e=rayIn.origin;
	  double temp=Math.pow(d.clone().dot(e.clone().sub(center)), 2)-(d.clone().dot(d))*(e.clone().sub(center).dot(e.clone().sub(center))-Math.pow(radius,2));
	  if(temp<0)return false;
	  u1=(-d.clone().dot(e.clone().sub(center))-Math.sqrt(temp))
			  /(d.clone().dot(d));
	  u2=(-d.clone().dot(e.clone().sub(center))+Math.sqrt(temp))
			  /(d.clone().dot(d));
	  if(u1>=0)u=u1;
	  else if(u2>=0)u=u2;
	  else return false;
	  Vector3d pos=e.clone().add(d.clone().mul(u));
	  Vector3d offset=pos.clone().sub(center);
	  Vector3d norm=new Vector3d(offset.clone().normalize());
	  Vector2d uv=getUV(offset.clone().normalize());
	  outRecord.location.set(pos);
	  outRecord.normal.set(norm);
	  outRecord.texCoords.set(uv);
	  outRecord.surface=new Sphere();
	  outRecord.t=u;
	  return true;
  }
  
  /**
   * @see Object#toString()
   */
  public String toString() {
    return "sphere " + center + " " + radius + " " + shader + " end";
  }

}