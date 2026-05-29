# make_release.ps1 - Automated Version Bumper and Play Console Compiler for Nightseed Bastion

$ErrorActionPreference = "Stop"

# Ensure UTF-8 console input and output encoding to prevent Korean characters from being corrupted (mojibake)
$OutputEncoding = [System.Text.Encoding]::UTF8
[Console]::InputEncoding = [System.Text.Encoding]::UTF8
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

# 1. Paths Setup
$GradlePath = Join-Path $PSScriptRoot "app/build.gradle.kts"
if (-not (Test-Path $GradlePath)) {
    Write-Error "Could not find build.gradle.kts at $GradlePath!"
}

# 2. Extract Version Code and Version Name
$GradleContent = Get-Content $GradlePath -Raw

if ($GradleContent -match 'versionCode\s*=\s*(\d+)') {
    $CurrentCode = [int]$Matches[1]
} else {
    Write-Error "Could not extract versionCode from build.gradle.kts!"
}

if ($GradleContent -match 'versionName\s*=\s*"([^"]+)"') {
    $CurrentName = $Matches[1]
} else {
    Write-Error "Could not extract versionName from build.gradle.kts!"
}

$NewCode = $CurrentCode + 1

Write-Host "=========================================================" -ForegroundColor Cyan
Write-Host "     NIGHTSEED BASTION - AUTOMATED RELEASE PIPELINE      " -ForegroundColor Cyan
Write-Host "=========================================================" -ForegroundColor Cyan
Write-Host "Current Version: $CurrentName (vc $CurrentCode)" -ForegroundColor Yellow
Write-Host "Suggested Next: $CurrentName (vc $NewCode)" -ForegroundColor Yellow
Write-Host "---------------------------------------------------------"

# 3. Prompt for new Version Name
$NewName = Read-Host "Enter new versionName [Default: $CurrentName]"
if ([string]::IsNullOrWhiteSpace($NewName)) {
    $NewName = $CurrentName
}

# 4. Prompt for Korean (ko-KR) Release Notes
Write-Host "`n[1/2] 한국어(ko-KR) 출시 노트를 입력하세요:" -ForegroundColor Green
$KoNotes = ""
$Line = ""
Write-Host "(완료 시 빈 줄에서 Enter를 누르세요)" -ForegroundColor DarkGray
do {
    $Line = Read-Host "> "
    if ($Line -ne "") {
        $KoNotes += $Line + "`n"
    }
} while ($Line -ne "")

if ([string]::IsNullOrWhiteSpace($KoNotes)) {
    $KoNotes = "나이트시드 배스천 v$NewName - 시스템 안정성 및 대미지 밸런스가 개선되었습니다."
}

# 5. Prompt for English (en-US) Release Notes
Write-Host "`n[2/2] 영어(en-US) 출시 노트를 입력하세요:" -ForegroundColor Green
$EnNotes = ""
$Line = ""
Write-Host "(완료 시 빈 줄에서 Enter를 누르세요)" -ForegroundColor DarkGray
do {
    $Line = Read-Host "> "
    if ($Line -ne "") {
        $EnNotes += $Line + "`n"
    }
} while ($Line -ne "")

if ([string]::IsNullOrWhiteSpace($EnNotes)) {
    $EnNotes = "Nightseed Bastion v$NewName - System stability and gameplay balance updates."
}

# 6. Format Release Notes with play store localized xml/angle-bracket tags
$FormattedReleaseNotes = "<ko-KR>`n$($KoNotes.Trim())`n</ko-KR>`n<en-US>`n$($EnNotes.Trim())`n</en-US>"

# 7. Apply Updates to build.gradle.kts
Write-Host "`nUpdating build.gradle.kts..." -ForegroundColor Cyan
$NewContent = $GradleContent -replace "versionCode\s*=\s*\d+", "versionCode = $NewCode"
$NewContent = $NewContent -replace 'versionName\s*=\s*"[^"]+"', "versionName = `"$NewName`""
Set-Content $GradlePath $NewContent -NoNewline

# Verify it was written correctly
$VerifiedContent = Get-Content $GradlePath -Raw
if ($VerifiedContent -match "versionCode\s*=\s*$NewCode" -and $VerifiedContent -match "versionName\s*=\s*`"$NewName`"") {
    Write-Host "Version bump applied successfully!" -ForegroundColor Green
} else {
    Write-Error "Failed to verify version modifications in build.gradle.kts!"
}

# 8. Execute Release Compilation
Write-Host "`nCompiling App Bundle (Release AAB)..." -ForegroundColor Cyan
& ".\gradlew.bat" clean
& ".\gradlew.bat" :app:bundleRelease

$AabSource = Join-Path $PSScriptRoot "app/build/outputs/bundle/release/app-release.aab"
if (-not (Test-Path $AabSource)) {
    Write-Error "Build finished but could not locate release App Bundle at $AabSource!"
}

# 9. Copy Outputs to Windows Desktop for Drag & Drop
$DesktopPath = [System.Environment]::GetFolderPath('Desktop')
if ([string]::IsNullOrWhiteSpace($DesktopPath)) {
    $DesktopPath = $PSScriptRoot
    Write-Host "Could not locate Windows Desktop path. Outputting to project root instead." -ForegroundColor Yellow
}

$DesktopAab = Join-Path $DesktopPath "NightseedBastion-v$NewName-vc$NewCode.aab"
$DesktopNotes = Join-Path $DesktopPath "NightseedBastion-v$NewName-vc$NewCode-release-notes.txt"

# Archive inside project docs
$DocsNotesDir = Join-Path $PSScriptRoot "docs/release-notes"
if (-not (Test-Path $DocsNotesDir)) {
    New-Item -ItemType Directory -Path $DocsNotesDir | Out-Null
}
$ArchiveNotes = Join-Path $DocsNotesDir "NightseedBastion-v$NewName-vc$NewCode-release-notes.txt"

# Perform copies with correct UTF-8 encoding
Copy-Item $AabSource $DesktopAab -Force
[System.IO.File]::WriteAllText($DesktopNotes, $FormattedReleaseNotes, [System.Text.Encoding]::UTF8)
[System.IO.File]::WriteAllText($ArchiveNotes, $FormattedReleaseNotes, [System.Text.Encoding]::UTF8)

Write-Host "`n=========================================================" -ForegroundColor Green
Write-Host "                 RELEASE SUCCESSFULLY BUILT!             " -ForegroundColor Green
Write-Host "=========================================================" -ForegroundColor Green
Write-Host "Files exported to Desktop for direct Play Console upload:" -ForegroundColor Yellow
Write-Host "1. App Bundle:    $DesktopAab" -ForegroundColor White
Write-Host "2. Release Notes: $DesktopNotes" -ForegroundColor White
Write-Host "`nYou can now drag and drop the .aab file and copy-paste the notes file into your Play Console Release screen!" -ForegroundColor Cyan
Write-Host "=========================================================" -ForegroundColor Green
