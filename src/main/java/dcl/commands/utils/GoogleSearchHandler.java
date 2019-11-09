package dcl.commands.utils;

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

import ch.qos.logback.classic.Logger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.impl.execchain.RequestAbortedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author rxcmr
 */
public class GoogleSearchHandler {
   public static final String googleURL = "https://www.googleapis.com/customsearch/v1?safe=medium&cx=%s&key=%s&num=%s&q=%s";
   private static String googleAPIKey = null;
   private static LocalDateTime startingDate = null;
   private static int APIUsageCounter = 0;
   private static Logger logger = (Logger) LoggerFactory.getLogger(GoogleSearchHandler.class);

   public static void init(String googleAPIKey) {
      GoogleSearchHandler.googleAPIKey = googleAPIKey;
      startingDate = LocalDateTime.now();
   }

   public static List<GoogleSearchResult> performSearch(String engineId, String terms) {
      return performSearch(engineId, terms, 1);
   }

   @Nullable
   public static List<GoogleSearchResult> performSearch(String engineId, String terms, int requiredResultsCount) {
      try {
         if (googleAPIKey == null) throw new IllegalStateException("API Key must not be null!");
         if (engineId == null || engineId.isEmpty()) throw new IllegalArgumentException("Engine ID must not be empty!");
         LocalDateTime currentTime = LocalDateTime.now();
         if (currentTime.isAfter(startingDate.plusDays(1))) {
            startingDate = currentTime;
            APIUsageCounter = 1;
         } else if (APIUsageCounter >= 80) throw new IllegalStateException("Limit reached. (80)");
         terms = terms.replace(" ", "%20");
         String searchUrl = String.format(googleURL, engineId, googleAPIKey, requiredResultsCount, terms);
         URL searchURL = new URL(searchUrl);
         OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(new UserAgentInterceptor()).build();
         Request request = new Request.Builder().url(searchURL).build();
         try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new RequestAbortedException("Failed to get search results.");
            APIUsageCounter++;
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
            return null;
         }
      } catch (IOException e) {
         return null;
      }
   }

   @NotNull
   public static String randomName(int randomLength) {
      char[] characters = new char[]{
         'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
         'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
         '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'
      };

      Random rng = new Random();
      return IntStream.range(0, randomLength)
         .mapToObj(i -> String.valueOf(characters[rng.nextInt(characters.length)]))
         .collect(Collectors.joining("", "DiscordBot/", ""));
   }
}
