package ro.pub.cs.systems.eim.practicaltest02;


import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
public class ClientThread extends Thread{
    private String address;
    private int port;
    private Socket socket;

    private String value;
    private String key;
    private String operation;

    private TextView textView;

    public ClientThread(String address, int port, String value, String key, String operation, TextView textView) {
        this.address = address;
        this.port = port;
        this.value = value;
        this.key = key;
        this.operation = operation;
        this.textView = textView;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            if (socket == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
                return;
            }
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            // trimitere data
            printWriter.println(value);
            printWriter.flush();
            printWriter.println(key);
            printWriter.flush();
            printWriter.println(operation);
            printWriter.flush();

            // primire date
            String infoReceived;
            while ((infoReceived = bufferedReader.readLine()) != null) {
                Log.i(Constants.TAG, "[CLIENT THREAD]: infoReceived=" +infoReceived);
                final String finalInfo = infoReceived;
                textView.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(Constants.TAG, "aici");
                        textView.setText(finalInfo);
                    }
                });
            }

        }  catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }
}