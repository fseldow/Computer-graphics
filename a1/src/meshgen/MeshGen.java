package meshgen;

import math.Vector2;
import math.Vector3;

enum Shape {
	sphere, cylinder, torus
};// shape options
/**
 * TO generate mesh or generate normals in a existed norm
 */
public class MeshGen {
	private int m; // parameters for certain shape
	private int n; // parameters for certain shape
	private float r;// min radius for torus
	private OBJMesh mesh;// mesh read from disk or to be constructed
	private Shape shape;// the shape of mesh to be constructed
	private String inputFileName;// inputfile path+name
	private String outputFileName;// outputfile path+name
	private boolean flag;// to decide the mode of the command line

	/** build cylinder OBJMesh and write to disk 
	 * @exception failed to write file*/
	private void constructCylinder() {
		mesh = new OBJMesh();
		// add texture first
		float interval = (float) 1.0 / n;
		for (int i = 0; i < n; i++) {
			float u1 = i * interval;
			float u2 = i * interval;
			mesh.uvs.add(new Vector2(u1, (float) 0));// bottom
			mesh.uvs.add(new Vector2(u2, (float) 0.5));// top
			// calculate position via texture
			// x=cos(-pi/2-2pi*u)
			// y=+-1
			// z=sin(-pi/2-2pi*u)
			mesh.positions.add(new Vector3((float) Math.cos(-Math.PI / 2 - 2
					* Math.PI * u1), (float) -1, (float) Math.sin(-Math.PI / 2
					- 2 * Math.PI * u1)));
			mesh.positions.add(new Vector3((float) Math.cos(-Math.PI / 2 - 2
					* Math.PI * u2), (float) 1, (float) Math.sin(-Math.PI / 2
					- 2 * Math.PI * u2)));
		}
		// add position of center of caps
		mesh.positions.add(new Vector3(0, -1, 0));// index n
		mesh.positions.add(new Vector3(0, 1, 0)); // index n+1

		// add duplicated uv
		mesh.uvs.add(new Vector2(1, (float) 0));// bottom index:
		mesh.uvs.add(new Vector2(1, (float) 0.5));// top index:

		// add bottom cap texture
		// index : 2n+i+2
		for (int i = 0; i < n; i++) {
			mesh.uvs.add(new Vector2((float) (0.25 + 0.25 * mesh.positions
					.get(2 * i).x), (float) (0.75 + 0.25 * mesh.positions
					.get(2 * i).z)));
		}
		// add top cap texture
		// index 3n+i+2
		for (int i = 0; i < n; i++) {
			mesh.uvs.add(new Vector2((float) (0.75 + 0.25 * mesh.positions
					.get(2 * i).x), (float) (0.75 - 0.25 * mesh.positions
					.get(2 * i).z)));
		}
		mesh.uvs.add(new Vector2((float) 0.25, (float) 0.75));// bottom cap
																// center--4n+2
		mesh.uvs.add(new Vector2((float) 0.75, (float) 0.75));// top cap
																// center--4n+1+2

		// add normal
		// index i
		for (int i = 0; i < n; i++) {
			mesh.normals.add(new Vector3(mesh.positions.get(2 * i).x, 0,
					mesh.positions.get(2 * i).z));
		}
		mesh.normals.add(new Vector3(0, -1, 0));// bottom--n
		mesh.normals.add(new Vector3(0, 1, 0));// top --n+1

		// add face
		// add side with two bottom vertex
		for (int i = 0; i < n; i++) {
			OBJFace face = new OBJFace(3, true, true);
			face.setVertex(0, 2 * i, 2 * i, i);
			face.setVertex(1, 2 * ((i + 1) % n), 2 * (i + 1), (i + 1) % n);
			face.setVertex(2, 2 * ((i + 1) % n) + 1, 2 * (i + 1) + 1, (i + 1)
					% n);
			mesh.faces.add(face);
		}
		// add side with two top vertex
		for (int i = 0; i < n; i++) {
			OBJFace face = new OBJFace(3, true, true);
			face.setVertex(1, 2 * i + 1, 2 * i + 1, i);
			face.setVertex(0, 2 * ((i + 1) % n) + 1, 2 * (i + 1) + 1, (i + 1)
					% n);
			face.setVertex(2, 2 * i, 2 * i, i);
			mesh.faces.add(face);
		}
		// add bottom cap
		for (int i = 0; i < n; i++) {
			OBJFace face = new OBJFace(3, true, true);
			face.setVertex(1, 2 * n, 4 * n + 2, n);
			face.setVertex(0, 2 * i, 2 * n + i + 2, n);
			face.setVertex(2, 2 * ((i + 1) % n), 2 * n + (1 + i) % n + 2, n);
			mesh.faces.add(face);
		}
		// add top cap
		for (int i = 0; i < n; i++) {
			OBJFace face = new OBJFace(3, true, true);
			face.setVertex(0, 2 * n + 1, 4 * n + 1 + 2, n + 1);
			face.setVertex(1, 2 * i + 1, 3 * n + i + 2, n + 1);
			face.setVertex(2, 2 * ((i + 1) % n) + 1, 3 * n + (i + 1) % n + 2,
					n + 1);
			mesh.faces.add(face);
		}
		// write file
		try {
			mesh.writeOBJ(outputFileName);
		} catch (Exception e) {
			System.out.print("fail to write " + outputFileName);
		}

	}

	/**
	 * use i,j space to map positionIndex, textureIndex & normalIndex
	 * @param i ith longitude
	 * @param j jth latitude
	 * @return Vector3 contains the three indexes
	 */
	private Vector3 mapSphere(int i, int j) {
		if (i == 0 && j == 0)
			i = n;// due to no (0,0) in uvs
		if (i == m && j == n)
			i = 0;// due to no (1,1) in uvs
		// when i<0, we turn it a wrap-around to n-1
		if (i < 0) {
			i += n;
		}
		// posIndex=normalIndex
		float posIndex = 2 + ((j - 1) % m) * n + i % n, textureIndex = j
				* (n + 1) + i - 1;
		// set positionIndex to 0 or 1, when it refers to the pole
		if (j == 0)
			posIndex = 0;
		if (j == m)
			posIndex = 1;
		return new Vector3(posIndex, textureIndex, posIndex);
	}

	/** build sphere OBJMesh and write to disk 
	 * @exception write file fails*/
	private void constructSphere() {
		mesh = new OBJMesh();
		float n_interval = (float) 1.0 / n;
		float m_interval = (float) 1.0 / m;
		// texture index : j*n+i-1
		for (int j = 0; j <= m; j++) {
			for (int i = 0; i <= n; i++) {
				// jump (0,0) & (1,1)
				if (i == 0 && j == 0)
					continue;
				if (i == n && j == m)
					continue;
				mesh.uvs.add(new Vector2(i * n_interval, j * m_interval));
			}
		}
		// postion
		mesh.positions.add(new Vector3(0, -1, 0));// south pole index 0
		mesh.positions.add(new Vector3(0, 1, 0));// north pole index 1
		// index 2+(j-1)*n+i
		for (int j = 1; j < m; j++) {
			for (int i = 0; i < n; i++) {
				float x = (float) (Math
						.cos(Math.PI / 2 + 2.0 * i / n * Math.PI))
						* (float) Math.sin(1.0 * j / m * Math.PI);
				float y = -(float) (Math.cos(1.0 * j / m * Math.PI));
				float z = -(float) (Math.sin(Math.PI / 2 + 2.0 * i / n
						* Math.PI))
						* (float) Math.sin(1.0 * j / m * Math.PI);
				mesh.positions.add(new Vector3(x, y, z));
			}
		}
		// normal
		// index : same as position
		for (Vector3 pos : mesh.positions) {
			mesh.normals.add(pos);
		}
		// face
		for (int j = 1; j < m; j++) {
			for (int i = 0; i < n; i++) {
				Vector3 v1 = mapSphere(i, j);
				Vector3 v2 = mapSphere(i + 1, j);
				Vector3 v3 = mapSphere(i, j + 1);
				OBJFace face1 = new OBJFace(3, true, true);
				face1.setVertex(0, (int) v1.x, (int) v1.y, (int) v1.z);
				face1.setVertex(1, (int) v2.x, (int) v2.y, (int) v2.z);
				face1.setVertex(2, (int) v3.x, (int) v3.y, (int) v3.z);
				mesh.faces.add(face1);
			}
		}
		for (int j = 1; j < m; j++) {
			for (int i = 1; i <= n; i++) {
				Vector3 v1 = mapSphere(i, j);
				Vector3 v2 = mapSphere(i - 1, j);
				Vector3 v3 = mapSphere(i, j - 1);
				OBJFace face1 = new OBJFace(3, true, true);
				face1.setVertex(0, (int) v1.x, (int) v1.y, (int) v1.z);
				face1.setVertex(1, (int) v2.x, (int) v2.y, (int) v2.z);
				face1.setVertex(2, (int) v3.x, (int) v3.y, (int) v3.z);
				mesh.faces.add(face1);
			}
		}

		try {
			mesh.writeOBJ(outputFileName);
		} catch (Exception e) {
			System.out.print("failed to write " + outputFileName);
		}

	}

	/**
	 * use i,j space to map positionIndex, textureIndex & normalIndex
	 * @param i ith longitude
	 * @param j jth circle latitude
	 * @return Vector3 contains the three indexes
	 */
	private Vector3 mapTorus(int i, int j) {
		float posIndex = (j % m) * n + i % n, textureIndex = j * (n + 1) + i;
		return new Vector3(posIndex, textureIndex, posIndex);
	}

	/** build torus OBJMesh and write to disk 
	 * @exception writes file fails*/
	private void constructTorus() {
		mesh = new OBJMesh();
		float n_interval = (float) 1.0 / n;
		float m_interval = (float) 1.0 / m;
		// texture Index : j*(n+1)+i
		for (int j = 0; j <= m; j++) {
			for (int i = 0; i <= n; i++) {
				mesh.uvs.add(new Vector2(i * n_interval, j * m_interval));
			}
		}
		// add position and normal
		// index : j*n+i
		for (int j = 0; j < m; j++) {
			for (int i = 0; i < n; i++) {
				Vector3 center = new Vector3(
						(float) (Math.cos(Math.PI / 2 + 2.0 * i / n * Math.PI)),
						0,
						-(float) (Math.sin(Math.PI / 2 + 2.0 * i / n * Math.PI)));
				Vector3 offset = new Vector3(-r
						* (float) (Math.cos(2.0 * j / m * Math.PI)), -r
						* (float) (Math.sin(2.0 * j / m * Math.PI)), -r
						* (float) (Math.cos(2.0 * j / m * Math.PI)));
				Vector3 position = new Vector3(center.x + center.x * offset.x,
						center.y + offset.y, center.z + center.z * offset.z);
				Vector3 normal = position.clone().sub(center).normalize();
				mesh.positions.add(position);
				mesh.normals.add(normal);
			}
		}
		// add face
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				Vector3 v1 = mapTorus(i, j);
				Vector3 v2 = mapTorus(i + 1, j);
				Vector3 v3 = mapTorus(i, j + 1);

				OBJFace face = new OBJFace(3, true, true);
				face.setVertex(0, (int) v1.x, (int) v1.y, (int) v1.z);
				face.setVertex(1, (int) v2.x, (int) v2.y, (int) v2.z);
				face.setVertex(2, (int) v3.x, (int) v3.y, (int) v3.z);
				mesh.faces.add(face);
			}
		}
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				Vector3 v1 = mapTorus(i + 1, j);
				Vector3 v2 = mapTorus(i + 1, j + 1);
				Vector3 v3 = mapTorus(i, j + 1);

				OBJFace face = new OBJFace(3, true, true);
				face.setVertex(0, (int) v1.x, (int) v1.y, (int) v1.z);
				face.setVertex(1, (int) v2.x, (int) v2.y, (int) v2.z);
				face.setVertex(2, (int) v3.x, (int) v3.y, (int) v3.z);
				mesh.faces.add(face);
			}
		}
		try {
			mesh.writeOBJ(outputFileName);
		} catch (Exception e) {
			System.out.print("failed to write " + outputFileName);
		}
	}

	/**
	 * usage 1 of MeshGen construct Obj and write to disk according to the
	 * parameters
	 * @exception invalid shape
	 */
	public void constructObj() {
		switch (shape) {
		case cylinder:
			constructCylinder();
			break;
		case sphere:
			constructSphere();
			break;
		case torus:
			constructTorus();
			break;
		default:
			System.out.print("No such a shape.");
			break;
		}
	}

	/**
	 * calculate the norm of the face
	 * the norm is calculated by (v1-v0) cross (v2-v0)
	 * @param face the source face
	 * @return the norm
	 */
	private Vector3 calculateFaceNorm(OBJFace face) {
		Vector3 v1 = mesh.getPosition(face, 1).clone()
				.sub(mesh.getPosition(face, 0));
		int i = 2;
		// to avoid (v1-v0) and (v2-v0) are parallel vectors
		while (i < face.positions.length) {
			Vector3 v2 = mesh.getPosition(face, i++).clone()
					.sub(mesh.getPosition(face, 0));
			Vector3 norm = new Vector3(0, 0, 0);
			norm.add(v1).cross(v2).normalize();
			if (!norm.equals(new Vector3(0, 0, 0)))
				return norm;
		}
		// all vectors are paralleled
		return new Vector3(0, 0, 0);
	}

	/**
	 * usage 2 of MeshGen read an obj and generate its normals write to disk
	 * @exception read file fails
	 */
	public void addNormal() {
		try {
			// read mesh from local disk
			if (flag)
				mesh = new OBJMesh(inputFileName);
			mesh.normals.clear();
			// initialize all norms (0,0,0)
			for (int i = 0; i < mesh.positions.size(); i++) {
				mesh.normals.add(new Vector3(0, 0, 0));
			}
			for (OBJFace face : mesh.faces) {
				Vector3 faceNorm = calculateFaceNorm(face);
				for (int posIndex : face.positions) {
					mesh.normals.get(posIndex + face.indexBase).add(faceNorm);
				}
				face.normals = face.positions.clone();
			}
			for (Vector3 norm : mesh.normals) {
				norm.normalize();
			}
			// write to disk
			mesh.writeOBJ(outputFileName);
		} catch (Exception e) {
			System.out.print("fail to open file " + inputFileName);
		}
	}

	/** analysis the command line and execute usage 1 default 
	 * @param args command lines*/
	public MeshGen(String[] args) {
		m = 16;
		n = 32;
		r = (float) 0.25;
		flag = false;// command type, true for usage2, false for usage1
		try {
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("-i")) {
					inputFileName = args[++i];
					flag = true;
				} else if (args[i].equals("-o")) {
					outputFileName = args[++i];
				} else if (args[i].equals("-n")) {
					n = Integer.parseInt(args[++i]);
				} else if (args[i].equals("-m")) {
					m = Integer.parseInt(args[++i]);
				} else if (args[i].equals("-r")) {
					r = Float.parseFloat(args[++i]);
				} else if (args[i].equals("-g")) {
					shape = Shape.valueOf(args[++i]);
				} else
					i++;
			}
		} catch (IndexOutOfBoundsException e) {
			System.out.print("invalid command line");
		}
		;
		if (!flag)
			constructObj();//usage1
		else
			addNormal();//useage2
	}

	/**
	 * args has two useages 
	 * usage1:java MeshGen -g <sphere|cylinder> [-n <divisionsU>] [-m <divisionsV>] -o <outfile.obj>
	 * 		the first required argument is the geometry specifier, and the second is the output filename. 
	 * 		If the geometry specifier is one of the fixed strings sphere or
	 * 		cylinder, a triangle mesh is generated that approximates that shape,
	 * 		where the number of triangles generated is controlled by the optional -n
	 * 		and -m options, and written to the output OBJ file.
	 * usage2: java MeshGen -i <infile.obj> -o <outfile.obj>
	 * 		the user provides an input OBJ mesh file, which the program reads in. The mesh is assumed to have no normals
	 * 		(if normals are included in the input file, they are ignored). The
	 * 		program then generates approximate normals at each vertex as described
	 * 		below, and writes the resulting mesh to the user-provided output file.
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new MeshGen(args);
	}

}
