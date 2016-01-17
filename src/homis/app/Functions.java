package homis.app;

import java.net.InetAddress;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by troll173 on 11/27/15.
 */
public class Functions {

    public static String toMoney(String doublePayment) {
        Locale locale = new Locale("ph", "PH");
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        String peso = formatter.format(Double.parseDouble(doublePayment));
      return peso;
    }

    public static String getMYIP()
    {
        String n=null;
        try
        {
            InetAddress ip=InetAddress.getLocalHost();
            n=ip.toString();

        }catch(Exception e){e.printStackTrace();}
        return n;
    }

}
