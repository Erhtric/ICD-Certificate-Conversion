import java.util.Map;

public class CodeConverter {
    private MapNode mappingTree;

    public CodeConverter(String mapFile){
        mappingTree=new MapNode();
    }

    public Code convert(Code code){
        return mappingTree.findAndReturnCode(code);
    }

}
