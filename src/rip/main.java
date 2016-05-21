package rip;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.time.LocalTime;


public class main {

    private static rip.nodo nodo = new nodo();
    private static ArrayList<tupla> tablaDeVecino = new ArrayList<tupla>();
    private static ArrayList<String> IPsVecinos = new ArrayList<String>();
    private static ArrayList<String> IPsSubConect = new ArrayList<String>();
    private static String ipNodo = null;
    private static int puerto;
    private static String nombreInterfaz = "en0";
    private static TreeMap<Integer, String> indice = new TreeMap<Integer, String>();
    private static boolean flag;
    private static String passwd;

    public static void main(String args[]) {
        try {
            getPasswd();
            String hostIP;
            getDatos(args);
            rellenarIndice();
            hostIP = ipNodo;
            System.out.println("\n\n\nIP: " + hostIP + " || Puerto: " + puerto + "\n");
            nodo.setIP(ipNodo);
            nodo.setPuerto(puerto);
            parseTopo(hostIP);
            tablaEncaminamiento();
            sysoTablaEncaminamiento(nodo.getTablaEncaminamiento());
            protocoloRIP();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void getPasswd() {
        System.out.println("Por favor, introduzca una contraseña para la autenticación básica de trama.");
        System.out.println("Sólo se tomarán como contraseña los primeros 16 caracteres que introduzca.");
        System.out.println("\nPassword: \n");
        Scanner scan = new Scanner(System.in);
        passwd = scan.nextLine();
        passwd = passwd + "0000000000000000";
        passwd = new String(passwd.toCharArray(), 0, 16);

    }

    public static void getDatos(String args[]) throws SocketException {
        if (args.length == 0) {
            //codigo para obtener IP de interfaz eth0 y puerto 5512
            String interfaceName = nombreInterfaz;
            NetworkInterface networkInterface = NetworkInterface.getByName(interfaceName);
            Enumeration<InetAddress> inetAddress = networkInterface.getInetAddresses();
            InetAddress currentAddress;
            currentAddress = inetAddress.nextElement();
            while (inetAddress.hasMoreElements()) {
                currentAddress = inetAddress.nextElement();
                if (currentAddress instanceof Inet4Address && !currentAddress.isLoopbackAddress()) {
                    String ip = currentAddress.toString();
                    ipNodo = ip.substring(1);
                    puerto = 5512;
                    break;
                }
            }
        } else {
            //IP por de línea de comandos
            String argEntrada[] = args[0].trim().split("[:]");
            if (argEntrada.length == 1) {
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
        }
    }

    public static void tablaEncaminamiento() {

        ArrayList<nodoVecino> vecinos = nodo.getVecinos();

        for (int i = 0; i < vecinos.size(); i++) {
            String IPvecino = (vecinos.get(i)).getIP();
            int puertoVecino = (vecinos.get(i)).getPuerto();
            IPvecino = IPvecino + ":" + Integer.toString(puertoVecino);
            tupla tupla = new tupla(vecinos.get(i).getIP(), IPvecino, 1, "255.255.255.255");
            nodo.addTupla(tupla);
        }
        nodo.addTupla(new tupla(ipNodo, ipNodo + ":" + Integer.toString(puerto), 0, "255.255.255.255"));

    }

    public static void sysoTablaEncaminamiento(ArrayList<tupla> tablaEnc) {

        Object[][] tabla = new String[tablaEnc.size() + 1][];
        tabla[0] = new String[]{"IP Destino", "Máscara", "Sig. Salto", "Métrica"};

        for (int i = 1; i <= tablaEnc.size(); i++) {
            tabla[i] = new String[]{(tablaEnc.get(i - 1)).getIPdestino(), (tablaEnc.get(i - 1)).getMascara(), (tablaEnc.get(i - 1)).getNextHop(), Integer.toString((tablaEnc.get(i - 1)).getMetrica())};
        }

        for (Object[] tupla : tabla) {
            System.out.format("%25s%25s%25s%25s\n", tupla);
        }

    }

    public static void parseTopo(String hostIP) {
        try {
            try (BufferedReader br = new BufferedReader(
                    new FileReader(System.getProperty("user.dir") + "/ripconf-" + hostIP + ".topo.txt"))) {
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
                                    nodo.setSubred(true);
                                    tupla nuevaSubred = new tupla(camposSubred[0], camposSubred[0], 1, indice.get(Integer.parseInt(camposSubred[1])));
                                    nodo.addTupla(nuevaSubred);
                                    IPsSubConect.add(nuevaSubred.getIPdestino());
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
                                    IPsVecinos.add(vecino.getIP());
                                } else {
                                    String IP = parseIp(line);
                                    if (!IP.equals("")) {
                                        nodoVecino vecino = new nodoVecino(IP);
                                        ArrayList<nodoVecino> vecinos = nodo.getVecinos();
                                        vecinos.add(vecino);
                                        nodo.setVecinos(vecinos);
                                        IPsVecinos.add(vecino.getIP());
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

    public static String parseSubred(String line) {
        String dirSubred = "";
        String[] camposSubred = line.split("[/]");
        if (camposSubred.length > 2) {
            System.out.println("Formato incorrecto, más de 2 campos en Subred.");
            return dirSubred;
        } else {
            if ((Integer.parseInt(camposSubred[1]) < 1) || (Integer.parseInt(camposSubred[1]) > 32)) {
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

    public static void rellenarIndice() {
        indice.put(32, "255.255.255.255");
        indice.put(31, "255.255.255.254");
        indice.put(30, "255.255.255.252");
        indice.put(29, "255.255.255.248");
        indice.put(28, "255.255.255.240");
        indice.put(27, "255.255.255.224");
        indice.put(26, "255.255.255.192");
        indice.put(25, "255.255.255.128");
        indice.put(24, "255.255.255.0");
        indice.put(23, "255.255.254.0");
        indice.put(22, "255.255.252.0");
        indice.put(21, "255.255.248.0");
        indice.put(20, "255.255.240.0");
        indice.put(19, "255.255.224.0");
        indice.put(18, "255.255.192.0");
        indice.put(17, "255.255.128.0");
        indice.put(16, "255.255.0.0");
        indice.put(15, "255.254.0.0");
        indice.put(14, "255.252.0.0");
        indice.put(13, "255.248.0.0");
        indice.put(12, "255.240.0.0");
        indice.put(11, "255.224.0.0");
        indice.put(10, "255.192.0.0");
        indice.put(9, "255.128.0.0");
        indice.put(8, "255.0.0.0");
        indice.put(7, "254.0.0.0");
        indice.put(6, "252.0.0.0");
        indice.put(5, "248.0.0.0");
        indice.put(4, "240.0.0.0");
        indice.put(3, "224.0.0.0");
        indice.put(2, "192.0.0.0");
        indice.put(1, "128.0.0.0");
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
                        if (flag) tiempo = 1;
                        socketUDP.setSoTimeout((int) tiempo);
                        DatagramPacket paqueteTabla = new DatagramPacket(mensajeRIP, mensajeRIP.length);
                        timeStart = System.currentTimeMillis();
                        socketUDP.receive(paqueteTabla);
                        tiempo = tiempo - (int) (System.currentTimeMillis() - timeStart);
                        if (tiempo <= 0) {
                            tiempo = 1;
                        }
                        recibirTabla(paqueteTabla.getData(), paqueteTabla.getAddress(), paqueteTabla.getLength());
                    }
                } catch (SocketTimeoutException ste) {
                    revisarFechas();
                    flag = false;
                    System.out.println("\n*------------------------------ Tabla de encaminamiento del router -------------------------------------* \n");
                    sysoTablaEncaminamiento(nodo.getTablaEncaminamiento());
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
            byte mensajeRIP[] = Arrays.copyOfRange(paqueteTabla, 0, tamTabla);
            int totalRIP = (mensajeRIP.length - 24) / 20;
            InetAddress ipv4, mask, nextHop;
            String puerto = null;
            boolean correcto = false;
            ByteArrayOutputStream bufferArray = new ByteArrayOutputStream();
            for (int i = 0; i < nodo.getVecinos().size(); i++) {
                if (nodo.getVecinos().get(i).getIP().equals(ipVecino.getHostAddress())) {
                    System.out.println("\n\n\n\n*--------------------------------------------------------------------*");
                    System.out.println(" Se ha recibido una tabla del vecino: " + ipVecino.getHostAddress());
                    System.out.println("*--------------------------------------------------------------------*\n");
                    puerto = Integer.toString(nodo.getVecinos().get(i).getPuerto());
                    correcto = true;
                    Iterator<tupla> tablaEnc= nodo.getTablaEncaminamiento().iterator();
                    while (tablaEnc.hasNext()){
                        tupla entrada = tablaEnc.next();
                        if(entrada.getIPdestino().equals(nodo.getVecinos().get(i).getIP())){
                            entrada.setTimerTupla();
                            entrada.setMetrica(1);
                        }
                    }
                }
            }
            if (correcto) {
                int inicio = 8;
                bufferArray.reset();
                for (int i = inicio; i < inicio + 16; i++) {
                    bufferArray.write(mensajeRIP[i]);
                }
                String passRecibida = new String(bufferArray.toByteArray());
                if (passRecibida.equals(passwd)) {
                    inicio = 28;
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
                        inicio = inicio + 4;

                        //Next Hop(4)
                        bufferArray.reset();
                        for (int j = inicio; j < inicio + 4; j++) {
                            bufferArray.write(mensajeRIP[j]);
                        }
                        nextHop = InetAddress.getByAddress(bufferArray.toByteArray());
                        inicio = inicio + 4;

                        //  Metric
                        bufferArray.reset();
                        for (int j = inicio; j < inicio + 4; j++) {
                            bufferArray.write(mensajeRIP[j]);
                        }
                        int metrica = new BigInteger(bufferArray.toByteArray()).intValue();
                        inicio = inicio + 8;
                        tablaDeVecino.add(new tupla(ipv4.getHostAddress(), nextHop.getHostAddress() + ":" + puerto, metrica, mask.getHostAddress()));
                    }
                    modificarTabla();
                } else {
                    System.out.println("\n*-----------------------------------------------------------------------*");
                    System.out.println(" Recibido paquete que no ha pasado autenticación básica. Descartando.");
                    System.out.println("*-----------------------------------------------------------------------*\n");
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public static void modificarTabla() {
        flag = false;
        revisarFechas();
        ArrayList<tupla> nuevasEntradas = new ArrayList<>();
        ArrayList<String> misIPs = new ArrayList<>();
        for (int i = 0; i < nodo.getTablaEncaminamiento().size(); i++) {
            misIPs.add(nodo.getTablaEncaminamiento().get(i).getIPdestino());
        }
        for (tupla entradaVecino : tablaDeVecino) {
            Iterator<tupla> iteradorTabla = nodo.getTablaEncaminamiento().iterator();
            if (misIPs.contains(entradaVecino.getIPdestino())) {
                while (iteradorTabla.hasNext()) {
                    tupla entradaPropia = iteradorTabla.next();
                    if (entradaVecino.getIPdestino().equals(entradaPropia.getIPdestino())) {
                        if (!entradaVecino.getIPdestino().equals(nodo.getIP())) {
                            if (!IPsVecinos.contains(entradaVecino.getIPdestino())) {
                                if (!IPsSubConect.contains(entradaVecino.getIPdestino())) {
                                    if (entradaPropia.getNextHop().equals(entradaVecino.getNextHop())) {
                                        if (entradaPropia.getMetrica() < 16) {
                                            if (entradaVecino.getMetrica() == 16) {
                                                entradaPropia.setMetrica(16);
                                                entradaPropia.setTimerTupla();
                                                flag = true;
                                            } else {
                                                if (entradaVecino.getMetrica() + 1 != entradaPropia.getMetrica()) {
                                                    flag = true;
                                                }
                                                entradaPropia.setMetrica(entradaVecino.getMetrica() + 1);
                                                entradaPropia.setTimerTupla();
                                            }
                                        } else {
                                            if (entradaVecino.getMetrica() < 16) {
                                                entradaPropia.setMetrica(entradaVecino.getMetrica() + 1);
                                                entradaPropia.setTimerTupla();
                                                flag = true;
                                            }
                                        }
                                    } else {
                                        int metricaPropia = entradaPropia.getMetrica();
                                        int metricaVecino = entradaVecino.getMetrica();
                                        if (metricaVecino + 1 < metricaPropia) {
                                            entradaPropia.setMetrica(metricaVecino + 1);
                                            entradaPropia.setNextHop(entradaVecino.getNextHop());
                                            entradaPropia.setTimerTupla();
                                            flag = true;
                                        } else {
                                            if (entradaPropia.getMetrica() < 16) {
                                                entradaPropia.setTimerTupla();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                if (entradaVecino.getMetrica() < 16) {
                    if (IPsVecinos.contains(entradaVecino.getIPdestino())) {
                        if (entradaVecino.getMetrica() == 0) {
                            nuevasEntradas.add(new tupla(entradaVecino.getIPdestino(), entradaVecino.getNextHop(), entradaVecino.getMetrica() + 1, entradaVecino.getMascara()));
                            flag = true;
                        }
                    } else {
                        if (!IPsSubConect.contains(entradaVecino.getIPdestino())) {
                                nuevasEntradas.add(new tupla(entradaVecino.getIPdestino(), entradaVecino.getNextHop(), entradaVecino.getMetrica() + 1, entradaVecino.getMascara()));
                                flag = true;
                        }
                    }
                }
            }
        }
        if (nuevasEntradas.size() > 0) {
            for (int i = 0; i < nuevasEntradas.size(); i++) {
                nodo.addTupla(nuevasEntradas.get(i));
            }
        }
        nuevasEntradas.clear();
        tablaDeVecino.clear();
    }

    public static void revisarFechas() {
        Iterator<tupla> iterador = nodo.getTablaEncaminamiento().iterator();
        while (iterador.hasNext()) {
            tupla entradaTabla = iterador.next();
            if (!IPsSubConect.contains(entradaTabla.getIPdestino())) {
                if (!nodo.getIP().equals(entradaTabla.getIPdestino())) {
                    if (IPsVecinos.contains(entradaTabla.getIPdestino())) {
                        LocalTime horaUltUpd = entradaTabla.getTimerTupla();
                        if (horaUltUpd.plusSeconds(90).isBefore(LocalTime.now())) {
                            iterador.remove();
                        } else {
                            if (horaUltUpd.plusSeconds(60).isBefore(LocalTime.now())) {
                                if (entradaTabla.getMetrica() < 16) {
                                    flag = true;
                                    entradaTabla.setMetrica(16);
                                }
                            }
                        }
                    } else {
                        String split[] = entradaTabla.getNextHop().split("[:]");
                        String nh = split[0].trim();
                        for (int i = 0; i < nodo.getTablaEncaminamiento().size(); i++) {
                            if (nh.equals(nodo.getTablaEncaminamiento().get(i).getIPdestino())) {
                                if (nodo.getTablaEncaminamiento().get(i).getMetrica() == 16) {
                                    if (entradaTabla.getMetrica() < 16) {
                                        entradaTabla.setMetrica(16);
                                        entradaTabla.setTimerTupla();
                                        flag = true;
                                    }
                                }
                            }
                        }
                        if (entradaTabla.getMetrica() == 16) {
                            LocalTime horaUltUpd = entradaTabla.getTimerTupla();
                            if (horaUltUpd.plusSeconds(30).isBefore(LocalTime.now())) {
                                iterador.remove();
                            }
                        }
                    }
                }
            }
        }
    }

    public static void enviarTabla() {
        DatagramSocket socketUDP = null;
        DatagramPacket paqueteTabla = null;
        InetAddress ipv4;
        ArrayList<tupla> tablaEnviar = new ArrayList<>();
        try {
            for (int i = 0; i < nodo.getVecinos().size(); i++) {
                Iterator<tupla> tablaEncaminamiento = nodo.getTablaEncaminamiento().iterator();
                while (tablaEncaminamiento.hasNext()){
                    tupla entrada = tablaEncaminamiento.next();
                    if(nodo.getVecinos().get(i).getIP().equals(entrada.getIPdestino())){
                        if(entrada.getMetrica() < 16){
                            for (int j = 0; j < nodo.getTablaEncaminamiento().size(); j++) {
                                tablaEnviar.add(new tupla(nodo.getTablaEncaminamiento().get(j).getIPdestino(), nodo.getTablaEncaminamiento().get(j).getNextHop(),
                                        nodo.getTablaEncaminamiento().get(j).getMetrica(), nodo.getTablaEncaminamiento().get(j).getMascara()));
                            }
                            //split horizon with poison reverse.
                            for (int j = 0; j < tablaEnviar.size(); j++) {
                                if (tablaEnviar.get(j).getNextHop().equals(nodo.getVecinos().get(i).getIP() + ":" + nodo.getVecinos().get(i).getPuerto()) && (!tablaEnviar.get(j).getIPdestino().equals(nodo.getVecinos().get(i).getIP()))) {
                                    tablaEnviar.get(j).setMetrica(16);
                                } else {
                                    tablaEnviar.get(j).setNextHop(nodo.getIP() + ":" + nodo.getPuerto());
                                }
                            }
                            byte[] rip = riptobyte(tablaEnviar);
                            socketUDP = new DatagramSocket();
                            ipv4 = InetAddress.getByName(nodo.getVecinos().get(i).getIP());
                            paqueteTabla = new DatagramPacket(rip, rip.length, ipv4, nodo.getVecinos().get(i).getPuerto());
                            socketUDP.send(paqueteTabla);
                            tablaEnviar.clear();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.getMessage();
            socketUDP.close();
        }

    }

    public static byte[] riptobyte(ArrayList<tupla> listaTablaEnc) {
        /*
            RIP PKT FORMAT Authentication
            ...................................................
            comand(1)     |    version(1)   |   must be zero(2)
            ...................................................

            ...................................................
                  0xFFFF(2)        |  Authentication Type (2)
            ...................................................
                            Authentication (16)
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
            arrayBits = new ByteArrayOutputStream();
            arrayBits.write(numToByte(1, 2)); //comand(1)
            arrayBits.write(numToByte(1, 2)); //version(1)
            arrayBits.write(numToByte(2, 0)); //must be zero(2)
            arrayBits.write(numToByte(2, 65535)); //0xFFFF(2)
            arrayBits.write(numToByte(2, 2)); //Authentication Type (2)
            arrayBits.write(passwd.getBytes()); //Authentication (16)
            for (int i = 0; i < listaTablaEnc.size(); i++) {
                ip = InetAddress.getByName(listaTablaEnc.get(i).getIPdestino());
                mask = InetAddress.getByName(listaTablaEnc.get(i).getMascara());
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

