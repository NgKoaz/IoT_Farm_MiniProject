import datetime

from model.mqtt.schedule import Schedule
from scheduler123.task import Task


class PrivateTask(Task):
    def __init__(self, schedule: Schedule):
        super().__init__()
        self.schedule = schedule

        self.isFirstRenewDelay = True
        # Reset
        self._renewDelay()

        self.pTask = self.taskk

    def taskk(self):
        print(datetime.datetime.now())
        print("TASK DO")

    # Base on date or weekday, we can calc the delay time of task.
    def _renewDelay(self):
        try:
            curTime = datetime.datetime.now()
            if self.schedule.isDate == 1:
                # With schedule has specific date, we just run one time.
                # First reset for set delay.
                # In second run,... we set -1 to make sure this task will be deleted.
                if self.isFirstRenewDelay:
                    self.delay = -1

                # Find delay the task will be executed.
                dateParts = self.schedule.date.split("/")
                timeParts = self.schedule.time.split(":")
                doTime = datetime.datetime(int(dateParts[2]), int(dateParts[1]), int(dateParts[0]),
                                           int(timeParts[0]), int(timeParts[1]))
                time_diff = doTime - curTime
                self.delay = time_diff.total_seconds()
                print("Time difference:", time_diff.total_seconds().__round__(), "seconds")
            else:
                pass
        except Exception as e:
            print(e)
