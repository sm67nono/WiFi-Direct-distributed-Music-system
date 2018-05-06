package de.tu_darmstadt.informatik.newapp.Server.Host_Core;



import android.os.Handler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


/**
 * Created by manna on 24-01-2017.
 */

public class ServerGroupOwnerSocketHandle extends Thread {


    private boolean reEstablishConnection=true;
    public static final String SEPERATOR = "##";
    private static final int BUFF_SIZE = 256;
    public static final String MUSIC_PLAY = "SIGNAL_PLAY";
    public static final String MUSIC_STOP = "SIGNAL_STOP";
    public static final String MUSIC_BAL = "SIGNAL_BAL";
    public static final String MUSIC_LIST = "MUSIC_LIST";
    public static final int PORT = 7950;
    public ServerSocket sockHost = null;
    private Handler handler;
    public ArrayList<Socket> clientSocketList;;
    public static final int CB_SVR = 112;

    //Constructor for initialization

    public ServerGroupOwnerSocketHandle(Handler handler) throws IOException
    {
        this.handler = handler;
        clientSocketList = new ArrayList<Socket>();
        HostCreateSocket();
    }


    @Override
    public void run()
    {
        while (sockHost != null)
        {
            try
            {
                handler.obtainMessage(CB_SVR, this).sendToTarget();
                HostCreateSocket();
                Socket Client_Socket = sockHost.accept();

                clientSocketList.add(Client_Socket);
            }
            catch (IOException e)
            {

                try
                {
                    if (sockHost != null && !sockHost.isClosed())
                    {
                        reEstablishConnection = true;


                        for (Socket clientsocket : clientSocketList)
                        {
                            clientsocket.close();
                        }

                        clientSocketList = new ArrayList<Socket>();
                    }
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                    //disconnect_the_Server();
                }
                break;
            }
        }
    }

    public void HostCreateSocket()
    {
        if (!reEstablishConnection)
        {
            return;
        }

        try
        {
            if (sockHost != null && !sockHost.isClosed())
            {
                sockHost.close();
                sockHost = null;
            }

            sockHost = new ServerSocket(); // <-- create an unbound socket first
            sockHost.setReuseAddress(true);
            sockHost.bind(new InetSocketAddress(PORT)); // <-- now bind it

            //sockHost.setReuseAddress(true);
            //sockHost = new ServerSocket(PORT);

            reEstablishConnection = false;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //Sending Music Message Order to Client to the Client Socket : manna Dec 2016

    private void orderClient(Socket Client_Socket, String toClient)
    {
        if (Client_Socket == null)
        {
            return;
        }

        if (Client_Socket.isClosed())
        {
            clientSocketList.remove(Client_Socket);
            Client_Socket = null;
            return;
        }

        try
        {
            OutputStream os = Client_Socket.getOutputStream();
            os.write(toClient.getBytes());

        }
        catch (IOException ex1)
        {
            try
            {

                Client_Socket.close();
                clientSocketList.remove(Client_Socket);
                Client_Socket = null;
            }
            catch (IOException ex2)
            {
                ex2.printStackTrace();
            }

        }
    }

    public void serverDisconnect()
    {
        reEstablishConnection = true;


        peersdisconnect();

        clientSocketList = null;
        clientSocketList = new ArrayList<Socket>();

        try
        {
            if (sockHost != null && !sockHost.isClosed())
            {
                sockHost.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        sockHost = null;
    }

    public void peersdisconnect()
    {

        for (Socket peer: clientSocketList)
        {
            try
            {
                if (peer != null && !peer.isClosed())
                {
                    peer.close();
                    peer = null;
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }


        clientSocketList = new ArrayList<Socket>();
    }

    public void clientCanPlay(String songName, long timing, int pos)
    {
        if (songName == null)
        {
            return;
        }

        String toClient = MUSIC_PLAY + SEPERATOR + songName + SEPERATOR
                + String.valueOf(timing) + SEPERATOR
                + String.valueOf(pos) + SEPERATOR;


        for (Socket Client_Socket : clientSocketList)
        {
            orderClient(Client_Socket, toClient);
        }
    }
    //Send Current playlist to Client : manna Jan 2017
    public void sendClientPlaylist(String songList)
    {
        if (songList == null)
        {
            return;
        }

        String toClient = MUSIC_LIST + SEPERATOR + songList + SEPERATOR;


        for (Socket Client_Socket : clientSocketList)
        {
            orderClient(Client_Socket, toClient);
        }
    }

    public void clientCanStop()
    {
        String toClient = MUSIC_STOP + SEPERATOR;


        for (Socket Client_Socket : clientSocketList)
        {
            orderClient(Client_Socket, toClient);
        }
    }
}
