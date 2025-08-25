# Script de configuration de l'environnement pour GestionPointsEtudiants
# Ce script configure les variables d'environnement nécessaires pour Gradle

Write-Host "Configuration de l'environnement pour GestionPointsEtudiants..." -ForegroundColor Green

# Configuration du JDK intégré d'Android Studio
$JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
$env:JAVA_HOME = $JAVA_HOME

# Ajout du JDK au PATH
$env:PATH = "$JAVA_HOME\bin;$env:PATH"

Write-Host "JAVA_HOME configuré: $env:JAVA_HOME" -ForegroundColor Yellow
Write-Host "Java version:" -ForegroundColor Yellow
java -version

Write-Host "`nEnvironnement configuré avec succès !" -ForegroundColor Green
Write-Host "Vous pouvez maintenant lancer: .\gradlew build" -ForegroundColor Cyan
