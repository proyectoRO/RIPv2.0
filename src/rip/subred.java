package rip;

public class subred {

	private String mascara;
	private String bits;
	
	public subred(){
	}
	
	public subred(String mascara){
		this.mascara=mascara;
	}
	
	public subred(String mascara, String bits){
		this.mascara=mascara;
		this.bits=bits;
	}
	
	public String getDireccion(){
		return this.mascara;
	}
	
	public void setDireccion(){
		this.mascara=mascara;
	}
	
	public String getBits(){
		return this.bits;
	}
	
	public void setBits(){
		this.bits=bits;
	}
	
	public String toString(){
		return ("Dirección de subred: "+this.mascara+"\nMáscara de subred: "+this.bits);
	}
}
