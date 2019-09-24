/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servers;

import java.io.*;



/**
 *
 * @author Nacho Sánchez
 */
public class DoctorsServerInit {
    
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException 
    {

     DoctorsServer doctorsServer = new DoctorsServer();
     doctorsServer.startServer();
     
     
    }
    
    
  
}


//Doctor User
//Secret Info
//New Patient
//Access Patient
//Modify Patient
//Close connection

//                   build