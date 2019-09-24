/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stresso;
import java.io.File;
import static java.lang.Thread.sleep;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import static pojos.Validator.encodePassword;




/**
 *
 * @author Pablo
 */
public class Stresso {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        String a = "Antonio";
        String b = encodePassword("astato");
        
        String fin = b;
        
        java.util.Date date = new Date();
        LocalDateTime primer=LocalDateTime.now();
        
        System.out.println(primer.getDayOfMonth()+" "+primer.getMonth()+" "+primer.getYear());
        System.out.println(primer.getSecond());
        try {    
            sleep(3000);
                    } catch (InterruptedException ex) {
            Logger.getLogger(Stresso.class.getName()).log(Level.SEVERE, null, ex);
        }
                System.out.println(primer.getSecond());
        LocalDateTime second = LocalDateTime.now();
                System.out.println(second.getSecond());

        
        
        System.out.println("Day:");
        System.out.println(date.getDay());
        System.out.println("Month:");
        System.out.println(date.getMonth());
        System.out.println("Year");
        System.out.println(date.getYear());
        System.out.println("Date:");
        System.out.println(date.getDate());

    }
    

    
   
}

//Doctorthread exit(0)??

//Jueves BioInfo y BDS
//Viernes Instrumentacion