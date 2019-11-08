package dcl.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author rxcmr
 */
@SuppressWarnings("unused")
public class QueryUserCommand extends Command {
   private EmbedBuilder embedBuilder = new EmbedBuilder();
   Member member;
   User author;

   public QueryUserCommand() {
      this.name = "queryuser";
      this.aliases = new String[]{"about", "userinfo"};
      this.cooldown = 10;
      this.arguments = "**user**";
      this.help = "Information about a user.";
      this.category = new Category("Utilities");
   }

   @Override
   protected void execute(@NotNull CommandEvent event) {
      event.getChannel().sendTyping().queue();
      member =
         event.getMessage().getMentionedMembers().isEmpty()
            ? (Member) event.getJDA().getUserById(event.getArgs())
            : event.getMessage().getMentionedMembers().get(0);
      author = event.getAuthor();
      event.reply(buildEmbed(member, author));
      clearEmbed();
   }

   @NotNull
   private MessageEmbed buildEmbed(@NotNull Member member, @NotNull User author) {
      User user = member.getUser();
      embedBuilder
         .setTitle("JDA v4, requesting: " + user.getName())
         .setDescription(String.format("Member: `%s`\nUser: `%s`", user, member))
         .setImage(user.getEffectiveAvatarUrl())
         .addField("Avatar ID: ", user.getAvatarId(), false)
         .addField("Avatar URL: ", user.getEffectiveAvatarUrl(), false)
         .addField("Name: ", String.format("%s#%s", user.getName(), user.getDiscriminator()), false)
         .addField("Nickname: ", member.getNickname() == null ? "No nickname" : member.getNickname(), false)
         .addField("ID: ", user.getId(), false)
         .setColor(0x41 + 0x64 + 0x64 + 0x65 + 0x72)
         .setFooter("requested by: " + author.getName(), Objects.requireNonNull(author.getAvatarUrl()));
      return embedBuilder.build();
   }

   private void clearEmbed() { embedBuilder.clear(); }
}
