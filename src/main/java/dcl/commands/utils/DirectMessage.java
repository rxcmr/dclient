package dcl.commands.utils;

import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author rxcmr
 */
public interface DirectMessage {
   void send(@NotNull String content, @NotNull User user, @Nullable String exceptionMessage);
}

