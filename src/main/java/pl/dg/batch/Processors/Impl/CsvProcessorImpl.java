package pl.dg.batch.Processors.Impl;

import pl.dg.batch.Handlers.CsvProcessorHandler;
import pl.dg.batch.POJO.User;
import pl.dg.batch.Processors.Processor;

import java.util.List;

public class CsvProcessorImpl implements Processor {

    private String path;
    private String delimiter;
    private CsvProcessorHandler handler;

    public CsvProcessorImpl(String path,
                            CsvProcessorHandler handler,
                            String delimiter) {
        this.path = path;
        this.handler = handler;
        this.delimiter = delimiter;
    }

    @Override
    public List<User> process() {
        return handler.parseLines(path, delimiter);
    }
}
