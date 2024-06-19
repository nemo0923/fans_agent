package fans.agent.tool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.api.services.sheets.v4.SheetsScopes;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SheetOper {
    @Value(value = "${google.credentialsPath}")
    private String credentialsPath;
    @Value(value = "${google.account}")
    private String account;


    public static void main(String[] args) throws Exception {
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("/Users/nemo/fans/fbdata-415411-2e8cb66bfb6e.json"))
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS))
                .createDelegated("lixiao@uma313.net");
    }

    public  GoogleCredentials genarateGoogleCredentials() throws IOException, GeneralSecurityException {
        return GoogleCredentials.fromStream(new FileInputStream(this.credentialsPath))
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS))
                .createDelegated(this.account);
    }
    //添加工作表
    public Integer addSheet(GoogleCredentials credentials, String appname,String spreadsheetId, String sheetName) throws Exception{
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
        Integer sheetId=null;
        Sheets service = new Sheets.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName(appname)
                .build();
        List<Request> requests = new ArrayList<>();
        BatchUpdateSpreadsheetResponse response = null;
        ValueRange result = null;
        AddSheetRequest addSheetRequest=new AddSheetRequest()
                .setProperties(new SheetProperties()
                        .setTitle(sheetName));
            requests.add(new Request()
                    .setAddSheet(addSheetRequest
                            ));
            BatchUpdateSpreadsheetRequest body =
                    new BatchUpdateSpreadsheetRequest().setRequests(requests);
            response = service.spreadsheets().batchUpdate(spreadsheetId, body).execute();
      //  System.out.println(response.toPrettyString());
        List list=(List)response.get("replies");
        if(list!=null && list.size()>0){
            JSONObject jobj = JSON.parseObject(list.get(0).toString());
            sheetId=jobj.getJSONObject("addSheet").getJSONObject("properties").getInteger("sheetId");
        }

        return sheetId;
    }
    //指定表格和工作表，删除
    public void deleteSheet(GoogleCredentials credentials, String appname,String spreadsheetId, Integer sheetId) throws IOException, GeneralSecurityException{
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        Sheets service = new Sheets.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName(appname)
                .build();
        List<Request> requests = new ArrayList<>();
        BatchUpdateSpreadsheetResponse response = null;
        ValueRange result = null;
        requests.add(new Request()
                .setDeleteSheet(new DeleteSheetRequest().setSheetId(sheetId)));
        BatchUpdateSpreadsheetRequest body =
                new BatchUpdateSpreadsheetRequest().setRequests(requests);
        response = service.spreadsheets().batchUpdate(spreadsheetId, body).execute();

    }
    //指定表格和工作表，批量写入指定范围的数据

    public void batchAppendValues(GoogleCredentials credentials, String appname,String spreadsheetId, String range,List<List<Object>> values) throws Exception{
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        Sheets service = new Sheets.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName(appname)
                .build();
        AppendValuesResponse result = null;


        ValueRange body = new ValueRange()
                .setValues(values);
        result = service.spreadsheets().values().append(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .execute();
    }
    //指定表格和工作表，写入指定范围的数据
    public void appendValues(GoogleCredentials credentials, String appname,String spreadsheetId, String range,List<Object> values) throws Exception{
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        Sheets service = new Sheets.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName(appname)
                .build();
        AppendValuesResponse result = null;
        List<List<Object>> list =new ArrayList<List<Object>>();
        list.add(values);

            ValueRange body = new ValueRange()
                    .setValues(list);
            result = service.spreadsheets().values().append(spreadsheetId, range, body)
                    .setValueInputOption("RAW")
                    .execute();
    }
    //指定表格和工作表，读取指定列范围的数据
    public  ValueRange getValues(GoogleCredentials credentials, String appname,String spreadsheetId, String range) throws Exception{
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        Sheets service = new Sheets.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName(appname)
                .build();

        ValueRange result = null;

            result = service.spreadsheets().values().get(spreadsheetId, range).execute();

        return result;
    }


}
