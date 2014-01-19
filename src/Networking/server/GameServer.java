/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Networking.server;

import Game.Map;
import Game.blocks.Block;
import Networking.communication.KeyMessage;
import Networking.communication.Message;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ubuntu
 */
public class GameServer {

    /*
     * Random rand = new Random(); int[] mapCodes = {0, rand.nextInt(8),
     * rand.nextInt(8), rand.nextInt(8),rand.nextInt(8),rand.nextInt(8), 8};
     * TiledMap[] allMaps = new TiledMap[9];
     *
     *
     * allMaps[0] = new TiledMap("/res/maps/Map1.tmx"); allMaps[1] = new
     * TiledMap("/res/maps/Map3.tmx"); allMaps[2] = new
     * TiledMap("/res/maps/Map4.tmx"); allMaps[3] = new
     * TiledMap("/res/maps/Map5.tmx"); allMaps[4] = new
     * TiledMap("/res/maps/Map6.tmx"); allMaps[5] = new
     * TiledMap("/res/maps/Map7.tmx"); allMaps[6] = new
     * TiledMap("/res/maps/Map8.tmx"); allMaps[7] = new
     * TiledMap("/res/maps/template.tmx"); allMaps[8] = new
     * TiledMap("/res/maps/EndMap.tmx");
     */
    //Data Structure: data/Players.txt
    //  Example Line: Aidan Boss
    //  Username = Aidan
    //  Password = Boss
    ArrayList Accounts = new ArrayList();
    private ServerSocket serverSocket;
    ArrayList<TcpThread> flexclients = new ArrayList<TcpThread>(10);
    //HashMap patches = new HashMap();
    /**
     * The time of the last update to the clients, in milliseconds
     */
    public long lastSend = 0;              //Milliseconds ago
    /**
     * The interval on which the server should send updates to all the clients,
     * in milliseconds
     */
    public final int updateInterval = 30; //In milliseconds
    /**
     * The time of the last server update cycle, in milliseconds
     */
    public long lastUpdate = 0;
    UpdaterThread updater = new UpdaterThread();
    PhysicsHandler physEngine;
    int currentPlayerNum = 0;
    ArrayList<Team> teams = new ArrayList<Team>();

    /**
     * The standard constructor for the GameServer class. Starts the server,
     * loading all required objects, and then waits for clients
     *
     * @param port The port on which the server should be created
     */
    public GameServer(int port) {

        //Load data from files
        loadAccounts();
        System.out.println("Done loading users");


        //BANDAID
        startServer();

        /*
         * create socket server and wait for connection requests
         */
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server waiting for client on port " + serverSocket.getLocalPort());

            //Loop continuously waiting for connections
            while (true) {
                // accept connection
                Socket socket = serverSocket.accept();
                //System.out.println();
                //System.out.println("New client asked for a connection");

                // make a thread of it          
                //System.out.println(numclients + ", " + clients.length);
                TcpThread newclient = new TcpThread(socket);
                newclient.ID = flexclients.size();
                newclient.start();
                flexclients.add(newclient);

                System.out.println("Starting a thread for Client #" + flexclients.size());
            }
        } catch (IOException e) {
            System.out.println("Exception on new ServerSocket: " + e);
        }
    }

    //Gets the account file
    /**
     * Gets the BufferedReader object corresponding to the account file.
     *
     * @return The BufferedReader object.
     */
    public BufferedReader getAccountFile() {
        InputStream file = this.getClass().getResourceAsStream("Players.txt");
        BufferedReader freader = new BufferedReader(new InputStreamReader(file));
        return freader;
    }

    //Loads the accout usernames and passwords into the server.
    //Wins and losses could easily be added here as well
    /**
     * Loads the account usernames and passwords into the server.
     */
    public void loadAccounts() {
        try {
            BufferedReader freader = getAccountFile();
            String str;
            while ((str = freader.readLine()) != null) {
                Accounts.add(str.split(" "));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //
    /**
     * Updates the chat. Not currently in use, but in the future could be used
     * to add chat functionality
     *
     * @param x The string to be sent.
     */
    public void updateChat(String x) {
        sendAll(x);
    }

    /**
     * Sends an object to all connected users. Used to synchronize clients.
     *
     * @param data The object to be sent.
     */
    public void sendAll(Object data) {
        for (int i = 0; i < flexclients.size(); i++) {
//            try {
//                if (((TcpThread) flexclients.get(i)).loggedin) {                    
//                    ((TcpThread) flexclients.get(i)).Soutput.writeObject(data);
//                    ((TcpThread) flexclients.get(i)).Soutput.flush();
//                }
//            } catch (IOException e) {
//                System.out.println("Exception reading/writing  Streams (UpdatePlayers): " + e);
//                if (e.toString().equals("java.net.SocketException: Broken pipe")) {
//                    System.out.println("Disconnected Client");
//                    disconnectClient(i);
//                }
//                continue;
//            }
            if (((TcpThread) flexclients.get(i)).loggedin) {
                ((TcpThread) flexclients.get(i)).sendQueue.add(data);
            }
        }
    }

    //Disconnect the clinet with the ID specified
    /**
     * Disconnects the client with the ID specified. Closes all streams, removes
     * the client from the client list, and updates the list.
     *
     * @param ID The client ID to remove.
     */
    public void disconnectClient(int ID) {
        //Closes the socket in addition to the input and output streams
        try {
            ((TcpThread) flexclients.get(ID)).Soutput.close();
            ((TcpThread) flexclients.get(ID)).Sinput.close();
            ((TcpThread) flexclients.get(ID)).socket.close();
        } catch (Exception e) {
        }

        //deletes the client from the client list
        flexclients.remove(ID);
        updateClients();
    }

    //Upodates the ID's of all of the clients
    /**
     * Updates the ID's of all of the clients
     */
    public synchronized void updateClients() {
        for (int i = 0; i < flexclients.size(); i++) {
            TcpThread currThread = (TcpThread) flexclients.get(i);
            currThread.ID = i;
            System.out.println(currThread.username + " " + currThread.ID);
        }
    }

    //Closes all open streams, and closes the server
    /**
     * Closes all open streams, and closes the server
     */
    public synchronized void exitServer() {
        for (int i = 0; i < flexclients.size(); i++) {
            try {
                TcpThread currclient = (TcpThread) flexclients.get(i);
                currclient.Soutput.close();
                currclient.Sinput.close();
                currclient.socket.close();
            } catch (Exception e) {
            }
        }
        flexclients.removeAll(flexclients);
        System.exit(0);
    }

    /**
     * Initializes the logic and physics components of the server
     */
    public void startServer() {
        physEngine = new PhysicsHandler();
        updater.start();
    }

    //----------------------------------------------
    //-----------------Updater Loop-----------------
    //----------------------------------------------    
    //This runs any code the server has to execute, in this case the UpdaterThread is
    //in charge of all of the games's physics
    class UpdaterThread extends Thread {

        @Override
        public void run() {
            init();
            while (true) {
                int t1 = (int) (System.currentTimeMillis());

                readQueue();
                update();

                int t2 = (int) (System.currentTimeMillis());

                //Sleep the thread so that it doesn't run too fast
                if (t2 - t1 < 15) {
                    try {
                        Thread.sleep(15 - t2 + t1);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        public void init() {
            lastUpdate = (int) (System.currentTimeMillis());
        }

        //Updates all the objects, and is in charge of synchronizing them
        //  with the players
        public void update() {
            int delta = (int) (System.currentTimeMillis() - lastUpdate);
            lastUpdate = System.currentTimeMillis();
            physEngine.update(delta);

            //Sends out the UpdatePatch Objects to all the players
            if ((System.currentTimeMillis() - lastSend) >= updateInterval) {
                Message[] updates = physEngine.getPlayerUpdates();
                sendAll(updates);

                //Send all the blocks to the client
                Block[] bUp = physEngine.getBlockUpdates();
                if (bUp.length != 0) {
                    sendAll(bUp);
                }
                lastSend = System.currentTimeMillis();
            }
        }

        public void readQueue() {
            for (int i = 0; i < physEngine.gameSync.queue.size(); i++) {
                parseObject(physEngine.gameSync.queue.get(i));
            }
            physEngine.gameSync.queue.clear();
        }

        public void parseObject(Object objectIn) {
            if (objectIn instanceof Message) {
                Message msg = (Message) objectIn;
                physEngine.makePlayer(msg.ID, msg.p, msg.user);
            } else if (objectIn instanceof KeyMessage) {
                physEngine.movePlayer((KeyMessage) objectIn);
            } else if (objectIn instanceof Block) {
                physEngine.makeBlock((Block) objectIn);
            } else if (objectIn instanceof Map) {
                physEngine.addMap((Map) objectIn);
            }
        }
    }

//----------------------------------------------
//-----------------Client Handling--------------
//----------------------------------------------    
    /**
     * One instance of this thread will run for each client Controls the reading
     * from streams.
     */
    class TcpThread extends Thread {
        // the socket where to listen/talk

        Socket socket;
        ObjectInputStream Sinput;
        ObjectOutputStream Soutput;
        int ID;
        String username = "";
        boolean loggedin = false;
        int team = -1;
        int player = -1;
        boolean outInUse = false;
        ArrayList sendQueue = new ArrayList();

        TcpThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            /*
             * Creating both Data Streams
             */
            System.out.println("Thread trying to create Object Input/Output Streams");
            try {
                //Create output first
                Soutput = new ObjectOutputStream(socket.getOutputStream());
                //Soutput.flush ();
                Sinput = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                //System.out.println("Exception creating new Input/output Streams: " + e);
                return;
            }

            //The output Thread
            new Thread() {

                boolean sent = false;

                @Override
                public void run() {
                    while (true) {
                        for (int i = 0; i < sendQueue.size(); i++) {
                            reply(sendQueue.get(i));
                            sent = true;
                        }
                        if (sent) {
                            sendQueue.clear();
                            sent = false;
                        }
                    }
                }
            }.start();

            //Login Process
            //--------------------------------------------------
            String loginusername;
            String loginpassword;

            while (!loggedin) {
                try {
                    String[] Credentials = ((String) Sinput.readObject()).split(" ");

                    loginusername = Credentials[1];
                    loginpassword = Credentials[2];
                    System.out.println("received login - user: " + loginusername + " pass: " + loginpassword);

                    for (int i = 0; i < Accounts.size(); i++) {

                        String[] accountinfo = ((String[]) Accounts.get(i));
                        if (Credentials[0].equals("login")) {
                            if (loginusername.equals(accountinfo[0])) {
                                System.out.println("username is correct");
                                if (loginpassword.equals(accountinfo[1])) {
                                    System.out.println("password is correct");
                                    login(accountinfo);
                                    loggedin = true;
                                    break;
                                } else {
                                    //password is wrong, put in code
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            //Loads any objects that may need to be loaded before the game can start
            loadObjects();

            //Team Select Process
            //--------------------------------------------------           
//            class playerSender extends Thread {
//                @Override
//                public void run() {
//                    while (true) {
//                        for (int i = 0; i < flexclients.size(); i++) {
//                            PlayerInfo np = new PlayerInfo(flexclients.get(i).username, flexclients.get(i).team, flexclients.get(i).player);
//                            //System.out.println("added " + np.getClass());
//                            reply(np);
//                        }
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException ex) {
//                            Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                    }
//                }
//            }
//            playerSender send = new playerSender();
//            send.start();
//
//            while (true) {
//                try {
//                    Object in = Sinput.readObject();
//                    //If the client has not made a choice, it will ask for an update 
//                    if (in instanceof String) {
//                        if (((String) in).equals("start")) {
//                            System.out.println("received start signal");
//                            sendAll(in);
//                            if (!updater.isAlive()) {
//                                startServer();
//                            }
//                            break;
//                        }
//                    } //If the client has made a choice, it will notfy the server with an player specification
//                    else if (in instanceof PlayerInfo) {
//                        System.out.println("received playerinfo");
//                        PlayerInfo p = (PlayerInfo) in;
//
//                        if (teams.contains(new Team(p.team))) {
//                            Team target = teams.get(teams.indexOf(new Team(p.team)));
//                            if (!target.players[p.player]) {
//                                //Empties old team slot
//                                if (team != -1 && player != -1) {
//                                    Team old = teams.get(teams.indexOf(new Team(team)));
//                                    old.players[player] = false;
//                                }
//                                target.players[p.player] = true;
//                                team = p.team;
//                                player = p.player;
//                            }
//                        } else {
//                            //Empties old team slot
//                            if (team != -1 && player != -1) {
//                                Team old = teams.get(teams.indexOf(new Team(team)));
//                                old.players[player] = false;
//                            }
//                            System.out.println("created team: " + p.team);
//                            Team nt = new Team(p.team);
//                            nt.players[p.player] = true;
//                            teams.add(nt);
//                            team = p.team;
//                            player = p.player;
//                        }
//                    }
//                } catch (IOException ex) {
//                    Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, null, ex);
//                } catch (ClassNotFoundException ex) {
//                    Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//
//            //Game Initialization
//            //--------------------------------------------------
//            sendQueue.clear();

            //Main Loop
            //--------------------------------------------------
            System.out.println("Main Loop running");
            while (true) {
                try {
                    //Receives object input
                    Object objectIn = Sinput.readObject();
                    if (objectIn instanceof String) {
                        System.out.println("Received String");
                        handleString((String) objectIn);
                    } else if (objectIn instanceof Message) {
                        ((Message) objectIn).ID = (short) ID;
                        physEngine.enQueue(objectIn);
                    } else if (objectIn instanceof KeyMessage) {
                        physEngine.enQueue(objectIn);
                    } else if (objectIn instanceof Map) {
                        physEngine.enQueue(objectIn);
                    } else if (objectIn instanceof Block) {
                        physEngine.enQueue(objectIn);
                    } else {
                        //do nothing
                    }
                } catch (IOException e) {
                    System.out.println("Exception reading/writing  Streams (TCPthread): " + e);
                    if (e.toString().equals("Broken pipe")) {
                        disconnectClient(ID);
                        System.out.println("disconnected");
                    }
                    //System.out.println("Server-Side Error");
                    return;
                } // will surely not happen with a String
                catch (ClassNotFoundException o) {
                }

            }
        }

        private void handleString(String x) {
            //tests to see if the code matches some predefined server commands  
            if (x.substring(0, 5).equals("serv.")) {
                if (x.equalsIgnoreCase("serv.dc")) {
                    sendQueue.add("disconecting");
                    disconnectClient(ID);
                } else if (x.equalsIgnoreCase("serv.pnum")) {
                    System.out.println("Server: Sending Client Number");
                    sendQueue.add(new Integer(ID));
                } else if (x.split(" ")[0].equalsIgnoreCase("serv.setuser")) {
                    username = x.split(" ")[1];
                } else if (x.equalsIgnoreCase("serv.whoami")) {
                    sendQueue.add("Username: " + username);
                } else if (x.equalsIgnoreCase("serv.close")) {
                    updateChat("Closing server, disconnecting all clients");
                    updateChat("Please close your client window");
                    exitServer();
                } else if (x.equalsIgnoreCase("serv.numclients")) {
                    sendQueue.add("# of connected clients: " + flexclients.size());
                } else {
                    sendQueue.add("malformed server command");
                }
            } else {
                updateChat(x);
            }
        }

        private void login(String[] accountinfo) {
            username = accountinfo[0];
            System.out.println("User: " + username + " logged in");
            sendQueue.add("loggedin");
        }

        private void loadObjects() {
        }

        private void reply(Object x) {
            try {
                Soutput.writeObject(x);
                Soutput.flush();
            } catch (Exception e) {
            }
        }
    }
}
