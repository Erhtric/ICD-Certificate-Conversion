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
        this.icd10Code=icd10Code;
        icd11Code=null;
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

    public Code clone(){
        Code codeCopy=new Code(this.icd10Code);
        codeCopy.setIcd11Code(this.icd11Code);
        switch (convType){
            case NotConvertedYet:
                codeCopy.setConversionTypeToNotConvertedYet();
                break;

            case Equivalent:
                codeCopy.setConversionTypeToEquivalent();
                break;

            case Subclass:
                codeCopy.setConversionTypeToSubclass();
                break;

            case NoMapping:
                codeCopy.setConversionTypeToNoMapping();
                break;
        }
        return codeCopy;
    }


    //Ritorna true se this è un sottoinsieme di otherCode. Non funziona per i numeri romani che indicano i capitoli.
    public boolean icd10IsUnder(Code otherCode){
        String otherCodeICD10=otherCode.getIcd10Code();
        if(isRomanNumber(this.icd10Code)||isRomanNumber(otherCodeICD10)||this.icd10Code.equals(otherCodeICD10)){
            //Ci fermiano subito se uno dei due codici è un numero romano o se i due codici sono uguali
            return false;
        } else if(otherCodeICD10.length()==7){
            //Controlliamo il caso in cui otherCode è nella forma "D37-D48"
            if(this.icd10Code.charAt(0)!=otherCodeICD10.charAt(0)){
                return false;
            } else if(this.icd10Code.length()==7&&(Integer.parseInt(otherCodeICD10.substring(1,3))<=Integer.parseInt(this.icd10Code.substring(1,3))&&Integer.parseInt(this.icd10Code.substring(5))<=Integer.parseInt(otherCodeICD10.substring(5)))){
                return true;
            }else if(Integer.parseInt(otherCodeICD10.substring(1,3))<=Integer.parseInt(this.icd10Code.substring(1,3))&&Integer.parseInt(this.icd10Code.substring(1,3))<=Integer.parseInt(otherCodeICD10.substring(5))){
                return true;
            } else {
                return false;
            }
        } else if(otherCodeICD10.length()==3){
            //Controlliamo il caso in cui otherCode è nella forma "D37"
            if(this.icd10Code.length()==5&&this.icd10Code.substring(0,3).equals(otherCodeICD10)){
                return true;
            } else {
                return false;
            }
        } else {
            //Nei casi rimanenti torniamo sempre falso
            return false;
        }
    }

    //Ritorna true se il codice è un numero romano del tipo usato per i nomi dei capitoli
    private boolean isRomanNumber(String s){
        for(int i=0;i<s.length();i++){
            if(s.charAt(i)!='I'||s.charAt(i)!='V'||s.charAt(i)!='X'){
                return false;
            }
        }
        return true;
    }

}
