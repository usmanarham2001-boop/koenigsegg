@echo off
where gradle >nul 2>nul
if errorlevel 1 (
  echo Gradle is not installed. Install JDK 21 and Gradle 8.8+ ^(https://gradle.org/install^), then run this again.
  pause
  exit /b 1
)
call gradle build
if errorlevel 1 (
  echo Build failed.
  pause
  exit /b 1
)
echo.
echo Done. Your mod is in build\libs\koenigsegg-cars-2.0.0.jar
pause
