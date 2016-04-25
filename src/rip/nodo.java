package rip;

import java.util.ArrayList;

public class nodo {
	
	private String nombre;
	private String IP;
	private ArrayList<nodoVecino> vecinos = new ArrayList<>();
	private Boolean subred=false;
	private String puerto;
	private ArrayList<subred> subredes = new ArrayList<>();
	private ArrayList<tupla> tablaEncaminamiento = new ArrayList<tupla>();
	
	public nodo(){
	}
	
	public nodo(String nombre, String IP){
		this.nombre = nombre;
		this.IP = IP;
		this.puerto = "5512";
		this.vecinos = new ArrayList<nodoVecino>();
		this.subredes = new ArrayList<subred>();
		this.tablaEncaminamiento = new ArrayList<tupla>();
	}
	
	public nodo(String nombre, String IP, ArrayList<nodoVecino> vecinos){
		this.nombre = nombre;
		this.IP = IP;
		this.vecinos = vecinos;
		this.puerto = "5512";
		this.subredes = new ArrayList<subred>();
		this.tablaEncaminamiento = new ArrayList<tupla>();
	}
	
	public nodo(String nombre, String IP, ArrayList<nodoVecino> vecinos, String puerto){
		this.nombre = nombre;
		this.IP = IP;
		this.vecinos = vecinos;
		this.puerto = puerto;
		this.subredes = new ArrayList<subred>();
		this.tablaEncaminamiento = new ArrayList<tupla>();
	}
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
	}

	public ArrayList<nodoVecino> getVecinos() {
		return vecinos;
	}

	public void setVecinos(ArrayList<nodoVecino> vecinos) {
		this.vecinos = vecinos;
	}

	public Boolean getSubred() {
		return subred;
	}

	public void setSubred(Boolean subred) {
		this.subred = subred;
	}

	public String getPuerto() {
		return puerto;
	}

	public void setPuerto(String puerto) {
		this.puerto = puerto;
	}

	public ArrayList<subred> getSubredes() {
		return subredes;
	}

	public void setSubredes(ArrayList<subred> subredes) {
		this.subredes = subredes;
	}
	
	public String toString(){
		String datos="";
		if((this.vecinos).size() == 0){
			System.out.println("No hay vecinos.");
		}
		for(int i=0; i<(this.vecinos).size(); i++){
			String substr = ("Nodo vecino "+Integer.toString(i)+": "+((this.vecinos).get(i)).toString());
			datos = datos +"\n"+ substr;
		}
		if(this.subred==true){
			for(int i=0; i<(this.subredes).size();i++){
				String substr = ("Subred conectada "+Integer.toString(i)+": "+((this.subredes).get(i)).toString());
				datos = datos +"\n"+ substr;
			}
		}
		return ("Nombre del nodo: "+this.nombre+", IP: "+this.IP+", puerto: "+this.puerto+"\n\n"+datos);
	}
	
	/*
	 * Hay que añadir la tabla de encaminamiento al método toString
	 */

	public ArrayList<tupla> getTablaEncaminamiento() {
		return tablaEncaminamiento;
	}

	public void setTablaEncaminamiento(ArrayList<tupla> tablaEncaminamiento) {
		this.tablaEncaminamiento = tablaEncaminamiento;
	}

}