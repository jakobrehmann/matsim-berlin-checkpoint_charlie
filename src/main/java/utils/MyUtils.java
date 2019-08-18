package utils;

import org.matsim.core.utils.io.IOUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class MyUtils {
    public static ArrayList<String> readLinksFile(String fileName){
        Scanner s ;
        ArrayList<String> list = new ArrayList<String>();
        try {
            s = new Scanner(new File(fileName));
            while (s.hasNext()){
                list.add(s.next());
            }
            s.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void writeIdsToFile(ArrayList<String> linkIds, String outputFile){
        BufferedWriter bw = IOUtils.getBufferedWriter(outputFile);
        try {
            for (int i = 0;i< linkIds.size();i++){
                bw.write(linkIds.get(i));
                bw.newLine();
            }
            bw.flush();
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
