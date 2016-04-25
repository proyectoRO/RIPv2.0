package rip;

import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.TimerTask;

public class modoCliente extends TimerTask {

    InetAddress ipdestino;

    public modoCliente() {
    }

    public void run() {
        DatagramSocket socketUDP = null;
        InetAddress ipv4,mask,nextHop;;
        System.out.println("\n\nRIP en modo cliente");
        byte[] mensaje = "cliente".getBytes();
        byte[] mensajeRecibido = new byte[1024];
        try {
            ipv4 = InetAddress.getByName("192.168.0.156");
            socketUDP = new DatagramSocket();
            socketUDP.setSoTimeout(2000);
            DatagramPacket peticionUDP = new DatagramPacket(mensaje, mensaje.length, ipv4, 5050);
            socketUDP.send(peticionUDP);
            DatagramPacket datagramaRecibido = new DatagramPacket(mensajeRecibido,mensajeRecibido.length);
            socketUDP.receive(datagramaRecibido);
            mensajeRecibido = Arrays.copyOfRange(datagramaRecibido.getData(),0,datagramaRecibido.getLength());
            ByteArrayOutputStream bufferArray= new ByteArrayOutputStream();
            bufferArray.write(mensajeRecibido[8]);
            bufferArray.write(mensajeRecibido[9]);
            bufferArray.write(mensajeRecibido[10]);
            bufferArray.write(mensajeRecibido[11]);
            byte ip[] = bufferArray.toByteArray();
            bufferArray.reset();
            bufferArray.write(mensajeRecibido[12]);
            bufferArray.write(mensajeRecibido[13]);
            bufferArray.write(mensajeRecibido[14]);
            bufferArray.write(mensajeRecibido[15]);
            byte submask[] = bufferArray.toByteArray();
            bufferArray.reset();
            bufferArray.write(mensajeRecibido[16]);
            bufferArray.write(mensajeRecibido[17]);
            bufferArray.write(mensajeRecibido[18]);
            bufferArray.write(mensajeRecibido[19]);
            byte nextH[] = bufferArray.toByteArray();
            ipv4 = InetAddress.getByAddress(ip);
            mask = InetAddress.getByAddress(submask);
            nextHop = InetAddress.getByAddress(nextH);
            System.out.println("Paquete recibido con exito.");
            System.out.println("IPv4: " + ipv4.getHostAddress() + " || Mascara de subred: " + mask.getHostAddress() + " || Siguiente Salto: " + nextHop.getHostAddress());
        } catch (Exception e) {
            socketUDP.close();
        }

    }
}
