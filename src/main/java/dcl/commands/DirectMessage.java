package dcl.commands;

import net.dv8tion.jda.api.entities.User;

public interface DirectMessage {
   void sendMessage(String content, User user, Throwable throwable);
}
