# Configuration Gradle JDK - GestionPointsEtudiants

## Problème résolu
Le projet avait une erreur "Invalid Gradle JDK configuration found" qui empêchait le lancement de l'application.

## Solution appliquée

### 1. Configuration du JDK dans gradle.properties
Ajout de la ligne suivante dans `gradle.properties` :
```properties
org.gradle.java.home=C:\\Program Files\\Android\\Android Studio\\jbr
```

### 2. Configuration des variables d'environnement
Dans PowerShell, exécuter :
```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
```

### 3. Nettoyage et reconstruction
```powershell
.\gradlew clean
.\gradlew build
```

## Script automatique
Utilisez le script `setup-env.ps1` pour configurer automatiquement l'environnement :
```powershell
.\setup-env.ps1
```

## Vérification
Après configuration, vérifiez que Java est accessible :
```powershell
java -version
```

## Structure des fichiers modifiés
- `gradle.properties` - Configuration JDK et optimisations Gradle
- `gradle/wrapper/gradle-wrapper.properties` - Version Gradle compatible
- `local.properties` - Configuration SDK Android
- `build.gradle.kts` - Suppression de la configuration allprojects conflictuelle

## Notes importantes
- Le projet utilise Java 11 (compatible avec Gradle 8.4)
- Le JDK intégré d'Android Studio est utilisé
- Les repositories sont configurés dans `settings.gradle.kts`

