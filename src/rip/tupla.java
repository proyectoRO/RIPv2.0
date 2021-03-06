package rip;

import java.time.LocalTime;

public class tupla {

	private LocalTime timerTupla;
	private String IPdestino;
	private String nextHop;
	private int metrica;
	private String mascara;

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

	public tupla(String ipVecino, String nexthop, int metrica, String mascara){
		this.IPdestino = ipVecino;
		this.nextHop = nexthop;
		this.metrica = metrica;
		this.mascara = mascara;
		this.timerTupla = LocalTime.now();
	}

	public String getIPdestino() {
		return IPdestino;
	}

	public void setIPdestino(String ip) {
		this.IPdestino = ip;
	}

	public String getNextHop() {
		return nextHop;
	}

	public void setNextHop(String nextHop) {
		this.nextHop = nextHop;
	}

	public int getMetrica() {
		return metrica;
	}

	public void setMetrica(int metrica) {
		this.metrica = metrica;
	}

	public String getMascara(){
		return this.mascara;
	}

	public tupla clonar(){
		return this;
	}

	public void setTimerTupla(){
		this.timerTupla = LocalTime.now();
	}

	public LocalTime getTimerTupla(){
		return this.timerTupla;
	}

}
