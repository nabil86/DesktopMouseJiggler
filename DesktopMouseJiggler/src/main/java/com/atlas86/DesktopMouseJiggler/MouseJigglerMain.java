package com.atlas86.DesktopMouseJiggler;

import java.awt.AWTException;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 *
 * @author atlas86
 */
public class MouseJigglerMain  {

    private boolean firstTime;
    private TrayIcon trayIcon;
    private boolean enabled = false;
        
    public static void main(String[] args) {
        new MouseJigglerMain().createTrayIcon();
    }
    
    private java.awt.Image loadImage() {
        java.awt.Image image = null;
        try {
            InputStream in =  getClass().getResourceAsStream("/desktopmousejiggler/mouseJiggler2.jpg");
            image = ImageIO.read(in);
        } catch (IOException ex) {
            System.out.println(ex);
        }

        return image;
    }

    public void createTrayIcon( ) {
        checkSupportedSysTray();
        var tray = SystemTray.getSystemTray();
        var image = loadImage();

        // create a popup menu
        var popup = new PopupMenu();             
        Mouse mouse = new Mouse();
           
        var activeItem = new java.awt.CheckboxMenuItem("Active", enabled);
        activeItem.addItemListener((ItemEvent e) -> {   
           if(activeItem.getState())
               mouse.start();
           else
               mouse.stop();
        });
        popup.add(activeItem);
        
        var showItem = new java.awt.MenuItem("Config");
        popup.add(showItem);

         // create a action listener to listen for default action executed on the tray icon
        var closeItem = new java.awt.MenuItem("Close");
        final ActionListener closeListener = (java.awt.event.ActionEvent e) -> System.exit(0);
        closeItem.addActionListener(closeListener);
        popup.add(closeItem);
        
        // construct a TrayIcon
        trayIcon = new TrayIcon(image, "Title", popup);
        
        // set the TrayIcon properties
        //trayIcon.addActionListener(showListener);
        // ...
        // add the tray image
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.err.println(e);
        }
    }

    private void checkSupportedSysTray() {
        if (!SystemTray.isSupported()) {
            System.err.println("SystemTray not supported ");
            System.exit(0);
        }
    }

    public void showProgramIsMinimizedMsg() {
        if (firstTime) {
            trayIcon.displayMessage("Some message.", "Some other message.", TrayIcon.MessageType.INFO);
            firstTime = false;
        }
    }
}
