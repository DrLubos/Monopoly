package zaklad;

import grafika.Grafika;
import hraci.Hrac;
import hraciaPlocha.HraciaPlocha;
import hraciaPlocha.Nacitavanie;
import hraciaPlocha.policka.Policko;
import hraciaPlocha.policka.kupa.Nehnutelnost;
import hraciaPlocha.policka.kupa.Pozemok;
import hraciaPlocha.policka.kupa.TypPozemku;
import karty.BonusoveKarty;
import karty.DruhKarty;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.Optional;
import java.util.List;

public class Spravca {
    private final ArrayList<BonusoveKarty> sanca;
    private final ArrayList<BonusoveKarty> truhlica;
    private final ArrayList<Hrac> hraci;
    private int cisloHracaNaTahu;
    private int[] kocky;
    private Grafika grafika;
    private int pocetHodov;

    /**
     * Konstruktor vytvara spravcu a na nom sa vytvoria aj karty, pomiesaju sa a nastavi sa grafika a kocky
     * @param grafika Grafika na ovladanie
     * @param vlastnaHra Ci sa maju nacitavat vlastne karty pre hru
     */
    public Spravca(Grafika grafika, boolean vlastnaHra) {
        HraciaPlocha.nacitajMapu(vlastnaHra);
        this.hraci = new ArrayList<>();
        if (vlastnaHra) {
            this.sanca = Nacitavanie.nacitajBalikKariet(DruhKarty.SANCA);
            this.truhlica = Nacitavanie.nacitajBalikKariet(DruhKarty.TRUHLICA);
        } else {
            this.sanca = Nacitavanie.nacitajDefaultBalikKariet(DruhKarty.SANCA);
            this.truhlica = Nacitavanie.nacitajDefaultBalikKariet(DruhKarty.TRUHLICA);
        }
        Collections.shuffle(this.sanca);
        Collections.shuffle(this.truhlica);
        this.kocky = new int[]{1, 1};
        this.grafika = grafika;
        this.pocetHodov = 0;
    }

    /**
     * Pridava do ArrayListu sanci kartu sanca
     * @param karta Karta na pridanie
     */
    public void pridajNaPlochuSancu(BonusoveKarty karta) {
        this.sanca.add(karta);
    }

    /**
     * Pridava do ArrayListu truhlic kartu truhlica
     * @param karta Karta na pridanie
     */
    public void pridajNaPlochuTruhlicu(BonusoveKarty karta) {
        this.truhlica.add(karta);
    }

    /**
     * Pridava hraca do hry
     * @param hrac Hrac na pridanie
     */
    public void pridajHraca(Hrac hrac) {
        this.hraci.add(hrac);
    }

    /**
     * Nastavuje graficku triedu
     * @param grafika Graficka trieda na nastavenie
     */
    public void setGrafika(Grafika grafika) {
        this.grafika = grafika;
    }

    /**
     * Vygeneruje sa nahodne cislo, aktualizuje sa grafika, posiela sa sprava na posun hraca, tiez zisti ci bolo hodene viac ako 3 krat a ak ano, tak podla pravidiel sa nastavi hrac na vazna
     * @return Pole toho co padlo na kockach
     */
    public int[] hodKockami() {
        Random r = new Random();
        this.kocky = new int[]{r.nextInt(6) + 1, r.nextInt(6) + 1};
        this.grafika.aktualizujKocky();
        this.aktualizujGrafiku();
        Hrac naTahu = this.hraci.get(this.cisloHracaNaTahu);
        naTahu.posunO(this.getsucetKociek());
        this.grafika.aktualizujHraca(this.cisloHracaNaTahu);
        HraciaPlocha.getPolicko(naTahu.getPozicia()).akcia(naTahu, this);
        this.grafika.aktualizujHraca(this.cisloHracaNaTahu);
        this.aktualizujGrafiku();
        this.pocetHodov++;
        if (this.pocetHodov > 2) {
            this.hraci.get(this.cisloHracaNaTahu).setVazen(true, this);
        }
        return this.kocky;
    }

    /**
     * Vracia sucet cisel na kockach
     * @return Sucet cisel na kockach
     */
    public int getsucetKociek() {
        int sucet = 0;
        for (int j : this.kocky) {
            sucet += j;
        }
        return sucet;
    }

    /**
     * Zresetuje sa pocet hodov, a povolia sa moznosti na grafike, ak sa nehra tak vypise vyhercu,inak sa zvysuje index cislo hraca na kroku.
     * Kontroluje sa ci index nove hraca na kroku je pre hraca, ktory nezbanrotoval, ak prislo na hraca, ktory ohlasil bankrot, tak sa opat zvysuje index hraca.
     * Potom sa zisti ci hrac ma menej ako 0 penazi, ak ano ma moznost ohlasit bankrot.
     * Ak je zvoleny hrac vazen, tak sa posiela sprava vazeniu a hrac sa v nej ma moznsot z neho dostat a opat sa kontroluje ci je vazen, ak stale je vazen, tak metoda spusta samu seba
     */
    public void zmenHraca() {
        this.pocetHodov = 0;
        this.grafika.setPovolMoznosti(true);
        // Zisti sa ci sa este hra
        if (!this.hraSa()) {
            Hrac vyherca = null;
            for (Hrac h : this.hraci) {
                if (!h.isBankrot()) {
                    vyherca = h;
                    while (true) {
                        JOptionPane.showMessageDialog(null, "Vyhral hrac " + vyherca.getMeno(), "Vyherca", JOptionPane.INFORMATION_MESSAGE);
                        if (JOptionPane.showConfirmDialog(null, "Chcete ukoncit hru", "Ukoncenie hry", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                            System.exit(0);
                        }
                    }
                }
            }
        }
        this.cisloHracaNaTahu++;
        if (this.cisloHracaNaTahu >= this.hraci.size()) {
            this.cisloHracaNaTahu = 0;
        }
        while (this.hraci.get(this.cisloHracaNaTahu).isBankrot()) {
            this.cisloHracaNaTahu++;
            if (this.cisloHracaNaTahu >= this.hraci.size()) {
                this.cisloHracaNaTahu = 0;
            }
        }
        this.grafika.aktualizujHraca(this.cisloHracaNaTahu);
        if (this.hraci.get(this.cisloHracaNaTahu).getFinancie() < 0) {
            if (JOptionPane.showConfirmDialog(null, "Nemate dostatok financii chcete ohlasit bankrot?", "Nedostatok financii", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                this.hraci.get(this.cisloHracaNaTahu).ohlasBankrot();
                this.aktualizujGrafiku();
                this.zmenHraca();
                return;
            }
            this.grafika.spristupniKoniec();
        }
        if (this.hraci.get(this.cisloHracaNaTahu).isVazen()) {
            HraciaPlocha.getPolicko(this.hraci.get(this.cisloHracaNaTahu).getPozicia()).akcia(this.hraci.get(this.cisloHracaNaTahu), this);
            if (this.hraci.get(this.cisloHracaNaTahu).isVazen()) {
                this.zmenHraca();
            }
        }
    }

    /**
     * Posle grafickej triede spravu na to aby mohli byt pouzivane tlacidla
     * @param povoleneMoznosti True a tlacidla sa mozu pouzivat
     */
    public void setPovoleneMoznosti(boolean povoleneMoznosti) {
        this.grafika.setPovolMoznosti(povoleneMoznosti);
    }

    /**
     * Zistuje ci ma este cenu hrat, to znamena ak je viac ako 1 hrac, ktory nezbankrotoval, tak ma este cenu hrat
     * @return True, ak je viac ako jeden hrac ktory nezbankrotoval
     */
    private boolean hraSa() {
        int pocet = 0;
        for (Hrac h : this.hraci) {
            if (!h.isBankrot()) {
                pocet++;
            }
        }
        return pocet > 1;
    }

    /**
     * metoda posuva hraca o velkost posunu a spusta na policku na ktorom sa ocitol akciu
     * @param velkostPosunu Pocet policok o kolko sa hrac posunie
     * @param hrac Hrac ktory sa posuva
     */
    public void posunO(int velkostPosunu, Hrac hrac) {
        hrac.posunO(velkostPosunu);
        Policko policko = HraciaPlocha.getPolicko(hrac.getPozicia());
        policko.akcia(hrac, this);
    }

    /**
     * Meroda posuva hraca na zadane miesto a spusta akciu na tom mieste.
     * @param miesto Mesto na presun hraca
     * @param hrac Hrac ktory sa presuva
     */
    public void posunNa(int miesto, Hrac hrac) {
        hrac.posunO(miesto - hrac.getPozicia());
        Policko policko = HraciaPlocha.getPolicko(hrac.getPozicia());
        policko.akcia(hrac, this);
    }

    // Metody na spracovanie akcii na tlacidlach

    /**
     * Metoda sa spusta ak hrac bude chciet postavit dom na svojej nehnutelnosti, ak je to nehnutelnost, tak sa jej posiela sprava na postavenie domu
     * @param cisloPolicka Index policka na ktorom sa bude pokusat postavit dom
     */
    public void postavajDom(int cisloPolicka) {
        if (HraciaPlocha.getPolicko(cisloPolicka) instanceof Nehnutelnost) {
            ((Nehnutelnost)HraciaPlocha.getPolicko(cisloPolicka)).pridajDom(this.hraci.get(this.cisloHracaNaTahu));
        } else {
            JOptionPane.showMessageDialog(null, "Tu nie je mozne postavit dom", "Nie je mozne vylepsit nehnutelnost", JOptionPane.ERROR_MESSAGE);
        }
        this.aktualizujGrafiku();
    }

    /**
     * Metoda sa spusta ak hrac bude chciet predat dom na svojej nehnutelnosti, ak je to nehnutelnost, tak sa jej posiela sprava na zobratie domu
     * @param cisloPolicka Index pozemku na ktorom sa bude pokusat predavat dom
     */
    public void predajDom(int cisloPolicka) {
        if (HraciaPlocha.getPolicko(cisloPolicka) instanceof Nehnutelnost) {
            ((Nehnutelnost)HraciaPlocha.getPolicko(cisloPolicka)).zoberDom(this.hraci.get(this.cisloHracaNaTahu));
        } else {
            JOptionPane.showMessageDialog(null, "Kedze sa tu neda postavit dom, tak potom ani predat", "Nie je mozne predat dom", JOptionPane.ERROR_MESSAGE);
        }
        this.aktualizujGrafiku();
    }

    /**
     * Metoda sa vola ak hrac si ziada zalozit banke pozemok, ak je to Pozemok tak sa dalej posle sprava pozemku na zalozenie
     * @param cisloPolicka Index pozemku ktory sa bude pokusat zalozit banke
     */
    public void zalozBanke(int cisloPolicka) {
        if (HraciaPlocha.getPolicko(cisloPolicka) instanceof Pozemok) {
            ((Pozemok)HraciaPlocha.getPolicko(cisloPolicka)).zaloz(this.hraci.get(this.cisloHracaNaTahu));
        } else {
            JOptionPane.showMessageDialog(null, "Toto policko sa neda zalozit banke", "Nie je mozne zalozit policko banke", JOptionPane.ERROR_MESSAGE);
        }
        this.aktualizujGrafiku();
    }

    /**
     * Metoda sa spusti ak hrac bude chciet banke vyplatit peniaze za pozemok, vdaka comu bude zase moct inkasovat najom. Algoritmus zisti ci je to Pozemok a ak ano posle spravu na odkupenie
     * @param cisloPolicka Index pozemku ktory sa bude pokusat vyplatit banke
     */
    public void vyplatBanku(int cisloPolicka) {
        if (HraciaPlocha.getPolicko(cisloPolicka) instanceof Pozemok) {
            ((Pozemok)HraciaPlocha.getPolicko(cisloPolicka)).odkupSpat(this.hraci.get(this.cisloHracaNaTahu));
        } else {
            JOptionPane.showMessageDialog(null, "Toto policko sa neda splatit banke", "Nie je mozne splatit policko banke", JOptionPane.ERROR_MESSAGE);
        }
        this.aktualizujGrafiku();
    }

    /**
     * Metoda sluzi na to ak hrac poziadal o obchodovanie zvoleneho pozemku. Obsahuje to algoritmy na zadanie sumy a opytanie sa hraca ci suhlasy z cenou, ak ano prevedu sa peniaze medzi hracmi a zmeni sa vlastnik pozemku
     * @param cisloPolicka Index pozemku o ktory sa bude pokusat spustit obchod
     */
    public void spustiObchod(int cisloPolicka) {
        Policko policko = HraciaPlocha.getPolicko(cisloPolicka);
        if (!(policko instanceof Pozemok)) {
            JOptionPane.showMessageDialog(null, "Toto policko nie je na obchodovanie", "Nie je mozne spustit obchodovanie", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (((Pozemok)policko).getVlastnik() == null) {
            JOptionPane.showMessageDialog(null, "Toto policko nema vlastnika, tak sa nema s kym obchodovat", "Nie je mozne spustit obchodovanie", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (this.hraci.get(this.cisloHracaNaTahu).getFinancie() < 0) {
            JOptionPane.showMessageDialog(null, "Mate malo penazi", "Nie je mozne spustit obchodovanie", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String novaCena = (String)JOptionPane.showInputDialog(null, this.hraci.get(this.cisloHracaNaTahu).getMeno() + " zadajte navrh ceny, za ktoru by ste chceli kupit " + policko.getNazov() + " od hraca " + ((Pozemok)policko).getVlastnik().getMeno() + "\nK dispozicii mate: " + this.hraci.get(this.cisloHracaNaTahu).getFinancie() + "$\nAk ste si rozmysleli obchod stlacte Cancel", this.hraci.get(this.cisloHracaNaTahu).getMeno() + " prave robi ponuku na " + policko.getNazov(), JOptionPane.INFORMATION_MESSAGE);
        if (novaCena == null) {
            return;
        }
        int cena = -1;
        try {
            cena = Integer.parseInt(novaCena);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Nabuduce zadajte cenu v celom cisle", "Nie je mozne spustit obchodovanie", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (cena > this.hraci.get(this.cisloHracaNaTahu).getFinancie()) {
            JOptionPane.showMessageDialog(null, "Mate malo penazi", "Nie je mozne spustit obchodovanie", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (cena >= 0 && JOptionPane.showConfirmDialog(null,  ((Pozemok)policko).getVlastnik().getMeno() + " chcete predat svoj pozemok " + policko.getNazov() + " hracovy " + this.hraci.get(this.cisloHracaNaTahu).getMeno() + " za cenu " + cena + "$?", ((Pozemok)policko).getVlastnik().getMeno() + " Potvrdzovanie obchodu", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            this.hraci.get(this.cisloHracaNaTahu).zmenFinancie(-cena);
            Hrac minuly = ((Pozemok)policko).getVlastnik();
            minuly.zmenFinancie(cena);
            ((Pozemok)policko).setVlastnik(this.hraci.get(this.cisloHracaNaTahu));
            JOptionPane.showMessageDialog(null, this.hraci.get(this.cisloHracaNaTahu).getMeno() + " kupil pozemok " + policko.getNazov() + " od hraca " + minuly.getMeno() + " za cenu " + cena + "$", "Obchod bol uspesny", JOptionPane.INFORMATION_MESSAGE);
            this.grafika.aktualizujPolicka();
        } else {
            JOptionPane.showMessageDialog(null, "Hrac " + ((Pozemok)policko).getVlastnik().getMeno() + " nesuhlasil s ponukou " + cena + "$ za pozemok " + policko.getNazov(), "Obchod bol neuspesny", JOptionPane.ERROR_MESSAGE);
        }
        this.aktualizujGrafiku();
    }

    /**
     * Metoda sa spusta ak hrac nema na akupenie pozemku, alebo ho nechce kupit
     * Vyherca sa nastavy na hraca ktory je na pozemku a cena zacina na 1$, hraci mozu zvysovat ponuku do vsetkych svojich prostriedkov
     * @param pozemok Pozemok o ktory sa spusta aukcia
     * @return Vyherca aukcie
     */
    public Hrac aukcia(Pozemok pozemok) {
        int cena = 0;
        Hrac vyherca = this.hraci.get(this.cisloHracaNaTahu);
        StringBuilder sb = new StringBuilder("Originalna cena: " + pozemok.getCena() + "$, prenajom: ");
        if (pozemok instanceof Nehnutelnost) {
            sb.append(pozemok.getNajom() + "$, s 1 domom: " + ((Nehnutelnost)pozemok).getjD() + "$, s 2 domami: " + ((Nehnutelnost)pozemok).getdD() + "$, 3 domami: " + ((Nehnutelnost)pozemok).gettD() + "$, so 4 domami: " + ((Nehnutelnost)pozemok).getsD() + "$, s hotelom: " + ((Nehnutelnost)pozemok).getHotel() + "$");
        } else if (pozemok.getTyp() == TypPozemku.DOPRAVA) {
            sb.append(pozemok.getNajom() + "$, prenajom sa potom nasoby, podla toho kolko pozemkov mate takehoto isteho typu");
        } else if (pozemok.getTyp() == TypPozemku.SPOLOCNOST) {
            sb.append("ak mate jednu nehnutelnosti prenajom bude 4 * sucet cisel na kockach a ak mate dve policka typu " + pozemok.getTyp().toString() + " tak sa zaplati 10 nasobok suctu cisel na kockach");
        }
        ArrayList<Hrac> zaujemci = new ArrayList<>(this.hraci);
        while (zaujemci.size() > 1) {
            Iterator<Hrac> iterator = zaujemci.iterator();
            while (iterator.hasNext()) {
                Hrac aktualny = iterator.next();
                // Ak hrac nema dostatok penazi ako je aktualna ponuka v aukcii, tak sa vymaze
                if (aktualny.getFinancie() < cena) {
                    iterator.remove();
                    continue;
                }
                String novaCena = (String)JOptionPane.showInputDialog(null, aktualny.getMeno() + ", na ucte mate: " + aktualny.getFinancie() + "$ zadajte vyssiu cenu (zadajte iba cele cislo) ako je: " + cena + "$, ktoru ponukol hrac " + vyherca.getMeno() + " za ktoru chcete kupit nehnutelnost: " + pozemok.getNazov() + "\n" + sb + "\nAk nechcete kupit stlacte Cancel", aktualny.getMeno() + " prave ste na aukcii za pozemok " + pozemok.getNazov(), JOptionPane.INFORMATION_MESSAGE, null, null, cena + 1);
                // Ak hrac stlacil cancel, tak sa odstranuje z aukcie
                if (novaCena == null) {
                    iterator.remove();
                    continue;
                }
                try {
                    if (Integer.parseInt(novaCena) > cena && Integer.parseInt(novaCena) <= aktualny.getFinancie()) {
                        cena = Integer.parseInt(novaCena);
                        vyherca = aktualny;
                    }
                } catch (NumberFormatException e) {
                    // Hrac ma smolu, lebo nedal cislo, ktore bolo platne. Hrac nadalej ostava v aukcii
                }
            }
        }
        if (vyherca != null) {
            JOptionPane.showMessageDialog(null, "Nehnutelnost " + pozemok.getNazov() + " kupil hrac " + vyherca.getMeno() + " za cenu " + cena + "$", vyherca.getMeno() + " vyhral aukciu o " + pozemok.getNazov(), JOptionPane.INFORMATION_MESSAGE);
            vyherca.zmenFinancie(-cena);
        } else {
            JOptionPane.showMessageDialog(null, "Nehnutelnost " + pozemok.getNazov() + " nekupil nikto", "Nikto nevyhral aukciu o " + pozemok.getNazov(), JOptionPane.INFORMATION_MESSAGE);
        }
        this.aktualizujGrafiku();
        return vyherca;
    }

    // Getere a nie az tak moc zaujimavy kod

    /**
     * Metoda posiela spravy grafike na jej aktualizovanie
     */
    public void aktualizujGrafiku() {
        this.grafika.aktualizujHracov();
        this.grafika.aktualizujKocky();
        this.grafika.aktualizujPolicka();
    }

    /**
     * Vracia kartu sanca, ktora je aktualne v poradu a odstrani ju zo zoznamu
     * @return Karta Sanca
     */
    public Optional<BonusoveKarty> getPrvuKartuSanca() {
        if (this.sanca.isEmpty()) {
            return Optional.empty();
        }
        return  Optional.of(this.sanca.remove(0));
    }

    /**
     * Vracia kartu truhlica, ktora je aktualne v poradi a odstrani ju zo zoznamu
     * @return Karta Truhlica
     */
    public Optional<BonusoveKarty> getPrvuKartuTruhlica() {
        if (this.truhlica.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(this.truhlica.remove(0));
    }

    /**
     * Vracia pocet hracov v hre
     * @return Pocet hracov v hre
     */
    public int getPocetHracov() {
        return this.hraci.size();
    }

    /**
     * Vracia neupravitrelny zoznam hracov
     * @return Zoznam hracov
     */
    public List<Hrac> getHraci() {
        return Collections.unmodifiableList(this.hraci);
    }

    /**
     * Vracia cislo hraca ktory je na tahu
     * @return Cislo hraca ktory je na tahu
     */
    public int getCisloHracaNaTahu() {
        return this.cisloHracaNaTahu;
    }

    /**
     * Vracia pole ktore reprezentuje hodene hodnoty na kockach
     * @return Pole ktore reprezentuje hodene hodnoty na kockach
     */
    public int[] getKocky() {
        return this.kocky;
    }
}