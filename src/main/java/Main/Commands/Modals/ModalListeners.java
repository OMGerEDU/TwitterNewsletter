package Main.Commands.Modals;

import Main.SQL.MySQL;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class ModalListeners extends ListenerAdapter {
    int frequency;
    Boolean invalidCredentials = false;
    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if (event.getModalId().equals("Scheduled-task")) {
            String phrase = event.getValue("phrase").getAsString();
            String frequencyString = event.getValue("frequency").getAsString();

            try {
                frequency = Integer.parseInt(frequencyString);
                if (frequency>1400||frequency<5)
                    invalidCredentials = true;
            }
            catch (Exception e) {
                invalidCredentials = true;
            }
            finally {
                if (invalidCredentials) {
//                    event.deferReply().queue();
                    event.reply("Wrong frequency inserted.").queue();
                }
                else {
//                    event.deferReply().queue();
                    event.reply("TaskJob submitted. \n  phrase: " + phrase + " \n frequency: " + frequency).queue();
                }
            }
        }
        if (event.getModalId().toString().contains("@~")) {
            String[] str = event.getModalId().toString().split("@~");
            System.out.println(event.getValues());
            try {
                new MySQL().deleteQuery(str[0],str[1]);
                event.reply(str[1]+" has been removed on client oid"+str[0]).queue();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                event.reply("delete failed.").queue();
            }

        }

            super.onModalInteraction(event);
    }
}
