package com.goldze.mvvmhabit.data.source.http.service;

import com.goldze.mvvmhabit.entity.DemoEntity;
import com.goldze.mvvmhabit.entity.VillageStructuresResult;

import java.util.List;

import io.reactivex.Observable;
import me.goldze.mvvmhabit.http.BaseResponse;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by goldze on 2017/6/15.
 */

public interface DemoApiService2 {
    /**
     * 逐级获取。
     * 	不填写villageID， parentStructureID=0，则获取TenantCode下所有的小区一级的节点信息；
     * 	填写villageID，并且parentStructureID=0，则获取villageID此小区节点下一级节点的信息；
     * 	无论是否填写villageID，parentStructureID是小区节点下的任意级节点时，则获取parentStructureID此节点下一级的节点信息。
     *
     * @param TenantCode 区域Code
     * @param villageID  小区ID
     * @return
     */
    @Headers({"ContentType: application/json", "Authorization: Basic QUNTV2ViQXBpOjEyMzQ1Ng=="})//需要添加头
    @GET("VillageStructures")
    Observable<List<VillageStructuresResult>> getTenantList(@Query("TenantCode") String TenantCode, @Query("villageID") String villageID);
}
