# Test 2 
This code will generate a csv file with the amount of records entered by the user, and import user selected csv file into sqlite database, with java and spring

## Basic Usage
Generating a csv file and importing a csv file into sqlite database.

### Generating Records

Generate random unique user records

With two predifined arrays for Names and Surnames and random dates for date of births', user records are generated:
```
public void genRecords(){
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
```
The record gets passed through when the method is called

### Generating CSV file

```
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
```
### Get file input from html form

```
@RequestMapping(value = "/importCSV", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
```
### Create upload file and write imported file data into uploadfile

```
String uploadFilePath = "src/main/resources/upload/" + impFile.getOriginalFilename();
                File uploadFile = new File(uploadFilePath);
                uploadFile.createNewFile();

                //get uploaded file data and write to new upload file
                try(FileOutputStream fOut = new FileOutputStream(uploadFile)) {
                        fOut.write(impFile.getBytes());
                }catch (Exception e){
                        e.printStackTrace();
                }
```
### Create csv_import table

```
 try {
                        ps = connect().prepareStatement("DROP TABLE IF EXISTS csv_import");
                        ps.execute();
                        ps = connect().prepareStatement("CREATE TABLE IF NOT EXISTS csv_import (Id int not null, Name varchar(128), Surname varchar(128), Initials varchar(128), Age int(3), DateOfBirth varchar(13));");
                        ps.execute();

                } catch (SQLException e) {
                        e.printStackTrace();
                }
```
using the connect() method
```
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
```
### Insert csv file data into sqlite 
```
InputStream is = new FileInputStream(uploadFilePath);
                try {
                        new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                                .lines()
                                .skip(1)
                                .forEach(this::handleLine);
                }catch (Exception e){
                        return e.getMessage();
                }
```
handleLine method:
```
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
```
