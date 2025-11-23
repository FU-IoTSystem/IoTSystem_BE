# Azure App Service Deployment with Maven Plugin

## Prerequisites

1. **Azure CLI** installed and logged in:
   ```powershell
   az login
   ```

2. **Maven Wrapper** - Use `.\mvnw.cmd` instead of `mvn` (Maven is not installed)

## Step 1: Configure Azure Settings

Edit `pom.xml` and update these properties in the `<properties>` section:

```xml
<azure.resourceGroup>iot-system-rg</azure.resourceGroup>
<azure.appName>iot-system-backend</azure.appName>
<azure.region>eastus</azure.region>
<azure.pricingTier>B1</azure.pricingTier>
```

**Note:** The app name must be globally unique in Azure.

## Step 2: Build the JAR

First, build your application:

```powershell
cd backend\iot-backend
.\mvnw.cmd clean package -DskipTests
```

**If you get path errors with spaces**, you can:
- Use the full path without spaces, or
- Install Maven globally and use `mvn` instead

## Step 3: Configure Azure Plugin

Run the configuration command:

```powershell
.\mvnw.cmd com.microsoft.azure:azure-webapp-maven-plugin:2.5.0:config
```

This will prompt you to:
- Select subscription
- Select/create resource group
- Select/create App Service plan
- Configure runtime (Java 21, Linux, Java SE)

## Step 4: Deploy to Azure

### Option A: Deploy directly
```powershell
.\mvnw.cmd azure-webapp:deploy
```

### Option B: Build and deploy in one command
```powershell
.\mvnw.cmd clean package azure-webapp:deploy -DskipTests
```

## Alternative: Manual Deployment

If the Maven wrapper has issues, you can:

1. **Build the JAR manually:**
   ```powershell
   .\mvnw.cmd clean package -DskipTests
   ```

2. **Deploy via Azure Portal:**
   - Go to Azure Portal → Your App Service
   - Deployment Center → ZIP Deploy
   - Upload the JAR from `target/IoTSystem-0.0.1-SNAPSHOT.jar`

3. **Or use Azure CLI:**
   ```powershell
   # Create ZIP
   cd target
   Compress-Archive -Path IoTSystem-0.0.1-SNAPSHOT.jar -DestinationPath deploy.zip
   
   # Deploy
   az webapp deployment source config-zip `
     --resource-group iot-system-rg `
     --name iot-system-backend `
     --src deploy.zip
   ```

## Troubleshooting Maven Wrapper

If `.\mvnw.cmd` fails with path errors:

1. **Install Maven globally:**
   - Download from https://maven.apache.org/download.cgi
   - Add to PATH
   - Use `mvn` instead of `.\mvnw.cmd`

2. **Or use full path without spaces:**
   - Move project to a path without spaces (e.g., `C:\iot\IOT`)

3. **Or use WSL (Windows Subsystem for Linux):**
   ```bash
   cd /mnt/c/iot/IOT/backend/iot-backend
   ./mvnw clean package
   ```

## Verify Deployment

1. Check logs:
   ```powershell
   az webapp log tail --name iot-system-backend --resource-group iot-system-rg
   ```

2. Visit your app:
   - `https://iot-system-backend.azurewebsites.net`
   - `https://iot-system-backend.azurewebsites.net/swagger-ui.html`

## Common Commands

```powershell
# Build only
.\mvnw.cmd clean package -DskipTests

# Configure Azure (interactive)
.\mvnw.cmd azure-webapp:config

# Deploy
.\mvnw.cmd azure-webapp:deploy

# Start app
az webapp start --name iot-system-backend --resource-group iot-system-rg

# Stop app
az webapp stop --name iot-system-backend --resource-group iot-system-rg

# View logs
az webapp log tail --name iot-system-backend --resource-group iot-system-rg
```



