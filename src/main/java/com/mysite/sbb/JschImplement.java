package com.mysite.sbb;

import com.jcraft.jsch.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

@Component
public class JschImplement {

    private final AddressConfigure addressConfigure;

    @Autowired
    public JschImplement(AddressConfigure addressConfigure) {
        this.addressConfigure = addressConfigure;
    }

    public void JschExecMethod(String command) {
        String host = addressConfigure.getAddress();
        String server_user = "user1";
        String server_passwd = "123456";
        Session session = null;
        Channel channel = null;
        try {
            //JSch 객체 생성
            JSch jsch = new JSch();
            jsch.addIdentity("src/main/resources/server_key/myncp_key.pem");
            session = jsch.getSession(server_user, host, 22);
            //패스워드 설정
            session.setPassword(server_passwd);
            //세션과 관련된 정보 설정
            java.util.Properties config = new java.util.Properties();
            //호스트 정보 검사안함
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            //접속함
            session.connect();
            //sftp 채널 연다
            channel = session.openChannel("exec");
            //채널을 ssh용 채널 객체로 캐스팅
            ChannelExec channelExec = (ChannelExec) channel;
            //명령어 넣기
            channelExec.setCommand(command);
            System.out.println(command);
            InputStream inputStream = channelExec.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            //실행
            channelExec.connect();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }

            bufferedReader.close();

        } catch (JSchException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }

    public Vector<String> JSchSftpMethod(String remote_directory) {
        String host = addressConfigure.getAddress();
        System.out.println("확인할 host "+host);
        String server_user = "user1";
        String server_passwd = "123456";
        Session session = null;
        Channel channel = null;
        Vector<String> directoryNames = new Vector<>();
        try {
            //JSch 객체 생성
            JSch jsch = new JSch();
            jsch.addIdentity("src/main/resources/server_key/myncp_key.pem");
            session = jsch.getSession(server_user, host, 22);
            //패스워드 설정
            session.setPassword(server_passwd);
            //세션과 관련된 정보 설정
            java.util.Properties config = new java.util.Properties();
            //호스트 정보 검사안함
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            //접속함
            session.connect();
            //sftp 채널 연다
            channel = session.openChannel("sftp");
            //채널을 ssh용 채널 객체로 캐스팅
            ChannelSftp channelSftp = (ChannelSftp) channel;
            channelSftp.connect();
            Vector<ChannelSftp.LsEntry> entries = channelSftp.ls(remote_directory);


            for (ChannelSftp.LsEntry entry : entries) {
                if (!entry.getFilename().equals(".") && !entry.getFilename().equals("..")) {
                    directoryNames.add(entry.getFilename());
                }
            }

            channelSftp.disconnect();
            session.disconnect();

        } catch (JSchException e) {
            throw new RuntimeException(e);
        } catch (SftpException e) {
            throw new RuntimeException(e);
        }
        System.out.println("here ok");
        return directoryNames;
    }
}