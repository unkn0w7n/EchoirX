### Changelog

#### Version 1.1
**Release Date:** 2024-12-13

---

### **Download Improvements & Fixes**
- **Resolved download issues:**
  - Fixed downloads getting stuck in the queue, ensuring smooth and reliable operations.
  - Addressed download failures caused by network timeouts, enhancing overall stability.
  - Extended storage permissions to support older Android versions (up to API 32), ensuring compatibility and successful downloads.

- **Optimized download performance:**
  - Implemented chunked downloads using `ByteReadChannel` to reduce memory usage and prevent out-of-memory errors, delivering a more efficient experience.
  - Enhanced parallel fetching of playback and metadata to minimize socket timeouts and improve responsiveness.

- **Improved metadata handling:**
  - Addressed issues with missing or incomplete metadata, ensuring comprehensive track information is embedded in downloaded files.

---

### **New Features & Enhancements**
- **Expanded quality options:**
  - Added support for **AAC** formats in two quality variations:
    - High Quality AAC (320kbps)
    - Standard Quality AAC (96kbps)
  - Introduced quality summary tooltips in download options for a clearer understanding of available choices.
  - Included visual indicators for download functionality per track, enhancing usability.

- **Advanced filtering:**
  - Implemented filters for search results based on:
    - Quality (e.g., Hi-Res, Lossless)
    - Content (Explicit/Clean)

- **Region expansion:**
  - Updated the list of available regions to align with current streaming coverage, providing access to a wider audience.

---

### **User Experience Updates**
- **Redesigned download interface:**
  - Refactored the download UI to streamline the management of multiple quality options, offering a more intuitive experience.

- **Visual refresh:**
  - Updated the app launcher icon with a new design by Xelxen, providing a fresh and appealing look.

---
