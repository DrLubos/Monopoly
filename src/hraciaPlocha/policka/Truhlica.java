package hraciaPlocha.policka;

import hraci.Hrac;
import zaklad.Spravca;
import karty.Priepustka;
import javax.swing.JOptionPane;

public class Truhlica implements Policko {

    @Override
    public String getNazov() {
        return "TRUHLICA";
    }

    /**
     * Vytiahne to kartu komunitna truhlica ktora je v poradi v spravcovy, informuje co to je za kartu a posle spravu na vykonanie akcie, ak to je karta Priepustka, tak akciu nevykonava, iba ju priradi do inventara
     * @param hrac Hrac ktory vstupil na policko
     * @param spravca Spravca hry
     */
    @Override
    public void akcia(Hrac hrac, Spravca spravca) {
        var karta = spravca.getPrvuKartuTruhlica();
        if (karta.isPresent()) {
            if (karta.get() instanceof Priepustka) {
                JOptionPane.showMessageDialog(null, "Hrac " + hrac.getMeno() + " dostal priepustku z vazenia", "Karta Truhlica", JOptionPane.INFORMATION_MESSAGE);
                karta.get().vykonanaAkcia(hrac, spravca);
            } else {
                JOptionPane.showMessageDialog(null, "Hrac " + hrac.getMeno() + " si vytiahol kartu " + karta.get().getPopis(), "Karta Truhlica", JOptionPane.INFORMATION_MESSAGE);
                karta.get().vykonanaAkcia(hrac, spravca);
                spravca.pridajNaPlochuTruhlicu(karta.get());
            }
        }
    }
}
