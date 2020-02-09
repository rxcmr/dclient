package com.fortuneteller.ndclient

import com.fortuneteller.dclient.utils.loadEnv
import me.aberrantfox.kjdautils.api.KUtils
import me.aberrantfox.kjdautils.api.dsl.PrefixDeleteMode
import me.aberrantfox.kjdautils.api.dsl.embed
import me.aberrantfox.kjdautils.api.startBot
import me.aberrantfox.kjdautils.extensions.jda.fullName
import java.awt.Color

/*
 * Copyright 2019-2020 rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>.
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
 *      Copyright (C) 2019-2020 rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
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

/**
 * @author rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
 */
class NContraption {
  companion object {
    const val VERSION = "0.0.1n"
    const val ID = "175610330217447424"
  }


  fun launch() {
    startBot(loadEnv("SUBTOKEN")) {
      configure {
        prefix = "nd."
        globalPath = "com.fortuneteller.ndclient"
        reactToCommands = true
        deleteMode = PrefixDeleteMode.Double
        deleteErrors = true
        allowPrivateMessages = true
        documentationSortOrder = listOf("Gadgets", "Moderation", "Music", "Owner", "Statistics")
        mentionEmbed = { embed {
          title = "ndclient"
          description = "*new dclient*"
          color = Color(0xd32ce6)

          author {
            name = it.author.fullName()
            iconUrl = it.author.effectiveAvatarUrl
          }

          field {
            value = "ndclient $VERSION"
            inline = false
          }

          footer {
            text = it.jda.selfUser.fullName()
            iconUrl = it.jda.selfUser.effectiveAvatarUrl
          }
        }}
        visibilityPredicate = { command, user, _, guild ->
          command.category == "Owner" && user.id == ID
          command.category == "Music" && guild?.voiceChannels?.size != 0
        }
      }
    }
  }
}