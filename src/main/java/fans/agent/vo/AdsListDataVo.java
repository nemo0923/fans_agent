package fans.agent.vo;

import lombok.Data;

@Data
public class AdsListDataVo {
    private AdsListObjVo[] list;
    private Integer page;
    private String page_size;
}
