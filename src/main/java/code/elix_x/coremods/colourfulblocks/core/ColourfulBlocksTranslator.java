package code.elix_x.coremods.colourfulblocks.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Charsets;

import cpw.mods.fml.common.asm.transformers.deobf.LZMAInputSupplier;
import cpw.mods.fml.relauncher.FMLInjectionData;
import cpw.mods.fml.relauncher.IFMLCallHook;

public class ColourfulBlocksTranslator implements IFMLCallHook{

	public static boolean debug = false;
    public static boolean obfuscatedEnv;

    private String deobFile;
    private String mcLocation;
    
    private static HashMap<String, String> classNameMap = new HashMap<String, String>();
    private static HashMap<String, String> fieldNameMap = new HashMap<String, String>();
    private static HashMap<String, String> methodNameMap = new HashMap<String, String>();
    private static HashMap<String, String> methodDescMap = new HashMap<String, String>();

    public static String getMapedFieldName(String className, String fieldName, String devName) {
        return obfuscatedEnv?fieldNameMap.get(className + "." + fieldName):devName;
    }

    public static String getMapedClassName(String className) {
    	if(obfuscatedEnv)
    		return classNameMap.get(className.substring(className.lastIndexOf(".")+1));
    	else{
    		return new StringBuilder("net.minecraft.").append(className.replace("/", ".")).toString();
    	}
    }

    public static String getMapedMethodName(String className, String methodName, String devName) {
        return obfuscatedEnv?methodNameMap.get(className + "." + methodName):devName;
    }

    public static String getMapedMethodDesc(String className, String methodName, String devDesc) {
        return obfuscatedEnv?methodDescMap.get(className + "." + methodName):devDesc;
    }

    public static void setup(String deobFileName){
        try{
            LZMAInputSupplier zis = new LZMAInputSupplier(FMLInjectionData.class.getResourceAsStream(deobFileName));
            List<String> srgList = zis.asCharSource(Charsets.UTF_8).readLines();

            for (String line : srgList) {

                line = line.replace(" #C", "").replace(" #S", "");

                if (line.startsWith("CL")) {
                    parseClass(line);
                } else if (line.startsWith("FD")) {
                    parseField(line);
                } else if (line.startsWith("MD")) {
                    parseMethod(line);
                }

            }

            /*System.out.println(getMapedClassName("client.settings.GameSettings"));
            System.out.println(getMapedMethodName("GameSettings", "func_74300_a", "loadOptions"));*/
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public Void call() throws Exception {
        setup(deobFile);
        return null;
    }

	private static void parseMethod(String line) {
        String[] splitLine = line.split(" ");

        String[] splitObName = splitLine[1].split("/");
        String[] splitTranslatedName = splitLine[3].split("/");

        String key = splitTranslatedName[splitTranslatedName.length - 2] + "." + splitTranslatedName[splitTranslatedName.length - 1];

        methodNameMap.put(key, splitObName[splitObName.length - 1]);

        methodDescMap.put(key, splitLine[2]);
    }

    private static void parseField(String line) {
        String[] splitLine = line.split(" ");

        String[] splitObName = splitLine[1].split("/");
        String[] splitTranslatedName = splitLine[2].split("/");

        String key = splitTranslatedName[splitTranslatedName.length - 2] + "." + splitTranslatedName[splitTranslatedName.length - 1];

        fieldNameMap.put(key, splitObName[splitObName.length - 1]);
    }

    private static void parseClass(String line) {
        String[] splitLine = line.split(" ");

        String[] splitClassPath = splitLine[2].split("/");

        classNameMap.put(splitClassPath[splitClassPath.length - 1], splitLine[1]);
    }

    @Override
    public void injectData(Map<String, Object> data) {
        deobFile = data.get("deobfuscationFileName").toString();
        obfuscatedEnv = (Boolean) data.get("runtimeDeobfuscationEnabled");
        mcLocation = data.get("mcLocation").toString();
    }

}
