# dzy Viewer  

A lightweight Android app designed to open **.dzy** files, which are ZIP-like archives containing `index.html`, `styles.css`, and assets, displaying them in a WebView.  

## How It Works

### 1. Handling File Opening  
- The app registers an intent filter in `AndroidManifest.xml` to handle `.dzy` files from both `file://` and `content://` URIs.  
- When a user selects a `.dzy` file, the system sends the file URI to `MainActivity.java`.  

### 2. Extracting `.dzy` Archive  
- Since `.dzy` is a ZIP-like format, the app extracts its contents into the app's private storage.  
- The extraction process:  
  - Reads the `.dzy` file using a `ZipInputStream`.  
  - Extracts `index.html`, `styles.css`, and other assets into a temporary directory.  
  - Ensures security by restricting access to extracted files.  

### 3. Loading in WebView  
- After extraction, the WebView loads `index.html` from the extracted directory.  
- JavaScript and CSS are enabled for proper rendering.  
- If the file fails to load, the app displays a custom error page.  

### 4. Error Handling  
- If the `.dzy` file is corrupted, missing `index.html`, or unreadable, a custom error page is shown using `loadData()` in WebView.  

### 5. App Hiding Mechanism  
- The app does not appear in the app drawer by modifying the `AndroidManifest.xml` to remove the launcher intent filter.  
- It can only be accessed when opening a `.dzy` file from another app.  

## Technologies Used  
- **Java** (No AppCompat or AndroidX)  
- **Android WebView** for displaying extracted HTML content  
- **ZipInputStream** for extracting `.dzy` archives  
- **Intent Filters** for handling file opening  

## Security Considerations  
- Files are extracted into private storage (`getCacheDir()` or `getFilesDir()`).  
- No external storage permissions are required.  
- Auto-clearing data prevents leftover extracted files. 
