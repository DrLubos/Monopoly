package grafika;

import hraci.Hrac;
import hraciaPlocha.HraciaPlocha;
import hraciaPlocha.policka.Policko;
import hraciaPlocha.policka.kupa.Nehnutelnost;
import hraciaPlocha.policka.kupa.Pozemok;
import hraciaPlocha.policka.kupa.TypPozemku;
import zaklad.Spravca;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Grafika {
    private final Spravca spravca;
    private final JFrame okno;
    private JButton[] polickaB;
    private JLabel[] hraciL;
    private JLabel hracNaTahuL;
    private JButton ukonciKrokB;
    private JLabel vysledokKociekL;
    private JButton hodKockamiB;
    private JButton obchodovanieB;
    private JButton postavDomB;
    private JButton predajDomB;
    private JButton pozickaB;
    private JButton splatenieB;
    private boolean mozemHadzat;
    private int zvolenaPozicia;
    private JLabel[] zvolenePolicko;
    private JPanel zvolenePolickoPanel;
    private boolean povolMoznosti;

    /**
     * Grafika vytvara instanciu JFrame a pridava do neho komponenty na zobrazenie
     * @param spravca Spravca hry na komunikovanie
     */
    public Grafika(Spravca spravca) {
        this.spravca = spravca;
        this.mozemHadzat = true;
        this.zvolenaPozicia = -1;
        this.povolMoznosti = true;
        // Nefungovalo mi menenie farby, tak podla odkazu nizsie som nastavil LookAndFeel
        // https://www.tabnine.com/code/java/classes/javax.swing.plaf.nimbus.NimbusLookAndFeel
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }
        this.okno = new JFrame("Monopoly");
        this.okno.setLayout(new BorderLayout());
        this.okno.add(this.nacitajHraciuPlochu(), BorderLayout.WEST);
        this.okno.add(this.nacitajMoznosti(), BorderLayout.EAST);
        this.okno.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.okno.pack();
        this.okno.setVisible(true);
        this.okno.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if ( JOptionPane.showConfirmDialog(Grafika.this.okno, "Chces naozaj skoncit", "Ukoncenie", JOptionPane.YES_NO_OPTION)
                        == JOptionPane.YES_OPTION) {
                    System.exit(0);
                } else {
                    Grafika.this.okno.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                }
            }
        });
    }

    /**
     * Nacitava to pravu cast okna, s tlacidlami akcii
     * @return JPanel s tlacidlami akcii a inych komponentov na pravej strane okna
     */
    private JPanel nacitajMoznosti() {
        this.hracNaTahuL = new JLabel();
        this.aktualizujHraca(0);
        this.hracNaTahuL.setHorizontalAlignment(0);
        JPanel moznostiCelokPanel = new JPanel(new GridLayout(4, 2));
        this.ukonciKrokB = new JButton("<html><span style=\"font-size:20\">Ukoncit tah</span></html>");
        this.ukonciKrokB.addActionListener(e -> {
            if (!this.mozemHadzat) {
                this.spravca.zmenHraca();
                this.umozniHadzanie();
            }
        });
        JPanel kockyPanel = new JPanel(new GridLayout(2, 1));
        this.vysledokKociekL = new JLabel();
        this.aktualizujKocky();
        this.hodKockamiB = new JButton("<html><span style=\"font-size:20\">Hod kockami</span></html>");
        this.hodKockamiB.addActionListener(e -> {
            if (this.mozemHadzat) {
                this.mozemHadzat = false;
                this.hodKockamiB.setBackground(Color.RED);
                this.spravca.hodKockami();
                this.aktualizujKocky();
            }
        });
        kockyPanel.add(this.hodKockamiB);
        kockyPanel.add(this.vysledokKociekL);
        this.obchodovanieB = new JButton("<html><span style=\"font-size:20\">Obchodovanie</span></html>");
        this.obchodovanieB.addActionListener(e -> {
            if (this.povolMoznosti && this.kontrolaZvoleniaPolicka()) {
                if (HraciaPlocha.getPolicko(this.zvolenaPozicia) instanceof Pozemok && ((Pozemok)HraciaPlocha.getPolicko(this.zvolenaPozicia)).getVlastnik() != this.spravca.getHraci().get(this.spravca.getCisloHracaNaTahu())) {
                    this.spravca.spustiObchod(this.zvolenaPozicia);
                } else {
                    JOptionPane.showMessageDialog(null, "Tuto nehnutelnost nemozete obchodovat, lebo ste jej vlastnikom", "Nie je mozne spustit obchodovanie", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        this.postavDomB = new JButton("<html><span style=\"font-size:20\">Postav Dom</span></html>");
        this.postavDomB.addActionListener(e -> {
            if (this.povolMoznosti && this.kontrolaZvoleniaPolicka()) {
                this.spravca.postavajDom(this.zvolenaPozicia);
            }
        });
        this.predajDomB = new JButton("<html><span style=\"font-size:20\">Predaj Dom</span></html>");
        this.predajDomB.addActionListener(e -> {
            if (this.povolMoznosti && this.kontrolaZvoleniaPolicka()) {
                this.spravca.predajDom(this.zvolenaPozicia);
            }
        });
        this.pozickaB = new JButton("<html><span style=\"font-size:20\">Pozicaj</span><br><span>50% z ceny</span></html>");
        this.pozickaB.addActionListener(e -> {
            if (this.povolMoznosti && this.kontrolaZvoleniaPolicka()) {
                this.spravca.zalozBanke(this.zvolenaPozicia);
            }
        });
        this.splatenieB = new JButton("<html><span style=\"font-size:20\">Splat</span><br><span>10% urok</span></html>");
        this.splatenieB.addActionListener(e -> {
            if (this.povolMoznosti && this.kontrolaZvoleniaPolicka()) {
                this.spravca.vyplatBanku(this.zvolenaPozicia);
            }
        });
        moznostiCelokPanel.add(this.hracNaTahuL);
        moznostiCelokPanel.add(this.ukonciKrokB);
        moznostiCelokPanel.add(kockyPanel);
        moznostiCelokPanel.add(this.obchodovanieB);
        moznostiCelokPanel.add(this.postavDomB);
        moznostiCelokPanel.add(this.predajDomB);
        moznostiCelokPanel.add(this.pozickaB);
        moznostiCelokPanel.add(this.splatenieB);
        return moznostiCelokPanel;
    }

    /**
     * Povoluje alebo zakazuje to tlacidla akcii a ich aj prefarbi
     * @param povolMoznosti True ak ma hrac povolene tlacidla akcii
     */
    public void setPovolMoznosti(boolean povolMoznosti) {
        this.povolMoznosti = povolMoznosti;
        if (!povolMoznosti) {
            this.obchodovanieB.setBackground(Color.RED);
            this.postavDomB.setBackground(Color.RED);
            this.predajDomB.setBackground(Color.RED);
            this.pozickaB.setBackground(Color.RED);
            this.splatenieB.setBackground(Color.RED);
            this.mozemHadzat = false;
        } else {
            this.obchodovanieB.setBackground(null);
            this.postavDomB.setBackground(null);
            this.predajDomB.setBackground(null);
            this.pozickaB.setBackground(null);
            this.splatenieB.setBackground(null);
        }
    }

    /**
     * Kontrola ci hrac zvolil nejaku nehnutelnost
     * @return True, ak je zvolena nejaka nehnutelnost
     */
    private boolean kontrolaZvoleniaPolicka() {
        if (this.zvolenaPozicia >= 0 && this.zvolenaPozicia < HraciaPlocha.getVelkostPlochy()) {
            return true;
        }
        JOptionPane.showMessageDialog(null, "Najskor zvolte nehnutelnost stlacenim na jej policko", "Nie je mozne spustit zvolenu akciu", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    /**
     * Nacitava panel zvolenej nehnutelnosti, alebo aj tlacidla
     */
    private void nacitajZvolenuNehnutelnostPanel() {
        this.zvolenePolicko = new JLabel[11];
        for (int i = 0; i < this.zvolenePolicko.length; i++) {
            this.zvolenePolicko[i] = new JLabel("");
            this.zvolenePolicko[i].setHorizontalAlignment(0);
            this.zvolenePolickoPanel.add(this.zvolenePolicko[i]);
        }
        this.zvolenePolicko[0].setText("<html><center style=\"font-size:20\"><b>Vybrane Policko</b></center></html>");
    }

    /**
     * Umozni to hadzanie kockami
     */
    private void umozniHadzanie() {
        if (this.hodKockamiB != null) {
            this.hodKockamiB.setBackground(null);
        }
        this.mozemHadzat = true;
        this.ukonciKrokB.setBackground(Color.RED);
    }

    /**
     * Nacitava to hraciu plochu, konkretne celu lavu cast okna, kde sa nachadzaju tlacidla, informacie o hracoch a o zvolenom policku
     * @return JPanel hracej plochy
     */
    private JPanel nacitajHraciuPlochu() {
        JPanel hraciaPlocha = new JPanel(new BorderLayout());
        JPanel severHP = new JPanel(new GridLayout(1, 10));
        JPanel juhHP = new JPanel(new GridLayout(1, 10));
        JPanel vychodHP = new JPanel(new GridLayout(9, 1));
        JPanel zapadHP = new JPanel(new GridLayout(9, 1));
        int pocetPolicok = HraciaPlocha.getVelkostPlochy();
        this.polickaB = new JButton[pocetPolicok];
        for (int i = 0; i < pocetPolicok; i++) {
            this.polickaB[i] = new JButton(this.tlacidloFormatovanieRiadku(i, HraciaPlocha.getPolicko(i).getNazov(), null));
            if (HraciaPlocha.getPolicko(i) instanceof Pozemok) {
                this.polickaB[i] = new JButton(this.tlacidloFormatovanieRiadku(i, HraciaPlocha.getPolicko(i).getNazov(), String.valueOf(((Pozemok)HraciaPlocha.getPolicko(i)).getCena())));
                switch (((Pozemok)HraciaPlocha.getPolicko(i)).getTyp()) {
                    case ZLTA -> this.polickaB[i].setBackground(Color.YELLOW);
                    case ZELENA -> this.polickaB[i].setBackground(Color.GREEN);
                    case ORANZOVA -> this.polickaB[i].setBackground(Color.ORANGE);
                    case RUZOVA -> this.polickaB[i].setBackground(Color.PINK);
                    case CERVENA -> this.polickaB[i].setBackground(Color.RED);
                    case MODRA -> this.polickaB[i].setBackground(new Color(0, 70, 200));
                    case BIELA -> this.polickaB[i].setBackground(Color.CYAN);
                    case FIALOVA -> this.polickaB[i].setBackground(Color.MAGENTA);
                    case SPOLOCNOST -> this.polickaB[i].setBackground(new Color(240, 100, 10));
                    case DOPRAVA -> this.polickaB[i].setBackground(Color.GRAY);
                }
            }
            this.polickaB[i].setPreferredSize(new Dimension(100, 80));
            final int pozicia = i;
            this.polickaB[i].addActionListener(e -> {
                this.zvolenaPozicia = pozicia;
                this.aktualizujZvolenePolicko();
            });
        }
        for (int i = 0; i <= this.polickaB.length / 4; i++) {
            severHP.add(this.polickaB[i]);
        }
        for (int i = this.polickaB.length / 4 + 1; i < this.polickaB.length / 2; i++) {
            vychodHP.add(this.polickaB[i]);
        }
        for (int i = this.polickaB.length / 4 * 3; i >= this.polickaB.length / 2; i--) {
            juhHP.add(this.polickaB[i]);
        }
        for (int i = this.polickaB.length - 1; i > this.polickaB.length / 4 * 3; i--) {
            zapadHP.add(this.polickaB[i]);
        }
        hraciaPlocha.add(severHP, BorderLayout.NORTH);
        hraciaPlocha.add(juhHP, BorderLayout.SOUTH);
        hraciaPlocha.add(zapadHP, BorderLayout.WEST);
        hraciaPlocha.add(vychodHP, BorderLayout.EAST);
        this.nacitajHracov();
        JPanel stredPlochy = new JPanel(new GridLayout(1, 2));
        JPanel hraciP = new JPanel(new GridLayout(this.spravca.getPocetHracov(), 1));
        for (JLabel jLabel : this.hraciL) {
            hraciP.add(jLabel);
        }
        this.zvolenePolickoPanel = new JPanel(new GridLayout(11, 1));
        this.nacitajZvolenuNehnutelnostPanel();
        for (JLabel jLabel : this.zvolenePolicko) {
            this.zvolenePolickoPanel.add(jLabel);
        }
        stredPlochy.add(hraciP);
        stredPlochy.add(this.zvolenePolickoPanel);
        hraciaPlocha.add(stredPlochy, BorderLayout.CENTER);
        return hraciaPlocha;
    }

    /**
     * Nacitava to hracov hry
     */
    private void nacitajHracov() {
        this.hraciL = new JLabel[this.spravca.getPocetHracov()];
        for (int i = 0; i < this.spravca.getPocetHracov(); i++) {
            this.hraciL[i] = new JLabel(this.formatovanieHracov(i));
        }
    }

    /**
     * Aktualizuje to hraca, ktory je na kroku
     * @param cislo Cislo hraca
     */
    public void aktualizujHraca(int cislo) {
        this.hracNaTahuL.setText("<html><center style=\"font-size:20\">" + this.spravca.getHraci().get(cislo).getMeno() + "<br>" + this.spravca.getHraci().get(cislo).getFinancie() + "$<br>Pozicia:" + this.spravca.getHraci().get(cislo).getPozicia());
    }

    /**
     * Aktualizuje to zobrazenie kociek
     */
    public void aktualizujKocky() {
        int[] kocky = this.spravca.getKocky();
        StringBuilder sb = new StringBuilder();
        for (int j : kocky) {
            sb.append(j + " ");
        }
        if (kocky.length > 1) {
            if (kocky[0] == kocky[1] && !this.spravca.getHraci().get(this.spravca.getCisloHracaNaTahu()).isVazen()) {
                this.umozniHadzanie();
            } else {
                this.ukonciKrokB.setBackground(Color.GREEN);
                this.mozemHadzat = false;
            }
        }
        this.vysledokKociekL.setText("<html><center style=\"font-size:20\">KOCKY<br>" + sb.toString().trim() + "</center></html>");
        this.vysledokKociekL.setHorizontalAlignment(0);
        this.vysledokKociekL.validate();
    }

    /**
     * Znemoznuje hadzanie a povoluje ukoncit krok, pouzite pri ohlasovani bankrotu
     */
    public void spristupniKoniec() {
        this.ukonciKrokB.setBackground(Color.GREEN);
        this.mozemHadzat = false;
        this.hodKockamiB.setBackground(Color.RED);
    }

    /**
     * Aktualizuje to vsetkych hracov v hracej ploche
     */
    public void aktualizujHracov() {
        for (int i = 0; i < this.hraciL.length; i++) {
            this.hraciL[i].setText(this.formatovanieHracov(i));
        }
    }

    /**
     * Aktualizuje to informacie v tlacidlach na celom hracom poli
     */
    public void aktualizujPolicka() {
        this.aktualizujHracov();
        for (int i = 0; i < this.polickaB.length; i++) {
            if (HraciaPlocha.getPolicko(i) instanceof Pozemok) {
                if (((Pozemok)HraciaPlocha.getPolicko(i)).getVlastnik() == null) {
                    this.polickaB[i].setText(this.tlacidloFormatovanieRiadku(i, HraciaPlocha.getPolicko(i).getNazov(), String.valueOf(((Pozemok)HraciaPlocha.getPolicko(i)).getCena())));
                }
                if (((Pozemok)HraciaPlocha.getPolicko(i)).isPozicany()) {
                    this.polickaB[i].setText(this.tlacidloFormatovanieRiadku(i, HraciaPlocha.getPolicko(i).getNazov(), "ZALOZENY"));
                }
                if (!((Pozemok)HraciaPlocha.getPolicko(i)).isPozicany() && ((Pozemok)HraciaPlocha.getPolicko(i)).getVlastnik() != null) {
                    this.polickaB[i].setText(this.tlacidloFormatovanieRiadku(i, HraciaPlocha.getPolicko(i).getNazov(), ((Pozemok)HraciaPlocha.getPolicko(i)).getVlastnik().getMeno()));
                }
                if (!((Pozemok)HraciaPlocha.getPolicko(i)).isPozicany() && ((Pozemok)HraciaPlocha.getPolicko(i)).getVlastnik() != null && (HraciaPlocha.getPolicko(i) instanceof Nehnutelnost) && ((Nehnutelnost)HraciaPlocha.getPolicko(i)).getPocetDomov() > 0) {
                    this.polickaB[i].setText(this.tlacidloFormatovanieRiadku(i, HraciaPlocha.getPolicko(i).getNazov(), ((Pozemok)HraciaPlocha.getPolicko(i)).getVlastnik().getMeno()) + "<br>" + ((Nehnutelnost)HraciaPlocha.getPolicko(i)).getPocetDomov());
                }
            }
        }
    }

    /**
     * Aktualizuje to zobrazeneie zvoleneho policka
     */
    private void aktualizujZvolenePolicko() {
        if (this.zvolenaPozicia < 0) {
            return;
        }
        Policko policko = HraciaPlocha.getPolicko(this.zvolenaPozicia);
        this.zvolenePolicko[1].setText(this.formatovanieVypisuTlacidielPolicok(this.zvolenaPozicia + ". " + policko.getNazov()));
        if (policko instanceof Pozemok) {
            if (((Pozemok)policko).getVlastnik() == null) {
                this.zvolenePolicko[2].setText(this.formatovanieVypisuTlacidielPolicok("Vlastnik: Tento pozemok je na predaj"));
            } else if (policko instanceof Nehnutelnost) {
                this.zvolenePolicko[2].setText(this.formatovanieVypisuTlacidielPolicok("Vlastnik: " + ((Pozemok)policko).getVlastnik().getMeno() + " je tu " + ((Nehnutelnost)policko).getPocetDomov() + " domov"));
            } else {
                this.zvolenePolicko[2].setText(this.formatovanieVypisuTlacidielPolicok("Vlastnik: " + ((Pozemok)policko).getVlastnik().getMeno()));
            }
            if (policko instanceof Nehnutelnost) {
                this.zvolenePolicko[3].setText(this.formatovanieVypisuTlacidielPolicok("Najom: " + ((Nehnutelnost)policko).getNajom() + "$, 2x ak mate vsetky tohto typu"));
                this.zvolenePolicko[4].setText(this.formatovanieVypisuTlacidielPolicok("Najom s 1 domom: " + ((Nehnutelnost)policko).getjD() + "$"));
                this.zvolenePolicko[5].setText(this.formatovanieVypisuTlacidielPolicok("Najom s 2 domami: " + ((Nehnutelnost)policko).getdD() + "$"));
                this.zvolenePolicko[6].setText(this.formatovanieVypisuTlacidielPolicok("Najom s 3 domami: " + ((Nehnutelnost)policko).gettD() + "$"));
                this.zvolenePolicko[7].setText(this.formatovanieVypisuTlacidielPolicok("Najom s 4 domami: " + ((Nehnutelnost)policko).getsD() + "$"));
                this.zvolenePolicko[8].setText(this.formatovanieVypisuTlacidielPolicok("Najom s hotelom: " + ((Nehnutelnost)policko).getHotel() + "$"));
                this.zvolenePolicko[9].setText(this.formatovanieVypisuTlacidielPolicok("Hodnota za pozicanie banke: " + ((Nehnutelnost)policko).getCena() / 2 + "$"));
                this.zvolenePolicko[10].setText(this.formatovanieVypisuTlacidielPolicok("Cena vylepsenia(postavenia domu): " + ((Nehnutelnost)policko).getTyp().getCenaVylepsenia() + "$"));
                return;
            }
            if (((Pozemok)policko).getTyp() == TypPozemku.DOPRAVA) {
                this.zvolenePolicko[3].setText(this.formatovanieVypisuTlacidielPolicok("Najom: " + ((Pozemok)policko).getNajom() + "$"));
                this.zvolenePolicko[4].setText(this.formatovanieVypisuTlacidielPolicok("Najom ak mate 2 policka tohto typu: " + ((Pozemok)policko).getNajom() * 2 + "$"));
                this.zvolenePolicko[5].setText(this.formatovanieVypisuTlacidielPolicok("Najom ak mate 3 policka tohto typu: " + ((Pozemok)policko).getNajom() * 2 * 2 + "$"));
                this.zvolenePolicko[6].setText(this.formatovanieVypisuTlacidielPolicok("Najom ak mate 4 policka tohto typu: " + ((Pozemok)policko).getNajom() * 2 * 2 * 2 + "$"));
                this.zvolenePolicko[7].setText(this.formatovanieVypisuTlacidielPolicok("Hodnota za pozicanie banke: " + ((Pozemok)policko).getCena() / 2 + "$"));
            }
            if (((Pozemok)policko).getTyp() == TypPozemku.SPOLOCNOST) {
                this.zvolenePolicko[3].setText(this.formatovanieVypisuTlacidielPolicok("Najom ak vlastnite 1 nehnutelnost tohto"));
                this.zvolenePolicko[4].setText(this.formatovanieVypisuTlacidielPolicok("typu je sucet co padlo na kocke * 4"));
                this.zvolenePolicko[5].setText(this.formatovanieVypisuTlacidielPolicok("Najom ak vlastnite 2 nehnutelnosti tohto"));
                this.zvolenePolicko[6].setText(this.formatovanieVypisuTlacidielPolicok("typu je sucet co padlo na kocke * 10"));
                this.zvolenePolicko[7].setText(this.formatovanieVypisuTlacidielPolicok("Hodnota za pozicanie banke: " + ((Pozemok)policko).getCena() / 2 + "$"));
            }
            for (int i = 8; i < this.zvolenePolicko.length; i++) {
                this.zvolenePolicko[i].setText("");
            }
            return;
        }
        for (int i = 2; i < this.zvolenePolicko.length; i++) {
            this.zvolenePolicko[i].setText("");
        }
    }

    /**
     * Formatuje to informacie zadanom hracovy
     * Ze sa to da cez HTML, som prisiel odtialto: https://stackoverflow.com/questions/13503280/new-line-n-is-not-working-in-jbutton-settextfnord-nfoo
     * @param cisloHraca Cislo hraca na formatovanie
     * @return String naformatovany na pouzitie na vypis v JLabel
     */
    private String formatovanieHracov(int cisloHraca) {
        Hrac hrac = this.spravca.getHraci().get(cisloHraca);
        StringBuilder sb = new StringBuilder("<html><span style=\"font-size:18\">" + hrac.getMeno());
        if (hrac.isBankrot()) {
            sb.append(" ZBANKROTOVAL");
        } else {
            sb.append(" je na cisle " + hrac.getPozicia() + " a ma " + hrac.getFinancie() + "$");
        }
        if (hrac.isVazen()) {
            sb.append(" a je vazen");
        }
        sb.append("</span></html>");
        return sb.toString();
    }

    /**
     * Formatuje to velkost textu pre zvolene tlacidlo
     * Ze sa to da cez HTML, som prisiel odtialto: https://stackoverflow.com/questions/13503280/new-line-n-is-not-working-in-jbutton-settextfnord-nfoo
     * @param s String texu na zvacsenie fontu
     * @return String naformatovany tak, ze jeho velkost bude vacsia
     */
    private String formatovanieVypisuTlacidielPolicok(String s) {
        return "<html><span style=\"font-size:18\">" + s + "</span></html>";
    }

    /**
     * Formatuje to text pre tlacidla, tak ze text bude aj na dalsom riadku
     * Ze sa to da cez HTML, som prisiel odtialto: https://stackoverflow.com/questions/13503280/new-line-n-is-not-working-in-jbutton-settextfnord-nfoo
     * @param cislo Cislo tlacila
     * @param nazov Nazov tlacidla
     * @param cenaAleboVlastnik Cena alebo vlastnik tlacidla (pozemku)
     * @return String naformatovany tak, ze kazdy zadany parameter je na novom riadku v tlacidle
     */
    private String tlacidloFormatovanieRiadku(int cislo, String nazov, String cenaAleboVlastnik) {
        if (cenaAleboVlastnik == null || cenaAleboVlastnik.equals("")) {
            return "<html><center>" + cislo + "<br>" + nazov + "</center></html>";
        }
        try {
            int test = Integer.parseInt(cenaAleboVlastnik);
            return "<html><center>" + cislo + "<br>" + nazov + "<br>" + cenaAleboVlastnik + "$</center></html>";
        } catch (Exception e) {
            return "<html><center>" + cislo + "<br>" + nazov + "<br>" + cenaAleboVlastnik + "</center></html>";
        }
    }
}