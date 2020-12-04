package Exercise1.Genetics.UI;

import Exercise1.Genetics.Enums.RecombinationType;
import Exercise1.Genetics.Enums.Protection;
import Exercise1.Genetics.Enums.ReplicationScheme;
import Exercise1.Genetics.Models.GeneSet;
import Exercise1.Genetics.Models.ParameterValue;
import Exercise1.Genetics.UI.Elements.DoubleSpinner;

import javax.swing.*;

public class SimulationGui extends JFrame {

    private JSlider gencntSlider;
    private JSlider genlenSlider;
    private JPanel genetischeAlgorithmen;
    private JTextField genlenValue;
    private JTextField gencntValue;
    private JTextField maxGenValue;
    private JTextField initRateValue;
    private JTextField numOfRunsValue;
    private JSlider maxGenSlider;
    private JSlider initRateSlider;
    private JSlider numOfRunsSlider;
    private JComboBox<ReplicationScheme> replicationSchemes;
    private JComboBox<RecombinationType> recombinationType;
    private JComboBox<Protection> protections;
    private JSlider pmSlider;
    private JSlider pcSlider;
    private JTextField pmValue;
    private JTextField pcValue;
    private DoubleSpinner pcStart;
    private DoubleSpinner pcEnd;
    private DoubleSpinner pcStep;
    private DoubleSpinner pmStart;
    private DoubleSpinner pmEnd;
    private DoubleSpinner pmStep;
    private JButton findParametersButton;
    private JButton runSimulationButton;
    private JProgressBar progressBar;
    private JLabel progressLabel;
    private JLabel resultLabel1;
    private JLabel resultLabel2;
    private JLabel resultLabel3;
    private JLabel resultLabel4;
    private JLabel resultLabel5;
    private JSpinner spinnerS;

    private int genecnt;
    private int genelen;
    private int maxgenerations;
    private double initrate;
    private double acceptRate;
    private int numberOfRuns;
    private double pc;
    private double pm;
    private RecombinationType crossingOverMethod;
    private ReplicationScheme replicationScheme;
    private Protection protection;
    double pcStartValue;
    double pcEndValue;
    double pcStepValue;
    double pmStartValue;
    double pmEndValue;
    double pmStepValue;

    public SimulationGui(String title) {
        super(title);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(genetischeAlgorithmen);
        this.pack();
        spinnerS.setVisible(false);
        spinnerS.setValue(2);
        initRateValue.setText(5 + "%");
        addChangeListenerToSlider(gencntSlider, gencntValue);
        addChangeListenerToSlider(genlenSlider, genlenValue);
        addChangeListenerToSlider(maxGenSlider, maxGenValue);
        addChangeListenerToSliderPercent(initRateSlider, initRateValue);
        addChangeListenerToSlider(numOfRunsSlider, numOfRunsValue);
        addChangeListenerToSliderSmallPercent(pmSlider, pmValue);
        addChangeListenerToSliderSmallPercent(pcSlider, pcValue);
        pcStart.setValue(0.5);
        pcEnd.setValue(0.9);
        pcStep.setValue(0.02);
        pmStart.setValue(0.0);
        pmEnd.setValue(0.03);
        pmStep.setValue(0.002);
        ReplicationScheme[] replicationSchemesArr = ReplicationScheme.class.getEnumConstants();
        RecombinationType[] recombinationTypesArr = RecombinationType.class.getEnumConstants();
        Protection[] protectionsArr = Protection.class.getEnumConstants();
        for (ReplicationScheme scheme : replicationSchemesArr)
            replicationSchemes.addItem(scheme);
        for (RecombinationType type : recombinationTypesArr)
            recombinationType.addItem(type);
        for (Protection protection : protectionsArr)
            protections.addItem(protection);

        replicationSchemes.addItemListener(e -> spinnerS.setVisible(replicationSchemes.getSelectedItem() == ReplicationScheme.RANK_BASED_SELECTION));
        runSimulationButton.addActionListener(e -> {
            clearResults();
            progressBar.setValue(0);
            progressBar.setString("Working...");
            progressBar.setStringPainted(true);
            readData();
            GeneSet gs = new GeneSet(genecnt, genelen, maxgenerations, initrate, acceptRate, numberOfRuns, replicationScheme, crossingOverMethod, protection);
            gs.setRankBasedSelectionParameter_s((int) spinnerS.getValue());
            try {
                long startTime = System.currentTimeMillis();
                gs.runSimulation(pc, pm);
                long endTime = System.currentTimeMillis();
                progressBar.setString("Complete");
                progressBar.setValue(100);
                int[] result = gs.getResult();
                resultLabel1.setText("Result:");
                resultLabel2.setText("Average Generations: " + result[0]);
                resultLabel3.setText("Highest Value: " + result[1]);
                resultLabel4.setText("Time: " + (int) ((endTime - startTime) / 1000 / 60) + ":" + (int) ((endTime - startTime) / 1000 % 60));
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        });
        findParametersButton.addActionListener(e -> {
            readData();
            clearResults();
            progressBar.setStringPainted(false);
            GeneSet.progress = 0;
            GeneSet gs = new GeneSet(genecnt, genelen, maxgenerations, initrate, acceptRate, numberOfRuns, replicationScheme, crossingOverMethod, protection);
            gs.setRankBasedSelectionParameter_s((int) spinnerS.getValue());
            SwingWorker worker = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    long startTime = System.currentTimeMillis();
                    gs.findIdealParameters(pcStartValue, pcEndValue, pcStepValue, pmStartValue, pmEndValue, pmStepValue);
                    long endTime = System.currentTimeMillis();
                    ParameterValue bestParams = gs.getBestParameters();
                    resultLabel1.setText("The best found Parameters are:");
                    resultLabel2.setText("Mutation Rate: " + bestParams.getPm());
                    resultLabel3.setText("Recombination Rate: " + bestParams.getPc());
                    resultLabel4.setText("Average Generations: " + bestParams.getAverageGens());
                    resultLabel5.setText("Time: " + (int) ((endTime - startTime) / 1000 / 60) + ":" + (int) ((endTime - startTime) / 1000 % 60));
                    return null;
                }
            };
            worker.execute();
        });

    }

    private void clearResults() {
        resultLabel5.setText("");
        resultLabel4.setText("");
        resultLabel3.setText("");
        resultLabel2.setText("");
        resultLabel1.setText("");
    }

    private void readData() {
        this.genecnt = Integer.parseInt(gencntValue.getText());
        this.genelen = Integer.parseInt(genlenValue.getText());
        this.maxgenerations = Integer.parseInt(maxGenValue.getText());
        this.pc = Double.parseDouble(pcValue.getText().replace("%", "")) / 100d;
        this.pm = Double.parseDouble(pmValue.getText().replace("%", "")) / 100d;
        this.acceptRate = 1;
        this.initrate = Double.parseDouble(initRateValue.getText().replace("%", "")) / 100d;
        this.crossingOverMethod = (RecombinationType) recombinationType.getSelectedItem();
        this.replicationScheme = (ReplicationScheme) replicationSchemes.getSelectedItem();
        this.protection = (Protection) protections.getSelectedItem();
        this.numberOfRuns = Integer.parseInt(numOfRunsValue.getText());
        this.pcStartValue = (double) pcStart.getValue();
        this.pcEndValue = (double) pcEnd.getValue();
        this.pcStepValue = (double) pcStep.getValue();
        this.pmStartValue = (double) pmStart.getValue();
        this.pmEndValue = (double) pmEnd.getValue();
        this.pmStepValue = (double) pmStep.getValue();
    }

    private void addChangeListenerToSlider(JSlider slider, JTextField label) {
        slider.addChangeListener(e -> {
            int value = slider.getValue();
            label.setText(String.valueOf(value));
        });

    }

    private void addChangeListenerToSliderPercent(JSlider slider, JTextField label) {
        slider.addChangeListener(e -> {
            int value = slider.getValue();
            label.setText(value + "%");
        });

    }

    private void addChangeListenerToSliderSmallPercent(JSlider slider, JTextField label) {
        slider.addChangeListener(e -> {
            int value = slider.getValue();
            label.setText((double) value / 10d + "%");
        });

    }

    public void setProgressValue(int value) {
        this.progressBar.setValue(value);
    }

    public void setProgressLabel(int progress, int amount) {
        progressLabel.setText(progress + "/" + amount + " Parameters tested (" + (int) ((double) progress / (double) amount * 100d) + "%)");
    }
}

