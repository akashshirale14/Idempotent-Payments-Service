package org.example;


import static spark.Spark.get;
import static spark.Spark.post;

public class PaymentsAPI {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        PaymentsService paymentsService = new PaymentsService();

        get("/hello", (req,res) -> "Hello world");

        get("/hello/:name",(request, response) ->
            "Hello " + request.params(":name"));

        get("/getPaymentKey/:userid",(request, response) ->
               "idempotency-key : " + paymentsService.generateIdempotencyKey(request.params(":userid")));

        post("/doPayment/:userid", (request, response) -> {
            String key = request.headers("idempotency-key");
            String responseBody = "Transaction Result : ";
            boolean ans = paymentsService.validateTransactionStatus(key);
            if (!ans) {
                return responseBody + "Invalid Payment Key";
            }
            String transactionType = request.headers("transaction-type");
            String senderID = request.params(":userid");
            String receiverID = request.headers("receiver-id");
            String amount = request.headers("payment-amt");
            System.out.println(transactionType + " " + senderID + " " + receiverID +" " + amount);
            System.out.println("Storing status info..");
            String statusMsg = paymentsService.storeTransactionStatusInfo(transactionType,key,senderID,receiverID,amount);
            if(statusMsg.equals("Success")) {
                //do transaction in SQL
                System.out.println("Doing transaction...");
                statusMsg = paymentsService.doTransactionInDB(key,senderID,receiverID,Integer.parseInt(amount),transactionType);
                //do logging in SQL

                //send response
            }
            return "Transaction Result : " + statusMsg;
        });
    }
}