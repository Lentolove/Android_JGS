package com.tsp.android.hiui.cityselector;
import androidx.annotation.NonNull;

import com.tsp.android.hiui.cityselector.entity.ProvinceEntity;

import java.util.List;

/**
 * 地址数据解析器
 */
public interface AddressParser {

    @NonNull
    List<ProvinceEntity> parseData(@NonNull String text);
}
