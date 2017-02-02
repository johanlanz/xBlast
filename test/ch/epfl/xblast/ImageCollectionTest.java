package ch.epfl.xblast;


import java.awt.BorderLayout;
import java.awt.Image;
import java.net.URISyntaxException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.junit.Test;

import ch.epfl.xblast.client.ImageCollection;

public final class ImageCollectionTest{

    @Test
    public void createsNewImageColl() throws URISyntaxException, InterruptedException{
        ImageCollection c = new ImageCollection("score");
        for(int i = 0; i< 91; i++){
            System.out.println(i);
            Image bomb = c.imageOrNull(i);
            JFrame frame = new JFrame();
            if(bomb != null){
            JLabel lblimage = new JLabel(new ImageIcon(bomb));
            frame.getContentPane().add(lblimage, BorderLayout.CENTER);
            frame.setSize(300, 400);
            frame.setVisible(true);
            Thread.sleep(200);
            }
        }
        
        
        
    }

}
