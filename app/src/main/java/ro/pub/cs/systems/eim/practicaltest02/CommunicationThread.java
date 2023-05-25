package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

public class CommunicationThread extends Thread {
    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client hour, minute, operation type!");
            String result = "none\n";

            // Get data
            String value = bufferedReader.readLine();
            String key  = bufferedReader.readLine();
            String op = bufferedReader.readLine();

            HashMap<String, DataModel> data = serverThread.getData();
            DataModel dataModel = null;

            Log.i(Constants.TAG, "[COMMUNICATION THREAD] data received: " + value + ":" + key + " op="+op);


            switch (op) {
                case Constants.PUT:
                    Log.i(Constants.TAG, "Put operation");
                    if (!data.containsKey(key)) {
                        result = "Put in hash " + value + ":" + key;
                        Log.i(Constants.TAG, "Put in hash " + value + ":" + key);
                        data.put(key, new DataModel(value, key));
                    } else {
                        result = "Key already exists:" + key;
                        Log.i(Constants.TAG, "Key already exists:" + key);
                    }

                    Log.i(Constants.TAG, "Set size = " + data.size());
                    break;
                case Constants.GET:
                    Log.i(Constants.TAG, "Get operation");
                    if (data.containsKey(key)) {
                        result = "Value for key=" + key  + " :" + data.get(key).toString();
                        Log.i(Constants.TAG, "Value for key=" + key  + " :" + data.get(key).toString());
                    } else {
                        result = "none";
                        Log.i(Constants.TAG, "none");

                    }
                    break;
                default:
                    break;
            }
            serverThread.setData(data);
            Log.i(Constants.TAG, "Set size = " + serverThread.getData().size());


            /* no data found */
            if (data == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Data is null!");
                return;
            }

            printWriter.println(result);
            printWriter.flush();
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }
}