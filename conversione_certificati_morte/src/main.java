import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.NoSuchElementException;

public class main {
    public static void main(String[] args) throws Exception {
        CodeConverter test=new CodeConverter("..\\10To11MapToOneCategory.txt");
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

            } catch (NoSuchElementException e) {

            }
            count++;
        }
        reader.close();
    }
}
