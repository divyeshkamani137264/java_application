
@echo off
title Digital Diary Application

cd /d E:\DigitalDiary

echo Compiling Digital Diary...
javac -cp ".;LIB\ojdbc11.jar" *.java

if errorlevel 1 (
    echo.
    echo Compilation Failed!
    pause
    exit /b
)

echo Starting Digital Diary...
java -cp ".;LIB\ojdbc11.jar" LoginFrame

pause
