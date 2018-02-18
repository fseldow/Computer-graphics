package ray1.shader;

import ray1.IntersectionRecord;
import ray1.Light;
import ray1.Ray;
import ray1.Scene;
import ray1.shader.BRDF;
import egl.math.Color;
import egl.math.Colorf;
import egl.math.Vector2;
import egl.math.Vector3;
import egl.math.Vector3d;

/**
 * Microfacet-based shader
 *
 * @author zechen
 */
public class Microfacet extends Shader {
	
	protected BRDF brdf = null;
	public void setBrdf(BRDF t) { brdf = t; }
	public BRDF getBrdf() { return brdf; }

	/** The color of the microfacet reflection. */
	protected final Colorf microfacetColor = new Colorf(Color.Black);
	public void setMicrofacetColor(Colorf microfacetColor) { this.microfacetColor.set(microfacetColor); }
	public Colorf getMicrofacetColor() {return new Colorf(microfacetColor);}
	
	/** The color of the diffuse reflection. */
	protected final Colorf diffuseColor = new Colorf(Color.Black);
	public void setDiffuseColor(Colorf diffuseColor) { this.diffuseColor.set(diffuseColor); }
	public Colorf getDiffuseColor() {return new Colorf(diffuseColor);}
	
	public Microfacet() { }

	/**
	 * @see Object#toString()
	 */
	public String toString() {    
		return "Microfacet model, microfacet color " + microfacetColor + " diffuseColor " + diffuseColor + brdf.toString();
	}

	/**
	 * Evaluate the intensity for a given intersection using the Microfacet shading model.
	 *
	 * @param outIntensity The color returned towards the source of the incoming ray.
	 * @param scene The scene in which the surface exists.
	 * @param ray The ray which intersected the surface.
	 * @param record The intersection record of where the ray intersected the surface.
	 */
	@Override
	public void shade(Colorf outIntensity, Scene scene, Ray ray, IntersectionRecord record) {
		// TODO#A2: Fill in this function.
		// 1) Loop through each light in the scene.
		// 2) If the intersection point is shadowed, skip the calculation for the light.
		//	  See Shader.java for a useful shadowing function.
		// 3) Compute the incoming direction by subtracting
		//    the intersection point from the light's position.
		// 4) Compute the color of the point using the microfacet shading model. 
		//	  EvalBRDF method of brdf object should be called to evaluate BRDF value at the shaded surface point.
		// 5) Add the computed color value to the output.
		outIntensity.set(new Colorf(0,0,0));
		Vector3 w0=new Vector3(ray.direction.clone().mul(-1));
		Vector3 n=new Vector3(record.normal.clone());
		if(texture!=null) {
			setDiffuseColor(texture.getTexColor(new Vector2(record.texCoords)));
		}
		for(Light light:scene.getLights()) {
			if(!isShadowed(scene,light,record,ray)) {
				Vector3 l1=light.position.clone().sub(new Vector3(record.location));
				Vector3 w1=l1.clone().normalize();
				float r2=l1.clone().dot(l1);
				float f=brdf.EvalBRDF(w1, w0, n);
				outIntensity.add(light.intensity.clone().mul(microfacetColor).mul((float)(Math.max(record.normal.clone().dot(w1), 0)/r2))).mul(f);
				outIntensity.add(light.intensity.clone().mul(diffuseColor).mul((float)(Math.max(record.normal.clone().dot(w1), 0)/r2/Math.PI)));
			}
			
		}
		
	}

}
