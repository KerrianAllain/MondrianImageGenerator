# Générateur de peintures abstraites

Le projet génère des images inspirées de Mondrian à partir d'arbres de découpe.
Le détail des méthodes et des exemples de résultats est présenté dans le
[rapport](rapport.md).

## Compilation et exécution

Depuis la racine du dépôt :

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

```sh
java -cp bin Main 1 tests/inputs/exemple1-200-50-24-9-3-A.txt 200 50 24 9 3 A
```

Arguments : `1 fichier largeur hauteur nbFeuilles minDimension largeurLigne variante`.

### Méthode 2 — génération aléatoire avec arbre binaire

Les paramètres peuvent être générés automatiquement :

```sh
java -cp bin Main 2
```

Ou précisés dans cet ordre : `2 longueur largeur hauteur nbFeuilles minDimension largeurLigne variante`.

### Méthode 3 — génération aléatoire avec RQuadtree

```sh
java -cp bin Main 3
```

Cette méthode accepte les mêmes paramètres optionnels que la méthode 2.

## Représentation textuelle réversible

`toString()` produit une représentation complète incluant les dimensions, les
divisions et les couleurs RGB des feuilles. La chaîne peut être passée telle
quelle au constructeur correspondant :

```java
Tree2D restoredBinaryTree = new Tree2D(binaryTree.toString());
RQuadtree restoredQuadtree = new RQuadtree(quadtree.toString());
```

Le format distingue les feuilles (`L(r,g,b)`) et les divisions (`D(...)`) ;
pour le RQuadtree, les quatre sous-arbres sont sérialisés dans l'ordre NW, NE,
SE, SW. Les constructeurs rejettent les représentations mal formées ou dont
les divisions sortent des limites.

## Tests

```sh
javac -d bin src/*.java tests/*.java
java -ea -cp bin Test_TreeSerialization
java -ea -cp bin Test_StrategySpecs
```
