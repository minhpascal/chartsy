package org.chartsy.main;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.dialogs.LoaderDialog;
import org.chartsy.main.managers.ChartFrameManager;
import org.chartsy.main.managers.DatasetManager;
import org.chartsy.main.managers.UpdaterManager;
import org.chartsy.main.updater.AbstractUpdater;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author viorel.gheba
 */
public class NewChart implements ActionListener {

    private String symbol = "";
    private String chart = "";

    public NewChart() {}

    public void setSymbol(String symbol) { this.symbol = symbol.toUpperCase(); }
    public void setChart(String chart) { this.chart = chart; }

    public void actionPerformed(ActionEvent e) {
        AbstractUpdater updater = UpdaterManager.getDefault().getActiveUpdater();
        if (updater != null) {
            NewChartDialog dialog = new NewChartDialog(new JFrame(), true);
            dialog.setLocationRelativeTo((Component) e.getSource());
            dialog.setListener(this);
            dialog.setVisible(true);

            if (!dialog.isVisible()) {
                if (!symbol.isEmpty() && !chart.isEmpty()) {
                    boolean exists = DatasetManager.getDefault().dataExists(symbol);
                    if (!exists) {
                        LoaderDialog loader = new LoaderDialog(new JFrame(), true);
                        loader.setLocationRelativeTo((Component) e.getSource());
                        loader.update(symbol);
                        loader.setVisible(true);

                        if (!loader.isVisible()) {
                            newChartFrame(symbol, chart);
                        }
                    } else newChartFrame(symbol, chart);
                }
            }
        } else {
            NotifyDescriptor nd = new NotifyDescriptor.Message("Connect to a data provider first.", NotifyDescriptor.INFORMATION_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
        }
    }

    protected void newChartFrame(String symbol, String chart) {
        if (DatasetManager.getDefault().getDataset(DatasetManager.getName(symbol, DatasetManager.DAILY)) != null) {
            ChartFrame chartFrame = new ChartFrame(symbol, chart);
            chartFrame.setID(ChartFrameManager.getDefault().getID());
            ChartFrameManager.getDefault().addChartFrame(chartFrame.preferredID(), chartFrame);
            chartFrame.open();
            chartFrame.requestActive();
        } else {
            NotifyDescriptor nd = new NotifyDescriptor.Message("There is no data for " + symbol + " symbol.", NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
        }
    }

}