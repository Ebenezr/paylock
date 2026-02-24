# How to Create a Docker Hub Personal Access Token

## Step 1: Go to Docker Hub Account Settings

1. Go to https://hub.docker.com
2. Sign in with your Docker Hub account
3. Click your **profile icon** (top right) → **Account settings**

## Step 2: Create a Personal Access Token

1. In the left sidebar, click **Security**
2. Click **New Access Token**
3. Give it a name (e.g., "GitHub Actions")
4. Select access permissions:
   - **Read & Write** - to push images
   - **Read only** - if you only need to pull
5. Click **Generate**

## Step 3: Copy Your Token

A long token string will appear. **Copy it immediately** — you won't be able to see it again.

Example token format:
```
dckr_pat_abc123defgh456ijklmnop789qrs
```

## Step 4: Add to GitHub Secrets

1. Go to your GitHub repo
2. **Settings** → **Secrets and variables** → **Actions**
3. Click **New repository secret**
4. Add two secrets:

| Name | Value |
|---|---|
| `DOCKER_USERNAME` | Your Docker Hub username |
| `DOCKER_PASSWORD` | The token you just created |

## Step 5: Verify It Works

Push to your `main` branch to trigger the workflow:
```bash
git push origin main
```

Watch the **Actions** tab — the `docker-push` job should:
- Build your image
- Log in to Docker Hub using your token
- Push image as `YOUR_USERNAME/paylock:latest`

## Important Security Notes

⚠️ **Never share your token publicly**
⚠️ **Don't commit it to git**
⚠️ **Regenerate it if you suspect it's been compromised**

To revoke a token:
1. Go to https://hub.docker.com → Account settings → Security
2. Find your token
3. Click the trash icon to delete it

## What Happens After

Once set up, every push to `main` automatically:
1. Builds your Docker image
2. Tags it as `yourusername/paylock:latest` and `yourusername/paylock:COMMIT_SHA`
3. Pushes it to Docker Hub

You can then pull it anywhere with:
```bash
docker pull yourusername/paylock:latest
```

## Troubleshooting

**Error: "unauthorized: authentication required"**
- Token is wrong or expired
- Double-check `DOCKER_PASSWORD` secret is the full token value
- Regenerate a new token if needed

**Error: "requested access to the resource is denied"**
- Your Docker Hub account doesn't have permission
- Try creating a new token with "Read & Write" scope

**Can't find Security tab**
- Make sure you're in Account settings, not organization settings
- Try logging out and back in
