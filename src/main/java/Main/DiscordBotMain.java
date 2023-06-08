package Main;

import Main.Commands.BotCommands;
import Main.Commands.Buttons.ButtonListeners;
import Main.Commands.Buttons.Buttons;
import Main.Commands.Modals.ModalListeners;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;

public class DiscordBotMain {
    public DiscordApi discordApi;
    public JDA jda;



    public static void main(String[] args) throws LoginException, InterruptedException, SQLException {


           JDA bot = JDABuilder.createDefault("MTAxOTE4MTM1MTI1MDk1NjM0OQ.GBHEB_.PLdJiSFuOZV32XmKUzH84IRdBuCrbGE3TNxfwo"
                 ,GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS,GatewayIntent.GUILD_INVITES )
                .setActivity(Activity.playing("with your mom")).disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOJI, CacheFlag.STICKER).build();

        DiscordApi discordApi = new DiscordApiBuilder().setToken("MTAxOTE4MTM1MTI1MDk1NjM0OQ.GBHEB_.PLdJiSFuOZV32XmKUzH84IRdBuCrbGE3TNxfwo").login().join();



        // Initialize Listener's files.
        bot.addEventListener(new BotCommands());
        bot.addEventListener(new ModalListeners());
        bot.addEventListener(new ButtonListeners());
//        bot.addEventListener(new CreateQuery());

        Guild guild = bot.awaitReady().getGuildById("1019186529802932316");
        if (guild!=null) {

            guild.upsertCommand("fart", "An amazing smelly fart in the delicate smell of fish and eggs.").queue();

            guild.upsertCommand("task", "select your phrase").
                    addOption(OptionType.STRING,"phrase","What should I search for, master?",true).queue();

            guild.upsertCommand("delete_query","delete exists query").queue();

            guild.upsertCommand("filtered_task","Add query to the database.").addOptions(
                    new OptionData(OptionType.STRING,"search_phrase","the word that will be used to search on twitter",true),
                    new OptionData(OptionType.STRING,"exact_quote","true/false?",false )
                            .addChoice("true","true").addChoice("false", "false"),
                    new OptionData(OptionType.STRING,"filter_links","filter posts with links inside. \n|| true/false? ||",false )
                            .addChoice("true","true").addChoice("false", "false"),
                    new OptionData(OptionType.INTEGER,"min_replies","minimum replies on the original post. may cause zero results.",false ).setRequiredRange(1,999999 ),
                    new OptionData(OptionType.INTEGER,"min_likes","minimum likes received.",false ).setRequiredRange(1,999999 ),
                    new OptionData(OptionType.INTEGER,"min_retweets","minimum times the post have been reTweeted",false ).setRequiredRange(1,999999 )

            ).queue();

            guild.upsertCommand("scheduled_task","create an auto pilot task").addOptions(
                    new OptionData(OptionType.INTEGER,"frequency","In minutes, how often should I execute this?",true),
                    new OptionData(OptionType.INTEGER,"duration","In days, for how long should I do it for?",true),
                    new OptionData(OptionType.STRING,"search_phrase","the word that will be used to search on twitter",true),
                    new OptionData(OptionType.STRING,"exact_quote_scheduled","true/false?",false )
                            .addChoice("true","true").addChoice("false", "false"),
                    new OptionData(OptionType.STRING,"filter_links","filter posts with links inside. \n|| true/false? ||",false )
                            .addChoice("true","true").addChoice("false", "false"),
                    new OptionData(OptionType.INTEGER,"min_replies_scheduled","minimum replies on the original post. may cause zero results.",false ).setRequiredRange(1,99 ),
                    new OptionData(OptionType.INTEGER,"min_likes_scheduled","minimum likes received.",false ).setRequiredRange(1,9999 ),
                    new OptionData(OptionType.INTEGER,"min_retweets_scheduled","minimum times the post have been reTweeted",false ).setRequiredRange(1,9999 )

            ).queue();

            guild.upsertCommand("query","Insert new query.").queue();
            guild.upsertCommand("invite","create invitation link").queue();
            guild.upsertCommand("scheduled_task_retarded","create buggy button.").queue();
            guild.upsertCommand("private_channel","new task").queue();
        }

        else System.out.println("null");

        MessageChannel channel = bot.getTextChannelsByName("general",true).get(0);
        new JobManager(bot).ManageQueries(channel);
        new Buttons(discordApi,bot);






        // Global commands and Guild Commands
        // Global commands - can be used everywhere : any chat and also in DMs
        // Guild commands : They can only be used in specific chat

    }
    public JDA getJda() {
        return jda;
    }

    public DiscordBotMain(JDA jda) {
        this.jda = jda;
    }

    public DiscordBotMain() {
    }

    public DiscordBotMain(DiscordApi discordApi, JDA jda) {
        this.discordApi = discordApi;
        this.jda = jda;
    }

    public DiscordApi getDiscordApi() {
        return discordApi;
    }

    public void setDiscordApi(DiscordApi discordApi) {
        this.discordApi = discordApi;
    }

    public void setJda(JDA jda) {
        this.jda = jda;
    }
}
