/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pojos;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Pablo
 */
public class Patient implements Serializable {
    String name;
    int age;
    String historyNumber;
    String address;
    ArrayList <Session> sessionsList = new ArrayList(); 
    
    public Patient (){
    this.name =  null;
    this.age = 0;
    this.historyNumber=null;
    this.address = null;
    this.sessionsList = new ArrayList();
    }
    
    public Patient (String name, int age, String historyNumber, String address){
    this.name =  name;
    this.age = age;
    this.historyNumber = historyNumber;
    this.address = address;
    this.sessionsList = new ArrayList();
    }
    
    public String getName(){
        return this.name;
    }
    public int getAge(){
        return this.age;
    }
    public String getHistoryNumber(){
        return this.historyNumber;
    }
    public String getAddress(){
        return this.address;
    }
    public ArrayList getSessionsList(){
        return this.sessionsList;
    }
    
    public void setName(String name){
        this.name = name;
    }
    public void setAge(int age){
        this.age = age;
    }
    public void setHistoryNumber(String historyNumber){
        this.historyNumber = historyNumber;
    }
    public void setAddress(String address){
        this.address = address;
    }
    public void setSessionList(ArrayList sessionsList){
        this.sessionsList = sessionsList;
    }   
    
}
    

