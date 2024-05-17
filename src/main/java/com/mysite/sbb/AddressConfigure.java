package com.mysite.sbb;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class AddressConfigure {
    @Value("${myAddress}")
    private String address;

    @Value("${srvAddress}")
    private String srvaddress;

    public String getAddress() {
        return address;
    }


    public String getsrvaddress() {
        return srvaddress;
    }
}
