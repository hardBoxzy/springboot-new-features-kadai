package com.example.samuraitravel.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.samuraitravel.form.ReservationRegisterForm;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionRetrieveParams;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class StripeService {
    @Value("${stripe.api-key}")
    private String stripeApiKey;
	
    private final ReservationService reservationService;
    
    public StripeService(ReservationService reservationService) {
        this.reservationService = reservationService;
    }   
    
    // セッションを作成し、Stripeに必要な情報を返す
    public String createStripeSession(String houseName, ReservationRegisterForm reservationRegisterForm, HttpServletRequest httpServletRequest) {
    	Stripe.apiKey = stripeApiKey;
        String requestUrl = new String(httpServletRequest.getRequestURL());
        SessionCreateParams params =
            SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)//決済方法	クレジットカード
                .addLineItem(
                    SessionCreateParams.LineItem.builder()
                        .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()   
                                .setProductData(
                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(houseName)//商品名	民宿名
                                        .build())
                                .setUnitAmount((long)reservationRegisterForm.getAmount())//料金	宿泊料金
                                .setCurrency("jpy")   //   通貨	日本円                          
                                .build())
                        .setQuantity(1L)//数量	1
                        .build())
                .setMode(SessionCreateParams.Mode.PAYMENT)//支払いモード	一回限りの支払い
                .setSuccessUrl(requestUrl.replaceAll("/houses/[0-9]+/reservations/confirm", "") + "/reservations?reserved")//決済成功時のリダイレクト先URL	予約一覧ページ（https://ドメイン名/reservations?reserved）
                .setCancelUrl(requestUrl.replace("/reservations/confirm", ""))//決済キャンセル時のリダイレクト先URL	民宿詳細ページ（https://ドメイン名/houses/{id}）
                .setPaymentIntentData(
                    SessionCreateParams.PaymentIntentData.builder()
                        .putMetadata("houseId", reservationRegisterForm.getHouseId().toString())//メタデータ「houseId」	民宿ID
                        .putMetadata("userId", reservationRegisterForm.getUserId().toString())
                        .putMetadata("checkinDate", reservationRegisterForm.getCheckinDate())
                        .putMetadata("checkoutDate", reservationRegisterForm.getCheckoutDate())//メタデータ「checkoutDate」	チェックアウト日
                        .putMetadata("numberOfPeople", reservationRegisterForm.getNumberOfPeople().toString())//メタデータ「numberOfPeople」	宿泊人数
                        .putMetadata("amount", reservationRegisterForm.getAmount().toString())//メタデータ「amount」	宿泊料金
                        .build())
                .build();
        try {
            Session session = Session.create(params);
            return session.getId();
        } catch (StripeException e) {
            e.printStackTrace();
            return "";
        }
    } 
    // セッションから予約情報を取得し、ReservationServiceクラスを介してデータベースに登録する  
    public void processSessionCompleted(Event event) {
    	// 引数として受け取ったEventオブジェクトからStripeObjectオブジェクトを取得し
        Optional<StripeObject> optionalStripeObject = event.getDataObjectDeserializer().getObject();//Optionalとは、nullを持つ可能性のあるオブジェクトを扱うためのクラスのことです。Java8から導入されました
        // それをSessionオブジェクトに型変換しています。
        optionalStripeObject.ifPresentOrElse(stripeObject -> {
            Session session = (Session)stripeObject;
         // "payment_intent"情報を展開する（詳細情報を含める）ように指定したSessionRetrieveParamsオブジェクトを生成
            SessionRetrieveParams params = SessionRetrieveParams.builder().addExpand("payment_intent").build();

            try {
                session = Session.retrieve(session.getId(), params, null);
                Map<String, String> paymentIntentObject = session.getPaymentIntentObject().getMetadata();
                reservationService.create(paymentIntentObject);
            } catch (StripeException e) {
                e.printStackTrace();
            }
            System.out.println("予約一覧ページの登録処理が成功しました。");
            System.out.println("Stripe API Version: " + event.getApiVersion());
            System.out.println("stripe-java Version: " + Stripe.VERSION);
        },
        () -> {
            System.out.println("予約一覧ページの登録処理が失敗しました。");
            System.out.println("Stripe API Version: " + event.getApiVersion());
            System.out.println("stripe-java Version: " + Stripe.VERSION);
        });
    }
}