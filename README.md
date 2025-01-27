Optimisation de Commandes
Une entreprise souhaite optimiser sa gestion des stocks en automatisant la recommandation des quantités à commander pour un produit donné, en fonction des ventes régulières quotidiennes, du jour de commande spécifié, d'un délai de livraison fixe, et en respectant un multiple de commande.
Fonctionnalités attendues :
1. Gestion des données :
   ○ Historique des ventes : Prévoir pour cet exercice par défaut, des ventes régulières de 5 unités par jour du lundi au vendredi, et de 10 unités par jour le samedi et le dimanche.
   ○ Jour de commande : lundi
   ○ Délai de livraison : Délai fixe, initialisé à 3 jours pour que les commandes soient livrées.
2. Calcul de la quantité optimale à commander :
   ○ Dates et quantités commandées pour les commandes de l’année en partant du lundi 6 janvier 2025 en évitant les ruptures de stock et le surstock. On considère que le stock de départ est de 20 unités.
3. L’interface utilisateur permet de modifier le délai de livraison, le profil hebdomadaire des ventes et déclencher le calcul.
   ○ Afficher dans une table le résultat du calcul (dates et quantités commandées)
   ○ Afficher la courbe des stocks (stock mini, stock maxi et stock moyen par mois)
4. Recherche d'un multiple alternatif :
   ○ Évaluer s'il existe un autre multiple plus approprié que 12 mais supérieur à 5 pour diminuer le stock moyen tout en garantissant l'absence de rupture de stock.
