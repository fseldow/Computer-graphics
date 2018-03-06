package manip;

import egl.math.*;
import gl.RenderObject;

public class RotationManipulator extends Manipulator {

	protected String meshPath = "Rotate.obj";

	public RotationManipulator(ManipulatorAxis axis) {
		super();
		this.axis = axis;
	}

	public RotationManipulator(RenderObject reference, ManipulatorAxis axis) {
		super(reference);
		this.axis = axis;
	}

	//assume X, Y, Z on stack in that order
	@Override
	protected Matrix4 getReferencedTransform() {
		Matrix4 m = new Matrix4();
		switch (this.axis) {
		case X:
			m.set(reference.rotationX).mulAfter(reference.translation);
			break;
		case Y:
			m.set(reference.rotationY)
				.mulAfter(reference.rotationX)
				.mulAfter(reference.translation);
			break;
		case Z:
			m.set(reference.rotationZ)
			.mulAfter(reference.rotationY)
			.mulAfter(reference.rotationX)
			.mulAfter(reference.translation);
			break;
		}
		return m;
	}

	@Override
	public void applyTransformation(Vector2 lastMousePos, Vector2 curMousePos, Matrix4 viewProjection) {
		// TODO#A3: Modify this.reference.rotationX, this.reference.rotationY, or this.reference.rotationZ
		//   given the mouse input.
		// Use this.axis to determine the axis of the transformation.
		// Note that the mouse positions are given in coordinates that are normalized to the range [-1, 1]
		//   for both X and Y. That is, the origin is the center of the screen, (-1,-1) is the bottom left
		//   corner of the screen, and (1, 1) is the top right corner of the screen.
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
		Vector3 axis1 = new Vector3();
		Vector3 axis2 = new Vector3();
		switch(this.axis) {
		case X:
			manipulatorDirection = getReferencedTransform().mulDir(new Vector3(1, 0, 0));
			axis1 = getReferencedTransform().mulDir(new Vector3(0, 1, 0));
			axis2 = getReferencedTransform().mulDir(new Vector3(0, 0, 1));
			break;
		case Y:
			manipulatorDirection = getReferencedTransform().mulDir(new Vector3(0, 1, 0));
			axis1 = getReferencedTransform().mulDir(new Vector3(0, 0, 1));
			axis2 = getReferencedTransform().mulDir(new Vector3(1, 0, 0));
			break;
		case Z:
			manipulatorDirection = getReferencedTransform().mulDir(new Vector3(0, 0, 1));
			axis1 = getReferencedTransform().mulDir(new Vector3(1, 0, 0));
			axis2 = getReferencedTransform().mulDir(new Vector3(0, 1, 0));
			break;
		default:
			break;
		}
		
		
		Vector3 t1, t2;
		t1 = calculateT(manipulatorOrigin, axis1, axis2, lastMouseOriWorld, v1);
		t2 = calculateT(manipulatorOrigin, axis1, axis2, curMouseOriWorld, v2);
		
		Vector3 intersection1 = lastMouseOriWorld.clone().add(v1.clone().mul(-t1.z));
		Vector3 intersection2 = lastMouseOriWorld.clone().add(v2.clone().mul(-t2.z));
		
		float radius = (float)Math.acos(intersection1.clone().dot(intersection2)/intersection1.len()/intersection2.len());
		float direction = intersection1.clone().cross(intersection2).dot(manipulatorDirection);
		if(direction>0)radius=-radius;
		switch(this.axis) {
		case X:
			this.reference.rotationX.mulAfter(Matrix4.createRotationX(radius));
			break;
		case Y:
			this.reference.rotationY.mulAfter(Matrix4.createRotationY(radius));
			break;
		case Z:
			this.reference.rotationZ.mulAfter(Matrix4.createRotationZ(radius));
			break;
		default:
			break;
		}

	}

	@Override
	protected String meshPath () {
		return "data/meshes/Rotate.obj";
	}
}
