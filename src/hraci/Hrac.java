package hraci;

import hraciaPlocha.HraciaPlocha;
import hraciaPlocha.policka.kupa.Pozemok;
import karty.BonusoveKarty;
import zaklad.Spravca;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Hrac {
    private final String meno;
    private int financie;
    private int pozicia;
    private boolean vazen;
    private final ArrayList<Pozemok> nehnutelnosti;
    private final ArrayList<BonusoveKarty> bonusoveKarty;
    private boolean bankrot;

    /**
     * V konstruktore sa inicializuju parametre
     * @param meno Meno hraca
     * @param suma Suma s ktorou bude zacinat
     */
    public Hrac(String meno, int suma) {
        if (meno.length() > 8) {
            this.meno = meno.substring(0, 8);
        } else {
            this.meno = meno;
        }
        this.financie = suma;
        this.pozicia = 0;
        this.vazen = false;
        this.nehnutelnosti = new ArrayList<>();
        this.bonusoveKarty = new ArrayList<>();
        this.bankrot = false;

    }

    /**
     * Posunie hraca o urcenu vzdialenost
     * @param velkostPosunu Cislo o kolko policok sa hrac posunie
     */
    public void posunO(int velkostPosunu) {
        if (this.pozicia + velkostPosunu >= HraciaPlocha.getVelkostPlochy()) {
            int prechodStartom = 200;
            this.pozicia += velkostPosunu;
            this.financie += prechodStartom;
            this.pozicia -= HraciaPlocha.getVelkostPlochy();
            return;
        }
        if (this.pozicia + velkostPosunu < 0) {
            this.pozicia += velkostPosunu;
            this.pozicia = this.pozicia + HraciaPlocha.getVelkostPlochy();
            return;
        }
        this.pozicia += velkostPosunu;
    }

    /**
     * Prida sa nehnutelnost do zoznamu hracovych nehnutelnosti
     * @param pozemok Pozemok, ktory sa pridava do zoznamu
     */
    public void pridajNehnutelnost(Pozemok pozemok) {
        this.nehnutelnosti.add(pozemok);
    }

    /**
     * Hracovy sa nastavy vazen na zadanu hodnotu a ak je vazen, tak si hrac poziada od hracej plochy vazenia a presunie sa na najblizsie vazenei, ktore sa nachadza za nim
     * @param vazen Boolean ci sa ma hrac nastavit ako vazen
     * @param spravca Spravca hry
     */
    public void setVazen(boolean vazen, Spravca spravca) {
        this.vazen = vazen;
        if (vazen) {
            List<Integer> vazenia = new ArrayList<>(HraciaPlocha.getVazenia());
            if (vazenia.isEmpty()) {
                return;
            }
            Collections.reverse(vazenia);
            for (Integer i : vazenia) {
                if (i < this.pozicia) {
                    System.out.println(i);
                    this.pozicia = i;
                    return;
                }
            }
            this.pozicia = vazenia.get(0);
            spravca.setPovoleneMoznosti(false);
            spravca.aktualizujGrafiku();
        }
    }

    /**
     * Zmenia sa hracovy financie o zadanu hodnotu
     * @param cena Suma o ktoru sa hraocvy menia peniaze
     */
    public void zmenFinancie(int cena) {
        this.financie += cena;
    }

    /**
     * Prida hracovy do inventara kartu
     * @param karta Karta na pridanie
     */
    public void pridajBonusovuKartu(BonusoveKarty karta) {
        this.bonusoveKarty.add(karta);
    }

    /**
     * Zmaze hracovy z inventara bonusovu kartu
     * @param karta Karta na vymazanie
     */
    public void zmazKartu(BonusoveKarty karta) {
        this.bonusoveKarty.remove(karta);
    }

    /**
     * Hrac ohlasy bankrot a vsetky nehnutelnosti ktore vlastnil stratia vlastnika
     */
    public void ohlasBankrot() {
        this.bankrot = true;
        for (Pozemok p : this.nehnutelnosti) {
            p.setVlastnik(null);
        }
    }

    public boolean isVazen() {
        return this.vazen;
    }

    public int getPozicia() {
        return this.pozicia;
    }

    public int getFinancie() {
        return this.financie;
    }

    public String getMeno() {
        return this.meno;
    }

    public List<Pozemok> getPozemky() {
        return Collections.unmodifiableList(this.nehnutelnosti);
    }

    public boolean isBankrot() {
        return this.bankrot;
    }

    public List<BonusoveKarty> getBonusoveKarty() {
        return Collections.unmodifiableList(this.bonusoveKarty);
    }
}