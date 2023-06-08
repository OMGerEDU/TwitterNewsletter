package Main.Commands.Modals;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

public class Modals {

    public void queryModal(SlashCommandInteractionEvent event) {
        TextInput name = TextInput.create("phrase","Your search phrase:", TextInputStyle.SHORT)
                .setMaxLength(25).setMinLength(2).setRequired(true).setPlaceholder("phrase").build();

        TextInput message = TextInput.create("frequency","How often should I check the phrase (5-1400):", TextInputStyle.SHORT)
                .setMaxLength(4).setMinLength(1).setRequired(true).setPlaceholder("minutes").build();


        Modal modal = Modal.create("Scheduled-task","Create new task").addActionRows(ActionRow.of(name,message),ActionRow.of(message)).build();
        event.replyModal(modal).queue();
    }

    public void deleteQueryModal(SlashCommandInteractionEvent event) {
        TextInput deleteName = TextInput.create("phrase","The query to delete", TextInputStyle.SHORT)
                .setMaxLength(25).setMinLength(2).setRequired(true).setPlaceholder("The query to delete").build();
        Modal modal = Modal.create("deleteQueryModal","Delete exists query.").addActionRows(ActionRow.of(deleteName)).build();
        event.replyModal(modal).queue();
    }
    public void deleteQueryModal(SelectMenuInteractionEvent event) {
        String ids = event.getSelectedOptions().get(0).getValue().split(":")[1]+"@~"+event.getSelectedOptions().get(0).getLabel();
        System.out.println(ids);

        TextInput agree = TextInput.create("agree","type yes to accept.", TextInputStyle.SHORT)
                .setMaxLength(3).setMinLength(3).setRequired(true).setPlaceholder("The query to delete").build();
        Modal modal = Modal.create(ids,"Deleting "+event.getSelectedOptions().get(0).getLabel())
                .addActionRows(ActionRow.of(agree)).build();
        event.replyModal(modal).queue();


    }

}
