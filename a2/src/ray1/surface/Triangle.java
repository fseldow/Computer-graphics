package ray1.surface;

import ray1.IntersectionRecord;
import ray1.Ray;
import egl.math.Matrix3d;
import egl.math.Vector2d;
import egl.math.Vector3;
import egl.math.Vector3d;
import ray1.shader.Shader;
import ray1.OBJFace;

/**
 * Represents a single triangle, part of a triangle mesh
 *
 * @author ags
 */
public class Triangle extends Surface {
  /** The normal vector of this triangle, if vertex normals are not specified */
  Vector3 norm;
  
  /** The mesh that contains this triangle */
  Mesh owner;
  
  /** The face that contains this triangle */
  OBJFace face = null;
  
  double a, b, c, d, e, f;
  public Triangle(Mesh owner, OBJFace face, Shader shader) {
    this.owner = owner;
    this.face = face;

    Vector3 v0 = owner.getMesh().getPosition(face,0);
    Vector3 v1 = owner.getMesh().getPosition(face,1);
    Vector3 v2 = owner.getMesh().getPosition(face,2);
    
    if (!face.hasNormals()) {
      Vector3 e0 = new Vector3(), e1 = new Vector3();
      e0.set(v1).sub(v0);
      e1.set(v2).sub(v0);
      norm = new Vector3();
      norm.set(e0).cross(e1).normalize();
    }

    a = v0.x-v1.x;
    b = v0.y-v1.y;
    c = v0.z-v1.z;
    
    d = v0.x-v2.x;
    e = v0.y-v2.y;
    f = v0.z-v2.z;
    
    this.setShader(shader);
  }

  /**
   * Tests this surface for intersection with ray. If an intersection is found
   * record is filled out with the information about the intersection and the
   * method returns true. It returns false otherwise and the information in
   * outRecord is not modified.
   *
   * @param outRecord the output IntersectionRecord
   * @param rayIn the ray to intersect
   * @return true if the surface intersects the ray
   */
  public boolean intersect(IntersectionRecord outRecord, Ray rayIn) {
    // TODO#A2: fill in this function.
	  Vector3d ori=rayIn.origin;
	  Vector3d dir=rayIn.direction;
	  Vector3 v0 = owner.getMesh().getPosition(face,0);
	  if (dir.clone().dot(norm)==0)return false;
	  Matrix3d A=new Matrix3d(
			  a,d,dir.x,
			  b,e,dir.y,
			  c,f,dir.z
			  );
	  Vector3d off=new Vector3d(v0).clone().sub(ori);
	  Vector3d result=A.clone().invert().mul(off);
	  double u=result.z;
	  if(u<0)return false;
	  if(u<rayIn.start)return false;
	  if(u>rayIn.end)return false;
	  if(result.x<0||result.y<0||(1-result.x-result.y)<0)return false;
	  /*
	   * u=(new Vector3d(owner.getMesh().getPosition(face,0)).sub(ori).dot(norm))/
			  dir.clone().dot(norm);
	  */
	  Vector3d pos=ori.clone().add(dir.clone().mul(u));
	  
	  
	  outRecord.location.set(pos);
	  outRecord.surface=this;
	  outRecord.t=u;
	  outRecord.normal.set(norm);
	  if(face.hasUVs()){
		  Vector2d uv=new Vector2d(owner.getMesh().getUV(face, 0)).clone().mul(1-result.x-result.y).add(new Vector2d(owner.getMesh().getUV(face, 1)).clone().mul(result.x)).add(new Vector2d(owner.getMesh().getUV(face, 2)).clone().mul(result.y));
		  outRecord.texCoords.set(uv);
	  }
	  return true;
  }

  /**
   * @see Object#toString()
   */
  public String toString() {
    return "Triangle ";
  }
}