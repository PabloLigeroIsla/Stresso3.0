/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pojos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Pablo
 */
public class Validator 
{
    static BufferedReader c = new BufferedReader(new InputStreamReader(System.in));
    
    public static String writeString()
    {
        String string = "";
        
        try {
            string = c.readLine();
        } catch (IOException ex) 
        {
            ex.printStackTrace();
        }
        
        return string;
    }
    
    public static int writeNumber() 
    {
		//P Methods used to write an integer value without conditions (Just to be an Integer)
        boolean out = false;
        int answer = -1;
        String stringNumber = "";
        try
        {
        	 do 
        	 {
                     System.out.println("Introduce The Number: \n");
                     stringNumber = c.readLine();
                     if (valNumString(stringNumber)) 
                     {
                         answer = Integer.parseInt(stringNumber);
                         out = true;
                     }
                
        	 } while (!out);//Mientras que no me introduzca un numero, no le dejo salir
        }catch(IOException ex)
        {
        	 ex.printStackTrace();
        }

        return answer;
    }
    
    public static int writeNumber(int lowerLim, int upperLim)
    {
        //P Method used to set one limit, the upper limit
	int numIntro = -1;
	boolean out = false;
	String stringNumber = "";
	try
	{
		while((numIntro > upperLim) || (numIntro < lowerLim))
		{
			 do 
                	 {
                            System.out.printf("Introduce the Number between %d and %d\n",lowerLim,upperLim);
	                    stringNumber = writeString();
	                    if (valNumString(stringNumber)) 
	                    {
                                numIntro = Integer.parseInt(stringNumber);
	                        out = true;
	                    }
			                
                   	 } while (!out);//Mientras que no me introduzca un numero, no le dejo salir
						
			if((numIntro > upperLim) || (numIntro < 0))//si hay 5 opciones no puedes poner 6
			{	
				System.out.println("Out of established limits ["+upperLim+","+lowerLim+"]\n");
			}
		}
	}catch(Exception e)
	{
		e.printStackTrace();
		System.out.println("Error Introducing the values");
	}
	return numIntro;
    }
    
    public static boolean writeBoolean()
    {
        boolean a = false;
        boolean change = false;
        String option;
        
        System.out.println("Introduce YES or NO\n");
        option = writeString();
        
        while(!change)
        {
                if(option.compareTo("Y") == 0 || option.compareTo("y") == 0)
                {
                        a = true;
                        change = true;
                }

                if(option.compareTo("N") == 0 || option.compareTo("n") == 0)		
                {

                        a = false;
                        change = true;
                }

                if(!change)
                {
                        System.out.println("\n Introduce YES or NO\n");
                        option = writeString();
                }

        }

        return a;
    }
    
    
    
    public static String writeName() 
    {
    String finalName,nom = null;
    try {
        char comparar;
        boolean validar;
        nom = c.readLine();
        do {
            validar = true;
            for (int i = 0; i < nom.length(); i++) {
                comparar = nom.charAt(i);
                if (!Character.isAlphabetic(comparar)) {/*devuelve true si encuentra un numero*/
                    validar = false;
                    System.out.println("Introduce only leters, no Numbers\n");
                    nom = c.readLine();
                    break;
                }
            }
        } while (validar == false);
        finalName = nom;
    } catch (IOException ex) {
        ex.printStackTrace();
    }
    finalName = nom;
    return finalName;
    }
       
    public static boolean valHistoryNumber(String hN)
    {
        //Return true if everything is ok
        boolean validate = true;
        char comparar;
        
        for (int i = 0; i < hN.length(); i++)
        {
            comparar = hN.charAt(i);
            if (Character.isAlphabetic(comparar))
            {/*devuelve true si encuentra un numero*/
                validate = false;
                break;
            }
        }
        
        return validate;
       
    }
    public static boolean valNumString(String val) 
    {
        try 
        {
            Integer.parseInt(val);
            return true;
        } catch (NumberFormatException nfe) 
        {
            return false;
        }

    }
    
    public static String encodePassword(String passwordToEncode)
    {
        //This method is use to encode the pasword of the Client or Doctor.
        String passwordToHash = passwordToEncode;
        String generatedPassword = null;
        try 
        {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            md.update(passwordToHash.getBytes());
            //Get the hash's bytes
            byte[] bytes = md.digest();
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

        return generatedPassword;
    }
    
    public static void waitEnter()
    {	
        System.out.println("Press enter to continue...:\n \n");
        try{
            String a = c.readLine();
            a = a+"a";
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
    public static void waitEnterBitalino()
    {	
        System.out.println("Press enter to continue and Record the Data of the Bitalino:\n \n");
        try{
            String a = c.readLine();
            a = a+"a";
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
}
