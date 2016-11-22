package com.jdjr.rdp.pd.service;

/**
 * Created by end on 2016/3/1 0001.
 */
public interface RdpPDService {
        boolean isRepeated(String biz, String uuid);
        boolean isRepeatedWithTimeOut(String biz, String uuid, int timeOut);

        boolean isRepeatedWithIsRecord(String biz, String uuid, boolean isRecord);

        boolean isRepeatedWithAll(String biz, String uuid, int timeOut, boolean isRecord);
        boolean delRepeated(String biz, String uuid);
}
