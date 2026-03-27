# Android Custom Tabs SSO Demo

This project demonstrates two Android login approaches using the same local web app:

- **WebView login** with a JavaScript bridge
- **Custom Tabs login** with a real browser session and a **deep-link callback back into the app**

The main goal is to show why **Custom Tabs are the preferred real-world choice for SSO / OAuth / browser-based auth**, while WebView is useful mainly for controlled embedded flows.

---

## What is implemented

### Web side
The local web app includes:
- login screen
- register screen
- hardcoded demo users from JSON
- local session storage
- Android bridge messaging for WebView
- Custom Tabs return-to-app flow using deep link redirect

### Android side
The Android app includes:
- a home screen with **Login with WebView** and **Login with Custom Tabs**
- a WebView with `AndroidBridge`
- toast feedback when bridge messages are received
- `CustomTabsPerformanceManager` for browser warmup and preloading
- deep-link handling through `androidcustomtabs://login`
- session UI update after deep-link return from browser

---

## Why this demo matters

In real SSO use cases, after opening a login page in Custom Tabs, you no longer have direct JavaScript/native control the way you do in WebView.

That is normal.

The correct production pattern is:

1. App opens auth page in **Custom Tabs**
2. User completes login in browser context
3. Web app or auth server redirects to app callback URL
4. Android app receives the callback and regains control

This project now demonstrates exactly that local callback model.

---

## Project structure

### Android files
- `app/src/main/java/com/shubham/androidcustomtabs/BrowserDemoApp.kt`
- `app/src/main/java/com/shubham/androidcustomtabs/HomeScreen.kt`
- `app/src/main/java/com/shubham/androidcustomtabs/WebViewScreen.kt`
- `app/src/main/java/com/shubham/androidcustomtabs/MainActivity.kt`
- `app/src/main/java/com/shubham/androidcustomtabs/CustomTabsPerformanceManager.kt`
- `app/src/main/java/com/shubham/androidcustomtabs/Helper.kt`
- `app/src/main/AndroidManifest.xml`

### Web files
- `web/server.js`
- `web/public/index.html`
- `web/public/assets/script.js`
- `web/public/assets/style.css`
- `web/public/users.json`

---

## Demo credentials

Stored in:
- `web/public/users.json`

Default users:
- `demo@sso.com` / `password123`
- `admin@sso.com` / `admin123`

You can also register a new user from the page. That extra user is stored in browser local storage for demo purposes.

---

## Web flow details

The web page supports two modes:
- **Login**
- **Register**

### When opened inside WebView
The page:
- uses `AndroidBridge.postMessage(...)`
- sends events like `pageReady`, `ping`, `loginSuccess`, `loginFailed`, `logout`
- can close the WebView via `AndroidBridge.closeWebView()`

### When opened inside Custom Tabs
The page:
- does **not** rely on direct Android bridge control
- stores or reads a callback URL
- redirects to Android after login success using a deep link

Example callback:

```text
androidcustomtabs://login?email=demo@sso.com&name=Demo%20User&source=login&transport=deep-link
```

---

## Android bridge behavior in WebView

Inside `WebViewScreen.kt`:

- `AndroidBridge` is injected with `addJavascriptInterface(..., "AndroidBridge")`
- JavaScript sends JSON payloads using `postMessage(...)`
- Android parses the event
- Android shows a toast such as:

```text
Android bridge received: loginSuccess
```

Additional toasts are shown for login success/logout as well.

---

## Custom Tabs deep-link callback setup

The Android app is configured to receive:

```text
androidcustomtabs://login
```

This is declared in `AndroidManifest.xml` with:
- `scheme = androidcustomtabs`
- `host = login`
- `launchMode = singleTask`

### Android handling

- `MainActivity` stores the latest incoming intent in `deepLinkIntent`
- `BrowserDemoApp` watches that intent
- when the app receives a matching callback URI, it extracts session info and updates the UI
- a toast confirms the browser returned to the app

---

## How Custom Tabs login works in this demo

When you tap **Login with Custom Tabs**:

1. Android opens the local login page in Custom Tabs
2. The login URL includes a callback parameter:

```text
http://10.0.2.2:3000/?callbackUrl=androidcustomtabs%3A%2F%2Flogin
```

3. After successful login, the web page redirects to:

```text
androidcustomtabs://login?...params...
```

4. Android receives that deep link and updates the session card

This is the core pattern used in real OAuth / SSO login flows, except production systems usually return an auth code instead of raw demo user info.

---

## Running the web server

Open a terminal:

```bash
cd /Users/shukugup/personal/AndroidCustomTabs/web
npm install
npm start
```

Expected output:

```text
Backend running at http://localhost:3000
```

> Use `npm start`. `npm run dev` requires `nodemon`, which is not installed by default.

---

## Running the Android app

1. Start the web server
2. Open the project in Android Studio
3. Run the app on emulator or device

### Android emulator URL

Use this inside the app:

```text
http://10.0.2.2:3000
```

Reason:
- `localhost` inside emulator points to emulator itself
- `10.0.2.2` maps to your host machine

### Physical device URL

Use your laptop IP, for example:

```text
http://192.168.1.5:3000
```

Make sure phone and laptop are on the same network.

---

## Recommended test flow

### Test 1: WebView path
1. Launch app
2. Keep URL as `http://10.0.2.2:3000`
3. Tap **Login with WebView**
4. Sign in with `demo@sso.com / password123`
5. Confirm:
   - bridge message toast appears
   - login success toast appears
   - app session card updates immediately

### Test 2: Custom Tabs deep-link path
1. Return to app home
2. Tap **Login with Custom Tabs**
3. Sign in with `demo@sso.com / password123`
4. Observe browser page redirect back to app
5. Confirm:
   - Android app opens again automatically
   - a toast says Custom Tabs returned to the app
   - session card updates from deep-link payload

---

## Build verification

Compile check:

```bash
cd /Users/shukugup/personal/AndroidCustomTabs
./gradlew :app:compileDebugKotlin
```

The project currently compiles successfully.

---

## Important real-world note

This demo returns user details directly in the deep link for simplicity.

In a real production auth setup, you usually should **not** return raw user/session data like this.
Instead, the normal pattern is:

1. browser login succeeds
2. auth server redirects back with an **authorization code** or secure token reference
3. app exchanges that code securely with backend
4. app fetches real session/user state from server

So this repo demonstrates the **shape of the flow**, not a production security design.

---

## Future improvements

Potential next steps:
- replace demo payload with auth-code style callback
- add logout callback for browser session
- support Android App Links in addition to custom scheme deep links
- add real OAuth provider mock
- improve docs/screenshots for step-by-step testing
