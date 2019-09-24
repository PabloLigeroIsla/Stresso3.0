/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threads;

import BITalino.Frame;
import java.io.*;
import static java.lang.System.exit;
import java.net.Socket;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import pojos.*;
import servers.DoctorsServer;

/**
 *
 * @author Nacho Sánchez
 */
public class DoctorThread extends Thread {

    private final Socket socket;
    private final String serverPassword = "2175d53352b5a633681ace4c715d8adb";//astato encoded

    static String pathPatientFile = "files/patientInfo.txt"; //Path de cada Ordenador, contiene los usiarios 
    static String pathP4t13ntFile = "files/p4t13ntInfo.txt"; //Path de cada Ordenador, contiene usiarios+ contraseñas codificadas
    static String pathDoctorFile = "files/doctorInfo.txt"; //Path de cada Ordenador, contiene usuarios y contraseñas de doctores  
    static String pathPatientDirectory = "files/patients";
    static String pathBitalinoDirectory = "files/bitFiles";


    private DoctorsServer doctorServer;

    public DoctorThread(Socket socket) {
        this.socket = socket;

    }

    public DoctorThread(Socket socket, DoctorsServer doctorServer) {
        this.socket = socket;
        this.doctorServer = doctorServer;
    }

    @Override
    public void run() {

        InputStream inputStream;
        OutputStream outputStream;
        String name;
        Patient pat;
        File file;

        try {
            
            System.out.println("Starting Socket");
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            BufferedReader bufferedReaderSocket = new BufferedReader(new InputStreamReader(inputStream));
            PrintWriter printWriterSocket = new PrintWriter(outputStream, true);

            ObjectOutputStream objectOutputStreamSocket = new ObjectOutputStream(outputStream);
            ObjectInputStream objectInputStreamSocket = new ObjectInputStream(inputStream);
            System.out.println("Object Connection Established");

            System.out.println("All Connection Established");
            loop:
            while (true) 
            {
                System.out.println("(DoctorThread)Waiting Instructions");
                String optionString = bufferedReaderSocket.readLine();

                switch (optionString) 
                {
                    case "Doctor User":
                        //Reciving the information about the doctor Username and pasword
                        String doctorInfo = bufferedReaderSocket.readLine();
                        //Open the file that contains the doctors users and paswords
                        file = new File(pathDoctorFile);
                        //Search the doctor with the global bufferedReaderFile of the opened file
                        boolean found = searchDoctor(file, doctorInfo);

                        if (found) {
                            printWriterSocket.println("Correct");
                        } else {
                            printWriterSocket.println("Error");
                        }
                        break;

                    case "Add Doctor":
                        //We recive the name and password of the doctor
                        String theNewDoctor = bufferedReaderSocket.readLine();

                        addClient2File(theNewDoctor, pathDoctorFile);

                    case "Patient Names":

                        //Load in the Array the users and passwords
                        ArrayList arrayList = getPatientNames();
                        Iterator iterator = arrayList.iterator();
                        //Send the array
                        for (int i = 1; i <= arrayList.size(); i++) {
                            printWriterSocket.println(iterator.next());
                        }
                        printWriterSocket.println("STOP");//in order to finish the while of the Doctor
                        break;

                    case "New Patient":
                        String userName = bufferedReaderSocket.readLine();
                        String namePassword = bufferedReaderSocket.readLine();
                        pat = (Patient) objectInputStreamSocket.readObject();

                        //We create the file
                        file = new File(pathPatientDirectory, userName);
                        file.createNewFile();
                        //We store the object in the file

                        storePatient(pat, file);

                        //We add the name to the file to the file
                        addClient2File(userName, pathPatientFile);
                        addClient2File(namePassword, pathP4t13ntFile);
                        break;

                    case "Access Patient":
                        name = bufferedReaderSocket.readLine();

                        //We search the patient and we take his information
                        pat = searchPatient(name);
                        objectOutputStreamSocket.writeObject(pat);
                        break;

                    case "Modify Patient":
                        //Search the patient we want to modify
                        name = bufferedReaderSocket.readLine();
                        pat = searchPatient(name);

                        //Send the found patient
                        objectOutputStreamSocket.writeObject(pat);

                        //We store the patient modificated
                        file = new File(pathPatientDirectory, name);
                        pat = (Patient) objectInputStreamSocket.readObject();
                        storePatient(pat, file);
                        break;

                    case "Doctor Close":
                        doctorServer.closeDoctor();
                        releaseResources(bufferedReaderSocket, printWriterSocket, objectOutputStreamSocket, objectInputStreamSocket, socket);
                        break loop;

                    case "Check ServerPassword":
                        String st = bufferedReaderSocket.readLine();
                        if (st.contains(serverPassword)) 
                        {
                            printWriterSocket.println("OK");
                        } else 
                        {
                            printWriterSocket.println("NO");
                        }
                        break;
                    case "Load Bitalino":
                        ArrayList<Frame[]> arrayListFrames = null;
                        String fileName = bufferedReaderSocket.readLine();
                        file = new File(pathBitalinoDirectory, fileName);
                        if (file.exists()) 
                        {
                            arrayListFrames = loadBitalino(file);
                        }

                        objectOutputStreamSocket.writeObject(arrayListFrames);

                        break;
                    case "Close Server":
                        doctorServer.closeDoctor();
                        doctorServer.closeServer();
                        releaseResources(bufferedReaderSocket, printWriterSocket, objectOutputStreamSocket, objectInputStreamSocket, socket);
                        exit(0);
                    default:
                        exit(0);
                }

            }
        } catch (IOException | ClassNotFoundException | InterruptedException ex) {
            Logger.getLogger(DoctorThread.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static ArrayList<Frame[]> loadBitalino(File file) throws FileNotFoundException, IOException, ClassNotFoundException {

        ArrayList<Frame[]> ar;
        
        FileInputStream fileInputStream = new FileInputStream(file);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        ar = (ArrayList<Frame[]>) objectInputStream.readObject();

        fileInputStream.close();
        objectInputStream.close();
        
        return ar;
    }

    private static Patient searchPatient(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException {
        //This method returns the patient if the file where is stored exist
        Patient pat = null;

        File file = new File(pathPatientDirectory, fileName);

        if (file.exists()) {

            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            pat = (Patient) objectInputStream.readObject();

            fileInputStream.close();
            objectInputStream.close();
        }

        return pat;
    }

    private static boolean searchDoctor(File file, String doctorInfo) throws FileNotFoundException, IOException {
        boolean found = false;
        String cadena;
        FileReader fileReader = new FileReader(file);
        BufferedReader bufReader = new BufferedReader(fileReader);
        while ((cadena = bufReader.readLine()) != null) {
            if (cadena.contains(doctorInfo)) {
                found = true;
                break;
            }
        }
        bufReader.close();
        fileReader.close();

        return found;
    }

    public static ArrayList getPatientNames() {
        //Load the names of the patients
        ArrayList array = new ArrayList();
        String patient;
        try {
            File file = new File(pathPatientFile);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufReader = new BufferedReader(fileReader);
            while ((patient = bufReader.readLine()) != null) {
                array.add(patient);
            }
            bufReader.close();
            fileReader.close();

        } catch (IOException ex) {
            Logger.getLogger(DoctorsServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return array;
    }

    public static void storePatient(Patient pat, File file) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        ObjectOutputStream objectOutputStreamFile = new ObjectOutputStream(fileOutputStream);
        objectOutputStreamFile.writeObject(pat);

        fileOutputStream.close();
        objectOutputStreamFile.close();
    }

    public static void addClient2File(String name, String filePath) throws FileNotFoundException, IOException {

        FileWriter fstream = new FileWriter(filePath, true);
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(name);
        out.newLine();
        out.close();
        fstream.close();

    }

    private static void releaseResources(BufferedReader bufferedReaderDoctor, PrintWriter printWriterDoctor, ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream, Socket socket) 
    {

        try {
            bufferedReaderDoctor.close();
        } catch (IOException ex) {
            Logger.getLogger(DoctorsServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        printWriterDoctor.close();
        try {
            objectOutputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(DoctorsServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            objectInputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(DoctorsServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(DoctorsServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
