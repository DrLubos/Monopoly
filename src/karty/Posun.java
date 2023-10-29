package karty;

import hraci.Hrac;
import hraciaPlocha.HraciaPlocha;
import zaklad.Spravca;
import hraciaPlocha.policka.kupa.TypPozemku;

public class Posun implements BonusoveKarty {
    private final String popis;
    private int velkostPosunu;
    private String nazovPolickaNaPresun;
    private TypPozemku typPozemku;

    /**
     * Konstruktor pouzivany ked zadavame o kolko policok sa bude hrac posuvat
     * @param popis Popis karty
     * @param posun Pocet policok na posun
     */
    public Posun(String popis, int posun) {
        this.popis = popis;
        this.velkostPosunu = posun;
    }

    /**
     * Konstruktor pouzivany, ked vieme nazov policka na ktory sa ma hrac posunut
     * @param popis Popis karty
     * @param nazovNaPresun Nazov policka na presun
     */
    public Posun(String popis, String nazovNaPresun) {
        this.popis = popis;
        this.nazovPolickaNaPresun = nazovNaPresun;
    }

    /**
     * Konstruktor pouzivany, ked vieme typ pozemku na ktory sa ma hracposunut
     * @param popis Popus karty
     * @param typPozemku Typ pozemku na posun
     */
    public Posun(String popis, TypPozemku typPozemku) {
        this.popis = popis;
        this.typPozemku = typPozemku;
    }

    @Override
    public String getPopis() {
        return this.popis;
    }

    /**
     * Vykonava presun, podla toho ktore parametre su nacitane, tak sa opyta na policka ktore splnaju podmienky a posunie sa na prve, kedze sa to hladalo aj podla pozicie hraca
     * @param hrac Hrac ktory si vytiahol kartu
     * @param spravca Spravca hry
     */
    @Override
    public void vykonanaAkcia(Hrac hrac, Spravca spravca) {
        if (this.nazovPolickaNaPresun != null && this.velkostPosunu == 0) {
            var hladanie = HraciaPlocha.vyhladajPodlaNazvu(this.nazovPolickaNaPresun, hrac.getPozicia());
            hladanie.ifPresent(integers -> spravca.posunNa(integers.get(0), hrac));
        } else if (this.typPozemku != null && this.velkostPosunu == 0) {
            var hladanie = HraciaPlocha.vyhladajPodlaTypu(this.typPozemku, hrac.getPozicia());
            hladanie.ifPresent(integers -> spravca.posunNa(integers.get(0), hrac));
        } else {
            spravca.posunO(this.velkostPosunu, hrac);
        }
        spravca.aktualizujGrafiku();
    }
}