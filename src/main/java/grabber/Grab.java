package grabber;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import parsing.Parse;
import store.Store;

/**
 * Supporting interface for a major class of the project
 */
public interface Grab {
    void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException;
}
