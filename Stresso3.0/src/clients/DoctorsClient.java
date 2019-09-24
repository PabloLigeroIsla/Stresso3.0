/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clients;


import BITalino.Frame;
import java.io.*;
import static java.lang.System.exit;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.*;
import pojos.*;
import static pojos.Validator.*;



/**
 *
 * @author Nacho Sánchez
 */
public class DoctorsClient {

    static Socket socket;
    static BufferedReader bufferedReaderServer;
    static ObjectInputStream objectInputStream;
    static PrintWriter printWriterSocket;
    static ObjectOutputStream objectOutputStream;
    
    static String patientID;
    
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        

        String name;
        Patient patient = new Patient();
        
        
        System.out.println("Starting Socket Connection");
        socket = new Socket("localhost",8000);//puerto del doctor:8000
        
        //Conexiones entre servidores y clientes
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();
        
        bufferedReaderServer = new BufferedReader (new InputStreamReader (inputStream));
        printWriterSocket = new PrintWriter(outputStream, true);

        objectInputStream = new ObjectInputStream(inputStream);
        objectOutputStream = new ObjectOutputStream(outputStream);

        System.out.println("All Socket Connections Finished\n");
        
        //USUARIO Y CONTRASEÑA
        checkDoctorUser();
        
        loop: while(true)
        {
            System.out.println("\n\n    || DOCTOR MENU ||");
            System.out.println("\n\nSelect an option for the patient:");
            System.out.println("1 - Create a new patient");
            System.out.println("2 - Access a patient's session");
            System.out.println("3 - Modify a patient's basic data (Age, History Number or Address)");
            System.out.println("4.- Add another Doctor");
            System.out.println("5 - Exit Doctor");
            System.out.println("6.- Close Servers (Doctor and Patient)\n");

            int option = writeNumber(1,6);

            switch (option){

                case 1: 
                    //Case Create a NewPatient
                    System.out.println("*****FOR THE PATIENT*****");
                    System.out.println("Select a username:");
                    String username = writeName();
                    System.out.println("Select a password:");
                    String passwordUser = writeString();
                    String password = encodePassword(passwordUser);
                    
                    //First we check if we have a patient with the same UserName,
                    //In case we find it, we dont allow to create this patient

                    if(!searchPatient(username))
                    {
                        //If there is no patient with this name
                        patient = createPatient(username,password);
                        //We set te switch option
                        printWriterSocket.println("New Patient");
                        //We create the file
                        printWriterSocket.println(username);
                        printWriterSocket.println(username+password);
                        //We store the patient
                        objectOutputStream.writeObject(patient);
                        
                        //we store the patient name int he file 
                        
                        System.out.println("\nPatient stored\n");
                        
                        
                    }else
                    {
                        System.out.println("\nChoose another Username\n");
                    }
                    
                    waitEnter();
                    break;

                case 2:

                    boolean patientExist;

                    System.out.println("Patient's name:");
                    name = writeName();
                    patientExist = searchPatient(name);

                    if(!patientExist)
                    {
                        System.out.println("The patient doesn´t exist");
                        waitEnter();
                        break;
                    }

                    printWriterSocket.println("Access Patient");
                    printWriterSocket.println(name);

                    patient = (Patient) objectInputStream.readObject();
                    ArrayList<Session> sessionsList = patient.getSessionsList();
                    if(!sessionsList.isEmpty())
                    {
                        for (int i = 0; i < sessionsList.size(); i++) 
                        {
                             System.out.println("Session "+i+" : ");
                             LocalDateTime date = sessionsList.get(i).getDate();
                             System.out.println(date.getDayOfMonth()+" "+date.getMonth()+" "+date.getYear());
                             System.out.println("");
                        }
                        System.out.print("Introduce the session number you want to see");
                        int sessionNumber = writeNumber(0,sessionsList.size()-1);

                        Session session = sessionsList.get(sessionNumber);

                        showSession(session);

                    }else
                    {
                        System.out.println("There are not register for this patient. Please retry later.");
                    }

                    
                    waitEnter();
                    break;


                case 3:
                    //Case we want to modify patient info
                    System.out.println("Patient's name:");
                    name = writeName();
                 
                    if(!searchPatient(name))
                    {
                        System.out.println("\nPatient doesnt exist\n");
                        waitEnter();
                        break;
                    }
                    //
                    printWriterSocket.println("Modify Patient");
                    printWriterSocket.println(name);

                    patient = (Patient) objectInputStream.readObject();

                       
                    loop2:while(true)
                          {
                                System.out.println("\n\n  || MODIFICATION MENU ||  \n\n");
                                System.out.println("Select an option: ");
                                System.out.println("1 - Change age");
                                System.out.println("2 - Change history number");
                                System.out.println("3 - Change address");
                                System.out.println("4 - Finish modifications and submit");

                                int selection = writeNumber(1,4);

                                switch (selection){
                                         case 1: 
                                            System.out.println("Patient's Age:");
                                            patient.setAge(writeNumber());
                                            break;

                                        case 2:
                                            System.out.println("Patient's history number:");
                                            patient.setHistoryNumber(writeHistoryNumber());
                                            break;

                                        case 3:
                                            System.out.println("Patient's address:");
                                            patient.setAddress(writeString());
                                            break;
                                        case 4:
                                            //We save the pacient. Server must remember the patient file info
                                            objectOutputStream.writeObject(patient);
                                            break loop2;
                                }
                        }

                    System.out.println("\nModification of Patient Finished, Patient Stored\n");
                    waitEnter();
                    break;
                case 4:
                    //Add another doctor to the magic file
                    System.out.println("\nIntroduce the Name of the New Doctor:");
                    String newDoctorName = writeName();
                    System.out.println("Introduce the new Password:");
                    String newDoctorPassword = writeString();
                    
                    String newDoctorFinalPassword = encodePassword(newDoctorPassword);
                    
                    printWriterSocket.println("Add Doctor");
                    printWriterSocket.println(newDoctorName+newDoctorFinalPassword);
                    System.out.println("\nDoctor Stored in the Hospital Records\n");
                    
                    waitEnter();
                    break;
                case 5:
                    //Case we close the doctor
                    printWriterSocket.println("Doctor Close");
                    releaseClientResources(printWriterSocket,bufferedReaderServer,objectOutputStream, objectInputStream,socket);
                    System.out.println("\nDocotr Closed\n");
                    break loop;
                    
                case 6:
                    System.out.println("Introduce the password to close the server(astato)");
                    String closePassword = writeString();
                    String finalPassword = encodePassword(closePassword);
                    //Comprobamos que sea la contraseña
                    printWriterSocket.println("Check ServerPassword");
                    printWriterSocket.println(finalPassword);
                    
                    String comp = bufferedReaderServer.readLine();
                    
                    boolean close = comp.contains("OK");
                    
                    //close the servers
                    if(!close)
                    {
                        System.out.println("Password Incorrect");
                        waitEnter();
                        break;
                    }
                    
                    printWriterSocket.println("Close Server");
                    releaseClientResources(printWriterSocket,bufferedReaderServer,objectOutputStream, objectInputStream,socket);
                    exit(1);
                    
            }
            
        }
        
    }
    
    private static boolean searchPatient(String name)
    {
        //This method returns the user and the password of the patient we search
        boolean result = false;
        
        ArrayList patientArray = loadPatientNames();//Nombres de los pacientes
        if(patientArray!=null)
        {
            Iterator arrayIt = patientArray.iterator();
            for(int i = 1;i<=patientArray.size();i++)
            {
                String patient = (String)arrayIt.next();
                if(patient.contains(name))
                {
                    result=true;//User and password
                    break;
                }
               
            }
        }
        
        return result;
    }
    
    private static void checkDoctorUser()
    {
        //This method conects with the server and checks in the doctorFile if the user and password is 
        //correct, until you introduce a correct user and password, you will be in this method.
        String doctorCheck = "Wrong";
        while(true)
        {
            
            System.out.println("Introduce the following information\n"
                + "Doctor Username:\n");
            String doctorUser = writeName();
            System.out.println("\n Introduce the pasword:\n");
            String doctorPasword = writeString();
            //EncodePasword return the MD5 password
            String searchInfo = doctorUser+encodePassword(doctorPasword);
        
            //Select the switch of the server 
            printWriterSocket.println("Doctor User");
            //Info to the process of search (user+password)
            printWriterSocket.println(searchInfo);
        
            try 
            {
                //We wait the response of the server if they find or not the doctor in the file 
                doctorCheck = bufferedReaderServer.readLine();
            } catch (IOException ex) 
            {
                Logger.getLogger(DoctorsClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if(doctorCheck.equals("Correct"))
            {
                //Just if we recive "Correct" we leave the while(true)
                break;            
            }else
            {
                System.out.println("\nUser or pasword incorrect, try again\n");
            }
        }
        
    }
    
    
    private static ArrayList loadPatientNames()
    {
        //This method contacts with the server and load the file information (objects) of the file P4t13
        ArrayList arrayList = new ArrayList();
        //Send the switch request to the server
        printWriterSocket.println("Patient Names");
        //We add in the list the main objects that we recive 
        try 
        {   
            String patientName;
            while(!(patientName = (String) bufferedReaderServer.readLine()).contains("STOP"))
            {
                arrayList.add(patientName);
            }
            
        }catch (IOException ex) 
        {
            Logger.getLogger(DoctorsClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return arrayList;
    }
    
    private static Patient createPatient(String username, String password) throws IOException
    {
        System.out.println("Patient's Name(no spaces):");
        String name = writeName();

        System.out.println("Patient's Age:");
        int age = writeNumber();

        System.out.println("Patient's history number:");
        String historyNumber = writeHistoryNumber();

        System.out.println("Patient's address:");
        String address = writeString();

        String MD5Password = encodePassword(password);
        patientID = username+MD5Password;
        Patient patient = new Patient(name,age,historyNumber,address);
        
        
        return patient;
                
    }
    
    private static String writeHistoryNumber()
    {
        String finalHistoryNumber="",unvalidatedHistoryNumber;
        System.out.println("Introduce the HistoryNumber of the patient:(OnlyNumbers)\n");
        unvalidatedHistoryNumber = writeString();
        
        
        while(true)
        {
            if(valHistoryNumber(unvalidatedHistoryNumber))//Return true if is ok
            {
                finalHistoryNumber = unvalidatedHistoryNumber;
                break;
            }else
            {
                System.out.println("Introduce only numbers:\n");
                unvalidatedHistoryNumber = writeString();
            }
        }
        
        return finalHistoryNumber;
        
    }
    
    private static void showSession(Session session) throws IOException, ClassNotFoundException
    {
        
        boolean question1,question2;
        question1 = session.getQuestion1();
        question2 = session.getQuestion2();
        
        
        if(question1 && question2)
            {
                //The patient print everything
                System.out.printf("Did the patient Measured his blood pressure: %s\n",question1);
                System.out.printf("High Pressure:%d\n",session.getQuestion1HP());
                System.out.printf("Low Pressure: %d\n",session.getQuestion1LP());
                System.out.printf("Did the Patient make any type of sport the day of the measure:%b\n",session.getQuestion2());
                System.out.printf("Type of sport: %s\n",session.getQuestion22());
                System.out.printf("The pateint was awake the day of the session %d hours.\n",session.getQuestion3());
                System.out.printf("has the patient any type of pain the day of the session recording?\n%s\n",session.getQuestion4());
                System.out.printf("Did the patient sleep more than 7hours?\n %s\n",session.getQuestion5());
                showBitalinoInfo(session.getFileNameBit());
                

            }else if(question1)
            {
                System.out.printf("Did the patient Measured his blood pressure: %s\n",question1);
                System.out.printf("High Pressure:%d\n",session.getQuestion1HP());
                System.out.printf("Low Pressure: %d\n",session.getQuestion1LP());
                System.out.printf("Did the Patient make any type of sport the day of the measure:%b\n",session.getQuestion2());
                System.out.printf("The pateint was awake the day of the session %d hours.\n",session.getQuestion3());
                System.out.printf("has the patient any type of pain the day of the session recording?\n%s\n",session.getQuestion4());
                System.out.printf("Did the patient sleep more than 7hours?\n %s\n",session.getQuestion5());
                showBitalinoInfo(session.getFileNameBit());

            }else if(question2)
            {
                System.out.printf("Did the patient Measured his blood pressure: %s\n",question1);
                System.out.printf("Did the Patient make any type of sport the day of the measure:%b\n",session.getQuestion2());
                System.out.printf("Type of sport: %s\n",session.getQuestion22());
                System.out.printf("The pateint was awake the day of the session %d hours.\n",session.getQuestion3());
                System.out.printf("has the patient any type of pain the day of the session recording?\n%s\n",session.getQuestion4());
                System.out.printf("Did the patient sleep more than 7hours?\n %s\n",session.getQuestion5());
                showBitalinoInfo(session.getFileNameBit());

            }else
            {
                System.out.printf("Did the patient Measured his blood pressure: %s\n",question1);
                System.out.printf("Did the Patient make any type of sport the day of the measure:%b\n",session.getQuestion2());
                System.out.printf("The pateint was awake the day of the session %d hours.\n",session.getQuestion3());
                System.out.printf("has the patient any type of pain the day of the session recording?\n%s\n",session.getQuestion4());
                System.out.printf("Did the patient sleep more than 7hours?\n %s\n",session.getQuestion5());
                showBitalinoInfo(session.getFileNameBit());

            }
    }
    
    
    public static void showBitalinoInfo(String bitFilePath) throws IOException, ClassNotFoundException
    {
        ArrayList<Frame[]> frameArrayList;
        //We send to the server the request
        printWriterSocket.println("Load Bitalino");
        printWriterSocket.println(bitFilePath);
        
        //We wait for the ArrayList with the info
        frameArrayList = (ArrayList<Frame[]>)objectInputStream.readObject();
        Iterator it = frameArrayList.iterator();
        
        int j=0;
        while(it.hasNext())
        {
            Frame[] frame = (Frame[]) it.next();
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
            j=j+1;
        }
        
    }
     
    /*iMPORTANTE*/
    //REVISAR ESTO QUE ES UN COPY-PASTE
    private static void releaseClientResources(PrintWriter printWriter,BufferedReader bufferedReader,ObjectOutputStream objectOutputStream,ObjectInputStream objectInputStream, Socket socket) {
        try {
            try {
                bufferedReader.close();
            } catch (IOException ex) {
                Logger.getLogger(DoctorsClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                printWriter.close();
            } catch (RuntimeException ex) {
                Logger.getLogger(DoctorsClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                objectOutputStream.close();
            } catch (RuntimeException ex) {
                Logger.getLogger(DoctorsClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                objectInputStream.close();
            } catch (RuntimeException ex) {
                Logger.getLogger(DoctorsClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(DoctorsClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
