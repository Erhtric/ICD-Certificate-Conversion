import strumentiConvertiCertificati.*;

import java.io.*;
import java.time.LocalTime;
import java.util.NoSuchElementException;

public class ConvertiCertificatiInICD11 {
    public static void main(String[] args) throws Exception {
        if(args.length==0){
            System.out.println("Non e' stato specificato il nome del file da convertire.\nUsare \"nomeprogramma -help\" per un aiuto su come usare il programma.");
            return;
        }else if(args.length==1&&args[0].equals("-help")){
            System.out.println("Il primo parametro deve essere sempre il nome (o il percorso) del file da convertire.");
            System.out.println("In aggiunta, e' possibile usare le seguenti opzioni:");
            System.out.println("-help\t\t\t\tUtilizzato come primo parametro, mostra questa guida.");
            System.out.println("-disableSubclassGood\t\tNon utilizza il tipo di conversione \"SubclassGood\".");
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
        long sommatoriaDecillesimiCodiciSubclassGoodInCertificatiDiTipoPeggioreSubclassGood=0;
        long sommatoriaDecillesimiCodiciSubclassInCertificatiDiTipoPeggioreSubclass=0;

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
                            sommatoriaDecillesimiCodiciSubclassGoodInCertificatiDiTipoPeggioreSubclassGood+=((double)countSubclassGood(cert))/getCertificateDimension(cert)*10000;
                            sommatoriaDimensioneCertificatiSubclassGood+=getCertificateDimension(cert);
                            break;

                        case Subclass:
                            numeroCertificatiSubclass++;
                            sommatoriaDecillesimiCodiciSubclassInCertificatiDiTipoPeggioreSubclass+=((double)countSubclass(cert))/getCertificateDimension(cert)*10000;
                            sommatoriaDimensioneCertificatiSubclass+=getCertificateDimension(cert);
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
                    writer.write(outputString+"\n");

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
        double PercentualeCertificatiTradottiCorrettamente=(((double)numeroCertificatiTradottiCorrettamente)/numeroTotaleCertificati)*100;
        double PercentualeCertificatiScartati=(((double)numeroCertificatiScartati)/numeroTotaleCertificati)*100;
        double PercentualeCertificatiScartatiPerNoMappingFraScartati=(((double)numeroCertificatiScartatiPerNoMapping)/numeroCertificatiScartati)*100;
        double PercentualeCertificatiScartatiPerCodiceInesistenteFraScartati=(((double)numeroCertificatiScartatiPerCodiceInesistente)/numeroCertificatiScartati)*100;
        double PercentualeCertificatiEquivalentFraCorretti=(((double)numeroCertificatiEquivalent)/numeroCertificatiTradottiCorrettamente)*100;
        double PercentualeCertificatiSubclassGoodFraCorretti=(((double)numeroCertificatiSubclassGood)/numeroCertificatiTradottiCorrettamente)*100;
        double PercentualeCertificatiSubclassFraCorretti=(((double)numeroCertificatiSubclass)/numeroCertificatiTradottiCorrettamente)*100;
        double DimensioneMediaCertificatiConvertitiCorrettamente=((double)sommatoriaDimensioneCertificatiTradottiCorrettamente)/numeroCertificatiTradottiCorrettamente;
        double DimensioneMediaCertificatiScartatiPerNoMapping=((double)sommatoriaDimensioneCertificatiScartatiPerNoMapping)/numeroCertificatiScartatiPerNoMapping;
        double DimensioneMediaCertificatiScartatiPerCodiceInesistente=((double)sommatoriaDimensioneCertificatiScartatiPerCodiceInesistente)/numeroCertificatiScartatiPerCodiceInesistente;
        double DimensioneMediaTuttiCertificati=(((double)sommatoriaDimensioneCertificatiTradottiCorrettamente)+sommatoriaDimensioneCertificatiScartatiPerNoMapping+sommatoriaDimensioneCertificatiScartatiPerCodiceInesistente)/numeroTotaleCertificati;
        double DimensioneMediaCertificatiEquivalent=((double)sommatoriaDimensioneCertificatiEquivalent)/numeroCertificatiEquivalent;
        double DimensioneMediaCertificatiSubclassGood=((double)sommatoriaDimensioneCertificatiSubclassGood)/numeroCertificatiSubclassGood;
        double DimensioneMediaCertificatiSubclass=((double)sommatoriaDimensioneCertificatiSubclass)/numeroCertificatiSubclass;
        double PercentualeDiCodiciSubclassGoodNeiCertificatiSubclassGood=((double)sommatoriaDecillesimiCodiciSubclassGoodInCertificatiDiTipoPeggioreSubclassGood)/(numeroCertificatiSubclassGood*100);
        double PercentualeDiCodiciSubclassNeiCertificatiSubclass=((double)sommatoriaDecillesimiCodiciSubclassInCertificatiDiTipoPeggioreSubclass)/(numeroCertificatiSubclass*100);

        //scrivo statistiche in standard output
        System.out.println("\n\n\n");
        System.out.println("Numero certificati tradotti correttamente:\t\t"+numeroCertificatiTradottiCorrettamente+"\t("+PercentualeCertificatiTradottiCorrettamente+"% sul totale)");
        System.out.println("Numero certificati scartati:\t\t\t\t"+numeroCertificatiScartati+"\t("+PercentualeCertificatiScartati+"% sul totale)");
        System.out.println("Numero certificati scartati per mancanza di mapping:\t"+numeroCertificatiScartatiPerNoMapping+"\t("+PercentualeCertificatiScartatiPerNoMappingFraScartati+"% dei certificati scartati)");
        System.out.println("Numero certificati scartati per codice inesistente:\t"+numeroCertificatiScartatiPerCodiceInesistente+"\t("+PercentualeCertificatiScartatiPerCodiceInesistenteFraScartati+"% dei certificati scartati)");
        System.out.println("Numero certificati totali:\t\t\t\t"+numeroTotaleCertificati);

        System.out.println("\n");
        System.out.println("Numero certificati con tipo di conversione peggiore Equivalent:\t\t"+numeroCertificatiEquivalent+"\t("+PercentualeCertificatiEquivalentFraCorretti+"% dei certificati tradotti correttamente)");
        if(includeSG)System.out.println("Numero certificati con tipo di conversione peggiore SubclassGood:\t"+numeroCertificatiSubclassGood+"\t("+PercentualeCertificatiSubclassGoodFraCorretti+"% dei certificati tradotti correttamente)");
        System.out.println("Numero certificati con tipo di conversione peggiore Subclass:\t\t"+numeroCertificatiSubclass+"\t("+PercentualeCertificatiSubclassFraCorretti+"% dei certificati tradotti correttamente)");
        System.out.println("Numero certificati tradotti correttamente:\t\t\t\t"+numeroCertificatiTradottiCorrettamente);

        System.out.println("\n");
        System.out.println("Dimensione media dei certificati convertiti correttamente:\t\t"+DimensioneMediaCertificatiConvertitiCorrettamente);
        System.out.println("Dimensione media dei certificati scartati per mancanza di mapping:\t"+DimensioneMediaCertificatiScartatiPerNoMapping);
        System.out.println("Dimensione media dei certificati scartati per codice inesistente:\t"+DimensioneMediaCertificatiScartatiPerCodiceInesistente);
        System.out.println("Dimensione media di tutti i certificati:\t\t\t\t"+DimensioneMediaTuttiCertificati);

        System.out.println("\n");
        System.out.println("Dimensione media dei certificati con tipo di conversione peggiore Equivalent:\t"+DimensioneMediaCertificatiEquivalent);
        if(includeSG)System.out.println("Dimensione media dei certificati con tipo di conversione peggiore SubclassGood:\t"+DimensioneMediaCertificatiSubclassGood);
        System.out.println("Dimensione media dei certificati con tipo di conversione peggiore Subclass:\t"+DimensioneMediaCertificatiSubclass);

        System.out.println("\n");
        if(includeSG)System.out.println("Percentuale media del numero di codici di tipo SubclassGood nei certificati con tipo di conversione peggiore SubclassGood:\t"+PercentualeDiCodiciSubclassGoodNeiCertificatiSubclassGood+"%");
        System.out.println("Percentuale media del numero di codici di tipo Subclass nei certificati con tipo di conversione peggiore Subclass:\t\t"+PercentualeDiCodiciSubclassNeiCertificatiSubclass+"%");
        System.out.println("");

        //scrivo sul file delle statistiche
        if(statsFile){
            statWriter.write("Numero certificati tradotti correttamente:\t\t"+numeroCertificatiTradottiCorrettamente+"\t("+PercentualeCertificatiTradottiCorrettamente+"% sul totale)\n");
            statWriter.write("Numero certificati scartati:\t\t\t\t"+numeroCertificatiScartati+"\t("+PercentualeCertificatiScartati+"% sul totale)\n");
            statWriter.write("Numero certificati scartati per mancanza di mapping:\t"+numeroCertificatiScartatiPerNoMapping+"\t("+PercentualeCertificatiScartatiPerNoMappingFraScartati+"% dei certificati scartati)\n");
            statWriter.write("Numero certificati scartati per codice inesistente:\t"+numeroCertificatiScartatiPerCodiceInesistente+"\t("+PercentualeCertificatiScartatiPerCodiceInesistenteFraScartati+"% dei certificati scartati)\n");
            statWriter.write("Numero certificati totali:\t\t\t\t"+numeroTotaleCertificati+"\n");

            statWriter.write("\n");
            statWriter.write("Numero certificati con tipo di conversione peggiore Equivalent:\t\t"+numeroCertificatiEquivalent+"\t("+PercentualeCertificatiEquivalentFraCorretti+"% dei certificati tradotti correttamente)\n");
            if(includeSG)statWriter.write("Numero certificati con tipo di conversione peggiore SubclassGood:\t"+numeroCertificatiSubclassGood+"\t("+PercentualeCertificatiSubclassGoodFraCorretti+"% dei certificati tradotti correttamente)\n");
            statWriter.write("Numero certificati con tipo di conversione peggiore Subclass:\t\t"+numeroCertificatiSubclass+"\t("+PercentualeCertificatiSubclassFraCorretti+"% dei certificati tradotti correttamente)\n");
            statWriter.write("Numero certificati tradotti correttamente:\t\t\t\t"+numeroCertificatiTradottiCorrettamente+"\n");

            statWriter.write("\n");
            statWriter.write("Dimensione media dei certificati convertiti correttamente:\t\t"+DimensioneMediaCertificatiConvertitiCorrettamente+"\n");
            statWriter.write("Dimensione media dei certificati scartati per mancanza di mapping:\t"+DimensioneMediaCertificatiScartatiPerNoMapping+"\n");
            statWriter.write("Dimensione media dei certificati scartati per codice inesistente:\t"+DimensioneMediaCertificatiScartatiPerCodiceInesistente+"\n");
            statWriter.write("Dimensione media di tutti i certificati:\t\t\t\t"+DimensioneMediaTuttiCertificati+"\n");

            statWriter.write("\n");
            statWriter.write("Dimensione media dei certificati con tipo di conversione peggiore Equivalent:\t"+DimensioneMediaCertificatiEquivalent+"\n");
            if(includeSG)statWriter.write("Dimensione media dei certificati con tipo di conversione peggiore SubclassGood:\t"+DimensioneMediaCertificatiSubclassGood+"\n");
            statWriter.write("Dimensione media dei certificati con tipo di conversione peggiore Subclass:\t"+DimensioneMediaCertificatiSubclass+"\n");

            statWriter.write("\n");
            if(includeSG)statWriter.write("Percentuale media del numero di codici di tipo SubclassGood nei certificati con tipo di conversione peggiore SubclassGood:\t"+PercentualeDiCodiciSubclassGoodNeiCertificatiSubclassGood+"%\n");
            statWriter.write("Percentuale media del numero di codici di tipo Subclass nei certificati con tipo di conversione peggiore Subclass:\t\t"+PercentualeDiCodiciSubclassNeiCertificatiSubclass+"%\n");
        }
        statWriter.close();

    }

    private static String getOutputFileName(String[] args){
        for(int i=0;i<args.length-1;i++){
            if(args[i].equals("-outputPath")){
                return args[i+1];
            }
        }
        String name="out_"+ LocalTime.now() +".txt";
        return name.replace(':','_');
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
        String name="stats_"+ LocalTime.now() +".txt";
        return name.replace(':','_');
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
