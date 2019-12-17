package com.fortuneteller.dcl.commands.gadgets;

/*
 * Copyright 2019 rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>.
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
 *
 * dclient, a JDA Discord bot
 *      Copyright (C) 2019 rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.fortuneteller.dcl.commands.utils.Categories;
import com.fortuneteller.dcl.commands.utils.JavadocPackages;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
 */
@SuppressWarnings("unused")
public class JavadocCommand extends Command {
  public JavadocCommand() {
    name = "javadoc";
    aliases = new String[]{"docs"};
    arguments = "**<package>** **<class>**";
    help = "Gets the URL of Javadocs for JDK 13";
    category = Categories.GADGETS.getCategory();
  }

  @Override
  protected void execute(@NotNull CommandEvent event) {
    Arrays
      .stream(JavadocPackages.values())
      .map(j -> String.format(j.getUrl(), event.getArgs()))
      .forEachOrdered(formatted -> {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(formatted).head().build();
        try (Response response = okHttpClient.newCall(request).execute()) {
          if (response.code() == 200) {
            event.reply(formatted);
          }
        } catch (IOException e) {
          event.reply("Something went wrong...");
        }
      });
  }
}
