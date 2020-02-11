import java.io.*;
import java.time.LocalTime;
import java.util.NoSuchElementException;

public class main {
    public static void main(String[] args) throws Exception {
        if(args.length==0){
            System.out.println("Non è stato specificato il nome del file da convertire.\nUsare \"nomeprogramma -help\" per un aiuto su come usare il programma.");
            return;
        }else if(args.length==1&&args[0].equals("-help")){
            System.out.println("Il primo parametro deve essere sempre il nome (o il percorso) del file da convertire.");
            System.out.println("In aggiunta, e' possibile usare le seguenti opzioni:");
            System.out.println("-help\t\tUtilizzato come primo parametro, mostra questa guida.");
            System.out.println("-disableSubclassGood\t\tNon utilizza il tipo di conversione \"SubclassGood\"");
            System.out.println("-outputPath <filePath>\t\tPermette di specificare il nome e la posizione del file di output.");
            System.out.println("-mappingPath <filePath>\t\tPermette di specificare il nome e la posizione del file di mapping.");
            System.out.println("-statsPath <filePath>\t\tPermette di specificare il nome e la posizione del file con i dati statistici.");
            System.out.println("-includeFirstLine\t\tConverte anche la prima riga del file di input, che di solito e' ignorata in quanto intestazione.");
            System.out.println("-disableStatsFile\t\tI dati statistici vengono scritti solo in standard output e non su file.");
            return;
        }

        boolean statsFile=printStatsFile(args);
        boolean includeSG=includeSubclassGood(args);

        //Crea il lettore per il file di input
        BufferedReader reader;
        try{
            reader = new BufferedReader(new FileReader(new File(args[0])));
        }catch(Exception e){
            System.out.println("Errore durante l'apertura del file di input.");
            return;
        }

        //Crea lo scrittore per il file di output
        OutputStreamWriter writer;
        try{
            writer = new OutputStreamWriter(new FileOutputStream(getOutputFileName(args)));
        }catch(Exception e){
            System.out.println("Errore durante l'apertura del file di output.");
            return;
        }

        //Crea lo scrittore per il file dei dati statistici
        OutputStreamWriter statWriter=new OutputStreamWriter(System.out);//lo inzializzo su standard output, se non viene modificato non verrà comunque usato
        try{
            if(statsFile)statWriter = new OutputStreamWriter(new FileOutputStream(getStatsFileName(args)));
        }catch(Exception e){
            System.out.println("Errore durante l'apertura del file di output dei dati statistici.");
            return;
        }

        //Crea i converter per i certificati
        //Questo passo è l'ultimo perché la creazione di CodeConverter è costosa ed è inutile eseguirla se ci sono errori nella creazione del lettore o dello scrittore
        CodeConverter codeConv;
        try{
            codeConv=new CodeConverter(getMappingFileName(args),skipFirstLine(args));
        }catch(Exception e){
            System.out.println("Errore durante l'apertura del file di mapping.");
            return;
        }
        CertificateConverter certConv = new CertificateConverter(codeConv);

        //Salta l'intestazione del file di input
        if(skipFirstLine(args)){
            reader.readLine();
        }

        Certificate cert;

        //variabili per i dati statistici
        long numeroTotaleCertificati=0;
        long numeroCertificatiTradottiCorrettamente=0;
        long numeroCertificatiScartati=0;
        long numeroCertificatiScartatiPerNoMapping=0;
        long numeroCertificatiScartatiPerCodiceInesistente=0;
        long numeroCertificatiEquivalent=0;
        long numeroCertificatiSubclassGood=0;
        long numeroCertificatiSubclass=0;
        long sommatoriaDimensioneCertificatiTradottiCorrettamente=0;
        long sommatoriaDimensioneCertificatiScartatiPerNoMapping=0;
        long sommatoriaDimensioneCertificatiScartatiPerCodiceInesistente=0;
        long sommatoriaDimensioneCertificatiEquivalent=0;
        long sommatoriaDimensioneCertificatiSubclassGood=0;
        long sommatoriaDimensioneCertificatiSubclass=0;
        long sommatoriaCodiciSubclassGoodNeiCertificatiSubclassGood=0;
        long sommatoriaCodiciSubclassNeiCertificatiSubclass=0;

        Code.ConversionType ct;
        String outputString;

        while (reader.ready()) {
            cert=new Certificate(reader.readLine());
            numeroTotaleCertificati++;
            try {
                certConv.convert(cert);
                ct=worstConversionTypeInCertificate(cert);//trovo il tipo di conversione "peggiore" nel certificato
                if(ct!= Code.ConversionType.NoMapping){
                    numeroCertificatiTradottiCorrettamente++;
                    sommatoriaDimensioneCertificatiTradottiCorrettamente+=getCertificateDimension(cert);
                    switch (ct){
                        case Equivalent:
                            numeroCertificatiEquivalent++;
                            sommatoriaDimensioneCertificatiEquivalent+=getCertificateDimension(cert);
                            break;

                        case SubclassGood:
                            numeroCertificatiSubclassGood++;
                            sommatoriaDimensioneCertificatiSubclassGood+=getCertificateDimension(cert);
                            sommatoriaCodiciSubclassGoodNeiCertificatiSubclassGood+=countSubclassGood(cert);
                            break;

                        case Subclass:
                            numeroCertificatiSubclass++;
                            sommatoriaDimensioneCertificatiSubclass+=getCertificateDimension(cert);
                            sommatoriaCodiciSubclassNeiCertificatiSubclass+=countSubclass(cert);
                            break;
                    }
                    //stampo il certificato sul file di output
                    outputString=ct+"\t"+cert.getYear()+"\t"+cert.getSex()+"\t"+cert.getAge();
                    for(int i=0;i<61;i++){
                        outputString+="\t";
                        if(!cert.getCodeFromIndex(i).getIcd10Code().equals("")){
                            outputString+=cert.getCodeFromIndex(i).getIcd11Code();
                        }
                    }

                }else{//scarto il certificato perchè uno dei suoi codici non ha mapping
                    numeroCertificatiScartati++;
                    numeroCertificatiScartatiPerNoMapping++;
                    sommatoriaDimensioneCertificatiScartatiPerNoMapping+=getCertificateDimension(cert);
                }
            } catch (NoSuchElementException e) {//scarto un certificato perché uno dei suoi codici non esiste
                numeroCertificatiScartati++;
                numeroCertificatiScartatiPerCodiceInesistente++;
                sommatoriaDimensioneCertificatiScartatiPerCodiceInesistente+=getCertificateDimension(cert);
            }
        }
        //chiudo il file di input e di output
        reader.close();
        writer.close();

        //calcoli statistici
        double PercentualeCertificatiTradottiCorrettamente=(numeroCertificatiTradottiCorrettamente/numeroTotaleCertificati)*100;
        double PercentualeCertificatiScartati=(numeroCertificatiScartati/numeroTotaleCertificati)*100;
        double PercentualeCertificatiScartatiPerNoMappingFraScartati=(numeroCertificatiScartatiPerNoMapping/numeroCertificatiScartati)*100;
        double PercentualeCertificatiScartatiPerCodiceInesistenteFraScartati=(numeroCertificatiScartatiPerCodiceInesistente/numeroCertificatiScartati)*100;
        double PercentualeCertificatiEquivalentFraCorretti=(numeroCertificatiEquivalent/numeroCertificatiTradottiCorrettamente)*100;
        double PercentualeCertificatiSubclassGoodFraCorretti=(numeroCertificatiSubclassGood/numeroCertificatiTradottiCorrettamente)*100;
        double PercentualeCertificatiSubclassFraCorretti=(numeroCertificatiSubclass/numeroCertificatiTradottiCorrettamente)*100;
        double DimensioneMediaCertificatiConvertitiCorrettamente=sommatoriaDimensioneCertificatiTradottiCorrettamente/numeroCertificatiTradottiCorrettamente;
        double DimensioneMediaCertificatiScartatiPerNoMapping=sommatoriaDimensioneCertificatiScartatiPerNoMapping/numeroCertificatiScartatiPerNoMapping;
        double DimensioneMediaCertificatiScartatiPerCodiceInesistente=sommatoriaDimensioneCertificatiScartatiPerCodiceInesistente/numeroCertificatiScartatiPerCodiceInesistente;
        double DimensioneMediaTuttiCertificati=(sommatoriaDimensioneCertificatiTradottiCorrettamente+sommatoriaDimensioneCertificatiScartatiPerNoMapping+sommatoriaDimensioneCertificatiScartatiPerCodiceInesistente)/numeroTotaleCertificati;
        double DimensioneMediaCertificatiEquivalent=sommatoriaDimensioneCertificatiEquivalent/numeroCertificatiEquivalent;
        double DimensioneMediaCertificatiSubclassGood=sommatoriaDimensioneCertificatiSubclassGood/numeroCertificatiSubclassGood;
        double DimensioneMediaCertificatiSubclass=sommatoriaDimensioneCertificatiSubclass/numeroCertificatiSubclass;
        double PercentualeDiCodiciSubclassGoodNeiCertificatiSubclassGood=sommatoriaCodiciSubclassGoodNeiCertificatiSubclassGood/numeroCertificatiSubclassGood;
        double PercentualeDiCodiciSubclassNeiCertificatiSubclass=sommatoriaCodiciSubclassNeiCertificatiSubclass/numeroCertificatiSubclass;


        //scrivo statistiche in standard output
        System.out.println("\n\n\n");
        System.out.println("Numero certificati tradotti correttamente:\t\t"+numeroCertificatiTradottiCorrettamente+"\t("+PercentualeCertificatiTradottiCorrettamente+"% sul totale)");
        System.out.println("Numero certificati scartati:\t\t\t"+numeroCertificatiScartati+"\t("+PercentualeCertificatiScartati+"% sul totale)");
        System.out.println("Numero certificati scartati per mancanza di mapping:\t"+numeroCertificatiScartatiPerNoMapping+"\t("+PercentualeCertificatiScartatiPerNoMappingFraScartati+"% dei certificati scartati)");
        System.out.println("Numero certificati scartati per codice inesistente:\t"+numeroCertificatiScartatiPerCodiceInesistente+"\t("+PercentualeCertificatiScartatiPerCodiceInesistenteFraScartati+"% dei certificati scartati)");
        System.out.println("Numero certificati totali:\t\t\t"+numeroTotaleCertificati);

        System.out.println("\n");
        System.out.println("Numero certificati con tipo di conversione peggiore Equivalent:\t"+numeroCertificatiEquivalent+"\t("+PercentualeCertificatiEquivalentFraCorretti+"dei certificati tradotti correttamente)");
        if(includeSG)System.out.println("Numero certificati con tipo di conversione peggiore SubclassGood:\t"+numeroCertificatiSubclassGood+"\t("+PercentualeCertificatiSubclassGoodFraCorretti+"dei certificati tradotti correttamente)");
        System.out.println("Numero certificati con tipo di conversione peggiore Subclass:\t"+numeroCertificatiSubclass+"\t("+PercentualeCertificatiSubclassFraCorretti+"dei certificati tradotti correttamente)");
        System.out.println("Numero certificati tradotti correttamente:\t\t\t"+numeroCertificatiTradottiCorrettamente);

        System.out.println("\n");
        System.out.println("Dimensione media dei certificati convertiti correttamente:\t"+DimensioneMediaCertificatiConvertitiCorrettamente);
        System.out.println("Dimensione media dei certificati scartati per mancanza di mapping:\t"+DimensioneMediaCertificatiScartatiPerNoMapping);
        System.out.println("Dimensione media dei certificati scartati per codice inesistente:\t"+DimensioneMediaCertificatiScartatiPerCodiceInesistente);
        System.out.println("Dimensione media di tutti i certificati:\t"+DimensioneMediaTuttiCertificati);

        System.out.println("\n");
        System.out.println("Dimensione media dei certificati con tipo di conversione peggiore Equivalent:\t"+DimensioneMediaCertificatiEquivalent);
        if(includeSG)System.out.println("Dimensione media dei certificati con tipo di conversione peggiore SubclassGood:\t"+DimensioneMediaCertificatiSubclassGood);
        System.out.println("Dimensione media dei certificati con tipo di conversione peggiore Subclass:\t"+DimensioneMediaCertificatiSubclass);

        System.out.println("\n");
        if(includeSG)System.out.println("Percentuale media del numero di codici di tipo SubclassGood neicertificati con tipo di conversione peggiore SubclassGood:\t"+PercentualeDiCodiciSubclassGoodNeiCertificatiSubclassGood);
        System.out.println("Percentuale media del numero di codici di tipo Subclass neicertificati con tipo di conversione peggiore Subclass:\t"+PercentualeDiCodiciSubclassNeiCertificatiSubclass);
        System.out.println();

        //scrivo sul file delle statistiche
        if(statsFile){
            statWriter.write("Numero certificati tradotti correttamente:\t\t"+numeroCertificatiTradottiCorrettamente+"\t("+PercentualeCertificatiTradottiCorrettamente+"% sul totale)");
            statWriter.write("Numero certificati scartati:\t\t\t"+numeroCertificatiScartati+"\t("+PercentualeCertificatiScartati+"% sul totale)");
            statWriter.write("Numero certificati scartati per mancanza di mapping:\t"+numeroCertificatiScartatiPerNoMapping+"\t("+PercentualeCertificatiScartatiPerNoMappingFraScartati+"% dei certificati scartati)");
            statWriter.write("Numero certificati scartati per codice inesistente:\t"+numeroCertificatiScartatiPerCodiceInesistente+"\t("+PercentualeCertificatiScartatiPerCodiceInesistenteFraScartati+"% dei certificati scartati)");
            statWriter.write("Numero certificati totali:\t\t\t"+numeroTotaleCertificati);

            statWriter.write("\n");
            statWriter.write("Numero certificati con tipo di conversione peggiore Equivalent:\t"+numeroCertificatiEquivalent+"\t("+PercentualeCertificatiEquivalentFraCorretti+"dei certificati tradotti correttamente)");
            if(includeSG)statWriter.write("Numero certificati con tipo di conversione peggiore SubclassGood:\t"+numeroCertificatiSubclassGood+"\t("+PercentualeCertificatiSubclassGoodFraCorretti+"dei certificati tradotti correttamente)");
            statWriter.write("Numero certificati con tipo di conversione peggiore Subclass:\t"+numeroCertificatiSubclass+"\t("+PercentualeCertificatiSubclassFraCorretti+"dei certificati tradotti correttamente)");
            statWriter.write("Numero certificati tradotti correttamente:\t\t\t"+numeroCertificatiTradottiCorrettamente);

            statWriter.write("\n");
            statWriter.write("Dimensione media dei certificati convertiti correttamente:\t"+DimensioneMediaCertificatiConvertitiCorrettamente);
            statWriter.write("Dimensione media dei certificati scartati per mancanza di mapping:\t"+DimensioneMediaCertificatiScartatiPerNoMapping);
            statWriter.write("Dimensione media dei certificati scartati per codice inesistente:\t"+DimensioneMediaCertificatiScartatiPerCodiceInesistente);
            statWriter.write("Dimensione media di tutti i certificati:\t"+DimensioneMediaTuttiCertificati);

            statWriter.write("\n");
            statWriter.write("Dimensione media dei certificati con tipo di conversione peggiore Equivalent:\t"+DimensioneMediaCertificatiEquivalent);
            if(includeSG)statWriter.write("Dimensione media dei certificati con tipo di conversione peggiore SubclassGood:\t"+DimensioneMediaCertificatiSubclassGood);
            statWriter.write("Dimensione media dei certificati con tipo di conversione peggiore Subclass:\t"+DimensioneMediaCertificatiSubclass);

            statWriter.write("\n");
            if(includeSG)statWriter.write("Percentuale media del numero di codici di tipo SubclassGood neicertificati con tipo di conversione peggiore SubclassGood:\t"+PercentualeDiCodiciSubclassGoodNeiCertificatiSubclassGood);
            statWriter.write("Percentuale media del numero di codici di tipo Subclass neicertificati con tipo di conversione peggiore Subclass:\t"+PercentualeDiCodiciSubclassNeiCertificatiSubclass);
        }

    }

    private static String getOutputFileName(String[] args){
        for(int i=0;i<args.length-1;i++){
            if(args[i].equals("-outputPath")){
                return args[i+1];
            }
        }
        return "out_"+ LocalTime.now() +".txt";
    }

    private static String getMappingFileName(String[] args){
        for(int i=0;i<args.length-1;i++){
            if(args[i].equals("-mappingPath")){
                return args[i+1];
            }
        }
        return "data\\10To11MapToOneCategory.txt";
    }

    private static boolean skipFirstLine(String[] args){
        for(int i=0;i<args.length;i++){
            if(args[i].equals("-includeFirstLine")){
                return false;
            }
        }
        return true;
    }

    private static boolean includeSubclassGood(String[] args){
        for(int i=0;i<args.length;i++){
            if(args[i].equals("-disableSubclassGood")){
                return false;
            }
        }
        return true;
    }

    private static String getStatsFileName(String[] args){
        for(int i=0;i<args.length-1;i++){
            if(args[i].equals("-statsPath")){
                return args[i+1];
            }
        }
        return "stats_"+ LocalTime.now() +".txt";
    }

    private static boolean printStatsFile(String[] args){
        for(int i=0;i<args.length;i++){
            if(args[i].equals("-disableStatsFile")){
                return false;
            }
        }
        return true;
    }

    private static Code.ConversionType worstConversionTypeInCertificate(Certificate cert){
        Code.ConversionType ct= Code.ConversionType.Equivalent;
        for(int i=0;i<61;i++){
            if(!cert.getCodeFromIndex(i).getIcd10Code().equals("")){
                switch (cert.getCodeFromIndex(i).getConvType()){
                    case SubclassGood:
                        if(ct== Code.ConversionType.Equivalent){
                            ct= Code.ConversionType.SubclassGood;
                        }
                        break;

                    case Subclass:
                        if(ct== Code.ConversionType.Equivalent||ct== Code.ConversionType.SubclassGood){
                            ct= Code.ConversionType.Subclass;
                        }
                        break;

                    case NoMapping:
                        if(ct!= Code.ConversionType.NoMapping){
                            ct= Code.ConversionType.NoMapping;
                        }
                        break;
                }
            }
        }
        return ct;
    }

    private static long getCertificateDimension(Certificate cert){
        long n=0;
        for(int i=0;i<61;i++){
            if(!cert.getCodeFromIndex(i).getIcd10Code().equals("")){
                n++;
            }
        }
        return n;
    }

    private static long countSubclassGood(Certificate cert){
        long n=0;
        for(int i=0;i<61;i++){
            if(!cert.getCodeFromIndex(i).getIcd10Code().equals("")&&cert.getCodeFromIndex(i).getConvType()== Code.ConversionType.SubclassGood){
                n++;
            }
        }
        return n;
    }

    private static long countSubclass(Certificate cert){
        long n=0;
        for(int i=0;i<61;i++){
            if(!cert.getCodeFromIndex(i).getIcd10Code().equals("")&&cert.getCodeFromIndex(i).getConvType()== Code.ConversionType.Subclass){
                n++;
            }
        }
        return n;
    }

}
