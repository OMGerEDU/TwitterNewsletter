package Main.Selenium;

import Main.Commands.BotCommands;
import Main.SQL.MySQL;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.sql.SQLException;
import java.util.EnumSet;

public class TaskJob  {
    MySQL sql = new MySQL();
    DateHelper dateHelper = new DateHelper();
    String clientoid;
    String phrase;
    String lastRun;
    String expireDate;
    String modeString,privateChannel;

    Filter filter;
    int frequency,duration,id;
    BotCommands.Mode mode;
    SlashCommandInteractionEvent event;


    public void organizeTask(SlashCommandInteractionEvent event, Boolean isMember, BotCommands.Mode mode) throws SQLException {
        event.deferReply().queue();
        if (event.getOption("search_phrase")==null)
            return;
        String phrase = event.getOption("search_phrase").getAsString();

        System.out.println(phrase);
        Filter filter = new Filter();
        if (event.getOption("filter_links") != null && event.getOption("filter_links").toString().contains("true"))
            filter.setNoLinksBool(true);
        if (event.getOption("exact_quote") != null && event.getOption("exact_quote").toString().contains("true"))
            filter.setExactPhrase(true);
        if (event.getOption("min_replies") != null)
            filter.setMinReplies(event.getOption("min_replies").getAsInt());
        if (event.getOption("min_likes") != null)
            filter.setMinFaves(event.getOption("min_likes").getAsInt());
        if (event.getOption("min_retweets") != null)
            filter.setMinRetweets(event.getOption("min_retweets").getAsInt());

        System.out.println(event.getOption("exact_quote"));
        phrase = filter.ConditionsToString(phrase, filter);
        event.getHook().sendMessage("The phrase after conditioned: " + phrase).queue();
        if (mode.equals(BotCommands.Mode.NORMAL_QUERY))
            insertNormalQuery(event, phrase);
        else if (mode.equals(BotCommands.Mode.SCHEDULED_TASK) && isMember) {
            insertScheduledTask(event, filter, phrase);
        }
    }

    public void insertNormalQuery(SlashCommandInteractionEvent event, String phrase) {

        Member member = event.getMember();
        try {
            if (!event.isAcknowledged())
                event.deferReply().queue();
        } catch (Exception e) {
        }
        event.getHook().sendMessage(member.getAsMention() + " A task on the phrase of " + phrase + " should be added soon.").mention().queue();
        // Add to mysql.
        try {
            new MySQL().insertQuery(new TaskJob(phrase,BotCommands.Mode.NORMAL_QUERY));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void insertScheduledTask(SlashCommandInteractionEvent event, Filter filter, String phrase) throws SQLException {
        int frequency = 0, duration = 0;
        if (phrase == null)
            return;

            frequency = event.getOption("frequency").getAsInt();
            duration = event.getOption("duration").getAsInt();
        if (frequency==0||duration==0)
            return;

        String expireDate = dateHelper.dateHelperToSqlFormat(dateHelper.setExpireDate(duration));

        String lastRun = dateHelper.dateHelperToSqlFormat(new DateHelper().nowToDateHelper());
        TaskJob taskJob = new TaskJob(phrase,lastRun,expireDate,frequency,duration, BotCommands.Mode.SCHEDULED_TASK,event);
        sql.insertQuery(taskJob);

        System.out.println("phrase:"+phrase +"\n expire date:"+expireDate+"\n last run: "+lastRun+"\n memberId: "+event.getMember().getId());
    }


    public  void createTextChannel(Member member, String name) throws SQLException {

        Guild guild = member.getGuild();

        guild.createTextChannel(name)
                .addPermissionOverride(member, EnumSet.of(Permission.VIEW_CHANNEL), null)
                .addPermissionOverride(guild.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                .complete(); // this actually sends the request to discord.
        TextChannel textChannel = guild.getTextChannelsByName(name,true).get(0);
        sql.setTextRoomID(guild.getTextChannelsByName(name,true).get(0).getId(),member.getId());

    }



    public  String createTextChannel(JDA jda,TaskJob taskJob) throws SQLException {
        System.out.println("jda>>"+jda);
        String name = taskJob.getClientoid();
        Member memberTry = jda.getGuilds().get(0).getMemberById(name);
        Guild guild = jda.getGuildChannelById("1019186529802932319").getGuild();
        //Member member = guild.getMemberById(name);
        Member member = guild.retrieveMemberById(name).complete();

        guild.createTextChannel(name)
                .addPermissionOverride(member, EnumSet.of(Permission.VIEW_CHANNEL), null)
                .addPermissionOverride(guild.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                .complete(); // this actually sends the request to discord.
        String newChannelID = guild.getTextChannelsByName(name,true).get(0).getId();

        sql.setTextRoomID(newChannelID,member.getId());
        return newChannelID;
    }




    public TaskJob() throws SQLException {
    }

    public TaskJob(int id, String clientoid, String phrase, String lastRun, String expireDate, String modeString, int frequency, String privateChannel) throws SQLException {
        this.privateChannel = privateChannel;
        this.id = id;
        this.clientoid = clientoid;
        this.phrase = phrase;
        this.lastRun = lastRun;
        this.expireDate = expireDate;
        this.modeString = modeString;
        this.frequency = frequency;
    }

    public TaskJob(int id, String clientoid, String phrase, String lastRun, String expireDate, String modeString, int frequency) throws SQLException {
        this.id = id;
        this.clientoid = clientoid;
        this.phrase = phrase;
        this.lastRun = lastRun;
        this.expireDate = expireDate;
        this.modeString = modeString;
        this.frequency = frequency;
    }

    public TaskJob(String clientoid, String phrase, String lastRun, String expireDate, String modeString, int frequency) throws SQLException {
        this.clientoid = clientoid;
        this.phrase = phrase;
        this.lastRun = lastRun;
        this.expireDate = expireDate;
        this.modeString = modeString;
        this.frequency = frequency;
    }

    public TaskJob(String clientoid, String phrase, String lastRun, String expireDate, String modeString) throws SQLException {
        this.clientoid = clientoid;
        this.phrase = phrase;
        this.lastRun = lastRun;
        this.expireDate = expireDate;
        this.modeString = modeString;
    }

    public TaskJob(String phrase, String lastRun, String expireDate, int frequency, int duration, BotCommands.Mode mode, SlashCommandInteractionEvent event) throws SQLException {
        this.phrase = phrase;
        this.lastRun = lastRun;
        this.expireDate = expireDate;
        this.frequency = frequency;
        this.duration = duration;
        this.mode = mode;
        this.event = event;
    }

    public TaskJob(String phrase, String lastRun, String expireDate, int frequency, int duration, BotCommands.Mode mode) throws SQLException {
        this.phrase = phrase;
        this.lastRun = lastRun;
        this.expireDate = expireDate;
        this.frequency = frequency;
        this.duration = duration;
        this.mode = mode;
    }

    public TaskJob(String phrase, Filter filter, int frequency, int duration) throws SQLException {
        this.phrase = phrase;
        this.filter = filter;
        this.frequency = frequency;
        this.duration = duration;
    }

    public TaskJob(String phrase, BotCommands.Mode mode) throws SQLException {
        this.phrase = phrase;
        this.mode = mode;
    }

    public String getClientoid() {
        return clientoid;
    }

    public void setClientoid(String clientoid) {
        this.clientoid = clientoid;
    }

    public String getModeString() {
        return modeString;
    }

    public void setModeString(String modeString) {
        this.modeString = modeString;
    }

    public SlashCommandInteractionEvent getEvent() {
        return event;
    }

    public void setEvent(SlashCommandInteractionEvent event) {
        this.event = event;
    }

    public MySQL getSql() {
        return sql;
    }

    public void setSql(MySQL sql) {
        this.sql = sql;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public String getLastRun() {
        return lastRun;
    }

    public void setLastRun(String lastRun) {
        this.lastRun = lastRun;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public BotCommands.Mode getMode() {
        return mode;
    }

    public void setMode(BotCommands.Mode mode) {
        this.mode = mode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DateHelper getDateHelper() {
        return dateHelper;
    }

    public void setDateHelper(DateHelper dateHelper) {
        this.dateHelper = dateHelper;
    }

    public String getPrivateChannel() {
        return privateChannel;
    }

    public void setPrivateChannel(String privateChannel) {
        this.privateChannel = privateChannel;
    }
}
