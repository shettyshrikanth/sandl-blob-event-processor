package uk.gov.moj.cpp.sandl.client;


import static java.lang.String.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class PutBlobRestClient {


    public static void main (String args[]) throws Exception {
        String path = "https://sandlstorage.blob.core.windows.net/sandlblobcontainer/lmn/%s?%s";
        String params = "sv=2018-03-28&ss=bfqt&srt=sco&sp=rwdlacup&se=2020-10-15T17:32:23Z&st=2018-10-15T09:32:23Z&sip=5.148.40.98&spr=https&sig=tmmRyszfvFVgffRs%2FytzPhwITZsMmmg6vQ%2FjvWFBDc4%3D";

        try (Stream<Path> paths = Files.walk(Paths.get("/home/shrikanth/Desktop/SCHEDULING_AND_LISTING/BULK-UPLOAD"))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            byte[] bFile = Files.readAllBytes(file);
                            final URL url =new URL(format(path, file.getFileName().toString(),params));
                            System.out.println(format(path, file.getFileName().toString(),params));

                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setDoOutput(true);
                            conn.setRequestMethod("PUT");
                            conn.setRequestProperty("Content-Type", "application/xml");
                            conn.setRequestProperty("x-ms-blob-type","BlockBlob");

                            OutputStream os = conn.getOutputStream();
                            os.write(bFile);
                            os.flush();

                            System.out.println(conn.getResponseCode());

                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        } /*

        String path = "/home/shrikanth/Desktop/SCHEDULING_AND_LISTING/BULK-UPLOAD/";
        String file = "1.xml";
        for(int i =2; i < 11; i++) {
            FileChannel src = new FileInputStream(path+file).getChannel();
            FileChannel dest = new FileOutputStream(path+i+".xml").getChannel();
            dest.transferFrom(src, 0, src.size());
        }*/
    }



}
