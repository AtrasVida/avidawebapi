package co.atrasvida.example.apiClient;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class BaseResponse<T> {

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

}
