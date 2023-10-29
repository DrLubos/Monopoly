package hraciaPlocha.policka.kupa;

import hraci.Hrac;
import hraciaPlocha.HraciaPlocha;
import zaklad.Spravca;
import hraciaPlocha.policka.Policko;
import javax.swing.JOptionPane;

public class Pozemok implements Policko {
    private final String nazov;
    private final int cena;
    private final TypPozemku typ;
    private Hrac vlastnik;
    private final int najom;
    private boolean pozicany;

    /**
     * Vytvori sa pozemok s nazvom a inymi parametrami. V hre reprezentuje policko, ktore si hrac moze kupit
     * @param nazov Nazov nehnutelnosti
     * @param cena Cena nehnutelnosti
     * @param typ Typ nehnutelnosti
     * @param najom Cena najmu nehnutelnosti, ak sa zada cislo mensie rovne 5,tak to sluzi na vypocet najmu pomocou algoritmu
     */
    public Pozemok(String nazov, int cena, TypPozemku typ, int najom) {
        this.nazov = nazov;
        this.cena = cena;
        this.typ = typ;
        this.vlastnik = null;
        this.najom = najom;
        this.pozicany = false;
    }

    /**
     * Ak este nie je vlastnik, tak ma moznost hrac kupit pozemok, inak sa spusti aukcia o pozemok pomocou spravcu
     * Ak je pozemok pozicany banke, tak sa nerobi nic, inak sa ale hracovy ktory prisiel na pozmok a nie je vlastnik odpocita z jeho financii cena najmu
     * @param hrac Hrac ktory vstupil na policko a podla toho sa urobi akcia
     * @param spravca Spravca hry, ktoremu sa posiela sprava v nejakych pripadoch
     */
    @Override
    public void akcia(Hrac hrac, Spravca spravca) {
        if (this.vlastnik == null) {
            if (hrac.getFinancie() < this.cena) {
                Hrac vyherca = spravca.aukcia(this);
                if (vyherca != null) {
                    this.vlastnik = vyherca;
                }
                return;
            }
            while (true) {
                int chceKupit = JOptionPane.showConfirmDialog(null, "Chcete kupit nehnutelnost " + this.nazov + " za cenu: " + this.cena + "$ ?\nMate na ucte: " + hrac.getFinancie() + "$", hrac.getMeno() + " kupa nehnutelnosti",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (chceKupit == JOptionPane.YES_OPTION) {
                    hrac.zmenFinancie(-this.cena);
                    hrac.pridajNehnutelnost(this);
                    this.vlastnik = hrac;
                    JOptionPane.showMessageDialog(null, "Uspesne ste kupili " + this.nazov + " za cenu: " + this.cena + "$\n" + hrac.getMeno() + " novy zostatok na ucte je: " + hrac.getFinancie() + "$", hrac.getMeno() + " uspesne ste nakupili", JOptionPane.INFORMATION_MESSAGE);
                    return;
                } else if (chceKupit == JOptionPane.NO_OPTION) {
                    Hrac vyherca = spravca.aukcia(this);
                    if (vyherca != null) {
                        this.vlastnik = vyherca;
                    }
                    return;
                }
            }
        }
        if (this.pozicany) {
            return;
        }
        if (this.vlastnik != hrac) {
            hrac.zmenFinancie(-this.vypocitajCelkovyNajom(spravca));
            this.vlastnik.zmenFinancie(this.vypocitajCelkovyNajom(spravca));
            JOptionPane.showMessageDialog(null, "Zaplatili ste najom vo vyske: " + this.vypocitajCelkovyNajom(spravca) + "$ za pozemok " + this.nazov + " ktoreho vlastnikom je " + this.vlastnik.getMeno() + "\n" + hrac.getMeno() + " novy zostatok na ucte je: " + hrac.getFinancie() + "$", hrac.getMeno() + " uspesne ste zaplatili prenajom", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Metoda sluzi na vypocet najmu. Pre Nehnutelnost to iba zisti ci hrac ma vsetky policka takeho isteho typu a vrati najom vynasobeny dvomi, inak rati pomocou getterov velkost najmu
     * Ak je cena najmu menej rovna 5, tak najom je sucect kociek vynasobeny hondotou v atribute cenaNajmu a ak hrac ma aspon 2 tohto typu, tak najom je sucet kociek vynasobeny 10
     * Inak sa to bude nasobit cislom 2, tolko krat, kolko ma hrac nehnutelnosti rovnakeho typu
     * @param spravca Sluzi na to aby metoda ziskala sucet kociek, pomocou ktorych vypocita najom, pomocou nasobenia
     * @return Vracia int o vypocitanej ceny najmu
     */
    private int vypocitajCelkovyNajom(Spravca spravca) {
        if (this instanceof Nehnutelnost) {
            return switch (((Nehnutelnost)this).getPocetDomov()) {
                case 1 -> ((Nehnutelnost)this).getjD();
                case 2 -> ((Nehnutelnost)this).getdD();
                case 3 -> ((Nehnutelnost)this).gettD();
                case 4 -> ((Nehnutelnost)this).getsD();
                case 5 -> ((Nehnutelnost)this).getHotel();
                default -> {
                    int pocet = 0;
                    for (Pozemok p : this.vlastnik.getPozemky()) {
                        if (p.getTyp() == this.typ) {
                            pocet++;
                        }
                    }
                    if (HraciaPlocha.getRovnakyTyp(this.typ) == pocet) {
                        // Na moznost yield som prisiel tu
                        // https://stackoverflow.com/questions/58049131/what-does-the-new-keyword-yield-mean-in-java-13
                        yield this.najom * 2;
                    } else {
                        yield this.najom;
                    }
                }
            };
        }
        int pocet = 0;
        for (Pozemok p : this.vlastnik.getPozemky()) {
            if (p.getTyp() == this.typ) {
                pocet++;
            }
        }
        if (this.najom <= 5) {
            if (pocet >= 2) {
                return spravca.getsucetKociek() * 10;
            }
            return spravca.getsucetKociek() * this.najom;
        }
        int vysledok = this.najom;
        for (int i = 0; i < pocet - 1; i++) {
            vysledok *= 2;
        }
        return vysledok;
    }

    /**
     * Hrac pozicia dom banke a za to dostane polovicu z ceny nehnutelnosti, ale hrac nebude poberat najom od ostatnych hracov
     * Kontroluje sa tu ci uz je pozicany a ak nie je tak sa zobrazi okno ci hrac naozaj chce zalozit nehnutelnost a zobrazi aj potom cenu, za ktoru ju hrac bude moct odkupit spat
     * @param hrac Hrac ktory poziadal o zalozenie pozemku banke
     */
    public void zaloz(Hrac hrac) {
        if (this.nemozneAkcie(hrac)) {
            return;
        }
        if (this.pozicany) {
            JOptionPane.showMessageDialog(null, "Nie je mozne zalozit pozemok, lebo uz je zalozeny", "Nie je mozne zalozit pozemok", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(null,  this.vlastnik.getMeno() + " chcete zalozit svoj pozemok " + this.nazov + "? Dostanete " + (this.cena / 2) + "$ a na splatenie budete potrebovat " + ((this.cena / 2) + (this.cena / 2 / 10)) + "$", this.vlastnik + " Potvrdzovanie zalozenia", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            this.vlastnik.zmenFinancie(this.cena / 2);
            this.pozicany = true;
        }
    }

    /**
     * Metoda sluzi na to aby hrac odkupil od banky svoj pozemok naspat, kontroluje sa tu aj ci je pozemok zalozeny a ci hrac ma financie na odkupenie
     * @param hrac Hrac ktory poziadal o akciu
     */
    public void odkupSpat(Hrac hrac) {
        if (this.nemozneAkcie(hrac)) {
            return;
        }
        if (!this.pozicany) {
            JOptionPane.showMessageDialog(null, "Pozemok uz je splateny", "Nie je mozne splatit pozemok", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (this.vlastnik.getFinancie() >= ((this.cena / 2) + (this.cena / 2 / 10))) {
            this.vlastnik.zmenFinancie(-((this.cena / 2) + (this.cena / 2 / 10)));
            this.pozicany = false;
        } else {
            JOptionPane.showMessageDialog(null, "Nemate financie na splatenie, potrebujete aspon " + ((this.cena / 2) + (this.cena / 2 / 100 * 10)) + "$", "Nie je mozne splatit pozemok", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Metoda sa vola pri tom ked hrac chce vykonat nejaku akciu s pozemkom alebo nehnutelnostou a sluzi na overenie ci hrac ktory ziada akciu je vlastnik, alebo ci je mozne vykonat akciu pre to ak ma pozemok nejakeho vlastnika
     * @param hrac Hrac ktory poz
     * @return Vracia true ak nastala nejaka chyba a tym padom nie je mozne vykonat akcie
     */
    protected boolean nemozneAkcie(Hrac hrac) {
        if (this.vlastnik == null) {
            JOptionPane.showMessageDialog(null, "Nie je mozne vykonat akciu na pozemku, ktory nik nevlaastni", "Nie je mozne vykonat akciu", JOptionPane.ERROR_MESSAGE);
            return true;
        }
        if (this.vlastnik != hrac) {
            JOptionPane.showMessageDialog(null, "Nie je mozne vykonat akciu, lebo nie ste vlastnik", "Nie je mozne vykonat akciu", JOptionPane.ERROR_MESSAGE);
            return true;
        }
        return false;
    }

    /**
     * Meni sa vlastnik nehnutelnosti, pouziva sa pri obchodovani
     * @param vlastnik Novy vlastnik nehnutelnosti
     */
    public void setVlastnik(Hrac vlastnik) {
        this.vlastnik = vlastnik;
    }

    public TypPozemku getTyp() {
        return this.typ;
    }

    @Override
    public String getNazov() {
        return this.nazov;
    }

    public int getCena() {
        return this.cena;
    }

    public Hrac getVlastnik() {
        return this.vlastnik;
    }

    public int getNajom() {
        return this.najom;
    }

    public boolean isPozicany() {
        return this.pozicany;
    }
}