import json


class ScheduleType:
    ADD = "add"
    DELETE = "delete"
    UPDATE = "update"


class Schedule:
    def __init__(
            self,
            scheduleId: str = "",
            email: str = "",
            type: str = "",
            name: str = "",
            water: float = None,
            mixer1: float = None,
            mixer2: float = None,
            mixer3: float = None,
            area1: float = None,
            area2: float = None,
            area3: float = None,
            isDate: int = None,
            date: str = "",
            weekday: str = "",
            time: str = "",
            isError: int = None,
            error: str = ""
    ):
        self.scheduleId = scheduleId
        self.email = email
        self.type = type
        self.name = name
        self.water = water
        self.mixer1 = mixer1
        self.mixer2 = mixer2
        self.mixer3 = mixer3
        self.area1 = area1
        self.area2 = area2
        self.area3 = area3
        self.isDate = isDate
        self.date = date
        self.weekday = weekday
        self.time = time
        self.isError = isError
        self.error = error

    def setId(self, scheduleId: str):
        self.scheduleId = scheduleId

    @classmethod
    def importFromJsonString(cls, json_string):
        json_dict = json.loads(json_string)
        return cls(**json_dict)

    def toJsonString(self):
        # Exclude bytes attributes from serialization
        data = {key: value for key, value in self.__dict__.items() if not isinstance(value, bytes)}
        return json.dumps(data)
