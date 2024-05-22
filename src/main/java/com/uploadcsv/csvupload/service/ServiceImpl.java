package com.uploadcsv.csvupload.service;


import com.uploadcsv.csvupload.entity.User;
import com.uploadcsv.csvupload.repository.UserRepository;
import org.apache.commons.csv.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@org.springframework.stereotype.Service
public class ServiceImpl implements Service{

    private final UserRepository userRepository;

    public ServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean haCsvFormat(MultipartFile file) {
        String type = "text/csv";
        if (!type.equals(file.getContentType()))
            return false;
        return true;
    }
    @Override
    public void processAndSaveData(MultipartFile file) {
        try {
            List<User>users=csvToUsers(file.getInputStream());
            userRepository.saveAll(users);
        } catch (IOException e) {
           e.printStackTrace();
        }

    }

    @Override
    public ByteArrayInputStream load() {
        List<User> users= userRepository.findAll();
        ByteArrayInputStream stream = usersToCsv(users);
        return stream;
    }

    private ByteArrayInputStream usersToCsv(List<User> users) {
        CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);

        try(ByteArrayOutputStream out = new ByteArrayOutputStream();
            CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format);){
                for (User user :users){
                    List<String> data = Arrays.asList(String.valueOf(user.getName()), user.getCode());
                    csvPrinter.printRecord(data);
                }
                csvPrinter.flush();
                return new ByteArrayInputStream(out.toByteArray());

        }catch (IOException e) {

    }
        return null;
    }

    private List<User> csvToUsers(InputStream inputStream) {
        try (BufferedReader fileReader= new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
            CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());){
            List<User> users = new ArrayList<User>();
            List<CSVRecord> records  = csvParser.getRecords();
            for (CSVRecord csvRecord : records){
                User user = new User(csvRecord.get("Name"),csvRecord.get("Code"));  //parse index to id Long.psrseLong(csvRecord.get(index)
                users.add(user);
            }
            return users;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
         //   throw new RuntimeException(e);
             e.printStackTrace();
        }
        return null;
    }
}
