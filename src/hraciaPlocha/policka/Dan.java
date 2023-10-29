package hraciaPlocha.policka;

import hraci.Banka;
import hraci.Hrac;
import zaklad.Spravca;
import javax.swing.JOptionPane;

public class Dan implements Policko {
    private final String nazov;
    private final int cena;
    private final int percenta;

    /**
     * Inicializuju sa atributy
     * @param nazov Nazov policka
     * @param cena Cena dane
     * @param percenta Percenta pre dan
     */
    public Dan(String nazov, int cena, int percenta) {
        this.nazov = nazov;
        this.cena = cena;
        this.percenta = percenta;
    }

    @Override
    public String getNazov() {
        if (this.percenta < 2) {
            return this.nazov + "\n" + this.cena + "$";
        }
        return this.nazov + "\n" + this.cena + "$ alebo " + this.percenta + "%";
    }

    /**
     * Hracivy sa odpocitaju financie ked vstupi na policko, ak su percenta menej ako 2, tak je to automaticky podla parametra cena, inak ma hrac moznost vyberu ci chce percenta, ktore mu to aj vyrata, alebo ci chec stiahnut pevnu ciastku
     * @param hrac Hrac ktory vstupil na policko
     * @param spravca Spravca hry
     */
    @Override
    public void akcia(Hrac hrac, Spravca spravca) {
        if (this.percenta < 2) {
            hrac.zmenFinancie(-this.cena);
            Banka.pridajPoplatok(this.cena);
            return;
        }
        do {
            // JOptionPane showOptionDialog nspiroval som sa z tejto stranky
            // https://mkyong.com/swing/java-swing-joptionpane-showoptiondialog-example/
            String[] moznosti = {"Zaplatit " + this.cena + "$", "Zaplatit " + this.percenta + "% zo svojich financii = " + hrac.getFinancie() / 100 * this.percenta + "$"};
            int moznost = JOptionPane.showOptionDialog(null, "Vyberete moznost zaplatenia dane",
                    hrac.getMeno() + " zaplatte dan",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, moznosti, moznosti[0]);
            switch (moznost) {
                // cena
                case 0 -> {
                    hrac.zmenFinancie(-this.cena);
                    Banka.pridajPoplatok(this.cena);
                    return;
                }
                // percenta
                case 1 -> {
                    if (hrac.getFinancie() / 100 * this.percenta < 1) {
                        hrac.zmenFinancie(-1);
                        Banka.pridajPoplatok(1);
                    } else {
                        hrac.zmenFinancie(-hrac.getFinancie() / 100 * this.percenta);
                        Banka.pridajPoplatok(hrac.getFinancie() / 100 * this.percenta);
                    }
                    return;
                }
            }
        } while (true);
    }
}