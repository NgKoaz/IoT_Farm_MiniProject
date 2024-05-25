from typing import List

import schedule

class SchedulerReminder:
    def __init__(self):
        while True:
            schedule.run_pending()

    def addScheduleWithDate(self):
        return schedule.every().at()

    def addScheduleWithWeekdays(self, weekdays: List[int]):
        pass