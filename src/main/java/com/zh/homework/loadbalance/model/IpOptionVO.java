package com.zh.homework.loadbalance.model;

import javax.validation.constraints.NotNull;
import java.util.List;

public class IpOptionVO {

    public @interface AddIps{};

    public @interface DeleteIps{};

    /**
     * 服务器IP地址
     */
    @NotNull(groups = {AddIps.class})
    private List<String> serverIps;

    /**
     * 删除所有地址标志
     */
    @NotNull(groups = {DeleteIps.class})
    private Boolean deleteAllFlag;

    public List<String> getServerIps() {
        return serverIps;
    }

    public void setServerIps(List<String> serverIps) {
        this.serverIps = serverIps;
    }

   public Boolean getDeleteAllFlag() {
       return deleteAllFlag;
   }

    public void setDeleteAllFlag(Boolean deleteAllFlag) {
        this.deleteAllFlag = deleteAllFlag;
    }

}
