import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class CertificateConverter {

    // path = "src/data/cert2017100K.txt";
    public static void certificateConverter(String path) throws Exception{
        File file = new File(path);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String header = reader.readLine();

        while(!reader.readLine().isEmpty()) {

            // Prima creiamo un certificato contenente i codici icd-10
            Certificate cer = new Certificate(reader.readLine());

            // Convertiamoli in icd-11

            // Scriviamo su file
        }
    }
}
