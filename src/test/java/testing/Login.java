package testing;

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

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

/**
 * @author rxcmr
 */
public class Login {
  final String path = "C:\\Users\\Marvin\\IdeaProjects\\dclient\\src\\test\\resources\\key.txt";
  HashMap<String, String> hashMap = new HashMap<>();

  public static void main(String[] args) {
    System.out.println(new Login().authenticateUser("kkk", "sss"));
  }

  public boolean authenticateUser(String usernameEntered, String passwordEntered) {
    try {
      Scanner scanner = new Scanner(new File(path));
      while (scanner.hasNext()) {
        String line = scanner.nextLine();
        String[] fields = line.split("\\|");
        hashMap.put(fields[0], fields[1]);
      }

      if (hashMap.get(usernameEntered).equals(passwordEntered)) {
        System.out.println(usernameEntered);
        System.out.println(hashMap.get(usernameEntered));
        System.out.println("Success.");
        return true;
      } else {
        System.out.println("Problem.");
      }
    } catch (Exception e) {
      System.out.println("Error " + e);
      e.printStackTrace();
    }
    return false;
  }
}
