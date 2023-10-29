package zaklad;

import grafika.Grafika;
import hraci.Hrac;
import javax.swing.JOptionPane;

public class Main {
    /**
     * Spusta hru, pyta sa ci hrac chce vlastnu verziu hry, nacitava hracov a spusta grafiku a spravcu.
     * @param args Nevyuzite
     */
    public static void main(String[] args) {
        int moznostPriStarte = JOptionPane.showConfirmDialog(null, "Chcete nacitat vlastnu veriu hry?", "Monopoly", JOptionPane.YES_NO_OPTION);
        if (moznostPriStarte == -1) {
            System.exit(0);
        }
        Spravca spravca = new Spravca(null, moznostPriStarte == 0);
        int zaciatocnePeniaze = 1500;
        if (moznostPriStarte == 0) {
            while (true) {
                String sumaNaZaciatok = JOptionPane.showInputDialog(null, "Zadajte sumu s ktorou budu hraci zacinat");
                if (sumaNaZaciatok == null) {
                    System.exit(0);
                }
                try {
                    if (Integer.parseInt(sumaNaZaciatok) > -1) {
                        zaciatocnePeniaze = Integer.parseInt(sumaNaZaciatok);
                        break;
                    }
                } catch (NumberFormatException ignored) {
                }
                JOptionPane.showMessageDialog(null, "Zadajte kladnu sumu iba v celom cisle, ktora sa zmesti do int", "Neplatne cislo", JOptionPane.ERROR_MESSAGE);
            }
        }
        // JOptionPane showOptionDialog bol inspirovany zo stranky nizsie
        // https://mkyong.com/swing/java-swing-joptionpane-showoptiondialog-example/
        String[] moznosti = {"2", "3", "4", "5", "6", "7", "8"};
        int moznost = JOptionPane.showOptionDialog(null, "Vyberte pocet hracov",
                " Vyberte pocet hraacov",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, moznosti, moznosti[0]);
        if (moznost == -1) {
            System.exit(0);
        }
        for (int i = 0; i < moznost + 2; i++) {
            while (true) {
                String meno = JOptionPane.showInputDialog("Zadajte meno " + (i + 1) + ". hraca. Prvy znak musi byt pismeno");
                if (meno == null) {
                    if (JOptionPane.showConfirmDialog(null, "Chcete ukoncit aplikaciu?", "Monopoly", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        System.exit(0);
                    } else {
                        continue;
                    }
                }
                if (meno.length() < 1) {
                    continue;
                }
                if (Character.isLetter(meno.charAt(0))) {
                    spravca.pridajHraca(new Hrac(meno, zaciatocnePeniaze));
                    break;
                }
            }
        }
        Grafika grafika = new Grafika(spravca);
        spravca.setGrafika(grafika);
    }
}