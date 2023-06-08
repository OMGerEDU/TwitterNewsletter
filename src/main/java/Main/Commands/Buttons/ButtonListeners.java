package Main.Commands.Buttons;

import Main.Commands.Modals.Modals;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ButtonListeners extends ListenerAdapter {

    private final String[] BAD_WORDS = {"puta","fuck","vegan shawarma","dark"};


    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

//            System.out.println(event.getMember().getRoles());

        for (String badword:BAD_WORDS) {
            if (event.getMessage().getContentRaw().contains(badword)) {
                event.getChannel().sendMessage("You said a bad word! I am telling on you.").queue();
                TextChannel staffChannel = event.getJDA().getTextChannelById("1020365292305649705");
                assert staffChannel != null;
                staffChannel.sendMessage("I'm a snitch").queue();
//                new Buttons().staffMessage(event);
//                if (staffChannel!=null) {
//
//                }
            }
        }

        super.onMessageReceived(event);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
//
//        event.deferReply().queue();
//        event.getHook().sendMessage("Create a new task:").queue();
        TextChannel textChannel = event.getJDA().getTextChannelById(event.getChannel().getId());

        if (event.getButton().getId().contains("next")) {
//
        }
        else if (event.getButton().getId().contains("duration_cancel")) {
            textChannel.deleteMessageById(event.getMessageId()).queue();
        }

        super.onButtonInteraction(event);
    }

    @Override
    public void onSelectMenuInteraction(@NotNull SelectMenuInteractionEvent event) {

//        if (event.getInteraction().getSelectMenu().toString().contains("delete_query")) {
//            TextChannel textChannel = event.getJDA().getTextChannelById(event.getChannel().getId());
////            new Modals().deleteQueryModal(event);
//        }

        if (event.getInteraction().getSelectMenu().toString().contains("delete_query")) {
            // Only yourself try to access this method && you don't try to delete empty slot.
            if (event.getSelectedOptions().get(0).getValue().contains(event.getUser().getId())&&!event.getSelectedOptions().get(0).getLabel().contains("Open slot"))
                new Modals().deleteQueryModal(event);
            else if (event.getSelectedOptions().get(0).getLabel().contains("Open slot")) {
                event.deferReply().queue();
                event.getHook().sendMessage("Select a valid option.").queue();
                return;
            }

            event.getJDA().getTextChannelById(event.getChannel().getId()).deleteMessageById(event.getMessageId()).queue();
        }

        if (event.getInteraction().getSelectMenu().toString().contains("SelectMenu:duration")) {
            switch (event.getInteraction().getValues().get(0)) {
                case "duration_day":
                    event.deferReply().queue();
                    event.getHook().sendMessage("day selected.").queue();
                    break;
                case "duration_week":
                    event.deferReply().queue();
                    event.getHook().sendMessage("week selected.").queue();
                    break;
                case "duration_month":
                    event.deferReply().queue();
                    event.getHook().sendMessage("month selected.").queue();
                    break;
            }

            System.out.println(event.getInteraction().getValues().get(0));
        }


        super.onSelectMenuInteraction(event);
    }
}
