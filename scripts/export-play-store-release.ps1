# export-play-store-release.ps1
# Canonical, non-interactive Play Store export for Nightseed Bastion.
#
# Responsibilities (single concern: EXPORT, not build/version-bump):
#   1. Read versionName / versionCode from app/build.gradle.kts.
#   2. Locate the already-built release App Bundle.
#   3. Locate the matching release notes in docs/release-notes/.
#   4. HARD-VALIDATE the notes: throw if any locale block exceeds the Play
#      Console 500 Unicode-char limit (tags excluded). Over-limit notes are
#      silently truncated by Play Console, so we fail-fast instead.
#   5. Copy the .aab and notes (flat) into Desktop\Build\ for upload.
#
# Build the bundle first:  .\gradlew.bat clean :app:bundleRelease

$ErrorActionPreference = "Stop"
$OutputEncoding = [System.Text.Encoding]::UTF8

$Root = Split-Path $PSScriptRoot -Parent

# 1. Extract version from build.gradle.kts
$GradlePath = Join-Path $Root "app/build.gradle.kts"
$GradleContent = Get-Content $GradlePath -Raw
if ($GradleContent -notmatch 'versionCode\s*=\s*(\d+)') { throw "versionCode not found in build.gradle.kts" }
$VersionCode = [int]$Matches[1]
if ($GradleContent -notmatch 'versionName\s*=\s*"([^"]+)"') { throw "versionName not found in build.gradle.kts" }
$VersionName = $Matches[1]

Write-Host "Exporting Nightseed Bastion v$VersionName (vc$VersionCode)" -ForegroundColor Cyan

# 2. Locate the release App Bundle
$AabSource = Join-Path $Root "app/build/outputs/bundle/release/app-release.aab"
if (-not (Test-Path $AabSource)) {
    throw "Release App Bundle not found at $AabSource. Run: .\gradlew.bat clean :app:bundleRelease"
}

# 3. Locate matching release notes
$NotesSource = Join-Path $Root "docs/release-notes/NightseedBastion-v$VersionName-vc$VersionCode-release-notes.txt"
if (-not (Test-Path $NotesSource)) {
    throw "Release notes not found at $NotesSource. Create them before exporting."
}
$NotesContent = Get-Content $NotesSource -Raw -Encoding UTF8

# 4. HARD enforcement: Play Console 500 Unicode-char limit per locale block.
$LocalePattern = '<(ko-KR|en-US|ja-JP|zh-CN|zh-TW)>([\s\S]*?)</\1>'
$Violations = @()
$matched = [regex]::Matches($NotesContent, $LocalePattern)
if ($matched.Count -eq 0) { throw "No locale blocks (<ko-KR>...</ko-KR> etc.) found in release notes." }
foreach ($m in $matched) {
    $locale = $m.Groups[1].Value
    $body = $m.Groups[2].Value.Trim()
    $len = $body.Length
    $status = if ($len -gt 500) { 'OVER' } else { 'OK' }
    Write-Host ("  {0,-7}  {1,4} / 500  {2}" -f $locale, $len, $status)
    if ($len -gt 500) { $Violations += "$locale ($len chars, $($len - 500) over)" }
}
if ($Violations.Count -gt 0) {
    throw "Play Console release notes exceed the 500-character limit per locale: " +
        ($Violations -join ', ') + ". Trim before exporting."
}

# 5. Copy outputs flat into Desktop\Build\ (OneDrive-redirect aware)
$Desktop = [System.Environment]::GetFolderPath('Desktop')
$BuildDir = Join-Path $Desktop 'Build'
New-Item -ItemType Directory -Force -Path $BuildDir | Out-Null

$DestAab = Join-Path $BuildDir "NightseedBastion-v$VersionName-vc$VersionCode.aab"
$DestNotes = Join-Path $BuildDir "NightseedBastion-v$VersionName-vc$VersionCode-release-notes.txt"

Copy-Item $AabSource $DestAab -Force
[System.IO.File]::WriteAllText($DestNotes, $NotesContent, (New-Object System.Text.UTF8Encoding($false)))

Write-Host "`nEXPORT SUCCESS" -ForegroundColor Green
Write-Host "  AAB:   $DestAab"
Write-Host "  Notes: $DestNotes"
