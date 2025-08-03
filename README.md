# Cardinal Dial Android

## 📱 Vue d'ensemble

Application Android simulant une interface d'appel entrant avec service en arrière-plan persistant. L'application démontre la capacité d'afficher une interface d'appel par-dessus d'autres applications et l'écran de verrouillage.

## ✨ Fonctionnalités

### Interface d'appel
- **Simulation d'appel entrant** avec interface utilisateur moderne
- **Boutons d'action** : Accepter, Rejeter, Mettre en sourdine
- **Affichage par-dessus** l'écran de verrouillage et autres applications
- **Auto-fermeture** après 10 secondes

### Service en arrière-plan
- **Service de premier plan** persistant avec notification
- **Déclenchement automatique** de l'interface d'appel après 10 secondes
- **Fonctionnement en arrière-plan** même quand l'application est fermée
- **Wake Lock** pour maintenir l'activité du service

### Permissions et sécurité
- **SYSTEM_ALERT_WINDOW** : Affichage par-dessus d'autres applications
- **FOREGROUND_SERVICE_SPECIAL_USE** : Service de premier plan spécialisé
- **POST_NOTIFICATIONS** : Notifications système
- **WAKE_LOCK** : Maintien de l'activité en arrière-plan
- **Optimisation batterie** : Gestion automatique des restrictions

## 🏗️ Architecture

### Structure du projet
```
app/src/main/java/com/cardinaldial/app/
├── MainActivity.kt          # Activité principale
├── call/
│   ├── CallActivity.kt      # Interface d'appel
│   └── CallService.kt       # Service d'arrière-plan
└── res/
    ├── layout/
    │   ├── activity_main.xml
    │   └── activity_call.xml
    └── values/
        ├── strings.xml
        ├── colors.xml
        └── themes.xml
```

### Composants principaux

#### MainActivity
- Point d'entrée de l'application
- Gestion des permissions système
- Démarrage du service d'appel
- Interface utilisateur simple avec bouton de déclenchement

#### CallActivity
- Interface d'appel simulée
- Configuration des drapeaux de fenêtre pour affichage overlay
- Gestion différentielle selon le contexte de lancement
- Support écran de verrouillage et applications tierces

#### CallService
- Service de premier plan avec notification persistante
- Planification et déclenchement de l'interface d'appel
- Gestion du Wake Lock
- Redémarrage automatique (START_STICKY)

## 🚀 Installation et utilisation

### Prérequis
- Android Studio
- SDK Android 24+ (API niveau 24)
- Appareil Android ou émulateur

### Compilation
```bash
./gradlew assembleDebug
```

### Installation
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Utilisation
1. **Lancer l'application** et accorder les permissions demandées
2. **Appuyer sur "Start Call Service"** pour démarrer le service
3. **Minimiser ou fermer l'application**
4. **Attendre 10 secondes** - l'interface d'appel apparaîtra automatiquement
5. **Tester sur écran de verrouillage** en verrouillant l'appareil

## 🔧 Configuration

### Permissions requises
L'application demande automatiquement :
- **Affichage par-dessus d'autres applications** (SYSTEM_ALERT_WINDOW)
- **Désactivation de l'optimisation batterie** pour un fonctionnement optimal

### Paramètres modifiables
- **Délai de déclenchement** : Modifiable dans `CallService.kt` (actuellement 10 secondes)
- **Durée d'affichage** : Modifiable dans `CallActivity.kt` (actuellement 10 secondes)
- **Style de notification** : Personnalisable dans `CallService.kt`

## 📋 État actuel

### ✅ Fonctionnalités implémentées
- [x] Service de premier plan persistant
- [x] Interface d'appel moderne
- [x] Affichage par-dessus écran de verrouillage
- [x] Affichage par-dessus autres applications
- [x] Gestion automatique des permissions
- [x] Optimisation batterie
- [x] Wake Lock pour maintien d'activité
- [x] Notification de service persistante

### 🔄 Améliorations possibles
- [ ] Personnalisation de l'interface d'appel
- [ ] Intégration avec contacts système
- [ ] Historique des appels simulés
- [ ] Configuration utilisateur avancée
- [ ] Support multi-langues

## 🐛 Résolution de problèmes

### L'interface d'appel n'apparaît pas
1. Vérifier que la permission SYSTEM_ALERT_WINDOW est accordée
2. Désactiver l'optimisation batterie pour l'application
3. Vérifier que le service fonctionne via les paramètres Android

### Le service s'arrête
1. Désactiver l'optimisation batterie
2. Ajouter l'application aux applications protégées (selon le fabricant)
3. Vérifier les logs avec `adb logcat`

## 📝 Logs et débogage

Pour surveiller le fonctionnement :
```bash
adb logcat | grep "CallService\|CallActivity"
```