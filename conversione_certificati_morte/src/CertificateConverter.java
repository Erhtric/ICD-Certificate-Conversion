import org.jetbrains.annotations.NotNull;
import java.util.NoSuchElementException;

/**
 * Questo classe rappresenta l'oggetto CertificateConverter.
 * Crea un certificato i cui campi codice posseggono sia il formato icd-10 che icd-11.
 * Il certificato è creato a partire da una stringa, quale deve necessariamente estratta nel formato di un certificato di un certificato di morte
 *      YEAR SEX AGE P_1_1_1 P_1_1_2 ... P_2_10 UCOD
 * L'oggetto una volta creato avrà il certificato compilato opportunamente
 */

public class CertificateConverter {

    Certificate certificate;

    /**
     * Costruttore per CertificateConverter
     * @param converter
     * @param str
     * @throws NoSuchElementException nel caso non vi sia una corrispondenza al codice icd-10 passato
     */
    public CertificateConverter(@NotNull CodeConverter converter, @NotNull String str) throws NoSuchElementException {

        // Prima creiamo un certificato contenente i codici icd-10
        this.certificate = new Certificate(str);

        // Ogni codice deve essere tradotto. I codici vuoti non vengono passati al convertitore.
        for(int i=0; i<61; i++) {
            if(!certificate.getCodeFromIndex(i).getIcd10Code().isEmpty()) {
                certificate.updateParts(converter.convert(certificate.getCodeFromIndex(i)), i);
            }
        }
    }

    public Certificate getCertificate() {
        return this.certificate;
    }
}
