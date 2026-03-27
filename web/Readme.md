# Web SSO Demo

This folder contains the local web app used by the Android sample.

It supports:
- login
- registration
- hardcoded demo users from JSON
- WebView JavaScript bridge messaging
- Custom Tabs deep-link callback back into Android

---

## Files

- `server.js` - Express server
- `public/index.html` - login/register UI
- `public/assets/script.js` - auth logic, bridge logic, callback redirect logic
- `public/assets/style.css` - styling
- `public/users.json` - default demo users

---

## Demo users

- `demo@sso.com` / `password123`
- `admin@sso.com` / `admin123`

---

## Run locally

```bash
cd /Users/shukugup/personal/AndroidCustomTabs/web
npm install
npm start
```

Server URL:

```text
http://localhost:3000
```

---

## Android emulator URL

When used from the Android emulator, open:

```text
http://10.0.2.2:3000
```

---

## Custom Tabs callback behavior

When the page is opened from Android Custom Tabs, Android app sends a login URL containing:

```text
?callbackUrl=androidcustomtabs%3A%2F%2Flogin
```

After successful login, the web page redirects to something like:

```text
androidcustomtabs://login?email=demo@sso.com&name=Demo%20User&source=login&transport=deep-link
```

That redirect brings the user back into the Android app.

---

## WebView behavior

When opened in Android WebView, JavaScript can call:

- `AndroidBridge.postMessage(...)`
- `AndroidBridge.closeWebView()`

Events sent include:
- `pageReady`
- `ping`
- `loginSuccess`
- `loginFailed`
- `logout`

---

## Optional manifest generation

If you later bring back manifest generation:

```bash
cd /Users/shukugup/personal/AndroidCustomTabs/web
node generateManifest.js
```

---

## Tunnel testing

If you want to expose the local server externally for physical-device testing:

```bash
npm install -g tunnelmole
cd /Users/shukugup/personal/AndroidCustomTabs/web
tmole 3000
```
