package org.altaprise.vawr.ui.filechartwizard;

import dai.shared.businessObjs.DBRecSet;

import java.awt.*;
import java.awt.event.*;

import java.awt.event.ActionEvent;


import java.io.File;

import java.util.ArrayList;

import javax.swing.*;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import javax.swing.border.EtchedBorder;

import org.altaprise.vawr.awrdata.AWRData;
import org.altaprise.vawr.awrdata.AWRMetrics;
import org.altaprise.vawr.awrdata.file.ReadAWRMinerFile;
import org.altaprise.vawr.charts.AWRIOPSTimeSeriesChart;
import org.altaprise.vawr.charts.AWRMemoryTimeSeriesChart;
import org.altaprise.vawr.charts.AWRMetricSummaryChart;
import org.altaprise.vawr.charts.AWRTimeSeriesChart;
import org.altaprise.vawr.charts.AvgActiveSessionChart;
import org.altaprise.vawr.charts.SizeOnDiskChart;
import org.altaprise.vawr.charts.TopWaitEventsBarChart;
import org.altaprise.vawr.ui.RootFrame;
import org.altaprise.vawr.utils.PropertyFile;
import org.altaprise.vawr.utils.SessionMetaData;

public class ChartFilePanel extends JPanel {
    private JTextField jTextField_fileName = new JTextField();
    private JButton jButton_selectFile = new JButton();
    private JButton jButton_chart = new JButton();
    private JComboBox jComboBox_metricName = new JComboBox();
    private JPanel  jPanel_panelTitle = new JPanel();
    private JPanel jPanel_contentPanel = new JPanel();
    private JLabel jLabel_panelTitle = new JLabel();
    private JTextPane jTextArea_osInfo = new JTextPane();
    private JScrollPane jScrollPane_osInfo = new JScrollPane(jTextArea_osInfo);

    private static String AWR_FILE_NAME = "INITIALIZED";
    private ReadAWRMinerFile _awrParser = null;
    private JLabel jLabel1 = new JLabel("Platform Details:");
    private JSeparator jSeparator1 = new JSeparator();
    private JButton jButton_export = new JButton();

    /**The default constructor for form
     */
    public ChartFilePanel() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**the JbInit method
     */
    public void jbInit() throws Exception {
        BorderLayout borderLayout = new BorderLayout();
        this.setLayout(borderLayout);

        this.setSize(new Dimension(603, 503));

        jLabel_panelTitle.setText("Select an AWRMiner file, then chart an AWR metric.");
        jLabel_panelTitle.setBounds(new Rectangle(20, 5, 500, 30));
        jLabel_panelTitle.setFont(new Font("Arial", 1, 16));

        jScrollPane_osInfo.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        jPanel_panelTitle.setLayout(null);
        jPanel_panelTitle.setBackground(new Color(247, 247, 247));
        jPanel_panelTitle.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        jPanel_panelTitle.setMinimumSize(new Dimension(48, 50));
        jPanel_panelTitle.setPreferredSize(new Dimension(48, 50));
        jPanel_panelTitle.add(jLabel_panelTitle, null);
        
        jPanel_contentPanel.setLayout(null);

        //jPanel_contentPanel.setSize(new Dimension(706, 300));
        jScrollPane_osInfo.setBounds(new Rectangle(35, 200, 560, 220));
        jScrollPane_osInfo.setMinimumSize(new Dimension(48, 200));
        jScrollPane_osInfo.setPreferredSize(new Dimension(48, 200));

        jLabel1.setBounds(new Rectangle(35, 185, 325, 15));

        jLabel1.setText("Platform Details Output(CPU & Memory values are per host):");
        jSeparator1.setBounds(new Rectangle(15, 170, 570, 5));
        jButton_export.setText("Export Metrics");
        jButton_export.setBounds(new Rectangle(445, 110, 130, 20));
        jButton_export.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton_export_actionPerformed(e);
            }
        });
        jPanel_contentPanel.setBounds(new Rectangle(20, 5, 400, 30));
        jPanel_contentPanel.setMinimumSize(new Dimension(48, 200));
        jPanel_contentPanel.add(jButton_export, null);
        jPanel_contentPanel.add(jSeparator1, null);
        jPanel_contentPanel.add(jLabel1, null);
        jPanel_contentPanel.add(jComboBox_metricName, null);
        jPanel_contentPanel.add(jButton_chart, null);

        jPanel_contentPanel.add(jButton_selectFile, null);
        jPanel_contentPanel.add(jTextField_fileName, null);

        //jPanel_contentPanel.add(jScrollPane_osInfo, null);
        jPanel_contentPanel.add(jScrollPane_osInfo, null);
        this.add(jPanel_panelTitle, BorderLayout.NORTH);

        this.add(jPanel_contentPanel, BorderLayout.CENTER);
        jTextField_fileName.setBounds(new Rectangle(40, 60, 480, 20));
        String appHome = SessionMetaData.getInstance().getDaiHome();
        jButton_selectFile.setText("Select File");
        jButton_selectFile.setBounds(new Rectangle(525, 60, 85, 20));
        jButton_selectFile.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jButton_selectFile_actionPerformed(e);
                }
            });
        jButton_chart.setText("Chart Metric");
        jButton_chart.setBounds(new Rectangle(345, 110, 95, 20));
        jButton_chart.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jButton_chart_actionPerformed(e);
                }
            });
        jComboBox_metricName.setBounds(new Rectangle(40, 110, 300, 20));

        jTextArea_osInfo.setEnabled(false);
        jTextArea_osInfo.setFont(new Font("monospaced", Font.PLAIN, 11));
        setComboBoxOptions();
    }

    private void jButton_selectFile_actionPerformed(ActionEvent e) {
        String lastPath = PropertyFile.getInstance().getLastFilePath();
        String appHome = System.getProperty("user.dir");
        if (lastPath != null && lastPath.trim().length() > 0) appHome = lastPath;
        JFileChooser chooser = new JFileChooser(appHome);
        //FileNameExtensionFilter filter = new FileNameExtensionFilter(
        //    "JPG & GIF Images", "jpg", "gif");
        //chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String fileName = chooser.getSelectedFile().getName();
            String filePath = chooser.getSelectedFile().getPath();
            this.jTextField_fileName.setText(filePath);
            String justPath = chooser.getCurrentDirectory().getPath();
            System.out.println("You chose to open this file: " + filePath + "/"+justPath);
            PropertyFile.getInstance().setLastFilePath(justPath);
            PropertyFile.getInstance().serializeIt();
        }

    }

    private void jButton_chart_actionPerformed(ActionEvent e) {
        String selectedFile = this.jTextField_fileName.getText();

        //SetCursor
        RootFrame.startWaitCursor();

        try {

            if (!AWR_FILE_NAME.equals(selectedFile)) {
                AWRData.getInstance().clearAWRData();
                _awrParser = new ReadAWRMinerFile();
                _awrParser.parse(selectedFile);
                _awrParser.parseMemData(selectedFile);
                AWR_FILE_NAME = selectedFile;
                if (SessionMetaData.getInstance().debugOn()) {
                    AWRData.getInstance().dumpData();
                }
            }

            //Chart the data
            String oracleMetricName = (String)jComboBox_metricName.getSelectedItem();
            //Convert to AWRMiner metric name
            String metricName = AWRMetrics.getAWRMinerMetricName(oracleMetricName);

            this.jTextArea_osInfo.setContentType("text/html");
            this.jTextArea_osInfo.setText(AWRData.getInstance().getPlatformInfoHTML());
            this.jTextArea_osInfo.setCaretPosition(0);

            if (AWRData.getInstance().awrMetricExists(metricName)|| metricName.equals("SUMMARY")) {
                if (metricName.equals("SGA_PGA_TOT")) {
                    new AWRMemoryTimeSeriesChart(metricName, AWRData.getInstance().getChartHeaderHTML());
                } else if (metricName.equals("AVG_ACTIVE_SESS_WAITS")) {
                    new AvgActiveSessionChart(metricName, AWRData.getInstance().getChartHeaderHTML());
                } else if (metricName.equals("TOP_N_TIMED_EVENTS")) {
                    new TopWaitEventsBarChart(metricName);
                } else if (metricName.equals("WRITE_IOPS") || metricName.equals("READ_IOPS")) {
                    new AWRIOPSTimeSeriesChart(metricName, AWRData.getInstance().getChartHeaderHTML());
                } else if (metricName.equals("SUMMARY")) {
                    new AWRMetricSummaryChart(metricName, true);
                } else if (metricName.equals("SIZE_GB")) {
                    new SizeOnDiskChart(metricName, AWRData.getInstance().getChartHeaderHTML());
                } else {
                    new AWRTimeSeriesChart(metricName, AWRData.getInstance().getChartHeaderHTML());
                }
            } else {
                JOptionPane.showMessageDialog(RootFrame.getFrameRef(),
                                              metricName +
                                              " Metric Does not exist in this file.",
                                              "Error",
                                              JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            //SetCursor
            RootFrame.stopWaitCursor();
            
            ex.printStackTrace();

            JOptionPane.showMessageDialog(RootFrame.getFrameRef(),
                                          ex.getLocalizedMessage(), "Error",
                                          JOptionPane.ERROR_MESSAGE);
        }
        //SetCursor
        RootFrame.stopWaitCursor();

    }

    private void setComboBoxOptions() {
        ArrayList<String> metricNames = AWRMetrics.getInstance().getOracleMetricNames();
        for (int i = 0; i < metricNames.size(); i++) {
            jComboBox_metricName.addItem(metricNames.get(i));
        }
    }

    private void jButton_export_actionPerformed(ActionEvent e) {
        //SetCursor
        RootFrame.startWaitCursor();
        
        try {
            //Do an Export
            if (AWRData.getInstance().getAWRDataRecordCount() > 0) {
                //Get the file name to export
                String fileName = getExportFileName();
                
                //Export it.
                if (fileName != null && fileName.length() > 0) {
                    AWRData.getInstance().exportAWRData(fileName);
                    JOptionPane.showMessageDialog(RootFrame.getFrameRef(), "File Saved.  " + fileName, "Status",
                                                  JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(RootFrame.getFrameRef(),
                                              "No AWR records found. Do Query First", "Error",
                                              JOptionPane.ERROR_MESSAGE);
            }        
        } catch (Exception ex) {
            daiBeans.daiDetailInfoDialog dialog =
                new daiBeans.daiDetailInfoDialog(RootFrame.getFrameRef(), "Error", true,
                                                 ex.getLocalizedMessage());
            ex.printStackTrace();
        } finally {
            //SetCursor
            RootFrame.stopWaitCursor();
        }
    }
    
    private String getExportFileName() {
        String fileName = "";
        JFileChooser FC = new JFileChooser(System.getProperty("user.dir"));
        int ret = FC.showSaveDialog(RootFrame.getFrameRef());

        if (ret == FC.APPROVE_OPTION) {
            File f = FC.getSelectedFile();
            fileName = f.getAbsolutePath();
        }
        return fileName;
    }

}
