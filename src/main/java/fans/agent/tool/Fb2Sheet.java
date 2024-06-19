package fans.agent.tool;

import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.oauth2.GoogleCredentials;
import fans.agent.vo.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

//facebook与googe sheet交互操作
@Component
public class Fb2Sheet {
    @Value(value = "${google.sheet.fbPost}")
    private String fbPost;//fb发帖
    @Value(value = "${google.sheet.fbPostData}")
    private String fbPostData;//fb发帖数据
    @Value(value = "${google.sheet.fbActive}")
    private String fbActive;//fb养号
    @Value(value = "${google.sheet.fbAddFriend}")
    private String fbAddFriend;//fb加好友
    @Value(value = "${google.sheet.fbFriendData}")
    private String fbFriendData;//fb好友名单数据
    @Value(value = "${google.sheet.fbSendMsg}")
    private String fbSendMsg;//fb发私信
    @Value(value = "${google.sheet.fbMsgData}")
    private String fbMsgData;//fb采集私信数据
    @Value(value = "${google.sheet.fbMyInfo}")
    private String fbMyInfo;//fb更新个人信息
    @Value(value = "${google.sheet.fbCrawList}")
    private String fbCrawList;//小号采集帖子数据名单

    @Value(value = "${google.sheet.fbSearchFans}")
    private String fbSearchFans;//小号搜粉指令
    @Value(value = "${google.sheet.fbSearchFansRet}")
    private String fbSearchFansRet;//小号搜粉结果
    @Value(value = "${google.sheet.fbSearchFansTmp}")
    private String fbSearchFansTmp;//小号搜粉临时表

    @Value(value = "${google.sheet.accountSheet}")
    private String accountSheet;//账号信息表


    @Autowired
    private SheetOper sheetOper;

    //读取google信息表（分给自己的，且未处理的）
    public List<Account> readToMountAccount(GoogleCredentials credentials, String agentIdSelf){
        List<Account> list=new ArrayList<Account>();
        try{
            ValueRange valueRange=sheetOper.getValues(credentials,"fbdata",accountSheet,"account");
            if(valueRange!=null){
                List<List<Object>> list2=valueRange.getValues();

                for(int i=1;i<list2.size();i++){
                    List<Object> list1=list2.get(i);
                    if(list1.size()>=18){
                        String agentId=(String)(list1.get(0));
                        if(agentId!=null && !agentId.equals(agentIdSelf)){
                            //不是自己需要处理的，过滤
                            continue;
                        }
                        String browserNo=(String)(list1.get(14));
                        String rpa_state=(String)(list1.get(17));
                        if(rpa_state!=null && !rpa_state.equals("")){
                            //rpa已处理，过滤
                            continue;
                        }
                        Account account=new Account();
                        int i1=i+1;
                        account.setSheetLine(i1);
                        //7proxySoft	8proxyName	9proxyType	10proxyHost	11proxyPort	12proxyUser	13proxyPassword	14time0	15browserNo	16link	17time
                        account.setAgentId(agentIdSelf);
                        account.setFansDomain((String)(list1.get(1)));
                        account.setUsername((String)(list1.get(2)));
                        account.setPassword((String)(list1.get(3)));
                        account.setFakey((String)(list1.get(4)));
                        account.setCg((String)(list1.get(5)));
                        account.setAutoFriend((String)(list1.get(6)));
                        account.setProxySoft((String)(list1.get(7)));
                        account.setProxyName((String)(list1.get(8)));
                        account.setProxyType((String)(list1.get(9)));
                        account.setProxyHost((String)(list1.get(10)));
                        account.setProxyPort((String)(list1.get(11)));
                        account.setProxyUser((String)(list1.get(12)));
                        account.setProxyPassword((String)(list1.get(13)));
                        account.setBrowserNo(browserNo);
                        account.setLink((String)(list1.get(15)));
                        account.setTime0((String)(list1.get(18)));

                        list.add(account);
                    }
                }

            }

        }catch (Exception e){
            e.printStackTrace();

        }
        return list;
    }

    //读取个人信息更新表（且未处理的）
    public List<String> readUndealedInfo(GoogleCredentials credentials,String browserNo,String type){
        List<String> list=new ArrayList<String>();
        try{
            ValueRange valueRange=sheetOper.getValues(credentials,"fbdata",fbMyInfo,browserNo);
            if(valueRange!=null){
                List<List<Object>> list2=valueRange.getValues();

                for(int i=1;i<list2.size();i++){
                    List<Object> list1=list2.get(i);
                    if(list1.size()==3){//已处理，只有三个值
                        String type0=(String)(list1.get(0));
                        if(type0!=null && !type0.equals(type)){
                            //类型不一致，过滤
                            continue;
                        }

                        list.add((String)(list1.get(1)));
                    }
                }

            }

        }catch (Exception e){
            e.printStackTrace();

        }
        return list;
    }
   /*
    //小号新增搜粉记录
    public boolean addFbNewSearchValue(GoogleCredentials credentials, String username, SearchFansVo vo){
        try{
            List<Object> values=new ArrayList<Object>();
            values.add(vo.getSearchUrl());
            values.add(vo.getBeginNum());
            values.add(vo.getBeginPage());
            values.add(vo.getEndPage());
            values.add(vo.getTime0());
            sheetOper.appendValues(credentials,"fbdata",fbSearchFans,username,values);
            return  true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    */



}
