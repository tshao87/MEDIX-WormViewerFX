/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package annotationtoolfx.db;

import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 *
 * @author jpiane
 */
public class Utilities {
    
    private Utilities(){

    }

    public static String getPrefixZeros(String strainId) {

        String six = "000035.jpeg";
        String seven = "0000035.jpeg";
        
        
        String partialUrl = annotationtoolfx.object.Constants.WormUrl + strainId + "/";

        String surl;
        try {
            surl = partialUrl + six;

            URL website;
            website = new URL(surl);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            rbc.close();
        }
        catch(Exception e){
            surl = partialUrl + seven;

            URL website;
            try{
                website = new URL(surl);
                ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                rbc.close();
                return annotationtoolfx.object.Constants.SevenZeros;
            }
            catch(Exception e1){
                
            }

        }
        
        return annotationtoolfx.object.Constants.SixZeros;

    }
}
