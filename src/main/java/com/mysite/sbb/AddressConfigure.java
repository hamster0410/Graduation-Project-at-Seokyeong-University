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
    public String getAddress() {
        return address;
    }
}
