import time


class TimeManager:
    def __init__(self):
        self.startPoint = None

    # it must be >= 0.01. Best is >= 0.1
    @staticmethod
    def millisSleep(duration):
        start = time.time()
        while time.time() - start < duration:
            time.sleep(0.007)

    def start(self):
        self.startPoint = time.time()

    def moveStartPoint(self, amount):
        self.startPoint += amount

    def waitUntil(self, duration):
        while time.time() - self.startPoint < duration:
            time.sleep(0.007)
