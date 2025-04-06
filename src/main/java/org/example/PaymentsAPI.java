package org.example;


import static spark.Spark.get;

public class PaymentsAPI {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        PaymentsService paymentsService = new PaymentsService();

        get("/hello", (req,res) -> "Hello world");

        get("/hello/:name",(request, response) ->
            "Hello " + request.params(":name"));

        get("/getTransferKey/:userid",(request, response) ->
               "idempotency-key : " + paymentsService.generateIdempotencyKey(request.params(":userid")));
    }
}