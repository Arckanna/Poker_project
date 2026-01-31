# Poker_project

Projet Java de simulation d’un jeu de poker (5 cartes par joueur). Le programme constitue un jeu de 52 cartes, distribue 5 cartes à chaque joueur, évalue les mains et désigne le ou les gagnants.

## Prérequis

- **Java 17** ou supérieur

## Compilation et exécution

### Compilation

```bash
javac -d out -encoding UTF-8 -sourcepath src src/module-info.java src/com/ivray/poker/App.java src/com/ivray/poker/business/*.java src/com/ivray/poker/util/*.java
```

Sous Windows (PowerShell), en listant les fichiers explicitement :

```powershell
javac -d out -encoding UTF-8 -sourcepath src src/module-info.java src/com/ivray/poker/App.java src/com/ivray/poker/business/Card.java src/com/ivray/poker/business/Color.java src/com/ivray/poker/business/HandRank.java src/com/ivray/poker/business/Player.java src/com/ivray/poker/business/Town.java src/com/ivray/poker/util/CardComparatorOnValue.java
```

### Exécution

```bash
java -p out -m Poker_project/com.ivray.poker.App
```

## Fonctionnalités

- **Jeu de 52 cartes** : 4 couleurs (cœur, pique, carreau, trèfle), valeurs 2 à 14 (As).
- **Joueurs** : 3 joueurs par défaut (Capucine, Louise, Maman), avec main de 5 cartes et balance initiale.
- **Évaluation des mains** : reconnaissance de toutes les combinaisons classiques, dans l’ordre de force suivant :

| Rang | Combinaison   | Description                          |
|------|---------------|--------------------------------------|
| 10   | Quinte royale | 10, J, Q, K, A de la même couleur    |
| 9    | Quinte flush  | 5 cartes consécutives, même couleur |
| 8    | Carré         | 4 cartes de même valeur             |
| 7    | Full          | Brelan + paire                       |
| 6    | Flush         | 5 cartes de la même couleur          |
| 5    | Quinte        | 5 valeurs consécutives (dont A-2-3-4-5) |
| 4    | Brelan        | 3 cartes de même valeur             |
| 3    | Deux paires   | 2 paires                             |
| 2    | Paire         | 2 cartes de même valeur             |
| 1    | Rien          | Carte haute                         |

- **Comparaison des mains** : utilisation de `HandRank.getForce()` pour comparer les combinaisons ; en cas d’égalité, départage par les valeurs des cartes (triées de la plus forte à la plus faible).
- **Gagnant(s)** : affichage du ou des gagnants en fin de main, avec gestion des ex æquo.

## Structure du projet

```
src/
├── module-info.java          # Module Poker_project
└── com/ivray/poker/
    ├── App.java              # Point d’entrée, distribution, analyse et comparaison des mains
    ├── business/
    │   ├── Card.java         # Carte (valeur, couleur)
    │   ├── Color.java        # Couleur (cœur, pique, carreau, trèfle)
    │   ├── HandRank.java     # Enum des combinaisons et de leur force
    │   ├── Player.java       # Joueur (pseudo, main, balance)
    │   └── Town.java        # Ville (optionnel, profil joueur)
    └── util/
        └── CardComparatorOnValue.java   # Tri des cartes par valeur
```

## Exemple de sortie

```
Capucine id:1, ... handCards: [3 of diamond, 10 of heart, ...]
{3=2, 6=1, 8=1, 10=1}
Paire
Louise id:2, ... handCards: [7 of club, 3 of club, 7 of heart, ...]
{3=1, 7=2, 12=1, 13=1}
Paire
...
--- Gagnant(s) ---
Louise remporte la main avec Paire.
```

## Pistes d’évolution

- Introduire un objet **Deck** (paquet) dédié.
- Ajouter un **pot** et des **tours de mise** (check, bet, call, raise, fold).
- Adapter au **Texas Hold’em** (2 cartes par joueur + 5 cartes communes).
- Interface graphique ou menu console pour enchaîner plusieurs mains.
