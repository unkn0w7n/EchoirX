### Changelog

#### Version 1.4
**Release Date:** March 31, 2025

---
- **Service Reliability Improvements:**
  - Added automatic handling of Cloudflare daily limits
  - App will now clearly inform you when service limits are reached
  - Search automatically becomes available again after limits reset
  - Your app remembers limit status even if you close and reopen it

- **Visual Improvements:**
  - Tooltips now match the app's color theme for better visibility

- **Bug Fixes:**
  - Fixed search history duplicating entries when selecting the same item multiple times
  - Improved deletion behavior to remove only the specific history item rather than all matching items

> [!IMPORTANT]
> Server URL Configuration Required
>
> Every build of Echoir requires a server URL. To obtain the server URL:
> - Join our Telegram group: [Echoir Support](https://t.me/ThisPandaCanTalk)
> - Follow group instructions to get the server configuration

> [!NOTE]
> Backend Server Update
>
> We've switched to Cloudflare for API services, which has daily usage limits that reset at 00:00 UTC.
> If you see a message about service limits being reached, normal functionality will automatically resume after midnight UTC.

> [!WARNING]
> The app will not function without a valid server URL
