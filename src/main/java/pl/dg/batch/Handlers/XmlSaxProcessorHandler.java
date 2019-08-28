package pl.dg.batch.Handlers;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import pl.dg.batch.Batch.BatchHandler;
import pl.dg.batch.POJO.User;

import java.sql.SQLException;
import java.util.*;

public class XmlSaxProcessorHandler extends DefaultHandler {

    private BatchHandler batchHandler;
    private User user = new User();
    private List<User> userList = new ArrayList<>();
    private Map<String, List<String>> contactsMapList = new HashMap<>();

    private boolean bname;
    private boolean bsname;
    private boolean bage;
    private boolean bphone;
    private boolean bemail;
    private boolean bicq;
    private boolean bjabber;

    public XmlSaxProcessorHandler(BatchHandler batchHandler) {
        this.batchHandler = batchHandler;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if ("person".equals(qName)) {
            contactsMapList.put("phone", new ArrayList<>());
            contactsMapList.put("email", new ArrayList<>());
            contactsMapList.put("unknown", new ArrayList<>());
            contactsMapList.put("jabber", new ArrayList<>());
        }

        switch (qName) {
            case "name":
                bname = true;
                break;
            case "surname":
                bsname = true;
                break;
            case "age":
                bage = true;
                break;
            case "phone":
                bphone = true;
                break;
            case "email":
                bemail = true;
                break;
            case "icq":
                bicq = true;
                break;
            case "jabber":
                bjabber = true;
                break;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (bname) {
            user.setName(new String(ch, start, length));
            bname = false;
        }
        if (bsname) {
            user.setSurname(new String(ch, start, length));
            bsname = false;
        }
        if (bage) {
            user.setAge(new String(ch, start, length));
            bage = false;
        }
        if (bphone) {
            contactsMapList.get("phone")
                    .add(new String(ch, start, length));
            bphone = false;
        }
        if (bemail) {
            contactsMapList.get("email")
                    .add(new String(ch, start, length));
            bemail = false;
        }
        if (bicq) {
            contactsMapList.get("unknown")
                    .add(new String(ch, start, length));
            bicq = false;
        }
        if (bjabber) {
            contactsMapList.get("jabber")
                    .add(new String(ch, start, length));
            bjabber = false;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {

        if ("contacts".equals(qName)) {
            user.setContacts(contactsMapList);
            contactsMapList = new HashMap<>();
        }

        // End of data
        if ("persons".equals(qName)) {
            if (userList.size() < BatchHandler.BATCH_LIMIT) {
                try {
                    this.batchHandler.handle(userList);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        // End of bulk
        if ("person".equals(qName)) {

            // reset user
            userList.add(user);
            user = new User();

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
    }

    public List<User> getUserList() {
        return userList;
    }
}
