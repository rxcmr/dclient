package com.fortuneteller.dcl.commands.utils;

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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.impl.execchain.RequestAbortedException;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
 */
public class GoogleSearchHandler {
  public static final String URL = "https://www.googleapis.com/customsearch/v1?safe=medium&cx=%s&key=%s&num=%s&q=%s";
  private static int apiUsageCounter = 0;
  private static String googleAPIKey = null;
  private static LocalDateTime startingDate = null;
  private static SecureRandom rng = new SecureRandom();

  private GoogleSearchHandler() {
  }

  public static void init(String googleAPIKey) {
    GoogleSearchHandler.googleAPIKey = googleAPIKey;
    startingDate = LocalDateTime.now();
  }

  @NotNull
  public static List<GoogleSearchResult> performSearch(String engineId, String terms, OkHttpClient okHttpClient) {
    return performSearch(engineId, terms, 1, okHttpClient);
  }

  @NotNull
  public static List<GoogleSearchResult> performSearch(String engineId,
                                                       String terms,
                                                       int resultCount,
                                                       OkHttpClient okHttpClient) {
    try {
      if (googleAPIKey == null) throw new IllegalStateException("API Key must not be null!");
      if (engineId == null || engineId.isEmpty()) throw new IllegalArgumentException("Engine ID must not be empty!");
      LocalDateTime currentTime = LocalDateTime.now();
      if (currentTime.isAfter(startingDate.plusDays(1))) {
        startingDate = currentTime;
        apiUsageCounter = 1;
      } else if (apiUsageCounter >= 80) throw new IllegalStateException("Limit reached. (80)");
      terms = terms.replace(" ", "%20");
      String searchUrl = String.format(URL, engineId, googleAPIKey, resultCount, terms);
      URL searchURL = new URL(searchUrl);
      Request request = new Request.Builder().url(searchURL).build();
      return performRequest(request, okHttpClient);
    } catch (IOException e) {
      return new LinkedList<>();
    }
  }

  private static synchronized List<GoogleSearchResult> performRequest(Request request, OkHttpClient okHttpClient) {
    try (Response response = okHttpClient.newCall(request).execute()) {
      if (!response.isSuccessful()) throw new RequestAbortedException("Failed to get search results.");
      apiUsageCounter++;
      String json;
      try (BufferedReader in = new BufferedReader(
        new InputStreamReader(Objects.requireNonNull(response.body()).byteStream())
      )) {
        json = in.lines().map(line -> line + "\n").collect(Collectors.joining());
      }
      JSONArray jsonResults = new JSONObject(json).getJSONArray("items");
      return IntStream.range(0, jsonResults.length())
        .mapToObj(i -> GoogleSearchResult.fromGoogle(jsonResults.getJSONObject(i)))
        .collect(Collectors.toCollection(LinkedList::new));
    } catch (IOException e) {
      return new LinkedList<>();
    }
  }

  @NotNull
  public static String randomName(int randomLength) {
    char[] characters = new char[]{
      'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
      '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'
    };

    return IntStream.range(0, randomLength)
      .mapToObj(i -> String.valueOf(characters[rng.nextInt(characters.length)]))
      .collect(Collectors.joining("", "DiscordBot/", ""));
  }
}
