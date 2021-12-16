package struct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

/**
 * struct
 * Created by Duy
 * Date 12/13/2021 - 11:00 PM
 * Description: ...
 */
public class StructClass {

    public StructClass(){
    }

    public static String pack(HashMap<String,String> data) {
        String[] keys =data.keySet().toArray(new String[0]);
        String str="";
        for (int i = 0; i <keys.length - 1;++i){
            str += keys[i] + "=" + data.get(keys[i])+";";
        }
        int index = keys.length-1;
        str+= keys[index] + "=" + data.get(keys[index]);
        return str;
    }

   public static HashMap<String,String> unpack(String message,String ... type){
       HashMap<String, String> data = new HashMap<>();
           if (type.length == 0) {
               String [] string;
               try {
                   string = message.split(";");
                   for (int i = 0; i < string.length; i++) {
                       String[] infor = string[i].split("=");
                       if (infor.length == 2) {
                           data.put(infor[0], infor[1]);
                       }
                   }
               }catch (Exception e){
                   System.out.println("Unpack = " + message);
               }

           }
       return data;
   }

   public static String[] getUserListByString(String listString){
        try {
            return listString.split(",");
        }catch (Exception e){
            return new String[]{};
        }
   }
   public static  String packBufferToString(byte[] buffer){
        try {
            String string = "";
            for (int i = 0; i < buffer.length - 1; ++i) {
                string += buffer[i] + ",";
            }
            string += buffer[buffer.length - 1];
            return string;
        }catch (Exception e){
            System.out.println("Pack buffer" + e.getMessage());
            return "";
        }
   }
   public static byte[] unpackBufferFromString(String string){
        try{
            String[] list = string.split(",");
            byte [] buffer = new byte[list.length];
            for (int i = 0 ; i < list.length;i++){
                buffer[i] = Byte.parseByte(list[i]);
            }
            return buffer;
        }catch (Exception e){
            return null;
        }
   }
//   public static void main(String[]args){
//       HashMap<String, String> packages = new HashMap<>();
//       packages.put("type","login");
//       packages.put("user","[]");
//       ArrayList<String> user = new ArrayList<>();
//       user.add("duy");
//       user.add("kha");
//       String string = user.toString().replace("[","").replace("]","");
//
//       System.out.println(string);
//
//   }
}

