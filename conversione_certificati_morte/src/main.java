public class main {
    public static void main(String[] args) throws Exception {
        CodeConverter test=new CodeConverter("..\\10To11MapToOneCategory.txt");
        Code code= test.convert(new Code("R41"));
        System.out.println(code.getIcd11Code()+" - "+code.getConvType());
    }
}
