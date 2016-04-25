package rip;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.TimerTask;

public class modoCliente extends TimerTask {

    InetAddress ipdestino;

    public modoCliente() {
    }

    public void run() {
        DatagramSocket socketUDP = null;
        try {
            System.out.println("\n\nRIP en modo cliente");
            byte[] mensaje = "cliente1".getBytes();
            byte[] mensajeRecibido = new byte[4];
            InetAddress ipv4 = InetAddress.getByName("192.168.0.156");
            socketUDP = new DatagramSocket();
            socketUDP.setSoTimeout(2000);
            DatagramPacket peticionUDP = new DatagramPacket(mensaje, mensaje.length, ipv4, 7000);
            socketUDP.send(peticionUDP);
            DatagramPacket datagramaRecibido = new DatagramPacket(mensajeRecibido,mensajeRecibido.length);
            socketUDP.receive(datagramaRecibido);
            InetAddress ipRecibida = InetAddress.getByAddress(datagramaRecibido.getData());
            System.out.println("IP recibida: " + ipRecibida.getHostAddress());
        } catch (Exception e) {
            socketUDP.close();
        }

    }
}