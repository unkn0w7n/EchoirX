### Changelog

#### Version 1.3
**Release Date:** March 31, 2025

---
- **Search Improvements:**
  - Added search history support
  - Direct URL input support for track/album searches

- **UI Enhancements:**
  - Added retry button for failed downloads
  - Fixed snackbar colors to properly follow app theme
  - Standardized navigation with outlined icons
  - Added UTC time display with Cloudflare reset countdown

- **Bug Fixes:**
  - Resolved MediaPlayer URL handling issues that prevented audio previews from playing
  - Various performance optimizations and stability improvements

> [!IMPORTANT]
> Server URL Configuration Required
>
> Every build of Echoir requires a server URL. To obtain the server URL:
> - Join our Telegram group: [Echoir Support](https://t.me/ThisPandaCanTalk)
> - Follow group instructions to get the server configuration

> [!NOTE]
> Backend Server Update
>
> We've switched to Cloudflare for API services, using Cloudflare KV for token storage with Upstash Redis as fallback.
> The free plan has daily usage limits that reset at 00:00 UTC.
> Please be patient if you experience temporary service limitations.

> [!WARNING]
> The app will not function without a valid server URL
