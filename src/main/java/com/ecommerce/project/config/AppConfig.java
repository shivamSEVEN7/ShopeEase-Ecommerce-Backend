package com.ecommerce.project.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ecommerce.project.model.*;
import com.ecommerce.project.repositories.CategoryRepo;
import com.ecommerce.project.repositories.ProductRepo;
import com.ecommerce.project.repositories.RoleRepo;
import com.ecommerce.project.repositories.UserRepo;
import io.netty.util.internal.ObjectUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua_parser.Parser;
import ua_parser.UserAgentParser;

@Configuration
public class AppConfig {
    @Value("${cloudinary.config.name}")
    private String cloudinaryName;
    @Value("${cloudinary.config.key}")
    private String cloudinaryKey;
    @Value("${cloudinary.config.secret}")
    private String cloudinarySecret;

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
    @Bean
    public Parser parser(){return new Parser();}
    @Bean
    public Cloudinary cloudinary(){
        return new Cloudinary(ObjectUtils.asMap("cloud_name", cloudinaryName,
                "api_key", cloudinaryKey,
                "api_secret", cloudinarySecret,
                "secure", true));
    }


}
