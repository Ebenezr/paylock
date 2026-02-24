# GitHub Secrets Setup for Paylock

## Step 1: Go to GitHub Secrets Settings

1. Go to your GitHub repo: `https://github.com/YOUR_USERNAME/paylock`
2. Click **Settings** (top right)
3. In the left sidebar, click **Secrets and variables** → **Actions**

## Step 2: Add the Required Secrets

Click **New repository secret** and add these secrets one by one:

### Database Secrets
| Secret Name | Value | Required |
|---|---|---|
| `DB_ROOT_PASSWORD` | Your MySQL root password | ✓ Yes |
| `DB_PASSWORD` | Your MySQL user password | ✓ Yes |
| `DB_NAME` | Your database name (default: `paylock`) | ✗ Optional |
| `DB_PORT` | MySQL port (default: `3307`) | ✗ Optional |

### Application Secrets
| Secret Name | Value | Required |
|---|---|---|
| `JWT_SECRET` | Your JWT secret key | ✓ Yes |

### Docker Hub Secrets (for pushing images)
| Secret Name | Value | Required |
|---|---|---|
| `DOCKER_USERNAME` | Your Docker Hub username | ✗ Optional* |
| `DOCKER_PASSWORD` | Your Docker Hub personal access token | ✗ Optional* |

*Only needed if you want automatic pushes to Docker Hub on main branch

## Step 3: Example Workflow

After adding secrets, the GitHub Actions workflow will:

1. **Build** - Compile & test your Maven project
2. **Docker Build** - Build the Docker image
3. **Docker Test** - Run `docker compose up` with your secrets injected
4. **Docker Push** (main branch only) - Push to Docker Hub

Your `.env` file variables will be replaced by the secrets automatically.

## Step 4: Verify Secrets Are Set

Run a workflow by pushing to main/develop:
```bash
git push origin main
```

Go to **Actions** tab in your GitHub repo to see the workflow run. If you see warnings like:
```
level=warning msg="The \"JWT_SECRET\" variable is not set"
```

Then the secret wasn't passed. Check that:
- Secret name matches exactly (case-sensitive)
- Secret is added to the correct repo, not an organization

## Accessing Secrets in Workflow

In the workflow file (`.github/workflows/build-and-test.yml`), secrets are accessed with:

```yaml
env:
  JWT_SECRET: ${{ secrets.JWT_SECRET }}
  DB_PASSWORD: ${{ secrets.DB_PASSWORD }}
```

This passes them as environment variables to docker-compose.

## Local Development

For local testing, copy `.env.example` to `.env` and fill in your values:

```bash
cp .env.example .env
# Edit .env with your actual credentials
docker compose up -d
```

**Never commit `.env` to git** — it should be in `.gitignore` already.
