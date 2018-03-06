package manip;

import egl.math.*;
import gl.RenderObject;

public class ScaleManipulator extends Manipulator {

	public ScaleManipulator (ManipulatorAxis axis) {
		super();
		this.axis = axis;
	}

	public ScaleManipulator (RenderObject reference, ManipulatorAxis axis) {
		super(reference);
		this.axis = axis;
	}

	@Override
	protected Matrix4 getReferencedTransform () {
		if (this.reference == null) {
			throw new RuntimeException ("Manipulator has no controlled object!");
		}
		return new Matrix4().set(reference.scale)
				.mulAfter(reference.rotationZ)
				.mulAfter(reference.rotationY)
				.mulAfter(reference.rotationX)
				.mulAfter(reference.translation);
	}

	@Override
	public void applyTransformation(Vector2 lastMousePos, Vector2 curMousePos, Matrix4 viewProjection) {
		// TODO#A3: Modify this.reference.scale given the mouse input.
		// Use this.axis to determine the axis of the transformation.
		// Note that the mouse positions are given in coordinates that are normalized to the range [-1, 1]
		//   for both X and Y. That is, the origin is the center of the screen, (-1,-1) is the bottom left
		//   corner of the screen, and (1, 1) is the top right corner of the screen.
		// Get world position of lasMouse, curMouse and their directions
		Matrix4 worldProjection = viewProjection.clone().invert();
		Vector3 lastMouseO = new Vector3(lastMousePos.x, lastMousePos.y, 1f);
		Vector3 lastMouseP = new Vector3(lastMousePos.x, lastMousePos.y, -1f);
		Vector3 curMouseO = new Vector3(curMousePos.x, curMousePos.y, 1f);
		Vector3 curMouseP = new Vector3(curMousePos.x, curMousePos.y, -1f);
				
		Vector3 lastMouseOriWorld = worldProjection.mulPos(lastMouseO);
		Vector3 lastMousePosWorld = worldProjection.mulPos(lastMouseP);
		Vector3 curMouseOriWorld = worldProjection.mulPos(curMouseO);
		Vector3 curMousePosWorld = worldProjection.mulPos(curMouseP);
				
		Vector3 v1 = lastMousePosWorld.clone().sub(lastMouseOriWorld);
		Vector3 v2 = curMousePosWorld.clone().sub(curMouseOriWorld);
				
				
		// get manipulator origin and direction
		Vector3 manipulatorOrigin = getReferencedTransform().mulPos(new Vector3());
		Vector3 manipulatorDirection = new Vector3();
		switch(this.axis) {
		case X:
			manipulatorDirection = getReferencedTransform().mulDir(new Vector3(1, 0, 0));
			break;
		case Y:
			manipulatorDirection = getReferencedTransform().mulDir(new Vector3(0, 1, 0));
			break;
		case Z:
			manipulatorDirection = getReferencedTransform().mulDir(new Vector3(0, 0, 1));
			break;
		default:
			break;
		}
		//get another axis
		//calculate the norm of image plane
		Vector3 a = new Vector3(-1f, 1f, 1f);
		Vector3 b = new Vector3(-1f, -1f, 1f);
		Vector3 c = new Vector3(1f, -1f, 1f);
			
		Vector3 aWorld = worldProjection.mulPos(a);
		Vector3 bWorld = worldProjection.mulPos(b);
		Vector3 cWorld = worldProjection.mulPos(c);
				
		Vector3 norm = aWorld.clone().sub(bWorld).cross(aWorld.clone().sub(cWorld));
		Vector3 anotherAxis = norm.clone().cross(manipulatorDirection);
		
		//calculate t
		float t1, t2;
		t1 = calculateT(manipulatorOrigin, manipulatorDirection, anotherAxis, lastMouseOriWorld, v1).x;
		t2 = calculateT(manipulatorOrigin, manipulatorDirection, anotherAxis, curMouseOriWorld, v2).x;
				
		switch(this.axis) {
		case X:
			this.reference.scale.mulAfter(Matrix4.createScale(t2/t1, 1.0f, 1.0f));
			break;
		case Y:
			this.reference.scale.mulAfter(Matrix4.createScale(1.0f, t2/t1, 1.0f));
			break;
		case Z:
			this.reference.scale.mulAfter(Matrix4.createScale(1.0f, 1.0f, t2/t1));
			break;
		default:
			break;
		}

	}

	@Override
	protected String meshPath () {
		return "data/meshes/Scale.obj";
	}

}
