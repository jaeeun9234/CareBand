import com.google.firebase.Timestamp


data class SensorData(
    val heartRate: Int? = null,
    val temperature: Float? = null,
    val fallDetected: Boolean? = null,
    val timestamp: Timestamp = Timestamp.now()
)
