package de.tu_darmstadt.informatik.newapp.Client.Clubber_Core;

import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import de.tu_darmstadt.informatik.newapp.Server.Host_Core.ServerGroupOwnerSocketHandle;

/**
 * Created by manna on 25-01-2017.
 */

public class ClubberSocketHandle extends Thread {
    public static final int message_recieve_event = 100;

    private InetAddress mAddress;





    private static final int Buff = 256;
    private Handler handler;


    public static final int CL_CB = 101;
    private static final int CONN_TIMEOUT = 0;

    private Socket clubber_Socks;

    public ClubberSocketHandle(Handler handler, InetAddress groupOwnerAddress) {
        this.handler = handler;
        this.mAddress = groupOwnerAddress;
    }

    @Override
    public void run() {

        handler.obtainMessage(CL_CB, this).sendToTarget();
        connect();
        while (clubber_Socks != null) {
            try {
                InputStream input_stream = clubber_Socks.getInputStream();
                OutputStream output_stream = clubber_Socks.getOutputStream();

                byte[] buffer = new byte[Buff];
                int bytes;

                bytes = input_stream.read(buffer);
                if (bytes == -1) {
                    continue;
                }

                String recieve_Message = new String(buffer);

                String[] command_Str = recieve_Message.split(ServerGroupOwnerSocketHandle.SEPERATOR);

                if (command_Str[0].equals(ServerGroupOwnerSocketHandle.MUSIC_BAL) && command_Str.length > 1) {
                    output_stream.write(recieve_Message.getBytes());
                }

                handler.obtainMessage(message_recieve_event, buffer).sendToTarget();
            } catch (SocketException e) {
                leave();
            } catch (IOException e) {

                leave();
            } catch (NumberFormatException e) {

                leave();
            }
        }
    }

    public void connect() {
        if (clubber_Socks == null || clubber_Socks.isClosed()) {
            clubber_Socks = new Socket();
        }

        try {
            clubber_Socks.bind(null);

            clubber_Socks.connect(new InetSocketAddress(mAddress.getHostAddress(),
                    ServerGroupOwnerSocketHandle.PORT), CONN_TIMEOUT);


            clubber_Socks.setSoTimeout(CONN_TIMEOUT);
        } catch (IOException e) {
            leave();
        }
    }

    public void leave() {
        if (clubber_Socks == null) {
            return;
        }
        try {
            clubber_Socks.close();
            clubber_Socks = null;
        } catch (IOException e) {

            clubber_Socks = null;
        }
    }
}