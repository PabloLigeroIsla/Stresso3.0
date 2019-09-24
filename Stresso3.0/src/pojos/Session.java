/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pojos;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 *
 * @author Pablo
 */
public class Session implements Serializable
{
    LocalDateTime date;
    String fileNameBit;
    //Ask if the patient measures his blood preasure today
    boolean question1;
    //Ask for the values of the Blood preaseure
    int question1HP;
    int question1LP;
    //Ask if they do some sport the day of the measure
    boolean question2;
    String question22;
    //
    int question3;
    //General question
    String question4;
    //Sleep question
    boolean question5;
    
    //More constructors.....@terminar
    
    public Session(String fp, boolean q1,int q1hp, int q1lp, boolean q2, String q2s, int q3, String q4, boolean q5)
    {
        this.date = LocalDateTime.now();
        this.fileNameBit = fp;
        this.question1 = q1;
        this.question1HP = q1hp;
        this.question1LP = q1lp;
        this.question2 = q2;
        this.question22 = q2s;
        this.question3 = q3;
        this.question4 = q4;
        this.question5 = q5;
    }
    
    public Session(String fp, boolean q1, boolean q2, String q2s, int q3, String q4, boolean q5)
    {
        this.date = LocalDateTime.now();
        this.fileNameBit = fp;
        this.question1 = q1;
        this.question1HP = 0;
        this.question1LP = 0;
        this.question2 = q2;
        this.question22 = q2s;
        this.question3 = q3;
        this.question4 = q4;
        this.question5 = q5;
    }
    public Session(String fp, boolean q1,int q1hp, int q1lp, boolean q2, int q3, String q4, boolean q5)
    {
        this.date = LocalDateTime.now();
        this.fileNameBit = fp;
        this.question1 = q1;
        this.question1HP = q1hp;
        this.question1LP = q1lp;
        this.question2 = q2;
        this.question22 = null;
        this.question3 = q3;
        this.question4 = q4;
        this.question5 = q5;
    }
    public Session(String fp, boolean q1, boolean q2, int q3, String q4, boolean q5)
    {
        this.date = LocalDateTime.now();
        this.fileNameBit = fp;
        this.question1 = q1;
        this.question1HP = 0;
        this.question1LP = 0;
        this.question2 = q2;
        this.question22 = null;
        this.question3 = q3;
        this.question4 = q4;
        this.question5 = q5;
    }
    
    public Session()
    {
        this.date = LocalDateTime.now();
        this.fileNameBit = "";
        this.question1 = false;
        this.question1HP = 0;
        this.question1LP = 0;
        this.question2 = false;
        this.question22 = "";
        this.question3 = 0;
        this.question4 = "";
        this.question5 = false;
    }
    public void setDate(LocalDateTime date)
    {
        this.date = date;
    }
    public void setFileNameBit(String fp)
    {
        this.fileNameBit = fp;
    }
    public void setQuestion1(boolean q1)
    {
        this.question1 = q1;
    }
    public void setQuestion1HP(int hp)
    {
        this.question1HP = hp;
    }
    public void setQuestion1LP(int lp)
    {
        this.question1LP = lp;
    }
    public void setQuestion2(boolean q2)
    {
        this.question2 = q2;
    }
    public void setQuestion22(String q22)
    {
        this.question22 = q22;
    }
    public void setQuestion3(int q3)
    {
        this.question3 = q3;
    }
    public void setQuestion4(String st)
    {
        this.question4 = st;
    }
    public void setQuestion5(boolean q5)
    {
        this.question5 = q5;
    }
    
    public LocalDateTime getDate ()
    {
        return this.date;
    }
    
    public String getFileNameBit()
    {
        return this.fileNameBit;
    }
    public boolean getQuestion1()
    {
        return this.question1;
    }
    public int getQuestion1HP()
    {
        return this.question1HP;
    }
    public int getQuestion1LP()
    {
        return this.question1LP;
    }
    public boolean getQuestion2()
    {
        return this.question2;
    }
    public String getQuestion22()
    {
        return this.question22;
    }
    public int getQuestion3()
    {
        return this.question3;
    }
    public String getQuestion4()
    {
        return this.question4;
    }
    public boolean getQuestion5()
    {
        return this.question5;
    }
    
}