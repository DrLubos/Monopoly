package hraciaPlocha.policka;

import hraci.Banka;
import hraci.Hrac;
import zaklad.Spravca;

public class Parkovanie implements Policko {

    @Override
    public String getNazov() {
        return "PARKOVANIE";
    }

    /**
     * Hracovy sa pridaju financie, ktore su v banke
     * @param hrac Hrac ktory vstupil na policko
     * @param spravca Spravca hry
     */
    @Override
    public void akcia(Hrac hrac, Spravca spravca) {
        hrac.zmenFinancie(Banka.vyplatVelkostPoplatkov());
    }
}