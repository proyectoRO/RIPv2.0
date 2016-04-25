package rip;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Timer;

public class main {

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
            nodo.setPuerto("5512");
            parseTopo(ruta, hostIP);
            System.out.println(nodo.toString());
            System.out.println("Llamando a tabla de encaminamiento...");
            tablaEncaminamiento();
            System.out.println(nodo.getVecinos().toString());
            sysoTablaEncaminamiento(nodo.getTablaEncaminamiento());

            // Socket UDP.
            System.out.println("Estableciendo comunicacion...");
            InetAddress ipv4 = InetAddress.getByName("192.168.0.45");
            DatagramSocket socketUDP = new DatagramSocket(5050);
            byte[] mensajeEnviar = ipv4.getAddress();
            byte[] mensajeCliente = new byte[8];
            Timer tiempo = new Timer();
            tiempo.schedule(new modoCliente(), 10000, 10000);

            while (true) {
                System.out.println("\n\nRIP en modo servidor");
                DatagramPacket peticion = new DatagramPacket(mensajeCliente, mensajeCliente.length);
                socketUDP.receive(peticion);
                String mensajeRecibido = new String(peticion.getData());
                System.out.println("El mensaje que el servidor recibe del cliente es: " + mensajeRecibido);
                DatagramPacket datagramaServidor = new DatagramPacket(mensajeEnviar, mensajeEnviar.length, peticion.getAddress(), peticion.getPort());
                socketUDP.send(datagramaServidor);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

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

}
