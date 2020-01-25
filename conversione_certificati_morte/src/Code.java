public class Code {
    enum ConversionType {
        NotConvertedYet,
        Equivalent,
        Subclass,
        NoMapping
    }

    private String icd10Code;
    private String icd11Code;
    private ConversionType convType;

    //Costruttore
    public Code(String icd10Code){
        this.icd10Code=icd10;
        icd11Code=NULL;
        convType=ConversionType.NotConvertedYet;
    }

    //Getters
    public String getIcd10Code() {
        return icd10Code;
    }

    public String getIcd11Code() {
        return icd11Code;
    }

    public ConversionType getConvType() {
        return convType;
    }

    //Setters
    public void setIcd11Code(String icd11Code) {
        this.icd11Code = icd11Code;
    }

    public void setConversionTypeToNotConvertedYet(){
        convType=ConversionType.NotConvertedYet;
    }

    public void setConversionTypeToEquivalent(){
        convType=ConversionType.Equivalent;
    }

    public void setConversionTypeToSubclass(){
        convType=ConversionType.Subclass;
    }

    public void setConversionTypeToNoMapping(){
        convType=ConversionType.NoMapping;
    }

}
