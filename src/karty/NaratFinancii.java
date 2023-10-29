package karty;

import hraci.Banka;
import hraci.Hrac;
import zaklad.Spravca;

public class NaratFinancii implements BonusoveKarty {
    private final String popis;
    private final int cena;
    private final boolean odKazdehoHraca;

    /**
     * Konstruktor na nacitanie atributov
     * @param popis Popis karty
     * @param cena Suma ktora sa hracovy vrati
     * @param odKazdehoHraca Ak sa ma tato suma oddcitat kazdemu hracovy a pricitat hracovy ktory si vytiahol tuto kartu, tak je to true
     */
    public NaratFinancii(String popis, int cena, boolean odKazdehoHraca) {
        this.popis = popis;
        this.cena = cena;
        this.odKazdehoHraca = odKazdehoHraca;
    }

    @Override
    public String getPopis() {
        return this.popis;
    }

    /**
     * Hracovy prida peniaze a z banky sa zoberu, popripade ak je to od kazdeho hraca, tak sa odcitaju kazdemu hracovy a pridaju sa hracovy ktory si vytiahol kartu
     * @param hrac Hrac ktory si vytiahol kartu
     * @param spravca Spravca hry
     */
    @Override
    public void vykonanaAkcia(Hrac hrac, Spravca spravca) {
        if (this.odKazdehoHraca) {
            for (Hrac h : spravca.getHraci()) {
                if (h != hrac) {
                    h.zmenFinancie(-this.cena);
                }
            }
            hrac.zmenFinancie(this.cena * (spravca.getPocetHracov() - 1));
        } else {
            hrac.zmenFinancie(this.cena);
            Banka.pridajPoplatok(-this.cena);
        }
    }
}