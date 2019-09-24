/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import static java.lang.System.exit;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import servers.PatientsServer;

/**
 *
 * @author Pablo
 */
public class PatientServerStopThread extends Thread
{
    
    //The main purpose of this thread is to send a request of connection to the 
    //DoctorServerStopThread in order to stop the patient server when the Thread
    //send an specific instruction.
    
    ServerSocket threadServerSocket;
    Socket threadSocket;
    BufferedReader bufRead;
    InputStream inputStreamThread;
    
    PatientsServer patientServer;
    
    
    public PatientServerStopThread(PatientsServer patientServer) 
    {
        this.patientServer = patientServer;
    }
    
    
    @Override
    public void run()
    {
        try 
        {
            threadServerSocket = new ServerSocket(7000);
            System.out.println("(PatinetServerStopThread)Waiting to conect with the Thread of Server Clossing...");
            threadSocket = threadServerSocket.accept();
            System.out.println("(PatientServerStopThread)Threads connection stablish");
            inputStreamThread = threadSocket.getInputStream();
            bufRead = new BufferedReader (new InputStreamReader (inputStreamThread));
            System.out.println("(PatientServerStopThread)Waiting instruction...");
            String sure = bufRead.readLine();
            
            if(sure.contains("Close"))
            {
                patientServer.closeServer();
                releaseResources();
                exit(1);
            }
        } catch (IOException ex) {
            Logger.getLogger(PatientServerStopThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(PatientServerStopThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void releaseResources() throws IOException
    {
        threadServerSocket.close();
        threadSocket.close();
        bufRead.close();
        inputStreamThread.close();
        
    }
    
    
}
