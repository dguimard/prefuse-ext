package edu.berkeley.guir.prefuse.util.display;

import java.awt.event.ActionEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashSet;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.util.io.IOLib;
import edu.berkeley.guir.prefuse.util.io.SimpleFileFilter;

/**
 * SaveImageAction
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class ExportDisplayAction extends AbstractAction {

    private Display display;
    private JFileChooser chooser;
    private ScaleSelector scaler;
    
    public ExportDisplayAction(Display display) {
        this.display = display;
        scaler  = new ScaleSelector();
        chooser = new JFileChooser();
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        chooser.setDialogTitle("Export Prefuse Display...");
        chooser.setAcceptAllFileFilterUsed(false);
        
        HashSet seen = new HashSet();
        String[] fmts = ImageIO.getWriterFormatNames();
        for ( int i=0; i<fmts.length; i++ ) {
            String s = fmts[i].toLowerCase();
            if ( s.length() == 3 && !seen.contains(s) ) {
                seen.add(s);
                chooser.setFileFilter(new SimpleFileFilter(s, 
                        s.toUpperCase()+" Image (*."+s+")"));
            }
        }
        seen.clear(); seen = null;
        chooser.setAccessory(scaler);
    } //
    
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent evt) {
        // open image save dialog
        File f = null;
        scaler.setImage(display.getOffscreenBuffer());
        int returnVal = chooser.showSaveDialog(display);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
           f = chooser.getSelectedFile();
        } else {
            return;
        }
        String format = ((SimpleFileFilter)chooser.getFileFilter()).getExtension();
        String ext = IOLib.getExtension(f);        
        if ( !format.equals(ext) ) {
            f = new File(f.toString()+"."+format);
        }
        
        double scale = scaler.getScale();
        
        // save image
        boolean success = false;
        try {
            OutputStream out = new BufferedOutputStream(new FileOutputStream(f));
            System.out.print("Saving image "+f.getName()+", "+format+" format...");
            success = display.saveImage(out, format, scale);
            out.flush();
            out.close();
            System.out.println("\tDONE");
        } catch ( Exception e ) {
            success = false;
        }
        // show result dialog on failure
        if ( !success ) {
            JOptionPane.showMessageDialog(display,
                    "Error Saving Image!",
                    "Image Save Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    } //
    
} // end of class SaveImageAction
