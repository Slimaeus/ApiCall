package com.example.apicall.rest;

import com.example.apicall.dto.CreateFacultyDTO;
import com.example.apicall.dto.MomoMessage;
import com.example.apicall.dto.MomoPaymentResponse;
import com.example.apicall.encryption.MomoSecurity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    @GetMapping("hello")
    public String getHello() {
        return "Hello";
    }

    @GetMapping("faculties/create")
    public String getClassrooms() {
        String uri = "https://hutechclassroom.azurewebsites.net/api/v1/Faculties";
        RestTemplate restTemplate = new RestTemplate();
        CreateFacultyDTO createFacultyDTO = new CreateFacultyDTO();
        createFacultyDTO.setName("This is a test Faculty");

        String result = restTemplate.postForObject(uri, createFacultyDTO, String.class);
        return result;
    }

    @GetMapping("momo-pay")
    public ResponseEntity<Void> momoPay() throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        String endPoint = "https://test-payment.momo.vn/v2/gateway/api/create";
        String partnerCode = "MOMOOJOI20210710";
        String accessKey = "iPXneGmrJH0G8FOP";
        String secretKey = "sFcbSGRSJjwGxwhhcEktCHWYUuTuPNDB";
        String redirectUrl = "https://hutech-classroom-edu.vercel.app";
        String ipnUrl = "https://webhook.site/00642294-4421-43fd-8415-7e195ddcdad8";
        String requestType = "captureWallet";
        String orderInfo = "Thanh toan";
        String amount = String.valueOf(100000).replaceAll("[^\\d]", ""); // Xóa dấu phẩy
        String orderId = java.util.UUID.randomUUID().toString();
        String requestId = java.util.UUID.randomUUID().toString();
        String extraData = "";

        String rawHash = "accessKey=" + accessKey +
                "&amount=" + amount +
                "&extraData=" + extraData +
                "&ipnUrl=" + ipnUrl +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + partnerCode +
                "&redirectUrl=" + redirectUrl +
                "&requestId=" + requestId +
                "&requestType=" + requestType;

        MomoSecurity crypto = new MomoSecurity();
        // Sign signature SHA256
        String signature = crypto.signSHA256(rawHash, secretKey);


        MomoMessage momoMessage = new MomoMessage();
        momoMessage.setPartnerCode(partnerCode);
        momoMessage.setPartnerName("Test");
        momoMessage.setStoreId("MomoTestStore");
        momoMessage.setRequestId(requestId);
        momoMessage.setAmount(amount);
        momoMessage.setOrderId(orderId);
        momoMessage.setOrderInfo(orderInfo);
        momoMessage.setRedirectUrl(redirectUrl);
        momoMessage.setIpnUrl(ipnUrl);
        momoMessage.setLang("en");
        momoMessage.setExtraData("");
        momoMessage.setRequestType(requestType);
        momoMessage.setSignature(signature);

        RestTemplate restTemplate = new RestTemplate();

        String result = restTemplate.postForObject(endPoint, momoMessage, String.class);

        ObjectMapper objectMapper = new ObjectMapper();

        MomoPaymentResponse response = objectMapper.readValue(result, MomoPaymentResponse.class);

        String externalLink = response.getPayUrl();
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", externalLink)
                .build();
    }
}
