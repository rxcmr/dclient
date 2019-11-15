package dcl.utils;

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

import okhttp3.Dns;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author rxcmr
 */
public class CloudFlareDNS implements Dns {
  public CloudFlareDNS() throws UnknownHostException {
    try {
      lookup("1.1.1.1");
    } catch (UnknownHostException e) {
      lookup("1.0.0.1");
    }
  }

  @NotNull
  @Override
  public List<InetAddress> lookup(@NotNull String s) throws UnknownHostException {
    List<InetAddress> addressList = new LinkedList<>();
    Collections.addAll(addressList, InetAddress.getAllByName(s));
    return addressList;
  }
}
