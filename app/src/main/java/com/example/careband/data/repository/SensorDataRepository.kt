import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

object SensorDataRepository {
    fun uploadSensorData(userId: String, data: SensorData) {
        Firebase.firestore.collection("Users")
            .document(userId)
            .collection("SensorData")
            .add(data)
    }
}
