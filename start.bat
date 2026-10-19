@echo off
setlocal
set "EXIT_CODE=1"

where java >nul 2>nul
if errorlevel 1 (
    echo Error: Java no esta disponible en PATH.
    goto :finish
)

pushd "%~dp0api"
if errorlevel 1 (
    echo Error: no se pudo acceder al directorio api.
    goto :finish
)

if not exist "mvnw.cmd" (
    echo Error: no se encontro api\mvnw.cmd.
    popd
    goto :finish
)

start "" "%~dp0index.html"

echo Iniciando API Spring Boot en http://localhost:8080 ...
call mvnw.cmd spring-boot:run
set "EXIT_CODE=%ERRORLEVEL%"
popd

if not "%EXIT_CODE%"=="0" (
    echo La API termino con error (codigo %EXIT_CODE%).
) else (
    echo La API se detuvo.
)

:finish
echo.
echo Presiona una tecla para cerrar esta ventana.
pause >nul
endlocal & exit /b %EXIT_CODE%
