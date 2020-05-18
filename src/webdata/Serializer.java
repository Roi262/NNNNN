package webdata;

import java.io.*;


public class Serializer {

    public static void WriteObjectToFile(Object serObj, String path) throws IOException {
        try {
            FileOutputStream fos = new FileOutputStream(path);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(serObj);
            oos.close();
            fos.close();
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
//        System.out.println("The Object was succesfully written to a filfe");
    }


    public static Object ReadObjectFromFile(String filePath) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(filePath);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object obj = ois.readObject();
        ois.close();
        fis.close();
        return obj;
    }
}
