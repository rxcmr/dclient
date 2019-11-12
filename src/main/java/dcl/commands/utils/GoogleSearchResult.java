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
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * @author rxcmr
 */
public class GoogleSearchResult {
  private static Logger logger = (Logger) LoggerFactory.getLogger(GoogleSearchResult.class);
  private String title, content, url;

  @NotNull
  public static GoogleSearchResult fromGoogle(@NotNull JSONObject googleResult) {
    GoogleSearchResult gsr = new GoogleSearchResult();
    gsr.title = cleanString(googleResult.getString("title"));
    gsr.content = cleanString(googleResult.getString("snippet"));
    gsr.url = URLDecoder.decode(cleanString(googleResult.getString("link")), StandardCharsets.UTF_8);
    return gsr;
  }

  private static String cleanString(@NotNull String dirtyString) {
    return StringEscapeUtils.unescapeJava(
      StringEscapeUtils.unescapeHtml4(
        dirtyString
          .replaceAll("\\s+", " ")
          .replaceAll("<.*?>", "")
          .replaceAll("\"", "")
      )
    );
  }

  public String getTitle() {
    return title;
  }

  public String getContent() {
    return content;
  }

  public String getUrl() {
    return url;
  }

  public String getSuggestedResult() {
    return getUrl() + " - *" + getTitle() + "*: \"" + getContent() + "\"";
  }
}
