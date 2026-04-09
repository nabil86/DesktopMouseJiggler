package com.atlas86.DesktopMouseJiggler;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

/**
 * Configuration window allowing the user to adjust:
 * <ul>
 *   <li>Interval between mouse movements (seconds)</li>
 *   <li>Movement amplitude (pixels)</li>
 *   <li>Morning activation window (start – end)</li>
 *   <li>Afternoon activation window (start – end)</li>
 * </ul>
 */
public final class ConfigWindow {

    private final AtomicReference<AppConfig> configRef;
    private final Consumer<AppConfig> onSave;

    /**
     * @param configRef shared config holder updated when the user saves.
     * @param onSave    callback invoked (on the Swing thread) after a successful save.
     */
    public ConfigWindow(AtomicReference<AppConfig> configRef, Consumer<AppConfig> onSave) {
        this.configRef = configRef;
        this.onSave    = onSave;
    }

    /** Creates and shows the dialog (must be called on the Event Dispatch Thread). */
    public void show() {
        SwingUtilities.invokeLater(this::buildAndShow);
    }

    private void buildAndShow() {
        var current = configRef.get();
        var dialog  = new JDialog();
        dialog.setTitle("Configuration – Desktop Mouse Jiggler");
        dialog.setModal(true);
        dialog.setResizable(false);
        dialog.setLayout(new GridBagLayout());

        // ── Interval ──────────────────────────────────────────────
        var intervalSpinner = new JSpinner(
                new SpinnerNumberModel(current.intervalSec(), 1, 3600, 1));

        // ── Amplitude ─────────────────────────────────────────────
        var amplitudeSpinner = new JSpinner(
                new SpinnerNumberModel(current.amplitudePx(), 10, 3000, 10));

        // ── Time fields ───────────────────────────────────────────
        var morningStartField    = new JTextField(current.morningStart().toString(),    5);
        var morningEndField      = new JTextField(current.morningEnd().toString(),      5);
        var afternoonStartField  = new JTextField(current.afternoonStart().toString(),  5);
        var afternoonEndField    = new JTextField(current.afternoonEnd().toString(),    5);
        var monitoredProcessesField = new JTextField(current.monitoredProcesses(),      20);

        // ── Layout ────────────────────────────────────────────────
        var gbc = new GridBagConstraints();
        gbc.insets  = new Insets(6, 8, 6, 8);
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.anchor  = GridBagConstraints.WEST;

        int row = 0;
        addRow(dialog, gbc, row++, "Intervalle (s) :",       intervalSpinner);
        addRow(dialog, gbc, row++, "Amplitude (px) :",       amplitudeSpinner);
        addRow(dialog, gbc, row++, "Matin début (HH:mm) :",  morningStartField);
        addRow(dialog, gbc, row++, "Matin fin (HH:mm) :",    morningEndField);
        addRow(dialog, gbc, row++, "Après-midi début :",     afternoonStartField);
        addRow(dialog, gbc, row++, "Après-midi fin :",       afternoonEndField);
        addRow(dialog, gbc, row++, "Processus surveillés :", monitoredProcessesField);

        // ── Buttons ───────────────────────────────────────────────
        var saveBtn   = new JButton("Enregistrer");
        var cancelBtn = new JButton("Annuler");

        saveBtn.addActionListener(e -> {
            try {
                long  newInterval   = ((Number) intervalSpinner.getValue()).longValue();
                int   newAmplitude  = ((Number) amplitudeSpinner.getValue()).intValue();
                LocalTime newMorningStart   = parseTime(morningStartField.getText());
                LocalTime newMorningEnd     = parseTime(morningEndField.getText());
                LocalTime newAfternoonStart = parseTime(afternoonStartField.getText());
                LocalTime newAfternoonEnd   = parseTime(afternoonEndField.getText());
                String newMonitoredProcesses = monitoredProcessesField.getText();

                var newConfig = new AppConfig(newInterval, newAmplitude,
                        newMorningStart, newMorningEnd, newAfternoonStart, newAfternoonEnd, newMonitoredProcesses);
                configRef.set(newConfig);
                ConfigPersistence.save(newConfig);
                onSave.accept(newConfig);
                dialog.dispose();
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Format d'heure invalide. Utilisez HH:mm (ex : 08:30).",
                        "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        dialog.add(saveBtn, gbc);
        gbc.gridx = 1;
        dialog.add(cancelBtn, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private static void addRow(JDialog dialog, GridBagConstraints gbc, int row,
                               String label, java.awt.Component field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        dialog.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        dialog.add(field, gbc);
    }

    private static LocalTime parseTime(String text) {
        return LocalTime.parse(text.trim());
    }
}
