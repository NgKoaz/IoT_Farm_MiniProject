import random
import threading
import time
import serial.tools.list_ports
import os
from dotenv import load_dotenv
from model.mqtt.schedule import Schedule
from scheduler.scheduler1 import Scheduler1, Task, TaskArgument
from utils.time_manager import TimeManager


class Uart:
    class Device:
        TEMPERATURE_SENSOR = 0
        SOIL_MOISTURE_SENSOR = 1

    WAITING_ACK = 2
    MAX_NON_ACK = 18
    MILLILITER_TO_SECOND = 50   # Pump 50ml / 1s
    NUMBER_ACK_AT_SECTION1 = 8
    NUMBER_ACK_AT_SECTION2 = 3
    NUMBER_ACK_AT_SECTION3 = 5

    def __init__(self, scheduler1: Scheduler1):
        load_dotenv()
        self.isRandom = int(os.getenv("RANDOM_VALUE"))

        self.scheduler1 = scheduler1
        self._lock = threading.Lock()
        self.rawDataBuffer = []
        self.buffer = []
        self.tempValue = 0
        self.moisValue = 0

        self.totalAck = 0
        self.nonAck = 0

        self.mixer1_ON = [1, 6, 0, 0, 0, 255, 201, 138]
        self.mixer1_OFF = [1, 6, 0, 0, 0, 0, 137, 202]
        self.mixer2_ON = [2, 6, 0, 0, 0, 255, 201, 185]
        self.mixer2_OFF = [2, 6, 0, 0, 0, 0, 137, 249]
        self.mixer3_ON = [3, 6, 0, 0, 0, 255, 200, 104]
        self.mixer3_OFF = [3, 6, 0, 0, 0, 0, 136, 40]
        self.selector1_ON = [4, 6, 0, 0, 0, 255, 201, 223]
        self.selector1_OFF = [4, 6, 0, 0, 0, 0, 137, 159]
        self.selector2_ON = [5, 6, 0, 0, 0, 255, 200, 14]
        self.selector2_OFF = [5, 6, 0, 0, 0, 0, 136, 78]
        self.selector3_ON = [6, 6, 0, 0, 0, 255, 200, 61]
        self.selector3_OFF = [6, 6, 0, 0, 0, 0, 136, 125]
        self.pumpIn_ON = [7, 6, 0, 0, 0, 255, 201, 236]
        self.pumpIn_OFF = [7, 6, 0, 0, 0, 0, 137, 172]
        self.pumpOut_ON = [8, 6, 0, 0, 0, 255, 201, 19]
        self.pumpOut_OFF = [8, 6, 0, 0, 0, 0, 137, 83]
        self.soil_temperature = [1, 3, 0, 6, 0, 1, 100, 11]
        self.soil_moisture = [1, 3, 0, 7, 0, 1, 53, 203]

        self.port = self.getPort()
        # Args: isSuccessful
        self.onProcessDone = None
        self.onUartIsDown = None

        self.scheduler1.SCH_AddTask(Task(pTask=self.trackError, delay=0, period=3))

        try:
            self.ser = serial.Serial(port=self.port, baudrate=9600)
            print("Open successfully")
        except:
            print("Can not open the port")

        thread = threading.Thread(target=self.loop)
        thread.daemon = True
        thread.start()

    def getPort(self):
        ports = serial.tools.list_ports.comports()
        N = len(ports)
        commPort = "None"
        for i in range(0, N):
            port = ports[i]
            strPort = str(port)
            if "USB" in strPort:
                splitPort = strPort.split(" ")
                commPort = (splitPort[0])
        return commPort
        # return "/dev/ttyUSB1"
        # return "COM2"

    def setOnUartIsDown(self, onUartIsDown):
        self.onUartIsDown = onUartIsDown

    def setOnProcessDone(self, onProcessDone):
        self.onProcessDone = onProcessDone

    def trackError(self):
        if self.nonAck >= self.MAX_NON_ACK:
            if self.onUartIsDown:
                self.onUartIsDown()
            else:
                print("[ERROR] CAN'T GET ACK, THE COMMUNICATION LINE MAY BE TAKEN DOWN!")
                print("[ERROR] CAN'T GET ACK, THE COMMUNICATION LINE MAY BE TAKEN DOWN!")
                print("[ERROR] CAN'T GET ACK, THE COMMUNICATION LINE MAY BE TAKEN DOWN!")
                # Halt program
                time.sleep(10000)

    def setMixer1(self, args):
        state = args.payload["state"]
        taskTurnOffId = None
        if state:
            data = self.mixer1_ON
            # OFF task
            delayTurnOffTask = args.payload["delayTurnOffTask"]
            task = Task(pTask=self.setMixer1, args=TaskArgument(state=0), delay=delayTurnOffTask, period=0)
            taskTurnOffId = self.scheduler1.SCH_AddTask(task)
        else:
            data = self.mixer1_OFF
        self.ser.write(data)
        print("Mixer 1: ", data)
        task = Task(pTask=self.waitControlAck,
                    args=TaskArgument(addr=data[0], func=data[1], taskTurnOffId=taskTurnOffId, setFunction=self.setMixer1, argsSetFunction=args),
                    delay=self.WAITING_ACK,
                    period=0)
        self.scheduler1.SCH_AddTask(task)

    def setMixer2(self, args):
        state = args.payload["state"]
        taskTurnOffId = None
        if state:
            data = self.mixer2_ON
            # OFF task
            delayTurnOffTask = args.payload["delayTurnOffTask"]
            task = Task(pTask=self.setMixer2, args=TaskArgument(state=0), delay=delayTurnOffTask, period=0)
            taskTurnOffId = self.scheduler1.SCH_AddTask(task)
        else:
            data = self.mixer2_OFF

        self.ser.write(data)
        print("Mixer 2: ", data)
        task = Task(pTask=self.waitControlAck,
                    args=TaskArgument(addr=data[0], func=data[1], taskTurnOffId=taskTurnOffId, setFunction=self.setMixer2, argsSetFunction=args),
                    delay=self.WAITING_ACK,
                    period=0)
        self.scheduler1.SCH_AddTask(task)

    def setMixer3(self, args):
        state = args.payload["state"]
        taskTurnOffId = None
        if state:
            data = self.mixer3_ON
            # OFF task
            delayTurnOffTask = args.payload["delayTurnOffTask"]
            task = Task(pTask=self.setMixer3, args=TaskArgument(state=0), delay=delayTurnOffTask, period=0)
            taskTurnOffId = self.scheduler1.SCH_AddTask(task)
        else:
            data = self.mixer3_OFF

        self.ser.write(data)
        print("Mixer 3: ", data)

        # Ack Task
        task = Task(pTask=self.waitControlAck,
                    args=TaskArgument(addr=data[0], func=data[1], taskTurnOffId=taskTurnOffId, setFunction=self.setMixer3, argsSetFunction=args),
                    delay=self.WAITING_ACK,
                    period=0)
        self.scheduler1.SCH_AddTask(task)

    def setPumpIn(self, args):
        state = args.payload["state"]
        taskTurnOffId = None
        if state:
            data = self.pumpIn_ON
            # OFF task
            delayTurnOffTask = args.payload["delayTurnOffTask"]
            task = Task(pTask=self.setPumpIn, args=TaskArgument(state=0), delay=delayTurnOffTask, period=0)
            taskTurnOffId = self.scheduler1.SCH_AddTask(task)
        else:
            data = self.pumpIn_OFF

        self.ser.write(data)
        print("Pump In: ", data)

        task = Task(pTask=self.waitControlAck,
                    args=TaskArgument(addr=data[0], func=data[1], taskTurnOffId=taskTurnOffId, setFunction=self.setPumpIn, argsSetFunction=args),
                    delay=self.WAITING_ACK,
                    period=0)
        self.scheduler1.SCH_AddTask(task)

    def setPumpOut(self, args):
        state = args.payload["state"]
        taskTurnOffId = None
        if state:
            data = self.pumpOut_ON
            # OFF task
            delayTurnOffTask = args.payload["delayTurnOffTask"]
            task = Task(pTask=self.setPumpOut, args=TaskArgument(state=0), delay=delayTurnOffTask, period=0)
            taskTurnOffId = self.scheduler1.SCH_AddTask(task)
        else:
            data = self.pumpOut_OFF
        self.ser.write(data)
        print("Pump Out: ", data)

        task = Task(pTask=self.waitControlAck,
                    args=TaskArgument(addr=data[0], func=data[1], taskTurnOffId=taskTurnOffId,
                                      setFunction=self.setPumpOut, argsSetFunction=args),
                    delay=self.WAITING_ACK,
                    period=0)
        self.scheduler1.SCH_AddTask(task)

    def setSelector1(self, args):
        state = args.payload["state"]
        taskTurnOffId = None
        if state:
            data = self.selector1_ON
            # # OFF task
            # delayTurnOffTask = args.payload["delayTurnOffTask"]
            # task = Task(pTask=self.setSelector1, args=TaskArgument(state=0), delay=delayTurnOffTask, period=0)
            # taskTurnOffId = self.scheduler1.SCH_AddTask(task)
        else:
            data = self.selector1_OFF
        self.ser.write(data)
        print("Selector 1: ", data)

        task = Task(pTask=self.waitControlAck,
                    args=TaskArgument(addr=data[0], func=data[1], taskTurnOffId=taskTurnOffId,
                                      setFunction=self.setSelector1, argsSetFunction=args),
                    delay=self.WAITING_ACK,
                    period=0)
        self.scheduler1.SCH_AddTask(task)

    def setSelector2(self, args):
        state = args.payload["state"]
        taskTurnOffId = None
        if state:
            data = self.selector2_ON
            # # OFF task
            # delayTurnOffTask = args.payload["delayTurnOffTask"]
            # task = Task(pTask=self.setSelector2, args=TaskArgument(state=0), delay=delayTurnOffTask, period=0)
            # taskTurnOffId = self.scheduler1.SCH_AddTask(task)
        else:
            data = self.selector2_OFF
        self.ser.write(data)
        print("Selector 2: ", data)

        task = Task(pTask=self.waitControlAck,
                    args=TaskArgument(addr=data[0], func=data[1], taskTurnOffId=taskTurnOffId,
                                      setFunction=self.setSelector2, argsSetFunction=args),
                    delay=self.WAITING_ACK,
                    period=0)
        self.scheduler1.SCH_AddTask(task)

    def setSelector3(self, args):
        state = args.payload["state"]
        taskTurnOffId = None
        if state:
            data = self.selector3_ON
            # # OFF task
            # delayTurnOffTask = args.payload["delayTurnOffTask"]
            # task = Task(pTask=self.setSelector3, args=TaskArgument(state=0), delay=delayTurnOffTask, period=0)
            # taskTurnOffId = self.scheduler1.SCH_AddTask(task)
        else:
            data = self.selector3_OFF
        self.ser.write(data)
        print("Selector 3: ", data)

        task = Task(pTask=self.waitControlAck,
                    args=TaskArgument(addr=data[0], func=data[1], taskTurnOffId=taskTurnOffId,
                                      setFunction=self.setSelector3, argsSetFunction=args),
                    delay=self.WAITING_ACK,
                    period=0)
        self.scheduler1.SCH_AddTask(task)

    def initializeIrrigationProcess(self, schedule: Schedule):
        # Check schedule attribute first
        if len(schedule.ratio) < 7:
            print("[ERROR] Schedule has not enough value!")
            return False

        self.totalAck = 0
        self.nonAck = 0

        # Start to add task
        # Start: Pump in start
        volume = int(schedule.volume)
        water = int(schedule.ratio[0])
        mixer1 = int(schedule.ratio[1])
        mixer2 = int(schedule.ratio[2])
        mixer3 = int(schedule.ratio[3])
        totalRatioIn = water + mixer1 + mixer2 + mixer3
        area1 = int(schedule.ratio[4])
        area2 = int(schedule.ratio[5])
        area3 = int(schedule.ratio[6])
        totalRatioOut = area1 + area2 + area3

        max_duration_in_section_1 = int(volume / totalRatioIn * max(water, mixer1, mixer2, mixer3) / self.MILLILITER_TO_SECOND)

        delayTurnOffTask = int(volume / totalRatioIn * water / self.MILLILITER_TO_SECOND)
        if delayTurnOffTask > 0:
            task = Task(pTask=self.setPumpIn, args=TaskArgument(state=1, delayTurnOffTask=delayTurnOffTask), delay=0,
                        period=0)
            self.scheduler1.SCH_AddTask(task)
        else:
            self.totalAck += 2

        delayTurnOffTask = int(volume / totalRatioIn * mixer1 / self.MILLILITER_TO_SECOND)
        if delayTurnOffTask > 0:
            task = Task(pTask=self.setMixer1, args=TaskArgument(state=1, delayTurnOffTask=delayTurnOffTask), delay=0, period=0)
            self.scheduler1.SCH_AddTask(task)
        else:
            self.totalAck += 2

        delayTurnOffTask = int(volume / totalRatioIn * mixer2 / self.MILLILITER_TO_SECOND)
        if delayTurnOffTask > 0:
            task = Task(pTask=self.setMixer2, args=TaskArgument(state=1, delayTurnOffTask=delayTurnOffTask), delay=0, period=0)
            self.scheduler1.SCH_AddTask(task)
        else:
            self.totalAck += 2

        delayTurnOffTask = int(volume / totalRatioIn * mixer3 / self.MILLILITER_TO_SECOND)
        if delayTurnOffTask > 0:
            task = Task(pTask=self.setMixer3, args=TaskArgument(state=1, delayTurnOffTask=delayTurnOffTask), delay=0, period=0)
            self.scheduler1.SCH_AddTask(task)
        else:
            self.totalAck += 2

        delayTurnOffArea1 = int(volume / totalRatioOut * area1 / self.MILLILITER_TO_SECOND)
        delayTurnOffArea2 = int(volume / totalRatioOut * area2 / self.MILLILITER_TO_SECOND)
        delayTurnOffArea3 = int(volume / totalRatioOut * area3 / self.MILLILITER_TO_SECOND)
        delayPumpOut = max(delayTurnOffArea1, delayTurnOffArea2, delayTurnOffArea3)

        task = Task(pTask=self.trackAckSection1,
                    args=TaskArgument(delayTurnOffArea1=delayTurnOffArea1,
                                      delayTurnOffArea2=delayTurnOffArea2,
                                      delayTurnOffArea3=delayTurnOffArea3,
                                      delayPumpOut=delayPumpOut),
                    delay=max_duration_in_section_1 + self.WAITING_ACK * 6,
                    period=0)
        self.scheduler1.SCH_AddTask(task)
        return True

    def trackAckSection1(self, args):
        if self.totalAck == self.NUMBER_ACK_AT_SECTION1:
            self.totalAck = 0
            print("PREPARE SECTION2 TASKS!")

            delayTurnOffArea1 = args.payload["delayTurnOffArea1"]
            delayTurnOffArea2 = args.payload["delayTurnOffArea2"]
            delayTurnOffArea3 = args.payload["delayTurnOffArea3"]
            delayPumpOut = args.payload["delayPumpOut"]

            # task = Task(pTask=self.setPumpOut, args=TaskArgument(state=1, delayTurnOffTask=delayPumpOut + self.WAITING_ACK * 3), delay=0,
            #             period=0)
            # self.scheduler1.SCH_AddTask(task)
            if delayTurnOffArea1 > 0:
                task = Task(pTask=self.setSelector1, args=TaskArgument(state=1, delayTurnOffTask=delayTurnOffArea1),
                            delay=0,
                            period=0)
                self.scheduler1.SCH_AddTask(task)
            else:
                self.totalAck += 1

            if delayTurnOffArea2 > 0:
                task = Task(pTask=self.setSelector2, args=TaskArgument(state=1, delayTurnOffTask=delayTurnOffArea2), delay=0,
                            period=0)
                self.scheduler1.SCH_AddTask(task)
            else:
                self.totalAck += 1

            if delayTurnOffArea3 > 0:
                task = Task(pTask=self.setSelector3, args=TaskArgument(state=1, delayTurnOffTask=delayTurnOffArea3), delay=0,
                            period=0)
                self.scheduler1.SCH_AddTask(task)
            else:
                self.totalAck += 1

            task = Task(pTask=self.trackingAckSection2,
                        args=TaskArgument(
                            delayPumpOut=delayPumpOut,
                            delayTurnOffArea1=delayTurnOffArea1,
                            delayTurnOffArea2=delayTurnOffArea2,
                            delayTurnOffArea3=delayTurnOffArea3),
                        delay=delayPumpOut + self.WAITING_ACK * 4,
                        period=0)
            self.scheduler1.SCH_AddTask(task)

        else:
            print("NON-OK AT SECTION 1, TERMINATE!")
            if self.onProcessDone:
                self.onProcessDone(False)

    def trackingAckSection2(self, args):
        if self.totalAck == self.NUMBER_ACK_AT_SECTION2:
            print("PREPARE SECTION 3 TASKS!")
            self.totalAck = 0

            delayPumpOut = args.payload["delayPumpOut"]
            delayTurnOffArea1 = args.payload["delayTurnOffArea1"]
            delayTurnOffArea2 = args.payload["delayTurnOffArea2"]
            delayTurnOffArea3 = args.payload["delayTurnOffArea3"]

            task = Task(pTask=self.setPumpOut,
                        args=TaskArgument(state=1, delayTurnOffTask=delayPumpOut),
                        delay=0,
                        period=0)
            self.scheduler1.SCH_AddTask(task)

            # Delay for turn off
            task = Task(pTask=self.setSelector1,
                        args=TaskArgument(state=0),
                        delay=delayTurnOffArea1,
                        period=0)
            self.scheduler1.SCH_AddTask(task)
            task = Task(pTask=self.setSelector2,
                        args=TaskArgument(state=0),
                        delay=delayTurnOffArea2,
                        period=0)
            self.scheduler1.SCH_AddTask(task)
            task = Task(pTask=self.setSelector3,
                        args=TaskArgument(state=0),
                        delay=delayTurnOffArea3,
                        period=0)
            self.scheduler1.SCH_AddTask(task)

            task = Task(pTask=self.trackingAckSection3,
                        delay=max(delayPumpOut, delayTurnOffArea1, delayTurnOffArea2, delayTurnOffArea3) + self.WAITING_ACK * 3,
                        period=0)
            self.scheduler1.SCH_AddTask(task)
        else:
            print("NON-OK AT SECTION 2, TERMINATE!")
            if self.onProcessDone:
                self.onProcessDone(False)

    def trackingAckSection3(self):
        if self.totalAck == self.NUMBER_ACK_AT_SECTION3:
            print("OK AT SECTION 3!")
            self.totalAck = 0
            if self.onProcessDone:
                self.onProcessDone(True)
        else:
            print("NON-OK AT SECTION 3, TERMINATE!")
            if self.onProcessDone:
                self.onProcessDone(False)

    def waitControlAck(self, args):
        res = self.waitAck(args)
        if res != -1:
            self.totalAck += 1
        else:
            self.nonAck += 1
            print("NON-OK, PREPARE FOR RESEND!")
            # Delete turn off task
            taskTurnOffId = args.payload["taskTurnOffId"]
            self.scheduler1.SCH_DeleteTask(taskTurnOffId)
            # Resend turn on task
            setFunction = args.payload["setFunction"]
            argsSetFunction = args.payload["argsSetFunction"]
            setFunction(argsSetFunction)
        return res != -1

    def readTemperature(self):
        self.ser.write(self.soil_temperature)
        task = Task(pTask=self.tempAck,
                    args=TaskArgument(addr=self.soil_temperature[0], func=self.soil_temperature[1]),
                    delay=self.WAITING_ACK,
                    period=0)
        self.scheduler1.SCH_AddTask(task)

    def readMoisture(self):
        self.ser.write(self.soil_moisture)
        task = Task(pTask=self.moisAck,
                    args=TaskArgument(addr=self.soil_moisture[0], func=self.soil_moisture[1]),
                    delay=self.WAITING_ACK,
                    period=0)
        self.scheduler1.SCH_AddTask(task)

    def tempAck(self, args: TaskArgument):
        res = self.waitAck(args)
        if res != -1:
            self.tempValue = res / 100

    def moisAck(self, args: TaskArgument):
        res = self.waitAck(args)
        if res != -1:
            self.moisValue = res / 100

    def waitAck(self, args: TaskArgument):
        addr = args.payload["addr"]
        func = args.payload["func"]
        result = -1
        with self._lock:
            for entry in self.buffer:
                if entry[0] == addr and entry[1] == func:
                    result = self.decodeModbus(entry)
                    if self.isRandom == 1 and addr == 1 and func == 3:
                        result = random.randint(2200, 3100)
            # Check and delete redundant ACK
            self.buffer = [entry for entry in self.buffer if not (entry[0] == addr and entry[1] == func)]
        return result

    def loop(self):
        while True:
            with self._lock:
                if self.rawDataBuffer:
                    if self.rawDataBuffer[0] == 0:
                        self.rawDataBuffer.pop(0)
                    elif len(self.rawDataBuffer) >= 7 and self.rawDataBuffer[0] == 1 and self.rawDataBuffer[1] == 3:
                        self.buffer.append(self.rawDataBuffer[:7])
                        self.rawDataBuffer = self.rawDataBuffer[7:]
                        print("BUFFER: ", self.buffer)
                        print("RAW DATA BUFFER 1: ", self.rawDataBuffer)
                    elif len(self.rawDataBuffer) >= 8 and self.rawDataBuffer[0] < 9:
                        self.buffer.append(self.rawDataBuffer[:8])
                        self.rawDataBuffer = self.rawDataBuffer[8:]
                        print("BUFFER: ", self.buffer)
                        print("RAW DATA BUFFER 2: ", self.rawDataBuffer)
                    else:
                        self.rawDataBuffer.clear()

            numBytes = self.ser.inWaiting()
            if numBytes:
                self.rawDataBuffer.extend(self.ser.read(numBytes))
                print("RAW DATA BUFFER: ", self.rawDataBuffer)
            else:
                if not self.rawDataBuffer:
                    TimeManager.sleep(0.2)

    @staticmethod
    def decodeModbus(data_array):
        if len(data_array) >= 7:
            array_size = len(data_array)
            payload = data_array[array_size - 4] * 256 + data_array[array_size - 3]
            return payload
        else:
            return -1





