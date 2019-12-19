package com.fortuneteller.dcl.utils;

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

import com.fortuneteller.dcl.commands.utils.GoogleSearchHandler;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
 */
public class UserAgentInterceptor implements Interceptor {
  public final String userAgent;

  public UserAgentInterceptor() {
    this(String.format(
      "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"
      + "AppleWebKit/537.36 (KHTML, like Gecko)"
      + "Chrome/78.0.3904.108 Safari/537.36 %s",
      GoogleSearchHandler.randomName(10)));
  }

  public UserAgentInterceptor(String userAgent) {
    this.userAgent = userAgent;
  }

  @Override
  public @NotNull Response intercept(@NotNull Chain chain) throws IOException {
    return chain.proceed(chain.request()
      .newBuilder()
      .header("User-Agent", userAgent)
      .build());
  }
}
