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
import threads.PatientServerStopThread;
import threads.PatientThread;




/**
 *
 * @author Nacho Sánchez
 */
public class PatientsServer 
{
    
    Socket socket = null;
    ServerSocket serverSocket = null;
    PatientServerStopThread dsst;
    
    boolean noClients = false;
    boolean closingServer = false;
    int clients = 0;
    
    public PatientsServer()
    {
                
    }
    
    public void startServer() 
    {
        
        try{
            
            //Socket and Thread creation to stop the server
            dsst = new PatientServerStopThread(this);
            dsst.start();
            //Connection to clients
            System.out.println("PatientServer9000 Open");
            serverSocket = new ServerSocket(9000);

            while (!closingServer)
            {
                try{
                    
                    socket = serverSocket.accept();
                    clients = clients +1 ;
                    
                    PatientThread patientThread= new PatientThread(socket,this);
                    patientThread.start();
                }catch (SocketTimeoutException e)
                {
                    
                }
            }
        }catch (IOException e)
        {
            e.printStackTrace();
        }    
    }
    
    public void closePatient()
    {
        clients = clients - 1;
    }
    
    public void closeServer() throws IOException, InterruptedException
    {
        closingServer = true;
        noClients = checkPatientsInServer();
        while (noClients == false)
        {
            noClients = checkPatientsInServer();
        }
        System.out.println("No clients in the Server, closing...");
        releaseServerResources();
        exit(1);
    }
    private boolean checkPatientsInServer() throws InterruptedException
    {
        System.out.println("Remining patients:"+clients);
        sleep(1000);
        return clients == 0;
    }
    public void releaseServerResources() throws IOException
    {
        if(socket != null)
        {
            socket.close();
        }
        
        serverSocket.close();
    }
}
