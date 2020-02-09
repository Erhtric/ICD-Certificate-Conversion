import org.jetbrains.annotations.NotNull;

import java.io.*;

/**
 * Questa classe rappresenta un convertitore di certificati da ICD-10 a ICD-11.
 * Necessita un convertitore di codici e una stringa da cui estrarre un certificato
 */

public class CertificateConverter {

    Certificate certificate;

    public CertificateConverter(CodeConverter converter, @NotNull String str) throws IOException{

            // Prima creiamo un certificato contenente i codici icd-10
             this.certificate = new Certificate(str);
             System.out.println(certificate.toStringICD10());

            // Convertiamoli in icd-11
            for(int i=0; i<61; i++) {
                 if(!certificate.getCodeFromIndex(i).getIcd10Code().equals("NVC")) {
                    certificate.updateParts(converter.convert(certificate.getCodeFromIndex(i)), i);
                }
            }
    }

    public Certificate getCertificate() {
        return this.certificate;
    }
}
