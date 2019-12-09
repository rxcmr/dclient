# SQLite Table Structure

#### Columns

- `tagKey`: tag names
- `tagValue`: tag content
- `ownerID`: user ID of the tag creator
- `guildID`: guild ID of the guild that a tag is created in 
(`GLOBAL` for global tags)

#### Special Conditions

- Both `tagKey` and `guildID` are unique together,
there can be no row whose `tagKey` and `guildID` are the same.

- So, having a unique `tagKey` with a non-unique `guildID`
is perfectly allowed as with it's converse,
but they can't be both non-unique.

- `tagKey` and `tagValue` cannot be of zero length

#### Representation

| tagKey | tagValue | ownerID | guildID |
| :--- | :--- | :--- | :--- |
| jagtag | https://github.com/jagrosh/JagTag | 175610330217447424 | GLOBAL |
| JDA | https://github.com/DV8FromTheWorld/JDA | 175610330217447424 | GLOBAL |
| whoami | {author} | 175610330217447424 | GLOBAL |

#### Query

```sqlite
CREATE TABLE IF NOT EXISTS tags (
    tagKey TEXT NOT NULL,
    tagValue TEXT NOT NULL,
    ownerID TEXT NOT NULL,
    guildID TEXT NOT NULL,
    UNIQUE (tagKey, guildID) ON CONFLICT ABORT,
    CHECK (length (tagKey) != 0 AND length (tagValue) != 0)
);
PRAGMA auto_vacuum = FULL;
```
