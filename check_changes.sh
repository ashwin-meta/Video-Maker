#!/bin/bash

# Android Studio Sync Troubleshooter
# Run this script to verify changes are present

echo "=========================================="
echo "Android Studio Sync Troubleshooter"
echo "=========================================="
echo ""

# Check if we're in the right directory
if [ ! -f "app/build.gradle" ]; then
    echo "❌ ERROR: Not in Video-Maker project directory"
    echo "Please cd to your Video-Maker directory first"
    exit 1
fi

echo "✅ Found Video-Maker project"
echo ""

# Check current branch
echo "📋 Current Branch:"
git branch | grep "*"
echo ""

# Check if on correct branch
current_branch=$(git branch --show-current)
if [ "$current_branch" != "copilot/upgrade-target-sdk-and-dependencies" ]; then
    echo "⚠️  WARNING: You're on branch '$current_branch'"
    echo "You should be on: copilot/upgrade-target-sdk-and-dependencies"
    echo ""
    echo "To switch, run:"
    echo "  git checkout copilot/upgrade-target-sdk-and-dependencies"
    echo ""
else
    echo "✅ On correct branch"
    echo ""
fi

# Check latest commit
echo "📋 Latest Commit:"
git log --oneline -1
echo ""

# Check if changes are present
echo "📋 Checking for Key Changes:"
echo ""

# Check 1: FFmpeg dependency removed
if grep -q "ffmpeg-kit-min-gpl:5.1" app/build.gradle 2>/dev/null; then
    if grep -q "//.*ffmpeg-kit-min-gpl:5.1" app/build.gradle 2>/dev/null; then
        echo "✅ FFmpeg dependency is commented out (correct)"
    else
        echo "❌ FFmpeg dependency still active - needs to be removed/commented"
    fi
else
    echo "✅ FFmpeg dependency removed"
fi

# Check 2: IronSource dependency removed
if grep -q "com.ironsource.sdk:mediationsdk" app/build.gradle 2>/dev/null; then
    echo "❌ IronSource dependency still present - should be removed"
else
    echo "✅ IronSource dependency removed"
fi

# Check 3: Chartboost dependency removed
if grep -q "com.google.ads.mediation:chartboost" app/build.gradle 2>/dev/null; then
    echo "❌ Chartboost dependency still present - should be removed"
else
    echo "✅ Chartboost dependency removed"
fi

# Check 4: VideoEncoderHelper exists
if [ -f "app/src/main/java/com/photofusion/holi/videomaker/photo/slideshow/VideoEncoderHelper.java" ]; then
    echo "✅ VideoEncoderHelper.java exists"
else
    echo "❌ VideoEncoderHelper.java missing - should exist"
fi

# Check 5: IronSource imports removed from VideoMakerActivity
if grep -q "import com.ironsource" app/src/main/java/com/photofusion/holi/videomaker/photo/slideshow/VideoMakerActivity.java 2>/dev/null; then
    echo "❌ IronSource import still in VideoMakerActivity - should be removed"
else
    echo "✅ IronSource import removed from VideoMakerActivity"
fi

# Check 6: compileSdk 36
if grep -q "compileSdk 36" app/build.gradle 2>/dev/null; then
    echo "✅ compileSdk is 36"
else
    echo "❌ compileSdk is not 36 - should be 36"
fi

echo ""
echo "=========================================="
echo "📋 Summary"
echo "=========================================="

# Count issues
issues=0

if grep -q "ffmpeg-kit-min-gpl:5.1" app/build.gradle 2>/dev/null; then
    if ! grep -q "//.*ffmpeg-kit-min-gpl:5.1" app/build.gradle 2>/dev/null; then
        issues=$((issues + 1))
    fi
fi

if grep -q "com.ironsource.sdk:mediationsdk" app/build.gradle 2>/dev/null; then
    issues=$((issues + 1))
fi

if grep -q "com.google.ads.mediation:chartboost" app/build.gradle 2>/dev/null; then
    issues=$((issues + 1))
fi

if [ ! -f "app/src/main/java/com/photofusion/holi/videomaker/photo/slideshow/VideoEncoderHelper.java" ]; then
    issues=$((issues + 1))
fi

if grep -q "import com.ironsource" app/src/main/java/com/photofusion/holi/videomaker/photo/slideshow/VideoMakerActivity.java 2>/dev/null; then
    issues=$((issues + 1))
fi

if ! grep -q "compileSdk 36" app/build.gradle 2>/dev/null; then
    issues=$((issues + 1))
fi

if [ $issues -eq 0 ]; then
    echo "✅ ALL CHANGES PRESENT!"
    echo ""
    echo "Your local repository has all the changes."
    echo "If Android Studio still doesn't show them:"
    echo "  1. File → Sync Project with Gradle Files"
    echo "  2. File → Invalidate Caches / Restart"
    echo "  3. Close and reopen the project"
else
    echo "⚠️  FOUND $issues ISSUE(S)"
    echo ""
    echo "Your local repository is missing some changes."
    echo "Please run:"
    echo "  git pull origin copilot/upgrade-target-sdk-and-dependencies"
    echo ""
    echo "Then try again."
fi

echo ""
echo "=========================================="
echo "For detailed help, see:"
echo "  ANDROID_STUDIO_SYNC_GUIDE.md"
echo "=========================================="
