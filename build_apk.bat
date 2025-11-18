@echo off
REM 易经占卜 APK 打包脚本 (Windows版本)
REM 用法: build_apk.bat [debug|release]

setlocal

set BUILD_TYPE=%1
if "%BUILD_TYPE%"=="" set BUILD_TYPE=debug

echo =========================================
echo   易经占卜 APK 打包工具 (Windows)
echo =========================================
echo.
echo 构建类型: %BUILD_TYPE%
echo.

REM 清理旧的构建
echo 📦 清理旧的构建...
if exist app\build\outputs\apk rmdir /s /q app\build\outputs\apk

REM 开始构建
if /i "%BUILD_TYPE%"=="release" (
    echo 🔨 开始构建 Release APK...
    call gradlew.bat assembleRelease

    set APK_PATH=app\build\outputs\apk\release\app-release.apk

) else if /i "%BUILD_TYPE%"=="debug" (
    echo 🔨 开始构建 Debug APK...
    call gradlew.bat assembleDebug

    set APK_PATH=app\build\outputs\apk\debug\app-debug.apk

) else (
    echo ❌ 错误: 无效的构建类型 '%BUILD_TYPE%'
    echo 用法: build_apk.bat [debug^|release]
    exit /b 1
)

REM 检查APK是否生成成功
if exist "%APK_PATH%" (
    echo.
    echo =========================================
    echo ✅ 构建成功！
    echo =========================================
    echo.
    echo 📱 APK 位置: %APK_PATH%

    REM 显示文件大小
    for %%A in ("%APK_PATH%") do (
        set SIZE=%%~zA
        set /a SIZE_MB=!SIZE! / 1048576
        echo 📊 APK 大小: !SIZE_MB! MB
    )

    echo.
    echo 🚀 安装到手机:
    echo    方法1: adb install %APK_PATH%
    echo    方法2: 直接拷贝到手机安装
    echo.
    echo 💡 APK文件位置:
    echo    %CD%\%APK_PATH%
    echo.

    REM 在资源管理器中打开APK所在文件夹
    if /i "%BUILD_TYPE%"=="debug" (
        explorer /select,"%CD%\app\build\outputs\apk\debug\app-debug.apk"
    ) else (
        explorer /select,"%CD%\app\build\outputs\apk\release\app-release.apk"
    )

) else (
    echo.
    echo ❌ 构建失败！APK 未生成
    exit /b 1
)

endlocal
