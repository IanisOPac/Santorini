package Modele;

import java.awt.*;
import java.util.ArrayList;

import static Utile.Constante.*;

/**
 * Classe gérant les cases de la grille dont les constructions de bâtiment et la pose de batisseurs.
 */
public class Plateau {
    private int[][] cases;
    private final int colonnes;
    private final int lignes;

    /**
     * Instantie un objet Plateau.
     */
    public Plateau() {
        cases = new int[PLATEAU_LIGNES][PLATEAU_COLONNES];
        lignes = PLATEAU_LIGNES;
        colonnes = PLATEAU_COLONNES;
    }

    /**
     * Instantie un plateau depuis une copie d'un plateau.
     *
     * @param plateau classe plateau comprenant les informations de la grille de jeu.
     */
    public Plateau(Plateau plateau) {
        lignes = plateau.lignes;
        colonnes = plateau.colonnes;

        cases = new int[lignes][colonnes];
        for (int i = 0; i < lignes; i++) {
            System.arraycopy(plateau.cases[i], 0, cases[i], 0, colonnes);
        }
    }

    /**
     * Vérifie que la case (c, l) est vide.
     *
     * @param position la position sur la grille
     * @return vrai si la case de la grille est vide
     */
    public boolean estVide(Point position) {
        return getTypeBatiments(position) == VIDE;
    }

    /**
     * Vérifie que la case (c, l) est un bâtiment de hauteur 1.
     *
     * @param position la position sur la grille
     * @return vrai si la case de la grille est un rez-de-chaussée (bâtiment de hauteur 1)
     */
    public boolean estRDC(Point position) {
        return getTypeBatiments(position) == RDC;
    }

    /**
     * Vérifie que la case (c, l) est un bâtiment de hauteur 2.
     *
     * @param position la position sur la grille
     * @return vrai si la case de la grille est un étage (bâtiment de hauteur 2)
     */
    public boolean estEtage(Point position) {
        return getTypeBatiments(position) == ETAGE;
    }

    /**
     * Vérifie que la case (c, l) est un bâtiment de hauteur 3.
     *
     * @param position la position sur la grille
     * @return boolean si la case de la grille est un toit (bâtiment de hauteur 3)
     */
    public boolean estToit(Point position) {
        return getTypeBatiments(position) == TOIT;
    }

    /**
     * Vérifie que la case (c, l) est un bâtiment de hauteur 4 (une coupole).
     *
     * @param position la position sur la grille
     * @return boolean si la case de la grille est une coupole (bâtiment de hauteur 4)
     */
    public boolean estCoupole(Point position) {
        return getTypeBatiments(position) == COUPOLE;
    }

    /**
     * Ajoute un rez-de-chaussée (bâtiment de hauteur 1) sur une case de la grille.
     *
     * @param position la position sur la grille
     */
    public void ajouterRDC(Point position) {
        setCase(position, getTypeBatisseurs(position) | RDC);
    }

    /**
     * Ajoute un étage (bâtiment de hauteur 2) sur une case de la grille.
     *
     * @param position la position sur la grille
     */
    public void ajouterEtage(Point position) {
        setCase(position, getTypeBatisseurs(position) | ETAGE);
    }

    /**
     * Ajoute un toit (bâtiment de hauteur 3) sur une case de la grille.
     *
     * @param position la position sur la grille
     */
    public void ajouterToit(Point position) {
        setCase(position, getTypeBatisseurs(position) | TOIT);
    }

    /**
     * Ajoute une coupole (bâtiment de hauteur 4) sur une case de la grille.
     *
     * @param position la position sur la grille
     */
    public void ajouterCoupole(Point position) {
        setCase(position, getTypeBatisseurs(position) | COUPOLE);
    }

    /**
     * Ameliore le batiment à la position l c si c'est possible, considère qu'il n'y pas de batisseurs dessus.
     *
     * @param position la position sur la grille
     */
    public void ameliorerBatiment(Point position) {
        setCase(position, getCase(position) + 1);
    }

    /**
     * Dé-améliore le batiment à la position l c si c'est possible, considère qu'il n'y pas de batisseurs dessus (un TOIT devient un ETAGE).
     *
     * @param position la position sur la grille
     */
    public void degraderBatiment(Point position) {
        setCase(position, getCase(position) - 1);
    }

    /**
     * Vérifie que l'on peut sélectionner le batisseur aux coordonnées (position) appartient au joueur.
     *
     * @param position la position sur la grille
     * @param joueur   le joueur (joueur en cours)
     */
    public boolean estBatisseur(Point position, Joueur joueur) {
        return getTypeBatisseurs(position) == joueur.getNum_joueur();
    }

    /**
     * Vérifie que l'on peut sélectionner le batisseur aux coordonnées (position) appartient au joueur.
     *
     * @param position  la position sur la grille
     * @param numJoueur le numéro de joueur (joueur en cours)
     */
    public boolean estBatisseur(Point position, int numJoueur) {
        return getTypeBatisseurs(position) == numJoueur;
    }

    /**
     * Vérifie que la case de la grille ne contient pas de joueur.
     *
     * @param position la position sur la grille
     * @return vrai si la case ne contient pas de joueur
     */
    public boolean estLibre(Point position) {
        return getTypeBatisseurs(position) == 0;
    }

    /**
     * Vérifie que la case de la grille visé peut acceuillir le batisseur (deplacement vers le haut de uniquement 1 de hauteur).
     *
     * @param position  la position sur la grille
     * @param batisseur position (x, y) d'un batisseur
     * @return si la case peut acceuillir un batisseur
     */
    public boolean deplacementPossible(Point position, Point batisseur) {
        return estLibre(position) && atteignable(position, batisseur) &&
                getTypeBatiments(position) - getTypeBatiments(batisseur) <= 1 && !estCoupole(position);
    }

    /**
     * Ajoute un joueur sur la case de la grille.
     *
     * @param position la position sur la grille
     * @param joueur   le numéro de joueur (joueur en cours)
     */
    public void ajouterJoueur(Point position, Joueur joueur) {
        setCase(position, (getTypeBatiments(position) | joueur.getNum_joueur()));
    }

    public void ajouterJoueur(Point position, int numJoueur) {
        setCase(position, (getTypeBatiments(position) | numJoueur));
    }

    /**
     * Vérifie que la distance entre le batisseur et le case cliqué est inférieur à 2 (en x et en y)
     *
     * @param position  la position sur la grille
     * @param batisseur position (x, y) d'un batisseur
     * @return vrai s'il est possible d'atteindre la case
     */
    public boolean atteignable(Point position, Point batisseur) {
        int a = Math.abs(position.x - batisseur.x);
        int b = Math.abs(position.y - batisseur.y);
        return (a + b > 0) && (a < 2) && (b < 2);
    }

    /**
     * Construit une liste de cases accessible depuis la position d'un batisseur.
     *
     * @param batisseur position (x, y) d'un batisseur
     * @return un ensemble de case accessible
     */
    public ArrayList<Point> getCasesAccessibles(Point batisseur) {

        Point cases;
        ArrayList<Point> cases_voisines = new ArrayList<>();
        int l = batisseur.x;
        int c = batisseur.y;

        for (int i = -1; i < 2; i++) {
            if(i+l < lignes && i+l>=0){
                for (int j = -1; j < 2; j++) {
                    cases = new Point(i+l, j+c);
                    if(j+c<colonnes && j+c>= 0 && deplacementPossible(cases, batisseur)){
                        cases_voisines.add(cases);
                    }
                }
            }
        }

        return cases_voisines;
    }

    /**
     * Construit une liste de case où une construction peut-être faite depuis la position d'un batisseur.
     *
     * @param batisseur position (x, y) d'un batisseur
     * @return une ensemble de case où la construction est possible
     */
    public ArrayList<Point> getConstructionsPossible(Point batisseur) {

        Point cases;
        ArrayList<Point> cases_acessibles = new ArrayList<>();
        int l = batisseur.x;
        int c = batisseur.y;

        for (int i = -1; i < 2; i++) {
            if(i+l < lignes && i+l>=0){
                for (int j = -1; j < 2; j++) {
                    cases = new Point(i+l, j+c);
                    if(j+c<colonnes && j+c>= 0 && peutConstruire(cases, batisseur)){
                        cases_acessibles.add(cases);
                    }
                }
            }
        }
        return cases_acessibles;
    }

    /**
     * Construit une liste de toutes les cases voisines à un batisseur.
     *
     * @param batisseur position (x, y) du batisseur
     * @return une ensemble de case de case voisine au batisseur
     */
    public ArrayList<Point> getCasesVoisines(Point batisseur) {
        Point cases;
        ArrayList<Point> cases_voisines = new ArrayList<>();
        int l = batisseur.x;
        int c = batisseur.y;

        for (int i = -1; i < 2; i++) {
            if(i+l < lignes && i+l>=0){
                for (int j = -1; j < 2; j++) {
                    cases = new Point(i+l, j+c);
                    if(j+c<colonnes && j+c>= 0){
                        cases_voisines.add(cases);
                    }
                }
            }
        }

        cases_voisines.remove(batisseur);

        return cases_voisines;
    }

    /**
     * Vérifie si un ouvrier peut construire sur la case de la grille en l et c
     *
     * @param position  la case (x, y) de la grille
     * @param batisseur position (x, y) d'un batisseur
     * @return vrai si le batisseur peut construire ici.
     */
    public boolean peutConstruire(Point position, Point batisseur) {
        return (atteignable(position, batisseur) && !estCoupole(position) && estLibre(position));
    }

    /**
     * Retire le joueur sur la case "position"
     *
     * @param position la case (x, y) de la grille
     */
    public void enleverJoueur(Point position) {
        setCase(position, getTypeBatiments(position));
    }

    /**
     * Met la case (x, y) à la valeur d'étage "value"
     *
     * @param position la case (x, y) de la grille
     * @param value    la nouvelle valeur d'étage
     */
    public void MAJEtage(Point position, int value) {
        setCase(position, getCase(position) + value);
    }

    /**
     * Remet à zéro le plateau.
     */
    public void RAZ() {
        cases = new int[PLATEAU_LIGNES][PLATEAU_COLONNES];
    }

    // GETTER / SETTER

    private void setCase(Point position, int valeur) {
        cases[position.x][position.y] = valeur;
    }

    public void setBatiments(Point position, int valeur) {
        cases[position.x][position.y] = getTypeBatisseurs(position)+valeur;
    }

    public int getLignes() {
        return lignes;
    }

    public int getColonnes() {
        return colonnes;
    }

    public int getCase(Point position) {
        return cases[position.x][position.y];
    }

    public int getTypeBatiments(Point position) {
        return getCase(position) & 7;
    }

    public int getTypeBatisseurs(Point position) {
        return getCase(position) & (~7);
    }

    // OVERRIDE MÉTHODES HÉRITÉES

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < PLATEAU_LIGNES; i++) {
            for (int j = 0; j < PLATEAU_COLONNES; j++) {
                Point pos = new Point(i, j);
                s.append("| ").append(getTypeBatisseurs(pos) / JOUEUR1).append(" : ").append(getTypeBatiments(pos)).append(" ");
            }
            s.append("\n");
        }
        return s.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Plateau)) return false;

        Plateau pl = (Plateau) o;
        for (int i = 0; i < PLATEAU_LIGNES; i++) {
            for (int j = 0; j < PLATEAU_COLONNES; j++) {
                Point p = new Point(i, j);
                if (this.getCase(p) != pl.getCase(p)) return false;
            }
        }

        return true;
    }
}