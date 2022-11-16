package com.kovynev.payout.application.configuration;

import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.integration.ftp.session.FtpRemoteFileTemplate;

import static org.apache.commons.net.ftp.FTPClient.PASSIVE_LOCAL_DATA_CONNECTION_MODE;

@Configuration
@Log4j2
@Profile("prod")
public class WakandaFtpConfiguration {

    @Value("${wakanda.ftp.host:#{null}}")
    private String ftpHost = null;

    @Value("${wakanda.ftp.port:#{21}}")
    private Integer ftpPort;

    @Value("${wakanda.ftp.user:#{null}}")
    private String ftpUser = null;

    @Value("${wakanda.ftp.pass:#{null}}")
    private String ftpPasword = null;

    @Bean
    public SessionFactory<FTPFile> ftpSessionFactory() {
        val factory = new DefaultFtpSessionFactory();
        factory.setHost(ftpHost);
        factory.setPort(ftpPort);
        factory.setUsername(ftpUser);
        factory.setPassword(ftpPasword);
        factory.setClientMode(PASSIVE_LOCAL_DATA_CONNECTION_MODE);
//        factory.setFileType(FTP.ASCII_FILE_TYPE);

        return new CachingSessionFactory<>(factory);
    }

    @Bean
    public FtpRemoteFileTemplate template(SessionFactory<FTPFile> sf) {
        return new FtpRemoteFileTemplate(sf);
    }
}
