import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.NoSuchElementException;

public class main {
    public static void main(String[] args) throws Exception {
        CodeConverter test=new CodeConverter("..\\10To11MapToOneCategory.txt");
        //Code code= test.convert(new Code("R41"));
        //System.out.println(code.getIcd11Code()+" - "+code.getConvType());

        File file = new File("..\\cert2017100k.txt");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        reader.readLine();          // intestazione

        Certificate[] listOfCertificates = new Certificate[100001];

        int count = 1;
        while (reader.ready()) {
            try {
                CertificateConverter c = new CertificateConverter(test, reader.readLine());
                listOfCertificates[count] = c.getCertificate();
                String icd11Certificate = c.getCertificate().toStringICD11();
                System.out.print("Certificato numero: " + count);
                System.out.print(" " + icd11Certificate + "\n");
            } catch (NoSuchElementException e) {
                System.out.println("Codice non riconosciuto nella conversione per il certificato n°: " + count);
            }
            count++;
        }
        reader.close();
    }
}
