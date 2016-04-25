package rip;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class tupla {

	private String IPdestino;
	private String nextHop;
	private String interfaz;
	private int metrica;
	private String modificado; 
	
	/*
	 * De acuerdo al RFC del protocolo RIP, cada entrada en la tabla de encaminamiento ha de contar con:
	 * address: in IP implementations of these algorithms, this will be the IP address of the host or network.
	 * router: the first router along the route to the destination.
	 * interface: the physical network which must be used to reach the first router.
	 * metric: a number, indicating the distance to the destination.
	 * timer: the amount of time since the entry was last updated.
	 */
	
	public tupla(){
	}
	
	public tupla(nodoVecino vecino, String nextHop, String interfaz, int metrica){
		this.interfaz = interfaz;
		this.metrica = metrica;
		this.nextHop = nextHop;
		this.IPdestino = vecino.getIP();
		Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		this.modificado = sdf.format(cal.getTime());
	}

	public String getIPdestino() {
		return IPdestino;
	}

	public void setIPdestino(nodoVecino vecino) {
		this.IPdestino = vecino.getIP();
	}

	public String getNextHop() {
		return nextHop;
	}

	public void setNextHop(String nextHop) {
		this.nextHop = nextHop;
	}

	public String getInterfaz() {
		return interfaz;
	}

	public void setInterfaz(String interfaz) {
		this.interfaz = interfaz;
	}

	public int getMetrica() {
		return metrica;
	}

	public void setMetrica(int metrica) {
		this.metrica = metrica;
	}

	public String getModificado() {
		return modificado;
	}

	public void setModificado(String modificado) {
		this.modificado = modificado;
	}
	
	
	
}
