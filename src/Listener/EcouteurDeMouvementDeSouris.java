package Listener;

import static Utile.Constante.*;

import Modele.Jeu;
import Modele.JoueurIA;
import Vue.JeuGraphique;
import Vue.Panels;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * Classe Listener qui gère les mouvements de souris pour définir le curseur.
 */
public class EcouteurDeMouvementDeSouris implements MouseMotionListener {

    JeuGraphique jg;
    Jeu jeu;
    Panels p;
    int largeur_plateau;
    int hauteur_plateau;
    Cursor c_pas_bleu;
    Cursor c_pas_rouge;
    Cursor c_pas_gris;
    Cursor c_outil_bleu;
    Cursor c_outil_rouge;
    Cursor c_outil_gris;
    Cursor c_drapeau_bleu;
    Cursor c_drapeau_rouge;
    Cursor c_drapeau_gris;
    Cursor c_defaut_gris;
    Cursor c_defaut_rouge;
    Cursor c_defaut_bleu;
    Cursor c_hand_rouge;
    Cursor c_hand_bleu;
    Cursor c_hand_gris;
    Cursor c_hourglass;

    int numJoueurBleu;

    static final Point CENTRE = new Point(16, 16);
    static final Point HAUT_GAUCHE = new Point(0, 0);

    /**
     * Constructeur qui génère un ensemble de curseur.
     */
    public EcouteurDeMouvementDeSouris(Jeu j, JeuGraphique jg, Panels p) {
        this.jg = jg;
        this.jeu = j;
        this.p = p;
        this.numJoueurBleu = (j.getConfigurationPartie().isJoueur1Bleu() ? JOUEUR1 : JOUEUR2);
        c_defaut_gris = creerCurseurGenerique("defaut_gris", HAUT_GAUCHE);
        c_defaut_rouge = creerCurseurGenerique("defaut_rouge", HAUT_GAUCHE);
        c_defaut_bleu = creerCurseurGenerique("defaut_bleu", HAUT_GAUCHE);
        c_pas_bleu = creerCurseurGenerique("pas_bleu", CENTRE);
        c_pas_rouge = creerCurseurGenerique("pas_rouge", CENTRE);
        c_pas_gris = creerCurseurGenerique("pas_gris", CENTRE);
        c_outil_bleu = creerCurseurGenerique("outil_bleu", HAUT_GAUCHE);
        c_outil_rouge = creerCurseurGenerique("outil_rouge", HAUT_GAUCHE);
        c_outil_gris = creerCurseurGenerique("outil_gris", HAUT_GAUCHE);
        c_drapeau_bleu = creerCurseurGenerique("drapeau_bleu", CENTRE);
        c_drapeau_rouge = creerCurseurGenerique("drapeau_rouge", CENTRE);
        c_drapeau_gris = creerCurseurGenerique("drapeau_gris", CENTRE);
        c_hand_rouge = creerCurseurGenerique("hand_rouge", CENTRE);
        c_hand_bleu = creerCurseurGenerique("hand_bleu", CENTRE);
        c_hand_gris = creerCurseurGenerique("hand_gris", CENTRE);
        c_hourglass = creerCurseurGenerique("hourglass", CENTRE);
    }

    /**
     * Crée un objet de type Cursor depuis son nom de fichier.
     *
     * @return le curseur crée
     */
    public static Cursor creerCurseurGenerique(String fichier_nom, Point decallage) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        try {
            Image img = toolkit.getImage(CHEMIN_RESSOURCE + "/curseur/" + fichier_nom + ".png");
            return toolkit.createCustomCursor(img.getScaledInstance(32, 32, Image.SCALE_SMOOTH), decallage, "c_" + fichier_nom);
        } catch (Exception ex) {
            System.err.println(ex);
        }
        return null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }


    /**
     * Définis le curseur selon sa position sur la grille et la situation du jeu.
     *
     * @see Jeu#getSituation()
     * @see Jeu#getJoueurEnCours()
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        this.largeur_plateau = jg.getTailleCase() * PLATEAU_COLONNES;
        this.hauteur_plateau = jg.getTailleCase() * PLATEAU_LIGNES;
        if (e.getX() <= largeur_plateau && e.getY() <= hauteur_plateau) {
            int pos_x = e.getX() / jg.getTailleCase();
            int pos_y = e.getY() / jg.getTailleCase();
            Point position = new Point(pos_y, pos_x);
            if (pos_x > 4 || pos_y > 4) {
                return;
            }

            if(jeu.getJoueurEnCours().getClass() == JoueurIA.class) {
                jg.setCursor(c_hourglass);
                return;
            }
            if (jeu.getNetUser() != null && jeu.getNetUser().getNumJoueur() != jeu.getJoueurEnCours().getNum_joueur()) {
                jg.setCursor(c_hourglass);
                return;
            }

                if(jeu.getSituation() == ATTENTE){
                    jg.setCursor(c_hourglass);
                }

                if (jeu.getSituation() == PLACEMENT) {

                    setCursor((jg.getJeu().estAtteignable(position)), c_hand_gris, c_hand_rouge, c_hand_bleu);
                    jg.setCase_sous_souris(new Point(pos_x, pos_y));
                    jg.repaint();
                }


            if (jeu.getSituation() == DEPLACEMENT) {

                if (jg.getJeu().estAtteignable(position)) {
                    setCursor(true, null, c_hand_rouge, c_hand_bleu);
                } else if (jg.getJeu().getPlateau().estBatisseur(position, jeu.getJoueurEnCours())) {
                    setCursor(true, null, c_defaut_rouge, c_defaut_bleu);
                } else {
                    jg.setCursor(c_defaut_gris);
                }

                jg.setCase_sous_souris(new Point(pos_x, pos_y));
                jg.repaint();
            }
            if (jeu.getSituation() == CONSTRUCTION) {

                setCursor((jg.getJeu().estAtteignable(position)), c_hand_gris, c_hand_rouge, c_hand_bleu);
                jg.setCase_sous_souris(new Point(pos_x, pos_y));
                jg.repaint();
            }
            if(jeu.getSituation() == SELECTION) {
                setCursor((jg.getJeu().getPlateau().estBatisseur(position, jeu.getJoueurEnCours())), c_defaut_gris, c_hand_rouge, c_hand_bleu);
            }
        }
    }

    private void setCursor(boolean condition, Cursor gris, Cursor rouge, Cursor bleu) {
        if(condition) {
            if (jeu.getJoueurEnCours().getNum_joueur() == numJoueurBleu) {
                jg.setCursor(bleu);
            } else {
                jg.setCursor(rouge);
            }
        } else {
            jg.setCursor(gris);
        }
    }



}
