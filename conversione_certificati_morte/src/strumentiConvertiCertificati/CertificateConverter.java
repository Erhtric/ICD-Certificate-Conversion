package strumentiConvertiCertificati;

import java.util.NoSuchElementException;

/**
 * Questo classe rappresenta l'oggetto CertificateConverter.
 * Crea un certificato i cui campi codice posseggono sia il formato icd-10 che icd-11.
 * Il certificato è creato a partire da una stringa, quale deve necessariamente estratta nel formato di un certificato di un certificato di morte
 *      YEAR\tSEX\tAGE\tP_1_1_1\tP_1_1_2\t...\tP_2_10\tUCOD
 * L'oggetto una volta creato avrà il certificato compilato opportunamente
 */

public class CertificateConverter {

    private CodeConverter converter;

    /**
     * Costruttore per CertificateConverter
     * @param converter
     */
    public CertificateConverter(CodeConverter converter){
        this.converter=converter;
    }

    //Riceve un certificato, converte tutti i suoi codici. Se un codice non esiste, lancia NoSuchElementException
    public void convert(Certificate c) throws NoSuchElementException {
        // Ogni codice deve essere tradotto. I codici vuoti non vengono passati al convertitore.
        for(int i=0; i<61; i++) {
            if (!c.getCodeFromIndex(i).getIcd10Code().isEmpty()) {
                c.updateParts(converter.convert(c.getCodeFromIndex(i)), i);
            }
        }
    }

}
