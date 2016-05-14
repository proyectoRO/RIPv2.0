package rip;

import java.time.LocalTime;

public class nodoVecino extends nodo{

	private LocalTime timer;

	public nodoVecino(){
	}

	public nodoVecino(String nombre, String IP){
		this.nombre = nombre;
		this.IP = IP;
		this.puerto = 5512;
		this.timer = LocalTime.now();
	}

	public nodoVecino(String nombre, String IP, int puerto){
		this.nombre = nombre;
		this.IP = IP;
		this.puerto = puerto;
		this.timer = LocalTime.now();
	}

	public nodoVecino(String IP, int puerto){
		this.IP = IP;
		this.puerto = puerto;
		this.timer = LocalTime.now();
	}

	public nodoVecino(String IP){
		this.IP = IP;
		this.puerto = 5512;
		this.timer = LocalTime.now();
	}

	public LocalTime getTimer(){
		return this.timer;
	}

	public void setTimer(){
		this.timer = LocalTime.now();
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
