### Changelog

#### Version 1.6
**Release Date:** April 13, 2025

---
- **New Features:**
  - **Track Version Display:** Along with the track title, the version of the track (e.g., remix, sped up, reverb, etc.) will now be visible across different sections in the app, including file details and metadata.
  - **Tidal URL Integration:** Users can now directly input a Tidal track or album URL into the search field by selecting the respective chip for seamless search.
  - **Spotify URL Support:** Users can enter a Spotify track URL (only track URLs for now, not albums) in the search field, and the app will automatically show the Tidal alternative version of that track.

- **Bug Fixes:**
  - Fixed an issue where `.lrc` files were incorrectly saved as `.lrc.txt` when choosing a custom output directory.
  - Fixed the resolution of covers when saving externally (previously low resolution, now high resolution).
  - Fixed an issue with file deletion when a custom output directory was selected.
  - Improved the home screen UI, which now updates immediately when files are deleted from the device.
  - Improved the download model to align with backend changes, ensuring proper quality songs are downloaded (fixed issue where lossless tracks would be given for Dolby or vice versa).

- **UI/UX Improvements:**
  - Added a clear button to the server URL field, allowing users to easily clear and enter a new URL (previously, they had to manually delete each part of the URL).

- **Miscellaneous:**
  - Updated source code and dependencies to the latest versions:
    - Bumped SDK version to 36.
    - Updated Gradle and various libraries for better stability and compatibility.

> [!IMPORTANT]
> **Understanding the Region Filter**
> 
> The Region Filter is a SEARCH FILTER, NOT a country selection:
> - Purpose: Filter search results by track availability in specific music markets
> - **Only affects search results**
> - Helps discover tracks available in different regions
> - Some tracks are only available in certain markets
> - Default is Brazil (BR)

> [!WARNING]
> **Potential Download Limitations**
> 
> If you experience issues downloading songs:
> - Some countries may have restricted access to certain music services
> - Examples: Russia currently has limited access to some music platforms
> - **Potential Solution:** Use a VPN to bypass regional restrictions

> [!IMPORTANT]
> Server URL is still required for app functionality
> 
> To obtain server configuration:
> - Join our Telegram group: [Echoir Support](https://t.me/ThisPandaCanTalk)
> - Follow group instructions to get the server setup

> [!NOTE]
> Continued development aims to provide the best possible music discovery and download experience.
