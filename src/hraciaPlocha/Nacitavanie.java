package hraciaPlocha;

import hraciaPlocha.policka.Start;
import hraciaPlocha.policka.Sanca;
import hraciaPlocha.policka.Vazenie;
import hraciaPlocha.policka.PosielanieDoVazenia;
import hraciaPlocha.policka.Truhlica;
import hraciaPlocha.policka.Dan;
import hraciaPlocha.policka.Parkovanie;
import hraciaPlocha.policka.Policko;
import hraciaPlocha.policka.kupa.Nehnutelnost;
import hraciaPlocha.policka.kupa.Pozemok;
import hraciaPlocha.policka.kupa.TypPozemku;
import karty.BonusoveKarty;
import karty.DruhKarty;
import karty.Priepustka;
import karty.NaratFinancii;
import karty.Posun;
import karty.Platba;
import karty.ZaMreze;
import karty.DruhPlatby;
import javax.swing.JOptionPane;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public abstract class Nacitavanie {
    /**
     * Nacita sa balik kariet zo suboru karty.csv moze sluzit na nacitavanie vlastnych typov kariet zo suboru
     * @param druhkarty Druh karty ktory sa prave nacitava
     * @return ArrayList bonusovych kariet, ktore sa nacitali
     */
    public static ArrayList<BonusoveKarty> nacitajBalikKariet(DruhKarty druhkarty) {
        ArrayList<BonusoveKarty> karty = new ArrayList<>();
        int cisloRiadka = 0;
        try {
            File subor = new File("assets/karty.csv");
            Scanner sc = new Scanner(subor);
            boolean citaj = false;
            while (sc.hasNextLine()) {
                String riadok = sc.nextLine();
                cisloRiadka++;
                if (riadok.equals("") || riadok.equals(" ") || riadok.length() < 2 || (riadok.charAt(0) == '/') && riadok.charAt(1) == '/') {
                    continue;
                }
                String[] stlpce = riadok.split(",");
                if (stlpce[0].trim().toUpperCase().contains(druhkarty.toString().toUpperCase().trim())) {
                    citaj = true;
                    continue;
                }
                if (stlpce[0].trim().contains("=") || stlpce[0].trim().contains("*") || stlpce[0].trim().contains("_")) {
                    citaj = false;
                }
                if (!citaj) {
                    continue;
                }
                switch (stlpce[0].toUpperCase().trim()) {
                    case "NAVRATFINANCII" -> {
                        if (stlpce.length < 4) {
                            karty.add(new NaratFinancii(stlpce[1].toUpperCase().trim(), Integer.parseInt(stlpce[2].trim()), false));
                        } else {
                            karty.add(new NaratFinancii(stlpce[1].toUpperCase().trim(), Integer.parseInt(stlpce[2].trim()), true));
                        }
                    }
                    case "PLATBA" -> {
                        if (stlpce.length > 3 && stlpce[2].toUpperCase().trim().equals("DOMY")) {
                            karty.add(new Platba(DruhPlatby.valueOf(stlpce[2].toUpperCase().trim()), stlpce[1].toUpperCase().trim(), Integer.parseInt(stlpce[3].trim()), Integer.parseInt(stlpce[4].trim())));
                        } else {
                            karty.add(new Platba(DruhPlatby.valueOf(stlpce[2].toUpperCase().trim()), stlpce[1].toUpperCase().trim(), Integer.parseInt(stlpce[3].trim())));
                        }
                    }
                    case "POSUN" -> {
                        if (Nacitavanie.existujeEnumTypNehnutelnosti(stlpce[2].toUpperCase().trim())) {
                            karty.add(new Posun(stlpce[1].toUpperCase().trim(), TypPozemku.valueOf(stlpce[2].toUpperCase().trim())));
                        } else {
                            try {
                                karty.add(new Posun(stlpce[1].toUpperCase().trim(), Integer.parseInt(stlpce[2].trim())));
                            } catch (Exception e) {
                                karty.add(new Posun(stlpce[1].toUpperCase().trim(), stlpce[2].toUpperCase().trim()));
                            }
                        }
                    }
                    case "PRIEPUSTKA" -> karty.add(new Priepustka(druhkarty));
                    case "ZAMREZE" -> karty.add(new ZaMreze());
                    default -> JOptionPane.showMessageDialog(null, "Upozornenie na riadku cislo: " + cisloRiadka + " nie je zrejmy o aky typ sa jedna. " + riadok + " Tato karta nebola pridana do balicka\nUistite sa ze Prvy udaj je niektora z moznosti:  NavratFinancii, Platba, Posun, Priepustka, ZaMreze. Nepouzivajte medzeri medzi slovami.", "Upozornenie pri citani kariet sanca", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            return karty;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Nastala chyba pri nacitavani kariet " + druhkarty.toString() + ": " + e + "\nChyba nastala pri nacitavani zo suboru na riadku cislo " + cisloRiadka + "\nPre tuto chybu sa namiesto nacitavania kariet zo suboru pouzili karty, ktore su zadane defaulte", "Cyba pri nacitavani kariet zo suboru", JOptionPane.ERROR_MESSAGE);
        }
        return Nacitavanie.nacitajDefaultBalikKariet(druhkarty);
    }

    /**
     * Metoda sluzi na to aby sa zistilo ci existuje enum s nazvom ktory sa zada v parametry
     * @param hladanyEnum Nazov enumu ktory sa ma hladat
     * @return true ak existuje zadany enum
     */
    private static boolean existujeEnumTypNehnutelnosti(String hladanyEnum) {
        TypPozemku[] enumy = TypPozemku.values();
        for (TypPozemku typPozemku : enumy) {
            if (typPozemku.toString().equals(hladanyEnum.trim().toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Nacita mapu zo suboru mapa.csv a podla tohto vyplna pole policok, ak nastane chyba, tak to oznami uzivatelovi a nacita zakladnu mapu
     * @return Pole nacitanych policok
     */
    public static Policko[] nacitajMapu() {
        Policko[] plocha;
        ArrayList<String> pozemky = new ArrayList<>();
        pozemky.add("DOPRAVA");
        pozemky.add("SPOLOCNOST");
        int aktualnyRiadok = 0;
        try {
            File subor = new File("assets/mapa.csv");
            Scanner riadky = new Scanner(subor);
            int pocetRiadkov = 0;
            while (riadky.hasNextLine()) {
                String riadok = riadky.nextLine();
                if (riadok.equals("") || riadok.equals(" ") || riadok.length() < 2 || (riadok.charAt(0) == '/') && riadok.charAt(1) == '/') {
                    continue;
                }
                pocetRiadkov++;
            }
            riadky.close();
            plocha = new Policko[pocetRiadkov];
            Scanner sc = new Scanner(subor);
            int riadokNaZapis = 0;
            while (sc.hasNextLine()) {
                String riadok = sc.nextLine();
                aktualnyRiadok++;
                if (riadok.equals("") || riadok.equals(" ") || riadok.length() < 2 || (riadok.charAt(0) == '/') && riadok.charAt(1) == '/') {
                    continue;
                }
                String[] stlpce = riadok.split(",");
                switch (stlpce[0].toUpperCase().trim()) {
                    case "START" -> {
                        plocha[riadokNaZapis] = new Start();
                    }
                    case "VAZENIE" -> {
                        plocha[riadokNaZapis] = new Vazenie();
                    }
                    case "PARKOVANIE" -> {
                        plocha[riadokNaZapis] = new Parkovanie();
                    }
                    case "POSIELANIEDOVAZENIA" -> {
                        plocha[riadokNaZapis] = new PosielanieDoVazenia();
                    }
                    case "SANCA" -> {
                        plocha[riadokNaZapis] = new Sanca();
                    }
                    case "TRUHLICA" -> {
                        plocha[riadokNaZapis] = new Truhlica();
                    }
                    case "DAN" ->  {
                        if (stlpce.length < 4) {
                            plocha[riadokNaZapis] = new Dan(stlpce[1], Integer.parseInt(stlpce[2].trim()), 0);
                        } else {
                            plocha[riadokNaZapis] = new Dan(stlpce[1], Integer.parseInt(stlpce[2].trim()), Integer.parseInt(stlpce[3].trim()));
                        }
                    }
                    default -> {
                        if (pozemky.contains(stlpce[2].trim().toUpperCase())) {
                            plocha[riadokNaZapis] = new Pozemok(stlpce[0].trim().toUpperCase(), Integer.parseInt(stlpce[1].trim()), TypPozemku.valueOf(stlpce[2].toUpperCase().trim()), Integer.parseInt(stlpce[3].trim()));
                        } else {
                            plocha[riadokNaZapis] = new Nehnutelnost(stlpce[0].trim().toUpperCase(), Integer.parseInt(stlpce[1].trim()), TypPozemku.valueOf(stlpce[2].toUpperCase().trim()), Integer.parseInt(stlpce[3].trim()), Integer.parseInt(stlpce[4].trim()), Integer.parseInt(stlpce[5].trim()), Integer.parseInt(stlpce[6].trim()), Integer.parseInt(stlpce[7].trim()), Integer.parseInt(stlpce[8].trim()));
                        }
                    }
                }
                riadokNaZapis++;
            }
            return plocha;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Nastala chyba pri nacitavani mapy zo suboru na riadku cislo " + aktualnyRiadok + " typ chyby: " + e + "\nNacitava sa default mapa", "Nastala chyba pri nacitavani mapy zo suboru", JOptionPane.ERROR_MESSAGE);
        }
        return Nacitavanie.nacitajDefaultMapu();
    }

    /**
     * Nacitava zakladu mapu hry
     * @return Zakladna mapa hry
     */
    public static Policko[] nacitajDefaultMapu() {
        Policko[] plocha;
        plocha = new Policko[40];
        plocha[0] = new Start();
        plocha[1] = new Nehnutelnost("MEDITERAREAN AVENUE", 60, TypPozemku.FIALOVA, 2, 10, 30, 90, 160, 250);
        plocha[2] = new Truhlica();
        plocha[3] = new Nehnutelnost("BALTIC AVENUE", 60, TypPozemku.FIALOVA, 4, 20, 60, 180, 320 , 450);
        plocha[4] = new Dan("DAN Z PRIJMU", 200, 10);
        plocha[5] = new Pozemok("READING RAILROAD", 200, TypPozemku.DOPRAVA, 25);
        plocha[6] = new Nehnutelnost("ORIENTAL AVENUE", 100, TypPozemku.BIELA, 6, 30, 90, 270, 400, 500);
        plocha[7] = new Sanca();
        plocha[8] = new Nehnutelnost("VERMONT AVENUE", 100, TypPozemku.BIELA, 6, 30, 90, 270, 400, 550);
        plocha[9] = new Nehnutelnost("CONNECTICUT AVENUE", 120, TypPozemku.BIELA, 8, 40, 100, 300, 450, 600);
        plocha[10] = new Vazenie();
        plocha[11] = new Nehnutelnost("ST. CHARLES PLACE", 140, TypPozemku.RUZOVA, 10, 50, 150, 450, 625, 750);
        plocha[12] = new Pozemok("ELEKTRINA", 150, TypPozemku.SPOLOCNOST, 4);
        plocha[13] = new Nehnutelnost("STATES AVENUE", 140, TypPozemku.RUZOVA, 10, 50, 150, 450, 625, 750);
        plocha[14] = new Nehnutelnost("VIRGINIA AVENUE", 160, TypPozemku.RUZOVA, 12, 60, 180, 500, 700, 900);
        plocha[15] = new Pozemok("PENSYLVANIA RAILROAD", 200, TypPozemku.DOPRAVA, 25);
        plocha[16] = new Nehnutelnost("ST. JAMES PLACE", 180, TypPozemku.ORANZOVA, 14, 70, 200, 550, 750, 950);
        plocha[17] = new Truhlica();
        plocha[18] = new Nehnutelnost("TENESSEE AVENUE", 180, TypPozemku.ORANZOVA, 14, 70, 200, 550, 750, 950);
        plocha[19] = new Nehnutelnost("NEW YORK AVENUE", 200, TypPozemku.ORANZOVA, 16, 80, 220, 600, 800, 1000);
        plocha[20] = new Parkovanie();
        plocha[21] = new Nehnutelnost("KENTUCKY AVENUE", 220, TypPozemku.CERVENA, 18, 90, 250, 700, 875, 1050);
        plocha[22] = new Sanca();
        plocha[23] = new Nehnutelnost("INDIANA AVENUE", 220, TypPozemku.CERVENA, 18, 90, 250, 700, 875, 1050);
        plocha[24] = new Nehnutelnost("ILLINOIS AVENUE", 240, TypPozemku.CERVENA, 20, 100, 300, 750, 925, 1050);
        plocha[25] = new Pozemok("B & O. RAILROAD", 200, TypPozemku.DOPRAVA, 25);
        plocha[26] = new Nehnutelnost("ATLANTIC AVENUE", 260, TypPozemku.ZLTA, 22, 110, 330, 800, 975, 1150);
        plocha[27] = new Nehnutelnost("VENTOR AVENUE", 260, TypPozemku.ZLTA, 22, 110, 330, 800, 975, 1150);
        plocha[28] = new Pozemok("VODA", 150, TypPozemku.SPOLOCNOST, 4);
        plocha[29] = new Nehnutelnost("MARVIN GARDENS", 280, TypPozemku.ZLTA, 24, 120, 360, 850, 1025, 1200);
        plocha[30] = new PosielanieDoVazenia();
        plocha[31] = new Nehnutelnost("PACIFIC AVENUE", 300, TypPozemku.ZELENA, 26, 130, 390, 900, 1100, 1275);
        plocha[32] = new Nehnutelnost("NORTH CAROLINA AVENUE", 300, TypPozemku.ZELENA, 26, 130, 390, 900, 1100, 1275);
        plocha[33] = new Truhlica();
        plocha[34] = new Nehnutelnost("PENSYLVANIA AVENUE", 330, TypPozemku.ZELENA, 28, 150, 450, 1000, 1200, 1400);
        plocha[35] = new Pozemok("SHORT LINE", 200, TypPozemku.DOPRAVA, 25);
        plocha[36] = new Sanca();
        plocha[37] = new Nehnutelnost("PARK PLACE", 350, TypPozemku.MODRA, 35, 175, 500, 1100, 1300, 1500);
        plocha[38] = new Dan("DAN Z LUXUSU", 75, 0);
        plocha[39] = new Nehnutelnost("BOARDWALK", 400, TypPozemku.MODRA, 50, 200, 600, 1400, 1700, 2000);
        return plocha;
    }

    /**
     * Nacitava zakladne karty hry podla ich druhu
     * @param druhKarty Zvoleny druh kariet na nacitanie
     * @return ArrayList nacitanych kariet
     */
    public static ArrayList<BonusoveKarty> nacitajDefaultBalikKariet(DruhKarty druhKarty) {
        ArrayList<BonusoveKarty> karty = new ArrayList<>();
        if (druhKarty == DruhKarty.SANCA) {
            karty.add(new Posun("Posun sa na BroadWalk", "BroadWalk"));
            karty.add(new Posun("Posun sa na Start", "Start"));
            karty.add(new Posun("Posun sa na Illinois Avenue", "Illinois Avenue"));
            karty.add(new Posun("Posun sa na St. Charles Place", "St. Charles Place"));
            karty.add(new Posun("Posun sa na najblizsiu zeleznicu", TypPozemku.DOPRAVA));
            karty.add(new Posun("Posun sa na najblizsiu zeleznicu", TypPozemku.DOPRAVA));
            karty.add(new Posun("Posun sa na najblizsiu spolocnost", TypPozemku.SPOLOCNOST));
            karty.add(new NaratFinancii("Banka vam vyplatila dividenty vo vyske 50$", 50, false));
            karty.add(new Priepustka(DruhKarty.SANCA));
            karty.add(new Posun("Posun sa o 3 policka spat", -3));
            karty.add(new ZaMreze());
            karty.add(new Platba(DruhPlatby.DOMY, "Generalna oprava zaplatte za kazdy dom 25$ a hotel 100$", 25, 100));
            karty.add(new Platba(DruhPlatby.JEDNORAZOVA, "Pokuta vo vyske 15$", 15));
            karty.add(new Posun("Chodte na reading Railroad", "Reading Railroad"));
            karty.add(new Platba(DruhPlatby.OSTATNYM_HRACOM, "Boli ste zvolený za predsedu správnej rady. Vyplaťte každému hráčovi 50$", 50));
            karty.add(new NaratFinancii("Splatnosť vášho stavebného úveru. Zinkasujte 150$", 150, false));
        }
        if (druhKarty == DruhKarty.TRUHLICA) {
            karty.add(new Posun("Posun sa na Start", "Start"));
            karty.add(new NaratFinancii("Bankova chyba ziskavate 200$", 200, false));
            karty.add(new Platba(DruhPlatby.JEDNORAZOVA, "Platba pre doktora 50$", 50));
            karty.add(new NaratFinancii("Z bury dostavate 50$", 50, false));
            karty.add(new Priepustka(DruhKarty.TRUHLICA));
            karty.add(new ZaMreze());
            karty.add(new NaratFinancii("Dovolenkový fond dozrieva. Príjem 100$", 100, false));
            karty.add(new NaratFinancii("Navrat dani, ziskavate 20$", 20, false));
            karty.add(new NaratFinancii("Mate narodeniny, od kazdeho hraca ziskavate 10$", 10, true));
            karty.add(new NaratFinancii("Doby splatnosti životného poistenia. Zinkasujte 100$", 100, false));
            karty.add(new Platba(DruhPlatby.JEDNORAZOVA, "Nemocnicne poplatky vo vyske 100$", 100));
            karty.add(new Platba(DruhPlatby.JEDNORAZOVA, "Zaplatte skolne vo vyske 50$", 50));
            karty.add(new NaratFinancii("Ziskavate poplatok za konzultaciu vo vyske 20$", 20, false));
            karty.add(new Platba(DruhPlatby.DOMY, "Gerneralna oprava zaplatte za kazdy dom 40$ a hotel 115$", 40, 115));
            karty.add(new NaratFinancii("Vyhrali ste v sutaze krasy 10$", 10, false));
            karty.add(new NaratFinancii("Dostali ste 100$", 100, false));
        }
        return karty;
    }
}