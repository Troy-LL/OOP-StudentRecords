# One-click setup for the Student Record Management System.
# Detects MySQL, makes sure it is running, forces the root password to match
# what the app expects, loads the database schema, then builds and launches the app.
# Run this via "Setup-And-Run.bat" (it elevates to Administrator, which is required
# for starting/stopping the MySQL service and resetting the password).

$ErrorActionPreference = 'Stop'
$ProjectRoot = $PSScriptRoot
Set-Location $ProjectRoot

# These must match src/main/resources/db.properties
$DbName    = 'srms_db'
$DbUser    = 'root'
$DbPass    = 'Root@1234'
$SchemaSql = Join-Path $ProjectRoot 'db\schema.sql'

# Pass the password via the environment instead of the command line. This avoids
# MySQL's "Using a password on the command line interface can be insecure" warning,
# which (because it goes to stderr) would otherwise abort the script under
# $ErrorActionPreference = 'Stop'.
$Env:MYSQL_PWD = $DbPass

$script:StandaloneProc = $null

function Write-Step($m) { Write-Host "`n==> $m" -ForegroundColor Cyan }
function Write-Ok($m)   { Write-Host "    $m" -ForegroundColor Green }
function Write-Note($m) { Write-Host "    $m" -ForegroundColor Yellow }
function Fail($m) {
    Write-Host "`nERROR: $m" -ForegroundColor Red
    Read-Host 'Press Enter to close'
    exit 1
}

function Test-Port {
    param([int]$Port = 3306)
    try {
        $c = New-Object System.Net.Sockets.TcpClient
        $c.Connect('127.0.0.1', $Port)
        $c.Close()
        return $true
    } catch { return $false }
}

function Wait-Port {
    param([int]$Port = 3306, [int]$TimeoutSec = 30)
    $deadline = (Get-Date).AddSeconds($TimeoutSec)
    while ((Get-Date) -lt $deadline) {
        if (Test-Port -Port $Port) { return $true }
        Start-Sleep -Milliseconds 500
    }
    return $false
}

function Test-DbLogin {
    # Native commands can write to stderr (e.g. warnings); don't let that abort us.
    $ErrorActionPreference = 'SilentlyContinue'
    & $mysql "-u$DbUser" "-e" "SELECT 1;" 2>$null | Out-Null
    return ($LASTEXITCODE -eq 0)
}

# Bring MySQL up for normal use (service if available, otherwise standalone).
function Start-MySql {
    if ($svc) {
        $s = Get-Service $svc.Name
        if ($s.Status -ne 'Running') { Start-Service $svc.Name }
    } elseif (-not (Test-Port)) {
        $script:StandaloneProc = Start-Process -FilePath $mysqld `
            -ArgumentList (Build-MysqldArgs) -WindowStyle Hidden -PassThru
    }
    if (-not (Wait-Port -TimeoutSec 30)) { Fail "MySQL did not start on port 3306." }
}

# Stop MySQL so it can be restarted with a password-reset init file.
function Stop-MySql {
    if ($svc) {
        $s = Get-Service $svc.Name
        if ($s.Status -ne 'Stopped') { Stop-Service $svc.Name -Force }
    } else {
        $ErrorActionPreference = 'SilentlyContinue'
        & $mysqladmin "-u$DbUser" shutdown 2>$null
        Start-Sleep 2
        if ($script:StandaloneProc -and -not $script:StandaloneProc.HasExited) {
            Stop-Process -Id $script:StandaloneProc.Id -Force -ErrorAction SilentlyContinue
        }
    }
    Start-Sleep 2
}

function Build-MysqldArgs {
    param([string]$InitFile)
    $line = ''
    if ($defaultsFile) { $line += "--defaults-file=`"$defaultsFile`" " }
    if ($InitFile)     { $line += "--init-file=`"$InitFile`" " }
    $line += '--console'
    return $line
}

# --- 1. Locate MySQL -------------------------------------------------------
Write-Step 'Locating MySQL...'
$svc = Get-Service -ErrorAction SilentlyContinue |
    Where-Object { $_.Name -match 'mysql' } | Select-Object -First 1

$mysqld = $null
$defaultsFile = $null
if ($svc) {
    $binPath = (Get-CimInstance Win32_Service -Filter "Name='$($svc.Name)'").PathName
    if ($binPath -match '"?(.*?mysqld\.exe)"?') { $mysqld = $Matches[1] }
    if ($binPath -match '--defaults-file=("([^"]+)"|(\S+\.ini))') {
        $defaultsFile = if ($Matches[2]) { $Matches[2] } else { $Matches[3] }
    }
}

if (-not $mysqld) {
    $searchDirs = @(
        "$Env:ProgramFiles\MySQL",
        "${Env:ProgramFiles(x86)}\MySQL",
        'C:\ProgramData\MySQL',
        'C:\xampp\mysql',
        'C:\wamp64\bin\mysql'
    )
    foreach ($d in $searchDirs) {
        if (Test-Path $d) {
            $f = Get-ChildItem $d -Recurse -Filter 'mysqld.exe' -ErrorAction SilentlyContinue |
                Select-Object -First 1
            if ($f) { $mysqld = $f.FullName; break }
        }
    }
}
if (-not $mysqld) {
    Fail "Could not find MySQL (mysqld.exe). Install MySQL 8.x, then run this again."
}

$binDir     = Split-Path $mysqld -Parent
$mysql      = Join-Path $binDir 'mysql.exe'
$mysqladmin = Join-Path $binDir 'mysqladmin.exe'
Write-Ok "Found MySQL at: $binDir"
if ($svc) { Write-Ok "Service: $($svc.Name)" }

# --- 2. Start MySQL --------------------------------------------------------
Write-Step 'Starting MySQL...'
Start-MySql
Write-Ok 'MySQL is up on port 3306.'

# --- 3. Make sure the root password matches --------------------------------
Write-Step 'Checking root password...'
if (Test-DbLogin) {
    Write-Ok "root already uses the expected password."
} else {
    Write-Note "Password mismatch. Resetting root password to '$DbPass'..."
    $reset = Join-Path $Env:TEMP 'srms_reset.sql'
    @"
ALTER USER '$DbUser'@'localhost' IDENTIFIED BY '$DbPass';
FLUSH PRIVILEGES;
"@ | Set-Content -Encoding ASCII $reset

    Stop-MySql
    $proc = Start-Process -FilePath $mysqld `
        -ArgumentList (Build-MysqldArgs -InitFile $reset) `
        -WindowStyle Hidden -PassThru
    if (-not (Wait-Port -TimeoutSec 30)) { Fail "MySQL did not start during password reset." }
    Start-Sleep 3
    & $mysqladmin "-u$DbUser" shutdown 2>$null
    Start-Sleep 3
    if (-not $proc.HasExited) { Stop-Process -Id $proc.Id -Force -ErrorAction SilentlyContinue }
    Remove-Item $reset -ErrorAction SilentlyContinue

    Start-MySql
    if (Test-DbLogin) { Write-Ok 'Password reset succeeded.' }
    else { Fail "Could not reset the root password automatically." }
}

# --- 4. Load schema + seed data --------------------------------------------
Write-Step "Setting up database '$DbName' and seeding data..."
if (-not (Test-Path $SchemaSql)) { Fail "Schema file not found: $SchemaSql" }
$loadCmd = '"{0}" -u{1} < "{2}" 2>nul' -f $mysql, $DbUser, $SchemaSql
& {
    $ErrorActionPreference = 'SilentlyContinue'
    cmd /c $loadCmd
}
if ($LASTEXITCODE -ne 0) { Fail "Failed to load the database schema." }
Write-Ok 'Database ready.'

# --- 5. Build and launch ---------------------------------------------------
Write-Step 'Building and launching the app...'
$jar = Join-Path $ProjectRoot 'target\student-record-management-system-1.0.0.jar'
if (Get-Command mvn -ErrorAction SilentlyContinue) {
    & mvn -q clean package
    if ($LASTEXITCODE -ne 0) { Fail "Maven build failed." }
} elseif (-not (Test-Path $jar)) {
    Fail "Maven is not installed and no prebuilt jar exists. Install Maven 3.8+ and retry."
}
if (-not (Test-Path $jar)) { Fail "Build output not found: $jar" }

# Locate the Java runtime. It may not be on PATH even when Maven built fine.
$java = (Get-Command java -ErrorAction SilentlyContinue).Source
if (-not $java -and $Env:JAVA_HOME) {
    $candidate = Join-Path $Env:JAVA_HOME 'bin\java.exe'
    if (Test-Path $candidate) { $java = $candidate }
}
if (-not $java) {
    $javaSearchDirs = @(
        "$Env:ProgramFiles\Java",
        "$Env:ProgramFiles\Eclipse Adoptium",
        "$Env:ProgramFiles\Microsoft\jdk",
        "${Env:ProgramFiles(x86)}\Java"
    )
    foreach ($d in $javaSearchDirs) {
        if (Test-Path $d) {
            $f = Get-ChildItem $d -Recurse -Filter 'java.exe' -ErrorAction SilentlyContinue |
                Select-Object -First 1
            if ($f) { $java = $f.FullName; break }
        }
    }
}
if (-not $java) {
    Fail "Could not find a Java runtime. Install a JDK/JRE (Java 17+) or set JAVA_HOME, then retry."
}

Start-Process $java -ArgumentList '-jar', "`"$jar`"" -WorkingDirectory $ProjectRoot
Write-Host "`nAll set! The app is launching." -ForegroundColor Green
Write-Host "Log in with:  admin / admin123   (or staff / admin123)" -ForegroundColor Green
Start-Sleep 3
