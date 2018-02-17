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
		c=Math.abs(IncomingVec.clone().dot(h));
		float ni=1.0f;
		g=(float)Math.sqrt(nt*nt/(ni*ni)-1+c*c);
		F=(float)(0.5*
				Math.pow((g-c), 2)/Math.pow((g+c), 2)
				*(1+Math.pow(c*(c+g)-1, 2)/Math.pow(c*(c+g)+1, 2))
				);
		//calculate G
		
		//calculate D
		return 0.0f;
	}
	
}
