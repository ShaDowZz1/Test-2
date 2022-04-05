package com.example.Test;

import com.opencsv.CSVWriter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


@RestController
public class TestController {
        @RequestMapping(value = "/importCSV", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public String goImport(@RequestParam("myFile") MultipartFile impFile) throws IOException{
                String sReturn = "Empty";

                //create new upload file
                String uploadFilePath = "src/main/resources/upload/" + impFile.getOriginalFilename();
                File uploadFile = new File(uploadFilePath);
                uploadFile.createNewFile();

                //get uploaded file data and write to new upload file
                try(FileOutputStream fOut = new FileOutputStream(uploadFile)) {
                        fOut.write(impFile.getBytes());
                }catch (Exception e){
                        e.printStackTrace();
                }

                //create table
                PreparedStatement ps = null;

                try {
                        ps = connect().prepareStatement("DROP TABLE IF EXISTS csv_import");
                        ps.execute();
                        ps = connect().prepareStatement("CREATE TABLE IF NOT EXISTS csv_import (Id int not null, Name varchar(128), Surname varchar(128), Initials varchar(128), Age int(3), DateOfBirth varchar(13));");
                        ps.execute();

                } catch (SQLException e) {
                        e.printStackTrace();
                }
                //read lines and insert into db with handleLine method
                InputStream is = new FileInputStream(uploadFilePath);
                try {
                        new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                                .lines()
                                .skip(1)
                                .forEach(this::handleLine);
                }catch (Exception e){
                        return e.getMessage();
                }

                  int count=0;
                //get amount of records entered into db
                try{
                        ResultSet rs;
                        ps = connect().prepareStatement("SELECT COUNT(*) AS total FROM csv_import");
                        rs = ps.executeQuery();
                        count = rs.getInt("total");
                        sReturn = count + " records has been entered into the database";
                }catch (SQLException s){
                        s.printStackTrace();
                }

                return sReturn;

        }

        @GetMapping("/TestController")
        public RedirectView getAmount(@RequestParam(value="amtGen", required = true, defaultValue = "0") String genAmt) {
                boolean bValid = false;
                String s = "";
                int ID = 1;
                int fAmount = 0;

                try {
                        fAmount = Integer.parseInt(genAmt);
                        bValid = true;

                } catch (NumberFormatException nfe) {
                        nfe.printStackTrace();
                        bValid = false;
                }
                RedirectView redirectView = new RedirectView();
                if (fAmount == 0){
                        bValid = false;
                }
                if (bValid == true) {
                        Generate nen = new Generate();
                        //create output file to write generated records to

                        File csvFile = new File("src/main/resources/output/output.csv");
                        String[] Headers = {"Id","Name","Surname","Initials","Age","DateOfBirth"};
                        String[] records = new String[fAmount];
                        String[] arrOut = new String[fAmount];
                        int count=0;
                        List<String[]> fOutput = new ArrayList<String[]>();
                        fOutput.add(Headers);
                        try {

                                //if file exists, delete and create new output file
                                if (!csvFile.createNewFile()) {

                                        csvFile.delete();
                                        csvFile.createNewFile();

                                }

                                        for (int x=0;x<=fAmount-1;x++)
                                        {
                                                nen.genRecords();
                                                s = nen.getRecords();
                                                s = Integer.toString(ID) + "." + s;
                                                records = s.split(Pattern.quote("."));

                                                //Add record to array list
                                                if (fOutput.contains(records)){
                                                        while (fOutput.contains(records)){
                                                                s = nen.getRecords();
                                                                s = Integer.toString(ID) + "." + s;
                                                                records = s.split(Pattern.quote("."));
                                                        }
                                                }else{
                                                        fOutput.add(records);
                                                }

                                                ID++;
                                        }
                                        //Write to CSV
                                        FileOutputStream fos = new FileOutputStream(csvFile);
                                        OutputStreamWriter osw = new OutputStreamWriter(fos);
                                        CSVWriter csvWriter = new CSVWriter(osw);
                                        csvWriter.writeAll(fOutput);
                                        csvWriter.close();
                                        osw.close();
                                        fos.close();


                        }catch (IOException e){
                                e.printStackTrace();
                        }

                        redirectView.setUrl("ExportCSV.html");
                } else {
                        redirectView.setUrl("IndexError.html");
                }

                return redirectView;

        }
        //connect method to connect to sqlite db
        public static Connection connect()
        {
                Connection con = null;
                try {
                        Class.forName("org.sqlite.JDBC");
                        String username = "";
                        String url = "jdbc:sqlite:src/main/resources/sqlite/mydatabase";
                        String password = "";
                        con = DriverManager.getConnection(url, username, password);
                }catch (Exception e){
                        e.printStackTrace();
                }
                return con;
        }

       public void handleLine(String s) {
               String[] arrInput = new String[6];
               arrInput = s.split(Pattern.quote(","));
               String sQry = "INSERT INTO csv_import(Id,Name,Surname,Initials,Age,DateOfBirth)Values(?,?,?,?,?,?)";
               int inID = 0;
               String sName = "";
               String sSurname = "";
               String sInitials = "";
               int Age = 0;
               String sDateOfBirth = "";

                try {
                        PreparedStatement ps = connect().prepareStatement(sQry);
                        inID = Integer.parseInt(arrInput[0].substring(1, arrInput[0].length()-1));
                        sName = arrInput[1].substring(1, arrInput[1].length()-1);
                        sSurname = arrInput[2].substring(1, arrInput[2].length()-1);
                        sInitials = arrInput[3].substring(1, arrInput[3].length()-1);
                        Age = Integer.parseInt(arrInput[4].substring(1, arrInput[4].length()-1));
                        sDateOfBirth = arrInput[5].substring(1, arrInput[5].length()-1);
                        ps.setInt(1, inID);
                        ps.setString(2, sName);
                        ps.setString(3, sSurname);
                        ps.setString(4, sInitials);
                        ps.setInt(5, Age);
                        ps.setString(6, sDateOfBirth);
                        ps.executeUpdate();

                }catch (SQLException eSQL){
                        eSQL.printStackTrace();
                }

       }

}
