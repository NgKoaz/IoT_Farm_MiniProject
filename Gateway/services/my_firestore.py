import firebase_admin
from firebase_admin import firestore, credentials


class MyFirestore:
    def __init__(self, brokerName, brokerUsername, brokerPassword):
        cred = credentials.Certificate("./firebase-credentials.json")
        firebase_admin.initialize_app(cred)

        self.brokerDocumentId = brokerName + "-" + brokerUsername + "-" + brokerPassword
        self.rootDoc = firestore.client().collection("brokers").document(self.brokerDocumentId)

    def putSchedule(self, docId, js):
        self.rootDoc.collection("schedule").document(docId).set(js)

    def deleteSchedule(self, docId):
        self.rootDoc.collection("schedule").document(docId).delete()

    def updateSchedule(self, docId, js):
        self.rootDoc.collection("schedule").document(docId).update(js)

    def putHistorySchedule(self, docId, js):
        self.rootDoc.collection("historySchedule").document(docId).set(js)


