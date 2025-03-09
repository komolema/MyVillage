# Install Chocolatey if not installed
if (!(Test-Path "C:\ProgramData\chocolatey")) {
    Set-ExecutionPolicy Bypass -Scope Process -Force
    [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072
    Invoke-Expression ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
}

# Install Java (for Kotlin) and Gradle
choco install -y openjdk17 gradle

# Download and Install Jenkins Agent
$JENKINS_URL = "http://YOUR-JENKINS-SERVER:8080"
$SECRET = "YOUR-JENKINS-SECRET"
$AGENT_NAME = "Windows-Vagrant-Agent"

mkdir C:\Jenkins
cd C:\Jenkins
Invoke-WebRequest -Uri "$JENKINS_URL/jnlpJars/agent.jar" -OutFile "agent.jar"
Start-Process -FilePath "java" -ArgumentList "-jar agent.jar -jnlpUrl $JENKINS_URL/computer/$AGENT_NAME/slave-agent.jnlp -secret $SECRET" -NoNewWindow