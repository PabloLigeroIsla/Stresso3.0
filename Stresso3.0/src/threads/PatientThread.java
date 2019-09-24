/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threads;

import BITalino.Frame;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import pojos.Patient;
import servers.PatientsServer;

/**
 *
 * @author Nacho Sánchez
 */
public class PatientThread extends Thread {
    
    static String pathPatientDirectory = "files/patients"; //Path de cada Ordenador, contiene usuarios y contraseñas
    static String pathbitFilesDirectory = "files/bitFiles";
    static String pathP4t13ntFile = "files/P4t13ntInfo.txt";

    public static ArrayList<Frame[]> bitData;
    public static Frame[] frame;
    String name;
    
    private final Socket socket;
    private final PatientsServer patientServer;
    
    public PatientThread(Socket socket, PatientsServer patientServer) 
    {
        this.socket = socket;
        this.patientServer = patientServer;
    }

    
    
    @Override
    public void run(){

            InputStream inputStream = null;
            OutputStream outputStream = null;
        try {

            //Conexiones entre servidores y clientes
            System.out.println("Starting Socket");
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            
            BufferedReader bufferedReaderSocket = new BufferedReader (new InputStreamReader (inputStream));
            PrintWriter printWriterSocket = new PrintWriter(outputStream, true);

            ObjectOutputStream objectOutputStreamSocket = new ObjectOutputStream(outputStream);
            ObjectInputStream objectInputStreamSocket = new ObjectInputStream (inputStream);
            
            System.out.println("Socket Connection Established");
            //We read the option we want to perform. Wait for the client selection
            
            loop: while(true)
            {
                
                System.out.println("(PatientThread)Waiting Instructions");//@quitar
                String option = bufferedReaderSocket.readLine();
                switch(option)
                {
                    case "Patient User":
                        String patientInfo = bufferedReaderSocket.readLine();//User and Password
                        //Search the doctor with the global bufferedReaderFile of the opened file
                        boolean found = searchPatient(patientInfo);

                        if(found)
                        {
                            printWriterSocket.println("Correct");
                        }else
                        {
                            printWriterSocket.println("Error");
                        }
                        break;

                    case "Open Patient":
                        Patient patient = new Patient();
                        name = bufferedReaderSocket.readLine();
                        //Intentamos abrir su fichero
                        try 
                        {
                            File file = new File(pathPatientDirectory,name);
                            boolean patientFound = file.exists();
                            if(patientFound)
                            {
                                patient = openPatient(file);
                            }

                            //Y le mandamos el paciente
                            objectOutputStreamSocket.writeObject(patient);
                            
                            
                             
                        } catch (ClassNotFoundException ex)
                        {
                            Logger.getLogger(PatientsServer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;
                        
                    case "Close Patient" :
                        name = bufferedReaderSocket.readLine();
                        try 
                        {
                            File file = new File(pathPatientDirectory,name);
                            if(file.exists()){
                                //Recibimos el paciente de vuelta con la nueva sesión
                                patient= (Patient) objectInputStreamSocket.readObject();
                                //Y lo guardamos con los nuevos valores
                                savePatient(patient,file);
                            }
                        } catch (ClassNotFoundException ex) 
                        {
                            Logger.getLogger(PatientsServer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;
                        
                    case "Store bit" :
                        //create the file that contains bitalino information
                        bitData = (ArrayList<Frame[]>)objectInputStreamSocket.readObject();
                        String fileName = generateFileName();
                        File file = new File(pathbitFilesDirectory,fileName);
                        file.createNewFile();
                        storeBitalino(bitData,file);
                        
                        //We send the name of the file back 
                        printWriterSocket.println(fileName);

                        break;
                    case "Client Close":
                        patientServer.closePatient();
                        releaseResources(bufferedReaderSocket,printWriterSocket,objectOutputStreamSocket,objectInputStreamSocket,socket);
                        break loop;
                }
                
            }
        } catch (IOException ex) {
            Logger.getLogger(PatientThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PatientThread.class.getName()).log(Level.SEVERE, null, ex);
        } 
           
    
    
    }

        
    private static boolean searchPatient(String patientInfo) throws FileNotFoundException, IOException
    {
        boolean found = false;
        ArrayList list = new ArrayList();
        String string;
        
        File file = new File(pathP4t13ntFile);
        BufferedReader bufferedReader;
        try (FileReader fileReader = new FileReader(file)) 
        {
            bufferedReader = new BufferedReader (fileReader);
            while((string = bufferedReader.readLine())!=null)
            {
                list.add(string);
            }
        }
        bufferedReader.close();
        
        //Look for the patient name in the list
        Iterator it = list.iterator();
        int length=list.size();
        String stringRead;
        for(int i = 1;i<=length;i++)
        {
           stringRead =(String)it.next();
           if(stringRead.contains(patientInfo))
            {
                found = true;
            }
        }
       
        return found;
    }
    
    private static Patient openPatient(File file) throws FileNotFoundException, IOException, ClassNotFoundException
    {
        Patient patient; 
        
        FileInputStream fileInputStream = new FileInputStream(file);
        ObjectInputStream objectInputStreamFile = new ObjectInputStream (fileInputStream); 
        patient = (Patient) objectInputStreamFile.readObject();
        
        fileInputStream.close();
        objectInputStreamFile.close();
        
        return patient;
    }
    
    private static void savePatient(Patient patient, File file) throws FileNotFoundException, IOException
    {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        ObjectOutputStream objectOutputStreamFile = new ObjectOutputStream (fileOutputStream);
        objectOutputStreamFile.writeObject(patient); 
        
        fileOutputStream.close();
        objectOutputStreamFile.close();
    }
    
    private static void storeBitalino(ArrayList<Frame[]> datos,File file) throws IOException
    {
        
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        ObjectOutputStream objectOutputStreamFile = new ObjectOutputStream(fileOutputStream);
        objectOutputStreamFile.writeObject(datos);
        
        fileOutputStream.close();
        objectOutputStreamFile.close();
    }
    
            
    private static String generateFileName()
    {
        //This method generate a random name/number and validate it doesn't exist
        String fileName = "";
        boolean valid=false;
        File file;
        while(!valid)
        {
            fileName = generateRandomString();
            file = new File(pathbitFilesDirectory,fileName);
            if(!file.exists())
            {
                valid = true;
            }
        }
        return fileName;
    }
    
    private static String generateRandomString()
    {
        String returnString = "";
        Random rand = new Random();

        int numAlt = rand.nextInt(10000000);
        returnString = Integer.toString(numAlt);
        
        return returnString;
    }
    
    private static void releaseResources(BufferedReader bufferedReaderDoctor,PrintWriter printWriterDoctor,ObjectOutputStream objectOutputStream,ObjectInputStream objectInputStream, Socket socket) 
   {
      //Este release Resources es el del server
        try 
        {
            bufferedReaderDoctor.close();
        } catch (IOException ex) {
            Logger.getLogger(PatientsServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        printWriterDoctor.close();
        try 
        {
            objectOutputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(PatientsServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        try 
        {
            objectInputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(PatientsServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(PatientsServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}