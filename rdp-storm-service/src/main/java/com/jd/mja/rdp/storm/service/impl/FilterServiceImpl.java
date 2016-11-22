package com.jd.mja.rdp.storm.service.impl;

import com.jd.mja.rdp.storm.constant.CommonConstant;
import com.jd.mja.rdp.storm.model.Order;
import com.jd.mja.rdp.storm.service.FilterService;
import com.jdjr.rdp.pd.service.RdpPDService;

/**
 * Created by liangchaolei on 2016/9/12.
 */
public class FilterServiceImpl implements FilterService {

    private static final String PD_BIZ="MJA,SDK,RDP,FILTER";

    private RdpPDService jsfRdpPDService;
    private String profileActive;




    @Override
    public boolean check(Order order) {
        if(order==null) return false;
        if(order.getBiz()==null || "".equals(order.getBiz().trim())) return false;
        if(order.getKey()!=null && order.getKey().split(",").length> CommonConstant.MAX_DIMENSION) return false;
        //为null设置成""
        if(order.getKey()==null) order.setKey("");
        return true;

    }

    @Override
    public boolean pdCheck(String biz, String uuid) {
        if("online".equals(profileActive)){
            return jsfRdpPDService.isRepeated(PD_BIZ,biz+","+uuid);
        }
        return false;
    }


    public void setJsfRdpPDService(RdpPDService jsfRdpPDService) {
        this.jsfRdpPDService = jsfRdpPDService;
    }

    public void setProfileActive(String profileActive) {
        this.profileActive = profileActive;
    }
}
