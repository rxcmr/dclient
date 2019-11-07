package dcl.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import dcl.commands.embeds.MEBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author rxcmr
 */
public class QueryUserCommand extends Command {
   private EmbedBuilder embedBuilder = new EmbedBuilder();
   Member member;
   User author;

   public QueryUserCommand() {
      this.name = "queryuser";
      this.aliases = new String[]{"about", "userinfo"};
      this.cooldown = 10;
      this.arguments = "*user*";
      this.help = "Information about a user.";
   }

   @Override
   protected void execute(@NotNull CommandEvent event) {
      event.getChannel().sendTyping().queue();
      member = event.getMessage().getMentionedMembers().get(0);
      author = event.getAuthor();
      event.reply(embed.buildEmbed(member, author));
      clearEmbed();
   }

   private MEBuilder embed = (member, author) -> {
      User user = member.getUser();
      embedBuilder.setTitle("JDA v4, requesting: " + user.getName());
      embedBuilder.setDescription(String.format("Member: `%s`\nUser: `%s`", user, member));
      embedBuilder.setImage(user.getEffectiveAvatarUrl());
      embedBuilder.addField("Avatar ID: ", user.getAvatarId(), false);
      embedBuilder.addField("Avatar URL: ", user.getEffectiveAvatarUrl(), false);
      embedBuilder.addField("Name: ", String.format("%s#%s", user.getName(), user.getDiscriminator()), false);
      embedBuilder.addField("Nickname: ", member.getNickname() == null ? "No nickname" : member.getNickname(), false);
      embedBuilder.addField("ID: ", user.getId(), false);
      embedBuilder.setColor(0x41 + 0x64 + 0x64 + 0x65+ 0x72);
      embedBuilder.setFooter("requested by: " + author.getName(), Objects.requireNonNull(author.getAvatarUrl()));
      return embedBuilder.build();
   };

   private void clearEmbed() {
      embedBuilder.clear();
   }
}
