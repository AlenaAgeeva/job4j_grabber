package grabber;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import parsing.Parse;
import store.Store;

public interface Grab {
    void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException;
}
