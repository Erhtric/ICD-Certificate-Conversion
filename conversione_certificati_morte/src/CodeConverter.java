import java.util.Map;

public class CodeConverter {
    private MapNode mappingTree;
    private boolean useSubclassGood;

    public CodeConverter(String mapFile){
        mappingTree=new MapNode();
        useSubclassGood=false;
        //sostituisci Subclass con SubclassGood nell'albero dove è giusto
    }

    public CodeConverter(String mapFile, boolean useSubclassGood){
        mappingTree=new MapNode();
        this.useSubclassGood=useSubclassGood;
        if(useSubclassGood){
            //sostituisci Subclass con SubclassGood nell'albero dove è giusto
        }
    }

    public Code convert(Code code){
        return mappingTree.findAndReturnCode(code);
    }

    public boolean usesSubclassGood(){
        return useSubclassGood;
    }

}
