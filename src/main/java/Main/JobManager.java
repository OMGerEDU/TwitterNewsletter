package Main;

import Main.Commands.BotCommands;
import Main.Selenium.*;
import Main.SQL.MySQL;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.utils.FileUpload;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.io.File;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

public class JobManager extends DiscordBotMain  {
    MySQL sql = new MySQL();
    final int Timezone = 3;
    File file;
    FileUpload fileUpload;

    public JobManager(JDA jda) throws SQLException {
        super(jda);
    }
    public enum TextChannels {
        BOT_MESSAGES_CHANNEL("1022457598970175568");
//        SIGN_CREATE_BONUS(1),
//        HOME_SCREEN(2),
//        REGISTER_SCREEN(3);

        private final String value;

        TextChannels(final String newValue) {
            value = newValue;
        }

        public String getValue() { return value; }
    }

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    public void ManageQueries(MessageChannel channel) {
        final Runnable executor = new Runnable() {
            @Override
            public void run() {
                Queue<TaskJob> queries = new LinkedList<>();

                jda.getTextChannelById(TextChannels.BOT_MESSAGES_CHANNEL.value).sendMessage("Queries manager started.").queue();
                //channel.sendMessage("Queries manager started.").queue();

                try {
                    queries = sql.currentTasks();
                } catch (SQLException | InterruptedException throwables) {
                    throwables.printStackTrace();
                    return;
                }
                while (true) {
                     assert queries != null;
                    if (queries.peek() == null) break;
                    TaskJob currentJob = queries.poll();

                    if (currentJob.getModeString().equals(BotCommands.Mode.NORMAL_QUERY.toString()))
                        runAndRemove(channel,currentJob);
                    else if (currentJob.getModeString().equals(BotCommands.Mode.SCHEDULED_TASK.toString())) {
                        try {
                            scheduledJob(channel, currentJob, jda);
                        } catch (Exception e) { System.out.println("scheduledJob broken"); } }
                }
            }
        };
        final ScheduledFuture<?> queriesHandle =
                scheduler.scheduleAtFixedRate(executor,0,2, TimeUnit.MINUTES);
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                queriesHandle.cancel(false);
            }
        },60*60,TimeUnit.HOURS);
    }




    public void runAndRemove(MessageChannel channel, TaskJob currentJob) {

        List<Post> posts = null;
        String fileName = currentJob.getPhrase().replaceAll("[^a-zA-Z0-9]", "").trim();
        try {
            System.out.println("scanning for "+currentJob.getPhrase());
            posts = new Scrapper().searchEvolved(currentJob.getPhrase());
        }
        catch (InterruptedException e) { System.out.println("NormalTask() failed."); }
        assert posts != null;
        if (!posts.isEmpty()) {
                //create excel.
                Excel.CreateSheet(posts, fileName);
                //get ready for attachment.
             fileUpload = new FileHelper().nameToFileUpload(fileName);
        channel.sendMessage("Your task in the subject of "+currentJob.getPhrase()+" fresh baked, out of the oven.").setFiles(fileUpload).queue();
        }
        else
        channel.sendMessage("No results found, I blame you for giving me bad credentials, human.").queue();

        try { new MySQL().deleteQuery(currentJob.getPhrase()); }
        catch (SQLException throwables) {
            System.out.println("This normal task is either Immortal or already dead.");; }

    }


    public void scheduledJob(MessageChannel channel, TaskJob currentJob,JDA jda) throws ExecutionException, InterruptedException, SQLException {
        DiscordApi discordApi = new DiscordApiBuilder().setToken("MTAxOTE4MTM1MTI1MDk1NjM0OQ.GBHEB_.PLdJiSFuOZV32XmKUzH84IRdBuCrbGE3TNxfwo").login().join();
        if (currentJob.getPrivateChannel()==null||currentJob.getPrivateChannel().equals("null")) {
            try { currentJob.setPrivateChannel(sql.searchForValidTextChannel(currentJob.getClientoid())); }
            catch (Exception e) { System.out.println("JobManager searchForValidTextChannel failed."); }
        }

        List<Post> freshPosts = new LinkedList<>();
        List<Post> posts = null;
        String fileName = currentJob.getPhrase().replaceAll("[^a-zA-Z0-9]", "").trim();
        try {
            System.out.println("scanning for "+currentJob.getPhrase());
            posts = new Scrapper().searchEvolved(currentJob.getPhrase());
        }
        catch (InterruptedException e) { System.out.println("NormalTask() failed."); }

        if (posts==null||posts.isEmpty()) {
            channel.sendMessage("No results found, I blame you for giving me bad credentials, human.").queue();
        }

        else  {
            for (Post post:posts) {
                // Real time updates - every query with update timing of below one day (1400 minutes)
                String comparable = post.getTimePosted().substring(post.getTimePosted().indexOf("T") + 1);
                String date = post.getTimePosted().substring(0, post.getTimePosted().indexOf("T"));
                String[] dated = date.split("-");
                comparable = comparable.trim();
                String[] fromTwitter = comparable.split(":");
                fromTwitter[0] = String.valueOf((Integer.parseInt(fromTwitter[0]) + Timezone));
                //
                // String[] lastRunTime = currentJob.lastRun.split("/");


                if(dated[dated.length-1].charAt(0)=='0')
                    dated[dated.length-1] = dated[dated.length-1].substring(1);

                //DateHelper fromTwitterDate = new DateHelper(Integer.parseInt(fromTwitter[0]),Integer.parseInt(fromTwitter[1]));
                //DateHelper lastRunDate = new DateHelper().stringToDateHelper(currentJob.getLastRun());
                //DateHelper lastRunDate = new DateHelper(Integer.parseInt(lastRunTime[lastRunTime.length - 2]),Integer.parseInt(lastRunTime[lastRunTime.length - 1]));

                Date lastRun = new DateHelper().stringToDate(currentJob.getLastRun());
                Date date1 = new Date(Integer.parseInt(dated[0]),Integer.parseInt(dated[1]),Integer.parseInt(dated[2]),Integer.parseInt(fromTwitter[0]),Integer.parseInt(fromTwitter[1]));
                //Date date2 = Calendar.getInstance().getTime();
                System.out.println(date1.after(lastRun));
                if (date1.after(lastRun))
                    freshPosts.add(post);

                //if (fromTwitterDate.getHour() > lastRunDate.getHour() || fromTwitterDate.getHour() == lastRunDate.getHour() && fromTwitterDate.getMin() > lastRunDate.getMin()) {
                }

            Excel.CreateSheet(freshPosts, fileName);
            fileUpload = new FileHelper().nameToFileUpload(fileName);

            //User user = discordApi.getUserById(currentJob.getClientoid()).get();

            if (currentJob.getPrivateChannel()==null||currentJob.getPrivateChannel().equals("null"))
                currentJob.setPrivateChannel(new TaskJob().createTextChannel(jda, currentJob));

            }


            //sql.getUserRoomID(currentJob.getClientoid())
        if (freshPosts.size()>0)
            jda.getTextChannelById(currentJob.getPrivateChannel())
                    .sendMessage("Your scheduled task, human. \n "+currentJob.getPhrase()+" was the phrase I've been asked to look for.")
                    .setFiles(fileUpload).queue();
        else
            jda.getTextChannelById(currentJob.getPrivateChannel())
                    .sendMessage("No posts were posted since the last check, try to Increase frequency or Ignore me, what do I know? I am just a bot.")
                    .queue();

            try { sql.setLastRun(currentJob); }
            catch (SQLException throwables) { throwables.printStackTrace();
                System.out.println(throwables.getErrorCode()); }

    }

    public void deepSearchTask() {

    }


}













