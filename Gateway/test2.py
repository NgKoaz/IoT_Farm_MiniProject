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