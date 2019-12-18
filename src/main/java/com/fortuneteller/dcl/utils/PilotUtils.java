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


import org.slf4j.LoggerFactory;

/**
 * @author rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
 */
@SuppressWarnings("unused")
public class PilotUtils {
  private PilotUtils() {
  }

  private static StackWalker.Option retainReference = StackWalker.Option.RETAIN_CLASS_REFERENCE;

  public static void info(String msg) {
    LoggerFactory.getLogger(StackWalker.getInstance(retainReference).getCallerClass()).info(msg);
  }

  public static void warn(String msg) {
    LoggerFactory.getLogger(StackWalker.getInstance(retainReference).getCallerClass()).warn(msg);
  }

  public static void error(String msg) {
    LoggerFactory.getLogger(StackWalker.getInstance(retainReference).getCallerClass()).error(msg);
  }

  public static void error(String msg, Exception e) {
    LoggerFactory.getLogger(StackWalker.getInstance(retainReference).getCallerClass()).error(msg, e);
  }

  public static void debug(String msg) {
    LoggerFactory.getLogger(StackWalker.getInstance(retainReference).getCallerClass()).debug(msg);
  }

  public static void gc() {
    warn("Garbage collection happening soon...");
    System.gc();
  }
}
