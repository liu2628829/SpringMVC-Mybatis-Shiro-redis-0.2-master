package com.sojson.user.controller;

/**
 * Created by mathman002 on 2017/10/10.
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/8.
 */
public class Test {

    private static final StringBuilder sb = new StringBuilder();

    public static void writeClass(String classStr, OutputStreamWriter out) {
        try {
            Class clz = Class.forName(classStr);
//            Method[] methods = clz.getDeclaredMethods();
//            for (Method method : methods) {
//                String methodName = method.getName();
//                Class[] params = method.getParameterTypes();
//                for (Class param : params) {
//                    System.out.println(param.getName());
//                }
//            }
            Field[] fields = clz.getDeclaredFields();

            for (Field field : fields) {
                Class c = field.getType();
//                System.out.println(c.isInterface());
//                System.out.println(c.getName());
//                Class cc = Class.forName(c.getName());
//                Object o = cc.newInstance();
                //只写入set方法即可

                out.write(setMethod(classStr, getParam(c, field.getName()), field.getName()));
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Object> getParam(Class clz, String field) {

        Map<String, Object> map = new HashMap<String, Object>();
        boolean isReference = false;
        String result = "";

        String className = clz.getName();
        String clzName = className;
        if (className.contains(".")) {
            clzName = className.substring(className.lastIndexOf(".") + 1);
        }
        clearSb(sb);
        if (clz.isInterface()) {
            result = null;
        } else if (className.startsWith("[L")) {
            clzName = clzName.replaceAll(";", "");
            sb.append(clzName).append("[] o = ").append("new ").append(clzName).append("[]{};");
            result = sb.toString();
            isReference = true;
        } else if (clz == String.class) {
            sb.append("\"").append(field).append("\"");
            result = sb.toString();
        } else if (clz == Integer.class || clz == int.class) {
            result = "1";
        } else if (clz == Long.class || clz == long.class) {
            result = "1L";
        } else if (clz == Float.class || clz == float.class) {
            result = "1.1f";
        } else if (clz == Double.class || clz == double.class) {
            result = "1.1";
        } else if (clz == Short.class || clz == short.class) {
            result = "(short)1";
        } else if (clz == Boolean.class || clz == boolean.class) {
            result = "true";
        } else if (clz == char.class || clz == Character.class) {
            result = "\'1\'";
        } else if (clz == byte.class || clz == Byte.class) {
            result = "(byte)1";
        } else {
            sb.append(clzName).append(" o = ").append("new ").append(clzName).append("();");
            result = sb.toString();
            isReference = true;
        }

        map.put("result", result);
        map.put("isReference", isReference);

        return map;
    }

    public static String setMethod(String classStr, Map<String, Object> map, String field) {
        clearSb(sb);
        classStr = classStr.substring(classStr.lastIndexOf(".") + 1);
        boolean isReference = (Boolean) map.get("isReference");
        String result = (String) map.get("result");
        String str = firstLowerToUpper(field);
        sb.append("\t");
        sb.append("@Test").append("\n");
        sb.append("\t");
        sb.append("public void testSet").append(str).append("()").append("\n");
        sb.append("\t");
        sb.append("{").append("\n");
        sb.append("\t").append("\t");
        sb.append(classStr).append(" obj = new ").append(classStr).append("();").append("\n");
        sb.append("\t").append("\t");

        if (isReference) {
            sb.append(result).append("\n");
            result = "o";
            sb.append("\t").append("\t");
        }

        sb.append("obj.set").append(str).append("(").append(result).append(");").append("\n");
        sb.append("\t").append("\t");
        sb.append("assertTrue(").append(result).append(" == obj.get").append(str).append("());")
                .append("\n");
        sb.append("\t");
        sb.append("}").append("\n").append("\n");
        return sb.toString();
    }

    public static String firstLowerToUpper(String field) {
        return field.substring(0, 1).toUpperCase() + field.substring(1);
    }


    public static void createTestFile(String projectPath, String[] pkgs, boolean rebuild) {
        OutputStreamWriter out = null;
        for (String pkg : pkgs) {

            String testPkgPath = checkTestPackage(projectPath, pkg);
            String srcPkgPath = getJavaPackagePath(projectPath, pkg);

            File file = new File(srcPkgPath);
            for (File file1 : file.listFiles()) {

                String className = getClassName(file1.getName());
                File testFile = new File(getTestFile(testPkgPath, className));
                if (!testFile.exists() || rebuild) {
                    try {
                        //0. 创建测试用例文件
                        testFile.createNewFile();

                        //1. 写入测试用例类的包名
                        out = new FileWriter(testFile);
                        out.write(packageLine(pkg));

                        //2. 写入junit测试用例用到的包
                        out.write(importJunitClassLine());

                        //3. 写入测试用例类名
                        out.write(startClassNameLine(className + "Test"));

                        //4. before,after方法写入
//                        out.write(beforeMethod());
//                        out.write(afterMethod());

                        //5. 写入测试方法
                        writeClass(getClass(pkg, className), out);

                        // 类结束
                        out.write(endClassNameLine());
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (out != null) {
                            try {
                                out.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

    }

    /**
     * 检查测试类路径下的指定包是否存在，不存在则创建
     *
     * @param projectPath
     * @param pkg
     * @return
     */
    public static String checkTestPackage(String projectPath, String pkg) {
        String dstPath = getTestPackagePath(projectPath, pkg);
        File f = new File(dstPath);
        if (!f.exists()) {
            f.mkdirs();
        }
        return dstPath;
    }

    /**
     * 获取测试类的全路径名
     *
     * @param dstPackage
     * @param className
     * @return
     */
    public static String getTestFile(String dstPackage, String className) {
        clearSb(sb);
        sb.append(dstPackage).append(File.separator).append(className).append("Test.java");
        return sb.toString();
    }

    /**
     * 包声明行
     *
     * @param pkg
     * @return
     */
    public static String packageLine(String pkg) {
        clearSb(sb);
        sb.append("package ").append(pkg).append(";");
        sb.append("\n").append("\n");
        return sb.toString();
    }

    /**
     * junit类导入行
     *
     * @return
     */
    public static String importJunitClassLine() {
        clearSb(sb);
        sb.append("import org.junit.Test;").append("\n");
        sb.append("import org.junit.Before;").append("\n");
        sb.append("import org.junit.After;").append("\n").append("\n").append("\n");
        sb.append("import static org.junit.Assert.*;").append("\n").append("\n");
        return sb.toString();
    }

    /**
     * 类开始
     *
     * @param fileName
     * @return
     */
    public static String startClassNameLine(String className) {
        clearSb(sb);
        sb.append("public class ").append(className).append("\n");
        sb.append("{").append("\n");
        return sb.toString();
    }

    /**
     * 类结束
     *
     * @return
     */
    public static String endClassNameLine() {
        return "\n}";
    }

    /**
     * beforeMethod
     *
     * @return
     */
    public static String beforeMethod() {
        clearSb(sb);
        sb.append("\n").append("\t").append("@Before").append("\n");
        sb.append("\t").append("public void before()").append("\n");
        sb.append("\t").append("{").append("\n");
        sb.append("\t").append("}").append("\n");
        return sb.toString();
    }

    /**
     * afterMethod
     *
     * @return
     */
    public static String afterMethod() {
        clearSb(sb);
        sb.append("\n").append("\t").append("@After").append("\n");
        sb.append("\t").append("public void after()").append("\n");
        sb.append("\t").append("{").append("\n");
        sb.append("\t").append("}").append("\n");
        return sb.toString();
    }

    /**
     * 获取类全名，含有包
     *
     * @param pkg
     * @param className
     * @return
     */
    public static String getClass(String pkg, String className) {
        return pkg + "." + className;
    }

    /**
     * 获取类名，不含包
     *
     * @param fileName
     * @return
     */
    public static String getClassName(String fileName) {
        return fileName.replace(".java", "");
    }

    /**
     * 获取java源文件路径下的指定包
     *
     * @param projectPath
     * @param pkg
     * @return
     */
    public static String getJavaPackagePath(String projectPath, String pkg) {
        String packagePath = pkg.replaceAll("\\.", "/");
        return projectPath + "/src/main/java" + File.separator + packagePath;
    }

    /**
     * 获取java测试类路径下的指定包
     *
     * @param projectPath
     * @param pkg
     * @return
     */
    public static String getTestPackagePath(String projectPath, String pkg) {
        String packagePath = pkg.replaceAll("\\.", "/");
        return projectPath + "/src/test/java" + File.separator + packagePath;
    }

    /**
     * 清空StringBuilder
     *
     * @param sb
     */
    public static void clearSb(StringBuilder sb) {
        sb.delete(0, sb.length());
    }

    public static void main(String[] args) throws IOException {
        String projectPath = "D:/data/IDEA workspace/wms-web";
        String[] pkgs = new String[]{"com.wms.junit"};

        createTestFile(projectPath, pkgs, true);
        //loadClass("com.wms.junit.Person", null);
    }

}
