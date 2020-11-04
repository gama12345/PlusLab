package com.example.pluslab.RestAPI;

import com.example.pluslab.RestAPI.Modelo.DatosUsuarioRequest;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Endpoints {
    @FormUrlEncoded
    @POST(ConstantesAPI.KEY_POST_ID_TOKEN)
    Call<DatosUsuarioRequest> registrarTokenID(@Field("token") String token, @Field("email") String email);

    @GET(ConstantesAPI.TOKEN_ANALISIS_NOTIFICACION)
    Call<DatosUsuarioRequest> enviarNotificacion(@Path("miToken") String miToken);
}
