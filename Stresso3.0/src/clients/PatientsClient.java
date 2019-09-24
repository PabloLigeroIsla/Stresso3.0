/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clients;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import pojos.*;
import BITalino.*;
import java.util.ArrayList;
import static pojos.Validator.*;

/**
 *
 * @author Nacho Sánchez
 */



public class PatientsClient {
static Socket socket;
public static Frame[] frame;
public static ArrayList<Frame[]> bitData;
static BufferedReader bufferedReaderServer;
static PrintWriter printWriterSocket;
static ObjectInputStream objectInputStream;
static ObjectOutputStream objectOutputStream;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        
        
        Patient patient = null;
        String name;
        String fileBitName = null;
           
        //Comprobamos que el Servidor este disponible, y no esté intentando cerrarse, en ese caso no aceptariamos más pacientes
        
       System.out.println("Starting Socket Connection");
       socket = new Socket("localhost",9000);//puerto del paciente:9000
        //Conexiones entre servidores y clientes
        
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();
        
        bufferedReaderServer= new BufferedReader (new InputStreamReader (inputStream));
        printWriterSocket = new PrintWriter(outputStream, true);
        
        objectInputStream = new ObjectInputStream(inputStream);
        objectOutputStream = new ObjectOutputStream(outputStream);
        System.out.println("All Sockets Connections Finished");
        
        name = checkPatientUser();
        
        System.out.println("Patient found");
        
        printWriterSocket.println("Open Patient");
        printWriterSocket.println(name);
        
        patient = (Patient) objectInputStream.readObject();
        System.out.println("\nLogin Correct\n");
        
        waitEnter();

        ArrayList<Session> sessionsList = patient.getSessionsList();

        System.out.println("Please answer the following questions\n\n");
        System.out.println("Have you measured your blood pressure?\n");
        Boolean question1 = writeBoolean();

        int hp=0;
        int lp=0;
        if(question1)
        {
             System.out.println("Introduce your High blood pressure (HP)");
             hp = writeNumber();

             System.out.println("Introduce your Low blood pressure (LP)");
             lp = writeNumber();
        }

        System.out.println("Did you do any sport the day of the measure?");
        Boolean question2 = writeBoolean();
        String question22="";
        if(question2)
        {
            System.out.println("What type of sport");
            question22 = writeString();
        }

        System.out.println("how many hours have you been awake?");
        int question3 = writeNumber();

        System.out.println("Do you have any pain or something?");
        String question4 = writeString();

        System.out.println("Did you sleep at least 7h each day?");
        Boolean question5 = writeBoolean();


        System.out.println("Please, be ready to record the Data:");
        //Leer Bitalino
        waitEnterBitalino();
        fileBitName= readBitalino();//Return name of the file that store the bit info for this session 


        Session session;

        if(question1 && question2)
        {
            session = new Session(fileBitName, question1,hp,lp,question2,question22,question3,question4,question5);

        }else if(question1)
        {
            session = new Session(fileBitName, question1,hp,lp,question2,question3,question4,question5);

        }else if(question2)
        {
            session = new Session(fileBitName, question1,question2,question22,question3,question4,question5);

        }else
        {
            session = new Session(fileBitName, question1,question2,question3,question4,question5);

        }

        sessionsList.add(session);

        patient.setSessionList(sessionsList);

        printWriterSocket.println("Close Patient");
        printWriterSocket.println(name);
        objectOutputStream.writeObject(patient);

        System.out.println("\nThe session has finished\n");
        
        waitEnter();

        //In order to check the number of clients in the Server
        printWriterSocket.println("Client Close");

        releaseClientResources(printWriterSocket,bufferedReaderServer,objectInputStream,objectOutputStream,socket);

    }
    
    private static String checkPatientUser()
    {

        String searchInfo="";
        String patientCheck = "Wrong";
        String patientUser="";
        while(true)
        {

            System.out.println("\nIntroduce the following information\n"
                + "Patient Username:\n");
            patientUser = writeName();
            System.out.println("\n Introduce the pasword:\n");
            String patientPassword = writeString();
            String password = encodePassword(patientPassword);
            
            searchInfo = patientUser+password;
            printWriterSocket.println("Patient User");
            printWriterSocket.println(searchInfo);

            try 
            {
                patientCheck = bufferedReaderServer.readLine();
            } catch (IOException ex) 
            {
                Logger.getLogger(DoctorsClient.class.getName()).log(Level.SEVERE, null, ex);
            }

            if(patientCheck.equals("Correct"))
            {
                break;            
            }else
            {
                System.out.println("\nUser or pasword incorrect, try again\n");
            }
        }

        return patientUser;
    }
    
    private static String readBitalino() throws IOException
    {
        
        bitData = new ArrayList<>();
        BITalino bitalino = null;
        try {
            bitalino = new BITalino();
            // find devices

            //You need TO CHANGE THE MAC ADDRESS
            String macAddress = "20:16:02:14:75:76"; // MAC of the device Pablo
            int SamplingRate = 100; //10, 100 or 1000
            bitalino.open(macAddress, SamplingRate); 

            // start acquisition on analog channels A2 and A6
            //If you want A1, A3 and A4 you should use {0,2,3}
            int[] channelsToAcquire = {1,5}; //From A1 to A6 (is one less)
            bitalino.start(channelsToAcquire);

            //read 10000 samples
            
            for (int j = 0; j < 5; j++) 
            {

                //Read a block of 100 samples 
                frame = bitalino.read(100);
                System.out.println("size block: " + frame.length);

                for (int i = 0; i < frame.length; i++) 
                {
                    System.out.println((j * 100 + i) + " seq: " + frame[i].seq + " "
                      +"HeartRate"+ frame[i].analog[1] + " "
                      +"Sweat"+ frame[i].analog[5] + " "
                     // + frame[i].analog[2] + " "
                     // + frame[i].analog[3] + " "
                     // + frame[i].analog[4] + " "
                     // + frame[i].analog[5]
                    );
                    
                }
                bitData.add(frame);
            }
            //stop acquisition
            bitalino.stop();
            printWriterSocket.println("Store bit");
            objectOutputStream.writeObject(bitData);
            
            //waiting for the path of the file 
            String bitPath = bufferedReaderServer.readLine();
            return bitPath;
        } catch (BITalinoException ex) 
        {
            Logger.getLogger(PatientsClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Throwable ex) 
        {
            Logger.getLogger(PatientsClient.class.getName()).log(Level.SEVERE, null, ex);
        } finally 
        {
            try {
                //close bluetooth connection
                if (bitalino != null) 
                {
                    bitalino.close();
                }
            } catch (BITalinoException ex) 
            {
                Logger.getLogger(PatientsClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "Error";
    }
    
    private static void releaseClientResources(PrintWriter printWriter,BufferedReader bufferedReader,ObjectInputStream objectInputStream,ObjectOutputStream objectOutputStream, Socket socket)
    {
        try 
        {
            try {
                printWriter.close();
            } catch (RuntimeException ex) {
                Logger.getLogger(PatientsClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                bufferedReader.close();
            } catch (IOException ex) {
                Logger.getLogger(PatientsClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                objectInputStream.close();
            } catch (RuntimeException ex) {
                Logger.getLogger(PatientsClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                objectOutputStream.close();
            } catch (RuntimeException ex) {
                Logger.getLogger(PatientsClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(PatientsClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
