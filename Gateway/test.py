import time
import datetime

from model.mqtt.schedule import Schedule
from scheduler.scheduler123 import Scheduler, PrivateTask


class Test:
    scheduler = Scheduler()

    def __init__(self):
        # schedule = Schedule(scheduleId="1", isDate=1, date="2/5/2024", time="21:44", isError=0)
        # privateTask = PrivateTask(schedule)
        # self.scheduler.SCH_AddTask(privateTask, 1)
        #
        # schedule = Schedule(scheduleId="2", isDate=1, date="2/5/2024", time="21:45", isError=0)
        # privateTask = PrivateTask(schedule)
        # self.scheduler.SCH_AddTask(privateTask, 1)
        #
        # schedule = Schedule(scheduleId="3", isDate=1, date="2/5/2024", time="21:46", isError=0)
        # privateTask = PrivateTask(schedule)
        # self.scheduler.SCH_AddTask(privateTask, 1)
        #
        # schedule = Schedule(scheduleId="4", isDate=1, date="2/5/2024", time="21:47", isError=0)
        # privateTask = PrivateTask(schedule)
        # self.scheduler.SCH_AddTask(privateTask, 1)
        #
        # schedule = Schedule(scheduleId="5", isDate=1, date="2/5/2024", time="21:42", isError=0)
        # privateTask = PrivateTask(schedule)
        # self.scheduler.SCH_AddTask(privateTask, 1)
        #
        # schedule = Schedule(scheduleId="6", isDate=1, date="2/5/2024", time="21:43", isError=0)
        # privateTask = PrivateTask(schedule)
        # self.scheduler.SCH_AddTask(privateTask, 1)

        # schedule = Schedule(scheduleId="3", isDate=1, date="2/5/2024", time="17:36", isError=0)
        # privateTask = PrivateTask(schedule)
        # self.scheduler.SCH_AddTask(privateTask, 1)
        #
        # schedule = Schedule(scheduleId="3", isDate=1, date="2/5/2024", time="17:40", isError=0)
        # privateTask = PrivateTask(schedule)
        # self.scheduler.SCH_AddTask(privateTask, 1)

        while True:
            curTime = datetime.datetime.now()
            curTime += datetime.timedelta(minutes=1)
            schedule = Schedule(scheduleId="6", isDate=1, date="4/5/2024", time=f"{curTime.hour}:{curTime.minute}")
            privateTask = PrivateTask(schedule)
            self.scheduler.SCH_AddTask(privateTask, 1)

            time.sleep(25)

    def deo(self):
        pass


Test()
