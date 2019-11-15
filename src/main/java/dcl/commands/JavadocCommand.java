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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author rxcmr
 */
@SuppressWarnings("unused")
public class JavadocCommand extends Command {
  public JavadocCommand() {
    name = "javadoc";
    aliases = new String[]{"docs"};
    arguments = "**<package>** **<class>**";
    help = "Gets the URL of Javadocs for JDK 13";
    category = Categories.Utilities;
  }

  @Override
  protected void execute(@NotNull CommandEvent event) {
    String[] args = event.getArgs().split("\\s+");
    if (args.length <= 2) throw new IllegalArgumentException();

    String url = String.format(
      "https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/%s/%s.html", args[0], args[1]
    );
    OkHttpClient okHttpClient = new OkHttpClient();
    Request request = new Request.Builder().url(url).head().build();

    try (Response response = okHttpClient.newCall(request).execute()) {
      if (response.code() == 404) event.reply("Not found.");
      else event.reply(url);
    } catch (IOException e) {
      event.reply("Something went wrong...");
    }
  }
}
