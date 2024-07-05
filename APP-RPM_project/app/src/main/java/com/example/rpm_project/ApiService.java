package com.example.rpm_project;

import java.time.LocalDate;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @FormUrlEncoded
    @POST("/signup")
    Call<Void> signup(
            @Field("id") String id,
            @Field("pw") String pw,
            @Field("name") String name,
            @Field("birth") LocalDate birth,
            @Field("email") String email,
            @Field("number") String number
    );

    @FormUrlEncoded
    @POST("/login") // 실제 로그인 API 엔드포인트 경로
    Call<ResponseBody> login(
            @Field("id") String id,
            @Field("pw") String pw
    );

    @GET("/update/{userNo}")
    Call<User> getUser(@Path("userNo") int userNo);

    @PUT("/update/{userNo}")
    Call<Void> updateUser(@Path("userNo") int userNo, @Body User updatedUser);

    @DELETE("/delete/{userNo}")
    Call<Void> deleteUser(@Path("userNo") int userNo);

    @POST("/validate_code")
    Call<ResponseBody> validateCode(@Query("code") String code);

    @POST("/api/childInfo")
    Call<Void> addChildInfo(@Query("userNo") int userNo, @Body ChildInfo childInfo);

    @GET("/api/childInfo")
    Call<List<ChildInfo>> getAllChildInfo();
}
