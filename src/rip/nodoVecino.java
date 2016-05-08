package rip;

public class nodoVecino extends nodo{

	private long timer;
	
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

	public long getTimer(){
		return this.timer;
	}

	public void setTimer(long timeSystem){
		this.timer = timeSystem;
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
