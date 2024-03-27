package com.dsi.backend.service.implententation;

import com.dsi.backend.exception.FinalBuyerNotFoundException;
import com.dsi.backend.exception.ReqParamNotFoundException;
import com.dsi.backend.exception.TransactionNotFoundException;
import com.dsi.backend.model.*;
import com.dsi.backend.projection.PaymentGatewayReqView;
import com.dsi.backend.repository.PaymentGatewayReqParamRepository;
import com.dsi.backend.repository.TransactionRepository;
import com.dsi.backend.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PaymentGatewayServiceImpl implements PaymentGatewayService {

    private final WebClient webClient;
    private static ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private ProductService productService;
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private PaymentGatewayReqParamRepository paymentGatewayReqParamRepository;
    @Autowired
    private BidService bidService;
    @Autowired
    private NotificationService notificationService;


    @Autowired
    public PaymentGatewayServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public PaymentGatewayResp initiatePayment(Product product, String token) throws JsonProcessingException {
        product = productService.fetchProductById(product.getId());
        AppUser appUser = appUserService.fetchInformation(token);
        Bid bid = bidService.fetchBidByProductAndBuyer(product.getId(),appUser.getEmail());
        if(bid==null) throw new FinalBuyerNotFoundException("Final buyer does not exist");

        Transaction transaction = new Transaction();
        transaction = transactionRepository.save(transaction);

        PaymentGatewayReqParam otherParams = paymentGatewayReqParamRepository.findById(1L)
                .orElseThrow(()->new ReqParamNotFoundException("Req param not found"));
        transaction.setPaymentGatewayReqParam(otherParams);
        transaction.setProduct(product);
        transaction.setAppUser(appUser);
        transaction.setFinalPrice(bid.getOfferedPrice());
        transaction = transactionRepository.save(transaction);

        String param = this.convertToUrl(transaction, Long.toString(product.getId()));
        PaymentGatewayResp paymentGatewayResp = this.postCall(param);
        transaction.setConnectionStatus(paymentGatewayResp.getStatus());
        transactionRepository.save(transaction);
        return paymentGatewayResp;
    }

    private PaymentGatewayResp postCall(String param) throws JsonProcessingException {


        String responseString = webClient
                .post()
                .uri("/gwprocess/v4/api.php")
                .body(BodyInserters.fromValue(param))
                .exchangeToMono(r -> {
                    if (r.statusCode().equals(HttpStatus.OK)) {
                        return r.bodyToMono(String.class);
                    } else if (r.statusCode().is4xxClientError()) {
                        return Mono.just("Error response");
                    } else {
                        return r.createException()
                                .flatMap(Mono::error);
                    }
                })
                .block();

        return this.convertToObject(responseString);
    }
    @Override
    public String convertToUrl(Transaction transaction, String productId) {
        PaymentGatewayReqView paymentGatewayReqView = this.convertToView(transaction);
        Map<String, String> paymentGatewayMap = objectMapper.convertValue(paymentGatewayReqView, Map.class);
        paymentGatewayMap.put("store_id", System.getenv("STORE_ID"));
        paymentGatewayMap.put("store_passwd", System.getenv("STORE_PASSWD"));

        String url = System.getenv("FRONTEND_BASE_URL");

        String successUrl = url+System.getenv("SSL_SUCCESS_URL")+"/"+paymentGatewayReqView.getTran_id();
        String cancelUrl = url+System.getenv("SSL_CANCEL_URL")+"/"+productId;
        String failUrl = url+System.getenv("SSL_FAIL_URL")+"/"+productId;

        paymentGatewayMap.put("success_url", successUrl);
        paymentGatewayMap.put("cancel_url", cancelUrl);
        paymentGatewayMap.put("fail_url", failUrl);


        return paymentGatewayMap.keySet().stream()
                .map(key -> key + '=' + URLEncoder.encode(paymentGatewayMap.get(key), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
    }

    @Override
    public PaymentGatewayResp convertToObject(String response) throws JsonProcessingException {
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.readValue(response, PaymentGatewayResp.class);
    }

    @Override
    public PaymentGatewayReqView convertToView(Transaction transaction) {
        return new SpelAwareProxyProjectionFactory().createProjection(PaymentGatewayReqView.class, transaction);
    }

    @Override
    public Map<String, String> validatePayment(ValidateResp validateResp, String token) {
        AppUser appUser = appUserService.fetchInformation(token);

        Transaction transaction = transactionRepository.findByTran_id(validateResp.getTran_id())
                .orElseThrow(()->new TransactionNotFoundException("Transaction does not exist"));
        transaction.setTransactionStatus(validateResp.getTransactionStatus());
        if(validateResp.getTransactionStatus().equalsIgnoreCase("SUCCESS")){
            Boolean status = bidService.changeIsSold(transaction.getProduct().getId(), appUser.getEmail());

            if(!status) throw new FinalBuyerNotFoundException("Final buyer id does not match or null");
            notificationService.transactionNotification(appUser, transaction.getTran_id());
        }
        transactionRepository.save(transaction);
        return Map.of("transactionStatus",validateResp.getTransactionStatus());
    }
}
