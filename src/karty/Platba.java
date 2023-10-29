package karty;

import hraci.Banka;
import hraci.Hrac;
import zaklad.Spravca;
import hraciaPlocha.policka.kupa.Nehnutelnost;
import hraciaPlocha.policka.kupa.Pozemok;

public class Platba implements BonusoveKarty {
    private final String popis;
    private final DruhPlatby druhPlatby;
    private final int cena;
    private final int cena2;

    /**
     * Konstruktor na nacitanie atributov
     * @param druhPlatby Druh platby ktoru musi hrac zaplatit
     * @param popis Popis karty
     * @param cena Suma, ktoru musi hrac zaplatit
     */
    public Platba(DruhPlatby druhPlatby, String popis, int cena) {
        this.popis = popis;
        this.druhPlatby = druhPlatby;
        this.cena = cena;
        this.cena2 = cena;
    }

    /**
     * Tento konstruktor sa pouziva ak ma byt rozdielna suma pri poplatkoch za kazdy dom a pri poplatku za hotel
     * @param druhPlatby Druh platby
     * @param popis Popis karty
     * @param cena Suma ktoru hrac musi zaplatit (zvycajne suma za kazdy dom)
     * @param cena2 Druha suma ktoru musi hrac zaplatit (zvycajne usma za kazdy hotel)
     */
    public Platba(DruhPlatby druhPlatby, String popis, int cena, int cena2) {
        this.popis = popis;
        this.druhPlatby = druhPlatby;
        this.cena = cena;
        this.cena2 = cena2;
    }

    @Override
    public String getPopis() {
        return this.popis;
    }

    /**
     * Podla druhu platby sa hracovy odcitaju peniaze
     * @param hrac Hrac ktory si vytiahol kartu
     * @param spravca Spravca hry
     */
    @Override
    public void vykonanaAkcia(Hrac hrac, Spravca spravca) {
        switch (this.druhPlatby) {
            case JEDNORAZOVA -> {
                hrac.zmenFinancie(-this.cena);
                Banka.pridajPoplatok(this.cena);
            }
            case DOMY -> {
                for (Pozemok p : hrac.getPozemky()) {
                    if (p instanceof Nehnutelnost) {
                        if (((Nehnutelnost)p).getPocetDomov() < 5) {
                            hrac.zmenFinancie(-this.cena * ((Nehnutelnost)p).getPocetDomov());
                            Banka.pridajPoplatok(this.cena * ((Nehnutelnost)p).getPocetDomov());
                        } else {
                            hrac.zmenFinancie(-this.cena2);
                            Banka.pridajPoplatok(this.cena2);
                        }
                    }
                }
            }
            case OSTATNYM_HRACOM -> {
                for (Hrac h : spravca.getHraci()) {
                    h.zmenFinancie(this.cena);
                    hrac.zmenFinancie(-this.cena);
                }
            }
        }
    }
}