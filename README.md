# Poker_project

Projet Java de simulation d’un jeu de poker (5 cartes par joueur). **Un des joueurs est l’utilisateur** : il voit sa main et peut choisir ses actions (check, miser, suivre, se coucher). Les autres joueurs sont des IA qui répondent au tour de mise. Chaque joueur paie une **ante** (mise fixe) au début ; après un **tour de mise** (humain en premier, puis les IA), le **showdown** désigne le ou les gagnants si personne ne s’est couché.

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

## Déroulement d’une main

1. **Ante** : chaque joueur paie une mise fixe (2 jetons par défaut) ; le pot est constitué de ces antes.
2. **Distribution** : 5 cartes à chaque joueur.
3. **Tour de mise** : le joueur humain (« Vous ») agit en premier, puis chaque IA à son tour.
   - Si personne n’a encore misé : **Check (c)** ou **Miser (m)** + montant.
   - Si quelqu’un a misé : **Suivre (s)** pour égaliser la mise, ou **Se coucher (f)** pour abandonner.
4. **Showdown** : si au moins deux joueurs sont encore en jeu, on compare les mains ; le ou les gagnants se partagent le pot. Si un seul joueur reste (les autres se sont couchés), il remporte le pot sans montrer sa main.

## Fonctionnalités

- **Jeu de 52 cartes** : 4 couleurs (cœur, pique, carreau, trèfle), valeurs 2 à 14 (As).
- **Joueurs** : 1 joueur humain (« Vous ») + 2 IA (Louise, Maman), chacun avec 5 cartes et une balance initiale (50 jetons).
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
- **Gagnant(s)** : affichage du ou des gagnants en fin de main, avec gestion des ex æquo. Le pot est crédité sur la (les) balance(s) du (des) gagnant(s).

## Structure du projet

```
src/
├── module-info.java          # Module Poker_project
└── com/ivray/poker/
    ├── App.java              # Point d’entrée : ante, distribution, tour de mise (humain + IA), showdown
    ├── business/
    │   ├── Card.java         # Carte (valeur, couleur)
    │   ├── Color.java        # Couleur (cœur, pique, carreau, trèfle)
    │   ├── HandRank.java     # Enum des combinaisons et de leur force
    │   ├── Player.java       # Joueur (pseudo, main, balance, humain ou IA)
    │   └── Town.java        # Ville (optionnel, profil joueur)
    └── util/
        └── CardComparatorOnValue.java   # Tri des cartes par valeur
```

## Exemple de sortie

```
--- Ante --- Chaque joueur paie 2. Pot = 6.0
--- Votre main --- Vous | [3 of diamond, 6 of heart, ...]
Combinaison : Paire
Pot : 6.0 | Votre stack : 48.0
Check (c) ou Miser (m) ? m
Montant à miser (min 2, max 48) ? 4
Vous mise 4. Pot = 10.0
Louise suit (4.0). Pot = 14.0
Maman se couche.
...
--- Showdown ---
Vous : [...] → Paire
Louise : [...] → Rien
--- Gagnant(s) ---
Vous remporte la main avec Paire.
Pot remporté : 14.0
```

## Pistes d’évolution

- Introduire un objet **Deck** (paquet) dédié.
- Relancer (raise) en plus de miser / suivre / se coucher.
- Adapter au **Texas Hold’em** (2 cartes par joueur + 5 cartes communes).
- Boucle de parties : enchaîner plusieurs mains jusqu’à ce qu’un joueur n’ait plus de jetons.
- Interface graphique ou menu console.
