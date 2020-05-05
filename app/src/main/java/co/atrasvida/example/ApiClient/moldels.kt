package co.atrasvida.example.ApiClient

internal data class ReportClickRequest(
    var app_key: String,
    var ads_id: Int,
    var extras: ReportExtrasRequest
)

internal data class ReportExtrasRequest(
    var device_id: String?,
    var duration: Int,
    var location: String,
    var internet_provider: String,
    var os: String,
    var android_api: String,
    var device: String,
    var model: String,
    var product: String,
    var manufacturer: String,
    var lat: String,
    var long: String
)

internal data class RequestAdModel(
    var id: Int,
    var url: String,
    var file_md: String,
    var file_hd: String,
    var file_full_hd: String,
    var ads_type: Int/*,
    var reward_time: Int*/
)

internal data class AppKysResponse(
    var app_key: String,
    var admob_key: String,
    var admob_banner: String,
    var admob_interstitial: String,
    var admob_rewarded: String
)