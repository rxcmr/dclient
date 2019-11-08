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

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author rxcmr
 */
public class UserAgentInterceptor implements Interceptor {
   public final String userAgent;

   public UserAgentInterceptor(String userAgent) {
      this.userAgent = userAgent;
   }

   public UserAgentInterceptor() {
      this(
         String.format("Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
               "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36 %s",
            GoogleSearchHandler.randomName(10)
         )
      );
   }

   @NotNull
   @Override
   public Response intercept(@NotNull Chain chain) throws IOException {
      Request userAgentRequest = chain.request()
         .newBuilder()
         .header("User-Agent", userAgent)
         .build();
      return chain.proceed(userAgentRequest);
   }
}
