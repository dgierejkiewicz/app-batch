package pl.dg.batch.Processors;

import pl.dg.batch.POJO.User;

import java.util.List;

public interface Processor {

    List<User> process();

}
