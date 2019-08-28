package pl.dg.batch.Processors.Impl;

import org.xml.sax.SAXException;
import pl.dg.batch.Handlers.XmlSaxProcessorHandler;
import pl.dg.batch.POJO.User;
import pl.dg.batch.Processors.Processor;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class XmlSaxProcessorImpl implements Processor {

    private String path;
    private XmlSaxProcessorHandler handler;

    public XmlSaxProcessorImpl(String path, XmlSaxProcessorHandler handler) {
        this.path = path;
        this.handler = handler;
    }

    private SAXParser createSaxParser() throws ParserConfigurationException, SAXException {
        return SAXParserFactory
                .newInstance()
                .newSAXParser();
    }

    @Override
    public List<User> process() {
        File xmlDoc = Paths.get(path).toFile();
        try {
            SAXParser parser = createSaxParser();
            parser.parse(xmlDoc, handler);
        } catch (ParserConfigurationException
                | SAXException | IOException e) {
            e.printStackTrace();
        }
        return handler.getUserList();
    }
}
