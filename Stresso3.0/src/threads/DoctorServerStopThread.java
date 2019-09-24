/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threads;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import static java.lang.System.exit;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import servers.DoctorsServer;

/**
 *
 * @author Pablo
 */
public class DoctorServerStopThread extends Thread
{
    
    Socket socketPatientThread;
    OutputStream outputStreamPT;
    PrintWriter printWriterPT = null;
    
    boolean connectionOpen = false;
    
    public DoctorServerStopThread(DoctorsServer doctorsServer) 
    {
        
    }
    //The main purpose of this thread is to manage the closing of the PatientServer
    //sending the request to a specific thread that will exist or not, so we will 
    //send the request each 10 seconds.

    @Override
    public void run()
    {
        try {
            while(true) 
            {
                try
                {
                    socketPatientThread = new Socket("localhost",7000);//7000, special thread
                    break;
                } catch (IOException ex)
                {
                    System.out.println("(DocotrServerStopThread)Waiting Connection");
                }
                try
                {
                    sleep(10000);
                } catch (InterruptedException ex) 
                {
                    Logger.getLogger(DoctorServerStopThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            outputStreamPT = socketPatientThread.getOutputStream();
            printWriterPT = new PrintWriter(outputStreamPT, true);
            connectionOpen = true;
            System.out.println("(DoctorServerStopThread)Connection Between Threads stablished");
        } catch (IOException ex) 
        {
            Logger.getLogger(DoctorServerStopThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void stopConnection() throws IOException
    {
        printWriterPT.println("Close");
        releaseResources();
        exit(1);
    }
    
    public boolean getStateConnection()
    {
        return connectionOpen;
    }
    
    public void releaseResources() throws IOException
    {
        socketPatientThread.close();
        printWriterPT.close();
        outputStreamPT.close();
    }
    

}
