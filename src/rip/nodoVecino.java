package rip;

public class nodoVecino {

	private String nombre;
	private String IP;
	private Boolean subred=false;
	private int puerto;
	
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
	
	public String getNombre(){
		return this.nombre;
	}
	
	public String getIP(){
		return this.IP;
	}
	
	public boolean getSubred(){
		return this.subred;
	}
	
	public int getPuerto(){
		return this.puerto;
	}
	
	public void setNombre(String nombre){
		this.nombre = nombre;
	}
	
	public void setIP(String IP){
		this.IP = IP;
	}
	
	public void setSubred(boolean subred){
		this.subred = subred;
	}
	
	public void setPuerto(int puerto){
		this.puerto = puerto;
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
