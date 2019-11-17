package dcl.commands;

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


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jagrosh.jagtag.JagTag;
import com.jagrosh.jagtag.Parser;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import dcl.commands.utils.Categories;
import dcl.commands.utils.CommandException;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.LinkedList;

/**
 * @author rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
 */
class Tag {
  public String ownerID;
  public String tagKey;
  public String tagValue;

  protected Tag(String ownerID, String tagKey, String tagValue) {
    this.ownerID = ownerID;
    this.tagKey = tagKey;
    this.tagValue = tagValue;
  }
}

@SuppressWarnings("unused")
public class JagTagCommand extends Command {
  File json = new File("src\\main\\resources\\JagTag.json");
  private LinkedList<Tag> tags = new LinkedList<>();

  public JagTagCommand() throws IOException {
    name = "jagtag";
    aliases = new String[]{"tag", "t"};
    category = Categories.Utilities;
    arguments = "**<modifier>** **<name>** **<content>**";
    help = "JagTag like the one you see in Spectra";
    if (!json.exists()) //noinspection ResultOfMethodCallIgnored
      json.createNewFile();
    deserializeData(json);
  }

  @Override
  protected void execute(@NotNull CommandEvent event) {
    String[] args = event.getArgs().split("\\s+");
    String authorID = event.getAuthor().getId();
    Parser jagtag = JagTag.newDefaultBuilder().build();

    switch (args[0]) {
      case "create":
        tags.stream().filter(t -> t.tagKey.equals(args[1])).forEachOrdered(t -> {
          throw new CommandException("Tag: " + t.tagKey + " exists.");
        });
        tags.add(new Tag(authorID, args[1], args[2]));
        try {
          serializeData(tags);
        } catch (IOException e) {
          event.reply("Serialization error.");
        }
        break;
      case "delete":
        if (args.length != 2) throw new CommandException();
        tags.removeIf(t -> t.tagKey.equals(args[1]) && t.ownerID.equals(authorID));
        try {
          serializeData(tags);
        } catch (IOException e) {
          event.reply("Serialization error.");
        }
        break;
      case "edit":
        tags.stream()
          .filter(t -> t.tagKey.equals(args[1]))
          .filter(t -> t.ownerID.equals(authorID))
          .forEachOrdered(t -> t.tagValue = args[2]);
        try {
          serializeData(tags);
        } catch (IOException e) {
          event.reply("Serialization error.");
        }
        break;
      case "raw":
        tags.forEach(t -> {
          if (t.tagKey.equals(args[0])) event.reply(t.tagValue);
          else throw new CommandException("Tag: " + args[0] + " does not exist.");
        });
        break;
      default:
        tags.forEach(t -> {
          if (t.tagKey.equals(args[0])) event.reply(jagtag.parse(t.tagValue));
          else throw new CommandException("Tag: " + args[0] + " does not exist.");
        });
        break;
    }

  }

  private synchronized void serializeData(LinkedList<Tag> tags) throws IOException {
    ObjectMapper serializer = new ObjectMapper();
    String result = serializer.writerWithDefaultPrettyPrinter().writeValueAsString(tags);
    try (BufferedWriter wtr = new BufferedWriter(new FileWriter(json))) {
      wtr.write(result);
    }
  }

  private synchronized void deserializeData(File json) throws IOException {
    try (BufferedReader rdr = new BufferedReader(new FileReader(json))) {
      ObjectMapper deserializer = new ObjectMapper();
      LinkedList<Tag> tempTags = new LinkedList<>();
      while (deserializer.readTree(rdr).elements().hasNext()) {
        JsonNode elements = deserializer.readTree(rdr).elements().next();
        String
          ownerID = elements.get("ownerID").textValue(),
          tagKey = elements.get("tagKey").textValue(),
          tagValue = elements.get("tagValue").textValue();
        tags.add(new Tag(ownerID, tagKey, tagValue));
        deserializer.readTree(rdr).elements().remove();
      }
    }
  }
}
