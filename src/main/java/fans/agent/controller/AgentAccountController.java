package fans.agent.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: 平台账号外出处理
 * @Author: jeecg-boot
 * @Date:   2024-02-11
 * @Version: V1.0
 */
@RestController
@RequestMapping("/agent/account")
@Slf4j
public class AgentAccountController {

            //后台发起：创建和修改本机ads的账号
            @PostMapping(value = "/mountAccount")
            public String mountAccount(@RequestBody String fansCtrlAccount) {
                //调用ads接口
                //查询账号是否存在，有则创建，无则修改
                return "";
            }
            //上传部分媒体文件到本地

}
