package karty;

import hraci.Hrac;
import zaklad.Spravca;

public interface BonusoveKarty {

    /**
     * Popis karty
     * @return Popis
     */
    String getPopis();

    /**
     * Vykonava sa akcia
     * @param hrac Hrac ktory si vytiahol kartu
     * @param spravca Spravca hry
     */
    void vykonanaAkcia(Hrac hrac, Spravca spravca);
}