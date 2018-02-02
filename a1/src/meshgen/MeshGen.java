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
		mesh.positions.add(new Vector3(0,-1,0));
		mesh.positions.add(new Vector3(0,1,0));
		mesh.uvs.add(new Vector2(1,(float) 0));//bottom
		mesh.uvs.add(new Vector2(1,(float) 0.5));//top

		//add bottom cap texture
		for(int i=0;i<n;i++) {//2n+i+2
			mesh.uvs.add(new Vector2((float)(0.25+0.25*mesh.positions.get(2*i).x),(float)(0.75+0.25*mesh.positions.get(2*i).z)));
		}
		//add top cap texture
		for(int i=0;i<n;i++) {//3n+i+2
			mesh.uvs.add(new Vector2((float)(0.75+0.25*mesh.positions.get(2*i).x),(float)(0.75-0.25*mesh.positions.get(2*i).z)));
		}
		mesh.uvs.add(new Vector2((float)0.25,(float) 0.75));//bottom center--4n+2
		mesh.uvs.add(new Vector2((float)0.75,(float) 0.75));//top center--4n+1+2
		
		//add normal
		for(int i=0;i<n;i++) {
			mesh.normals.add(new Vector3(mesh.positions.get(2*i).x,0,mesh.positions.get(2*i).z));
		}
		mesh.normals.add(new Vector3(0,-1,0));//bottom--n
		mesh.normals.add(new Vector3(0, 1,0));//top --n+1
		
		//add face
		//add bottom side
		for(int i=0;i<n;i++){
			OBJFace face=new OBJFace(3,true,true);
			face.setVertex(0, 2*i, 2*i, i);
			face.setVertex(1, 2*((i+1)%n), 2*(i+1), (i+1)%n);
			face.setVertex(2, 2*((i+1)%n)+1, 2*(i+1)+1, (i+1)%n);
			mesh.faces.add(face);
		}
		//add up side
		for(int i=0;i<n;i++){
			OBJFace face=new OBJFace(3,true,true);
			face.setVertex(1, 2*i+1, 2*i+1, i);
			face.setVertex(0, 2*((i+1)%n)+1, 2*(i+1)+1, (i+1)%n);
			face.setVertex(2, 2*i, 2*i, i);
			mesh.faces.add(face);
		}
		//add bottom cap
		for(int i=0;i<n;i++){
			OBJFace face=new OBJFace(3,true,true);
			face.setVertex(1, 2*n, 4*n+2, n);
			face.setVertex(0, 2*i, 2*n+i+2, n);
			face.setVertex(2, 2*((i+1)%n), 2*n+(1+i)%n+2, n);
			mesh.faces.add(face);
		}
		//add top cap
		for(int i=0;i<n;i++){
			OBJFace face=new OBJFace(3,true,true);
			face.setVertex(0, 2*n+1, 4*n+1+2, n+1);
			face.setVertex(1, 2*i+1, 3*n+i+2, n+1);
			face.setVertex(2, 2*((i+1)%n)+1, 3*n+(i+1)%n+2, n+1);
			mesh.faces.add(face);
		}
		
		try{
			mesh.writeOBJ(outputFileName);
		}catch(Exception e) {System.out.print("fail to write "+outputFileName);}
		
	}
	
	private Vector3 mapSphere(int i,int j) {
		
		if(i==0&&j==0)i=n;
		if(i==m&&j==n)i=0;
		if(i<0){i+=n;}
		float posIndex=2+((j-1)%m)*n+i%n,textureIndex=j*(n+1)+i-1;
		if(j==0)posIndex=0;
		if(j==m)posIndex=1;
		return new Vector3(posIndex,textureIndex,posIndex);
	}
	private void constructSphere(){
		mesh=new OBJMesh();
		float n_interval=(float)1.0/n;
		float m_interval=(float)1.0/m;
		//texture NO=j*n+i-1
		for(int j=0;j<=m;j++) {
			for(int i=0;i<=n;i++) {
				if(i==0&&j==0)continue;
				if(i==n&&j==m)continue;
				mesh.uvs.add(new Vector2(i*n_interval,j*m_interval));
			}
		}
		//postion
		mesh.positions.add(new Vector3(0,-1,0));//south pole  0
		mesh.positions.add(new Vector3(0,1,0));//north pole   1
		for(int j=1;j<m;j++) {
			for(int i=0;i<n;i++) {
				float x=(float)(Math.cos(Math.PI/2+2.0*i/n*Math.PI))*(float)Math.sin(1.0*j/m*Math.PI);
				float y=-(float)(Math.cos(1.0*j/m*Math.PI));
				float z=-(float)(Math.sin(Math.PI/2+2.0*i/n*Math.PI))*(float)Math.sin(1.0*j/m*Math.PI);
				mesh.positions.add(new Vector3(x,y,z));
			}
		}
		//normal
		for(Vector3 pos:mesh.positions) {
			mesh.normals.add(pos);
		}
		//face
		for(int j=1;j<m;j++) {
			for(int i=0;i<n;i++) {
				Vector3 v1=mapSphere(i,j);
				Vector3 v2=mapSphere(i+1,j);
				Vector3 v3=mapSphere(i,j+1);
				OBJFace face1=new OBJFace(3,true,true);
				face1.setVertex(0, (int)v1.x, (int)v1.y, (int)v1.z);
				face1.setVertex(1, (int)v2.x, (int)v2.y, (int)v2.z);
				face1.setVertex(2, (int)v3.x, (int)v3.y, (int)v3.z);
				mesh.faces.add(face1);
			}
		}
		for(int j=1;j<m;j++) {
			for(int i=1;i<=n;i++) {
				Vector3 v1=mapSphere(i,j);
				Vector3 v2=mapSphere(i-1,j);
				Vector3 v3=mapSphere(i,j-1);
				OBJFace face1=new OBJFace(3,true,true);
				face1.setVertex(0, (int)v1.x, (int)v1.y, (int)v1.z);
				face1.setVertex(1, (int)v2.x, (int)v2.y, (int)v2.z);
				face1.setVertex(2, (int)v3.x, (int)v3.y, (int)v3.z);
				mesh.faces.add(face1);
			}
		}
		
		try {
			mesh.writeOBJ(outputFileName);
		}catch(Exception e) {System.out.print("failed to write "+outputFileName);}
		
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
