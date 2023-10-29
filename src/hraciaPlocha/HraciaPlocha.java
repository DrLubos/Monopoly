package hraciaPlocha;

import hraciaPlocha.policka.Policko;
import hraciaPlocha.policka.Vazenie;
import hraciaPlocha.policka.kupa.Pozemok;
import hraciaPlocha.policka.kupa.TypPozemku;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class HraciaPlocha {
    private static Policko[] plocha;

    /**
     * Metoda odkaze na triedu Nacitavanie a pomocou nej sa nacitava mapa do atributu
     * @param vlastnaVerzia Ak chce hrac nacitat vlastnu mapu, tak sa tu posle true, ak chce nacitat predvolenu, tak sa zada false
     */
    public static void nacitajMapu(boolean vlastnaVerzia) {
        if (vlastnaVerzia) {
            plocha = Nacitavanie.nacitajMapu();
        } else  {
            plocha = Nacitavanie.nacitajDefaultMapu();
        }
    }

    /**
     * Vracia Optional podla toho ci naslo nejake policko podla zadanehu typu nehnutelnosti
     * @param typ Typ policka na vyhladavanie
     * @param start Start na akom indexe sa ma zacat
     * @return Optional najdenych policok
     */
    public static Optional<List<Integer>> vyhladajPodlaTypu(TypPozemku typ, int start) {
        ArrayList<Integer> policka = new ArrayList<>();
        if (start < plocha.length) {
            for (int i = start; i < plocha.length; i++) {
                if (plocha[i].getNazov().toUpperCase().trim().equals(typ.toString().trim().toUpperCase()) || (plocha[i] instanceof Pozemok && ((Pozemok)plocha[i]).getTyp() == typ)) {
                    policka.add(i);
                }
            }
            for (int i = 0; i <= start; i++) {
                if (plocha[i].getNazov().toUpperCase().trim().equals(typ.toString().trim().toUpperCase()) || (plocha[i] instanceof Pozemok && ((Pozemok)plocha[i]).getTyp() == typ)) {
                    policka.add(i);
                }
            }
        } else {
            for (int i = 0; i < plocha.length; i++) {
                if (plocha[i].getNazov().toUpperCase().trim().equals(typ.toString().trim().toUpperCase()) || (plocha[i] instanceof Pozemok && ((Pozemok)plocha[i]).getTyp() == typ)) {
                    policka.add(i);
                }
            }
        }
        if (policka.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(Collections.unmodifiableList(policka));
    }

    /**
     * Metoda vyhladava policka podla nazvu
     * @param nazov Nazov podla ktoreho sa yvhladava
     * @param start Index pre zaciatok odkial sa vyhladava
     * @return Optional najdenych policok
     */
    public static Optional<List<Integer>> vyhladajPodlaNazvu(String nazov, int start) {
        ArrayList<Integer> policka = new ArrayList<>();
        if (start < plocha.length) {
            for (int i = start; i < plocha.length; i++) {
                if (plocha[i].getNazov().equals(nazov.trim().toUpperCase())) {
                    policka.add(i);
                }
            }
            for (int i = 0; i <= start; i++) {
                if (plocha[i].getNazov().equals(nazov.trim().toUpperCase())) {
                    policka.add(i);
                }
            }
        } else {
            for (int i = 0; i < plocha.length; i++) {
                if (plocha[i].getNazov().equals(nazov.trim().toUpperCase())) {
                    policka.add(i);
                }
            }
        }
        if (policka.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(Collections.unmodifiableList(policka));
    }

    /**
     * Metoda hlada instancie triedy Vazenie v poli plocha
     * @return List cisiel, ktore znacia na ktorych indexoch je vazenie
     */
    public static List<Integer> getVazenia() {
        List<Integer> cislaPolicok = new ArrayList<>();
        for (int i = 0; i < plocha.length; i++) {
            if (plocha[i] instanceof Vazenie) {
                cislaPolicok.add(i);
            }
        }
        return Collections.unmodifiableList(cislaPolicok);
    }

    /**
     * Metoda vracai pocet policok na hracej ploche, ktore maju rovnaky typ
     * @param typ Typ o ktory sa zaujmame
     * @return Pocet kolko policok je zadaneho typu
     */
    public static int getRovnakyTyp(TypPozemku typ) {
        int sucet = 0;
        for (Policko policko : plocha) {
            if (policko instanceof Pozemok) {
                if (((Pozemok)policko).getTyp() == typ) {
                    sucet++;
                }
            }
        }
        return sucet;
    }

    public static Policko getPolicko(int cisloPolicka) {
        return plocha[cisloPolicka];
    }

    public static int getVelkostPlochy() {
        return plocha.length;
    }
}