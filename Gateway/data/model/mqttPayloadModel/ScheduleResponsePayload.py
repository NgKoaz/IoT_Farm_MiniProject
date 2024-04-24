import json


class ScheduleResponsePayload:
    def __init__(
            self,
            email: str = None,
            type: str = None,
            name: str = None,
            water: float = None,
            mixer1: float = None,
            mixer2: float = None,
            mixer3: float = None,
            area1: float = None,
            area2: float = None,
            area3: float = None,
            isDate: bytes = None,
            date: str = None,
            weekday: str = None,
            time: str = None,
    ):
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

    def importStringInJsonForm(self, jsonString):
        js = json.dumps(jsonString)
        print(js)



