package dcl.commands;

/*
 * Copyright 2019 rxcmr <lythe1107@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import dcl.commands.utils.Categories;
import dcl.music.Loader;
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;

/**
 * @author rxcmr
 */
@SuppressWarnings("unused")
public class PlayTrackCommand extends Command {
  private final Loader loader;

  public PlayTrackCommand() {
    name = "play";
    arguments = "**<URL>**";
    botPermissions = new Permission[]{Permission.PRIORITY_SPEAKER, Permission.VOICE_SPEAK, Permission.VOICE_CONNECT};
    help = "Plays a track from URL.";
    category = Categories.Music;
    loader = new Loader();
  }

  @Override
  protected void execute(@NotNull CommandEvent event) {
    event.getChannel().sendTyping().queue();
    loader.loadAndPlay(event.getTextChannel(), event.getArgs());
  }
}
