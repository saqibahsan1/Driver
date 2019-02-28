package com.akhdmny.driver.Service;

import com.akhdmny.driver.ApiResponse.AcceptModel.AcceptOrderApiModel;
import com.akhdmny.driver.ApiResponse.AddComplaintResponse;
import com.akhdmny.driver.ApiResponse.AddToCart;
import com.akhdmny.driver.ApiResponse.BidResp.SubmitBidResp;
import com.akhdmny.driver.ApiResponse.CartOrder;
import com.akhdmny.driver.ApiResponse.ComplainHistory.ComplaintHistoryResponse;
import com.akhdmny.driver.ApiResponse.DeliverOrderPkg.DeliverOrderApi;
import com.akhdmny.driver.ApiResponse.FourSquare;
import com.akhdmny.driver.ApiResponse.MyOrderDetails.MyOrders;
import com.akhdmny.driver.ApiResponse.OrderConfirmation;
import com.akhdmny.driver.ApiResponse.CategoriesDetailResponse;
import com.akhdmny.driver.ApiResponse.CategoriesResponse;
import com.akhdmny.driver.ApiResponse.LoginApiResponse;
import com.akhdmny.driver.ApiResponse.OrdersResponse.GetOrderItemsResp;
import com.akhdmny.driver.ApiResponse.ParcelApiResponse;
import com.akhdmny.driver.ApiResponse.RegisterResponse;
import com.akhdmny.driver.ApiResponse.TimeOut.OrderTimeOut;
import com.akhdmny.driver.ApiResponse.TransactionPojo.TransactionModel;
import com.akhdmny.driver.ApiResponse.UpdateDriverLoc;
import com.akhdmny.driver.ApiResponse.UpdateFbmodel;
import com.akhdmny.driver.ApiResponse.UpdateTokenResponse;
import com.akhdmny.driver.ApiResponse.UserAcceptedResponse.DriverAwardedResp;
import com.akhdmny.driver.ApiResponse.cancelOrder.CancelOrderResponse;
import com.akhdmny.driver.Models.CancelReasonModel;
import com.akhdmny.driver.Requests.LoginRequest;
import com.akhdmny.driver.Requests.ProfileResponse;
import com.akhdmny.driver.Requests.SignInRequest;
import com.akhdmny.driver.Requests.VerificationReguest;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface AuthService {

    @PUT("/akhdmny/public/api/driver/register")
    Call<RegisterResponse> register(@Body SignInRequest request);

    @POST("/akhdmny/public/api/driver/login")
    Call<LoginApiResponse> LoginApi(@Body LoginRequest request);

    @POST("/akhdmny/public/api/driver/verification")
    Call<LoginApiResponse> VerificationApi(@Body VerificationReguest request);

    @GET("/akhdmny/public/api/driver/categories")
    Call<CategoriesResponse> CategoryApi();

    @GET("/akhdmny/public/api/driver/cart_item?lat=24.9070714&long=67.1124509")
    Call<CartOrder> CartOrders();

    @GET("/akhdmny/public/api/driver/cart_item/destroy")
    Call<CartOrder> RemoveCartOrders(@Query("cart_id") int driverId, @Query("lat") double lat, @Query("long") double longi);

    @GET("/akhdmny/public/api/driver/services")
    Call<CategoriesDetailResponse> CatDetails(@Query("category_id") int id, @Query("lat") double lat,@Query("long") double longitude,
                                              @Query("address") String address);

    @GET("/akhdmny/public/api/driver/get-transactions")
    Call<TransactionModel> Transactions();

    @GET("/akhdmny/public/api/driver/get-complains")
    Call<ComplaintHistoryResponse> History();

    @GET("/akhdmny/public/api/driver/foursquare")
    Call<FourSquare> fourSquareApiCall(@Query("lat") double lat, @Query("long") double longi);

    @GET("/akhdmny/public/api/driver/order/request")
    Call<OrderConfirmation> OrderRequest(@Query("lat") double lat,@Query("long") double longitude);

    @GET("/akhdmny/public/api/driver/cancel-reasons")
    Call<CancelReasonModel> cancelApi();

    @GET("/akhdmny/public/api/driver/cancel-order")
    Call<OrderTimeOut> cancelOrderApi(@Query("order_id") String orderId, @Query("cancel_reason") String cancelReason);

    @Multipart
    @POST("/akhdmny/public/api/driver/submit-complain")
    Call<AddComplaintResponse> AddComplaint(@Part("message") RequestBody description, @Part("title") RequestBody title,
                                            @Part MultipartBody.Part[] Images, @Part MultipartBody.Part Sound);
    @Multipart
    @POST("/akhdmny/public/api/driver/cart/store")
    Call<AddToCart> addToCart(@Part("description") RequestBody description,@Part("title") RequestBody title,
                              @Part("type") int type,
                              @Part("address") String address,@Part("amount") int amount,
                              @Part("distance") double distance,@Part("lat") double lat,@Part("long") double longitude,
                              @Part("service_id") String service_id,
                              @Part MultipartBody.Part[] Images, @Part MultipartBody.Part Sound);

    @Multipart
    @POST("/akhdmny/public/api/driver/parcel")
    Call<ParcelApiResponse> ParcelApi(@Part("from_lat") double lat, @Part("from_long") double longitude,
                                      @Part("to_lat") double to_lat, @Part("to_long") double to_longitude,
                                      @Part("description") String description, @Part("amount") int amount, @Part("distance") String distance,
                                      @Part MultipartBody.Part[] Images, @Part MultipartBody.Part Sound);

    @Multipart
    @POST("/akhdmny/public/api/driver/update/profile")
    Call<ProfileResponse> UpdateProfile(@Part("first_name") String firstNme, @Part("last_name") String last_name,
                                        @Part("password") String password, @Part("email") String email,
                                        @Part("address") String address, @Part("gender") String gender, @Part MultipartBody.Part[] Image);

    @GET("/updateToken")
    Call<UpdateTokenResponse> Token(@Query("id") int id, @Query("token") String token);

    @GET("/sendCustomNotification")
    Call<UpdateFbmodel> updateFirebase(@Query("token") String token, @Query("order_id") String order_id,
                                       @Query("title") String title, @Query("body") String body);

    @GET("/akhdmny/public/api/driver/get-order-items")
    Call<GetOrderItemsResp> getOrderItems(@Query("order_id") int orderId);

    @GET("/akhdmny/public/api/driver/get-orders")
    Call<MyOrders> MyOrders();

    @GET("/akhdmny/public/api/driver/get-order-detail")
    Call<DriverAwardedResp> getOrderDetails(@Query("order_id") int orderId);

    @GET("/akhdmny/public/api/user/get-order-detail")
    Call<AcceptOrderApiModel> GetOrderDetails(@Query("order_id") String OrderId);

    @GET("/akhdmny/public/api/driver/deliver-order")
    Call<DeliverOrderApi> DeliverOrder(@Query("order_id") int orderId);

    @GET("/akhdmny/public/api/driver/cancel-order")
    Call<CancelOrderResponse> CancelOrderApi(@Query("order_id") int orderId);

    @GET("/akhdmny/public/api/driver/submit-bid")
    Call<SubmitBidResp> SubmitBid(@Query("order_id") int orderId, @Query("bid") String bid, @Query("lat") double lat, @Query("long") double longitude);

    @GET("/updateGeofireLocation")
    Call<UpdateDriverLoc> DriverLoc(@Query("id") int id, @Query("lat") double lat, @Query("long") double longitude);
}
