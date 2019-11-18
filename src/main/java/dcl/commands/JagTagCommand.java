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


import com.jagrosh.jagtag.JagTag;
import com.jagrosh.jagtag.Parser;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import dcl.commands.utils.Categories;
import dcl.commands.utils.CommandException;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
 */
@SuppressWarnings("unused")
class Tag {
  public String ownerID;
  public String tagKey;
  public String tagValue;

  public String getOwnerID() {
    return ownerID;
  }

  public void setOwnerID(String ownerID) {
    this.ownerID = ownerID;
  }

  public String getTagKey() {
    return tagKey;
  }

  public void setTagKey(String tagKey) {
    this.tagKey = tagKey;
  }

  public String getTagValue() {
    return tagValue;
  }

  public void setTagValue(String tagValue) {
    this.tagValue = tagValue;
  }

  protected Tag set(String ownerID, String tagKey, String tagValue) {
    this.ownerID = ownerID;
    this.tagKey = tagKey;
    this.tagValue = tagValue;
    return this;
  }
}

@SuppressWarnings("unused")
public class JagTagCommand extends Command {
  File tagData = new File("src\\main\\resources\\JagTag.yaml");
  private List<Tag> tags = new ArrayList<>();
  private List<Tag> tagBuffer = new ArrayList<>();

  public JagTagCommand() throws IOException {
    name = "jagtag";
    aliases = new String[]{"tag", "t"};
    category = Categories.Utilities;
    arguments = "**<modifier>** **<name>** **<content>**";
    help = "JagTag like the one you see in Spectra";
    if (!tagData.exists() && tagData.createNewFile()) {
      try (BufferedWriter wtr = new BufferedWriter(new FileWriter(tagData))) {
        wtr.write("---");
        deserializeData(tagData);
      }
    } else deserializeData(tagData);
  }

  @Override
  protected void execute(@NotNull CommandEvent event) {
    String singleArg = event.getArgs();
    String[] args = event.getArgs().split("\\s+");
    String authorID = event.getAuthor().getId();
    Parser jagtag = JagTag.newDefaultBuilder().build();

    if (args.length == 3) {
      switch (args[0]) {
        case "create" -> {
          if (tags.stream().anyMatch(t -> t.getTagKey().equals(args[1])))
            throw new CommandException("Tag " + args[1] + " exists.");
          tags.stream()
            .filter(t -> !t.getOwnerID().equals(authorID) && !t.getTagKey().equals(args[1]))
            .forEachOrdered(t -> {
              tagBuffer.add(t);
              tagBuffer.add(new Tag().set(authorID, args[1], args[2]));
              updateTagList();
              tagBuffer.clear();
            });

          tags.add(new Tag().set(authorID, args[1], args[2]));
          serializeData(tags);
        }
        case "delete", "remove" -> tags.stream()
          .filter(t -> !t.getOwnerID().equals(authorID) && !t.getTagKey().equals(args[1]))
          .forEachOrdered(t -> {
            tagBuffer.add(t);
            updateTagList();
            tagBuffer.clear();
          });
        case "edit" -> {
          tags.removeIf(t -> t.getOwnerID().equals(authorID) && t.getTagKey().equals(args[1]));
          tags.stream()
            .filter(t -> !t.getOwnerID().equals(authorID) && !t.getTagKey().equals(args[1]))
            .forEachOrdered(t -> {
              tagBuffer.add(t);
              tagBuffer.add(new Tag().set(authorID, args[1], args[2]));
              updateTagList();
              tagBuffer.clear();
            });
        }
        default -> throw new CommandException("Unexpected value: " + args[0]);
      }
    } else if (args.length == 2) {
      switch (args[0]) {
        case "raw", "source" -> {
          if (tags.stream().anyMatch(t -> t.getTagKey().equals(args[1]))) {
            tags.stream()
              .filter(t -> t.getTagKey().equals(args[1]))
              .map(Tag::getTagValue)
              .forEachOrdered(event::reply);
          }
        }
      }
    } else if (args.length == 1) {
      if (tags.stream().anyMatch(t -> t.getTagKey().equals(singleArg))) {
        tags.stream()
          .filter(t -> t.getTagKey().equals(singleArg))
          .map(t -> jagtag.parse(t.getTagValue()))
          .forEachOrdered(event::reply);
      }
    } else throw new CommandException();
  }

  private void updateTagList() {
    serializeData(tagBuffer);
    deserializeData(tagData);
  }

  private synchronized void serializeData(@NotNull List<Tag> tags) {
    Yaml yaml = new Yaml(new Constructor(Tag.class));
    for (Tag t : tags) {
      try (BufferedWriter wtr = new BufferedWriter(new FileWriter(tagData, true))) {
        try (BufferedWriter cleaner = new BufferedWriter(new FileWriter(tagData))) {
          cleaner.write("");
          wtr.newLine();
          yaml.dump(t, wtr);
        }
      } catch (IOException e) {
        throw new CommandException("Serialization failed.", e);
      }
    }
  }

  private synchronized void deserializeData(File tagData) {
    Yaml yaml = new Yaml(new Constructor(Tag.class));
    try (BufferedReader rdr = new BufferedReader(new FileReader(tagData))) {
      for (Object o : yaml.loadAll(rdr)) tags.add((Tag) o);
    } catch (IOException e) {
      throw new CommandException("Deserialization failed.", e);
    }
  }
}
