package rip;

public class nodoVecino extends nodo{

	
	public nodoVecino(){
	}
	
	public nodoVecino(String nombre, String IP){
		this.nombre = nombre;
		this.IP = IP;
		this.puerto = 5512;
	}
	
	public nodoVecino(String nombre, String IP, int puerto){
		this.nombre = nombre;
		this.IP = IP;
		this.puerto = puerto;
	}
	
	public nodoVecino(String IP, int puerto){
		this.IP = IP;
		this.puerto = puerto;
	}
	
	public nodoVecino(String IP){
		this.IP = IP;
		this.puerto = 5512;
	}

	
	public String toString(){
		String subredTS=null;
		if(this.subred==true){
			subredTS="sí";
		}else{
			subredTS="no";
		}
		return("Nombre del nodo: "+this.nombre+", dirección IP: "+this.IP+", Subred conectada: "+subredTS+", puerto en uso: "+Integer.toString(this.puerto)+".\n");
	}
}
