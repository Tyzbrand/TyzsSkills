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
|`icon`| Chemin valide vers un asset (16x16) | Determine le chemin de l'icone du skill | Doit suivre le format d'assets minecraft et doit provenir d'un ressource pack ou d'un mod chargé|
|`displayName`| Clé de traduction | Determines le nom d'affichage du skill | |
|`description`| Clé de traduction | Determines la description a afficher | |
|`modifers`| [modifier](#modifier), vide | Determines les données relatives aux attributs affectés par le skill | Dans le cas d'un skill de type `IMMUTABLE` ou `TRAIT`, ce champ peux rester vide |
|`customValues`| [custom value](#custom-values), vide |  Determines les données relatives aux valeures du skill | Dans le cas d'un skill qui n'est pas de type `IMMUTABLE`, ce champ peux rester vide|



### MODIFIER
Un modifier est un objet qu'un skill peux posséder seulement si il est de type `GENERIC` ou `CUSTOM`. Il contient toute les informations relatives a la modfication d'un attribut. Un skill peux avoir plusieurs modfiers sur différents attributs.

un modifer est composé des propriétés suivantes :

`attribute` 
- This is the player attribute you are going to modify [Full vanilla attribute list](https://minecraft.wiki/w/Attribute).
- It can come from another mod.
- e.g. **"minecraft:generic.movement_speed"**

**🛑 If the modifier is missing, the skill will not be loaded.**

**🛑 If the modifier is incorrect, the skill will have no effect in game.**


`operation`
- This is how the skill is going to affect the attribute
- It can be :
  - **ADD_VALUE** -> adds all of the modifiers' amounts to the base attribute (ex: a value of 1.0 adds +1)
  - **ADD_MULTIPLIED_BASE** (treated as a percentage) -> multiplies the base attribute by (1 + value / 100) (ex: a value of 10 adds +10%)
  - **ADD_MULTIPLIED_TOTAL** (treated as a percentage) -> multiplies the attribute value with all existing modifers  by (1 + value / 100) (ex: a value of 10 adds +10%)
  - [More details](https://minecraft.wiki/w/Attribute)
 

`values`
- Il s'agit de la liste des valeures du modifer (1ere valeure = valeure de l'attribut au niveau 1 du skill).
- Les valeures peuvent etre négatives ou positives
- il faut que la taille de la liste corresponde au niveau maximal.


`unit`
- Il s'agit d'une clé de traduction pour l'unité a afficher dans le tootlip d'achat/remboursement.
- Ce champ peux etre vide et aucune unité ne sera affichée.



### CUSTOM VALUES
Les valeurs custom est aussi un objet qu'un skill peux posséder, seulement si il est de type `IMMUTABLE`. Il contient toute les informations relatives au valeurs que le skill utilise dans le gameplay.

**🛑 Vous n'aurez besoin de toucher que a la liste de valeures. Si vous modifier la clé de valeur, le skill ne fonctionnera plus. Ne changez la clé que si vous etes surs de ce que vous faites.**

Custom values est composé des propriétés suivantes :

`values`
- Il s'agit de la liste des valeures que le skill traite (1ere valeure = valeure effective au niveau 1 du skill).
- Les valeures peuvent etre négatives ou positives
- il faut que la taille de la liste corresponde au niveau maximal.


`unit`
- Il s'agit d'une clé de traduction pour l'unité a afficher dans le tootlip d'achat/remboursement.
- Ce champ peux etre vide et aucune unité ne sera affichée.







