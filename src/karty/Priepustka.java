package karty;

import hraci.Hrac;
import zaklad.Spravca;
import javax.swing.JOptionPane;

public class Priepustka implements BonusoveKarty {
    private boolean pouzita;
    private final DruhKarty druhKarty;
    private Spravca spravca;

    /**
     * Nacitanie priepustky a urcenei druhu karty, ktory sa pouzije neksor na ulozenie naspat do balicka kariet na ploche
     * @param druhKarty Druh karty
     */
    public Priepustka(DruhKarty druhKarty) {
        this.pouzita = false;
        this.druhKarty = druhKarty;
    }

    /**
     * Metoda sa vola z vazenia a karta sa pouzije a zavola metoda vykonana akcia
     */
    public void pouzitie() {
        this.pouzita = true;
        this.vykonanaAkcia(null, this.spravca);
    }

    @Override
    public String getPopis() {
        return "Priepustka z vazenia";
    }

    /**
     * Ak je karta nepouzita, tak sa prida hracovy do inventara bonusovych kariet a moze ju potom pouzit
     * Inak sa prida karta naspat na plochu a vrati sa pouzitie na false
     * @param hrac Hrac ktory si vytiahol kartu
     * @param spravca Spravca hry
     */
    @Override
    public void vykonanaAkcia(Hrac hrac, Spravca spravca) {
        if (!this.pouzita) {
            hrac.pridajBonusovuKartu(this);
            this.spravca = spravca;
        } else {
            try {
                switch (this.druhKarty) {
                    case SANCA -> spravca.pridajNaPlochuSancu(this);
                    case TRUHLICA -> spravca.pridajNaPlochuTruhlicu(this);
                }
            } catch (NullPointerException e) {
                JOptionPane.showMessageDialog(null, "Doslo k cyhe, pravdepodobne ste kartu neziskali cestnym sposobom. Napriek tomu ste opustili vazenei a karta bola odstranena z inventara.\nNastala chyba " + e, "Chyba", JOptionPane.ERROR_MESSAGE);
            }
            this.pouzita = false;
        }
    }
}