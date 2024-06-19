package fans.agent.timer;

import com.google.auth.oauth2.GoogleCredentials;
import fans.agent.service.AgentService;
import fans.agent.tool.SheetOper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

//每隔1分钟运行
@Service
@Slf4j
@Component
public class TimerAgent {

    @Autowired
    private AgentService agentService;
    @Autowired
    private SheetOper sheetOper;

    @Scheduled(cron = "0 0/10 * * * ?")
    //@Scheduled(cron = "0 0/1 * * * ?")
    public void run() {

        try{
           System.out.println("TimerAgent:" );
            GoogleCredentials credentials=null;
            agentService.heartBeat();
            credentials=sheetOper.genarateGoogleCredentials();
            agentService.mount(credentials);
       //     agentService.dealMedia(credentials);
        }catch (Exception e){
            e.printStackTrace();
        }finally {

        }
    }
}