package pl.dg.batch;

import org.apache.commons.cli.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import pl.dg.batch.Batch.BatchHandler;
import pl.dg.batch.Handlers.CsvProcessorHandler;
import pl.dg.batch.Handlers.XmlSaxProcessorHandler;
import pl.dg.batch.Processors.Impl.CsvProcessorImpl;
import pl.dg.batch.Processors.Impl.XmlSaxProcessorImpl;
import pl.dg.batch.Processors.Context;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.TimeZone;

public class App {
    final static Logger logger = Logger.getLogger(pl.dg.batch.App.class);

    public static void main(String[] args) {
        BasicConfigurator.configure();
        Options options = new Options();
        Option input = new Option("p",
                "path",
                true,
                "input file path");
        input.setRequired(true);
        Option type = new Option("m",
                "mime-type",
                true,
                "file mime type [eg. text/plain, text/csv, text/xml ... etc]"
        );
        type.setRequired(true);
        Option delimiter = new Option("d",
                "delimiter",
                true,
                "delimiter for csv file"
        );
        options.addOption(input);
        options.addOption(type);
        options.addOption(delimiter);
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd;
            cmd = parser.parse(options, args);
            String filePathArg = cmd.getOptionValue('p');
            String fileType = cmd.getOptionValue('m');
            String delim = cmd.getOptionValue("d");
            if (null != filePathArg) {
                if (Files.notExists(Paths.get(filePathArg))) {
                    throw new FileNotFoundException("File not found in given path: " + filePathArg);
                }
                Context context = new Context();
                switch (fileType) {
                    case "text/xml":
                    case "application/xml":
                        context.set(new XmlSaxProcessorImpl(
                                filePathArg, new XmlSaxProcessorHandler(new BatchHandler(getConnection()))));
                        break;
                    case "text/csv":
                        context.set(new CsvProcessorImpl(
                                filePathArg, new CsvProcessorHandler(new BatchHandler(getConnection())), delim));
                        break;
                }
                context.handle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Connection getConnection() {
        Connection conn = null;
        try {
            InputStream input = App.class.getClassLoader().getResourceAsStream("config.properties");
            Properties prop = new Properties();
            prop.load(input);
            String user = prop.getProperty("db.user");
            String pass = prop.getProperty("db.password");

            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/appbatch"
                            + "?characterEncoding=utf8&"
                            + "serverTimezone=" + TimeZone.getDefault().getID(),
                    user, pass);
        } catch (SQLException | IOException e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return conn;
    }
}
