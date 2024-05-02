class TaskArgument:
    def __init__(self, args=None):
        self.args = args


class Task:
    def __init__(
            self,
            pTask=None,
            args: TaskArgument = None,
            delay: int = 0,
            period: int = 0
    ):
        self.pTask = pTask
        self.args = args
        self.delay = delay
        self.period = period
        self.taskId = 0

    def setTaskId(self, newId):
        self.taskId = newId

    # Renew delay then check if delay is negative, return false.
    # Otherwise true.
    def canContinue(self):
        self._renewDelay()
        return self.delay >= 0

    def _renewDelay(self):
        self.delay = self.period

    def doTask(self):
        if self.pTask:
            if self.args:
                self.pTask(self.args)
            else:
                self.pTask()
        else:
            print("[ERROR] No task to run!")

