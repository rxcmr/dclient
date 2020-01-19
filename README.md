# dclient 
[![Build Status](https://travis-ci.org/rxcmr/dclient.svg?branch=master)](https://travis-ci.org/rxcmr/dclient)
[![CircleCI](https://circleci.com/gh/rxcmr/dclient/tree/master.svg?style=svg)](https://circleci.com/gh/rxcmr/dclient/tree/master)

To use, simply call a new instance of Pilot passing in a token, prefix and shards

Requirements:
- `.env` file in root directory, with Google Custom Search API Token (API_KEY) field,
 YouTube API v3 Token (YT_API_KEY) field, and Engine ID (ENGINE_ID) field.

```kotlin
fun main() {
  Pilot("token", "pl.", 1).start()
}
```

*This project is licensed under the Apache License 2.0, and the GNU Affero General Public License v3 (AGPLv3)*

*See [Apache License 2.0](ApacheLicense2.0.md) and [GNU Affero General Public License v3](GNUAGPLv3.md)*

