package meshgen;

import math.Vector2;
import math.Vector3;

enum Shape{sphere,cylinder,torus};


public class MeshGen {
	private int m;
	private int n;
	private OBJMesh mesh;
	private Shape shape;
	private String inputFileName;
	private String outputFileName;
	private boolean flag;
	
	private Vector3 calculateFaceNorm(OBJFace face){
		Vector3 v1=mesh.getPosition(face, 1).clone().sub(mesh.getPosition(face, 0));
		int i=2;
		while(i<face.positions.length){
			Vector3 v2=mesh.getPosition(face, i++).clone().sub(mesh.getPosition(face, 0));
			Vector3 norm=new Vector3(0,0,0);
			norm.add(v1).cross(v2).normalize();
			if(!norm.equals(new Vector3(0,0,0)))//avoid parallel vector
				return norm;
		}
		return new Vector3(0,0,0);
	}
	private void constructCylinder(){
		mesh=new OBJMesh();
		//add texture first
		float interval=(float)1.0/n;
		for(int i=0;i<n;i++){
			float u1=i*interval;
			float u2=i*interval;
			mesh.uvs.add(new Vector2(u1,(float) 0));//bottom
			mesh.uvs.add(new Vector2(u2,(float) 0.5));//top
			mesh.positions.add(new Vector3((float)Math.cos(-Math.PI/2-2*Math.PI*u1),(float)-1,(float)Math.sin(-Math.PI/2-2*Math.PI*u1)));
			mesh.positions.add(new Vector3((float)Math.cos(-Math.PI/2-2*Math.PI*u2),(float)1,(float)Math.sin(-Math.PI/2-2*Math.PI*u2)));
		}
		mesh.uvs.add(new Vector2((float)0.25,(float) 0.75));//bottom
		mesh.uvs.add(new Vector2((float)0.75,(float) 0.75));//top
		mesh.positions.add(new Vector3(0,-1,0));
		mesh.positions.add(new Vector3(0,1,0));
		//add face
		//add bottom cap
		for(int i=0;i<n;i++){
			OBJFace face=new OBJFace(3,true,true);
			face.setVertex(0, 2*n, 2*n, 2*n);
			face.setVertex(1, 2*i, 2*i, 2*i);
			face.setVertex(2, 2*((i+1)%n), 2*((i+1)%n), 2*((i+1)%n));
			mesh.faces.add(face);
		}
		//add top cap
		for(int i=0;i<n;i++){
			OBJFace face=new OBJFace(3,true,true);
			face.setVertex(0, 2*n+1, 2*n+1, 2*n+1);
			face.setVertex(1, 2*i+1, 2*i+1, 2*i+1);
			face.setVertex(2, 2*((i+1)%n)+1, 2*((i+1)%n)+1, 2*((i+1)%n)+1);
			mesh.faces.add(face);
		}
		//add bottom side
		for(int i=0;i<n;i++){
			OBJFace face=new OBJFace(3,true,true);
			face.setVertex(0, 2*i, 2*i, 2*i);
			face.setVertex(1, 2*((i+1)%n), 2*((i+1)%n), 2*((i+1)%n));
			face.setVertex(2, 2*(i%n)+1, 2*(i%n)+1, 2*(i%n)+1);
			mesh.faces.add(face);
		}
		//add up side
		for(int i=0;i<n;i++){
			OBJFace face=new OBJFace(3,true,true);
			face.setVertex(0, 2*i+1, 2*i+1, 2*i+1);
			face.setVertex(1, 2*((i+1)%n)+1, 2*((i+1)%n)+1, 2*((i+1)%n)+1);
			face.setVertex(2, 2*((i+1)%n), 2*((i+1)%n), 2*((i+1)%n));
			mesh.faces.add(face);
		}
		addNormal();
	}
	private void constructSphere(){
			
	}

	public void constructObj(){
		switch(shape){
		case cylinder:
			constructCylinder();
			break;
		case sphere:
			constructSphere();
			break;
		default:
			break;
		}
	}
	public void addNormal(){
		try{
			if(flag)mesh=new OBJMesh(inputFileName);
			mesh.normals.clear();
			for(int i=0;i<mesh.positions.size();i++){
				mesh.normals.add(new Vector3(0,0,0));
			}
			for(OBJFace face : mesh.faces){
				Vector3 faceNorm=calculateFaceNorm(face);
				for(int posIndex:face.positions){
					mesh.normals.get(posIndex+face.indexBase).add(faceNorm);
				}
				face.normals=face.positions.clone();
			}
			for(Vector3 norm:mesh.normals){
				norm.normalize();
			}
			
			mesh.writeOBJ(outputFileName);
		}catch(Exception e){
			System.out.print("fail to open file "+inputFileName);
		}
	}
	
	public MeshGen(String[] args){
		m=16;
		n=32;
		flag=false;//command type, true for usage2, false for usage1
		try{
			for(int i=0;i<args.length;i++){
				if(args[i].equals("-i")){
					inputFileName=args[++i];
					flag=true;
				}
				else if(args[i].equals("-o")){
					outputFileName=args[++i];
				}
				else if(args[i].equals("-n")){
					n=Integer.parseInt(args[++i]);
				}
				else if(args[i].equals("-m")){
					m=Integer.parseInt(args[++i]);
				}
				else if(args[i].equals("-g")){
					shape=Shape.valueOf(args[++i]);
				}
			}
			
		}catch(IndexOutOfBoundsException e){
			
		};
		if(!flag)constructObj();
		else addNormal();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new MeshGen(args);
	}

}
