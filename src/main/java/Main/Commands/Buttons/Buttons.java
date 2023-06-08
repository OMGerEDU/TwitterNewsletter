package Main.Commands.Buttons;



import Main.SQL.MySQL;
import discord4j.core.object.entity.Message;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public class Buttons extends Main.DiscordBotMain  {

    public Buttons(JDA jda) {
        super(jda);
    }

    public Buttons() {
    }

    public Buttons(DiscordApi discordApi, JDA jda) {
        super(discordApi, jda);
    }

    DiscordApi discordApi = new DiscordApiBuilder().setToken("MTAxOTE4MTM1MTI1MDk1NjM0OQ.GBHEB_.PLdJiSFuOZV32XmKUzH84IRdBuCrbGE3TNxfwo").login().join();


    public void staffMessage(MessageReceivedEvent event) {
        String  id = event.getChannel().getId();
        TextChannel textChannel = discordApi.getTextChannelById(id).flatMap(Channel::asTextChannel).orElse(null);
        new MessageBuilder().setContent("\n Why are you like dis?").addComponents(
                ActionRow.of(Button.success("success","me eiz gey"),Button.secondary("fai6l", "not sure"))
        ).addComponents(ActionRow.of(Button.success("suc3cess","me eiz gey"),Button.secondary("fai36l", "not sure")))
                .addComponents(ActionRow.of(Button.success("suc13cess","me eiz gey"),Button.secondary("fai5636l", "not sure")))
                .addComponents(ActionRow.of(Button.success("suc23cess","me eiz gey"),Button.secondary("fai346l", "not sure")))
                .addComponents(ActionRow.of(Button.success("suc33cess","me eiz gey"),Button.secondary("fai336l", "not sure")))
                .send(textChannel);

    }

    public void optionMenu_times(SlashCommandInteractionEvent event) {
//        event.deferReply().queue();
//        event.getHook().sendMessage("Create a new task:").queue();
        String  id = event.getChannel().getId();
        TextChannel textChannel = discordApi.getTextChannelById(id).flatMap(Channel::asTextChannel).orElse(null);

        new MessageBuilder().setContent("")
                .addComponents(ActionRow.of(
                        SelectMenu.create("duration","Select query duration",1,1, Arrays.asList(
                                SelectMenuOption.create("24 hours.", "duration_day"),
                                SelectMenuOption.create("7 days.","duration_week"),
                                SelectMenuOption.create("month.","duration_month")))))

                .addComponents(ActionRow.of(
                    Button.danger("duration_cancel","Abort mission."),Button.success("next_times","Next.")))
                .send(textChannel);

    }


    public void deleteScheduledOptionsMenu(SlashCommandInteractionEvent event) throws SQLException {
        event.deferReply(true).queue();
        event.getHook().sendMessage("Delete Scheduled query.").setEphemeral(true).queue();


        ArrayList<String> userQueries = new MySQL().getUserQueries(event.getUser().getId());
        System.out.println("User has "+userQueries.size()+" active queries.");
        while (userQueries.size()<5)
            userQueries.add("Open slot.");

        String  id = event.getChannel().getId();
        TextChannel textChannel = discordApi.getTextChannelById(id).flatMap(Channel::asTextChannel).orElse(null);


        new MessageBuilder().setContent("")
                .addComponents(ActionRow.of(
                        SelectMenu.create("delete_query","select query",1,1, Arrays.asList(
                                SelectMenuOption.create(userQueries.get(0),"slot0:"+event.getMember().getId()),
                                SelectMenuOption.create(userQueries.get(1),"slot1:"+event.getMember().getId()),
                                SelectMenuOption.create(userQueries.get(2),"slot2:"+event.getMember().getId()),
                                SelectMenuOption.create(userQueries.get(3),"slot3:"+event.getMember().getId()),
                                SelectMenuOption.create(userQueries.get(4),"slot4:"+event.getMember().getId())
                        ))))

                .addComponents(ActionRow.of(
                        Button.danger("duration_cancel","Abort mission."))).send(textChannel);
    }

//    public void optionMenuSavta(String id) {
//
//        System.out.println(discordApi);
//        TextChannel textChannel = getDiscordApi().getTextChannelById("1022230480684331018").flatMap(Channel::asTextChannel).orElse(null);
//
//        ArrayList<String> list = new ArrayList<>();
//        list.add("Hi");
//        list.add("Bye");
//        list.add("Tri");
//        while (list.size()<5)
//            list.add("available slot.");
//
//        new MessageBuilder().setContent("")
//                .addComponents(ActionRow.of(
//                        SelectMenu.create("delete_query","select query",1,1, Arrays.asList(
//                                SelectMenuOption.create(list.get(0),list.get(0)),
//                                SelectMenuOption.create(list.get(1),list.get(1)),
//                                SelectMenuOption.create(list.get(2),list.get(2)),
//                                SelectMenuOption.create(list.get(3),list.get(3)),
//                                SelectMenuOption.create(list.get(4),list.get(4))
//                        ))))
//                .send(textChannel);
//
//
//    }






}
