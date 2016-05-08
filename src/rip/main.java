package rip;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;

public class main {

    static rip.nodo nodo = new nodo();
    static ArrayList<tupla> tablaVecinos = new ArrayList<tupla>();
    static String ipNodo = null;
    static int puerto;


    public static void main(String args[]) {
        try {
            InetAddress address = InetAddress.getLocalHost();
            String hostName = address.getHostName();
            String hostIP = address.getHostAddress();
            if (!args[0].equals(null)) {
                String argEntrada[] = args[0].trim().split("[:]");
                if (argEntrada.length == 1) {
                    //usar codigo david para comprobar la validez de la ip introducida por linea de comandos.
                    ipNodo = argEntrada[0].trim();
                    puerto = 5512;
                } else {
                    if (argEntrada.length == 2) {
                        ipNodo = argEntrada[0].trim();
                        puerto = Integer.parseInt(argEntrada[1].trim());
                    } else {
                        System.out.println("Datos introducidos por linea de comandos incorrectos");
                        System.exit(-1);
                    }
                }
            } else {
                ipNodo = hostIP;
                puerto = 5512;
            }
            hostIP = ipNodo;
            String ruta = args[1];
            System.out.println("IP: " + hostIP + " || Name: " + hostName + " || Puerto: " + puerto + "\n");
            nodo.setIP(ipNodo);
            nodo.setNombre(hostName);
            nodo.setPuerto(puerto);
            parseTopo(ruta, hostIP);
            System.out.println(nodo.toString());
            tablaEncaminamiento();
            System.out.println(nodo.getVecinos().toString());
            sysoTablaEncaminamiento(nodo.getTablaEncaminamiento());
            System.out.println("\n\n");
            protocoloRIP();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void tablaEncaminamiento() {
        ArrayList<nodoVecino> vecinos = nodo.getVecinos();

        for (int i = 0; i < vecinos.size(); i++) {
            vecinos.get(i).setTimer(System.currentTimeMillis());
            String IPvecino = (vecinos.get(i)).getIP();
            int puertoVecino = (vecinos.get(i)).getPuerto();
            IPvecino = IPvecino + ":" + Integer.toString(puertoVecino);
            tupla tupla = new tupla(vecinos.get(i), IPvecino, "default", 1);
            /*
            Para cada vecino, ruta conectada, metrica 1.
             */
            ArrayList<rip.tupla> tablaEnc = nodo.getTablaEncaminamiento();
            tablaEnc.add(tupla);
            nodo.setTablaEncaminamiento(tablaEnc);
        }
        ArrayList<tupla> miTabla = nodo.getTablaEncaminamiento();
        miTabla.add(new tupla(ipNodo, ipNodo + ":" + Integer.toString(puerto), 0, "255.255.255.0", "default"));
        nodo.setTablaEncaminamiento(miTabla);
    }

    public static void sysoTablaEncaminamiento(ArrayList<tupla> tablaEnc) {

        final Object[][] tabla = new String[tablaEnc.size() + 1][];
        tabla[0] = new String[]{"IP Destino", "Sig. Salto", "Interfaz", "Métrica"};

        for (int i = 1; i <= tablaEnc.size(); i++) {
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
            File archivo = new File(ruta);
            try (BufferedReader br = new BufferedReader(
                    new FileReader(ruta + "/ripconf-" + hostIP + ".topo.txt"))) {
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


    public static void protocoloRIP() {
        byte mensajeRIP[] = new byte[1024];
        DatagramSocket socketUDP = null;
        try {
            socketUDP = new DatagramSocket(nodo.getPuerto());
            while (true) {
                Random rand = new Random();
                int simbolo = rand.nextInt(2);
                float numAleat = rand.nextFloat() * 3;
                float tiempo;
                if (simbolo == 0) {
                    tiempo = (10000) + numAleat;
                } else {
                    tiempo = (10000) - numAleat;
                }
                long timeStart;
                try {
                    while (true) {
                        socketUDP.setSoTimeout((int) tiempo);
                        DatagramPacket paqueteTabla = new DatagramPacket(mensajeRIP, mensajeRIP.length);
                        timeStart = System.currentTimeMillis();
                        socketUDP.receive(paqueteTabla);
                        tiempo = tiempo - (int) (System.currentTimeMillis() - timeStart);
                        recibirTabla(paqueteTabla.getData(), paqueteTabla.getAddress(), paqueteTabla.getLength());
                    }
                } catch (SocketTimeoutException ste) {
                    enviarTabla();
                } catch (IOException ioe) {
                    ioe.getMessage();
                    socketUDP.close();
                }
            }
        } catch (SocketException se) {
            se.getMessage();
            socketUDP.close();
        }
    }

    public static void recibirTabla(byte paqueteTabla[], InetAddress ipVecino, int tamTabla) {
        try {
            //seteamos el paqueteTabla recibido por el socket
            byte mensajeRIP[] = Arrays.copyOfRange(paqueteTabla, 0, tamTabla);
            int totalRIP = (mensajeRIP.length - 4) / 20;
            InetAddress ipv4, mask;
            String puerto = null;
            boolean correcto = false;
            ByteArrayOutputStream bufferArray = new ByteArrayOutputStream();
            System.out.println("\n\nTabla de encaminamiento de los vecinos recibida con exito. Tamaño paquete: " + mensajeRIP.length + " IP RECIBIDA: " + ipVecino.getHostAddress());
            for (int i = 0; i < nodo.getVecinos().size(); i++) {
                if (nodo.getVecinos().get(i).getIP().equals(ipVecino.getHostAddress())) {
                    System.out.println("tabla proviene de un vecino autenticado: " + ipVecino.getHostAddress());
                    correcto = true;
                    puerto = Integer.toString(nodo.getVecinos().get(i).getPuerto());
                    nodo.getVecinos().get(i).setTimer(System.currentTimeMillis());
                }
            }
            if (correcto) {
                int inicio = 8;
                for (int i = 0; i < totalRIP; i++) {
                    //IPv4 address (4)
                    bufferArray.reset();
                    for (int j = inicio; j < inicio + 4; j++) {
                        bufferArray.write(mensajeRIP[j]);
                    }
                    ipv4 = InetAddress.getByAddress(bufferArray.toByteArray());
                    inicio = inicio + 4;

                    //Subnet Mask (4)
                    bufferArray.reset();
                    for (int j = inicio; j < inicio + 4; j++) {
                        bufferArray.write(mensajeRIP[j]);
                    }
                    mask = InetAddress.getByAddress(bufferArray.toByteArray());
                    inicio = inicio + 8;
                    //  Metric
                    bufferArray.reset();
                    for (int j = inicio; j < inicio + 4; j++) {
                        bufferArray.write(mensajeRIP[j]);
                    }
                    int metrica = new BigInteger(bufferArray.toByteArray()).intValue();
                    inicio = inicio + 8;
                    tablaVecinos.add(new tupla(ipv4.getHostAddress(), ipVecino.getHostAddress() + ":" + puerto, metrica, mask.getHostAddress(), "default"));

                }
                modificarTabla();
            }
        } catch (Exception e) {
            e.getMessage();
        }

    }

    public static void modificarTabla() {
        ArrayList<String> ipsConocidas = new ArrayList<String>();
        for (int i = 0; i < nodo.getTablaEncaminamiento().size(); i++) {
            ipsConocidas.add(nodo.getTablaEncaminamiento().get(i).getIPdestino());
        }

        for (int i = 0; i < tablaVecinos.size(); i++) {
            if (ipsConocidas.contains(tablaVecinos.get(i).getIPdestino())) {
                //comparar metrica
                int puesto = ipsConocidas.indexOf(tablaVecinos.get(i).getIPdestino());
                tupla tuplaVieja = nodo.getTablaEncaminamiento().get(puesto);
                int metricaPropia = nodo.getTablaEncaminamiento().get(puesto).getMetrica();
                int metricaVecino = tablaVecinos.get(i).getMetrica();
                if (metricaVecino + 1 < metricaPropia) {
                    nodo.getTablaEncaminamiento().remove(puesto);
                    tupla tuplaNueva = new tupla(tablaVecinos.get(i).getIPdestino(), tablaVecinos.get(i).getNextHop(),
                            tablaVecinos.get(i).getMetrica() + 1, tablaVecinos.get(i).getMascara(), tablaVecinos.get(i).getInterfaz());
                    ArrayList<tupla> vieja = nodo.getTablaEncaminamiento();
                    vieja.add(tuplaNueva);
                    nodo.setTablaEncaminamiento(vieja);
                }
            } else {
                tupla tuplaNueva = new tupla(tablaVecinos.get(i).getIPdestino(), tablaVecinos.get(i).getNextHop(),
                        tablaVecinos.get(i).getMetrica() + 1, tablaVecinos.get(i).getMascara(), tablaVecinos.get(i).getInterfaz());
                ArrayList<tupla> vieja = nodo.getTablaEncaminamiento();
                vieja.add(tuplaNueva);
                nodo.setTablaEncaminamiento(vieja);
            }
        }

        tablaVecinos.clear();
    }

    public static void enviarTabla() {
        DatagramSocket socketUDP = null;
        DatagramPacket paqueteTabla = null;
        InetAddress ipv4;
        System.out.println("\n\nEnviado la siguiente tabla de encaminamiento a los vecinos:\n");
        sysoTablaEncaminamiento(nodo.getTablaEncaminamiento());
        try {
            for (int i = 0; i < nodo.getVecinos().size(); i++) {
                String ip = nodo.getVecinos().get(i).getIP();
                ipv4 = InetAddress.getByName(ip);
                socketUDP = new DatagramSocket();
                byte[] rip = riptobyte();
                paqueteTabla = new DatagramPacket(rip, rip.length, ipv4, nodo.getVecinos().get(i).getPuerto());
                socketUDP.send(paqueteTabla);
            }

        } catch (Exception e) {
            e.getMessage();
            socketUDP.close();
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
        ArrayList<tupla> listaTablaEnc = nodo.getTablaEncaminamiento();
        try {
            arrayBits = new ByteArrayOutputStream();
            arrayBits.write(numToByte(1, 2)); //comand(1)
            arrayBits.write(numToByte(1, 2)); //version(1)
            arrayBits.write(numToByte(2, 0)); //must be zero(2)
            for (int i = 0; i < listaTablaEnc.size(); i++) {
                ip = InetAddress.getByName(listaTablaEnc.get(i).getIPdestino());
                mask = InetAddress.getByName("255.255.255.0");
                String nh[] = listaTablaEnc.get(i).getNextHop().split("[:]");
                siguienteSalto = InetAddress.getByName(nh[0]);
                byte ipv4[] = ip.getAddress();
                byte subMask[] = mask.getAddress();
                byte nextHop[] = siguienteSalto.getAddress();
                arrayBits.write(numToByte(2, 2)); //address family indentifier(2)
                arrayBits.write(numToByte(2, 0)); //Route Tag(2)
                arrayBits.write(ipv4); //IPv4
                arrayBits.write(subMask); //Subnet Mask(4)
                arrayBits.write(nextHop); //Next Hop (4)
                arrayBits.write(numToByte(4, listaTablaEnc.get(i).getMetrica())); //Metric (4)
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return arrayBits.toByteArray();
    }


    public static byte[] numToByte(int sizeInBytes, int numToConvert) {
        ByteBuffer byteBuffer = null;
        if (sizeInBytes == 2) {
            short numShort = (short) numToConvert;
            byteBuffer = ByteBuffer.allocate(2);
            byteBuffer.putShort(numShort);
        } else {
            if (sizeInBytes == 4) {
                int numInt = numToConvert;
                byteBuffer = ByteBuffer.allocate(4);
                byteBuffer.putInt(numInt);
            } else {
                byte numByte = (byte) numToConvert;
                byteBuffer = ByteBuffer.allocate(1);
                byteBuffer.put(numByte);
            }
        }
        return byteBuffer.array();
    }
}
