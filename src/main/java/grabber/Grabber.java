package grabber;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import parsing.HabrCareerParse;
import parsing.Parse;
import parsing.Post;
import store.PsqlStore;
import store.Store;
import utils.HabrCareerDateTimeParser;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * A major class for implementing scheduled work of
 * grabbing and posting to the web and a database.
 *
 * @author Alena Ageeva
 */
public class Grabber implements Grab {
    private final Properties cfg = new Properties();
    private static final String LINK = "https://career.habr.com/vacancies/java_developer?page=";

    /**
     * A method creates an object of Store interface via polymorphism.
     *
     * @return an Object of PsqlStore with loaded properties
     */
    public Store store() {
        return new PsqlStore(cfg);
    }

    /**
     * A method adjusts configuration for a periodical tasks implementation.
     *
     * @return Scheduler object
     * @throws SchedulerException
     */
    public Scheduler scheduler() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        return scheduler;
    }

    /**
     * A methods loads properties settings details for further work.
     *
     * @throws IOException
     */
    public void cfg() throws IOException {
        try (InputStream in = Grabber.class.getClassLoader().getResourceAsStream("app.properties")) {
            cfg.load(in);
        }
    }

    /**
     * Quartz library in action
     *
     * @param parse     Parse object for parsing
     * @param store     Store object for storing
     * @param scheduler Scheduler as a part of work
     * @throws SchedulerException
     */
    @Override
    public void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException {
        JobDataMap data = new JobDataMap();
        data.put("store", store);
        data.put("parse", parse);
        JobDetail job = newJob(GrabJob.class)
                .usingJobData(data)
                .build();
        SimpleScheduleBuilder times = simpleSchedule()
                .withIntervalInSeconds(Integer.parseInt(cfg.getProperty("time")))
                .repeatForever();
        Trigger trigger = newTrigger()
                .startNow()
                .withSchedule(times)
                .build();
        scheduler.scheduleJob(job, trigger);
    }

    /**
     * A class implementing interface Job with overwritten method execute
     */
    public static class GrabJob implements Job {
        /**
         * Overwritten method with a defined logic of a repetitive work.
         *
         * @param context JobExecutionContext
         * @throws JobExecutionException
         */
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            JobDataMap map = context.getJobDetail().getJobDataMap();
            Store store = (Store) map.get("store");
            Parse parse = (Parse) map.get("parse");
            try {
                parse.list(LINK)
                        .forEach(store::save);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Creates a server socket, bound to the specified port defined in app.properties
     *
     * @param store
     */
    public void web(Store store) {
        new Thread(() -> {
            try (ServerSocket server = new ServerSocket(Integer.parseInt(cfg.getProperty("port")))) {
                while (!server.isClosed()) {
                    Socket socket = server.accept();
                    try (OutputStream out = socket.getOutputStream()) {
                        out.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
                        for (Post post : store.getAll()) {
                            out.write(post.toString().getBytes(Charset.forName("Windows-1251")));
                            out.write(System.lineSeparator().getBytes());
                        }
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) throws Exception {
        Grabber grab = new Grabber();
        grab.cfg();
        Scheduler scheduler = grab.scheduler();
        Store store = grab.store();
        grab.init(new HabrCareerParse(new HabrCareerDateTimeParser()), store, scheduler);
        grab.web(store);
    }
}
