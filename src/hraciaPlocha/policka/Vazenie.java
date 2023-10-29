package hraciaPlocha.policka;

import hraci.Banka;
import hraci.Hrac;
import zaklad.Spravca;
import karty.BonusoveKarty;
import karty.Priepustka;
import javax.swing.JOptionPane;
import java.util.List;

public class Vazenie implements Policko {

    @Override
    public String getNazov() {
        return "VAZENIE";
    }

    /**
     * Ak je hrac vazen ma na vyber ako sa dostat z vazenia, ma moznosti: Zaplatenie poplatku, pouzitie karty Priepustka alebo hodenie dvojice (dve take iste cisla na oboch kockach)
     * @param hrac Hrac ktory vstupil na policko
     * @param spravca Spravca hry
     */
    @Override
    public void akcia(Hrac hrac, Spravca spravca) {
        if (!hrac.isVazen()) {
            return;
        }
        int cenaUtekuZVazenia = 50;
        do {
            // JOptionPane showOptionDialog som ssa inspiroval zo stranky nizisie
            // https://mkyong.com/swing/java-swing-joptionpane-showoptiondialog-example/
            String[] moznosti = {"Zaplatit " + cenaUtekuZVazenia + "$", "Hodit kockami", "Pouzit kartu"};
            int moznost = JOptionPane.showOptionDialog(null, "Vyberete moznost o pokusenie sa opustenie vazenia",
                    hrac.getMeno() + " ste vo vazeni, vyberte moznost na opustenie vazenia",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, moznosti, moznosti[0]);
            switch (moznost) {
                // 50$
                case 0 -> {
                    if (hrac.getFinancie() >= cenaUtekuZVazenia) {
                        hrac.zmenFinancie(-cenaUtekuZVazenia);
                        Banka.pridajPoplatok(cenaUtekuZVazenia);
                        hrac.setVazen(false, spravca);
                        spravca.aktualizujGrafiku();
                        return;
                    } else {
                        JOptionPane.showMessageDialog(null, "Nedisponujete sumou " + cenaUtekuZVazenia + "$ potrebnej na utek", "Zla volba", JOptionPane.ERROR_MESSAGE);
                    }
                }
                // max 3 pokusi a tie iste cisla
                case 1 -> {
                    for (int i = 0; i < 3; i++) {
                        int[] kocky = spravca.hodKockami();
                        spravca.aktualizujGrafiku();
                        if (kocky[0] == kocky[1]) {
                            hrac.setVazen(false, spravca);
                            break;
                        }
                    }
                    JOptionPane.showMessageDialog(null, "Nepodarilo sa ti hodit dvojice na kockach, menim hraca");
                    spravca.zmenHraca();
                    return;
                }
                // karta
                case 2 -> {
                    if (hrac.getBonusoveKarty().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Nedisponujete ziadnou kartou na prepustenie z vazenia", "Zla volba", JOptionPane.ERROR_MESSAGE);
                    } else {
                        List<BonusoveKarty> karty = hrac.getBonusoveKarty();
                        for (BonusoveKarty b : karty) {
                            if (b instanceof Priepustka) {
                                ((Priepustka)b).pouzitie();
                                hrac.setVazen(false, spravca);
                                hrac.zmazKartu(b);
                                return;
                            }
                        }
                        JOptionPane.showMessageDialog(null, "Nedisponujete ziadnou kartou na prepustenie z vazenia", "Zla volba", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } while (true);
    }
}