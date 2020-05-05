package co.atrasvida.example.ApiClient

import com.google.gson.annotations.SerializedName


class BaseResponse<T> {

    @SerializedName("IsSuccess")
    var isSuccess: Boolean = false

    @SerializedName("Item")
    var item: T? = null

    @SerializedName("ListItems")
    var listItems: List<T>? = null

    @SerializedName("ErrorCode")
    var errorCode: Int? = null

    @SerializedName("Message")
    var message: String? = null

    /**
     * this is only for /api/token/ request
     */
    @SerializedName("access")
    var access: String? = null

}
