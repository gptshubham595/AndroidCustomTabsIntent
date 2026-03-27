const state = {
  mode: 'login',
  users: [],
  session: null,
};

const storageKeys = {
  users: 'demo-users',
  session: 'demo-session',
};

const elements = {
  loginTab: document.getElementById('login-tab'),
  registerTab: document.getElementById('register-tab'),
  authForm: document.getElementById('auth-form'),
  submitButton: document.getElementById('submit-button'),
  status: document.getElementById('status'),
  nameGroup: document.getElementById('name-group'),
  name: document.getElementById('name'),
  email: document.getElementById('email'),
  password: document.getElementById('password'),
  sessionCard: document.getElementById('session-card'),
  sessionName: document.getElementById('session-name'),
  sessionEmail: document.getElementById('session-email'),
  sendSession: document.getElementById('send-session'),
  logout: document.getElementById('logout'),
  pingNative: document.getElementById('ping-native'),
  closeWebView: document.getElementById('close-webview'),
};

init();

async function init() {
  await hydrateUsers();
  restoreSession();
  bindEvents();
  render();
  notifyBridge('pageReady', {
    availableUsers: state.users.map(({ password, ...user }) => user),
    hasSession: Boolean(state.session),
  });
}

async function hydrateUsers() {
  const stored = safeParse(localStorage.getItem(storageKeys.users));
  if (Array.isArray(stored) && stored.length > 0) {
    state.users = stored;
    return;
  }

  try {
    const response = await fetch('users.json', { cache: 'no-store' });
    const users = await response.json();
    state.users = Array.isArray(users) ? users : [];
    persistUsers();
  } catch (error) {
    console.error('Unable to load users.json', error);
    state.users = [];
  }
}

function bindEvents() {
  elements.loginTab.addEventListener('click', () => switchMode('login'));
  elements.registerTab.addEventListener('click', () => switchMode('register'));
  elements.authForm.addEventListener('submit', onSubmit);
  elements.sendSession.addEventListener('click', () => pushSessionToAndroid('manualShare'));
  elements.logout.addEventListener('click', logout);
  elements.pingNative.addEventListener('click', () => {
    notifyBridge('ping', { message: 'Ping from web page' });
    setStatus('Pinged Android bridge from JavaScript.');
  });
  elements.closeWebView.addEventListener('click', () => closeNativeWebView());
}

function switchMode(mode) {
  state.mode = mode;
  render();
}

function render() {
  const isLogin = state.mode === 'login';
  elements.loginTab.classList.toggle('active', isLogin);
  elements.registerTab.classList.toggle('active', !isLogin);
  elements.nameGroup.classList.toggle('hidden', isLogin);
  elements.submitButton.textContent = isLogin ? 'Sign in' : 'Create account';
  elements.password.autocomplete = isLogin ? 'current-password' : 'new-password';

  if (state.session) {
    elements.sessionCard.classList.remove('hidden');
    elements.sessionName.textContent = state.session.name;
    elements.sessionEmail.textContent = state.session.email;
    setStatus(`Signed in as ${state.session.email}`);
  } else {
    elements.sessionCard.classList.add('hidden');
    setStatus(isLogin ? 'Waiting for action…' : 'Register a new local demo account.');
  }
}

function onSubmit(event) {
  event.preventDefault();
  const name = elements.name.value.trim();
  const email = elements.email.value.trim().toLowerCase();
  const password = elements.password.value.trim();

  if (!email || !password) {
    setStatus('Email and password are required.');
    return;
  }

  if (state.mode === 'login') {
    handleLogin(email, password);
  } else {
    handleRegister(name, email, password);
  }
}

function handleLogin(email, password) {
  const user = state.users.find((item) => item.email === email && item.password === password);
  if (!user) {
    setStatus('Invalid credentials. Try demo@sso.com / password123');
    notifyBridge('loginFailed', { email });
    return;
  }

  establishSession(user, 'login');
}

function handleRegister(name, email, password) {
  if (!name) {
    setStatus('Full name is required for registration.');
    return;
  }

  const existing = state.users.find((item) => item.email === email);
  if (existing) {
    setStatus('User already exists. Please login instead.');
    return;
  }

  const user = { name, email, password };
  state.users.push(user);
  persistUsers();
  establishSession(user, 'register');
}

function establishSession(user, source) {
  state.session = {
    name: user.name,
    email: user.email,
    source,
    loggedInAt: new Date().toISOString(),
  };
  localStorage.setItem(storageKeys.session, JSON.stringify(state.session));
  render();
  pushSessionToAndroid(source);
}

function pushSessionToAndroid(source) {
  if (!state.session) return;

  notifyBridge('loginSuccess', {
    ...state.session,
    source,
    transport: isAndroidBridgeAvailable() ? 'webview-bridge' : 'browser-session',
  });
}

function logout() {
  state.session = null;
  localStorage.removeItem(storageKeys.session);
  render();
  notifyBridge('logout', { loggedOut: true });
}

function restoreSession() {
  const storedSession = safeParse(localStorage.getItem(storageKeys.session));
  if (storedSession?.email) {
    state.session = storedSession;
  }
}

function persistUsers() {
  localStorage.setItem(storageKeys.users, JSON.stringify(state.users));
}

function setStatus(message) {
  elements.status.textContent = message;
}

function safeParse(value) {
  try {
    return value ? JSON.parse(value) : null;
  } catch (error) {
    return null;
  }
}

function isAndroidBridgeAvailable() {
  return Boolean(window.AndroidBridge);
}

function notifyBridge(eventName, payload) {
  const message = JSON.stringify({ event: eventName, payload });

  if (window.AndroidBridge?.postMessage) {
    window.AndroidBridge.postMessage(message);
    return;
  }

  if (window.webkit?.messageHandlers?.AndroidBridge) {
    window.webkit.messageHandlers.AndroidBridge.postMessage(message);
    return;
  }

  console.log('Bridge unavailable:', message);
}

function closeNativeWebView() {
  if (window.AndroidBridge?.closeWebView) {
    window.AndroidBridge.closeWebView();
  } else {
    setStatus('Close action is only available inside Android WebView.');
  }
}
