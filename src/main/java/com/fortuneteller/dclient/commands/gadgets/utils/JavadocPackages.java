package com.fortuneteller.dclient.commands.gadgets.utils;
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


/**
 * @author rxcmr <lythe1107@gmail.com> or <lythe1107@icloud.com>
 */
@SuppressWarnings("unused")
public enum JavadocPackages {
  IO("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/io/%s.html"),
  LANG("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/lang/%s.html"),
  ANNOTATION("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/lang/annotation/%s.html"),
  CONSTANT("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/lang/constant/%s.html"),
  INVOKE("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/lang/invoke/%s.html"),
  MODULE("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/lang/module/%s.html"),
  REF("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/lang/ref/%s.html"),
  REFLECT("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/lang/reflect/%s.html"),
  MATH("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/math/%s.html"),
  NET("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/net/%s.html"),
  SPI("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/net/spi/%s.html"),
  NIO("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/nio/%s.html"),
  CHANNELS("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/nio/channels/%s.html"),
  CHANNELS_SPI("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/nio/channels/spi/%s.html"),
  CHARSET("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/nio/charset/%s.html"),
  CHARSET_SPI("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/nio/charset/spi/%s.html"),
  FILE("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/nio/file/%s.html"),
  ATTRIBUTE("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/nio/file/attribute/%s.html"),
  FILE_SPI("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/nio/file/spi/%s.html"),
  SECURITY("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/security/%s.html"),
  ACL("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/security/acl/%s.html"),
  CERT("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/security/cert/%s.html"),
  INTERFACES("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/security/interfaces/%s.html"),
  SPEC("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/security/spec/%s.html"),
  TEXT("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/text/%s.html"),
  TEXT_SPI("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/text/spi/%s.html"),
  TIME("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/time/%s.html"),
  CHRONO("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/time/chrono/%s.html"),
  FORMAT("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/time/format/%s.html"),
  TEMPORAL("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/time/temporal/%s.html"),
  ZONE("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/time/zone/%s.html"),
  UTIL("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/util/%s.html"),
  CONCURRENT("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/util/concurrent/%s.html"),
  ATOMIC("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/util/concurrent/atomic/%s.html"),
  LOCKS("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/util/concurrent/locks/%s.html"),
  FUNCTION("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/util/function/%s.html"),
  JAR("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/util/jar/%s.html"),
  REGEX("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/util/regex/%s.html"),
  UTIL_SPI("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/util/spi/%s.html"),
  STREAM("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/util/stream/%s.html"),
  ZIP("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/util/zip/%s.html"),
  CRYPTO("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/javax/crypto/%s.html"),
  CRYPTO_INTERFACES("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/javax/crypto/interfaces/%s.html"),
  CRYPTO_SPEC("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/javax/crypto/spec/%s.html"),
  JAVAX_NET("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/javax/net/%s.html"),
  SSL("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/net/ssl/%s.html"),
  AUTH("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/javax/security/auth/%s.html"),
  CALLBACK("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/javax/security/auth/callback/%s.html"),
  LOGIN("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/javax/security/auth/login/%s.html"),
  AUTH_SPI("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/javax/security/auth/spi/%s.html"),
  X500("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/javax/security/auth/x500/%s.html"),
  SECURITY_CERT("https://docs.oracle.com/en/java/javase/13/docs/api/java.base/javax/security/cert/%s.html");

  public final String url;

  JavadocPackages(String url) {
    this.url = url;
  }

  public String getUrl() {
    return url;
  }
}
