package Main.Commands;

import Main.Commands.Buttons.Buttons;
import Main.Selenium.TaskJob;
import Main.SQL.MySQL;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.FileUpload;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.jetbrains.annotations.NotNull;


import java.io.File;
import java.sql.SQLException;

public class BotCommands extends ListenerAdapter  {

    public enum  Mode {
        NORMAL_QUERY,
        SCHEDULED_TASK,
        DEEP_SEARCH_TASK
    }

    File file;
    FileUpload fileUpload;
    DiscordApi discordApi = new DiscordApiBuilder().setToken("MTAxOTE4MTM1MTI1MDk1NjM0OQ.GBHEB_.PLdJiSFuOZV32XmKUzH84IRdBuCrbGE3TNxfwo").login().join();
    //event.getHook().sendMessage("Hidden for everyone else").setEphemeral(true).queue();



    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        boolean isMember = false;
        if (event.getMember().getRoles().toString().contains("Member")) {
            isMember = true;
        }

        if (event.getName().equals("fart")) {

            try {
                new MySQL().getUserRoomID(event.getMember().getId());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            Guild guild = event.getGuild();
            TextChannel t = guild.getManager().getGuild().getTextChannelById("1022230480684331018");
            //event.deferReply().queue();
            t.sendMessage("fart").queue();
            //event.deferReply().queue();
            //event.getHook().sendMessage("That was smelly.").queue();
        }
        else if (event.getName().equals("tweet"))  {
            event.deferReply().queue();
            event.getHook().sendMessage("Walk walk it like you tweet it, yo!").queue();
        }

        else if (event.getName().equals("task")) {
            OptionMapping optionMapping = event.getOption("phrase");
            if (optionMapping==null) {
                event.getHook().sendMessage("Insert your phrase you dumbfuc ").queue();
                    return;
            }
            try {
                new TaskJob().insertNormalQuery(event,optionMapping.getAsString());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return;
        }

        else if (event.getName().equals("filtered_task")) {
            try {
                new TaskJob().organizeTask(event,isMember,Mode.NORMAL_QUERY);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        else if (event.getName().equals("invite")) {
            new InviteCommands().sendInvite(event);
        }
        else if (event.getName().equals("delete_query")) {
                if (isMember) {
                    try {
                        new Buttons().deleteScheduledOptionsMenu(event);
                    } catch (SQLException throwables) { throwables.printStackTrace(); }
                } else {
                    event.deferReply().queue();
                    event.getHook().sendMessage(event.getMember().getAsMention() + "This commands is for members only.").queue();
                }
            }

        else if (event.getName().equals("scheduled_task")) {
            if (isMember) {
                try {
                    new TaskJob().organizeTask(event,isMember,Mode.SCHEDULED_TASK);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            else {
                event.deferReply().queue();
                event.getHook().sendMessage(event.getMember().getAsMention() + "This commands is for members only, use filtered_task instead.").queue();
            }
        }        else if (event.getName().equals("private_channel")) {
            try {
                new TaskJob().createTextChannel(event.getMember(),"testicle");
            } catch (SQLException throwables) { throwables.printStackTrace(); }
        }


        else if (event.getName().equals("scheduled_task_retarded")) {
            new Buttons().optionMenu_times(event);
        }



        super.onSlashCommandInteraction(event);
    }



}
