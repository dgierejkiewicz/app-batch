package pl.dg.batch.Handlers;

import pl.dg.batch.Batch.BatchHandler;
import pl.dg.batch.POJO.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CsvProcessorHandler {

    private BatchHandler batchHandler;
    private User user = new User();
    private List<User> userList = new ArrayList<>();
    private Map<String, List<String>> contactsMapList = new HashMap<>();

    public CsvProcessorHandler(BatchHandler batchHandler) {
        this.batchHandler = batchHandler;
    }

    public List<User> parseLines(String path, String delimiter) {

        // TODO: not precise, just basic sample of regular expressions
        Pattern emailPattern = Pattern.compile("^(.+)@(.+)$");
        Pattern phoneNumPatternNoSpaces = Pattern.compile("^\\d{9}$");
        Pattern phoneNumPatternWithSpaces = Pattern.compile("^\\d{3} \\d{3} \\d{3}$");
        Pattern jabberPattern = Pattern.compile("^[a-z]{3}$");

        contactsMapList = initContactsMap();

        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {

            while ((line = br.readLine()) != null) {


                String[] person = line.split(delimiter);
                if (person.length > 0) {

                    contactsMapList = initContactsMap();

                    user.setName(person[0]);
                    user.setSurname(person[1]);
                    user.setAge(person[2]);

                    for (int i = 4; i < person.length; i++) {
                        Matcher emailMatcher = emailPattern.matcher(person[i]);
                        Matcher phoneNumNoSpMatcher = phoneNumPatternNoSpaces.matcher(person[i]);
                        Matcher phoneNumWithSpMatcher = phoneNumPatternWithSpaces.matcher(person[i]);
                        Matcher jabberMatcher = jabberPattern.matcher(person[i]);
                        if (emailMatcher.matches()) {
                            contactsMapList.get("email").add(person[i]);
                        } else if (phoneNumNoSpMatcher.matches() || phoneNumWithSpMatcher.matches()) {
                            contactsMapList.get("phone").add(person[i]);
                        } else if (jabberMatcher.matches()) {
                            contactsMapList.get("jabber").add(person[i]);
                        } else {
                            contactsMapList.get("unknown").add(person[i]);
                        }

                    }
                    user.setContacts(contactsMapList);
                    userList.add(user);
                    user = new User();

                }

                // End of bulk
                if (userList.size() == BatchHandler.BATCH_LIMIT) {
                    try {
                        this.batchHandler.handle(userList);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    // reset collection
                    userList = new ArrayList<>();
                }
            }

            // End of data
            if (userList.size() < BatchHandler.BATCH_LIMIT) {
                try {
                    this.batchHandler.handle(userList);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return userList;
    }

    private Map<String, List<String>> initContactsMap() {
        Map<String, List<String>> map = new HashMap<>();
        map.put("phone", new ArrayList<>());
        map.put("email", new ArrayList<>());
        map.put("unknown", new ArrayList<>());
        map.put("jabber", new ArrayList<>());
        return map;
    }
}
