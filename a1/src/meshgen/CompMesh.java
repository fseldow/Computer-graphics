package meshgen;

import java.io.IOException;

import meshgen.OBJMesh.OBJFileFormatException;

public class CompMesh {

	public static void main(String[] args) throws OBJFileFormatException, IOException {
		// TODO Auto-generated method stub
		OBJMesh m1=new OBJMesh("horse-norms.obj");
		OBJMesh m2=new OBJMesh("data/horse-norms-reference.obj");
		float ep=(float)0.00001;
		System.out.print(OBJMesh.compare(m1,m2,true,ep));
	}

}
