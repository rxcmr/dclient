package com.fortuneteller.dclient.commands.gadgets.utils

import com.fortuneteller.dclient.commands.gadgets.utils.GoogleSearchResult.Companion.fromGoogle
import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.http.impl.execchain.RequestAbortedException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.security.SecureRandom
import java.time.LocalDateTime
import java.util.*
import java.util.stream.Collectors
import java.util.stream.IntStream

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
 */ /**
 * @author rxcmr <lythe1107></lythe1107>@gmail.com> or <lythe1107></lythe1107>@icloud.com>
 */
object GoogleSearchHandler {
  private var apiUsageCounter = 0
  private var googleAPIKey: String? = null
  private var startingDate: LocalDateTime? = null
  private val rng = SecureRandom()

  @JvmStatic
  fun init(googleAPIKey: String) {
    GoogleSearchHandler.googleAPIKey = googleAPIKey
    startingDate = LocalDateTime.now()
  }

  fun performSearch(engineId: String, terms: String, okHttpClient: OkHttpClient): List<GoogleSearchResult> {
    return performSearch(engineId, terms, 1, okHttpClient)
  }

  private fun performSearch(engineId: String?,
                            terms: String,
                            resultCount: Int,
                            okHttpClient: OkHttpClient): List<GoogleSearchResult> {
    var searchTerms = terms
    return try {
      checkNotNull(googleAPIKey) { "API Key must not be null!" }
      require(!(engineId == null || engineId.isEmpty())) { "Engine ID must not be empty!" }
      val currentTime = LocalDateTime.now()
      if (currentTime.isAfter(startingDate!!.plusDays(1))) {
        startingDate = currentTime
        apiUsageCounter = 1
      } else check(apiUsageCounter < 80) { "Limit reached. (80)" }
      searchTerms = searchTerms.replace(" ", "%20")
      val searchURL = URL("https://www.googleapis.com/customsearch/" +
        "v1?safe=medium&cx=$engineId&key=$googleAPIKey&num=$resultCount&q=$searchTerms")
      val request = Request.Builder().url(searchURL).build()
      performRequest(request, okHttpClient)
    } catch (e: IOException) {
      LinkedList()
    }
  }

  @Synchronized
  private fun performRequest(request: Request, okHttpClient: OkHttpClient): List<GoogleSearchResult> {
    return try {
      val response = okHttpClient.newCall(request).execute()
      response.use { r ->
        if (!r.isSuccessful) throw RequestAbortedException("Failed to get search results.")
        apiUsageCounter++
        val json: String
        val reader = BufferedReader(InputStreamReader(Objects.requireNonNull(r.body())!!.byteStream()))
        json = reader.use { input -> input.lines().map { line: String -> line + "\n" }.collect(Collectors.joining()) }
        val jsonResults = JSONObject(json).getJSONArray("items")
        IntStream.range(0, jsonResults.length()).mapToObj { i -> fromGoogle(jsonResults.getJSONObject(i)) }
          .collect(Collectors.toCollection { LinkedList<GoogleSearchResult>() })
      }
    } catch (e: IOException) {
      LinkedList()
    }
  }

  fun randomName(randomLength: Int): String {
    val characters = charArrayOf(
      'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
      'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
      'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
      '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'
    )
    return IntStream.range(0, randomLength)
      .mapToObj { characters[rng.nextInt(characters.size)].toString() }
      .collect(Collectors.joining("", "Pilot/", ""))
  }
}