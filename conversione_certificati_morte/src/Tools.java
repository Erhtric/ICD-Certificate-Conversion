import java.io.*;

public class Tools {

    // Fa esattamente quello che dice
    private static String readALineFromFile(BufferedReader reader) throws Exception{
        return reader.readLine();
    }

    public static Certificate createCertificate(BufferedReader reader) throws Exception{
        String str = readALineFromFile(reader);
        
        Certificate certificate = new Certificate(str);
        return certificate;
    }

}
