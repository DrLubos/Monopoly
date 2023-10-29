package karty;

import hraci.Hrac;
import zaklad.Spravca;

public class ZaMreze implements BonusoveKarty {

    @Override
    public String getPopis() {
        return "Chod do vazenia";
    }

    /**
     * Ak si ju hrac vytiahol, tak sa hrac stane vaznom
     * @param hrac Hrac ktory si vytiahol kartu
     * @param spravca Spravca hry
     */
    @Override
    public void vykonanaAkcia(Hrac hrac, Spravca spravca) {
        hrac.setVazen(true, spravca);
    }
}