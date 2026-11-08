# Art Abstrait et Arbres (ASD3)

**Auteurs:** Kerrian et Ethann ALLAIN

---

## Instructions d'utilisation du projet

Pour utiliser le projet, il faut se situer dans le répertoire **parent** du projet.

La compilation s'effectue à l'aide de la commande suivante :

``` bash
javac -source 1.8 -target 1.8 -d AllainAllain/bin AllainAllain/src/*.java
```

L'exécution de la stratégie **proposée** s'effectue de la manière suivante :

``` bash
java -classpath AllainAllain/bin Main 1 [fichier.txt] [largeur] [hauteur] [nbFeuillesADiviser] [minDimensionCoupe] [largeurLigne] [variante]
```

L'exécution de la stratégie **personnalisée** s'effectue de la manière suivante :

``` bash
java -classpath AllainAllain/bin Main 2 [fileLength] [largeur] [hauteur] [nbFeuillesADiviser] [minDimensionCoupe] [largeurLigne] [variante]
```
