
import com.google.gson.annotations.SerializedName

data class Box(
    @SerializedName("large")
    val large: String,
    @SerializedName("medium")
    val medium: String,
    @SerializedName("small")
    val small: String,
    @SerializedName("template")
    val template: String
)