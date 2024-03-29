# FiguraServerBackend
Plugin/mod that allows server to act as a Figura backend.

## Server config
In config you can configure things related to your backend. Here is the default config:
```jsonc
{
    // If disabled, FSB will ignore packets any avatar packets
    "avatar_upload_and_download": true,
    // 100 KB, as in Figura backend
    "max_avatar_size": 100000,
    // Folder where FSB will be saving uploaded avatars
    "avatars_folder": "figura/avatars",

    // If disabled, FSB will ignore ping packets
    "pings": true, 
    // Rate limit in pings per second
    "pings_rate_limit": 20,
    // Ping size limit in bytes per second
    "pings_size_limit": 10000
}
```