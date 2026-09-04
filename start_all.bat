@echo off
echo ============================================
echo   Employment Data Platform - Auto Startup
echo ============================================
echo.

echo [1/3] Starting SpringBoot backend (port 8081)...
start "Backend" cmd /k "cd /d %~dp0backend-springboot && mvn spring-boot:run"

echo [2/3] Waiting for backend to start (15s)...
timeout /t 15 /nobreak >nul

echo [3/3] Starting Vue frontend (port 3000)...
start "Frontend" cmd /k "cd /d %~dp0frontend-vue && npm run dev"

echo Waiting for frontend to start (8s)...
timeout /t 8 /nobreak >nul

echo.
echo Opening browser...
start http://localhost:3000

echo.
echo ============================================
echo   Startup complete!
echo   Frontend: http://localhost:3000
echo   Backend:  http://localhost:8081
echo   Close terminal windows to stop services.
echo ============================================
pause
