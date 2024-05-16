package com.mysite.sbb.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import com.fasterxml.jackson.core.Base64Variant;
import com.mysite.sbb.AddressConfigure;
import com.mysite.sbb.JschImplement;
import com.mysite.sbb.entity.Question;
import com.mysite.sbb.entity.SiteUser;
import com.mysite.sbb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mysite.sbb.DataNotFoundException;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AddressConfigure addressConfigure;

	public SiteUser create(String username, String email, String password) {
		SiteUser user = new SiteUser();
		user.setUsername(username);
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(password));
		this.userRepository.save(user);
		List<String> commandList = new ArrayList<>();

		//RSTUDIO 계정생성 코드
		commandList.add("echo \"123456\" | sudo -S useradd -m " + username);
		commandList.add("echo -e \"123456\\n"+password+"\\n"+password+"\" | sudo -S passwd "+username);

		commandList.add("echo \"123456\" | sudo -S mkdir /srv/shiny-server/sample-apps/" + username);
//		commandList.add("echo \"123456\" | sudo -S mkdir -p /home/" + username + "/R/x86_64-pc-linux-gnu-library/4.3");
		commandList.add("echo \"123456\" | sudo -S mkdir -p /home/" + username + "/shiny");
		commandList.add("echo \"123456\" | sudo -S chmod 777 /home/" + username + "/shiny");
//		commandList.add("echo \"123456\" | sudo -S mount --bind /usr/local/lib/R/site-library /home/"+username+"/R/x86_64-pc-linux-gnu-library/4.3");
//		//ShinyServer 디렉토리 생성 코드
		for (String s : commandList) {
			JschImplement jsch = new JschImplement(addressConfigure);
			jsch.JschExecMethod(s);
        }
		return user;
	}

	public SiteUser getUser(String username) {
		Optional<SiteUser> siteUser = this.userRepository.findByusername(username);
		if (siteUser.isPresent()) {
			return siteUser.get();
		} else {
			throw new DataNotFoundException("siteuser not found");
		}
	}
	public void delete(SiteUser siteUser) {
		this.userRepository.delete(siteUser);

	}


}
