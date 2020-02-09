import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.NoSuchElementException;

public class main {
    public static void main(String[] args) throws Exception {
        CodeConverter test=new CodeConverter("..\\10To11MapToOneCategory.txt");
        //Code code= test.convert(new Code("R41"));
        //System.out.println(code.getIcd11Code()+" - "+code.getConvType());

        File file = new File("..\\cert2017100k.txt");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        System.out.println(reader.readLine());          // intestazione

        int count = 1;
        // Non va
        while (reader.ready()) {
            try {
                CertificateConverter c = new CertificateConverter(test, reader.readLine());
            } catch (NoSuchElementException e) {
                System.out.println("Codice non riconosciuto nella conversione per il certificato n°: " + count);
            }
            count++;
        }
        reader.close();
    }
}
