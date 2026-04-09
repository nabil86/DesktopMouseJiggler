package com.atlas86.DesktopMouseJiggler;

import java.awt.AWTException;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import java.awt.Image;
/**
 * Application entry point. Manages the system-tray icon and its popup menu.
 */
public class MouseJigglerMain {

    private static final Logger LOGGER = Logger.getLogger(MouseJigglerMain.class.getName());

    private TrayIcon trayIcon;
    private final AtomicReference<AppConfig> configRef =
            new AtomicReference<>(ConfigPersistence.load());
    private final Mouse mouse = new Mouse(configRef);

    public static void main(String[] args) {
        new MouseJigglerMain().createTrayIcon();
    }

    private Image loadImage() {
        try (InputStream in = getClass().getResourceAsStream("/desktopmousejiggler/mouso.png")) {
            if (in == null) {
                LOGGER.warning("Tray icon image not found");
                return null;
            }
            return ImageIO.read(in);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Failed to load tray icon image", ex);
            return null;
        }
    }

    public void createTrayIcon() {
        checkSupportedSysTray();
        var tray = SystemTray.getSystemTray();
        var image = loadImage();
        var popup = createPopupMenu();

        trayIcon = new TrayIcon(image, "Desktop Mouse Jiggler", popup);
        trayIcon.setImageAutoSize(true);
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            LOGGER.log(Level.SEVERE, "Failed to add tray icon", e);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(mouse::stop, "shutdown-hook"));
    }

    private PopupMenu createPopupMenu() {
        var popup = new PopupMenu();

        var activeItem = new CheckboxMenuItem("Active", true);
        mouse.start(); // Started by default
        
        activeItem.addItemListener((ItemEvent e) -> {
            if (activeItem.getState()) {
                mouse.start();
            } else {
                mouse.stop();
            }
        });
        popup.add(activeItem);

        var showItem = new MenuItem("Config");
        showItem.addActionListener(e ->
                new ConfigWindow(configRef, saved -> {}).show());
        popup.add(showItem);

        var closeItem = new MenuItem("Close");
        final ActionListener closeListener = e -> System.exit(0);
        closeItem.addActionListener(closeListener);
        popup.add(closeItem);

        return popup;
    }

    private void checkSupportedSysTray() {
        if (!SystemTray.isSupported()) {
            LOGGER.severe("SystemTray not supported on this platform");
            System.exit(0);
        }
    }
}
