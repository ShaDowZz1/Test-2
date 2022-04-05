package com.example.Test;

import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;


@Component
public class Generate {
    //initializing variables and arrays to generate users
    String[] name = {"James", "John", "Jack", "Daniel", "Dan", "Damon", "Ben", "Brendan", "Bryan", "Tristan", "Trent", "Teagan", "Ryan", "Ronald", "Russell", "Peter", "Patricia", "Patrick", "Mary", "Margeret"};   //initializing name array
    String[] surname = {"Jensen", "Jackson", "Jenner", "Daniels", "Douglas", "Diesel", "Black", "Bieber", "Benson", "Turner", "Thompson", "Tucker", "Reagan", "Riley", "Smith", "Smuts", "Stevenson", "Langley", "Hall", "Sims"};    //initializing surname array
    int maxName = 20;
    int UID = 0;
    String uInit;
    String uAge;
    String uDOB;
    String result;
    int total = 0;

    //generate random records
    public void genRecords(){
        Random random = new Random();
        String resName = "";
        String resSName = "";
        String resINIT = "";
        String resAge = "";
        String resDOB = "";
        char csvDelimiter = '.';
        String CombDate = "";

        CombDate = genDOB();
        if (CombDate.length() == 12){
            resAge = CombDate.substring(0, CombDate.indexOf(","));
            resDOB = CombDate.substring(CombDate.indexOf(",")+1,12);
        }else{
            resAge = CombDate.substring(0, CombDate.indexOf(","));
            resDOB = CombDate.substring(CombDate.indexOf(",")+1,13);

        }


        resName = name[random.nextInt(maxName)];
        resSName = surname[random.nextInt(maxName)];
        String fn = resName.substring(0, 1);
        String sn = resSName.substring(0, 1);
        resINIT = fn + sn;

        result = resName + csvDelimiter + resSName + csvDelimiter + resINIT + csvDelimiter + resAge + csvDelimiter + resDOB;

    }

    //generate random Date
    public String genDOB(){

        int sDay = 12;
        int ri = 5;
        boolean yes = false;
        int bday = 0;
        String fDOB = "";
        String Age;

        Random rndmY = new Random();
        Random rndmM = new Random();
        Random rndmD = new Random();

        LocalDate Today = LocalDate.now();
        LocalDate rDOB = LocalDate.now().minusYears(rndmY.nextInt(100));
        rDOB = rDOB.minusMonths(rndmM.nextInt(12));
        rDOB = rDOB.minusDays(rndmD.nextInt(31));


        ri = Today.getDayOfYear(); //doy today
        sDay = rDOB.getDayOfYear(); //doy Dob
        bday = Today.compareTo(rDOB);
        fDOB = DateFormatter(rDOB);

        Age = Integer.toString(bday);
        return Age + "," + fDOB;



    }

    public String getRecords(){
        this.result = result;
        return this.result;
    }

    public String DateFormatter(LocalDate date){
        String s = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        return s;
    }
}
