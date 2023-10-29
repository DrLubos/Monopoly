package hraciaPlocha.policka.kupa;

import hraci.Hrac;
import hraciaPlocha.HraciaPlocha;
import javax.swing.JOptionPane;
import java.util.List;

public class Nehnutelnost extends Pozemok {
    private final int jD;
    private final int dD;
    private final int tD;
    private final int sD;
    private final int hotel;
    private int pocetDomov;

    /**
     * V konstruktore sa vytvori objekt a inicializuju atributy
     * @param nazov Nazov nehnutelnosti
     * @param cena Cena pre kupu nehnutelnosti
     * @param typ Typ nehnutelnosti
     * @param najom Cena pre zakladny najom nehnutelnosti
     * @param jD Cena najmu s jednym domom
     * @param dD Cena najmu s dvoma domami
     * @param tD Cena najmu s troma domami
     * @param sD Cena najmu so styrmi domami
     * @param hotel Cena najmu s hotekom
     */
    public Nehnutelnost(String nazov, int cena, TypPozemku typ, int najom, int jD, int dD, int tD, int sD, int hotel) {
        super(nazov, cena, typ, najom);
        this.jD = jD;
        this.dD = dD;
        this.tD = tD;
        this.sD = sD;
        this.hotel = hotel;
        this.pocetDomov = 0;
    }

    /**
     * Metoda sluzi na to aby hrac pridal dom na policko, a vdaka tomu sa zvysuje najom ked na policko vstupy iny hrac.
     * Obsahuje algoritmy, ktore zistuju ci je mozne pridat dom podla pravidiel. Napriklad: ci hrac ma dostatok penazi, ci hrac ma vsetky nehnutelnosti rovnakeho typu, ci hrac ma na ostatnych pozemkoch rovnakeho typu aspon taky isty pocet domov ako sa nachadza na zvolenom policku, ci uz sa neprekrocil maximalny pocet domov
     * @param h Hrac, ktory poziadal o pridanie domu
     */
    public void pridajDom(Hrac h) {
        if (super.nemozneAkcie(h)) {
            return;
        }
        if (HraciaPlocha.vyhladajPodlaTypu(super.getTyp(), 0).isPresent()) {
            int pocetNaPloche = HraciaPlocha.vyhladajPodlaTypu(super.getTyp(), 0).get().size();
            List<Pozemok> hracove = super.getVlastnik().getPozemky();
            int pocetHracovych = 0;
            for (Pozemok pozemok : hracove) {
                if (pozemok.getTyp() == super.getTyp()) {
                    pocetHracovych++;
                }
            }
            if (pocetHracovych < pocetNaPloche) {
                JOptionPane.showMessageDialog(null, "Vyzera to ze nemate zakupene vsetky policka rovnakej farby, tak nemozete postavi dom", super.getVlastnik().getMeno() + " nie je mozne vylepsit nehnutelnosst", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        if (this.pocetDomov >= 5) {
            JOptionPane.showMessageDialog(null, "Mas uz hotel, co je maximalne vylepsenie", super.getVlastnik().getMeno() + " nie je mozne vylepsit nehnutelnosst", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        for (Pozemok p : super.getVlastnik().getPozemky()) {
            if (p.getTyp() == super.getTyp() && p instanceof Nehnutelnost) {
                if (((Nehnutelnost)p).getPocetDomov() < this.pocetDomov) {
                    JOptionPane.showMessageDialog(null, "Na ostatnych pozemkoch treba mat aspon " + this.pocetDomov + " domi", super.getVlastnik().getMeno() + " nie je mozne vylepsit nehnutelnosst", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }
        if (super.getVlastnik().getFinancie() < super.getTyp().getCenaVylepsenia()) {
            JOptionPane.showMessageDialog(null, "Nemas potrebne financie na vylepsenie. Potrbujes aspon " + super.getTyp().getCenaVylepsenia() + "$", super.getVlastnik().getMeno() + " nie je mozne vylepsit nehnutelnosst", JOptionPane.ERROR_MESSAGE);
            return;
        }
        super.getVlastnik().zmenFinancie(-super.getTyp().getCenaVylepsenia());
        this.pocetDomov++;
    }

    /**
     * Metoda sluzi na to aby hrac mohol znizit pocet domov na nehnutelnosti. Hrac dostane polovicu ceny z ceny vylepsenia
     * Obsahuje aj algoritmy, ktore zistuju napriklad: ci hrac ktory poziadal o tuto akciu je vlastnikom nehnutelnosti, ci na ostatnych domoch tohto typu nema hrac viac domov ako je na aktualnom policku alebo ci sa nepojde s poctom domov do minusu
     * @param h Hrac ktory poziadal o zobratie domu
     */
    public void zoberDom(Hrac h) {
        if (super.nemozneAkcie(h)) {
            return;
        }
        for (Pozemok p : super.getVlastnik().getPozemky()) {
            if (p.getTyp() == super.getTyp() && p instanceof Nehnutelnost) {
                if (((Nehnutelnost)p).getPocetDomov() > this.pocetDomov) {
                    JOptionPane.showMessageDialog(null, "Na ostatnych pozemkoch mozete mat maximalne " + this.pocetDomov + " domov", super.getVlastnik().getMeno() + " nie je mozne predat dom", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }
        if (this.pocetDomov > 0) {
            super.getVlastnik().zmenFinancie(super.getTyp().getCenaVylepsenia() / 2);
            this.pocetDomov--;
        } else {
            JOptionPane.showMessageDialog(null, "Na tejto nehnutelnosti nemas ziadny dom", super.getVlastnik().getMeno() + " nie je mozne predat dom", JOptionPane.ERROR_MESSAGE);
        }
    }

    public int getPocetDomov() {
        return this.pocetDomov;
    }

    public int getjD() {
        return this.jD;
    }

    public int getdD() {
        return this.dD;
    }

    public int gettD() {
        return this.tD;
    }

    public int getsD() {
        return this.sD;
    }

    public int getHotel() {
        return this.hotel;
    }
}