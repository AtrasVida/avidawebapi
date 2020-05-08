package co.atrasvida.example.ApiClient



data class UpdateDetailsRequest(var fcmId: String, var phoneBrand: String, var phoneModel: String)

data class CalculateRequest(

    var car_model: Int,
    var cover: Long,
    var year_discount: Int,
    var driver_discount: Int,
    var receive_damage: Boolean,
    var life_damage: Int,
    var financial_damage: Int,
    var driver_damage: Int,
    var diff_days: Int,
    var delay_days: Int,
    var year_construction: Int
)

data class CalculateBodyRequest(
    var insurance: Int,
    var car_price: Long,
    var chemicals: Int,
    var glass_breakdown: Int,
    var transportation: Int,
    var natural_disasters: Int,
    var ghaede_nesbi_sarmaye: Int,
    var transit: Int,
    var price_fluctuations: Int,
    var remove_franchise: Int,
    var stealing_1: Int,
    var stealing_2: Int,
    var history_major_hazards: Int,
    var history_minor_hazards: Int
)

data class ContractModel(

    var contract_status: String,
    var contract_step: Int,
    var insurance_type: Int,
    var insurance_company: Int,
    var car_model: Int,
    var cover: Long,
    var year_discount: Int,
    var driver_discount: Int,
    var delay_days: Int,
    var year_construction: Int,
    var estimate_price: Int,
    var beginning_date_previous_insurance: String,
    var expire_date_previous_insurance: String,
    var receive_damages: Boolean,
    var life_damage: Int,
    var financial_damage: Int,
    var driver_damage: Int
)

data class ContractCarBodyModel(

    var contract_status: String,
    var contract_step: Int,
    var insurance_type: Int,
    var insurance_company: Int,
    var car_model: Int,
    var year_construction: Int,
    var estimate_price: Int,
    var beginning_date_previous_insurance: String,
    var expire_date_previous_insurance: String,
    var receive_damages: Boolean,
    var car_price: Int,
    var chemicals: Int,
    var glass_breakdown: Int,
    var transportation: Int,
    var natural_disasters: Int,
    var ghaede_nesbi_sarmaye: Int,
    var transit: Int,
    var price_fluctuations: Int,
    var remove_franchise: Int,
    var stealing_1: Int,
    var stealing_2: Int,
    var history_major_hazards: Int,
    var history_minor_hazards: Int

)

data class FinalContractModel(
    var contract_id: Int,
    var contract_status: String,
    var contract_step: Int,
    var first_name: String,
    var last_name: String,
    var phone_number: String,
    var tell_number: String,
    var gender: Int,
    var birth_date_shamsi: String,
    var address_type: Int,
    var postal_address: String,
    var postal_code: String,
    var state: String,
    var city: String,
    var description: String
)


data class ContractResponceModel(

    var id: Int,
    var documents: Int,
    var receiver: Int,
    var insurance_type: Int,
    var object_id: Int,
    var status: String,
    var step: Int,
    var description: String,
    var estimate_price: Double,
    var override_price: Double,
    var issue_date: String,
    var created_at: String,
    var updated_at: String,
    var is_deleted: Boolean,
    var user: Int,
    var content_type: Int
)

data class CalculateResponce(
    var final_price: Double,
    var insurance: InsuranceModel
)

data class InsuranceModel(
    var id: Int,
    var name: String,
    var icon: String,
    var support_level: Long,
    var response_time: Long,
    var branches_count: Int,
    var has_discount: Boolean
)

data class CompanyModel(
    var id: Int,
    var name: String,
    var icon: String,
    var support_level: Long,
    var response_time: Long,
    var branches_count: Long,
    var has_discount: Boolean
)


data class CarBrandsModel(
    var id: Int,
    var persian_car_brand: String,
    var english_car_brand: String
)

data class BrandModelsModel(
    var id: Int,
    var car_model_name: String,
    var car_model_brand: String,
    var car_model_type: Int
)

data class DiscountModel(
    var id: Int,
    var tittle: String,
    var discount_percentage: String
)


data class LoginRequest(
    var username: String,
    var password: String
)

data class VerificationCodeRequest(
    var phone_number: String,
    var national_code: String,
    var dev: Boolean
)

data class UploadImageResponce(
    var image_url: String
)

data class UploadImageModel(
    var contract_status: String,
    var contract_step: Int,
    var contract_id: Int,
    var image_type: Int
)


data class EditedUser(
    var father_name: String?,
    var email: String,
    //var address: String,
    var birth_date_shamsi: String,
    var structure_kind: String
)


data class InsuranceData(var insurance_company: InsuranceModel)

data class UserContractsModel(
    var id: Int,
    var insurance_type: Int,
    var object_id: Int,
    var status: String,
    var step: Int,
    var description: String,
    var estimate_price: Long,
    var override_price: Long,
    var issue_date: String,
    var created_at: String,
    var updated_at: String,
    var is_deleted: Boolean,
    var user: Int,
    var receiver: Int,
    var documents: Int,
    var content_type: Int,
    var insurance_data: InsuranceData

)


data class DocumentModel(
    var id: Int,
    var on_car_card_image: String,
    var back_car_card_image: String,
    var insurance_card_image: String,
    var created_at: String,
    var updated_at: String,
    var user: Int
)

data class ReceiverModel(
    var id: Int,
    var first_name: String,
    var last_name: String,
    var phone_number: String,
    var tell_number: String,
    var birth_date_shamsi: String,
    var gender: Int,
    var address_type: Int,
    var postal_address: String,
    var postal_code: String,
    var state: String,
    var city: String,
    var description: String,
    var created_at: String,
    var updated_at: String,
    var user: Int
)

data class DetailDiscountModel(
    var id: Int,
    var tittle: String,
    var discount_percentage: Long,
    var is_deleted: Boolean
)

data class InsuranceCompanyModel(
    var id: Int,
    var name: String,
    var icon: String,
    var support_level: Long,
    var response_time: Long,
    var branches_count: Long,
    var has_discount: Boolean,
    var is_deleted: Boolean
)

data class CarModelBrand(
    var id: Int,
    var persian_car_brand: String,
    var english_car_brand: String,
    var is_deleted: Boolean
)

data class CarModelType(
    var id: Int,
    var tittle: String,
    var is_deleted: Boolean
)

data class CarModel(
    var id: Int,
    var car_model_brand: CarModelBrand,
    var car_model_type: CarModelType,
    var car_model_name: String,
    var is_deleted: Boolean
)


data class InsuranceDataModel(

    var id: Int,
    var year_discount: DetailDiscountModel,
    var driver_discount: DetailDiscountModel,
    var insurance_company: InsuranceCompanyModel,
    var car_model: CarModel,
    var cover: Int,
    var delay_days: Int,
    var year_construction: String,
    var beginning_date_previous_insurance: String,
    var expire_date_previous_insurance: String,
    var receive_damages: Boolean,
    var user: Int
)

data class OrderDetailModel(
    var id: Int,
    var documents: DocumentModel,
    var receiver: ReceiverModel,
    var insurance_type: Int,
    var object_id: Int,
    var status: String,
    var step: Int,
    var description: String,
    var estimate_price: Long,
    var override_price: Long,
    var issue_date: String,
    var created_at: String,
    var updated_at: String,
    var is_deleted: Boolean,
    var user: Int,
    var content_type: Int,
    var insurance_data: InsuranceDataModel
)