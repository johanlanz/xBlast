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
        ImageCollection c = new ImageCollection("player");
        int index = -1;
        for(int i = 0; i< 50; i++){
            System.out.println(i);
            if(i % 2 == 0){
                index = 6;
            }else if(i % 4 == 1){
                index = 88;
            }else if(i % 4 == 3){
                index = 87;
            }
            Image player = c.imageOrNull(index);
            JFrame frame = new JFrame();
            
            JLabel lblimage = new JLabel(new ImageIcon(player));
            frame.getContentPane().add(lblimage, BorderLayout.CENTER);
            frame.setSize(300, 400);
            frame.setVisible(true);
            Thread.sleep(200);
            
        }
        
        
        
    }

}
