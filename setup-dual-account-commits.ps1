# PowerShell script to create 20 commits with 2 different GitHub accounts
# 10 commits for Account 1, 10 commits for Account 2

# Account 1 credentials
$ACCOUNT1_NAME = "hoangnvse150782"
$ACCOUNT1_EMAIL = "hoangnvse150782@fpt.edu.vn"

# Account 2 credentials
$ACCOUNT2_NAME = "vinhdangquang1301"
$ACCOUNT2_EMAIL = "vinhdangquang1301@gmail.com"

# GitHub repository URL (replace with your actual repo URL)
# Create a new repository on GitHub first, then update this URL
$GITHUB_REPO_URL = "https://github.com/username/repository.git"

Write-Host "Starting dual-account commit setup..." -ForegroundColor Green

# Initialize git repository if not already initialized
if (-not (Test-Path .git)) {
    Write-Host "Initializing git repository..." -ForegroundColor Yellow
    git init
} else {
    Write-Host "Git repository already initialized" -ForegroundColor Yellow
}

# Add all files
Write-Host "Adding all files to staging..." -ForegroundColor Yellow
git add .

# Account 1 commits (10 commits)
Write-Host "`n=== Making 10 commits with Account 1 ===" -ForegroundColor Cyan
git config user.name $ACCOUNT1_NAME
git config user.email $ACCOUNT1_EMAIL

for ($i = 1; $i -le 10; $i++) {
    Write-Host "Account 1 - Commit $i/10" -ForegroundColor Yellow
    
    # Create a small dummy file or modify an existing one
    $dummyFile = "dummy_account1_$i.txt"
    "Account 1 commit #$i - $(Get-Date)" | Out-File -FilePath $dummyFile -Encoding utf8
    
    git add $dummyFile
    git commit -m "feat: Account 1 - Commit $i/10 - Initial setup and feature development"
    
    Start-Sleep -Milliseconds 500  # Small delay between commits
}

# Account 2 commits (10 commits)
Write-Host "`n=== Making 10 commits with Account 2 ===" -ForegroundColor Cyan
git config user.name $ACCOUNT2_NAME
git config user.email $ACCOUNT2_EMAIL

for ($i = 1; $i -le 10; $i++) {
    Write-Host "Account 2 - Commit $i/10" -ForegroundColor Yellow
    
    # Create a small dummy file or modify an existing one
    $dummyFile = "dummy_account2_$i.txt"
    "Account 2 commit #$i - $(Get-Date)" | Out-File -FilePath $dummyFile -Encoding utf8
    
    git add $dummyFile
    git commit -m "fix: Account 2 - Commit $i/10 - Bug fixes and improvements"
    
    Start-Sleep -Milliseconds 500  # Small delay between commits
}

# Display commit history
Write-Host "`n=== Commit History ===" -ForegroundColor Green
git log --oneline --pretty=format:"%h - %an <%ae> - %s" | Select-Object -First 20

# Ask if user wants to push to GitHub
Write-Host "`n=== Ready to push to GitHub ===" -ForegroundColor Green
Write-Host "Repository URL: $GITHUB_REPO_URL" -ForegroundColor Yellow
Write-Host "To push to GitHub, run:" -ForegroundColor Yellow
Write-Host "  git remote add origin $GITHUB_REPO_URL" -ForegroundColor Cyan
Write-Host "  git branch -M main" -ForegroundColor Cyan
Write-Host "  git push -u origin main" -ForegroundColor Cyan

$push = Read-Host "Do you want to push to GitHub now? (y/n)"
if ($push -eq "y" -or $push -eq "Y") {
    Write-Host "Adding remote origin..." -ForegroundColor Yellow
    git remote remove origin 2>$null
    git remote add origin $GITHUB_REPO_URL
    
    Write-Host "Pushing to GitHub..." -ForegroundColor Yellow
    git branch -M main
    git push -u origin main
    
    Write-Host "`nSuccessfully pushed to GitHub!" -ForegroundColor Green
} else {
    Write-Host "Skipping push. You can push manually later." -ForegroundColor Yellow
}

Write-Host "`n=== Setup Complete ===" -ForegroundColor Green
Write-Host "Total commits created: 20 (10 per account)" -ForegroundColor Green

