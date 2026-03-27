# Android Custom Tabs SSO Demo

This project compares **Custom Tabs** and **WebView** for Android login flows, with the main focus on showing why **Custom Tabs are usually the better choice for SSO**.

It now includes:
- a simple web login/register demo,
- hardcoded demo users stored in JSON,
- Android WebView bridge integration,
- Custom Tabs launch flow,
- session feedback inside the Android UI.

---

## What this demo shows

The app gives two login options:

1. **Login with WebView**
   - opens the hosted login page inside an Android `WebView`
   - injects `AndroidBridge`
   - receives JavaScript messages from the page
   - shows Android toasts when bridge messages arrive

2. **Login with Custom Tabs**
   - opens the same login page in the browser via Custom Tabs
   - reuses real browser session/cookies
   - is closer to how real SSO / OAuth / enterprise login should work

The key point of this sample:

> If your login is really a browser login, especially SSO, prefer **Custom Tabs** over **WebView**.

---

## Project structure

### Android app

Important files:

- `app/src/main/java/com/shubham/androidcustomtabs/BrowserDemoApp.kt`
- `app/src/main/java/com/shubham/androidcustomtabs/HomeScreen.kt`
- `app/src/main/java/com/shubham/androidcustomtabs/WebViewScreen.kt`
- `app/src/main/java/com/shubham/androidcustomtabs/CustomTabsPerformanceManager.kt`
- `app/src/main/java/com/shubham/androidcustomtabs/Helper.kt`
- `app/src/main/AndroidManifest.xml`

### Web app

Important files:

- `web/server.js`
- `web/public/index.html`
- `web/public/assets/script.js`
- `web/public/assets/style.css`
- `web/public/users.json`

---

## Demo credentials

Default users are stored in:

- `web/public/users.json`

Current demo credentials:

- `demo@sso.com` / `password123`
- `admin@sso.com` / `admin123`

You can also register a new user from the demo page. New users are stored in browser local storage for the demo session.

---

## How the web login works

The web page has two modes:

- **Login**
- **Register**

### Login flow
- loads default users from `users.json`
- validates entered email/password
- creates a demo session in local storage
- sends a message to Android through `AndroidBridge.postMessage(...)` when inside WebView

### Register flow
- accepts name, email, password
- adds the user to in-browser storage for demo use
- creates a session immediately after registration

### Android bridge messages from web

The page sends structured JSON messages like:

- `pageReady`
- `ping`
- `loginSuccess`
- `loginFailed`
- `logout`

---

## How Android handles bridge messages

Inside `WebViewScreen.kt`:

- `AndroidBridge` is injected with `addJavascriptInterface(..., "AndroidBridge")`
- JavaScript calls `AndroidBridge.postMessage(message)`
- Android parses the event payload
- Android shows a toast like:

```text
Android bridge received: loginSuccess
```

Additional toasts are shown for important actions like successful login/logout.

---

## Why Custom Tabs are important for SSO

Custom Tabs are better for SSO because they:

- reuse browser cookies,
- reuse existing logged-in browser accounts,
- work better with redirects and enterprise identity providers,
- reduce the amount of auth logic your app owns,
- align better with OAuth/browser-based login expectations.

This makes them a much stronger fit for:

- Google / Microsoft / Okta login
- enterprise SSO
- OAuth authorization pages
- external identity provider flows

---

## Deep link setup in Android

The Android app is configured with this scheme:

- `androidcustomtabs://login`

This is declared in `AndroidManifest.xml` so the app can later be extended to receive browser redirect results from Custom Tabs/browser login flows.

Current manifest behavior:

- launcher activity remains `MainActivity`
- deep link host is `login`
- launch mode is `singleTask`

---

## Running the web server

Open a terminal and run:

```bash
cd /Users/shukugup/personal/AndroidCustomTabs/web
npm install
npm start
```

Expected output:

```text
Backend running at http://localhost:3000
```

> Use `npm start`, not `npm run dev`, unless you install `nodemon` yourself.

---

## Running the Android app

1. Start the web server.
2. Open the Android project in Android Studio.
3. Run the app on emulator or device.

### Emulator URL

Use this base URL inside the app:

```text
http://10.0.2.2:3000
```

Because:
- `localhost` inside Android emulator points to the emulator itself
- `10.0.2.2` points to your host machine

### Physical device

For a real phone, use your computer's local network IP, for example:

```text
http://192.168.1.5:3000
```

Make sure both devices are on the same network.

---

## Suggested test flow

### WebView test
1. Start the app
2. Keep URL as `http://10.0.2.2:3000`
3. Tap **Login with WebView**
4. Login using `demo@sso.com / password123`
5. Observe:
   - bridge toast on Android
   - login success toast
   - session card update in app UI

### Custom Tabs test
1. Return to home screen
2. Tap **Login with Custom Tabs**
3. Sign in using the same page in browser UI
4. Observe browser-based login behavior
5. Compare user experience with WebView

---

## Build verification

Android compile check used in this project:

```bash
cd /Users/shukugup/personal/AndroidCustomTabs
./gradlew :app:compileDebugKotlin
```

At the moment, compilation succeeds.

---

## Notes

- This project is a demo, not a production auth implementation.
- The web login is intentionally simple and local.
- The Custom Tabs path is the recommended conceptual direction for real SSO.
- The WebView path exists to compare behavior and demonstrate JavaScript bridge handling.

---

## Future improvements

Possible next steps:

- complete redirect-based Custom Tabs login return flow
- add real OAuth/SSO provider simulation
- persist Android-side session more formally
- add logout redirect handling for browser session
- improve Custom Tabs callback/deep link round-trip metrics
