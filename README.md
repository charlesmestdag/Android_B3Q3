# 📱 Gestion Points Étudiants - Android App

> Application mobile Android pour la gestion des notes et évaluations d'étudiants par les enseignants

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Java](https://img.shields.io/badge/Language-Java-orange.svg)](https://www.java.com)
[![SQLite](https://img.shields.io/badge/Database-SQLite-blue.svg)](https://www.sqlite.org)
[![Room](https://img.shields.io/badge/ORM-Room-purple.svg)](https://developer.android.com/training/data-storage/room)

## 📖 Description

Application Android native développée pour les enseignants souhaitant gérer efficacement les points de leurs étudiants. L'application fonctionne entièrement en local avec une base de données SQLite, sans nécessiter de connexion internet.

### ✨ Fonctionnalités principales

- 🏫 **Gestion des classes** : Organisation par blocs (BA1, BA2, BA3, MA1, MA2)
- 👥 **Gestion des étudiants** : Matricules uniques, association automatique aux cours
- 📚 **Gestion des cours** : Création et organisation par classe
- 📝 **Système d'évaluations** : Évaluations principales et sous-évaluations hiérarchiques
- 🔢 **Encodage des notes** : Saisie avec validation, arrondi automatique au 0,5
- 📊 **Calcul des moyennes** : Pondération automatique selon les points maximum
- 💾 **Stockage local** : Toutes les données sauvegardées localement (SQLite)

## 🎯 Statut du Projet

| Fonctionnalité | Statut | Progression |
|----------------|--------|-------------|
| Base de données & Architecture | ✅ Terminé | 100% |
| Gestion Classes/Étudiants/Cours | ✅ Terminé | 100% |
| Système d'évaluations | ✅ Terminé | 100% |
| Encodage et calcul des notes | ✅ Terminé | 100% |
| Interface utilisateur | ✅ Terminé | 100% |
| Notes forcées (UI) | ⚠️ En cours | 20% |
| Polymorphisme évaluations | ❌ À faire | 0% |

**Progression globale : 85% ✅**

## 🚀 Installation

### Prérequis
- Android Studio Arctic Fox ou plus récent
- JDK 11+
- Android SDK 24+ (Android 7.0)

### Étapes d'installation
```bash
# Cloner le repository
git clone https://github.com/[username]/GestionPointsEtudiants.git

# Ouvrir dans Android Studio
cd GestionPointsEtudiants
# File → Open → Sélectionner le dossier

# Ou compiler en ligne de commande
./gradlew assembleDebug
```

## 📱 Utilisation

### Flux de travail typique

1. **Créer des classes** 
   - Sélectionner parmi : BA1, BA2, BA3, MA1, MA2
   
2. **Ajouter des étudiants**
   - Saisir matricule (unique), nom, prénom
   - Association automatique à tous les cours du bloc
   
3. **Créer des cours**
   - Définir nom et description par classe
   
4. **Configurer les évaluations**
   - Créer évaluations principales (ex: Examen, TP)
   - Ajouter des sous-évaluations si nécessaire
   - Définir les points maximum (défaut: 20)
   
5. **Encoder les notes**
   - Cliquer sur un étudiant pour saisir sa note
   - Validation automatique et arrondi au 0,5
   
6. **Consulter les moyennes**
   - Calcul automatique avec pondération

### Captures d'écran

```
🏠 Classes → 👥 Étudiants → 📚 Cours → 📝 Évaluations → 🔢 Notes
```

## 🏗️ Architecture

### Technologies utilisées
- **Platform** : Android (minSdk 24, targetSdk 34)
- **Langages** : Java + Kotlin
- **Base de données** : Room (SQLite)
- **Architecture** : MVC avec Android Architecture Components
- **UI** : Fragments + Navigation Component
- **Design** : Material Design avec thème sombre

### Structure du projet
```
app/src/main/java/com/mestdag/gestionpointsetudiants/
├── 📁 database/     # Configuration Room
├── 📁 model/        # Entités (ClassEntity, Student, Course, etc.)
├── 📁 DAO/          # Data Access Objects
├── 📁 fragment/     # Interfaces utilisateur
├── 📁 utils/        # Utilitaires (calculs, formatage)
└── MainActivity.java
```

### Base de données
```sql
-- Schéma principal
Classes (name PK) → Students (className FK) 
                 → Courses (className FK) 
                 → Evaluations → Notes
```

## 🔧 Fonctionnalités techniques

### Gestion des notes
- **Précision** : Sauvegarde au centième (0,01), affichage au demi-point (0,5)
- **Validation** : Contrôle des plages et formats
- **Calcul** : Moyennes pondérées automatiques
- **Hiérarchie** : Support des sous-évaluations

### Interface utilisateur
- **Thème sombre** : Contraste optimisé, pas de blanc sur blanc
- **Navigation** : Retour système géré correctement
- **Responsive** : Adaptation aux différentes tailles d'écran
- **Validation** : Feedback utilisateur en temps réel

## 🤝 Contribution

### Roadmap
- [ ] Finaliser l'interface des notes forcées
- [ ] Implémenter le polymorphisme pour les évaluations  
- [ ] Ajouter les tests unitaires
- [ ] Export/Import des données
- [ ] Mode clair/sombre commutable

### Développement
```bash
# Créer une branche feature
git checkout -b feature/nom-fonctionnalite

# Commiter les changes
git commit -m "feat: description de la fonctionnalité"

# Proposer une Pull Request
git push origin feature/nom-fonctionnalite
```

## 📋 Cahier des charges

Ce projet répond à un énoncé académique précis pour une application de gestion de points étudiants. Consultez le fichier [`GestionPointsEtudiants/README.md`](./GestionPointsEtudiants/README.md) pour le détail complet des exigences et leur statut d'implémentation.

## 📄 Licence

Ce projet est développé dans un cadre éducatif.

## 👨‍💻 Auteur

**Projet Académique** - Gestion Points Étudiants  
Développement Android en Java/Kotlin avec Architecture Components

---

⭐ **N'hésitez pas à star le projet si vous le trouvez utile !**