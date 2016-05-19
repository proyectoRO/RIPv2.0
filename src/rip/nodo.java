package rip;

import java.util.ArrayList;

public class nodo {
	
	protected String nombre;
	protected String IP;
	private ArrayList<nodoVecino> vecinos = new ArrayList<nodoVecino>();
	protected Boolean subred=false;
	protected int puerto;
	private ArrayList<tupla> tablaEncaminamiento = new ArrayList<tupla>();
	
	public nodo(){
	}
	
	public nodo(String nombre, String IP){
		this.nombre = nombre;
		this.IP = IP;
		this.puerto = 5512;
		this.vecinos = new ArrayList<nodoVecino>();
		this.tablaEncaminamiento = new ArrayList<tupla>();
	}
	
	public nodo(String nombre, String IP, ArrayList<nodoVecino> vecinos){
		this.nombre = nombre;
		this.IP = IP;
		this.vecinos = vecinos;
		this.puerto = 5512;
		this.tablaEncaminamiento = new ArrayList<tupla>();
	}
	
	public nodo(String nombre, String IP, ArrayList<nodoVecino> vecinos, int puerto){
		this.nombre = nombre;
		this.IP = IP;
		this.vecinos = vecinos;
		this.puerto = puerto;
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

	
	public String toString(){
		String datos="";
		if((this.vecinos).size() == 0){
			System.out.println("No hay vecinos.");
		}
		for(int i=0; i<(this.vecinos).size(); i++){
			String substr = ("Nodo vecino "+Integer.toString(i)+": "+((this.vecinos).get(i)).toString());
			datos = datos +"\n"+ substr;
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

	public void modificarTablaEncaminamiento(int indice, tupla nuevaTupla){
		this.tablaEncaminamiento.remove(indice);
		this.tablaEncaminamiento.add(nuevaTupla);
	}

	public void addTupla(tupla nuevaTupla){

		this.tablaEncaminamiento.add(nuevaTupla);
	}

	public int getMetrica(tupla nuevaTupla){
		int metrica = 16;
		for (int i = 0; i < this.tablaEncaminamiento.size(); i++) {
			if(nuevaTupla.getIPdestino().equals(this.tablaEncaminamiento.get(i).getIPdestino())){
				metrica =  this.tablaEncaminamiento.get(i).getMetrica();
			}
		}
		return metrica;
	}

	public void borrarTupla(tupla borrarTupla){
		for (int i = 0; i < this.tablaEncaminamiento.size(); i++) {
			if(borrarTupla.getIPdestino().equals(this.tablaEncaminamiento.get(i).getIPdestino())){
				this.tablaEncaminamiento.remove(i);
			}
		}
	}

	public void actualizarTimer(tupla actualizarTupla){
		for (int i = 0; i < this.tablaEncaminamiento.size(); i++) {
			if(actualizarTupla.getIPdestino().equals(this.tablaEncaminamiento.get(i).getIPdestino())){
				this.tablaEncaminamiento.get(i).setTimerTupla();
			}
		}
	}
}
