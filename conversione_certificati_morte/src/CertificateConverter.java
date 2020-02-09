import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.NoSuchElementException;

/**
 * Questa classe rappresenta un convertitore di certificati da ICD-10 a ICD-11.
 * Necessita un convertitore di codici e una stringa da cui estrarre un certificato
 */

public class CertificateConverter {

    Certificate certificate;

    public CertificateConverter(CodeConverter converter, @NotNull String str) throws IOException{

            // Prima creiamo un certificato contenente i codici icd-10
             this.certificate = new Certificate(str);

            // Convertiamoli in icd-11
            try {
                for(int i=0; i<61; i++) {
                    if(!certificate.getCodeFromIndex(i).getIcd10Code().equals("NVC")) {
                        certificate.updateParts(converter.convert(certificate.getCodeFromIndex(i)), i);
                    }
                }
            } catch (NoSuchElementException e) {
                System.out.println("Un codice icd-10 associato al certificato non presenta corrispondenza nel mapping.");
            }
    }

    public Certificate getCertificate() {
        return this.certificate;
    }
}
