import java.util.Map;

public class CodeConverter {
    private MapNode mappingTree;
    private boolean useSubclassGood;

    public CodeConverter(String mapFile){
        mappingTree=new MapNode();
        useSubclassGood=false;
        fillTreeFromFile(mapFile);
        setSubclassGoodInTree();
    }

    public CodeConverter(String mapFile, boolean useSubclassGood){
        mappingTree=new MapNode();
        this.useSubclassGood=useSubclassGood;
        fillTreeFromFile(mapFile);
        if(useSubclassGood){
            setSubclassGoodInTree();
        }
    }

    private void fillTreeFromFile(String mapFile){

    }

    private void setSubclassGoodInTree(){

    }

    public Code convert(Code code){
        return mappingTree.findAndReturnCode(code);
    }

    public boolean usesSubclassGood(){
        return useSubclassGood;
    }

}
