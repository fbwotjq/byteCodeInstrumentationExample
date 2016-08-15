package byteCodeInstrumentation;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;

public class MyBciAgent implements ClassFileTransformer {
	
    public MyBciAgent() {
        super();
    }
    
    public static void premain(String agentArguments, Instrumentation instrumentation) {
        instrumentation.addTransformer(new MyBciAgent());
    }
    
    public byte[] transform(ClassLoader loader, String className, Class redefiningClass, ProtectionDomain domain, byte[] bytes)
            throws IllegalClassFormatException {
    	
        String[] ignore = new String[] { "sun/", "java/", "javax/", "javassist/", "org/slf4j", "org/apache/log4j", "com/sun" };
        for (int i = 0; i < ignore.length; i++) {
            if (className.startsWith(ignore[i])) {
                return bytes;
            }
        }
        return transformClass(redefiningClass, bytes, className);
    }
    
    private byte[] transformClass(Class classToTransform, byte[] b, String className) {
    	
        ClassPool pool = ClassPool.getDefault();
        CtClass cl = null;
        try {
        	
            cl = pool.makeClass(new java.io.ByteArrayInputStream(b));
            	
                CtBehavior[] methods = cl.getDeclaredBehaviors();
                for (int i = 0; i < methods.length; i++) {
                    if (methods[i].isEmpty() == false) {
                    	System.out.println(String.format("%s.%s", className, methods[i].getName()));
                    }
                }
            
            b = cl.toBytecode();
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cl != null) {
                cl.detach();
            }
        }
        
        return b;
    }
    
}