const express = require('express');
const fs = require('fs');
const path = require('path');
const cors = require('cors');

const app = express();
const PORT = 3000;
const PUBLIC_DIR = path.join(__dirname, 'public');
const USERS_FILE = path.join(PUBLIC_DIR, 'users.json');

app.use(cors());
app.use(express.json());
app.use('/static', express.static(PUBLIC_DIR));
app.use(express.static(PUBLIC_DIR));

app.get('/manifest', (req, res) => {
  const manifest = JSON.parse(fs.readFileSync(path.join(__dirname, 'manifest.json'), 'utf8'));
  res.json(manifest);
});

app.get('/api/users', (req, res) => {
  const users = JSON.parse(fs.readFileSync(USERS_FILE, 'utf8'));
  res.json(users.map(({ password, ...user }) => user));
});

app.listen(PORT, () => {
  console.log(`Backend running at http://localhost:${PORT}`);
});
