@echo off
REM OptLoad GUI Quick Launcher
REM Single-click batch file to run the application

powershell -ExecutionPolicy Bypass -File "%~dp0setup-and-run.ps1" %*
