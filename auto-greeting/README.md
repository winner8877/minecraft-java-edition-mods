# Auto Greeting

Client-side Fabric mod that automatically sends greeting messages when you join a Minecraft server.

## Features

- Auto-send chat messages on server join
- Supports multiple messages
- Supports commands and plain chat
- Client-only, safe for multiplayer servers

## Commands

```
/autogreet status
/autogreet on
/autogreet off
/autogreet toggle

/autogreet add <message> [index]
/autogreet remove [index]
/autogreet removeAll
/autogreet list
```

Notes:
- index is optional and 1-based
- add <message> [index]: insert as the index-th item (before existing), or append if omitted/out of range
- remove [index]: remove specified item, or last if omitted

### Examples

```
/autogreet status
/autogreet add Hello everyone!
/autogreet add /msg admin hi 1
/autogreet list
/autogreet remove 2
/autogreet remove
/autogreet removeAll
```

## Security

- No password storage
- No encryption
- No server interaction beyond normal chat

## Supported Versions

- Minecraft 1.21.11
- Fabric Loader 0.18+
- Fabric API
- Java 21

## License

MIT
