import threading
import time
from scheduler123.private_task import PrivateTask
from scheduler123.task import Task, TaskArgument
from utils.time_manager import TimeManager


# 2 Threads: LoopInfinity and Timer.
# SCH_Update and SCH_Dispatch run concurrently by using lock.
# We try to make SCH_Update, SCH_AddTask, SCH_DeleteTask has higher priority to run than SCH_Dispatch.
class Scheduler:
    TIMER_CYCLE = 1
    IDLE_SLEEP = 0.02
    PRIORITY_WAIT = 0.01

    def __init__(self):
        # List of tasks.
        self._taskList0 = []    # Highest priority. For communicate with hardware
        self._taskList1 = []    # Lowest priority. List<PrivateTask>

        # Lock
        self._lock = threading.Lock()

        # For delay
        self.time = TimeManager()

        # Number for priority.
        # This is the number show the scheduler function need to be done.
        # Assume we have SCH_Dispatch always acquire lock of SCH_Update, make starvation.
        # We prior SCH_Update, SCH_AddTask, SCH_DeleteTask than SCH_Dispatch.
        self.priorityFunc = 0

        # For SCH_Update()
        self.runUpdate = 0

        # Notify no task, loopInfinity thread can be sleep
        self.isNoTask = 0

        # Start timer
        thread = threading.Thread(target=self.TIMER)
        thread.daemon = True
        thread.start()

        # Start loop
        thread = threading.Thread(target=self.LoopInfinity)
        thread.daemon = True
        thread.start()

    def SCH_AddTask(self, task: Task, priority: int):
        self.priorityFunc += 1
        # Critical section !!!
        with self._lock:
            if priority == 0:
                self.addTaskSortedByDelay(self._taskList0, task)
            elif priority == 1:
                self.addTaskSortedByDelay(self._taskList1, task)
            else:
                print("[ERROR] Wrong priority number!")
        # End critical section !!!
        self.priorityFunc -= 1

    @staticmethod
    def addTaskSortedByDelay(taskList, newTask: Task):
        idx = 0
        for task in taskList:
            delay_diff = newTask.delay - task.delay
            if delay_diff > 0:
                newTask.delay = delay_diff
            else:
                task.delay = -delay_diff
                taskList.insert(idx, newTask)
                return
            idx += 1
        taskList.append(newTask)

    def SCH_DeleteTask(self, task: Task):
        self.priorityFunc += 1
        # Critical section !!!
        with self._lock:
            self.deleteTaskWithPointer(task)
        # End critical section !!!
        self.priorityFunc -= 1

    def SCH_DeletePrivateTask(self, scheduleId: str):
        # NO RETURN FOR THIS FUNCTION
        self.priorityFunc += 1
        # Critical section !!!
        with self._lock:
            for task in self._taskList1:
                if task.taskId == scheduleId:
                    self._taskList1.remove(task)
                    break
        # End critical section !!!
        self.priorityFunc -= 1

    def deleteTaskWithPointer(self, task):
        try:
            self._taskList0.remove(task)
            return True
        except ValueError as e:
            pass
        try:
            self._taskList1.remove(task)
            return True
        except ValueError as e:
            return False

    def SCH_Dispatch(self):
        if self.priorityFunc > 0:
            # Sleep about = 16ms
            TimeManager.millisSleep(self.PRIORITY_WAIT)
            return

        self.isNoTask = False
        # Critical section !!!
        with self._lock:
            if len(self._taskList0) > 0:
                self.dispatchInSpecificList(self._taskList0)
            elif len(self._taskList1) > 0:
                self.dispatchInSpecificList(self._taskList1)
            else:
                self.isNoTask = True
        # End critical section !!!

    @staticmethod
    def dispatchInSpecificList(taskList):
        task = taskList[0]
        if task and task.delay <= 0:
            # Pop (remove) task out of queue
            taskList.pop(0)
            # Execute
            task.doTask()
            # Check if the task has period.
            # If true will reset new delay then add task again.
            if task.canContinue():
                taskList.append(task)

    def SCH_Update(self):
        self.priorityFunc += 1
        # Critical section !!!
        with self._lock:
            if len(self._taskList0) > 0:
                if self._taskList0[0].delay > 0:
                    self._taskList0[0].delay -= 1
            if len(self._taskList1) > 0:
                if self._taskList1[0].delay > 0:
                    self._taskList1[0].delay -= 1
        # End critical section !!!
        self.priorityFunc -= 1

    def TIMER(self):
        self.time.start()
        while True:
            self.SCH_Update()
            self.time.waitUntil(self.TIMER_CYCLE)
            self.time.moveStartPoint(self.TIMER_CYCLE)

    def LoopInfinity(self):
        while True:
            self.SCH_Dispatch()
