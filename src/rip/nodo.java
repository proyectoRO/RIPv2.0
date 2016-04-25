package rip;

import java.util.ArrayList;

public class nodo {
	
	protected String nombre;
	protected String IP;
	protected ArrayList<nodoVecino> vecinos = new ArrayList<>();
	protected Boolean subred=false;
	protected int puerto;
	protected ArrayList<subred> subredes = new ArrayList<>();
	protected ArrayList<tupla> tablaEncaminamiento = new ArrayList<tupla>();
	
	public nodo(){
	}
	
	public nodo(String nombre, String IP){
		this.nombre = nombre;
		this.IP = IP;
		this.puerto = 5512;
		this.vecinos = new ArrayList<nodoVecino>();
		this.subredes = new ArrayList<subred>();
		this.tablaEncaminamiento = new ArrayList<tupla>();
	}
	
	public nodo(String nombre, String IP, ArrayList<nodoVecino> vecinos){
		this.nombre = nombre;
		this.IP = IP;
		this.vecinos = vecinos;
		this.puerto = 5512;
		this.subredes = new ArrayList<subred>();
		this.tablaEncaminamiento = new ArrayList<tupla>();
	}
	
	public nodo(String nombre, String IP, ArrayList<nodoVecino> vecinos, int puerto){
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

	public int getPuerto() {
		return puerto;
	}

	public void setPuerto(int puerto) {
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
		return ("Nombre del nodo: "+this.nombre+", IP: "+this.IP+", puerto: "+ this.puerto+"\n\n"+datos);
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
