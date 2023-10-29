package hraciaPlocha.policka;

import hraci.Hrac;
import zaklad.Spravca;

public class PosielanieDoVazenia implements Policko {

    @Override
    public String getNazov() {
        return "POSIELANIE DO VAZENIA";
    }

    /**
     * Hracovy sa posle sprava aby sa stal vaznom
     * @param hrac Hrac ktory vstupil na policko
     * @param spravca Spravca hry
     */
    @Override
    public void akcia(Hrac hrac, Spravca spravca) {
        hrac.setVazen(true, spravca);
    }
}