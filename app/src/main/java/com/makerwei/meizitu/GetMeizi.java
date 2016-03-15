package com.makerwei.meizitu;

import com.makerwei.meizitu.model.Meizi;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by weixianbin on 16/3/4.
 */
public interface GetMeizi {
    @GET("data/福利/10/{id}")
    Call<Meizi> get(@Path("id") int groupId);
}
