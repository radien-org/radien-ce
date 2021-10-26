# Copyright (c) 2021-present radien GmbH. All rights reserved.
# <p>
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# <p>
# http://www.apache.org/licenses/LICENSE-2.0
# <p>
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.Ku

$FolderName = $PSScriptRoot + "\tmp_radien_dependencys\"
$InstallLoacation = "C:\Program Files\"
$CorretoVerion = "8"
$MavenVersion = "3.8.2"
$TomEEVersion = "8.0.6"
Write-Host 'Folder "Radien_Dependencys" Exists'
$confirmation = Read-Host "Do you want to Delete the Folder? (y/N) "
if ($confirmation -eq 'y') {
    Remove-Item -Recurse -Force $FolderName
}
#PowerShell Create directory if not exists
New-Item $FolderName -ItemType Directory
Write-Host "Folder Created successfully"
    
#Downloading needed Dependencys
    
#Correto
$CorretoSavePath =  $FolderName + "amazon-corretto-"+$CorretoVerion+"-x64-windows-jdk.msi"    
$CorretoInstallArguments = "/i `"$CorretoSavePath`" "
$CorretoInstallLoacation = $FolderName + "\Amazon Corretto\jdk1.8.0_302"
Write-Host "Downloading Correto 8 Latest (OpenJDK) ..."
(New-Object System.Net.WebClient).DownloadFile( "https://corretto.aws/downloads/latest/amazon-corretto-"+$CorretoVerion+"-x64-windows-jdk.msi", $CorretoSavePath)
Write-Host "Installing Correto 8 Latest (OpenJDK) ..."
Write-Host "/!\ Please continue installing in the new Window /!\"
Start-Process msiexec.exe -ArgumentList $CorretoInstallArguments -Wait
Write-Host "Installation of Correto (OpenJDK) was successful!"
Write-Host "Setting Pathvariable for Correto (OpenJDK) ..."
[System.Environment]::SetEnvironmentVariable('JAVA_HOME', $CorretoInstallLoacation, [System.EnvironmentVariableTarget]::User)
Write-Host ("Added: JAVA_HOME "+$CorretoInstallLoacation+" to PATH")
Write-Host "#=================================#"

#Maven
Write-Host "Downloading Apache Maven 3.8.2 ..."
(New-Object System.Net.WebClient).DownloadFile("https://downloads.apache.org/maven/maven-3/"+$MavenVersion+"/binaries/apache-maven-"+$MavenVersion+"-bin.zip", $FolderName + "apache-maven-"+$MavenVersion+"-bin.zip")
Write-Host "Expand Apache Maven 3.8.2 Archive to install location..."
Expand-Archive -Path ($FolderName + "apache-maven-"+$MavenVersion+"-bin.zip") -DestinationPath $InstallLoacation
Write-Host "Removing Apache Maven 3.8.2 Archive ..."
$MavenInstallLoacation = ($FolderName + "\apache-maven-"+$MavenVersion+"\")
Remove-Item -Recurse -Force ($FolderName + "apache-maven-"+$MavenVersion+"-bin.zip")
Write-Host "Setting Pathvariables for Maven ..."
[System.Environment]::SetEnvironmentVariable('M2', $MavenInstallLoacation+"bin", [System.EnvironmentVariableTarget]::User)
Write-Host ("Added: M2 "+$MavenInstallLoacation+"bin to PATH")
[System.Environment]::SetEnvironmentVariable('MAVEN_HOME', $MavenInstallLoacation, [System.EnvironmentVariableTarget]::User)
Write-Host ("Added: MAVEN_HOME "+$MavenInstallLoacation+" to PATH")
[System.Environment]::SetEnvironmentVariable('MAVEN_OPTS', "-Xms4G -Xmx4G")
Write-Host ("Added: MAVEN_OPTS -Xms4G -Xmx4G to PATH")
Write-Host "#=================================#"

#Apache TomEE 8
Write-Host "Downloading Apache TomEE 8.0.6 ..."
(New-Object System.Net.WebClient).DownloadFile("https://dlcdn.apache.org/tomee/tomee-"+$TomEEVersion+"/apache-tomee-"+$TomEEVersion+"-plus.zip", $FolderName + "apache-tomee-"+$TomEEVersion+"-plus.zip") 
Write-Host "Expand Apache TomEE 8.0.6 Archive ..."
Expand-Archive -Path ($FolderName + "apache-tomee-"+$TomEEVersion+"-plus.zip") -DestinationPath $InstallLoacation
Write-Host "Removing Apache TomEE 8.0.6 ..."
Remove-Item -Recurse -Force ($FolderName + "apache-tomee-"+$TomEEVersion+"-plus.zip")
Write-Host "#=================================#"
