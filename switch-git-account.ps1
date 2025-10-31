# PowerShell script to switch between GitHub accounts
# Usage: .\switch-git-account.ps1 [1|2]

param(
    [Parameter(Mandatory=$false)]
    [ValidateSet(1,2)]
    [int]$Account = 1
)

# Account 1 credentials
$ACCOUNT1_NAME = "hoangnvse150782"
$ACCOUNT1_EMAIL = "hoangnvse150782@fpt.edu.vn"

# Account 2 credentials
$ACCOUNT2_NAME = "vinhdangquang1301"
$ACCOUNT2_EMAIL = "vinhdangquang1301@gmail.com"

if ($Account -eq 1) {
    Write-Host "Switching to Account 1: $ACCOUNT1_NAME" -ForegroundColor Green
    git config user.name $ACCOUNT1_NAME
    git config user.email $ACCOUNT1_EMAIL
    Write-Host "Current git config:" -ForegroundColor Yellow
    Write-Host "  Name: $(git config user.name)" -ForegroundColor Cyan
    Write-Host "  Email: $(git config user.email)" -ForegroundColor Cyan
} else {
    Write-Host "Switching to Account 2: $ACCOUNT2_NAME" -ForegroundColor Green
    git config user.name $ACCOUNT2_NAME
    git config user.email $ACCOUNT2_EMAIL
    Write-Host "Current git config:" -ForegroundColor Yellow
    Write-Host "  Name: $(git config user.name)" -ForegroundColor Cyan
    Write-Host "  Email: $(git config user.email)" -ForegroundColor Cyan
}

Write-Host "`nAccount switched successfully!" -ForegroundColor Green
Write-Host "Next commits will be attributed to: $(git config user.name) <$(git config user.email)>" -ForegroundColor Yellow

