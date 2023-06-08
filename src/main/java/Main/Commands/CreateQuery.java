package Main.Commands;

import Main.Commands.Buttons.Buttons;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class CreateQuery extends ListenerAdapter {

    int duration = 0;
    String querySelected = null;

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        System.out.println(event.getButton().getId());
        TextChannel textChannel = event.getJDA().getTextChannelById(event.getChannel().getId());
        if (textChannel==null)
            return;

        String id = event.getChannel().getId();
        if (event.getButton().getId().contains("next_delete")) {
            textChannel.sendMessage("duration:"+duration+"\n query selected: "+querySelected).queue();
        }
        else if (event.getButton().getId().contains("duration_cancel")) {
            textChannel.deleteMessageById(event.getMessageId()).queue();
        }


        super.onButtonInteraction(event);
    }

    @Override
    public void onSelectMenuInteraction(@NotNull SelectMenuInteractionEvent event) {

        if (event.getInteraction().getSelectMenu().toString().contains("delete_query")) {
            TextChannel textChannel = event.getJDA().getTextChannelById(event.getChannel().getId());
        }

        if (event.getInteraction().getSelectMenu().toString().contains("SelectMenu:duration")) {
            switch (event.getInteraction().getValues().get(0)) {
                case "duration_day":
                    duration = 1;
                    event.deferReply().queue();
                    event.getHook().sendMessage("day selected.").queue();
                    break;
                case "duration_week":
                    duration = 7;
                    event.deferReply().queue();
                    event.getHook().sendMessage("week selected.").queue();
                    break;
                case "duration_month":
                    duration = 30;
                    event.deferReply().queue();
                    event.getHook().sendMessage("month selected.").queue();
                    break;
            }

        }
        if (event.getInteraction().getSelectMenu().toString().contains("SelectMenu:delete_query")) {
            querySelected = event.getValues().get(0);
        }

            System.out.println("Interaction:"+event.getInteraction().getValues().get(0));
            System.out.println("SelectMenu:"+event.getInteraction().getSelectMenu().toString());


        super.onSelectMenuInteraction(event);
    }
}
