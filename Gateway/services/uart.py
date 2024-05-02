import time
import serial.tools.list_ports


class CRC:
    pass


class UART:
    _PORT_NAME = "/dev/ttyUSB1"

    def __init__(self):
        self.message = ""

        self.port = UART.getPort()
        print(self.port)

        try:
            self.ser = serial.Serial(port=UART.getPort(), baudrate=9600)
            print("Open successfully")
        except Exception as e:
            print("Can not open the port")
            print(e)

        self.loopInfinity()

    @staticmethod
    def getPort():
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

    def readSerial(self):
        waitingSize = self.ser.inWaiting()
        if waitingSize <= 0:
            return
        self.message = self.message + self.ser.read(waitingSize).decode("UTF-8")
        while ("!" in self.message) and ("#" in self.message):
            start = self.message.find("!")
            end = self.message.find("#")
            while start != -1 and end != -1 and start > end:
                end = self.message.find("#", end)
            if start == -1 or end == -1:
                return
            self.processData(self.message[start:end + 1])
            if end == len(self.message) - 1:
                self.message = ""
            else:
                self.message = self.message[end + 1:]

    def processData(self, rawData):
        pass

    def getTempValue(self):
        pass

    def getHumidValue(self):
        pass

    def loopInfinity(self):
        pass



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
# while True:
#     print("TEST SENSOR")
#     print(readMoisture())
#     time.sleep(1)
#     print(readTemperature())
#     time.sleep(1)