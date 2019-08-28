package pl.dg.batch.Batch;

import pl.dg.batch.Enums.ContactTypes;
import pl.dg.batch.POJO.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * Class for handling batch operation
 * with multi insert
 */
public class BatchHandler {

    public static final int BATCH_LIMIT = 500;

    private Connection connection;
    private static String INSERT_CUSTOMERS_SQL = "INSERT INTO CUSTOMERS(ID, NAME, SURNAME, AGE) VALUES(?,?,?,?)";
    private static String INSERT_CONTACTS_SQL = "INSERT INTO CONTACTS(ID, ID_CUSTOMER, TYPE, CONTACT) VALUES(?,?,?,?)";

    public BatchHandler(Connection connection) {
        this.connection = connection;
        try {
            this.connection
                    .setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void handle(List<User> userList) throws SQLException {
        Map<String, PreparedStatement> batchInsert = createPreparedStatementFromData(userList);
        // for test only
        int[] rowsCustomers = new int[0];
        int[] rowsContacts = new int[0];
        try {
            if (null != batchInsert.get("customers") && null != batchInsert.get("contacts")) {
                rowsCustomers = batchInsert.get("customers").executeBatch();
                rowsContacts = batchInsert.get("contacts").executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.getConnection().commit();
        System.out.println(Arrays.toString(rowsCustomers));
        System.out.println(Arrays.toString(rowsContacts));
    }

    private Map<String, PreparedStatement> createPreparedStatementFromData(List<User> userList) throws SQLException {
        Map<String, PreparedStatement> statementMap = new HashMap<>();
        PreparedStatement insertCustomers = this.getConnection()
                .prepareStatement(INSERT_CUSTOMERS_SQL);
        PreparedStatement insertContacts = this.getConnection()
                .prepareStatement(INSERT_CONTACTS_SQL);
        for (User user : userList) {
            try {
                String userId = String.valueOf(UUID.randomUUID());
                insertCustomers.setString(1, userId);
                insertCustomers.setString(2, user.getName());
                insertCustomers.setString(3, user.getSurname());
                insertCustomers.setString(4, user.getAge());

                Map<String, List<String>> contacts = user.getContacts();
                PreparedStatement stmt = createSingleInsert(contacts, insertContacts, userId);
                statementMap.put("contacts", stmt);

            } catch (Exception e) {
                e.printStackTrace();
            }
            insertCustomers.addBatch();
        }
        statementMap.put("customers", insertCustomers);
        return statementMap;
    }

    public Connection getConnection() {
        return connection;
    }

    public PreparedStatement createSingleInsert(Map<String, List<String>> contacts,
                                                PreparedStatement insertContacts,
                                                String userId) {

        for (ContactTypes type : ContactTypes.values()) {
            String contactType = type.toString();
            if (contacts.get(contactType).size() > 0) {
                List list = contacts.get(contactType);
                list.forEach((v) -> {
                    try {
                        insertContacts.setString(1, String.valueOf(UUID.randomUUID()));
                        insertContacts.setString(2, userId);
                        insertContacts.setString(3, ContactTypes.valueOf(contactType).getIdx());
                        insertContacts.setString(4, v.toString());
                        insertContacts.addBatch();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }
        }

        return insertContacts;
    }
}
