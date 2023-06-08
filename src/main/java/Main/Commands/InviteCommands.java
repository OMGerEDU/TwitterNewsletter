package Main.Commands;


import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.invite.Invite;
import org.javacord.api.entity.server.invite.InviteBuilder;

import java.net.URISyntaxException;

class InviteCommands  {
    DiscordApi discordApi = new DiscordApiBuilder().setToken("MTAxOTE4MTM1MTI1MDk1NjM0OQ.GSuBIr.H5oUOz5Jr3B4BYm39JqX_KxcsZy5vUTzTdzYPw").login().join();


     public void sendInvite(SlashCommandInteractionEvent event) {
         ServerTextChannel channel1 = discordApi.getServerTextChannelById("1019186529802932319").get();
         Invite invite = new InviteBuilder(channel1).setMaxAgeInSeconds(60*60*24).setMaxUses(42).create().join();
         event.deferReply().queue();
         try {
             event.getHook().sendMessage(" "+invite.getUrl().toURI()).queue();
         } catch (URISyntaxException e) {
             event.deferReply().queue();
             event.getHook().sendMessage("Invitation link sending failed. \n please try again later.");
         }
         return;
    }
}
