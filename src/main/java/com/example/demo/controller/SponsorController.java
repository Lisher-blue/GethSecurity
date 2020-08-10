package com.example.demo.controller;

import com.example.demo.base.Result;
import com.example.demo.base.Status;
import com.example.demo.base.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.model.CrowdFunding;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import javax.xml.ws.Response;
import java.math.BigInteger;

@Slf4j
@Controller
public class SponsorController {

    @Value("${contract.address}")
    private String ADDRESS;

    @Autowired
    private Web3j web3j;

    @GetMapping("success")
    public String success() {
        return "/success";
    }

    @PostMapping("success")
    public void success(@RequestParam("file") MultipartFile file, @RequestParam("password") String password,
                         @RequestParam("content") String content, @RequestParam("goal") int goal) {
    }

    @GetMapping("contributeSuccess")
    public String contributeSuccess() {
        return "/contributeSuccess";
    }

    @PostMapping("contributeSuccess")
    public void contributeSuccess(@RequestParam("file") MultipartFile file, @RequestParam("password") String password,
                        @RequestParam("content") String content, @RequestParam("goal") int goal) {
    }

    @GetMapping("create")
    public String create() {
        return "/create";
    }

    @PostMapping("create")
    public String create(@RequestParam("file") MultipartFile file, @RequestParam("password") String password,
                         @RequestParam("content") String content, @RequestParam("goal") int goal) {
        try {
            Credentials credentials = WalletUtils.loadCredentials(password, Utils.StoreTmpFile(file.getBytes()));
            CrowdFunding cf = CrowdFunding.load(ADDRESS, web3j, credentials, new DefaultGasProvider());
            cf.create(content, BigInteger.valueOf(goal))
                    .sendAsync(); // 异步
            return "redirect:http://localhost:8080/success";
        } catch (Exception e) {
            log.error("", e);
            return "redirect:http://localhost:8080/";
        }
    }

    @GetMapping("contribute")
    public String contribute() {
        return "/contribute";
    }

    @PostMapping("contribute")
    public String contribute(@RequestParam("file") MultipartFile file, @RequestParam("password") String password,
                             @RequestParam("campaignID") int campaignID, @RequestParam("amount") int amount) {
        try {
            Credentials credentials = WalletUtils.loadCredentials(password, Utils.StoreTmpFile(file.getBytes()));
            CrowdFunding cf = CrowdFunding.load(ADDRESS, web3j, credentials, new DefaultGasProvider());
            cf.contribute(BigInteger.valueOf(campaignID), BigInteger.valueOf(amount).multiply(Convert.Unit.ETHER.getWeiFactor().toBigInteger()))
                    .sendAsync(); // 异步
            return "redirect:http://localhost:8080/contributeSuccess";
        } catch (Exception e) {
            log.error("", e);
            return "redirect:http://localhost:8080/";
        }
    }
}
