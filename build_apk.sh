#!/bin/bash

# 易经占卜 APK 打包脚本
# 用法: ./build_apk.sh [debug|release]

set -e

BUILD_TYPE=${1:-debug}

echo "========================================="
echo "  易经占卜 APK 打包工具"
echo "========================================="
echo ""
echo "构建类型: $BUILD_TYPE"
echo ""

# 清理旧的构建
echo "📦 清理旧的构建..."
rm -rf app/build/outputs/apk

# 开始构建
if [ "$BUILD_TYPE" = "release" ]; then
    echo "🔨 开始构建 Release APK..."
    ./gradlew assembleRelease

    APK_PATH="app/build/outputs/apk/release/app-release.apk"

elif [ "$BUILD_TYPE" = "debug" ]; then
    echo "🔨 开始构建 Debug APK..."
    ./gradlew assembleDebug

    APK_PATH="app/build/outputs/apk/debug/app-debug.apk"

else
    echo "❌ 错误: 无效的构建类型 '$BUILD_TYPE'"
    echo "用法: ./build_apk.sh [debug|release]"
    exit 1
fi

# 检查APK是否生成成功
if [ -f "$APK_PATH" ]; then
    APK_SIZE=$(du -h "$APK_PATH" | cut -f1)
    echo ""
    echo "========================================="
    echo "✅ 构建成功！"
    echo "========================================="
    echo ""
    echo "📱 APK 位置: $APK_PATH"
    echo "📊 APK 大小: $APK_SIZE"
    echo ""
    echo "🚀 安装到手机:"
    echo "   方法1: adb install $APK_PATH"
    echo "   方法2: 直接拷贝到手机安装"
    echo ""
else
    echo ""
    echo "❌ 构建失败！APK 未生成"
    exit 1
fi
