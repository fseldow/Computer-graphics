package ray1.shader;

import ray1.shader.BRDF;
import egl.math.Vector3;

/**
 * Evaluate microfacet BRDF function with Beckmann distribution
 * @author zechen
 */
public class Beckmann  extends BRDF
{

	public String toString() {    
		return "Beckmann microfacet " + super.toString();
	}
	public float Gfoo(Vector3 v,Vector3 h, Vector3 n) {
		float ret=0;
		float tan=v.clone().cross(n).len()/(v.dot(n));
		float a=1/(alpha*tan);
		float temp=(v.dot(h))*(v.dot(n));
		if(temp>0) {
			if(a<1.6) {
				ret=3.535f*a+2.181f*a*a;
				ret/=1f+2.276f*a+2.577f*a*a;
			}
			else ret=1;
		}
		else ret=0f;
		return ret;
	}
	/**
	 * Evaluate the BRDF function value in microfacet model with Beckmann distribution
	 *
	 * @param IncomingVec Direction vector of the incoming ray.
	 * @param OutgoingVec Direction vector of the outgoing ray.
	 * @param SurfaceNormal Normal vector of the surface at the shaded point.
	 * @return evaluated BRDF function value
	 */
	public float EvalBRDF(Vector3 IncomingVec, Vector3 OutgoingVec, Vector3 SurfaceNormal)
	{
		// TODO#A2: Evaluate the BRDF function of microfacet-based model with Beckmann distribution
		// Walter, Bruce, et al. 
		// "Microfacet models for refraction through rough surfaces." 
		// Proceedings of the 18th Eurographics conference on Rendering Techniques. Eurographics Association, 2007.
		float F,G,D;
		Vector3 h=new Vector3();
		h=IncomingVec.clone().add(OutgoingVec).normalize();
		//calculate F
		float g,c;
		c=Math.abs(IncomingVec.dot(h));
		float ni=1.0f;
		g=(float)Math.sqrt((nt*nt)/(ni*ni)-1+c*c);
		F=(float)(0.5*
				Math.pow((g-c), 2)/Math.pow((g+c), 2)
				*(1+Math.pow(c*(g+c)-1, 2)/Math.pow(c*(g-c)+1, 2))
				);
		//calculate G
		float temp;
		G=Gfoo(IncomingVec,h,SurfaceNormal)*Gfoo(OutgoingVec,h,SurfaceNormal);
		//calculate D
		temp=h.dot(SurfaceNormal);
		if(temp<0)D=0;
		else {
			float cos2=(float)Math.pow(temp/(h.len())/(SurfaceNormal.len()),2);
			D=(float)(Math.exp((1-1/cos2)/alpha/alpha)/
					(Math.PI*alpha*alpha*cos2*cos2));
		}
		float ret=F*G*D/4/(IncomingVec.dot(SurfaceNormal))/(OutgoingVec.dot(SurfaceNormal));
		
		return ret;
	}
	
}
