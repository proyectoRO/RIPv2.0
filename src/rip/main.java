package rip;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class main extends TimerTask{

    public static rip.nodo nodo = new nodo();

    /*
    Nota: nodoVecino debe ser hija de nodo. Se estan implementando metodos duplicados.
     */

    public static void main(String args[]) {
        try {
            InetAddress address = InetAddress.getLocalHost();
            String hostIP = address.getHostAddress();
            String hostName = address.getHostName();
            String ruta = "/Users/Ruben/Desktop/";
            System.out.println("IP: " + hostIP + "\n" + "Name: " + hostName);
            nodo.setIP(hostIP);
            nodo.setNombre(hostName);
            nodo.setPuerto(5512);
            parseTopo(ruta, hostIP);
            System.out.println(nodo.toString());
            System.out.println("Llamando a tabla de encaminamiento...");
            tablaEncaminamiento();
            System.out.println(nodo.getVecinos().toString());
            sysoTablaEncaminamiento(nodo.getTablaEncaminamiento());

            // Socket UDP.
            System.out.println("Estableciendo comunicacion...");
            byte mensajeCliente[] = new byte[1024];
            Timer tiempo = new Timer();
            tiempo.schedule(new main(), 10000, 10000);
            DatagramSocket socketUDP = new DatagramSocket(5050);
            byte rip[] = riptobyte();

            while (true) {
                System.out.println("\n\nRIP en modo servidor");
                DatagramPacket peticion = new DatagramPacket(mensajeCliente, mensajeCliente.length);
                socketUDP.receive(peticion);
                DatagramPacket datagramaServidor = new DatagramPacket(rip, rip.length, peticion.getAddress(), peticion.getPort());
                socketUDP.send(datagramaServidor);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static byte[] riptobyte() {
        /*
            RIP PKT FORMAT

            ...................................................
            comand(1)     |    version(1)   |   must be zero(2)
            ...................................................
                            RIP ENTRY(20)
            ...................................................


             RIP ENTRY:

             ..................................................
             address family identifier (2)  |    Route Tag (2)
             ..................................................
                                   IPv4 address (4)
             ..................................................
                                   Subnet Mask (4)
             ..................................................
                                    Next Hop (4)
             ..................................................
                                     Metric (4)
             ..................................................
             */

        ByteArrayOutputStream arrayBits = null;
        InetAddress ip;
        InetAddress mask;
        InetAddress siguienteSalto;
        try {
            ip = InetAddress.getByName("192.168.0.15");
            mask = InetAddress.getByName("255.255.255.0");
            siguienteSalto = InetAddress.getByName("192.168.10.2");
            byte[] comand = new byte[1];
            byte[] version = new byte[1];
            byte[] mbz = new byte[2];
            byte[] addfi = new byte[2];
            byte[] routeTag = new byte[2];
            byte[] ipv4 = new byte[4];
            byte[] subMask = new byte[4];
            byte[] nextHop = new byte[4];
            byte[] metric = new byte[4];

            comand = "2".getBytes();
            version = "2".getBytes();
            mbz = "00".getBytes();
            addfi = "xx".getBytes();
            routeTag = "00".getBytes();
            ipv4 = ip.getAddress();
            subMask = mask.getAddress();
            nextHop = siguienteSalto.getAddress();
            metric = "0001".getBytes();

            arrayBits = new ByteArrayOutputStream();
            arrayBits.write(comand);
            arrayBits.write(version);
            arrayBits.write(mbz);
            arrayBits.write(addfi);
            arrayBits.write(routeTag);
            arrayBits.write(ipv4);
            arrayBits.write(subMask);
            arrayBits.write(nextHop);
            arrayBits.write(metric);
            return arrayBits.toByteArray();

        } catch (Exception e) {
            e.getMessage();
        }
        return arrayBits.toByteArray();
    }


    public static void tablaEncaminamiento() {
        ArrayList<nodoVecino> vecinos = nodo.getVecinos();

        for (int i = 0; i < vecinos.size(); i++) {
            String IPvecino = (vecinos.get(i)).getIP();
            int puertoVecino = (vecinos.get(i)).getPuerto();
            IPvecino = IPvecino + ":" + Integer.toString(puertoVecino);
            tupla tupla = new tupla(vecinos.get(i), IPvecino, "default", 0);
            /*
            Para cada vecino, ruta conectada, metrica 1.
             */
            ArrayList<rip.tupla> tablaEnc = nodo.getTablaEncaminamiento();
            tablaEnc.add(tupla);
            nodo.setTablaEncaminamiento(tablaEnc);
        }
    }

    public static void sysoTablaEncaminamiento(ArrayList<tupla> tablaEnc) {

        final Object[][] tabla = new String[4][];
        tabla[0] = new String[]{"IP Vecino", "Sig. Salto", "Interfaz", "Métrica"};

        for (int i = 1; i - 1 < tablaEnc.size(); i++) {
            tabla[i] = new String[]{(tablaEnc.get(i - 1)).getIPdestino(), (tablaEnc.get(i - 1)).getNextHop(),
                    (tablaEnc.get(i - 1)).getInterfaz(), Integer.toString((tablaEnc.get(i - 1)).getMetrica())};
        }

        for (final Object[] tupla : tabla) {
            System.out.format("%20s%20s%20s%20s\n", tupla);
        }

    }

    public static String parseSubred(String line) {
        String dirSubred = "";
        String[] camposSubred = line.split("[/]");
        if (camposSubred.length > 2) {
            System.out.println("Formato incorrecto, más de 2 campos en Subred.");
            return dirSubred;
        } else {
            if ((Integer.parseInt(camposSubred[1]) < 0) || (Integer.parseInt(camposSubred[1]) > 32)) {
                System.out.println("Formato incorrecto. Número de bits de máscara de subred inválidos.");
                return dirSubred;
            } else {
                String bitsMascara = camposSubred[1];
                String mascara = parseIp(camposSubred[0]);
                if (!mascara.equals("")) {
                    dirSubred = mascara + "/" + bitsMascara;
                    return dirSubred;
                } else
                    return dirSubred;
            }
        }
    }

    public static void parseTopo(String ruta, String hostIP) {
        try {
            File archivo = new File("/Users/Ruben/Desktop/");
            try (BufferedReader br = new BufferedReader(
                    new FileReader("/Users/Ruben/Desktop/ripconf-" + hostIP + ".topo.txt"))) {
                String line = null;
                while ((line = br.readLine()) != null) {
                    line.trim();
                    try {
                        if (line.equals("") == false) {
                            String[] campo = line.split("[/]");
                            if (campo.length > 1) {
                                String direccionSubred = parseSubred(line);
                                if (!direccionSubred.equals("")) {
                                    String[] camposSubred = direccionSubred.split("/");
                                    subred subred = new subred(camposSubred[0], camposSubred[1]);
                                    ArrayList<rip.subred> subredes = nodo.getSubredes();
                                    subredes.add(subred);
                                    nodo.setSubredes(subredes);
                                    nodo.setSubred(true);
                                }
                            } else {
                                String[] campo2 = line.split("[:]");
                                if (campo2.length > 1) {
                                    System.out.println("prueba de puertos, si estás aquí es una iP con puertos");
                                    String IPPuertos = parseIpPuerto(line);
                                    String[] camposIPPuertos = IPPuertos.split(":");
                                    nodoVecino vecino = new nodoVecino(camposIPPuertos[0],
                                            Integer.parseInt(camposIPPuertos[1]));
                                    ArrayList<nodoVecino> vecinos = nodo.getVecinos();
                                    vecinos.add(vecino);
                                    nodo.setVecinos(vecinos);
                                } else {
                                    String IP = parseIp(line);
                                    if (!IP.equals("")) {
                                        nodoVecino vecino = new nodoVecino(IP);
                                        ArrayList<nodoVecino> vecinos = nodo.getVecinos();
                                        vecinos.add(vecino);
                                        nodo.setVecinos(vecinos);
                                    }
                                }
                            }

                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        System.out.println("La línea \"" + line + "\" contiene información inválida.");
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.out.println("Archivo TOPO no encontrado.");
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public static String parseIpPuerto(String line) {
        String IPPuerto = "";
        String[] camposIPPuerto = line.split("[:]");
        if (camposIPPuerto.length > 2) {
            System.out.println("Formato incorrecto, más de 2 campos en Puerto.");
            return IPPuerto;
        } else {
            if ((Integer.parseInt(camposIPPuerto[1]) > 65535) || (Integer.parseInt(camposIPPuerto[1]) < 0)) {
                System.out.println("Formato incorrecto. Número de puerto inválido.");
                return IPPuerto;
            } else {
                String puerto = camposIPPuerto[1];
                String IP = parseIp(camposIPPuerto[0]);
                if (!IP.equals("")) {
                    IPPuerto = IP + ":" + puerto;
                    return IPPuerto;
                } else
                    return IPPuerto;
            }
        }
    }

    public static String parseIp(String line) {
        String IP = "";
        Boolean validez = true, primero = true;
        String[] camposIP = line.split("[.]");
        if (camposIP.length > 4) {
            System.out.println("Formato incorrecto, más de 4 campos en IP.");
            return "";
        } else {
            for (int i = 0; i < camposIP.length; i++) {
                int valor = Integer.parseInt(camposIP[i]);
                if (valor < 0 || valor > 255) {
                    validez = false;
                } else {
                    if (primero == true) {
                        IP = camposIP[i];
                        primero = false;
                    } else {
                        IP = IP + "." + camposIP[i];
                    }
                }
            }
            if (validez == true) {
                return IP;
            } else {
                return "";
            }
        }
    }

    public void run() {
        DatagramSocket socketUDP = null;
        InetAddress ipv4,mask,nextHop;
        System.out.println("\n\nRIP en modo cliente");
        byte[] mensaje = "cliente".getBytes();
        byte[] mensajeRecibido = new byte[1024];
        try {
            ipv4 = InetAddress.getByName("192.168.0.156");
            socketUDP = new DatagramSocket();
            socketUDP.setSoTimeout(2000);
            DatagramPacket peticionUDP = new DatagramPacket(mensaje, mensaje.length, ipv4, 7000);
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
            prueba();
        } catch (Exception e) {
            socketUDP.close();
        }

    }

    public static void prueba(){
        System.out.println("Esto es una prueba, si entra, siuu");
        for (int i = 0; i < nodo.getTablaEncaminamiento().size(); i++) {
            System.out.println(nodo.getTablaEncaminamiento().get(i).getIPdestino());
        }

    }

}
