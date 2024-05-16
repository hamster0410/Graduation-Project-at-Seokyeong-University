package com.mysite.sbb.service;


import com.mysite.sbb.AddressConfigure;
import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.JschImplement;
import com.mysite.sbb.entity.R_Container;
import com.mysite.sbb.entity.SiteUser;
import com.mysite.sbb.repository.R_ContainerRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class R_containerService {
    private final R_ContainerRepository rContainerRepository;
    private final AddressConfigure addressConfigure;

    public R_Container getRcontainer(SiteUser siteUser) {
        Optional<R_Container> rContainer = Optional.ofNullable(this.rContainerRepository.findByAuthor(siteUser));
        if (rContainer.isPresent()) {
            return rContainer.get();
        } else {
            throw new DataNotFoundException("rcontainer not found");
        }
    }

    public boolean[] using_container(boolean[] container_list) {
        List<String> using_now = rContainerRepository.findAllPortNumbers();
        int index = 0;
        for (String portNumber : using_now) {
            index = Integer.parseInt(portNumber); // 문자열을 정수로 변환
            container_list[index-3000] = true; // 해당 인덱스에 true 설정
        }
        return container_list;
    }

    public void create_Container(SiteUser siteUser, int i, String password) {
        int port_number = 3000+i;
        String username = siteUser.getUsername();
        R_Container rContainer = new R_Container();
        rContainer.setAuthor(siteUser);
        rContainer.setPortNumber(port_number);
        rContainer.setContainerName(siteUser.getUsername());
        rContainer.setCreateDate(LocalDateTime.now());
        siteUser.setHasContainer(true);
        rContainerRepository.save(rContainer);

        JschImplement js = new JschImplement(addressConfigure);
        System.out.println(username);
        String command = "echo \"123456\" | sudo -S docker service create --name rstudio-"+username+" \\\n" +
                "  --network rstudio \\\n" +
                "  --publish "+String.valueOf(port_number)+":8787 \\\n" +
                "  --env PASSWORD="+password+" \\\n" +
                "  --env USER="+ username + " \\\n" +
                "  --replicas 1 \\\n" +
                "  --mount type=bind,source=/home/"+username+"/shiny,target=/home/"+username+"/shiny/" +
                "  --mount type=bind,source=/mnt/data,target=/home/"+username+"/data/" +
                "  --mount type=bind,source=/mnt/shiny_library,target=/usr/local/lib/R/site-library" +
//                "  --mount type=bind,source=/usr/include,target=/usr/include" +

                "  --constraint 'node.role == worker' \\\n" +
                "  my-rstudio-image:latest\n";
        js.JschExecMethod(command);
    }

    public void delete_Container(SiteUser siteUser) {
        R_Container rc = getRcontainer(siteUser);
        siteUser.setHasContainer(false);
        rContainerRepository.delete(rc);
        JschImplement js = new JschImplement(addressConfigure);
        String command = "echo \"123456\" | sudo -S docker service rm rstudio-" + siteUser.getUsername();
        js.JschExecMethod(command);
    }
}
