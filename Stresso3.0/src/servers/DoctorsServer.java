/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servers;

import java.io.*;
import static java.lang.System.exit;
import static java.lang.Thread.sleep;
import java.net.*;
import threads.DoctorServerStopThread;

import threads.DoctorThread;


/**
 *
 * @author Nacho Sánchez
 */
public class DoctorsServer {
    
    
    //Each computer has his own path.
    Socket socketClient = null;
    
    ServerSocket serverSocketClient = null;
    DoctorServerStopThread dsst;
    boolean noClients = false;
    boolean closingServer = false;
    int clients = 0;
    
    public DoctorsServer() 
    {
        
    }
    
    public void startServer() throws InterruptedException
    {
        try
        {
            //Thread creation to stop the server
            dsst = new DoctorServerStopThread(this);
            dsst.start();
            //Socket that conects to the clients
            serverSocketClient = new ServerSocket(8000);
            System.out.println("(DoctorServer)DoctorServer8000 Open");
            
            while (!closingServer)
            {
                try{
                    socketClient = serverSocketClient.accept();
                    clients = clients + 1;
                    //Habría que pasarselo tambíén al DoctorThread y modificar Doctor Thread para que lo admita
                    DoctorThread doctorThread = new DoctorThread (socketClient,this);
                    doctorThread.start();
                }catch (SocketTimeoutException e){
                }
            }
            
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void closeDoctor()
    {
        clients = clients-1;
    }
    public void closeServer() throws IOException, InterruptedException
    {
        
        closingServer = true;
        
        boolean connection = dsst.getStateConnection();
        if(connection)
        {
            dsst.stopConnection();
        }else
        {
            System.out.println("(DoctorServer)No connection with patient Server");
        }
        
        
        noClients = checkDoctorsInServer();
        while (noClients == false)
        {
            noClients = checkDoctorsInServer();
        }
        System.out.println("(DoctorServer)No Clients in the server, Closing..");
        releaseServerResources();
        exit(1);
    }
    private boolean checkDoctorsInServer() throws InterruptedException
    {
        System.out.println("(DoctorServer)Remining patients:"+clients);
        sleep(1000);
        return clients == 0;
    }
    public void releaseServerResources() throws IOException
    {
        socketClient.close();
        serverSocketClient.close();
    }
}


//Doctor User
//Secret Info
//New Patient
//Access Patient
//Modify Patient
//Close connection

//                   build