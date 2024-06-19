package fans.agent.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.auth.oauth2.GoogleCredentials;
import fans.agent.tool.Fb2Sheet;
import fans.agent.tool.FileTool;
import fans.agent.tool.SheetOper;
import fans.agent.vo.Account;
import fans.agent.vo.AdsListObjVo;
import fans.agent.vo.AdsListRetVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class AgentService {
    @Value(value = "${agent.agentIdFilePath}")
    private String agentIdFilePath;
    @Value(value = "${agent.server}")
    private String server;
    @Value(value = "${agent.agentPath}")
    private String agentPath;

    @Value(value = "${google.sheet.accountSheet}")
    private String accountSheet;//账号信息表
    @Value(value = "${google.sheet.fbMyInfo}")
    private String fbMyInfo;//fb更新个人信息
    @Autowired
    private Fb2Sheet fb2Sheet;
    @Autowired
    private SheetOper sheetOper;
    //心跳
    public void heartBeat(){
        try{


        RestTemplate client = new RestTemplate();
        AdsListRetVo adsListRetVo = client.getForObject("http://local.adspower.net:50325/api/v1/user/list?page_size=200", AdsListRetVo.class);
        int count=0;
        if(adsListRetVo!=null && adsListRetVo.getData()!=null && adsListRetVo.getData().getList()!=null){
            AdsListObjVo[] list=adsListRetVo.getData().getList();
            if(list!=null && list.length>0){
                for (int i = 0; i < list.length; i++) {
                    String last_open_time=list[i].getLast_open_time();
                    Long t1=new Date().getTime()/1000;
                    Long t2=Long.parseLong(last_open_time);
                    if(t1-t2<60*60*24*7){
                        //一周内活跃的账号
                        count++;
                    }
                }
            }
        }
        //汇报内容是多少个号活跃（1周内）
        String agentId= FileTool.readAgentId(agentIdFilePath);
        System.out.println(server+"/interact/fansCtrlNode/agentHeartBeat?count="+count+"&agentId="+agentId);
        String ret = client.getForObject(server+"/interact/fansCtrlNode/agentHeartBeat?count="+count+"&agentId="+agentId, String.class);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //上号挂载
    public void mount(GoogleCredentials credentials){
        try{
        RestTemplate client = new RestTemplate();
        String agentId= FileTool.readAgentId(agentIdFilePath);
        //读取google信息表（分给自己的，且未处理的）
        List<Account> accountList=this.fb2Sheet.readToMountAccount(credentials,agentId);
        if(accountList!=null && accountList.size()>0){
            for (int i = 0; i < accountList.size(); i++) {
              //  for (int i = 0; i < 2; i++) {
                Account account=accountList.get(i);
                //System.out.println(account.getUsername());
                // 形成完整ads请求对象
                JSONObject postData=new JSONObject();
                postData.put("domain_name",account.getFansDomain()+".com");
                postData.put("username",account.getUsername());
                postData.put("password",account.getPassword());
                postData.put("fakey",account.getFakey());
                postData.put("group_id","0");
                JSONArray arr=new JSONArray();
                arr.add(2);
                postData.put("repeat_config",arr);
                postData.put("ip",account.getProxyHost());
                JSONObject user_proxy_config=new JSONObject();
                //代理
                user_proxy_config.put("proxy_soft",account.getProxySoft());
                user_proxy_config.put("proxy_type",account.getProxyType());
                user_proxy_config.put("proxy_host",account.getProxyHost());
                user_proxy_config.put("proxy_port",account.getProxyPort());
                if(account.getProxyUser()!=null && !account.getProxyUser().equals("")){
                    user_proxy_config.put("proxy_user",account.getProxyUser());
                }
                if(account.getProxyPassword()!=null && !account.getProxyPassword().equals("")){
                    user_proxy_config.put("proxy_password",account.getProxyPassword());
                }
                postData.put("user_proxy_config",user_proxy_config);
                JSONObject fingerprint_config=new JSONObject();
                //指纹
                fingerprint_config.put("automatic_timezone",0);
                fingerprint_config.put("timezone","Etc/GMT-8");
                fingerprint_config.put("location","allow");
                fingerprint_config.put("language_switch",0);
                fingerprint_config.put("do_not_track",true);

                postData.put("fingerprint_config",fingerprint_config);

                //调接口创建ads账号
                System.out.println(postData);
                JSONObject retdata = client.postForEntity("http://local.adspower.net:50325/api/v1/user/create", postData, JSONObject.class).getBody();
                System.out.println(retdata);
                //上号完成后将google账号表的浏览器账号更新
                if(retdata!=null && retdata.getInteger("code").intValue()==0){
                    JSONObject  data =retdata.getJSONObject("data");
                    String browserNo=data.getString("serial_number");
                    List<Object> values=new ArrayList<Object>();
                    values.add(browserNo);
                    String range="account!O"+account.getSheetLine()+":O"+account.getSheetLine();
                  //  System.out.println(range);
                    this.sheetOper.appendValues(credentials,"fbdata",accountSheet,range,values);
                }
                Thread.sleep(1000);
            }
        }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //媒体文件本地化
    public void dealMedia(GoogleCredentials credentials){
        try{
            //获取近期有信息更新的浏览器账号（调后台接口）
            RestTemplate client = new RestTemplate();
            String agentId= FileTool.readAgentId(agentIdFilePath);
            List<String> ret = client.getForObject(server+"/interact/fansCtrlAccount/querySyncBrowserNoList?agentId="+agentId, List.class);
            if(ret!=null && ret.size()>0){
                for (int i = 0; i < ret.size(); i++) {
                    String browserNo=ret.get(i);
                   // System.out.println(browserNo);
                    //检查google个人信息表
                    List<String> profiles=this.fb2Sheet.readUndealedInfo( credentials, browserNo,"profile");
                    if(profiles!=null && profiles.size()>0) {
                        for (int j = 0; j < profiles.size(); j++) {
                            String profilePath = profiles.get(j);
                      //      System.out.println(profilePath);
                            //头像下载
                            String savePath=agentPath+"/profiles/"+browserNo+"_profile.png";
                            FileTool.FileDownloader(profilePath,savePath);
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
