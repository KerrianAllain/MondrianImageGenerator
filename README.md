# Art Abstrait et Arbres (ASD3)

**Auteurs:** Kerrian et Ethann ALLAIN

---

## Contenu du dépôt

- `src/` : sources Java du projet.
- `tests/*.java` : tests utilitaires du projet.
- `tests/inputs/` : fichiers texte d'entrée fournies.
- `tests/expected/` : fichiers images de sortie attendues.

## Instructions d'utilisation du projet

Pour utiliser le projet, il faut se situer dans le répertoire **parent** du projet.

La compilation s'effectue à l'aide de la commande suivante :

``` sh
javac -source 1.8 -target 1.8 -d AllainAllain/bin AllainAllain/src/*.java
```

### Stratégie proposée

L'exécution de la stratégie **proposée** s'effectue de la manière suivante :

``` sh
java -classpath AllainAllain/bin Main 1 [fichier.txt] [largeur] [hauteur] [nbFeuillesADiviser] [minDimensionCoupe] [largeurLigne] [variante]
```

### Stratégie personnalisée 1

L'exécution de la première stratégie **personnalisée** s'effectue de la manière suivante :

``` sh
java -classpath AllainAllain/bin Main 2 ([lignesDansLeFichier] [largeur] [hauteur] [nbFeuillesADiviser] [minDimensionCoupe] [largeurLigne] [variante])
```

Cette stratégie reprend celle proposée, mais cette fois-ci avec des valeurs aléatoires.
Aucun argument n'est nécessaire, cela repose alors intégralement sur l'aléatoire.

### Stratégie personnalisée 2

L'exécution de la seconde stratégie **personnalisée** s'effectue de la manière suivante :
``` bash
java -classpath AllainAllain/bin Main 3 ([lignesDansLeFichier] [largeur] [hauteur] [nbFeuillesADiviser] [minDimensionCoupe] [largeurLigne] [variante])
```

Cette stratégie utilise un RQuadtree pour modéliser les divisions et la génération d'image.
Même principe que la stratégie personnalisée 1, aucun argument n'est nécessaire.

## Détails d'implémentations

La décision des feuilles à utiliser pour la découpe s'effectue à l'aide d'un **tas binaire** (`binary heap`), afin de garantir des opérations d'insertion et de suppression en **`O(log(n))`**.

L'implémentation d'un RQuadtree s'est vue justifiée dans l'idée de **densifier le nombre de découpes** à chaque étape, et afin de **manipuler une structure de données** non encore travaillée.

Tout fichier ou paramètre donné en argument à la fonction Main se voit **vérifié** pour assurer la validité de l'exécution du projet.
