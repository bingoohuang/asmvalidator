package com.github.bingoohuang.asmvalidator.validator.domain;

import java.util.List;

public class ListBean {
    private String name;
    private List<MobileBean> mobiles;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MobileBean> getMobiles() {
        return mobiles;
    }

    public void setMobiles(List<MobileBean> mobiles) {
        this.mobiles = mobiles;
    }
}
