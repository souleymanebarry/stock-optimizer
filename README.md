# Service d'Optimisation des Stocks

Ce projet est une application **Spring Boot Java 17** 
qui automatise la gestion des stocks en optimisant les recommandations de commande.  
Il utilise des règles métier et des contraintes pour éviter les surstocks et les ruptures de stock,
garantissant ainsi une gestion efficace des inventaires.

---

## Table des Matières
- [Aperçu](#aperçu)
- [Fonctionnalités](#fonctionnalités)
- [Prérequis Techniques](#prérequis-techniques)
- [Démarrage](#démarrage)
- [Structure du Projet](#structure-du-projet)
- [Documentation API](#documentation-api)
- [Contribuer](#contribuer)
- [Licence](#licence)

---

## Aperçu

Cette application permet aux entreprises d'optimiser leur gestion des stocks en :
- Calculant la quantité minimale à commander tout en respectant les multiples de commande.
- Évitant les commandes inutiles si le stock suffit pour la semaine.
- Générant des projections hebdomadaires de stocks pour anticiper les ruptures.
- Respectant des jours de commande fixes (par exemple, les lundis).

### Algorithme d'optimisation des stocks

1. Évalue les stocks jour par jour.
2. Projette les stocks pour la semaine à venir chaque lundi.
3. Passe des commandes uniquement si un risque de rupture est détecté.
4. Minimise les commandes pour rester au-dessus de zéro, arrondies au multiple de commande le plus proche.

---

## Fonctionnalités

- **Optimisation des Commandes** : Prévient les surstocks et automatise les calculs de commande pour une gestion efficace des stocks.
- **Projections Hebdomadaires des Stocks** : Évalue les besoins en stocks pour la semaine à venir.
- **Contraintes de Commande** : Prend en charge des multiples de commande configurables et des jours de commande fixes.
- **Rapports Statistiques** :
   - Statistiques mensuelles : niveaux minimum, maximum et moyen.
   - Recommandations de multiples de commande optimaux.
- **API REST** :
   - Récupération des statistiques mensuelles des stocks.
   - Calcul des plans de commande.
   - Recherche de multiples de commande optimaux.

---

## Prérequis Techniques

### Pile Technologique
- **Java** : 17
- **Spring Boot**
- **Maven** : 3.8+
- **H2** : Base de données en mémoire pour simplifier le développement.
- **OpenAPI** : Documentation API.

---

## Démarrage

### Prérequis
- Installer [Java 17](https://www.oracle.com/java/technologies/downloads/#java17).
- Installer [Maven 3.8+](https://maven.apache.org/download.cgi).

### Lancer l'Application

1. Clonez le dépôt :
   ```bash
   git clone https://github.com/souleymanebarry/stock-optimizer.git
   cd stock-optimizer
