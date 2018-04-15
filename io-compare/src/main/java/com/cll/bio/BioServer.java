package com.cll.bio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * @author chenliangliang
 * @date 2018/4/15
 */
public class BioServer {

    private final int port;

    public BioServer(int port){
        this.port=port;
    }

    public void start() throws IOException {
        final ServerSocket server=new ServerSocket(port);
        try {
            while (true){
                final Socket client=server.accept();
                System.out.println("Accepted connection from " + client);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OutputStream out=null;
                        try {
                            out=client.getOutputStream();
                            out.write("Hi! \r\n".getBytes(Charset.forName("UTF-8")));
                            out.flush();
                            client.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }finally {
                            if (out!=null){
                                try {
                                    out.close();
                                }catch (IOException ee){ }
                            }


                        }
                    }
                }).start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        new BioServer(9899).start();
    }
}
