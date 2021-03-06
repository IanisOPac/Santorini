package Reseau;

import java.awt.*;
import java.io.*;
import java.net.*;

import static Utile.Constante.*;

/**
 * Classe serveur gérant la connexion d'un client grâce à l'adresse ip du joueur-serveur et communiquant avec ce dernier via des messages.
 * Voir la classe abstraite Reseau pour une description des méthodes.
 * @see Reseau
 * @see Message
 */
public class Server extends Reseau {
    private ServerSocket serverSocket;
    private boolean client_pret = false;

    public Server(String username) {
        super(username, JOUEUR1);
        connexion();
    }

    /**
     * Démarre un serveur selon un port.
     */
    @Override
    public void connexion() {
        try {
            serverSocket = new ServerSocket(PORT);
            thread = new Thread(new ServerThread(serverSocket));
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void analyserMessage(Message _message) {
        synchronized (this) {
            switch (_message.getCode()) {
                case Message.DECO -> deconnexion();
                case Message.MOVE -> jouerCoupLocal((Point) _message.getContenu());
                case Message.UNAME -> setAdversaireNom((String) _message.getContenu());
                case Message.RDY -> setClientReadyStatus((Boolean) _message.getContenu());
                default -> System.err.println("Unknown code operation.");
            }
        }
    }

    @Override
    public void deconnexion() {
        thread.interrupt();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void demarrerPartie() {
        synchronized (this) {
            if (serverSocket != null) {
                try {
                    streamEnvoie.writeObject(new Message(Message.START));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void envoieCoup(Point coup) {
        synchronized (this) {
            try {
                Message m = new Message(Message.MOVE, coup);
                streamEnvoie.writeObject(m);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void envoyerNomUtilisateur() {
        synchronized (this) {
            if (serverSocket != null) {
                Message uname = new Message(Message.UNAME, nomJoueur);
                try {
                    streamEnvoie.writeObject(uname);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void setAdversaireNom(String nom) {
        if (nom != null && lobby != null) {
            nomAdversaire = nom;
            lobby.setAdversaireNom(nom);
        }
        envoyerNomUtilisateur();
    }

    /**
     * Récupère l'adresse IP de l'hôte. Marche seulement en réseau privé.
     *
     * @return l'ip de l'hôte
     */
    public String getIPAddress() {
        String ip = "";
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            ip += socket.getLocalAddress().getHostAddress();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
        return ip;
    }

    /**
     * Met à jour sur l'interface et dans le serveur si le client est prêt
     *
     * @param status status du client (prêt ou non)
     */
    private void setClientReadyStatus(boolean status) {
        client_pret = status;
        lobby.setClient_ready(client_pret);
    }


    /**
     * Classe gérant la réception des messages venant du client.
     */
    private class ServerThread implements Runnable {

        private final ServerSocket serverSocket;
        ObjectInputStream in_object;

        public ServerThread(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            try {
                Socket socket = serverSocket.accept();
                streamEnvoie = new ObjectOutputStream(socket.getOutputStream());
                in_object = new ObjectInputStream(socket.getInputStream());
                Message result;
                while ((result = (Message) in_object.readObject()) != null) {
                    analyserMessage(result);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    // GETTER
    
    public boolean estClientPret() {
        return client_pret;
    }
}
