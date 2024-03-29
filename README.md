# FiguraServerBackend
Plugin/mod that allows server to act as a Figura backend.

## Server config
In config you can configure things related to your backend. Here is the default config:
```jsonc
{
    // If disabled, FSB will ignore any avatar packets
    "avatar_upload_and_download": true,
    // 100 KB, as in Figura backend
    "max_avatar_size": 100000,
    // Folder where FSB will be saving uploaded avatars
    "avatars_folder": "figura/avatars",
    // Max amount of bytes transferred per AvatarPartS2C packet. If 0 then it will send whole avatar in packet (not recommended).
    "avatar_part_size": 50000,

    // If disabled, FSB will ignore ping packets
    "pings": true, 
    // Rate limit in pings per second
    "pings_rate_limit": 20,
    // Ping size limit in bytes per second
    "pings_size_limit": 10000
}
```

## Client config
Client side of FSB can be configured through Figura settings menu for global settings, and in server info edit menu for per-server settings.

## FAQ
**Q:** My server if lagging when sending avatars to players, what to do?
**A:** Make `avatar_part_size` in server config lower, it will slow down avatar download speed, but cuz of that it will have to read less bytes per tick.

**Q:** Plugins API?
**A:** Planned, once main set of features will be done and fully tested.

**Q:** Badges support?
**A:** Planned, and only for pride ones. Special badges will remain exclusive to Figura's official backend.

**Q:** Offline mod servers support?
**A:** No.