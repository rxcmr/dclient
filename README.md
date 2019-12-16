# dclient

To use, simply call a new instance of Pilot passing in a token, prefix and shards

Requirements:
- `.env` file in root directory, with Discord API Token (TOKEN) field,
 Google Custom Search API Token (API_KEY) field,
 and Engine ID (ENGINE_ID) field.

```java
package dcl;
@SuppressWarnings("ALL")
public class Runner {
  int shards = 1;
  public static void main(String[] args) throws ReflectiveOperationException { new Flesh("token", "prefix", shards); }
}
```

*This project is licensed under the Apache License 2.0 and the GNU Affero General Public License v3 (AGPLv3)*

*See [Apache License 2.0](ApacheLicense2.0.md) and [GNU Affero General Public License v3](GNUAGPLv3.md)*

