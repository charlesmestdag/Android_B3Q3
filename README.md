# 📱 Gestion Points Étudiants

Application Android native pour la gestion des notes et évaluations d'étudiants par les enseignants.

## ✨ Fonctionnalités

- 🏫 **Gestion des classes** : Organisation par blocs (BA1, BA2, BA3, MA1, MA2)
- 👥 **Gestion des étudiants** : Matricules uniques avec association automatique aux cours
- 📚 **Gestion des cours** : Création et organisation par classe
- 📝 **Système d'évaluations** : Évaluations principales et sous-évaluations hiérarchiques
- 🔢 **Encodage des notes** : Saisie avec validation et arrondi automatique au 0,5
- 📊 **Calcul des moyennes** : Pondération automatique selon les points maximum
- 💾 **Stockage local** : Base de données SQLite avec Room

## 🏗️ Architecture MVVM

Le projet suit l'architecture **Model-View-ViewModel (MVVM)** avec les composants Android Architecture :

```
📁 app/src/main/java/com/mestdag/gestionpointsetudiants/
├── 📁 model/        # Entités Room (ClassEntity, Student, Course, etc.)
├── 📁 DAO/          # Data Access Objects
├── 📁 repository/   # Couche d'abstraction des données
├── 📁 viewmodel/    # ViewModels avec LiveData
├── 📁 fragment/     # Views (Fragments)
├── 📁 utils/        # Utilitaires (calculs, formatage)
└── MainActivity.java
```

### Technologies utilisées

- **Platform** : Android (minSdk 24, targetSdk 34)
- **Langage** : Java
- **Architecture** : MVVM avec Android Architecture Components
- **Base de données** : Room (SQLite)
- **UI** : Fragments + Navigation Component
- **Binding** : ViewBinding

## 🚀 Installation

### Prérequis
- Android Studio Arctic Fox ou plus récent
- JDK 11+
- Android SDK 24+

### Étapes
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

### Flux de travail

1. **Créer des classes** → Sélectionner parmi : BA1, BA2, BA3, MA1, MA2
2. **Ajouter des étudiants** → Saisir matricule (unique), nom, prénom
3. **Créer des cours** → Définir nom et description par classe
4. **Configurer les évaluations** → Créer évaluations principales et sous-évaluations
5. **Encoder les notes** → Saisie avec validation automatique
6. **Consulter les moyennes** → Calcul automatique avec pondération

## 🔧 Fonctionnalités techniques

- **Précision** : Sauvegarde au centième, affichage au demi-point
- **Validation** : Contrôle des plages et formats en temps réel
- **Calcul** : Moyennes pondérées automatiques
- **Hiérarchie** : Support des sous-évaluations
- **Thème sombre** : Interface optimisée pour le confort visuel

## 📋 Statut du projet

| Fonctionnalité | Statut |
|----------------|--------|
| Architecture MVVM | ✅ Terminé |
| Gestion Classes/Étudiants/Cours | ✅ Terminé |
| Système d'évaluations | ✅ Terminé |
| Encodage et calcul des notes | ✅ Terminé |
| Interface utilisateur | ✅ Terminé |
| Notes forcées | ✅ Terminé |


## 📄 Licence

Projet développé dans un cadre éducatif.

---
