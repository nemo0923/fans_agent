package fans.agent.tool;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class FileTool {
    public static String readAgentId(String filePath){
        String line = null;
        try {
            InputStreamReader read = new InputStreamReader(
                    new FileInputStream(filePath), StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(read);

            if ((line = bufferedReader.readLine()) != null) {
                //System.out.println(line);
            }
            bufferedReader.close();
            read.close();
        }catch (IOException exception){
            System.out.println(exception);
        }
        return line;
    }
    public static void FileDownloader(String fileUrl,String savePath) throws Exception{
        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = connection.getInputStream();
            FileOutputStream outputStream = new FileOutputStream(savePath);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

        } else {
        }
    }
}