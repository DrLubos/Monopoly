package hraciaPlocha.policka;

import hraci.Hrac;
import zaklad.Spravca;

public interface Policko {
    String getNazov();

    /**
     * Vykonavana akcie pri zastaveni na policku
     * @param hrac Hrac ktory vstupil na policko
     * @param spravca Spravca hry
     */
    void akcia(Hrac hrac, Spravca spravca);
}