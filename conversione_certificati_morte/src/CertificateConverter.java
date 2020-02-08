import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class CertificateConverter {

    // path = "src/data/cert2017100K.txt";
    public CertificateConverter(String path) throws Exception{
        File file = new File(path);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String header = reader.readLine();

        while(!reader.readLine().isEmpty()) {

            // Prima creiamo un certificato contenente i codici icd-10
            Certificate cer = new Certificate(reader.readLine());
            // Generiamo il convertitore
            CodeConverter converter = new CodeConverter("src/data/10To11MapToOneCategory.txt");

            // Convertiamoli in icd-11
            for(int i=0; i<59; ++i) {
                Code cod = cer.getParts(i);
                converter.convert(cod);
            }

            // Conversione ucod in icd-11
            Code ucod = cer.getUcod();
            converter.convert(ucod);

            // Scriviamo su file
        }
    }
}
