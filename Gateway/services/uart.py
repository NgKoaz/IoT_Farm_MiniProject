import threading
import time
import serial.tools.list_ports

from scheduler.scheduler1 import Scheduler1, Task, TaskArgument
from utils.time_manager import TimeManager


class Uart:
    WAITING_ACK = 0.5

    def __init__(self, scheduler1: Scheduler1):
        self.scheduler1 = scheduler1
        self._lock = threading.Lock()
        self.buffer = []
        self.tempValue = 0
        self.moisValue = 0

        self.mixer1_ON = [1, 6, 0, 0, 0, 255, 201, 138]
        self.mixer1_OFF = [1, 6, 0, 0, 0, 0, 137, 202]

        self.mixer2_ON = [2, 6, 0, 1, 0, 255, 201, 185]
        self.mixer2_OFF = [2, 6, 0, 1, 0, 0, 137, 249]

        self.mixer3_ON = [3, 6, 0, 2, 0, 255, 200, 104]
        self.mixer3_OFF = [3, 6, 0, 2, 0, 0, 136, 40]

        self.selector1_ON = [4, 6, 0, 3, 0, 255, 201, 223]
        self.selector1_OFF = [4, 6, 0, 3, 0, 0, 137, 159]

        self.selector2_ON = [5, 6, 0, 4, 0, 255, 200, 14]
        self.selector2_OFF = [5, 6, 0, 4, 0, 0, 136, 78]

        self.selector3_ON = [6, 6, 0, 5, 0, 255, 200, 61]
        self.selector3_OFF = [6, 6, 0, 5, 0, 0, 136, 125]

        self.pumpIn_ON = [7, 6, 0, 6, 0, 255, 201, 236]
        self.pumpIn_OFF = [7, 6, 0, 6, 0, 0, 137, 172]

        self.pumpOut_ON = [8, 6, 0, 7, 0, 255, 201, 19]
        self.pumpOut_OFF = [8, 6, 0, 7, 0, 0, 137, 83]

        self.soil_temperature = [1, 3, 0, 6, 0, 1, 100, 11]
        self.soil_moisture = [1, 3, 0, 7, 0, 1, 53, 203]

        self.port = self.getPort()
        try:
            self.ser = serial.Serial(port=self.port, baudrate=115200)
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
        # return commPort
        # return "/dev/ttyUSB1"
        return "COM2"

    def setMixer1(self, args):
        state = args.payload["state"]
        if state:
            self.ser.write(self.mixer1_ON)
        else:
            self.ser.write(self.mixer1_OFF)
        time.sleep(5)
        # print(self.decodeModbus(self.ser))

    def setMixer2(self, state):
        if state:
            self.ser.write(self.mixer2_ON)
        else:
            self.ser.write(self.mixer2_OFF)
        time.sleep(1)
        # print(self.decodeModbus(self.ser))

    def setMixer3(self, state):
        if state:
            self.ser.write(self.mixer3_ON)
        else:
            self.ser.write(self.mixer3_OFF)
        time.sleep(1)
        # print(self.decodeModbus(self.ser))

    def setSelector1(self, state):
        if state:
            self.ser.write(self.selector1_ON)
        else:
            self.ser.write(self.selector1_OFF)
        time.sleep(1)
        # print(self.decodeModbus(self.ser))

    def setSelector2(self, state):
        if state:
            self.ser.write(self.selector2_ON)
        else:
            self.ser.write(self.selector2_OFF)
        time.sleep(1)
        # print(self.decodeModbus(self.ser))

    def setSelector3(self, state):
        if state:
            self.ser.write(self.selector3_ON)
        else:
            self.ser.write(self.selector3_OFF)
        time.sleep(1)
        # print(self.decodeModbus(self.ser))

    def setPumpIn(self, state):
        if state:
            self.ser.write(self.pumpIn_ON)
        else:
            self.ser.write(self.pumpIn_OFF)
        time.sleep(1)
        # print(self.decodeModbus(self.ser))

    def setPumpOut(self, state):
        if state:
            self.ser.write(self.pumpOut_ON)
        else:
            self.ser.write(self.pumpOut_OFF)
        time.sleep(1)
        # print(self.decodeModbus(self.ser))

    def readTemperature(self):
        self.ser.write(self.soil_temperature)
        task = Task(pTask=self.tempAck,
                    args=TaskArgument(addr=self.soil_temperature[0], func=self.soil_temperature[1]),
                    delay=self.WAITING_ACK,
                    period=0)
        self.scheduler1.SCH_AddTask(task)
        time.sleep(0.1)

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
            self.tempValue = res

    def moisAck(self, args: TaskArgument):
        res = self.waitAck(args)
        if res != -1:
            self.moisValue = res

    def waitAck(self, args: TaskArgument):
        addr = args.payload["addr"]
        func = args.payload["func"]
        result = -1
        with self._lock:
            for entry in self.buffer:
                if entry[0] == addr and entry[1] == func:
                    result = self.decodeModbus(entry)
            # Check and delete redundant ACK
            self.buffer = [entry for entry in self.buffer if not (entry[0] == addr and entry[1] == func)]
        return result

    def loop(self):
        while True:
            numBytes = self.ser.inWaiting()
            if numBytes:
                with self._lock:
                    self.buffer.append([b for b in self.ser.read(numBytes)])
                    print(self.buffer)

            TimeManager.sleep(0.1)

    @staticmethod
    def decodeModbus(data_array):
        if len(data_array) >= 7:
            array_size = len(data_array)
            payload = data_array[array_size - 4] * 256 + data_array[array_size - 3]
            return payload
        else:
            return -1

    # @staticmethod
    # def serial_read_data(ser):
    #     bytesToRead = ser.inWaiting()
    #     if bytesToRead > 0:
    #         out = ser.read(bytesToRead)
    #         data_array = [b for b in out]
    #         print(data_array)
    #         if len(data_array) >= 7:
    #             array_size = len(data_array)
    #             value = data_array[array_size - 4] * 256 + data_array[array_size - 3]
    #             return value
    #         else:
    #             return -1
    #     return 0


# relay1_ON  = [0, 6, 0, 0, 0, 255, 200, 91]
# relay1_OFF = [0, 6, 0, 0, 0, 0, 136, 27]
#
# def setDevice1(state):
#     if state == True:
#         ser.write(relay1_ON)
#     else:
#         ser.write(relay1_OFF)
#     time.sleep(1)
#     print(serial_read_data(ser))
#


# while True:
#     setDevice1(True)
#     time.sleep(2)
#     setDevice1(False)
#     time.sleep(2)

#
#
# def serial_read_data(ser):
#     bytesToRead = ser.inWaiting()
#     if bytesToRead > 0:
#         out = ser.read(bytesToRead)
#         data_array = [b for b in out]
#         print(data_array)
#         if len(data_array) >= 7:
#             array_size = len(data_array)
#             value = data_array[array_size - 4] * 256 + data_array[array_size - 3]
#             return value
#         else:
#             return -1
#     return 0
#
# soil_temperature = [1, 3, 0, 6, 0, 1, 100, 11]
# def readTemperature():
#     serial_read_data(ser)
#     ser.write(soil_temperature)
#     time.sleep(1)
#     return serial_read_data(ser)
#
# soil_moisture = [1, 3, 0, 7, 0, 1, 53, 203]
# def readMoisture():
#     serial_read_data(ser)
#     ser.write(soil_moisture)
#     time.sleep(1)
#     return serial_read_data(ser)
#





