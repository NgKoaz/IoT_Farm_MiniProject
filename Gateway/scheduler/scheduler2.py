import threading
from model.mqtt.schedule import Schedule
from utils.time_manager import TimeManager
import datetime


class ScheduleTask:
    def __init__(
            self,
            schedule: Schedule
    ):
        self.schedule = schedule

    def getScheduleId(self):
        return self.schedule.scheduleId

    # Call after run the task to check if it will run one more time or not.
    def willRunAgain(self):
        if self.schedule.date:
            return False
        if self.schedule.weekday:
            return True

    def isInTime(self):
        try:
            hour, minute = self.schedule.time.split(":")
            if self.schedule.date:
                day, month, year = self.schedule.date.split("/")
                cur_time = datetime.datetime.now()
                do_time = datetime.datetime(int(year), int(month), int(day), int(hour), int(minute))
                time_difference = do_time - cur_time
                delay = round(time_difference.total_seconds())
                if delay <= 0:
                    return True
                else:
                    return False
            elif self.schedule.weekday:
                cur_time = datetime.datetime.now()
                do_time = datetime.datetime()
                return True
            else:
                return False
        except Exception as e:
            print(e)
            return False

    def doTask(self):
        TimeManager.millisSleep(5)
        print(self.schedule.scheduleId, self.schedule.time, self.schedule.date)


# 1 Thread: LoopInfinity
class Scheduler2:
    IDLE_SLEEP = 0.2
    PRIORITY_WAIT = 0.01

    def __init__(self):
        # List of tasks.
        self._scheduleList = []

        # Lock
        self._lock = threading.Lock()

        # For thread delay
        self._time = TimeManager()

        # Number for priority.
        # This is the number show the scheduler function need to be done.
        # Assume we have SCH_Dispatch always acquire lock of SCH_Update, make starvation.
        # We prior SCH_AddTask, SCH_DeleteTask than SCH_Dispatch.
        self._priorityFunc = 0

        self.runningScheduleId = ""

        self.on_task_done = None

        # Start loop
        thread = threading.Thread(target=self._LoopInfinity)
        thread.daemon = True
        thread.start()

    def setOnTaskDone(self, on_task_done):
        self.on_task_done = on_task_done

    def SCH_AddTask(self, scheduleTask: ScheduleTask):
        self._priorityFunc += 1
        # Critical section !!!
        with self._lock:
            self._scheduleList.append(scheduleTask)
        # End critical section !!!
        self._priorityFunc -= 1

    def SCH_DeleteTask(self, scheduleTask: ScheduleTask):
        self._priorityFunc += 1
        # Critical section !!!
        with self._lock:
            self._scheduleList.remove(scheduleTask)
        # End critical section !!!
        self._priorityFunc -= 1

    def SCH_DeleteScheduleTask(self, scheduleId: str):
        self._priorityFunc += 1
        # Critical section !!!
        with self._lock:
            for task in self._scheduleList:
                if task.getScheduleId() == scheduleId:
                    self._scheduleList.remove(task)
                    break

        # End critical section !!!
        self._priorityFunc -= 1

    def SCH_Dispatch(self):
        # Critical section !!!
        haveToDoTask = None
        with self._lock:
            if len(self._scheduleList) > 0:
                for scheduleTask in self._scheduleList:
                    if scheduleTask.isInTime():
                        haveToDoTask = scheduleTask
                        break

        # End critical section !!!
        if haveToDoTask:
            haveToDoTask.doTask()
            self.SCH_DeleteTask(haveToDoTask)
            if haveToDoTask.willRunAgain():
                self.SCH_AddTask(haveToDoTask)
            else:
                if self.on_task_done:
                    self.on_task_done(haveToDoTask)

    def _LoopInfinity(self):
        while True:
            self.SCH_Dispatch()

            if self._priorityFunc > 0:
                # Sleep about = 16ms
                TimeManager.millisSleep(self.PRIORITY_WAIT)

            if len(self._scheduleList) <= 0:
                TimeManager.millisSleep(self.IDLE_SLEEP)

