package co.atrasvida.example.ApiClient;

import com.google.gson.annotations.SerializedName;

import java.util.List;


class BaseResponse<T> {

    @SerializedName("IsSuccess")
    boolean isSuccess = false;

    @SerializedName("Item")
    T item = null;

    @SerializedName("ListItems")
    List<T> listItems = null;

    @SerializedName("ErrorCode")
    int errorCode = 0;

    @SerializedName("Message")
    String message = null;

    /**
     * this is only for /api/token/ request
     */
    @SerializedName("access")
    String access = null;

}
