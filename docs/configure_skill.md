# Configure an existing skill for 6.2+


I will explain how to properly modify an existing skill.

## Fonctionnement
Dans Tyz's skills, les skills sont chargés a apartir de fichiers json dans le dossier `GAMEDIRECTORY/config/tyzs_skills/skills`.

Dans ce dossier vous pourrez trouver :
- `default` : Il s'agit de tout les skills générés par defaut par le mod et les addons, ce dossier est recréé a chaque partie, il sert de reférence générale. Il est inutile de modifer les jsons ici, ils pourraient ne pas être pris en compte et etre écrasés.

- `custom` : Il s'agit de tout les skills qui sont modifiés ou custom. Il agit comme un overwritte du dossier `default`, c'est a dire que si un skill possède le meme id qu'un skill dans le dossier `default`, il sera chargé a sa place.

- `legacy_backup_6.1`: Il est possible que vous ayez un ce dossier, il contient tout les skills des versions **6.0.0 - 6.1.2** qui ne sont plus compatibles. Vous pouvez le supprimer ou vous en servir comme reférence car il contient toute les anciennes mocifcations potentiellement faites.

Ducoup pour modifier un skill existant, il faut simplement copier le fichier original (de `default` vers `custom`) et appliquer les modifications sur la copie !


## Configuration

Nous allons maintenant voir comment modifier les données d'un skill proprement pour eviter des bugs ou des comportements bizzares.

**🛑 Si l'id ne correspond pas a un skill dans le dossier default, l'overwittre ne sera pas appliqué**

**🛑 Ne modifiez pas les propriétés qui ne sont pas expliquées en dessous, ça pourrait corrompre vos données et rendre les skills non fonctionnels**

__Propriétés configurables__ :

|Propriété|Valeures possibles|Description|Notes|
|:---|:---:|:---|:---|
|`active`| true, false | Défiinit si le skill est chargé | C'est le seul moyen de désactiver un skill |
|`maximumLevel`| Valeures entre 1 et 99 | Determine le niveau maximal du skill | |
|`prices`| Valeures entieres positives | Prix pour chaque niveau (1ere valeure = prix pour acheter le niveau ) | La taille de la liste doit correspondre au `maximumLevel` |
|`category`| ABILITIES, FIGHT, MISC | determines l'onglet dans lequel le skill apparait | |
|`purchasable`| true, false | définit si les joueurs peuvent achetr ou rembouser le skill | Si c'est définit sur true, le seul moyen d'acheter/rembourser sera via commandes |
|`icon`| chemin valide vers un .png (16x16) | Determine le chemin de l'icone du skill | Doit suivre le format d'assets minecraft et doit provenir d'un ressource pack ou d'un mod chargé|
|`displayName`| clé de traduction | Determines le nom d'affichage du skill | |
|`description`| clé de traduction | Determines la description a afficher | |






