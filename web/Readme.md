# Web SSO Demo

This folder contains the small web app used by the Android project.

It provides:
- login screen
- register screen
- hardcoded demo users from JSON
- Android WebView bridge messaging

## Files

- `server.js` - Express server
- `public/index.html` - login/register page
- `public/assets/script.js` - auth logic + Android bridge messaging
- `public/assets/style.css` - page styling
- `public/users.json` - default demo users
- `manifest.json` - generated static file manifest

## Demo users

- `demo@sso.com` / `password123`
- `admin@sso.com` / `admin123`

## Run locally

```bash
cd /Users/shukugup/personal/AndroidCustomTabs/web
npm install
npm start
```

Server runs on:

```text
http://localhost:3000
```

## Android emulator URL

Use this URL inside the Android app:

```text
http://10.0.2.2:3000
```

## Optional manifest generation

If you need to regenerate the manifest:

```bash
cd /Users/shukugup/personal/AndroidCustomTabs/web
node generateManifest.js
```

## Tunnel testing

If you want external access for testing on a device outside localhost:

```bash
npm install -g tunnelmole
cd /Users/shukugup/personal/AndroidCustomTabs/web
tmole 3000
```
