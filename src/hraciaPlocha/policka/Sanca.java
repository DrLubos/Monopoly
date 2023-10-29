package hraciaPlocha.policka;

import hraci.Hrac;
import zaklad.Spravca;
import karty.Priepustka;
import javax.swing.JOptionPane;

public class Sanca implements Policko {

    @Override
    public String getNazov() {
        return "SANCA";
    }

    /**
     * Vytiahne to kartu sanca ktora je v poradi v spravcovy, informuje co to je za kartu a posle spravu na vykonanie akcie, ak to je karta Priepustka, tak akciu nevykonava, iba ju priradi do inventara
     * @param hrac Hrac ktory vstupil na policko
     * @param spravca Spravca hry
     */
    @Override
    public void akcia(Hrac hrac, Spravca spravca) {
        var karta = spravca.getPrvuKartuSanca();
        if (karta.isPresent()) {
            if (karta.get() instanceof Priepustka) {
                JOptionPane.showMessageDialog(null, "Hrac " + hrac.getMeno() + " dostal priepustku z vazenia", "Karta Sanca", JOptionPane.INFORMATION_MESSAGE);
                karta.get().vykonanaAkcia(hrac, spravca);
            } else {
                JOptionPane.showMessageDialog(null, "Hrac " + hrac.getMeno() + " si vytiahol kartu " + karta.get().getPopis(), "Karta Sanca", JOptionPane.INFORMATION_MESSAGE);
                karta.get().vykonanaAkcia(hrac, spravca);
                spravca.pridajNaPlochuSancu(karta.get());
            }
        }
    }
}